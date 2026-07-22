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

import java.lang.reflect.InvocationTargetException;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;

/**
 * Base {@link org.synchronoss.cpo.core.CpoData} implementation for reading a bound attribute's
 * value out of a datastore result row (e.g. a JDBC {@code ResultSet} or Cassandra {@code Row}).
 * Resolves the appropriate {@link MethodMapEntry} for the attribute's data type on first use and
 * caches it, since it cannot change across invocations of the same bound instance; datastore
 * implementations supply the actual reflective invocation via {@link #invokeGetterImpl}.
 *
 * @author dberry
 */
public abstract class ResultSetCpoData extends AbstractBindableCpoData {

  private Object rs = null;
  MethodMapper<?> methodMapper;
  // resolved on first use; the attribute's data type never changes, so with one instance
  // per column the mapper lookup happens once per query instead of once per cell
  private MethodMapEntry<?, ?> resolvedMethodMapEntry = null;

  /**
   * Creates an instance bound to a single column/attribute of the given result row.
   *
   * @param methodMapper the mapper used to resolve the getter method for the attribute's data type
   * @param rs the datastore-specific result row object (e.g. {@code ResultSet} or {@code Row})
   * @param cpoAttribute the attribute this instance reads
   * @param index the positional index (e.g. column index) of this binding
   */
  public ResultSetCpoData(
      MethodMapper<?> methodMapper, Object rs, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.methodMapper = methodMapper;
    this.rs = rs;
  }

  /**
   * Gets the datastore-specific result row object this instance reads from.
   *
   * @return the result row object (e.g. {@code ResultSet} or {@code Row})
   */
  public Object getRs() {
    return rs;
  }

  /**
   * Invokes the datastore-specific getter method to read the raw value for the given attribute.
   *
   * @param cpoAttribute the attribute being read
   * @param methodMapEntry the resolved method map entry for the attribute's data type
   * @return the raw value read from the result row, before {@code transformIn} is applied
   * @throws IllegalAccessException if the getter method cannot be accessed
   * @throws InvocationTargetException if the getter method throws
   */
  protected abstract Object invokeGetterImpl(
      CpoAttribute cpoAttribute, MethodMapEntry<?, ?> methodMapEntry)
      throws IllegalAccessException, InvocationTargetException;

  /**
   * {@inheritDoc}
   *
   * <p>Resolves (and caches) the {@link MethodMapEntry} for the attribute's data getter return
   * type, invokes the datastore-specific getter via {@link #invokeGetterImpl}, and applies the
   * attribute's transform.
   */
  @Override
  public Object invokeGetter() throws CpoException {
    Object javaObject;
    MethodMapEntry<?, ?> methodMapEntry = resolvedMethodMapEntry;
    if (methodMapEntry == null) {
      methodMapEntry = methodMapper.getDataMethodMapEntry(getDataGetterReturnType());
      if (methodMapEntry == null) {
        if (Object.class.isAssignableFrom(getDataGetterReturnType())) {
          methodMapEntry = methodMapper.getDataMethodMapEntry(Object.class);
        }
        if (methodMapEntry == null) {
          throw new CpoException(
              "Error Retrieving Jdbc Method for type: " + getDataGetterReturnType().getName());
        }
      }
      resolvedMethodMapEntry = methodMapEntry;
    }

    try {
      // Get the getter for the Callable Statement
      javaObject = transformIn(invokeGetterImpl(getCpoAttribute(), methodMapEntry));
    } catch (IllegalAccessException iae) {
      throw new CpoException("Error Invoking ResultSet Method: ", iae);
    } catch (InvocationTargetException ite) {
      throw new CpoException("Error Invoking ResultSet Method: ", ite);
    }

    return javaObject;
  }
}
