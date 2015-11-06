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
package org.synchronoss.cpo.cassandra.config;

import com.datastax.driver.core.*;
import com.datastax.driver.core.policies.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.CassandraCpoAdapter;
import org.synchronoss.cpo.cassandra.CassandraCpoAdapterFactory;
import org.synchronoss.cpo.cassandra.ClusterDataSourceInfo;
import org.synchronoss.cpo.cassandra.cpoCassandraConfig.CtCassandraConfig;
import org.synchronoss.cpo.cassandra.cpoCassandraConfig.CtCassandraReadWriteConfig;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

import java.util.ArrayList;
import java.util.Collection;

/**
 * CassandraCpoConfigProcessor processes the datasource configuration file for cassandra. It pulls out all the information needed to configure a cluster for use
 * within the application.
 *
 * User: dberry
 * Date: 9/10/13
 * Time: 08:20 AM
 * To change this template use File | Settings | File Templates.
 */
public class CassandraCpoConfigProcessor implements CpoConfigProcessor {
  private static final Logger logger = LoggerFactory.getLogger(CassandraCpoConfigProcessor.class);

  @Override
  public CpoAdapterFactory processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException {
    CpoAdapterFactory cpoAdapterFactory = null;

    if (cpoConfig == null || !(cpoConfig instanceof CtCassandraConfig)) {
      throw new CpoException("Invalid Jdbc Configuration Information");
    }

    CtCassandraConfig cassandraConfig = (CtCassandraConfig) cpoConfig;

    CassandraCpoMetaDescriptor metaDescriptor = (CassandraCpoMetaDescriptor) CpoMetaDescriptor.getInstance(cassandraConfig.getMetaDescriptorName());

    // build the cluster information
    if (cassandraConfig.isSetReadWriteConfig()) {
      ClusterDataSourceInfo clusterInfo = buildDataSourceInfo(cassandraConfig.getName(), cassandraConfig.getReadWriteConfig());
      cpoAdapterFactory = new CassandraCpoAdapterFactory(CassandraCpoAdapter.getInstance(metaDescriptor, clusterInfo));
    } else {
      ClusterDataSourceInfo readClusterInfo = buildDataSourceInfo(cassandraConfig.getName(), cassandraConfig.getReadConfig());
      ClusterDataSourceInfo writeClusterInfo = buildDataSourceInfo(cassandraConfig.getName(), cassandraConfig.getWriteConfig());
      cpoAdapterFactory = new CassandraCpoAdapterFactory(CassandraCpoAdapter.getInstance(metaDescriptor, writeClusterInfo, readClusterInfo));
    }
    logger.debug("Adapter Datasourcename =" + cpoAdapterFactory.getCpoAdapter().getDataSourceName());

    return cpoAdapterFactory;

  }

