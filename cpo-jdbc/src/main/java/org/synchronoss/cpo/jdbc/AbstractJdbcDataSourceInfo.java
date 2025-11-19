package org.synchronoss.cpo.jdbc;

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
import org.synchronoss.cpo.AbstractDataSourceInfo;

/**
 * A class used for collecting the properties to instantiate the datasource
 *
 * @author dberry
 */
public abstract class AbstractJdbcDataSourceInfo extends AbstractDataSourceInfo {
  // common password strings
  private static final String PASSWORD = "password";
  private static final String PASSWD = "passwd";
  private static final String PWD = "pwd";

  /**
   * Constructs a AbstractJdbcDataSourceInfo
   *
   * @param dataSourceName - The name of the datasource to instantiate,
   */
  public AbstractJdbcDataSourceInfo(String dataSourceName, int fetchSize, int batchSize) {
    super(dataSourceName, fetchSize, batchSize);
  }

  /**
   * Constructs a AbstractJdbcDataSourceInfo
   *
   * @param className - The DataSource className from the Driver.
   * @param properties - The list of properties to be passed to the driver
   */
  public AbstractJdbcDataSourceInfo(
      String className, SortedMap<String, String> properties, int fetchSize, int batchSize) {
    super(BuildDataSourceName(className, properties), fetchSize, batchSize);
  }

  /**
   * Constructs a AbstractJdbcDataSourceInfo
   *
   * @param className - The DataSource className from the Driver.
   * @param properties - The list of properties to be passed to the driver
   */
  public AbstractJdbcDataSourceInfo(
      String className, Properties properties, int fetchSize, int batchSize) {
    super(BuildDataSourceName(className, properties), fetchSize, batchSize);
  }

  private static String BuildDataSourceName(String s, Properties properties) {
    // Use a tree map so that the properties are sorted. This way if we have
    // the same datasource with the same properties but in different order,
    // we will generate the same key.
    SortedMap<String, String> map = new TreeMap<>();
    for (Object key : properties.keySet()) {
      map.put((String) key, properties.getProperty((String) key));
    }
    return BuildDataSourceName(s, map);
  }

  private static String BuildDataSourceName(String s, SortedMap<String, String> map) {
    StringBuilder dsName = new StringBuilder(s);

    for (Object obj : map.keySet()) {
      String key = (String) obj;
      // Don't store the password in the datasource generated name.
      if (!PASSWORD.equalsIgnoreCase(key)
          && !PASSWD.equalsIgnoreCase(key)
          && !PWD.equalsIgnoreCase(key)) {
        dsName.append(key);
        dsName.append("=");
        dsName.append(map.get(key));
      }
    }

    return dsName.toString();
  }
}
