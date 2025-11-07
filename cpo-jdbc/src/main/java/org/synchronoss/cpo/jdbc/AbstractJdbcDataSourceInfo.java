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
package org.synchronoss.cpo.jdbc;

import org.synchronoss.cpo.AbstractDataSourceInfo;

import java.util.*;

/**
 *
 * @author dberry
 */
public abstract class AbstractJdbcDataSourceInfo extends AbstractDataSourceInfo {
  // common password strings
  private static final String PASSWORD = "password";
  private static final String PASSWD = "passwd";
  private static final String PWD = "pwd";

  public AbstractJdbcDataSourceInfo(String dataSourceName) {
    super(dataSourceName);
  }

  public AbstractJdbcDataSourceInfo(String className, SortedMap<String, String> properties) {
    super(BuildDataSourceName(className, properties));
  }

  public AbstractJdbcDataSourceInfo(String className, Properties properties) {
    super(BuildDataSourceName(className, properties));
  }

  private static String BuildDataSourceName(String s, Properties properties) {
    // Use a tree map so that the properties are sorted. This way if we have
    // the same datasource with the same properties but in different order,
    // we will generate the same key.
    SortedMap<String, String> map = new TreeMap<>();
    for (Object key : properties.keySet()){
      map.put((String)key, properties.getProperty((String)key));
    }
    return BuildDataSourceName(s, map);
  }

  private static String BuildDataSourceName(String s, SortedMap<String, String> map) {
    StringBuilder dsName = new StringBuilder(s);

    for (Object obj : map.keySet()) {
      String key = (String)obj;
      // Don't store the password in the datasource generated name.
      if (!PASSWORD.equalsIgnoreCase(key) && !PASSWD.equalsIgnoreCase(key) && !PWD.equalsIgnoreCase(key) ) {
        dsName.append(key);
        dsName.append("=");
        dsName.append(map.get(key));
      }
    }

    return dsName.toString();
  }

}
