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
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;

import java.sql.Timestamp;
import java.util.*;

/**
 * InsertObjectTest is a JUnit test class for testing the insert api calls of cpo
 *
 * @author david berry
 */
public class CaseSensitiveTest extends TestCase {

  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CpoAdapter readAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;

  public CaseSensitiveTest(String name) {
    super(name);
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
   */
  @Override
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CASESENSITIVE);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      readAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CASESENSITIVE);
      assertNotNull(method + "IdoAdapter is null", readAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testCaseSensitiveObject() {
    String method = "testCaseSensitiveObject:";
    ValueObject valObj = new ValueObjectBean(5);

    valObj.setAttrVarChar("testCaseSensitiveObject");
    valObj.setAttrInteger(3);
    Timestamp ts = new Timestamp(System.currentTimeMillis());

    if (!metaDescriptor.isSupportsMillis()) {
      ts.setNanos(0);
    }

    valObj.setAttrDatetime(ts);

    valObj.setAttrBit(true);

    al.add(valObj);

    try {
      cpoAdapter.insertObject(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject vo = readAdapter.retrieveBean(null, valObj, valObj, null, null);
      assertFalse("Ids should not match", vo.getId() == valObj.getId());
      assertFalse("Integers should not match", vo.getAttrInteger() == valObj.getAttrInteger());
      assertFalse("Strings should not match", valObj.getAttrVarChar().equals(vo.getAttrVarChar()));
      assertFalse("Timestamps should not match", valObj.getAttrDatetime().equals(vo.getAttrDatetime()));
      assertFalse("boolean not stored correctly", vo.getAttrBit());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }

  }

  @Override
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