package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.NettyOptions;
import com.datastax.driver.core.PoolingOptions;
import com.datastax.driver.core.ProtocolOptions;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.SocketOptions;
import com.datastax.driver.core.TimestampGenerator;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.ConstantSpeculativeExecutionPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.IdentityTranslator;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit tests for ClusterDataSourceInfo accessors and cluster building. The data-source creation
 * tests connect through the suite's TCP proxy to the Cassandra test container.
 */
public class ClusterDataSourceInfoTest {

  private String contactPoint;
  private int nativePort;
  private String keySpace;

  @Parameters({"cassandra.contactPoint", "cassandra.nativeport", "cassandra.keyspace"})
  @BeforeClass
  public void setUp(String contactPoint, int nativePort, String keySpace) {
    this.contactPoint = contactPoint;
    this.nativePort = nativePort;
    this.keySpace = keySpace;
  }

  private ClusterDataSourceInfo newInfo() {
    return new ClusterDataSourceInfo("testCluster", "testKeyspace", List.of("127.0.0.1"), 10, 20);
  }

  private ClusterDataSourceInfo newLiveInfo() {
    ClusterDataSourceInfo info =
        new ClusterDataSourceInfo("testCluster", keySpace, List.of(contactPoint), 10, 20);
    info.setPort(nativePort);
    return info;
  }

  @Test
  public void testConstructorAndName() {
    ClusterDataSourceInfo info = newInfo();
    assertEquals(info.getClusterName(), "testCluster");
    assertEquals(info.getKeySpace(), "testKeyspace");
    assertEquals(info.getDataSourceName(), "testClustertestKeyspace127.0.0.1");
    assertEquals(info.getFetchSize(), 10);
    assertEquals(info.getBatchSize(), 20);
  }

  @Test
  public void testAccessors() {
    ClusterDataSourceInfo info = newInfo();

    info.setClusterName("otherCluster");
    assertEquals(info.getClusterName(), "otherCluster");

    info.setMaxSchemaAgreementWaitSeconds(30);
    assertEquals(info.getMaxSchemaAgreementWaitSeconds(), Integer.valueOf(30));

    info.setNettyOptions(NettyOptions.DEFAULT_INSTANCE);
    assertSame(info.getNettyOptions(), NettyOptions.DEFAULT_INSTANCE);

    IdentityTranslator translator = new IdentityTranslator();
    info.setAddressTranslater(translator);
    assertSame(info.getAddressTranslator(), translator);

    info.setPort(9042);
    assertEquals(info.getPort(), 9042);

    info.setProtocolVersion(ProtocolVersion.V3);
    assertEquals(info.getProtocolVersion(), ProtocolVersion.V3);

    RoundRobinPolicy lbp = new RoundRobinPolicy();
    info.setLoadBalancingPolicy(lbp);
    assertSame(info.getLoadBalancingPolicy(), lbp);

    ConstantReconnectionPolicy rp = new ConstantReconnectionPolicy(1000);
    info.setReconnectionPolicy(rp);
    assertSame(info.getReconnectionPolicy(), rp);

    info.setRetryPolicy(DefaultRetryPolicy.INSTANCE);
    assertSame(info.getRetryPolicy(), DefaultRetryPolicy.INSTANCE);

    info.setHasCredentials(true);
    assertTrue(info.hasCredentials());
    info.setUserName("user");
    assertEquals(info.getUserName(), "user");
    info.setPassword("pass");
    assertEquals(info.getPassword(), "pass");

    info.setAuthProvider(AuthProvider.NONE);
    assertSame(info.getAuthProvider(), AuthProvider.NONE);

    info.setCompressionType(ProtocolOptions.Compression.NONE);
    assertEquals(info.getCompressionType(), ProtocolOptions.Compression.NONE);

    info.setUseMetrics(Boolean.FALSE);
    assertEquals(info.getUseMetrics(), Boolean.FALSE);

    info.setSslOptions(null);
    assertNull(info.getSslOptions());

    info.setListeners(List.of());
    assertTrue(info.getListeners().isEmpty());

    info.setUseJmxReporting(Boolean.FALSE);
    assertEquals(info.getUseJmxReporting(), Boolean.FALSE);

    PoolingOptions po = new PoolingOptions();
    info.setPoolingOptions(po);
    assertSame(info.getPoolingOptions(), po);

    SocketOptions so = new SocketOptions();
    info.setSocketOptions(so);
    assertSame(info.getSocketOptions(), so);

    QueryOptions qo = new QueryOptions();
    info.setQueryOptions(qo);
    assertSame(info.getQueryOptions(), qo);

    ConstantSpeculativeExecutionPolicy sep = new ConstantSpeculativeExecutionPolicy(100, 2);
    info.setSpeculativeExecutionPolicy(sep);
    assertSame(info.getSpeculativeExecutionPolicy(), sep);

    TimestampGenerator tg =
        com.datastax.driver.core.AtomicMonotonicTimestampGenerator.class.cast(
            new com.datastax.driver.core.AtomicMonotonicTimestampGenerator());
    info.setTimestampGenerator(tg);
    assertSame(info.getTimestampGenerator(), tg);
  }

  @Test
  public void testCreateDataSourceWithDefaults() throws Exception {
    ClusterDataSourceInfo info = newLiveInfo();
    ClusterDataSource ds = info.getDataSource();
    assertNotNull(ds);
    assertEquals(ds.getKeySpace(), keySpace);
    assertNotNull(ds.getCluster());
    assertSame(info.getDataSource(), ds, "data source is cached");
    ds.getCluster().close();
  }

  @Test
  public void testCreateDataSourceWithAllOptions() throws Exception {
    ClusterDataSourceInfo info = newLiveInfo();
    info.setMaxSchemaAgreementWaitSeconds(20);
    info.setNettyOptions(NettyOptions.DEFAULT_INSTANCE);
    info.setAddressTranslater(new IdentityTranslator());
    info.setLoadBalancingPolicy(new RoundRobinPolicy());
    info.setReconnectionPolicy(new ConstantReconnectionPolicy(1000));
    info.setRetryPolicy(DefaultRetryPolicy.INSTANCE);
    info.setHasCredentials(true);
    info.setUserName("user");
    info.setPassword("pass");
    info.setCompressionType(ProtocolOptions.Compression.NONE);
    info.setUseMetrics(Boolean.FALSE);
    info.setUseJmxReporting(Boolean.FALSE);
    info.setPoolingOptions(new PoolingOptions());
    info.setSocketOptions(new SocketOptions());
    info.setQueryOptions(new QueryOptions());
    info.setSpeculativeExecutionPolicy(new ConstantSpeculativeExecutionPolicy(100, 2));
    info.setTimestampGenerator(new com.datastax.driver.core.AtomicMonotonicTimestampGenerator());

    ClusterDataSource ds = info.getDataSource();
    assertNotNull(ds);
    assertEquals(ds.getCluster().getClusterName(), "testCluster");
    ds.getCluster().close();
  }
}
