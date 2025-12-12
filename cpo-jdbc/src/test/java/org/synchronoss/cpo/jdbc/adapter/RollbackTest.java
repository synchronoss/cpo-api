package org.synchronoss.cpo.jdbc.adapter;

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
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RollbackTest {

  private CpoAdapter cpoAdapter = null;

  public RollbackTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
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
    try {
      cpoAdapter.insertBean(vo);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /** DOCUMENT ME! */
  @AfterClass
  public void tearDown() {
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    try {
      cpoAdapter.deleteBean(vo);
    } catch (Exception e) {
    }
    cpoAdapter = null;
  }

  /** DOCUMENT ME! */
  @Test
  public void testRollbackProcessUpdateCollection() {
    String method = "testRollbackProcessUpdateCollection:";
    ValueObject vo = ValueObjectFactory.createValueObject(2);
    ValueObject vo2 = ValueObjectFactory.createValueObject(1);
    ArrayList<ValueObject> al = new ArrayList<>();

    al.add(vo);
    al.add(vo2);

    try {
      cpoAdapter.insertBeans(ValueObject.FG_CREATE_TESTROLLBACK, al);
      fail(method + "Insert should have thrown an exception");
    } catch (Exception e) {
      try {
        ValueObject rvo = cpoAdapter.retrieveBean(vo);
        assertNull(rvo, method + "Value Object did not rollback");
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }

  /** DOCUMENT ME! */
  @Test
  public void testSingleRollback() {
    String method = "testSingleRollback:";
    ValueObject vo = ValueObjectFactory.createValueObject(2);
    try {
      cpoAdapter.insertBean(ValueObject.FG_CREATE_TESTSINGLEROLLBACK, vo);
      fail(method + "Insert should have thrown an exception");
    } catch (Exception e) {
      try {
        ValueObject rvo = cpoAdapter.retrieveBean(vo);
        assertNull(rvo, method + "Value Object did not rollback");
      } catch (Exception e2) {
        fail(method + e.getMessage());
      }
    }
  }
}
