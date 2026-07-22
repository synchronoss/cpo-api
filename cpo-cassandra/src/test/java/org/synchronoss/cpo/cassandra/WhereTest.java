package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoQuery;
import org.synchronoss.cpo.core.CpoWhere;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;
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
  private static final int IDB = 1200000;

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
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "CpoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(IDB + 1);
    vo.setAttrVarChar("Test");
    vo.setAttrInt(1);
    vo.setAttrBigInt(1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(IDB + 2));
    al.add(ValueObjectFactory.createValueObject(IDB + 3));
    al.add(ValueObjectFactory.createValueObject(IDB + 4));
    al.add(ValueObjectFactory.createValueObject(IDB + 5));
    al.add(ValueObjectFactory.createValueObject(-(IDB + 6)));
    try {
      cpoAdapter.insertBeans("TestOrderByInsert", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /** DOCUMENT ME! */
  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteBeans("TestOrderByDelete", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }

  @Test
  public void testStaticWhere() {
    String method = "testStaticWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      cw = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, null);
      cw.setStaticValue(String.valueOf(IDB + 3));
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
  public void testValueWhere() {
    String method = "testValueWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      cw = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, valObj);

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
  public void testNoMarkerWhere() {
    String method = "testValueWhere:";
    Collection<ValueObject> col;
    CpoWhere cw;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      cw = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, valObj);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_RETRIEVE_NULL).wheres(wheres), valObj)) {
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

  /*
   * This test is because retrieveBeans was not honoring the old functionality of passing null for cpo_where should
   * ignore the where clause.
   */
  @Test
  public void testNoWhere() {
    String method = "testNoWhere:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj)) {
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
  public void testAndWhere() {
    String method = "testAndWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, IDB + 1);
      CpoWhere cw2 = cpoAdapter.newWhere(Logical.AND, "attrInt", Comparison.EQ, 1);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

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
  public void testMultipleBindWhere() {
    String method = "testMultipleBindWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      valObj.setAttrInt(1);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "attrInt", Comparison.EQ, valObj);
      CpoWhere cw2 = cpoAdapter.newWhere(Logical.AND, "id", Comparison.EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

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

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, IDB + 1);

      cw.addWhere(cw1);

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
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.IN, null);
      cw1.setStaticValue("(" + (IDB + 1) + "," + (IDB + 3) + "," + (IDB + 5) + ")");

      cw.addWhere(cw1);

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
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(IDB + 1);
      inColl.add(IDB + 3);
      inColl.add(IDB + 5);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.IN, inColl);

      cw.addWhere(cw1);

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
  public void testNonAttributeInWhereCollection() {
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(IDB + 1);
      inColl.add(IDB + 3);
      inColl.add(IDB + 5);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.IN, inColl);

      cw.addWhere(cw1);

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
}
