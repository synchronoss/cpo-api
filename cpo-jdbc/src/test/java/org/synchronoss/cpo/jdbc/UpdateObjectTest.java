/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc;

import java.sql.Timestamp;
import java.util.*;
import junit.framework.TestCase;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaAdapter;

/**
 * DeleteObjectTest is a JUnit test class for testing the JdbcAdapter deleteObject method
 *
 * @author david berry
 */
public class UpdateObjectTest extends TestCase {

  private ArrayList<ValueObject> al = new ArrayList<ValueObject>();
  private CpoAdapter cpoAdapter = null;
  private JdbcCpoMetaAdapter metaAdapter = null;

  public UpdateObjectTest(String name) {
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
      cpoAdapter = new CpoAdapterBean(CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT));
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
      metaAdapter = (JdbcCpoMetaAdapter) cpoAdapter.getCpoMetaAdapter();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testUpdateObject() {
    String method = "testUpdateObject:";
    ValueObject valObj = new ValueObject(5);

    valObj.setAttrVarChar("testUpdate");
    valObj.setAttrInteger(3);
    Timestamp ts = new Timestamp(System.currentTimeMillis());

    if (!metaAdapter.isSupportsMillis()) {
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

    // try the where on the update, should update 0
    try {
      List<CpoWhere> cws = new ArrayList<CpoWhere>();
      cws.add(cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, new Integer(2)));
      long updated = cpoAdapter.updateObject(null, valObj, cws, null, null);
      assertEquals("Should not have updated anything", 0, updated);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    // try the where on the update, should update 1
    try {
      List<CpoWhere> cws = new ArrayList<CpoWhere>();
      cws.add(cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, new Integer(5)));
      long updated = cpoAdapter.updateObject(null, valObj, cws, null, null);
      assertEquals("Should have updated 1", 1, updated);
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
  }
}