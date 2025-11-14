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

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * ExistObjectTest is a test class for the exists api calls
 *
 * @author david berry
 */
public class ExistObjectTest {

  private static final Logger logger = LoggerFactory.getLogger(ExistObjectTest.class);
  private CpoAdapter cpoAdapter = null;

  public ExistObjectTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: ExistObjectTest.java,v 1.2 2006/01/30 19:09:23 dberry Exp $'
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
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("WHERE");

    try {
      long count = cpoAdapter.insertBean(vo);
      assertEquals(1, count, "Should be inserted");
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testExistObject() {
    String method = "testExistObject:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      long count = cpoAdapter.existsBean(valObj);
      assertEquals(1, count, "Object not Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(5);
      long count = cpoAdapter.existsBean(valObj);
      assertEquals(0, count, "Object Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testExistObjectWhere() {
    String method = "testExistObjectWhere:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      CpoWhere where =
          cpoAdapter.newWhere(Logical.AND, ValueObject.ATTR_ATTRVARCHAR, Comparison.EQ, "WHERE");
      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(where);
      long count = cpoAdapter.existsBean(ValueObject.FG_EXIST_NULL, valObj, wheres);
      assertEquals(1, count, "Object not Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      CpoWhere where =
          cpoAdapter.newWhere(Logical.AND, ValueObject.ATTR_ATTRVARCHAR, Comparison.EQ, "NOWHERE");
      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(where);
      long count = cpoAdapter.existsBean(ValueObject.FG_EXIST_NULL, valObj, wheres);
      assertEquals(0, count, "Object Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterClass
  public void tearDown() {
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    try {
      long count = cpoAdapter.deleteBean(vo);
      assertEquals(1, count, "Should be deleted");
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
    }
    cpoAdapter = null;
  }
}
