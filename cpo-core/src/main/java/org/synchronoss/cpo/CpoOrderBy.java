package org.synchronoss.cpo;

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

import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 * CpoOrderBy is an interface for specifying the sort order in which objects are returned from the
 * Datasource.
 *
 * @author david berry
 */
public interface CpoOrderBy {

  String DEFAULT_MARKER = "__CPO_ORDERBY__";

  /**
   * Gets the boolean that determines if the objects will be returned from from the CpoAdapter in
   * Ascending order or Descending order
   *
   * @return boolean true if it is to sort in Ascensing Order false if it is to be sorted in
   *     Descending Order
   */
  boolean getAscending();

  /**
   * Gets the name of the attribute that is to be used to sort the results from the CpoAdapter.
   *
   * @return String The name of the attribute
   */
  String getAttribute();

  /**
   * Gets a string representing a datasource specific function call that must be applied to the
   * attribute that will be used for sorting.
   *
   * <p>i.e. - "upper(attribute_name)"
   *
   * @return String The name of the function
   */
  String getFunction();

  /**
   * Gets the string marker that this cpoOrderBy will search for in the expression to replace
   *
   * @return String The marker of the CpoOrderBy
   */
  String getMarker();

  /**
   * @param cpoClass The cpoClass
   * @return the string that will be added into the expression
   * @throws CpoException an exception
   */
  String toString(CpoClass cpoClass) throws CpoException;
}
