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

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.CqlSessionBuilder;
import com.datastax.oss.driver.api.core.addresstranslation.AddressTranslator;
import com.datastax.oss.driver.api.core.auth.AuthProvider;
import com.datastax.oss.driver.api.core.config.DefaultDriverOption;
import com.datastax.oss.driver.api.core.config.DriverConfigLoader;
import com.datastax.oss.driver.api.core.config.ProgrammaticDriverConfigLoaderBuilder;
import com.datastax.oss.driver.api.core.connection.ReconnectionPolicy;
import com.datastax.oss.driver.api.core.loadbalancing.LoadBalancingPolicy;
import com.datastax.oss.driver.api.core.metadata.NodeStateListener;
import com.datastax.oss.driver.api.core.metadata.schema.SchemaChangeListener;
import com.datastax.oss.driver.api.core.retry.RetryPolicy;
import com.datastax.oss.driver.api.core.specex.SpeculativeExecutionPolicy;
import com.datastax.oss.driver.api.core.ssl.SslEngineFactory;
import com.datastax.oss.driver.api.core.time.TimestampGenerator;
import com.datastax.oss.driver.api.core.tracker.RequestTracker;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.AbstractDataSourceInfo;
import org.synchronoss.cpo.core.CpoException;

/**
 * Contains the information needed to connect to a Cassandra cluster
 *
 * @author dberry
 */
public class ClusterDataSourceInfo extends AbstractDataSourceInfo<ClusterDataSource> {
  private static final Logger logger = LoggerFactory.getLogger(ClusterDataSourceInfo.class);

  private final List<String> contactPoints;
  private final String keySpace;
  private String clusterName;
  private String localDatacenter;
  private String applicationName;
  private String applicationVersion;
  private Integer maxSchemaAgreementWaitSeconds;
  private Long schemaAgreementIntervalMillis;
  private Boolean schemaAgreementWarnOnFailure;
  private Long controlConnectionTimeoutMillis;
  private Integer port;
  private Long requestTimeoutMillis;
  private String protocolVersion;
  private Long protocolMaxFrameLengthBytes;
  private String addressTranslatorClassName;
  private String addressTranslatorAdvertisedHostname;
  private String addressTranslatorDefaultAddress;
  private Boolean addressTranslatorResolveAddresses;
  private Map<String, String> addressTranslatorSubnetAddresses;
  private String loadBalancingPolicyClassName;
  private Boolean loadBalancingSlowReplicaAvoidance;
  private String loadBalancingDistanceEvaluatorClassName;
  private Integer dcFailoverMaxNodesPerRemoteDc;
  private Boolean dcFailoverAllowForLocalConsistencyLevels;
  private List<String> dcFailoverPreferredRemoteDcs;
  private String reconnectionPolicyClassName;
  private Long reconnectionBaseDelayMillis;
  private Long reconnectionMaxDelayMillis;
  private Boolean reconnectOnInit;
  private String retryPolicyClassName;
  private boolean credentials;
  private String userName;
  private String password;
  private AuthProvider authProvider;
  private String compressionType;
  private SslEngineFactory sslEngineFactory;
  private List<String> sslCipherSuites;
  private Boolean sslHostnameValidation;
  private Boolean sslAllowDnsReverseLookupSan;
  private String sslTruststorePath;
  private String sslTruststorePassword;
  private String sslKeystorePath;
  private String sslKeystorePassword;
  private Long sslKeystoreReloadIntervalMinutes;
  private Collection<NodeStateListener> listeners;
  private Collection<SchemaChangeListener> schemaChangeListeners;
  private String metricsFactoryClassName;
  private String metricsIdGeneratorClassName;
  private String metricsIdGeneratorPrefix;
  private Boolean metricsGenerateAggregableHistograms;
  private List<String> metricsSessionEnabled;
  private List<String> metricsNodeEnabled;
  private Long metricsNodeExpireAfterMinutes;
  private HistogramOptions metricsSessionCqlRequests;
  private HistogramOptions metricsSessionThrottlingDelay;
  private HistogramOptions metricsNodeCqlMessages;
  private Integer connectionPoolLocalSize;
  private Integer connectionPoolRemoteSize;
  private Integer heartbeatIntervalSeconds;
  private Integer heartbeatTimeoutSeconds;
  private Long connectInitQueryTimeoutMillis;
  private Long setKeyspaceTimeoutMillis;
  private Integer maxRequestsPerConnection;
  private Integer maxOrphanRequests;
  private Boolean warnOnInitError;
  private Integer connectTimeoutMillis;
  private Boolean tcpNoDelay;
  private Boolean socketKeepAlive;
  private Boolean socketReuseAddress;
  private Integer socketLingerIntervalSeconds;
  private Integer socketReceiveBufferSize;
  private Integer socketSendBufferSize;
  private String consistencyLevel;
  private String serialConsistencyLevel;
  private Boolean defaultIdempotence;
  private Integer pageSize;
  private String speculativeExecutionPolicyClassName;
  private Integer speculativeExecutionMaxExecutions;
  private Long speculativeExecutionDelayMillis;
  private String timestampGeneratorClassName;
  private Boolean timestampGeneratorForceJavaClock;
  private Long timestampGeneratorDriftWarningThresholdMillis;
  private Long timestampGeneratorDriftWarningIntervalSeconds;
  private Boolean requestWarnIfSetKeyspace;
  private Boolean requestLogWarnings;
  private Integer requestTraceAttempts;
  private Long requestTraceIntervalMillis;
  private String requestTraceConsistencyLevel;
  private List<String> requestTrackerClasses;
  private Boolean requestLoggerSuccessEnabled;
  private Long requestLoggerSlowThresholdMillis;
  private Boolean requestLoggerSlowEnabled;
  private Boolean requestLoggerErrorEnabled;
  private Integer requestLoggerMaxQueryLength;
  private Boolean requestLoggerShowValues;
  private Integer requestLoggerMaxValueLength;
  private Integer requestLoggerMaxValues;
  private Boolean requestLoggerShowStackTraces;
  private String throttlerClassName;
  private Integer throttlerMaxQueueSize;
  private Integer throttlerMaxConcurrentRequests;
  private Integer throttlerMaxRequestsPerSecond;
  private Long throttlerDrainIntervalMillis;
  private Boolean metadataSchemaEnabled;
  private List<String> metadataSchemaRefreshedKeyspaces;
  private Long metadataSchemaRequestTimeoutMillis;
  private Integer metadataSchemaRequestPageSize;
  private Long metadataSchemaWindowMillis;
  private Integer metadataSchemaMaxEvents;
  private Long metadataTopologyWindowMillis;
  private Integer metadataTopologyMaxEvents;
  private Boolean metadataTokenMapEnabled;
  private Boolean prepareOnAllNodes;
  private Boolean reprepareEnabled;
  private Boolean reprepareCheckSystemTable;
  private Integer reprepareMaxStatements;
  private Integer reprepareMaxParallelism;
  private Long reprepareTimeoutMillis;
  private Boolean preparedCacheWeakValues;
  private Boolean nettyDaemonThreads;
  private Integer nettyIoGroupSize;
  private Integer nettyIoGroupShutdownQuietPeriodSeconds;
  private Integer nettyIoGroupShutdownTimeoutSeconds;
  private Integer nettyAdminGroupSize;
  private Integer nettyAdminGroupShutdownQuietPeriodSeconds;
  private Integer nettyAdminGroupShutdownTimeoutSeconds;
  private Long nettyTimerTickDurationMillis;
  private Integer nettyTimerTicksPerWheel;
  private Long coalescerIntervalMicros;
  private Integer coalescerMaxRuns;
  private Integer sessionLeakThreshold;
  private Boolean resolveContactPoints;

  /**
   * Constructs a ClusterDataSourceInfo
   *
   * @param clusterName The cluster name
   * @param keySpace The keyspace
   * @param contactPoints The contact points
   * @param fetchSize the number of rows to fetch per round trip on a retrieve
   * @param batchSize the number of statements to batch together before executing
   */
  public ClusterDataSourceInfo(
      String clusterName,
      String keySpace,
      List<String> contactPoints,
      int fetchSize,
      int batchSize) {
    super(buildDataSourceName(clusterName, keySpace, contactPoints), fetchSize, batchSize);
    this.keySpace = keySpace;
    this.clusterName = clusterName;
    this.contactPoints = contactPoints;
  }

  /**
   * Get the MaxSchemaAgreementWaitSeconds setting
   *
   * @return The MaxSchemaAgreementWaitSeconds
   */
  public Integer getMaxSchemaAgreementWaitSeconds() {
    return maxSchemaAgreementWaitSeconds;
  }

  /**
   * Set the MaxSchemaAgreementWaitSeconds setting
   *
   * @param maxSchemaAgreementWaitSeconds The MaxSchemaAgreementWaitSeconds
   */
  public void setMaxSchemaAgreementWaitSeconds(Integer maxSchemaAgreementWaitSeconds) {
    this.maxSchemaAgreementWaitSeconds = maxSchemaAgreementWaitSeconds;
  }

  /**
   * Get the schema agreement check interval, in milliseconds
   *
   * @return the schema agreement check interval in milliseconds
   */
  public Long getSchemaAgreementIntervalMillis() {
    return schemaAgreementIntervalMillis;
  }

  /**
   * Set the schema agreement check interval, in milliseconds
   *
   * @param schemaAgreementIntervalMillis the schema agreement check interval in milliseconds
   */
  public void setSchemaAgreementIntervalMillis(Long schemaAgreementIntervalMillis) {
    this.schemaAgreementIntervalMillis = schemaAgreementIntervalMillis;
  }

  /**
   * Get whether a warning is logged if schema agreement fails
   *
   * @return the schemaAgreementWarnOnFailure flag
   */
  public Boolean getSchemaAgreementWarnOnFailure() {
    return schemaAgreementWarnOnFailure;
  }

  /**
   * Set whether a warning is logged if schema agreement fails
   *
   * @param schemaAgreementWarnOnFailure the schemaAgreementWarnOnFailure flag
   */
  public void setSchemaAgreementWarnOnFailure(Boolean schemaAgreementWarnOnFailure) {
    this.schemaAgreementWarnOnFailure = schemaAgreementWarnOnFailure;
  }

  /**
   * Get the control connection timeout, in milliseconds
   *
   * @return the control connection timeout in milliseconds
   */
  public Long getControlConnectionTimeoutMillis() {
    return controlConnectionTimeoutMillis;
  }

  /**
   * Set the control connection timeout, in milliseconds
   *
   * @param controlConnectionTimeoutMillis the control connection timeout in milliseconds
   */
  public void setControlConnectionTimeoutMillis(Long controlConnectionTimeoutMillis) {
    this.controlConnectionTimeoutMillis = controlConnectionTimeoutMillis;
  }

  /**
   * Get the AddressTranslator implementation class name
   *
   * @return The fully qualified AddressTranslator implementation class name
   */
  public String getAddressTranslatorClassName() {
    return addressTranslatorClassName;
  }

  /**
   * Set the AddressTranslator implementation class name. The driver instantiates this class itself
   * via a (DriverContext) constructor.
   *
   * @param addressTranslatorClassName The fully qualified AddressTranslator implementation class
   *     name
   */
  public void setAddressTranslatorClassName(String addressTranslatorClassName) {
    this.addressTranslatorClassName = addressTranslatorClassName;
  }

  /**
   * Get the advertised hostname used by FixedHostNameAddressTranslator
   *
   * @return the advertised hostname
   */
  public String getAddressTranslatorAdvertisedHostname() {
    return addressTranslatorAdvertisedHostname;
  }

  /**
   * Set the advertised hostname used by FixedHostNameAddressTranslator
   *
   * @param addressTranslatorAdvertisedHostname the advertised hostname
   */
  public void setAddressTranslatorAdvertisedHostname(String addressTranslatorAdvertisedHostname) {
    this.addressTranslatorAdvertisedHostname = addressTranslatorAdvertisedHostname;
  }

  /**
   * Get the default address used by SubnetAddressTranslator when no subnet matches
   *
   * @return the default address
   */
  public String getAddressTranslatorDefaultAddress() {
    return addressTranslatorDefaultAddress;
  }

  /**
   * Set the default address used by SubnetAddressTranslator when no subnet matches
   *
   * @param addressTranslatorDefaultAddress the default address
   */
  public void setAddressTranslatorDefaultAddress(String addressTranslatorDefaultAddress) {
    this.addressTranslatorDefaultAddress = addressTranslatorDefaultAddress;
  }

  /**
   * Get whether addresses are resolved once at initialization (true) or on each reconnection
   * (false)
   *
   * @return the addressTranslatorResolveAddresses flag
   */
  public Boolean getAddressTranslatorResolveAddresses() {
    return addressTranslatorResolveAddresses;
  }

