/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc.exporter;

import org.synchronoss.cpo.core.cpoCoreMeta.CtArgument;
import org.synchronoss.cpo.core.cpoCoreMeta.CtAttribute;
import org.synchronoss.cpo.exporter.CoreMetaXmlObjectExporter;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.jdbc.JdbcCpoArgument;
import org.synchronoss.cpo.jdbc.JdbcCpoAttribute;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcArgument;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcAttribute;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * XmlObject exporter for jdbc meta objects
 *
 * @author Michael Bellomo
 * @since 4/18/12
 */
public class JdbcMetaXmlObjectExporter extends CoreMetaXmlObjectExporter implements MetaXmlObjectExporter {

  public JdbcMetaXmlObjectExporter(String metaAdapter) {
    super(metaAdapter);
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

      // CtClass.addNewCpoAttribute() can't be used here because it returns a CtAttribute, not a CtJdbcAttribute
      CtJdbcAttribute ctJdbcAttribute = CtJdbcAttribute.Factory.newInstance();

      ctJdbcAttribute.setJavaName(jdbcAttribute.getJavaName());
      ctJdbcAttribute.setJavaType(jdbcAttribute.getJavaType());
      ctJdbcAttribute.setDataName(jdbcAttribute.getDataName());
      ctJdbcAttribute.setDataType(jdbcAttribute.getDataType());

      if (jdbcAttribute.getTransformClassName() != null && jdbcAttribute.getTransformClassName().length() > 0) {
        ctJdbcAttribute.setTransformClass(jdbcAttribute.getTransformClassName());
      }

      if (jdbcAttribute.getDescription() != null && jdbcAttribute.getDescription().length() > 0) {
        ctJdbcAttribute.setDescription(jdbcAttribute.getDescription());
      }

      if (jdbcAttribute.getDbTable() != null && jdbcAttribute.getDbTable().length() > 0) {
        ctJdbcAttribute.setDbTable(jdbcAttribute.getDbTable());
      }

      if (jdbcAttribute.getDbColumn() != null && jdbcAttribute.getDbColumn().length() > 0) {
        ctJdbcAttribute.setDbColumn(jdbcAttribute.getDbColumn());
      }

      // add it to the class
      CtAttribute ctAttribute = currentCtClass.addNewCpoAttribute();
      ctAttribute.set(ctJdbcAttribute);
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

      // CtFunction.addNewCpoArgument() can't be used here because it returns a CtArgument, not a CtJdbcArgument
      CtJdbcArgument ctJdbcArgument = CtJdbcArgument.Factory.newInstance();

      ctJdbcArgument.setAttributeName(jdbcArgument.getAttributeName());

      if (jdbcArgument.getDescription() != null && jdbcArgument.getDescription().length() > 0) {
        ctJdbcArgument.setDescription(jdbcArgument.getDescription());
      }

      if (jdbcArgument.isInParameter() && jdbcArgument.isOutParameter()) {
        ctJdbcArgument.setScope(CtJdbcArgument.Scope.BOTH);
      } else if (jdbcArgument.isInParameter()) {
        ctJdbcArgument.setScope(CtJdbcArgument.Scope.IN);
      } else if (jdbcArgument.isOutParameter()) {
        ctJdbcArgument.setScope(CtJdbcArgument.Scope.OUT);
      }

      CtArgument ctArgument = currentCtFunction.addNewCpoArgument();
      ctArgument.set(ctJdbcArgument);
    }
  }
}
