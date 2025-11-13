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
package org.synchronoss.cpo.enums;

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
