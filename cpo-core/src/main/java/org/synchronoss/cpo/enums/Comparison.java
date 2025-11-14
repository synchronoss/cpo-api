package org.synchronoss.cpo.enums;

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

/** The comparison operators for where clauses */
public enum Comparison {
  /** No comparison operator defined */
  NONE("NONE"),
  /** The equals operator */
  EQ("="),
  /** The less than operator */
  LT("<"),
  /** The greater than operator */
  GT(">"),
  /** The not equals operator */
  NEQ("<>"),
  /** The in operator */
  IN("IN"),
  /** The like operator */
  LIKE("LIKE"),
  /** The less than or equal to operator */
  LTEQ("<="),
  /** The greater than or equal to operator */
  GTEQ(">="),
  /** The exists operator */
  EXISTS("EXISTS"),
  /** The is null operator */
  ISNULL("IS NULL");

  /** The string operator in this enum */
  public final String operator;

  Comparison(String operator) {
    this.operator = operator;
  }
}
