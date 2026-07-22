package org.synchronoss.cpo.core.meta;

/*-
 * [[
 * core
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
import org.synchronoss.cpo.core.parser.BoundExpressionParser;
import org.synchronoss.cpo.core.parser.ExpressionParser;

/** A minimal concrete meta adapter for core meta tests. */
public class CoreTestMetaAdapter extends AbstractCpoMetaAdapter {
  private static final DataTypeMapEntry<String> defaultDataTypeMapEntry =
      new DataTypeMapEntry<>(0, "VARCHAR", String.class);
  private static final DataTypeMapper dataTypeMapper = initDataTypeMapper();

  public CoreTestMetaAdapter() {}

  @Override
  protected DataTypeMapper getDataTypeMapper() {
    return dataTypeMapper;
  }

  @Override
  public ExpressionParser getExpressionParser() throws CpoException {
    return new BoundExpressionParser();
  }

  private static DataTypeMapper initDataTypeMapper() {
    DataTypeMapper mapper = new DataTypeMapper(defaultDataTypeMapEntry);
    mapper.addDataTypeEntry(new DataTypeMapEntry<>(1, "INT", int.class));
    return mapper;
  }
}
