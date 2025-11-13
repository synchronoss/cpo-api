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

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.jta.CpoXaResource;

/**
 * A factory for creating CassandraCpoAdapter
 *
 * @author dberry
 */
public class CassandraCpoAdapterFactory implements CpoAdapterFactory {

  private CassandraCpoAdapter cassandraCpoAdapter = null;

    /**
     * Constructs a CassandraCpoAdapterFactory
     *
     * @param cassandraCpoAdapter A CassandraCpoAdapter
     */
  public CassandraCpoAdapterFactory(CassandraCpoAdapter cassandraCpoAdapter) {
    this.cassandraCpoAdapter = cassandraCpoAdapter;
  }

  @Override
  public CpoAdapter getCpoAdapter() throws CpoException {
    return cassandraCpoAdapter;
  }

  @Override
  public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    throw new UnsupportedOperationException();
  }

  @Override
  public CpoXaResource getCpoXaAdapter() throws CpoException {
    throw new UnsupportedOperationException();
  }
}
