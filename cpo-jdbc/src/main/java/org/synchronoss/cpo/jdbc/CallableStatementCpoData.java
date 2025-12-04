package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
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

import java.lang.reflect.InvocationTargetException;
import java.sql.CallableStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapEntry;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.CpoTransform;
import org.synchronoss.cpo.transform.jdbc.JdbcCpoTransform;

/**
 * A class that builds constructs and executes callable statements
 *
 * @author dberry
 */
public class CallableStatementCpoData extends AbstractStatementCpoData {

  private static final Logger logger = LoggerFactory.getLogger(CallableStatementCpoData.class);
  private CallableStatement cs = null;
  JdbcCallableStatementFactory jcsf = null;

  /**
   * Constructs a CallableStatementCpoData
   *
   * @param cs - The callable statement
   * @param cpoAttribute - The attribute to add.
   * @param index - The index of the attribute in the callable statement
   */
  public CallableStatementCpoData(CallableStatement cs, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.cs = cs;
  }

  /**
   * Constructs a CallableStatementCpoData
   *
   * @param jcsf - The factory for building the callable statement
   * @param cpoAttribute - The attribute to add.
   * @param index - The index of the attribute in the callable statement
   */
  public CallableStatementCpoData(
      JdbcCallableStatementFactory jcsf, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.jcsf = jcsf;
  }

  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;
    CpoTransform cpoTransform = getCpoAttribute().getCpoTransform();

    if (cpoTransform != null) {
      if (cpoTransform instanceof JdbcCpoTransform) {
        retObj = ((JdbcCpoTransform) cpoTransform).transformOut(jcsf, attributeObject);
      } else {
        retObj = cpoTransform.transformOut(attributeObject);
      }
    }
    return retObj;
  }

  @Override
  public Object invokeGetter() throws CpoException {
    Object javaObject = null;
    JdbcMethodMapEntry<?, ?> jdbcMethodMapEntry =
        JdbcMethodMapper.getJavaSqlMethod(getDataGetterReturnType());
    if (jdbcMethodMapEntry == null) {
      if (Object.class.isAssignableFrom(getDataGetterReturnType())) {
        jdbcMethodMapEntry = JdbcMethodMapper.getJavaSqlMethod(Object.class);
      }
      if (jdbcMethodMapEntry == null) {
        throw new CpoException(
            "Error Retrieving Jdbc Method for type: " + getDataGetterReturnType().getName());
      }
    }

    try {
      // Get the getter for the Callable Statement
      switch (jdbcMethodMapEntry.getMethodType()) {
        case JdbcMethodMapEntry.METHOD_TYPE_BASIC:
        case JdbcMethodMapEntry.METHOD_TYPE_STREAM:
        case JdbcMethodMapEntry.METHOD_TYPE_READER:
          javaObject = jdbcMethodMapEntry.getCsGetter().invoke(cs, getIndex());
          break;
        case JdbcMethodMapEntry.METHOD_TYPE_OBJECT:
        default:
          javaObject =
              jdbcMethodMapEntry
                  .getCsGetter()
                  .invoke(cs, getIndex(), jdbcMethodMapEntry.getJavaClass());
          break;
      }
      javaObject = transformIn(javaObject);
    } catch (IllegalAccessException iae) {
      logger.debug(
          "Error Invoking CallableStatement Method: " + ExceptionHelper.getLocalizedMessage(iae));
      throw new CpoException(iae);
    } catch (InvocationTargetException ite) {
      logger.debug(
          "Error Invoking CallableStatement Method: " + ExceptionHelper.getLocalizedMessage(ite));
      throw new CpoException(ite.getCause());
    }

    return javaObject;
  }

  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    Logger localLogger =
        instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());
    CpoAttribute cpoAttribute = getCpoAttribute();
    Object param = transformOut(cpoAttribute.invokeGetter(instanceObject));
    localLogger.info(cpoAttribute.getDataName() + "=" + param);
    JdbcMethodMapEntry<?, ?> jdbcMethodMapEntry = getJdbcMethodMapEntry(instanceObject);
    try {
      jdbcMethodMapEntry.getCsSetter().invoke(jcsf.getCallableStatement(), getIndex(), param);
    } catch (Exception e) {
      throw new CpoException(
          "Error Invoking Jdbc Method: "
              + jdbcMethodMapEntry.getBsSetter().getName()
              + ":"
              + ExceptionHelper.getLocalizedMessage(e));
    }
  }
}
