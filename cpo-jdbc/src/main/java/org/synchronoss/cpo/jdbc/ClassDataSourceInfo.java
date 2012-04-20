/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Map;
import java.util.TreeMap;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.DataSourceInfo;

/**
 * Collects the info required to instantiate a DataSource from a JDBC Driver 
 * 
 * Provides the DataSourceInfo factory method getDataSource which instantiates the DataSource
 * 
 * @author dberry
 */
public class ClassDataSourceInfo implements DataSourceInfo, DataSource, ConnectionEventListener {
  private Logger logger = LoggerFactory.getLogger(this.getClass());
  private DataSource dataSource = null;
  private ConnectionPoolDataSource poolDataSource = null;
  
  private String dataSourceName = null;
  private Map<String, String> properties = null;

  private PrintWriter printWriter_ = null;
  private int timeout_ = 0;
   
  // Make sure DataSource creation is thread safe.
  final private Object LOCK = new Object();

  private Queue<PooledConnection> freeConnections = new LinkedList<PooledConnection>();
  private Queue<PooledConnection> usedConnections = new LinkedList<PooledConnection>();
  
  /**
   * Creates a ClassDataSourceInfo from a Jdbc Driver
   *
   * @param classname The classname of a class that implements datasource
   */
  public ClassDataSourceInfo(String className) throws CpoException {
    loadClass(className);
    dataSourceName=className;
  }

  /**
   * Creates a ClassDataSourceInfo from a Jdbc Driver
   *
   * @param classname The classname of a class that implements datasource
   * @param properties - The connection properties for connecting to the database
   */
  public ClassDataSourceInfo(String className, Map<String, String> properties) throws CpoException {
    loadClass(className);
    dataSourceName=BuildDataSourceName(className,properties);
    setClassProperties(properties);
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  public DataSource getDataSource() throws CpoException {
    return this.dataSource;
  }
  
  /**
   * DOCUMENT ME!
   *
   * @param url DOCUMENT ME!
   * @param properties DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  private String BuildDataSourceName(String s, Map<String, String> properties) {
    StringBuilder dsName = new StringBuilder(s);
    TreeMap<Object, Object> treeMap = new TreeMap<Object, Object>(properties);

    // Use a tree map so that the properties are sorted. This way if we have
    // the same datasource with the same properties but in different order,
    // we will generate the same key.
    for (Object key : treeMap.keySet()) {
      dsName.append((String)key);
      dsName.append("=");
      dsName.append(properties.get((String)key));
    }

    return dsName.toString();
  }
  
    public Connection getConnection(String userName, String password) 
    throws SQLException {
            throw new SQLException("Not Implemented");
    }
    

    public Connection getConnection() throws SQLException {
      Connection conn = null;
      if (poolDataSource!=null)
        conn = getPooledConnection();
      else if (dataSource!=null)
        conn = dataSource.getConnection();
      
      return conn;
    }

    private Connection getPooledConnection() throws SQLException {
      PooledConnection pooledConn = null;
      synchronized(LOCK){
        if (!freeConnections.isEmpty()){
          pooledConn = freeConnections.poll();
        } else {
          pooledConn = poolDataSource.getPooledConnection();
          pooledConn.addConnectionEventListener(this);
        }
        usedConnections.add(pooledConn);
      }
      return pooledConn.getConnection();
    }

    @Override
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
  
  private void loadClass(String className) throws CpoException {
    try {
      Class dsClass = Class.forName(className);
      Object ds = dsClass.newInstance();
      
      if (ds instanceof ConnectionPoolDataSource){
        this.poolDataSource=(ConnectionPoolDataSource)ds;
        this.dataSource=this;
      } else if (ds instanceof DataSource){
        this.dataSource=(DataSource)ds;
      } else {
        throw new CpoException(className+"is not a DataSource");
      }
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("Could Not Find Class" + className, cnfe);
    } catch (InstantiationException ie){
      throw new CpoException("Could Not Instantiate Class" + className, ie);
    } catch (IllegalAccessException iae){
      throw new CpoException("Could Not Access Class" + className, iae);
    }
  }

  public void connectionClosed(ConnectionEvent ce) {
    synchronized (LOCK){
      PooledConnection pc = (PooledConnection)ce.getSource();
      if (usedConnections.remove(pc)){
        freeConnections.add(pc);
      }
    }
  }

  public void connectionErrorOccurred(ConnectionEvent ce) {
    synchronized (LOCK){
      PooledConnection pc = (PooledConnection)ce.getSource();
      if (!usedConnections.remove(pc)){
        // just in case the error is on a connection in the free pool
        freeConnections.remove(pc);
      }
    }
  }

  @Override
  public void finalize() throws Throwable {
    super.finalize();
    for (PooledConnection pc : freeConnections){
      pc.removeConnectionEventListener(this);
      try{
        pc.close();
      } catch (SQLException se){
        
      }
    }
    for (PooledConnection pc : usedConnections){
      pc.removeConnectionEventListener(this);
      try{
        pc.close();
      } catch (SQLException se){
        
      }
    }
  }
  
  private void setClassProperties(Map<String, String> properties) {
    Object ds = dataSource;
    if (poolDataSource!=null)
      ds = poolDataSource;
    
    for (String key : properties.keySet()){
      setObjectProperty(ds, key, properties.get(key));
    }
  }
  
  private void setObjectProperty(Object obj, String key, String value)  {
    String methodName = "set"+key.substring(0,1).toUpperCase()+key.substring(1);
    try {
      Method setter = obj.getClass().getMethod(methodName, String.class);
      setter.invoke(obj, value);
    } catch (NoSuchMethodException nsme) {
      logger.error("=========>>> Could not find setter Method:"+methodName+" for property:"+key+" please check the java docs for "+obj.getClass().getName());
    } catch (InvocationTargetException ite){
      logger.error("Error Invoking setter Method:"+methodName, ite);
    } catch (IllegalAccessException iae){
      logger.error("Error accessing setter Method:"+methodName, iae);
    }
  }
}
