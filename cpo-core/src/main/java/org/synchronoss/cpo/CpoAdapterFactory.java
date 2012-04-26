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
  private static String defaultContext = null;

  public static CpoAdapter getCpoAdapter() throws CpoException {
    return getCpoAdapter(defaultContext);
  }

  public static CpoAdapter getCpoAdapter(String context) throws CpoException {

    return adapterMap.get(context);

  }

  private static Map<String, CpoAdapter> loadAdapters() {
    HashMap<String, CpoAdapter> map = new HashMap<String, CpoAdapter>();

    InputStream is = CpoAdapterFactory.class.getResourceAsStream(CPO_CONFIG_XML);

    try {
      CtCpoConfig cpoConfig = CpoConfigDocument.Factory.parse(is).getCpoConfig();

      // Set the default context.
      if (cpoConfig.isSetDefaultConfig()) {
        defaultContext = cpoConfig.getDefaultConfig();
      } else {
        // make the first listed config the default.
        defaultContext = cpoConfig.getDataConfigArray(0).getName();
      }

      // now lets loop through all the adapters and get them cached.
      for (CtDataSourceConfig dataSourceConfig : cpoConfig.getDataConfigArray()) {
        CpoAdapter cpoAdapter = makeCpoAdapter(dataSourceConfig);
        if (cpoAdapter != null) {
          map.put(dataSourceConfig.getName(), cpoAdapter);
        }
      }
    } catch (IOException ioe) {
      logger.error("Error reading cpoConfig.xml: ", ioe);
    } catch (XmlException xe) {
      logger.error("Error processing cpoConfig.xml: ", xe);
    }


    return map;
  }

  private static CpoAdapter makeCpoAdapter(CtDataSourceConfig dataSourceConfig) {
    CpoAdapter cpoAdapter = null;

    // make the CpoAdapter
    try {
      CpoConfigProcessor configProcessor = (CpoConfigProcessor) Class.forName(dataSourceConfig.getCpoConfigProcessor()).newInstance();
      cpoAdapter = configProcessor.processCpoConfig(dataSourceConfig);
    } catch (ClassNotFoundException cnfe) {
      logger.error("CpoConfigProcessor not found: " + dataSourceConfig.getCpoConfigProcessor(), cnfe);
    } catch (IllegalAccessException iae) {
      logger.error("Could not access CpoConfigProcessor: " + dataSourceConfig.getCpoConfigProcessor(), iae);
    } catch (InstantiationException ie) {
      logger.error("Could not instantiate CpoConfigProcessor: " + dataSourceConfig.getCpoConfigProcessor(), ie);
    } catch (CpoException ce) {
      logger.error("Error Creating CpoConfigProcessor: " + dataSourceConfig.getCpoConfigProcessor(), ce);
    }

    return cpoAdapter;
  }
}
