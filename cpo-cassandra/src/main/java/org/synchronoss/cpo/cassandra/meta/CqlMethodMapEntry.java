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
package org.synchronoss.cpo.cassandra.meta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;

import java.lang.reflect.Method;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * CqlMethodMapEntry is a class defines the getters and setters for JDBC specific data classes
 *
 * @author david berry
 */
public class CqlMethodMapEntry<T> implements java.io.Serializable, Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(CqlMethodMapEntry.class);
  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  private Class<T> javaClass_ = null;
  private Class<T> javaSqlMethodClass_ = null;
  private Method rsGetter_ = null;
  private Method psSetter_ = null;
  private Method csGetter_ = null;
  private Method csSetter_ = null;
  private static final Class<PreparedStatement> psc = PreparedStatement.class;
  private static final Class<ResultSet> rsc = ResultSet.class;
  private static final Class<CallableStatement> csc = CallableStatement.class;

  @SuppressWarnings("unused")
  private CqlMethodMapEntry() {
  }

  public CqlMethodMapEntry(Class<T> javaClass, Class<T> javaSqlMethodClass, String getterName, String setterName) {

    try {
      this.javaClass_ = javaClass;
      this.javaSqlMethodClass_ = javaSqlMethodClass;
      setRsGetter(getterName);
      setPsSetter(setterName);
    } catch (CpoException ce) {
      logger.error("Error In CqlMethodMapEntry", ce);
    }
  }

  public void setPsSetter(String setterName) throws CpoException {
    try {
        psSetter_ = psc.getMethod(setterName, new Class[]{int.class, getJavaSqlMethodClass()});

    } catch (NoSuchMethodException nsme) {
      logger.error("Error loading Setter" + setterName, nsme);
      throw new CpoException(nsme);
    }
  }

  public void setRsGetter(String getterName) throws CpoException {
    try {
      rsGetter_ = rsc.getMethod(getterName, new Class[]{int.class});
    } catch (NoSuchMethodException nsme) {
      logger.error("Error loading Getter" + getterName, nsme);
      throw new CpoException(nsme);
    }
  }

  public Class<T> getJavaClass() {
    return javaClass_;
  }

  public Class<T> getJavaSqlMethodClass() {
    return javaSqlMethodClass_;
  }

  public Method getRsGetter() {
    return rsGetter_;
  }

  public Method getPsSetter() {
    return psSetter_;
  }
}