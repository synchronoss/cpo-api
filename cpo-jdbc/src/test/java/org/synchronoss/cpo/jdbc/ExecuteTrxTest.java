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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ExecuteTrxTest {

  private static final Logger logger = LoggerFactory.getLogger(ExecuteTrxTest.class);
  private CpoAdapter cpoAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private boolean isSupportsCalls = Boolean.valueOf(JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_CALLS_SUPPORTED));

  /**
   * Creates a new RollbackTest object.
   *
   */
  public ExecuteTrxTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   */
  @Before
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "CpoAdapter is null", cpoAdapter);
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /**
   * DOCUMENT ME!
   */
  @After
  public void tearDown() {
    cpoAdapter = null;
  }

  /**
   * DOCUMENT ME!
   */
  @Test
  public void testExecuteTrx() {
    if (isSupportsCalls) {
      String method = "testExecuteTrx:";
      ValueObject vo = ValueObjectFactory.createValueObject(1);
      vo.setAttrInteger(3);
      ValueObject rvo;

      try {
        rvo = cpoAdapter.executeObject("TestExecuteObject", vo);
        assertNotNull(method + "Returned Value object is null");
        assertTrue("power(3,3)=" + rvo.getAttrDouble(), rvo.getAttrDouble() == 27);
      } catch (Exception e) {
        logger.error(ExceptionHelper.getLocalizedMessage(e));
        fail(method + e.getMessage());
      }


      try {
        vo = ValueObjectFactory.createValueObject(1);
        vo.setAttrSmallInt((short)3);
        rvo = cpoAdapter.executeObject("TestExecuteObjectNoTransform", vo);
        assertNotNull(method + "Returned Value object is null");
        assertTrue("power(3,3)=" + rvo.getAttrDouble(), rvo.getAttrDouble() == 27);
      } catch (Exception e) {
        logger.error(ExceptionHelper.getLocalizedMessage(e));
        fail(method + e.getMessage());
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support CallableStatements");
    }
  }
}
