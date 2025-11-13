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
package org.synchronoss.cpo.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.SortedMap;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * An AbstractJDBCDatasource used for instantiating the DataSource from the database driver
 *
 * @author dberry
 */
public abstract class AbstractJdbcDataSource extends AbstractJdbcDataSourceInfo
    implements DataSource {

  private PrintWriter printWriter_ = null;
  private int timeout_ = 0;

  /**
   * Constructs a AbstractJdbcDataSource
   *
   * @param dataSourceName - The name of the datasource to instantiate,
   */
  public AbstractJdbcDataSource(String dataSourceName) {
    super(dataSourceName);
  }

  /**
   * Constructs a AbstractJdbcDataSource
   *
   * @param className - The DataSource className from the Driver.
   * @param properties - The list of properties to be passed to the driver
   */
  public AbstractJdbcDataSource(String className, SortedMap<String, String> properties) {
    super(className, properties);
  }

  /**
   * Constructs a AbstractJdbcDataSource
   *
   * @param className - The DataSource className from the Driver.
   * @param properties - The list of properties to be passed to the driver
   */
  public AbstractJdbcDataSource(String className, Properties properties) {
    super(className, properties);
  }

  @Override
  public Connection getConnection(String userName, String password) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return printWriter_;
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {
    printWriter_ = out;
  }

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {
    timeout_ = seconds;
  }

  @Override
  public int getLoginTimeout() throws SQLException {
    return timeout_;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }

  // Cannot use @Override if you want this to build in java 6
  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    throw new SQLFeatureNotSupportedException();
  }
}