  /**
   * buildDataSourceInfo takes the config information from cpoconfig.xml and insantiates a CLusterDataSourceInfo object
   *
   * @param dataConfigName - The name of the data config
   * @param readWriteConfig - The configuration information
   * @return A ClusterDataSourceInfo Object
   * @throws CpoException
   */
  private ClusterDataSourceInfo buildDataSourceInfo(String dataConfigName, CtCassandraReadWriteConfig readWriteConfig) throws CpoException {
    ClusterDataSourceInfo clusterInfo = new ClusterDataSourceInfo(dataConfigName, readWriteConfig.getKeySpace(), readWriteConfig.getContactPointArray());

    // add clusterName
    if(readWriteConfig.isSetClusterName())
      clusterInfo.setClusterName(readWriteConfig.getClusterName());

    // add maxSchemaAgreementWaitSeconds
    if (readWriteConfig.isSetMaxSchemaAgreementWaitSeconds())
      clusterInfo.setMaxSchemaAgreementWaitSeconds(readWriteConfig.getMaxSchemaAgreementWaitSeconds());

    // add port
    if (readWriteConfig.isSetPort())
      clusterInfo.setPort(readWriteConfig.getPort());

    // add loadBalancing
    if (readWriteConfig.isSetLoadBalancingPolicy()) {
      clusterInfo.setLoadBalancingPolicy(new ConfigInstantiator<LoadBalancingPolicy>().instantiate(readWriteConfig.getLoadBalancingPolicy()));
    }

    // add reconnectionPolicy
    if (readWriteConfig.isSetReconnectionPolicy())
      clusterInfo.setReconnectionPolicy(new ConfigInstantiator<ReconnectionPolicy>().instantiate(readWriteConfig.getReconnectionPolicy()));

    // add retryPolicy
    if (readWriteConfig.isSetRetryPolicy())
      clusterInfo.setRetryPolicy(new ConfigInstantiator<RetryPolicy>().instantiate(readWriteConfig.getRetryPolicy()));

    // add credentials
    if (readWriteConfig.isSetCredentials()) {
      clusterInfo.setHasCredentials(true);
      clusterInfo.setUserName(readWriteConfig.getCredentials().getUser());
      clusterInfo.setPassword(readWriteConfig.getCredentials().getUser());
    }

    // add addressTranslater
    if (readWriteConfig.isSetAddressTranslater())
      clusterInfo.setAddressTranslater(new ConfigInstantiator<AddressTranslater>().instantiate(readWriteConfig.getAddressTranslater()));

    // add AuthProvider
    if (readWriteConfig.isSetAuthProvider())
      clusterInfo.setAuthProvider(new ConfigInstantiator<AuthProvider>().instantiate(readWriteConfig.getAuthProvider()));

    // add Compression
    if (readWriteConfig.isSetCompression())
      clusterInfo.setCompressionType(ProtocolOptions.Compression.valueOf(readWriteConfig.getCompression().toString()));

    // add NettyOptions
    if (readWriteConfig.isSetNettyOptions())
      clusterInfo.setNettyOptions(new ConfigInstantiator<NettyOptions>().instantiate(readWriteConfig.getNettyOptions()));

    // add Metrics
    if (readWriteConfig.isSetMetrics())
      clusterInfo.setUseMetrics(readWriteConfig.getMetrics());

    // add SSL
    if (readWriteConfig.isSetSslOptions())
      clusterInfo.setSslOptions(new ConfigInstantiator<SSLOptions>().instantiate(readWriteConfig.getSslOptions()));

    // add Listeners
    if (readWriteConfig.isSetInitialListeners()){
      clusterInfo.setListeners(new ConfigInstantiator<Collection<Host.StateListener>>().instantiate(readWriteConfig.getInitialListeners()));
    }

    // add JMX Reporting
    if (readWriteConfig.isSetJmxReporting())
      clusterInfo.setUseJmxReporting(readWriteConfig.getJmxReporting());

    // add pooling options
    if (readWriteConfig.isSetPoolingOptions())
      clusterInfo.setPoolingOptions(new ConfigInstantiator<PoolingOptions>().instantiate(readWriteConfig.getPoolingOptions()));

    // add socket options
    if (readWriteConfig.isSetSocketOptions())
      clusterInfo.setSocketOptions(new ConfigInstantiator<SocketOptions>().instantiate(readWriteConfig.getSocketOptions()));

    // add query Options
    if (readWriteConfig.isSetQueryOptions())
      clusterInfo.setQueryOptions(new ConfigInstantiator<QueryOptions>().instantiate(readWriteConfig.getQueryOptions()));

    // add speculativeExecutionPolicy
    if (readWriteConfig.isSetSpeculativeExecutionPolicy())
      clusterInfo.setSpeculativeExecutionPolicy(new ConfigInstantiator<SpeculativeExecutionPolicy>().instantiate(readWriteConfig.getSpeculativeExecutionPolicy()));

    // add TimestampGenerator
    if (readWriteConfig.isSetTimestampGenerator())
      clusterInfo.setTimestampGenerator(new ConfigInstantiator<TimestampGenerator>().instantiate(readWriteConfig.getTimestampGenerator()));

    logger.debug("Created DataSourceInfo: " + clusterInfo);
    return clusterInfo;
  }

}
