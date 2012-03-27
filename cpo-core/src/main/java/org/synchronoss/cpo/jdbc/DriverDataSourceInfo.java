/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.DataSourceInfo;

/**
 * Collects the info required to instantiate a DataSource from a JDBC Driver 
 * 
 * Provides the DataSourceInfo factory method getDataSource which instantiates the DataSource
 * 
 * @author dberry
 */
public class DriverDataSourceInfo implements DataSourceInfo, DataSource  {
  private static final int               URL_CONNECTION = 1;
  private static final int         URL_PROPS_CONNECTION = 2;
  private static final int URL_USER_PASSWORD_CONNECTION = 3;
  private int    connectionType = 0;

  private String dataSourceName = null;
  private String    tablePrefix = null;
  private String            url = null;
  private String       username = null;
  private String       password = null;
  private Properties properties = null;

  private PrintWriter printWriter_ = null;
  private int timeout_ = 0;
   
  // Make sure DataSource creation is thread safe.
  private Object LOCK = new Object();

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   */
  public DriverDataSourceInfo(String driver, String url) throws CpoException {
    loadDriver(driver);
    connectionType=URL_CONNECTION;
    this.url=url;
    this.dataSourceName=url;
  }

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @tablePrefix The prefix added to the cpo tables in the metadata source
   */
  public DriverDataSourceInfo(String driver, String url, String tablePrefix) throws CpoException {
    loadDriver(driver);
    connectionType=URL_CONNECTION;
    this.url=url;
    this.dataSourceName=url;
    if (tablePrefix != null) {
      this.tablePrefix=tablePrefix;
    }
  }

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param properties - The connection properties for connecting to the database
   */
  public DriverDataSourceInfo(String driver, String url, Properties properties) throws CpoException {
    loadDriver(driver);
    connectionType=URL_PROPS_CONNECTION;
    this.url=url;
    this.properties=properties;
    this.dataSourceName=BuildDataSourceName(url, properties);
  }

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param properties - The connection properties for connecting to the database
   * @tablePrefix The prefix added to the cpo tables in the metadata source
   */
  public DriverDataSourceInfo(String driver, String url, Properties properties, String tablePrefix) throws CpoException {
    loadDriver(driver);
    connectionType=URL_PROPS_CONNECTION;
    this.url=url;
    this.properties=properties;
    this.dataSourceName=BuildDataSourceName(url, properties);
    if (tablePrefix != null) {
      this.tablePrefix=tablePrefix;
    }
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
    loadDriver(driver);
    connectionType=URL_USER_PASSWORD_CONNECTION;
    this.url=url;
    this.username=username;
    this.password=password;
    this.dataSourceName=url + username;
  }

  /**
   * Creates a DriverDataSourceInfo from a Jdbc Driver
   *
   * @param driver The text name of the driver
   * @param url - The url that points to the database.
   * @param username - The username for connecting to the database
   * @param password - The password for connectinf to the database
  * @tablePrefix The prefix added to the cpo tables in the metadata source
   */
  public DriverDataSourceInfo(String driver, String url, String username, String password, String tablePrefix) throws CpoException {
    loadDriver(driver);
    connectionType=URL_USER_PASSWORD_CONNECTION;
    this.url=url;
    this.username=username;
    this.password=password;
    this.dataSourceName=url + username;
    if (tablePrefix != null) {
      this.tablePrefix=tablePrefix;
    }
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  public String getTablePrefix() {
    return tablePrefix;
  }

  public DataSource getDataSource() throws CpoException {
    return this;
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param url DOCUMENT ME!
   * @param properties DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  private String BuildDataSourceName(String url, Properties properties) {
    StringBuilder dsName = new StringBuilder(url);
    TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>(properties);

    // Use a tree map so that the properties are sorted. This way if we have
    // the same datasource with the same properties but in different order,
    // we will generate the same key.
    for (Object key : treeMap.keySet()) {
      dsName.append((String)key);
      dsName.append("=");
      dsName.append(properties.getProperty((String)key));
    }

    return dsName.toString();
  }
  
    public Connection getConnection(String userName, String password) 
    throws SQLException {
            throw new SQLException("Not Implemented");
    }
    

    public Connection getConnection()
    throws SQLException {
        return makeNewConnection();
    }

    private Connection makeNewConnection() throws SQLException {
        Connection connection = null;
        switch(connectionType) {
          case DriverDataSourceInfo.URL_CONNECTION: 
              connection=DriverManager.getConnection(url);
              break;
          case DriverDataSourceInfo.URL_PROPS_CONNECTION:
              connection=DriverManager.getConnection(url, properties);
              break;
          case DriverDataSourceInfo.URL_USER_PASSWORD_CONNECTION:
              connection=DriverManager.getConnection(url, username, password);
              break;
          default: throw new SQLException("Invalid Connection Type");
        }
        return connection;
    }

    public synchronized String toString() {
        StringBuilder info = new StringBuilder();
        info.append("JdbcDataSource(");
        info.append(dataSourceName);
        info.append(")");
        return(info.toString());
    }

    public PrintWriter getLogWriter()
    throws SQLException{
        return printWriter_;
    }

    public void setLogWriter(PrintWriter out)
    throws SQLException{
        printWriter_ = out;

    }

    public void setLoginTimeout(int seconds)
    throws SQLException {
        timeout_ = seconds;
    }

    public int getLoginTimeout()
    throws SQLException {
        return timeout_;
    }

  public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
  
  private void loadDriver(String driver) throws CpoException {
    try {
      Class.forName(driver);
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("Could Not Load Driver" + driver);
    }
  }
}
