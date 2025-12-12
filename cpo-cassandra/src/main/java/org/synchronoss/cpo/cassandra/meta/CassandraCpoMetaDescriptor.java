package org.synchronoss.cpo.cassandra.meta;

/*-
 * [[
 * cassandra
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

import org.synchronoss.cpo.cassandra.exporter.CassandraMetaXmlObjectExporter;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;

/**
 * Created with IntelliJ IDEA. User: dberry Date: 9/10/13 Time: 07:56 AM To change this template use
 * File | Settings | File Templates.
 */
public class CassandraCpoMetaDescriptor extends CpoMetaDescriptor {

  /**
   * Constructs a CassandraCpoMetaDescriptor
   *
   * @param name The descriptor name
   * @param caseSensitive Is data member matching case sensitive
   * @throws CpoException An error occurred
   */
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
