package org.synchronoss.cpo.core.meta.bean;

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
 * Plain-data holder for the fields of a CPO function as loaded from meta XML: its name, native
 * expression (SQL/CQL), and description. {@link org.synchronoss.cpo.core.meta.domain.CpoFunction}
 * extends this with the runtime behavior (bound arguments).
 *
 * @author dberry
 */
public class CpoFunctionBean implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Properties
   */
  /** The name of this function. */
  private String name;

  /** The native (SQL/CQL) expression this function evaluates to. */
  private String expression;

  /** The description of this function. */
  private String description;

  /** Creates an empty instance. */
  public CpoFunctionBean() {}

  /*
   * Getters and Setters
   */

  /**
   * Gets the name of this function.
   *
   * @return the function name
   */
  public String getName() {
    return name;
  }

  /**
   * Sets the name of this function.
   *
   * @param name the function name
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Gets the human-readable description of this function, as loaded from the meta XML.
   *
   * @return the description, or {@code null} if none was specified
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the human-readable description of this function.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the native (SQL/CQL) expression executed by this function.
   *
   * @return the native expression
   */
  public String getExpression() {
    return expression;
  }

  /**
   * Sets the native (SQL/CQL) expression executed by this function.
   *
   * @param expression the native expression
   */
  public void setExpression(String expression) {
    this.expression = expression;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("expression = " + getExpression() + "\n");
    str.append("description = " + getDescription() + "\n");
    return str.toString();
  }

  @Override
  public int hashCode() {
    int result = 0;
    result = 31 * result + getClass().getName().hashCode();
    result = 31 * result + (getExpression() != null ? getExpression().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    CpoFunctionBean that = (CpoFunctionBean) o;

    if (getExpression() != null
        ? !getExpression().equals(that.getExpression())
        : that.getExpression() != null) return false;

    return getDescription() != null
        ? getDescription().equals(that.getDescription())
        : that.getDescription() == null;
  }
}
