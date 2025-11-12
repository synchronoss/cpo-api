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

import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;

import java.util.Collection;

/**
 * BindableCpoWhere is an interface for specifying the sort order in which objects are returned from the Datasource.
 *
 * @author david berry
 */
public class BindableCpoWhere extends Node implements CpoWhere {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  static final String[] comparisons = {
    "=", //COMP_EQ
    "<", //COMP_LT
    ">", //COMP_GT
    "<>", //COMP_NEQ
    "IN", //COMP_IN
    "LIKE", //COMP_LIKE
    "<=", //COMP_LTEQ
    ">=", //COMP_GTEQ
    "EXISTS", //COMP_EXISTS
    "IS NULL" //COMP_ISNULL
  };
  static final String[] logicals = {
    "AND", //LOGIC_AND
    "OR" //LOGIC_OR
  };
  private int comparison = CpoWhere.COMP_NONE;
  private int logical = CpoWhere.LOGIC_NONE;
  private String attribute = null;
  private String rightAttribute = null;
  private Object value = null;
  private String attributeFunction = null;
  private String rightAttributeFunction = null;
  private String valueFunction = null;
  private boolean not = false;
  private String staticValue_ = null;
  private String name = "__CPO_WHERE__";

  public <T> BindableCpoWhere(int logical, String attr, int comp, T value) {
    setLogical(logical);
    setAttribute(attr);
    setComparison(comp);
    setValue(value);
  }

  public <T> BindableCpoWhere(int logical, String attr, int comp, T value, boolean not) {
    setLogical(logical);
    setAttribute(attr);
    setComparison(comp);
    setValue(value);
    setNot(not);
  }

  public BindableCpoWhere() {
  }

  @Override
  public void setComparison(int i) {
    if (i < 0 || i >= comparisons.length) {
      this.comparison = CpoWhere.COMP_NONE;
    } else {
      this.comparison = i;
    }
  }

  @Override
  public int getComparison() {
    return this.comparison;
  }

  @Override
  public void setLogical(int i) {
    if (i < 0 || i >= logicals.length) {
      this.logical = CpoWhere.LOGIC_NONE;
    } else {
      this.logical = i;
    }
  }

  @Override
  public int getLogical() {
    return this.logical;
  }

  @Override
  public void setAttribute(String s) {
    this.attribute = s;
  }

  @Override
  public String getAttribute() {
    return this.attribute;
  }

  @Override
  public void setRightAttribute(String s) {
    this.rightAttribute = s;
  }

  @Override
  public String getRightAttribute() {
    return this.rightAttribute;
  }

  @Override
  public void setValue(Object s) {
    this.value = s;
  }

  @Override
  public Object getValue() {
    return this.value;
  }

  @Override
  public void setStaticValue(String staticValue) {
    this.staticValue_ = staticValue;
  }

  @Override
  public String getStaticValue() {
    return this.staticValue_;
  }

  @Override
  public boolean getNot() {
    return this.not;
  }

  @Override
  public void setNot(boolean b) {
    this.not = b;
  }

