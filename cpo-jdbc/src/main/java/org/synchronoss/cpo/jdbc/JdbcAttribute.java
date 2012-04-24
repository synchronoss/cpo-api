/**
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * A copy of the GNU Lesser General Public License may also be found at http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.Types;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoByteArrayInputStream;
import org.synchronoss.cpo.CpoCharArrayReader;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.jdbc.JdbcTransform;

/**
 * JdbcAttribute is a class that maps traditional java classes to tables in a jdbc database.
 *
 * @author david berry
 */
public class JdbcAttribute extends CpoAttribute implements java.io.Serializable, java.lang.Cloneable {

  private static Logger logger = LoggerFactory.getLogger(JdbcAttribute.class.getName());
  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  private String dbTable_ = null;
  private String dbColumn_ = null;
  private int javaSqlType_ = Types.NULL;
  //Transform attributes
  private JdbcTransform jdbcTransform = null;
  private Method transformPSOutMethod = null;
  private Method transformCSOutMethod = null;

  public JdbcAttribute() {
  }

  public void setDbTable(String dbTable) {
    dbTable_ = dbTable;
  }

  public void setDbColumn(String dbColumn) {
    dbColumn_ = dbColumn;
  }

  public String getDbTable() {
    return dbTable_;
  }

  public String getDbColumn() {
    return dbColumn_;
  }

  public void invokeSetter(Object obj, ResultSet rs, int idx) throws CpoException {
    JavaSqlMethod<?> jdbcMethod;
    Object param = null;
    Class<?> paramClass = null;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());

    if (getSetters().isEmpty()) {
      throw new CpoException("There are no setters");
    }

    if (jdbcTransform != null) {
      localLogger.info("Calling Transform In:" + jdbcTransform.getClass().getName());

      // Get the JavaSqlMethod for the class that we are passing into the transform
      jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getTransformInMethod().getParameterTypes()[0]);

