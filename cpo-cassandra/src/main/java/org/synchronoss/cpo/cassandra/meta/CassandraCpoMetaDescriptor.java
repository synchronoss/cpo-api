/*
 * Copyright (C) 2003-2013 David E. Berry
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
package org.synchronoss.cpo.cassandra.meta;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.exporter.CassandraMetaXmlObjectExporter;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 9/10/13
 * Time: 07:56 AM
 * To change this template use File | Settings | File Templates.
 */
public class CassandraCpoMetaDescriptor extends CpoMetaDescriptor {

  public CassandraCpoMetaDescriptor(String name, boolean caseSensitive) throws CpoException {
    super(name, caseSensitive);
  }

  @Override
  protected Class getMetaAdapterClass() throws CpoException {
    return CassandraCpoMetaAdapter.class;
  }

  @Override
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new CassandraMetaXmlObjectExporter(this);
  }

}
