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

import junit.framework.TestCase;
import org.synchronoss.cpo.*;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ConstructorTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ConstructorTest extends TestCase {
  private static final Logger logger = LoggerFactory.getLogger(ConstructorTest.class);
  private static final String PASSWORDSTRING = "password=";

  public ConstructorTest(String name) {
    super(name);
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: ConstructorTest.java,v 1.7 2006/01/31 22:55:03 dberry Exp $'
   */
  @Override
  public void setUp() {
  }

  public void testConstructorClass() {
    String method = "testConstructorClass:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASS);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      logger.debug("=====> DatasourceName: "+cpoAdapter.getDataSourceName());
      // make sure the password is not in the name
      assertTrue("password is in datasource name",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorClassProp() {
    String method = "testConstructorClassProp:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASSPROP);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      // make sure the password is not in the name
      assertTrue("password is in datasource name:",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorDriver() {
    String method = "testConstructorDriver:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVER);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      // make sure the password is not in the name
      assertTrue("password is in datasource name",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorDriverUrlOnly() {
    String method = "testConstructorDriverUrlOnly:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVER_URLONLY);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
       
      // make sure the password is not in the name
      // The password is in the url as this is url only so it may hve the password string in it.
      //assertTrue("password is in datasource name"+cpoAdapter.getDataSourceName(),cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
     ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorDriverProp() {
    String method = "testConstructorDriverProp:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVERPROP);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      // make sure the password is not in the name
      assertTrue("password is in datasource name",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorClassClass() {
    String method = "testConstructorClassClass:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASSCLASS);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      // make sure the password is not in the name
      assertTrue("password is in datasource name",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorDriverDriver() {
    String method = "testConstructorDriverDriver:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVERDRIVER);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      // make sure the password is not in the name
      assertTrue("password is in datasource name",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorClassDriver() {
    String method = "testConstructorClassDriver:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASSDRIVER);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      // make sure the password is not in the name
      assertTrue("password is in datasource name",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConstructorDriverClass() {
    String method = "testConstructorDriverClass:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_DRIVERCLASS);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      
      // make sure the password is not in the name
      assertTrue("password is in datasource name",cpoAdapter.getDataSourceName().indexOf(PASSWORDSTRING)==-1);
      
      ValueObject valObj = new ValueObject();
      List<ValueObject> objs = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("List size is " + objs.size(), objs.size() == 0);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Override
  public void tearDown() {
  }
}