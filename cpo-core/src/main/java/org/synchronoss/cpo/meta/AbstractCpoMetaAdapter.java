/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.meta;

import org.slf4j.*;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.exporter.*;
import org.synchronoss.cpo.meta.domain.*;

import java.util.*;

/**
 *
 * @author dberry
 */
public abstract class AbstractCpoMetaAdapter implements CpoMetaAdapter {

  private static Logger logger = LoggerFactory.getLogger(AbstractCpoMetaAdapter.class.getName());
  /**
   * The map of classes in this metaAdapter
   */
  private static SortedMap<String, CpoClass> classMap = new TreeMap<String, CpoClass>();
  private CpoClass currentClass = null;

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

    if (cpoClass.getMetaClass() == null) {
      cpoClass.loadRunTimeInfo(this);
    }

    return cpoClass;
  }

  @Override
  public List<CpoClass> getCpoClasses() {
    List<CpoClass> result = new ArrayList<CpoClass>();
    result.addAll(classMap.values());
    return result;
  }

  public void loadCpoMetaDataDocument(CpoMetaDataDocument metaDataDoc) throws CpoException {

    for (CtClass ctClass : metaDataDoc.getCpoMetaData().getCpoClassArray()) {
      CpoClass cpoClass = loadCpoClass(ctClass);
      addCpoClass(cpoClass);
    }

  }

  protected CpoClass loadCpoClass(CtClass ctClass) throws CpoException {
    logger.debug("Loading class: " + ctClass.getName());
    CpoClass cpoClass = createCpoClass();
    cpoClass.setName(ctClass.getName());
    cpoClass.setDescription(ctClass.getDescription());

    currentClass = cpoClass;

    for (CtAttribute ctAttribute : ctClass.getCpoAttributeArray()) {
      CpoAttribute cpoAttribute = createCpoAttribute();
      loadCpoAttribute(cpoAttribute, ctAttribute);
      cpoClass.addAttribute(cpoAttribute);
    }

    for (CtFunctionGroup ctFunctionGroup : ctClass.getCpoFunctionGroupArray()) {
      CpoFunctionGroup functionGroup = createCpoFunctionGroup();
      loadCpoFunctionGroup(functionGroup, ctFunctionGroup);
      cpoClass.addFunctionGroup(functionGroup);
    }

    return cpoClass;
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
    cpoFunctionGroup.setType(ctFunctionGroup.getType());

    for (CtFunction ctFunction : ctFunctionGroup.getCpoFunctionArray()) {
      CpoFunction cpoFunction = createCpoFunction();
      cpoFunctionGroup.addFunction(cpoFunction);
      loadCpoFunction(cpoFunction, ctFunction);
    }

  }

  protected void loadCpoFunction(CpoFunction cpoFunction, CtFunction ctFunction) {
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

  protected CpoClass createCpoClass() {
    return new CpoClass();
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

  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new CoreMetaXmlObjectExporter(this);
  }

  @Override
  public final CpoMetaDataDocument export() {
    MetaXmlObjectExporter metaXmlObjectExporter = getMetaXmlObjectExporter();
    for (CpoClass cpoClass : classMap.values()) {
      cpoClass.acceptMetaDFVisitor(metaXmlObjectExporter);
    }
    return metaXmlObjectExporter.getCpoMetaDataDocument();
  }

  protected void addCpoClass(CpoClass metaClass) {
    logger.debug("Adding class: " + metaClass.getName());
    classMap.put(metaClass.getName(), metaClass);
  }
}
