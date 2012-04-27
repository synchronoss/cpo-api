/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import java.util.SortedMap;
import javax.sql.DataSource;

/**
 *
 * @author dberry
 */
public abstract class AbstractDataSource extends AbstractDataSourceInfo implements DataSource {
  private PrintWriter printWriter_ = null;
  private int timeout_ = 0;

  public AbstractDataSource(String dataSourceName) {
    super(dataSourceName);
  }
  
  public AbstractDataSource(String className, SortedMap<String, String> properties) {
    super(className, properties);
  }

  public AbstractDataSource(String className, Properties properties) {
    super(className, properties);
  }

  @Override
  public Connection getConnection(String userName, String password) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public PrintWriter getLogWriter()
          throws SQLException {
    return printWriter_;
  }

  @Override
  public void setLogWriter(PrintWriter out)
          throws SQLException {
    printWriter_ = out;

  }

  @Override
  public void setLoginTimeout(int seconds)
          throws SQLException {
    timeout_ = seconds;
  }

  @Override
  public int getLoginTimeout()
          throws SQLException {
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

  
}
