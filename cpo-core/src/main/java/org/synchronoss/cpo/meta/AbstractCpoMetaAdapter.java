package org.synchronoss.cpo.meta;

/*-
 * [[
 * core
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

import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cpometa.*;
import org.synchronoss.cpo.enums.Crud;
import org.synchronoss.cpo.meta.domain.*;

/**
 * @author dberry
 */
public abstract class AbstractCpoMetaAdapter implements CpoMetaAdapter {

  private static final Logger logger = LoggerFactory.getLogger(AbstractCpoMetaAdapter.class);

  /** The map of classes in this metaAdapter */
  private Map<String, CpoClass> classMap = new HashMap<>();

  private CpoClass currentClass = null;

  protected AbstractCpoMetaAdapter() {
    super();
  }

  /**
   * DOCUMENT ME!
   *
   * @param bean DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
  public <T> CpoClass getMetaClass(T bean) throws CpoException {
    CpoClass cpoClass = null;
    String className;
    String requestedName;
    List<Class<?>> classList = new ArrayList<>();
    Class<?> requestedClass;

    if (bean != null) {
      requestedClass = bean.getClass();
      classList.add(requestedClass);
      requestedName = requestedClass.getName();
      className = requestedName;
      cpoClass = classMap.get(className);

      while (cpoClass == null && !classList.isEmpty()) {
        classList = getSuperClasses(classList);
        for (Class<?> clazz : classList) {
          className = clazz.getName();
          cpoClass = classMap.get(className);
          if (cpoClass != null) break;
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
    DataTypeMapEntry<?> dataTypeMapEntry =
        getDataTypeMapper().getDataTypeMapEntry(attribute.getDataType());

    if (attribute.getTransformInMethod() != null) {
      clazz = attribute.getTransformInMethod().getReturnType();
    } else if (attribute.getGetterReturnType() != null) {
      clazz = attribute.getGetterReturnType();
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

  protected void loadCpoMetaDataDocument(CtCpoMetaData ctCpoMetaData, boolean caseSensitive)
      throws CpoException {
    for (CtClass ctClass : ctCpoMetaData.getCpoClass()) {

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

    logger.debug("Loading " + ctClass.getCpoAttribute().size() + " attributes ");

    for (var jaxbElement : ctClass.getCpoAttribute()) {
      CtAttribute ctAttribute = jaxbElement.getValue();
      logger.debug("ctAttribute: " + ctAttribute);
      logger.debug("ctAttribute.getValue(): " + ctAttribute);
      logger.debug("ctAttribute.getValue().getJavaName(): " + ctAttribute.getJavaName());
      CpoAttribute cpoAttribute = cpoClass.getAttributeJava(ctAttribute.getJavaName());
      logger.debug("cpoAttribute: " + cpoAttribute);

      if (cpoAttribute == null) {
        cpoAttribute = createCpoAttribute();
        logger.debug("loading attribute: " + ctAttribute.getJavaName());
        loadCpoAttribute(cpoAttribute, ctAttribute);
        logger.debug("loaded attribute: " + ctAttribute.getJavaName());
        cpoClass.addAttribute(cpoAttribute);
      } else {
        loadCpoAttribute(cpoAttribute, ctAttribute);
      }
    }

    for (CtFunctionGroup ctFunctionGroup : ctClass.getCpoFunctionGroup()) {
      CpoFunctionGroup functionGroup = null;

      try {
        functionGroup =
            cpoClass.getFunctionGroup(
                Crud.valueOf(ctFunctionGroup.getType().toString()), ctFunctionGroup.getName());
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

  protected void loadCpoFunctionGroup(
      CpoFunctionGroup cpoFunctionGroup, CtFunctionGroup ctFunctionGroup) {
    cpoFunctionGroup.setDescription(ctFunctionGroup.getDescription());
    cpoFunctionGroup.setName(ctFunctionGroup.getName());
    cpoFunctionGroup.setType(ctFunctionGroup.getType().toString());

    for (CtFunction ctFunction : ctFunctionGroup.getCpoFunction()) {
      CpoFunction cpoFunction = createCpoFunction();
      cpoFunctionGroup.addFunction(cpoFunction);
      loadCpoFunction(cpoFunction, ctFunction);
    }
  }

  protected void loadCpoFunction(CpoFunction cpoFunction, CtFunction ctFunction) {
    cpoFunction.setName(ctFunction.getName());
    cpoFunction.setExpression(ctFunction.getExpression());
    cpoFunction.setDescription(ctFunction.getDescription());

    for (var jaxbElement : ctFunction.getCpoArgument()) {
      CtArgument ctArgument = jaxbElement.getValue();
      CpoArgument cpoArgument = createCpoArgument();
      cpoFunction.addArgument(cpoArgument);
      logger.debug("loading argument: " + ctArgument.getAttributeName());
      loadCpoArgument(cpoArgument, ctArgument);
      logger.debug("loaded argument: " + ctArgument.getAttributeName());
    }
  }

  protected void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument) {
    cpoArgument.setName(ctArgument.getAttributeName());
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
