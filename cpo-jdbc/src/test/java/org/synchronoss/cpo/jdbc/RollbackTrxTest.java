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

import java.util.ArrayList;
import junit.framework.TestCase;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RollbackTrxTest extends TestCase {

  private CpoAdapter cpoAdapter = null;
  private CpoTrxAdapter trxAdapter = null;

  /**
   * Creates a new RollbackTest object.
   *
   * @param name DOCUMENT ME!
   */
  public RollbackTrxTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   */
  @Override
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      trxAdapter = cpoAdapter.getCpoTrxAdapter();
      assertNotNull(method + "CpoAdapter is null", trxAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = new ValueObject(1);
    vo.setAttrVarChar("Test");
    try {
      trxAdapter.insertObject(vo);
      trxAdapter.commit();
    } catch (Exception e) {
      try {
        trxAdapter.rollback();
      } catch (Exception e1) {
      }
      fail(method + e.getMessage());
    }
  }

  /**
   * DOCUMENT ME!
   */
  @Override
  public void tearDown() {
    ValueObject vo = new ValueObject(1);
    try {
      trxAdapter.deleteObject(vo);
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

  /**
   * DOCUMENT ME!
   */
  public void testTrxRollbackProcessUpdateCollection() {
    String method = "testTrxRollbackProcessUpdateCollection:";
    ValueObject vo = new ValueObject(2);
    ValueObject vo2 = new ValueObject(1);
    ArrayList<ValueObject> al = new ArrayList<>();

    al.add(vo);
    al.add(vo2);

    try {
      trxAdapter.insertObjects("TestRollback", al);
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
        assertNull(method + "Value Object did not rollback", rvo);
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }

  /**
   * DOCUMENT ME!
   */
  public void testTrxSingleRollback() {
    String method = "testTrxSingleRollback:";
    ValueObject vo = new ValueObject(2);
    try {
      trxAdapter.insertObject("TestSingleRollback", vo);
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
        assertNull(method + "Value Object did not rollback", rvo);
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }
}
