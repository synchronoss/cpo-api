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

import com.google.common.reflect.TypeToken;
import org.synchronoss.cpo.meta.MethodMapEntry;
import org.synchronoss.cpo.meta.MethodMapper;
import org.synchronoss.cpo.meta.ResultSetCpoData;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

import java.lang.reflect.InvocationTargetException;

public class CassandraResultSetCpoData extends ResultSetCpoData {

  public CassandraResultSetCpoData(MethodMapper<?> methodMapper, Object rs, CpoAttribute cpoAttribute, int index) {
    super(methodMapper, rs, cpoAttribute, index);
  }

  protected Object invokeGetterImpl(CpoAttribute cpoAttribute, MethodMapEntry<?, ?> methodMapEntry) throws IllegalAccessException, InvocationTargetException {
    CassandraCpoAttribute cassandraCpoAttribute = (CassandraCpoAttribute) cpoAttribute;

    Object javaObject=null;
    switch (methodMapEntry.getMethodType()) {
      case CassandraMethodMapEntry.METHOD_TYPE_BASIC:
        javaObject = methodMapEntry.getRsGetter().invoke(getRs(), getIndex());
        break;
      case CassandraMethodMapEntry.METHOD_TYPE_ONE:
        javaObject = methodMapEntry.getRsGetter().invoke(getRs(), getIndex(), TypeToken.of(cassandraCpoAttribute.getValueTypeClass()));
        break;
      case CassandraMethodMapEntry.METHOD_TYPE_TWO:
        javaObject = methodMapEntry.getRsGetter().invoke(getRs(), getIndex(), TypeToken.of(cassandraCpoAttribute.getKeyTypeClass()), TypeToken.of(cassandraCpoAttribute.getValueTypeClass()));
        break;
    }
    return javaObject;
  }
}
