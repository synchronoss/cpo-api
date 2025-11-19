package org.synchronoss.cpo;

/*-
 * [[
 * core
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

/**
 * Created with IntelliJ IDEA. User: dberry Date: 9/10/13 Time: 12:51 PM To change this template use
 * File | Settings | File Templates.
 */
public abstract class AbstractDataSourceInfo<T> implements DataSourceInfo<T> {
  private T dataSource = null;
  private String dataSourceName = null;
  private int fetchSize = 0;
  private int batchSize = 0;

  // Make sure DataSource creation is thread safe.
  private final Object LOCK = new Object();

  public AbstractDataSourceInfo(String dataSourceName, int fetchSize, int batchSize) {
    this.dataSourceName = dataSourceName;
    this.fetchSize = fetchSize;
    this.batchSize = batchSize;
  }

  protected abstract T createDataSource() throws CpoException;

  @Override
  public String getDataSourceName() {
    return dataSourceName;
  }

  @Override
  public int getFetchSize() {
    return fetchSize;
  }

  @Override
  public int getBatchSize() {
    return batchSize;
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
