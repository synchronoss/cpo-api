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

import com.datastax.oss.driver.api.core.auth.AuthProvider;
import com.datastax.oss.driver.api.core.metadata.NodeStateListener;
import com.datastax.oss.driver.api.core.metadata.schema.SchemaChangeListener;
import com.datastax.oss.driver.api.core.ssl.SslEngineFactory;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cassandra.CassandraCpoAdapter;
import org.synchronoss.cpo.cassandra.CassandraCpoAdapterFactory;
import org.synchronoss.cpo.cassandra.ClusterDataSourceInfo;
import org.synchronoss.cpo.cassandra.HistogramOptions;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.core.CpoAdapterFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.cpoconfig.*;

/**
 * CassandraCpoConfigProcessor processes the datasource configuration file for cassandra. It pulls
 * out all the information needed to configure a session for use within the application.
 *
 * @author dberry
 */
public class CassandraCpoConfigProcessor implements CpoConfigProcessor {
  private static final Logger logger = LoggerFactory.getLogger(CassandraCpoConfigProcessor.class);

  /** Constructs a CassandraCpoConfigProcessor */
  public CassandraCpoConfigProcessor() {}

  @Override
  public CpoAdapterFactory processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException {
    CpoAdapterFactory cpoAdapterFactory = null;

    if (cpoConfig == null || !(cpoConfig instanceof CtCassandraConfig)) {
      throw new CpoException("Invalid Cassandra Configuration Information");
    }

    CtCassandraConfig cassandraConfig = (CtCassandraConfig) cpoConfig;

    CassandraCpoMetaDescriptor metaDescriptor =
        (CassandraCpoMetaDescriptor)
            CpoMetaDescriptor.getInstance(cassandraConfig.getMetaDescriptorName());

    // build the cluster information
    if (cassandraConfig.getReadWriteConfig() != null) {
      ClusterDataSourceInfo clusterInfo =
          buildDataSourceInfo(
              cassandraConfig.getName(),
              cassandraConfig.getReadWriteConfig(),
              cassandraConfig.getFetchSize().intValue(),
              cassandraConfig.getBatchSize().intValue());
      cpoAdapterFactory =
          new CassandraCpoAdapterFactory(
              CassandraCpoAdapter.getInstance(metaDescriptor, clusterInfo));
    } else {
      ClusterDataSourceInfo readClusterInfo =
          buildDataSourceInfo(
              cassandraConfig.getName(),
              cassandraConfig.getReadConfig(),
              cassandraConfig.getFetchSize().intValue(),
              cassandraConfig.getBatchSize().intValue());
      ClusterDataSourceInfo writeClusterInfo =
          buildDataSourceInfo(
              cassandraConfig.getName(),
              cassandraConfig.getWriteConfig(),
              cassandraConfig.getFetchSize().intValue(),
              cassandraConfig.getBatchSize().intValue());
      cpoAdapterFactory =
          new CassandraCpoAdapterFactory(
              CassandraCpoAdapter.getInstance(metaDescriptor, writeClusterInfo, readClusterInfo));
    }
    logger.debug(
        "Adapter Datasourcename =" + cpoAdapterFactory.getCpoAdapter().getDataSourceName());

    return cpoAdapterFactory;
  }

