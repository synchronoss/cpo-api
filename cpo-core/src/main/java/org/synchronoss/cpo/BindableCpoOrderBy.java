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

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoOrderBy;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 * BindableCpoOrderBy is an interface for specifying the sort order in which objects are returned from the Datasource.
 *
 * @author david berry
 */
public class BindableCpoOrderBy implements CpoOrderBy {

  private boolean ascending;
  private String attribute;
  private String function;
  private String marker = DEFAULT_MARKER;

  @SuppressWarnings("unused")
  private BindableCpoOrderBy() {
  }

  public BindableCpoOrderBy(String attr, boolean asc) {
    ascending = asc;
    attribute = attr;
    function = null;
  }

  public BindableCpoOrderBy(String marker, String attr, boolean asc) {
    this.marker = marker;
    ascending = asc;
    attribute = attr;
    function = null;
  }

  public BindableCpoOrderBy(String attr, boolean asc, String func) {
    ascending = asc;
    attribute = attr;
    function = func;
  }

  public BindableCpoOrderBy(String marker, String attr, boolean asc, String func) {
    this.marker = marker;
    ascending = asc;
    attribute = attr;
    function = func;
  }

  /**
   * Gets the boolean that determines if the objects will be returned from from the CpoAdapter in Ascending order or
   * Descending order
   *
   * @return boolean true if it is to sort in Ascensing Order false if it is to be sorted in Descending Order
   */
  @Override
  public boolean getAscending() {
    return this.ascending;
  }

  /**
   * Gets the name of the attribute that is to be used to sort the results from the CpoAdapter.
   *
   * @return String The name of the attribute
   */
  @Override
  public String getAttribute() {
    return this.attribute;
  }

  /**
   * Gets a string representing a datasource specific function call that must be applied to the attribute that will be
   * used for sorting.
   *
   * i.e. - "upper(attribute_name)"
   *
   * @return String The name of the function
   */
  @Override
  public String getFunction() {
    return this.function;
  }

  @Override
  public String toString(CpoClass cpoClass) throws CpoException {
    StringBuilder sb = new StringBuilder();
    String column;
    int attrOffset;
    int fromIndex = 0;

    if (attribute != null && attribute.length() > 0) {
      CpoAttribute jdbcAttribute = cpoClass.getAttributeJava(attribute);
      if (jdbcAttribute == null) {
        throw new CpoException(attribute);
      }
      sb.append(" ");

      column = jdbcAttribute.getDataName();
      if (column != null && column.length() > 0) {
        if (function != null && function.length() > 0) {
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

  @Override
  public String getMarker() {
    return marker;
  }

}
