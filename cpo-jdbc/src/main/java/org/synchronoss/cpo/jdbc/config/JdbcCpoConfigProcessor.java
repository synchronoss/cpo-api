/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc.config;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.config.CpoConfigProcessor;
import org.synchronoss.cpo.config.CpoConfigProcessorFactory;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;
import org.synchronoss.cpo.jdbc.cpoJdbcConfig.CtJdbcConfig;

/**
 *
 * @author dberry
 */
public class JdbcCpoConfigProcessor implements CpoConfigProcessorFactory, CpoConfigProcessor {
  
  public JdbcCpoConfigProcessor() {
    
  }

  @Override
  public CpoConfigProcessor getCpoConfigProcessor() {
    return this;
  }

  @Override
  public void processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException {
    if (cpoConfig == null || !(cpoConfig instanceof CtJdbcConfig))
      throw new CpoException("Invalid Jdbc Configuration Information");
    
    CtJdbcConfig jdbcConfig = (CtJdbcConfig) cpoConfig;

//    JdbcCpoMetaAdapter metaAdapter = new JdbcCpoMetaAdapter(jdbcConfig.getMetaXml());
    //TODO: build the meta adapter
//    this.getClass().getResourceAsStream(null)

    //TODO: build a datasource info
    
    //TODO: build the CpoAdapter and add to the cache.
  }
  
}
