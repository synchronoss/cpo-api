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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoWhere;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class InterleavedWhereTest {

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  /**
   * Creates a new InterleavedWhereTest object.
   */
  public InterleavedWhereTest() {
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
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo1 = new ValueObjectBean(1);
    vo1.setAttrVarChar("Test");
    vo1.setAttrBit(true);
    al.add(vo1);

    ValueObject vo3 = new ValueObjectBean(3);
    vo3.setAttrVarChar("Test");
    vo3.setAttrBit(true);
    al.add(vo3);

    ValueObject vo5 = new ValueObjectBean(5);
    vo5.setAttrVarChar("Test");
    vo5.setAttrBit(true);
    al.add(vo5);
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
  public void testInterleavedInWhereCollection() {
    String method = "testInterleavedInWhereCollection:";
    Collection<ValueObject> coll;
    CpoWhere cw;
    CpoWhere cw1 = null;

    try {
      ValueObject valObj = new ValueObjectBean(1);
      valObj.setAttrBit(true);
      valObj.setAttrVarChar("Test");
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(1);
      inColl.add(3);
      inColl.add(5);

      cw = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, "id", CpoWhere.COMP_IN, inColl);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      coll = cpoAdapter.retrieveBeans("InterleavedWhere", valObj, wheres, null);

      assertTrue("Collection size is " + coll.size(), coll.size() == 3);


    } catch (Exception e) {
      fail(method + e.getMessage());
    }

  }
}
