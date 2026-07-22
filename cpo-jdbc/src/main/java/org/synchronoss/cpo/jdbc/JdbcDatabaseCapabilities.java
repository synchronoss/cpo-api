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
import java.sql.DatabaseMetaData;
import org.synchronoss.cpo.core.CpoException;

/**
 * The capabilities of the database behind a {@link JdbcCpoAdapter}, probed once from {@link
 * DatabaseMetaData} when the adapter is constructed. Immutable; add a field here (and probe it in
 * {@link #probe(JdbcConnectionStrategy)}) when the adapter needs a new capability test.
 *
 * @author david berry
 */
class JdbcDatabaseCapabilities implements Serializable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private final boolean batchUpdatesSupported;

  private JdbcDatabaseCapabilities(boolean batchUpdatesSupported) {
    this.batchUpdatesSupported = batchUpdatesSupported;
  }

  /**
   * Probes the database's capabilities using a short-lived read connection.
   *
   * @param connectionStrategy The strategy used to obtain and release the probe connection
   * @return The probed capabilities
   * @throws CpoException The database metadata could not be retrieved
   */
  static JdbcDatabaseCapabilities probe(JdbcConnectionStrategy connectionStrategy)
      throws CpoException {
    Connection c = null;
    try {
      c = connectionStrategy.getReadConnection();
      DatabaseMetaData dmd = c.getMetaData();

      // do all the tests here
      return new JdbcDatabaseCapabilities(dmd.supportsBatchUpdates());
    } catch (Throwable t) {
      throw new CpoException("Could Not Retrieve Database Metadata", t);
    } finally {
      // terminate the read-only transaction before pooling the connection (see existsBean)
      connectionStrategy.rollbackLocalConnection(c);
      connectionStrategy.closeLocalConnection(c);
    }
  }

  /**
   * Are batch updates supported
   *
   * @return true if batch updates are supported
   */
  boolean batchUpdatesSupported() {
    return batchUpdatesSupported;
  }
}
