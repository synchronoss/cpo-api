package org.synchronoss.cpo.cassandra.config;

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

import java.math.BigInteger;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.core.CpoAdapterFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.cpoconfig.CtAddressTranslatorOptions;
import org.synchronoss.cpo.cpoconfig.CtCassandraConfig;
import org.synchronoss.cpo.cpoconfig.CtCassandraReadWriteConfig;
import org.synchronoss.cpo.cpoconfig.CtCredentials;
import org.synchronoss.cpo.cpoconfig.CtDataSourceConfig;
import org.synchronoss.cpo.cpoconfig.CtHistogramOptions;
import org.synchronoss.cpo.cpoconfig.CtLoadBalancingOptions;
import org.synchronoss.cpo.cpoconfig.CtMetadataOptions;
import org.synchronoss.cpo.cpoconfig.CtMetricsOptions;
import org.synchronoss.cpo.cpoconfig.CtNettyOptions;
import org.synchronoss.cpo.cpoconfig.CtPoolingOptions;
import org.synchronoss.cpo.cpoconfig.CtPreparedStatementOptions;
import org.synchronoss.cpo.cpoconfig.CtQueryOptions;
import org.synchronoss.cpo.cpoconfig.CtRequestOptions;
import org.synchronoss.cpo.cpoconfig.CtRequestTrackerOptions;
import org.synchronoss.cpo.cpoconfig.CtSocketOptions;
import org.synchronoss.cpo.cpoconfig.CtSubnetAddress;
import org.synchronoss.cpo.cpoconfig.CtThrottlerOptions;
import org.synchronoss.cpo.cpoconfig.CtTimestampGeneratorOptions;
import org.synchronoss.cpo.cpoconfig.StCompression;
import org.synchronoss.cpo.cpoconfig.StConsistencyLevel;
import org.synchronoss.cpo.cpoconfig.StProtocolVersion;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/** Tests CassandraCpoConfigProcessor with minimal, maximal, and split configurations. */
public class CassandraCpoConfigProcessorTest {

  private static final String LOCAL_DATACENTER = "datacenter1";

  private String contactPoint;
  private int nativePort;
  private String keySpace;
  private String metaDescriptorName;

  @Parameters({"cassandra.contactPoint", "cassandra.nativeport", "cassandra.keyspace"})
  @BeforeClass
  public void setUp(String contactPoint, int nativePort, String keySpace) throws CpoException {
    this.contactPoint = contactPoint;
    this.nativePort = nativePort;
    this.keySpace = keySpace;

    // find the cassandra meta descriptor the suite already loaded
    for (String name : CpoMetaDescriptor.getCpoMetaDescriptorNames()) {
      if (CpoMetaDescriptor.getInstance(name) instanceof CassandraCpoMetaDescriptor) {
        metaDescriptorName = name;
        break;
      }
    }
    assertNotNull(metaDescriptorName, "suite should have loaded a cassandra meta descriptor");
  }

  private CtCassandraReadWriteConfig minimalRwConfig() {
    CtCassandraReadWriteConfig rw = new CtCassandraReadWriteConfig();
    rw.setKeySpace(keySpace);
    rw.getContactPoint().add(contactPoint);
    rw.setLocalDatacenter(LOCAL_DATACENTER);
    rw.setPort(nativePort);
    // Every test in this class builds its own real CqlSession via processCpoConfig(); running
    // several concurrently (parallel="classes") pushes the driver's process-wide session count
    // past its default advisory threshold of 4 and logs a false-positive leak warning.
    rw.setSessionLeakThreshold(20);
    return rw;
  }

  private CtCassandraConfig config(String name, CtCassandraReadWriteConfig rw) {
    CtCassandraConfig cfg = new CtCassandraConfig();
    cfg.setName(name);
    cfg.setMetaDescriptorName(metaDescriptorName);
    cfg.setFetchSize(BigInteger.valueOf(10));
    cfg.setBatchSize(BigInteger.valueOf(10));
    cfg.setReadWriteConfig(rw);
    return cfg;
  }

  @Test
  public void testMinimalReadWriteConfig() throws Exception {
    CpoAdapterFactory factory =
        new CassandraCpoConfigProcessor().processCpoConfig(config("cfgMinimal", minimalRwConfig()));
    assertNotNull(factory);
    assertNotNull(factory.getCpoAdapter().getDataSourceName());
  }

