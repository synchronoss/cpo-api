package org.synchronoss.cpo.cassandra.meta;

/*-
 * [[
 * cassandra
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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
 * CassandraCpoMetaDescriptor is the Cassandra implementation of {@link CpoMetaDescriptor}. It
 * points CPO at the {@link CassandraCpoMetaAdapter} used to load the meta configuration and at the
 * {@link CassandraMetaXmlObjectExporter} used to export it back to XML.
 *
 * @author dberry
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
  protected Class<CassandraCpoMetaAdapter> getMetaAdapterClass() throws CpoException {
    return CassandraCpoMetaAdapter.class;
  }

  @Override
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new CassandraMetaXmlObjectExporter(this);
  }
}
