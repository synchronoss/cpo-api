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
package org.synchronoss.cpo.meta.domain;

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.bean.CpoAttributeBean;
import org.synchronoss.cpo.transform.CpoTransform;

import java.lang.reflect.*;
import java.util.*;

public class CpoAttribute extends CpoAttributeBean {

  private static final Logger logger = LoggerFactory.getLogger(CpoAttribute.class);
  protected static final String TRANSFORM_IN_NAME = "transformIn";
  protected static final String TRANSFORM_OUT_NAME = "transformOut";
  private String getterName_ = null;
  private String setterName_ = null;
  private Method getter_ = null;
  private Method setter_ = null;
  
  //Transform attributes
  private CpoTransform cpoTransform = null;
  private Method transformInMethod = null;
  private Method transformOutMethod = null;

  public CpoAttribute() {
  }

  public CpoTransform getCpoTransform() {
    return cpoTransform;
  }

  public Method getTransformInMethod() {
    return transformInMethod;
  }

  public Method getTransformOutMethod() {
    return transformOutMethod;
  }

  public Class getSetterParamType() {
    return getter_.getReturnType();
  }
 
  public Class getGetterReturnType() {
    return getter_.getReturnType();
   }
  
  protected Method getGetter() {
    return getter_;
  }

  protected Method getSetter() {
    return setter_;
  }

  protected void setGetter(Method getter) {
    getter_ = getter;
  }

  protected void setSetter(Method setter) {
    setter_ = setter;
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

  static protected List<Method> findMethods(Class clazz, String methodName, int args, boolean hasReturn) throws CpoException {
    int count = 0;
    List<Method> retMethods = new ArrayList<Method>();

    try {
      Method methods[] = clazz.getMethods();

      // go through once and find the accessor methods that match the method name
      for (Method m : methods) {
        // The method name must match as well as the number of parameters and return types
        if (!m.isSynthetic() && !m.isBridge() && m.getName().equals(methodName) && m.getParameterTypes().length == args) {
          if ((!hasReturn && m.getReturnType() == java.lang.Void.TYPE) || (hasReturn && m.getReturnType() != java.lang.Void.TYPE)) {
            retMethods.add(m);
          }
        }
      }
    } catch (Exception e) {
      throw new CpoException("findMethod() Failed - Method Not Found: " + methodName);
    }
    return retMethods;
  }

  static protected String buildMethodName(String prefix, String base) {

    StringBuilder methodName = new StringBuilder();
    methodName.append(prefix);
    methodName.append(base);
    methodName.setCharAt(3, Character.toUpperCase(methodName.charAt(3)));

    return methodName.toString();
  }

  public void invokeSetter(Object instanceObject, CpoData cpoData) throws CpoException {
    Logger localLogger = instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());

    try {
      setter_.invoke(instanceObject, new Object[]{cpoData.invokeGetter()});
    } catch (IllegalAccessException iae) {
      localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(iae));
    } catch (InvocationTargetException ite) {
      localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(ite));
    }
    
  }

  public Object invokeGetter(Object obj) throws CpoException {
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass());

    try {
      return getGetter().invoke(obj, (Object[])null);
    } catch (IllegalAccessException iae) {
      localLogger.debug("Error Invoking Getter Method: " + ExceptionHelper.getLocalizedMessage(iae));
    } catch (InvocationTargetException ite) {
      localLogger.debug("Error Invoking Getter Method: " + ExceptionHelper.getLocalizedMessage(ite));
    }

    throw new CpoException("invokeGetter: Could not find a Getter for " + obj.getClass());
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
          if (types.length > 0 && types[0].isAssignableFrom(primClass)) {
            return true;
          }
        }
      } else {
        logger.debug("Wrapper Class:" + objClass.getName().toLowerCase() + "does not start with " + primClass.getName());
      }
    }

    return false;
  }

  public void loadRunTimeInfo(CpoMetaDescriptor metaDescriptor, CpoClass cpoClass) throws CpoException {
    StringBuilder failedMessage = new StringBuilder();
    setGetterName(buildMethodName("get", getJavaName()));
    setSetterName(buildMethodName("set", getJavaName()));

    try {
      setGetter(findMethods(cpoClass.getMetaClass(), getGetterName(), 0, true).get(0));
    } catch (CpoException ce1) {
      failedMessage.append(ce1.getMessage());
    }
    try {
      initTransformClass(metaDescriptor);
    } catch (Exception ce2) {
      failedMessage.append(ce2.getMessage());
    }
    try {
      Class actualClass = getGetterReturnType();
      
      for (Method setter : findMethods(cpoClass.getMetaClass(), getSetterName(), 1, false)) {
        if (setter.getParameterTypes()[0].isAssignableFrom(actualClass) || isPrimitiveAssignableFrom(setter.getParameterTypes()[0], actualClass)) {
          setSetter(setter);
        }
      }
      if (getSetter()==null){
        failedMessage.append("invokeSetter: Could not find a Setter:" + getSetterName() + "(" + actualClass.getName() + ")");
      }
    } catch (Exception ce2) {
      failedMessage.append(ce2.getMessage());
    }
    if (failedMessage.length() > 0) {
      throw new CpoException(failedMessage.toString());
    }
  }

  protected void initTransformClass(CpoMetaDescriptor metaDescriptor) throws CpoException {
    String className = getTransformClassName();
    Class<?> transformClass = null;
    Logger localLogger = className == null ? logger : LoggerFactory.getLogger(className);

    if (className != null && className.length() > 0) {
      try {
        transformClass = Class.forName(className);
      } catch (Exception e) {
        String msg = ExceptionHelper.getLocalizedMessage(e);

        localLogger.error("Invalid Transform Class specified:<" + className + ">");
        throw new CpoException("Invalid Transform Class specified:<" + className + ">:");
      }

      Object transformObject;
      try {
        transformObject = transformClass.newInstance();
      } catch (Exception e) {
        localLogger.debug("Error Setting Transform Class: " + ExceptionHelper.getLocalizedMessage(e));
        throw new CpoException(e);
      }

      if (transformObject instanceof CpoTransform) {
        cpoTransform = (CpoTransform)transformObject;
        List<Method> methods = findMethods(transformClass, TRANSFORM_IN_NAME, 1, true);
        if (methods.size() > 0) {
          transformInMethod = methods.get(0);
        }
        methods = findMethods(transformClass, TRANSFORM_OUT_NAME, 1, true);
        if (methods.size() > 0) {
          transformOutMethod = methods.get(0);
        }
      } else {
        localLogger.error("Invalid CpoTransform Class specified:<" + className + ">");
        throw new CpoException("Invalid CpoTransform Class specified:<" + className + ">");
      }
    }
  }

  @Override
  public String toString() {
    return this.getJavaName();
  }

  public String toStringFull() {
    return super.toString();
  }
}
