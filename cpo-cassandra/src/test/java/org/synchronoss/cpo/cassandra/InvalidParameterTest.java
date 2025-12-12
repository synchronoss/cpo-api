package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.fail;

import java.util.Collection;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class InvalidParameterTest {

  private static final Logger logger = LoggerFactory.getLogger(InvalidParameterTest.class);
  private CpoAdapter cpoAdapter = null;

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
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
  }

  @Test
  public void testRetrieveBeanBadContext() {
    String method = "testRetrieveBeanBadContext:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      try (Stream<ValueObject> beans = cpoAdapter.retrieveBeans("BadContext", valObj); ) {
        long count = beans.count();
        fail(method + "Test got to unreachable code " + count);
      }
    } catch (CpoException ce) {
      // This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
    } catch (Exception e) {
      fail(method + "Unexpected Exception" + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @Test
  public void testRetrieveBeansNullBean() {
    String method = "testRetrieveBeansNullBean:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = null;
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj); ) {
        long count = beans.count();
        fail(method + "Test got to unreachable code " + count);
      }
    } catch (CpoException ce) {
      // This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
    } catch (Exception e) {
      fail(method + "Unexpected Exception" + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @Test
  public void testRetrieveBeanNullBean() {
    String method = "testRetrieveBeanNullBean:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = cpoAdapter.retrieveBean(null, null);
      fail(method + "Test got to unreachable code");
    } catch (CpoException ce) {
      // This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
    } catch (Exception e) {
      fail(method + "Unexpected Exception" + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @Test
  public void testInsertObjectNullBean() {
    String method = "testInsertObjectNullBean:";
    Collection<ValueObject> col = null;

    try {
      ValueObject valObj = null;
      cpoAdapter.insertBean(null, valObj);
      fail(method + "Test got to unreachable code");
    } catch (CpoException ce) {
      // This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
    } catch (Exception e) {
      fail(method + "Unexpected Exception" + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @Test
  public void testRetrieveBeanNullContext() {
    String method = "testRetrieveBeanNullContext:";
    Collection<ValueObject> lvos = null;

    try {
      ValueObject lvo = ValueObjectFactory.createValueObject();
      logger.debug("Calling the NULL List");
      try (Stream<ValueObject> beans = cpoAdapter.retrieveBeans("NULL", lvo); ) {
        logger.debug("Called the NULL List");
        long count = beans.count();
        fail(method + "Test got to unreachable code " + count);
      }
    } catch (CpoException ce) {
      // This is what I am expecting so let it go
      // This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
    } catch (Exception e) {
      fail(method + "Unexpected Exception" + ExceptionHelper.getLocalizedMessage(e));
    }
  }
}
