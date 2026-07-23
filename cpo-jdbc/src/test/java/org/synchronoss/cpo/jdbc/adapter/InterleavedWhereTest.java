package org.synchronoss.cpo.jdbc.adapter;

/*-
 * [[
 * jdbc
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
import org.synchronoss.cpo.core.CpoQuery;
import org.synchronoss.cpo.core.CpoWhere;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class InterleavedWhereTest {

  // unique id base so this class's rows never collide with another test class's
  private static final int IDB = 1500000;

  private CpoAdapter cpoAdapter = null;
  private final ArrayList<ValueObject> al = new ArrayList<>();

  /** Creates a new InterleavedWhereTest object. */
  public InterleavedWhereTest() {}

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
    ValueObject vo1 = ValueObjectFactory.createValueObject(IDB + 1);
    vo1.setAttrVarChar("Test");
    vo1.setAttrBit(true);
    al.add(vo1);

    ValueObject vo3 = ValueObjectFactory.createValueObject(IDB + 3);
    vo3.setAttrVarChar("Test");
    vo3.setAttrBit(true);
    al.add(vo3);

    ValueObject vo5 = ValueObjectFactory.createValueObject(IDB + 5);
    vo5.setAttrVarChar("Test");
    vo5.setAttrBit(true);
    al.add(vo5);
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
  public void testInterleavedInWhereCollection() {
    String method = "testInterleavedInWhereCollection:";
    Collection<ValueObject> coll;
    CpoWhere cw;
    CpoWhere cw1 = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(IDB + 1);
      valObj.setAttrBit(true);
      valObj.setAttrVarChar("Test");
      Collection<Integer> inColl = new ArrayList<>();
      inColl.add(IDB + 1);
      inColl.add(IDB + 3);
      inColl.add(IDB + 5);

      cw = cpoAdapter.startAnd(ValueObject.ATTR_ID, Comparison.IN, inColl).build();

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(
              CpoQuery.group(ValueObject.FG_LIST_INTERLEAVEDWHERE).wheres(wheres), valObj); ) {
        long count = beans.count();
        assertEquals(count, 3, "Number of beans is " + count);
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
