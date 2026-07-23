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
 * A fluent builder for assembling {@link CpoWhere} trees, including nested AND/OR groups, without
 * hand-placing {@link Logical} operators on leaves and group containers.
 *
 * <p>Building a {@code CpoWhere} tree by hand requires knowing that a leaf's {@link Logical} joins
 * it to the *previous sibling*, while a group container's {@link Logical} joins the *whole group*
 * to its previous sibling in the parent chain — an easy detail to invert with no compile-time
 * protection. This builder derives the correct placement from the order methods are called:
 *
 * <pre>{@code
 * CpoWhere where = CpoWhereBuilder.start(cpoAdapter)
 *     .where("id", Comparison.EQ, valObj)
 *     .and(g -> g.where("id", Comparison.EQ, valObj2).or("id", Comparison.EQ, valObj3))
 *     .build();
 * }</pre>
 *
 * <p>{@code where()} must be the first condition in a chain; {@code and()}/{@code or()} add
 * subsequent conditions or nested groups. Modifier methods ({@link #compareToAttribute}, {@link
 * #attributeFunction}, {@link #rightAttributeFunction}, {@link #valueFunction}, {@link
 * #staticValue}) apply to the condition most recently added. Misuse (calling a modifier before any
 * condition exists, calling {@code where()} more than once, or calling {@link #build()} with no
 * conditions) throws {@link IllegalStateException}, since these are programmer sequencing errors
 * rather than datastore failures.
 *
 * <p>Some function groups' own native expression already supplies a base {@code WHERE} clause (or
 * an always-true placeholder condition), and a run-time where must be interleaved onto it with an
 * explicit {@code AND}/{@code OR} rather than starting fresh. For that case, start the chain with
 * {@link #and(String, Comparison, Object)}/{@link #or(String, Comparison, Object)} (or their
 * group-taking overloads) instead of {@code where()} — or use the {@link #startAnd} / {@link
 * #startOr} convenience factories, which are equivalent to {@code start(adapter).and(...)} / {@code
 * start(adapter).or(...)}:
 *
 * <pre>{@code
 * CpoWhere where = CpoWhereBuilder.startAnd(cpoAdapter, "id", Comparison.EQ, valObj).build();
 * }</pre>
 *
 * A chain started this way must contain exactly that one top-level condition or group (nest further
 * conditions inside the group instead) — {@link #build()} then returns it directly, without the
 * wrapping container that a normal {@code where()}-started chain uses to render its own leading
 * {@code WHERE}, since that wrapper would otherwise duplicate the base clause already baked into
 * the query.
 *
 * <p>Instances are not thread-safe and are meant to be built up and discarded once {@link #build()}
 * is called.
 *
 * @author david berry
 */
public final class CpoWhereBuilder {

  private final CpoAdapter adapter;
  private final CpoWhere root;
  private CpoWhere lastAdded;
  private int conditionCount;

  private CpoWhereBuilder(CpoAdapter adapter) throws CpoException {
    this.adapter = adapter;
    this.root = adapter.newWhere();
  }

  /**
   * Starts a new where-clause chain.
   *
   * @param adapter the adapter used to create the underlying {@link CpoWhere} nodes
   * @return a new builder with no conditions yet
   * @throws CpoException if the underlying where clause cannot be created
   */
  public static CpoWhereBuilder start(CpoAdapter adapter) throws CpoException {
    return new CpoWhereBuilder(adapter);
  }

  /**
   * Starts a chain whose sole top-level condition is joined by AND, for interleaving with a query
   * whose own expression already supplies a base WHERE clause. Equivalent to {@code
   * start(adapter).and(attr, comp, value)}.
   *
   * @param <T> the type of the comparison value
   * @param adapter the adapter used to create the underlying {@link CpoWhere} nodes
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @return a new builder with that one condition already added
   * @throws CpoException if the underlying where clause cannot be created
   */
  public static <T> CpoWhereBuilder startAnd(
      CpoAdapter adapter, String attr, Comparison comp, T value) throws CpoException {
    return start(adapter).and(attr, comp, value);
  }

  /**
   * Starts a chain whose sole top-level condition is joined by AND, optionally negated, for
   * interleaving with a query whose own expression already supplies a base WHERE clause. Equivalent
   * to {@code start(adapter).and(attr, comp, value, not)}.
   *
   * @param <T> the type of the comparison value
   * @param adapter the adapter used to create the underlying {@link CpoWhere} nodes
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @param not {@code true} to negate the comparison
   * @return a new builder with that one condition already added
   * @throws CpoException if the underlying where clause cannot be created
   */
  public static <T> CpoWhereBuilder startAnd(
      CpoAdapter adapter, String attr, Comparison comp, T value, boolean not) throws CpoException {
    return start(adapter).and(attr, comp, value, not);
  }

  /**
   * Starts a chain whose sole top-level condition is a nested group joined by AND, for interleaving
   * with a query whose own expression already supplies a base WHERE clause. Equivalent to {@code
   * start(adapter).and(group)}.
   *
   * @param adapter the adapter used to create the underlying {@link CpoWhere} nodes
   * @param group populates the group's conditions
   * @return a new builder with that one group already added
   * @throws CpoException if the group or its conditions cannot be created
   */
  public static CpoWhereBuilder startAnd(CpoAdapter adapter, CpoWhereGroup group)
      throws CpoException {
    return start(adapter).and(group);
  }

  /**
   * Starts a chain whose sole top-level condition is joined by OR, for interleaving with a query
   * whose own expression already supplies a base WHERE clause. Equivalent to {@code
   * start(adapter).or(attr, comp, value)}.
   *
   * @param <T> the type of the comparison value
   * @param adapter the adapter used to create the underlying {@link CpoWhere} nodes
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @return a new builder with that one condition already added
   * @throws CpoException if the underlying where clause cannot be created
   */
  public static <T> CpoWhereBuilder startOr(
      CpoAdapter adapter, String attr, Comparison comp, T value) throws CpoException {
    return start(adapter).or(attr, comp, value);
  }

  /**
   * Starts a chain whose sole top-level condition is joined by OR, optionally negated, for
   * interleaving with a query whose own expression already supplies a base WHERE clause. Equivalent
   * to {@code start(adapter).or(attr, comp, value, not)}.
   *
   * @param <T> the type of the comparison value
   * @param adapter the adapter used to create the underlying {@link CpoWhere} nodes
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @param not {@code true} to negate the comparison
   * @return a new builder with that one condition already added
   * @throws CpoException if the underlying where clause cannot be created
   */
  public static <T> CpoWhereBuilder startOr(
      CpoAdapter adapter, String attr, Comparison comp, T value, boolean not) throws CpoException {
    return start(adapter).or(attr, comp, value, not);
  }

  /**
   * Starts a chain whose sole top-level condition is a nested group joined by OR, for interleaving
   * with a query whose own expression already supplies a base WHERE clause. Equivalent to {@code
   * start(adapter).or(group)}.
   *
   * @param adapter the adapter used to create the underlying {@link CpoWhere} nodes
   * @param group populates the group's conditions
   * @return a new builder with that one group already added
   * @throws CpoException if the group or its conditions cannot be created
   */
  public static CpoWhereBuilder startOr(CpoAdapter adapter, CpoWhereGroup group)
      throws CpoException {
    return start(adapter).or(group);
  }

  private void requireNotAfterSoleInterleavedCondition() {
    if (conditionCount == 1 && lastAdded.getLogical() != Logical.NONE) {
      throw new IllegalStateException(
          "a chain started with and()/or() as its first condition may contain only that one"
              + " top-level condition; nest additional conditions inside its group instead");
    }
  }

  private <T> CpoWhereBuilder addCondition(
      Logical joinOperator, String attr, Comparison comp, T value, boolean not)
      throws CpoException {
    requireNotAfterSoleInterleavedCondition();
    CpoWhere leaf = adapter.newWhere(joinOperator, attr, comp, value, not);
    root.addWhere(leaf);
    lastAdded = leaf;
    conditionCount++;
    return this;
  }

  /**
   * Starts the chain with a comparison of the named attribute to a value.
   *
   * @param <T> the type of the comparison value
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @return this builder
   * @throws CpoException if the condition cannot be created
   * @throws IllegalStateException if this builder already has a condition
   */
  public <T> CpoWhereBuilder where(String attr, Comparison comp, T value) throws CpoException {
    return where(attr, comp, value, false);
  }

  /**
   * Starts the chain with a comparison of the named attribute to a value, optionally negated.
   *
   * @param <T> the type of the comparison value
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @param not {@code true} to negate the comparison
   * @return this builder
   * @throws CpoException if the condition cannot be created
   * @throws IllegalStateException if this builder already has a condition
   */
  public <T> CpoWhereBuilder where(String attr, Comparison comp, T value, boolean not)
      throws CpoException {
    if (conditionCount != 0) {
      throw new IllegalStateException("where() only starts a chain; use and()/or() after that");
    }
    return addCondition(Logical.NONE, attr, comp, value, not);
  }

  /**
   * Adds a condition joined to the previous condition with AND.
   *
   * @param <T> the type of the comparison value
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @return this builder
   * @throws CpoException if the condition cannot be created
   */
  public <T> CpoWhereBuilder and(String attr, Comparison comp, T value) throws CpoException {
    return addCondition(Logical.AND, attr, comp, value, false);
  }

  /**
   * Adds a condition joined to the previous condition with AND, optionally negated.
   *
   * @param <T> the type of the comparison value
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @param not {@code true} to negate the comparison
   * @return this builder
   * @throws CpoException if the condition cannot be created
   */
  public <T> CpoWhereBuilder and(String attr, Comparison comp, T value, boolean not)
      throws CpoException {
    return addCondition(Logical.AND, attr, comp, value, not);
  }

  /**
   * Adds a condition joined to the previous condition with OR.
   *
   * @param <T> the type of the comparison value
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @return this builder
   * @throws CpoException if the condition cannot be created
   */
  public <T> CpoWhereBuilder or(String attr, Comparison comp, T value) throws CpoException {
    return addCondition(Logical.OR, attr, comp, value, false);
  }

  /**
   * Adds a condition joined to the previous condition with OR, optionally negated.
   *
   * @param <T> the type of the comparison value
   * @param attr the name of the bean attribute to compare
   * @param comp the comparison operator to apply
   * @param value the value to compare the attribute against
   * @param not {@code true} to negate the comparison
   * @return this builder
   * @throws CpoException if the condition cannot be created
   */
  public <T> CpoWhereBuilder or(String attr, Comparison comp, T value, boolean not)
      throws CpoException {
    return addCondition(Logical.OR, attr, comp, value, not);
  }

  private CpoWhereBuilder addGroup(Logical joinOperator, CpoWhereGroup group) throws CpoException {
    requireNotAfterSoleInterleavedCondition();
    CpoWhereBuilder nested = new CpoWhereBuilder(adapter);
    group.build(nested);
    if (nested.conditionCount == 0) {
      throw new IllegalStateException("group added no conditions");
    }
    nested.root.setLogical(joinOperator);
    root.addWhere(nested.root);
    lastAdded = nested.root;
    conditionCount++;
    return this;
  }

  /**
   * Adds a nested group of conditions, joined to the previous condition with AND.
   *
   * @param group populates the nested group's conditions
   * @return this builder
   * @throws CpoException if the group or its conditions cannot be created
   * @throws IllegalStateException if the group adds no conditions
   */
  public CpoWhereBuilder and(CpoWhereGroup group) throws CpoException {
    return addGroup(Logical.AND, group);
  }

  /**
   * Adds a nested group of conditions, joined to the previous condition with OR.
   *
   * @param group populates the nested group's conditions
   * @return this builder
   * @throws CpoException if the group or its conditions cannot be created
   * @throws IllegalStateException if the group adds no conditions
   */
  public CpoWhereBuilder or(CpoWhereGroup group) throws CpoException {
    return addGroup(Logical.OR, group);
  }

  private void requireLastAdded(String method) {
    if (lastAdded == null) {
      throw new IllegalStateException(method + "() must follow where()/and()/or()");
    }
  }

  /**
   * Compares the condition just added against another attribute instead of a literal value.
   *
   * @param rightAttr the name of the right-hand bean attribute to compare against
   * @return this builder
   * @throws IllegalStateException if no condition has been added yet
   */
  public CpoWhereBuilder compareToAttribute(String rightAttr) {
    requireLastAdded("compareToAttribute");
    lastAdded.setRightAttribute(rightAttr);
    return this;
  }

  /**
   * Applies a native datastore function to the left-hand attribute of the condition just added.
   *
   * @param fn the function expression to apply
   * @return this builder
   * @throws IllegalStateException if no condition has been added yet
   */
  public CpoWhereBuilder attributeFunction(String fn) {
    requireLastAdded("attributeFunction");
    lastAdded.setAttributeFunction(fn);
    return this;
  }

  /**
   * Applies a native datastore function to the right-hand attribute of the condition just added.
   *
   * @param fn the function expression to apply
   * @return this builder
   * @throws IllegalStateException if no condition has been added yet
   */
  public CpoWhereBuilder rightAttributeFunction(String fn) {
    requireLastAdded("rightAttributeFunction");
    lastAdded.setRightAttributeFunction(fn);
    return this;
  }

  /**
   * Applies a native datastore function to the comparison value of the condition just added.
   *
   * @param fn the function expression to apply
   * @return this builder
   * @throws IllegalStateException if no condition has been added yet
   */
  public CpoWhereBuilder valueFunction(String fn) {
    requireLastAdded("valueFunction");
    lastAdded.setValueFunction(fn);
    return this;
  }

  /**
   * Sets a literal, unescaped value on the condition just added, in place of a bound value.
   *
   * @param literal the static (literal) value text
   * @return this builder
   * @throws IllegalStateException if no condition has been added yet
   */
  public CpoWhereBuilder staticValue(String literal) {
    requireLastAdded("staticValue");
    lastAdded.setStaticValue(literal);
    return this;
  }

  /**
   * Builds the {@link CpoWhere} tree assembled so far.
   *
   * <p>If the chain was started with {@link #and(String, Comparison, Object)}/{@link #or(String,
   * Comparison, Object)} (or a group overload) rather than {@code where()}, and it contains only
   * that one top-level condition/group, the returned value is that condition/group itself rather
   * than the internal wrapping container — so its own {@code AND}/{@code OR} renders directly,
   * without a duplicated leading {@code WHERE}.
   *
   * @return the built where clause
   * @throws IllegalStateException if no conditions were added
   */
  public CpoWhere build() {
    if (conditionCount == 0) {
      throw new IllegalStateException("no conditions were added");
    }
    if (conditionCount == 1 && lastAdded.getLogical() != Logical.NONE) {
      ((Node) lastAdded).setParent(null);
      return lastAdded;
    }
    return root;
  }
}
