package org.synchronoss.cpo.core.meta.domain;

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

import org.synchronoss.cpo.core.meta.bean.CpoClassBean;

/**
 * Represents a single bound argument of a {@link CpoFunction}: a reference to the {@link
 * CpoAttribute} whose value is supplied for one of the function's bind markers. Setting the
 * attribute also takes this argument's {@link #getName() name} from the attribute's Java name.
 *
 * @author dberry
 */
public class CpoArgument extends CpoClassBean {

  private static final long serialVersionUID = 1L;

  /** The attribute bound to this argument. */
  CpoAttribute attribute = null;

  /** Creates an empty instance. */
  public CpoArgument() {}

  /**
   * Gets the attribute bound to this argument.
   *
   * @return the bound attribute
   */
  public CpoAttribute getAttribute() {
    return attribute;
  }

  /**
   * Sets the attribute bound to this argument, and updates this argument's name to match the
   * attribute's Java name.
   *
   * @param attribute the attribute to bind
   */
  public void setAttribute(CpoAttribute attribute) {
    this.attribute = attribute;
    if (attribute != null) setName(attribute.getJavaName());
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns this argument's {@link #getDescription() description}.
   */
  @Override
  public String toString() {
    return this.getDescription();
  }

  /**
   * Gets the full field-by-field string representation of this argument, as produced by {@link
   * CpoClassBean#toString()}.
   *
   * @return the full string representation
   */
  public String toStringFull() {
    return super.toString();
  }
}
