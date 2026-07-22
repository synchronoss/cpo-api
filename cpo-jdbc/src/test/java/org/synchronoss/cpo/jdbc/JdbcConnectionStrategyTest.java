package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
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

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.sql.DataSource;
import org.synchronoss.cpo.core.CpoException;
import org.testng.annotations.Test;

/**
 * Pure-JVM unit tests for the connection lifecycle strategies. No database is needed: the
 * datasources and connections are reflective stubs, which lets these tests drive the failure and
 * failover branches that the integration suites never reach.
 *
 * @author david berry
 */
public class JdbcConnectionStrategyTest {

  /** A recording stub connection. */
  private static final class StubConnection {
    final AtomicBoolean closed = new AtomicBoolean(false);
    final AtomicBoolean failOnUse = new AtomicBoolean(false);
    final List<String> calls = new ArrayList<>();
    final Connection connection;

    StubConnection() {
      connection =
          (Connection)
              Proxy.newProxyInstance(
                  getClass().getClassLoader(),
                  new Class<?>[] {Connection.class},
                  (proxy, method, args) -> {
                    switch (method.getName()) {
                      case "setAutoCommit":
                        calls.add("setAutoCommit:" + args[0]);
                        return null;
                      case "close":
                        if (failOnUse.get()) throw new SQLException("close failed");
                        calls.add("close");
                        closed.set(true);
                        return null;
                      case "isClosed":
                        if (failOnUse.get()) throw new SQLException("isClosed failed");
                        return closed.get();
                      case "commit":
                        if (failOnUse.get()) throw new SQLException("commit failed");
                        calls.add("commit");
                        return null;
                      case "rollback":
                        if (failOnUse.get()) throw new SQLException("rollback failed");
                        calls.add("rollback");
                        return null;
                      case "toString":
                        return "StubConnection";
                      case "equals":
                        return proxy == args[0];
                      case "hashCode":
                        return System.identityHashCode(proxy);
                      default:
                        throw new UnsupportedOperationException(method.getName());
                    }
                  });
    }
  }

  /** A datasource stub that vends a fresh recording connection, or fails on demand. */
  private static final class StubDataSource {
    final AtomicBoolean fail = new AtomicBoolean(false);
    final AtomicInteger requests = new AtomicInteger(0);
    final List<StubConnection> vended = new ArrayList<>();
    final DataSource dataSource;

    StubDataSource() {
      dataSource =
          (DataSource)
              Proxy.newProxyInstance(
                  getClass().getClassLoader(),
                  new Class<?>[] {DataSource.class},
                  (proxy, method, args) -> {
                    if ("getConnection".equals(method.getName())
                        && (args == null || args.length == 0)) {
                      requests.incrementAndGet();
                      if (fail.get()) throw new SQLException("no connections available");
                      StubConnection stub = new StubConnection();
                      vended.add(stub);
                      return stub.connection;
                    }
                    if ("toString".equals(method.getName())) return "StubDataSource";
                    throw new UnsupportedOperationException(method.getName());
                  });
    }
  }

  @Test
  public void testPooledReadUsesReadDataSource() throws Exception {
    StubDataSource read = new StubDataSource();
    StubDataSource write = new StubDataSource();
    JdbcPooledConnectionStrategy strategy =
        new JdbcPooledConnectionStrategy(read.dataSource, write.dataSource);

    Connection c = strategy.getReadConnection();

    assertSame(c, read.vended.get(0).connection, "read connection should come from the read pool");
    assertEquals(
        read.vended.get(0).calls,
        List.of("setAutoCommit:false"),
        "read connection should have autoCommit disabled");
    assertEquals(write.requests.get(), 0, "write pool should be untouched");
  }

  @Test
  public void testPooledReadFailsOverToWriteDataSourcePermanently() throws Exception {
    StubDataSource read = new StubDataSource();
    StubDataSource write = new StubDataSource();
    read.fail.set(true);
    JdbcPooledConnectionStrategy strategy =
        new JdbcPooledConnectionStrategy(read.dataSource, write.dataSource);

    Connection first = strategy.getReadConnection();
    assertSame(
        first, write.vended.get(0).connection, "failed read should fall back to the write pool");

    read.fail.set(false); // even a recovered read pool is not retried
    Connection second = strategy.getReadConnection();
    assertSame(second, write.vended.get(1).connection, "later reads should stay on the write pool");
    assertEquals(read.requests.get(), 1, "read pool should not be consulted after failover");
  }

