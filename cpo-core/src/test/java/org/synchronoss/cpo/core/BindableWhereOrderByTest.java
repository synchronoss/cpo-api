package org.synchronoss.cpo.core;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
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
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Unit tests for BindableCpoWhere and BindableCpoOrderBy clause rendering. */
public class BindableWhereOrderByTest {

  private CpoClass cpoClass;

  @BeforeClass
  public void setUp() throws CpoException {
    CpoMetaDescriptor descriptor =
        CpoMetaDescriptor.getInstance("bindableWhereTest", "coreTestMeta.xml", false);
    cpoClass = descriptor.getCpoClasses().get(0);
  }

  @AfterClass
  public void tearDown() throws CpoException {
    CpoMetaDescriptor.clearAllInstances();
  }

  @Test
  public void testSimpleWhere() throws Exception {
    BindableCpoWhere where = new BindableCpoWhere(Logical.NONE, "id", Comparison.EQ, "x");
    String clause = where.toString(cpoClass);
    assertTrue(clause.contains("id"), clause);
    assertTrue(clause.contains("="), clause);
    assertTrue(clause.contains("?"), clause);
  }

  @Test
  public void testUnknownAttributeFallsBackToColumnName() throws Exception {
    BindableCpoWhere where = new BindableCpoWhere(Logical.NONE, "some_column", Comparison.EQ, 1);
    String clause = where.toString(cpoClass);
    assertTrue(clause.contains("some_column"), clause);
  }

  @Test
  public void testInCollection() throws Exception {
    BindableCpoWhere where =
        new BindableCpoWhere(Logical.NONE, "id", Comparison.IN, List.of("a", "b", "c"));
    String clause = where.toString(cpoClass);
    assertTrue(clause.contains("(?, ?, ?)"), clause);

    BindableCpoWhere emptyIn = new BindableCpoWhere(Logical.NONE, "id", Comparison.IN, List.of());
    String emptyClause = emptyIn.toString(cpoClass);
    assertTrue(emptyClause.contains("()"), emptyClause);

    // an IN whose value is not a collection binds a single marker
    BindableCpoWhere scalarIn = new BindableCpoWhere(Logical.NONE, "id", Comparison.IN, "x");
    assertTrue(scalarIn.toString(cpoClass).contains("?"));
  }

  @Test
  public void testStaticValue() throws Exception {
    BindableCpoWhere where = new BindableCpoWhere();
    where.setLogical(Logical.NONE);
    where.setAttribute("age");
    where.setComparison(Comparison.EQ);
    where.setStaticValue("42");
    String clause = where.toString(cpoClass);
    assertTrue(clause.contains("42"), clause);
    assertFalse(clause.contains("?"), clause);
  }

  @Test
  public void testRightAttribute() throws Exception {
    BindableCpoWhere where = new BindableCpoWhere();
    where.setLogical(Logical.NONE);
    where.setAttribute("id");
    where.setComparison(Comparison.EQ);
    where.setRightAttribute("name");
    String clause = where.toString(cpoClass);
    assertTrue(clause.contains("name"), clause);

    // unknown right attribute falls back to the raw name
    BindableCpoWhere unknownRight = new BindableCpoWhere();
    unknownRight.setLogical(Logical.NONE);
    unknownRight.setAttribute("id");
    unknownRight.setComparison(Comparison.EQ);
    unknownRight.setRightAttribute("other_column");
    assertTrue(unknownRight.toString(cpoClass).contains("other_column"));
  }

  @Test
  public void testRightAttributeWithFunction() throws Exception {
    BindableCpoWhere where = new BindableCpoWhere();
    where.setLogical(Logical.NONE);
    where.setComparison(Comparison.EQ);
    where.setRightAttribute("name");
    where.setRightAttributeFunction("LOWER(name)");
    String clause = where.toString(cpoClass);
    assertTrue(clause.contains("LOWER"), clause);
  }

