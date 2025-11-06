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

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.util.Collection;

public class InvalidParameterTest extends JdbcDbContainerBase {

  private static final Logger logger = LoggerFactory.getLogger(InvalidParameterTest.class);
  private CpoAdapter cpoAdapter = null;

  public InvalidParameterTest() {

  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   */
  @BeforeMethod
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter,method + "cpoAdapter is null");
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
      col = cpoAdapter.retrieveBeans("BadContext", valObj);
      fail(method + "Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
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
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      fail(method + "Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
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
      ValueObject valObj = cpoAdapter.retrieveBean(ValueObject.FG_RETRIEVE_NULL, null);
      fail(method + "Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
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
      cpoAdapter.insertObject(ValueObject.FG_CREATE_NULL, valObj);
      fail(method + "Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
    } catch (Exception e) {
      fail(method + "Unexpected Exception" + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @Test
  public void testRetrieveBeanNullContext() {
    String method = "testRetrieveBeanNullContext:";
    Collection<LobValueObject> lvos = null;

    try {
      LobValueObject lvo = new LobValueObjectBean();
      logger.debug("Calling the NULL List");
      lvos = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, lvo);
      logger.debug("Called the NULL List");
      fail(method + "Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
    } catch (Exception e) {
      fail(method + "Unexpected Exception" + ExceptionHelper.getLocalizedMessage(e));
    }
  }
}
