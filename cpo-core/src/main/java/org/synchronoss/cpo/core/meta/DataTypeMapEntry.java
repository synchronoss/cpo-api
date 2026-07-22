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

/**
 * DataTypeMapEntry is a class that defines the mapping of datasource datatypes to java types
 *
 * @param <T> the Java type this entry maps a native data type to
 * @author david berry
 */
public class DataTypeMapEntry<T> implements java.io.Serializable, Cloneable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  /** The integer identifier of the native data type. */
  private int dataTypeInt = Integer.MIN_VALUE;

  /** The name of the native data type. */
  private String dataTypeName = null;

  /** The Java class values of this data type are represented as. */
  private Class<T> javaClass = null;

  @SuppressWarnings("unused")
  private DataTypeMapEntry() {}

  /**
   * Creates an entry mapping a native data type to a Java class.
   *
   * @param dataTypeInt the integer identifier of the native data type
   * @param dataTypeName the name of the native data type
   * @param javaClass the Java class values of this data type are represented as
   */
  public DataTypeMapEntry(int dataTypeInt, String dataTypeName, Class<T> javaClass) {
    this.dataTypeInt = dataTypeInt;
    this.dataTypeName = dataTypeName;
    this.javaClass = javaClass;
  }

  /**
   * Gets the integer identifier of the native data type.
   *
   * @return the native data type's integer identifier
   */
  public int getDataTypeInt() {
    return dataTypeInt;
  }

  /**
   * Gets the name of the native data type.
   *
   * @return the native data type's name
   */
  public String getDataTypeName() {
    return dataTypeName;
  }

  /**
   * Gets the Java class values of this data type are represented as.
   *
   * @return the mapped Java class
   */
  public Class<T> getJavaClass() {
    return javaClass;
  }

  /**
   * Converts a {@code SNAKE_CASE} native data type name to {@code camelCase}, e.g. for use as a
   * generated Java identifier.
   *
   * @param dataTypeName the native data type name to convert
   * @return the camel-cased equivalent of {@code dataTypeName}
   */
  public String makeJavaName(String dataTypeName) {
    dataTypeName = dataTypeName.toLowerCase();

    int idx;
    while ((idx = dataTypeName.indexOf("_")) > 0) {
      // remove the underscore, upper case the following character
      dataTypeName =
          dataTypeName.substring(0, idx)
              + dataTypeName.substring(idx + 1, idx + 2).toUpperCase()
              + dataTypeName.substring(idx + 2);
    }
    return dataTypeName;
  }
}
