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
package org.synchronoss.cpo.jdbc.meta;

import org.synchronoss.cpo.meta.MethodMapEntry;
import org.synchronoss.cpo.meta.MethodMapper;
import org.synchronoss.cpo.meta.ResultSetCpoData;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

import java.lang.reflect.InvocationTargetException;

public class JdbcResultSetCpoData extends ResultSetCpoData {

  public JdbcResultSetCpoData(MethodMapper<?> methodMapper, Object rs, CpoAttribute cpoAttribute, int index) {
    super(methodMapper, rs, cpoAttribute, index);
  }

  protected Object invokeGetterImpl(CpoAttribute cpoAttribute, MethodMapEntry<?, ?> methodMapEntry) throws IllegalAccessException, InvocationTargetException {
    Object javaObject=null;
    switch (methodMapEntry.getMethodType()) {
      case JdbcMethodMapEntry.METHOD_TYPE_BASIC:
      case JdbcMethodMapEntry.METHOD_TYPE_STREAM:
      case JdbcMethodMapEntry.METHOD_TYPE_READER:
        javaObject = methodMapEntry.getRsGetter().invoke(getRs(), getIndex());
        break;
      case JdbcMethodMapEntry.METHOD_TYPE_OBJECT:
        javaObject = methodMapEntry.getRsGetter().invoke(getRs(), getIndex(), methodMapEntry.getJavaClass());
        break;
    }
    return javaObject;
  }

}
