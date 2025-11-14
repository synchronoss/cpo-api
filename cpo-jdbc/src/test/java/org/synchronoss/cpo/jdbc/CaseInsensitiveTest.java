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
package org.synchronoss.cpo.jdbc;

import static org.testng.Assert.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * InsertObjectTest is a test class for testing the insert api calls of cpo
 *
 * @author david berry
 */
public class CaseInsensitiveTest {

  private ArrayList<CaseValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CpoAdapter readAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private boolean isSupportsMillis = true;

  public CaseInsensitiveTest() {}

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
      cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CASEINSENSITIVE);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    try {
      readAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CASEINSENSITIVE);
      assertNotNull(readAdapter, method + "readAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testCaseInsensitiveObject() {
    String method = "testCaseInsensitiveObject:";

    CaseValueObject valObj = new CaseValueObjectBean();
    valObj.setId(5);

    valObj.setAttrVarChar("testCaseInsensitiveObject");
    valObj.setAttrInteger(3);
    Timestamp ts = new Timestamp(System.currentTimeMillis());

    if (!isSupportsMillis) {
      ts.setNanos(0);
    }

    valObj.setAttrDatetime(ts);

    valObj.setAttrBool(true);

    al.add(valObj);

    try {
      cpoAdapter.insertBean(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      CaseValueObject vo =
          readAdapter.retrieveBean(CaseValueObject.FG_RETRIEVE_NULL, valObj, valObj, null, null);
      assertEquals(vo.getId(), valObj.getId(), "Ids do not match");
      assertEquals(vo.getAttrInteger(), valObj.getAttrInteger(), "Integers do not match");
      assertEquals(vo.getAttrVarChar(), valObj.getAttrVarChar(), "Strings do not match");
      assertEquals(vo.getAttrDatetime(), valObj.getAttrDatetime(), "Timestamps do not match");
      assertTrue(vo.getAttrBool(), "boolean not stored correctly");

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
