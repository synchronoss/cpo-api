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

import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;

/**
 * Default {@link CpoOrderBy} implementation: an immutable, bindable sort clause on a single
 * attribute, optionally wrapped in a native datastore function and associated with a custom marker
 * for multi-clause substitution.
 *
 * @author david berry
 */
public class BindableCpoOrderBy implements CpoOrderBy {

  private boolean ascending;
  private String attribute;
  private String function;
  private String marker = DEFAULT_MARKER;

  @SuppressWarnings("unused")
  private BindableCpoOrderBy() {}

  /**
   * Creates an order-by clause on the given attribute using the {@link #DEFAULT_MARKER}.
   *
   * @param attr the name of the attribute to sort by
   * @param asc {@code true} to sort ascending, {@code false} to sort descending
   */
  public BindableCpoOrderBy(String attr, boolean asc) {
    ascending = asc;
    attribute = attr;
    function = null;
  }

  /**
   * Creates an order-by clause on the given attribute using a custom marker, for use when more than
   * one order-by expression must be substituted into the same native expression.
   *
   * @param marker the marker string this clause replaces in the expression
   * @param attr the name of the attribute to sort by
   * @param asc {@code true} to sort ascending, {@code false} to sort descending
   */
  public BindableCpoOrderBy(String marker, String attr, boolean asc) {
    this.marker = marker;
    ascending = asc;
    attribute = attr;
    function = null;
  }

  /**
   * Creates an order-by clause on the given attribute, wrapped in a native datastore function,
   * using the {@link #DEFAULT_MARKER}.
   *
   * @param attr the name of the attribute to sort by
   * @param asc {@code true} to sort ascending, {@code false} to sort descending
   * @param func the native function expression to apply to the attribute (e.g. {@code
   *     "upper(attribute_name)"})
   */
  public BindableCpoOrderBy(String attr, boolean asc, String func) {
    ascending = asc;
    attribute = attr;
    function = func;
  }

  /**
   * Creates an order-by clause on the given attribute, wrapped in a native datastore function,
   * using a custom marker, for use when more than one order-by expression must be substituted into
   * the same native expression.
   *
   * @param marker the marker string this clause replaces in the expression
   * @param attr the name of the attribute to sort by
   * @param asc {@code true} to sort ascending, {@code false} to sort descending
   * @param func the native function expression to apply to the attribute (e.g. {@code
   *     "upper(attribute_name)"})
   */
  public BindableCpoOrderBy(String marker, String attr, boolean asc, String func) {
    this.marker = marker;
    ascending = asc;
    attribute = attr;
    function = func;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getAscending() {
    return this.ascending;
  }

  /** {@inheritDoc} */
  @Override
  public String getAttribute() {
    return this.attribute;
  }

  /** {@inheritDoc} */
  @Override
  public String getFunction() {
    return this.function;
  }

  /** {@inheritDoc} */
  @Override
  public String toString(CpoClass cpoClass) throws CpoException {
    StringBuilder sb = new StringBuilder();
    String column;
    int attrOffset;
    int fromIndex = 0;

    if (attribute != null && !attribute.isEmpty()) {
      CpoAttribute jdbcAttribute = cpoClass.getAttributeJava(attribute);
      if (jdbcAttribute == null) {
        throw new CpoException(attribute);
      }
      sb.append(" ");

      column = jdbcAttribute.getDataName();
      if (column != null && !column.isEmpty()) {
        if (function != null && !function.isEmpty()) {
          while ((attrOffset = function.indexOf(attribute, fromIndex)) != -1) {
            sb.append(function.substring(0, attrOffset));
            sb.append(column);
            fromIndex += attrOffset + attribute.length();
          }
          sb.append(function.substring(fromIndex));
        } else {
          sb.append(column);
        }
      }

      if (this.getAscending()) {
        sb.append(" ASC");
      } else {
        sb.append(" DESC");
      }
    }

    return sb.toString();
  }

  /** {@inheritDoc} */
  @Override
  public String getMarker() {
    return marker;
  }
}
