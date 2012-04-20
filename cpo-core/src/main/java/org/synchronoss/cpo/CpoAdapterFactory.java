/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.cpoCoreConfig.CpoConfigDocument;
import org.synchronoss.cpo.core.cpoCoreConfig.CtCpoConfig;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 *
 * @author dberry
 */
public final class CpoAdapterFactory {
  private static final Logger logger = LoggerFactory.getLogger(CpoAdapterFactory.class.getName());
  private static final String CPO_CONFIG_XML = "/cpoConfig.xml";
  private static final Map<String, CpoAdapter> adapterMap = loadAdapters();
  private static String defaultContext=null;
  
  public static CpoAdapter getCpoAdapter() throws CpoException {
    return getCpoAdapter(defaultContext);
  }

  public static CpoAdapter getCpoAdapter(String context) throws CpoException {
    
    return adapterMap.get(context); 
    
  }
  
  private static Map<String, CpoAdapter> loadAdapters() {
    HashMap<String, CpoAdapter> map = new HashMap<String, CpoAdapter>();
    
    InputStream is = null;
    is = CpoAdapterFactory.class.getResourceAsStream(CPO_CONFIG_XML);
    
    try {
      CtCpoConfig cpoConfig = CpoConfigDocument.Factory.parse(is).getCpoConfig();

      // Set the default context.
      if (cpoConfig.isSetDefault()) {
        defaultContext = cpoConfig.getDefault();
      } else {
        // make the first listed config the default.
        defaultContext = cpoConfig.getDataConfigArray(0).getName();
      }

      // now lets loop through all the adapters and get them cached.
      for (CtDataSourceConfig dataSourceConfig : cpoConfig.getDataConfigArray()){
        CpoAdapter cpoAdapter = makeCpoAdapter(dataSourceConfig);
        if (cpoAdapter != null)
          map.put(dataSourceConfig.getName(), cpoAdapter);
      }
    } catch (IOException ioe) {
      logger.error("Error reading cpoConfig.xml: "+ExceptionHelper.getLocalizedMessage(ioe));
    } catch (XmlException xe) {
      logger.error("Error processing cpoConfig.xml: "+ExceptionHelper.getLocalizedMessage(xe));
    }
    
    
    return map;
  }
  
  private static CpoAdapter makeCpoAdapter(CtDataSourceConfig dataSourceConfig) {
    CpoAdapter cpoAdapter = null;
    
    // make the cpoMetaAdapter
    try {
      CpoConfigProcessor configProcessor = (CpoConfigProcessor) Class.forName(dataSourceConfig.getCpoConfigProcessor()).newInstance();
      cpoAdapter = configProcessor.processCpoConfig(dataSourceConfig);
    } catch (ClassNotFoundException cnfe) {
      logger.error("CpoConfigProcessor not found: "+dataSourceConfig.getCpoConfigProcessor()+": "+ExceptionHelper.getLocalizedMessage(cnfe));
    } catch (IllegalAccessException iae) {
      logger.error("Could not access CpoConfigProcessor: "+dataSourceConfig.getCpoConfigProcessor()+": "+ExceptionHelper.getLocalizedMessage(iae));
    } catch (InstantiationException ie)  {
      logger.error("Could not instantiate CpoConfigProcessor: "+dataSourceConfig.getCpoConfigProcessor()+": "+ExceptionHelper.getLocalizedMessage(ie));
    } catch (CpoException ce) {
      logger.error("Error Creating CpoAdapter: "+dataSourceConfig.getCpoConfigProcessor()+": "+ExceptionHelper.getLocalizedMessage(ce));
    }

    return cpoAdapter;
  }

}