  /**
   * buildDataSourceInfo takes the config information from cpoconfig.xml and insantiates a
   * CLusterDataSourceInfo object
   *
   * @param dataConfigName The name of the data config
   * @param readWriteConfig The configuration information
   * @return A ClusterDataSourceInfo Object
   * @throws CpoException if a configured policy/provider class cannot be instantiated
   */
  private ClusterDataSourceInfo buildDataSourceInfo(
      String dataConfigName,
      CtCassandraReadWriteConfig readWriteConfig,
      int fetchSize,
      int batchSize)
      throws CpoException {
    ClusterDataSourceInfo clusterInfo =
        new ClusterDataSourceInfo(
            dataConfigName,
            readWriteConfig.getKeySpace(),
            readWriteConfig.getContactPoint(),
            fetchSize,
            batchSize);

    clusterInfo.setClusterName(readWriteConfig.getClusterName());
    clusterInfo.setLocalDatacenter(readWriteConfig.getLocalDatacenter());
    clusterInfo.setApplicationName(readWriteConfig.getApplicationName());
    clusterInfo.setApplicationVersion(readWriteConfig.getApplicationVersion());
    clusterInfo.setMaxSchemaAgreementWaitSeconds(
        readWriteConfig.getMaxSchemaAgreementWaitSeconds());
    clusterInfo.setSchemaAgreementIntervalMillis(
        readWriteConfig.getSchemaAgreementIntervalMillis());
    clusterInfo.setSchemaAgreementWarnOnFailure(readWriteConfig.isSchemaAgreementWarnOnFailure());
    clusterInfo.setControlConnectionTimeoutMillis(
        readWriteConfig.getControlConnectionTimeoutMillis());

    if (readWriteConfig.getPort() != null) clusterInfo.setPort(readWriteConfig.getPort());

    clusterInfo.setRequestTimeoutMillis(readWriteConfig.getRequestTimeoutMillis());

    // driver 4.x instantiates load-balancing/reconnection/retry/address-translator/speculative
    // -execution/timestamp-generator classes itself, so the XML string is passed straight through
    // as the implementation class name (no CPO factory wrapper for these)
    if (isNotBlank(readWriteConfig.getLoadBalancingPolicy()))
      clusterInfo.setLoadBalancingPolicyClassName(readWriteConfig.getLoadBalancingPolicy());
    applyLoadBalancingOptions(clusterInfo, readWriteConfig.getLoadBalancingOptions());

    if (isNotBlank(readWriteConfig.getReconnectionPolicy()))
      clusterInfo.setReconnectionPolicyClassName(readWriteConfig.getReconnectionPolicy());
    clusterInfo.setReconnectionBaseDelayMillis(readWriteConfig.getReconnectionBaseDelayMillis());
    clusterInfo.setReconnectionMaxDelayMillis(readWriteConfig.getReconnectionMaxDelayMillis());
    clusterInfo.setReconnectOnInit(readWriteConfig.isReconnectOnInit());

    if (isNotBlank(readWriteConfig.getRetryPolicy()))
      clusterInfo.setRetryPolicyClassName(readWriteConfig.getRetryPolicy());

    if (readWriteConfig.getCredentials() != null) {
      clusterInfo.setHasCredentials(true);
      clusterInfo.setUserName(readWriteConfig.getCredentials().getUser());
      clusterInfo.setPassword(readWriteConfig.getCredentials().getPassword());
    }

    if (isNotBlank(readWriteConfig.getAddressTranslater()))
      clusterInfo.setAddressTranslatorClassName(readWriteConfig.getAddressTranslater());
    applyAddressTranslatorOptions(clusterInfo, readWriteConfig.getAddressTranslatorOptions());

    // AuthProvider: still built via the CPO factory pattern since CqlSessionBuilder accepts a
    // pre-built AuthProvider instance directly
    if (isNotBlank(readWriteConfig.getAuthProvider()))
      clusterInfo.setAuthProvider(
          new ConfigInstantiator<AuthProvider>().instantiate(readWriteConfig.getAuthProvider()));

    if (readWriteConfig.getCompression() != null)
      clusterInfo.setCompressionType(readWriteConfig.getCompression().toString().toLowerCase());

    if (readWriteConfig.getProtocolVersion() != null)
      clusterInfo.setProtocolVersion(readWriteConfig.getProtocolVersion().value());
    clusterInfo.setProtocolMaxFrameLengthBytes(readWriteConfig.getProtocolMaxFrameLengthBytes());

    // SSL: a custom SSLOptionsFactory still uses the CPO factory pattern (CqlSessionBuilder
    // accepts a pre-built SslEngineFactory instance directly); sslEngineOptions is a declarative
    // alternative that configures the driver's built-in DefaultSslEngineFactory instead
    if (readWriteConfig.getSslOptions() != null
        && readWriteConfig.getSslOptions().getValue() != null
        && !readWriteConfig.getSslOptions().getValue().isBlank()) {
      clusterInfo.setSslEngineFactory(
          new ConfigInstantiator<SslEngineFactory>()
              .instantiate(readWriteConfig.getSslOptions().getValue()));
    }
    applySslEngineOptions(clusterInfo, readWriteConfig.getSslEngineOptions());

    // Listeners: still built via the CPO factory pattern since CqlSessionBuilder accepts
    // pre-built NodeStateListener/SchemaChangeListener instances directly
    if (isNotBlank(readWriteConfig.getInitialListeners())) {
      clusterInfo.setListeners(
          new ConfigInstantiator<Collection<NodeStateListener>>()
              .instantiate(readWriteConfig.getInitialListeners()));
    }
    if (isNotBlank(readWriteConfig.getSchemaChangeListeners())) {
      clusterInfo.setSchemaChangeListeners(
          new ConfigInstantiator<Collection<SchemaChangeListener>>()
              .instantiate(readWriteConfig.getSchemaChangeListeners()));
    }

    applyMetricsOptions(clusterInfo, readWriteConfig.getMetricsOptions());

    if (readWriteConfig.getPoolingOptions() != null)
      applyPoolingOptions(clusterInfo, readWriteConfig.getPoolingOptions());

    if (readWriteConfig.getSocketOptions() != null)
      applySocketOptions(clusterInfo, readWriteConfig.getSocketOptions());

    if (readWriteConfig.getQueryOptions() != null)
      applyQueryOptions(clusterInfo, readWriteConfig.getQueryOptions());

    if (isNotBlank(readWriteConfig.getSpeculativeExecutionPolicy()))
      clusterInfo.setSpeculativeExecutionPolicyClassName(
          readWriteConfig.getSpeculativeExecutionPolicy());
    clusterInfo.setSpeculativeExecutionMaxExecutions(
        readWriteConfig.getSpeculativeExecutionMaxExecutions());
    clusterInfo.setSpeculativeExecutionDelayMillis(
        readWriteConfig.getSpeculativeExecutionDelayMillis());

    if (isNotBlank(readWriteConfig.getTimestampGenerator()))
      clusterInfo.setTimestampGeneratorClassName(readWriteConfig.getTimestampGenerator());
    applyTimestampGeneratorOptions(clusterInfo, readWriteConfig.getTimestampGeneratorOptions());

    applyRequestOptions(clusterInfo, readWriteConfig.getRequestOptions());
    applyRequestTrackerOptions(clusterInfo, readWriteConfig.getRequestTrackerOptions());
    applyThrottlerOptions(clusterInfo, readWriteConfig.getThrottlerOptions());
    applyMetadataOptions(clusterInfo, readWriteConfig.getMetadataOptions());
    applyPreparedStatementOptions(clusterInfo, readWriteConfig.getPreparedStatementOptions());
    applyNettyOptions(clusterInfo, readWriteConfig.getNettyOptions());

    clusterInfo.setCoalescerIntervalMicros(readWriteConfig.getCoalescerIntervalMicros());
    clusterInfo.setSessionLeakThreshold(readWriteConfig.getSessionLeakThreshold());
    clusterInfo.setResolveContactPoints(readWriteConfig.isResolveContactPoints());

    logger.debug("Created DataSourceInfo: " + clusterInfo);
    return clusterInfo;
  }

