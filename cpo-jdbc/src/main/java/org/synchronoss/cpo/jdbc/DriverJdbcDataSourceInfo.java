package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
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

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.helper.CpoClassLoader;

/**
 * Collects the info required to instantiate a DataSource from a JDBC Driver Provides the
 * DataSourceInfo factory method getDataSource which instantiates the DataSource
 *
 * @author dberry
 */
public class DriverJdbcDataSourceInfo extends AbstractJdbcDataSource {

  private static final int URL_CONNECTION = 1;
  private static final int URL_PROPS_CONNECTION = 2;
  private static final int URL_USER_PASSWORD_CONNECTION = 3;
  private int connectionType = 0;
  private String url = null;
  private String username = null;
  private String password = null;
  private Properties properties = null;
  private Driver driver = null;

  /**
   * Creates a DriverJdbcDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param fetchSize The fetchSize for the datasource
   * @param batchSize The batchSize used to update the datasource
   * @throws CpoException - Any errors encountered loading the driver
   */
  public DriverJdbcDataSourceInfo(String driver, String url, int fetchSize, int batchSize)
      throws CpoException {
    super(url, fetchSize, batchSize);
    loadDriver(driver);
    connectionType = URL_CONNECTION;
    this.url = url;
  }

  /**
   * Creates a DriverJdbcDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param properties - The connection properties for connecting to the database
   * @param fetchSize The fetchSize for the datasource
   * @param batchSize The batchSize used to update the datasource
   * @throws CpoException - Any errors encountered loading the driver
   */
  public DriverJdbcDataSourceInfo(
      String driver, String url, Properties properties, int fetchSize, int batchSize)
      throws CpoException {
    super(url, properties, fetchSize, batchSize);
    loadDriver(driver);
    connectionType = URL_PROPS_CONNECTION;
    this.url = url;
    this.properties = properties;
  }

  /**
   * Creates a DriverJdbcDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param username - The username for connecting to the database
   * @param password - The password for connecting to the database
   * @param fetchSize The fetchSize for the datasource
   * @param batchSize The batchSize used to update the datasource
   * @throws CpoException - Any errors encountered loading the driver
   */
  public DriverJdbcDataSourceInfo(
      String driver, String url, String username, String password, int fetchSize, int batchSize)
      throws CpoException {
    super(url + username, fetchSize, batchSize);
    loadDriver(driver);
    connectionType = URL_USER_PASSWORD_CONNECTION;
    this.url = url;
    this.username = username;
    this.password = password;
  }

  @Override
  protected DataSource createDataSource() throws CpoException {
    return this;
  }

  @Override
  public Connection getConnection() throws SQLException {
    return makeNewConnection();
  }

  private Connection makeNewConnection() throws SQLException {
    // Connect through the resolved Driver instance directly rather than DriverManager: the
    // driver may have been loaded by a classloader (e.g. a tool's user-configurable custom
    // classpath) that isn't an ancestor of this class's own classloader, in which case
    // DriverManager.getConnection() silently ignores it ("No suitable driver found") even
    // though the class loaded and registered itself successfully.
    Connection connection;
    switch (connectionType) {
      case URL_CONNECTION:
        connection = driver.connect(url, new Properties());
        break;
      case URL_PROPS_CONNECTION:
        connection = driver.connect(url, properties);
        break;
      case URL_USER_PASSWORD_CONNECTION:
        Properties userPassProps = new Properties();
        userPassProps.setProperty("user", username);
        userPassProps.setProperty("password", password);
        connection = driver.connect(url, userPassProps);
        break;
      default:
        throw new SQLException("Invalid Connection Type");
    }
    if (connection == null) {
      throw new SQLException("Driver " + driver + " does not accept URL " + url);
    }
    return connection;
  }

  @Override
  public String toString() {
    StringBuilder info = new StringBuilder();
    info.append("JdbcDataSource(");
    info.append(getDataSourceName());
    info.append(")");
    return info.toString();
  }

  private void loadDriver(String driverClassName) throws CpoException {
    try {
      Class<?> driverClass = CpoClassLoader.forName(driverClassName);
      driver = (Driver) driverClass.getDeclaredConstructor().newInstance();
    } catch (Exception ex) {
      throw new CpoException("Could Not Load Driver" + driverClassName, ex);
    }
  }
}
