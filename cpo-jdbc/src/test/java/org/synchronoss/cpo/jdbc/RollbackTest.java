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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RollbackTest {

  private CpoAdapter cpoAdapter = null;

  public RollbackTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   */
  @Before
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    try {
      cpoAdapter.insertObject(vo);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }

  }

  /**
   * DOCUMENT ME!
   */
  @After
  public void tearDown() {
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    try {
      cpoAdapter.deleteObject(vo);
    } catch (Exception e) {
    }
    cpoAdapter = null;
  }

  /**
   * DOCUMENT ME!
   */
  @Test
  public void testRollbackProcessUpdateCollection() {
    String method = "testRollbackProcessUpdateCollection:";
    ValueObject vo = ValueObjectFactory.createValueObject(2);
    ValueObject vo2 = ValueObjectFactory.createValueObject(1);
    ArrayList<ValueObject> al = new ArrayList<>();

    al.add(vo);
    al.add(vo2);

    try {
      cpoAdapter.insertObjects(ValueObject.FG_CREATE_TESTROLLBACK, al);
      fail(method + "Insert should have thrown an exception");
    } catch (Exception e) {
      try {
        ValueObject rvo = cpoAdapter.retrieveBean(vo);
        assertNull(method + "Value Object did not rollback", rvo);
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }

  /**
   * DOCUMENT ME!
   */
  @Test
  public void testSingleRollback() {
    String method = "testSingleRollback:";
    ValueObject vo = ValueObjectFactory.createValueObject(2);
    try {
      cpoAdapter.insertObject(ValueObject.FG_CREATE_TESTSINGLEROLLBACK, vo);
      fail(method + "Insert should have thrown an exception");
    } catch (Exception e) {
      try {
        ValueObject rvo = cpoAdapter.retrieveBean(vo);
        assertNull(method + "Value Object did not rollback", rvo);
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }
}
