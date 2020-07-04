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
import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;

import java.util.ArrayList;

/**
 * BigBatchTest is a JUnit test class for testing big batches
 *
 * @author david berry
 */
public class BigBatchTest {

  private static final Logger logger = LoggerFactory.getLogger(BigBatchTest.class);
  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;

  public BigBatchTest() {

  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
   */
  @Before
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /**
   * So oracle seems to fail on a batch size of 100,000 but does not throw an error.
   *
   * lets try to break it to fix it to return a good message.
   *
   */
  @Test
  public void testTooManyInserts() {

    String method = "testTooManyInserts:";
    int numInserts = 100000;

    for (int i = 0; i < numInserts; i++) {
      al.add(ValueObjectFactory.createValueObject(i));
    }

    try {
      long inserts = cpoAdapter.insertObjects(al);
      assertEquals("inserts performed do not equal inserts requested: ", inserts, numInserts);
    } catch (CpoException ce) {
      logger.debug("Received a CpoException:" + ExceptionHelper.getLocalizedMessage(ce));
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + ":Received an Exception instead of a CpoException: " + ExceptionHelper.getLocalizedMessage(e));
    } catch (Throwable t) {
      logger.error(ExceptionHelper.getLocalizedMessage(t));
      fail(method + ":Received a Throwable instead of a CpoException: " + ExceptionHelper.getLocalizedMessage(t));
    }

  }

  @After
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteObjects(al);

    } catch (Exception e) {
            logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}