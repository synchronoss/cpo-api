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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * BigBatchTest is a test class for testing big batches
 *
 * @author david berry
 */
public class BigBatchTest {

  private static final Logger logger = LoggerFactory.getLogger(BigBatchTest.class);
  private ArrayList<ValueObject> al = new ArrayList<>();
  private CpoAdapter cpoAdapter = null;

  public BigBatchTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
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
  }

  /**
   * So oracle seems to fail on a batch size of 100,000 but does not throw an error.
   *
   * <p>lets try to break it to fix it to return a good message.
   */
  @Test
  public void testTooManyInserts() {

    String method = "testTooManyInserts:";
    int numInserts = 100000;
    int id = numInserts;

    int oldBatchSize = cpoAdapter.getBatchSize();
    assertEquals(oldBatchSize, 100);
    cpoAdapter.setBatchSize(10000);

    for (int i = 0; i < numInserts; i++) {
      al.add(ValueObjectFactory.createValueObject(id++));
    }

    try {
      long inserts = cpoAdapter.insertBeans(al);
      assertEquals(inserts, numInserts, "inserts performed do not equal inserts requested: ");
    } catch (CpoException ce) {
      logger.debug("Received a CpoException:" + ExceptionHelper.getLocalizedMessage(ce));
    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(
          method
              + ":Received an Exception instead of a CpoException: "
              + ExceptionHelper.getLocalizedMessage(e));
    } catch (Throwable t) {
      logger.error(ExceptionHelper.getLocalizedMessage(t));
      fail(
          method
              + ":Received a Throwable instead of a CpoException: "
              + ExceptionHelper.getLocalizedMessage(t));
    } finally {
      cpoAdapter.setBatchSize(oldBatchSize);
    }
  }

  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteBeans(al);

    } catch (Exception e) {
      logger.error(ExceptionHelper.getLocalizedMessage(e));
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}