  /**
   * Set whether addresses are resolved once at initialization (true) or on each reconnection
   * (false)
   *
   * @param addressTranslatorResolveAddresses the addressTranslatorResolveAddresses flag
   */
  public void setAddressTranslatorResolveAddresses(Boolean addressTranslatorResolveAddresses) {
    this.addressTranslatorResolveAddresses = addressTranslatorResolveAddresses;
  }

  /**
   * Get the CIDR-to-"host:port" map used by SubnetAddressTranslator
   *
   * @return the subnet address map
   */
  public Map<String, String> getAddressTranslatorSubnetAddresses() {
    return addressTranslatorSubnetAddresses;
  }

  /**
   * Set the CIDR-to-"host:port" map used by SubnetAddressTranslator
   *
   * @param addressTranslatorSubnetAddresses the subnet address map
   */
  public void setAddressTranslatorSubnetAddresses(
      Map<String, String> addressTranslatorSubnetAddresses) {
    this.addressTranslatorSubnetAddresses = addressTranslatorSubnetAddresses;
  }

  /**
   * Get the cluster name
   *
   * @return The cluster name
   */
  public String getClusterName() {
    return clusterName;
  }

  /**
   * Set the cluster name. Wired to the driver's session name (basic.session-name / SESSION_NAME),
   * used as a log/metrics prefix, in addition to being used for the CPO datasource display name.
   *
   * @param clusterName The cluster name
   */
  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

  /**
   * Get the keyspace name
   *
   * @return The keyspace name
   */
  public String getKeySpace() {
    return keySpace;
  }

  /**
   * Get the local datacenter, required by the driver's default load balancing policy whenever
   * contact points are supplied.
   *
   * @return The local datacenter name
   */
  public String getLocalDatacenter() {
    return localDatacenter;
  }

  /**
   * Set the local datacenter
   *
   * @param localDatacenter The local datacenter name
   */
  public void setLocalDatacenter(String localDatacenter) {
    this.localDatacenter = localDatacenter;
  }

  /**
   * Get the application name sent in the STARTUP protocol message
   *
   * @return the application name
   */
  public String getApplicationName() {
    return applicationName;
  }

  /**
   * Set the application name sent in the STARTUP protocol message
   *
   * @param applicationName the application name
   */
  public void setApplicationName(String applicationName) {
    this.applicationName = applicationName;
  }

  /**
   * Get the application version sent in the STARTUP protocol message
   *
   * @return the application version
   */
  public String getApplicationVersion() {
    return applicationVersion;
  }

  /**
   * Set the application version sent in the STARTUP protocol message
   *
   * @param applicationVersion the application version
   */
  public void setApplicationVersion(String applicationVersion) {
    this.applicationVersion = applicationVersion;
  }

  /**
   * Get the port number
   *
   * @return The port number
   */
  public int getPort() {
    return port;
  }

  /**
   * Set the port number
   *
   * @param port The port number
   */
  public void setPort(int port) {
    this.port = port;
  }

  /**
   * Get the global request timeout, in milliseconds
   *
   * @return the request timeout in milliseconds
   */
  public Long getRequestTimeoutMillis() {
    return requestTimeoutMillis;
  }

  /**
   * Set the global request timeout, in milliseconds
   *
   * @param requestTimeoutMillis the request timeout in milliseconds
   */
  public void setRequestTimeoutMillis(Long requestTimeoutMillis) {
    this.requestTimeoutMillis = requestTimeoutMillis;
  }

  /**
   * Get the LoadBalancingPolicy implementation class name
   *
   * @return The fully qualified LoadBalancingPolicy implementation class name
   */
  public String getLoadBalancingPolicyClassName() {
    return loadBalancingPolicyClassName;
  }

  /**
   * Set the LoadBalancingPolicy implementation class name. The driver instantiates this class
   * itself via a (DriverContext, String profileName) constructor.
   *
   * @param loadBalancingPolicyClassName The fully qualified LoadBalancingPolicy implementation
   *     class name
   */
  public void setLoadBalancingPolicyClassName(String loadBalancingPolicyClassName) {
    this.loadBalancingPolicyClassName = loadBalancingPolicyClassName;
  }

  /**
   * Get whether slow-replica avoidance is enabled on the default load balancing policy
   *
   * @return the loadBalancingSlowReplicaAvoidance flag
   */
  public Boolean getLoadBalancingSlowReplicaAvoidance() {
    return loadBalancingSlowReplicaAvoidance;
  }

  /**
   * Set whether slow-replica avoidance is enabled on the default load balancing policy
   *
   * @param loadBalancingSlowReplicaAvoidance the loadBalancingSlowReplicaAvoidance flag
   */
  public void setLoadBalancingSlowReplicaAvoidance(Boolean loadBalancingSlowReplicaAvoidance) {
    this.loadBalancingSlowReplicaAvoidance = loadBalancingSlowReplicaAvoidance;
  }

  /**
   * Get the custom NodeDistanceEvaluator implementation class name
   *
   * @return the fully qualified NodeDistanceEvaluator implementation class name
   */
  public String getLoadBalancingDistanceEvaluatorClassName() {
    return loadBalancingDistanceEvaluatorClassName;
  }

  /**
   * Set the custom NodeDistanceEvaluator implementation class name. The driver instantiates this
   * class itself via a (DriverContext, String profileName) constructor.
   *
   * @param loadBalancingDistanceEvaluatorClassName the fully qualified NodeDistanceEvaluator
   *     implementation class name
   */
  public void setLoadBalancingDistanceEvaluatorClassName(
      String loadBalancingDistanceEvaluatorClassName) {
    this.loadBalancingDistanceEvaluatorClassName = loadBalancingDistanceEvaluatorClassName;
  }

  /**
   * Get the maximum number of nodes to contact in each remote datacenter for cross-DC failover
   *
   * @return the max nodes per remote datacenter
   */
  public Integer getDcFailoverMaxNodesPerRemoteDc() {
    return dcFailoverMaxNodesPerRemoteDc;
  }

  /**
   * Set the maximum number of nodes to contact in each remote datacenter for cross-DC failover
   *
   * @param dcFailoverMaxNodesPerRemoteDc the max nodes per remote datacenter
   */
  public void setDcFailoverMaxNodesPerRemoteDc(Integer dcFailoverMaxNodesPerRemoteDc) {
    this.dcFailoverMaxNodesPerRemoteDc = dcFailoverMaxNodesPerRemoteDc;
  }

  /**
   * Get whether cross-DC failover is allowed for local consistency levels
   *
   * @return the dcFailoverAllowForLocalConsistencyLevels flag
   */
  public Boolean getDcFailoverAllowForLocalConsistencyLevels() {
    return dcFailoverAllowForLocalConsistencyLevels;
  }

  /**
   * Set whether cross-DC failover is allowed for local consistency levels
   *
   * @param dcFailoverAllowForLocalConsistencyLevels the dcFailoverAllowForLocalConsistencyLevels
   *     flag
   */
  public void setDcFailoverAllowForLocalConsistencyLevels(
      Boolean dcFailoverAllowForLocalConsistencyLevels) {
    this.dcFailoverAllowForLocalConsistencyLevels = dcFailoverAllowForLocalConsistencyLevels;
  }

  /**
   * Get the ordered list of preferred remote datacenters for cross-DC failover
   *
   * @return the preferred remote datacenters
   */
  public List<String> getDcFailoverPreferredRemoteDcs() {
    return dcFailoverPreferredRemoteDcs;
  }

  /**
   * Set the ordered list of preferred remote datacenters for cross-DC failover
   *
   * @param dcFailoverPreferredRemoteDcs the preferred remote datacenters
   */
  public void setDcFailoverPreferredRemoteDcs(List<String> dcFailoverPreferredRemoteDcs) {
    this.dcFailoverPreferredRemoteDcs = dcFailoverPreferredRemoteDcs;
  }

  /**
   * Get the ReconnectionPolicy implementation class name
   *
   * @return The fully qualified ReconnectionPolicy implementation class name
   */
  public String getReconnectionPolicyClassName() {
    return reconnectionPolicyClassName;
  }

  /**
   * Set the ReconnectionPolicy implementation class name. The driver instantiates this class itself
   * via a (DriverContext) constructor.
   *
   * @param reconnectionPolicyClassName The fully qualified ReconnectionPolicy implementation class
   *     name
   */
  public void setReconnectionPolicyClassName(String reconnectionPolicyClassName) {
    this.reconnectionPolicyClassName = reconnectionPolicyClassName;
  }

  /**
   * Get the base reconnection delay, in milliseconds
   *
   * @return the base delay in milliseconds
   */
  public Long getReconnectionBaseDelayMillis() {
    return reconnectionBaseDelayMillis;
  }

  /**
   * Set the base reconnection delay, in milliseconds
   *
   * @param reconnectionBaseDelayMillis the base delay in milliseconds
   */
  public void setReconnectionBaseDelayMillis(Long reconnectionBaseDelayMillis) {
    this.reconnectionBaseDelayMillis = reconnectionBaseDelayMillis;
  }

  /**
   * Get the maximum reconnection delay, in milliseconds
   *
   * @return the max delay in milliseconds
   */
  public Long getReconnectionMaxDelayMillis() {
    return reconnectionMaxDelayMillis;
  }

  /**
   * Set the maximum reconnection delay, in milliseconds
   *
   * @param reconnectionMaxDelayMillis the max delay in milliseconds
   */
  public void setReconnectionMaxDelayMillis(Long reconnectionMaxDelayMillis) {
    this.reconnectionMaxDelayMillis = reconnectionMaxDelayMillis;
  }

  /**
   * Get whether reconnection is attempted if all contact points are unreachable on first init
   *
   * @return the reconnectOnInit flag
   */
  public Boolean getReconnectOnInit() {
    return reconnectOnInit;
  }

  /**
   * Set whether reconnection is attempted if all contact points are unreachable on first init
   *
   * @param reconnectOnInit the reconnectOnInit flag
   */
  public void setReconnectOnInit(Boolean reconnectOnInit) {
    this.reconnectOnInit = reconnectOnInit;
  }

  /**
   * Get the RetryPolicy implementation class name
   *
   * @return The fully qualified RetryPolicy implementation class name
   */
  public String getRetryPolicyClassName() {
    return retryPolicyClassName;
  }

  /**
   * Set the RetryPolicy implementation class name. The driver instantiates this class itself via a
   * (DriverContext, String profileName) constructor.
   *
   * @param retryPolicyClassName The fully qualified RetryPolicy implementation class name
   */
  public void setRetryPolicyClassName(String retryPolicyClassName) {
    this.retryPolicyClassName = retryPolicyClassName;
  }

  /**
   * Has Credentials
   *
   * @return hasCredentials
   */
  public boolean hasCredentials() {
    return credentials;
  }

  /**
   * Set hasCredentials
   *
   * @param credentials hasCredentials
   */
  public void setHasCredentials(boolean credentials) {
    this.credentials = credentials;
  }

  /**
   * Get the username
   *
   * @return The username
   */
  public String getUserName() {
    return userName;
  }

  /**
   * Set the username
   *
   * @param userName The username
   */
  public void setUserName(String userName) {
    this.userName = userName;
  }

  /**
   * Get the password
   *
   * @return The password
   */
  public String getPassword() {
    return password;
  }

  /**
   * Set the password
   *
   * @param password - The password
   */
  public void setPassword(String password) {
    this.password = password;
  }

  /**
   * Get the AuthProvider
   *
   * @return - The AuthProvider
   */
  public AuthProvider getAuthProvider() {
    return authProvider;
  }

  /**
   * Set the AuthProvider
   *
   * @param authProvider - The AuthProvider
   */
  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

  /**
   * Get the compression type
   *
   * @return - the protocol compression name ("lz4", "snappy", or "none")
   */
  public String getCompressionType() {
    return compressionType;
  }

  /**
   * Set the compression type
   *
   * @param compressionType - the protocol compression name ("lz4", "snappy", or "none")
   */
  public void setCompressionType(String compressionType) {
    this.compressionType = compressionType;
  }

  /**
   * Get the protocol maximum frame length, in bytes
   *
   * @return the max frame length in bytes
   */
  public Long getProtocolMaxFrameLengthBytes() {
    return protocolMaxFrameLengthBytes;
  }

  /**
   * Set the protocol maximum frame length, in bytes
   *
   * @param protocolMaxFrameLengthBytes the max frame length in bytes
   */
  public void setProtocolMaxFrameLengthBytes(Long protocolMaxFrameLengthBytes) {
    this.protocolMaxFrameLengthBytes = protocolMaxFrameLengthBytes;
  }

  /**
   * Get the SslEngineFactory
   *
   * @return - The SslEngineFactory
   */
  public SslEngineFactory getSslEngineFactory() {
    return sslEngineFactory;
  }

  /**
   * Set the SslEngineFactory
   *
   * @param sslEngineFactory - The SslEngineFactory
   */
  public void setSslEngineFactory(SslEngineFactory sslEngineFactory) {
    this.sslEngineFactory = sslEngineFactory;
  }

