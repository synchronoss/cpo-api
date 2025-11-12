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
package org.synchronoss.cpo.cassandra.meta;

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.MethodMapper;

import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * MethodMapper is a class defines the getters and setters for all the JDBC specific data classes
 *
 * @author david berry
 */
public class CassandraMethodMapper implements Serializable, Cloneable {
  private static final Logger logger = LoggerFactory.getLogger(CassandraMethodMapper.class);

  /**
   * Version Id for this class.
   */
  @Serial
  private static final long serialVersionUID = 1L;
  private static final Class<BoundStatement> bsc = BoundStatement.class;
  private static final Class<Row> rsc = Row.class;
  private static MethodMapper<CassandraMethodMapEntry<?,?>> methodMapper = initMethodMapper();


  private CassandraMethodMapper() {
  }

    /**
     * Looks up the datasource method for the Clazz
     * @param clazz The clazz to lookup
     * @return The CassandraMethodMapEntry
     * @throws CpoException An error occurred
     */
  static public CassandraMethodMapEntry<?,?> getDatasourceMethod(Class<?> clazz) throws CpoException {
    return (CassandraMethodMapEntry)methodMapper.getDataMethodMapEntry(clazz);
  }

  static private MethodMapper<CassandraMethodMapEntry<?,?>> initMethodMapper() throws IllegalArgumentException {
    MethodMapper<CassandraMethodMapEntry<?,?>> mapper = new MethodMapper<>();
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, boolean.class, boolean.class, "getBool", "setBool"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, Boolean.class, boolean.class, "getBool", "setBool"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, byte.class, byte.class, "getByte", "setByte"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, Byte.class, byte.class, "getByte", "setByte"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, ByteBuffer.class, ByteBuffer.class, "getBytes", "setBytes"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, ByteBuffer.class, ByteBuffer.class, "getBytesUnsafe", "setBytesUnsafe"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, com.datastax.driver.core.LocalDate.class, com.datastax.driver.core.LocalDate.class, "getDate", "setDate"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, BigDecimal.class, BigDecimal.class, "getDecimal", "setDecimal"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, double.class, double.class, "getDouble", "setDouble"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, Double.class, double.class, "getDouble", "setDouble"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, float.class, float.class, "getFloat", "setFloat"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, Float.class, float.class, "getFloat", "setFloat"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, InetAddress.class, InetAddress.class, "getInet", "setInet"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, int.class, int.class, "getInt", "setInt"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, Integer.class, int.class, "getInt", "setInt"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_ONE, List.class, List.class, "getList", "setList"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, long.class, long.class, "getLong", "setLong"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, Long.class, long.class, "getLong", "setLong"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_TWO, Map.class, Map.class, "getMap", "setMap"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_ONE, Set.class, Set.class, "getSet", "setSet"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, short.class, short.class, "getShort", "setShort"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, Short.class, short.class, "getShort", "setShort"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, String.class, String.class, "getString", "setString"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, java.util.Date.class, java.util.Date.class, "getTimestamp", "setTimestamp"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, com.datastax.driver.core.TupleValue.class, com.datastax.driver.core.TupleValue.class, "getTupleValue", "setTupleValue"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, com.datastax.driver.core.UDTValue.class, com.datastax.driver.core.UDTValue.class, "getUDTValue", "setUDTValue"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, UUID.class, UUID.class, "getUUID", "setUUID"));
    mapper.addMethodMapEntry(makeCassandraMethodMapEntry(CassandraMethodMapEntry.METHOD_TYPE_BASIC, BigInteger.class, BigInteger.class, "getVarint", "setVarint"));

    return mapper;
  }

    /**
     * Gets the MethodMapper
     * @return A MethodMapper
     */
  public static MethodMapper getMethodMapper() {
    return methodMapper;
  }

  private static <T> CassandraMethodMapEntry makeCassandraMethodMapEntry(int methodType, Class<T> javaClass, Class<T> datasourceMethodClass, String getterName, String setterName) throws IllegalArgumentException {
    Method rsGetter=loadGetter(methodType, rsc, getterName);
    Method bsSetter=loadSetter(methodType, bsc, datasourceMethodClass, setterName);

    return new CassandraMethodMapEntry(methodType, javaClass, datasourceMethodClass, rsGetter, bsSetter);
  }

  private static <M,D> Method loadSetter(int methodType, Class<M> methodClass, Class<D> datasourceClass, String setterName) throws IllegalArgumentException {
    Method setter;
    try {
      setter = methodClass.getMethod(setterName, new Class[]{int.class, datasourceClass});
    } catch (NoSuchMethodException nsme) {
      logger.error("Error loading Setter" + setterName, nsme);
      throw new IllegalArgumentException(nsme);
    }
    return setter;
  }

    /**
     * Loads the GetterMethod for the Datasource class
     * @param methodType The type of method to load
     * @param methodClass The Clazz we are getting the method from
     * @param getterName the getter name
     * @param <M> The type of the CLazz
     * @return The Method
     * @throws IllegalArgumentException An invalid Method Type, or invalid getter name was provided
     */
  public static <M> Method loadGetter(int methodType, Class<M> methodClass, String getterName) throws IllegalArgumentException {
    Method getter=null;
    try {
      switch (methodType) {
        case CassandraMethodMapEntry.METHOD_TYPE_BASIC:
          getter = methodClass.getMethod(getterName, new Class[]{int.class});
          break;
        case CassandraMethodMapEntry.METHOD_TYPE_ONE:
          getter = methodClass.getMethod(getterName, new Class[]{int.class, Class.class});
          break;
        case CassandraMethodMapEntry.METHOD_TYPE_TWO:
          getter = methodClass.getMethod(getterName, new Class[]{int.class, Class.class, Class.class});
          break;
        default:
          throw new IllegalArgumentException("Illegal Method Type:"+methodType);
      }
    } catch (NoSuchMethodException nsme) {
      logger.error("Error loading Getter" + getterName, nsme);
      throw new IllegalArgumentException(nsme);
    }
    return getter;
  }
}