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
package org.synchronoss.cpo.jdbc;

import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcSuiteListener implements ISuiteListener {
  private static final String MYSQL = "mysql";
  private static final String MARIADB = "mariadb";
  private static final String POSTGRES = "postgres";
  public static final String PROP_INIT_SCRIPT = "cpo.db.initScript";
  public static final String PROP_DB_TYPE = "cpo.db";
  public static final String PROP_DB_PORT = "cpo.db.port";
  public static final String PROP_DB_USER = "cpo.db.user";
  public static final String PROP_DB_PSWD = "cpo.db.pswd";
  public static final String PROP_DB_NAME = "cpo.db.database";

  private Logger logger = LoggerFactory.getLogger(JdbcSuiteListener.class);
  private JdbcDatabaseContainer<?> jdbcContainer=null;

  @Override
  public void onStart(ISuite suite) {
    jdbcContainer = createJdbcContainer(
            suite.getParameter(PROP_DB_TYPE),
            suite.getParameter(PROP_INIT_SCRIPT),
            suite.getParameter(PROP_DB_USER),
            suite.getParameter(PROP_DB_PSWD),
            suite.getParameter(PROP_DB_NAME),
            Integer.parseInt(suite.getParameter(PROP_DB_PORT)));
    if (jdbcContainer!=null)
      jdbcContainer.start();
    logger.debug("onStart");
  }

  @Override
  public void onFinish(ISuite suite) {
    jdbcContainer.close();
    logger.debug("onFinish");
  }

  private JdbcDatabaseContainer<?> createJdbcContainer(String dbType, String initScript, String dbUser, String dbPswd, String dbName, int dbPort) {
    logger.debug("Creating a container for:"+dbType);
    switch (dbType) {
      case MYSQL: return new MySQLContainer<>().withInitScript(initScript).withUsername(dbUser).withPassword(dbPswd).withDatabaseName(dbName).withExposedPorts(dbPort);
      case MARIADB: return new MariaDBContainer<>().withInitScript(initScript).withUsername(dbUser).withPassword(dbPswd).withDatabaseName(dbName).withExposedPorts(dbPort);
      case POSTGRES: return new PostgreSQLContainer<>().withInitScript(initScript).withUsername(dbUser).withPassword(dbPswd).withDatabaseName(dbName).withExposedPorts(dbPort);
      default: logger.debug("No Container to start, unknown dbType:"+dbType);
    }
    return null;
  }
}