  private static boolean isNotBlank(String value) {
    return value != null && !value.isBlank();
  }

  private void applyLoadBalancingOptions(
      ClusterDataSourceInfo clusterInfo, CtLoadBalancingOptions options) {
    if (options == null) return;

    clusterInfo.setLoadBalancingSlowReplicaAvoidance(options.isSlowReplicaAvoidance());
    clusterInfo.setLoadBalancingDistanceEvaluatorClassName(options.getDistanceEvaluatorClass());
    clusterInfo.setDcFailoverMaxNodesPerRemoteDc(options.getDcFailoverMaxNodesPerRemoteDc());
    clusterInfo.setDcFailoverAllowForLocalConsistencyLevels(
        options.isDcFailoverAllowForLocalConsistencyLevels());
    if (!options.getDcFailoverPreferredRemoteDc().isEmpty())
      clusterInfo.setDcFailoverPreferredRemoteDcs(options.getDcFailoverPreferredRemoteDc());
  }

  private void applyAddressTranslatorOptions(
      ClusterDataSourceInfo clusterInfo, CtAddressTranslatorOptions options) {
    if (options == null) return;

    clusterInfo.setAddressTranslatorAdvertisedHostname(options.getAdvertisedHostname());
    clusterInfo.setAddressTranslatorDefaultAddress(options.getDefaultAddress());
    clusterInfo.setAddressTranslatorResolveAddresses(options.isResolveAddresses());
    if (!options.getSubnetAddress().isEmpty()) {
      Map<String, String> subnetAddresses = new LinkedHashMap<>();
      for (CtSubnetAddress subnetAddress : options.getSubnetAddress())
        subnetAddresses.put(subnetAddress.getCidr(), subnetAddress.getAddress());
      clusterInfo.setAddressTranslatorSubnetAddresses(subnetAddresses);
    }
  }

