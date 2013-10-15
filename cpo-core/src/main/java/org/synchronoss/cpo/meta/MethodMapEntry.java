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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;

import java.lang.reflect.Method;

/**
 * MethodMapEntry is a class defines the getters and setters for JDBC specific data classes
 *
 * @author david berry
 */
public class MethodMapEntry<J,D> implements java.io.Serializable, Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(MethodMapEntry.class);
  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  public static final int METHOD_TYPE_BASIC = 0;
  private Class<J> javaClass_ = null;
  private Class<D> datasourceMethodClass = null;
  private Method rsGetter = null;
  private Method bsSetter = null;
  private int methodType = METHOD_TYPE_BASIC;

  @SuppressWarnings("unused")
  private MethodMapEntry() {
  }

  public MethodMapEntry(int methodType, Class<J> javaClass, Class<D> datasourceMethodClass, Method rsGetter, Method bsSetter) {
      this.methodType = methodType;
      this.javaClass_ = javaClass;
      this.datasourceMethodClass = datasourceMethodClass;
      this.bsSetter = bsSetter;
      this.rsGetter = rsGetter;
  }

//  public void setBsSetter(String setterName) throws CpoException {
//    try {
//        bsSetter = B.class.getMethod(setterName, new Class[]{int.class, getJavaSqlMethodClass()});
//    } catch (NoSuchMethodException nsme) {
//      logger.error("Error loading Setter" + setterName, nsme);
//      throw new CpoException(nsme);
//    }
//  }
//
//  public void setRsGetter(String getterName) throws CpoException {
//    try {
//      rsGetter = R.class.getMethod(getterName, new Class[]{int.class});
//    } catch (NoSuchMethodException nsme) {
//      logger.error("Error loading Getter" + getterName, nsme);
//      throw new CpoException(nsme);
//    }
//  }
//
  public Class<J> getJavaClass() {
    return javaClass_;
  }

  public Class<D> getJavaSqlMethodClass() {
    return datasourceMethodClass;
  }

  public Method getRsGetter() {
    return rsGetter;
  }

  public Method getBsSetter() {
    return bsSetter;
  }

  public int getMethodType() {
    return methodType;
  }
}