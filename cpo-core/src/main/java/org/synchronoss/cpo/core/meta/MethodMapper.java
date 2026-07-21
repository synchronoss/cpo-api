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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import org.synchronoss.cpo.core.CpoException;

/**
 * MethodMapper maps java classes to the datastore-specific getter and setter methods used to bind
 * them. Each datastore implementation registers its own entries.
 *
 * @param <T> the concrete {@link MethodMapEntry} type registered by the datastore implementation
 * @author david berry
 */
public class MethodMapper<T extends MethodMapEntry<?, ?>> implements Serializable, Cloneable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  /** Registered entries, keyed by the Java class they move values for. */
  private Map<Class<?>, T> dataMethodMap = new HashMap<>();

  /** Creates an empty mapper with no registered entries. */
  public MethodMapper() {}

  /**
   * Gets the method map entry registered for the given Java class.
   *
   * @param c the Java class to look up
   * @return the entry registered for {@code c}, or {@code null} if none is registered
   * @throws CpoException if the entry cannot be retrieved
   */
  public T getDataMethodMapEntry(Class<?> c) throws CpoException {
    return dataMethodMap.get(c);
  }

  /**
   * Registers a method map entry, keyed by its {@link MethodMapEntry#getJavaClass()}.
   *
   * @param dataMethodMapEntry the entry to register
   */
  public void addMethodMapEntry(T dataMethodMapEntry) {
    dataMethodMap.put(dataMethodMapEntry.getJavaClass(), dataMethodMapEntry);
  }
}
