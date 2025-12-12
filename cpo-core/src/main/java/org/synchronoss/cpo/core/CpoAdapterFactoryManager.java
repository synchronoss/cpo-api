package org.synchronoss.cpo.core;

/*-
 * [[
 * core
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

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.cache.CpoAdapterFactoryCache;
import org.synchronoss.cpo.core.config.CpoConfigProcessor;
import org.synchronoss.cpo.core.helper.CpoClassLoader;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.core.helper.XmlHelper;
import org.synchronoss.cpo.core.jta.CpoXaResource;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.cpoconfig.CtCpoConfig;
import org.synchronoss.cpo.cpoconfig.CtDataSourceConfig;
import org.synchronoss.cpo.cpoconfig.CtMetaDescriptor;

/**
 * @author dberry
 */
public final class CpoAdapterFactoryManager extends CpoAdapterFactoryCache {
  private static final Logger logger = LoggerFactory.getLogger(CpoAdapterFactoryManager.class);
  private static final ReentrantLock lock = new ReentrantLock();
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
    CpoAdapter cpoAdapter = null;
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
    CpoTrxAdapter cpoTrxAdapter = null;
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
    CpoXaResource cpoXaResource = null;
    CpoAdapterFactory cpoAdapterFactory = findCpoAdapterFactory(context);
    if (cpoAdapterFactory != null) {
      cpoXaResource = cpoAdapterFactory.getCpoXaAdapter();
    }
    return cpoXaResource;
  }

  /**
   * LoadAdapters is responsible for loading the config file and then subsequently loading all the
   * metadata.
   */
  public static void loadAdapters() {
    String cpoConfig = System.getProperty(CPO_CONFIG, System.getenv(CPO_CONFIG));
    if (cpoConfig == null) cpoConfig = CPO_CONFIG_XML;

    loadAdapters(cpoConfig);
  }

  /**
   * LoadAdapters is responsible for loading the config file and then subsequently loading all the
   * metadata.
   *
   * @param cpoConfig
   */
  private static void loadAdapters(String cpoConfig) {
    lock.lock();
    try {
      var errBuilder = new StringBuilder();
      CtCpoConfig ctCpoConfig =
          XmlHelper.unmarshalXmlObject(
              XmlHelper.CPO_CONFIG_XSD, cpoConfig, CtCpoConfig.class, errBuilder);
      if (!errBuilder.isEmpty()) {
        throw new RuntimeException("Error parsing CPO config XML: " + errBuilder.toString());
      }
      logger.info("Processing Config File: " + cpoConfig);
      // Moving the clear to here to make sure we get a good file before we just blow away all
      // the
      // adapters.
      // We are doing a load clear all the caches first, in case the load gets called more than
      // once.
      CpoMetaDescriptor.clearAllInstances();
      clearCpoAdapterFactoryCache();

      // Set the default context.
      defaultContext = ctCpoConfig.getDefaultConfig();
      if (defaultContext == null) {
        // make the first listed config the default.
        defaultContext = ctCpoConfig.getDataConfig().getFirst().getValue().getName();
      }

      for (CtMetaDescriptor metaDescriptor : ctCpoConfig.getMetaConfig()) {
        boolean caseSensitive = true;
        caseSensitive = metaDescriptor.isCaseSensitive();

        // this will create and cache, so we don't need the return
        CpoMetaDescriptor.getInstance(
            metaDescriptor.getName(), metaDescriptor.getMetaXml(), caseSensitive);
      }

      // now lets loop through all the adapters and get them cached.
      for (var jaxbElement : ctCpoConfig.getDataConfig()) {
        CtDataSourceConfig dataSourceConfig = jaxbElement.getValue();
        CpoAdapterFactory cpoAdapterFactory = makeCpoAdapterFactory(dataSourceConfig);
        if (cpoAdapterFactory != null) {
          addCpoAdapterFactory(dataSourceConfig.getName(), cpoAdapterFactory);
        }
      }
    } catch (Exception e) {
      logger.error("Error unmarshalling XML " + cpoConfig + ": ", e);
    } finally {
      lock.unlock();
    }
  }

  public static CpoAdapterFactory makeCpoAdapterFactory(CtDataSourceConfig dataSourceConfig)
      throws CpoException {
    CpoAdapterFactory cpoAdapterFactory = null;

    // make the CpoAdapter
    try {
      CpoConfigProcessor configProcessor =
          (CpoConfigProcessor)
              CpoClassLoader.forName(dataSourceConfig.getCpoConfigProcessor())
                  .getDeclaredConstructor()
                  .newInstance();
      cpoAdapterFactory = configProcessor.processCpoConfig(dataSourceConfig);
    } catch (ClassNotFoundException cnfe) {
      String msg = "CpoConfigProcessor not found: " + dataSourceConfig.getCpoConfigProcessor();
      throw new CpoException(msg, cnfe);
    } catch (IllegalAccessException iae) {
      String msg =
          "Could not access CpoConfigProcessor: " + dataSourceConfig.getCpoConfigProcessor();
      throw new CpoException(msg, iae);
    } catch (InstantiationException ie) {
      String msg =
          "Could not instantiate CpoConfigProcessor: "
              + dataSourceConfig.getCpoConfigProcessor()
              + ": "
              + ExceptionHelper.getLocalizedMessage(ie);
      throw new CpoException(msg, ie);
    } catch (ClassCastException cce) {
      String msg =
          "Class is not instance of CpoConfigProcessor: "
              + dataSourceConfig.getCpoConfigProcessor();
      throw new CpoException(msg, cce);
    } catch (NoSuchMethodException e) {
      String msg =
          "Could not find the constructor for CpoConfigProcessor: "
              + dataSourceConfig.getCpoConfigProcessor();
      throw new CpoException(msg, e);
    } catch (InvocationTargetException e) {
      String msg =
          "Could not invoke the constructor for CpoConfigProcessor: "
              + dataSourceConfig.getCpoConfigProcessor();
      throw new CpoException(msg, e);
    }

    return cpoAdapterFactory;
  }
}