  private void applySslEngineOptions(
      ClusterDataSourceInfo clusterInfo, CtSslEngineOptions options) {
    if (options == null) return;

    if (!options.getCipherSuite().isEmpty())
      clusterInfo.setSslCipherSuites(options.getCipherSuite());
    clusterInfo.setSslHostnameValidation(options.isHostnameValidation());
    clusterInfo.setSslAllowDnsReverseLookupSan(options.isAllowDnsReverseLookupSan());
    clusterInfo.setSslTruststorePath(options.getTruststorePath());
    clusterInfo.setSslTruststorePassword(options.getTruststorePassword());
    clusterInfo.setSslKeystorePath(options.getKeystorePath());
    clusterInfo.setSslKeystorePassword(options.getKeystorePassword());
    clusterInfo.setSslKeystoreReloadIntervalMinutes(options.getKeystoreReloadIntervalMinutes());
  }

  private void applyTimestampGeneratorOptions(
      ClusterDataSourceInfo clusterInfo, CtTimestampGeneratorOptions options) {
    if (options == null) return;

    clusterInfo.setTimestampGeneratorForceJavaClock(options.isForceJavaClock());
    clusterInfo.setTimestampGeneratorDriftWarningThresholdMillis(
        options.getDriftWarningThresholdMillis());
    clusterInfo.setTimestampGeneratorDriftWarningIntervalSeconds(
        options.getDriftWarningIntervalSeconds());
  }

  private void applyRequestOptions(ClusterDataSourceInfo clusterInfo, CtRequestOptions options) {
    if (options == null) return;

    clusterInfo.setRequestWarnIfSetKeyspace(options.isWarnIfSetKeyspace());
    clusterInfo.setRequestLogWarnings(options.isLogWarnings());
    clusterInfo.setRequestTraceAttempts(options.getTraceAttempts());
    clusterInfo.setRequestTraceIntervalMillis(options.getTraceIntervalMillis());
    if (options.getTraceConsistencyLevel() != null)
      clusterInfo.setRequestTraceConsistencyLevel(options.getTraceConsistencyLevel().toString());
  }

  private void applyRequestTrackerOptions(
      ClusterDataSourceInfo clusterInfo, CtRequestTrackerOptions options) {
    if (options == null) return;

    if (!options.getRequestTrackerClass().isEmpty())
      clusterInfo.setRequestTrackerClasses(options.getRequestTrackerClass());
    clusterInfo.setRequestLoggerSuccessEnabled(options.isRequestLoggerSuccessEnabled());
    clusterInfo.setRequestLoggerSlowThresholdMillis(options.getRequestLoggerSlowThresholdMillis());
    clusterInfo.setRequestLoggerSlowEnabled(options.isRequestLoggerSlowEnabled());
    clusterInfo.setRequestLoggerErrorEnabled(options.isRequestLoggerErrorEnabled());
    clusterInfo.setRequestLoggerMaxQueryLength(options.getRequestLoggerMaxQueryLength());
    clusterInfo.setRequestLoggerShowValues(options.isRequestLoggerShowValues());
    clusterInfo.setRequestLoggerMaxValueLength(options.getRequestLoggerMaxValueLength());
    clusterInfo.setRequestLoggerMaxValues(options.getRequestLoggerMaxValues());
    clusterInfo.setRequestLoggerShowStackTraces(options.isRequestLoggerShowStackTraces());
  }

  private void applyThrottlerOptions(
      ClusterDataSourceInfo clusterInfo, CtThrottlerOptions options) {
    if (options == null) return;

    clusterInfo.setThrottlerClassName(options.getThrottlerClass());
    clusterInfo.setThrottlerMaxQueueSize(options.getMaxQueueSize());
    clusterInfo.setThrottlerMaxConcurrentRequests(options.getMaxConcurrentRequests());
    clusterInfo.setThrottlerMaxRequestsPerSecond(options.getMaxRequestsPerSecond());
    clusterInfo.setThrottlerDrainIntervalMillis(options.getDrainIntervalMillis());
  }

