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

import com.github.terma.javaniotcpproxy.StaticTcpProxyConfig;
import com.github.terma.javaniotcpproxy.TcpProxy;
import com.github.terma.javaniotcpproxy.TcpProxyConfig;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.CassandraContainer;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import static org.junit.Assert.fail;

/**
 * Test Suite so we only have to load up cassandra once
 *
 * @author Michael Bellomo
 * @since 04/10/2014
 */
@RunWith (Suite.class)
@Suite.SuiteClasses ({
  HotDeployTest.class,
  BlobTest.class,
  CollectionsTest.class,
  ConstructorTest.class,
  DeleteObjectTest.class,
  EntityTest.class,
  ExistObjectTest.class,
  InheritanceTest.class,
  InsertObjectTest.class,
  InvalidParameterTest.class,
  NativeExpressionTest.class,
  OrderByTest.class,
  RetrieveBeanTest.class,
  UpdateObjectTest.class,
  WhereTest.class,
  XmlValidationTest.class
})
public class CassandraTestSuite {
  private static final Logger logger = LoggerFactory.getLogger(CassandraTestSuite.class);

  @ClassRule
  public static final CassandraContainer cassandraContainer = new CassandraContainer().withInitScript("initDB.cql");
  private static TcpProxy proxy;

  @BeforeClass
  public static void startCassandra() {
    logger.debug("===== start setting up =====");
    try {
      cassandraContainer.start();

      // Now map the random port to something we can use in the config file
      TcpProxyConfig config = new StaticTcpProxyConfig(9142, cassandraContainer.getHost(), cassandraContainer.getFirstMappedPort());
      config.setWorkerCount(1);

      // init proxy
      TcpProxy proxy = new TcpProxy(config);

      // start proxy
      proxy.start();

      logger.debug("Cassandra Host:"+ cassandraContainer.getHost());
    } catch (Exception ex) {
      fail("Failed to start Cassandra: "+ex.getMessage());
    }
    logger.debug("===== end setting up =====");
  }

  @AfterClass
  public static void stopCassandra() {
    logger.debug("===== start tearing down =====");
    if (proxy!=null)
      proxy.shutdown();
    cassandraContainer.stop();
    logger.debug("===== end tearing down =====");
  }
}

