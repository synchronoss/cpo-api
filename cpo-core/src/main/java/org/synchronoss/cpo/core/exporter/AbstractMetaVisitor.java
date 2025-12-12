package org.synchronoss.cpo.core.exporter;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import org.synchronoss.cpo.core.MetaVisitor;
import org.synchronoss.cpo.core.meta.domain.CpoArgument;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.synchronoss.cpo.core.meta.domain.CpoFunction;
import org.synchronoss.cpo.core.meta.domain.CpoFunctionGroup;

public abstract class AbstractMetaVisitor implements MetaVisitor {
  protected static final String ATTR_PREFIX = "ATTR_";
  protected static final String FG_PREFIX = "FG_";

  protected StringBuilder attributeStatics = new StringBuilder();
  protected StringBuilder functionGroupStatics = new StringBuilder();
  protected StringBuilder gettersSetters = new StringBuilder();

  protected String buildPackageName(CpoClass cpoClass) {
    StringBuilder header = new StringBuilder();
    // generate class header
    if (cpoClass.getName().lastIndexOf(".") != -1) {
      String packageName = cpoClass.getName().substring(0, cpoClass.getName().lastIndexOf("."));
      header.append("package ").append(packageName).append(";\n\n");
    }
    return header.toString();
  }

  protected String buildGetterName(String attName) {
    // the getter name is get concatenated with the camel case of the attribute name
    String getterName;
    if (attName.length() > 1) {
      getterName = ("get" + attName.substring(0, 1).toUpperCase() + attName.substring(1) + "()");
    } else {
      getterName = ("get" + attName.toUpperCase() + "()");
    }
    return getterName;
  }

  protected String buildSetterName(String attName) {
    // the setter name is get concatenated with the camel case of the attribute name
    String setterName;
    if (attName.length() > 1) {
      setterName = ("set" + attName.substring(0, 1).toUpperCase() + attName.substring(1));
    } else {
      setterName = ("set" + attName.toUpperCase());
    }
    return setterName;
  }

  protected String buildAttributeStatic(String attName) {
    return "  public final static String "
        + ATTR_PREFIX
        + attName.toUpperCase()
        + " = \""
        + attName
        + "\";\n";
  }

  @Override
  public void visit(CpoFunctionGroup cpoFunctionGroup) {

    // generate statics for function group
    String fgName = cpoFunctionGroup.getName();
    if (fgName == null) {
      fgName = "NULL";
    }
    fgName = scrubName(fgName);

    String staticName = FG_PREFIX + cpoFunctionGroup.getType() + "_" + fgName.toUpperCase();

    if (cpoFunctionGroup.getName() == null) {
      functionGroupStatics.append("  public final static String " + staticName + " = null;\n");
    } else {
      functionGroupStatics.append(
          "  public final static String " + staticName + " = \"" + fgName + "\";\n");
    }
  }

  @Override
  public void visit(CpoFunction cpoFunction) {
    // nothing to do
  }

  @Override
  public void visit(CpoArgument cpoArgument) {
    // nothing to do
  }

  protected String scrubName(String name) {
    return name.replaceAll("[^0-9a-zA-Z_]", "_");
  }
}
