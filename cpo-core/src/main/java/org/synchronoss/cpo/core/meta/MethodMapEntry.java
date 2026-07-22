package org.synchronoss.cpo.core.meta;

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

import java.lang.reflect.Method;

/**
 * MethodMapEntry is a class defines the getters and setters for JDBC specific data classes
 *
 * @param <J> the Java type moved by this entry's getter/setter pair
 * @param <D> the datastore-specific class (e.g. {@code ResultSet}, {@code Row}) that declares the
 *     getter/setter methods
 * @author david berry
 */
public class MethodMapEntry<J, D> implements java.io.Serializable, Cloneable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  /** The basic (default) method type: a plain getter/setter pair with no special handling. */
  public static final int METHOD_TYPE_BASIC = 0;

  /** The Java type moved by this entry's getter/setter pair. */
  private Class<J> javaClass_ = null;

  /** The datastore-specific class that declares the getter/setter methods. */
  private Class<D> datasourceMethodClass = null;

  /** The getter method used to read the value from the datastore. */
  private Method rsGetter = null;

  /** The setter method used to write the value to the datastore. */
  private Method bsSetter = null;

  /** The method type of this entry, e.g. {@link #METHOD_TYPE_BASIC}. */
  private int methodType = METHOD_TYPE_BASIC;

  @SuppressWarnings("unused")
  private MethodMapEntry() {}

  /**
   * Creates an entry describing the getter/setter pair used to move values of {@code javaClass} to
   * and from a datastore-specific class.
   *
   * @param methodType the method type, e.g. {@link #METHOD_TYPE_BASIC}
   * @param javaClass the Java type moved by this entry
   * @param datasourceMethodClass the datastore-specific class declaring the getter/setter
   * @param rsGetter the getter method (e.g. a {@code ResultSet} getter) used to read the value
   * @param bsSetter the setter method (e.g. a {@code PreparedStatement} setter) used to write the
   *     value
   */
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

  /**
   * Gets the Java type moved by this entry's getter/setter pair.
   *
   * @return the mapped Java class
   */
  public Class<J> getJavaClass() {
    return javaClass_;
  }

  /**
   * Gets the datastore-specific class that declares the getter/setter methods.
   *
   * @return the datastore-specific method-declaring class
   */
  public Class<D> getJavaSqlMethodClass() {
    return datasourceMethodClass;
  }

  /**
   * Gets the getter method used to read the value from the datastore.
   *
   * @return the getter method
   */
  public Method getRsGetter() {
    return rsGetter;
  }

  /**
   * Gets the setter method used to write the value to the datastore.
   *
   * @return the setter method
   */
  public Method getBsSetter() {
    return bsSetter;
  }

  /**
   * Gets the method type of this entry.
   *
   * @return the method type, e.g. {@link #METHOD_TYPE_BASIC}
   */
  public int getMethodType() {
    return methodType;
  }
}
