package org.synchronoss.cpo.jdbc;

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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * DeleteObjectTest is a test class for testing the JdbcAdapter deleteObject method
 *
 * @author david berry
 */
public class DeleteObjectTest {

  private static final Logger logger = LoggerFactory.getLogger(DeleteObjectTest.class);
  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private boolean isSupportsMillis = true;

  public DeleteObjectTest() {}

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
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testDeleteObject() {
    String method = "testDeleteObject:";
    ValueObject valObj = ValueObjectFactory.createValueObject(5);

    valObj.setAttrVarChar("testDelete");
    valObj.setAttrInteger(3);
    Timestamp ts = new Timestamp(System.currentTimeMillis());

    if (!isSupportsMillis) {
      ts.setNanos(0);
    }

    valObj.setAttrDatetime(ts);

    valObj.setAttrBit(true);

    al.add(valObj);

    try {
      cpoAdapter.insertBean(valObj);
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + e.getMessage());
    }

    // try the where on the delete, should delete 0
    try {
      List<CpoWhere> cws = new ArrayList<>();
      cws.add(cpoAdapter.newWhere(Logical.AND, ValueObject.ATTR_ID, Comparison.EQ, 2));
      long deleted = cpoAdapter.deleteBean(ValueObject.FG_DELETE_NULL, valObj, cws, null, null);
      assertEquals(0, deleted, "Should not have deleted anything");
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + e.getMessage());
    }

    // try the where on the delete, should delete 1
    try {
      List<CpoWhere> cws = new ArrayList<>();
      cws.add(cpoAdapter.newWhere(Logical.OR, ValueObject.ATTR_ID, Comparison.EQ, 2));
      long deleted = cpoAdapter.deleteBean(ValueObject.FG_DELETE_NULL, valObj, cws, null, null);
      assertEquals(1, deleted, "Should have deleted 1");
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + e.getMessage());
    }
  }

  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteBeans(al);

    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}
