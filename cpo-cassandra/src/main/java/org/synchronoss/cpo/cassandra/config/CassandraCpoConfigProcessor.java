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
import com.datastax.oss.driver.api.core.ssl.SslEngineFactory;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cassandra.CassandraCpoAdapter;
import org.synchronoss.cpo.cassandra.CassandraCpoAdapterFactory;
import org.synchronoss.cpo.cassandra.ClusterDataSourceInfo;
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

    // add clusterName
    clusterInfo.setClusterName(readWriteConfig.getClusterName());

    // add localDatacenter
    clusterInfo.setLocalDatacenter(readWriteConfig.getLocalDatacenter());

    // add maxSchemaAgreementWaitSeconds
    clusterInfo.setMaxSchemaAgreementWaitSeconds(
        readWriteConfig.getMaxSchemaAgreementWaitSeconds());

    // add port
    if (readWriteConfig.getPort() != null) clusterInfo.setPort(readWriteConfig.getPort());

    // add loadBalancing: driver 4.x instantiates the policy class itself, so the XML string is
    // passed straight through as the implementation class name
    if (readWriteConfig.getLoadBalancingPolicy() != null
        && !readWriteConfig.getLoadBalancingPolicy().isBlank()) {
      clusterInfo.setLoadBalancingPolicyClassName(readWriteConfig.getLoadBalancingPolicy());
    }

    // add reconnectionPolicy
    if (readWriteConfig.getReconnectionPolicy() != null
        && !readWriteConfig.getReconnectionPolicy().isBlank())
      clusterInfo.setReconnectionPolicyClassName(readWriteConfig.getReconnectionPolicy());

    // add retryPolicy
    if (readWriteConfig.getRetryPolicy() != null && !readWriteConfig.getRetryPolicy().isBlank())
      clusterInfo.setRetryPolicyClassName(readWriteConfig.getRetryPolicy());

    // add credentials
    if (readWriteConfig.getCredentials() != null) {
      clusterInfo.setHasCredentials(true);
      clusterInfo.setUserName(readWriteConfig.getCredentials().getUser());
      clusterInfo.setPassword(readWriteConfig.getCredentials().getPassword());
    }

    // add addressTranslater
    if (readWriteConfig.getAddressTranslater() != null
        && !readWriteConfig.getAddressTranslater().isBlank())
      clusterInfo.setAddressTranslatorClassName(readWriteConfig.getAddressTranslater());

    // add AuthProvider: still built via the CPO factory pattern since CqlSessionBuilder accepts
    // a pre-built AuthProvider instance directly
    if (readWriteConfig.getAuthProvider() != null && !readWriteConfig.getAuthProvider().isBlank())
      clusterInfo.setAuthProvider(
          new ConfigInstantiator<AuthProvider>().instantiate(readWriteConfig.getAuthProvider()));

    // add Compression
    if (readWriteConfig.getCompression() != null)
      clusterInfo.setCompressionType(readWriteConfig.getCompression().toString().toLowerCase());

    // add Metrics
    if (readWriteConfig.isMetrics() != null) clusterInfo.setUseMetrics(readWriteConfig.isMetrics());

    // add SSL: still built via the CPO factory pattern since CqlSessionBuilder accepts a
    // pre-built SslEngineFactory instance directly
    if (readWriteConfig.getSslOptions() != null
        && readWriteConfig.getSslOptions().getValue() != null
        && !readWriteConfig.getSslOptions().getValue().isBlank()) {
      clusterInfo.setSslEngineFactory(
          new ConfigInstantiator<SslEngineFactory>()
              .instantiate(readWriteConfig.getSslOptions().getValue()));
    }

    // add Listeners: still built via the CPO factory pattern since CqlSessionBuilder accepts
    // pre-built NodeStateListener instances directly
    if (readWriteConfig.getInitialListeners() != null
        && !readWriteConfig.getInitialListeners().isBlank()) {
      clusterInfo.setListeners(
          new ConfigInstantiator<Collection<NodeStateListener>>()
              .instantiate(readWriteConfig.getInitialListeners()));
    }

    // add JMX Reporting
    if (readWriteConfig.isJmxReporting() != null)
      clusterInfo.setUseJmxReporting(readWriteConfig.isJmxReporting());

    // add protocolVersion
    // the JAXB enum's name (V_3) differs from its XML value (V3), which is what the
    // driver's config expects, so the value must be used for the lookup
    if (readWriteConfig.getProtocolVersion() != null)
      clusterInfo.setProtocolVersion(readWriteConfig.getProtocolVersion().value());

    // add pooling options
    if (readWriteConfig.getPoolingOptions() != null)
      applyPoolingOptions(clusterInfo, readWriteConfig.getPoolingOptions());

    // add socket options
    if (readWriteConfig.getSocketOptions() != null)
      applySocketOptions(clusterInfo, readWriteConfig.getSocketOptions());

    // add query Options
    if (readWriteConfig.getQueryOptions() != null)
      applyQueryOptions(clusterInfo, readWriteConfig.getQueryOptions());

    // add speculativeExecutionPolicy
    if (readWriteConfig.getSpeculativeExecutionPolicy() != null
        && !readWriteConfig.getSpeculativeExecutionPolicy().isBlank())
      clusterInfo.setSpeculativeExecutionPolicyClassName(
          readWriteConfig.getSpeculativeExecutionPolicy());

    // add TimestampGenerator
    if (readWriteConfig.getTimestampGenerator() != null
        && !readWriteConfig.getTimestampGenerator().isBlank())
      clusterInfo.setTimestampGeneratorClassName(readWriteConfig.getTimestampGenerator());

    logger.debug("Created DataSourceInfo: " + clusterInfo);
    return clusterInfo;
  }

  /**
   * Applies the pooling options onto the ClusterDataSourceInfo. Driver 4.x has no core/max-per-host
   * distinction, connection-request-threshold, or pool-checkout-timeout equivalent, so
   * connectionsPerHost/coreConnectionsPerHost/maxRequestsPerConnection/newConnectionThreshold/
   * poolTimeoutMillis have no target and are dropped with a debug log.
   */
  private void applyPoolingOptions(ClusterDataSourceInfo clusterInfo, CtPoolingOptions options) {
    if (options.getConnectionsPerHost() != null) {
      CtConnectionsPerHost cph = options.getConnectionsPerHost();
      if ("LOCAL".equalsIgnoreCase(cph.getDistance().toString()))
        clusterInfo.setConnectionPoolLocalSize(cph.getMax());
      else if ("REMOTE".equalsIgnoreCase(cph.getDistance().toString()))
        clusterInfo.setConnectionPoolRemoteSize(cph.getMax());
    }

    if (options.getHeartbeatIntervalSeconds() != null)
      clusterInfo.setHeartbeatIntervalSeconds(options.getHeartbeatIntervalSeconds());

    if (options.getCoreConnectionsPerHost() != null
        || options.getMaxConnectionsPerHost() != null
        || options.getMaxRequestsPerConnection() != null
        || options.getNewConnectionThreshold() != null
        || options.getIdleTimeoutSeconds() != null
        || options.getPoolTimeoutMillis() != null)
      logger.debug(
          "coreConnectionsPerHost/maxConnectionsPerHost/maxRequestsPerConnection/"
              + "newConnectionThreshold/idleTimeoutSeconds/poolTimeoutMillis have no driver 4.x"
              + " equivalent; ignoring.");
  }

  /**
   * Applies the socket options onto the ClusterDataSourceInfo. Driver 4.x's read-timeout equivalent
   * is the global request timeout (not a socket option), and
   * keepAlive/receiveBufferSize/reuseAddress/sendBufferSize/soLinger have no confirmed driver 4.x
   * config equivalent, so they are dropped with a debug log.
   */
  private void applySocketOptions(ClusterDataSourceInfo clusterInfo, CtSocketOptions options) {
    if (options.getConnectionTimeoutMillis() != null)
      clusterInfo.setConnectTimeoutMillis(options.getConnectionTimeoutMillis());

    if (options.isTcpNoDelay() != null) clusterInfo.setTcpNoDelay(options.isTcpNoDelay());

    if (options.getReadTimeoutMillis() != null
        || options.isKeepAlive() != null
        || options.getReceiveBufferSize() != null
        || options.isReuseAddress() != null
        || options.getSendBufferSize() != null
        || options.getSoLinger() != null)
      logger.debug(
          "readTimeoutMillis/keepAlive/receiveBufferSize/reuseAddress/sendBufferSize/soLinger"
              + " have no driver 4.x equivalent; ignoring.");
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
