package org.synchronoss.cpo.cache;

/*-
 * #%L
 * core
 * %%
 * Copyright (C) 2003 - 2025 David E. Berry
 * %%
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
 * #L%
 */

import java.util.HashMap;
import java.util.Map;
import org.synchronoss.cpo.CpoAdapterFactory;

/**
 * @author dberry
 */
public class CpoAdapterFactoryCache {

  private static final Map<String, CpoAdapterFactory> adapterMap = new HashMap<>();

  protected static CpoAdapterFactory findCpoAdapterFactory(String adapterKey) {
    CpoAdapterFactory adapter = null;

    if (adapterKey != null) {
      adapter = adapterMap.get(adapterKey);
    }

    return adapter;
  }

  protected static CpoAdapterFactory addCpoAdapterFactory(
      String adapterKey, CpoAdapterFactory adapter) {
    CpoAdapterFactory oldAdapter = null;

    if (adapterKey != null && adapter != null) {
      oldAdapter = adapterMap.put(adapterKey, adapter);
    }

    return oldAdapter;
  }

  protected static void clearCpoAdapterFactoryCache() {
    adapterMap.clear();
  }
}
