package org.synchronoss.cpo.cassandra.meta;

/*-
 * [[
 * cassandra
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

import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.data.TupleValue;
import com.datastax.oss.driver.api.core.data.UdtValue;
import java.io.Serial;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.MethodMapper;

/**
 * CassandraMethodMapper defines, for each Java type CPO binds to/from Cassandra, the {@link Row}
 * getter used to read that type back from a query result. It also serves as the registry of known
 * driver-native Java types: {@code org.synchronoss.cpo.cassandra.CassandraBoundStatementFactory}'s
 * {@code setBindValues} consults it to tell a raw datastore-typed literal (e.g. a dynamic
 * where-clause value) apart from a bean instance whose attribute value still needs to be extracted.
 * There is no corresponding registry of BoundStatement setter methods -- bind values are applied
 * via a single {@code PreparedStatement.bind(Object...)} call rather than per-attribute reflective
 * setter invocation.
 *
 * @author david berry
 */
public class CassandraMethodMapper implements Serializable, Cloneable {
  private static final Logger logger = LoggerFactory.getLogger(CassandraMethodMapper.class);

  /** Version Id for this class. */
  @Serial private static final long serialVersionUID = 1L;

  private static final Class<Row> rsc = Row.class;
  private static MethodMapper<CassandraMethodMapEntry<?, ?>> methodMapper = initMethodMapper();

  private CassandraMethodMapper() {}

  /**
   * Looks up the datasource method for the Clazz
   *
   * @param clazz The clazz to lookup
   * @return The CassandraMethodMapEntry
   * @throws CpoException An error occurred
   */
  public static CassandraMethodMapEntry<?, ?> getDatasourceMethod(Class<?> clazz)
      throws CpoException {
    return methodMapper.getDataMethodMapEntry(clazz);
  }

  private static MethodMapper<CassandraMethodMapEntry<?, ?>> initMethodMapper()
      throws IllegalArgumentException {
    MethodMapper<CassandraMethodMapEntry<?, ?>> mapper = new MethodMapper<>();
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, boolean.class, boolean.class, "getBool"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Boolean.class, boolean.class, "getBool"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, byte.class, byte.class, "getByte"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Byte.class, byte.class, "getByte"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            ByteBuffer.class,
            ByteBuffer.class,
            "getByteBuffer"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            ByteBuffer.class,
            ByteBuffer.class,
            "getBytesUnsafe"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            LocalDate.class,
            LocalDate.class,
            "getLocalDate"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            BigDecimal.class,
            BigDecimal.class,
            "getBigDecimal"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, double.class, double.class, "getDouble"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Double.class, double.class, "getDouble"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, float.class, float.class, "getFloat"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Float.class, float.class, "getFloat"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            InetAddress.class,
            InetAddress.class,
            "getInetAddress"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, int.class, int.class, "getInt"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Integer.class, int.class, "getInt"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_ONE, List.class, List.class, "getList"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, long.class, long.class, "getLong"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Long.class, long.class, "getLong"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_TWO, Map.class, Map.class, "getMap"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_ONE, Set.class, Set.class, "getSet"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, short.class, short.class, "getShort"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Short.class, short.class, "getShort"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, String.class, String.class, "getString"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, Instant.class, Instant.class, "getInstant"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            TupleValue.class,
            TupleValue.class,
            "getTupleValue"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            UdtValue.class,
            UdtValue.class,
            "getUdtValue"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC, UUID.class, UUID.class, "getUuid"));
    mapper.addMethodMapEntry(
        makeCassandraMethodMapEntry(
            CassandraMethodMapEntry.METHOD_TYPE_BASIC,
            BigInteger.class,
            BigInteger.class,
            "getBigInteger"));

    return mapper;
  }

  /**
   * Gets the MethodMapper
   *
   * @return A MethodMapper
   */
  public static MethodMapper<CassandraMethodMapEntry<?, ?>> getMethodMapper() {
    return methodMapper;
  }

  private static <T> CassandraMethodMapEntry<T, T> makeCassandraMethodMapEntry(
      int methodType, Class<T> javaClass, Class<T> datasourceMethodClass, String getterName)
      throws IllegalArgumentException {
    Method rsGetter = loadGetter(methodType, rsc, getterName);

    // no BoundStatement setter Method is resolved: bind values are applied via a single
    // PreparedStatement.bind(Object...) call, not per-attribute reflective setter invocation
    return new CassandraMethodMapEntry<>(
        methodType, javaClass, datasourceMethodClass, rsGetter, null);
  }

  /**
   * Loads the GetterMethod for the Datasource class
   *
   * @param methodType The type of method to load
   * @param methodClass The Clazz we are getting the method from
   * @param getterName the getter name
   * @param <M> The type of the CLazz
   * @return The Method
   * @throws IllegalArgumentException An invalid Method Type, or invalid getter name was provided
   */
  public static <M> Method loadGetter(int methodType, Class<M> methodClass, String getterName)
      throws IllegalArgumentException {
    Method getter = null;
    try {
      switch (methodType) {
        case CassandraMethodMapEntry.METHOD_TYPE_BASIC:
          getter = methodClass.getMethod(getterName, new Class<?>[] {int.class});
          break;
        case CassandraMethodMapEntry.METHOD_TYPE_ONE:
          getter = methodClass.getMethod(getterName, new Class<?>[] {int.class, Class.class});
          break;
        case CassandraMethodMapEntry.METHOD_TYPE_TWO:
          getter =
              methodClass.getMethod(
                  getterName, new Class<?>[] {int.class, Class.class, Class.class});
          break;
        default:
          throw new IllegalArgumentException("Illegal Method Type:" + methodType);
      }
    } catch (NoSuchMethodException nsme) {
      logger.error("Error loading Getter" + getterName, nsme);
      throw new IllegalArgumentException(nsme);
    }
    return getter;
  }
}
