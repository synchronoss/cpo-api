/*
 * Copyright (C) 2003-2025 David E. Berry
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
package org.synchronoss.cpo.cache;

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
