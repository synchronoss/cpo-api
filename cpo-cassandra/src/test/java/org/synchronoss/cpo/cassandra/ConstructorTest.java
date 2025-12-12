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

import static org.testng.Assert.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.testng.annotations.Test;

/**
 * ConstructorTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ConstructorTest {
  private static final Logger logger = LoggerFactory.getLogger(ConstructorTest.class);

  @Test
  public void testConstructorClass() {
    String method = "testConstructorClass:";
    try {
      CpoAdapter cpoAdapter =
          CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      assertNotNull("DataSourceName is null", cpoAdapter.getDataSourceName());
      assertEquals(cpoAdapter.getBatchSize(), 100, "Default batch size should be 100");

      logger.debug("=====> DatasourceName: " + cpoAdapter.getDataSourceName());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
