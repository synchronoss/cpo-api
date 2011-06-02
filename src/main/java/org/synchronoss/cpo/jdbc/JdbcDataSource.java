/**
 * JdbcDataSource.java
 * 
 *  Copyright (C) 2006  David E. Berry
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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.sql.DataSource;

/** 
 * A class for that makes the DriverManager look like a datasource
 * 
 */

public class JdbcDataSource implements DataSource {
    // Attributes to help satisfy the DataSource Interface
    private PrintWriter printWriter_ = null;
    private int timeout_ = 0;
    
    private JdbcDataSourceInfo dataSourceInfo=null;
    
    static {
    }
    @SuppressWarnings("unused")
    private JdbcDataSource(){}

    public JdbcDataSource(JdbcDataSourceInfo jdsi) throws SQLException {
    	setDataSourceInfo(jdsi);
      try {
        Class.forName(getDataSourceInfo().getDriver());
      } catch(ClassNotFoundException cnfe) {
        throw new SQLException("Could Not Load Driver"+getDataSourceInfo().getDriver());
      }
    }

    public Connection getConnection(String userName, String password) 
    throws SQLException {
            throw new SQLException("Not Implemented");
    }
    

    public Connection getConnection()
    throws SQLException {
        return makeNewConnection();
    }

    // This explicitly makes a new connection. Called in
    // the foreground when initializing the JdbcDataSource,
    // and called in the background when running.

    private Connection makeNewConnection() throws SQLException {
        Connection connection = null;
        switch(getDataSourceInfo().getConnectionType()) {
          case JdbcDataSourceInfo.URL_CONNECTION: 
              connection=DriverManager.getConnection(getDataSourceInfo().getUrl());
              break;
          case JdbcDataSourceInfo.URL_PROPS_CONNECTION:
              connection=DriverManager.getConnection(getDataSourceInfo().getUrl(), getDataSourceInfo().getProperties());
              break;
          case JdbcDataSourceInfo.URL_USER_PASSWORD_CONNECTION:
              connection=DriverManager.getConnection(getDataSourceInfo().getUrl(), getDataSourceInfo().getUserName(), getDataSourceInfo().getPassword());
              break;
          default: throw new SQLException("Invalid Connection Type");
        }
        return connection;
    }

    public synchronized String toString() {
        StringBuilder info = new StringBuilder();
        info.append("JdbcDataSource(");
        info.append(getDataSourceInfo().getUrl());
        info.append(",");
        info.append(getDataSourceInfo().getUserName());
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

	/**
	 * @return Returns the dataSourceInfo.
	 */
	public JdbcDataSourceInfo getDataSourceInfo() {
		return dataSourceInfo;
	}

	/**
	 * @param dataSourceInfo The dataSourceInfo to set.
	 */
	public void setDataSourceInfo(JdbcDataSourceInfo dataSourceInfo) {
		this.dataSourceInfo = dataSourceInfo;
	}

  public <T> T unwrap(Class<T> iface) throws SQLException {
      throw new UnsupportedOperationException("Not supported yet.");
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
