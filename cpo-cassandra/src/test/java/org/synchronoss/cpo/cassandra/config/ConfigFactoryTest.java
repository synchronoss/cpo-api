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

import com.datastax.oss.driver.api.core.auth.AuthProvider;
import com.datastax.oss.driver.api.core.auth.ProgrammaticPlainTextAuthProvider;
import com.datastax.oss.driver.api.core.metadata.EndPoint;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.metadata.NodeStateListener;
import com.datastax.oss.driver.api.core.metadata.schema.AggregateMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.FunctionMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.KeyspaceMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.SchemaChangeListener;
import com.datastax.oss.driver.api.core.metadata.schema.TableMetadata;
import com.datastax.oss.driver.api.core.metadata.schema.ViewMetadata;
import com.datastax.oss.driver.api.core.ssl.SslEngineFactory;
import com.datastax.oss.driver.api.core.type.UserDefinedType;
import java.util.Collection;
import java.util.List;
import javax.net.ssl.SSLEngine;
import org.synchronoss.cpo.core.CpoException;
import org.testng.annotations.Test;

/**
 * Unit tests for the config factory base classes that still wrap driver instances (AuthProvider,
 * SslEngineFactory, NodeStateListener) and the ConfigInstantiator. LoadBalancingPolicy,
 * ReconnectionPolicy, RetryPolicy, AddressTranslator, SpeculativeExecutionPolicy, and
 * TimestampGenerator no longer go through a CPO factory wrapper: driver 4.x instantiates those
 * itself from a plain class name, exercised end-to-end in ClusterDataSourceInfoTest instead.
 */
public class ConfigFactoryTest {

  public static class TestAuthProviderFactory extends AuthProviderFactory {
    public TestAuthProviderFactory() {}

    @Override
    public AuthProvider createAuthProvider() {
      return new ProgrammaticPlainTextAuthProvider("user", "pass");
    }
  }

  public static class TestSSLOptionsFactory extends SSLOptionsFactory {
    public TestSSLOptionsFactory() {}

    @Override
    public SslEngineFactory createSSLOptions() {
      return new SslEngineFactory() {
        @Override
        public SSLEngine newSslEngine(EndPoint remoteEndpoint) {
          return null;
        }

        @Override
        public void close() {}
      };
    }
  }

  public static class TestListenerFactory extends ListenerFactory {
    public TestListenerFactory() {}

    @Override
    public Collection<NodeStateListener> createListeners() {
      return List.of(
          new NodeStateListener() {
            @Override
            public void onAdd(Node node) {}

            @Override
            public void onUp(Node node) {}

            @Override
            public void onDown(Node node) {}

            @Override
            public void onRemove(Node node) {}

            @Override
            public void close() {}
          });
    }
  }

  public static class TestSchemaChangeListenerFactory extends SchemaChangeListenerFactory {
    public TestSchemaChangeListenerFactory() {}

    @Override
    public Collection<SchemaChangeListener> createSchemaChangeListeners() {
      return List.of(
          new SchemaChangeListener() {
            @Override
            public void onKeyspaceCreated(KeyspaceMetadata keyspace) {}

            @Override
            public void onKeyspaceDropped(KeyspaceMetadata keyspace) {}

            @Override
            public void onKeyspaceUpdated(KeyspaceMetadata current, KeyspaceMetadata previous) {}

            @Override
            public void onTableCreated(TableMetadata table) {}

            @Override
            public void onTableDropped(TableMetadata table) {}

            @Override
            public void onTableUpdated(TableMetadata current, TableMetadata previous) {}

            @Override
            public void onUserDefinedTypeCreated(UserDefinedType type) {}

            @Override
            public void onUserDefinedTypeDropped(UserDefinedType type) {}

            @Override
            public void onUserDefinedTypeUpdated(
                UserDefinedType current, UserDefinedType previous) {}

            @Override
            public void onFunctionCreated(FunctionMetadata function) {}

            @Override
            public void onFunctionDropped(FunctionMetadata function) {}

            @Override
            public void onFunctionUpdated(FunctionMetadata current, FunctionMetadata previous) {}

            @Override
            public void onAggregateCreated(AggregateMetadata aggregate) {}

            @Override
            public void onAggregateDropped(AggregateMetadata aggregate) {}

            @Override
            public void onAggregateUpdated(AggregateMetadata current, AggregateMetadata previous) {}

            @Override
            public void onViewCreated(ViewMetadata view) {}

            @Override
            public void onViewDropped(ViewMetadata view) {}

            @Override
            public void onViewUpdated(ViewMetadata current, ViewMetadata previous) {}

            @Override
            public void close() {}
          });
    }
  }

  public static class ThrowingAuthProviderFactory extends AuthProviderFactory {
    public ThrowingAuthProviderFactory() {}

    @Override
    public AuthProvider createAuthProvider() {
      throw new IllegalStateException("factory blew up");
    }
  }

  @Test
  public void testFactoryMethodNames() {
    assertEquals(new TestAuthProviderFactory().getFactoryMethodName(), "createAuthProvider");
    assertEquals(new TestSSLOptionsFactory().getFactoryMethodName(), "createSSLOptions");
    assertEquals(new TestListenerFactory().getFactoryMethodName(), "createListeners");
    assertEquals(
        new TestSchemaChangeListenerFactory().getFactoryMethodName(),
        "createSchemaChangeListeners");
  }

  @Test
  public void testInstantiateHappyPaths() throws Exception {
    assertTrue(
        new ConfigInstantiator<AuthProvider>().instantiate(TestAuthProviderFactory.class.getName())
            instanceof AuthProvider);
    assertTrue(
        new ConfigInstantiator<SslEngineFactory>()
                .instantiate(TestSSLOptionsFactory.class.getName())
            instanceof SslEngineFactory);
    assertTrue(
        new ConfigInstantiator<Collection<NodeStateListener>>()
                .instantiate(TestListenerFactory.class.getName())
            instanceof Collection);
    assertTrue(
        new ConfigInstantiator<Collection<SchemaChangeListener>>()
                .instantiate(TestSchemaChangeListenerFactory.class.getName())
            instanceof Collection);
  }

  @Test
  public void testInstantiateErrorPaths() {
    ConfigInstantiator<Object> instantiator = new ConfigInstantiator<>();

    // unknown class
    expectThrows(CpoException.class, () -> instantiator.instantiate("no.such.FactoryClass"));

    // a class that is not a FactoryMethodName
    expectThrows(CpoException.class, () -> instantiator.instantiate(String.class.getName()));

    // an abstract factory cannot be instantiated
    expectThrows(
        CpoException.class, () -> instantiator.instantiate(AuthProviderFactory.class.getName()));

    // the factory method itself throws
    expectThrows(
        CpoException.class,
        () -> instantiator.instantiate(ThrowingAuthProviderFactory.class.getName()));
  }
}