  @Test
  public void testAttributeFunction() throws Exception {
    BindableCpoWhere known = new BindableCpoWhere(Logical.NONE, "id", Comparison.EQ, "x");
    known.setAttributeFunction("UPPER(id)");
    String clause = known.toString(cpoClass);
    assertTrue(clause.contains("UPPER"), clause);

    // an unknown attribute appends the function verbatim
    BindableCpoWhere unknown = new BindableCpoWhere(Logical.NONE, "bogus", Comparison.EQ, "x");
    unknown.setAttributeFunction("UPPER(bogus)");
    assertTrue(unknown.toString(cpoClass).contains("UPPER(bogus)"));
  }

  @Test
  public void testValueFunction() throws Exception {
    BindableCpoWhere where = new BindableCpoWhere(Logical.NONE, "id", Comparison.EQ, "x");
    where.setValueFunction("UPPER(id)");
    String clause = where.toString(cpoClass);
    assertTrue(clause.contains("UPPER"), clause);

    // with an unknown attribute the value function name resolution falls back to the raw name
    BindableCpoWhere unknown = new BindableCpoWhere(Logical.NONE, "bogus", Comparison.EQ, "x");
    unknown.setValueFunction("UPPER(bogus)");
    assertNotNull(unknown.toString(cpoClass));
  }

  @Test
  public void testIsNullAndNot() throws Exception {
    BindableCpoWhere isNull = new BindableCpoWhere(Logical.NONE, "id", Comparison.ISNULL, null);
    String clause = isNull.toString(cpoClass);
    assertFalse(clause.contains("?"), clause);

    BindableCpoWhere notWhere = new BindableCpoWhere(Logical.NONE, "id", Comparison.EQ, "x", true);
    assertTrue(notWhere.getNot());
    assertNotNull(notWhere.toString(cpoClass));
  }

  @Test
  public void testNestedWheres() throws Exception {
    // nested wheres render through the BindableWhereBuilder visitor, not the parent's toString
    BindableCpoWhere outer = new BindableCpoWhere(Logical.NONE, "id", Comparison.EQ, "x");
    BindableCpoWhere inner = new BindableCpoWhere(Logical.AND, "name", Comparison.EQ, "y");
    outer.addWhere(inner);

    BindableWhereBuilder<Object> builder = new BindableWhereBuilder<>(cpoClass);
    outer.acceptDFVisitor(builder);
    String clause = builder.getWhereClause();
    assertTrue(clause.contains("AND"), clause);
    assertTrue(clause.contains("name"), clause);
    // a where with children acts as a grouping node, so only the leaf comparisons bind
    assertFalse(builder.getBindValues().isEmpty(), "leaf comparison should bind a value");
  }

  @Test
  public void testOrderByVariants() throws Exception {
    BindableCpoOrderBy asc = new BindableCpoOrderBy("id", true);
    String ascClause = asc.toString(cpoClass);
    assertTrue(ascClause.contains("id"), ascClause);
    assertTrue(asc.getAscending());
    assertEquals(asc.getAttribute(), "id");

    BindableCpoOrderBy desc = new BindableCpoOrderBy("id", false);
    assertNotNull(desc.toString(cpoClass));
    assertFalse(desc.getAscending());

    BindableCpoOrderBy marker = new BindableCpoOrderBy("theMarker", "id", true);
    assertEquals(marker.getMarker(), "theMarker");

    // a function referencing the attribute substitutes the column name
    BindableCpoOrderBy withFunction = new BindableCpoOrderBy("id", true, "UPPER(id)");
    String funcClause = withFunction.toString(cpoClass);
    assertTrue(funcClause.contains("UPPER"), funcClause);
    assertEquals(withFunction.getFunction(), "UPPER(id)");

    // an empty function renders the plain column
    BindableCpoOrderBy emptyFunction = new BindableCpoOrderBy("id", true, "");
    assertNotNull(emptyFunction.toString(cpoClass));

    // a null attribute renders nothing but does not fail
    BindableCpoOrderBy nullAttr = new BindableCpoOrderBy(null, true);
    assertNotNull(nullAttr.toString(cpoClass));

    // an unknown attribute is rejected
    BindableCpoOrderBy unknown = new BindableCpoOrderBy("bogus", true);
    expectThrows(CpoException.class, () -> unknown.toString(cpoClass));
  }
}
