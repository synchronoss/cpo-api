/*
 * Copyright (C) 2003-2025 David E. Berry
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
import com.datastax.driver.core.policies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.AbstractDataSourceInfo;
import org.synchronoss.cpo.CpoException;

import java.util.Collection;

/**
 * Contains the information needed to connect to a Cassandra cluster
 *
 * @author dberry
 */
public class ClusterDataSourceInfo extends AbstractDataSourceInfo<ClusterDataSource>{
  private static final Logger logger = LoggerFactory.getLogger(ClusterDataSourceInfo.class);
  private String[] contactPoints;
  private String keySpace;
  private String clusterName;
  private Integer maxSchemaAgreementWaitSeconds;
  private NettyOptions nettyOptions;
  private Integer port;
  private ProtocolVersion protocolVersion;
  private AddressTranslator addressTranslator;
  private LoadBalancingPolicy loadBalancingPolicy;
  private ReconnectionPolicy reconnectionPolicy;
  private RetryPolicy retryPolicy;
  private boolean credentials;
  private String userName;
  private String password;
  private AuthProvider authProvider;
  private ProtocolOptions.Compression compressionType;
  private Boolean useMetrics;
  private SSLOptions sslOptions;
  private Collection<Host.StateListener> listeners;
  private Boolean useJmxReporting;
  private PoolingOptions poolingOptions;
  private SocketOptions socketOptions;
  private QueryOptions queryOptions;
  private SpeculativeExecutionPolicy speculativeExecutionPolicy;
  private TimestampGenerator timestampGenerator;

    /**
     * Constructs a ClusterDataSourceInfo
     * @param clusterName The cluster name
     * @param keySpace The keyspace
     * @param contactPoints The contact points
     */
  public ClusterDataSourceInfo(String clusterName, String keySpace, String[] contactPoints) {
    super(buildDataSourceName(clusterName, keySpace, contactPoints));
    this.keySpace=keySpace;
    this.clusterName=clusterName;
    this.contactPoints=contactPoints;
  }

    /**
     * Get the MaxSchemaAgreementWaitSeconds setting
     * @return The MaxSchemaAgreementWaitSeconds
     */
  public Integer getMaxSchemaAgreementWaitSeconds() {
    return maxSchemaAgreementWaitSeconds;
  }

    /**
     * Set the MaxSchemaAgreementWaitSeconds setting
     * @param maxSchemaAgreementWaitSeconds The MaxSchemaAgreementWaitSeconds
     */
  public void setMaxSchemaAgreementWaitSeconds(Integer maxSchemaAgreementWaitSeconds) {
    this.maxSchemaAgreementWaitSeconds = maxSchemaAgreementWaitSeconds;
  }

    /**
     * Get the NettyOptions
     * @return NettyOptions
     */
  public NettyOptions getNettyOptions() {
    return nettyOptions;
  }

    /**
     * Set the NettyOptions
     * @param nettyOptions The NettyOptions
     */
  public void setNettyOptions(NettyOptions nettyOptions) {
    this.nettyOptions = nettyOptions;
  }

    /**
     * Get the AddressTranslator
     * @return The AddressTranslator
     */
  public AddressTranslator getAddressTranslator() {
    return addressTranslator;
  }

    /**
     * Set the AddressTranslator
     * @param addressTranslator The AddressTranslator
     */
  public void setAddressTranslater(AddressTranslator addressTranslator) {
    this.addressTranslator = addressTranslator;
  }

    /**
     * Get the cluster name
     * @return The cluster name
     */
  public String getClusterName() {
    return clusterName;
  }

    /**
     * Set the cluster name
     * @param clusterName The cluster name
     */
  public void setClusterName(String clusterName) {
    this.clusterName = clusterName;
  }

    /**
     * Get the keyspace name
     * @return The keyspace name
     */
  public String getKeySpace() {
    return keySpace;
  }

    /**
     * Get the port number
     * @return The port number
     */
  public int getPort() {
    return port;
  }

    /**
     * Set the port number
     * @param port The port number
     */
  public void setPort(int port) {
    this.port = port;
  }

    /**
     * Get the LoadBalancingPolicy
     * @return The LoadBalancingPolicy
     */
  public LoadBalancingPolicy getLoadBalancingPolicy() {
    return loadBalancingPolicy;
  }

    /**
     * Set the LoadBalancingPolicy
     * @param loadBalancingPolicy The LoadBalancingPolicy
     */
  public void setLoadBalancingPolicy(LoadBalancingPolicy loadBalancingPolicy) {
    this.loadBalancingPolicy = loadBalancingPolicy;
  }

    /**
     * Get the ReconnectionPolicy
     * @return The ReconnectionPolicy
     */
  public ReconnectionPolicy getReconnectionPolicy() {
    return reconnectionPolicy;
  }

