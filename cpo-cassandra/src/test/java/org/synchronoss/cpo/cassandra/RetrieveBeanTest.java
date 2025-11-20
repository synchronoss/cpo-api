package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * RetrieveBeanTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RetrieveBeanTest {

  private static final Logger logger = LoggerFactory.getLogger(RetrieveBeanTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "IdoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(6));
    al.add(ValueObjectFactory.createValueObject(7));
    al.add(ValueObjectFactory.createValueObject(8));
    al.add(ValueObjectFactory.createValueObject(9));
    al.add(ValueObjectFactory.createValueObject(10));
    try {
      cpoAdapter.insertBeans("TestOrderByInsert", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeans() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, al.size(), "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeans2() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        assertEquals(count, al.size(), "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @DataProvider(name = "fetchSize")
  public Object[][] createData1() {
    return new Object[][] {
      {2}, {9}, {10}, {11}, {20},
    };
  }

  @Test(dataProvider = "fetchSize")
  public void testRetrieveBeansFetchSize(Integer fetchSize) {
    String method = "testRetrieveBeansNoWaitSize2:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      var oldSize = cpoAdapter.getFetchSize();
      cpoAdapter.setFetchSize(fetchSize);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj, null, null, null); ) {
        AtomicInteger count = new AtomicInteger();
        beans.forEach(
            bean -> {
              count.getAndIncrement();
            });
        assertEquals(count.get(), al.size(), "Number of beans is " + count);
      }
      cpoAdapter.setFetchSize(oldSize);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBean() {

    String method = "testRetrieveBean:";
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    ValueObject rvo;

    try {
      rvo = cpoAdapter.retrieveBean(vo);
      assertNotNull(rvo, method + "Returned Value object is null");
      assertNotSame(vo, rvo, method + "ValueObjects are the same");
      assertEquals(rvo.getAttrVarChar(), "Test", method + "Strings are not the same");
      if (rvo.getAttrVarChar().equals(vo.getAttrVarChar())) {
        fail(method + "ValueObjects are the same");
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNullRetrieveBean() {

    String method = "testNullRetrieveBean:";
    ValueObject vo = ValueObjectFactory.createValueObject(100);
    ValueObject rvo;

    try {
      rvo = cpoAdapter.retrieveBean(vo);
      assertNull(rvo, method + "Returned Value object is Not Null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteBeans("TestOrderByDelete", al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}
