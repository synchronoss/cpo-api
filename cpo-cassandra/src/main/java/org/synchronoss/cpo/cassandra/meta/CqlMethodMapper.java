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
package org.synchronoss.cpo.cassandra.meta;

import org.synchronoss.cpo.CpoException;

import java.io.InputStream;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.*;

/**
 * JdbcMethodMapper is a class defines the getters and setters for all the JDBC specific data classes
 *
 * @author david berry
 */
public class CqlMethodMapper implements Serializable, Cloneable {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  // JDK 1.4.2 Values
  private static final CqlMethodMapEntry<?>[] JDBC_METHOD_MAP_ENTRies = {
    new CqlMethodMapEntry<Boolean>(CqlMethodMapEntry.METHOD_TYPE_BASIC, boolean.class, boolean.class, "getBoolean", "setBoolean"),
    new CqlMethodMapEntry<Boolean>(CqlMethodMapEntry.METHOD_TYPE_BASIC, Boolean.class, boolean.class, "getBoolean", "setBoolean"),
    new CqlMethodMapEntry<ByteBuffer>(CqlMethodMapEntry.METHOD_TYPE_BASIC, ByteBuffer.class, ByteBuffer.class, "getBytes", "setBytes"),
    new CqlMethodMapEntry<Date>(CqlMethodMapEntry.METHOD_TYPE_BASIC, Date.class, Date.class, "getDate", "setDate"),
    new CqlMethodMapEntry<BigDecimal>(CqlMethodMapEntry.METHOD_TYPE_BASIC, BigDecimal.class, BigDecimal.class, "getDecimal", "setDecimal"),
    new CqlMethodMapEntry<Double>(CqlMethodMapEntry.METHOD_TYPE_BASIC, double.class, double.class, "getDouble", "setDouble"),
    new CqlMethodMapEntry<Double>(CqlMethodMapEntry.METHOD_TYPE_BASIC, Double.class, double.class, "getDouble", "setDouble"),
    new CqlMethodMapEntry<Float>(CqlMethodMapEntry.METHOD_TYPE_BASIC, float.class, float.class, "getFloat", "setFloat"),
    new CqlMethodMapEntry<Float>(CqlMethodMapEntry.METHOD_TYPE_BASIC, Float.class, float.class, "getFloat", "setFloat"),
    new CqlMethodMapEntry<InetAddress>(CqlMethodMapEntry.METHOD_TYPE_BASIC, InetAddress.class, InetAddress.class, "getInet", "setInet"),
    new CqlMethodMapEntry<Integer>(CqlMethodMapEntry.METHOD_TYPE_BASIC, int.class, int.class, "getInt", "setInt"),
    new CqlMethodMapEntry<Integer>(CqlMethodMapEntry.METHOD_TYPE_BASIC, Integer.class, int.class, "getInt", "setInt"),
    new CqlMethodMapEntry<List>(CqlMethodMapEntry.METHOD_TYPE_ONE, List.class, List.class, "getList", "setList"),
    new CqlMethodMapEntry<Long>(CqlMethodMapEntry.METHOD_TYPE_BASIC, long.class, long.class, "getLong", "setLong"),
    new CqlMethodMapEntry<Long>(CqlMethodMapEntry.METHOD_TYPE_BASIC, Long.class, long.class, "getLong", "setLong"),
    new CqlMethodMapEntry<Map>(CqlMethodMapEntry.METHOD_TYPE_TWO, Map.class, Map.class, "getMap", "setMap"),
    new CqlMethodMapEntry<Set>(CqlMethodMapEntry.METHOD_TYPE_TWO, Set.class, Set.class, "getSet", "setSet"),
    new CqlMethodMapEntry<String>(CqlMethodMapEntry.METHOD_TYPE_BASIC, String.class, String.class, "getString", "setString"),
    new CqlMethodMapEntry<UUID>(CqlMethodMapEntry.METHOD_TYPE_BASIC, UUID.class, UUID.class, "getUUID", "setUUID"),
    new CqlMethodMapEntry<BigInteger>(CqlMethodMapEntry.METHOD_TYPE_BASIC, BigInteger.class, BigInteger.class, "getVarint", "setVarint")
  };

  private static HashMap<Class<?>, CqlMethodMapEntry<?>> cqlMethodMap = null;

  private CqlMethodMapper() {
  }

  static public CqlMethodMapEntry<?> getJavaSqlMethod(Class<?> c) throws CpoException {
    return getJavaSqlMethodMap().get(c);
  }

  static private void initCqlMethodMap() {
    synchronized (JDBC_METHOD_MAP_ENTRies) {
      cqlMethodMap = new HashMap<Class<?>, CqlMethodMapEntry<?>>();
      for (CqlMethodMapEntry<?> cmm : JDBC_METHOD_MAP_ENTRies) {
        cqlMethodMap.put(cmm.getJavaClass(), cmm);
      }
    }
  }

  private static HashMap<Class<?>, CqlMethodMapEntry<?>> getJavaSqlMethodMap() {
    if (cqlMethodMap == null) {
      initCqlMethodMap();
    }
    return cqlMethodMap;
  }
}