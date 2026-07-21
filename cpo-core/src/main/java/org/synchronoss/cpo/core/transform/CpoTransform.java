package org.synchronoss.cpo.core.transform;

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

import org.synchronoss.cpo.core.CpoException;

/**
 * {@code CpoTransform} converts an attribute's value between the raw form stored/read at the
 * datastore and the form exposed on the Java bean, e.g. clob-to-string or gzip compression.
 * Implementations are configured per-attribute in the meta XML and instantiated reflectively, so
 * they must provide a public no-arg constructor.
 *
 * @param <D> the raw datastore-side type
 * @param <J> the Java bean-side type
 * @author Michael Bellomo
 * @since 9/19/10
 */
public interface CpoTransform<D, J> {

  /**
   * Converts a raw datastore value into the bean-side representation.
   *
   * @param inObject the raw value read from the datastore
   * @return the value to set on the bean attribute
   * @throws CpoException if the value cannot be converted
   */
  J transformIn(D inObject) throws CpoException;

  /**
   * Converts a bean attribute's value into the raw datastore-side representation.
   *
   * @param attributeObject the value read from the bean attribute
   * @return the value to bind to the datastore
   * @throws CpoException if the value cannot be converted
   */
  D transformOut(J attributeObject) throws CpoException;
}
