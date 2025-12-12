package org.synchronoss.cpo.jdbc.exporter;

/*-
 * [[
 * jdbc
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

import org.synchronoss.cpo.core.exporter.CoreMetaXmlObjectExporter;
import org.synchronoss.cpo.core.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoArgument;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.cpometa.CtJdbcArgument;
import org.synchronoss.cpo.cpometa.CtJdbcAttribute;
import org.synchronoss.cpo.cpometa.ObjectFactory;
import org.synchronoss.cpo.cpometa.StArgumentScope;
import org.synchronoss.cpo.jdbc.JdbcCpoArgument;
import org.synchronoss.cpo.jdbc.JdbcCpoAttribute;

/**
 * XmlObject exporter for jdbc meta objects
 *
 * @author Michael Bellomo
 * @since 4/18/12
 */
public class JdbcMetaXmlObjectExporter extends CoreMetaXmlObjectExporter
    implements MetaXmlObjectExporter {

  protected ObjectFactory objectFactory = new ObjectFactory();

  /**
   * Constructs a JdbcMetaXmlObjectExporter object.
   *
   * @param metaDescriptor - The meta descriptor to export
   */
  public JdbcMetaXmlObjectExporter(CpoMetaDescriptor metaDescriptor) {
    super(metaDescriptor);
  }

  @Override
  public void visit(CpoAttribute cpoAttribute) {

    // shouldn't happen, but if what we got wasn't a JdbcAttribute...
    if (!(cpoAttribute instanceof JdbcCpoAttribute)) {
      super.visit(cpoAttribute);
      return;
    }

    JdbcCpoAttribute jdbcAttribute = (JdbcCpoAttribute) cpoAttribute;

    if (currentCtClass != null) {

      // CtClass.addNewCpoAttribute() can't be used here because it returns a CtAttribute, not a
      // CtJdbcAttribute
      CtJdbcAttribute ctJdbcAttribute = new CtJdbcAttribute();
      var jaxbElement = objectFactory.createJdbcAttribute(ctJdbcAttribute);
      // add it to the class
      currentCtClass.getCpoAttribute().add(jaxbElement);

      ctJdbcAttribute.setJavaName(jdbcAttribute.getJavaName());
      ctJdbcAttribute.setJavaType(jdbcAttribute.getJavaType());
      ctJdbcAttribute.setDataName(jdbcAttribute.getDataName());
      ctJdbcAttribute.setDataType(jdbcAttribute.getDataType());

      if (jdbcAttribute.getTransformClassName() != null
          && !jdbcAttribute.getTransformClassName().isEmpty()) {
        ctJdbcAttribute.setTransformClass(jdbcAttribute.getTransformClassName());
      }

      if (jdbcAttribute.getDescription() != null && !jdbcAttribute.getDescription().isEmpty()) {
        ctJdbcAttribute.setDescription(jdbcAttribute.getDescription());
      }

      if (jdbcAttribute.getDbTable() != null && !jdbcAttribute.getDbTable().isEmpty()) {
        ctJdbcAttribute.setDbTable(jdbcAttribute.getDbTable());
      }

      if (jdbcAttribute.getDbColumn() != null && !jdbcAttribute.getDbColumn().isEmpty()) {
        ctJdbcAttribute.setDbColumn(jdbcAttribute.getDbColumn());
      }
    }
  }

  @Override
  public void visit(CpoArgument cpoArgument) {

    // shouldn't happen, but if what we got wasn't a JdbcArgument...
    if (!(cpoArgument instanceof JdbcCpoArgument)) {
      super.visit(cpoArgument);
      return;
    }

    JdbcCpoArgument jdbcArgument = (JdbcCpoArgument) cpoArgument;

    if (currentCtFunction != null) {

      // CtFunction.addNewCpoArgument() can't be used here because it returns a CtArgument, not a
      // CtJdbcArgument
      CtJdbcArgument ctJdbcArgument = new CtJdbcArgument();
      var jaxbElement = objectFactory.createJdbcArgument(ctJdbcArgument);
      currentCtFunction.getCpoArgument().add(jaxbElement);

      ctJdbcArgument.setAttributeName(jdbcArgument.getName());

      if (jdbcArgument.getDescription() != null && !jdbcArgument.getDescription().isEmpty()) {
        ctJdbcArgument.setDescription(jdbcArgument.getDescription());
      }

      if (jdbcArgument.isInParameter() && jdbcArgument.isOutParameter()) {
        ctJdbcArgument.setScope(StArgumentScope.BOTH);
      } else if (jdbcArgument.isInParameter()) {
        ctJdbcArgument.setScope(StArgumentScope.IN);
      } else if (jdbcArgument.isOutParameter()) {
        ctJdbcArgument.setScope(StArgumentScope.OUT);
      }

      if (jdbcArgument.getTypeInfo() != null && !jdbcArgument.getTypeInfo().isEmpty()) {
        ctJdbcArgument.setTypeInfo(jdbcArgument.getTypeInfo());
      }
    }
  }
}