  public String toString(CpoClass cpoClass) throws CpoException {
    StringBuilder sb = new StringBuilder();
    CpoAttribute cpoAttribute = null;


    if (getLogical() != CpoWhere.LOGIC_NONE) {
      sb.append(" ");
      sb.append(logicals[getLogical()]);
    } else if (!hasParent()) {
      // This is the root where clause
      sb.append("WHERE");
    }

    if (getNot()) {
      sb.append(" NOT");
    }

    if (getAttribute() != null) {
      if (sb.length() > 0) {
        sb.append(" ");
      }
      String fullyQualifiedColumn;

      cpoAttribute = cpoClass.getAttributeJava(getAttribute());
      if (cpoAttribute == null) {
        // This is not an attribute on the cpo bean passed to the retrieveBeans method.
        // treat it as the column name
        fullyQualifiedColumn = getAttribute();
      } else {
        fullyQualifiedColumn = buildColumnName(cpoAttribute);
      }

      if (getAttributeFunction() != null) {
        if (cpoAttribute != null) {
          sb.append(buildFunction(getAttributeFunction(), cpoAttribute.getJavaName(), fullyQualifiedColumn));
        } else {
          sb.append(getAttributeFunction());
        }
      } else {
        sb.append(fullyQualifiedColumn);
      }
    }

    if (getComparison() != CpoWhere.COMP_NONE) {
      sb.append(" ");
      sb.append(comparisons[getComparison()]);
    }

    if (getComparison() != CpoWhere.COMP_ISNULL && (getValue() != null || getRightAttribute() != null || getStaticValue() != null)) {
      sb.append(" ");

      if (getValue() != null) {
        if (getValueFunction() != null) {
          if (cpoAttribute == null) {
            cpoAttribute = cpoClass.getAttributeJava(getRightAttribute());
          }
          sb.append(buildFunction(getValueFunction(), getAttributeName(cpoAttribute, getAttribute(), getRightAttribute()), "?"));
        } else if (getComparison() == CpoWhere.COMP_IN && getValue() instanceof Collection) {
          Collection coll = (Collection) getValue();
          sb.append("(");
          if (coll.size() > 0) {
            sb.append("?"); // add the parameter, we will bind it later.
            for (int i = 1; i < coll.size(); i++) {
              sb.append(", ?"); // add the parameter, we will bind it later.
            }
          }
          sb.append(")");
        } else {
          sb.append("?"); // add the parameter, we will bind it later.
        }
      } else if (getRightAttribute() != null) {
        cpoAttribute = cpoClass.getAttributeJava(getRightAttribute());
        String fullyQualifiedColumn;
        if (cpoAttribute == null) {
          fullyQualifiedColumn = getRightAttribute();
        } else {
          fullyQualifiedColumn = buildColumnName(cpoAttribute);
        }

        if (getRightAttributeFunction() != null) {
          sb.append(buildFunction(getRightAttributeFunction(), getAttributeName(cpoAttribute, getAttribute(), getRightAttribute()), fullyQualifiedColumn));
        } else {
          sb.append(fullyQualifiedColumn);
        }
      } else if (getStaticValue() != null) {
        sb.append(getStaticValue());
      }
    }
    return sb.toString();
  }

  private String getAttributeName(CpoAttribute jdbcAttribute, String leftAttribute, String rightAttribute) {
    String attrName = null;

    if (jdbcAttribute != null) {
      attrName = jdbcAttribute.getJavaName();
    }

    if (attrName == null && leftAttribute != null) {
      attrName = leftAttribute;
    }

    if (attrName == null && rightAttribute != null) {
      attrName = rightAttribute;
    }

    return attrName;
  }

  @Override
  public void addWhere(CpoWhere cw) throws CpoException {
    try {
      this.addChild((Node) cw);
    } catch (ChildNodeException cne) {
      throw new CpoException("Error Adding Where Statement");
    }
  }

  @Override
  public void setAttributeFunction(String s) {
    this.attributeFunction = s;
  }

  @Override
  public String getAttributeFunction() {
    return this.attributeFunction;
  }

  @Override
  public void setValueFunction(String s) {
    this.valueFunction = s;
  }

  @Override
  public String getValueFunction() {
    return this.valueFunction;
  }

  @Override
  public void setRightAttributeFunction(String s) {
    this.rightAttributeFunction = s;
  }

  @Override
  public String getRightAttributeFunction() {
    return this.rightAttributeFunction;
  }

  private String buildFunction(String function, String match, String value) {
    StringBuilder sb = new StringBuilder();
    int attrOffset;
    int fromIndex = 0;

    if (function != null && function.length() > 0) {
      while ((attrOffset = function.indexOf(match, fromIndex)) != -1) {
        sb.append(function.substring(0, attrOffset));
        sb.append(value);
        fromIndex += attrOffset + match.length();
      }
      sb.append(function.substring(fromIndex));
    }

    return sb.toString();
  }

  protected String buildColumnName(CpoAttribute attribute) {
    return attribute.getDataName();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }
}
