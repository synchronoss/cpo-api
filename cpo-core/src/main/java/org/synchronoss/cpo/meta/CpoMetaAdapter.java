package org.synchronoss.cpo.meta;

/*-
 * [[
 * core
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

import java.util.List;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.parser.ExpressionParser;

/**
 * @author dberry
 */
public interface CpoMetaAdapter {

  /**
   * Returns the metadata for the class that is contained within the metadata source
   *
   * @param <T> The bean Type
   * @param bean The java bean whose metaclass is needed
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> CpoClass getMetaClass(T bean) throws CpoException;

  /**
   * Returns a list of the cpo class objects that the meta adapter is aware of.
   *
   * @return java.util.List of CpoClass
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  List<CpoClass> getCpoClasses() throws CpoException;

  ExpressionParser getExpressionParser() throws CpoException;

  String getDataTypeName(CpoAttribute attribute) throws CpoException;

  Class<?> getDataTypeJavaClass(CpoAttribute attribute) throws CpoException;

  int getDataTypeInt(String dataTypeName) throws CpoException;

  DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) throws CpoException;

  List<String> getAllowableDataTypes() throws CpoException;
}
