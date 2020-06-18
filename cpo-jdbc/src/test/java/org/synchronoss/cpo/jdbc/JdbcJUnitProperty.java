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
package org.synchronoss.cpo.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class JdbcJUnitProperty {
  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoAttribute.class);
  private static HashMap<String, String> propMap = new HashMap<String, String>();

  public static final String PROP_BLOBS_SUPPORTED = "cpo.db.blobssupported";
  public static final String PROP_CALLS_SUPPORTED = "cpo.db.callssupported";
  public static final String PROP_SELECT4UPDATE = "cpo.db.select4update";
  public static final String PROP_MILLIS_SUPPORTED = "cpo.db.millisupport";
  public static final String PROP_INIT_SCRIPT = "cpo.db.initScript";
  public static final String PROP_DB_TYPE = "cpo.db";
  public static final String PROP_DB_PORT = "cpo.db.port";
  public static final String PROP_DB_USER = "cpo.db.user";
  public static final String PROP_DB_PSWD = "cpo.db.pswd";
  public static final String PROP_DB_NAME = "cpo.db.database";

  static {
    InputStream inputStream = null;
    try {
      Properties properties = new Properties();
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      inputStream = loader.getResourceAsStream("cpoJdbcJUnit.properties");
      properties.load(inputStream);

      properties.forEach(
        (key, value) -> propMap.put(key.toString(),value.toString())
      );
    } catch (IOException ioe) {
      logger.error("Error loading properties file",ioe);
    } finally{
      if (inputStream != null) {
        try{inputStream.close();}catch(Exception e){}
      }
    }
  }

  static public String getProperty(String key) {
    return propMap.get(key);
  }
}