  /**
   * Get the cipher suites enabled for the default SSL engine factory
   *
   * @return the cipher suites
   */
  public List<String> getSslCipherSuites() {
    return sslCipherSuites;
  }

  /**
   * Set the cipher suites enabled for the default SSL engine factory
   *
   * @param sslCipherSuites the cipher suites
   */
  public void setSslCipherSuites(List<String> sslCipherSuites) {
    this.sslCipherSuites = sslCipherSuites;
  }

  /**
   * Get whether server certificate hostname validation is enabled
   *
   * @return the sslHostnameValidation flag
   */
  public Boolean getSslHostnameValidation() {
    return sslHostnameValidation;
  }

  /**
   * Set whether server certificate hostname validation is enabled
   *
   * @param sslHostnameValidation the sslHostnameValidation flag
   */
  public void setSslHostnameValidation(Boolean sslHostnameValidation) {
    this.sslHostnameValidation = sslHostnameValidation;
  }

  /**
   * Get whether a DNS reverse lookup is allowed for SAN addresses
   *
   * @return the sslAllowDnsReverseLookupSan flag
   */
  public Boolean getSslAllowDnsReverseLookupSan() {
    return sslAllowDnsReverseLookupSan;
  }

  /**
   * Set whether a DNS reverse lookup is allowed for SAN addresses
   *
   * @param sslAllowDnsReverseLookupSan the sslAllowDnsReverseLookupSan flag
   */
  public void setSslAllowDnsReverseLookupSan(Boolean sslAllowDnsReverseLookupSan) {
    this.sslAllowDnsReverseLookupSan = sslAllowDnsReverseLookupSan;
  }

  /**
   * Get the truststore path
   *
   * @return the truststore path
   */
  public String getSslTruststorePath() {
    return sslTruststorePath;
  }

  /**
   * Set the truststore path
   *
   * @param sslTruststorePath the truststore path
   */
  public void setSslTruststorePath(String sslTruststorePath) {
    this.sslTruststorePath = sslTruststorePath;
  }

  /**
   * Get the truststore password
   *
   * @return the truststore password
   */
  public String getSslTruststorePassword() {
    return sslTruststorePassword;
  }

  /**
   * Set the truststore password
   *
   * @param sslTruststorePassword the truststore password
   */
  public void setSslTruststorePassword(String sslTruststorePassword) {
    this.sslTruststorePassword = sslTruststorePassword;
  }

  /**
   * Get the keystore path
   *
   * @return the keystore path
   */
  public String getSslKeystorePath() {
    return sslKeystorePath;
  }

  /**
   * Set the keystore path
   *
   * @param sslKeystorePath the keystore path
   */
  public void setSslKeystorePath(String sslKeystorePath) {
    this.sslKeystorePath = sslKeystorePath;
  }

  /**
   * Get the keystore password
   *
   * @return the keystore password
   */
  public String getSslKeystorePassword() {
    return sslKeystorePassword;
  }

  /**
   * Set the keystore password
   *
   * @param sslKeystorePassword the keystore password
   */
  public void setSslKeystorePassword(String sslKeystorePassword) {
    this.sslKeystorePassword = sslKeystorePassword;
  }

  /**
   * Get the keystore reload interval, in minutes
   *
   * @return the keystore reload interval in minutes
   */
  public Long getSslKeystoreReloadIntervalMinutes() {
    return sslKeystoreReloadIntervalMinutes;
  }

  /**
   * Set the keystore reload interval, in minutes
   *
   * @param sslKeystoreReloadIntervalMinutes the keystore reload interval in minutes
   */
  public void setSslKeystoreReloadIntervalMinutes(Long sslKeystoreReloadIntervalMinutes) {
    this.sslKeystoreReloadIntervalMinutes = sslKeystoreReloadIntervalMinutes;
  }

  /**
   * Get the NodeStateListeners
   *
   * @return - A collection of NodeStateListeners
   */
  public Collection<NodeStateListener> getListeners() {
    return listeners;
  }

  /**
   * Set the NodeStateListeners
   *
   * @param listeners - A collection of NodeStateListeners
   */
  public void setListeners(Collection<NodeStateListener> listeners) {
    this.listeners = listeners;
  }

  /**
   * Get the SchemaChangeListeners
   *
   * @return a collection of SchemaChangeListeners
   */
  public Collection<SchemaChangeListener> getSchemaChangeListeners() {
    return schemaChangeListeners;
  }

  /**
   * Set the SchemaChangeListeners
   *
   * @param schemaChangeListeners a collection of SchemaChangeListeners
   */
  public void setSchemaChangeListeners(Collection<SchemaChangeListener> schemaChangeListeners) {
    this.schemaChangeListeners = schemaChangeListeners;
  }

  /**
   * Get the metrics factory class name
   *
   * @return the metrics factory class name
   */
  public String getMetricsFactoryClassName() {
    return metricsFactoryClassName;
  }

  /**
   * Set the metrics factory class name (e.g. "DefaultMetricsFactory", "MicrometerMetricsFactory",
   * "NoopMetricsFactory", or a fully qualified custom class name)
   *
   * @param metricsFactoryClassName the metrics factory class name
   */
  public void setMetricsFactoryClassName(String metricsFactoryClassName) {
    this.metricsFactoryClassName = metricsFactoryClassName;
  }

  /**
   * Get the metric id generator class name
   *
   * @return the metric id generator class name
   */
  public String getMetricsIdGeneratorClassName() {
    return metricsIdGeneratorClassName;
  }

  /**
   * Set the metric id generator class name (e.g. "DefaultMetricIdGenerator" or
   * "TaggingMetricIdGenerator")
   *
   * @param metricsIdGeneratorClassName the metric id generator class name
   */
  public void setMetricsIdGeneratorClassName(String metricsIdGeneratorClassName) {
    this.metricsIdGeneratorClassName = metricsIdGeneratorClassName;
  }

  /**
   * Get the prefix prepended to every generated metric name
   *
   * @return the metric name prefix
   */
  public String getMetricsIdGeneratorPrefix() {
    return metricsIdGeneratorPrefix;
  }

  /**
   * Set the prefix prepended to every generated metric name
   *
   * @param metricsIdGeneratorPrefix the metric name prefix
   */
  public void setMetricsIdGeneratorPrefix(String metricsIdGeneratorPrefix) {
    this.metricsIdGeneratorPrefix = metricsIdGeneratorPrefix;
  }

  /**
   * Get whether aggregable histogram buckets are generated for monitoring systems
   *
   * @return the metricsGenerateAggregableHistograms flag
   */
  public Boolean getMetricsGenerateAggregableHistograms() {
    return metricsGenerateAggregableHistograms;
  }

  /**
   * Set whether aggregable histogram buckets are generated for monitoring systems
   *
   * @param metricsGenerateAggregableHistograms the metricsGenerateAggregableHistograms flag
   */
  public void setMetricsGenerateAggregableHistograms(Boolean metricsGenerateAggregableHistograms) {
    this.metricsGenerateAggregableHistograms = metricsGenerateAggregableHistograms;
  }

  /**
   * Get the session-level metric names to enable
   *
   * @return the enabled session metric names
   */
  public List<String> getMetricsSessionEnabled() {
    return metricsSessionEnabled;
  }

  /**
   * Set the session-level metric names to enable
   *
   * @param metricsSessionEnabled the enabled session metric names
   */
  public void setMetricsSessionEnabled(List<String> metricsSessionEnabled) {
    this.metricsSessionEnabled = metricsSessionEnabled;
  }

  /**
   * Get the node-level metric names to enable
   *
   * @return the enabled node metric names
   */
  public List<String> getMetricsNodeEnabled() {
    return metricsNodeEnabled;
  }

  /**
   * Set the node-level metric names to enable
   *
   * @param metricsNodeEnabled the enabled node metric names
   */
  public void setMetricsNodeEnabled(List<String> metricsNodeEnabled) {
    this.metricsNodeEnabled = metricsNodeEnabled;
  }

  /**
   * Get how long a node's metrics are kept registered after it leaves the cluster, in minutes
   *
   * @return the node metric expiry in minutes
   */
  public Long getMetricsNodeExpireAfterMinutes() {
    return metricsNodeExpireAfterMinutes;
  }

  /**
   * Set how long a node's metrics are kept registered after it leaves the cluster, in minutes
   *
   * @param metricsNodeExpireAfterMinutes the node metric expiry in minutes
   */
  public void setMetricsNodeExpireAfterMinutes(Long metricsNodeExpireAfterMinutes) {
    this.metricsNodeExpireAfterMinutes = metricsNodeExpireAfterMinutes;
  }

  /**
   * Get the histogram tuning for the session "cql-requests" metric
   *
   * @return the histogram options
   */
  public HistogramOptions getMetricsSessionCqlRequests() {
    return metricsSessionCqlRequests;
  }

  /**
   * Set the histogram tuning for the session "cql-requests" metric
   *
   * @param metricsSessionCqlRequests the histogram options
   */
  public void setMetricsSessionCqlRequests(HistogramOptions metricsSessionCqlRequests) {
    this.metricsSessionCqlRequests = metricsSessionCqlRequests;
  }

  /**
   * Get the histogram tuning for the session "throttling.delay" metric
   *
   * @return the histogram options
   */
  public HistogramOptions getMetricsSessionThrottlingDelay() {
    return metricsSessionThrottlingDelay;
  }

  /**
   * Set the histogram tuning for the session "throttling.delay" metric
   *
   * @param metricsSessionThrottlingDelay the histogram options
   */
  public void setMetricsSessionThrottlingDelay(HistogramOptions metricsSessionThrottlingDelay) {
    this.metricsSessionThrottlingDelay = metricsSessionThrottlingDelay;
  }

  /**
   * Get the histogram tuning for the node "cql-messages" metric
   *
   * @return the histogram options
   */
  public HistogramOptions getMetricsNodeCqlMessages() {
    return metricsNodeCqlMessages;
  }

  /**
   * Set the histogram tuning for the node "cql-messages" metric
   *
   * @param metricsNodeCqlMessages the histogram options
   */
  public void setMetricsNodeCqlMessages(HistogramOptions metricsNodeCqlMessages) {
    this.metricsNodeCqlMessages = metricsNodeCqlMessages;
  }

  /**
   * Get the local connection pool size
   *
   * @return - the local connection pool size
   */
  public Integer getConnectionPoolLocalSize() {
    return connectionPoolLocalSize;
  }

  /**
   * Set the local connection pool size
   *
   * @param connectionPoolLocalSize - the local connection pool size
   */
  public void setConnectionPoolLocalSize(Integer connectionPoolLocalSize) {
    this.connectionPoolLocalSize = connectionPoolLocalSize;
  }

  /**
   * Get the remote connection pool size
   *
   * @return - the remote connection pool size
   */
  public Integer getConnectionPoolRemoteSize() {
    return connectionPoolRemoteSize;
  }

  /**
   * Set the remote connection pool size
   *
   * @param connectionPoolRemoteSize - the remote connection pool size
   */
  public void setConnectionPoolRemoteSize(Integer connectionPoolRemoteSize) {
    this.connectionPoolRemoteSize = connectionPoolRemoteSize;
  }

  /**
   * Get the heartbeat interval, in seconds
   *
   * @return - the heartbeat interval in seconds
   */
  public Integer getHeartbeatIntervalSeconds() {
    return heartbeatIntervalSeconds;
  }

  /**
   * Set the heartbeat interval, in seconds
   *
   * @param heartbeatIntervalSeconds - the heartbeat interval in seconds
   */
  public void setHeartbeatIntervalSeconds(Integer heartbeatIntervalSeconds) {
    this.heartbeatIntervalSeconds = heartbeatIntervalSeconds;
  }

  /**
   * Get the heartbeat response timeout, in seconds
   *
   * @return the heartbeat timeout in seconds
   */
  public Integer getHeartbeatTimeoutSeconds() {
    return heartbeatTimeoutSeconds;
  }

  /**
   * Set the heartbeat response timeout, in seconds
   *
   * @param heartbeatTimeoutSeconds the heartbeat timeout in seconds
   */
  public void setHeartbeatTimeoutSeconds(Integer heartbeatTimeoutSeconds) {
    this.heartbeatTimeoutSeconds = heartbeatTimeoutSeconds;
  }

  /**
   * Get the timeout for internal queries run just after a connection opens, in milliseconds
   *
   * @return the init query timeout in milliseconds
   */
  public Long getConnectInitQueryTimeoutMillis() {
    return connectInitQueryTimeoutMillis;
  }

  /**
   * Set the timeout for internal queries run just after a connection opens, in milliseconds
   *
   * @param connectInitQueryTimeoutMillis the init query timeout in milliseconds
   */
  public void setConnectInitQueryTimeoutMillis(Long connectInitQueryTimeoutMillis) {
    this.connectInitQueryTimeoutMillis = connectInitQueryTimeoutMillis;
  }