  private void applyMetadataOptions(ClusterDataSourceInfo clusterInfo, CtMetadataOptions options) {
    if (options == null) return;

    clusterInfo.setMetadataSchemaEnabled(options.isSchemaEnabled());
    if (!options.getSchemaRefreshedKeyspace().isEmpty())
      clusterInfo.setMetadataSchemaRefreshedKeyspaces(options.getSchemaRefreshedKeyspace());
    clusterInfo.setMetadataSchemaRequestTimeoutMillis(options.getSchemaRequestTimeoutMillis());
    clusterInfo.setMetadataSchemaRequestPageSize(options.getSchemaRequestPageSize());
    clusterInfo.setMetadataSchemaWindowMillis(options.getSchemaWindowMillis());
    clusterInfo.setMetadataSchemaMaxEvents(options.getSchemaMaxEvents());
    clusterInfo.setMetadataTopologyWindowMillis(options.getTopologyWindowMillis());
    clusterInfo.setMetadataTopologyMaxEvents(options.getTopologyMaxEvents());
    clusterInfo.setMetadataTokenMapEnabled(options.isTokenMapEnabled());
  }

  private void applyPreparedStatementOptions(
      ClusterDataSourceInfo clusterInfo, CtPreparedStatementOptions options) {
    if (options == null) return;

    clusterInfo.setPrepareOnAllNodes(options.isPrepareOnAllNodes());
    clusterInfo.setReprepareEnabled(options.isReprepareEnabled());
    clusterInfo.setReprepareCheckSystemTable(options.isReprepareCheckSystemTable());
    clusterInfo.setReprepareMaxStatements(options.getReprepareMaxStatements());
    clusterInfo.setReprepareMaxParallelism(options.getReprepareMaxParallelism());
    clusterInfo.setReprepareTimeoutMillis(options.getReprepareTimeoutMillis());
    clusterInfo.setPreparedCacheWeakValues(options.isPreparedCacheWeakValues());
  }

  private void applyNettyOptions(ClusterDataSourceInfo clusterInfo, CtNettyOptions options) {
    if (options == null) return;

    clusterInfo.setNettyDaemonThreads(options.isDaemonThreads());
    clusterInfo.setNettyIoGroupSize(options.getIoGroupSize());
    clusterInfo.setNettyIoGroupShutdownQuietPeriodSeconds(
        options.getIoGroupShutdownQuietPeriodSeconds());
    clusterInfo.setNettyIoGroupShutdownTimeoutSeconds(options.getIoGroupShutdownTimeoutSeconds());
    clusterInfo.setNettyAdminGroupSize(options.getAdminGroupSize());
    clusterInfo.setNettyAdminGroupShutdownQuietPeriodSeconds(
        options.getAdminGroupShutdownQuietPeriodSeconds());
    clusterInfo.setNettyAdminGroupShutdownTimeoutSeconds(
        options.getAdminGroupShutdownTimeoutSeconds());
    clusterInfo.setNettyTimerTickDurationMillis(options.getTimerTickDurationMillis());
    clusterInfo.setNettyTimerTicksPerWheel(options.getTimerTicksPerWheel());
  }

  private void applyMetricsOptions(ClusterDataSourceInfo clusterInfo, CtMetricsOptions options) {
    if (options == null) return;

    clusterInfo.setMetricsFactoryClassName(options.getFactoryClass());
    clusterInfo.setMetricsIdGeneratorClassName(options.getIdGeneratorClass());
    clusterInfo.setMetricsIdGeneratorPrefix(options.getIdGeneratorPrefix());
    clusterInfo.setMetricsGenerateAggregableHistograms(options.isGenerateAggregableHistograms());
    if (!options.getSessionEnabled().isEmpty())
      clusterInfo.setMetricsSessionEnabled(options.getSessionEnabled());
    if (!options.getNodeEnabled().isEmpty())
      clusterInfo.setMetricsNodeEnabled(options.getNodeEnabled());
    clusterInfo.setMetricsNodeExpireAfterMinutes(options.getNodeExpireAfterMinutes());
    clusterInfo.setMetricsSessionCqlRequests(toHistogramOptions(options.getSessionCqlRequests()));
    clusterInfo.setMetricsSessionThrottlingDelay(
        toHistogramOptions(options.getSessionThrottlingDelay()));
    clusterInfo.setMetricsNodeCqlMessages(toHistogramOptions(options.getNodeCqlMessages()));
  }

