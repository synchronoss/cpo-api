package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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
import org.synchronoss.cpo.core.CpoQuery;
import org.synchronoss.cpo.core.CpoWhere;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class NativeExpressionTest {

  // unique id base so this class's rows never collide with another test class's
  private static final int IDB = 800000;

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  public NativeExpressionTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "CpoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(IDB + 1);
    vo.setAttrVarChar("Test");
    vo.setAttrInt(1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(IDB + 2));
    al.add(ValueObjectFactory.createValueObject(IDB + 3));
    al.add(ValueObjectFactory.createValueObject(IDB + 4));
    al.add(ValueObjectFactory.createValueObject(IDB + 5));
    al.add(ValueObjectFactory.createValueObject(-(IDB + 6)));
    try {
      cpoAdapter.insertBeans("TestOrderByInsert", al);
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

  @Test
  public void testNativeInWhere() {
    String method = "testNativeOrWhere:";
    Collection<ValueObject> col;
    CpoWhere cw = null;
    CpoWhere cw1 = null;
    CpoWhere cw2 = null;

    try {
      ArrayList<CpoNativeFunction> cnqAl = new ArrayList<>();

      cnqAl.add(
          new CpoNativeFunction(
              "__CPO_WHERE__", "WHERE id IN (" + (IDB + 2) + "," + (IDB + 3) + ")"));

      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 3);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_TESTWHERERETRIEVE).nativeExpressions(cnqAl),
              valObj,
              valObj); ) {
        long count =
            beans
                .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
                .count();
        assertEquals(count, 2, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
