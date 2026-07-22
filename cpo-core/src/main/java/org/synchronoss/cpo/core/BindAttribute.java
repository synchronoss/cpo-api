package org.synchronoss.cpo.core;

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

import org.synchronoss.cpo.core.meta.domain.CpoAttribute;

/**
 * {@code BindAttribute} pairs a single bind value produced while walking a {@link CpoWhere} tree
 * (see {@link BindableWhereBuilder}) with either the resolved {@link CpoAttribute} it belongs to,
 * or, when no matching bean attribute exists, the raw attribute name it was addressed by.
 *
 * @param cpoAttribute the resolved bean attribute this value should be bound to, or {@code null} if
 *     this instance was created with a raw name instead
 * @param bindObject the value to bind
 * @param name the raw attribute or column name this value should be bound to, or {@code null} if
 *     this instance was created with a resolved {@link CpoAttribute} instead
 * @author david.berry
 */
public record BindAttribute(CpoAttribute cpoAttribute, Object bindObject, String name) {

  /**
   * Creates an instance bound to a resolved bean attribute.
   *
   * @param cpoAttribute the attribute this value should be bound to
   * @param bindObject the value to bind
   */
  public BindAttribute(CpoAttribute cpoAttribute, Object bindObject) {
    this(cpoAttribute, bindObject, null);
  }

  /**
   * Creates an instance for a value that has no matching bean attribute, identified instead by name
   * (for example, a raw column name used directly in a where clause).
   *
   * @param name the attribute or column name this value should be bound to
   * @param bindObject the value to bind
   */
  public BindAttribute(String name, Object bindObject) {
    this(null, bindObject, name);
  }
}