  private HistogramOptions toHistogramOptions(CtHistogramOptions ctHistogramOptions) {
    if (ctHistogramOptions == null) return null;

    HistogramOptions histogramOptions = new HistogramOptions();
    histogramOptions.setHighestLatencyMillis(ctHistogramOptions.getHighestLatencyMillis());
    histogramOptions.setLowestLatencyMillis(ctHistogramOptions.getLowestLatencyMillis());
    histogramOptions.setSignificantDigits(ctHistogramOptions.getSignificantDigits());
    histogramOptions.setRefreshIntervalMinutes(ctHistogramOptions.getRefreshIntervalMinutes());
    if (!ctHistogramOptions.getSloMillis().isEmpty())
      histogramOptions.setSloMillis(ctHistogramOptions.getSloMillis());
    if (!ctHistogramOptions.getPublishPercentile().isEmpty())
      histogramOptions.setPublishPercentiles(ctHistogramOptions.getPublishPercentile());
    return histogramOptions;
  }

  /**
   * Applies the pooling options onto the ClusterDataSourceInfo.
   *
   * @param clusterInfo the ClusterDataSourceInfo to configure
   * @param options the pooling options from the XML config
   */
  private void applyPoolingOptions(ClusterDataSourceInfo clusterInfo, CtPoolingOptions options) {
    clusterInfo.setConnectionPoolLocalSize(options.getConnectionPoolLocalSize());
    clusterInfo.setConnectionPoolRemoteSize(options.getConnectionPoolRemoteSize());
    clusterInfo.setHeartbeatIntervalSeconds(options.getHeartbeatIntervalSeconds());
    clusterInfo.setHeartbeatTimeoutSeconds(options.getHeartbeatTimeoutSeconds());
    clusterInfo.setConnectInitQueryTimeoutMillis(options.getConnectInitQueryTimeoutMillis());
    clusterInfo.setSetKeyspaceTimeoutMillis(options.getSetKeyspaceTimeoutMillis());
    clusterInfo.setMaxRequestsPerConnection(options.getMaxRequestsPerConnection());
    clusterInfo.setMaxOrphanRequests(options.getMaxOrphanRequests());
    clusterInfo.setWarnOnInitError(options.isWarnOnInitError());
  }

  /**
   * Applies the socket options onto the ClusterDataSourceInfo.
   *
   * @param clusterInfo the ClusterDataSourceInfo to configure
   * @param options the socket options from the XML config
   */
  private void applySocketOptions(ClusterDataSourceInfo clusterInfo, CtSocketOptions options) {
    clusterInfo.setConnectTimeoutMillis(options.getConnectionTimeoutMillis());
    clusterInfo.setTcpNoDelay(options.isTcpNoDelay());
    clusterInfo.setSocketKeepAlive(options.isKeepAlive());
    clusterInfo.setSocketReuseAddress(options.isReuseAddress());
    clusterInfo.setSocketLingerIntervalSeconds(options.getSoLinger());
    clusterInfo.setSocketReceiveBufferSize(options.getReceiveBufferSize());
    clusterInfo.setSocketSendBufferSize(options.getSendBufferSize());
  }

  private void applyQueryOptions(ClusterDataSourceInfo clusterInfo, CtQueryOptions options) {
    if (options.getConsistencyLevel() != null)
      clusterInfo.setConsistencyLevel(options.getConsistencyLevel().toString());

    if (options.isDefaultIdempotence() != null)
      clusterInfo.setDefaultIdempotence(options.isDefaultIdempotence());

    if (options.getFetchSize() != null) clusterInfo.setPageSize(options.getFetchSize());

    if (options.getSerialConsistencyLevel() != null)
      clusterInfo.setSerialConsistencyLevel(options.getSerialConsistencyLevel().toString());
  }
}
