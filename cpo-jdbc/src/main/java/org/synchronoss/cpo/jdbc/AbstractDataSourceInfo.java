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

import org.synchronoss.cpo.*;

import javax.sql.DataSource;
import java.util.*;

/**
 *
 * @author dberry
 */
public abstract class AbstractDataSourceInfo implements DataSourceInfo {
  private DataSource dataSource = null;
  private String dataSourceName = null;
  // Make sure DataSource creation is thread safe.
  private final Object LOCK = new Object();
  
  // common password strings
  private final String PASSWORD = "password";
  private final String PASSWD = "passwd";
  private final String PWD = "pwd";

  public AbstractDataSourceInfo(String dataSourceName) {
    this.dataSourceName=dataSourceName;
  }
  
  public AbstractDataSourceInfo(String className, SortedMap<String, String> properties) {
    this.dataSourceName=BuildDataSourceName(className, properties);
  }

  public AbstractDataSourceInfo(String className, Properties properties) {
    this.dataSourceName=BuildDataSourceName(className, properties);
  }

  protected abstract DataSource createDataSource() throws CpoException ;
  
  @Override
  public String getDataSourceName() {
    return dataSourceName;
  }

  @Override
  public DataSource getDataSource() throws CpoException {
    if (dataSource == null) {
      synchronized (LOCK) {
        try {
          dataSource = createDataSource();
        } catch (Exception e) {
          throw new CpoException("Error instantiating DataSource", e);
        }
      }
    }

    return dataSource;
  }
  
  private String BuildDataSourceName(String s, Properties properties) {
    // Use a tree map so that the properties are sorted. This way if we have
    // the same datasource with the same properties but in different order,
    // we will generate the same key.
    SortedMap<String, String> map = new TreeMap<String, String>();
    for (Object key : properties.keySet()){
      map.put((String)key, properties.getProperty((String)key));
    }
    return BuildDataSourceName(s, map);
  }
  
  private String BuildDataSourceName(String s, SortedMap<String, String> map) {
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
