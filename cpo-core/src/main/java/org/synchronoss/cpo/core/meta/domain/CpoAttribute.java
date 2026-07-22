package org.synchronoss.cpo.core.meta.domain;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoData;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.helper.CpoClassLoader;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.bean.CpoAttributeBean;
import org.synchronoss.cpo.core.transform.CpoTransform;

/**
 * Runtime metadata for a single JavaBean-to-datastore attribute binding: extends {@link
 * CpoAttributeBean}'s plain configuration data with the reflectively-resolved getter/setter {@link
 * Method}s and, if configured, the {@link CpoTransform} instance and its transform methods. {@link
 * #loadRunTimeInfo(CpoMetaDescriptor, Class)} performs that resolution against a concrete bean
 * class and must be called before the getter/setter/transform accessors are used.
 *
 * @author dberry
 */
public class CpoAttribute extends CpoAttributeBean {

  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CpoAttribute.class);
  protected static final String TRANSFORM_IN_NAME = "transformIn";
  protected static final String TRANSFORM_OUT_NAME = "transformOut";

  /** The resolved name of the bean getter method. */
  private String getterName_ = null;

  /** The resolved name of the bean setter method. */
  private String setterName_ = null;

  /** The reflectively-resolved bean getter method. */
  private Method getter_ = null;

  /** The reflectively-resolved bean setter method. */
  private Method setter_ = null;

  /** The native datastore type code for this attribute. */
  private int dataTypeInt = Integer.MIN_VALUE;

  // Transform attributes
  /** The configured transform instance, if any. */
  private CpoTransform cpoTransform = null;

  /** The resolved {@link #TRANSFORM_IN_NAME} method on {@link #cpoTransform}, if any. */
  private Method transformInMethod = null;

  /** The resolved {@link #TRANSFORM_OUT_NAME} method on {@link #cpoTransform}, if any. */
  private Method transformOutMethod = null;

  // cached because Method.getParameterTypes() clones its array on every call
  /** The parameter type of {@link #transformInMethod}, cached to avoid repeated reflection. */
  private Class<?> transformInParamType = null;

  /** Creates an empty instance. Call {@link #loadRunTimeInfo} before use. */
  public CpoAttribute() {}

  /**
   * Gets the transform instance configured for this attribute, if any.
   *
   * @param <D> the transform's datastore-side type
   * @param <J> the transform's Java-side type
   * @return the configured transform, or {@code null} if none is configured
   */
  public <D, J> CpoTransform<D, J> getCpoTransform() {
    return cpoTransform;
  }

  /**
   * Gets the resolved {@code transformIn} method of the configured transform.
   *
   * @return the {@code transformIn} method, or {@code null} if no transform is configured
   */
  public Method getTransformInMethod() {
    return transformInMethod;
  }

  /**
   * Gets the resolved {@code transformOut} method of the configured transform.
   *
   * @return the {@code transformOut} method, or {@code null} if no transform is configured
   */
  public Method getTransformOutMethod() {
    return transformOutMethod;
  }

  /**
   * Gets the parameter type of the configured transform's {@code transformIn} method.
   *
   * @return the {@code transformIn} parameter type, or {@code null} if no transform is configured
   */
  public Class<?> getTransformInParamType() {
    return transformInParamType;
  }

  /**
   * Gets the parameter type expected by the bean's setter method.
   *
   * @return the setter's parameter type
   */
  public Class<?> getSetterParamType() {
    return getter_.getReturnType();
  }

  /**
   * Gets the return type of the bean's getter method.
   *
   * @return the getter's return type
   */
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

  /**
   * Sets the integer identifier of this attribute's native data type.
   *
   * @param dataTypeInt the native data type identifier
   */
  public void setDataTypeInt(int dataTypeInt) {
    this.dataTypeInt = dataTypeInt;
  }

  /**
   * Gets the integer identifier of this attribute's native data type.
   *
   * @return the native data type identifier
   */
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
    } catch (SecurityException e) {
      throw new CpoException("findMethod() Failed: " + methodName);
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

  /**
   * Invokes the bean's setter method, passing the value read from {@code cpoData}.
   *
   * @param instanceObject the bean instance to set the value on
   * @param cpoData the source of the value, via {@link CpoData#invokeGetter()}
   * @throws CpoException if the setter cannot be invoked
   */
  public void invokeSetter(Object instanceObject, CpoData cpoData) throws CpoException {
    try {
      setter_.invoke(instanceObject, cpoData.invokeGetter());
    } catch (IllegalAccessException | InvocationTargetException | IllegalArgumentException e) {
      Logger localLogger =
          instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());
      String msg =
          "Error Invoking Setter Method: "
              + getDataName()
              + ":"
              + getJavaName()
              + ":"
              + setterName_;
      localLogger.debug(msg + ExceptionHelper.getLocalizedMessage(e));
      throw new CpoException(msg, e);
    }
  }

  /**
   * Invokes the bean's getter method to read this attribute's value.
   *
   * @param obj the bean instance to read the value from
   * @return the value returned by the bean's getter
   * @throws CpoException if the getter cannot be invoked
   */
  public Object invokeGetter(Object obj) throws CpoException {
    try {
      return getGetter().invoke(obj, (Object[]) null);
    } catch (IllegalAccessException | InvocationTargetException e) {
      Logger localLogger = LoggerFactory.getLogger(obj.getClass());
      String msg = "invokeGetter: Could not invoke getter for " + obj.getClass();
      localLogger.debug(msg);
      throw new CpoException(msg, e);
    }
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

  /**
   * Gets whether one of {@code clazz}/{@code paramClass} is a primitive type and the other is its
   * corresponding wrapper class (e.g. {@code int}/{@code Integer}), determined heuristically by
   * name and by checking for a wrapper constructor that accepts the primitive.
   *
   * @param clazz one of the two classes being compared
   * @param paramClass the other class being compared
   * @return {@code true} if exactly one of the two is primitive and it is assignable to/from the
   *     other via boxing, {@code false} otherwise
   */
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

  /**
   * Resolves this attribute's reflective getter/setter methods against {@code metaClass}, and
   * instantiates and resolves its configured transform (if any) via {@code metaDescriptor}. Must be
   * called before {@link #invokeGetter(Object)}, {@link #invokeSetter(Object, CpoData)}, or any of
   * the getter/setter/transform accessor methods are used.
   *
   * @param metaDescriptor the owning descriptor, used to instantiate the transform class
   * @param metaClass the concrete bean class to resolve the getter/setter against, or {@code null}
   *     to skip getter/setter resolution
   * @throws CpoException if the transform cannot be instantiated, or the getter/setter cannot be
   *     found
   */
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
    if (!failedMessage.isEmpty()) {
      throw new CpoException(failedMessage.toString());
    }
  }

  protected void initTransformClass(CpoMetaDescriptor metaDescriptor) throws CpoException {
    String className = getTransformClassName();
    Class<?> transformClass;

    if (className != null && !className.isEmpty()) {
      try {
        transformClass = CpoClassLoader.forName(className);
      } catch (ClassNotFoundException e) {
        String msg = "Invalid Transform Class specified:<" + className + ">";
        throw new CpoException(msg, e);
      }

      Object transformObject;
      try {
        transformObject = transformClass.getDeclaredConstructor().newInstance();
      } catch (InstantiationException
          | IllegalAccessException
          | InvocationTargetException
          | NoSuchMethodException e) {
        String msg = "Error Setting Transform Class: ";
        throw new CpoException(msg, e);
      }

      if (transformObject instanceof CpoTransform) {
        cpoTransform = (CpoTransform) transformObject;
        List<Method> methods = findMethods(transformClass, TRANSFORM_IN_NAME, 1, true);
        if (methods.size() > 0) {
          transformInMethod = methods.get(0);
          transformInParamType = transformInMethod.getParameterTypes()[0];
        }
        methods = findMethods(transformClass, TRANSFORM_OUT_NAME, 1, true);
        if (methods.size() > 0) {
          transformOutMethod = methods.get(0);
        }
      } else {
        throw new CpoException("Invalid CpoTransform Class specified:<" + className + ">");
      }
    }
  }

  @Override
  public String toString() {
    return this.getJavaName();
  }

  /**
   * Gets the full field-by-field string representation of this attribute, as produced by {@link
   * CpoAttributeBean#toString()}.
   *
   * @return the full string representation
   */
  public String toStringFull() {
    return super.toString();
  }
}
