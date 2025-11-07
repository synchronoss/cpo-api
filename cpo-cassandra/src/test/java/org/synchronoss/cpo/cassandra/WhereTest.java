/*
 * Copyright (C) 2003-2025 David E. Berry
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
package org.synchronoss.cpo.cassandra;

import org.synchronoss.cpo.*;

import java.util.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class WhereTest extends CassandraContainerBase {

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  public WhereTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
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
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    vo.setAttrInt(1);
    vo.setAttrBigInt(1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(-6));
    try {
      cpoAdapter.insertObjects("TestOrderByInsert", al);
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
      cpoAdapter.deleteObjects("TestOrderByDelete", al);
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
      cw = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, null);
      cw.setStaticValue("3");
      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

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
      cw = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, valObj);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

      assertEquals(1, col.size(), "Col size is " + col.size());
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
      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      cw = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, valObj);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      col = cpoAdapter.retrieveBeans(null, valObj, wheres, null);

      assertEquals(1, col.size(), "Col size is " + col.size());
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

      col = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testAndWhere() {
    String method = "testAndWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, 1);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, "attrInt", CpoWhere.COMP_EQ, 1);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

      assertEquals(1, col.size(), "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMultipleBindWhere() {
    String method = "testMultipleBindWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      valObj.setAttrInt(1);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "attrInt", CpoWhere.COMP_EQ, valObj);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, "id", CpoWhere.COMP_EQ, valObj);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

      assertEquals(1, col.size(), "Col size is " + col.size());
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
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, 1);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

      assertEquals(1, col.size(), "Col size is " + col.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereStaticValue() {
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_IN, null);
      cw1.setStaticValue("(1,3,5)");

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

      assertEquals(3, coll.size(), "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInWhereCollection() {
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(1);
      inColl.add(3);
      inColl.add(5);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_IN, inColl);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

      assertEquals(3, coll.size(), "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNonAttributeInWhereCollection() {
    String method = "testNonAttributeWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(1);
      inColl.add(3);
      inColl.add(5);

      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_IN, inColl);

      cw.addWhere(cw1);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> coll = cpoAdapter.retrieveBeans("TestWhereRetrieve", valObj, wheres, null);

      assertEquals(3, coll.size(), "Collection size is " + coll.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