  /**
   * Get the timeout used when switching keyspace on a connection at runtime, in milliseconds
   *
   * @return the set-keyspace timeout in milliseconds
   */
  public Long getSetKeyspaceTimeoutMillis() {
    return setKeyspaceTimeoutMillis;
  }

  /**
   * Set the timeout used when switching keyspace on a connection at runtime, in milliseconds
   *
   * @param setKeyspaceTimeoutMillis the set-keyspace timeout in milliseconds
   */
  public void setSetKeyspaceTimeoutMillis(Long setKeyspaceTimeoutMillis) {
    this.setKeyspaceTimeoutMillis = setKeyspaceTimeoutMillis;
  }

  /**
   * Get the maximum number of requests that can execute concurrently on a single connection
   *
   * @return the max requests per connection
   */
  public Integer getMaxRequestsPerConnection() {
    return maxRequestsPerConnection;
  }

  /**
   * Set the maximum number of requests that can execute concurrently on a single connection
   *
   * @param maxRequestsPerConnection the max requests per connection
   */
  public void setMaxRequestsPerConnection(Integer maxRequestsPerConnection) {
    this.maxRequestsPerConnection = maxRequestsPerConnection;
  }

  /**
   * Get the maximum number of orphaned requests before a connection is closed and replaced
   *
   * @return the max orphan requests
   */
  public Integer getMaxOrphanRequests() {
    return maxOrphanRequests;
  }

  /**
   * Set the maximum number of orphaned requests before a connection is closed and replaced
   *
   * @param maxOrphanRequests the max orphan requests
   */
  public void setMaxOrphanRequests(Integer maxOrphanRequests) {
    this.maxOrphanRequests = maxOrphanRequests;
  }

  /**
   * Get whether non-fatal connection-init errors are logged
   *
   * @return the warnOnInitError flag
   */
  public Boolean getWarnOnInitError() {
    return warnOnInitError;
  }

  /**
   * Set whether non-fatal connection-init errors are logged
   *
   * @param warnOnInitError the warnOnInitError flag
   */
  public void setWarnOnInitError(Boolean warnOnInitError) {
    this.warnOnInitError = warnOnInitError;
  }

  /**
   * Get the connect timeout, in milliseconds
   *
   * @return - the connect timeout in milliseconds
   */
  public Integer getConnectTimeoutMillis() {
    return connectTimeoutMillis;
  }

  /**
   * Set the connect timeout, in milliseconds
   *
   * @param connectTimeoutMillis - the connect timeout in milliseconds
   */
  public void setConnectTimeoutMillis(Integer connectTimeoutMillis) {
    this.connectTimeoutMillis = connectTimeoutMillis;
  }

  /**
   * Get the tcpNoDelay flag
   *
   * @return - the tcpNoDelay flag
   */
  public Boolean getTcpNoDelay() {
    return tcpNoDelay;
  }

  /**
   * Set the tcpNoDelay flag
   *
   * @param tcpNoDelay - the tcpNoDelay flag
   */
  public void setTcpNoDelay(Boolean tcpNoDelay) {
    this.tcpNoDelay = tcpNoDelay;
  }

  /**
   * Get the socket keep-alive flag
   *
   * @return the socketKeepAlive flag
   */
  public Boolean getSocketKeepAlive() {
    return socketKeepAlive;
  }

  /**
   * Set the socket keep-alive flag
   *
   * @param socketKeepAlive the socketKeepAlive flag
   */
  public void setSocketKeepAlive(Boolean socketKeepAlive) {
    this.socketKeepAlive = socketKeepAlive;
  }

  /**
   * Get the socket reuse-address flag
   *
   * @return the socketReuseAddress flag
   */
  public Boolean getSocketReuseAddress() {
    return socketReuseAddress;
  }

  /**
   * Set the socket reuse-address flag
   *
   * @param socketReuseAddress the socketReuseAddress flag
   */
  public void setSocketReuseAddress(Boolean socketReuseAddress) {
    this.socketReuseAddress = socketReuseAddress;
  }

  /**
   * Get the socket linger interval, in seconds
   *
   * @return the linger interval in seconds
   */
  public Integer getSocketLingerIntervalSeconds() {
    return socketLingerIntervalSeconds;
  }

  /**
   * Set the socket linger interval, in seconds
   *
   * @param socketLingerIntervalSeconds the linger interval in seconds
   */
  public void setSocketLingerIntervalSeconds(Integer socketLingerIntervalSeconds) {
    this.socketLingerIntervalSeconds = socketLingerIntervalSeconds;
  }

  /**
   * Get the socket receive buffer size hint
   *
   * @return the receive buffer size
   */
  public Integer getSocketReceiveBufferSize() {
    return socketReceiveBufferSize;
  }

  /**
   * Set the socket receive buffer size hint
   *
   * @param socketReceiveBufferSize the receive buffer size
   */
  public void setSocketReceiveBufferSize(Integer socketReceiveBufferSize) {
    this.socketReceiveBufferSize = socketReceiveBufferSize;
  }

  /**
   * Get the socket send buffer size hint
   *
   * @return the send buffer size
   */
  public Integer getSocketSendBufferSize() {
    return socketSendBufferSize;
  }

  /**
   * Set the socket send buffer size hint
   *
   * @param socketSendBufferSize the send buffer size
   */
  public void setSocketSendBufferSize(Integer socketSendBufferSize) {
    this.socketSendBufferSize = socketSendBufferSize;
  }

  /**
   * Get the request consistency level name
   *
   * @return - the request consistency level name
   */
  public String getConsistencyLevel() {
    return consistencyLevel;
  }

  /**
   * Set the request consistency level name (e.g. "LOCAL_ONE")
   *
   * @param consistencyLevel - the request consistency level name
   */
  public void setConsistencyLevel(String consistencyLevel) {
    this.consistencyLevel = consistencyLevel;
  }

  /**
   * Get the request serial consistency level name
   *
   * @return - the request serial consistency level name
   */
  public String getSerialConsistencyLevel() {
    return serialConsistencyLevel;
  }

  /**
   * Set the request serial consistency level name (e.g. "LOCAL_SERIAL")
   *
   * @param serialConsistencyLevel - the request serial consistency level name
   */
  public void setSerialConsistencyLevel(String serialConsistencyLevel) {
    this.serialConsistencyLevel = serialConsistencyLevel;
  }

  /**
   * Get the defaultIdempotence flag
   *
   * @return - the defaultIdempotence flag
   */
  public Boolean getDefaultIdempotence() {
    return defaultIdempotence;
  }

  /**
   * Set the defaultIdempotence flag
   *
   * @param defaultIdempotence - the defaultIdempotence flag
   */
  public void setDefaultIdempotence(Boolean defaultIdempotence) {
    this.defaultIdempotence = defaultIdempotence;
  }

  /**
   * Get the default request page size
   *
   * @return - the default request page size
   */
  public Integer getPageSize() {
    return pageSize;
  }

