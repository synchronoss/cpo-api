package org.synchronoss.cpo.core.meta;

/*-
 * [[
 * core
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA. User: dberry Date: 10/10/13 Time: 21:33 PM To change this template
 * use File | Settings | File Templates.
 */
public class DataTypeMapper {

  private static final Logger logger = LoggerFactory.getLogger(DataTypeMapper.class);
  private HashMap<Integer, DataTypeMapEntry<?>> dataTypeIntMap = new HashMap<>();
  private HashMap<String, DataTypeMapEntry<?>> dataTypeNameMap = new HashMap<>();
  DataTypeMapEntry<?> defaultDataTypeMapEntry = null;

  // Do not allow default constructor
  private DataTypeMapper() {}

  public DataTypeMapper(DataTypeMapEntry<?> defaultDataTypeMapEntry) {
    if (defaultDataTypeMapEntry == null) {
      throw new IllegalArgumentException();
    }
    this.defaultDataTypeMapEntry = defaultDataTypeMapEntry;
  }

  public void addDataTypeEntry(DataTypeMapEntry<?> dtme) {
    dataTypeIntMap.put(dtme.getDataTypeInt(), dtme);
    dataTypeNameMap.put(dtme.getDataTypeName(), dtme);
    logger.debug("Added DataType " + dtme.getDataTypeName());
  }

  public DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeIntMap.get(dataTypeInt);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry;
  }

  public DataTypeMapEntry<?> getDataTypeMapEntry(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry;
  }

  public int getDataTypeInt(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry.getDataTypeInt();
  }

  public Class<?> getDataTypeJavaClass(int dataTypeInt) {
    return getDataTypeJavaClass(Integer.valueOf(dataTypeInt));
  }

  public Class<?> getDataTypeJavaClass(Integer dataTypeInt) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeIntMap.get(dataTypeInt);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry.getJavaClass();
  }

  public Class<?> getDataTypeJavaClass(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null) {
      dataTypeMapEntry = defaultDataTypeMapEntry;
    }

    return dataTypeMapEntry.getJavaClass();
  }

  public List<String> getDataTypeNames() {
    ArrayList<String> al = new ArrayList<>();
    // need to put the keySet into an arraylist. The inner class is not serializable
    al.addAll(dataTypeNameMap.keySet());
    return al;
  }
}
