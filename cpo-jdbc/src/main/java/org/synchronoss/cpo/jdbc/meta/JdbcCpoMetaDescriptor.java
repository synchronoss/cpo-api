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
package org.synchronoss.cpo.jdbc.meta;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.jdbc.JavaSqlType;
import org.synchronoss.cpo.jdbc.exporter.JdbcMetaXmlObjectExporter;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 *
 * @author dberry
 */
public class JdbcCpoMetaDescriptor extends CpoMetaDescriptor {
  private boolean supportsBlobs = false;
  private boolean supportsCalls = false;
  private boolean supportsMillis = false;
  private boolean supportsSelect4Update = false;
  

  public JdbcCpoMetaDescriptor(String name) throws CpoException {
    super(name);
  }
  
  @Override
  protected Class getMetaAdapterClass() throws CpoException {
    return JdbcCpoMetaAdapter.class;
  }
  
  @Override
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new JdbcMetaXmlObjectExporter(this);
  }

  public boolean isSupportsBlobs() {
    return supportsBlobs;
  }

  public void setSupportsBlobs(boolean supportsBlobs) {
    this.supportsBlobs = supportsBlobs;
  }

  public boolean isSupportsCalls() {
    return supportsCalls;
  }

  public void setSupportsCalls(boolean supportsCalls) {
    this.supportsCalls = supportsCalls;
  }

  public boolean isSupportsMillis() {
    return supportsMillis;
  }

  public void setSupportsMillis(boolean supportsMillis) {
    this.supportsMillis = supportsMillis;
  }

  public boolean isSupportsSelect4Update() {
    return supportsSelect4Update;
  }

  public void setSupportsSelect4Update(boolean supportsSelect4Update) {
    this.supportsSelect4Update = supportsSelect4Update;
  }
  
  public int getJavaSqlType(String javaSqlTypeName) throws CpoException {
    return ((JdbcCpoMetaAdapter)getCpoMetaAdapter()).getJavaSqlType(javaSqlTypeName);
  }
  
  public JavaSqlType<?> getJavaSqlType(int sqlType) throws CpoException {
    return ((JdbcCpoMetaAdapter)getCpoMetaAdapter()).getJavaSqlType(sqlType);
  }
}
