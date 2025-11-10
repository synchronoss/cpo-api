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
package org.synchronoss.cpo.cassandra;

import com.github.terma.javaniotcpproxy.StaticTcpProxyConfig;
import com.github.terma.javaniotcpproxy.TcpProxy;
import com.github.terma.javaniotcpproxy.TcpProxyConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.testcontainers.cassandra.CassandraContainer;
import org.testcontainers.utility.DockerImageName;
import org.testng.ISuite;
import org.testng.ISuiteListener;

import java.sql.SQLException;

public class CassandraSuiteListener implements ISuiteListener {
    public static final String PROP_INIT_SCRIPT="cassandra.initScript";
    public static final String PROP_CPO_CONFIG = "cassandra.cpoConfig";
    public static final String PROP_CONTACT_POINT = "cassandra.contactPoint";
    public static final String PROP_KEY_SPACE = "cassandra.keyspace>cpokeyspace";
    public static final String PROP_META_XML = "cassandra.metaXml";
    public static final String PROP_NATIVE_PORT = "cassandra.nativeport";
    public static final String PROP_STORAGE_PORT = "cassandra.storageport";
    public static final String PROP_SSL_STORAGE_PORT = "cassandra.sslstorageport";
    public static final String PROP_RPC_PORT = "cassandra.rpcport";
    public static final String PROP_IMAGE = "cassandra.image";

    private Logger logger = LoggerFactory.getLogger(CassandraSuiteListener.class);
    private CassandraContainer cassandraContainer=null;
    private TcpProxy tcpProxy = null;

    @Override
    public void onStart(ISuite suite) {
        String image = suite.getParameter(PROP_IMAGE);
        String cpoConfig = suite.getParameter(PROP_CPO_CONFIG);
        String initScript = suite.getParameter(PROP_INIT_SCRIPT);
        String nativePort = suite.getParameter(PROP_NATIVE_PORT);

        cassandraContainer = new CassandraContainer(DockerImageName.parse(image))
                .withInitScript(initScript);

        if (cassandraContainer != null) {
            cassandraContainer.start();

            // Now map the random port to something we can use in the config file
            TcpProxyConfig config = new StaticTcpProxyConfig(Integer.parseInt(nativePort), cassandraContainer.getHost(), cassandraContainer.getFirstMappedPort());
            config.setWorkerCount(1);

            // init proxy
            tcpProxy = new TcpProxy(config);
            // start proxy
            tcpProxy.start();

        }

        // Tell CPO where to find the config for the test
        System.setProperty(CpoAdapterFactoryManager.CPO_CONFIG, cpoConfig);
        // Go Ahead and load them
        CpoAdapterFactoryManager.loadAdapters();

        logger.debug("Cassandra Host:"+ cassandraContainer.getHost());

    }

    @Override
    public void onFinish(ISuite suite) {
        if (tcpProxy!=null)
            tcpProxy.shutdown();
        if (cassandraContainer!=null)
            cassandraContainer.close();
        logger.debug("onFinish");
    }
}
