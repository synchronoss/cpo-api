package org.synchronoss.cpo.core.meta;

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
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.synchronoss.cpo.core.parser.ExpressionParser;

/**
 * {@code CpoMetaAdapter} is the datastore-agnostic view over a loaded CPO metadata source: it
 * resolves the {@link CpoClass} for a given bean, exposes the classes it knows about, and bridges
 * between the datastore's native data type names/ids and their Java equivalents via the
 * implementation's {@link DataTypeMapper}.
 *
 * @author dberry
 */
public interface CpoMetaAdapter {

  /**
   * Returns the metadata for the class that is contained within the metadata source
   *
   * @param <T> The bean Type
   * @param bean The java bean whose metaclass is needed
   * @return the {@link CpoClass} metadata for {@code bean}'s class
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

  /**
   * Gets the parser used to extract bind-marker/column information from this metadata source's
   * native function expressions.
   *
   * @return the expression parser for this meta adapter
   * @throws CpoException if the parser cannot be created
   */
  ExpressionParser getExpressionParser() throws CpoException;

  /**
   * Gets the Java class name that the given attribute's native data type maps to.
   *
   * @param attribute the attribute whose data type is being resolved
   * @return the fully-qualified Java class name (array types are reported as {@code byte[]} or
   *     {@code char[]})
   * @throws CpoException if the data type cannot be resolved
   */
  String getDataTypeName(CpoAttribute attribute) throws CpoException;

  /**
   * Gets the Java class that the given attribute's native data type maps to.
   *
   * @param attribute the attribute whose data type is being resolved
   * @return the Java class the attribute's value is represented as
   * @throws CpoException if the data type cannot be resolved
   */
  Class<?> getDataTypeJavaClass(CpoAttribute attribute) throws CpoException;

  /**
   * Gets the integer identifier for a named native data type.
   *
   * @param dataTypeName the native data type name
   * @return the integer identifier registered for {@code dataTypeName}
   * @throws CpoException if the data type name is not recognized
   */
  int getDataTypeInt(String dataTypeName) throws CpoException;

  /**
   * Gets the full data type mapping entry for a given native data type identifier.
   *
   * @param dataTypeInt the integer identifier of the native data type
   * @return the {@link DataTypeMapEntry} registered for {@code dataTypeInt}
   * @throws CpoException if the data type identifier is not recognized
   */
  DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) throws CpoException;

  /**
   * Gets the names of all native data types this meta adapter knows how to map.
   *
   * @return the list of allowable native data type names
   * @throws CpoException if the data types cannot be retrieved
   */
  List<String> getAllowableDataTypes() throws CpoException;
}
