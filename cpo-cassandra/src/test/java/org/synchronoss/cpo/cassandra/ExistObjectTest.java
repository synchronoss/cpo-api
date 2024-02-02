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

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.testng.annotations.Test;
import static org.testng.Assert.*;

import java.util.ArrayList;

/**
 * ExistObjectTest is a test class for the exists api calls
 *
 * @author david berry
 */
public class ExistObjectTest extends CassandraContainerBase {

  private static final Logger logger = LoggerFactory.getLogger(ExistObjectTest.class);
  private CpoAdapter cpoAdapter = null;

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: ExistObjectTest.java,v 1.2 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "IdoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrInt(3);

    try {
      cpoAdapter.insertObject(vo);
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
      long count = cpoAdapter.existsObject(valObj);
      assertTrue(count == 1, "Object not Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(5);
      long count = cpoAdapter.existsObject(valObj);
      assertTrue(count == 0, "Object Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testExistObjectWhere() {
    String method = "testExistObject:";

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      CpoWhere where = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, "attrInt", CpoWhere.COMP_EQ, 3);
      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(where);
      long count = cpoAdapter.existsObject(null, valObj, wheres);
      assertTrue(count == 1, "Object not Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      CpoWhere where = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, "attrInt", CpoWhere.COMP_EQ, 5);
      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(where);
      long count = cpoAdapter.existsObject(null, valObj, wheres);
      assertTrue(count == 0, "Object Found");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterClass
  public void tearDown() {
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    try {
      cpoAdapter.deleteObject(vo);
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
    }
    cpoAdapter = null;
  }
}