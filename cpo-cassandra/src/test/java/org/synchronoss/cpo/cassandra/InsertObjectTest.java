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
package org.synchronoss.cpo.cassandra;

import org.synchronoss.cpo.*;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;

import java.sql.Timestamp;
import java.util.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * InsertObjectTest is a JUnit test class for testing the insert api calls of cpo
 *
 * @author david berry
 */
public class InsertObjectTest extends CassandraContainerBase {

  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CpoAdapter readAdapter = null;
  private CassandraCpoMetaDescriptor metaDescriptor = null;

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeEach
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      metaDescriptor = (CassandraCpoMetaDescriptor)cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      readAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(readAdapter,method + "readAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInsertObject() {
    String method = "testInsertObject:";
    ValueObject valObj = ValueObjectFactory.createValueObject(5);

    valObj.setAttrVarChar("testInsert");
    valObj.setAttrInt(3);
    Date ts = new Timestamp(System.currentTimeMillis());

//    if (!metaDescriptor.isSupportsMillis()) {
//      ts.setNanos(0);
//    }

    valObj.setAttrTimestamp(ts);
    valObj.setAttrBool(true);
    al.add(valObj);

    try {
      cpoAdapter.insertObject(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject vo = readAdapter.retrieveBean(null, valObj, valObj, null, null);
      assertEquals(vo.getId(), valObj.getId(), "Ids do not match");
      assertEquals(vo.getAttrInt(), valObj.getAttrInt(), "Integers do not match");
      assertEquals(vo.getAttrVarChar(), valObj.getAttrVarChar(), "Strings do not match");
      assertEquals(vo.getAttrTimestamp(), valObj.getAttrTimestamp(), "Timestamps do not match");
      assertTrue(vo.getAttrBool(), "boolean not stored correctly");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testInsertObjects() {

    String method = "testInsertObjects:";
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");

    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    try {
      cpoAdapter.insertObjects(al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      Collection<ValueObject> col = readAdapter.retrieveBeans(null, vo);

      assertEquals(col.size(), al.size(), method + "Invalid number of objects returned");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterEach
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