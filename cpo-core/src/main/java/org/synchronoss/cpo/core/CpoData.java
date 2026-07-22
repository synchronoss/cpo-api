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

/**
 * {@code CpoData} is the binding between a single bean attribute and the mechanics needed to move
 * its value into and out of the datastore: invoking the bean's getter/setter reflectively and
 * applying any configured {@code CpoTransform} on the way in or out.
 *
 * @author dberry
 */
public interface CpoData {

  /**
   * Invokes the bean's getter for the bound attribute.
   *
   * @return the value returned by the attribute's getter
   * @throws CpoException if the getter cannot be invoked
   */
  Object invokeGetter() throws CpoException;

  /**
   * Invokes the bean's setter for the bound attribute.
   *
   * @param instanceObject the value to pass to the attribute's setter
   * @throws CpoException if the setter cannot be invoked
   */
  void invokeSetter(Object instanceObject) throws CpoException;

  /**
   * Transforms a value read from the datastore into the form expected by the bean attribute,
   * applying the attribute's configured transform (if any).
   *
   * @param datasourceObject the raw value read from the datastore
   * @return the value to pass to the bean's setter, transformed if a transform is configured
   * @throws CpoException if the transform fails
   */
  Object transformIn(Object datasourceObject) throws CpoException;

  /**
   * Transforms a value read from the bean attribute into the form expected by the datastore,
   * applying the attribute's configured transform (if any).
   *
   * @param attributeObject the raw value read from the bean's getter
   * @return the value to bind to the datastore statement, transformed if a transform is configured
   * @throws CpoException if the transform fails
   */
  Object transformOut(Object attributeObject) throws CpoException;
}
