package org.synchronoss.cpo;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

public class CpoNativeFunction {

  private String marker = null;
  private String expession = null;

  public CpoNativeFunction() {}

  public CpoNativeFunction(String marker, String text) {
    this.marker = marker;
    this.expession = text;
  }

  public void setMarker(String marker) {
    this.marker = marker;
  }

  public String getMarker() {
    return this.marker;
  }

  public void setExpression(String expession) {
    this.expession = expession;
  }

  public String getExpression() {
    return this.expession;
  }
}
