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

import java.util.Collection;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;

/**
 * Default {@link CpoWhere} implementation: a bindable where-clause node that is also a {@link
 * Node}, allowing leaf comparisons to be chained into branches via {@link #addWhere(CpoWhere)} and
 * rendered to native SQL/CQL text via {@link #toString(CpoClass)}.
 *
 * @author david berry
 */
public class BindableCpoWhere extends Node implements CpoWhere {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  static final String[] comparisons = {
    "=", // COMP_EQ
    "<", // COMP_LT
    ">", // COMP_GT
    "<>", // COMP_NEQ
    "IN", // COMP_IN
    "LIKE", // COMP_LIKE
    "<=", // COMP_LTEQ
    ">=", // COMP_GTEQ
    "EXISTS", // COMP_EXISTS
    "IS NULL" // COMP_ISNULL
  };
  static final String[] logicals = {
    "AND", // LOGIC_AND
    "OR" // LOGIC_OR
  };

  /** The comparison operator applied between the attribute and value. */
  private Comparison comparison = Comparison.NONE;

  /** The logical operator joining this clause to the next one added via {@link #addWhere}. */
  private Logical logical = Logical.NONE;

  /** The left-hand bean attribute name. */
  private String attribute = null;

  /** The right-hand bean attribute name, when comparing two attributes. */
  private String rightAttribute = null;

  /** The literal comparison value. */
  private Object value = null;

  /** Native function applied to the left-hand attribute before comparison. */
  private String attributeFunction = null;

  /** Native function applied to the right-hand attribute before comparison. */
  private String rightAttributeFunction = null;

  /** Native function applied to the comparison value before comparison. */
  private String valueFunction = null;

  /** Whether the comparison result is negated. */
  private boolean not = false;

  /** A literal, unescaped value inserted directly into the native expression. */
  private String staticValue_ = null;

  /** The name of this where clause node. */
  private String name = "__CPO_WHERE__";

  /**
   * Creates a leaf where clause comparing the named attribute to a literal value, joined to a
   * subsequent clause (if any) by the given logical operator.
   *
   * @param <T> the type of the comparison value
   * @param logical the logical operator joining this clause to the next one added
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   */
  public <T> BindableCpoWhere(Logical logical, String attr, Comparison comp, T value) {
    setLogical(logical);
    setAttribute(attr);
    setComparison(comp);
    setValue(value);
  }

  /**
   * Creates a leaf where clause comparing the named attribute to a literal value, optionally
   * negated, joined to a subsequent clause (if any) by the given logical operator.
   *
   * @param <T> the type of the comparison value
   * @param logical the logical operator joining this clause to the next one added
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @param not {@code true} to negate the comparison, {@code false} otherwise
   */
  public <T> BindableCpoWhere(Logical logical, String attr, Comparison comp, T value, boolean not) {
    setLogical(logical);
    setAttribute(attr);
    setComparison(comp);
    setValue(value);
    setNot(not);
  }

  /** Creates an empty where clause with no comparison, attribute, or value set. */
  public BindableCpoWhere() {}

  /** {@inheritDoc} */
  @Override
  public void setComparison(Comparison comparison) {
    this.comparison = comparison;
  }

  /** {@inheritDoc} */
  @Override
  public Comparison getComparison() {
    return this.comparison;
  }

  /** {@inheritDoc} */
  @Override
  public void setLogical(Logical logical) {
    this.logical = logical;
  }

  /** {@inheritDoc} */
  @Override
  public Logical getLogical() {
    return this.logical;
  }

  /** {@inheritDoc} */
  @Override
  public void setAttribute(String s) {
    this.attribute = s;
  }

  /** {@inheritDoc} */
  @Override
  public String getAttribute() {
    return this.attribute;
  }

  /** {@inheritDoc} */
  @Override
  public void setRightAttribute(String s) {
    this.rightAttribute = s;
  }

  /** {@inheritDoc} */
  @Override
  public String getRightAttribute() {
    return this.rightAttribute;
  }

  /** {@inheritDoc} */
  @Override
  public void setValue(Object s) {
    this.value = s;
  }

  /** {@inheritDoc} */
  @Override
  public Object getValue() {
    return this.value;
  }

  /** {@inheritDoc} */
  @Override
  public void setStaticValue(String staticValue) {
    this.staticValue_ = staticValue;
  }

  /** {@inheritDoc} */
  @Override
  public String getStaticValue() {
    return this.staticValue_;
  }

  /** {@inheritDoc} */
  @Override
  public boolean getNot() {
    return this.not;
  }

  /** {@inheritDoc} */
  @Override
  public void setNot(boolean b) {
    this.not = b;
  }