  @Test
  public void testPooledReadThrowsWhenBothDataSourcesFail() {
    StubDataSource read = new StubDataSource();
    StubDataSource write = new StubDataSource();
    read.fail.set(true);
    write.fail.set(true);
    JdbcPooledConnectionStrategy strategy =
        new JdbcPooledConnectionStrategy(read.dataSource, write.dataSource);

    expectThrows(CpoException.class, strategy::getReadConnection);
  }

  @Test
  public void testPooledWriteConnection() throws Exception {
    StubDataSource read = new StubDataSource();
    StubDataSource write = new StubDataSource();
    JdbcPooledConnectionStrategy strategy =
        new JdbcPooledConnectionStrategy(read.dataSource, write.dataSource);

    Connection c = strategy.getWriteConnection();

    assertSame(c, write.vended.get(0).connection);
    assertEquals(write.vended.get(0).calls, List.of("setAutoCommit:false"));

    write.fail.set(true);
    expectThrows(CpoException.class, strategy::getWriteConnection);
  }

  @Test
  public void testPooledLocalReleaseOperations() throws Exception {
    StubDataSource read = new StubDataSource();
    StubDataSource write = new StubDataSource();
    JdbcPooledConnectionStrategy strategy =
        new JdbcPooledConnectionStrategy(read.dataSource, write.dataSource);

    // null-safe
    strategy.closeLocalConnection(null);
    strategy.commitLocalConnection(null);
    strategy.rollbackLocalConnection(null);

    StubConnection stub = new StubConnection();
    strategy.commitLocalConnection(stub.connection);
    strategy.rollbackLocalConnection(stub.connection);
    strategy.closeLocalConnection(stub.connection);
    assertEquals(stub.calls, List.of("commit", "rollback", "close"));

    // closing an already-closed connection is a no-op
    strategy.closeLocalConnection(stub.connection);
    assertEquals(stub.calls, List.of("commit", "rollback", "close"), "no second close expected");

    // a connection that throws on use must not propagate out of the release operations
    StubConnection broken = new StubConnection();
    broken.failOnUse.set(true);
    strategy.commitLocalConnection(broken.connection);
    strategy.rollbackLocalConnection(broken.connection);
    strategy.closeLocalConnection(broken.connection);
    assertTrue(broken.calls.isEmpty(), "broken connection should have swallowed every operation");
  }

  @Test
  public void testPinnedConnectionIsSharedAndLazilyRepinned() throws Exception {
    StubDataSource read = new StubDataSource();
    StubDataSource write = new StubDataSource();
    JdbcPinnedConnectionStrategy strategy =
        new JdbcPinnedConnectionStrategy(
            new JdbcPooledConnectionStrategy(read.dataSource, write.dataSource));

    assertTrue(strategy.isConnectionClosed(), "no connection pinned yet");

    Connection first = strategy.getReadConnection();
    assertSame(
        first, strategy.getWriteConnection(), "reads and writes share the pinned connection");
    assertSame(first, strategy.getConnection());
    assertFalse(strategy.isConnectionClosed());
    assertEquals(write.requests.get(), 1, "exactly one connection should have been pinned");
    assertEquals(read.requests.get(), 0, "the pinned connection always comes from the write pool");

    // the pinned connection going stale is visible through isConnectionClosed
    write.vended.get(0).closed.set(true);
    assertTrue(strategy.isConnectionClosed());

    strategy.clearConnection();
    Connection second = strategy.getConnection();
    assertNotSame(second, first, "a cleared strategy should pin a fresh connection");
    assertEquals(write.requests.get(), 2);
  }

  @Test
  public void testPinnedLocalReleaseOperationsAreNoOps() throws Exception {
    StubDataSource read = new StubDataSource();
    StubDataSource write = new StubDataSource();
    JdbcPinnedConnectionStrategy strategy =
        new JdbcPinnedConnectionStrategy(
            new JdbcPooledConnectionStrategy(read.dataSource, write.dataSource));

    Connection pinned = strategy.getConnection();
    StubConnection stub = write.vended.get(0);
    stub.calls.clear(); // drop the setAutoCommit recorded during pinning

    strategy.commitLocalConnection(pinned);
    strategy.rollbackLocalConnection(pinned);
    strategy.closeLocalConnection(pinned);

    assertTrue(
        stub.calls.isEmpty(), "local release operations must not touch the pinned connection");
    assertFalse(strategy.isConnectionClosed(), "the pinned connection must remain usable");
  }
}
