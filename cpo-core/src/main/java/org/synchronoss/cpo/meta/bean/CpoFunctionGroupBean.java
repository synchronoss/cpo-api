package org.synchronoss.cpo.meta.bean;

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

public class CpoFunctionGroupBean extends CpoClassBean {

  /*
   * Properties
   */
  private String type;

  public CpoFunctionGroupBean() {}

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  @Override
  public int hashCode() {
    int result = 0;
    result = 31 * result + super.hashCode();
    result = 31 * result + (getType() != null ? getType().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append(super.toString());
    str.append("type = " + getType() + "\n");
    return str.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    CpoFunctionGroupBean that = (CpoFunctionGroupBean) o;

    if (!super.equals(that)) return false;

    return getType() != null ? getType().equals(that.getType()) : that.getType() == null;
  }
}
