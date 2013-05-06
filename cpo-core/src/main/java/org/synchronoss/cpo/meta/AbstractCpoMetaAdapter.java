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
package org.synchronoss.cpo.meta;

import org.slf4j.*;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.meta.domain.*;

import java.util.*;

/**
 *
 * @author dberry
 */
public abstract class AbstractCpoMetaAdapter implements CpoMetaAdapter {

  private static final Logger logger = LoggerFactory.getLogger(AbstractCpoMetaAdapter.class);
  /**
   * The map of classes in this metaAdapter
   */
  private Map<String, CpoClass> classMap = new HashMap<String, CpoClass>();
  private CpoClass currentClass = null;

  protected AbstractCpoMetaAdapter() {
    super();
  }

  /**
   * DOCUMENT ME!
   *
   * @param obj DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
  public <T> CpoClass getMetaClass(T obj) throws CpoException {
    CpoClass cpoClass = null;
    String className;
    String requestedName;
    Class<?> classObj;
    Class<?> requestedClass;

    if (obj != null) {
      requestedClass = obj.getClass();
      classObj = requestedClass;
      requestedName = requestedClass.getName();
      className = requestedName;
      cpoClass = classMap.get(className);

      while (cpoClass == null && classObj != null) {
        classObj = classObj.getSuperclass();
        className = classObj == null ? null : classObj.getName();
        if (className != null) {
          cpoClass = classMap.get(className);
        }
      }
      if (cpoClass == null) {
        throw new CpoException("No Metadata found for class:" + requestedName);
      }
    }

    return cpoClass;
  }

  @Override
  public List<CpoClass> getCpoClasses() {
    List<CpoClass> result = new ArrayList<CpoClass>();
    result.addAll(classMap.values());
    return result;
  }

  protected void loadCpoMetaDataDocument(CpoMetaDataDocument metaDataDoc, boolean caseSensitive) throws CpoException {
    for (CtClass ctClass : metaDataDoc.getCpoMetaData().getCpoClassArray()) {

      CpoClass cpoClass = getCpoClass(ctClass.getName());
      if (cpoClass == null) {
        cpoClass = createCpoClass(caseSensitive);
        cpoClass.setName(ctClass.getName());
        cpoClass.setDescription(ctClass.getDescription());
        loadCpoClass(cpoClass, ctClass);
        addCpoClass(cpoClass);
      } else {
        loadCpoClass(cpoClass, ctClass);
      }
    }
  }

  protected void loadCpoClass(CpoClass cpoClass, CtClass ctClass) throws CpoException {
    logger.debug("Loading class: " + ctClass.getName());

    currentClass = cpoClass;

    for (CtAttribute ctAttribute : ctClass.getCpoAttributeArray()) {
      CpoAttribute cpoAttribute = cpoClass.getAttributeJava(ctAttribute.getJavaName());
      
      if (cpoAttribute == null) {
        cpoAttribute = createCpoAttribute();
        loadCpoAttribute(cpoAttribute, ctAttribute);
        cpoClass.addAttribute(cpoAttribute);
      } else {
        loadCpoAttribute(cpoAttribute, ctAttribute);
      }
    }

    for (CtFunctionGroup ctFunctionGroup : ctClass.getCpoFunctionGroupArray()) {
      CpoFunctionGroup functionGroup = null;
      
      try {
        functionGroup = cpoClass.getFunctionGroup(ctFunctionGroup.getType().toString(), ctFunctionGroup.getName());
      } catch (Exception e){
        // this a runtime exception that we can ignore during load time.
        if (logger.isTraceEnabled()) {
          logger.trace(e.getLocalizedMessage());
        }
      }
      
      if (functionGroup == null) {
        functionGroup = createCpoFunctionGroup();
        loadCpoFunctionGroup(functionGroup, ctFunctionGroup);
        cpoClass.addFunctionGroup(functionGroup);
      } else {
        functionGroup.clearFunctions();
        loadCpoFunctionGroup(functionGroup, ctFunctionGroup);
      }
      logger.debug("Added Function Group: " + functionGroup.getName());
    }
  }

  protected void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute) {
    cpoAttribute.setDataName(ctAttribute.getDataName());
    cpoAttribute.setDataType(ctAttribute.getDataType());
    cpoAttribute.setDescription(ctAttribute.getDescription());
    cpoAttribute.setJavaName(ctAttribute.getJavaName());
    cpoAttribute.setJavaType(ctAttribute.getJavaType());
    cpoAttribute.setTransformClassName(ctAttribute.getTransformClass());
  }

  protected void loadCpoFunctionGroup(CpoFunctionGroup cpoFunctionGroup, CtFunctionGroup ctFunctionGroup) {
    cpoFunctionGroup.setDescription(ctFunctionGroup.getDescription());
    if (ctFunctionGroup.isSetName()) {
      cpoFunctionGroup.setName(ctFunctionGroup.getName());
    }
    cpoFunctionGroup.setType(ctFunctionGroup.getType().toString());

    for (CtFunction ctFunction : ctFunctionGroup.getCpoFunctionArray()) {
      CpoFunction cpoFunction = createCpoFunction();
      cpoFunctionGroup.addFunction(cpoFunction);
      loadCpoFunction(cpoFunction, ctFunction);
    }

  }

  protected void loadCpoFunction(CpoFunction cpoFunction, CtFunction ctFunction) {
    cpoFunction.setName(ctFunction.getName());
    cpoFunction.setExpression(ctFunction.getExpression());
    cpoFunction.setDescription(ctFunction.getDescription());

    for (CtArgument ctArgument : ctFunction.getCpoArgumentArray()) {
      CpoArgument cpoArgument = createCpoArgument();
      cpoFunction.addArgument(cpoArgument);
      loadCpoArgument(cpoArgument, ctArgument);
    }
  }

  protected void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument) {
    cpoArgument.setAttributeName(ctArgument.getAttributeName());
    cpoArgument.setDescription(ctArgument.getDescription());

    cpoArgument.setAttribute(currentClass.getAttributeJava(ctArgument.getAttributeName()));
  }

  protected CpoClass createCpoClass(boolean caseSensitive) {
    if (caseSensitive)
      return new CpoClassCaseSensitive();
    else 
      return new CpoClassCaseInsensitive();
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

  protected CpoClass getCpoClass(String name) {
    return classMap.get(name);
  }

  protected void addCpoClass(CpoClass metaClass) {
    CpoClass oldMetaClass = classMap.put(metaClass.getName(), metaClass);
    if (oldMetaClass != null)
      logger.debug("Overwrote class: " + metaClass.getName());
    else 
      logger.debug("Added class: " + metaClass.getName());

  }

  protected void removeCpoClass(CpoClass metaClass) {
    if (metaClass != null) {
      logger.debug("Removing class: " + metaClass.getName());
      metaClass.emptyMaps();
      classMap.remove(metaClass.getName());
    }
  }
}
