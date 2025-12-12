package org.synchronoss.cpo.jdbc.config;

/*-
 * [[
 * jdbc
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
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

import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoAdapterFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.DataSourceInfo;
import org.synchronoss.cpo.core.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.cpoconfig.CtDataSourceConfig;
import org.synchronoss.cpo.cpoconfig.CtJdbcConfig;
import org.synchronoss.cpo.cpoconfig.CtJdbcReadWriteConfig;
import org.synchronoss.cpo.cpoconfig.CtProperty;
import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;

/**
 * Processes the cpoConfig.xml data for Jdbc
 *
 * @author dberry
 */
public class JdbcCpoConfigProcessor implements CpoConfigProcessor {

  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoConfigProcessor.class);
  private static final String PROP_URL1 = "url";
  private static final String PROP_URL2 = "URL";
  private static final String PROP_USER = "user";
  private static final String PROP_PASSWORD = "password";

  /** Default constructor */
  public JdbcCpoConfigProcessor() {}

  @Override
  public CpoAdapterFactory processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException {

    CpoAdapterFactory cpoAdapterFactory = null;

    if (cpoConfig == null || !(cpoConfig instanceof CtJdbcConfig)) {
      throw new CpoException("Invalid Jdbc Configuration Information");
    }

    CtJdbcConfig jdbcConfig = (CtJdbcConfig) cpoConfig;

    JdbcCpoMetaDescriptor metaDescriptor =
        (JdbcCpoMetaDescriptor) CpoMetaDescriptor.getInstance(jdbcConfig.getMetaDescriptorName());

    int fetchSize = jdbcConfig.getFetchSize().intValue();
    int batchSize = jdbcConfig.getBatchSize().intValue();

    // build a datasource info
    if (jdbcConfig.getReadWriteConfig() != null) {
      DataSourceInfo dataSourceInfo =
          buildDataSourceInfo(jdbcConfig.getReadWriteConfig(), fetchSize, batchSize);
      cpoAdapterFactory =
          new JdbcCpoAdapterFactory(JdbcCpoAdapter.getInstance(metaDescriptor, dataSourceInfo));
    } else {
      DataSourceInfo readDataSourceInfo =
          buildDataSourceInfo(jdbcConfig.getReadConfig(), fetchSize, batchSize);
      DataSourceInfo writeDataSourceInfo =
          buildDataSourceInfo(jdbcConfig.getWriteConfig(), fetchSize, batchSize);
      cpoAdapterFactory =
          new JdbcCpoAdapterFactory(
              JdbcCpoAdapter.getInstance(metaDescriptor, writeDataSourceInfo, readDataSourceInfo));
    }

    return cpoAdapterFactory;
  }

  private DataSourceInfo buildDataSourceInfo(
      CtJdbcReadWriteConfig readWriteConfig, int fetchSize, int batchSize) throws CpoException {
    DataSourceInfo dataSourceInfo = null;

    if (readWriteConfig.getJndiName() != null) {
      dataSourceInfo =
          new JndiJdbcDataSourceInfo(readWriteConfig.getJndiName(), fetchSize, batchSize);
    } else if (readWriteConfig.getDataSourceClassName() != null) {
      SortedMap<String, String> props = new TreeMap<>();

      if (readWriteConfig.getUrl() != null) {
        props.put(PROP_URL1, readWriteConfig.getUrl());
        props.put(PROP_URL2, readWriteConfig.getUrl());
      }

      if (readWriteConfig.getUser() != null) {
        props.put(PROP_USER, readWriteConfig.getUser());
      }

      if (readWriteConfig.getPassword() != null) {
        props.put(PROP_PASSWORD, readWriteConfig.getPassword());
      }

      for (CtProperty property : readWriteConfig.getProperty()) {
        props.put(property.getName(), property.getValue());
        logger.debug("Adding property " + property.getName() + "=" + property.getValue());
      }

      dataSourceInfo =
          new ClassJdbcDataSourceInfo(
              readWriteConfig.getDataSourceClassName(), props, fetchSize, batchSize);
    } else if (readWriteConfig.getDriverClassName() != null) {
      if (readWriteConfig.getUser() != null) {
        dataSourceInfo =
            new DriverJdbcDataSourceInfo(
                readWriteConfig.getDriverClassName(),
                readWriteConfig.getUrl(),
                readWriteConfig.getUser(),
                readWriteConfig.getPassword(),
                fetchSize,
                batchSize);
      } else if (!readWriteConfig.getProperty().isEmpty()) {
        Properties props = new Properties();
        for (CtProperty property : readWriteConfig.getProperty()) {
          props.put(property.getName(), property.getValue());
        }
        dataSourceInfo =
            new DriverJdbcDataSourceInfo(
                readWriteConfig.getDriverClassName(),
                readWriteConfig.getUrl(),
                props,
                fetchSize,
                batchSize);
      } else {
        dataSourceInfo =
            new DriverJdbcDataSourceInfo(
                readWriteConfig.getDriverClassName(),
                readWriteConfig.getUrl(),
                fetchSize,
                batchSize);
      }
    }

    logger.debug("Created DataSourceInfo: " + dataSourceInfo);
    return dataSourceInfo;
  }
}
