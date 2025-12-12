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

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoNativeFunction;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class NativeExpressionTest {

  private CpoAdapter cpoAdapter = null;
  private final ArrayList<ValueObject> al = new ArrayList<>();

  public NativeExpressionTest() {}

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
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt((short) 1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(-6));
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

  @Test
  public void testNativeInWhere() {
    String method = "testNativeOrWhere:";
    Collection<ValueObject> col;

    try {
      ArrayList<CpoNativeFunction> cnqAl = new ArrayList<>();

      cnqAl.add(new CpoNativeFunction("__CPO_WHERE__", "WHERE id IN (2,3)"));

      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, valObj, null, null, cnqAl); ) {
        long count = beans.count();
        assertEquals(count, 2, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /** DOCUMENT ME! */
  @Test
  public void testNativeOrWhere() {
    String method = "testNativeOrWhere:";
    Collection<ValueObject> col;

    try {
      ArrayList<CpoNativeFunction> cnqAl = new ArrayList<>();

      cnqAl.add(new CpoNativeFunction("__CPO_WHERE__", "WHERE ID = 2 OR ID = 3"));

      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, valObj, null, null, cnqAl); ) {
        long count = beans.count();
        assertEquals(count, 2, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNullNative() {
    String method = "testNullNative:";
    Collection<ValueObject> col;

    try {
      ArrayList<CpoNativeFunction> cnqAl = new ArrayList<>();

      cnqAl.add(new CpoNativeFunction("__CPO_WHERE__", null));

      ValueObject valObj = ValueObjectFactory.createValueObject(3);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, valObj, null, null, cnqAl); ) {
        long count = beans.count();
        assertEquals(count, 6, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
