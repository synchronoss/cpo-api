/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.exporter;

import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 * The CpoClassSourceGenerator generates java source code to define the cpo classes.
 *
 * This implemenation will generate only a class, without extending an interface.  It is available
 * to support existing systems that aren't able/willing to change to the interface model.
 *
 * @author Michael Bellomo
 * @since 4/17/12
 */
public class CpoLegacyClassSourceGenerator extends CpoClassSourceGenerator {

  public CpoLegacyClassSourceGenerator(CpoMetaDescriptor metaDescriptor) {
    super(metaDescriptor);
  }

  public String getClassName() {
    return className;
  }

  public String getSourceCode() {
    StringBuilder source = new StringBuilder();

    source.append("/* \n");
    source.append(" *  This class auto-generated by " + this.getClass().getName() + "\n");
    source.append(" */\n");
    source.append(header);
    source.append("\n");
    source.append("  /* Attribute name statics */\n");
    source.append(attributeStatics);
    source.append("\n");
    source.append("  /* Function group statics */\n");
    source.append(functionGroupStatics);
    source.append("\n");
    source.append("  /* Properties */\n");
    source.append(properties);
    source.append("\n");
    source.append("  /* Constructor */\n");
    source.append(constructor);
    source.append("\n");
    source.append("  /* Getters and Setters */\n");
    source.append(gettersSetters);

    // generate equals()
    source.append("  public boolean equals(Object o) {\n");
    source.append("    if (this == o)\n");
    source.append("      return true;\n");
    source.append("    if (o == null || getClass() != o.getClass())\n");
    source.append("      return false;\n");
    source.append("\n");
    source.append("    " + className + " that = (" + className + ")o;\n");
    source.append("\n");
    source.append(equals);
    source.append("\n");
    source.append("    return true;\n");
    source.append("  }\n\n");

    // generate hashCode()
    source.append("  public int hashCode() {\n");
    source.append("    int result = 0;\n");
    source.append("    result = 31 * result + getClass().getName().hashCode();\n");
    source.append(hashCode);
    source.append("    return result;\n");
    source.append("  }\n\n");

    // generate toString()
    source.append("  public String toString() {\n");
    source.append("    StringBuilder str = new StringBuilder();\n");
    source.append(toString);
    source.append("    return str.toString();\n");
    source.append("  }\n");

    source.append(footer);
    source.append("\n");

    return source.toString();
  }

  /**
   * Returns the name of the class to use
   */
  protected String generateClassName(CpoClass cpoClass) {
    return super.generateInterfaceName(cpoClass);
  }

  @Override
  public void visit(CpoClass cpoClass) {

    // reset all the buffers
    reset();

    className = generateClassName(cpoClass);

    // generate class header
    if (cpoClass.getName().lastIndexOf(".") != -1) {
      String packageName = cpoClass.getName().substring(0, cpoClass.getName().lastIndexOf("."));
      header.append("package " + packageName + ";\n\n");
    }

    // generate class declaration
    header.append("public class " + className + " implements java.io.Serializable {\n");

    // footer
    footer.append("}\n");

    // generate constructor
    constructor.append("  public " + className + "() {\n");
    constructor.append("  }\n");
  }
}
