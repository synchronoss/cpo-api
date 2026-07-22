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

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;

/**
 * Process-wide cache of {@link CpoMetaDescriptor} instances keyed by descriptor name.
 *
 * <p>The backing map is a {@link ConcurrentHashMap} because descriptors are read during query
 * processing while hot-deploy reloads can replace or remove descriptors concurrently.
 *
 * @author dberry
 */
public class CpoMetaDescriptorCache {

  // ConcurrentHashMap: read during query processing while hot-deploy reloads descriptors
  private static final Map<String, CpoMetaDescriptor> metaDescriptorMap = new ConcurrentHashMap<>();

  /** Constructs an instance; the cache itself is static and shared by all instances. */
  protected CpoMetaDescriptorCache() {}

  /**
   * Looks up a previously cached meta descriptor by key.
   *
   * @param adapterKey the descriptor's cache key; {@code null} always yields a miss
   * @return the cached {@link CpoMetaDescriptor}, or {@code null} if none is registered under that
   *     key
   */
  protected static CpoMetaDescriptor findCpoMetaDescriptor(String adapterKey) {
    CpoMetaDescriptor metaDescriptor = null;
    if (adapterKey != null) {
      metaDescriptor = metaDescriptorMap.get(adapterKey);
    }

    return metaDescriptor;
  }

  /**
   * Registers a meta descriptor under its own {@link CpoMetaDescriptor#getName()}, replacing any
   * descriptor previously registered under that name.
   *
   * @param metaDescriptor the descriptor to cache; if {@code null} or its name is {@code null} the
   *     call is a no-op
   * @return the descriptor previously registered under the same name, or {@code null} if there was
   *     none
   */
  protected static CpoMetaDescriptor addCpoMetaDescriptor(CpoMetaDescriptor metaDescriptor) {
    CpoMetaDescriptor oldMetaDescriptor = null;
    if (metaDescriptor != null && metaDescriptor.getName() != null) {
      oldMetaDescriptor = metaDescriptorMap.put(metaDescriptor.getName(), metaDescriptor);
    }
    return oldMetaDescriptor;
  }

  /**
   * @return A collection of names of all meta descriptors currently loaded
   */
  protected static Collection<String> getCpoMetaDescriptorNames() {
    return metaDescriptorMap.keySet();
  }

  /**
   * Removes a single cached meta descriptor.
   *
   * @param adapterKey the cache key of the descriptor to remove
   */
  protected static void removeCpoMetaDescriptor(String adapterKey) {
    metaDescriptorMap.remove(adapterKey);
  }

  /** Removes all cached meta descriptors. */
  protected static void clearCpoMetaDescriptorCache() {
    metaDescriptorMap.clear();
  }
}
