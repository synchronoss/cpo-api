package org.synchronoss.cpo.core;

/*-
 * [[
 * core
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

/**
 * Interface that defines the methods needed by CPO for any class that collects DataSource info and
 * instantiates the DataSource
 *
 * @author david.berry
 * @param <T> The type of the DatasourceInfo
 */
public interface DataSourceInfo<T> {

  /**
   * Gets the configured name of this datasource, as declared in {@code cpoConfig.xml}.
   *
   * @return the dataSourceName
   */
  String getDataSourceName();

  /**
   * Gets the number of rows the driver should fetch per round-trip when reading result sets from
   * this datasource.
   *
   * @return the fetchSize for this datasource
   */
  int getFetchSize();

  /**
   * Gets the number of statements to accumulate before executing a batch against this datasource.
   *
   * @return the batchSize for this datasource
   */
  int getBatchSize();

  /**
   * Gets the underlying datasource object, creating it on first access.
   *
   * @return the datasource
   * @throws CpoException if the datasource cannot be found or created
   */
  T getDataSource() throws CpoException;
}
