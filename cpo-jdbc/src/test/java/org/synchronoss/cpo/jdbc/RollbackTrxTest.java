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

import java.util.ArrayList;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RollbackTrxTest {

  private CpoTrxAdapter trxAdapter = null;

  /** Creates a new RollbackTest object. */
  public RollbackTrxTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      trxAdapter = CpoAdapterFactoryManager.getCpoTrxAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(trxAdapter, method + "trxAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    try {
      trxAdapter.insertBean(vo);
      trxAdapter.commit();
    } catch (Exception e) {
      try {
        trxAdapter.rollback();
      } catch (Exception e1) {
      }
      fail(method + e.getMessage());
    }
  }

  /** DOCUMENT ME! */
  @AfterClass
  public void tearDown() {
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    try {
      trxAdapter.deleteBean(vo);
      trxAdapter.commit();
    } catch (Exception e) {
      try {
        trxAdapter.rollback();
      } catch (Exception e1) {
      }
    } finally {
      try {
        trxAdapter.close();
      } catch (Exception e1) {
      }
      trxAdapter = null;
    }
  }

  /** DOCUMENT ME! */
  @Test
  public void testTrxRollbackProcessUpdateCollection() {
    String method = "testTrxRollbackProcessUpdateCollection:";
    ValueObject vo = ValueObjectFactory.createValueObject(2);
    ValueObject vo2 = ValueObjectFactory.createValueObject(1);
    ArrayList<ValueObject> al = new ArrayList<>();

    al.add(vo);
    al.add(vo2);

    try {
      trxAdapter.insertBeans(ValueObject.FG_CREATE_TESTROLLBACK, al);
      trxAdapter.commit();
      fail(method + "Insert should have thrown an exception");
    } catch (Exception e) {
      try {
        trxAdapter.rollback();
      } catch (CpoException ce) {
        fail(method + "Rollback failed:" + ExceptionHelper.getLocalizedMessage(ce));
      }
      try {
        ValueObject rvo = trxAdapter.retrieveBean(vo);
        assertNull(rvo, method + "Value Object did not rollback");
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }

  /** DOCUMENT ME! */
  @Test
  public void testTrxSingleRollback() {
    String method = "testTrxSingleRollback:";
    ValueObject vo = ValueObjectFactory.createValueObject(2);
    try {
      trxAdapter.insertBean(ValueObject.FG_CREATE_TESTSINGLEROLLBACK, vo);
      trxAdapter.commit();
      fail(method + "Insert should have thrown an exception");
    } catch (Exception e) {
      try {
        trxAdapter.rollback();
      } catch (CpoException ce) {
        fail(method + "Rollback failed:" + ExceptionHelper.getLocalizedMessage(ce));
      }
      try {
        ValueObject rvo = trxAdapter.retrieveBean(vo);
        assertNull(rvo, method + "Value Object did not rollback");
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }
}
