package org.synchronoss.cpo.core;

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

import java.util.concurrent.locks.ReentrantLock;

/**
 * Base {@link DataSourceInfo} implementation that holds the common name/fetchSize/batchSize
 * configuration and lazily creates the underlying datasource object on first access, guarding
 * creation with a lock so concurrent callers cannot race to create it twice.
 *
 * @param <T> the type of the underlying datasource object
 * @author dberry
 */
public abstract class AbstractDataSourceInfo<T> implements DataSourceInfo<T> {
  private T dataSource = null;
  private final String dataSourceName;
  private final int fetchSize;
  private final int batchSize;

  // Make sure DataSource creation is thread safe.
  private final ReentrantLock lock = new ReentrantLock();

  /**
   * Creates an instance with the given configuration. The underlying datasource object itself is
   * not created until {@link #getDataSource()} is first called.
   *
   * @param dataSourceName the configured name of this datasource
   * @param fetchSize the number of rows to fetch per round-trip
   * @param batchSize the number of statements to accumulate before executing a batch
   */
  public AbstractDataSourceInfo(String dataSourceName, int fetchSize, int batchSize) {
    this.dataSourceName = dataSourceName;
    this.fetchSize = fetchSize;
    this.batchSize = batchSize;
  }

  /**
   * Creates the underlying datasource object. Called at most once, the first time {@link
   * #getDataSource()} is invoked.
   *
   * @return the newly created datasource object
   * @throws CpoException if the datasource cannot be created
   */
  protected abstract T createDataSource() throws CpoException;

  /** {@inheritDoc} */
  @Override
  public String getDataSourceName() {
    return dataSourceName;
  }

  /** {@inheritDoc} */
  @Override
  public int getFetchSize() {
    return fetchSize;
  }

  /** {@inheritDoc} */
  @Override
  public int getBatchSize() {
    return batchSize;
  }

  /**
   * {@inheritDoc}
   *
   * <p>The datasource object is created lazily on first call via {@link #createDataSource()} and
   * cached for subsequent calls; creation is synchronized to be thread-safe.
   */
  @Override
  public T getDataSource() throws CpoException {
    lock.lock();
    try {
      if (dataSource == null) {
        dataSource = createDataSource();
      }
    } finally {
      lock.unlock();
    }

    return dataSource;
  }
}