  /**
   * Builds the native (SQL/CQL) fragment for this where clause against the given class's
   * attribute-to-column mapping. Unlike {@link #toString()}, this resolves attribute names to
   * datastore column names and does not require the class to declare the attribute (unresolved
   * names are used verbatim, e.g. as raw column names).
   *
   * @param cpoClass the class metadata used to resolve attributes to datastore columns
   * @return the native expression fragment for this clause
   */
  public String toString(CpoClass cpoClass) {
    StringBuilder sb = new StringBuilder();
    CpoAttribute cpoAttribute = null;

    if (getLogical() != Logical.NONE) {
      sb.append(" ");
      sb.append(getLogical().operator);
    } else if (!hasParent()) {
      // This is the root where clause
      sb.append("WHERE");
    }

    if (getNot()) {
      sb.append(" NOT");
    }

    if (getAttribute() != null) {
      if (!sb.isEmpty()) {
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
          sb.append(
              buildFunction(
                  getAttributeFunction(), cpoAttribute.getJavaName(), fullyQualifiedColumn));
        } else {
          sb.append(getAttributeFunction());
        }
      } else {
        sb.append(fullyQualifiedColumn);
      }
    }

    if (getComparison() != Comparison.NONE) {
      sb.append(" ");
      sb.append(getComparison().operator);
    }

    if (getComparison() != Comparison.ISNULL
        && (getValue() != null || getRightAttribute() != null || getStaticValue() != null)) {
      sb.append(" ");

      if (getValue() != null) {
        if (getValueFunction() != null) {
          if (cpoAttribute == null) {
            cpoAttribute = cpoClass.getAttributeJava(getRightAttribute());
          }
          sb.append(
              buildFunction(
                  getValueFunction(),
                  getAttributeName(cpoAttribute, getAttribute(), getRightAttribute()),
                  "?"));
        } else if (getComparison() == Comparison.IN && getValue() instanceof Collection) {
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
          sb.append(
              buildFunction(
                  getRightAttributeFunction(),
                  getAttributeName(cpoAttribute, getAttribute(), getRightAttribute()),
                  fullyQualifiedColumn));
        } else {
          sb.append(fullyQualifiedColumn);
        }
      } else if (getStaticValue() != null) {
        sb.append(getStaticValue());
      }
    }
    return sb.toString();
  }

  private String getAttributeName(
      CpoAttribute attribute, String leftAttribute, String rightAttribute) {
    String attrName = null;

    if (attribute != null) {
      attrName = attribute.getJavaName();
    }

    if (attrName == null && leftAttribute != null) {
      attrName = leftAttribute;
    }

    if (attrName == null && rightAttribute != null) {
      attrName = rightAttribute;
    }

    return attrName;
  }

  /**
   * {@inheritDoc}
   *
   * <p>The added clause must itself be a {@link Node} (all built-in {@code CpoWhere}
   * implementations are); it is attached as a child node of this instance.
   */
  @Override
  public void addWhere(CpoWhere cw) throws CpoException {
    try {
      this.addChild((Node) cw);
    } catch (ChildNodeException cne) {
      throw new CpoException("Error Adding Where Statement");
    }
  }

  /** {@inheritDoc} */
  @Override
  public void setAttributeFunction(String s) {
    this.attributeFunction = s;
  }

  /** {@inheritDoc} */
  @Override
  public String getAttributeFunction() {
    return this.attributeFunction;
  }

  /** {@inheritDoc} */
  @Override
  public void setValueFunction(String s) {
    this.valueFunction = s;
  }

  /** {@inheritDoc} */
  @Override
  public String getValueFunction() {
    return this.valueFunction;
  }

  /** {@inheritDoc} */
  @Override
  public void setRightAttributeFunction(String s) {
    this.rightAttributeFunction = s;
  }

  /** {@inheritDoc} */
  @Override
  public String getRightAttributeFunction() {
    return this.rightAttributeFunction;
  }

  private String buildFunction(String function, String match, String value) {
    StringBuilder sb = new StringBuilder();
    int attrOffset;
    int fromIndex = 0;

    if (function != null && !function.isEmpty()) {
      while ((attrOffset = function.indexOf(match, fromIndex)) != -1) {
        sb.append(function, fromIndex, attrOffset);
        sb.append(value);
        fromIndex = attrOffset + match.length();
      }
      sb.append(function.substring(fromIndex));
    }

    return sb.toString();
  }

  /**
   * Gets the datastore column name for the given attribute. Subclasses may override this to further
   * qualify the column name (e.g. with a table alias).
   *
   * @param attribute the attribute to resolve
   * @return the datastore column name for {@code attribute}
   */
  protected String buildColumnName(CpoAttribute attribute) {
    return attribute.getDataName();
  }

  /** {@inheritDoc} */
  @Override
  public String getName() {
    return name;
  }

  /** {@inheritDoc} */
  @Override
  public void setName(String name) {
    this.name = name;
  }
}
