package org.synchronoss.cpo.jdbc.adapter;

/*-
 * [[
 * jdbc
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoOrderBy;
import org.synchronoss.cpo.core.CpoQuery;
import org.synchronoss.cpo.core.CpoWhere;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class WhereTest {

  // unique id base so this class's rows never collide with another test class's
  private static final int IDB = 2400000;

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  public WhereTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(IDB + 1);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt((short) 1);
    vo.setAttrInteger(IDB + 1);
    vo.setAttrBigInt(BigInteger.valueOf(IDB + 1));
    vo.setAttrDate(new java.sql.Date(new java.util.Date().getTime()));
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(IDB + 2));
    al.add(ValueObjectFactory.createValueObject(IDB + 3));
    al.add(ValueObjectFactory.createValueObject(IDB + 4));
    al.add(ValueObjectFactory.createValueObject(IDB + 5));
    al.add(ValueObjectFactory.createValueObject(-(IDB + 6)));
    try {
      cpoAdapter.insertBeans(ValueObject.FG_CREATE_TESTORDERBYINSERT, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /** DOCUMENT ME! */
  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteBeans(ValueObject.FG_DELETE_TESTORDERBYDELETE, al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }

  /** DOCUMENT ME! */
  @Test
  public void testStaticWhere() {
    String method = "testStaticWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.GT, null)
              .staticValue(String.valueOf(IDB + 3))
              .build();
      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 2, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testSetObjectWhere() {
    String method = "testSetObjectWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      BigInteger bigInt = BigInteger.valueOf(IDB + 1);
      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ATTRBIGINT, Comparison.EQ, bigInt)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres),
              ValueObjectFactory.createValueObject())) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 1, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testValueWhere() {
    String method = "testValueWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      cw = cpoAdapter.whereBuilder().where(ValueObject.ATTR_ID, Comparison.GT, valObj).build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 2, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNoMarkerWhere() {
    String method = "testNoMarkerWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      cw = cpoAdapter.whereBuilder().where(ValueObject.ATTR_ID, Comparison.GT, valObj).build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_NULL).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 2, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /*
   * This test is because retrieveBeans was not honoring the old functionality of passing null for cpo_where should
   * ignore the where clause.
   */
  @Test
  public void testNoWhere() {
    String method = "testNoWhere:";
    Collection<ValueObject> col = null;

    ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
    try (Stream<ValueObject> beans =
        cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj)) {
      long count =
          beans
              .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
              .count();
      assertEquals(count, 6, "Number of beans is " + count);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNestedWhere() {
    String method = "testNestedWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;
    ArrayList<CpoWhere> wheres = new ArrayList<>();

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-(IDB + 6));
      ValueObject valObjOr1 = ValueObjectFactory.createValueObject(IDB + 2);
      ValueObject valObjOr2 = ValueObjectFactory.createValueObject(IDB + 3);
      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.EQ, valObj)
              .or(
                  g ->
                      g.where(ValueObject.ATTR_ID, Comparison.EQ, valObjOr1)
                          .or(ValueObject.ATTR_ID, Comparison.EQ, valObjOr2))
              .build();

      wheres.add(cw);
      valObj = valObjOr2;
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 3, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testIsNullWhere() {
    String method = "testIsNullWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 6, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testAttributeFunction() {
    String method = "testAttributeFunction:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 6);
      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.EQ, valObj)
              .attributeFunction("ABS(id)")
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        var list =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .toList();
        assertEquals(list.size(), 1, "Number of beans is " + list.size());
        var rvo = list.getFirst();
        assertEquals(rvo.getId(), -(IDB + 6), "-6 != " + rvo.getId());
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testValueFunction() {
    String method = "testValueFunction:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-(IDB + 1));
      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.EQ, valObj, false)
              .valueFunction("abs(id)")
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        var list =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .toList();
        assertEquals(list.size(), 1, "Number of beans is " + list.size());
        var rvo = list.getFirst();
        assertEquals(rvo.getId(), IDB + 1, "1 != " + rvo.getId());
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testAndWhere() {
    String method = "testAndWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null)
              .and(ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null, true)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 0, "Number of beans is " + count);
      }

      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null)
              .and(ValueObject.ATTR_ID, Comparison.EQ, valObj)
              .build();

      wheres.clear();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 1, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testOrWhere() {
    String method = "testOrWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.EQ, null)
              .staticValue(String.valueOf(IDB + 2))
              .or(ValueObject.ATTR_ID, Comparison.EQ, valObj)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 2, "Number of beans is " + count);
      }

      cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.EQ, null)
              .staticValue(String.valueOf(IDB + 3))
              .or(ValueObject.ATTR_ID, Comparison.EQ, valObj, true)
              .build();

      wheres.clear();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 6, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRightAttributeFunction() {
    String method = "testRightAttributeFunction:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-(IDB + 1));
      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where("id", Comparison.EQ, null)
              .compareToAttribute("attrBigInt")
              .attributeFunction("ABS(id)")
              .rightAttributeFunction("ABS(attrBigInt)")
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        var list =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .toList();
        assertEquals(list.size(), 1, "Number of beans is " + list.size());
        var rvo = list.getFirst();
        assertEquals(rvo.getId(), IDB + 1, "1 != " + rvo.getId());
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRightAttribute() {
    String method = "testRightAttribute:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-(IDB + 1));
      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where("id", Comparison.EQ, null)
              .compareToAttribute("attrBigInt")
              .build();

      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).where(cw), valObj, valObj)) {
        var list =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .toList();
        assertEquals(list.size(), 1, "Number of beans is " + list.size());
        var rvo = list.getFirst();
        assertEquals(rvo.getId(), IDB + 1, "1 != " + rvo.getId());
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMultipleBindWhere() {
    String method = "testMultipleBindWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      valObj.setAttrVarChar("Test");

      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ATTRVARCHAR, Comparison.EQ, valObj)
              .and(ValueObject.ATTR_ID, Comparison.EQ, valObj)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 1, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testLikeWhere() {
    String method = "testLikeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      valObj.setAttrVarChar("T%");

      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ATTRVARCHAR, Comparison.LIKE, valObj)
              .and(ValueObject.ATTR_ID, Comparison.EQ, valObj)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 1, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testLikeWhereStrings() {
    String method = "testLikeWhereStrings:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      valObj.setAttrVarChar("T%");

      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ATTRVARCHAR, Comparison.LIKE, "T%")
              .and(ValueObject.ATTR_ID, Comparison.EQ, IDB + 1)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 1, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNonAttributeWhere() {
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);

      CpoWhere cw =
          cpoAdapter.whereBuilder().where("value_object.id", Comparison.LT, IDB + 1).build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 1, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereStaticValue() {
    String method = "testInWhereStaticValue:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);

      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.IN, null)
              .staticValue("(" + (IDB + 1) + "," + (IDB + 3) + "," + (IDB + 5) + ")")
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 3, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereCollection() {
    String method = "testInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(IDB + 1);
      inColl.add(IDB + 3);
      inColl.add(IDB + 5);

      CpoWhere cw =
          cpoAdapter.whereBuilder().where(ValueObject.ATTR_ID, Comparison.IN, inColl).build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 3, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMultiInWhereCollection() {
    String method = "testMultiInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      Collection<Integer> inColl1 = new ArrayList<>();
      inColl1.add(IDB + 1);
      inColl1.add(IDB + 2);

      Collection<Integer> inColl2 = new ArrayList<>();
      inColl2.add(IDB + 3);
      inColl2.add(IDB + 4);

      Collection<Integer> inColl3 = new ArrayList<>();
      inColl3.add(IDB + 5);
      inColl3.add(-(IDB + 6));

      CpoWhere cw =
          cpoAdapter
              .whereBuilder()
              .where(ValueObject.ATTR_ID, Comparison.IN, inColl1)
              .or(ValueObject.ATTR_ID, Comparison.IN, inColl2)
              .or(ValueObject.ATTR_ID, Comparison.IN, inColl3)
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTORDERBYRETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 6, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNonAttributeInWhereCollection() {
    String method = "testNonAttributeInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(IDB + 1);
      inColl.add(IDB + 3);
      inColl.add(IDB + 5);

      CpoWhere cw =
          cpoAdapter.whereBuilder().where("value_object.id", Comparison.IN, inColl).build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).wheres(wheres), valObj)) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 3, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testWhereParens() {
    String method = "testWhereParens:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);

      // Without the correct parens, this will return multiple rows for a retrieveBean which is a
      // failure
      CpoWhere cw1 =
          cpoAdapter
              .startAnd(
                  g ->
                      g.where(ValueObject.ATTR_ID, Comparison.EQ, IDB + 1)
                          .or(ValueObject.ATTR_ID, Comparison.EQ, IDB + 3))
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      ArrayList<CpoOrderBy> orderBys = new ArrayList<>();
      wheres.add(cw1);

      valObj =
          cpoAdapter.retrieveBean(
              CpoQuery.group(ValueObject.FG_RETRIEVE_NULL).wheres(wheres).orderBys(orderBys),
              valObj);

      assertNotNull(valObj, "Value Object should not be null");
      assertEquals(valObj.getId(), IDB + 1, "Id should equal 1");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testWhereOrderBy() {
    String method = "testWhereOrderBy:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);

      // Without the correct parens, this will return multiple rows for a retrieveBean which is a
      // failure
      CpoWhere cw1 =
          cpoAdapter
              .startAnd(
                  g ->
                      g.where(ValueObject.ATTR_ID, Comparison.EQ, IDB + 1)
                          .or(ValueObject.ATTR_ID, Comparison.EQ, IDB + 3))
              .build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      ArrayList<CpoOrderBy> orderBys = new ArrayList<>();
      wheres.add(cw1);

      valObj =
          cpoAdapter.retrieveBean(
              CpoQuery.group(ValueObject.FG_RETRIEVE_NULL).wheres(wheres).orderBys(orderBys),
              valObj,
              valObj);

      assertNotNull(valObj, "Value Object should not be null");
      assertEquals(valObj.getId(), IDB + 1, "Id should equal 1");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