  @Test
  public void testMaximalReadWriteConfig() throws Exception {
    CtCassandraReadWriteConfig rw = minimalRwConfig();
    rw.setClusterName("maximalCluster");
    rw.setApplicationName("cpo-cassandra-tests");
    rw.setApplicationVersion("1.0");
    rw.setMaxSchemaAgreementWaitSeconds(15);
    rw.setSchemaAgreementIntervalMillis(200L);
    rw.setSchemaAgreementWarnOnFailure(Boolean.TRUE);
    rw.setControlConnectionTimeoutMillis(5000L);
    rw.setRequestTimeoutMillis(5000L);

    rw.setLoadBalancingPolicy(
        "com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy");
    CtLoadBalancingOptions loadBalancingOptions = new CtLoadBalancingOptions();
    loadBalancingOptions.setSlowReplicaAvoidance(Boolean.TRUE);
    rw.setLoadBalancingOptions(loadBalancingOptions);

    rw.setReconnectionPolicy(
        "com.datastax.oss.driver.internal.core.connection.ConstantReconnectionPolicy");
    rw.setReconnectionBaseDelayMillis(1000L);
    rw.setReconnectionMaxDelayMillis(60000L);
    rw.setReconnectOnInit(Boolean.FALSE);

    rw.setRetryPolicy("com.datastax.oss.driver.internal.core.retry.DefaultRetryPolicy");

    CtCredentials credentials = new CtCredentials();
    credentials.setUser("cpoUser");
    credentials.setPassword("cpoPass");
    rw.setCredentials(credentials);

    rw.setAddressTranslater(
        "com.datastax.oss.driver.internal.core.addresstranslation.PassThroughAddressTranslator");
    CtAddressTranslatorOptions addressTranslatorOptions = new CtAddressTranslatorOptions();
    addressTranslatorOptions.setResolveAddresses(Boolean.FALSE);
    CtSubnetAddress subnetAddress = new CtSubnetAddress();
    subnetAddress.setCidr("100.64.0.0/15");
    subnetAddress.setAddress("cassandra.datacenter1.com:9042");
    addressTranslatorOptions.getSubnetAddress().add(subnetAddress);
    rw.setAddressTranslatorOptions(addressTranslatorOptions);

    rw.setAuthProvider(ConfigFactoryTest.TestAuthProviderFactory.class.getName());
    rw.setCompression(StCompression.NONE);
    rw.setProtocolVersion(StProtocolVersion.V_3);
    rw.setProtocolMaxFrameLengthBytes(268435456L);

    // sslEngineOptions is only wired up here (not asserted against a live handshake): the test
    // container doesn't have SSL enabled, so a real ssl-engine-factory-class would be set only
    // when sslOptions is also configured (see testMaximalReadWriteConfig doesn't enable SSL)

    rw.setInitialListeners(ConfigFactoryTest.TestListenerFactory.class.getName());
    rw.setSchemaChangeListeners(ConfigFactoryTest.TestSchemaChangeListenerFactory.class.getName());

    CtMetricsOptions metricsOptions = new CtMetricsOptions();
    metricsOptions.getSessionEnabled().add("cql-requests");
    metricsOptions.getNodeEnabled().add("pool.open-connections");
    metricsOptions.setGenerateAggregableHistograms(Boolean.TRUE);
    CtHistogramOptions cqlRequestsHistogram = new CtHistogramOptions();
    cqlRequestsHistogram.setHighestLatencyMillis(3000L);
    cqlRequestsHistogram.setLowestLatencyMillis(1L);
    cqlRequestsHistogram.setSignificantDigits(3);
    cqlRequestsHistogram.setRefreshIntervalMinutes(5L);
    metricsOptions.setSessionCqlRequests(cqlRequestsHistogram);
    rw.setMetricsOptions(metricsOptions);

    CtPoolingOptions pooling = new CtPoolingOptions();
    pooling.setConnectionPoolLocalSize(1);
    pooling.setConnectionPoolRemoteSize(1);
    pooling.setHeartbeatIntervalSeconds(30);
    pooling.setHeartbeatTimeoutSeconds(5);
    pooling.setConnectInitQueryTimeoutMillis(5000L);
    pooling.setSetKeyspaceTimeoutMillis(5000L);
    pooling.setMaxRequestsPerConnection(1024);
    pooling.setMaxOrphanRequests(256);
    pooling.setWarnOnInitError(Boolean.TRUE);
    rw.setPoolingOptions(pooling);

    CtQueryOptions query = new CtQueryOptions();
    query.setConsistencyLevel(StConsistencyLevel.ONE);
    query.setSerialConsistencyLevel(StConsistencyLevel.LOCAL_SERIAL);
    query.setDefaultIdempotence(Boolean.FALSE);
    query.setFetchSize(100);
    rw.setQueryOptions(query);

    CtSocketOptions socket = new CtSocketOptions();
    socket.setConnectionTimeoutMillis(5000);
    socket.setKeepAlive(Boolean.TRUE);
    socket.setReceiveBufferSize(65536);
    socket.setReuseAddress(Boolean.TRUE);
    socket.setSendBufferSize(65536);
    socket.setTcpNoDelay(Boolean.TRUE);
    rw.setSocketOptions(socket);

    rw.setSpeculativeExecutionPolicy(
        "com.datastax.oss.driver.internal.core.specex.NoSpeculativeExecutionPolicy");
    rw.setSpeculativeExecutionMaxExecutions(3);
    rw.setSpeculativeExecutionDelayMillis(100L);

    rw.setTimestampGenerator("com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator");
    CtTimestampGeneratorOptions timestampGeneratorOptions = new CtTimestampGeneratorOptions();
    timestampGeneratorOptions.setForceJavaClock(Boolean.TRUE);
    rw.setTimestampGeneratorOptions(timestampGeneratorOptions);

    CtRequestOptions requestOptions = new CtRequestOptions();
    requestOptions.setWarnIfSetKeyspace(Boolean.TRUE);
    requestOptions.setLogWarnings(Boolean.TRUE);
    requestOptions.setTraceAttempts(5);
    requestOptions.setTraceIntervalMillis(3L);
    requestOptions.setTraceConsistencyLevel(StConsistencyLevel.ONE);
    rw.setRequestOptions(requestOptions);

    CtRequestTrackerOptions requestTrackerOptions = new CtRequestTrackerOptions();
    requestTrackerOptions
        .getRequestTrackerClass()
        .add("com.datastax.oss.driver.internal.core.tracker.RequestLogger");
    requestTrackerOptions.setRequestLoggerSuccessEnabled(Boolean.TRUE);
    rw.setRequestTrackerOptions(requestTrackerOptions);

    CtThrottlerOptions throttlerOptions = new CtThrottlerOptions();
    throttlerOptions.setThrottlerClass(
        "com.datastax.oss.driver.internal.core.session.throttling.PassThroughRequestThrottler");
    rw.setThrottlerOptions(throttlerOptions);

    CtMetadataOptions metadataOptions = new CtMetadataOptions();
    metadataOptions.setSchemaEnabled(Boolean.TRUE);
    metadataOptions.setTokenMapEnabled(Boolean.TRUE);
    rw.setMetadataOptions(metadataOptions);

    CtPreparedStatementOptions preparedStatementOptions = new CtPreparedStatementOptions();
    preparedStatementOptions.setPrepareOnAllNodes(Boolean.TRUE);
    preparedStatementOptions.setReprepareEnabled(Boolean.TRUE);
    preparedStatementOptions.setPreparedCacheWeakValues(Boolean.TRUE);
    rw.setPreparedStatementOptions(preparedStatementOptions);

    CtNettyOptions nettyOptions = new CtNettyOptions();
    nettyOptions.setDaemonThreads(Boolean.TRUE);
    rw.setNettyOptions(nettyOptions);

    rw.setCoalescerIntervalMicros(10L);
    rw.setSessionLeakThreshold(10);
    rw.setResolveContactPoints(Boolean.TRUE);

    CpoAdapterFactory factory =
        new CassandraCpoConfigProcessor().processCpoConfig(config("cfgMaximal", rw));
    assertNotNull(factory);
    assertNotNull(factory.getCpoAdapter().getDataSourceName());
  }

