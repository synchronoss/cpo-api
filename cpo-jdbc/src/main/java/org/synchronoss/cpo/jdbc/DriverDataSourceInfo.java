/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import javax.sql.DataSource;
import org.synchronoss.cpo.CpoException;

/**
 * Collects the info required to instantiate a DataSource from a JDBC Driver
 *
 * Provides the DataSourceInfo factory method getDataSource which instantiates the DataSource
 *
 * @author dberry
 */
public class DriverDataSourceInfo extends AbstractDataSource {

  private static final int URL_CONNECTION = 1;
  private static final int URL_PROPS_CONNECTION = 2;
  private static final int URL_USER_PASSWORD_CONNECTION = 3;
  private int connectionType = 0;
  private String url = null;
  private String username = null;
  private String password = null;
  private Properties properties = null;

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   */
  public DriverDataSourceInfo(String driver, String url) throws CpoException {
    super(url);
    loadDriver(driver);
    connectionType = URL_CONNECTION;
    this.url = url;
  }

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param properties - The connection properties for connecting to the database
   */
  public DriverDataSourceInfo(String driver, String url, Properties properties) throws CpoException {
    super(url, properties);
    loadDriver(driver);
    connectionType = URL_PROPS_CONNECTION;
    this.url = url;
    this.properties = properties;
  }

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param username - The username for connecting to the database
   * @param password - The password for connectinf to the database
   */
  public DriverDataSourceInfo(String driver, String url, String username, String password) throws CpoException {
    super(url + username);
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
  public Connection getConnection()
          throws SQLException {
    return makeNewConnection();
  }

  private Connection makeNewConnection() throws SQLException {
    Connection connection = null;
    switch (connectionType) {
      case DriverDataSourceInfo.URL_CONNECTION:
        connection = DriverManager.getConnection(url);
        break;
      case DriverDataSourceInfo.URL_PROPS_CONNECTION:
        connection = DriverManager.getConnection(url, properties);
        break;
      case DriverDataSourceInfo.URL_USER_PASSWORD_CONNECTION:
        connection = DriverManager.getConnection(url, username, password);
        break;
      default:
        throw new SQLException("Invalid Connection Type");
    }
    return connection;
  }

  @Override
  public synchronized String toString() {
    StringBuilder info = new StringBuilder();
    info.append("JdbcDataSource(");
    info.append(getDataSourceName());
    info.append(")");
    return (info.toString());
  }

  private void loadDriver(String driver) throws CpoException {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("Could Not Load Driver" + driver);
    }
  }
}
