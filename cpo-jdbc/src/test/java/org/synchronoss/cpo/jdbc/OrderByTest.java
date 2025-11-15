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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoOrderBy;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class OrderByTest {

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  public OrderByTest() {}

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
      // Add the test valueObjects
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    al.add(ValueObjectFactory.createValueObject(1));
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
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

  /** DOCUMENT ME! */
  @Test
  public void testOrderByAscending() {
    String method = "testOrderByAscending:";
    Collection<ValueObject> col;

    try {
      CpoOrderBy cob = cpoAdapter.newOrderBy(ValueObject.ATTR_ID, true);
      CpoOrderBy cob1 =
          cpoAdapter.newOrderBy(CpoOrderBy.DEFAULT_MARKER, ValueObject.ATTR_ATTRVARCHAR, true);
      Collection<CpoOrderBy> colCob = new ArrayList<>();
      colCob.add(cob);
      colCob.add(cob1);
      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTORDERBYRETRIEVE, valObj, colCob);

      int id = 1;
      for (ValueObject vo : col) {
        assertEquals(id, vo.getId());
        id++;
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /** DOCUMENT ME! */
  @Test
  public void testOrderByDescending() {
    String method = "testOrderByDescending:";
    List<ValueObject> col;

    try {
      CpoOrderBy cob = cpoAdapter.newOrderBy(ValueObject.ATTR_ID, false, null);
      CpoOrderBy cob2 =
          cpoAdapter.newOrderBy(
              CpoOrderBy.DEFAULT_MARKER, ValueObject.ATTR_ATTRVARCHAR, false, null);
      Collection<CpoOrderBy> colCob = new ArrayList<>();
      colCob.add(cob);
      colCob.add(cob2);
      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTORDERBYRETRIEVE, valObj, colCob);
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

    ValueObject vobj = ValueObjectFactory.createValueObject(-6);
    try {
      cpoAdapter.insertBean(ValueObject.FG_CREATE_TESTORDERBYINSERT, vobj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      CpoOrderBy cob = cpoAdapter.newOrderBy(ValueObject.ATTR_ID, true, "ABS(id)");
      Collection<CpoOrderBy> colCob = new ArrayList<>();
      colCob.add(cob);
      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTORDERBYRETRIEVE, valObj, colCob);

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
      cpoAdapter.deleteBean(ValueObject.FG_DELETE_TESTORDERBYDELETE, vobj);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
