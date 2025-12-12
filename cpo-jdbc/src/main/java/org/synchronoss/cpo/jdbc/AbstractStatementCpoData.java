package org.synchronoss.cpo.jdbc;

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

import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.AbstractBindableCpoData;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapEntry;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;

public abstract class AbstractStatementCpoData extends AbstractBindableCpoData {

  public AbstractStatementCpoData(CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
  }

  protected JdbcMethodMapEntry<?, ?> getJdbcMethodMapEntry(Object instanceObject)
      throws CpoException {
    JdbcMethodMapEntry<?, ?> methodMapEntry =
        JdbcMethodMapper.getJavaSqlMethod(getDataSetterParamType());
    if (methodMapEntry == null) {
      if (Object.class.isAssignableFrom(getDataSetterParamType())) {
        methodMapEntry = JdbcMethodMapper.getJavaSqlMethod(Object.class);
      }
      if (methodMapEntry == null) {
        throw new CpoException(
            "Error Retrieving Jdbc Method for type: " + getDataSetterParamType().getName());
      }
    }
    switch (methodMapEntry.getMethodType()) {
      case JdbcMethodMapEntry.METHOD_TYPE_BASIC:
      case JdbcMethodMapEntry.METHOD_TYPE_OBJECT:
      case JdbcMethodMapEntry.METHOD_TYPE_STREAM:
      case JdbcMethodMapEntry.METHOD_TYPE_READER:
      default:
        return methodMapEntry;
    }
  }
}
