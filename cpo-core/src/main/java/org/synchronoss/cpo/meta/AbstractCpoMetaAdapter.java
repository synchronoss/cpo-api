/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.meta;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.*;

import javax.sql.DataSource;
import java.util.*;
import org.apache.xmlbeans.XmlException;

/**
 *
 * @author dberry
 */
public abstract class AbstractCpoMetaAdapter implements CpoMetaAdapter {
  
  /**
   * The map of classes in this metaAdapter
   */
  private static SortedMap<String, CpoClass> classMap = new TreeMap<String, CpoClass>();
  

  /**
   * Load the meta class from the CpoMetaAdapter Implementation
   * 
   */
  protected abstract <T> CpoMetaClass<T> loadMetaClass(Class<T> metaClass, String className) throws CpoException;
  
  /**
   * DOCUMENT ME!
   *
   * @param obj DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
  public <T> CpoMetaClass<T> getMetaClass(T obj) throws CpoException {
    CpoMetaClass<T> cpoClass = null;
    String className;
    String requestedName;
    Class<?> classObj;
    Class<?> requestedClass;

    if (obj != null) {
      requestedClass = obj.getClass();
      classObj = requestedClass;
      requestedName = requestedClass.getName();
      className = requestedName;

      while(cpoClass==null && classObj!=null){
        classObj = classObj.getSuperclass();
        className = classObj==null?null:classObj.getName();
        cpoClass = (CpoMetaClass<T>) classMap.get(className);
      }
      if (cpoClass==null){
        throw new CpoException("No Metadata found for class:" + requestedName);
      }
    }

    return cpoClass;
  }

  public static void loadCpoMetaDataDocument(CpoMetaDataDocument metaDataDoc, AbstractCpoMetaAdapter metaAdapter) throws CpoException {
    
    for(CtClass ctClass : metaDataDoc.getCpoMetaData().getCpoClassArray()) {
      CpoClass cpoClass = loadCpoClass(ctClass);
      metaAdapter.addCpoClass(cpoClass);
    }
    
  }
  
  protected static CpoClass loadCpoClass(CtClass ctClass) throws CpoException {
    CpoClass cpoClass = null;
    
    try {
      cpoClass = createCpoClass(Class.forName(ctClass.getName()));
      cpoClass.setDescription(ctClass.getDescription());
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("Unable to create class: "+ctClass.getName()+": "+ExceptionHelper.getLocalizedMessage(cnfe));
    }
    
    for (CtAttribute ctAttribute : ctClass.getCpoAttributeArray()){
      loadCpoAttribute(createCpoAttribute(), ctAttribute);
    }
    
    for (CtFunctionGroup ctFunctionGroup : ctClass.getCpoFunctionGroupArray()){
      loadCpoFunctionGroup(createCpoFunctionGroup(), ctFunctionGroup);
    }
    
    return cpoClass;    
  }
  
  protected static void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute){
    cpoAttribute.setDataName(ctAttribute.getDataName());
    cpoAttribute.setDataType(ctAttribute.getDataType());
    cpoAttribute.setDescription(ctAttribute.getDescription());
    cpoAttribute.setJavaName(ctAttribute.getJavaName());
    cpoAttribute.setJavaType(ctAttribute.getJavaType());
    cpoAttribute.setTransformClass(ctAttribute.getTransformClass());
  }
  
  protected static void loadCpoFunctionGroup(CpoFunctionGroup cpoFunctionGroup, CtFunctionGroup ctFunctionGroup){
    cpoFunctionGroup.setDescription(ctFunctionGroup.getDescription());
    cpoFunctionGroup.setName(ctFunctionGroup.getName());
    cpoFunctionGroup.setType(ctFunctionGroup.getType());
    cpoFunctionGroup.setFunctions(new ArrayList<CpoFunction>());
    List<CpoFunction> functions = cpoFunctionGroup.getFunctions();
    
    for (CtFunction ctFunction : ctFunctionGroup.getCpoFunctionArray()){
      CpoFunction cpoFunction = createCpoFunction();
      functions.add(cpoFunction);
      loadCpoFunction(cpoFunction, ctFunction);
    }
    
  }
  
  protected static void loadCpoFunction(CpoFunction cpoFunction, CtFunction ctFunction){
    cpoFunction.setExpression(ctFunction.getExpression());
    cpoFunction.setDescription(ctFunction.getDescription());
    cpoFunction.setArguments(new ArrayList<CpoArgument>());
    List<CpoArgument> arguments = cpoFunction.getArguments();
    
    for (CtArgument ctArgument : ctFunction.getCpoArgumentArray()){
      CpoArgument cpoArgument = createCpoArgument();
      arguments.add(cpoArgument);
      loadCpoArgument(cpoArgument, ctArgument);
    }    
  }
  
  protected static void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument){
    cpoArgument.setAttributeName(ctArgument.getAttributeName());
    cpoArgument.setDescription(ctArgument.getDescription());
    
    //TODO: do the attribute look up here and set appropriately.
    cpoArgument.setAttribute(null);
  }
   
  protected static CpoMetaClass createCpoClass(Class<?> clazz) {
    return new CpoMetaClass(clazz);
  }
  
  protected static CpoAttribute createCpoAttribute() {
    return new CpoAttribute();
  }
  
  protected static CpoFunctionGroup createCpoFunctionGroup() {
    return new CpoFunctionGroup();
  }
  
  protected static CpoFunction createCpoFunction() {
    return new CpoFunction();
  }
  
  protected static CpoArgument createCpoArgument() {
    return new CpoArgument();
  }
  
  protected static CpoMetaAdapter getCpoMetaAdapter(String metaXml, AbstractCpoMetaAdapter metaAdapter) throws CpoException {
    
    // calculate the hash of metaXml
    
    // see if it exists in the cache
    
    // if it does, return it
    
    // if not, load the new one.
    
    InputStream is = null;
    CpoMetaDataDocument metaDataDoc = null;
    
    is = AbstractCpoMetaAdapter.class.getResourceAsStream(metaXml);
    if (is == null){
      try {
        is = new FileInputStream(metaXml);
      } catch (FileNotFoundException fnfe){
        is = null;
      }
    }
    
    try {
      if (is == null){
        metaDataDoc = CpoMetaDataDocument.Factory.parse(metaXml);
      } else {
        metaDataDoc = CpoMetaDataDocument.Factory.parse(is);
      }
    } catch (IOException ioe){
      throw new CpoException("Error processing metaData from InputStream");
    } catch (XmlException xe){
      throw new CpoException("Error processing metaData from String");
    }
    
    // We should have a valid metaData xml document now.
    loadCpoMetaDataDocument(metaDataDoc, metaAdapter);

    return metaAdapter;
  }
  
  protected void addCpoClass(CpoClass metaClass) {
    classMap.put(metaClass.getName(), metaClass);
  }
  
}
