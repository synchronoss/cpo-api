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
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.sql.DataSource;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.DataSourceInfo;
import org.testng.annotations.Test;

/**
 * Pure-JVM unit tests for JdbcCpoTrxAdapter's transaction control and for the capability probe's
 * failure path. The adapter is constructed against reflective stub datasources, so the commit,
 * rollback, close, and isClosed error branches — unreachable through the integration suites, which
 * only exercise healthy connections — are driven directly.
 *
 * @author david berry
 */
public class JdbcCpoTrxAdapterUnitTest {

  /** A recording stub connection whose failure behavior can be toggled per operation. */
  private static final class StubConnection {
    final AtomicBoolean closed = new AtomicBoolean(false);
    final AtomicBoolean failOnUse = new AtomicBoolean(false);
    final List<String> calls = new ArrayList<>();
    final Connection connection;

    StubConnection() {
      DatabaseMetaData metaData =
          (DatabaseMetaData)
              Proxy.newProxyInstance(
                  getClass().getClassLoader(),
                  new Class<?>[] {DatabaseMetaData.class},
                  (proxy, method, args) -> {
                    if ("supportsBatchUpdates".equals(method.getName())) return true;
                    if ("toString".equals(method.getName())) return "StubDatabaseMetaData";
                    throw new UnsupportedOperationException(method.getName());
                  });
      connection =
          (Connection)
              Proxy.newProxyInstance(
                  getClass().getClassLoader(),
                  new Class<?>[] {Connection.class},
                  (proxy, method, args) -> {
                    switch (method.getName()) {
                      case "setAutoCommit":
                        return null;
                      case "getMetaData":
                        return metaData;
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

  /** Vends recording stub connections as a DataSourceInfo for the adapter constructor. */
  private static final class StubDataSourceInfo implements DataSourceInfo<DataSource> {
    final AtomicBoolean fail = new AtomicBoolean(false);
    final List<StubConnection> vended = new ArrayList<>();
    private final DataSource dataSource;

    StubDataSourceInfo() {
      dataSource =
          (DataSource)
              Proxy.newProxyInstance(
                  getClass().getClassLoader(),
                  new Class<?>[] {DataSource.class},
                  (proxy, method, args) -> {
                    if ("getConnection".equals(method.getName())
                        && (args == null || args.length == 0)) {
                      if (fail.get()) throw new SQLException("no connections available");
                      StubConnection stub = new StubConnection();
                      vended.add(stub);
                      return stub.connection;
                    }
                    if ("toString".equals(method.getName())) return "StubDataSource";
                    throw new UnsupportedOperationException(method.getName());
                  });
    }

    @Override
    public String getDataSourceName() {
      return "trx-adapter-unit-test";
    }

    @Override
    public int getFetchSize() {
      return 0;
    }

    @Override
    public int getBatchSize() {
      return 100;
    }

    @Override
    public DataSource getDataSource() {
      return dataSource;
    }

    StubConnection lastVended() {
      return vended.get(vended.size() - 1);
    }
  }

  private static JdbcCpoTrxAdapter newTrxAdapter(StubDataSourceInfo dsi) throws CpoException {
    return new JdbcCpoTrxAdapter(new JdbcCpoAdapter(null, dsi));
  }

  @Test
  public void testCapabilityProbeReleasesItsConnection() throws Exception {
    StubDataSourceInfo dsi = new StubDataSourceInfo();

    JdbcCpoAdapter adapter = new JdbcCpoAdapter(null, dsi);

    assertTrue(adapter.isBatchUpdatesSupported());
    assertEquals(dsi.vended.size(), 1, "the probe should use exactly one connection");
    assertEquals(
        dsi.vended.get(0).calls,
        List.of("rollback", "close"),
        "the probe connection should be rolled back and closed");
  }

  @Test
  public void testCapabilityProbeFailureBecomesCpoException() {
    StubDataSourceInfo dsi = new StubDataSourceInfo();
    dsi.fail.set(true);

    expectThrows(CpoException.class, () -> new JdbcCpoAdapter(null, dsi));
  }

  @Test
  public void testCommitAndRollbackUseThePinnedConnection() throws Exception {
    StubDataSourceInfo dsi = new StubDataSourceInfo();
    JdbcCpoTrxAdapter trx = newTrxAdapter(dsi);

    trx.commit();
    StubConnection pinned = dsi.lastVended();
    trx.rollback();
    trx.commit();

    assertEquals(pinned.calls, List.of("commit", "rollback", "commit"));
    assertSame(dsi.lastVended(), pinned, "every operation should reuse the pinned connection");
  }

  @Test
  public void testCommitAndRollbackFailuresBecomeCpoExceptions() throws Exception {
    StubDataSourceInfo dsi = new StubDataSourceInfo();
    JdbcCpoTrxAdapter trx = newTrxAdapter(dsi);

    trx.commit(); // pin the connection
    dsi.lastVended().failOnUse.set(true);

    expectThrows(CpoException.class, trx::commit);
    expectThrows(CpoException.class, trx::rollback);
    expectThrows(CpoException.class, trx::isClosed);
  }

  @Test
  public void testCloseRollsBackAndReleasesThePinnedConnection() throws Exception {
    StubDataSourceInfo dsi = new StubDataSourceInfo();
    JdbcCpoTrxAdapter trx = newTrxAdapter(dsi);

    trx.commit(); // pin the connection
    StubConnection pinned = dsi.lastVended();
    assertFalse(trx.isClosed());

    trx.close();

    assertEquals(pinned.calls, List.of("commit", "rollback", "close"));
    assertTrue(trx.isClosed(), "a closed trx adapter should report closed");

    // the adapter is reusable: the next transaction pins a fresh connection
    trx.commit();
    assertNotSame(dsi.lastVended(), pinned, "reuse after close should pin a new connection");
  }

  @Test
  public void testCloseSwallowsConnectionFailures() throws Exception {
    StubDataSourceInfo dsi = new StubDataSourceInfo();
    JdbcCpoTrxAdapter trx = newTrxAdapter(dsi);

    trx.commit(); // pin the connection
    dsi.lastVended().failOnUse.set(true);

    trx.close(); // rollback/close failures must not propagate

    assertTrue(trx.isClosed(), "the pinned connection should be forgotten even when close fails");
  }
}
