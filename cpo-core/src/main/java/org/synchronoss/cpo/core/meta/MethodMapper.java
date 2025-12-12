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
 * MethodMapper is a class defines the getters and setters for all the JDBC specific data classes
 *
 * @author david berry
 */
public class MethodMapper<T extends MethodMapEntry<?, ?>> implements Serializable, Cloneable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private Map<Class<?>, T> dataMethodMap = new HashMap<>();

  public T getDataMethodMapEntry(Class<?> c) throws CpoException {
    return dataMethodMap.get(c);
  }

  public void addMethodMapEntry(T dataMethodMapEntry) {
    dataMethodMap.put(dataMethodMapEntry.getJavaClass(), dataMethodMapEntry);
  }
}
