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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 * @author dberry
 */
public class CpoMetaDescriptorCache {

  private static final Map<String, CpoMetaDescriptor> metaDescriptorMap = new HashMap<>();

  protected static CpoMetaDescriptor findCpoMetaDescriptor(String adapterKey) {
    CpoMetaDescriptor metaDescriptor = null;
    if (adapterKey != null) {
      metaDescriptor = metaDescriptorMap.get(adapterKey);
    }

    return metaDescriptor;
  }

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

  protected static void removeCpoMetaDescriptor(String adapterKey) {
    metaDescriptorMap.remove(adapterKey);
  }

  protected static void clearCpoMetaDescriptorCache() {
    metaDescriptorMap.clear();
  }
}
