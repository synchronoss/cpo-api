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
package org.synchronoss.cpo.jdbc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoOrderBy;

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
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
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

  /**
   * DOCUMENT ME!
   */
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

  /**
   * DOCUMENT ME!
   */
  @Test
  public void testNewOrderBy() {
    String method = "testNewOrderBy:";
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
   * DOCUMENT ME!
   */
  @Test
  public void testOrderByAscending() {
    String method = "testOrderByAscending:";
    Collection<ValueObject> col;

    try {
      CpoOrderBy cob = cpoAdapter.newOrderBy("id", true);
      CpoOrderBy cob1 = cpoAdapter.newOrderBy(CpoOrderBy.DEFAULT_MARKER, "attrVarChar", true);
      Collection<CpoOrderBy> colCob = new ArrayList<>();
      colCob.add(cob);
      colCob.add(cob1);
      ValueObject valObj = new ValueObjectBean();
      col = cpoAdapter.retrieveBeans("TestOrderByRetrieve", valObj, colCob);

      int id = 1;
      for (ValueObject vo : col) {
        assertEquals(id, vo.getId());
        id++;
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /**
   * DOCUMENT ME!
   */
  @Test
  public void testOrderByDescending() {
    String method = "testOrderByDescending:";
    List<ValueObject> col;

    try {
      CpoOrderBy cob = cpoAdapter.newOrderBy("id", false, null);
      CpoOrderBy cob2 = cpoAdapter.newOrderBy(CpoOrderBy.DEFAULT_MARKER, "attrVarChar", false, null);
      Collection<CpoOrderBy> colCob = new ArrayList<>();
      colCob.add(cob);
      colCob.add(cob2);
      ValueObject valObj = new ValueObjectBean();
      col = cpoAdapter.retrieveBeans("TestOrderByRetrieve", valObj, colCob);
      int id = 5;
      for (ValueObject vo : col) {
        assertEquals(id, vo.getId());
        id--;
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testOrderByFunction() {
    String method = "testOrderByFunction:";
    Collection<ValueObject> col;

    ValueObject vobj = new ValueObjectBean(-6);
    try {
      cpoAdapter.insertObject("TestOrderByInsert", vobj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      CpoOrderBy cob = cpoAdapter.newOrderBy("id", true, "ABS(id)");
      Collection<CpoOrderBy> colCob = new ArrayList<>();
      colCob.add(cob);
      ValueObject valObj = new ValueObjectBean();
      col = cpoAdapter.retrieveBeans("TestOrderByRetrieve", valObj, colCob);

      int id = 1;
      for (ValueObject vo : col) {
        int voId = vo.getId();
        if (voId < 0) {
          voId *= -1;
        }
        assertEquals(id, voId);
        id++;
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      cpoAdapter.deleteObject("TestOrderByDelete", vobj);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
