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

/**
 * Builds the contents of a nested where-clause group passed to {@link
 * CpoWhereBuilder#and(CpoWhereGroup)} or {@link CpoWhereBuilder#or(CpoWhereGroup)}.
 *
 * <p>A plain {@link java.util.function.Consumer} can't be used here since {@link CpoWhereBuilder}'s
 * chain methods declare {@code throws CpoException}.
 *
 * @author david berry
 */
@FunctionalInterface
public interface CpoWhereGroup {

  /**
   * Populates the nested group's conditions using the given builder.
   *
   * @param group the builder for the nested group's conditions
   * @throws CpoException if a condition cannot be added
   */
  void build(CpoWhereBuilder group) throws CpoException;
}
