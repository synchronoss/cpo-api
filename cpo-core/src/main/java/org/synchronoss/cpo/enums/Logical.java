package org.synchronoss.cpo.enums;

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

/** Logical operators for where clauses */
public enum Logical {
  /** No operator */
  NONE("NONE"),
  /** Logical AND operator */
  AND("AND"),
  /** Logical OR operator */
  OR("OR");

  /** The string operator in this enum */
  public final String operator;

  Logical(String operator) {
    this.operator = operator;
  }
}
