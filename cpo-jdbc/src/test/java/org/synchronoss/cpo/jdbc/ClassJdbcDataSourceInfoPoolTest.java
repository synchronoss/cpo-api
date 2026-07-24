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

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.logging.Logger;
import javax.sql.ConnectionEvent;
import javax.sql.DataSource;
import javax.sql.PooledConnection;
import org.h2.jdbcx.JdbcDataSource;
import org.testng.annotations.Test;

/** Exercises ClassJdbcDataSourceInfo's pooled and plain datasource paths with in-memory H2. */
public class ClassJdbcDataSourceInfoPoolTest {

  private static final String H2_MEM_URL = "jdbc:h2:mem:poolTest;DB_CLOSE_DELAY=-1";

  /** A DataSource that is not a ConnectionPoolDataSource, for the plain-DataSource branch. */
  public static class PlainDataSource implements DataSource {
    private String url;

    public PlainDataSource() {}

    public void setUrl(String url) {
      this.url = url;
    }

    @Override
    public Connection getConnection() throws SQLException {
      return DriverManager.getConnection(url);
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
      return DriverManager.getConnection(url, username, password);
    }

    @Override
    public PrintWriter getLogWriter() {
      return null;
    }

    @Override
    public void setLogWriter(PrintWriter out) {}

    @Override
    public void setLoginTimeout(int seconds) {}

    @Override
    public int getLoginTimeout() {
      return 0;
    }

    @Override
    public Logger getParentLogger() {
      return Logger.getGlobal();
    }

    @Override
    public <T> T unwrap(Class<T> iface) {
      return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
      return false;
    }
  }

  private ClassJdbcDataSourceInfo pooledInfo() {
    TreeMap<String, String> props = new TreeMap<>();
    props.put("URL", H2_MEM_URL);
    return new ClassJdbcDataSourceInfo("org.h2.jdbcx.JdbcDataSource", props, 10, 10);
  }

  @Test
  public void testPooledConnectionLifecycle() throws Exception {
    ClassJdbcDataSourceInfo info = pooledInfo();
    DataSource ds = info.getDataSource();
    assertSame(ds, info, "pooled mode uses the info object itself as the DataSource");

    // check out two connections, return one, and check out again to reuse the free one
    Connection c1 = ds.getConnection();
    Connection c2 = ds.getConnection();
    assertFalse(c1.isClosed());
    c1.close();
    Connection c3 = ds.getConnection();
    assertNotNull(c3);
    c2.close();
    c3.close();

    // one connection left checked out so close() walks both the free and used lists
    Connection c4 = ds.getConnection();
    assertNotNull(c4);
    info.close();
  }

  @Test
  public void testConnectionErrorOnUnknownConnection() throws Exception {
    ClassJdbcDataSourceInfo info = pooledInfo();
    info.getDataSource();

    // an error event for a connection the pool is not tracking must not fail
    JdbcDataSource h2 = new JdbcDataSource();
    h2.setURL(H2_MEM_URL);
    PooledConnection foreign = h2.getPooledConnection();
    info.connectionErrorOccurred(new ConnectionEvent(foreign, new SQLException("boom")));
    foreign.close();
  }

  @Test
  public void testPlainDataSourceMode() throws Exception {
    TreeMap<String, String> props = new TreeMap<>();
    props.put("Url", "jdbc:h2:mem:plainDsTest;DB_CLOSE_DELAY=-1");
    ClassJdbcDataSourceInfo info =
        new ClassJdbcDataSourceInfo(PlainDataSource.class.getName(), props, 10, 10);

    DataSource ds = info.getDataSource();
    assertNotSame(ds, info, "plain mode returns the datasource directly");
    try (Connection connection = ds.getConnection()) {
      assertFalse(connection.isClosed());
    }
  }
}
