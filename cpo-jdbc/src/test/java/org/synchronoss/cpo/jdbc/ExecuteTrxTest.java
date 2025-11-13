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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ExecuteTrxTest {

  private static final Logger logger = LoggerFactory.getLogger(ExecuteTrxTest.class);
  //  private CpoAdapter cpoAdapter = null;
  private CpoTrxAdapter trxAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private boolean isSupportsCalls = true;

  /** Creates a new RollbackTest object. */
  public ExecuteTrxTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   */
  @Parameters({"db.callsupport"})
  @BeforeClass
  public void setUp(boolean callSupport) {
    String method = "setUp:";
    isSupportsCalls = callSupport;

    try {
      //      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      trxAdapter = CpoAdapterFactoryManager.getCpoTrxAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      //      assertNotNull(method + "CpoAdapter is null", cpoAdapter);
      assertNotNull(trxAdapter, method + "trxAdapter is null");
      metaDescriptor = (JdbcCpoMetaDescriptor) trxAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /** DOCUMENT ME! */
  @AfterClass
  public void tearDown() {
    try {
      trxAdapter.close();
    } catch (Exception e) {
    }
    trxAdapter = null;
  }

  /** DOCUMENT ME! */
  @Test
  public void testExecuteTrx() {
    if (isSupportsCalls) {
      String method = "testExecuteTrx:";
      ValueObject vo = ValueObjectFactory.createValueObject(1);
      vo.setAttrInteger(3);
      ValueObject rvo;

      try {
        rvo = trxAdapter.executeObject(ValueObject.FG_EXECUTE_TESTEXECUTEOBJECT, vo);
        trxAdapter.commit();
        assertNotNull(rvo, method + "Returned Value object is null");
        assertEquals(27, rvo.getAttrDouble(), "power(3,3)=" + rvo.getAttrDouble());
      } catch (Exception e) {
        try {
          trxAdapter.rollback();
        } catch (Exception ex) {
        }
        logger.error(ExceptionHelper.getLocalizedMessage(e));
        fail(method + e.getMessage());
      }

      try {
        vo = ValueObjectFactory.createValueObject(1);
        vo.setAttrSmallInt((short) 3);
        rvo = trxAdapter.executeObject(ValueObject.FG_EXECUTE_TESTEXECUTEOBJECTNOTRANSFORM, vo);
        trxAdapter.commit();
        assertNotNull(method + "Returned Value object is null");
        assertTrue(rvo.getAttrDouble() == 27, "power(3,3)=" + rvo.getAttrDouble());
      } catch (Exception e) {
        try {
          trxAdapter.rollback();
        } catch (Exception ex) {
        }
        logger.error(ExceptionHelper.getLocalizedMessage(e));
        fail(method + e.getMessage());
      }
    } else {
      logger.error(trxAdapter.getDataSourceName() + " does not support CallableStatements");
    }
  }

  @Test
  public void testExecute2() {
    if (isSupportsCalls) {
      String method = "testExecuteObject:";
      ValueObject vo = ValueObjectFactory.createValueObject(1);
      vo.setAttrInteger(3);
      ValueObject rvo;

      try {
        rvo = trxAdapter.executeObject(ValueObject.FG_EXECUTE_TESTEXECUTEOBJECT, vo, vo);
        trxAdapter.commit();
        assertNotNull(method + "Returned Value object is null");
        assertTrue(rvo.getAttrDouble() == 27, "power(3,3)=" + rvo.getAttrDouble());
      } catch (Exception e) {
        try {
          trxAdapter.rollback();
        } catch (Exception ex) {
        }
        logger.error(ExceptionHelper.getLocalizedMessage(e));
        fail(method + e.getMessage());
      }
    }
  }
}
