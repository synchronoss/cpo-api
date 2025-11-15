package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
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

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoOrderBy;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class WhereTest {

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
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt((short) 1);
    vo.setAttrInteger(1);
    vo.setAttrBigInt(BigInteger.valueOf(2075L));
    vo.setAttrDate(new java.sql.Date(new java.util.Date().getTime()));
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(-6));
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
      cw = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.GT, null);
      cw.setStaticValue("3");
      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(2, col.size(), "Col size is " + col.size());
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
      BigInteger bigInt = BigInteger.valueOf(2075L);
      cw = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ATTRBIGINT, Comparison.EQ, bigInt);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_TESTWHERERETRIEVE,
              ValueObjectFactory.createValueObject(),
              wheres,
              null);

      assertEquals(1, col.size(), "Col size is " + col.size());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      cw = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.GT, valObj);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(2, col.size(), "Col size is " + col.size());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      cw = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.GT, valObj);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, wheres, null);

      assertEquals(2, col.size(), "Col size is " + col.size());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(3);

      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj);

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
      ValueObject valObj = ValueObjectFactory.createValueObject(-6);
      cw = cpoAdapter.newWhere();
      cw.addWhere(cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, valObj));

      CpoWhere cwAnd = cpoAdapter.newWhere();
      cwAnd.setLogical(Logical.OR);
      valObj = ValueObjectFactory.createValueObject(2);
      cwAnd.addWhere(cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, valObj));
      valObj = ValueObjectFactory.createValueObject(3);
      cwAnd.addWhere(cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.EQ, valObj));

      cw.addWhere(cwAnd);
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(3, col.size(), "Col size is " + col.size());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      cw = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(6, col.size(), "Col size is " + col.size());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(6);
      cw = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, valObj);
      cw.setAttributeFunction("ABS(id)");

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(1, col.size(), "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertEquals(-6, rvo.getId(), "-6 != " + rvo.getId());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(-1);
      cw = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, valObj, false);
      cw.setValueFunction("abs(id)");

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertTrue(rvo.getId() == 1, "1 != " + rvo.getId());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testAndWhere() {
    String method = "testAndWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 =
          cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null);
      CpoWhere cw2 =
          cpoAdapter.newWhere(
              Logical.AND, ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null, true);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.isEmpty(), "Col size is " + col.size());

      cw = cpoAdapter.newWhere();
      cw1 = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ATTRCHAR, Comparison.ISNULL, null);
      cw2 = cpoAdapter.newWhere(Logical.AND, ValueObject.ATTR_ID, Comparison.EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      wheres.clear();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testOrWhere() {
    String method = "testOrWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, null);
      CpoWhere cw2 = cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.EQ, valObj);

      cw1.setStaticValue("2");
      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 2, "Col size is " + col.size());

      cw = cpoAdapter.newWhere();
      cw1 = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, null);
      cw2 = cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.EQ, valObj, true);

      cw1.setStaticValue("3");
      cw.addWhere(cw1);
      cw.addWhere(cw2);

      wheres.clear();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 6, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRightAttributeFunction() {
    String method = "testRightAttributeFunction:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-1);
      CpoWhere cw = cpoAdapter.newWhere();
      cw.setAttribute("id");
      cw.setRightAttribute("attrSmallInt");
      cw.setAttributeFunction("ABS(id)");
      cw.setComparison(Comparison.EQ);
      cw.setRightAttributeFunction("ABS(attrSmallInt)");
      cw.setLogical(Logical.NONE);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertTrue(rvo.getId() == 1, "1 != " + rvo.getId());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRightAttribute() {
    String method = "testRightAttribute:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-1);
      CpoWhere cw = cpoAdapter.newWhere();
      cw.setAttribute("id");
      cw.setRightAttribute("attrSmallInt");
      cw.setComparison(Comparison.EQ);
      cw.setLogical(Logical.NONE);

      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, valObj, cw, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertTrue(rvo.getId() == 1, "1 != " + rvo.getId());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMultipleBindWhere() {
    String method = "testMultipleBindWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      valObj.setAttrVarChar("Test");

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 =
          cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ATTRVARCHAR, Comparison.EQ, valObj);
      CpoWhere cw2 = cpoAdapter.newWhere(Logical.AND, ValueObject.ATTR_ID, Comparison.EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testLikeWhere() {
    String method = "testLikeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      valObj.setAttrVarChar("T%");

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 =
          cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ATTRVARCHAR, Comparison.LIKE, valObj);
      CpoWhere cw2 = cpoAdapter.newWhere(Logical.AND, ValueObject.ATTR_ID, Comparison.EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testLikeWhereStrings() {
    String method = "testLikeWhereStrings:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      valObj.setAttrVarChar("T%");

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 =
          cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ATTRVARCHAR, Comparison.LIKE, "T%");
      CpoWhere cw2 = cpoAdapter.newWhere(Logical.AND, ValueObject.ATTR_ID, Comparison.EQ, 1);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNonAttributeWhere() {
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "value_object.id", Comparison.LT, 1);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.size() == 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereStaticValue() {
    String method = "testInWhereStaticValue:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.IN, null);
      cw1.setStaticValue("(1,3,5)");

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(coll.size() == 3, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereCollection() {
    String method = "testInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(1);
      inColl.add(3);
      inColl.add(5);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.IN, inColl);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(coll.size() == 3, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMultiInWhereCollection() {
    String method = "testMultiInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      Collection<Integer> inColl1 = new ArrayList<>();
      inColl1.add(1);
      inColl1.add(2);

      Collection<Integer> inColl2 = new ArrayList<>();
      inColl2.add(3);
      inColl2.add(4);

      Collection<Integer> inColl3 = new ArrayList<>();
      inColl3.add(5);
      inColl3.add(-6);

      //      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.IN, inColl1);
      CpoWhere cw2 = cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.IN, inColl2);
      CpoWhere cw3 = cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.IN, inColl3);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw1);
      wheres.add(cw2);
      wheres.add(cw3);
      Collection<ValueObject> coll =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTORDERBYRETRIEVE, valObj, wheres, null);

      assertTrue(coll.size() == 6, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNonAttributeInWhereCollection() {
    String method = "testNonAttributeInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(1);
      inColl.add(3);
      inColl.add(5);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(Logical.NONE, "value_object.id", Comparison.IN, inColl);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(coll.size() == 3, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testWhereParens() {
    String method = "testWhereParens:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);

      // Without the correct parens, this will return multiple rows for a retrieveBean which is a
      // failure
      CpoWhere cw1 = cpoAdapter.newWhere();
      cw1.setLogical(Logical.AND);
      cw1.addWhere(cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, 1));
      cw1.addWhere(cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.EQ, 3));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      ArrayList<CpoOrderBy> orderBys = new ArrayList<>();
      wheres.add(cw1);

      valObj =
          cpoAdapter.retrieveBean(ValueObject.FG_RETRIEVE_NULL, valObj, wheres, orderBys, null);

      assertNotNull(valObj, "Value Object should not be null");
      assertTrue(valObj.getId() == 1, "Id should equal 1");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testWhereOrderBy() {
    String method = "testWhereOrderBy:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);

      // Without the correct parens, this will return multiple rows for a retrieveBean which is a
      // failure
      CpoWhere cw1 = cpoAdapter.newWhere();
      cw1.setLogical(Logical.AND);
      cw1.addWhere(cpoAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, 1));
      cw1.addWhere(cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.EQ, 3));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      ArrayList<CpoOrderBy> orderBys = new ArrayList<>();
      wheres.add(cw1);

      valObj =
          cpoAdapter.retrieveBean(ValueObject.FG_RETRIEVE_NULL, valObj, valObj, wheres, orderBys);

      assertNotNull(valObj, "Value Object should not be null");
      assertTrue(valObj.getId() == 1, "Id should equal 1");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
