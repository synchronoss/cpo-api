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

/**
 * CpoWhere is an interface for specifying the where clause to filter objects that are returned from the Datasource.
 *
 * @author david berry
 */
public interface CpoWhere {

  int COMP_NONE = -1;
  int COMP_EQ = 0;
  int COMP_LT = 1;
  int COMP_GT = 2;
  int COMP_NEQ = 3;
  int COMP_IN = 4;
  int COMP_LIKE = 5;
  int COMP_LTEQ = 6;
  int COMP_GTEQ = 7;
  int COMP_EXISTS = 8;
  int COMP_ISNULL = 9;
  int LOGIC_NONE = -1;
  int LOGIC_AND = 0;
  int LOGIC_OR = 1;

  void setComparison(int comp);

  int getComparison();

  void setLogical(int log);

  int getLogical();

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
