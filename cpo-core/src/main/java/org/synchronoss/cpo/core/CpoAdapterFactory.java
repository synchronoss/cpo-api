package org.synchronoss.cpo.core;

/*-
 * [[
 * core
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

import org.synchronoss.cpo.core.jta.CpoXaResource;

/**
 * Creates {@link CpoAdapter} instances (plain, transactional, or XA) for a single named data source
 * configuration.
 *
 * <p>Implementations are obtained from {@code CpoAdapterFactoryManager} by config name; each
 * factory is bound to one {@code dataConfig} entry from {@code cpoConfig.xml}.
 *
 * @author dberry
 */
public interface CpoAdapterFactory {

  /**
   * Gets a {@link CpoAdapter} that manages its own connections per call (no explicit transaction
   * control).
   *
   * @return a usable {@link CpoAdapter}
   * @throws CpoException if the adapter cannot be created
   */
  CpoAdapter getCpoAdapter() throws CpoException;

  /**
   * Gets a {@link CpoTrxAdapter} pinned to a single connection/session for explicit transaction
   * control (commit/rollback/close).
   *
   * @return a usable {@link CpoTrxAdapter}
   * @throws CpoException if the adapter cannot be created
   */
  CpoTrxAdapter getCpoTrxAdapter() throws CpoException;

  /**
   * Gets a {@link CpoXaResource} that participates in a JTA-managed distributed transaction.
   *
   * @return a usable {@link CpoXaResource}
   * @throws CpoException if the resource cannot be created
   */
  CpoXaResource getCpoXaAdapter() throws CpoException;
}
