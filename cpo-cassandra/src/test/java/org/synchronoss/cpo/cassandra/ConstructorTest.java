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
package org.synchronoss.cpo.cassandra;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactory;

import java.util.List;

/**
 * ConstructorTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ConstructorTest extends TestCase {
  private static final Logger logger = LoggerFactory.getLogger(ConstructorTest.class);

  public ConstructorTest(String name) {
    super(name);
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: ConstructorTest.java,v 1.7 2006/01/31 22:55:03 dberry Exp $'
   */
  @Override
  public void setUp() {
  }

  public void testConstructorClass() {
    String method = "testConstructorClass:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);
      assertNotNull("DataSourceName is null", cpoAdapter.getDataSourceName());

      logger.debug("=====> DatasourceName: "+cpoAdapter.getDataSourceName());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Override
  public void tearDown() {
  }
}