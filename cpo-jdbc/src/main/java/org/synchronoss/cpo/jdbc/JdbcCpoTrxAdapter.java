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

import java.io.Serial;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.CpoTrxAdapter;

/** A transaction adapter that allows the user to control the commits and role backs */
public class JdbcCpoTrxAdapter extends JdbcCpoAdapter implements CpoTrxAdapter {

  /** Version Id for this class. */
  @Serial private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoTrxAdapter.class);

  /** DOCUMENT ME! */
  // Default Connection. Only used JdbcCpoTrxAdapter
  private Connection writeConnection_ = null;

  /**
   * Creates a JdbcCpoTrxAdapter from a jdbcCpoAdapter
   *
   * @param jdbcCpoAdapter The cpoAdapter to use behind the scenes
   * @throws CpoException Any errors that might occur
   */
  protected JdbcCpoTrxAdapter(JdbcCpoAdapter jdbcCpoAdapter) throws CpoException {
    super(jdbcCpoAdapter);
  }

  @Override
  public void commit() throws CpoException {
    Connection conn = getStaticConnection();

    if (conn != null) {
      try {
        conn.commit();
      } catch (SQLException se) {
        throw new CpoException(se.getMessage());
      }
    } else {
      throw new CpoException("Transaction Object has been Closed");
    }
  }

  @Override
  public void rollback() throws CpoException {
    Connection conn = getStaticConnection();

    if (conn != null) {
      try {
        conn.rollback();
      } catch (Exception e) {
        throw new CpoException(e.getMessage());
      }
    } else {
      throw new CpoException("Transaction Object has been Closed");
    }
  }

  @Override
  public boolean isClosed() throws CpoException {
    boolean closed = false;

    try {
      closed = (writeConnection_ == null || writeConnection_.isClosed());
    } catch (Exception e) {
      throw new CpoException(e.getMessage());
    }
    return closed;
  }

  @Override
  public void close() throws CpoException {
    Connection conn = getStaticConnection();

    try {
      if (conn != null && !conn.isClosed()) {
        try {
          conn.rollback();
        } catch (Exception e) {
          if (logger.isTraceEnabled()) {
            logger.trace(e.getLocalizedMessage());
          }
        }
        try {
          conn.close();
        } catch (Exception e) {
          if (logger.isTraceEnabled()) {
            logger.trace(e.getLocalizedMessage());
          }
        }
      }
    } catch (Exception e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getLocalizedMessage());
      }
    } finally {
      setStaticConnection(null);
    }
  }

  /**
   * Get a static connection
   *
   * @return The static connection
   * @throws CpoException - an error occurred
   */
  protected Connection getStaticConnection() throws CpoException {
    if (writeConnection_ == null) {
      // enable lazy loading and automatic connection creating for re-using and adapter after
      // closing it
      writeConnection_ = super.getWriteConnection();
    }
    return writeConnection_;
  }

  private void setStaticConnection(Connection c) {
    writeConnection_ = c;
  }

  @Override
  protected Connection getReadConnection() throws CpoException {
    return getStaticConnection();
  }

  @Override
  protected Connection getWriteConnection() throws CpoException {
    return getStaticConnection();
  }

  /**
   * Closes a local connection. A CpoTrxAdapter allows the user to manage the transaction so <code>
   * closeLocalConnection</code> does nothing
   *
   * @param connection The connection to be closed
   */
  @Override
  protected void closeLocalConnection(Connection connection) {}

  /**
   * Commits a local connection. A CpoTrxAdapter allows the user to manage the transaction so <code>
   * commitLocalConnection</code> does nothing
   *
   * @param connection The connection to be committed
   */
  @Override
  protected void commitLocalConnection(Connection connection) {}

  /**
   * Rollbacks a local connection. A CpoTrxAdapter allows the user to manage the transaction so
   * <code>rollbackLocalConnection</code> does nothing
   *
   * @param connection The connection to be rolled back
   */
  @Override
  protected void rollbackLocalConnection(Connection connection) {}
}
