package org.synchronoss.cpo.cassandra.meta;

/*-
 * [[
 * cassandra
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.meta.MethodMapEntry;

import java.io.Serial;
import java.lang.reflect.Method;

/**
 * MethodMapEntry is a class defines the getters and setters for JDBC specific data classes
 *
 * @author david berry
 * @param <D> The datasource class
 * @param <J> The java class
 */
public class CassandraMethodMapEntry<J, D> extends MethodMapEntry<J, D>
    implements java.io.Serializable, Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(CassandraMethodMapEntry.class);

  /** Version Id for this class. */
  @Serial private static final long serialVersionUID = 1L;

  /** Method Types that take 1 additional parameter */
  public static final int METHOD_TYPE_ONE = 1;

  /** Method types that take two additional parameters */
  public static final int METHOD_TYPE_TWO = 2;

  /**
   * Contsructs a method map entry
   *
   * @param methodType The method type
   * @param javaClass The java class that we are mapping
   * @param datasourceMethodClass The datasource class that we are mapping
   * @param rsGetter The getter from the resultset
   * @param bsSetter The bound statement setter
   */
  public CassandraMethodMapEntry(
      int methodType,
      Class<J> javaClass,
      Class<D> datasourceMethodClass,
      Method rsGetter,
      Method bsSetter) {
    super(methodType, javaClass, datasourceMethodClass, rsGetter, bsSetter);
  }
}
