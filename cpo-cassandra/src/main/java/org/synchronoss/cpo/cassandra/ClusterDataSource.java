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

import com.datastax.oss.driver.api.core.CqlSession;

/**
 * Represents a Cassandra session
 *
 * @author dberry
 */
public class ClusterDataSource {
  private String keySpace = null;
  private CqlSession session = null;

  private ClusterDataSource() {}

  /**
   * Construct a ClusterDataSource
   *
   * @param session The cassandra session, already built with the keyspace applied
   * @param keySpace The keyspace
   */
  public ClusterDataSource(CqlSession session, String keySpace) {
    if (session == null) throw new IllegalArgumentException("CqlSession cannot be null");
    if (keySpace == null) throw new IllegalArgumentException("KeySpace cannot be null");

    this.session = session;
    this.keySpace = keySpace;
  }

  CqlSession getSession() {
    return session;
  }

  /** Closes the underlying Cassandra session. */
  void close() {
    session.close();
  }

  /**
   * Gets the keyspace this data source's session is connected to.
   *
   * @return the keyspace name
   */
  public String getKeySpace() {
    return keySpace;
  }
}
