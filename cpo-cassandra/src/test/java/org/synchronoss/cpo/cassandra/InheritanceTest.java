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

import junit.framework.TestCase;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

/**
 * InheritanceTest is a JUnit test class for testing the polymorphic capabilites of CPO
 *
 * @author david berry
 */
public class InheritanceTest extends TestCase {

  private ArrayList<ChildValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CassandraCpoMetaDescriptor metaDescriptor = null;

  public InheritanceTest(String name) {
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
      cpoAdapter = CpoAdapterFactory.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
      metaDescriptor = (CassandraCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testInsertObject() {
    String method = "testInsertObject:";
    ChildValueObject valObj = new ChildValueObject();

    valObj.setId(5);
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
      ChildValueObject vo = cpoAdapter.retrieveBean(null, valObj, valObj, null, null);
      assertEquals("Ids do not match", vo.getId(), valObj.getId());
      assertEquals("Integers do not match", vo.getAttrInt(), valObj.getAttrInt());
      assertEquals("Strings do not match", vo.getAttrVarChar(), valObj.getAttrVarChar());
      assertEquals("Timestamps do not match", vo.getAttrTimestamp(), valObj.getAttrTimestamp());
      assertTrue("boolean not stored correctly", vo.getAttrBool());

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