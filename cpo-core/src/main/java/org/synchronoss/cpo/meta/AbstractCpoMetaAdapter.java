/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.meta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.sql.DataSource;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.*;

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

  public void loadCpoMetaDataDocument(CpoMetaDataDocument metaDataDoc) throws CpoException {
    
    for(CtClass ctClass : metaDataDoc.getCpoMetaData().getClassMetaArray()) {
      CpoClass cpoClass = loadCpoClass(ctClass);
    }
    
  }
  
  protected CpoClass loadCpoClass(CtClass ctClass) throws CpoException {
    CpoClass cpoClass = null;
    
    try {
      cpoClass = createCpoClass(Class.forName(ctClass.getName()));
      cpoClass.setDescription(ctClass.getDescription());
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("Unable to create class: "+ctClass.getName()+": "+ExceptionHelper.getLocalizedMessage(cnfe));
    }
    
    for (CtAttribute ctAttribute : ctClass.getAttributeArray()){
      loadCpoAttribute(createCpoAttribute(), ctAttribute);
    }
    
    for (CtFunctionGroup ctFunctionGroup : ctClass.getFunctionsGroupArray()){
      loadCpoFunctionGroup(createCpoFunctionGroup(), ctFunctionGroup);
    }
    
    return cpoClass;    
  }
  
  protected void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute){
    cpoAttribute.setDataName(ctAttribute.getDataName());
    cpoAttribute.setDataType(ctAttribute.getDataType());
    cpoAttribute.setDescription(ctAttribute.getDescription());
    cpoAttribute.setJavaName(ctAttribute.getJavaName());
    cpoAttribute.setJavaType(ctAttribute.getJavaType());
    cpoAttribute.setTransformClass(ctAttribute.getTransformClass());
  }
  
  protected void loadCpoFunctionGroup(CpoFunctionGroup cpoFunctionGroup, CtFunctionGroup ctFunctionGroup){
    cpoFunctionGroup.setDescription(ctFunctionGroup.getDescription());
    cpoFunctionGroup.setName(ctFunctionGroup.getName());
    cpoFunctionGroup.setType(ctFunctionGroup.getType());
    cpoFunctionGroup.setFunctions(new ArrayList<CpoFunction>());
    List<CpoFunction> functions = cpoFunctionGroup.getFunctions();
    
    for (CtFunction ctFunction : ctFunctionGroup.getFunctionArray()){
      CpoFunction cpoFunction = createCpoFunction();
      functions.add(cpoFunction);
      loadCpoFunction(cpoFunction, ctFunction);
    }
    
  }
  
  protected void loadCpoFunction(CpoFunction cpoFunction, CtFunction ctFunction){
    cpoFunction.setExpression(ctFunction.getExpression());
    cpoFunction.setDescription(ctFunction.getDescription());
    cpoFunction.setArguments(new ArrayList<CpoArgument>());
    List<CpoArgument> arguments = cpoFunction.getArguments();
    
    for (CtArgument ctArgument : ctFunction.getArgumentsArray()){
      CpoArgument cpoArgument = createCpoArgument();
      arguments.add(cpoArgument);
      loadCpoArgument(cpoArgument, ctArgument);
    }    
  }
  
  protected void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument){
    cpoArgument.setAttributeName(ctArgument.getAttribute());
    cpoArgument.setDescription(ctArgument.getDescription());
    
    //TODO: do the attribute look up here and set appropriately.
    cpoArgument.setAttribute(null);
  }
   
  protected CpoClass createCpoClass(Class<?> clazz) {
    return new CpoClass(clazz);
  }
  
  protected CpoAttribute createCpoAttribute() {
    return new CpoAttribute();
  }
  
  protected CpoFunctionGroup createCpoFunctionGroup() {
    return new CpoFunctionGroup();
  }
  
  protected CpoFunction createCpoFunction() {
    return new CpoFunction();
  }
  
  protected CpoArgument createCpoArgument() {
    return new CpoArgument();
  }
  
}
