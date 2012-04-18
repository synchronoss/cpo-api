/**
 *  Copyright (C) 2006-2012  David E. Berry
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

package org.synchronoss.cpo.meta.domain;

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.bean.CpoAttributeBean;
import org.synchronoss.cpo.transform.CpoTransform;

import java.lang.reflect.*;

public class CpoAttribute extends CpoAttributeBean implements IMetaDFVisitable {

  private static Logger logger = LoggerFactory.getLogger(CpoAttribute.class.getName());

  private String getterName_ = null;
  private String setterName_ = null;
  private Method[] getters_ = null;
  private Method[] setters_ = null;

  //Transform attributes
  private CpoTransform cpoTransform = null;

  public CpoAttribute() {
  }

  public <T> CpoAttribute(CpoClass<T> jmc, String name, String dataName, String transformClass) throws CpoException {
    LoggerFactory.getLogger(jmc.getMetaClass().getName()).debug("Adding Attribute for class " + jmc.getMetaClass().getName() + ": " + name + "(" + dataName + "," + transformClass + ")");
    setJavaName(name);
    setTransformClass(transformClass);
    initMethods(jmc);
    setDataName(dataName);
  }

  protected Method[] getGetters() {
    return getters_;
  }

  protected Method[] getSetters() {
    return setters_;
  }

  protected void setGetters(Method[] getters) {
    getters_ = getters;
  }

  protected void setSetters(Method[] setters) {
    setters_ = setters;
  }

  protected String getGetterName() {
    return getterName_;
  }

  protected String getSetterName() {
    return setterName_;
  }

  protected void setGetterName(String getterName) {
    getterName_ = getterName;
  }

  protected void setSetterName(String setterName) {
    setterName_ = setterName;
  }

  private <T> void initMethods(CpoClass<T> jmc) throws CpoException {
    StringBuilder failedMessage = new StringBuilder();
    setGetterName(buildMethodName("get", getJavaName()));
    setSetterName(buildMethodName("set", getJavaName()));

    try {
      setGetters(findMethods(jmc, getGetterName(), 0, true));
    } catch (CpoException ce1) {
      failedMessage.append(ce1.getMessage());
    }
    try {
      setSetters(findMethods(jmc, getSetterName(), 1, false));
    } catch (Exception ce2) {
      failedMessage.append(ce2.getMessage());
    }

    if (failedMessage.length() > 0) {
      throw new CpoException(failedMessage.toString());
    }
  }

  static protected <T> Method[] findMethods(CpoClass<T> jmc, String methodName, int args, boolean hasReturn) throws CpoException {
    Method m[] = null;
    int count = 0;
    int idx[] = null;
    Method ret[] = null;

    try {
      m = jmc.getMetaClass().getMethods();
      idx = new int[m.length];

      // go through once and find the accessor methods that match the method name
      for (int i = 0; i < m.length; i++) {
        // The method name must match as well as the number of parameters and return types
        if (m[i].getName().equals(methodName) && m[i].getParameterTypes().length == args) {
          if ((!hasReturn && m[i].getReturnType() == java.lang.Void.TYPE) || (hasReturn && m[i].getReturnType() != java.lang.Void.TYPE)) {
            idx[count++] = i;
          }
        }
      }

      // Now loop through and build return array
      if (count > 0) {
        ret = new Method[count];
        for (int i = 0; i < count; i++) {
          ret[i] = m[idx[i]];
        }
      } else {
        throw new Exception();
      }
    } catch (Exception e) {
      throw new CpoException("findMethod() Failed - Method Not Found: " + methodName);
    }
    return ret;
  }

  static protected String buildMethodName(String prefix, String base) {

    StringBuilder methodName = new StringBuilder();
    methodName.append(prefix);
    methodName.append(base);
    methodName.setCharAt(3, Character.toUpperCase(methodName.charAt(3)));

    return methodName.toString();
  }

  public void invokeSetter(Object obj, Object param, Class<?> paramClass) throws CpoException {
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());

    if (getSetters().length == 0)
      throw new CpoException("There are no setters");

    for (int i = 0; i < getSetters().length; i++) {
      try {
        if (getSetters()[i].getParameterTypes()[0].isAssignableFrom(paramClass) || isPrimitiveAssignableFrom(getSetters()[i].getParameterTypes()[0], paramClass)) {
          getSetters()[i].invoke(obj, new Object[]{param});
          return;
        }
      } catch (IllegalAccessException iae) {
        localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(iae));
      } catch (InvocationTargetException ite) {
        localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(ite));
      }
    }

    throw new CpoException("invokeSetter: Could not find a Setter for " + obj.getClass() + " Attribute<" + this.getJavaName() + ">");
  }

  public Object invokeGetter(Object obj) throws CpoException {
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());

    try {
      return getGetters()[0].invoke(obj, (Object[])null);
    } catch (IllegalAccessException iae) {
      localLogger.debug("Error Invoking Getter Method: " + ExceptionHelper.getLocalizedMessage(iae));
    } catch (InvocationTargetException ite) {
      localLogger.debug("Error Invoking Getter Method: " + ExceptionHelper.getLocalizedMessage(ite));
    }

    throw new CpoException("invokeGetter: Could not find a Getter for " + obj.getClass());
  }

  protected void setTransformClassForName(String className) throws CpoException {
    Class<CpoTransform> transformClass = null;
    Logger localLogger = className == null ? logger : LoggerFactory.getLogger(className);

    try {
      if (className != null && className.length() > 0) {
        try {
          transformClass = (Class<CpoTransform>)Class.forName(className);
        } catch (Exception e) {
          String msg = ExceptionHelper.getLocalizedMessage(e);

          localLogger.error("Invalid Transform Class specified:<" + className + ">");
          throw new CpoException("Invalid Transform Class specified:<" + className + ">:");
        }

        this.cpoTransform = transformClass.newInstance();
      }
    } catch (CpoException ce) {
      throw ce;
    } catch (Exception e) {
      localLogger.debug("Error Setting Transform Class: " + ExceptionHelper.getLocalizedMessage(e));
      throw new CpoException(e);
    }
  }

  private void dumpMethod(Method m) {
    logger.debug("========================");
    logger.debug("===> Declaring Class: " + m.getDeclaringClass().getName());
    logger.debug("===> Method Signature: " + m.toString());
    logger.debug("===> Generic Signature: " + m.toGenericString());
    logger.debug("===> Method isBridge: " + m.isBridge());
    logger.debug("===> Method isSynthetic: " + m.isSynthetic());
    logger.debug("========================");
  }

  protected Object transformIn(Object datasourceObject) throws CpoException {
    Object retObj = datasourceObject;

    if (cpoTransform != null) {
      retObj = cpoTransform.transformIn(datasourceObject);
    }
    return retObj;
  }

  protected Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;

    if (cpoTransform != null) {
      retObj = cpoTransform.transformOut(attributeObject);
    }
    return retObj;
  }

  static public boolean isPrimitiveAssignableFrom(Class clazz, Class paramClass) {

    // check to see if one is primitive and one is a possible wrapper
    if (clazz.isPrimitive() ^ paramClass.isPrimitive()) {
      // identify the prim and the wrapper
      Class primClass, objClass;
      if (clazz.isPrimitive()) {
        primClass = clazz;
        objClass = paramClass;
      } else {
        primClass = paramClass;
        objClass = clazz;
      }

      // Lets do a quick name check
      if (objClass.getSimpleName().toLowerCase().startsWith(primClass.getSimpleName())) {
        // go through the constructors of the wrapper to see if there one with a parameter type
        // that is the same as the primitive
        for (Constructor ctor : objClass.getConstructors()) {
          Class types[] = ctor.getParameterTypes();
          if (types.length > 0 && types[0].isAssignableFrom(primClass))
            return true;
        }
      } else {
        logger.debug("Wrapper Class:" + objClass.getName().toLowerCase() + "does not start with " + primClass.getName());
      }
    }

    return false;
  }

  @Override
  public void acceptMetaDFVisitor(IMetaVisitor visitor) {
    visitor.visit(this);
  }
}
