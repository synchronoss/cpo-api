/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo;

import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 * CpoOrderBy is an interface for specifying the sort order in which objects are returned from the Datasource.
 *
 * @author david berry
 */
public interface CpoOrderBy {
  
  public static final String DEFAULT_MARKER = "__CPO_ORDERBY__";

  /**
   * Gets the boolean that determines if the objects will be returned from from the CpoAdapter in Ascending order or
   * Descending order
   *
   * @return boolean true if it is to sort in Ascensing Order false if it is to be sorted in Descending Order
   */
  public boolean getAscending();

  /**
   * Gets the name of the attribute that is to be used to sort the results from the CpoAdapter.
   *
   * @return String The name of the attribute
   */
  public String getAttribute();

  /**
   * Gets a string representing a datasource specific function call that must be applied to the attribute that will be
   * used for sorting.
   *
   * i.e. - "upper(attribute_name)"
   *
   * @return String The name of the function
   */
  public String getFunction();

  /**
   * Gets the string marker that this cpoOrderBy will search for in the expression to replace
   *
   * @return String The marker of the CpoOrderBy
   */
  public String getMarker();  
  
  /**
   * @param s returns the string that will be added into the expression 
   */
  public String toString(CpoClass cpoClass) throws CpoException;
}
