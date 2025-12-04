package org.synchronoss.cpo.jta;

/*-
 * [[
 * core
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

import java.util.HashMap;
import javax.transaction.xa.Xid;

/** Created by dberry on 8/9/15. */
public class CpoXaStateMap<T> {

  // map of all seen XIDs
  private final HashMap<Xid, CpoXaState<T>> xidStateMap = new HashMap<>();

  // map of all xid State
  public HashMap<Xid, CpoXaState<T>> getXidStateMap() {
    return xidStateMap;
  }
}
