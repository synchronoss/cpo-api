/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.cache;

import java.util.SortedMap;
import java.util.TreeMap;
import org.synchronoss.cpo.meta.CpoMetaAdapter;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;

/**
 *
 * @author dberry
 */
public class CpoMetaAdapterCache {
  private static final SortedMap<String, CpoMetaAdapter> metaAdapterMap = new TreeMap<String, CpoMetaAdapter>();
  
  protected static CpoMetaAdapter findCpoMetaAdapter(String adapterKey){
    CpoMetaAdapter metaAdapter = null;
    if (adapterKey!=null) {
      metaAdapter = metaAdapterMap.get(adapterKey);
    }
    
    return metaAdapter;
  }
  
  protected static CpoMetaAdapter addCpoMetaAdapter(String adapterKey, CpoMetaAdapter metaAdapter){
    CpoMetaAdapter oldMetaAdapter = null;
    if (adapterKey!=null && metaAdapter != null) {
      oldMetaAdapter = metaAdapterMap.put(adapterKey, metaAdapter);
    }
    return oldMetaAdapter;
  }
  
}
