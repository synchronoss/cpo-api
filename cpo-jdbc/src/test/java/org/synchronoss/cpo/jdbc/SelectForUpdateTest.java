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
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class SelectForUpdateTest {

  private static final Logger logger = LoggerFactory.getLogger(SelectForUpdateTest.class);
  private CpoAdapter cpoAdapter = null;
  private CpoTrxAdapter trxAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;
  private boolean isSupportsSelect4Update = Boolean.valueOf(JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_SELECT4UPDATE));

  public SelectForUpdateTest() {
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
      trxAdapter = CpoAdapterFactoryManager.getCpoTrxAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "trxAdapter is null", trxAdapter);
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      logger.debug(ExceptionHelper.getLocalizedMessage(e));
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    ValueObject vo2 = ValueObjectFactory.createValueObject(2);
    try {
      trxAdapter.insertObject(vo);
      trxAdapter.insertObject(vo2);
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
  @After
  public void tearDown() {
   String method = "tearDown:";
   ValueObject vo = ValueObjectFactory.createValueObject(1);
    ValueObject vo2 = ValueObjectFactory.createValueObject(2);
    try {
      trxAdapter.deleteObject(vo);
      trxAdapter.deleteObject(vo2);
      trxAdapter.commit();
    } catch (Exception e) {
      try {
        trxAdapter.rollback();
      } catch (Exception e1) {
      }
      fail(method + e.getMessage());
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
  @Test
  public void testSelect4UpdateSingleObject() {
    if (isSupportsSelect4Update) {
      String method = "testSelect4UpdateSingleObject:";
      ValueObject vo2 = ValueObjectFactory.createValueObject(1);

      try {
        trxAdapter.retrieveBean(ValueObject.FG_RETRIEVE_SELECTFORUPDATE, vo2);
      } catch (Exception e) {
        fail(method + "Select For Update should work:" + ExceptionHelper.getLocalizedMessage(e));
      }

      try {
        trxAdapter.retrieveBean(ValueObject.FG_RETRIEVE_SELECTFORUPDATE, vo2);
      } catch (Exception e) {
        fail(method + "Select For Update should work:" + ExceptionHelper.getLocalizedMessage(e));
      }

      try {
        cpoAdapter.retrieveBean(ValueObject.FG_RETRIEVE_SELECT4UPDATENOWAIT, vo2);
        fail(method + "SelectForUpdateNoWait should fail:");
      } catch (Exception e) {
        logger.debug(ExceptionHelper.getLocalizedMessage(e));
      }

      try {
        trxAdapter.commit();
      } catch (Exception e) {
        try {
          trxAdapter.rollback();
        } catch (CpoException ce) {
          fail(method + "Rollback failed:" + ExceptionHelper.getLocalizedMessage(e));

        }
        fail(method + "Commit should have worked.");
      }
      try {
        cpoAdapter.retrieveBean(ValueObject.FG_RETRIEVE_SELECT4UPDATENOWAIT, vo2);
      } catch (Exception e) {
        fail(method + "SelectForUpdateNoWait should success:" + ExceptionHelper.getLocalizedMessage(e));
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support Select For Update");
    }
  }

  @Test
  public void testSelect4UpdateExists() {
    if (isSupportsSelect4Update) {
      String method = "testSelect4UpdateExists:";
      ValueObject vo2 = ValueObjectFactory.createValueObject(1);

      try {
        long count = trxAdapter.existsObject(ValueObject.FG_EXIST_SELECTFORUPDATEEXISTZERO, vo2);
        assertTrue("Zero objects should have been returned", count == 0);
      } catch (Exception e) {
        fail(method + "Select For Update should work:" + ExceptionHelper.getLocalizedMessage(e));
      }

      try {
        long count = trxAdapter.existsObject(ValueObject.FG_EXIST_SELECTFORUPDATEEXISTSINGLE, vo2);
        assertTrue("One object should have been returned, got " + count, count == 1);
      } catch (Exception e) {
        fail(method + "Select For Update should work:" + ExceptionHelper.getLocalizedMessage(e));
      }

      try {
        long count = trxAdapter.existsObject(ValueObject.FG_EXIST_SELECTFORUPDATEEXISTALL, vo2);
        assertTrue("Two objects should have been returned, got " + count, count == 2);
      } catch (Exception e) {
        fail(method + "Select For Update should work:" + ExceptionHelper.getLocalizedMessage(e));
      }

      try {
        trxAdapter.commit();
      } catch (Exception e) {
        try {
          trxAdapter.rollback();
        } catch (CpoException ce) {
          fail(method + "Rollback failed:" + ExceptionHelper.getLocalizedMessage(ce));

        }
        fail(method + "Commit should have worked.");
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support Select For Update");
    }
  }
}
