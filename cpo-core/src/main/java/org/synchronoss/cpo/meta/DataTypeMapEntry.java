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

/**
 * DataTypeMapEntry is a class that defines the mapping of datasource datatypes to java types
 *
 * @author david berry
 */
public class DataTypeMapEntry<T> implements java.io.Serializable, Cloneable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private int dataTypeInt = Integer.MIN_VALUE;
  private String dataTypeName = null;
  private Class<T> javaClass = null;

  @SuppressWarnings("unused")
  private DataTypeMapEntry() {}

  public DataTypeMapEntry(int dataTypeInt, String dataTypeName, Class<T> javaClass) {
    this.dataTypeInt = dataTypeInt;
    this.dataTypeName = dataTypeName;
    this.javaClass = javaClass;
  }

  public int getDataTypeInt() {
    return dataTypeInt;
  }

  public String getDataTypeName() {
    return dataTypeName;
  }

  public Class<T> getJavaClass() {
    return javaClass;
  }

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
