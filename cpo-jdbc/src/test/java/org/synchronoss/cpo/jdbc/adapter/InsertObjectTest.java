package org.synchronoss.cpo.jdbc.adapter;

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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * InsertObjectTest is a test class for testing the insert api calls of cpo
 *
 * @author david berry
 */
public class InsertObjectTest {

  private final ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CpoAdapter readAdapter = null;
  private boolean isSupportsMillis = true;

  public InsertObjectTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
   */
  @Parameters({"db.millisupport"})
  @BeforeClass
  public void setUp(boolean milliSupport) {
    String method = "setUp:";
    isSupportsMillis = milliSupport;

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
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
    ValueObject valObj = ValueObjectFactory.createValueObject(5);

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
      cpoAdapter.insertBean(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject vo =
          readAdapter.retrieveBean(ValueObject.FG_RETRIEVE_NULL, valObj, valObj, null, null);
      assertEquals(vo.getId(), valObj.getId(), "Ids do not match");
      assertEquals(vo.getAttrInteger(), valObj.getAttrInteger(), "Integers do not match");
      assertEquals(vo.getAttrVarChar(), valObj.getAttrVarChar(), "Strings do not match");
      assertEquals(vo.getAttrDatetime(), valObj.getAttrDatetime(), "Timestamps do not match");
      assertTrue(vo.getAttrBit(), "boolean not stored correctly");

    } catch (Exception e) {
      fail(method + e.getMessage());
    } finally {
      try {
        cpoAdapter.deleteBean(valObj);
      } catch (Exception e) {
        fail(method + e.getMessage());
      }
    }
  }

  @Test
  public void testInsertObjects() {

    String method = "testInsertObjects:";
    ValueObject vo = ValueObjectFactory.createValueObject(61);
    vo.setAttrVarChar("Test");
    ArrayList<ValueObject> a2 = new ArrayList<>();
    a2.add(vo);
    a2.add(ValueObjectFactory.createValueObject(62));
    a2.add(ValueObjectFactory.createValueObject(63));
    a2.add(ValueObjectFactory.createValueObject(64));
    al.addAll(a2);
    try {
      long inserts = cpoAdapter.insertBeans(a2);
      assertEquals(inserts, 4, "inserts performed do not equal inserts requested");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try (Stream<ValueObject> beans = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, vo); ) {
      long count = beans.count();
      assertEquals(count, a2.size(), "Number of beans is " + count);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteBeans(al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
    readAdapter = null;
  }
}
