/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc.config;

import java.util.HashMap;
import java.util.Map;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.DataSourceInfo;
import org.synchronoss.cpo.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;
import org.synchronoss.cpo.core.cpoCoreConfig.CtJdbcConfig;

import org.synchronoss.cpo.jdbc.ClassDataSourceInfo;
import org.synchronoss.cpo.jdbc.DriverDataSourceInfo;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;
import org.synchronoss.cpo.jdbc.JndiDataSourceInfo;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaAdapter;

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
    
    if (cpoConfig == null || !(cpoConfig instanceof CtJdbcConfig))
      throw new CpoException("Invalid Jdbc Configuration Information");
    
    CtJdbcConfig jdbcConfig = (CtJdbcConfig) cpoConfig;

    JdbcCpoMetaAdapter metaAdapter = JdbcCpoMetaAdapter.newInstance(jdbcConfig.getMetaXml());

    DataSourceInfo dataSourceInfo = null;
    
    // build a datasource info
    if (jdbcConfig.isSetJndiName()){
      dataSourceInfo = new JndiDataSourceInfo(jdbcConfig.getJndiName());
    } else if (jdbcConfig.isSetDataSourceClassName()) {
      Map<String, String> props = new HashMap<String, String>();
      
      if (jdbcConfig.isSetUrl()) {
        props.put(PROP_URL1, jdbcConfig.getUrl());
        props.put(PROP_URL2, jdbcConfig.getUrl());
      }
      
      if (jdbcConfig.isSetUser())
        props.put(PROP_USER, jdbcConfig.getUser());
      
      if (jdbcConfig.isSetPassword())
        props.put(PROP_PASSWORD, jdbcConfig.getUser());
      
      dataSourceInfo = new ClassDataSourceInfo(jdbcConfig.getDataSourceClassName(), props);
    } else if (jdbcConfig.isSetDriverClassName()) {
      if (jdbcConfig.isSetUser())
        dataSourceInfo = new DriverDataSourceInfo(jdbcConfig.getDriverClassName(), jdbcConfig.getUrl(), jdbcConfig.getUser(), jdbcConfig.getPassword());
      else 
        dataSourceInfo = new DriverDataSourceInfo(jdbcConfig.getDriverClassName(), jdbcConfig.getUrl());
    }
    
    if (dataSourceInfo != null) {
      cpoAdapter = new JdbcCpoAdapter(metaAdapter, dataSourceInfo);
    }
    return cpoAdapter;
  }
  
}

