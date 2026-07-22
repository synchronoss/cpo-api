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
import com.datastax.oss.driver.api.core.retry.RetryPolicy;
import com.datastax.oss.driver.api.core.specex.SpeculativeExecutionPolicy;
import com.datastax.oss.driver.api.core.ssl.SslEngineFactory;
import com.datastax.oss.driver.api.core.time.TimestampGenerator;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
  private Integer maxSchemaAgreementWaitSeconds;
  private Integer port;
  private String protocolVersion;
  private String addressTranslatorClassName;
  private String loadBalancingPolicyClassName;
  private String reconnectionPolicyClassName;
  private String retryPolicyClassName;
  private boolean credentials;
  private String userName;
  private String password;
  private AuthProvider authProvider;
  private String compressionType;
  private Boolean useMetrics;
  private SslEngineFactory sslEngineFactory;
  private Collection<NodeStateListener> listeners;
  private Boolean useJmxReporting;
  private Integer connectionPoolLocalSize;
  private Integer connectionPoolRemoteSize;
  private Integer heartbeatIntervalSeconds;
  private Integer connectTimeoutMillis;
  private Boolean tcpNoDelay;
  private String consistencyLevel;
  private String serialConsistencyLevel;
  private Boolean defaultIdempotence;
  private Integer pageSize;
  private String speculativeExecutionPolicyClassName;
  private String timestampGeneratorClassName;

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
   * Get the cluster name
   *
   * @return The cluster name
   */
  public String getClusterName() {
    return clusterName;
  }

  /**
   * Set the cluster name. Note: driver 4.x has no session-level equivalent of the old
   * Cluster.Builder#withClusterName; this value is retained only for the CPO datasource display
   * name and is not passed to the driver.
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
   * UseMetrics flag
   *
   * @return - useMetrics flag
   */
  public Boolean getUseMetrics() {
    return useMetrics;
  }

  /**
   * Set useMetrics flag
   *
   * @param useMetrics - useMetrics flag
   */
  public void setUseMetrics(Boolean useMetrics) {
    this.useMetrics = useMetrics;
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
   * Get the useJmxReporting flag
   *
   * @return - The useJmxReporting flag
   */
  public Boolean getUseJmxReporting() {
    return useJmxReporting;
  }

  /**
   * Set the useJmxReporting flag. Driver 4.x has no built-in JMX reporter (it requires the optional
   * java-driver-metrics-jmx module, which isn't on the classpath here), so setting this true only
   * logs a warning.
   *
   * @param useJmxReporting - The useJmxReporting flag
   */
  public void setUseJmxReporting(Boolean useJmxReporting) {
    this.useJmxReporting = useJmxReporting;
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

    if (loadBalancingPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.LOAD_BALANCING_POLICY_CLASS,
          loadPolicyClass(loadBalancingPolicyClassName, LoadBalancingPolicy.class));

    if (reconnectionPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.RECONNECTION_POLICY_CLASS,
          loadPolicyClass(reconnectionPolicyClassName, ReconnectionPolicy.class));

    if (retryPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.RETRY_POLICY_CLASS,
          loadPolicyClass(retryPolicyClassName, RetryPolicy.class));

    if (addressTranslatorClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.ADDRESS_TRANSLATOR_CLASS,
          loadPolicyClass(addressTranslatorClassName, AddressTranslator.class));

    if (speculativeExecutionPolicyClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.SPECULATIVE_EXECUTION_POLICY_CLASS,
          loadPolicyClass(speculativeExecutionPolicyClassName, SpeculativeExecutionPolicy.class));

    if (timestampGeneratorClassName != null)
      configLoaderBuilder.withClass(
          DefaultDriverOption.TIMESTAMP_GENERATOR_CLASS,
          loadPolicyClass(timestampGeneratorClassName, TimestampGenerator.class));

    if (compressionType != null)
      configLoaderBuilder.withString(DefaultDriverOption.PROTOCOL_COMPRESSION, compressionType);

    if (protocolVersion != null)
      configLoaderBuilder.withString(DefaultDriverOption.PROTOCOL_VERSION, protocolVersion);

    if (connectionPoolLocalSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.CONNECTION_POOL_LOCAL_SIZE, connectionPoolLocalSize);

    if (connectionPoolRemoteSize != null)
      configLoaderBuilder.withInt(
          DefaultDriverOption.CONNECTION_POOL_REMOTE_SIZE, connectionPoolRemoteSize);

    if (heartbeatIntervalSeconds != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.HEARTBEAT_INTERVAL, Duration.ofSeconds(heartbeatIntervalSeconds));

    if (connectTimeoutMillis != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONNECTION_CONNECT_TIMEOUT, Duration.ofMillis(connectTimeoutMillis));

    if (tcpNoDelay != null)
      configLoaderBuilder.withBoolean(DefaultDriverOption.SOCKET_TCP_NODELAY, tcpNoDelay);

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

    if (maxSchemaAgreementWaitSeconds != null)
      configLoaderBuilder.withDuration(
          DefaultDriverOption.CONTROL_CONNECTION_AGREEMENT_TIMEOUT,
          Duration.ofSeconds(maxSchemaAgreementWaitSeconds));

    if (useMetrics != null && !useMetrics) {
      configLoaderBuilder.withStringList(
          DefaultDriverOption.METRICS_SESSION_ENABLED, Collections.emptyList());
      configLoaderBuilder.withStringList(
          DefaultDriverOption.METRICS_NODE_ENABLED, Collections.emptyList());
    }

    if (useJmxReporting != null && useJmxReporting)
      logger.warn(
          "jmxReporting was requested, but the driver 4.x JMX metrics extension "
              + "(java-driver-metrics-jmx) is not on the classpath; ignoring.");

    CqlSessionBuilder sessionBuilder = CqlSession.builder();

    for (String contactPoint : contactPoints)
      sessionBuilder.addContactPoint(
          new InetSocketAddress(contactPoint, port != null ? port : 9042));

    if (localDatacenter != null) sessionBuilder.withLocalDatacenter(localDatacenter);

    if (keySpace != null) sessionBuilder.withKeyspace(keySpace);

    if (hasCredentials()) sessionBuilder.withAuthCredentials(userName, password);

    if (authProvider != null) sessionBuilder.withAuthProvider(authProvider);

    if (sslEngineFactory != null) sessionBuilder.withSslEngineFactory(sslEngineFactory);

    if (listeners != null)
      for (NodeStateListener listener : listeners) sessionBuilder.addNodeStateListener(listener);

    sessionBuilder.withConfigLoader(configLoaderBuilder.build());

    return new ClusterDataSource(sessionBuilder.build(), keySpace);
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
