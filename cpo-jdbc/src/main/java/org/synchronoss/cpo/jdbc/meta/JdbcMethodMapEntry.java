/*
 * Copyright (C) 2003-2025 David E. Berry
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
package org.synchronoss.cpo.jdbc.meta;

import org.slf4j.*;
import org.synchronoss.cpo.meta.MethodMapEntry;

import java.io.Serial;
import java.lang.reflect.Method;

/**
 * MethodMapEntry is a class defines the getters and setters for JDBC specific data classes
 *
 * @param <D> - The datasource type
 * @param <J> - The java type
 *
 * @author david berry
 */
public class JdbcMethodMapEntry<J,D> extends MethodMapEntry<J,D> implements java.io.Serializable, java.lang.Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(JdbcMethodMapEntry.class);
  /**
   * Version Id for this class.
   */
  @Serial
  private static final long serialVersionUID = 1L;
    /**
     * Methods that accept and return streams
     */
  public static final int METHOD_TYPE_STREAM = 1;
    /**
     * Methods that accept and return readers
     */
  public static final int METHOD_TYPE_READER = 2;
    /**
     * Methods that accept and return objects
     */
  public static final int METHOD_TYPE_OBJECT = 3;
  private Method csGetter = null;
  private Method csSetter = null;

    /**
     * Constructs a JdbcMethodMapEntry
     *
     * @param methodType - The method type (METHOD_TYPE_STREAM, METHOD_TYPE_READER, METHOD_TYPE_OBJECT)
     * @param javaClass - The java clazz
     * @param datasourceMethodClass - datasource clazz with the method on it.
     * @param rsGetter - the resultset getter Method
     * @param bsSetter - the prepared statement setter method
     * @param csGetter - the callable statement getter method.
     * @param csSetter - the callable statement setter method.
     */
  public JdbcMethodMapEntry(int methodType, Class<J> javaClass, Class<D> datasourceMethodClass, Method rsGetter, Method bsSetter, Method csGetter, Method csSetter) {
    super(methodType, javaClass, datasourceMethodClass, rsGetter, bsSetter);
    this.csGetter = csGetter;
    this.csSetter = csSetter;
  }

    /**
     * Gets the callable statement getter
     *
     * @return The callable statement getter Method
     */
  public Method getCsGetter() {
    return csGetter;
  }

    /**
     * Gets the callable statement setter
     *
     * @return The callable statement setter Method
     */
  public Method getCsSetter() {
    return csSetter;
  }
}