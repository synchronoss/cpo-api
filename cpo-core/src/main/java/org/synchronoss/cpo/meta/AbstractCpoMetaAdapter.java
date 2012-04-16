/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.meta;

import java.sql.Connection;
import java.util.HashMap;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 *
 * @author dberry
 */
public abstract class AbstractCpoMetaAdapter implements CpoMetaAdapter {
  
  /**
   * Each datasource has a cache for the meta data for each class inside that datasource
   */
  private static HashMap<String, HashMap<String, CpoClass<?>>> dataSourceMap_ = new HashMap<String, HashMap<String, CpoClass<?>>>(); // Contains the
  
  /**
   * This is the DataSource for the meta datasource. Not sure if this belongs here or if it needs to be in JdbcCpoMetaAdapter.
   */
  private DataSource metaDataSource_ = null;

  /**
   * The unique statsource name for the meta store.
   */
  private String metaDataSourceName_ = null;

  /**
   * Clears the metadata for the specified object. The metadata will be reloaded
   * the next time that CPO is called to access this object
   *
   * @param obj The object whose metadata must be cleared
   */
  @Override
  public void clearMetaClass(Object obj) {
    String className;
    Class<?> objClass;

    if (obj != null) {
      objClass = obj.getClass();
      className = objClass.getName();
      clearMetaClass(className);
    }
  }

  /**
   * Clears the metadata for the specified fully qualifed class name. The metadata
   * will be reloaded the next time CPO is called to access this class.
   *
   * @param className The fully qualified class name for the class that needs its
   *                  metadata cleared.
   */
  @Override
  public void clearMetaClass(String className) {
    HashMap<String, CpoClass<?>> metaClassMap;

    synchronized (getDataSourceMap()) {
      metaClassMap = getMetaClassMap();
      metaClassMap.remove(className);
    }
  }

  /**
   * Clears the metadata for all classes. The metadata will be lazy-loaded from 
   * the metadata repository as classes are accessed.
   *
   * @param all true - clear all classes for all datasources.
   *            false - clear all classes for the current datasource.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
  */
  @Override
  public void clearMetaClass(boolean all) {
    synchronized (getDataSourceMap()) {
      if (all==false) {
        HashMap<String, CpoClass<?>> metaClassMap = getMetaClassMap();
        metaClassMap.clear();
      } else {
        for (HashMap<String, CpoClass<?>> metamap : getDataSourceMap().values()){
          metamap.clear();
        }
      }
    }
  }
  
  /**
   * Clears the metadata for all classes for the current datasource. The metadata will be lazy-loaded from 
   * the metadata repository as classes are accessed.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
  */
  @Override
  public void clearMetaClass() {
    clearMetaClass(false);
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected HashMap<String, HashMap<String, CpoClass<?>>> getDataSourceMap() {
    return dataSourceMap_;
  }

  protected void setDataSourceMap(HashMap<String, HashMap<String, CpoClass<?>>> dsMap) {
    dataSourceMap_ = dsMap;
  }

  // All meta data will come from the meta datasource.
  protected HashMap<String, CpoClass<?>> getMetaClassMap() {
    HashMap<String, HashMap<String, CpoClass<?>>> dataSourceMap = getDataSourceMap();
    String dataSourceName = getMetaDataSourceName();
    HashMap<String, CpoClass<?>> metaClassMap = dataSourceMap.get(dataSourceName);

    if (metaClassMap == null) {
      metaClassMap = new HashMap<String, CpoClass<?>>();
      dataSourceMap.put(dataSourceName, metaClassMap);
    }

    return metaClassMap;
  }

  /**
   * DOCUMENT ME!
   *
   * @param metaDataSource DOCUMENT ME!
   */
  protected void setMetaDataSource(DataSource metaDataSource) {
    metaDataSource_ = metaDataSource;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected DataSource getMetaDataSource() {
    return metaDataSource_;
  }

  /**
   * DOCUMENT ME!
   *
   * @param metaDataSourceName DOCUMENT ME!
   */
  protected void setMetaDataSourceName(String metaDataSourceName) {
    metaDataSourceName_ = metaDataSourceName;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   */
  protected String getMetaDataSourceName() {
    return metaDataSourceName_;
  }

  /**
   * Load the meta class from the CpoMetaAdapter Implementation
   * 
   */
  protected abstract <T> CpoClass<T> loadMetaClass(Class<T> metaClass, String className) throws CpoException;
  
  /**
   * DOCUMENT ME!
   *
   * @param obj DOCUMENT ME!
   * @param c   connection
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
  public <T> CpoClass<T> getMetaClass(T obj) throws CpoException {
    CpoClass<T> cpoClass = null;
    String className;
    String requestedName;
    Class<?> classObj;
    Class<?> requestedClass;
    HashMap<String, CpoClass<?>> metaClassMap;

    if (obj != null) {
      requestedClass = obj.getClass();
      classObj = requestedClass;
      requestedName = requestedClass.getName();
      className = requestedName;

      synchronized (getDataSourceMap()) {
        metaClassMap = getMetaClassMap();
        cpoClass = (CpoClass<T>) metaClassMap.get(className);
        while(cpoClass==null && classObj!=null){
          try {
            cpoClass = (CpoClass<T>) loadMetaClass(requestedClass, className);
            // reset the class name to the original 
            cpoClass.setName(requestedName);
            metaClassMap.put(requestedName, cpoClass);
            LoggerFactory.getLogger(requestedName).debug("Loading Class:" + requestedName);
          } catch (CpoException ce) {
            cpoClass = null;
            classObj = classObj.getSuperclass();
            className = classObj==null?null:classObj.getName();
          }
        }
        if (cpoClass==null){
          throw new CpoException("No Metadata found for class:" + requestedName);
        }
      }
    }

    return cpoClass;
  }

}
