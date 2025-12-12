package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.CpoTrxAdapter;
import org.synchronoss.cpo.core.jta.CpoXaResource;

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
