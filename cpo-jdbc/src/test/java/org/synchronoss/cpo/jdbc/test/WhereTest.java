/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.jdbc.test;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.jdbc.JdbcDbContainerBase;
import org.synchronoss.cpo.jdbc.JdbcStatics;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.synchronoss.cpo.jdbc.ValueObjectFactory;
import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class WhereTest {

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();
  private final String className = this.getClass().getSimpleName();

  public WhereTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
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
    ValueObject vo = ValueObjectFactory.createValueObject(21, className);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt((short)21);
    vo.setAttrInteger(1);
    vo.setAttrBigInt(BigInteger.valueOf(2075L));
    vo.setAttrDate(new java.sql.Date(new java.util.Date().getTime()));
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(22, className));
    al.add(ValueObjectFactory.createValueObject(23, className));
    al.add(ValueObjectFactory.createValueObject(24, className));
    al.add(ValueObjectFactory.createValueObject(25, className));
    al.add(ValueObjectFactory.createValueObject(-26, className));
    try {
      cpoAdapter.insertObjects(ValueObject.FG_CREATE_TESTORDERBYINSERT, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /**
   * DOCUMENT ME!
   */
  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteObjects(ValueObject.FG_DELETE_TESTORDERBYDELETE, al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }

  /**
   * DOCUMENT ME!
   */
  @Test
  public void testStaticWhere() {
    String method = "testStaticWhere:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(className);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_GT, null);
      cw1.setStaticValue("23");
      cw.addWhere(cw1);
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);

      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 2, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testSetObjectWhere() {
    String method = "testSetObjectWhere:";
    Collection<ValueObject> col;

    try {
      BigInteger bigInt = BigInteger.valueOf(2075L);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ATTRBIGINT, CpoWhere.COMP_EQ, bigInt);
      cw.addWhere(cw1);
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, ValueObjectFactory.createValueObject(className), wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testValueWhere() {
    String method = "testValueWhere:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(23, className);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_GT, valObj);
      cw.addWhere(cw1);
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 2, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNoMarkerWhere() {
    String method = "testNoMarkerWhere:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(23, className);
      CpoWhere cw = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_GT, valObj);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      wheres.add(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_DYNAMICWHERE, valObj, wheres, null);

      assertEquals(col.size(), 2, "Col size is " + col.size());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(23, className);

      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNestedWhere() {
    String method = "testNestedWhere:";
    Collection<ValueObject> col;
    ArrayList<CpoWhere> wheres = new ArrayList<>();

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-26, className);
      CpoWhere cw = cpoAdapter.newWhere();
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj));

      CpoWhere cwAnd = cpoAdapter.newWhere();
      cwAnd.setLogical(CpoWhere.LOGIC_OR);
      valObj = ValueObjectFactory.createValueObject(22, className);
      cwAnd.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj));
      valObj = ValueObjectFactory.createValueObject(23, className);
      cwAnd.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_OR, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj));

      cw.addWhere(cwAnd);
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 3, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testIsNullWhere() {
    String method = "testIsNullWhere:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(23, className);
      CpoWhere cw = cpoAdapter.newWhere();
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ATTRCHAR, CpoWhere.COMP_ISNULL, null));
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 6, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testAttributeFunction() {
    String method = "testAttributeFunction:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(26, className);
      CpoWhere cw = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj);
      cw.setAttributeFunction("ABS(id)");

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertEquals(rvo.getId(), -26, "-6 != " + rvo.getId());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testValueFunction() {
    String method = "testValueFunction:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-21, className);
      CpoWhere cw = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj, false);
      cw.setValueFunction("abs(id)");

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertEquals(rvo.getId(), 21, "1 != " + rvo.getId());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testAndWhere() {
    String method = "testAndWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(23, className);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ATTRCHAR, CpoWhere.COMP_ISNULL, null);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_ATTRCHAR, CpoWhere.COMP_ISNULL, null, true);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertTrue(col.isEmpty(), "Col size is " + col.size());

      cw = cpoAdapter.newWhere();
      cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ATTRCHAR, CpoWhere.COMP_ISNULL, null);
      cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      wheres.clear();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testOrWhere() {
    String method = "testOrWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(23, className);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, null);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_OR, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj);

      cw1.setStaticValue("22");
      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 2, "Col size is " + col.size());

      cw = cpoAdapter.newWhere();
      CpoWhere cwName = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className);
      CpoWhere cwAnd = cpoAdapter.newWhere();
      cwAnd.setLogical(CpoWhere.LOGIC_AND);

      cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, null);
      cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_OR, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj, true);

      cw1.setStaticValue("23");
      cwAnd.addWhere(cw1);
      cwAnd.addWhere(cw2);

      cw.addWhere(cwName);
      cw.addWhere(cwAnd);

      wheres.clear();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 6, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRightAttributeFunction() {
    String method = "testRightAttributeFunction:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-21, className);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere();
      cw1.setAttribute("id");
      cw1.setRightAttribute("attrSmallInt");
      cw1.setAttributeFunction("ABS(id)");
      cw1.setComparison(CpoWhere.COMP_EQ);
      cw1.setRightAttributeFunction("ABS(attrSmallInt)");
      cw1.setLogical(CpoWhere.LOGIC_NONE);

      cw.addWhere(cw1);
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertEquals(rvo.getId(), 21, "21 != " + rvo.getId());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRightAttribute() {
    String method = "testRightAttribute:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(-21, className);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere();
      cw1.setAttribute("id");
      cw1.setRightAttribute("attrSmallInt");
      cw1.setComparison(CpoWhere.COMP_EQ);
      cw1.setLogical(CpoWhere.LOGIC_NONE);

      cw.addWhere(cw1);
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));

      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, valObj, cw, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
      ValueObject rvo = col.iterator().next();
      assertEquals(rvo.getId(), 21, "1 != " + rvo.getId());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMultipleBindWhere() {
    String method = "testMultipleBindWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);
      valObj.setAttrVarChar("Test");

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ATTRVARCHAR, CpoWhere.COMP_EQ, valObj);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testLikeWhere() {
    String method = "testLikeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);
      valObj.setAttrVarChar("T%");

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ATTRVARCHAR, CpoWhere.COMP_LIKE, valObj);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testLikeWhereStrings() {
    String method = "testLikeWhereStrings:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);
      valObj.setAttrVarChar("T%");

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ATTRVARCHAR, CpoWhere.COMP_LIKE, "T%");
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, 21);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNonAttributeWhere() {
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "value_object.id", CpoWhere.COMP_LT, 21);

      cw.addWhere(cw1);
      cw.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_AND, ValueObject.ATTR_NAME, CpoWhere.COMP_EQ, className));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(col.size(), 1, "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereStaticValue() {
    String method = "testInWhereStaticValue:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_IN, null);
      cw1.setStaticValue("(21,23,25)");

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(coll.size(), 3, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereCollection() {
    String method = "testInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(21);
      inColl.add(23);
      inColl.add(25);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_IN, inColl);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(coll.size(), 3, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMultiInWhereCollection() {
    String method = "testMultiInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);
      Collection<Integer> inColl1 = new ArrayList<>();
      inColl1.add(21);
      inColl1.add(22);

      Collection<Integer> inColl2 = new ArrayList<>();
      inColl2.add(23);
      inColl2.add(24);

      Collection<Integer> inColl3 = new ArrayList<>();
      inColl3.add(25);
      inColl3.add(-26);

//      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_IN, inColl1);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_OR, ValueObject.ATTR_ID, CpoWhere.COMP_IN, inColl2);
      CpoWhere cw3 = cpoAdapter.newWhere(CpoWhere.LOGIC_OR, ValueObject.ATTR_ID, CpoWhere.COMP_IN, inColl3);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw1);
      wheres.add(cw2);
      wheres.add(cw3);
      Collection<ValueObject> coll = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_DYNAMICWHERE, valObj, wheres, null);

      assertEquals(coll.size(), 6, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNonAttributeInWhereCollection() {
    String method = "testNonAttributeInWhereCollection:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(21);
      inColl.add(23);
      inColl.add(25);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "value_object.id", CpoWhere.COMP_IN, inColl);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, wheres, null);

      assertEquals(coll.size(), 3, "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testWhereParens() {
    String method = "testWhereParens:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);

      // Without the correct parens, this will return multiple rows for a retrieveBean which is a
      // failure
      CpoWhere cw1 = cpoAdapter.newWhere();
      cw1.setLogical(CpoWhere.LOGIC_AND);
      cw1.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, 21));
      cw1.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_OR, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, 23));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      ArrayList<CpoOrderBy> orderBys = new ArrayList<>();
      wheres.add(cw1);

      valObj = cpoAdapter.retrieveBean(ValueObject.FG_RETRIEVE_NULL, valObj, wheres, orderBys, null);

      assertNotNull(valObj,"Value Object should not be null");
      assertEquals(valObj.getId(), 21, "Id should equal 31");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testWhereOrderBy() {
    String method = "testWhereOrderBy:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(21, className);

      // Without the correct parens, this will return multiple rows for a retrieveBean which is a
      // failure
      CpoWhere cw1 = cpoAdapter.newWhere();
      cw1.setLogical(CpoWhere.LOGIC_AND);
      cw1.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, 21));
      cw1.addWhere(cpoAdapter.newWhere(CpoWhere.LOGIC_OR, ValueObject.ATTR_ID, CpoWhere.COMP_EQ, 23));

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      ArrayList<CpoOrderBy> orderBys = new ArrayList<>();
      wheres.add(cw1);

      valObj = cpoAdapter.retrieveBean(ValueObject.FG_RETRIEVE_NULL, valObj, valObj, wheres, orderBys);

      assertNotNull(valObj, "Value Object should not be null");
      assertEquals(valObj.getId(), 21, "Id should equal 31");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
