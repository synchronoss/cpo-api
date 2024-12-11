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

import org.synchronoss.cpo.*;
import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * InsertObjectTest is a test class for testing the insert api calls of cpo
 *
 * @author david berry
 */
public class InsertObjectTest {

  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CpoAdapter readAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private boolean isSupportsMillis = Boolean.valueOf(JdbcTestProperty.getProperty(JdbcTestProperty.PROP_MILLIS_SUPPORTED));
  private final String className = this.getClass().getSimpleName();

  public InsertObjectTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      readAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(readAdapter, method + "readAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInsertObject() {
    String method = "testInsertObject:";
    ValueObject valObj = ValueObjectFactory.createValueObject(91, className);

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
      ValueObject vo = readAdapter.retrieveBean(ValueObject.FG_RETRIEVE_NULL, valObj, valObj, null, null);
      assertEquals(valObj.getId(), vo.getId(), "Ids do not match");
      assertEquals(valObj.getAttrInteger(), vo.getAttrInteger(), "Integers do not match");
      assertEquals(vo.getAttrVarChar(), valObj.getAttrVarChar(),"Strings do not match");
      assertEquals(vo.getAttrDatetime(), valObj.getAttrDatetime(),"Timestamps do not match");
      assertTrue(vo.getAttrBit(), "boolean not stored correctly");

    } catch (Exception e) {
      fail(method + e.getMessage());
    }


  }

  @Test
  public void testInsertObjects() {

    String method = "testInsertObjects:";
    ValueObject vo = ValueObjectFactory.createValueObject(92, className);
    vo.setAttrVarChar("Test");

    ArrayList<ValueObject> arrayList = new ArrayList<>();
    arrayList.add(vo);
    arrayList.add(ValueObjectFactory.createValueObject(93, className));
    arrayList.add(ValueObjectFactory.createValueObject(94, className));
    arrayList.add(ValueObjectFactory.createValueObject(95, className));
    al.addAll(arrayList);
    try {
      long inserts = cpoAdapter.insertObjects(arrayList);
      assertEquals(inserts, 4,"inserts performed do not equal inserts requested");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      Collection<ValueObject> col = readAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, vo);

      assertEquals(al.size(), col.size(), method + "Invalid number of objects returned");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }


  }

  @AfterClass
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