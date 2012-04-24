/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo;

/**
 * CpoWhere is an interface for specifying the where clause to filter objects that are returned from the Datasource.
 *
 * @author david berry
 */
public interface CpoWhere {

  static final int COMP_NONE = -1;
  static final int COMP_EQ = 0;
  static final int COMP_LT = 1;
  static final int COMP_GT = 2;
  static final int COMP_NEQ = 3;
  static final int COMP_IN = 4;
  static final int COMP_LIKE = 5;
  static final int COMP_LTEQ = 6;
  static final int COMP_GTEQ = 7;
  static final int COMP_EXISTS = 8;
  static final int COMP_ISNULL = 9;
  static final int LOGIC_NONE = -1;
  static final int LOGIC_AND = 0;
  static final int LOGIC_OR = 1;

  public void setComparison(int comp);

  public int getComparison();

  public void setLogical(int log);

  public int getLogical();

  public void setAttribute(String attr);

  public String getAttribute();

  public void setRightAttribute(String attr);

  public String getRightAttribute();

  public void setValue(Object val);

  public Object getValue();

  public boolean getNot();

  public void setNot(boolean b);

  public void addWhere(CpoWhere cw) throws CpoException;

  public void setAttributeFunction(String s);

  public String getAttributeFunction();

  public void setValueFunction(String s);

  public String getValueFunction();

  public void setRightAttributeFunction(String s);

  public String getRightAttributeFunction();

  public void setStaticValue(String staticValue);

  public String getStaticValue();

  public boolean isLeaf();

  /**
   * Gets a string representing the name of this instance of the CpoOrderBy
   *
   * @return String The name of the CpoOrderBy
   */
  public String getName();

  /**
   * Sets a string representing the name of this instance of the CpoOrderBy
   *
   * @param s The name of the CpoOrderBy
   */
  public void setName(String s);
}