    /**
     * Set the ReconnectionPolicy
     * @param reconnectionPolicy The ReconnectionPolicy
     */
  public void setReconnectionPolicy(ReconnectionPolicy reconnectionPolicy) {
    this.reconnectionPolicy = reconnectionPolicy;
  }

    /**
     * Get the RetryPolicy
     * @return The RetryPolicy
     */
  public RetryPolicy getRetryPolicy() {
    return retryPolicy;
  }

    /**
     * Set the RetryPolicy
     * @param retryPolicy The RetryPolicy
     */
  public void setRetryPolicy(RetryPolicy retryPolicy) {
    this.retryPolicy = retryPolicy;
  }

    /**
     * Has Credentials
     * @return hasCredentials
     */
  public boolean hasCredentials() {
    return credentials;
  }

    /**
     * Set hasCredentials
     * @param credentials hasCredentials
     */
  public void setHasCredentials(boolean credentials) {
    this.credentials = credentials;
  }

    /**
     * Get the username
     * @return The username
     */
  public String getUserName() {
    return userName;
  }

    /**
     * Set the username
     * @param userName The username
     */
  public void setUserName(String userName) {
    this.userName = userName;
  }

    /**
     * Get the password
     * @return The password
     */
  public String getPassword() {
    return password;
  }

    /**
     * Set the password
     * @param password - The password
     */
  public void setPassword(String password) {
    this.password = password;
  }

    /**
     * Get the AuthProvider
     * @return - The AuthProvider
     */
  public AuthProvider getAuthProvider() {
    return authProvider;
  }

    /**
     * Set the AuthProvider
     * @param authProvider - The AuthProvider
     */
  public void setAuthProvider(AuthProvider authProvider) {
    this.authProvider = authProvider;
  }

    /**
     * Get the compression type
     * @return - ProtocolOptions.Compression
     */
  public ProtocolOptions.Compression getCompressionType() {
    return compressionType;
  }

    /**
     * Set the compression type
     * @param compressionType - ProtocolOptions.Compression
     */
  public void setCompressionType(ProtocolOptions.Compression compressionType) {
    this.compressionType = compressionType;
  }

    /**
     * UseMetrics flag
     * @return - useMetrics flag
     */
  public Boolean getUseMetrics() {
    return useMetrics;
  }

    /**
     * Set useMetrics flag
     * @param useMetrics - useMetrics flag
     */
  public void setUseMetrics(Boolean useMetrics) {
    this.useMetrics = useMetrics;
  }

    /**
     * Get the SSLOptions
     * @return - The SSLOptions
     */
  public SSLOptions getSslOptions() {
    return sslOptions;
  }

    /**
     * Set the SSLOptions
     * @param sslOptions - The SSLOptions
     */
  public void setSslOptions(SSLOptions sslOptions) {
    this.sslOptions = sslOptions;
  }

    /**
     * Get the Host.StateListeners
     * @return - A collection of Host.StateListeners
     */
  public Collection<Host.StateListener> getListeners() {
    return listeners;
  }

    /**
     * Set the Host.StateListeners
     * @param listeners - A collection of Host.StateListeners
     */
  public void setListeners(Collection<Host.StateListener> listeners) {
    this.listeners = listeners;
  }

    /**
     * Get the useJmxReporting flag
     * @return - The useJmxReporting flag
     */
  public Boolean getUseJmxReporting() {
    return useJmxReporting;
  }

    /**
     * Set the useJmxReporting flag
     * @param useJmxReporting - The useJmxReporting flag
     */
  public void setUseJmxReporting(Boolean useJmxReporting) {
    this.useJmxReporting = useJmxReporting;
  }

    /**
     * Get the PoolingOptions
     * @return - The PoolingOptions
     */
  public PoolingOptions getPoolingOptions() {
    return poolingOptions;
  }

    /**
     * Set the PoolingOptions
     * @param poolingOptions - The PoolingOptions
     */
  public void setPoolingOptions(PoolingOptions poolingOptions) {
    this.poolingOptions = poolingOptions;
  }

    /**
     * Get the SocketOptions
     * @return - The SocketOptions
     */
  public SocketOptions getSocketOptions() {
    return socketOptions;
  }

    /**
     * Set the SocketOptions
     * @param socketOptions - The SocketOptions
     */
  public void setSocketOptions(SocketOptions socketOptions) {
    this.socketOptions = socketOptions;
  }

    /**
     * Get the QueryOptions
     * @return - The QueryOptions
     */
  public QueryOptions getQueryOptions() {
    return queryOptions;
  }

    /**
     * Set the QueryOptions
     * @param queryOptions - The QueryOptions
     */
  public void setQueryOptions(QueryOptions queryOptions) {
    this.queryOptions = queryOptions;
  }

