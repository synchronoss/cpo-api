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
package org.synchronoss.cpo;

import org.apache.xmlbeans.XmlException;
import org.slf4j.*;
import org.synchronoss.cpo.cache.CpoAdapterFactoryCache;
import org.synchronoss.cpo.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.cpoCoreConfig.*;
import org.synchronoss.cpo.helper.*;
import org.synchronoss.cpo.jta.CpoXaResource;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * @author dberry
 */
public final class CpoAdapterFactoryManager extends CpoAdapterFactoryCache {

  private static final Logger logger = LoggerFactory.getLogger(CpoAdapterFactoryManager.class);
  public static final String CPO_CONFIG = "CPO_CONFIG";
  private static final String CPO_CONFIG_XML = "/cpoConfig.xml";
  private static String defaultContext = null;

  static {
      loadAdapters();
  }

  public static CpoAdapter getCpoAdapter() throws CpoException {
    return getCpoAdapter(defaultContext);
  }

  public static CpoAdapter getCpoAdapter(String context) throws CpoException {
    CpoAdapter cpoAdapter=null;
    CpoAdapterFactory cpoAdapterFactory = findCpoAdapterFactory(context);
    if (cpoAdapterFactory != null) {
      cpoAdapter = cpoAdapterFactory.getCpoAdapter();
    }
    return cpoAdapter;
  }

  public static CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    return getCpoTrxAdapter(defaultContext);
  }

  public static CpoTrxAdapter getCpoTrxAdapter(String context) throws CpoException {
    CpoTrxAdapter cpoTrxAdapter=null;
    CpoAdapterFactory cpoAdapterFactory = findCpoAdapterFactory(context);
    if (cpoAdapterFactory != null) {
      cpoTrxAdapter = cpoAdapterFactory.getCpoTrxAdapter();
    }
    return cpoTrxAdapter;
  }

  public static CpoXaResource getCpoXaAdapter() throws CpoException {
    return getCpoXaAdapter(defaultContext);
  }

  public static CpoXaResource getCpoXaAdapter(String context) throws CpoException {
    CpoXaResource cpoXaResource =null;
    CpoAdapterFactory cpoAdapterFactory = findCpoAdapterFactory(context);
    if (cpoAdapterFactory != null) {
      cpoXaResource = cpoAdapterFactory.getCpoXaAdapter();
    }
    return cpoXaResource;
  }

    /**
     * LoadAdapters is responsible for loading the config file and then subsequently loading all the metadata.
     */
    public static void loadAdapters() {
        String cpoConfig = System.getProperty(CPO_CONFIG, System.getenv(CPO_CONFIG));
        if (cpoConfig==null)
            cpoConfig = CPO_CONFIG_XML;

        loadAdapters(cpoConfig);
    }

        /**
         * LoadAdapters is responsible for loading the config file and then subsequently loading all the metadata.
         *
         * @param cpoConfig
         */
  synchronized private static void loadAdapters(String cpoConfig) {
    InputStream is = null;

    // See if the file is a uri
    try {
      URL cpoConfigUrl = new URL(cpoConfig);
      is = cpoConfigUrl.openStream();
    } catch (IOException e) {
      logger.info("Uri Not Found: " + cpoConfig);
    }

    // See if the file is a resource in the jar
    if (is == null)
      is = CpoClassLoader.getResourceAsStream(cpoConfig);

    if (is == null) {
      logger.info("Resource Not Found: " + cpoConfig);
      try {
        //See if the file is a local file on the server
        is = new FileInputStream(cpoConfig);
      } catch (FileNotFoundException fnfe) {
        logger.info("File Not Found: " + cpoConfig);
        is = null;
      }
    }

    try {
      CpoConfigDocument configDoc;
      if (is == null) {
        //See if the config is sent in as a string
        configDoc = CpoConfigDocument.Factory.parse(cpoConfig);
      } else {
        configDoc = CpoConfigDocument.Factory.parse(is);
      }

      String errMsg = XmlBeansHelper.validateXml(configDoc);
      if (errMsg != null) {
        logger.error("Invalid CPO Config file: " + cpoConfig + ":" + errMsg);
      } else {
        logger.info("Processing Config File: " + cpoConfig);
        // Moving the clear to here to make sure we get a good file before we just blow away all the adapters.
        // We are doing a load clear all the caches first, in case the load gets called more than once.
        CpoMetaDescriptor.clearAllInstances();
        clearCpoAdapterFactoryCache();

        CtCpoConfig ctCpoConfig = configDoc.getCpoConfig();

        // Set the default context.
        if (ctCpoConfig.isSetDefaultConfig()) {
          defaultContext = ctCpoConfig.getDefaultConfig();
        } else {
          // make the first listed config the default.
          defaultContext = ctCpoConfig.getDataConfigArray(0).getName();
        }

        for (CtMetaDescriptor metaDescriptor : ctCpoConfig.getMetaConfigArray()) {
          boolean caseSensitive = true;
          if (metaDescriptor.isSetCaseSensitive()) {
            caseSensitive = metaDescriptor.getCaseSensitive();
          }

          // this will create and cache, so we don't need the return
          CpoMetaDescriptor.getInstance(metaDescriptor.getName(), metaDescriptor.getMetaXmlArray(), caseSensitive);
        }

        // now lets loop through all the adapters and get them cached.
        for (CtDataSourceConfig dataSourceConfig : ctCpoConfig.getDataConfigArray()) {
          CpoAdapterFactory cpoAdapterFactory = makeCpoAdapterFactory(dataSourceConfig);
          if (cpoAdapterFactory != null) {
            addCpoAdapterFactory(dataSourceConfig.getName(), cpoAdapterFactory);
          }
        }
      }
    } catch (IOException ioe) {
      logger.error("Error reading " + cpoConfig + ": ", ioe);
    } catch (XmlException xe) {
      logger.error("Error processing " + cpoConfig + ": Invalid XML", xe);
    } catch (CpoException ce) {
      logger.error("Error processing " + cpoConfig + ": ", ce);
    } finally {
      if (is != null)
        try { is.close(); } catch (Exception e) {}
    }
  }

  public static CpoAdapterFactory makeCpoAdapterFactory(CtDataSourceConfig dataSourceConfig) throws CpoException {
    CpoAdapterFactory cpoAdapterFactory;

    // make the CpoAdapter
    try {
      CpoConfigProcessor configProcessor = (CpoConfigProcessor)CpoClassLoader.forName(dataSourceConfig.getCpoConfigProcessor()).newInstance();
      cpoAdapterFactory = configProcessor.processCpoConfig(dataSourceConfig);
    } catch (ClassNotFoundException cnfe) {
      String msg = "CpoConfigProcessor not found: " + dataSourceConfig.getCpoConfigProcessor();
      logger.error(msg);
      throw new CpoException(msg);
    } catch (IllegalAccessException iae) {
      String msg = "Could not access CpoConfigProcessor: " + dataSourceConfig.getCpoConfigProcessor();
      logger.error(msg);
      throw new CpoException(msg);
    } catch (InstantiationException ie) {
      String msg = "Could not instantiate CpoConfigProcessor: " + dataSourceConfig.getCpoConfigProcessor() + ": " + ExceptionHelper.getLocalizedMessage(ie);
      logger.error(msg);
      throw new CpoException(msg);
    } catch (ClassCastException cce) {
      String msg = "Class is not instance of CpoConfigProcessor: " + dataSourceConfig.getCpoConfigProcessor();
      logger.error(msg);
      throw new CpoException(msg);
    }

    return cpoAdapterFactory;
  }
}
