package org.synchronoss.cpo.core.meta.bean;

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
 * Plain-data holder for a name and description, the common base for the CPO metadata bean classes
 * ({@code CpoClass}, {@code CpoArgument}, {@code CpoFunctionGroup}) that all identify themselves
 * primarily by name.
 *
 * @author dberry
 */
public class CpoClassBean implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Properties
   */
  /** The name of this metadata element. */
  private String name;

  /** The description of this metadata element. */
  private String description;

  /** Creates an empty instance. */
  public CpoClassBean() {}

  /*
   * Getters and Setters
   */

  /**
   * Gets the name of this metadata element.
   *
   * @return the name
   */
  public String getName() {
    return this.name;
  }

  /**
   * Sets the name of this metadata element.
   *
   * @param name the name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the human-readable description of this metadata element, as loaded from the meta XML.
   *
   * @return the description, or {@code null} if none was specified
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the human-readable description of this metadata element.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
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

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    CpoClassBean that = (CpoClassBean) o;

    if (getName() != null ? !getName().equals(that.getName()) : that.getName() != null)
      return false;

    return getDescription() != null
        ? getDescription().equals(that.getDescription())
        : that.getDescription() == null;
  }
}
