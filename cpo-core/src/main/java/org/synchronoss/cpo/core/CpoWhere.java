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

import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;

/**
 * `CpoWhere` is an interface for specifying the where clause to filter objects that are returned
 * from the Datasource.
 *
 * <p>A `CpoWhere` node is either a *leaf*, comparing an attribute to a value or another attribute,
 * or a *branch*, formed by chaining leaves/branches together with {@link #addWhere(CpoWhere)} and a
 * {@link Logical} operator. Use {@link #isLeaf()} to tell which kind a given instance is.
 *
 * @author david berry
 */
public interface CpoWhere {

  /**
   * Sets the {@link Comparison} operator (e.g. equals, greater-than) used to compare the attribute
   * against the value or right-hand attribute.
   *
   * @param comparison the comparison operator for this where clause
   */
  void setComparison(Comparison comparison);

  /**
   * Gets the {@link Comparison} operator used to compare the attribute against the value or
   * right-hand attribute.
   *
   * @return the comparison operator for this where clause
   */
  Comparison getComparison();

  /**
   * Sets the {@link Logical} operator (e.g. AND, OR) used to join this where clause with the next
   * one added via {@link #addWhere(CpoWhere)}.
   *
   * @param logical the logical operator joining this clause to the next
   */
  void setLogical(Logical logical);

  /**
   * Gets the {@link Logical} operator used to join this where clause with the next one added via
   * {@link #addWhere(CpoWhere)}.
   *
   * @return the logical operator joining this clause to the next
   */
  Logical getLogical();

  /**
   * Sets the name of the bean attribute on the left-hand side of the comparison.
   *
   * @param attr the attribute name
   */
  void setAttribute(String attr);

  /**
   * Gets the name of the bean attribute on the left-hand side of the comparison.
   *
   * @return the attribute name
   */
  String getAttribute();

  /**
   * Sets the name of the bean attribute to compare against, when comparing two attributes rather
   * than an attribute and a literal value.
   *
   * @param attr the right-hand attribute name
   */
  void setRightAttribute(String attr);

  /**
   * Gets the name of the bean attribute to compare against, when comparing two attributes rather
   * than an attribute and a literal value.
   *
   * @return the right-hand attribute name
   */
  String getRightAttribute();

  /**
   * Sets the literal value to compare the attribute against.
   *
   * @param val the comparison value
   */
  void setValue(Object val);

  /**
   * Gets the literal value the attribute is compared against.
   *
   * @return the comparison value
   */
  Object getValue();

  /**
   * Gets whether this where clause's comparison result is negated.
   *
   * @return `true` if the comparison is negated (NOT), `false` otherwise
   */
  boolean getNot();

  /**
   * Sets whether this where clause's comparison result should be negated.
   *
   * @param b `true` to negate the comparison (NOT), `false` otherwise
   */
  void setNot(boolean b);

  /**
   * Chains another where clause onto this one, joined by this instance's {@link Logical} operator,
   * turning this instance into a branch node.
   *
   * @param cw the where clause to add
   * @throws CpoException if the where clause cannot be added
   */
  void addWhere(CpoWhere cw) throws CpoException;

  /**
   * Sets a native datastore function (e.g. `UPPER`) to apply to the left-hand attribute before
   * comparison.
   *
   * @param s the function expression to apply to the attribute
   */
  void setAttributeFunction(String s);

  /**
   * Gets the native datastore function applied to the left-hand attribute before comparison.
   *
   * @return the function expression applied to the attribute
   */
  String getAttributeFunction();

  /**
   * Sets a native datastore function (e.g. `UPPER`) to apply to the comparison value before
   * comparison.
   *
   * @param s the function expression to apply to the value
   */
  void setValueFunction(String s);

  /**
   * Gets the native datastore function applied to the comparison value before comparison.
   *
   * @return the function expression applied to the value
   */
  String getValueFunction();

  /**
   * Sets a native datastore function (e.g. `UPPER`) to apply to the right-hand attribute before
   * comparison.
   *
   * @param s the function expression to apply to the right-hand attribute
   */
  void setRightAttributeFunction(String s);

  /**
   * Gets the native datastore function applied to the right-hand attribute before comparison.
   *
   * @return the function expression applied to the right-hand attribute
   */
  String getRightAttributeFunction();

  /**
   * Sets a literal, unescaped value to use in place of a bound comparison value, inserted directly
   * into the native expression.
   *
   * @param staticValue the static (literal) value text
   */
  void setStaticValue(String staticValue);

  /**
   * Gets the literal, unescaped value used in place of a bound comparison value.
   *
   * @return the static (literal) value text
   */
  String getStaticValue();

  /**
   * Gets whether this instance is a leaf node (a single comparison) rather than a branch node
   * formed by {@link #addWhere(CpoWhere)}.
   *
   * @return `true` if this is a leaf comparison node, `false` if it is a branch
   */
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
