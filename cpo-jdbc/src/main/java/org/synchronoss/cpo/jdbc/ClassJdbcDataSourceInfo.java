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

import java.lang.ref.Cleaner;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.SortedMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.sql.CommonDataSource;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.helper.CpoClassLoader;
import org.synchronoss.cpo.core.helper.ExceptionHelper;

/**
 * Collects the info required to instantiate a DataSource from a JDBC Driver Provides the
 * DataSourceInfo factory method getDataSource which instantiates the DataSource
 *
 * @author dberry
 */
public class ClassJdbcDataSourceInfo extends AbstractJdbcDataSource
    implements ConnectionEventListener, AutoCloseable {
  private static final Cleaner cleaner = Cleaner.create();

  private final Logger logger = LoggerFactory.getLogger(this.getClass());
  private ConnectionPoolDataSource poolDataSource = null;
  private String className = null;
  private SortedMap<String, String> properties = null;
  private final ConcurrentLinkedQueue<PooledConnection> freeConnections =
      new ConcurrentLinkedQueue<>();
  private final ConcurrentLinkedQueue<PooledConnection> usedConnections =
      new ConcurrentLinkedQueue<>();

  /**
   * Creates a ClassJdbcDataSourceInfo from a Jdbc Driver
   *
   * @param className The classname of a class that implements datasource
   * @param properties - The connection properties for connecting to the database
   * @param fetchSize the number of rows to fetch per round trip on a retrieve
   * @param batchSize the number of statements to batch together before executing
   */
  public ClassJdbcDataSourceInfo(
      String className, SortedMap<String, String> properties, int fetchSize, int batchSize) {
    super(className, properties, fetchSize, batchSize);
    this.className = className;
    this.properties = properties;
    cleaner.register(this, new ConnectionCleaner(this));
  }

  @Override
  public Connection getConnection() throws SQLException {
    return getPooledConnection();
  }

  private Connection getPooledConnection() throws SQLException {
    // A cached connection's physical link may have died while it sat in the free pool,
    // so validate on borrow and discard the dead ones instead of handing them out.
    PooledConnection pooledConn;
    while ((pooledConn = freeConnections.poll()) != null) {
      try {
        Connection conn = pooledConn.getConnection();
        if (!conn.isClosed()) {
          usedConnections.add(pooledConn);
          return conn;
        }
      } catch (SQLException se) {
        logger.debug("Discarding dead pooled connection", se);
      }
      pooledConn.removeConnectionEventListener(this);
      try {
        pooledConn.close();
      } catch (SQLException se) {
        logger.debug("Error closing dead pooled connection", se);
      }
    }
    pooledConn = poolDataSource.getPooledConnection();
    pooledConn.addConnectionEventListener(this);
    usedConnections.add(pooledConn);
    return pooledConn.getConnection();
  }

  @Override
  public String toString() {
    StringBuilder info = new StringBuilder();
    info.append("JdbcDataSource(");
    info.append(getDataSourceName());
    info.append(")");
    return info.toString();
  }

  @Override
  protected DataSource createDataSource() throws CpoException {
    DataSource dataSource = null;
    try {
      Class dsClass = CpoClassLoader.forName(className);
      CommonDataSource ds = (CommonDataSource) dsClass.getDeclaredConstructor().newInstance();

      // The ConnectionPoolDataSource must be a pure PooledConnection factory: a
      // datasource with its own internal pool (e.g. MariaDbPoolDataSource) also
      // reclaims closed connections, so both pools would hand out the same physical
      // connection to different callers. Configure such classes' factory variant
      // (e.g. MariaDbDataSource) instead.
      if (ds instanceof ConnectionPoolDataSource) {
        this.poolDataSource = (ConnectionPoolDataSource) ds;
        dataSource = this;
      } else if (ds instanceof DataSource) {
        dataSource = (DataSource) ds;
      } else {
        throw new CpoException(className + "is not a DataSource");
      }
      if (properties != null) setClassProperties(ds, properties);
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("Could Not Find Class: " + className, cnfe);
    } catch (InstantiationException ie) {
      throw new CpoException(
          "Could Not Instantiate Class: "
              + className
              + ":"
              + ExceptionHelper.getLocalizedMessage(ie));
    } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException iae) {
      throw new CpoException("Could Not Access Class: " + className, iae);
    }

    return dataSource;
  }

  @Override
  public void connectionClosed(ConnectionEvent ce) {
    PooledConnection pc = (PooledConnection) ce.getSource();
    if (usedConnections.remove(pc)) {
      freeConnections.add(pc);
    }
  }

  @Override
  public void connectionErrorOccurred(ConnectionEvent ce) {
    PooledConnection pc = (PooledConnection) ce.getSource();
    if (!usedConnections.remove(pc)) {
      // just in case the error is on a connection in the free pool
      freeConnections.remove(pc);
    }
  }

  @Override
  public void close() {
    for (PooledConnection pc : freeConnections) {
      pc.removeConnectionEventListener(this);
      try {
        pc.close();
      } catch (SQLException se) {
        logger.error(se.getMessage(), se);
      }
    }
    for (PooledConnection pc : usedConnections) {
      pc.removeConnectionEventListener(this);
      try {
        pc.close();
      } catch (SQLException se) {
        logger.error(se.getMessage(), se);
      }
    }
  }

  private void setClassProperties(CommonDataSource ds, SortedMap<String, String> properties) {
    // The url must be set last: some datasources (e.g. MariaDbPoolDataSource) eagerly
    // initialize their connection pool when the url is set, so all other properties,
    // in particular the credentials, must already be in place.
    for (String key : properties.keySet()) {
      if (!"url".equalsIgnoreCase(key)) {
        setObjectProperty(ds, key, properties.get(key));
      }
    }
    for (String key : properties.keySet()) {
      if ("url".equalsIgnoreCase(key)) {
        setObjectProperty(ds, key, properties.get(key));
      }
    }
  }

  private void setObjectProperty(Object obj, String key, String value) {
    String methodName = "set" + key.substring(0, 1).toUpperCase() + key.substring(1);
    Method setter = findSetter(obj.getClass(), methodName, key);
    if (setter == null) {
      logger.error(
          "=========>>> Could not find setter Method:"
              + methodName
              + " for property:"
              + key
              + " please check the java docs for "
              + obj.getClass().getName());
      return;
    }
    logger.debug("Calling " + setter.getName() + "(" + value + ")");
    try {
      setter.invoke(obj, value);
    } catch (InvocationTargetException ite) {
      logger.error("Error Invoking setter Method:" + setter.getName(), ite);
    } catch (IllegalAccessException iae) {
      logger.error("Error accessing setter Method:" + setter.getName(), iae);
    }
  }

  private Method findSetter(Class<?> clazz, String methodName, String key) {
    try {
      return clazz.getMethod(methodName, String.class);
    } catch (NoSuchMethodException nsme) {
      // Oracle's OracleDataSource exposes setURL (all-caps URL) rather than the
      // JavaBean-standard setUrl every other supported driver uses.
      if ("url".equalsIgnoreCase(key)) {
        try {
          return clazz.getMethod("setURL", String.class);
        } catch (NoSuchMethodException nsme2) {
          return null;
        }
      }
      return null;
    }
  }

  private static class ConnectionCleaner implements Runnable {
    private final ClassJdbcDataSourceInfo jdbcDataSourceInfo;

    ConnectionCleaner(ClassJdbcDataSourceInfo jdbcDataSourceInfo) {
      this.jdbcDataSourceInfo = jdbcDataSourceInfo;
    }

    @Override
    public void run() {
      jdbcDataSourceInfo.close();
    }
  }
}
