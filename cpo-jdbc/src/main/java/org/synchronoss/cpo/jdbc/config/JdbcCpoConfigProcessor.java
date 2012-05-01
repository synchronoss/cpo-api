/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc.config;

import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.DataSourceInfo;
import org.synchronoss.cpo.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;
import org.synchronoss.cpo.core.cpoCoreConfig.CtMetaDescriptor;
import org.synchronoss.cpo.jdbc.ClassDataSourceInfo;
import org.synchronoss.cpo.jdbc.DriverDataSourceInfo;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;
import org.synchronoss.cpo.jdbc.JndiDataSourceInfo;
import org.synchronoss.cpo.jdbc.cpoJdbcConfig.CtJdbcConfig;
import org.synchronoss.cpo.jdbc.cpoJdbcConfig.CtJdbcReadWriteConfig;
import org.synchronoss.cpo.jdbc.cpoJdbcConfig.CtProperty;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 *
 * @author dberry
 */
public class JdbcCpoConfigProcessor implements CpoConfigProcessor {

  private static final String PROP_URL1 = "url";
  private static final String PROP_URL2 = "URL";
  private static final String PROP_USER = "user";
  private static final String PROP_PASSWORD = "password";
  
  public JdbcCpoConfigProcessor() {
  }

  @Override
  public CpoAdapter processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException {

    CpoAdapter cpoAdapter = null;

    if (cpoConfig == null || !(cpoConfig instanceof CtJdbcConfig)) {
      throw new CpoException("Invalid Jdbc Configuration Information");
    }

    CtJdbcConfig jdbcConfig = (CtJdbcConfig) cpoConfig;
    CtMetaDescriptor ctMetaDescriptor = jdbcConfig.getMetaDescriptor();
    
    JdbcCpoMetaDescriptor metaDescriptor = (JdbcCpoMetaDescriptor) CpoMetaDescriptor.getInstance(jdbcConfig.getMetaDescriptor().getName(), jdbcConfig.getMetaDescriptor().getMetaXmlArray());
    
    if (jdbcConfig.isSetSupportsBlobs())
      metaDescriptor.setSupportsBlobs(jdbcConfig.getSupportsBlobs());
    
    if (jdbcConfig.isSetSupportsCalls())
      metaDescriptor.setSupportsCalls(jdbcConfig.getSupportsCalls());
    
    if (jdbcConfig.isSetSupportsMillis())
      metaDescriptor.setSupportsMillis(jdbcConfig.getSupportsMillis());
    
    if (jdbcConfig.isSetSupportsSelect4Update())
      metaDescriptor.setSupportsSelect4Update(jdbcConfig.getSupportsSelect4Update());
    
    // build a datasource info
    if (jdbcConfig.isSetReadWriteConfig()) {
      DataSourceInfo dataSourceInfo = buildDataSourceInfo(jdbcConfig.getReadWriteConfig());
      cpoAdapter = JdbcCpoAdapter.getInstance(metaDescriptor, dataSourceInfo);
    } else {
      DataSourceInfo readDataSourceInfo = buildDataSourceInfo(jdbcConfig.getReadConfig());
      DataSourceInfo writeDataSourceInfo = buildDataSourceInfo(jdbcConfig.getWriteConfig());
      cpoAdapter = JdbcCpoAdapter.getInstance(metaDescriptor, writeDataSourceInfo, readDataSourceInfo);
    }
    
    return cpoAdapter;
  }
  
  private DataSourceInfo buildDataSourceInfo(CtJdbcReadWriteConfig readWriteConfig) throws CpoException {
    DataSourceInfo dataSourceInfo = null;
    
    if (readWriteConfig.isSetJndiName()) {
      dataSourceInfo = new JndiDataSourceInfo(readWriteConfig.getJndiName());
    } else if (readWriteConfig.isSetDataSourceClassName()) {
      SortedMap<String, String> props = new TreeMap<String, String>();

      if (readWriteConfig.isSetUrl()) {
        props.put(PROP_URL1, readWriteConfig.getUrl());
        props.put(PROP_URL2, readWriteConfig.getUrl());
      }

      if (readWriteConfig.isSetUser()) {
        props.put(PROP_USER, readWriteConfig.getUser());
      }

      if (readWriteConfig.isSetPassword()) {
        props.put(PROP_PASSWORD, readWriteConfig.getPassword());
      }
      
      for (CtProperty property : readWriteConfig.getPropertyArray()){
        props.put(property.getName(), property.getValue());
      }

      dataSourceInfo = new ClassDataSourceInfo(readWriteConfig.getDataSourceClassName(), props);
    } else if (readWriteConfig.isSetDriverClassName()) {
      if (readWriteConfig.isSetUser()) {
        dataSourceInfo = new DriverDataSourceInfo(readWriteConfig.getDriverClassName(), readWriteConfig.getUrl(), readWriteConfig.getUser(), readWriteConfig.getPassword());
      } else if (readWriteConfig.getPropertyArray().length>0) {
        Properties props = new Properties();
        for (CtProperty property : readWriteConfig.getPropertyArray()){
          props.put(property.getName(), property.getValue());
        }
        dataSourceInfo = new DriverDataSourceInfo(readWriteConfig.getDriverClassName(), readWriteConfig.getUrl(), props);
      } else {
        dataSourceInfo = new DriverDataSourceInfo(readWriteConfig.getDriverClassName(), readWriteConfig.getUrl());
      }
    }

    return dataSourceInfo;
  }
  
}
