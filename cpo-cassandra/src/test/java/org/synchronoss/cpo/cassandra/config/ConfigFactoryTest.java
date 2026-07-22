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

import com.datastax.driver.core.AuthProvider;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.JdkSSLOptions;
import com.datastax.driver.core.NettyOptions;
import com.datastax.driver.core.SSLOptions;
import com.datastax.driver.core.policies.AddressTranslator;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.DefaultRetryPolicy;
import com.datastax.driver.core.policies.IdentityTranslator;
import com.datastax.driver.core.policies.LoadBalancingPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;
import com.datastax.driver.core.policies.RetryPolicy;
import com.datastax.driver.core.policies.RoundRobinPolicy;
import java.util.Collection;
import java.util.List;
import org.synchronoss.cpo.core.CpoException;
import org.testng.annotations.Test;

/** Unit tests for the config factory base classes and the ConfigInstantiator. */
public class ConfigFactoryTest {

  public static class TestRetryPolicyFactory extends RetryPolicyFactory {
    public TestRetryPolicyFactory() {}

    @Override
    public RetryPolicy createRetryPolicy() {
      return DefaultRetryPolicy.INSTANCE;
    }
  }

  public static class TestReconnectionPolicyFactory extends ReconnectionPolicyFactory {
    public TestReconnectionPolicyFactory() {}

    @Override
    public ReconnectionPolicy createReconnectionPolicy() {
      return new ConstantReconnectionPolicy(1000);
    }
  }

  public static class TestLoadBalancingPolicyFactory extends LoadBalancingPolicyFactory {
    public TestLoadBalancingPolicyFactory() {}

    @Override
    public LoadBalancingPolicy createLoadBalancingPolicy() {
      return new RoundRobinPolicy();
    }
  }

  public static class TestAddressTranslatorFactory extends AddressTranslatorFactory {
    public TestAddressTranslatorFactory() {}

    @Override
    public AddressTranslator createAddressTranslator() {
      return new IdentityTranslator();
    }
  }

  public static class TestAuthProviderFactory extends AuthProviderFactory {
    public TestAuthProviderFactory() {}

    @Override
    public AuthProvider createAuthProvider() {
      return AuthProvider.NONE;
    }
  }

  public static class TestSSLOptionsFactory extends SSLOptionsFactory {
    public TestSSLOptionsFactory() {}

    @Override
    public SSLOptions createSSLOptions() {
      return JdkSSLOptions.builder().build();
    }
  }

  public static class TestNettyOptionsFactory extends NettyOptionsFactory {
    public TestNettyOptionsFactory() {}

    @Override
    public NettyOptions createNettyOptions() {
      return NettyOptions.DEFAULT_INSTANCE;
    }
  }

  public static class TestListenerFactory extends ListenerFactory {
    public TestListenerFactory() {}

    @Override
    public Collection<Host.StateListener> createListeners() {
      return List.of();
    }
  }

  public static class ThrowingRetryPolicyFactory extends RetryPolicyFactory {
    public ThrowingRetryPolicyFactory() {}

    @Override
    public RetryPolicy createRetryPolicy() {
      throw new IllegalStateException("factory blew up");
    }
  }

  @Test
  public void testFactoryMethodNames() {
    assertEquals(new TestRetryPolicyFactory().getFactoryMethodName(), "createRetryPolicy");
    assertEquals(
        new TestReconnectionPolicyFactory().getFactoryMethodName(), "createReconnectionPolicy");
    assertEquals(
        new TestLoadBalancingPolicyFactory().getFactoryMethodName(), "createLoadBalancingPolicy");
    assertEquals(
        new TestAddressTranslatorFactory().getFactoryMethodName(), "createAddressTranslator");
    assertEquals(new TestAuthProviderFactory().getFactoryMethodName(), "createAuthProvider");
    assertEquals(new TestSSLOptionsFactory().getFactoryMethodName(), "createSSLOptions");
    assertEquals(new TestNettyOptionsFactory().getFactoryMethodName(), "createNettyOptions");
    assertEquals(new TestListenerFactory().getFactoryMethodName(), "createListeners");
  }

  @Test
  public void testInstantiateHappyPaths() throws Exception {
    assertTrue(
        new ConfigInstantiator<RetryPolicy>().instantiate(TestRetryPolicyFactory.class.getName())
            instanceof RetryPolicy);
    assertTrue(
        new ConfigInstantiator<ReconnectionPolicy>()
                .instantiate(TestReconnectionPolicyFactory.class.getName())
            instanceof ReconnectionPolicy);
    assertTrue(
        new ConfigInstantiator<LoadBalancingPolicy>()
                .instantiate(TestLoadBalancingPolicyFactory.class.getName())
            instanceof LoadBalancingPolicy);
    assertTrue(
        new ConfigInstantiator<AddressTranslator>()
                .instantiate(TestAddressTranslatorFactory.class.getName())
            instanceof AddressTranslator);
    assertTrue(
        new ConfigInstantiator<AuthProvider>().instantiate(TestAuthProviderFactory.class.getName())
            instanceof AuthProvider);
    assertTrue(
        new ConfigInstantiator<SSLOptions>().instantiate(TestSSLOptionsFactory.class.getName())
            instanceof SSLOptions);
    assertTrue(
        new ConfigInstantiator<NettyOptions>().instantiate(TestNettyOptionsFactory.class.getName())
            instanceof NettyOptions);
    assertTrue(
        new ConfigInstantiator<Collection<Host.StateListener>>()
                .instantiate(TestListenerFactory.class.getName())
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
        CpoException.class, () -> instantiator.instantiate(RetryPolicyFactory.class.getName()));

    // the factory method itself throws
    expectThrows(
        CpoException.class,
        () -> instantiator.instantiate(ThrowingRetryPolicyFactory.class.getName()));
  }
}
