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
package org.synchronoss.cpo;

import org.synchronoss.cpo.cache.CpoAdapterCache;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 9/10/13
 * Time: 09:00 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CpoBaseAdapter<T> extends CpoAdapterCache implements CpoAdapter {
  // DataSource Information

  /**
   * The datasource where read queries are executed against
   */
  private T readDataSource = null;
  /**
   * The datasource where write queries are executed against
   */
  private T writeDataSource = null;
  /**
   * The name of the datasource
   */
  private String dataSourceName = null;

  protected T getReadDataSource() {
    return readDataSource;
  }

  protected void setReadDataSource(T readDataSource) {
    this.readDataSource = readDataSource;
  }

  protected T getWriteDataSource() {
    return writeDataSource;
  }

  protected void setWriteDataSource(T writeDataSource) {
    this.writeDataSource = writeDataSource;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  protected void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }
}
