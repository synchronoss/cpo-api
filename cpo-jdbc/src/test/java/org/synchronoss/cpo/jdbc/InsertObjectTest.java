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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;

/**
 * InsertObjectTest is a JUnit test class for testing the insert api calls of cpo
 *
 * @author david berry
 */
public class InsertObjectTest {

  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CpoAdapter readAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private boolean isSupportsMillis = Boolean.valueOf(JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_MILLIS_SUPPORTED));

  public InsertObjectTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
   */
  @Before
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      readAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "IdoAdapter is null", readAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInsertObject() {
    String method = "testInsertObject:";
    ValueObject valObj = new ValueObjectBean(5);

    valObj.setAttrVarChar("testInsert");
    valObj.setAttrInteger(3);
    Timestamp ts = new Timestamp(System.currentTimeMillis());

    if (!isSupportsMillis) {
      ts.setNanos(0);
    }

    valObj.setAttrDatetime(ts);

    valObj.setAttrBit(true);

    // test the setObject and getObject type
    BigInteger bigInteger = BigInteger.valueOf(1234);
    valObj.setAttrBigInt(bigInteger);

    al.add(valObj);

    try {
      cpoAdapter.insertObject(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject vo = readAdapter.retrieveBean(null, valObj, valObj, null, null);
      assertTrue("Ids do not match", vo.getId() == valObj.getId());
      assertTrue("Integers do not match", vo.getAttrInteger() == valObj.getAttrInteger());
      assertEquals("Strings do not match", vo.getAttrVarChar(), valObj.getAttrVarChar());
      assertEquals("Timestamps do not match", vo.getAttrDatetime(), valObj.getAttrDatetime());
      assertTrue("boolean not stored correctly", vo.getAttrBit());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }


  }

  @Test
  public void testInsertObjects() {

    String method = "testInsertObjects:";
    ValueObject vo = new ValueObjectBean(1);
    vo.setAttrVarChar("Test");

    al.add(vo);
    al.add(new ValueObjectBean(2));
    al.add(new ValueObjectBean(3));
    al.add(new ValueObjectBean(4));
    try {
      long inserts = cpoAdapter.insertObjects(al);
      assertEquals("inserts performed do not equal inserts requested", inserts, 4);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      Collection<ValueObject> col = readAdapter.retrieveBeans(null, vo);

      assertTrue(method + "Invalid number of objects returned", col.size() == al.size());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }


  }

  @After
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteObjects(al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
    readAdapter = null;
  }
}