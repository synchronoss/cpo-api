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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Maps datastore data types, identified by either a {@code java.sql.Types} constant or a native
 * type name, to their {@link DataTypeMapEntry}. Lookups that don't match a registered type fall
 * back to the map's default entry.
 */
public class DataTypeMapper {

  private static final Logger logger = LoggerFactory.getLogger(DataTypeMapper.class);
  private HashMap<Integer, DataTypeMapEntry<?>> dataTypeIntMap = new HashMap<>();
  private HashMap<String, DataTypeMapEntry<?>> dataTypeNameMap = new HashMap<>();
  DataTypeMapEntry<?> defaultDataTypeMapEntry = null;

  // Do not allow default constructor
  private DataTypeMapper() {}

  /**
   * Creates a data type mapper with the given fallback entry for unrecognized types.
   *
   * @param defaultDataTypeMapEntry the entry to return when a lookup finds no registered type
   * @throws IllegalArgumentException if {@code defaultDataTypeMapEntry} is {@code null}
   */
  public DataTypeMapper(DataTypeMapEntry<?> defaultDataTypeMapEntry) {
    if (defaultDataTypeMapEntry == null) {
      throw new IllegalArgumentException();
    }
    this.defaultDataTypeMapEntry = defaultDataTypeMapEntry;
  }

  /**
   * Registers a data type entry, indexed by both its type code and its name.
   *
   * @param dtme the data type entry to register
   */
  public void addDataTypeEntry(DataTypeMapEntry<?> dtme) {
    dataTypeIntMap.put(dtme.getDataTypeInt(), dtme);
    dataTypeNameMap.put(dtme.getDataTypeName(), dtme);
    logger.debug("Added DataType " + dtme.getDataTypeName());
  }

  /**
   * Gets the data type entry registered under the given type code, or the default entry if none is
   * registered.
   *
   * @param dataTypeInt the datastore type code
   * @return the matching data type entry, or the default entry
   */
  public DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeIntMap.get(dataTypeInt);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry;
  }

  /**
   * Gets the data type entry registered under the given type name, or the default entry if none is
   * registered.
   *
   * @param dataTypeName the datastore type name
   * @return the matching data type entry, or the default entry
   */
  public DataTypeMapEntry<?> getDataTypeMapEntry(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry;
  }

  /**
   * Gets the type code for the data type registered under the given name, or the default entry's
   * type code if none is registered.
   *
   * @param dataTypeName the datastore type name
   * @return the matching (or default) type code
   */
  public int getDataTypeInt(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry.getDataTypeInt();
  }

  /**
   * Gets the Java class for the data type registered under the given type code, or the default
   * entry's Java class if none is registered.
   *
   * @param dataTypeInt the datastore type code
   * @return the matching (or default) Java class
   */
  public Class<?> getDataTypeJavaClass(int dataTypeInt) {
    return getDataTypeJavaClass(Integer.valueOf(dataTypeInt));
  }

  /**
   * Gets the Java class for the data type registered under the given type code, or the default
   * entry's Java class if none is registered.
   *
   * @param dataTypeInt the datastore type code
   * @return the matching (or default) Java class
   */
  public Class<?> getDataTypeJavaClass(Integer dataTypeInt) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeIntMap.get(dataTypeInt);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry.getJavaClass();
  }

  /**
   * Gets the Java class for the data type registered under the given name, or the default entry's
   * Java class if none is registered.
   *
   * @param dataTypeName the datastore type name
   * @return the matching (or default) Java class
   */
  public Class<?> getDataTypeJavaClass(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry.getJavaClass();
  }

  /**
   * Gets the names of all registered data types.
   *
   * @return a list of registered data type names
   */
  public List<String> getDataTypeNames() {
    ArrayList<String> al = new ArrayList<>();
    // need to put the keySet into an arraylist. The inner class is not serializable
    al.addAll(dataTypeNameMap.keySet());
    return al;
  }
}
