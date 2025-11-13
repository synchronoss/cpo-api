/*
 * Copyright (C) 2003-2025 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */

package org.synchronoss.cpo.cassandra;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

/**
 * Represents a Cassandra Cluster
 *
 * @author dberry
 */
public class ClusterDataSource {
  private Cluster cluster = null;
  private String keySpace = null;
  private Session session = null;

  private ClusterDataSource() {}

  /**
   * Construct a ClusterDataSource
   *
   * @param cluster The cassandra cluster
   * @param keySpace The keyspace
   */
  public ClusterDataSource(Cluster cluster, String keySpace) {
    this.cluster = cluster;
    this.keySpace = keySpace;

    if (cluster == null) throw new IllegalArgumentException("Cluster cannot be null");
    if (keySpace == null) throw new IllegalArgumentException("KeySpace cannot be null");
    session = cluster.connect(keySpace);
  }

  Session getSession() {
    return session;
  }
}