    /**
     * Get the SpeculativeExecutionPolicy
     * @return - The SpeculativeExecutionPolicy
     */
  public SpeculativeExecutionPolicy getSpeculativeExecutionPolicy() {
    return speculativeExecutionPolicy;
  }

    /**
     * Set the SpeculativeExecutionPolicy
     * @param speculativeExecutionPolicy - The SpeculativeExecutionPolicy
     */
  public void setSpeculativeExecutionPolicy(SpeculativeExecutionPolicy speculativeExecutionPolicy) {
    this.speculativeExecutionPolicy = speculativeExecutionPolicy;
  }

    /**
     * Get the TimestampGenerator
     * @return - The TimestampGenerator
     */
  public TimestampGenerator getTimestampGenerator() {
    return timestampGenerator;
  }

    /**
     * Set the TimestampGenerator
     * @param timestampGenerator - The TimestampGenerator
     */
  public void setTimestampGenerator(TimestampGenerator timestampGenerator) {
    this.timestampGenerator = timestampGenerator;
  }

    /**
     * Get the ProtocolVersion
     * @return - The ProtocolVersion
     */
  public ProtocolVersion getProtocolVersion() {
    return protocolVersion;
  }

    /**
     * Set the ProtocolVersion
     * @param protocolVersion - ProtocolVersion
     */
  public void setProtocolVersion(ProtocolVersion protocolVersion) {
    this.protocolVersion = protocolVersion;
  }

  @Override
  protected ClusterDataSource createDataSource() throws CpoException {
    Cluster.Builder clusterBuilder = Cluster.builder();

    // add the contact points
    for(String s : contactPoints)
      clusterBuilder.addContactPoint(s);

    // add addressTranslater
    if (addressTranslator != null)
      clusterBuilder.withAddressTranslator(addressTranslator);

    // add AuthProvider
    if (authProvider != null)
      clusterBuilder.withAuthProvider(authProvider);

    // add clusterName
    if (clusterName != null)
      clusterBuilder.withClusterName(clusterName);

    // add Compression
    if (compressionType != null)
      clusterBuilder.withCompression(compressionType);

    // add credentials
    if (hasCredentials())
      clusterBuilder.withCredentials(userName, password);

    // add Listeners
    if (listeners!=null && !listeners.isEmpty())
      clusterBuilder.withInitialListeners(listeners);

    // add loadBalancing
    if (loadBalancingPolicy != null)
      clusterBuilder.withLoadBalancingPolicy(loadBalancingPolicy);

    // add maxSchemaAgreementWaitSeconds
    if (maxSchemaAgreementWaitSeconds != null)
      clusterBuilder.withMaxSchemaAgreementWaitSeconds(maxSchemaAgreementWaitSeconds);

    // add NettyOptions
    if (nettyOptions != null)
      clusterBuilder.withNettyOptions(nettyOptions);

    // add JMX Reporting
    if (useJmxReporting != null && !useJmxReporting)
      clusterBuilder.withoutJMXReporting();

    // add Metrics
    if (useMetrics != null && !useMetrics)
      clusterBuilder.withoutMetrics();

    // add pooling options
    if (poolingOptions != null)
      clusterBuilder.withPoolingOptions(poolingOptions);

    // add port
    if (port != null)
      clusterBuilder.withPort(port);

    if (protocolVersion != null)
      clusterBuilder.withProtocolVersion(protocolVersion);

    // add query options
    if (queryOptions != null)
      clusterBuilder.withQueryOptions(queryOptions);

    // add reconnectionPolicy
    if (reconnectionPolicy != null)
      clusterBuilder.withReconnectionPolicy(reconnectionPolicy);

    // add retryPolicy
    if (retryPolicy != null)
      clusterBuilder.withRetryPolicy(retryPolicy);

    // add socket options
    if (socketOptions != null)
      clusterBuilder.withSocketOptions(socketOptions);

    // add SpeculativeExecutionPolicy
    if (speculativeExecutionPolicy == null)
      clusterBuilder.withSpeculativeExecutionPolicy(speculativeExecutionPolicy);

    // add SSL
    if (sslOptions != null)
      clusterBuilder.withSSL(sslOptions);

    // add TimestampGenerator
    if (timestampGenerator == null)
      clusterBuilder.withTimestampGenerator(timestampGenerator);

    return new ClusterDataSource(clusterBuilder.build(), keySpace);
  }

  private static String buildDataSourceName(String clusterName, String keySpace, String[] contactPoints) {
    StringBuilder sb = new StringBuilder();
    sb.append(clusterName);
    sb.append(keySpace);
    for (String s : contactPoints)
      sb.append(s);
    logger.debug("DatasourceName="+sb.toString());
    return sb.toString();
  }

}
