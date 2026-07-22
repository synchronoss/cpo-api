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

import com.datastax.oss.driver.api.core.auth.ProgrammaticPlainTextAuthProvider;
import java.util.List;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Unit tests for ClusterDataSourceInfo accessors and cluster building. The data-source creation
 * tests connect through the suite's TCP proxy to the Cassandra test container.
 */
public class ClusterDataSourceInfoTest {

  private static final String LOCAL_DATACENTER = "datacenter1";

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
    info.setLocalDatacenter(LOCAL_DATACENTER);
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

    info.setLocalDatacenter(LOCAL_DATACENTER);
    assertEquals(info.getLocalDatacenter(), LOCAL_DATACENTER);

    info.setMaxSchemaAgreementWaitSeconds(30);
    assertEquals(info.getMaxSchemaAgreementWaitSeconds(), Integer.valueOf(30));

    info.setAddressTranslatorClassName(
        "com.datastax.oss.driver.internal.core.addresstranslation.PassThroughAddressTranslator");
    assertEquals(
        info.getAddressTranslatorClassName(),
        "com.datastax.oss.driver.internal.core.addresstranslation.PassThroughAddressTranslator");

    info.setPort(9042);
    assertEquals(info.getPort(), 9042);

    info.setProtocolVersion("V4");
    assertEquals(info.getProtocolVersion(), "V4");

    info.setLoadBalancingPolicyClassName(
        "com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy");
    assertEquals(
        info.getLoadBalancingPolicyClassName(),
        "com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy");

    info.setReconnectionPolicyClassName(
        "com.datastax.oss.driver.internal.core.connection.ConstantReconnectionPolicy");
    assertEquals(
        info.getReconnectionPolicyClassName(),
        "com.datastax.oss.driver.internal.core.connection.ConstantReconnectionPolicy");

    info.setRetryPolicyClassName("com.datastax.oss.driver.internal.core.retry.DefaultRetryPolicy");
    assertEquals(
        info.getRetryPolicyClassName(),
        "com.datastax.oss.driver.internal.core.retry.DefaultRetryPolicy");

    info.setHasCredentials(true);
    assertTrue(info.hasCredentials());
    info.setUserName("user");
    assertEquals(info.getUserName(), "user");
    info.setPassword("pass");
    assertEquals(info.getPassword(), "pass");

    ProgrammaticPlainTextAuthProvider authProvider =
        new ProgrammaticPlainTextAuthProvider("user", "pass");
    info.setAuthProvider(authProvider);
    assertSame(info.getAuthProvider(), authProvider);

    info.setCompressionType("none");
    assertEquals(info.getCompressionType(), "none");

    info.setUseMetrics(Boolean.FALSE);
    assertEquals(info.getUseMetrics(), Boolean.FALSE);

    info.setSslEngineFactory(null);
    assertNull(info.getSslEngineFactory());

    info.setListeners(List.of());
    assertTrue(info.getListeners().isEmpty());

    info.setUseJmxReporting(Boolean.FALSE);
    assertEquals(info.getUseJmxReporting(), Boolean.FALSE);

    info.setConnectionPoolLocalSize(2);
    assertEquals(info.getConnectionPoolLocalSize(), Integer.valueOf(2));

    info.setConnectionPoolRemoteSize(1);
    assertEquals(info.getConnectionPoolRemoteSize(), Integer.valueOf(1));

    info.setHeartbeatIntervalSeconds(30);
    assertEquals(info.getHeartbeatIntervalSeconds(), Integer.valueOf(30));

    info.setConnectTimeoutMillis(5000);
    assertEquals(info.getConnectTimeoutMillis(), Integer.valueOf(5000));

    info.setTcpNoDelay(Boolean.TRUE);
    assertEquals(info.getTcpNoDelay(), Boolean.TRUE);

    info.setConsistencyLevel("LOCAL_ONE");
    assertEquals(info.getConsistencyLevel(), "LOCAL_ONE");

    info.setSerialConsistencyLevel("LOCAL_SERIAL");
    assertEquals(info.getSerialConsistencyLevel(), "LOCAL_SERIAL");

    info.setDefaultIdempotence(Boolean.TRUE);
    assertEquals(info.getDefaultIdempotence(), Boolean.TRUE);

    info.setPageSize(500);
    assertEquals(info.getPageSize(), Integer.valueOf(500));

    info.setSpeculativeExecutionPolicyClassName(
        "com.datastax.oss.driver.internal.core.specex.NoSpeculativeExecutionPolicy");
    assertEquals(
        info.getSpeculativeExecutionPolicyClassName(),
        "com.datastax.oss.driver.internal.core.specex.NoSpeculativeExecutionPolicy");

    info.setTimestampGeneratorClassName(
        "com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator");
    assertEquals(
        info.getTimestampGeneratorClassName(),
        "com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator");
  }

  @Test
  public void testCreateDataSourceWithDefaults() throws Exception {
    ClusterDataSourceInfo info = newLiveInfo();
    ClusterDataSource ds = info.getDataSource();
    assertNotNull(ds);
    assertEquals(ds.getKeySpace(), keySpace);
    assertNotNull(ds.getSession());
    assertSame(info.getDataSource(), ds, "data source is cached");
    ds.getSession().close();
  }

  @Test
  public void testCreateDataSourceWithAllOptions() throws Exception {
    ClusterDataSourceInfo info = newLiveInfo();
    info.setMaxSchemaAgreementWaitSeconds(20);
    info.setAddressTranslatorClassName(
        "com.datastax.oss.driver.internal.core.addresstranslation.PassThroughAddressTranslator");
    info.setLoadBalancingPolicyClassName(
        "com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy");
    info.setReconnectionPolicyClassName(
        "com.datastax.oss.driver.internal.core.connection.ConstantReconnectionPolicy");
    info.setRetryPolicyClassName("com.datastax.oss.driver.internal.core.retry.DefaultRetryPolicy");
    info.setHasCredentials(true);
    info.setUserName("user");
    info.setPassword("pass");
    info.setCompressionType("none");
    info.setUseMetrics(Boolean.FALSE);
    info.setUseJmxReporting(Boolean.FALSE);
    info.setConnectionPoolLocalSize(1);
    info.setConnectTimeoutMillis(5000);
    info.setTcpNoDelay(Boolean.TRUE);
    info.setConsistencyLevel("LOCAL_ONE");
    info.setDefaultIdempotence(Boolean.TRUE);
    info.setPageSize(500);
    info.setSpeculativeExecutionPolicyClassName(
        "com.datastax.oss.driver.internal.core.specex.NoSpeculativeExecutionPolicy");
    info.setTimestampGeneratorClassName(
        "com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator");

    ClusterDataSource ds = info.getDataSource();
    assertNotNull(ds);
    assertEquals(ds.getKeySpace(), keySpace);
    ds.getSession().close();
  }
}
