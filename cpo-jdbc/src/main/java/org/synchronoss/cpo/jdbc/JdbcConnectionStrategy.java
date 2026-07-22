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

import java.io.Serializable;
import java.sql.Connection;
import org.synchronoss.cpo.core.CpoException;

/**
 * Strategy for how a {@link JdbcCpoAdapter} obtains and releases its JDBC connections. The plain
 * adapter uses a pooled per-call strategy ({@link JdbcPooledConnectionStrategy}); a {@link
 * JdbcCpoTrxAdapter} pins a single connection for the life of the transaction ({@link
 * JdbcPinnedConnectionStrategy}).
 *
 * @author david berry
 */
interface JdbcConnectionStrategy extends Serializable {

  /**
   * Gets a connection for a read-only operation.
   *
   * @return A read connection
   * @throws CpoException An error has occurred.
   */
  Connection getReadConnection() throws CpoException;

  /**
   * Gets a connection for a write operation.
   *
   * @return A write connection
   * @throws CpoException An error has occurred.
   */
  Connection getWriteConnection() throws CpoException;

  /**
   * Releases a connection at the end of a call-local unit of work. A transaction-scoped strategy
   * keeps the connection open instead.
   *
   * @param connection The connection to close
   */
  void closeLocalConnection(Connection connection);

  /**
   * Commits a call-local unit of work. A transaction-scoped strategy leaves the commit to the
   * transaction owner instead.
   *
   * @param connection The connection to commit
   */
  void commitLocalConnection(Connection connection);

  /**
   * Rolls back a call-local unit of work. A transaction-scoped strategy leaves the rollback to the
   * transaction owner instead.
   *
   * @param connection The connection to rollback
   */
  void rollbackLocalConnection(Connection connection);
}
