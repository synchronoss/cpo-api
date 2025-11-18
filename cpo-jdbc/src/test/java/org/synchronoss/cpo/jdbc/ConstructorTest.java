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

import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * ConstructorTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ConstructorTest {
  private static final Logger logger = LoggerFactory.getLogger(ConstructorTest.class);
  private static final String PASSWORDSTRING = "password=";

  public ConstructorTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: ConstructorTest.java,v 1.7 2006/01/31 22:55:03 dberry Exp $'
   */
  @BeforeClass
  public void setUp() {}

  @Test
  public void testConstructorClass() {
    String method = "testConstructorClass:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASS);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      logger.debug("=====> DatasourceName: " + cpoAdapter.getDataSourceName());
      // make sure the password is not in the name
      assertFalse(
          cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorClassProp() {
    String method = "testConstructorClassProp:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASSPROP);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      assertFalse(
          cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name:");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorDriver() {
    String method = "testConstructorDriver:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVER);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      assertFalse(
          cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorDriverUrlOnly() {
    String method = "testConstructorDriverUrlOnly:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVER_URLONLY);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      // The password is in the url as this is url only so it may hve the password string in it.
      // assertTrue("password is in datasource
      // name"+cpoAdapter.getDataSourceName(),cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorDriverProp() {
    String method = "testConstructorDriverProp:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVERPROP);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      assertTrue(
          !cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorClassClass() {
    String method = "testConstructorClassClass:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASSCLASS);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      assertTrue(
          !cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorDriverDriver() {
    String method = "testConstructorDriverDriver:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVERDRIVER);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      assertTrue(
          !cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorClassDriver() {
    String method = "testConstructorClassDriver:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASSDRIVER);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      assertTrue(
          !cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConstructorDriverClass() {
    String method = "testConstructorDriverClass:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVERCLASS);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      // make sure the password is not in the name
      assertTrue(
          !cpoAdapter.getDataSourceName().contains(PASSWORDSTRING),
          "password is in datasource name");

      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, 0, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterClass
  public void tearDown() {}
}