  @Test
  public void testReadWriteSplitConfig() throws Exception {
    CtCassandraConfig cfg = new CtCassandraConfig();
    cfg.setName("cfgSplit");
    cfg.setMetaDescriptorName(metaDescriptorName);
    cfg.setFetchSize(BigInteger.valueOf(10));
    cfg.setBatchSize(BigInteger.valueOf(10));
    cfg.setReadConfig(minimalRwConfig());
    cfg.setWriteConfig(minimalRwConfig());

    CpoAdapterFactory factory = new CassandraCpoConfigProcessor().processCpoConfig(cfg);
    assertNotNull(factory);
    assertNotNull(factory.getCpoAdapter().getDataSourceName());
  }

  @Test
  public void testBlankOptionalValuesIgnored() throws Exception {
    // blank strings must be treated the same as absent options
    CtCassandraReadWriteConfig rw = minimalRwConfig();
    rw.setLoadBalancingPolicy("");
    rw.setReconnectionPolicy("");
    rw.setRetryPolicy("");
    rw.setAddressTranslater("");
    rw.setAuthProvider("");
    rw.setInitialListeners("");
    rw.setSchemaChangeListeners("");
    rw.setSpeculativeExecutionPolicy("");
    rw.setTimestampGenerator("");
    rw.setSslOptions(
        new jakarta.xml.bind.JAXBElement<>(
            new javax.xml.namespace.QName(
                "http://www.synchronoss.org/cpo/CpoConfig.xsd", "sslOptions"),
            String.class,
            ""));

    CpoAdapterFactory factory =
        new CassandraCpoConfigProcessor().processCpoConfig(config("cfgBlanks", rw));
    assertNotNull(factory);
  }

  @Test
  public void testInvalidConfigRejected() {
    CassandraCpoConfigProcessor processor = new CassandraCpoConfigProcessor();
    expectThrows(CpoException.class, () -> processor.processCpoConfig(null));
    expectThrows(CpoException.class, () -> processor.processCpoConfig(new CtDataSourceConfig()));
  }
}
