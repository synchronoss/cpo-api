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

import com.github.terma.javaniotcpproxy.TcpProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.JdbcDatabaseContainer;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.PostgreSQLContainer;

public abstract class JdbcDbContainerBase {
  private static final String MYSQL = "mysql";
  private static final String MARIADB = "mariadb";
  private static final String POSTGRES = "postgres";
  private static final String ORACLE_XE = "oracle-xe";

  private static String dbType;
  private static String dbUser;
  private static String dbPswd;
  private static String dbName;
  private static String initScript;
  private static Logger logger;
  private static TcpProxy proxy=null;

  public static JdbcDatabaseContainer jdbcContainer;

  static {
    dbType = JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_DB_TYPE);
    dbUser = JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_DB_USER);
    dbPswd = JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_DB_PSWD);
    dbName = JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_DB_NAME);
    initScript = JdbcJUnitProperty.getProperty(JdbcJUnitProperty.PROP_INIT_SCRIPT);
    logger = LoggerFactory.getLogger(JdbcDatabaseContainer.class);
    jdbcContainer = createJdbcContainer(dbType, initScript, dbUser, dbPswd, dbName);
    if (jdbcContainer!=null)
      jdbcContainer.start();
  }

  private static JdbcDatabaseContainer createJdbcContainer(String dbType, String initScript, String dbUser, String dbPswd, String dbName) {
    logger.debug("Creating a container for:"+dbType);
    switch (dbType) {
      case MYSQL: return new MySQLContainer().withInitScript(initScript).withUsername(dbUser).withPassword(dbPswd).withDatabaseName(dbName);
      case MARIADB: return new MariaDBContainer().withInitScript(initScript).withUsername(dbUser).withPassword(dbPswd).withDatabaseName(dbName);
      case POSTGRES: return new PostgreSQLContainer().withInitScript(initScript).withUsername(dbUser).withPassword(dbPswd).withDatabaseName(dbName);
      default: logger.debug("No Container to start, unknown dbType:"+dbType);
    }
    return null;
  }
}
