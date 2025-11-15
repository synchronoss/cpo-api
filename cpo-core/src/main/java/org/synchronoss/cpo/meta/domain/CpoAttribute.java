package org.synchronoss.cpo.meta.domain;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoData;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.CpoClassLoader;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.bean.CpoAttributeBean;
import org.synchronoss.cpo.transform.CpoTransform;

public class CpoAttribute extends CpoAttributeBean {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CpoAttribute.class);
  protected static final String TRANSFORM_IN_NAME = "transformIn";
  protected static final String TRANSFORM_OUT_NAME = "transformOut";
  private String getterName_ = null;
  private String setterName_ = null;
  private Method getter_ = null;
  private Method setter_ = null;
  private int dataTypeInt = Integer.MIN_VALUE;

  // Transform attributes
  private CpoTransform cpoTransform = null;
  private Method transformInMethod = null;
  private Method transformOutMethod = null;

  public CpoAttribute() {}

  public <D, J> CpoTransform<D, J> getCpoTransform() {
    return cpoTransform;
  }

  public Method getTransformInMethod() {
    return transformInMethod;
  }

  public Method getTransformOutMethod() {
    return transformOutMethod;
  }

  public Class<?> getSetterParamType() {
    return getter_.getReturnType();
  }

  public Class<?> getGetterReturnType() {
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

  public void setDataTypeInt(int dataTypeInt) {
    this.dataTypeInt = dataTypeInt;
  }

  public int getDataTypeInt() {
    return this.dataTypeInt;
  }

  protected List<Method> findMethods(Class<?> clazz, String methodName, int args, boolean hasReturn)
      throws CpoException {
    List<Method> retMethods = new ArrayList<>();

    try {
      Method[] methods = clazz.getMethods();

      // go through once and find the accessor methods that match the method name
      for (Method m : methods) {
        // The method name must match as well as the number of parameters and return types
        if (!m.isSynthetic()
            && !m.isBridge()
            && m.getName().equals(methodName)
            && m.getParameterTypes().length == args
            && ((!hasReturn && m.getReturnType() == Void.TYPE)
                || (hasReturn && m.getReturnType() != Void.TYPE))) {
          retMethods.add(m);
        }
      }
    } catch (Exception e) {
      throw new CpoException("findMethod() Failed - Method Not Found: " + methodName);
    }
    return retMethods;
  }

  protected String buildMethodName(String prefix, String base) {

    StringBuilder methodName = new StringBuilder();
    methodName.append(prefix);
    methodName.append(base);
    methodName.setCharAt(3, Character.toUpperCase(methodName.charAt(3)));

    return methodName.toString();
  }

  public void invokeSetter(Object instanceObject, CpoData cpoData) throws CpoException {
    Logger localLogger =
        instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());

    try {
      setter_.invoke(instanceObject, cpoData.invokeGetter());
    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
      localLogger.debug(
          "Error Invoking Setter Method: "
              + getDataName()
              + ":"
              + getJavaName()
              + ":"
              + setterName_
              + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  public Object invokeGetter(Object obj) throws CpoException {
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass());

    try {
      return getGetter().invoke(obj, (Object[]) null);
    } catch (IllegalAccessException | InvocationTargetException e) {
      localLogger.debug("Error Invoking Getter Method: " + ExceptionHelper.getLocalizedMessage(e));
    }

    throw new CpoException("invokeGetter: Could not find a Getter for " + obj.getClass());
  }

  private void dumpMethod(Method m) {
    logger.trace("========================");
    logger.trace("===> Declaring Class: " + m.getDeclaringClass().getName());
    logger.trace("===> Method Signature: " + m.toString());
    logger.trace("===> Generic Signature: " + m.toGenericString());
    logger.trace("===> Method isBridge: " + m.isBridge());
    logger.trace("===> Method isSynthetic: " + m.isSynthetic());
    logger.trace("========================");
  }

  public static boolean isPrimitiveAssignableFrom(Class<?> clazz, Class<?> paramClass) {

    // check to see if one is primitive and one is a possible wrapper
    if (clazz.isPrimitive() ^ paramClass.isPrimitive()) {
      // identify the prim and the wrapper
      Class<?> primClass, objClass;
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
        for (Constructor<?> ctor : objClass.getConstructors()) {
          Class<?>[] types = ctor.getParameterTypes();
          if (types.length > 0 && types[0].isAssignableFrom(primClass)) {
            return true;
          }
        }
      } else {
        logger.debug(
            "Wrapper Class:"
                + objClass.getName().toLowerCase()
                + "does not start with "
                + primClass.getName());
      }
    }

    return false;
  }

  public void loadRunTimeInfo(CpoMetaDescriptor metaDescriptor, Class<?> metaClass)
      throws CpoException {
    StringBuilder failedMessage = new StringBuilder();
    setGetterName(buildMethodName("get", getJavaName()));
    setSetterName(buildMethodName("set", getJavaName()));

    try {
      initTransformClass(metaDescriptor);
    } catch (Exception ce2) {
      failedMessage.append(ce2.getMessage());
    }
    if (metaClass != null) {
      try {
        List<Method> methods = findMethods(metaClass, getGetterName(), 0, true);
        if (methods.isEmpty()) {
          failedMessage.append(
              "loadRunTimeInfo: Could not find a Getter:"
                  + getGetterName()
                  + "("
                  + metaClass.getName()
                  + ")");
        } else {
          setGetter(methods.get(0));
          dumpMethod(getGetter());
        }
      } catch (CpoException ce1) {
        failedMessage.append(ce1.getMessage());
      }
      try {
        Class<?> actualClass = getGetterReturnType();

        for (Method setter : findMethods(metaClass, getSetterName(), 1, false)) {
          if (setter.getParameterTypes()[0].isAssignableFrom(actualClass)
              || isPrimitiveAssignableFrom(setter.getParameterTypes()[0], actualClass)) {
            setSetter(setter);
            dumpMethod(getSetter());
          }
        }
        if (getSetter() == null) {
          failedMessage.append(
              "loadRunTimeInfo: Could not find a Setter:"
                  + getSetterName()
                  + "("
                  + actualClass.getName()
                  + ")");
        }
      } catch (Exception ce2) {
        failedMessage.append(ce2.getMessage());
      }
    }
    if (failedMessage.length() > 0) {
      throw new CpoException(failedMessage.toString());
    }
  }

  protected void initTransformClass(CpoMetaDescriptor metaDescriptor) throws CpoException {
    String className = getTransformClassName();
    Class<?> transformClass;
    Logger localLogger = className == null ? logger : LoggerFactory.getLogger(className);

    if (className != null && className.length() > 0) {
      try {
        transformClass = CpoClassLoader.forName(className);
      } catch (Exception e) {
        localLogger.error("Invalid Transform Class specified:<" + className + ">");
        throw new CpoException("Invalid Transform Class specified:<" + className + ">:");
      }

      Object transformObject;
      try {
        transformObject = transformClass.newInstance();
      } catch (Exception e) {
        localLogger.debug(
            "Error Setting Transform Class: " + ExceptionHelper.getLocalizedMessage(e));
        throw new CpoException(e);
      }

      if (transformObject instanceof CpoTransform) {
        cpoTransform = (CpoTransform) transformObject;
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
