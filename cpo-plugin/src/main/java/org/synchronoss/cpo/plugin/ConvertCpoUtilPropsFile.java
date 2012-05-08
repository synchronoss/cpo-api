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
package org.synchronoss.cpo.plugin;

import org.apache.maven.plugin.*;
import org.apache.xmlbeans.XmlOptions;
import org.synchronoss.cpo.core.cpoCoreConfig.*;
import org.synchronoss.cpo.jdbc.cpoJdbcConfig.*;
import org.synchronoss.cpo.util.cpoUtilConfig.*;

import java.io.*;
import java.util.*;

/**
 * @goal convertcpoutilpropsfile
 */
public class ConvertCpoUtilPropsFile extends AbstractMojo {

  private final File oldPropFile = new File(System.getProperties().getProperty("user.home"), ".cpoutil.properties");

  private final File cpoUtilConfigDir = new File(System.getProperties().getProperty("user.home"), ".cpoutil");
  private final File cpoUtilConfigFile = new File(cpoUtilConfigDir, "CpoUtilConfig.xml");

  private static final String JDBC_CONFIG_PROCESSOR = "org.synchronoss.cpo.jdbc.config.JdbcCpoConfigProcessor";
  private static final String DEFAULT_META_DESCRIPTOR = "cpoutil";
  private static final String PARAM_DELIM = ";";
  private static final String PARAM_ASSIGNMENT = "=";

  private static final String PROP_WLSURL="cpoutil.wls.url.";
  /*
  private static final String PROP_WLSUSER="cpoutil.wls.user.";
  private static final String PROP_WLSPASS="cpoutil.wls.pass.";
  private static final String PROP_WLSCONNPOOL="cpoutil.wls.connpool.";
  private static final String PROP_WLSINITCTXFCTRY="cpoutil.wls.initialcontextfactory.";
  private static final String PROP_CPONAME="cpoutil.wls.cponame.";
  private static final String PROP_THEME_URL="cpoutil.app.theme_url";
   */

  private static final String PROP_JDBC_URL="cpoutil.jdbc.url.";
  private static final String PROP_JDBC_DRIVER="cpoutil.jdbc.driver.";
  private static final String PROP_JDBC_PARAMS="cpoutil.jdbc.params.";

	public void execute() throws MojoExecutionException {
		getLog().info("Converting CpoUtil properties file to XML...");

    getLog().info("Converting: " + oldPropFile.getAbsolutePath());
    getLog().info("Destination: " + cpoUtilConfigFile.getAbsolutePath());

    if (!oldPropFile.exists() && !oldPropFile.canRead()) {
      throw new MojoExecutionException("Cannot find cpoutil properties file: " + oldPropFile.getAbsolutePath());
    }

    if (cpoUtilConfigFile.exists()) {
      throw new MojoExecutionException("Configuration already exists, aborting: " + cpoUtilConfigFile.getAbsolutePath());
    }

    Properties oldProps = new Properties();

    if (oldPropFile.exists() && oldPropFile.canRead()) {
      try {
        oldProps.load(new FileInputStream(oldPropFile));
      } catch (IOException ex) {
        throw new MojoExecutionException(ex.getMessage(), ex);
      }
    }

    Set<String> servers = new TreeSet<String>();
    Enumeration propsEnum = oldProps.propertyNames();
    while (propsEnum.hasMoreElements()) {
      String name = (String)propsEnum.nextElement();
      if (name.startsWith(PROP_WLSURL)) {
        String server = name.substring(PROP_WLSURL.length());
        servers.add(server);
      }
      else if (name.startsWith(PROP_JDBC_URL)) {
        String server = name.substring(PROP_JDBC_URL.length());
        servers.add(server);
      }
    }

    if (servers.isEmpty()) {
      // if there's no servers, nothing to migrate, so just bail quietly
      return;
    }

    // create the document
    CpoUtilConfigDocument doc = CpoUtilConfigDocument.Factory.newInstance();
    CtCpoUtilConfig cpoUtilConfig = doc.addNewCpoUtilConfig();
    CtDataConfig dataConfig = cpoUtilConfig.addNewDataConfigs();

    for (String server : servers) {
      getLog().info("Converting " + server);
      if (oldProps.getProperty(PROP_JDBC_URL + server) == null && oldProps.getProperty(PROP_JDBC_URL + server) == null) {
        // weblogic
        // TODO - do we need this?
      } else {
        // jdbc
        String userName = null;
        String password = null;
        String url = oldProps.getProperty(PROP_JDBC_URL + server);
        String driver = oldProps.getProperty(PROP_JDBC_DRIVER + server);
        String params = oldProps.getProperty(PROP_JDBC_PARAMS + server);

        // try to parse the user/pass out of the url
        String oraclePrefix = "jdbc:oracle:thin:";
        String hsqlPrefix = "jdbc:hsqldb:file:";
        if (url.startsWith(oraclePrefix)) {
          String userPass = url.substring(oraclePrefix.length(), url.indexOf("@"));
          userName = userPass.substring(0, userPass.indexOf("/"));
          password = userPass.substring(userPass.indexOf("/") + 1);
        } else if (url.startsWith(hsqlPrefix)) {
          String userPass = url.substring(url.indexOf("user="));
          userName = userPass.substring("user=".length(), userPass.indexOf(";"));
          password = userPass.substring(userPass.indexOf(";")).substring("password=".length() + 1);
        }

        CtJdbcConfig jdbcConfig = CtJdbcConfig.Factory.newInstance();
        jdbcConfig.setCpoConfigProcessor(JDBC_CONFIG_PROCESSOR);
        CtMetaDescriptor metaDescriptor = jdbcConfig.addNewMetaDescriptor();
        metaDescriptor.setName(DEFAULT_META_DESCRIPTOR);

        CtJdbcReadWriteConfig rwc = jdbcConfig.addNewReadWriteConfig();
        rwc.setUser(userName);
        rwc.setPassword(password);
        rwc.setUrl(url);
        rwc.setDriverClassName(driver);

        if (params != null && !params.isEmpty()) {
          StringTokenizer st = new StringTokenizer(params, PARAM_DELIM);
          while (st.hasMoreTokens()) {
            String token = st.nextToken();
            StringTokenizer stNameValue = new StringTokenizer(token, PARAM_ASSIGNMENT);
            String name = null, value = null;
            if (stNameValue.hasMoreTokens())
              name = stNameValue.nextToken();
            if (stNameValue.hasMoreTokens())
              value = stNameValue.nextToken();

            CtProperty prop = rwc.addNewProperty();
            prop.setName(name);
            prop.setValue(value);
          }
        }

        CtDataSourceConfig dataSourceConfig = dataConfig.addNewDataConfig();
        dataSourceConfig.set(jdbcConfig);
      }
    }

    // save the file
    XmlOptions xo = new XmlOptions();
    xo.setCharacterEncoding("utf-8");
    xo.setSaveAggressiveNamespaces();
    xo.setSaveNamespacesFirst();
    xo.setSavePrettyPrint();
    xo.setUseDefaultNamespace();

    try {
      doc.save(cpoUtilConfigFile, xo);
    } catch (Exception ex) {
      throw new MojoExecutionException(ex.getMessage(), ex);
    }
	}
}

