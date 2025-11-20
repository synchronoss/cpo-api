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

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.testng.Assert.*;

/**
 * DeleteObjectTest is a test class for testing the JdbcAdapter deleteObject method
 *
 * @author david berry
 */
public class UpdateObjectTest {

  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private CassandraCpoMetaDescriptor metaDescriptor = null;

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
  }

  @Test
  public void testUpdateObject() {
    String method = "testUpdateObject:";
    ValueObject valObj = ValueObjectFactory.createValueObject(5);

    valObj.setAttrVarChar("testUpdate");
    valObj.setAttrInt(3);
    Date ts = new Timestamp(System.currentTimeMillis());

    //    if (!metaDescriptor.isSupportsMillis()) {
    //      ts.setNanos(0);
    //    }

    valObj.setAttrTimestamp(ts);

    valObj.setAttrBool(true);

    al.add(valObj);

    try {
      cpoAdapter.insertBean(valObj);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    // try the where on the update, should update 0
    try {
      List<CpoWhere> cws = new ArrayList<>();
      cws.add(cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 2));
      valObj.setAttrInt(4);
      cpoAdapter.updateBean(null, valObj, cws, null, null);
      ValueObject rObj = cpoAdapter.retrieveBean(valObj);
      assertTrue(
          rObj.getAttrInt() != valObj.getAttrInt(),
          "It should not be equal since it did not update");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    // try the where on the update, should update 1
    try {
      List<CpoWhere> cws = new ArrayList<>();
      cws.add(cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 5));
      cpoAdapter.updateBean(null, valObj, cws, null, null);
      ValueObject rObj = cpoAdapter.retrieveBean(valObj);
      assertEquals(rObj.getAttrInt(), valObj.getAttrInt(), "It should be equal since it updated");
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
  }
}
