package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import com.datastax.driver.core.Session;
import java.io.Serializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;

/**
 * How a {@link CassandraCpoAdapter} obtains its sessions. Cassandra sessions are long-lived and
 * shared, so unlike the JDBC connection strategy there is no per-call release; if the read cluster
 * ever fails to produce a session, the strategy permanently fails over to the write cluster for
 * reads. This mirrors the composition shape of the JDBC JdbcConnectionStrategy — promote it to an
 * interface if a second acquisition behavior ever appears.
 *
 * @author david berry
 */
class CassandraSessionStrategy implements Serializable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CassandraSessionStrategy.class);

  private final ClusterDataSource readDataSource;
  private final ClusterDataSource writeDataSource;
  private boolean invalidReadSession = false;

  /**
   * Constructs a CassandraSessionStrategy
   *
   * @param readDataSource The datasource used for read sessions
   * @param writeDataSource The datasource used for write sessions
   */
  CassandraSessionStrategy(ClusterDataSource readDataSource, ClusterDataSource writeDataSource) {
    this.readDataSource = readDataSource;
    this.writeDataSource = writeDataSource;
  }

  /**
   * Gets the session for read operations
   *
   * @return A Session bean for reading
   * @throws CpoException An exception occurred
   */
  Session getReadSession() throws CpoException {
    Session session;

    try {
      if (!invalidReadSession) {
        session = readDataSource.getSession();
      } else {
        session = writeDataSource.getSession();
      }
    } catch (Exception e) {
      invalidReadSession = true;

      String msg = "getReadSession(): failed";
      logger.error(msg, e);

      session = getWriteSession();
    }

    return session;
  }

  /**
   * Gets the session for write operations
   *
   * @return A Session bean for writing
   * @throws CpoException An exception occurred
   */
  Session getWriteSession() throws CpoException {
    Session session;

    try {
      session = writeDataSource.getSession();
    } catch (Throwable t) {
      String msg = "getWriteSession(): failed";
      logger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return session;
  }
}
