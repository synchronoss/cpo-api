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

import java.sql.Connection;
import java.sql.SQLException;
import org.synchronoss.cpo.core.CpoException;

/**
 * The transaction connection strategy: one write connection is pinned for the life of the
 * transaction and reused for every read and write. Call-local close/commit/rollback are no-ops
 * because the transaction owner ({@link JdbcCpoTrxAdapter}) controls the connection lifecycle
 * through {@link #clearConnection()} and the adapter's commit/rollback/close methods.
 *
 * @author david berry
 */
class JdbcPinnedConnectionStrategy implements JdbcConnectionStrategy {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private final JdbcConnectionStrategy pool;

  // A live JDBC connection is never serializable; a deserialized strategy re-pins lazily.
  private transient Connection connection = null;

  /**
   * Constructs a JdbcPinnedConnectionStrategy
   *
   * @param pool The strategy used to obtain the pinned connection
   */
  JdbcPinnedConnectionStrategy(JdbcConnectionStrategy pool) {
    this.pool = pool;
  }

  /**
   * Gets the pinned connection, lazily pinning a new one so an adapter can be reused after being
   * closed.
   *
   * @return The pinned connection
   * @throws CpoException An error occurred obtaining a connection
   */
  Connection getConnection() throws CpoException {
    if (connection == null) {
      connection = pool.getWriteConnection();
    }
    return connection;
  }

  /** Forgets the pinned connection so the next use pins a fresh one. */
  void clearConnection() {
    connection = null;
  }

  /**
   * Whether the pinned connection is absent or closed
   *
   * @return true if there is no usable pinned connection
   * @throws SQLException An error occurred checking the connection
   */
  boolean isConnectionClosed() throws SQLException {
    return connection == null || connection.isClosed();
  }

  @Override
  public Connection getReadConnection() throws CpoException {
    return getConnection();
  }

  @Override
  public Connection getWriteConnection() throws CpoException {
    return getConnection();
  }

  /**
   * Does nothing: the transaction owner controls when the pinned connection is closed.
   *
   * @param connection The connection to be closed
   */
  @Override
  public void closeLocalConnection(Connection connection) {}

  /**
   * Does nothing: the transaction owner controls when the transaction commits.
   *
   * @param connection The connection to be committed
   */
  @Override
  public void commitLocalConnection(Connection connection) {}

  /**
   * Does nothing: the transaction owner controls when the transaction rolls back.
   *
   * @param connection The connection to be rolled back
   */
  @Override
  public void rollbackLocalConnection(Connection connection) {}
}
