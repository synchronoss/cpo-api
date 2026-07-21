package org.synchronoss.cpo.core;

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

/**
 * Represents a native (datastore-specific) function expression bound to a marker, for embedding
 * SQL/CQL functions like {@code UPPER(?)} into a CPO where/order-by clause.
 *
 * @author david berry
 */
public class CpoNativeFunction {

  private String marker = null;
  private String expession = null;

  /** Creates an empty native function with no marker or expression set. */
  public CpoNativeFunction() {}

  /**
   * Creates a native function with the given marker and expression text.
   *
   * @param marker the bind marker this function is associated with
   * @param text the native expression text
   */
  public CpoNativeFunction(String marker, String text) {
    this.marker = marker;
    this.expession = text;
  }

  /**
   * Sets the bind marker this function is associated with.
   *
   * @param marker the bind marker
   */
  public void setMarker(String marker) {
    this.marker = marker;
  }

  /**
   * Gets the bind marker this function is associated with.
   *
   * @return the bind marker
   */
  public String getMarker() {
    return this.marker;
  }

  /**
   * Sets the native expression text.
   *
   * @param expession the native expression text
   */
  public void setExpression(String expession) {
    this.expession = expession;
  }

  /**
   * Gets the native expression text.
   *
   * @return the native expression text
   */
  public String getExpression() {
    return this.expession;
  }
}
