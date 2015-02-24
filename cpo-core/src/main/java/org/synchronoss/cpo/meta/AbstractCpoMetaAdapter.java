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
 * @author dberry
 */
public abstract class AbstractCpoMetaAdapter implements CpoMetaAdapter {

  private static final Logger logger = LoggerFactory.getLogger(AbstractCpoMetaAdapter.class);
  /**
   * The map of classes in this metaAdapter
   */
  private Map<String, CpoClass> classMap = new HashMap<>();
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
    List<Class<?>> classList = new ArrayList<>();
    Class<?> requestedClass;

    if (obj != null) {
      requestedClass = obj.getClass();
      classList.add(requestedClass);
      requestedName = requestedClass.getName();
      className = requestedName;
      cpoClass = classMap.get(className);

      while (cpoClass == null && !classList.isEmpty()) {
        classList = getSuperClasses(classList);
        for (Class<?> clazz : classList) {
          className = clazz.getName();
          cpoClass = classMap.get(className);
          if (cpoClass != null)
            break;
        }
      }
      if (cpoClass == null) {
        throw new CpoException("No Metadata found for class:" + requestedName);
      }
    }

    return cpoClass;
  }

  private List<Class<?>> getSuperClasses(List<Class<?>> classList) {
    List<Class<?>> superClassList = new ArrayList<>();

    for (Class<?> clazz : classList) {
      Class<?> superClass = clazz.getSuperclass();
      if (superClass != null) {
        superClassList.add(superClass);
      }
      superClassList.addAll(Arrays.asList(clazz.getInterfaces()));
    }
    return superClassList;
  }


  @Override
  public List<CpoClass> getCpoClasses() {
    List<CpoClass> result = new ArrayList<>();
    result.addAll(classMap.values());
    return result;
  }

  @Override
  public String getDataTypeName(CpoAttribute attribute) {
    String clazzName = getDataTypeJavaClass(attribute).getName();
    byte[] b = new byte[0];
    char[] c = new char[0];

    if (b.getClass().getName().equals(clazzName)) {
      clazzName = "byte[]";
    } else if (c.getClass().getName().equals(clazzName)) {
      clazzName = "char[]";
    }
    return clazzName;
  }

  @Override
  public Class<?> getDataTypeJavaClass(CpoAttribute attribute) {
    Class<?> clazz = String.class;
    DataTypeMapEntry<?> dataTypeMapEntry = getDataTypeMapper().getDataTypeMapEntry(attribute.getDataType());

    if (attribute.getTransformInMethod() != null) {
      clazz = attribute.getTransformInMethod().getReturnType();
    } else if (dataTypeMapEntry != null) {
      clazz = dataTypeMapEntry.getJavaClass();
    }

    return clazz;
  }

  @Override
  public int getDataTypeInt(String dataTypeName) throws CpoException {
    return getDataTypeMapper().getDataTypeInt(dataTypeName);
  }

  @Override
  public DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) throws CpoException {
    return getDataTypeMapper().getDataTypeMapEntry(dataTypeInt);
  }

  @Override
  public List<String> getAllowableDataTypes() throws CpoException {
    return getDataTypeMapper().getDataTypeNames();
  }

  protected abstract DataTypeMapper getDataTypeMapper();

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
      } catch (Exception e) {
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
    if (caseSensitive) {
      return new CpoClassCaseSensitive();
    } else {
      return new CpoClassCaseInsensitive();
    }
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
    if (oldMetaClass != null) {
      logger.debug("Overwrote class: " + metaClass.getName());
    } else {
      logger.debug("Added class: " + metaClass.getName());
    }
  }

  protected void removeCpoClass(CpoClass metaClass) {
    if (metaClass != null) {
      logger.debug("Removing class: " + metaClass.getName());
      metaClass.emptyMaps();
      classMap.remove(metaClass.getName());
    }
  }

  protected void removeAllCpoClass() {
    classMap.clear();
  }
}
