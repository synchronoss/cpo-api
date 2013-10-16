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
package org.synchronoss.cpo.cassandra.exporter;

import org.synchronoss.cpo.cassandra.cpoCassandraMeta.CtCassandraArgument;
import org.synchronoss.cpo.cassandra.cpoCassandraMeta.CtCassandraAttribute;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoAttribute;
import org.synchronoss.cpo.core.cpoCoreMeta.CtArgument;
import org.synchronoss.cpo.core.cpoCoreMeta.CtAttribute;
import org.synchronoss.cpo.exporter.CoreMetaXmlObjectExporter;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 9/10/13
 * Time: 08:13 AM
 * To change this template use File | Settings | File Templates.
 */
public class CassandraMetaXmlObjectExporter extends CoreMetaXmlObjectExporter implements MetaXmlObjectExporter {
  public CassandraMetaXmlObjectExporter(CpoMetaDescriptor metaDescriptor) {
    super(metaDescriptor);
  }

  @Override
  public void visit(CpoAttribute cpoAttribute) {

    // shouldn't happen, but if what we got wasn't a JdbcAttribute...
    if (!(cpoAttribute instanceof CassandraCpoAttribute)) {
      super.visit(cpoAttribute);
      return;
    }

    CassandraCpoAttribute cassAttribute = (CassandraCpoAttribute) cpoAttribute;

    if (currentCtClass != null) {

      // CtClass.addNewCpoAttribute() can't be used here because it returns a CtAttribute, not a CtJdbcAttribute
      CtCassandraAttribute ctCassandraAttribute = CtCassandraAttribute.Factory.newInstance();

      ctCassandraAttribute.setJavaName(cassAttribute.getJavaName());
      ctCassandraAttribute.setJavaType(cassAttribute.getJavaType());
      ctCassandraAttribute.setDataName(cassAttribute.getDataName());
      ctCassandraAttribute.setDataType(cassAttribute.getDataType());

      if (cassAttribute.getTransformClassName() != null && cassAttribute.getTransformClassName().length() > 0) {
        ctCassandraAttribute.setTransformClass(cassAttribute.getTransformClassName());
      }

      if (cassAttribute.getDescription() != null && cassAttribute.getDescription().length() > 0) {
        ctCassandraAttribute.setDescription(cassAttribute.getDescription());
      }

      // add it to the class
      CtAttribute ctAttribute = currentCtClass.addNewCpoAttribute();
      ctAttribute.set(ctCassandraAttribute);
    }
  }

  @Override
  public void visit(CpoArgument cpoArgument) {

    if (currentCtFunction != null) {

      // CtFunction.addNewCpoArgument() can't be used here because it returns a CtArgument, not a CtJdbcArgument
      CtCassandraArgument ctCassandraArgument = CtCassandraArgument.Factory.newInstance();

      ctCassandraArgument.setAttributeName(cpoArgument.getAttributeName());

      if (cpoArgument.getDescription() != null && cpoArgument.getDescription().length() > 0) {
        ctCassandraArgument.setDescription(cpoArgument.getDescription());
      }

      CtArgument ctArgument = currentCtFunction.addNewCpoArgument();
      ctArgument.set(ctCassandraArgument);
    }
  }

}