  /**
   * Set the default request page size
   *
   * @param pageSize - the default request page size
   */
  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }

  /**
   * Get the SpeculativeExecutionPolicy implementation class name
   *
   * @return - The fully qualified SpeculativeExecutionPolicy implementation class name
   */
  public String getSpeculativeExecutionPolicyClassName() {
    return speculativeExecutionPolicyClassName;
  }

  /**
   * Set the SpeculativeExecutionPolicy implementation class name. The driver instantiates this
   * class itself via a (DriverContext, String profileName) constructor.
   *
   * @param speculativeExecutionPolicyClassName - The fully qualified SpeculativeExecutionPolicy
   *     implementation class name
   */
  public void setSpeculativeExecutionPolicyClassName(String speculativeExecutionPolicyClassName) {
    this.speculativeExecutionPolicyClassName = speculativeExecutionPolicyClassName;
  }

  /**
   * Get the maximum number of speculative executions (including the initial one)
   *
   * @return the max executions
   */
  public Integer getSpeculativeExecutionMaxExecutions() {
    return speculativeExecutionMaxExecutions;
  }

  /**
   * Set the maximum number of speculative executions (including the initial one), used by the
   * built-in ConstantSpeculativeExecutionPolicy
   *
   * @param speculativeExecutionMaxExecutions the max executions
   */
  public void setSpeculativeExecutionMaxExecutions(Integer speculativeExecutionMaxExecutions) {
    this.speculativeExecutionMaxExecutions = speculativeExecutionMaxExecutions;
  }

  /**
   * Get the delay between speculative executions, in milliseconds
   *
   * @return the delay in milliseconds
   */
  public Long getSpeculativeExecutionDelayMillis() {
    return speculativeExecutionDelayMillis;
  }

  /**
   * Set the delay between speculative executions, in milliseconds, used by the built-in
   * ConstantSpeculativeExecutionPolicy
   *
   * @param speculativeExecutionDelayMillis the delay in milliseconds
   */
  public void setSpeculativeExecutionDelayMillis(Long speculativeExecutionDelayMillis) {
    this.speculativeExecutionDelayMillis = speculativeExecutionDelayMillis;
  }

  /**
   * Get the TimestampGenerator implementation class name
   *
   * @return - The fully qualified TimestampGenerator implementation class name
   */
  public String getTimestampGeneratorClassName() {
    return timestampGeneratorClassName;
  }

  /**
   * Set the TimestampGenerator implementation class name. The driver instantiates this class itself
   * via a (DriverContext) constructor.
   *
   * @param timestampGeneratorClassName - The fully qualified TimestampGenerator implementation
   *     class name
   */
  public void setTimestampGeneratorClassName(String timestampGeneratorClassName) {
    this.timestampGeneratorClassName = timestampGeneratorClassName;
  }

  /**
   * Get whether the generator is forced to use Java's millisecond-precision system clock
   *
   * @return the timestampGeneratorForceJavaClock flag
   */
  public Boolean getTimestampGeneratorForceJavaClock() {
    return timestampGeneratorForceJavaClock;
  }

  /**
   * Set whether the generator is forced to use Java's millisecond-precision system clock
   *
   * @param timestampGeneratorForceJavaClock the timestampGeneratorForceJavaClock flag
   */
  public void setTimestampGeneratorForceJavaClock(Boolean timestampGeneratorForceJavaClock) {
    this.timestampGeneratorForceJavaClock = timestampGeneratorForceJavaClock;
  }

  /**
   * Get how far timestamps may drift into the future before a warning is logged, in milliseconds
   *
   * @return the drift warning threshold in milliseconds
   */
  public Long getTimestampGeneratorDriftWarningThresholdMillis() {
    return timestampGeneratorDriftWarningThresholdMillis;
  }

  /**
   * Set how far timestamps may drift into the future before a warning is logged, in milliseconds
   *
   * @param timestampGeneratorDriftWarningThresholdMillis the drift warning threshold in
   *     milliseconds
   */
  public void setTimestampGeneratorDriftWarningThresholdMillis(
      Long timestampGeneratorDriftWarningThresholdMillis) {
    this.timestampGeneratorDriftWarningThresholdMillis =
        timestampGeneratorDriftWarningThresholdMillis;
  }

  /**
   * Get how often the drift warning is re-logged, in seconds
   *
   * @return the drift warning interval in seconds
   */
  public Long getTimestampGeneratorDriftWarningIntervalSeconds() {
    return timestampGeneratorDriftWarningIntervalSeconds;
  }

  /**
   * Set how often the drift warning is re-logged, in seconds
   *
   * @param timestampGeneratorDriftWarningIntervalSeconds the drift warning interval in seconds
   */
  public void setTimestampGeneratorDriftWarningIntervalSeconds(
      Long timestampGeneratorDriftWarningIntervalSeconds) {
    this.timestampGeneratorDriftWarningIntervalSeconds =
        timestampGeneratorDriftWarningIntervalSeconds;
  }

  /**
   * Get whether a warning is logged when a request changes the active keyspace
   *
   * @return the requestWarnIfSetKeyspace flag
   */
  public Boolean getRequestWarnIfSetKeyspace() {
    return requestWarnIfSetKeyspace;
  }

  /**
   * Set whether a warning is logged when a request changes the active keyspace
   *
   * @param requestWarnIfSetKeyspace the requestWarnIfSetKeyspace flag
   */
  public void setRequestWarnIfSetKeyspace(Boolean requestWarnIfSetKeyspace) {
    this.requestWarnIfSetKeyspace = requestWarnIfSetKeyspace;
  }

  /**
   * Get whether server-generated query warnings are logged
   *
   * @return the requestLogWarnings flag
   */
  public Boolean getRequestLogWarnings() {
    return requestLogWarnings;
  }

  /**
   * Set whether server-generated query warnings are logged
   *
   * @param requestLogWarnings the requestLogWarnings flag
   */
  public void setRequestLogWarnings(Boolean requestLogWarnings) {
    this.requestLogWarnings = requestLogWarnings;
  }

  /**
   * Get how many times the driver retries fetching a not-yet-ready query trace
   *
   * @return the trace fetch attempts
   */
  public Integer getRequestTraceAttempts() {
    return requestTraceAttempts;
  }

  /**
   * Set how many times the driver retries fetching a not-yet-ready query trace
   *
   * @param requestTraceAttempts the trace fetch attempts
   */
  public void setRequestTraceAttempts(Integer requestTraceAttempts) {
    this.requestTraceAttempts = requestTraceAttempts;
  }

  /**
   * Get the interval between query trace fetch attempts, in milliseconds
   *
   * @return the trace fetch interval in milliseconds
   */
  public Long getRequestTraceIntervalMillis() {
    return requestTraceIntervalMillis;
  }

  /**
   * Set the interval between query trace fetch attempts, in milliseconds
   *
   * @param requestTraceIntervalMillis the trace fetch interval in milliseconds
   */
  public void setRequestTraceIntervalMillis(Long requestTraceIntervalMillis) {
    this.requestTraceIntervalMillis = requestTraceIntervalMillis;
  }

  /**
   * Get the consistency level used when fetching query traces
   *
   * @return the trace consistency level name
   */
  public String getRequestTraceConsistencyLevel() {
    return requestTraceConsistencyLevel;
  }

  /**
   * Set the consistency level used when fetching query traces
   *
   * @param requestTraceConsistencyLevel the trace consistency level name
   */
  public void setRequestTraceConsistencyLevel(String requestTraceConsistencyLevel) {
    this.requestTraceConsistencyLevel = requestTraceConsistencyLevel;
  }

  /**
   * Get the RequestTracker implementation class names to register
   *
   * @return the fully qualified RequestTracker implementation class names
   */
  public List<String> getRequestTrackerClasses() {
    return requestTrackerClasses;
  }

  /**
   * Set the RequestTracker implementation class names to register. The driver instantiates each
   * class itself via a (DriverContext) constructor.
   *
   * @param requestTrackerClasses the fully qualified RequestTracker implementation class names
   */
  public void setRequestTrackerClasses(List<String> requestTrackerClasses) {
    this.requestTrackerClasses = requestTrackerClasses;
  }

  /**
   * Get whether the built-in RequestLogger logs successful requests
   *
   * @return the requestLoggerSuccessEnabled flag
   */
  public Boolean getRequestLoggerSuccessEnabled() {
    return requestLoggerSuccessEnabled;
  }

  /**
   * Set whether the built-in RequestLogger logs successful requests
   *
   * @param requestLoggerSuccessEnabled the requestLoggerSuccessEnabled flag
   */
  public void setRequestLoggerSuccessEnabled(Boolean requestLoggerSuccessEnabled) {
    this.requestLoggerSuccessEnabled = requestLoggerSuccessEnabled;
  }

  /**
   * Get the threshold above which a successful request is classified as "slow", in milliseconds
   *
   * @return the slow-request threshold in milliseconds
   */
  public Long getRequestLoggerSlowThresholdMillis() {
    return requestLoggerSlowThresholdMillis;
  }

  /**
   * Set the threshold above which a successful request is classified as "slow", in milliseconds
   *
   * @param requestLoggerSlowThresholdMillis the slow-request threshold in milliseconds
   */
  public void setRequestLoggerSlowThresholdMillis(Long requestLoggerSlowThresholdMillis) {
    this.requestLoggerSlowThresholdMillis = requestLoggerSlowThresholdMillis;
  }

  /**
   * Get whether the built-in RequestLogger logs slow requests
   *
   * @return the requestLoggerSlowEnabled flag
   */
  public Boolean getRequestLoggerSlowEnabled() {
    return requestLoggerSlowEnabled;
  }

  /**
   * Set whether the built-in RequestLogger logs slow requests
   *
   * @param requestLoggerSlowEnabled the requestLoggerSlowEnabled flag
   */
  public void setRequestLoggerSlowEnabled(Boolean requestLoggerSlowEnabled) {
    this.requestLoggerSlowEnabled = requestLoggerSlowEnabled;
  }

  /**
   * Get whether the built-in RequestLogger logs failed requests
   *
   * @return the requestLoggerErrorEnabled flag
   */
  public Boolean getRequestLoggerErrorEnabled() {
    return requestLoggerErrorEnabled;
  }

  /**
   * Set whether the built-in RequestLogger logs failed requests
   *
   * @param requestLoggerErrorEnabled the requestLoggerErrorEnabled flag
   */
  public void setRequestLoggerErrorEnabled(Boolean requestLoggerErrorEnabled) {
    this.requestLoggerErrorEnabled = requestLoggerErrorEnabled;
  }

  /**
   * Get the maximum logged query string length
   *
   * @return the max query length
   */
  public Integer getRequestLoggerMaxQueryLength() {
    return requestLoggerMaxQueryLength;
  }

  /**
   * Set the maximum logged query string length
   *
   * @param requestLoggerMaxQueryLength the max query length
   */
  public void setRequestLoggerMaxQueryLength(Integer requestLoggerMaxQueryLength) {
    this.requestLoggerMaxQueryLength = requestLoggerMaxQueryLength;
  }

  /**
   * Get whether the built-in RequestLogger logs bound values
   *
   * @return the requestLoggerShowValues flag
   */
  public Boolean getRequestLoggerShowValues() {
    return requestLoggerShowValues;
  }

  /**
   * Set whether the built-in RequestLogger logs bound values
   *
   * @param requestLoggerShowValues the requestLoggerShowValues flag
   */
  public void setRequestLoggerShowValues(Boolean requestLoggerShowValues) {
    this.requestLoggerShowValues = requestLoggerShowValues;
  }

  /**
   * Get the maximum logged bound-value length
   *
   * @return the max value length
   */
  public Integer getRequestLoggerMaxValueLength() {
    return requestLoggerMaxValueLength;
  }

  /**
   * Set the maximum logged bound-value length
   *
   * @param requestLoggerMaxValueLength the max value length
   */
  public void setRequestLoggerMaxValueLength(Integer requestLoggerMaxValueLength) {
    this.requestLoggerMaxValueLength = requestLoggerMaxValueLength;
  }

  /**
   * Get the maximum number of bound values logged per request
   *
   * @return the max number of bound values logged
   */
  public Integer getRequestLoggerMaxValues() {
    return requestLoggerMaxValues;
  }

  /**
   * Set the maximum number of bound values logged per request
   *
   * @param requestLoggerMaxValues the max number of bound values logged
   */
  public void setRequestLoggerMaxValues(Integer requestLoggerMaxValues) {
    this.requestLoggerMaxValues = requestLoggerMaxValues;
  }

  /**
   * Get whether the built-in RequestLogger logs stack traces for failed queries
   *
   * @return the requestLoggerShowStackTraces flag
   */
  public Boolean getRequestLoggerShowStackTraces() {
    return requestLoggerShowStackTraces;
  }

  /**
   * Set whether the built-in RequestLogger logs stack traces for failed queries
   *
   * @param requestLoggerShowStackTraces the requestLoggerShowStackTraces flag
   */
  public void setRequestLoggerShowStackTraces(Boolean requestLoggerShowStackTraces) {
    this.requestLoggerShowStackTraces = requestLoggerShowStackTraces;
  }

  /**
   * Get the RequestThrottler implementation class name
   *
   * @return the fully qualified RequestThrottler implementation class name
   */
  public String getThrottlerClassName() {
    return throttlerClassName;
  }

  /**
   * Set the RequestThrottler implementation class name (e.g. "ConcurrencyLimitingRequestThrottler",
   * "RateLimitingRequestThrottler", or a fully qualified custom class name). The driver
   * instantiates this class itself via a (DriverContext) constructor.
   *
   * @param throttlerClassName the fully qualified RequestThrottler implementation class name
   */
  public void setThrottlerClassName(String throttlerClassName) {
    this.throttlerClassName = throttlerClassName;
  }

  /**
   * Get the maximum number of requests that can be enqueued once throttled
   *
   * @return the max queue size
   */
  public Integer getThrottlerMaxQueueSize() {
    return throttlerMaxQueueSize;
  }

  /**
   * Set the maximum number of requests that can be enqueued once throttled
   *
   * @param throttlerMaxQueueSize the max queue size
   */
  public void setThrottlerMaxQueueSize(Integer throttlerMaxQueueSize) {
    this.throttlerMaxQueueSize = throttlerMaxQueueSize;
  }

  /**
   * Get the maximum number of requests allowed to execute in parallel
   * (ConcurrencyLimitingRequestThrottler only)
   *
   * @return the max concurrent requests
   */
  public Integer getThrottlerMaxConcurrentRequests() {
    return throttlerMaxConcurrentRequests;
  }

  /**
   * Set the maximum number of requests allowed to execute in parallel
   * (ConcurrencyLimitingRequestThrottler only)
   *
   * @param throttlerMaxConcurrentRequests the max concurrent requests
   */
  public void setThrottlerMaxConcurrentRequests(Integer throttlerMaxConcurrentRequests) {
    this.throttlerMaxConcurrentRequests = throttlerMaxConcurrentRequests;
  }

  /**
   * Get the maximum allowed request rate (RateLimitingRequestThrottler only)
   *
   * @return the max requests per second
   */
  public Integer getThrottlerMaxRequestsPerSecond() {
    return throttlerMaxRequestsPerSecond;
  }

  /**
   * Set the maximum allowed request rate (RateLimitingRequestThrottler only)
   *
   * @param throttlerMaxRequestsPerSecond the max requests per second
   */
  public void setThrottlerMaxRequestsPerSecond(Integer throttlerMaxRequestsPerSecond) {
    this.throttlerMaxRequestsPerSecond = throttlerMaxRequestsPerSecond;
  }

  /**
   * Get how often the throttler attempts to dequeue requests, in milliseconds
   * (RateLimitingRequestThrottler only)
   *
   * @return the drain interval in milliseconds
   */
  public Long getThrottlerDrainIntervalMillis() {
    return throttlerDrainIntervalMillis;
  }

  /**
   * Set how often the throttler attempts to dequeue requests, in milliseconds
   * (RateLimitingRequestThrottler only)
   *
   * @param throttlerDrainIntervalMillis the drain interval in milliseconds
   */
  public void setThrottlerDrainIntervalMillis(Long throttlerDrainIntervalMillis) {
    this.throttlerDrainIntervalMillis = throttlerDrainIntervalMillis;
  }

  /**
   * Get whether schema metadata is enabled
   *
   * @return the metadataSchemaEnabled flag
   */
  public Boolean getMetadataSchemaEnabled() {
    return metadataSchemaEnabled;
  }

  /**
   * Set whether schema metadata is enabled
   *
   * @param metadataSchemaEnabled the metadataSchemaEnabled flag
   */
  public void setMetadataSchemaEnabled(Boolean metadataSchemaEnabled) {
    this.metadataSchemaEnabled = metadataSchemaEnabled;
  }

  /**
   * Get the keyspace name-matching rules for which schema/token metadata is maintained
   *
   * @return the keyspace matching rules
   */
  public List<String> getMetadataSchemaRefreshedKeyspaces() {
    return metadataSchemaRefreshedKeyspaces;
  }

  /**
   * Set the keyspace name-matching rules for which schema/token metadata is maintained
   *
   * @param metadataSchemaRefreshedKeyspaces the keyspace matching rules
   */
  public void setMetadataSchemaRefreshedKeyspaces(List<String> metadataSchemaRefreshedKeyspaces) {
    this.metadataSchemaRefreshedKeyspaces = metadataSchemaRefreshedKeyspaces;
  }

  /**
   * Get the timeout for schema metadata requests, in milliseconds
   *
   * @return the schema request timeout in milliseconds
   */
  public Long getMetadataSchemaRequestTimeoutMillis() {
    return metadataSchemaRequestTimeoutMillis;
  }

  /**
   * Set the timeout for schema metadata requests, in milliseconds
   *
   * @param metadataSchemaRequestTimeoutMillis the schema request timeout in milliseconds
   */
  public void setMetadataSchemaRequestTimeoutMillis(Long metadataSchemaRequestTimeoutMillis) {
    this.metadataSchemaRequestTimeoutMillis = metadataSchemaRequestTimeoutMillis;
  }

  /**
   * Get the page size for schema metadata requests
   *
   * @return the schema request page size
   */
  public Integer getMetadataSchemaRequestPageSize() {
    return metadataSchemaRequestPageSize;
  }

  /**
   * Set the page size for schema metadata requests
   *
   * @param metadataSchemaRequestPageSize the schema request page size
   */
  public void setMetadataSchemaRequestPageSize(Integer metadataSchemaRequestPageSize) {
    this.metadataSchemaRequestPageSize = metadataSchemaRequestPageSize;
  }

  /**
   * Get the debounce window for coalescing schema-change events, in milliseconds
   *
   * @return the schema debounce window in milliseconds
   */
  public Long getMetadataSchemaWindowMillis() {
    return metadataSchemaWindowMillis;
  }

  /**
   * Set the debounce window for coalescing schema-change events, in milliseconds
   *
   * @param metadataSchemaWindowMillis the schema debounce window in milliseconds
   */
  public void setMetadataSchemaWindowMillis(Long metadataSchemaWindowMillis) {
    this.metadataSchemaWindowMillis = metadataSchemaWindowMillis;
  }

  /**
   * Get the maximum number of schema-change events accumulated before forcing a refresh
   *
   * @return the max schema events
   */
  public Integer getMetadataSchemaMaxEvents() {
    return metadataSchemaMaxEvents;
  }

  /**
   * Set the maximum number of schema-change events accumulated before forcing a refresh
   *
   * @param metadataSchemaMaxEvents the max schema events
   */
  public void setMetadataSchemaMaxEvents(Integer metadataSchemaMaxEvents) {
    this.metadataSchemaMaxEvents = metadataSchemaMaxEvents;
  }

  /**
   * Get the debounce window for coalescing topology events, in milliseconds
   *
   * @return the topology debounce window in milliseconds
   */
  public Long getMetadataTopologyWindowMillis() {
    return metadataTopologyWindowMillis;
  }

  /**
   * Set the debounce window for coalescing topology events, in milliseconds
   *
   * @param metadataTopologyWindowMillis the topology debounce window in milliseconds
   */
  public void setMetadataTopologyWindowMillis(Long metadataTopologyWindowMillis) {
    this.metadataTopologyWindowMillis = metadataTopologyWindowMillis;
  }

  /**
   * Get the maximum number of topology events accumulated before forcing propagation
   *
   * @return the max topology events
   */
  public Integer getMetadataTopologyMaxEvents() {
    return metadataTopologyMaxEvents;
  }

  /**
   * Set the maximum number of topology events accumulated before forcing propagation
   *
   * @param metadataTopologyMaxEvents the max topology events
   */
  public void setMetadataTopologyMaxEvents(Integer metadataTopologyMaxEvents) {
    this.metadataTopologyMaxEvents = metadataTopologyMaxEvents;
  }

  /**
   * Get whether token metadata is enabled
   *
   * @return the metadataTokenMapEnabled flag
   */
  public Boolean getMetadataTokenMapEnabled() {
    return metadataTokenMapEnabled;
  }

  /**
   * Set whether token metadata is enabled
   *
   * @param metadataTokenMapEnabled the metadataTokenMapEnabled flag
   */
  public void setMetadataTokenMapEnabled(Boolean metadataTokenMapEnabled) {
    this.metadataTokenMapEnabled = metadataTokenMapEnabled;
  }

  /**
   * Get whether Session.prepare() calls are re-sent to all other active nodes
   *
   * @return the prepareOnAllNodes flag
   */
  public Boolean getPrepareOnAllNodes() {
    return prepareOnAllNodes;
  }

  /**
   * Set whether Session.prepare() calls are re-sent to all other active nodes
   *
   * @param prepareOnAllNodes the prepareOnAllNodes flag
   */
  public void setPrepareOnAllNodes(Boolean prepareOnAllNodes) {
    this.prepareOnAllNodes = prepareOnAllNodes;
  }

  /**
   * Get whether the driver re-prepares statements on nodes that come back up or join the cluster
   *
   * @return the reprepareEnabled flag
   */
  public Boolean getReprepareEnabled() {
    return reprepareEnabled;
  }

  /**
   * Set whether the driver re-prepares statements on nodes that come back up or join the cluster
   *
   * @param reprepareEnabled the reprepareEnabled flag
   */
  public void setReprepareEnabled(Boolean reprepareEnabled) {
    this.reprepareEnabled = reprepareEnabled;
  }

  /**
   * Get whether system.prepared_statements is checked before repreparing
   *
   * @return the reprepareCheckSystemTable flag
   */
  public Boolean getReprepareCheckSystemTable() {
    return reprepareCheckSystemTable;
  }

  /**
   * Set whether system.prepared_statements is checked before repreparing
   *
   * @param reprepareCheckSystemTable the reprepareCheckSystemTable flag
   */
  public void setReprepareCheckSystemTable(Boolean reprepareCheckSystemTable) {
    this.reprepareCheckSystemTable = reprepareCheckSystemTable;
  }

  /**
   * Get the maximum number of statements to reprepare
   *
   * @return the max statements to reprepare
   */
  public Integer getReprepareMaxStatements() {
    return reprepareMaxStatements;
  }

  /**
   * Set the maximum number of statements to reprepare; 0 or negative means no limit
   *
   * @param reprepareMaxStatements the max statements to reprepare
   */
  public void setReprepareMaxStatements(Integer reprepareMaxStatements) {
    this.reprepareMaxStatements = reprepareMaxStatements;
  }

  /**
   * Get the maximum number of concurrent reprepare requests
   *
   * @return the max reprepare parallelism
   */
  public Integer getReprepareMaxParallelism() {
    return reprepareMaxParallelism;
  }

  /**
   * Set the maximum number of concurrent reprepare requests
   *
   * @param reprepareMaxParallelism the max reprepare parallelism
   */
  public void setReprepareMaxParallelism(Integer reprepareMaxParallelism) {
    this.reprepareMaxParallelism = reprepareMaxParallelism;
  }

  /**
   * Get the reprepare request timeout, in milliseconds
   *
   * @return the reprepare timeout in milliseconds
   */
  public Long getReprepareTimeoutMillis() {
    return reprepareTimeoutMillis;
  }

  /**
   * Set the reprepare request timeout, in milliseconds
   *
   * @param reprepareTimeoutMillis the reprepare timeout in milliseconds
   */
  public void setReprepareTimeoutMillis(Long reprepareTimeoutMillis) {
    this.reprepareTimeoutMillis = reprepareTimeoutMillis;
  }

  /**
   * Get whether the prepared-statement cache uses weak references for its values
   *
   * @return the preparedCacheWeakValues flag
   */
  public Boolean getPreparedCacheWeakValues() {
    return preparedCacheWeakValues;
  }

  /**
   * Set whether the prepared-statement cache uses weak references for its values
   *
   * @param preparedCacheWeakValues the preparedCacheWeakValues flag
   */
  public void setPreparedCacheWeakValues(Boolean preparedCacheWeakValues) {
    this.preparedCacheWeakValues = preparedCacheWeakValues;
  }

  /**
   * Get whether the driver's internal threads are daemon threads
   *
   * @return the nettyDaemonThreads flag
   */
  public Boolean getNettyDaemonThreads() {
    return nettyDaemonThreads;
  }

  /**
   * Set whether the driver's internal threads are daemon threads
   *
   * @param nettyDaemonThreads the nettyDaemonThreads flag
   */
  public void setNettyDaemonThreads(Boolean nettyDaemonThreads) {
    this.nettyDaemonThreads = nettyDaemonThreads;
  }

  /**
   * Get the number of I/O threads
   *
   * @return the I/O thread count
   */
  public Integer getNettyIoGroupSize() {
    return nettyIoGroupSize;
  }

  /**
   * Set the number of I/O threads; 0 means availableProcessors() * 2
   *
   * @param nettyIoGroupSize the I/O thread count
   */
  public void setNettyIoGroupSize(Integer nettyIoGroupSize) {
    this.nettyIoGroupSize = nettyIoGroupSize;
  }

  /**
   * Get the I/O event loop group shutdown quiet period, in seconds
   *
   * @return the quiet period in seconds
   */
  public Integer getNettyIoGroupShutdownQuietPeriodSeconds() {
    return nettyIoGroupShutdownQuietPeriodSeconds;
  }

  /**
   * Set the I/O event loop group shutdown quiet period, in seconds
   *
   * @param nettyIoGroupShutdownQuietPeriodSeconds the quiet period in seconds
   */
  public void setNettyIoGroupShutdownQuietPeriodSeconds(
      Integer nettyIoGroupShutdownQuietPeriodSeconds) {
    this.nettyIoGroupShutdownQuietPeriodSeconds = nettyIoGroupShutdownQuietPeriodSeconds;
  }

  /**
   * Get the I/O event loop group shutdown timeout, in seconds
   *
   * @return the shutdown timeout in seconds
   */
  public Integer getNettyIoGroupShutdownTimeoutSeconds() {
    return nettyIoGroupShutdownTimeoutSeconds;
  }

  /**
   * Set the I/O event loop group shutdown timeout, in seconds
   *
   * @param nettyIoGroupShutdownTimeoutSeconds the shutdown timeout in seconds
   */
  public void setNettyIoGroupShutdownTimeoutSeconds(Integer nettyIoGroupShutdownTimeoutSeconds) {
    this.nettyIoGroupShutdownTimeoutSeconds = nettyIoGroupShutdownTimeoutSeconds;
  }

  /**
   * Get the number of admin threads
   *
   * @return the admin thread count
   */
  public Integer getNettyAdminGroupSize() {
    return nettyAdminGroupSize;
  }

  /**
   * Set the number of admin threads (cluster events, metadata refresh, reconnection scheduling)
   *
   * @param nettyAdminGroupSize the admin thread count
   */
  public void setNettyAdminGroupSize(Integer nettyAdminGroupSize) {
    this.nettyAdminGroupSize = nettyAdminGroupSize;
  }

  /**
   * Get the admin event loop group shutdown quiet period, in seconds
   *
   * @return the quiet period in seconds
   */
  public Integer getNettyAdminGroupShutdownQuietPeriodSeconds() {
    return nettyAdminGroupShutdownQuietPeriodSeconds;
  }

  /**
   * Set the admin event loop group shutdown quiet period, in seconds
   *
   * @param nettyAdminGroupShutdownQuietPeriodSeconds the quiet period in seconds
   */
  public void setNettyAdminGroupShutdownQuietPeriodSeconds(
      Integer nettyAdminGroupShutdownQuietPeriodSeconds) {
    this.nettyAdminGroupShutdownQuietPeriodSeconds = nettyAdminGroupShutdownQuietPeriodSeconds;
  }

  /**
   * Get the admin event loop group shutdown timeout, in seconds
   *
   * @return the shutdown timeout in seconds
   */
  public Integer getNettyAdminGroupShutdownTimeoutSeconds() {
    return nettyAdminGroupShutdownTimeoutSeconds;
  }

  /**
   * Set the admin event loop group shutdown timeout, in seconds
   *
   * @param nettyAdminGroupShutdownTimeoutSeconds the shutdown timeout in seconds
   */
  public void setNettyAdminGroupShutdownTimeoutSeconds(
      Integer nettyAdminGroupShutdownTimeoutSeconds) {
    this.nettyAdminGroupShutdownTimeoutSeconds = nettyAdminGroupShutdownTimeoutSeconds;
  }

  /**
   * Get the request-timeout/speculative-execution timer's tick duration, in milliseconds
   *
   * @return the tick duration in milliseconds
   */
  public Long getNettyTimerTickDurationMillis() {
    return nettyTimerTickDurationMillis;
  }

  /**
   * Set the request-timeout/speculative-execution timer's tick duration, in milliseconds
   *
   * @param nettyTimerTickDurationMillis the tick duration in milliseconds
   */
  public void setNettyTimerTickDurationMillis(Long nettyTimerTickDurationMillis) {
    this.nettyTimerTickDurationMillis = nettyTimerTickDurationMillis;
  }

  /**
   * Get the timer's hashed wheel size
   *
   * @return the ticks per wheel
   */
  public Integer getNettyTimerTicksPerWheel() {
    return nettyTimerTicksPerWheel;
  }

  /**
   * Set the timer's hashed wheel size
   *
   * @param nettyTimerTicksPerWheel the ticks per wheel
   */
  public void setNettyTimerTicksPerWheel(Integer nettyTimerTicksPerWheel) {
    this.nettyTimerTicksPerWheel = nettyTimerTicksPerWheel;
  }

  /**
   * Get the write-coalescer reschedule interval, in microseconds
   *
   * @return the reschedule interval in microseconds
   */
  public Long getCoalescerIntervalMicros() {
    return coalescerIntervalMicros;
  }

  /**
   * Set the write-coalescer reschedule interval, in microseconds. Expert-level tuning; the driver's
   * default is fine for almost all use cases.
   *
   * @param coalescerIntervalMicros the reschedule interval in microseconds
   */
  public void setCoalescerIntervalMicros(Long coalescerIntervalMicros) {
    this.coalescerIntervalMicros = coalescerIntervalMicros;
  }

  /**
   * Get the maximum number of coalescing runs per reschedule interval
   *
   * @return the max coalescing runs
   */
  public Integer getCoalescerMaxRuns() {
    return coalescerMaxRuns;
  }

  /**
   * Set the maximum number of coalescing runs per reschedule interval. Expert-level tuning.
   *
   * @param coalescerMaxRuns the max coalescing runs
   */
  public void setCoalescerMaxRuns(Integer coalescerMaxRuns) {
    this.coalescerMaxRuns = coalescerMaxRuns;
  }

  /**
   * Get the maximum number of live sessions allowed to coexist in this JVM before a leak warning is
   * logged
   *
   * @return the session leak threshold
   */
  public Integer getSessionLeakThreshold() {
    return sessionLeakThreshold;
  }

  /**
   * Set the maximum number of live sessions allowed to coexist in this JVM before a leak warning is
   * logged
   *
   * @param sessionLeakThreshold the session leak threshold
   */
  public void setSessionLeakThreshold(Integer sessionLeakThreshold) {
    this.sessionLeakThreshold = sessionLeakThreshold;
  }

  /**
   * Get whether contact-point addresses are resolved once at startup (true) or on every connection
   * attempt (false)
   *
   * @return the resolveContactPoints flag
   */
  public Boolean getResolveContactPoints() {
    return resolveContactPoints;
  }

  /**
   * Set whether contact-point addresses are resolved once at startup (true) or on every connection
   * attempt (false)
   *
   * @param resolveContactPoints the resolveContactPoints flag
   */
  public void setResolveContactPoints(Boolean resolveContactPoints) {
    this.resolveContactPoints = resolveContactPoints;
  }

  /**
   * Get the ProtocolVersion name
   *
   * @return - the protocol version name (e.g. "V4")
   */
  public String getProtocolVersion() {
    return protocolVersion;
  }

  /**
   * Set the ProtocolVersion name
   *
   * @param protocolVersion - the protocol version name (e.g. "V4")
   */
  public void setProtocolVersion(String protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  @Override
  protected ClusterDataSource createDataSource() throws CpoException {
    ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder =
        DriverConfigLoader.programmaticBuilder();

    if (clusterName != null)
      configLoaderBuilder.withString(DefaultDriverOption.SESSION_NAME, clusterName);

    if (loadBalancingPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.LOAD_BALANCING_POLICY_CLASS,
          loadPolicyClass(loadBalancingPolicyClassName, LoadBalancingPolicy.class));

    if (loadBalancingSlowReplicaAvoidance != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.LOAD_BALANCING_POLICY_SLOW_AVOIDANCE,
          loadBalancingSlowReplicaAvoidance);

    if (loadBalancingDistanceEvaluatorClassName != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.LOAD_BALANCING_DISTANCE_EVALUATOR_CLASS,
          loadBalancingDistanceEvaluatorClassName);

    if (dcFailoverMaxNodesPerRemoteDc != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.LOAD_BALANCING_DC_FAILOVER_MAX_NODES_PER_REMOTE_DC,
          dcFailoverMaxNodesPerRemoteDc);

    if (dcFailoverAllowForLocalConsistencyLevels != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.LOAD_BALANCING_DC_FAILOVER_ALLOW_FOR_LOCAL_CONSISTENCY_LEVELS,
          dcFailoverAllowForLocalConsistencyLevels);

    if (dcFailoverPreferredRemoteDcs != null)
      configLoaderBuilder.withStringList(
          DefaultDriverOption.LOAD_BALANCING_DC_FAILOVER_PREFERRED_REMOTE_DCS,
          dcFailoverPreferredRemoteDcs);

    if (reconnectionPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.RECONNECTION_POLICY_CLASS,
          loadPolicyClass(reconnectionPolicyClassName, ReconnectionPolicy.class));

    if (reconnectionBaseDelayMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.RECONNECTION_BASE_DELAY,
          Duration.ofMillis(reconnectionBaseDelayMillis));

    if (reconnectionMaxDelayMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.RECONNECTION_MAX_DELAY,
          Duration.ofMillis(reconnectionMaxDelayMillis));

    if (reconnectOnInit != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.RECONNECT_ON_INIT, reconnectOnInit);

    if (retryPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.RETRY_POLICY_CLASS,
          loadPolicyClass(retryPolicyClassName, RetryPolicy.class));

    if (addressTranslatorClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.ADDRESS_TRANSLATOR_CLASS,
          loadPolicyClass(addressTranslatorClassName, AddressTranslator.class));

    if (addressTranslatorAdvertisedHostname != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.ADDRESS_TRANSLATOR_ADVERTISED_HOSTNAME,
          addressTranslatorAdvertisedHostname);

    if (addressTranslatorDefaultAddress != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.ADDRESS_TRANSLATOR_DEFAULT_ADDRESS, addressTranslatorDefaultAddress);

    if (addressTranslatorResolveAddresses != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.ADDRESS_TRANSLATOR_RESOLVE_ADDRESSES,
          addressTranslatorResolveAddresses);

    if (addressTranslatorSubnetAddresses != null)
      configLoaderBuilder.withStringMap(
          DefaultDriverOption.ADDRESS_TRANSLATOR_SUBNET_ADDRESSES,
          addressTranslatorSubnetAddresses);

    if (speculativeExecutionPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.SPECULATIVE_EXECUTION_POLICY_CLASS,
          loadPolicyClass(speculativeExecutionPolicyClassName, SpeculativeExecutionPolicy.class));

    if (speculativeExecutionMaxExecutions != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.SPECULATIVE_EXECUTION_MAX, speculativeExecutionMaxExecutions);

    if (speculativeExecutionDelayMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.SPECULATIVE_EXECUTION_DELAY,
          Duration.ofMillis(speculativeExecutionDelayMillis));

    if (timestampGeneratorClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.TIMESTAMP_GENERATOR_CLASS,
          loadPolicyClass(timestampGeneratorClassName, TimestampGenerator.class));

    if (timestampGeneratorForceJavaClock != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.TIMESTAMP_GENERATOR_FORCE_JAVA_CLOCK,
          timestampGeneratorForceJavaClock);

    if (timestampGeneratorDriftWarningThresholdMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.TIMESTAMP_GENERATOR_DRIFT_WARNING_THRESHOLD,
          Duration.ofMillis(timestampGeneratorDriftWarningThresholdMillis));

    if (timestampGeneratorDriftWarningIntervalSeconds != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.TIMESTAMP_GENERATOR_DRIFT_WARNING_INTERVAL,
          Duration.ofSeconds(timestampGeneratorDriftWarningIntervalSeconds));

    if (compressionType != null)
      configLoaderBuilder.withString(DefaultDriverOption.PROTOCOL_COMPRESSION, compressionType);

    if (protocolVersion != null)
      configLoaderBuilder.withString(DefaultDriverOption.PROTOCOL_VERSION, protocolVersion);

    if (protocolMaxFrameLengthBytes != null)
      configLoaderBuilder.withBytes(
          DefaultDriverOption.PROTOCOL_MAX_FRAME_LENGTH, protocolMaxFrameLengthBytes);

    if (sslCipherSuites != null)
      configLoaderBuilder.withStringList(DefaultDriverOption.SSL_CIPHER_SUITES, sslCipherSuites);

    if (sslHostnameValidation != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.SSL_HOSTNAME_VALIDATION, sslHostnameValidation);

    if (sslAllowDnsReverseLookupSan != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.SSL_ALLOW_DNS_REVERSE_LOOKUP_SAN, sslAllowDnsReverseLookupSan);

    if (sslTruststorePath != null)
      configLoaderBuilder.withString(DefaultDriverOption.SSL_TRUSTSTORE_PATH, sslTruststorePath);

    if (sslTruststorePassword != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.SSL_TRUSTSTORE_PASSWORD, sslTruststorePassword);

    if (sslKeystorePath != null)
      configLoaderBuilder.withString(DefaultDriverOption.SSL_KEYSTORE_PATH, sslKeystorePath);

    if (sslKeystorePassword != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.SSL_KEYSTORE_PASSWORD, sslKeystorePassword);

    if (sslKeystoreReloadIntervalMinutes != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.SSL_KEYSTORE_RELOAD_INTERVAL,
          Duration.ofMinutes(sslKeystoreReloadIntervalMinutes));

    if (sslEngineFactory == null
        && (sslCipherSuites != null
            || sslHostnameValidation != null
            || sslAllowDnsReverseLookupSan != null
            || sslTruststorePath != null
            || sslKeystorePath != null
            || sslKeystoreReloadIntervalMinutes != null))
      configLoaderBuilder.withString(
          DefaultDriverOption.SSL_ENGINE_FACTORY_CLASS, "DefaultSslEngineFactory");

    if (metricsFactoryClassName != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.METRICS_FACTORY_CLASS, metricsFactoryClassName);

    if (metricsIdGeneratorClassName != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.METRICS_ID_GENERATOR_CLASS, metricsIdGeneratorClassName);

    if (metricsIdGeneratorPrefix != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.METRICS_ID_GENERATOR_PREFIX, metricsIdGeneratorPrefix);

    if (metricsGenerateAggregableHistograms != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.METRICS_GENERATE_AGGREGABLE_HISTOGRAMS,
          metricsGenerateAggregableHistograms);

    if (metricsSessionEnabled != null)
      configLoaderBuilder.withStringList(
          DefaultDriverOption.METRICS_SESSION_ENABLED, metricsSessionEnabled);

    if (metricsNodeEnabled != null)
      configLoaderBuilder.withStringList(
          DefaultDriverOption.METRICS_NODE_ENABLED, metricsNodeEnabled);

    if (metricsNodeExpireAfterMinutes != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.METRICS_NODE_EXPIRE_AFTER,
          Duration.ofMinutes(metricsNodeExpireAfterMinutes));

    applyHistogramOptions(
        configLoaderBuilder,
        metricsSessionCqlRequests,
        DefaultDriverOption.METRICS_SESSION_CQL_REQUESTS_HIGHEST,
        DefaultDriverOption.METRICS_SESSION_CQL_REQUESTS_LOWEST,
        DefaultDriverOption.METRICS_SESSION_CQL_REQUESTS_DIGITS,
        DefaultDriverOption.METRICS_SESSION_CQL_REQUESTS_INTERVAL,
        DefaultDriverOption.METRICS_SESSION_CQL_REQUESTS_SLO,
        DefaultDriverOption.METRICS_SESSION_CQL_REQUESTS_PUBLISH_PERCENTILES);

    applyHistogramOptions(
        configLoaderBuilder,
        metricsSessionThrottlingDelay,
        DefaultDriverOption.METRICS_SESSION_THROTTLING_HIGHEST,
        DefaultDriverOption.METRICS_SESSION_THROTTLING_LOWEST,
        DefaultDriverOption.METRICS_SESSION_THROTTLING_DIGITS,
        DefaultDriverOption.METRICS_SESSION_THROTTLING_INTERVAL,
        DefaultDriverOption.METRICS_SESSION_THROTTLING_SLO,
        DefaultDriverOption.METRICS_SESSION_THROTTLING_PUBLISH_PERCENTILES);

    applyHistogramOptions(
        configLoaderBuilder,
        metricsNodeCqlMessages,
        DefaultDriverOption.METRICS_NODE_CQL_MESSAGES_HIGHEST,
        DefaultDriverOption.METRICS_NODE_CQL_MESSAGES_LOWEST,
        DefaultDriverOption.METRICS_NODE_CQL_MESSAGES_DIGITS,
        DefaultDriverOption.METRICS_NODE_CQL_MESSAGES_INTERVAL,
        DefaultDriverOption.METRICS_NODE_CQL_MESSAGES_SLO,
        DefaultDriverOption.METRICS_NODE_CQL_MESSAGES_PUBLISH_PERCENTILES);

    if (connectionPoolLocalSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.CONNECTION_POOL_LOCAL_SIZE, connectionPoolLocalSize);

    if (connectionPoolRemoteSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.CONNECTION_POOL_REMOTE_SIZE, connectionPoolRemoteSize);

    if (heartbeatIntervalSeconds != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.HEARTBEAT_INTERVAL, Duration.ofSeconds(heartbeatIntervalSeconds));

    if (heartbeatTimeoutSeconds != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.HEARTBEAT_TIMEOUT, Duration.ofSeconds(heartbeatTimeoutSeconds));

    if (connectInitQueryTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONNECTION_INIT_QUERY_TIMEOUT,
          Duration.ofMillis(connectInitQueryTimeoutMillis));

    if (setKeyspaceTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONNECTION_SET_KEYSPACE_TIMEOUT,
          Duration.ofMillis(setKeyspaceTimeoutMillis));

    if (maxRequestsPerConnection != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.CONNECTION_MAX_REQUESTS, maxRequestsPerConnection);

    if (maxOrphanRequests != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.CONNECTION_MAX_ORPHAN_REQUESTS, maxOrphanRequests);

    if (warnOnInitError != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.CONNECTION_WARN_INIT_ERROR, warnOnInitError);

    if (connectTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofMillis(connectTimeoutMillis));

    if (tcpNoDelay != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.SOCKET_TCP_NODELAY, tcpNoDelay);

    if (socketKeepAlive != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.SOCKET_KEEP_ALIVE, socketKeepAlive);

    if (socketReuseAddress != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.SOCKET_REUSE_ADDRESS, socketReuseAddress);

    if (socketLingerIntervalSeconds != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.SOCKET_LINGER_INTERVAL, socketLingerIntervalSeconds);

    if (socketReceiveBufferSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.SOCKET_RECEIVE_BUFFER_SIZE, socketReceiveBufferSize);

    if (socketSendBufferSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.SOCKET_SEND_BUFFER_SIZE, socketSendBufferSize);

    if (consistencyLevel != null)
      configLoaderBuilder.withString(DefaultDriverOption.REQUEST_CONSISTENCY, consistencyLevel);

    if (serialConsistencyLevel != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.REQUEST_SERIAL_CONSISTENCY, serialConsistencyLevel);

    if (defaultIdempotence != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REQUEST_DEFAULT_IDEMPOTENCE, defaultIdempotence);

    if (pageSize != null)
      configLoaderBuilder.withInt(DefaultDriverOption.REQUEST_PAGE_SIZE, pageSize);

    if (requestTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.REQUEST_TIMEOUT, Duration.ofMillis(requestTimeoutMillis));

    if (requestWarnIfSetKeyspace != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REQUEST_WARN_IF_SET_KEYSPACE, requestWarnIfSetKeyspace);

    if (requestLogWarnings != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.REQUEST_LOG_WARNINGS, requestLogWarnings);

    if (requestTraceAttempts != null)
      configLoaderBuilder.withInt(DefaultDriverOption.REQUEST_TRACE_ATTEMPTS, requestTraceAttempts);

    if (requestTraceIntervalMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.REQUEST_TRACE_INTERVAL,
          Duration.ofMillis(requestTraceIntervalMillis));

    if (requestTraceConsistencyLevel != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.REQUEST_TRACE_CONSISTENCY, requestTraceConsistencyLevel);

    if (requestTrackerClasses != null) {
      List<Class<?>> trackerClasses = new ArrayList<>(requestTrackerClasses.size());
      for (String className : requestTrackerClasses)
        trackerClasses.add(loadPolicyClass(className, RequestTracker.class));
      configLoaderBuilder.withClassList(
          DefaultDriverOption.REQUEST_TRACKER_CLASSES, trackerClasses);
    }

    if (requestLoggerSuccessEnabled != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REQUEST_LOGGER_SUCCESS_ENABLED, requestLoggerSuccessEnabled);

    if (requestLoggerSlowThresholdMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.REQUEST_LOGGER_SLOW_THRESHOLD,
          Duration.ofMillis(requestLoggerSlowThresholdMillis));

    if (requestLoggerSlowEnabled != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REQUEST_LOGGER_SLOW_ENABLED, requestLoggerSlowEnabled);

    if (requestLoggerErrorEnabled != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REQUEST_LOGGER_ERROR_ENABLED, requestLoggerErrorEnabled);

    if (requestLoggerMaxQueryLength != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REQUEST_LOGGER_MAX_QUERY_LENGTH, requestLoggerMaxQueryLength);

    if (requestLoggerShowValues != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REQUEST_LOGGER_VALUES, requestLoggerShowValues);

    if (requestLoggerMaxValueLength != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REQUEST_LOGGER_MAX_VALUE_LENGTH, requestLoggerMaxValueLength);

    if (requestLoggerMaxValues != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REQUEST_LOGGER_MAX_VALUES, requestLoggerMaxValues);

    if (requestLoggerShowStackTraces != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REQUEST_LOGGER_STACK_TRACES, requestLoggerShowStackTraces);

    if (throttlerClassName != null)
      configLoaderBuilder.withString(
          DefaultDriverOption.REQUEST_THROTTLER_CLASS, throttlerClassName);

    if (throttlerMaxQueueSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REQUEST_THROTTLER_MAX_QUEUE_SIZE, throttlerMaxQueueSize);

    if (throttlerMaxConcurrentRequests != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REQUEST_THROTTLER_MAX_CONCURRENT_REQUESTS,
          throttlerMaxConcurrentRequests);

    if (throttlerMaxRequestsPerSecond != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REQUEST_THROTTLER_MAX_REQUESTS_PER_SECOND,
          throttlerMaxRequestsPerSecond);

    if (throttlerDrainIntervalMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.REQUEST_THROTTLER_DRAIN_INTERVAL,
          Duration.ofMillis(throttlerDrainIntervalMillis));

    if (metadataSchemaEnabled != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.METADATA_SCHEMA_ENABLED, metadataSchemaEnabled);

    if (metadataSchemaRefreshedKeyspaces != null)
      configLoaderBuilder.withStringList(
          DefaultDriverOption.METADATA_SCHEMA_REFRESHED_KEYSPACES,
          metadataSchemaRefreshedKeyspaces);

    if (metadataSchemaRequestTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.METADATA_SCHEMA_REQUEST_TIMEOUT,
          Duration.ofMillis(metadataSchemaRequestTimeoutMillis));

    if (metadataSchemaRequestPageSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.METADATA_SCHEMA_REQUEST_PAGE_SIZE, metadataSchemaRequestPageSize);

    if (metadataSchemaWindowMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.METADATA_SCHEMA_WINDOW,
          Duration.ofMillis(metadataSchemaWindowMillis));

    if (metadataSchemaMaxEvents != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.METADATA_SCHEMA_MAX_EVENTS, metadataSchemaMaxEvents);

    if (metadataTopologyWindowMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.METADATA_TOPOLOGY_WINDOW,
          Duration.ofMillis(metadataTopologyWindowMillis));

    if (metadataTopologyMaxEvents != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.METADATA_TOPOLOGY_MAX_EVENTS, metadataTopologyMaxEvents);

    if (metadataTokenMapEnabled != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.METADATA_TOKEN_MAP_ENABLED, metadataTokenMapEnabled);

    if (maxSchemaAgreementWaitSeconds != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONTROL_CONNECTION_AGREEMENT_TIMEOUT,
          Duration.ofSeconds(maxSchemaAgreementWaitSeconds));

    if (schemaAgreementIntervalMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONTROL_CONNECTION_AGREEMENT_INTERVAL,
          Duration.ofMillis(schemaAgreementIntervalMillis));

    if (schemaAgreementWarnOnFailure != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.CONTROL_CONNECTION_AGREEMENT_WARN, schemaAgreementWarnOnFailure);

    if (controlConnectionTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONTROL_CONNECTION_TIMEOUT,
          Duration.ofMillis(controlConnectionTimeoutMillis));

    if (prepareOnAllNodes != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.PREPARE_ON_ALL_NODES, prepareOnAllNodes);

    if (reprepareEnabled != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.REPREPARE_ENABLED, reprepareEnabled);

    if (reprepareCheckSystemTable != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.REPREPARE_CHECK_SYSTEM_TABLE, reprepareCheckSystemTable);

    if (reprepareMaxStatements != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REPREPARE_MAX_STATEMENTS, reprepareMaxStatements);

    if (reprepareMaxParallelism != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.REPREPARE_MAX_PARALLELISM, reprepareMaxParallelism);

    if (reprepareTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.REPREPARE_TIMEOUT, Duration.ofMillis(reprepareTimeoutMillis));

    if (preparedCacheWeakValues != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.PREPARED_CACHE_WEAK_VALUES, preparedCacheWeakValues);

    if (nettyDaemonThreads != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.NETTY_DAEMON, nettyDaemonThreads);

    if (nettyIoGroupSize != null)
      configLoaderBuilder.withInt(DefaultDriverOption.NETTY_IO_SIZE, nettyIoGroupSize);

    if (nettyIoGroupShutdownQuietPeriodSeconds != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.NETTY_IO_SHUTDOWN_QUIET_PERIOD,
          nettyIoGroupShutdownQuietPeriodSeconds);

    if (nettyIoGroupShutdownTimeoutSeconds != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.NETTY_IO_SHUTDOWN_TIMEOUT, nettyIoGroupShutdownTimeoutSeconds);

    if (nettyAdminGroupSize != null)
      configLoaderBuilder.withInt(DefaultDriverOption.NETTY_ADMIN_SIZE, nettyAdminGroupSize);

    if (nettyAdminGroupShutdownQuietPeriodSeconds != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.NETTY_ADMIN_SHUTDOWN_QUIET_PERIOD,
          nettyAdminGroupShutdownQuietPeriodSeconds);

    if (nettyAdminGroupShutdownTimeoutSeconds != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.NETTY_ADMIN_SHUTDOWN_TIMEOUT, nettyAdminGroupShutdownTimeoutSeconds);

    if (nettyTimerTickDurationMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.NETTY_TIMER_TICK_DURATION,
          Duration.ofMillis(nettyTimerTickDurationMillis));

    if (nettyTimerTicksPerWheel != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.NETTY_TIMER_TICKS_PER_WHEEL, nettyTimerTicksPerWheel);

    if (coalescerIntervalMicros != null)
      configLoaderBuilder.withLong(DefaultDriverOption.COALESCER_INTERVAL, coalescerIntervalMicros);

    if (coalescerMaxRuns != null)
      configLoaderBuilder.withInt(DefaultDriverOption.COALESCER_MAX_RUNS, coalescerMaxRuns);

    if (sessionLeakThreshold != null)
      configLoaderBuilder.withInt(DefaultDriverOption.SESSION_LEAK_THRESHOLD, sessionLeakThreshold);

    if (resolveContactPoints != null)
      configLoaderBuilder.withBoolean(
          DefaultDriverOption.RESOLVE_CONTACT_POINTS, resolveContactPoints);

    CqlSessionBuilder sessionBuilder = CqlSession.builder();

    for (String contactPoint : contactPoints)
      sessionBuilder.addContactPoint(
          new InetSocketAddress(contactPoint, port != null ? port : 9042));

    if (localDatacenter != null) sessionBuilder.withLocalDatacenter(localDatacenter);

    if (keySpace != null) sessionBuilder.withKeyspace(keySpace);

    if (applicationName != null) sessionBuilder.withApplicationName(applicationName);

    if (applicationVersion != null) sessionBuilder.withApplicationVersion(applicationVersion);

    if (hasCredentials()) sessionBuilder.withAuthCredentials(userName, password);

    if (authProvider != null) sessionBuilder.withAuthProvider(authProvider);

    if (sslEngineFactory != null) sessionBuilder.withSslEngineFactory(sslEngineFactory);

    if (listeners != null)
      for (NodeStateListener listener : listeners) sessionBuilder.addNodeStateListener(listener);

    if (schemaChangeListeners != null)
      for (SchemaChangeListener listener : schemaChangeListeners)
        sessionBuilder.addSchemaChangeListener(listener);

    sessionBuilder.withConfigLoader(configLoaderBuilder.build());

    return new ClusterDataSource(sessionBuilder.build(), keySpace);
  }

  private static void applyHistogramOptions(
      ProgrammaticDriverConfigLoaderBuilder configLoaderBuilder,
      HistogramOptions options,
      DefaultDriverOption highestOption,
      DefaultDriverOption lowestOption,
      DefaultDriverOption digitsOption,
      DefaultDriverOption refreshOption,
      DefaultDriverOption sloOption,
      DefaultDriverOption publishPercentilesOption) {
    if (options == null) return;

    if (options.getHighestLatencyMillis() != null)
      configLoaderBuilder.withDuration(
          highestOption, Duration.ofMillis(options.getHighestLatencyMillis()));

    if (options.getLowestLatencyMillis() != null)
      configLoaderBuilder.withDuration(
          lowestOption, Duration.ofMillis(options.getLowestLatencyMillis()));

    if (options.getSignificantDigits() != null)
      configLoaderBuilder.withInt(digitsOption, options.getSignificantDigits());

    if (options.getRefreshIntervalMinutes() != null)
      configLoaderBuilder.withDuration(
          refreshOption, Duration.ofMinutes(options.getRefreshIntervalMinutes()));

    if (options.getSloMillis() != null) {
      List<Duration> slo = new ArrayList<>(options.getSloMillis().size());
      for (Long millis : options.getSloMillis()) slo.add(Duration.ofMillis(millis));
      configLoaderBuilder.withDurationList(sloOption, slo);
    }

    if (options.getPublishPercentiles() != null)
      configLoaderBuilder.withDoubleList(publishPercentilesOption, options.getPublishPercentiles());
  }

  @SuppressWarnings("unchecked")
  private static <T> Class<? extends T> loadPolicyClass(String className, Class<T> type)
      throws CpoException {
    try {
      return Class.forName(className).asSubclass(type);
    } catch (ClassNotFoundException e) {
      throw new CpoException("Unable to load policy class: " + className, e);
    }
  }

  private static String buildDataSourceName(
      String clusterName, String keySpace, List<String> contactPoints) {
    StringBuilder sb = new StringBuilder();
    sb.append(clusterName);
    sb.append(keySpace);
    for (String s : contactPoints) sb.append(s);
    logger.debug("DatasourceName=" + sb.toString());
    return sb.toString();
  }
}
