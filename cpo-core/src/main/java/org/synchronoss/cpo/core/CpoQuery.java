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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The clauses of a {@link CpoAdapter} operation: the function group to run plus any run-time where
 * constraints, orderings, and native expressions. A CpoQuery replaces the telescoping method
 * overloads that previously carried these as positional parameters.
 *
 * <p>Instances are immutable — every accumulator returns a new CpoQuery — so a query may be built
 * once, held in a constant, and shared freely between threads:
 *
 * <pre>{@code
 * Stream<ValueObject> beans =
 *     adapter.retrieveBeans(CpoQuery.group("byDept").where(w).orderBy(ob), criteria);
 * }</pre>
 *
 * @param groupName The function group name; null selects the default group
 * @param wheres The run-time where constraints, in the order added
 * @param orderBys The orderings, in the order added
 * @param nativeExpressions The native expressions, in the order added
 * @author david berry
 */
public record CpoQuery(
    String groupName,
    List<CpoWhere> wheres,
    List<CpoOrderBy> orderBys,
    List<CpoNativeFunction> nativeExpressions)
    implements Serializable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final CpoQuery DEFAULT_GROUP = new CpoQuery(null, List.of(), List.of(), List.of());

  /**
   * A query against the named function group.
   *
   * @param groupName The function group name; null signifies the default group
   * @return A CpoQuery with no clauses
   */
  public static CpoQuery group(String groupName) {
    return groupName == null
        ? DEFAULT_GROUP
        : new CpoQuery(groupName, List.of(), List.of(), List.of());
  }

  /**
   * A query against the default (unnamed) function group.
   *
   * @return A CpoQuery with no clauses
   */
  public static CpoQuery defaultGroup() {
    return DEFAULT_GROUP;
  }

  /**
   * Adds a where constraint.
   *
   * @param where The constraint to add; null is ignored
   * @return A new CpoQuery with the constraint appended
   */
  public CpoQuery where(CpoWhere where) {
    if (where == null) return this;
    return new CpoQuery(groupName, append(wheres, where), orderBys, nativeExpressions);
  }

  /**
   * Adds a collection of where constraints.
   *
   * @param wheres The constraints to add; null or empty is ignored
   * @return A new CpoQuery with the constraints appended
   */
  public CpoQuery wheres(Collection<CpoWhere> wheres) {
    if (wheres == null || wheres.isEmpty()) return this;
    return new CpoQuery(groupName, appendAll(this.wheres, wheres), orderBys, nativeExpressions);
  }

  /**
   * Adds an ordering.
   *
   * @param orderBy The ordering to add; null is ignored
   * @return A new CpoQuery with the ordering appended
   */
  public CpoQuery orderBy(CpoOrderBy orderBy) {
    if (orderBy == null) return this;
    return new CpoQuery(groupName, wheres, append(orderBys, orderBy), nativeExpressions);
  }

  /**
   * Adds a collection of orderings.
   *
   * @param orderBys The orderings to add; null or empty is ignored
   * @return A new CpoQuery with the orderings appended
   */
  public CpoQuery orderBys(Collection<CpoOrderBy> orderBys) {
    if (orderBys == null || orderBys.isEmpty()) return this;
    return new CpoQuery(groupName, wheres, appendAll(this.orderBys, orderBys), nativeExpressions);
  }

  /**
   * Adds a native expression that augments the expression stored in the metadata.
   *
   * @param nativeExpression The native expression to add; null is ignored
   * @return A new CpoQuery with the native expression appended
   */
  public CpoQuery nativeExpression(CpoNativeFunction nativeExpression) {
    if (nativeExpression == null) return this;
    return new CpoQuery(groupName, wheres, orderBys, append(nativeExpressions, nativeExpression));
  }

  /**
   * Adds a collection of native expressions.
   *
   * @param nativeExpressions The native expressions to add; null or empty is ignored
   * @return A new CpoQuery with the native expressions appended
   */
  public CpoQuery nativeExpressions(Collection<CpoNativeFunction> nativeExpressions) {
    if (nativeExpressions == null || nativeExpressions.isEmpty()) return this;
    return new CpoQuery(
        groupName, wheres, orderBys, appendAll(this.nativeExpressions, nativeExpressions));
  }

  private static <E> List<E> append(List<E> list, E element) {
    List<E> appended = new ArrayList<>(list);
    appended.add(element);
    return Collections.unmodifiableList(appended);
  }

  private static <E> List<E> appendAll(List<E> list, Collection<E> elements) {
    List<E> appended = new ArrayList<>(list);
    appended.addAll(elements);
    return Collections.unmodifiableList(appended);
  }
}
