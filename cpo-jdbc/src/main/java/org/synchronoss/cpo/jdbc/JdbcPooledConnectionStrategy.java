package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
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
import java.sql.SQLException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;

/**
 * The default connection strategy: every call-local unit of work checks a connection out of the
 * read or write datasource and commits/rolls back and returns it when the call completes. If the
 * read datasource ever fails to produce a connection, the strategy permanently fails over to the
 * write datasource for reads.
 *
 * @author david berry
 */
class JdbcPooledConnectionStrategy implements JdbcConnectionStrategy {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(JdbcPooledConnectionStrategy.class);

  private final DataSource readDataSource;
  private final DataSource writeDataSource;
  private boolean invalidReadDataSource = false;

  /**
   * Constructs a JdbcPooledConnectionStrategy
   *
   * @param readDataSource The datasource used for read connections
   * @param writeDataSource The datasource used for write connections
   */
  JdbcPooledConnectionStrategy(DataSource readDataSource, DataSource writeDataSource) {
    this.readDataSource = readDataSource;
    this.writeDataSource = writeDataSource;
  }

  @Override
  public Connection getReadConnection() throws CpoException {
    Connection connection;

    try {
      if (!invalidReadDataSource) {
        connection = readDataSource.getConnection();
      } else {
        connection = writeDataSource.getConnection();
      }
      connection.setAutoCommit(false);
    } catch (Exception e) {
      invalidReadDataSource = true;

      String msg = "getReadConnection(): failed";
      logger.error(msg, e);

      try {
        connection = writeDataSource.getConnection();
        connection.setAutoCommit(false);
      } catch (SQLException e2) {
        msg = "getWriteConnection(): failed";
        logger.error(msg, e2);
        throw new CpoException(msg, e2);
      }
    }

    return connection;
  }

  @Override
  public Connection getWriteConnection() throws CpoException {
    Connection connection;

    try {
      connection = writeDataSource.getConnection();
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      String msg = "getWriteConnection(): failed";
      logger.error(msg, e);
      throw new CpoException(msg, e);
    }

    return connection;
  }

  @Override
  public void closeLocalConnection(Connection connection) {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getMessage());
      }
    }
  }

  @Override
  public void commitLocalConnection(Connection connection) {
    try {
      if (connection != null) {
        connection.commit();
      }
    } catch (SQLException e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getMessage());
      }
    }
  }

  @Override
  public void rollbackLocalConnection(Connection connection) {
    try {
      if (connection != null) {
        connection.rollback();
      }
    } catch (Exception e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getMessage());
      }
    }
  }
}
