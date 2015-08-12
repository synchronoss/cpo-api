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
package org.synchronoss.cpo.cassandra;

import org.junit.*;
import org.synchronoss.cpo.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class OrderByTest {

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  public OrderByTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   */
  @Before
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(method + "CpoAdapter is null", cpoAdapter);
      // Add the test valueObjects
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    al.add(new ValueObjectBean(1));
    al.add(new ValueObjectBean(2));
    al.add(new ValueObjectBean(3));
    al.add(new ValueObjectBean(4));
    al.add(new ValueObjectBean(5));
    try {
      cpoAdapter.insertObjects("TestOrderByInsert", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @After
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
  public void testNewOrderBy() {
    String method = "testOrderByAscending:";
    Collection<ValueObject> col;
    String marker = "MY_MARKER";
    String attribute = "MY_ATTRIBUTE";
    String function = "MY_FUNCTION";
    boolean ascending = false;

    try {
      CpoOrderBy cob = cpoAdapter.newOrderBy(marker, attribute, ascending, function);
      assertEquals(marker, cob.getMarker());
      assertEquals(attribute, cob.getAttribute());
      assertEquals(ascending, cob.getAscending());
      assertEquals(function, cob.getFunction());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /**
   * TODO - add back in when you get the tables correct
   */
  @Test
  public void testOrderByAscending() {
//    String method = "testOrderByAscending:";
//    Collection<ValueObject> col;
//
//
//    try {
//      Collection<Integer> inColl = new ArrayList<Integer>();
//      inColl.add(new Integer(1));
//      inColl.add(new Integer(3));
//      inColl.add(new Integer(5));
//
////      CpoWhere cw = cpoAdapter.newWhere();
//      CpoWhere cw = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_IN, inColl);
//      ArrayList<CpoWhere> wheres = new ArrayList<CpoWhere>();
//      wheres.add(cw);
//
//      CpoOrderBy cob = cpoAdapter.newOrderBy("id", true);
//      Collection<CpoOrderBy> colCob = new ArrayList<CpoOrderBy>();
//      colCob.add(cob);
//      ValueObject valObj = new ValueObjectBean();
//      col = cpoAdapter.retrieveBeans("TestOrderByRetrieve", valObj, wheres, colCob);
//
//      int id = 1;
//      for (ValueObject vo : col) {
//        assertEquals(id, vo.getId());
//        id++;
//      }
//    } catch (Exception e) {
//      fail(method + e.getMessage());
//    }
  }

  /**
   * TODO - add back in when you get the tables correct
   */
  @Test
  public void testOrderByDescending() {
//    String method = "testOrderByDescending:";
//    List<ValueObject> col;
//
//    try {
//      CpoOrderBy cob = cpoAdapter.newOrderBy("id", false, null);
//      CpoOrderBy cob2 = cpoAdapter.newOrderBy(CpoOrderBy.DEFAULT_MARKER, "attrVarChar", false, null);
//      Collection<CpoOrderBy> colCob = new ArrayList<CpoOrderBy>();
//      colCob.add(cob);
//      colCob.add(cob2);
//      ValueObject valObj = new ValueObjectBean();
//      col = cpoAdapter.retrieveBeans("TestOrderByRetrieve", valObj, colCob);
//      int id = 5;
//      for (ValueObject vo : col) {
//        assertEquals(id, vo.getId());
//        id--;
//      }
//    } catch (Exception e) {
//      fail(method + e.getMessage());
//    }
  }

  @Test
  public void testOrderByFunction() {
//    String method = "testOrderByAscending:";
//    Collection<ValueObject> col;
//
//    ValueObject vobj = new ValueObjectBean(-6);
//    try {
//      cpoAdapter.insertObject("TestOrderByInsert", vobj);
//    } catch (Exception e) {
//      fail(method + e.getMessage());
//    }
//    try {
//      CpoOrderBy cob = cpoAdapter.newOrderBy("id", true, "ABS(id)");
//      Collection<CpoOrderBy> colCob = new ArrayList<CpoOrderBy>();
//      colCob.add(cob);
//      ValueObject valObj = new ValueObjectBean();
//      col = cpoAdapter.retrieveBeans("TestOrderByRetrieve", valObj, colCob);
//
//      int id = 1;
//      for (ValueObject vo : col) {
//        int voId = vo.getId();
//        if (voId < 0) {
//          voId *= -1;
//        }
//        assertEquals(id, voId);
//        id++;
//      }
//    } catch (Exception e) {
//      fail(method + e.getMessage());
//    }
//
//    try {
//      cpoAdapter.deleteObject("TestOrderByDelete", vobj);
//
//    } catch (Exception e) {
//      fail(method + e.getMessage());
//    }
  }
}
