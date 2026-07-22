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
import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.h2.jdbcx.JdbcDataSource;
import org.synchronoss.cpo.core.CpoException;
import org.testng.annotations.Test;

/** Unit tests for JndiJdbcDataSourceInfo using a stubbed JNDI Context. */
public class JndiJdbcDataSourceInfoTest {

  private static final String JNDI_NAME = "jdbc/cpoTestDataSource";

  /** Returns a JNDI Context stub whose lookup(String) resolves only the given binding. */
  private static Context stubContext(String boundName, Object boundValue) {
    return (Context)
        Proxy.newProxyInstance(
            JndiJdbcDataSourceInfoTest.class.getClassLoader(),
            new Class<?>[] {Context.class},
            (proxy, method, args) -> {
              if ("lookup".equals(method.getName())
                  && args != null
                  && args.length == 1
                  && boundName != null
                  && boundName.equals(args[0])) {
                return boundValue;
              }
              if ("lookup".equals(method.getName())) {
                throw new NamingException("Name not bound: " + args[0]);
              }
              if ("close".equals(method.getName())) {
                return null;
              }
              throw new UnsupportedOperationException(method.getName());
            });
  }

  @Test
  public void testGetDataSourceFromContext() throws Exception {
    JdbcDataSource h2DataSource = new JdbcDataSource();
    JndiJdbcDataSourceInfo info =
        new JndiJdbcDataSourceInfo(JNDI_NAME, 10, 20, stubContext(JNDI_NAME, h2DataSource));

    assertEquals(info.getDataSourceName(), JNDI_NAME);
    assertEquals(info.getFetchSize(), 10);
    assertEquals(info.getBatchSize(), 20);

    DataSource ds = (DataSource) info.getDataSource();
    assertSame(ds, h2DataSource, "lookup should return the bound DataSource");

    // the created DataSource is cached; a second call returns the same instance
    assertSame(info.getDataSource(), ds);
  }

  @Test
  public void testLookupFailureThrowsCpoException() {
    JndiJdbcDataSourceInfo info =
        new JndiJdbcDataSourceInfo("jdbc/notBound", 10, 20, stubContext(JNDI_NAME, null));

    CpoException ce = expectThrows(CpoException.class, info::getDataSource);
    assertTrue(
        ce.getMessage().contains("Error instantiating DataSource"),
        "unexpected message: " + ce.getMessage());
  }

  @Test
  public void testNoInitialContextThrowsCpoException() {
    // the 3-arg constructor falls back to new InitialContext(); with no JNDI provider
    // configured in the test JVM the lookup must fail with a CpoException
    JndiJdbcDataSourceInfo info = new JndiJdbcDataSourceInfo(JNDI_NAME, 10, 20);
    expectThrows(CpoException.class, info::getDataSource);
  }
}
