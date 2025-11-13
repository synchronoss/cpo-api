/*
 * Copyright (C) 2003-2025 David E. Berry
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

/**
 * Created with IntelliJ IDEA. User: dberry Date: 9/10/13 Time: 12:51 PM To change this template use
 * File | Settings | File Templates.
 */
public abstract class AbstractDataSourceInfo<T> implements DataSourceInfo<T> {
  private T dataSource = null;
  private String dataSourceName = null;

  // Make sure DataSource creation is thread safe.
  private final Object LOCK = new Object();

  public AbstractDataSourceInfo(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

  protected abstract T createDataSource() throws CpoException;

  @Override
  public String getDataSourceName() {
    return dataSourceName;
  }

  @Override
  public T getDataSource() throws CpoException {
    if (dataSource == null) {
      synchronized (LOCK) {
        try {
          dataSource = createDataSource();
        } catch (Exception e) {
          throw new CpoException("Error instantiating DataSource", e);
        }
      }
    }

    return dataSource;
  }
}
