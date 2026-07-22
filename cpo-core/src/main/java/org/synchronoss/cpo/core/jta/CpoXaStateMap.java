package org.synchronoss.cpo.core.jta;

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

import java.util.HashMap;
import javax.transaction.xa.Xid;

/**
 * Simple holder for a {@link Xid}-to-{@link CpoXaState} map, one per family of XA resources.
 *
 * @param <T> the type of the underlying resource tracked by the held {@link CpoXaState} entries
 * @author dberry
 */
public class CpoXaStateMap<T> {

  // map of all seen XIDs
  private final HashMap<Xid, CpoXaState<T>> xidStateMap = new HashMap<>();

  /** Constructs an empty state map. */
  public CpoXaStateMap() {}

  /**
   * Gets the map of all transaction branches currently tracked.
   *
   * @return the map of all transaction branches currently tracked, keyed by {@link Xid}
   */
  public HashMap<Xid, CpoXaState<T>> getXidStateMap() {
    return xidStateMap;
  }
}
