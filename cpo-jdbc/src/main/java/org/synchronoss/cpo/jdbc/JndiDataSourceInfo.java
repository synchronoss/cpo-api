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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.DataSourceInfo;

/**
 * Collects the info required to instantiate a DataSource stored as a JNDI Resource. 
 * 
 * Provides the DataSourceInfo factory method getDataSource which instantiates the DataSource
 * 
 * @author dberry
 */
public class JndiDataSourceInfo implements DataSourceInfo {
  private DataSource dataSource = null;
  private String       jndiName = null;
  private Context       jndiCtx = null;
  private String dataSourceName = null;
  
  // Make sure DataSource creation is thread safe.
  private Object LOCK = new Object();

  /**
   * Creates a JndiDataSourceInfo from a JNDIName that represents the 
   * datasource in the application server.
   *
   * @param jndiName The JndiName of the app server datasource
   *
   */
  public JndiDataSourceInfo(String jndiName) {
    this.jndiName=jndiName;
    dataSourceName = jndiName;
  }

  /**
   * Creates a JndiDataSourceInfo from a JNDIName that represents the 
   * datasource in the application server.
   *
   * @param jndiName The JndiName of the app server datasource
   * @param ctx - The context for which the Jndi Lookup should use.
   *
   */
  public JndiDataSourceInfo(String jndiName, Context ctx) {
    this.jndiName=jndiName;
    dataSourceName = jndiName;
    jndiCtx=ctx;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  public DataSource getDataSource() throws CpoException {

    if (dataSource!=null)
      return dataSource;
     
    synchronized (LOCK) {
      try {
        if (jndiCtx == null) {
          jndiCtx = new InitialContext();
        }
        dataSource = (DataSource) jndiCtx.lookup(jndiName);
        //        ds = new JdbcDataSource(jdsi);
      } catch (Exception e) {
        throw new CpoException("Error instantiating DataSource", e);
      }
    }
    return dataSource;
  }
  
}
