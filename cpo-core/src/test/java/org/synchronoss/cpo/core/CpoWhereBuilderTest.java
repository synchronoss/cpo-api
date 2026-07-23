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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertThrows;
import static org.testng.Assert.assertTrue;

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Crud;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Unit tests for {@link CpoWhereBuilder}. */
public class CpoWhereBuilderTest {

  private CpoClass cpoClass;
  private CpoAdapter cpoAdapter;

  @BeforeClass
  public void setUp() throws CpoException {
    CpoMetaDescriptor descriptor =
        CpoMetaDescriptor.getInstance("cpoWhereBuilderTest", "coreTestMeta.xml", false);
    cpoClass = descriptor.getCpoClasses().get(0);
    cpoAdapter =
        new CpoBaseAdapter<Object>("test", 100, 100) {
          @Override
          public CpoMetaDescriptor getCpoMetaDescriptor() {
            return descriptor;
          }

          @Override
          protected <T> long processUpdateGroup(
              T bean,
              Crud crud,
              String groupName,
              Collection<CpoWhere> wheres,
              Collection<CpoOrderBy> orderBy,
              Collection<CpoNativeFunction> nativeExpressions) {
            throw new UnsupportedOperationException();
          }

          @Override
          protected <T> long processUpdateGroup(
              List<T> beans,
              Crud crud,
              String groupName,
              Collection<CpoWhere> wheres,
              Collection<CpoOrderBy> orderBy,
              Collection<CpoNativeFunction> nativeExpressions) {
            throw new UnsupportedOperationException();
          }

          @Override
          protected <T, C> T processExecuteGroup(String groupName, C criteria, T result) {
            throw new UnsupportedOperationException();
          }

          @Override
          public <T> long existsBean(CpoQuery query, T bean) {
            throw new UnsupportedOperationException();
          }

          @Override
          public List<CpoAttribute> getCpoAttributes(String expression) {
            throw new UnsupportedOperationException();
          }

          @Override
          protected <T> T processSelectGroup(
              T bean,
              String groupName,
              Collection<CpoWhere> wheres,
              Collection<CpoOrderBy> orderBy,
              Collection<CpoNativeFunction> nativeExpressions) {
            throw new UnsupportedOperationException();
          }

          @Override
          protected <T, C> Stream<T> processSelectGroup(
              String groupName,
              C criteria,
              T result,
              Collection<CpoWhere> wheres,
              Collection<CpoOrderBy> orderBy,
              Collection<CpoNativeFunction> nativeExpressions,
              boolean useRetrieve) {
            throw new UnsupportedOperationException();
          }
        };
  }

  @AfterClass
  public void tearDown() throws CpoException {
    CpoMetaDescriptor.clearAllInstances();
  }

  private String renderClause(CpoWhere where) {
    BindableWhereBuilder<Object> visitor = new BindableWhereBuilder<>(cpoClass);
    ((BindableCpoWhere) where).acceptDFVisitor(visitor);
    return visitor.getWhereClause();
  }

  private Collection<BindAttribute> bindValues(CpoWhere where) {
    BindableWhereBuilder<Object> visitor = new BindableWhereBuilder<>(cpoClass);
    ((BindableCpoWhere) where).acceptDFVisitor(visitor);
    return visitor.getBindValues();
  }

