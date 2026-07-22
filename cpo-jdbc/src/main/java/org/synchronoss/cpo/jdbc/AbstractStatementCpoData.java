package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
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

import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.AbstractBindableCpoData;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapEntry;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;

/**
 * Base for {@link org.synchronoss.cpo.core.CpoData} implementations backed by a JDBC {@code
 * Statement} subtype, resolving the {@link JdbcMethodMapEntry} used to bind/read a bound
 * attribute's value.
 */
public abstract class AbstractStatementCpoData extends AbstractBindableCpoData {

  /**
   * Creates an instance for the given bound attribute and bind-marker index.
   *
   * @param cpoAttribute the attribute this instance moves values for
   * @param index the bind-marker index within the statement
   */
  public AbstractStatementCpoData(CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
  }

  /**
   * Resolves the {@link JdbcMethodMapEntry} used to move this attribute's value to/from a JDBC
   * statement.
   *
   * @param instanceObject unused; retained for the overriding subclasses' call sites
   * @return the resolved method map entry
   * @throws CpoException if no entry can be resolved for the attribute's setter parameter type
   */
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
