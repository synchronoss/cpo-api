package org.synchronoss.cpo.meta.bean;

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

public class CpoClassBean implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Properties
   */
  private String name;
  private String description;

  public CpoClassBean() {}

  /*
   * Getters and Setters
   */
  public String getName() {
    return this.name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CpoClassBean that = (CpoClassBean) o;

    if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null) {
      return false;
    }
    if (getDescription() != null
        ? !getDescription().equals(that.getDescription())
        : that.getDescription() != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = 0;
    result = 31 * result + getClass().getName().hashCode();
    result = 31 * result + (getName() != null ? getName().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("name = " + getName() + "\n");
    str.append("description = " + getDescription() + "\n");
    return str.toString();
  }
}