  @Test
  public void testFlatAndChain() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, 1)
            .and("id", Comparison.EQ, 2)
            .and("id", Comparison.EQ, 3)
            .build();

    String clause = renderClause(where);
    assertEquals(clause.split("AND", -1).length - 1, 2, clause);
    assertEquals(bindValues(where).size(), 3);
  }

  @Test
  public void testFlatOrChain() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, 1)
            .or("id", Comparison.EQ, 2)
            .build();

    String clause = renderClause(where);
    assertTrue(clause.contains("OR"), clause);
    assertFalse(clause.contains("AND"), clause);
  }

  @Test
  public void testNestedGroup() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, 1)
            .and(g -> g.where("id", Comparison.EQ, 2).or("id", Comparison.EQ, 3))
            .build();

    String clause = renderClause(where);
    assertTrue(clause.contains("AND ("), clause);
    assertTrue(clause.contains("OR"), clause);
    assertEquals(bindValues(where).size(), 3);
  }

  @Test
  public void testNegation() throws CpoException {
    CpoWhere where = CpoWhereBuilder.start(cpoAdapter).where("id", Comparison.EQ, 1, true).build();

    String clause = renderClause(where);
    assertTrue(clause.contains("NOT"), clause);
  }

  @Test
  public void testCompareToAttribute() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, null)
            .compareToAttribute("name")
            .build();

    String clause = renderClause(where);
    assertTrue(clause.contains("name"), clause);
    assertEquals(bindValues(where).size(), 0);
  }

  @Test
  public void testAttributeAndValueFunction() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, 1)
            .attributeFunction("UPPER(id)")
            .build();
    assertTrue(renderClause(where).contains("UPPER"));

    CpoWhere where2 =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, 1)
            .valueFunction("UPPER(id)")
            .build();
    assertTrue(renderClause(where2).contains("UPPER"));
  }

  @Test
  public void testRightAttributeFunction() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, null)
            .compareToAttribute("name")
            .rightAttributeFunction("LOWER(name)")
            .build();

    String clause = renderClause(where);
    assertTrue(clause.contains("LOWER"), clause);
    assertEquals(bindValues(where).size(), 0);
  }

  @Test
  public void testStaticValue() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.start(cpoAdapter)
            .where("id", Comparison.EQ, null)
            .staticValue("42")
            .build();

    String clause = renderClause(where);
    assertTrue(clause.contains("42"), clause);
    assertEquals(bindValues(where).size(), 0);
  }

  @Test
  public void testModifierBeforeConditionThrows() throws CpoException {
    CpoWhereBuilder builder = CpoWhereBuilder.start(cpoAdapter);
    assertThrows(IllegalStateException.class, () -> builder.compareToAttribute("id"));
  }

  @Test
  public void testWhereCalledTwiceThrows() throws CpoException {
    CpoWhereBuilder builder = CpoWhereBuilder.start(cpoAdapter).where("id", Comparison.EQ, 1);
    assertThrows(IllegalStateException.class, () -> builder.where("id", Comparison.EQ, 2));
  }

  @Test
  public void testBuildWithNoConditionsThrows() throws CpoException {
    CpoWhereBuilder builder = CpoWhereBuilder.start(cpoAdapter);
    assertThrows(IllegalStateException.class, builder::build);
  }

  @Test
  public void testEmptyGroupThrows() throws CpoException {
    CpoWhereBuilder builder = CpoWhereBuilder.start(cpoAdapter).where("id", Comparison.EQ, 1);
    assertThrows(IllegalStateException.class, () -> builder.and(g -> {}));
  }

  @Test
  public void testStartAndInterleavesWithoutLeadingWhere() throws CpoException {
    CpoWhere where = CpoWhereBuilder.startAnd(cpoAdapter, "id", Comparison.EQ, 1).build();

    String clause = renderClause(where);
    assertTrue(clause.contains("AND"), clause);
    assertFalse(clause.toUpperCase().contains("WHERE"), clause);
  }

  @Test
  public void testStartOrInterleavesWithoutLeadingWhere() throws CpoException {
    CpoWhere where = CpoWhereBuilder.startOr(cpoAdapter, "id", Comparison.EQ, 1).build();

    String clause = renderClause(where);
    assertTrue(clause.contains("OR"), clause);
    assertFalse(clause.toUpperCase().contains("WHERE"), clause);
  }

  @Test
  public void testStartAndWithGroupInterleavesWithoutLeadingWhere() throws CpoException {
    CpoWhere where =
        CpoWhereBuilder.startAnd(
                cpoAdapter, g -> g.where("id", Comparison.EQ, 1).or("id", Comparison.EQ, 3))
            .build();

    String clause = renderClause(where);
    assertTrue(clause.contains("AND ("), clause);
    assertTrue(clause.contains("OR"), clause);
    assertFalse(clause.toUpperCase().contains("WHERE"), clause);
    assertEquals(bindValues(where).size(), 2);
  }

  @Test
  public void testAndAsFirstCallMatchesStartAnd() throws CpoException {
    CpoWhere viaStartAnd = CpoWhereBuilder.startAnd(cpoAdapter, "id", Comparison.EQ, 1).build();
    CpoWhere viaAndFirst = CpoWhereBuilder.start(cpoAdapter).and("id", Comparison.EQ, 1).build();

    assertEquals(renderClause(viaAndFirst), renderClause(viaStartAnd));
  }

  @Test
  public void testSecondTopLevelConditionAfterStartAndThrows() throws CpoException {
    CpoWhereBuilder builder = CpoWhereBuilder.startAnd(cpoAdapter, "id", Comparison.EQ, 1);
    assertThrows(IllegalStateException.class, () -> builder.and("id", Comparison.EQ, 2));
  }

  @Test
  public void testWhereAfterStartAndThrows() throws CpoException {
    CpoWhereBuilder builder = CpoWhereBuilder.startAnd(cpoAdapter, "id", Comparison.EQ, 1);
    assertThrows(IllegalStateException.class, () -> builder.where("id", Comparison.EQ, 2));
  }
}
