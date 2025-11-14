package org.synchronoss.cpo;

/*-
 * #%L
 * core
 * %%
 * Copyright (C) 2003 - 2025 David E. Berry
 * %%
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
 * #L%
 */

import org.synchronoss.cpo.jta.CpoXaResource;

/** Created by dberry on 11/8/15. */
public interface CpoAdapterFactory {
  CpoAdapter getCpoAdapter() throws CpoException;

  CpoTrxAdapter getCpoTrxAdapter() throws CpoException;

  CpoXaResource getCpoXaAdapter() throws CpoException;
}
