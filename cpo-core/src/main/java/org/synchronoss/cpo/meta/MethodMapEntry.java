package org.synchronoss.cpo.meta;

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

import java.lang.reflect.Method;

/**
 * MethodMapEntry is a class defines the getters and setters for JDBC specific data classes
 *
 * @author david berry
 */
public class MethodMapEntry<J, D> implements java.io.Serializable, Cloneable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  public static final int METHOD_TYPE_BASIC = 0;
  private Class<J> javaClass_ = null;
  private Class<D> datasourceMethodClass = null;
  private Method rsGetter = null;
  private Method bsSetter = null;
  private int methodType = METHOD_TYPE_BASIC;

  @SuppressWarnings("unused")
  private MethodMapEntry() {}

  public MethodMapEntry(
      int methodType,
      Class<J> javaClass,
      Class<D> datasourceMethodClass,
      Method rsGetter,
      Method bsSetter) {
    this.methodType = methodType;
    this.javaClass_ = javaClass;
    this.datasourceMethodClass = datasourceMethodClass;
    this.bsSetter = bsSetter;
    this.rsGetter = rsGetter;
  }

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
