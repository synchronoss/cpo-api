/*
 * Copyright (C) 2003-2025 David E. Berry
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

import com.github.terma.javaniotcpproxy.StaticTcpProxyConfig;
import com.github.terma.javaniotcpproxy.TcpProxy;
import com.github.terma.javaniotcpproxy.TcpProxyConfig;
import org.h2.tools.RunScript;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.testcontainers.containers.*;
import org.testcontainers.utility.DockerImageName;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.h2.tools.Server;

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
    public static final String PROP_DB_IMAGE = "db.image";

    private Logger logger = LoggerFactory.getLogger(JdbcSuiteListener.class);
    private JdbcDatabaseContainer<?> jdbcContainer=null;
    private Server h2Server = null;
    private TcpProxy tcpProxy = null;

    @Override
    public void onStart(ISuite suite) {
        String dbType = suite.getParameter(PROP_DB_TYPE);
        String dbInitScript = suite.getParameter(PROP_INIT_SCRIPT);
        String dbUser = suite.getParameter(PROP_DB_USER);
        String dbPasswd = suite.getParameter(PROP_DB_PSWD);
        String dbName = suite.getParameter(PROP_DB_NAME);
        String dbPort = suite.getParameter(PROP_DB_PORT);
        String dbUrl = suite.getParameter(PROP_DB_URL);
        String dbImage = suite.getParameter(PROP_DB_IMAGE);
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
            jdbcContainer = createJdbcContainer(dbType, dbInitScript, dbUser, dbPasswd, dbName, dbImage);

            if (jdbcContainer != null) {
                jdbcContainer.start();

                // Now map the random port to something we can use in the config file
                TcpProxyConfig config = new StaticTcpProxyConfig(Integer.parseInt(dbPort), jdbcContainer.getHost(), jdbcContainer.getFirstMappedPort());
                config.setWorkerCount(1);

                // init proxy
                tcpProxy = new TcpProxy(config);
                // start proxy
                tcpProxy.start();

            }
        }

        // Tell CPO where to find the config for the test
        System.setProperty(CpoAdapterFactoryManager.CPO_CONFIG, cpoConfig);
        // Go Ahead and load them
        CpoAdapterFactoryManager.loadAdapters();
        logger.debug("onStart");

    }

    @Override
    public void onFinish(ISuite suite) {
        if (tcpProxy!=null)
            tcpProxy.shutdown();
        if (jdbcContainer!=null)
            jdbcContainer.close();
        if (h2Server!=null)
            h2Server.stop();
        logger.debug("onFinish");
    }

    private JdbcDatabaseContainer<?> createJdbcContainer(String dbType, String initScript, String dbUser, String dbPswd, String dbName, String image) {
        logger.debug("Creating a container for:"+dbType);
        switch (dbType) {
            case MYSQL:
                return new MySQLContainer<>(DockerImageName.parse(image))
                        .withInitScript(initScript)
                        .withUsername(dbUser)
                        .withPassword(dbPswd)
                        .withDatabaseName(dbName);
            case MARIADB:
                return new MariaDBContainer<>(DockerImageName.parse(image))
                        .withInitScript(initScript)
                        .withUsername(dbUser)
                        .withPassword(dbPswd)
                        .withDatabaseName(dbName);
            case POSTGRES:
                return new PostgreSQLContainer<>(DockerImageName.parse(image))
                        .withInitScript(initScript)
                        .withUsername(dbUser)
                        .withPassword(dbPswd)
                        .withDatabaseName(dbName);
            case ORACLE:
                return new OracleContainer(DockerImageName.parse(image))
                        .withInitScript(initScript)
                        .withUsername(dbUser)
                        .withPassword(dbPswd)
                        .withDatabaseName(dbName);
            default:
                logger.debug("No Container to start, unknown dbType:" + dbType);
        }
        return null;
    }
}
