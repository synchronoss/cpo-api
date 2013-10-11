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
    new CqlMethodMapEntry<Boolean>(boolean.class, boolean.class, "getBoolean", "setBoolean"),
    new CqlMethodMapEntry<Boolean>(Boolean.class, boolean.class, "getBoolean", "setBoolean"),
    new CqlMethodMapEntry<ByteBuffer>(ByteBuffer.class, ByteBuffer.class, "getBytes", "setBytes"),
    new CqlMethodMapEntry<Date>(Date.class, Date.class, "getDate", "setDate"),
    new CqlMethodMapEntry<BigDecimal>(BigDecimal.class, BigDecimal.class, "getDecimal", "setDecimal"),
    new CqlMethodMapEntry<Double>(double.class, double.class, "getDouble", "setDouble"),
    new CqlMethodMapEntry<Double>(Double.class, double.class, "getDouble", "setDouble"),
    new CqlMethodMapEntry<Float>(float.class, float.class, "getFloat", "setFloat"),
    new CqlMethodMapEntry<Float>(Float.class, float.class, "getFloat", "setFloat"),
    new CqlMethodMapEntry<InetAddress>(InetAddress.class, InetAddress.class, "getInet", "setInet"),
    new CqlMethodMapEntry<Integer>(int.class, int.class, "getInt", "setInt"),
    new CqlMethodMapEntry<Integer>(Integer.class, int.class, "getInt", "setInt"),
    new CqlMethodMapEntry<List>(List.class, List.class, "getList", "setList"),
    new CqlMethodMapEntry<Long>(long.class, long.class, "getLong", "setLong"),
    new CqlMethodMapEntry<Long>(Long.class, long.class, "getLong", "setLong"),
    new CqlMethodMapEntry<Map>(Map.class, Map.class, "getMap", "setMap"),
    new CqlMethodMapEntry<Set>(Set.class, Set.class, "getSet", "setSet"),
    new CqlMethodMapEntry<String>(String.class, String.class, "getString", "setString"),
    new CqlMethodMapEntry<UUID>(UUID.class, UUID.class, "getUUID", "setUUID"),
    new CqlMethodMapEntry<BigInteger>(BigInteger.class, BigInteger.class, "getVarint", "setVarint")
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