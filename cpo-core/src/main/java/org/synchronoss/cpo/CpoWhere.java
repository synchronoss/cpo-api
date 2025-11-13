/*
 * Copyright (C) 2003-2025 David E. Berry
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

import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;

/**
 * CpoWhere is an interface for specifying the where clause to filter objects that are returned from the Datasource.
 *
 * @author david berry
 */
public interface CpoWhere {

  void setComparison(Comparison comparison);

    Comparison getComparison();

  void setLogical(Logical logical);

    Logical getLogical();

  void setAttribute(String attr);

  String getAttribute();

  void setRightAttribute(String attr);

  String getRightAttribute();

  void setValue(Object val);

  Object getValue();

  boolean getNot();

  void setNot(boolean b);

  void addWhere(CpoWhere cw) throws CpoException;

  void setAttributeFunction(String s);

  String getAttributeFunction();

  void setValueFunction(String s);

  String getValueFunction();

  void setRightAttributeFunction(String s);

  String getRightAttributeFunction();

  void setStaticValue(String staticValue);

  String getStaticValue();

  boolean isLeaf();

  /**
   * Gets a string representing the name of this instance of the CpoOrderBy
   *
   * @return String The name of the CpoOrderBy
   */
  String getName();

  /**
   * Sets a string representing the name of this instance of the CpoOrderBy
   *
   * @param s The name of the CpoOrderBy
   */
  void setName(String s);
}
