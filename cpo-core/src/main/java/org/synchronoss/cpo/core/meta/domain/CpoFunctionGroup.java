package org.synchronoss.cpo.core.meta.domain;

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

import java.util.ArrayList;
import java.util.List;
import org.synchronoss.cpo.core.meta.bean.CpoFunctionGroupBean;

/**
 * Runtime metadata for a named group of {@link CpoFunction}s that together implement one CRUD
 * operation (e.g. all the INSERT functions named {@code "cpo_default"}) for a {@link CpoClass}.
 *
 * @author dberry
 */
public class CpoFunctionGroup extends CpoFunctionGroupBean implements Comparable<CpoFunctionGroup> {

  private static final long serialVersionUID = 1L;

  /** The functions in this group. */
  List<CpoFunction> functions = new ArrayList<>();

  /** Creates an empty instance. */
  public CpoFunctionGroup() {}

  /**
   * Gets the functions in this group.
   *
   * @return the functions in this group
   */
  public List<CpoFunction> getFunctions() {
    return functions;
  }

  /**
   * Appends a function to this group. A no-op if {@code function} is {@code null}.
   *
   * @param function the function to add
   */
  public void addFunction(CpoFunction function) {
    if (function != null) {
      functions.add(function);
    }
  }

  /**
   * Removes a function from this group.
   *
   * @param function the function to remove
   * @return {@code true} if the function was found and removed, {@code false} otherwise
   */
  public boolean removeFunction(CpoFunction function) {
    if (function != null) {
      return functions.remove(function);
    }
    return false;
  }

  /** Removes all functions from this group. */
  public void clearFunctions() {
    functions.clear();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns this group's {@link #getType() type} and {@link #getName() name}.
   */
  @Override
  public String toString() {
    return this.getType() + " - " + this.getName();
  }

  /**
   * Gets the full field-by-field string representation of this function group, as produced by
   * {@link CpoFunctionGroupBean#toString()}.
   *
   * @return the full string representation
   */
  public String toStringFull() {
    return super.toString();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Orders function groups by their {@link #toString()} representation (type, then name).
   */
  @Override
  public int compareTo(CpoFunctionGroup fg) {
    return this.toString().compareTo(fg.toString());
  }
}