      try {
        // Get the getter for the ResultSet
        param = jdbcMethod.getRsGetter().invoke(rs, new Object[]{new Integer(idx)});
        param = transformIn(param);
        paramClass = getTransformInMethod().getReturnType();
      } catch (IllegalAccessException iae) {
        localLogger.debug("Error Invoking ResultSet Method: " + ExceptionHelper.getLocalizedMessage(iae));
        throw new CpoException(iae);
      } catch (InvocationTargetException ite) {
        localLogger.debug("Error Invoking ResultSet Method: " + ExceptionHelper.getLocalizedMessage(ite));
        throw new CpoException(ite.getCause());
      }

    }

    for (Method setter : getSetters()) {
      try {
        if (jdbcTransform == null) {
          // Get the JavaSqlMethod for the class that we are passing in as the Setter parameter
          jdbcMethod = JavaSqlMethods.getJavaSqlMethod(setter.getParameterTypes()[0]);

          // Get the getter for the ResultSet
          param = jdbcMethod.getRsGetter().invoke(rs, new Object[]{new Integer(idx)});
          paramClass = jdbcMethod.getJavaSqlMethodClass();
        }
        if (setter.getParameterTypes()[0].isAssignableFrom(paramClass) || isPrimitiveAssignableFrom(setter.getParameterTypes()[0], paramClass)) {
          setter.invoke(obj, new Object[]{param});
          return;
        }
      } catch (IllegalAccessException iae) {
        localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(iae));
      } catch (InvocationTargetException ite) {
        localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(ite));
      }
    }

    throw new CpoException("invokeSetter: Could not find a Setter for " + obj.getClass() + ": Column<" + this.getDataName() + "> Attribute<" + this.getJavaName() + ">");
  }

  public void invokeSetter(Object obj, CallableStatement cs, int idx) throws CpoException {
    JavaSqlMethod<?> jdbcMethod;
    Object param = null;
    Class<?> paramClass = null;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());

    if (getSetters().isEmpty()) {
      throw new CpoException("There are no setters");
    }

    if (jdbcTransform != null) {
      localLogger.info("Calling Transform In:" + jdbcTransform.getClass().getName());

      // Get the jdbcType for the class that we are passing into the transform
      jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getTransformInMethod().getParameterTypes()[0]);

      try {
        // Get the getter for the Callable Statement
        param = jdbcMethod.getCsGetter().invoke(cs, new Object[]{new Integer(idx)});
        param = transformIn(param);
        paramClass = getTransformInMethod().getReturnType();
      } catch (IllegalAccessException iae) {
        localLogger.debug("Error Invoking CallableStatement Method: " + ExceptionHelper.getLocalizedMessage(iae));
        throw new CpoException(iae);
      } catch (InvocationTargetException ite) {
        localLogger.debug("Error Invoking CallableStatement Method: " + ExceptionHelper.getLocalizedMessage(ite));
        throw new CpoException(ite.getCause());
      }
    }

    for (Method setter : getSetters()) {
      try {

        if (jdbcTransform == null) {
          // Get the jdbcType for the class that we are passing in as the Setter parameter
          jdbcMethod = JavaSqlMethods.getJavaSqlMethod(setter.getParameterTypes()[0]);

          // Get the getter for the CallableStatement
          // What we get from the cs will be set in the value object
          param = jdbcMethod.getCsGetter().invoke(cs, new Object[]{new Integer(idx)});
          paramClass = jdbcMethod.getJavaSqlMethodClass();
        }

        if (setter.getParameterTypes()[0].isAssignableFrom(paramClass)) {
          setter.invoke(obj, new Object[]{param});
          return;
        }
      } catch (IllegalAccessException iae) {
        localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(iae));
      } catch (InvocationTargetException ite) {
        localLogger.debug("Error Invoking Setter Method: " + ExceptionHelper.getLocalizedMessage(ite));
      }
    }

    throw new CpoException("invokeSetter: Could not find a Setter for " + obj.getClass());
  }

  public void invokeGetter(JdbcCallableStatementFactory jcsf, Object obj, int idx) throws CpoException {
    Object param;
    JavaSqlMethod<?> jdbcMethod;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());

    try {
      if (jdbcTransform != null) {
        localLogger.info("Calling Transform Out:" + jdbcTransform.getClass().getName());
        param = transformOut(jcsf, getGetters().get(0).invoke(obj, (Object[]) null));
        jdbcMethod = JavaSqlMethods.getJavaSqlMethod(transformCSOutMethod.getReturnType());
      } else {
        jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getGetters().get(0).getReturnType());
        param = getGetters().get(0).invoke(obj, (Object[]) null);
      }
      int length = 0;

      localLogger.info(this.getDataName() + "=" + param);

      switch (jdbcMethod.getMethodType()) {
        case JavaSqlMethod.METHOD_TYPE_BASIC:
          jdbcMethod.getCsSetter().invoke(jcsf.getCallableStatement(), new Object[]{new Integer(idx), param});
          break;
        case JavaSqlMethod.METHOD_TYPE_STREAM:
          CpoByteArrayInputStream cbis = CpoByteArrayInputStream.getCpoStream((InputStream) param);
          // Get the length of the InputStream in param
          jdbcMethod.getCsSetter().invoke(jcsf.getCallableStatement(), new Object[]{new Integer(idx), (InputStream) cbis, new Integer(length)});
          break;
        case JavaSqlMethod.METHOD_TYPE_READER:
          CpoCharArrayReader ccar = CpoCharArrayReader.getCpoReader((Reader) param);
          // Get the length of the Reader in param
          jdbcMethod.getCsSetter().invoke(jcsf.getCallableStatement(), new Object[]{new Integer(idx), (Reader) ccar, new Integer(length)});
          break;
      }
      return;
    } catch (IllegalAccessException iae) {
      localLogger.debug("Error Invoking Getter Method: " + ExceptionHelper.getLocalizedMessage(iae));
    } catch (InvocationTargetException ite) {
      localLogger.debug("Error Invoking Getter Method: " + ExceptionHelper.getLocalizedMessage(ite));
    }

    throw new CpoException("invokeGetter: Could not find a Getter for " + obj.getClass());
  }

  public void invokeGetter(JdbcPreparedStatementFactory jpsf, Object obj, int idx) throws CpoException {
    Object param = null;
    JavaSqlMethod<?> jdbcMethod = null;
    String msg = null;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());
    try {
      if (jdbcTransform != null) {
        localLogger.info("Calling Transform Out:" + jdbcTransform.getClass().getName());
        param = transformOut(jpsf, getGetters().get(0).invoke(obj, (Object[]) null));
        jdbcMethod = JavaSqlMethods.getJavaSqlMethod(transformPSOutMethod.getReturnType());
        if (jdbcMethod == null) {
          throw new CpoException("Error Retrieveing Jdbc Method for type: " + transformPSOutMethod.getReturnType().getName());
        }
      } else {
        jdbcMethod = JavaSqlMethods.getJavaSqlMethod(getGetters().get(0).getReturnType());
        param = getGetters().get(0).invoke(obj, (Object[]) null);
        if (jdbcMethod == null) {
          localLogger.debug("jdbcMethod is null");
          throw new CpoException("Error Retrieveing Jdbc Method for type: " + getGetters().get(0).getReturnType().getName());
        }
      }
    } catch (Exception e) {
      msg = "Error Invoking Getter Method: " + getGetters().get(0).getReturnType().getName() + " " + getGetters().get(0).getName() + "():" + ExceptionHelper.getLocalizedMessage(e);
    }

    if (msg == null) {
      localLogger.info(this.getDataName() + "=" + param);
      try {
        switch (jdbcMethod.getMethodType()) {
          case JavaSqlMethod.METHOD_TYPE_BASIC:
            jdbcMethod.getPsSetter().invoke(jpsf.getPreparedStatement(), new Object[]{new Integer(idx), param});
            break;
          case JavaSqlMethod.METHOD_TYPE_STREAM:
            CpoByteArrayInputStream cbais = CpoByteArrayInputStream.getCpoStream((InputStream) param);
            // Get the length of the InputStream in param
            jdbcMethod.getPsSetter().invoke(jpsf.getPreparedStatement(), new Object[]{new Integer(idx), (InputStream) cbais, new Integer(cbais.getLength())});
            break;
          case JavaSqlMethod.METHOD_TYPE_READER:
            CpoCharArrayReader ccar = CpoCharArrayReader.getCpoReader((Reader) param);
            // Get the length of the Reader in param
            jdbcMethod.getPsSetter().invoke(jpsf.getPreparedStatement(), new Object[]{new Integer(idx), (Reader) ccar, new Integer(ccar.getLength())});
            break;
        }
        return;
      } catch (Exception e) {
        msg = "Error Invoking Jdbc Method: " + jdbcMethod.getPsSetter().getName() + ":" + ExceptionHelper.getLocalizedMessage(e);
      }
    }

    if (msg != null) {
      localLogger.error(msg);
      throw new CpoException(msg);
    }
  }

  protected void setJavaSqlType(int type) {
    javaSqlType_ = type;

  }

  protected int getJavaSqlType() {
    return this.javaSqlType_;
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

  protected Object transformOut(JdbcPreparedStatementFactory jpsf, Object attributeObject) throws CpoException {
    Object retObj = attributeObject;

    if (jdbcTransform != null) {
      retObj = jdbcTransform.transformOut(jpsf, attributeObject);
    }
    return retObj;
  }

  protected Object transformOut(JdbcCallableStatementFactory jcsf, Object attributeObject) throws CpoException {
    Object retObj = attributeObject;

    if (jdbcTransform != null) {
      retObj = jdbcTransform.transformOut(jcsf, attributeObject);
    }
    return retObj;
  }

  @Override
  protected void initTransformClass() throws CpoException {
    super.initTransformClass();
    if (getCpoTransform() != null && getCpoTransform() instanceof JdbcTransform) {
      jdbcTransform = (JdbcTransform) getCpoTransform();

      for (Method m : findMethods(jdbcTransform.getClass(), TRANSFORM_OUT_NAME, 2, true)) {
        if (m.getParameterTypes()[0].getName().equals("org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory")) {
          transformPSOutMethod = m;
        } else if (m.getParameterTypes()[0].getName().equals("org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory")) {
          transformCSOutMethod = m;
        }
      }
    }
    setJavaSqlType(JavaSqlTypes.getJavaSqlType(getDataName()));
  }
}
