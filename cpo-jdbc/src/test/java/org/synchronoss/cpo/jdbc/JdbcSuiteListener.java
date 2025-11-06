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

import org.h2.tools.RunScript;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.testcontainers.containers.*;
import org.testcontainers.utility.DockerImageName;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.h2.tools.Server;

import java.nio.charset.Charset;
import java.sql.SQLException;

public class JdbcSuiteListener implements ISuiteListener {
  private static final String H2 = "h2";
  private static final String MYSQL = "mysql";
  private static final String MARIADB = "mariadb";
  private static final String POSTGRES = "postgres";
  private static final String ORACLE = "oracle";
  public static final String PROP_INIT_SCRIPT = "db.initScript";
  public static final String PROP_CPO_CONFIG = "db.cpoConfig";
  public static final String PROP_DB_TYPE = "db.type";
  public static final String PROP_DB_PORT = "db.port";
  public static final String PROP_DB_USER = "db.user";
  public static final String PROP_DB_PSWD = "db.pswd";
  public static final String PROP_DB_NAME = "db.database";
  public static final String PROP_DB_URL = "db.url";
  public static final String PROP_DB_BLOBSUPPORT = "db.blobsupport";
  public static final String PROP_DB_CALLSUPORT = "db.callsupport";
  public static final String PROP_DB_SELECT4UPDATE = "db.select4update";
  public static final String PROP_DB_MILLISUPPORT = "db.millisupport";

  private Logger logger = LoggerFactory.getLogger(JdbcSuiteListener.class);
  private JdbcDatabaseContainer<?> jdbcContainer=null;
  private Server h2Server = null;

  @Override
  public void onStart(ISuite suite) {
      String dbType = suite.getParameter(PROP_DB_TYPE);
      String dbInitScript = suite.getParameter(PROP_INIT_SCRIPT);
      String dbUser = suite.getParameter(PROP_DB_USER);
      String dbPasswd = suite.getParameter(PROP_DB_PSWD);
      String dbName = suite.getParameter(PROP_DB_NAME);
      String dbPort = suite.getParameter(PROP_DB_PORT);
      String dbUrl = suite.getParameter(PROP_DB_URL);
      String cpoConfig = suite.getParameter(PROP_CPO_CONFIG);
      String initScript = suite.getParameter(PROP_INIT_SCRIPT);

      if (dbType.equals(H2)) {
          try {
              h2Server = Server.createTcpServer("-tcpPort", dbPort, "-tcpAllowOthers", "-baseDir", "~/", "-ifNotExists");
              h2Server.start();
              RunScript.execute(dbUrl, dbUser, dbPasswd, initScript, null,false);
          } catch (SQLException e) {
              logger.error(e.getMessage());
              System.exit(1);
          }
      } else {
          jdbcContainer = createJdbcContainer(dbType, dbInitScript, dbUser, dbPasswd, dbName, dbPort);

          if (jdbcContainer != null) {
              jdbcContainer.start();
              logger.error("Trying to load cpoConfig <"+cpoConfig+">");
              System.setProperty(CpoAdapterFactoryManager.CPO_CONFIG, cpoConfig);
              logger.error("Loaded cpoConfig <"+cpoConfig+">");
          }
      }

    logger.debug("onStart");

  }

  @Override
  public void onFinish(ISuite suite) {
    if (jdbcContainer!=null)
        jdbcContainer.close();
    if (h2Server!=null)
        h2Server.stop();
    logger.debug("onFinish");
  }

  private JdbcDatabaseContainer<?> createJdbcContainer(String dbType, String initScript, String dbUser, String dbPswd, String dbName, String dbPort) {
    logger.debug("Creating a container for:"+dbType);
    JdbcDatabaseContainer<?> jdbcContainer = null;
    switch (dbType) {
      case MYSQL: jdbcContainer = new MySQLContainer<>(); break;
      case MARIADB: jdbcContainer = new MariaDBContainer<>(); break;
      case POSTGRES: jdbcContainer = new PostgreSQLContainer<>(); break;
      case ORACLE: jdbcContainer = new OracleContainer(); break;
      default: logger.debug("No Container to start, unknown dbType:"+dbType);
    }

    if (initScript != null && !initScript.isEmpty())
        jdbcContainer = jdbcContainer.withInitScript(initScript);

    if (dbUser != null && !dbUser.isEmpty())
        jdbcContainer = jdbcContainer.withUsername(dbUser);
    if (dbPswd != null && !dbPswd.isEmpty())
        jdbcContainer = jdbcContainer.withPassword(dbPswd);
    if (dbName != null && !dbName.isEmpty())
        jdbcContainer = jdbcContainer.withDatabaseName(dbName);
    if (dbPort != null && !dbPort.isEmpty())
        jdbcContainer = jdbcContainer.withExposedPorts(Integer.parseInt(dbPort));

    return jdbcContainer;
  }
}

