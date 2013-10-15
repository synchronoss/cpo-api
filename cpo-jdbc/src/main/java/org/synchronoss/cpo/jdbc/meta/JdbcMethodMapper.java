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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.MethodMapEntry;
import org.synchronoss.cpo.meta.MethodMapper;

import java.io.*;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;

/**
 * MethodMapper is a class defines the getters and setters for all the JDBC specific data classes
 *
 * @author david berry
 */
public class JdbcMethodMapper implements java.io.Serializable, java.lang.Cloneable {
  private static final Logger logger = LoggerFactory.getLogger(JdbcMethodMapper.class);

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  private static final Class<PreparedStatement> psc = PreparedStatement.class;
  private static final Class<ResultSet> rsc = ResultSet.class;
  private static final Class<CallableStatement> csc = CallableStatement.class;
  private static MethodMapper<JdbcMethodMapEntry<?,?>> methodMapper = initMethodMapper();


  private JdbcMethodMapper() {
  }

  static public JdbcMethodMapEntry<?,?> getJavaSqlMethod(Class<?> c) throws CpoException {
    return (JdbcMethodMapEntry)methodMapper.getDataMethodMapEntry(c);
  }

  static private MethodMapper<JdbcMethodMapEntry<?,?>> initMethodMapper() throws IllegalArgumentException {
    MethodMapper mapper = new MethodMapper();
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, String.class, String.class, "getString", "setString"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, BigDecimal.class, BigDecimal.class, "getBigDecimal", "setBigDecimal"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, byte.class, byte.class, "getByte", "setByte"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Byte.class, byte.class, "getByte", "setByte"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, short.class, short.class, "getShort", "setShort"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Short.class, short.class, "getShort", "setShort"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, int.class, int.class, "getInt", "setInt"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Integer.class, int.class, "getInt", "setInt"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, long.class, long.class, "getLong", "setLong"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Long.class, long.class, "getLong", "setLong"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, float.class, float.class, "getFloat", "setFloat"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Float.class, float.class, "getFloat", "setFloat"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, double.class, double.class, "getDouble", "setDouble"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Double.class, double.class, "getDouble", "setDouble"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, byte[].class, byte[].class, "getBytes", "setBytes"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Date.class, java.sql.Date.class, "getDate", "setDate"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Time.class, java.sql.Time.class, "getTime", "setTime"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Timestamp.class, java.sql.Timestamp.class, "getTimestamp", "setTimestamp"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Clob.class, java.sql.Clob.class, "getClob", "setClob"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Blob.class, java.sql.Blob.class, "getBlob", "setBlob"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Array.class, java.sql.Array.class, "getArray", "setArray"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, java.sql.Ref.class, java.sql.Ref.class, "getRef", "setRef"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Object.class, Object.class, "getObject", "setObject"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, URL.class, URL.class, "getURL", "setURL"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, boolean.class, boolean.class, "getBoolean", "setBoolean"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_BASIC, Boolean.class, boolean.class, "getBoolean", "setBoolean"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_STREAM, InputStream.class, InputStream.class, "getBlob", "setBinaryStream"));
    mapper.addMethodMapEntry(makeJdbcMethodMapEntry(JdbcMethodMapEntry.METHOD_TYPE_READER, Reader.class, Reader.class, "getClob", "setCharacterStream"));

    return mapper;
  }

  public static MethodMapper getMethodMapper() {
    return methodMapper;
  }

  private static <T> JdbcMethodMapEntry<?,?> makeJdbcMethodMapEntry(int methodType, Class<T> javaClass, Class<T> datasourceMethodClass, String getterName, String setterName) throws IllegalArgumentException {
    Method rsGetter=loadGetter(methodType, rsc, getterName);
    Method bsSetter=loadSetter(methodType, psc, datasourceMethodClass, setterName);
    Method csGetter=loadGetter(methodType, csc, getterName);
    Method csSetter=loadSetter(methodType, csc, datasourceMethodClass, setterName);

    return new JdbcMethodMapEntry(methodType, javaClass, datasourceMethodClass, rsGetter, bsSetter, csGetter, csSetter);
  }

  private static <M,D> Method loadSetter(int methodType, Class<M> methodClass, Class<D> datasourceClass, String setterName) throws IllegalArgumentException {
    Method setter;
    try {
      if (methodType == JdbcMethodMapEntry.METHOD_TYPE_BASIC) {
        setter = methodClass.getMethod(setterName, new Class[]{int.class, datasourceClass});
      } else {
        setter = methodClass.getMethod(setterName, new Class[]{int.class, datasourceClass, int.class});
      }

    } catch (NoSuchMethodException nsme) {
      logger.error("Error loading Setter" + setterName, nsme);
      throw new IllegalArgumentException(nsme);
    }
    return setter;
  }

  private static <M> Method loadGetter(int methodType, Class<M> methodClass, String getterName) throws IllegalArgumentException {
    Method getter;
    try {
      getter = methodClass.getMethod(getterName, new Class[]{int.class});
    } catch (NoSuchMethodException nsme) {
      logger.error("Error loading Getter" + getterName, nsme);
      throw new IllegalArgumentException(nsme);
    }
    return getter;
  }

}