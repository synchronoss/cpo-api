/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
  private String    tablePrefix = null;
  
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
   * @tablePrefix The prefix added to the cpo tables in the metadata source
   *
   */
  public JndiDataSourceInfo(String jndiName, String tablePrefix) {
    this.jndiName=jndiName;
    dataSourceName = jndiName;
    if (tablePrefix != null) {
      this.tablePrefix=tablePrefix;
    }
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

  /**
   * Creates a JndiDataSourceInfo from a JNDIName that represents the 
   * datasource in the application server.
   *
   * @param jndiName The JndiName of the app server datasource
   * @param ctx - The context for which the Jndi Lookup should use.
   * @tablePrefix The prefix added to the cpo tables in the metadata source
   *
   */
  public JndiDataSourceInfo(String jndiName, Context ctx, String tablePrefix) {
    this.jndiName=jndiName;
    dataSourceName = jndiName;
    jndiCtx=ctx;
    if (tablePrefix != null) {
      this.tablePrefix=tablePrefix;
    }
  }
	
  public String getDataSourceName() {
    return dataSourceName;
  }

  public String getTablePrefix() {
    return this.tablePrefix;
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
