package org.synchronoss.cpo.core.cache;

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.synchronoss.cpo.core.CpoAdapterFactory;

/**
 * Process-wide cache of {@link CpoAdapterFactory} instances keyed by config name.
 *
 * <p>The backing map is a {@link ConcurrentHashMap} because factories are read from the hot lookup
 * path while {@code CpoAdapterFactoryManager} repopulates the cache concurrently (e.g. during
 * hot-deploy).
 *
 * @author dberry
 */
public class CpoAdapterFactoryCache {

  // ConcurrentHashMap: read on the hot lookup path while loadAdapters() repopulates concurrently
  private static final Map<String, CpoAdapterFactory> adapterMap = new ConcurrentHashMap<>();

  /** Constructs an instance; the cache itself is static and shared by all instances. */
  protected CpoAdapterFactoryCache() {}

  /**
   * Looks up a previously cached adapter factory by key.
   *
   * @param adapterKey the factory's cache key; {@code null} always yields a miss
   * @return the cached {@link CpoAdapterFactory}, or {@code null} if none is registered under that
   *     key
   */
  protected static CpoAdapterFactory findCpoAdapterFactory(String adapterKey) {
    CpoAdapterFactory adapter = null;

    if (adapterKey != null) {
      adapter = adapterMap.get(adapterKey);
    }

    return adapter;
  }

  /**
   * Registers an adapter factory under the given key, replacing any factory previously registered
   * there.
   *
   * @param adapterKey the cache key to register the factory under; if {@code null} the call is a
   *     no-op
   * @param adapter the factory to cache; if {@code null} the call is a no-op
   * @return the factory previously registered under {@code adapterKey}, or {@code null} if there
   *     was none
   */
  protected static CpoAdapterFactory addCpoAdapterFactory(
      String adapterKey, CpoAdapterFactory adapter) {
    CpoAdapterFactory oldAdapter = null;

    if (adapterKey != null && adapter != null) {
      oldAdapter = adapterMap.put(adapterKey, adapter);
    }

    return oldAdapter;
  }

  /** Removes all cached adapter factories. */
  protected static void clearCpoAdapterFactoryCache() {
    adapterMap.clear();
  }
}
