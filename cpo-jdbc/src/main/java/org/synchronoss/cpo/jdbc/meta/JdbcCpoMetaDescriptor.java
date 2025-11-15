package org.synchronoss.cpo.jdbc.meta;

/*-
 * [-------------------------------------------------------------------------
 * jdbc
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.jdbc.exporter.JdbcMetaXmlObjectExporter;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 * Process the Jdbc specific metadata from the xml metadata
 *
 * @author dberry
 */
public class JdbcCpoMetaDescriptor extends CpoMetaDescriptor {

  /**
   * Constructs a JdbcCpoMetaDescriptor
   *
   * @param name - The name of the metadescriptor
   * @param caseSensitive - Whether we do caseSensitive dataName matches or not
   * @throws CpoException - Any errors processing the metadata
   */
  public JdbcCpoMetaDescriptor(String name, boolean caseSensitive) throws CpoException {
    super(name, caseSensitive);
  }

  @Override
  protected Class<JdbcCpoMetaAdapter> getMetaAdapterClass() throws CpoException {
    return JdbcCpoMetaAdapter.class;
  }

  @Override
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new JdbcMetaXmlObjectExporter(this);
  }
}
