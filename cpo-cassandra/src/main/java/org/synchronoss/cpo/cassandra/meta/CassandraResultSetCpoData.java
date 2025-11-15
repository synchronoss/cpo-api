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

import java.lang.reflect.InvocationTargetException;
import org.synchronoss.cpo.meta.MethodMapEntry;
import org.synchronoss.cpo.meta.MethodMapper;
import org.synchronoss.cpo.meta.ResultSetCpoData;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/** Manages data transfer between the cpo objects and the result set */
public class CassandraResultSetCpoData extends ResultSetCpoData {

  /**
   * Constructs the CassandraResultSetCpoData
   *
   * @param methodMapper The MethodMapper
   * @param rs The result set
   * @param cpoAttribute The CpoAttribute
   * @param index The index of the CpoAttribute in the result set
   */
  public CassandraResultSetCpoData(
      MethodMapper<?> methodMapper, Object rs, CpoAttribute cpoAttribute, int index) {
    super(methodMapper, rs, cpoAttribute, index);
  }

  protected Object invokeGetterImpl(CpoAttribute cpoAttribute, MethodMapEntry<?, ?> methodMapEntry)
      throws IllegalAccessException, InvocationTargetException {
    CassandraCpoAttribute cassandraCpoAttribute = (CassandraCpoAttribute) cpoAttribute;

    Object javaObject = null;
    switch (methodMapEntry.getMethodType()) {
      case CassandraMethodMapEntry.METHOD_TYPE_BASIC:
        javaObject = methodMapEntry.getRsGetter().invoke(getRs(), getIndex());
        break;
      case CassandraMethodMapEntry.METHOD_TYPE_ONE:
        javaObject =
            methodMapEntry
                .getRsGetter()
                .invoke(getRs(), getIndex(), cassandraCpoAttribute.getValueTypeClass());
        break;
      case CassandraMethodMapEntry.METHOD_TYPE_TWO:
        javaObject =
            methodMapEntry
                .getRsGetter()
                .invoke(
                    getRs(),
                    getIndex(),
                    cassandraCpoAttribute.getKeyTypeClass(),
                    cassandraCpoAttribute.getValueTypeClass());
        break;
    }
    return javaObject;
  }
}
