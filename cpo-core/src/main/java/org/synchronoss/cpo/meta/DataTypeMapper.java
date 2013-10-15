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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 10/10/13
 * Time: 21:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class DataTypeMapper {
  private static final Logger logger = LoggerFactory.getLogger(DataTypeMapper.class);
  private HashMap<Integer, DataTypeMapEntry<?>> dataTypeIntMap = new HashMap<Integer, DataTypeMapEntry<?>>();
  private HashMap<String, DataTypeMapEntry<?>> dataTypeNameMap = new HashMap<String, DataTypeMapEntry<?>>();
  DataTypeMapEntry<?> defaultDataTypeMapEntry = null;

  // Do not allow default constructor
  private DataTypeMapper() {
  }

  public DataTypeMapper(DataTypeMapEntry<?> defaultDataTypeMapEntry) {
    if (defaultDataTypeMapEntry == null)
      throw new IllegalArgumentException();
    this.defaultDataTypeMapEntry = defaultDataTypeMapEntry;
  }

  public void addDataTypeEntry(DataTypeMapEntry<?> dtme){
    dataTypeIntMap.put(dtme.getDataTypeInt(), dtme);
    dataTypeNameMap.put(dtme.getDataTypeName(), dtme);
    logger.debug("Added DataType "+dtme.getDataTypeName());
  }

  public DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeIntMap.get(dataTypeInt);

    if (dataTypeMapEntry == null)
      dataTypeMapEntry = defaultDataTypeMapEntry;

    return dataTypeMapEntry;
  }

  public DataTypeMapEntry<?> getDataTypeMapEntry(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null)
      dataTypeMapEntry = defaultDataTypeMapEntry;

    return dataTypeMapEntry;
  }

  public int getDataTypeInt(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null)
      dataTypeMapEntry = defaultDataTypeMapEntry;

    return dataTypeMapEntry.getDataTypeInt();
  }

  public Class<?> getDataTypeJavaClass(int dataTypeInt) {
    return getDataTypeJavaClass(Integer.valueOf(dataTypeInt));
  }

  public Class<?> getDataTypeJavaClass(Integer dataTypeInt) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeIntMap.get(dataTypeInt);

    if (dataTypeMapEntry == null)
      dataTypeMapEntry = defaultDataTypeMapEntry;

    return dataTypeMapEntry.getJavaClass();
  }

  public Class<?> getDataTypeJavaClass(String dataTypeName) {
    DataTypeMapEntry<?> dataTypeMapEntry = dataTypeNameMap.get(dataTypeName);

    if (dataTypeMapEntry == null)
      dataTypeMapEntry = defaultDataTypeMapEntry;

    return dataTypeMapEntry.getJavaClass();
  }

  public List<String> getDataTypeNames() {
    ArrayList<String> al = new ArrayList<String>();
    // need to put the keySet into an arraylist. The inner class is not serializable
    al.addAll(dataTypeNameMap.keySet());
    return al;
  }

}
