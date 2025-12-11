package org.synchronoss.cpo.exporter;

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

import org.synchronoss.cpo.MetaVisitor;
import org.synchronoss.cpo.cpometa.*;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.*;

/**
 * XmlObject exporter for meta objects
 *
 * @author Michael Bellomo
 * @since 4/17/12
 */
public class CoreMetaXmlObjectExporter implements MetaXmlObjectExporter, MetaVisitor {

  protected CtCpoMetaData ctCpoMetaData = null;
  protected CtClass currentCtClass;
  protected CtFunctionGroup currentCtFunctionGroup;
  protected CtFunction currentCtFunction;
  protected ObjectFactory objectFactory = new ObjectFactory();

  public CoreMetaXmlObjectExporter(CpoMetaDescriptor metaDescriptor) {
    ctCpoMetaData = new CtCpoMetaData();
    ctCpoMetaData.setMetaDescriptor(metaDescriptor.getClass().getName());
    ctCpoMetaData.setDefaultPackageName(metaDescriptor.getDefaultPackageName());
  }

  @Override
  public CtCpoMetaData getCpoMetaData() {
    return ctCpoMetaData;
  }

  @Override
  public void visit(CpoClass cpoClass) {
    CtClass ctClass = new CtClass();
    ctCpoMetaData.getCpoClass().add(ctClass);

    ctClass.setName(cpoClass.getName());

    if (cpoClass.getDescription() != null && !cpoClass.getDescription().isEmpty()) {
      ctClass.setDescription(cpoClass.getDescription());
    }

    // save the reference
    currentCtClass = ctClass;
  }

  @Override
  public void visit(CpoAttribute cpoAttribute) {
    if (currentCtClass != null) {
      CtAttribute ctAttribute = new CtAttribute();
      var jaxbElement = objectFactory.createCpoAttribute(ctAttribute);
      currentCtClass.getCpoAttribute().add(jaxbElement);

      ctAttribute.setJavaName(cpoAttribute.getJavaName());
      ctAttribute.setJavaType(cpoAttribute.getJavaType());
      ctAttribute.setDataName(cpoAttribute.getDataName());
      ctAttribute.setDataType(cpoAttribute.getDataType());

      if (cpoAttribute.getTransformClassName() != null
          && !cpoAttribute.getTransformClassName().isEmpty()) {
        ctAttribute.setTransformClass(cpoAttribute.getTransformClassName());
      }

      if (cpoAttribute.getDescription() != null && !cpoAttribute.getDescription().isEmpty()) {
        ctAttribute.setDescription(cpoAttribute.getDescription());
      }
    }
  }

  @Override
  public void visit(CpoFunctionGroup cpoFunctionGroup) {
    if (currentCtClass != null) {
      CtFunctionGroup ctFunctionGroup = new CtFunctionGroup();
      currentCtClass.getCpoFunctionGroup().add(ctFunctionGroup);

      if (cpoFunctionGroup.getName() != null && !cpoFunctionGroup.getName().isEmpty()) {
        ctFunctionGroup.setName(cpoFunctionGroup.getName());
      }

      ctFunctionGroup.setType(StFunctionGroupType.fromValue(cpoFunctionGroup.getType()));

      if (cpoFunctionGroup.getDescription() != null
          && !cpoFunctionGroup.getDescription().isEmpty()) {
        ctFunctionGroup.setDescription(cpoFunctionGroup.getDescription());
      }

      // save the reference
      currentCtFunctionGroup = ctFunctionGroup;
    }
  }

  @Override
  public void visit(CpoFunction cpoFunction) {
    if (currentCtFunctionGroup != null) {
      CtFunction ctFunction = new CtFunction();
      currentCtFunctionGroup.getCpoFunction().add(ctFunction);

      ctFunction.setName(cpoFunction.getName());
      ctFunction.setExpression(cpoFunction.getExpression());

      if (cpoFunction.getDescription() != null && !cpoFunction.getDescription().isEmpty()) {
        ctFunction.setDescription(cpoFunction.getDescription());
      }

      // save the reference
      currentCtFunction = ctFunction;
    }
  }

  @Override
  public void visit(CpoArgument cpoArgument) {
    if (currentCtFunction != null) {
      CtArgument ctArgument = new CtArgument();
      var jaxbElement = objectFactory.createCpoArgument(ctArgument);
      currentCtFunction.getCpoArgument().add(jaxbElement);

      ctArgument.setAttributeName(cpoArgument.getAttribute().getJavaName());

      if (cpoArgument.getDescription() != null && !cpoArgument.getDescription().isEmpty()) {
        ctArgument.setDescription(cpoArgument.getDescription());
      }
    }
  }
}
