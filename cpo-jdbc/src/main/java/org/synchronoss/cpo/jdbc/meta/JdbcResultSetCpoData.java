package org.synchronoss.cpo.jdbc.meta;

/*-
 * [[
 * jdbc
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
import org.synchronoss.cpo.core.meta.MethodMapEntry;
import org.synchronoss.cpo.core.meta.MethodMapper;
import org.synchronoss.cpo.core.meta.ResultSetCpoData;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;

/** Result set data getter helper */
public class JdbcResultSetCpoData extends ResultSetCpoData {

  /**
   * Construct a JdbcResultSetCpoData
   *
   * @param methodMapper The method mapper
   * @param rs - the result set
   * @param cpoAttribute - The CpoAttribute to get
   * @param index - The index of the CpoAttribute in the result set
   */
  public JdbcResultSetCpoData(
      MethodMapper<?> methodMapper, Object rs, CpoAttribute cpoAttribute, int index) {
    super(methodMapper, rs, cpoAttribute, index);
  }

  protected Object invokeGetterImpl(CpoAttribute cpoAttribute, MethodMapEntry<?, ?> methodMapEntry)
      throws IllegalAccessException, InvocationTargetException {
    Object javaObject = null;
    switch (methodMapEntry.getMethodType()) {
      case JdbcMethodMapEntry.METHOD_TYPE_BASIC:
      case JdbcMethodMapEntry.METHOD_TYPE_STREAM:
      case JdbcMethodMapEntry.METHOD_TYPE_READER:
        javaObject = methodMapEntry.getRsGetter().invoke(getRs(), getIndex());
        break;
      case JdbcMethodMapEntry.METHOD_TYPE_OBJECT:
        javaObject =
            methodMapEntry.getRsGetter().invoke(getRs(), getIndex(), methodMapEntry.getJavaClass());
        break;
    }
    return javaObject;
  }
}
