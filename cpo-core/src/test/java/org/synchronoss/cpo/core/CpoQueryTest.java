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

import static org.testng.Assert.*;

import java.util.List;
import org.testng.annotations.Test;

/**
 * Unit tests for the CpoQuery parameter object: factory behavior, accumulation order,
 * null/empty-clause tolerance, and immutability.
 *
 * @author david berry
 */
public class CpoQueryTest {

  @Test
  public void testGroupFactories() {
    assertNull(CpoQuery.defaultGroup().getGroupName());
    assertNull(CpoQuery.group(null).getGroupName(), "null group is the default group");
    assertEquals(CpoQuery.group("g").getGroupName(), "g");

    CpoQuery empty = CpoQuery.defaultGroup();
    assertTrue(empty.getWheres().isEmpty());
    assertTrue(empty.getOrderBys().isEmpty());
    assertTrue(empty.getNativeExpressions().isEmpty());
  }

  @Test
  public void testClausesAccumulateInOrder() {
    CpoWhere w1 = new BindableCpoWhere();
    CpoWhere w2 = new BindableCpoWhere();
    CpoOrderBy o1 = new BindableCpoOrderBy("attr1", true);
    CpoOrderBy o2 = new BindableCpoOrderBy("attr2", false);
    CpoNativeFunction n1 = new CpoNativeFunction();
    CpoNativeFunction n2 = new CpoNativeFunction();

    CpoQuery query =
        CpoQuery.group("g")
            .where(w1)
            .wheres(List.of(w2))
            .orderBy(o1)
            .orderBys(List.of(o2))
            .nativeExpression(n1)
            .nativeExpressions(List.of(n2));

    assertEquals(query.getGroupName(), "g");
    assertEquals(query.getWheres(), List.of(w1, w2));
    assertEquals(query.getOrderBys(), List.of(o1, o2));
    assertEquals(query.getNativeExpressions(), List.of(n1, n2));
  }

  @Test
  public void testNullAndEmptyClausesAreIgnored() {
    CpoQuery query = CpoQuery.group("g");

    assertSame(query.where(null), query);
    assertSame(query.wheres(null), query);
    assertSame(query.wheres(List.of()), query);
    assertSame(query.orderBy(null), query);
    assertSame(query.orderBys(null), query);
    assertSame(query.orderBys(List.of()), query);
    assertSame(query.nativeExpression(null), query);
    assertSame(query.nativeExpressions(null), query);
    assertSame(query.nativeExpressions(List.of()), query);
  }

  @Test
  public void testAccumulatorsDoNotMutateTheOriginal() {
    CpoWhere w = new BindableCpoWhere();
    CpoQuery original = CpoQuery.group("g");

    CpoQuery extended = original.where(w);

    assertNotSame(extended, original);
    assertTrue(original.getWheres().isEmpty(), "the original query must be unchanged");
    assertEquals(extended.getWheres(), List.of(w));
    assertEquals(extended.getGroupName(), "g", "the group carries over to the extended query");
  }

  @Test
  public void testClauseListsAreUnmodifiable() {
    CpoQuery query = CpoQuery.group("g").where(new BindableCpoWhere());

    expectThrows(
        UnsupportedOperationException.class, () -> query.getWheres().add(new BindableCpoWhere()));
    expectThrows(
        UnsupportedOperationException.class,
        () -> query.getOrderBys().add(new BindableCpoOrderBy("attr", true)));
    expectThrows(
        UnsupportedOperationException.class,
        () -> query.getNativeExpressions().add(new CpoNativeFunction()));
  }
}
