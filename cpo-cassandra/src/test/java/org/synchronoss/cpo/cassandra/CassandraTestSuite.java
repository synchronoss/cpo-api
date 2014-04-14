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

import com.datastax.driver.core.*;
import org.cassandraunit.*;
import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.cassandraunit.utils.EmbeddedCassandraServerHelper;
import org.junit.*;
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
  BlobTest.class,
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
  XmlValidationTest.class,
  ZZHotDeployTest.class
})
public class CassandraTestSuite {

  private static final String yaml = "testCassandra.yaml";
  private static final String keyspace = "cpokeyspace";
  private static final String cqlDataSet = "test.cql";
  private static final String hostIp = "127.0.0.1";
  private static final int port = 9142;
  public static Session session;
  public static Cluster cluster;

  @BeforeClass
  public static void startCassandra() {
    System.out.println("===== start setting up =====");
    try {
      EmbeddedCassandraServerHelper.startEmbeddedCassandra(yaml);
      cluster = new Cluster.Builder().addContactPoints(hostIp).withPort(port).build();
      session = cluster.connect();
      CQLDataLoader dataLoader = new CQLDataLoader(session);
      dataLoader.load(new ClassPathCQLDataSet(cqlDataSet, keyspace));
      session = dataLoader.getSession();
    } catch (Exception ex) {
      fail("Failed to start Cassandra");
    }
    System.out.println("===== end setting up =====");
  }

  @AfterClass
  public static void stopCassandra() {
    System.out.println("===== start tearing down =====");
    if (session != null && !session.isClosed()) {
      session.close();
    }
    if (cluster != null && !cluster.isClosed()) {
      cluster.close();
    }
    System.out.println("===== end tearing down =====");
  }
}

