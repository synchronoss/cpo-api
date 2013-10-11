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
package org.synchronoss.cpo.jdbc.meta;

import org.synchronoss.cpo.CpoException;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;

/**
 * JdbcMethodMapper is a class defines the getters and setters for all the JDBC specific data classes
 *
 * @author david berry
 */
public class JdbcMethodMapper implements java.io.Serializable, java.lang.Cloneable {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  // JDK 1.4.2 Values
  private static final JdbcMethodMapEntry<?>[] JDBC_METHOD_MAP_ENTRies = {
    new JdbcMethodMapEntry<String>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, String.class, String.class, "getString", "setString"), // 12
    new JdbcMethodMapEntry<BigDecimal>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, BigDecimal.class, BigDecimal.class, "getBigDecimal", "setBigDecimal"), // 3
    new JdbcMethodMapEntry<Byte>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, byte.class, byte.class, "getByte", "setByte"), // -6
    new JdbcMethodMapEntry<Byte>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Byte.class, byte.class, "getByte", "setByte"), // -6
    new JdbcMethodMapEntry<Short>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, short.class, short.class, "getShort", "setShort"), // 5
    new JdbcMethodMapEntry<Short>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Short.class, short.class, "getShort", "setShort"), // 5
    new JdbcMethodMapEntry<Integer>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, int.class, int.class, "getInt", "setInt"), // 4
    new JdbcMethodMapEntry<Integer>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Integer.class, int.class, "getInt", "setInt"), // 4
    new JdbcMethodMapEntry<Long>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, long.class, long.class, "getLong", "setLong"), // -5
    new JdbcMethodMapEntry<Long>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Long.class, long.class, "getLong", "setLong"), // -5
    new JdbcMethodMapEntry<Float>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, float.class, float.class, "getFloat", "setFloat"), // 7
    new JdbcMethodMapEntry<Float>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Float.class, float.class, "getFloat", "setFloat"), // 7
    new JdbcMethodMapEntry<Double>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, double.class, double.class, "getDouble", "setDouble"), // 6
    new JdbcMethodMapEntry<Double>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Double.class, double.class, "getDouble", "setDouble"), // 8
    new JdbcMethodMapEntry<byte[]>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, byte[].class, byte[].class, "getBytes", "setBytes"), // -2
    new JdbcMethodMapEntry<Date>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Date.class, java.sql.Date.class, "getDate", "setDate"), // 91
    new JdbcMethodMapEntry<Time>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Time.class, java.sql.Time.class, "getTime", "setTime"), // 92
    new JdbcMethodMapEntry<Timestamp>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Timestamp.class, java.sql.Timestamp.class, "getTimestamp", "setTimestamp"), // 93
    new JdbcMethodMapEntry<Clob>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Clob.class, java.sql.Clob.class, "getClob", "setClob"), // 2005
    new JdbcMethodMapEntry<Blob>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Blob.class, java.sql.Blob.class, "getBlob", "setBlob"), // 2004
    new JdbcMethodMapEntry<Array>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Array.class, java.sql.Array.class, "getArray", "setArray"), // 2003
    new JdbcMethodMapEntry<Ref>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Ref.class, java.sql.Ref.class, "getRef", "setRef"), // 2006
    new JdbcMethodMapEntry<Object>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Object.class, Object.class, "getObject", "setObject"), // 2001
    new JdbcMethodMapEntry<URL>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, URL.class, URL.class, "getURL", "setURL"), // 70
    new JdbcMethodMapEntry<Boolean>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, boolean.class, boolean.class, "getBoolean", "setBoolean"), // -7
    new JdbcMethodMapEntry<Boolean>(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Boolean.class, boolean.class, "getBoolean", "setBoolean"), // 16
    new JdbcMethodMapEntry<InputStream>(JdbcMethodMapEntry.METHOD_TYPE_STREAM, InputStream.class, InputStream.class, "getBlob", "setBinaryStream"), // 16
    new JdbcMethodMapEntry<Reader>(JdbcMethodMapEntry.METHOD_TYPE_READER, Reader.class, Reader.class, "getClob", "setCharacterStream") // 16
  };
  private static HashMap<Class<?>, JdbcMethodMapEntry<?>> javaSqlMethodMap = null;

  private JdbcMethodMapper() {
  }

  static public JdbcMethodMapEntry<?> getJavaSqlMethod(Class<?> c) throws CpoException {
    return getJavaSqlMethodMap().get(c);
  }

  static private void initMaps() {
    synchronized (JDBC_METHOD_MAP_ENTRies) {
      javaSqlMethodMap = new HashMap<Class<?>, JdbcMethodMapEntry<?>>();
      for (JdbcMethodMapEntry<?> jsm : JDBC_METHOD_MAP_ENTRies) {
        javaSqlMethodMap.put(jsm.getJavaClass(), jsm);
      }
    }
  }

  private static HashMap<Class<?>, JdbcMethodMapEntry<?>> getJavaSqlMethodMap() {
    if (javaSqlMethodMap == null) {
      initMaps();
    }
    return javaSqlMethodMap;
  }
}