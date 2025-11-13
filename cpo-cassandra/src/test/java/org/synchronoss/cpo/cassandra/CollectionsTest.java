/*
 * Copyright (C) 2003-2025 David E. Berry
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

import static org.testng.Assert.*;

import java.util.*;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * CollectionsTest is a test class for testing the List, Set, and Map attributes
 *
 * @author david berry
 */
public class CollectionsTest {
  private CpoAdapter cpoAdapter = null;
  private CpoAdapter readAdapter = null;
  private CassandraCpoMetaDescriptor metaDescriptor = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "IdoAdapter is null");
      metaDescriptor = (CassandraCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      readAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(readAdapter, method + "IdoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testList() {
    String method = "testList";
    String testString = "Test String!!!";
    ValueObject valObj = ValueObjectFactory.createValueObject(0);
    List<String> testList = new ArrayList<String>();
    testList.add(testString);

    valObj.setAttrList(testList);
    al.add(valObj);

    try {
      cpoAdapter.insertObject(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject vo = readAdapter.retrieveBean(null, valObj, valObj, null, null);
      assertEquals(testString, vo.getAttrList().get(0), "Strings do not match");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testSet() {
    String method = "testList";
    String testString = "One,Two,Three";
    ValueObject valObj = ValueObjectFactory.createValueObject(1);
    Set<String> testSet = new TreeSet<String>();
    testSet.add(testString);

    valObj.setAttrSet(testSet);
    al.add(valObj);

    try {
      cpoAdapter.insertObject(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject vo = readAdapter.retrieveBean(null, valObj, valObj, null, null);
      assertTrue(vo.getAttrSet().contains(testString), "Set does not contain the teststring");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testMap() {
    String method = "testList";
    String testKey = "CT";
    String testValue = "Hartford";
    ValueObject valObj = ValueObjectFactory.createValueObject(2);
    Map<String, String> testMap = new HashMap<>();
    testMap.put(testKey, testValue);

    valObj.setAttrMap(testMap);
    al.add(valObj);

    try {
      cpoAdapter.insertObject(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject vo = readAdapter.retrieveBean(null, valObj, valObj, null, null);
      assertEquals(testValue, vo.getAttrMap().get(testKey), "Strings do not match");
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
