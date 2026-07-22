package org.synchronoss.cpo.core.cache;

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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.synchronoss.cpo.core.CpoAdapter;

/**
 * Process-wide cache of {@link CpoAdapter} instances keyed by adapter name.
 *
 * <p>{@code CpoBaseAdapter} extends this class so that concrete adapter implementations (JDBC,
 * Cassandra, etc.) share a single lookup/registration point without duplicating cache logic. The
 * backing map is a {@link ConcurrentHashMap} because adapters are looked up and lazily registered
 * concurrently from multiple threads.
 *
 * @author dberry
 */
public class CpoAdapterCache {

  // ConcurrentHashMap: adapters are cached lazily from concurrent getInstance() calls
  private static final Map<String, CpoAdapter> adapterMap = new ConcurrentHashMap<>();

  /** Constructs an instance; the cache itself is static and shared by all instances. */
  protected CpoAdapterCache() {}

  /**
   * Looks up a previously cached adapter by key.
   *
   * @param adapterKey the adapter's cache key; {@code null} always yields a miss
   * @return the cached {@link CpoAdapter}, or {@code null} if none is registered under that key
   */
  protected static CpoAdapter findCpoAdapter(String adapterKey) {
    CpoAdapter adapter = null;

    if (adapterKey != null) {
      adapter = adapterMap.get(adapterKey);
    }

    return adapter;
  }

  /**
   * Registers an adapter under the given key, replacing any adapter previously registered there.
   *
   * @param adapterKey the cache key to register the adapter under; if {@code null} the call is a
   *     no-op
   * @param adapter the adapter to cache; if {@code null} the call is a no-op
   * @return the adapter previously registered under {@code adapterKey}, or {@code null} if there
   *     was none
   */
  protected static CpoAdapter addCpoAdapter(String adapterKey, CpoAdapter adapter) {
    CpoAdapter oldAdapter = null;

    if (adapterKey != null && adapter != null) {
      oldAdapter = adapterMap.put(adapterKey, adapter);
    }

    return oldAdapter;
  }
}
