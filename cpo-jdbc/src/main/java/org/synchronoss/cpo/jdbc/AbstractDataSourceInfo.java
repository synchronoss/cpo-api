/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.sql.DataSource;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.DataSourceInfo;

/**
 *
 * @author dberry
 */
public abstract class AbstractDataSourceInfo implements DataSourceInfo {
  private DataSource dataSource = null;
  private String dataSourceName = null;
  // Make sure DataSource creation is thread safe.
  private final Object LOCK = new Object();

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
    SortedMap<String, String> map = new TreeMap<String, String>();
    for (Object key : properties.keySet()){
      map.put((String)key, properties.getProperty((String)key));
    }
    return BuildDataSourceName(s, map);
  }
  
  private String BuildDataSourceName(String s, SortedMap<String, String> map) {
    StringBuilder dsName = new StringBuilder(s);

    // Use a tree map so that the properties are sorted. This way if we have
    // the same datasource with the same properties but in different order,
    // we will generate the same key.
    for (Object key : map.keySet()) {
      dsName.append(key);
      dsName.append("=");
      dsName.append(map.get(key));
    }

    return dsName.toString();
  }

}
