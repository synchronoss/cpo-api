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
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.CassandraCpoAdapter;
import org.synchronoss.cpo.cassandra.ClusterDataSourceInfo;
import org.synchronoss.cpo.cassandra.cpoCassandraConfig.CtCassandraConfig;
import org.synchronoss.cpo.cassandra.cpoCassandraConfig.CtCassandraReadWriteConfig;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

import java.util.ArrayList;

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
  public CpoAdapter processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException {
    CpoAdapter cpoAdapter = null;

    if (cpoConfig == null || !(cpoConfig instanceof CtCassandraConfig)) {
      throw new CpoException("Invalid Jdbc Configuration Information");
    }

    CtCassandraConfig cassandraConfig = (CtCassandraConfig) cpoConfig;

    CassandraCpoMetaDescriptor metaDescriptor = (CassandraCpoMetaDescriptor) CpoMetaDescriptor.getInstance(cassandraConfig.getMetaDescriptorName());

    // build the cluster information
    if (cassandraConfig.isSetReadWriteConfig()) {
      ClusterDataSourceInfo clusterInfo = buildDataSourceInfo(cassandraConfig.getReadWriteConfig());
      cpoAdapter = CassandraCpoAdapter.getInstance(metaDescriptor, clusterInfo);
    } else {
      ClusterDataSourceInfo readClusterInfo = buildDataSourceInfo(cassandraConfig.getReadConfig());
      ClusterDataSourceInfo writeClusterInfo = buildDataSourceInfo(cassandraConfig.getWriteConfig());
      cpoAdapter = CassandraCpoAdapter.getInstance(metaDescriptor, writeClusterInfo, readClusterInfo);
    }

    return cpoAdapter;

  }

  private ClusterDataSourceInfo buildDataSourceInfo(CtCassandraReadWriteConfig readWriteConfig) throws CpoException {
    ClusterDataSourceInfo clusterInfo = new ClusterDataSourceInfo(readWriteConfig.getContactPointArray());

    // add clusterName
    if(readWriteConfig.isSetClusterName())
      clusterInfo.setClusterName(readWriteConfig.getClusterName());

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

    // add AuthProvider
    if (readWriteConfig.isSetAuthProvider())
      clusterInfo.setAuthProvider(new ConfigInstantiator<AuthProvider>().instantiate(readWriteConfig.getAuthProvider()));

    // add Compression
    if (readWriteConfig.isSetCompression())
      clusterInfo.setCompressionType(ProtocolOptions.Compression.valueOf(readWriteConfig.getCompression().toString()));

    // add Metrics
    if (readWriteConfig.isSetMetrics())
      clusterInfo.setUseMetrics(readWriteConfig.getMetrics());

    // add SSL
    if (readWriteConfig.isSetSslOptions())
      clusterInfo.setSslOptions(new ConfigInstantiator<SSLOptions>().instantiate(readWriteConfig.getSslOptions()));

    // add Listeners
    if (readWriteConfig.getInitialListenersArray().length>0){
      ArrayList<Host.StateListener> listeners = new ArrayList<Host.StateListener>();
      for (String s : readWriteConfig.getInitialListenersArray()) {
        listeners.add(new ConfigInstantiator<Host.StateListener>().instantiate(s));
      }
      clusterInfo.setListeners(listeners);
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

    // add query options
    if (readWriteConfig.isSetQueryOptions())
      clusterInfo.setQueryOptions(new ConfigInstantiator<QueryOptions>().instantiate(readWriteConfig.getQueryOptions()));


    logger.debug("Created DataSourceInfo: " + clusterInfo);
    return clusterInfo;
  }

}
