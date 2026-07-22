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

import com.datastax.driver.core.AtomicMonotonicTimestampGenerator;
import com.datastax.driver.core.TimestampGenerator;
import com.datastax.driver.core.policies.ConstantSpeculativeExecutionPolicy;
import com.datastax.driver.core.policies.SpeculativeExecutionPolicy;
import java.math.BigInteger;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.core.CpoAdapterFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.cpoconfig.CtCassandraConfig;
import org.synchronoss.cpo.cpoconfig.CtCassandraReadWriteConfig;
import org.synchronoss.cpo.cpoconfig.CtConnectionsPerHost;
import org.synchronoss.cpo.cpoconfig.CtCredentials;
import org.synchronoss.cpo.cpoconfig.CtDataSourceConfig;
import org.synchronoss.cpo.cpoconfig.CtHostDistanceAndThreshold;
import org.synchronoss.cpo.cpoconfig.CtPoolingOptions;
import org.synchronoss.cpo.cpoconfig.CtQueryOptions;
import org.synchronoss.cpo.cpoconfig.CtSocketOptions;
import org.synchronoss.cpo.cpoconfig.StCompression;
import org.synchronoss.cpo.cpoconfig.StConsistencyLevel;
import org.synchronoss.cpo.cpoconfig.StHostDistance;
import org.synchronoss.cpo.cpoconfig.StProtocolVersion;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/** Tests CassandraCpoConfigProcessor with minimal, maximal, and split configurations. */
public class CassandraCpoConfigProcessorTest {

  public static class TestSpeculativeExecutionPolicyFactory implements FactoryMethodName {
    public TestSpeculativeExecutionPolicyFactory() {}

    @Override
    public String getFactoryMethodName() {
      return "createPolicy";
    }

    public SpeculativeExecutionPolicy createPolicy() {
      return new ConstantSpeculativeExecutionPolicy(500, 1);
    }
  }

  public static class TestTimestampGeneratorFactory implements FactoryMethodName {
    public TestTimestampGeneratorFactory() {}

    @Override
    public String getFactoryMethodName() {
      return "createGenerator";
    }

    public TimestampGenerator createGenerator() {
      return new AtomicMonotonicTimestampGenerator();
    }
  }

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
    rw.setPort(nativePort);
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
    rw.setMaxSchemaAgreementWaitSeconds(15);
    rw.setLoadBalancingPolicy(ConfigFactoryTest.TestLoadBalancingPolicyFactory.class.getName());
    rw.setReconnectionPolicy(ConfigFactoryTest.TestReconnectionPolicyFactory.class.getName());
    rw.setRetryPolicy(ConfigFactoryTest.TestRetryPolicyFactory.class.getName());

    CtCredentials credentials = new CtCredentials();
    credentials.setUser("cpoUser");
    credentials.setPassword("cpoPass");
    rw.setCredentials(credentials);

    rw.setAddressTranslater(ConfigFactoryTest.TestAddressTranslatorFactory.class.getName());
    rw.setAuthProvider(ConfigFactoryTest.TestAuthProviderFactory.class.getName());
    rw.setCompression(StCompression.NONE);
    rw.setNettyOptions(ConfigFactoryTest.TestNettyOptionsFactory.class.getName());
    rw.setMetrics(Boolean.FALSE);
    rw.setInitialListeners(ConfigFactoryTest.TestListenerFactory.class.getName());
    rw.setJmxReporting(Boolean.FALSE);
    rw.setProtocolVersion(StProtocolVersion.V_3);
    rw.setSpeculativeExecutionPolicy(TestSpeculativeExecutionPolicyFactory.class.getName());
    rw.setTimestampGenerator(TestTimestampGeneratorFactory.class.getName());

    CtPoolingOptions pooling = new CtPoolingOptions();
    CtConnectionsPerHost cph = new CtConnectionsPerHost();
    cph.setDistance(StHostDistance.LOCAL);
    cph.setCore(1);
    cph.setMax(2);
    pooling.setConnectionsPerHost(cph);
    pooling.setCoreConnectionsPerHost(hdt(StHostDistance.LOCAL, 1));
    pooling.setMaxConnectionsPerHost(hdt(StHostDistance.LOCAL, 2));
    pooling.setMaxRequestsPerConnection(hdt(StHostDistance.LOCAL, 1024));
    pooling.setNewConnectionThreshold(hdt(StHostDistance.LOCAL, 800));
    pooling.setHeartbeatIntervalSeconds(30);
    pooling.setIdleTimeoutSeconds(120);
    pooling.setPoolTimeoutMillis(5000);
    rw.setPoolingOptions(pooling);

    CtQueryOptions query = new CtQueryOptions();
    query.setConsistencyLevel(StConsistencyLevel.ONE);
    query.setSerialConsistencyLevel(StConsistencyLevel.LOCAL_SERIAL);
    query.setDefaultIdempotence(Boolean.FALSE);
    query.setFetchSize(100);
    rw.setQueryOptions(query);

    CtSocketOptions socket = new CtSocketOptions();
    socket.setConnectionTimeoutMillis(5000);
    socket.setKeepAlive(Boolean.FALSE);
    socket.setReadTimeoutMillis(12000);
    socket.setReceiveBufferSize(65536);
    socket.setReuseAddress(Boolean.TRUE);
    socket.setSendBufferSize(65536);
    socket.setSoLinger(0);
    socket.setTcpNoDelay(Boolean.TRUE);
    rw.setSocketOptions(socket);

    CpoAdapterFactory factory =
        new CassandraCpoConfigProcessor().processCpoConfig(config("cfgMaximal", rw));
    assertNotNull(factory);
    assertNotNull(factory.getCpoAdapter().getDataSourceName());
  }

  private CtHostDistanceAndThreshold hdt(StHostDistance distance, int threshold) {
    CtHostDistanceAndThreshold value = new CtHostDistanceAndThreshold();
    value.setDistance(distance);
    value.setThreshold(threshold);
    return value;
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
    rw.setNettyOptions("");
    rw.setInitialListeners("");
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
