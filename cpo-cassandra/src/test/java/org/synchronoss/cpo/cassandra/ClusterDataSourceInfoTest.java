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
import java.util.Map;
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
    // Every test in this class builds its own real, short-lived CqlSession via getDataSource();
    // running several concurrently (parallel="classes") pushes the driver's process-wide session
    // count past its default advisory threshold of 4 and logs a false-positive leak warning.
    info.setSessionLeakThreshold(20);
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

    info.setApplicationName("myApp");
    assertEquals(info.getApplicationName(), "myApp");

    info.setApplicationVersion("1.2.3");
    assertEquals(info.getApplicationVersion(), "1.2.3");

    info.setMaxSchemaAgreementWaitSeconds(30);
    assertEquals(info.getMaxSchemaAgreementWaitSeconds(), Integer.valueOf(30));

    info.setSchemaAgreementIntervalMillis(250L);
    assertEquals(info.getSchemaAgreementIntervalMillis(), Long.valueOf(250L));

    info.setSchemaAgreementWarnOnFailure(Boolean.TRUE);
    assertEquals(info.getSchemaAgreementWarnOnFailure(), Boolean.TRUE);

    info.setControlConnectionTimeoutMillis(6000L);
    assertEquals(info.getControlConnectionTimeoutMillis(), Long.valueOf(6000L));

    info.setAddressTranslatorClassName(
        "com.datastax.oss.driver.internal.core.addresstranslation.PassThroughAddressTranslator");
    assertEquals(
        info.getAddressTranslatorClassName(),
        "com.datastax.oss.driver.internal.core.addresstranslation.PassThroughAddressTranslator");

    info.setAddressTranslatorAdvertisedHostname("advertised.example.com");
    assertEquals(info.getAddressTranslatorAdvertisedHostname(), "advertised.example.com");

    info.setAddressTranslatorDefaultAddress("cassandra.datacenter1.com:9042");
    assertEquals(info.getAddressTranslatorDefaultAddress(), "cassandra.datacenter1.com:9042");

    info.setAddressTranslatorResolveAddresses(Boolean.TRUE);
    assertEquals(info.getAddressTranslatorResolveAddresses(), Boolean.TRUE);

    Map<String, String> subnetAddresses = Map.of("100.64.0.0/15", "cassandra.dc1.com:9042");
    info.setAddressTranslatorSubnetAddresses(subnetAddresses);
    assertEquals(info.getAddressTranslatorSubnetAddresses(), subnetAddresses);

    info.setPort(9042);
    assertEquals(info.getPort(), 9042);

    info.setRequestTimeoutMillis(3000L);
    assertEquals(info.getRequestTimeoutMillis(), Long.valueOf(3000L));

    info.setLoadBalancingPolicyClassName(
        "com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy");
    assertEquals(
        info.getLoadBalancingPolicyClassName(),
        "com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy");

    info.setLoadBalancingSlowReplicaAvoidance(Boolean.TRUE);
    assertEquals(info.getLoadBalancingSlowReplicaAvoidance(), Boolean.TRUE);

    info.setLoadBalancingDistanceEvaluatorClassName("com.example.MyDistanceEvaluator");
    assertEquals(
        info.getLoadBalancingDistanceEvaluatorClassName(), "com.example.MyDistanceEvaluator");

    info.setDcFailoverMaxNodesPerRemoteDc(2);
    assertEquals(info.getDcFailoverMaxNodesPerRemoteDc(), Integer.valueOf(2));

    info.setDcFailoverAllowForLocalConsistencyLevels(Boolean.TRUE);
    assertEquals(info.getDcFailoverAllowForLocalConsistencyLevels(), Boolean.TRUE);

    info.setDcFailoverPreferredRemoteDcs(List.of("dc2", "dc3"));
    assertEquals(info.getDcFailoverPreferredRemoteDcs(), List.of("dc2", "dc3"));

    info.setReconnectionPolicyClassName(
        "com.datastax.oss.driver.internal.core.connection.ConstantReconnectionPolicy");
    assertEquals(
        info.getReconnectionPolicyClassName(),
        "com.datastax.oss.driver.internal.core.connection.ConstantReconnectionPolicy");

    info.setReconnectionBaseDelayMillis(1000L);
    assertEquals(info.getReconnectionBaseDelayMillis(), Long.valueOf(1000L));

    info.setReconnectionMaxDelayMillis(60000L);
    assertEquals(info.getReconnectionMaxDelayMillis(), Long.valueOf(60000L));

    info.setReconnectOnInit(Boolean.TRUE);
    assertEquals(info.getReconnectOnInit(), Boolean.TRUE);

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

    info.setProtocolMaxFrameLengthBytes(268435456L);
    assertEquals(info.getProtocolMaxFrameLengthBytes(), Long.valueOf(268435456L));

    info.setSslEngineFactory(null);
    assertNull(info.getSslEngineFactory());

    info.setSslCipherSuites(List.of("TLS_RSA_WITH_AES_128_CBC_SHA"));
    assertEquals(info.getSslCipherSuites(), List.of("TLS_RSA_WITH_AES_128_CBC_SHA"));

    info.setSslHostnameValidation(Boolean.TRUE);
    assertEquals(info.getSslHostnameValidation(), Boolean.TRUE);

    info.setSslAllowDnsReverseLookupSan(Boolean.FALSE);
    assertEquals(info.getSslAllowDnsReverseLookupSan(), Boolean.FALSE);

    info.setSslTruststorePath("/path/to/truststore");
    assertEquals(info.getSslTruststorePath(), "/path/to/truststore");

    info.setSslTruststorePassword("truststorePass");
    assertEquals(info.getSslTruststorePassword(), "truststorePass");

    info.setSslKeystorePath("/path/to/keystore");
    assertEquals(info.getSslKeystorePath(), "/path/to/keystore");

    info.setSslKeystorePassword("keystorePass");
    assertEquals(info.getSslKeystorePassword(), "keystorePass");

    info.setSslKeystoreReloadIntervalMinutes(30L);
    assertEquals(info.getSslKeystoreReloadIntervalMinutes(), Long.valueOf(30L));

    info.setListeners(List.of());
    assertTrue(info.getListeners().isEmpty());

    info.setSchemaChangeListeners(List.of());
    assertTrue(info.getSchemaChangeListeners().isEmpty());

    info.setMetricsFactoryClassName("NoopMetricsFactory");
    assertEquals(info.getMetricsFactoryClassName(), "NoopMetricsFactory");

    info.setMetricsIdGeneratorClassName("TaggingMetricIdGenerator");
    assertEquals(info.getMetricsIdGeneratorClassName(), "TaggingMetricIdGenerator");

    info.setMetricsIdGeneratorPrefix("cassandra");
    assertEquals(info.getMetricsIdGeneratorPrefix(), "cassandra");

    info.setMetricsGenerateAggregableHistograms(Boolean.TRUE);
    assertEquals(info.getMetricsGenerateAggregableHistograms(), Boolean.TRUE);

    info.setMetricsSessionEnabled(List.of("cql-requests", "bytes-sent"));
    assertEquals(info.getMetricsSessionEnabled(), List.of("cql-requests", "bytes-sent"));

    info.setMetricsNodeEnabled(List.of("pool.open-connections"));
    assertEquals(info.getMetricsNodeEnabled(), List.of("pool.open-connections"));

    info.setMetricsNodeExpireAfterMinutes(60L);
    assertEquals(info.getMetricsNodeExpireAfterMinutes(), Long.valueOf(60L));

    HistogramOptions cqlRequestsHistogram = new HistogramOptions();
    cqlRequestsHistogram.setHighestLatencyMillis(3000L);
    cqlRequestsHistogram.setLowestLatencyMillis(1L);
    cqlRequestsHistogram.setSignificantDigits(3);
    cqlRequestsHistogram.setRefreshIntervalMinutes(5L);
    cqlRequestsHistogram.setSloMillis(List.of(100L, 500L));
    cqlRequestsHistogram.setPublishPercentiles(List.of(0.95, 0.99));
    info.setMetricsSessionCqlRequests(cqlRequestsHistogram);
    assertSame(info.getMetricsSessionCqlRequests(), cqlRequestsHistogram);
    assertEquals(
        info.getMetricsSessionCqlRequests().getHighestLatencyMillis(), Long.valueOf(3000L));
    assertEquals(info.getMetricsSessionCqlRequests().getSloMillis(), List.of(100L, 500L));
    assertEquals(info.getMetricsSessionCqlRequests().getPublishPercentiles(), List.of(0.95, 0.99));

    HistogramOptions throttlingHistogram = new HistogramOptions();
    info.setMetricsSessionThrottlingDelay(throttlingHistogram);
    assertSame(info.getMetricsSessionThrottlingDelay(), throttlingHistogram);

    HistogramOptions nodeCqlMessagesHistogram = new HistogramOptions();
    info.setMetricsNodeCqlMessages(nodeCqlMessagesHistogram);
    assertSame(info.getMetricsNodeCqlMessages(), nodeCqlMessagesHistogram);

    info.setConnectionPoolLocalSize(2);
    assertEquals(info.getConnectionPoolLocalSize(), Integer.valueOf(2));

    info.setConnectionPoolRemoteSize(1);
    assertEquals(info.getConnectionPoolRemoteSize(), Integer.valueOf(1));

    info.setHeartbeatIntervalSeconds(30);
    assertEquals(info.getHeartbeatIntervalSeconds(), Integer.valueOf(30));

    info.setHeartbeatTimeoutSeconds(5);
    assertEquals(info.getHeartbeatTimeoutSeconds(), Integer.valueOf(5));

    info.setConnectInitQueryTimeoutMillis(5000L);
    assertEquals(info.getConnectInitQueryTimeoutMillis(), Long.valueOf(5000L));

    info.setSetKeyspaceTimeoutMillis(5000L);
    assertEquals(info.getSetKeyspaceTimeoutMillis(), Long.valueOf(5000L));

    info.setMaxRequestsPerConnection(1024);
    assertEquals(info.getMaxRequestsPerConnection(), Integer.valueOf(1024));

    info.setMaxOrphanRequests(256);
    assertEquals(info.getMaxOrphanRequests(), Integer.valueOf(256));

    info.setWarnOnInitError(Boolean.TRUE);
    assertEquals(info.getWarnOnInitError(), Boolean.TRUE);

    info.setConnectTimeoutMillis(5000);
    assertEquals(info.getConnectTimeoutMillis(), Integer.valueOf(5000));

    info.setTcpNoDelay(Boolean.TRUE);
    assertEquals(info.getTcpNoDelay(), Boolean.TRUE);

    info.setSocketKeepAlive(Boolean.TRUE);
    assertEquals(info.getSocketKeepAlive(), Boolean.TRUE);

    info.setSocketReuseAddress(Boolean.TRUE);
    assertEquals(info.getSocketReuseAddress(), Boolean.TRUE);

    info.setSocketLingerIntervalSeconds(0);
    assertEquals(info.getSocketLingerIntervalSeconds(), Integer.valueOf(0));

    info.setSocketReceiveBufferSize(65536);
    assertEquals(info.getSocketReceiveBufferSize(), Integer.valueOf(65536));

    info.setSocketSendBufferSize(65536);
    assertEquals(info.getSocketSendBufferSize(), Integer.valueOf(65536));

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

    info.setSpeculativeExecutionMaxExecutions(3);
    assertEquals(info.getSpeculativeExecutionMaxExecutions(), Integer.valueOf(3));

    info.setSpeculativeExecutionDelayMillis(100L);
    assertEquals(info.getSpeculativeExecutionDelayMillis(), Long.valueOf(100L));

    info.setTimestampGeneratorClassName(
        "com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator");
    assertEquals(
        info.getTimestampGeneratorClassName(),
        "com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator");

    info.setTimestampGeneratorForceJavaClock(Boolean.TRUE);
    assertEquals(info.getTimestampGeneratorForceJavaClock(), Boolean.TRUE);

    info.setTimestampGeneratorDriftWarningThresholdMillis(1000L);
    assertEquals(info.getTimestampGeneratorDriftWarningThresholdMillis(), Long.valueOf(1000L));

    info.setTimestampGeneratorDriftWarningIntervalSeconds(10L);
    assertEquals(info.getTimestampGeneratorDriftWarningIntervalSeconds(), Long.valueOf(10L));

    info.setRequestWarnIfSetKeyspace(Boolean.TRUE);
    assertEquals(info.getRequestWarnIfSetKeyspace(), Boolean.TRUE);

    info.setRequestLogWarnings(Boolean.TRUE);
    assertEquals(info.getRequestLogWarnings(), Boolean.TRUE);

    info.setRequestTraceAttempts(5);
    assertEquals(info.getRequestTraceAttempts(), Integer.valueOf(5));

    info.setRequestTraceIntervalMillis(3L);
    assertEquals(info.getRequestTraceIntervalMillis(), Long.valueOf(3L));

    info.setRequestTraceConsistencyLevel("ONE");
    assertEquals(info.getRequestTraceConsistencyLevel(), "ONE");

    info.setRequestTrackerClasses(List.of("RequestLogger"));
    assertEquals(info.getRequestTrackerClasses(), List.of("RequestLogger"));

    info.setRequestLoggerSuccessEnabled(Boolean.TRUE);
    assertEquals(info.getRequestLoggerSuccessEnabled(), Boolean.TRUE);

    info.setRequestLoggerSlowThresholdMillis(1000L);
    assertEquals(info.getRequestLoggerSlowThresholdMillis(), Long.valueOf(1000L));

    info.setRequestLoggerSlowEnabled(Boolean.TRUE);
    assertEquals(info.getRequestLoggerSlowEnabled(), Boolean.TRUE);

    info.setRequestLoggerErrorEnabled(Boolean.TRUE);
    assertEquals(info.getRequestLoggerErrorEnabled(), Boolean.TRUE);

    info.setRequestLoggerMaxQueryLength(500);
    assertEquals(info.getRequestLoggerMaxQueryLength(), Integer.valueOf(500));

    info.setRequestLoggerShowValues(Boolean.TRUE);
    assertEquals(info.getRequestLoggerShowValues(), Boolean.TRUE);

    info.setRequestLoggerMaxValueLength(50);
    assertEquals(info.getRequestLoggerMaxValueLength(), Integer.valueOf(50));

    info.setRequestLoggerMaxValues(50);
    assertEquals(info.getRequestLoggerMaxValues(), Integer.valueOf(50));

    info.setRequestLoggerShowStackTraces(Boolean.TRUE);
    assertEquals(info.getRequestLoggerShowStackTraces(), Boolean.TRUE);

    info.setThrottlerClassName(
        "com.datastax.oss.driver.internal.core.session.throttling.ConcurrencyLimitingRequestThrottler");
    assertEquals(
        info.getThrottlerClassName(),
        "com.datastax.oss.driver.internal.core.session.throttling.ConcurrencyLimitingRequestThrottler");

    info.setThrottlerMaxQueueSize(10000);
    assertEquals(info.getThrottlerMaxQueueSize(), Integer.valueOf(10000));

    info.setThrottlerMaxConcurrentRequests(10000);
    assertEquals(info.getThrottlerMaxConcurrentRequests(), Integer.valueOf(10000));

    info.setThrottlerMaxRequestsPerSecond(10000);
    assertEquals(info.getThrottlerMaxRequestsPerSecond(), Integer.valueOf(10000));

    info.setThrottlerDrainIntervalMillis(10L);
    assertEquals(info.getThrottlerDrainIntervalMillis(), Long.valueOf(10L));

    info.setMetadataSchemaEnabled(Boolean.TRUE);
    assertEquals(info.getMetadataSchemaEnabled(), Boolean.TRUE);

    info.setMetadataSchemaRefreshedKeyspaces(List.of("!system", "!/^system_.*/"));
    assertEquals(info.getMetadataSchemaRefreshedKeyspaces(), List.of("!system", "!/^system_.*/"));

    info.setMetadataSchemaRequestTimeoutMillis(2000L);
    assertEquals(info.getMetadataSchemaRequestTimeoutMillis(), Long.valueOf(2000L));

    info.setMetadataSchemaRequestPageSize(5000);
    assertEquals(info.getMetadataSchemaRequestPageSize(), Integer.valueOf(5000));

    info.setMetadataSchemaWindowMillis(1000L);
    assertEquals(info.getMetadataSchemaWindowMillis(), Long.valueOf(1000L));

    info.setMetadataSchemaMaxEvents(20);
    assertEquals(info.getMetadataSchemaMaxEvents(), Integer.valueOf(20));

    info.setMetadataTopologyWindowMillis(1000L);
    assertEquals(info.getMetadataTopologyWindowMillis(), Long.valueOf(1000L));

    info.setMetadataTopologyMaxEvents(20);
    assertEquals(info.getMetadataTopologyMaxEvents(), Integer.valueOf(20));

    info.setMetadataTokenMapEnabled(Boolean.TRUE);
    assertEquals(info.getMetadataTokenMapEnabled(), Boolean.TRUE);

    info.setPrepareOnAllNodes(Boolean.TRUE);
    assertEquals(info.getPrepareOnAllNodes(), Boolean.TRUE);

    info.setReprepareEnabled(Boolean.TRUE);
    assertEquals(info.getReprepareEnabled(), Boolean.TRUE);

    info.setReprepareCheckSystemTable(Boolean.FALSE);
    assertEquals(info.getReprepareCheckSystemTable(), Boolean.FALSE);

    info.setReprepareMaxStatements(0);
    assertEquals(info.getReprepareMaxStatements(), Integer.valueOf(0));

    info.setReprepareMaxParallelism(100);
    assertEquals(info.getReprepareMaxParallelism(), Integer.valueOf(100));

    info.setReprepareTimeoutMillis(5000L);
    assertEquals(info.getReprepareTimeoutMillis(), Long.valueOf(5000L));

    info.setPreparedCacheWeakValues(Boolean.TRUE);
    assertEquals(info.getPreparedCacheWeakValues(), Boolean.TRUE);

    info.setNettyDaemonThreads(Boolean.TRUE);
    assertEquals(info.getNettyDaemonThreads(), Boolean.TRUE);

    info.setNettyIoGroupSize(4);
    assertEquals(info.getNettyIoGroupSize(), Integer.valueOf(4));

    info.setNettyIoGroupShutdownQuietPeriodSeconds(2);
    assertEquals(info.getNettyIoGroupShutdownQuietPeriodSeconds(), Integer.valueOf(2));

    info.setNettyIoGroupShutdownTimeoutSeconds(15);
    assertEquals(info.getNettyIoGroupShutdownTimeoutSeconds(), Integer.valueOf(15));

    info.setNettyAdminGroupSize(2);
    assertEquals(info.getNettyAdminGroupSize(), Integer.valueOf(2));

    info.setNettyAdminGroupShutdownQuietPeriodSeconds(2);
    assertEquals(info.getNettyAdminGroupShutdownQuietPeriodSeconds(), Integer.valueOf(2));

    info.setNettyAdminGroupShutdownTimeoutSeconds(15);
    assertEquals(info.getNettyAdminGroupShutdownTimeoutSeconds(), Integer.valueOf(15));

    info.setNettyTimerTickDurationMillis(100L);
    assertEquals(info.getNettyTimerTickDurationMillis(), Long.valueOf(100L));

    info.setNettyTimerTicksPerWheel(2048);
    assertEquals(info.getNettyTimerTicksPerWheel(), Integer.valueOf(2048));

    info.setCoalescerIntervalMicros(10L);
    assertEquals(info.getCoalescerIntervalMicros(), Long.valueOf(10L));

    info.setSessionLeakThreshold(4);
    assertEquals(info.getSessionLeakThreshold(), Integer.valueOf(4));

    info.setResolveContactPoints(Boolean.TRUE);
    assertEquals(info.getResolveContactPoints(), Boolean.TRUE);

    info.setProtocolVersion("V4");
    assertEquals(info.getProtocolVersion(), "V4");
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
    info.setApplicationName("cpo-cassandra-tests");
    info.setApplicationVersion("1.0");
    info.setMaxSchemaAgreementWaitSeconds(20);
    info.setSchemaAgreementIntervalMillis(200L);
    info.setSchemaAgreementWarnOnFailure(Boolean.TRUE);
    info.setControlConnectionTimeoutMillis(5000L);
    info.setRequestTimeoutMillis(5000L);
    info.setAddressTranslatorClassName(
        "com.datastax.oss.driver.internal.core.addresstranslation.PassThroughAddressTranslator");
    // PassThroughAddressTranslator ignores these; set purely to exercise the config-wiring
    info.setAddressTranslatorAdvertisedHostname("advertised.example.com");
    info.setAddressTranslatorDefaultAddress("cassandra.datacenter1.com:9042");
    info.setAddressTranslatorResolveAddresses(Boolean.TRUE);
    info.setAddressTranslatorSubnetAddresses(Map.of("100.64.0.0/15", "cassandra.dc1.com:9042"));
    info.setLoadBalancingPolicyClassName(
        "com.datastax.oss.driver.internal.core.loadbalancing.DefaultLoadBalancingPolicy");
    info.setLoadBalancingSlowReplicaAvoidance(Boolean.TRUE);
    // single-node test cluster has no remote DC, so these are harmless no-ops
    info.setDcFailoverMaxNodesPerRemoteDc(1);
    info.setDcFailoverAllowForLocalConsistencyLevels(Boolean.FALSE);
    info.setDcFailoverPreferredRemoteDcs(List.of("dc2"));
    info.setReconnectionPolicyClassName(
        "com.datastax.oss.driver.internal.core.connection.ConstantReconnectionPolicy");
    info.setReconnectionBaseDelayMillis(1000L);
    info.setReconnectionMaxDelayMillis(60000L);
    info.setReconnectOnInit(Boolean.FALSE);
    info.setRetryPolicyClassName("com.datastax.oss.driver.internal.core.retry.DefaultRetryPolicy");
    info.setHasCredentials(true);
    info.setUserName("user");
    info.setPassword("pass");
    info.setCompressionType("none");
    info.setMetricsFactoryClassName("NoopMetricsFactory");
    info.setMetricsIdGeneratorClassName("TaggingMetricIdGenerator");
    info.setMetricsIdGeneratorPrefix("cassandra");
    info.setMetricsGenerateAggregableHistograms(Boolean.TRUE);
    info.setMetricsSessionEnabled(List.of("cql-requests"));
    info.setMetricsNodeEnabled(List.of("pool.open-connections"));
    info.setMetricsNodeExpireAfterMinutes(60L);
    HistogramOptions cqlRequestsHistogram = new HistogramOptions();
    cqlRequestsHistogram.setHighestLatencyMillis(3000L);
    cqlRequestsHistogram.setLowestLatencyMillis(1L);
    cqlRequestsHistogram.setSignificantDigits(3);
    cqlRequestsHistogram.setRefreshIntervalMinutes(5L);
    cqlRequestsHistogram.setSloMillis(List.of(100L, 500L));
    cqlRequestsHistogram.setPublishPercentiles(List.of(0.95, 0.99));
    info.setMetricsSessionCqlRequests(cqlRequestsHistogram);
    info.setMetricsSessionThrottlingDelay(cqlRequestsHistogram);
    info.setMetricsNodeCqlMessages(cqlRequestsHistogram);
    info.setConnectionPoolLocalSize(1);
    info.setHeartbeatTimeoutSeconds(5);
    info.setConnectInitQueryTimeoutMillis(5000L);
    info.setSetKeyspaceTimeoutMillis(5000L);
    info.setMaxRequestsPerConnection(1024);
    info.setMaxOrphanRequests(256);
    info.setWarnOnInitError(Boolean.TRUE);
    info.setConnectTimeoutMillis(5000);
    info.setTcpNoDelay(Boolean.TRUE);
    info.setSocketKeepAlive(Boolean.TRUE);
    info.setSocketReuseAddress(Boolean.TRUE);
    info.setSocketLingerIntervalSeconds(0);
    info.setSocketReceiveBufferSize(65536);
    info.setSocketSendBufferSize(65536);
    info.setConsistencyLevel("LOCAL_ONE");
    info.setDefaultIdempotence(Boolean.TRUE);
    info.setPageSize(500);
    info.setSpeculativeExecutionPolicyClassName(
        "com.datastax.oss.driver.internal.core.specex.NoSpeculativeExecutionPolicy");
    info.setSpeculativeExecutionMaxExecutions(3);
    info.setSpeculativeExecutionDelayMillis(100L);
    info.setTimestampGeneratorClassName(
        "com.datastax.oss.driver.internal.core.time.AtomicTimestampGenerator");
    info.setTimestampGeneratorForceJavaClock(Boolean.TRUE);
    info.setRequestWarnIfSetKeyspace(Boolean.TRUE);
    info.setRequestLogWarnings(Boolean.TRUE);
    info.setRequestTraceAttempts(5);
    info.setRequestTraceIntervalMillis(3L);
    info.setRequestTraceConsistencyLevel("ONE");
    info.setRequestTrackerClasses(
        List.of("com.datastax.oss.driver.internal.core.tracker.RequestLogger"));
    info.setRequestLoggerSuccessEnabled(Boolean.TRUE);
    info.setRequestLoggerSlowThresholdMillis(1000L);
    info.setRequestLoggerSlowEnabled(Boolean.TRUE);
    info.setRequestLoggerErrorEnabled(Boolean.TRUE);
    info.setRequestLoggerMaxQueryLength(500);
    info.setRequestLoggerShowValues(Boolean.TRUE);
    info.setRequestLoggerMaxValueLength(50);
    info.setRequestLoggerMaxValues(50);
    info.setRequestLoggerShowStackTraces(Boolean.TRUE);
    info.setThrottlerClassName(
        "com.datastax.oss.driver.internal.core.session.throttling.PassThroughRequestThrottler");
    info.setThrottlerMaxQueueSize(10000);
    info.setThrottlerMaxConcurrentRequests(10000);
    info.setThrottlerMaxRequestsPerSecond(10000);
    info.setThrottlerDrainIntervalMillis(10L);
    info.setMetadataSchemaEnabled(Boolean.TRUE);
    info.setMetadataSchemaRefreshedKeyspaces(List.of("!system", "!/^system_.*/"));
    info.setMetadataSchemaRequestTimeoutMillis(2000L);
    info.setMetadataSchemaRequestPageSize(5000);
    info.setMetadataSchemaWindowMillis(1000L);
    info.setMetadataSchemaMaxEvents(20);
    info.setMetadataTopologyWindowMillis(1000L);
    info.setMetadataTopologyMaxEvents(20);
    info.setMetadataTokenMapEnabled(Boolean.TRUE);
    info.setPrepareOnAllNodes(Boolean.TRUE);
    info.setReprepareEnabled(Boolean.TRUE);
    info.setReprepareCheckSystemTable(Boolean.FALSE);
    info.setReprepareMaxStatements(0);
    info.setReprepareMaxParallelism(100);
    info.setReprepareTimeoutMillis(5000L);
    info.setPreparedCacheWeakValues(Boolean.TRUE);
    info.setNettyDaemonThreads(Boolean.TRUE);
    info.setNettyIoGroupSize(0);
    info.setNettyIoGroupShutdownQuietPeriodSeconds(2);
    info.setNettyIoGroupShutdownTimeoutSeconds(15);
    info.setNettyAdminGroupSize(2);
    info.setNettyAdminGroupShutdownQuietPeriodSeconds(2);
    info.setNettyAdminGroupShutdownTimeoutSeconds(15);
    info.setNettyTimerTickDurationMillis(100L);
    info.setNettyTimerTicksPerWheel(2048);
    info.setCoalescerIntervalMicros(10L);
    info.setSessionLeakThreshold(10);
    info.setResolveContactPoints(Boolean.TRUE);
    info.setSchemaChangeListeners(List.of());

    ClusterDataSource ds = info.getDataSource();
    assertNotNull(ds);
    assertEquals(ds.getKeySpace(), keySpace);
    ds.getSession().close();
  }

  @Test
  public void testCreateDataSourceWithSslOptionsExercisesConfigWiring() {
    // The test container has no SSL enabled, so activating the SSL engine factory always fails
    // the handshake; this test exists only to exercise the SSL config-loader wiring branches in
    // ClusterDataSourceInfo#createDataSource, not to verify a successful TLS session.
    ClusterDataSourceInfo info = newLiveInfo();
    info.setSslCipherSuites(List.of("TLS_RSA_WITH_AES_128_CBC_SHA"));
    info.setSslHostnameValidation(Boolean.TRUE);
    info.setSslAllowDnsReverseLookupSan(Boolean.FALSE);
    info.setSslTruststorePath("/nonexistent/truststore");
    info.setSslTruststorePassword("truststorePass");
    info.setSslKeystorePath("/nonexistent/keystore");
    info.setSslKeystorePassword("keystorePass");
    info.setSslKeystoreReloadIntervalMinutes(30L);

    try {
      ClusterDataSource ds = info.getDataSource();
      ds.getSession().close();
    } catch (Exception expected) {
      // handshake failure against the non-SSL test container is the expected outcome here
    }
  }
}
