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
public class CpoMetaDescriptorCache {
  private static final SortedMap<String, CpoMetaDescriptor> metaDescriptorMap = new TreeMap<String, CpoMetaDescriptor>();
  
  protected static CpoMetaDescriptor findCpoMetaDescriptor(String adapterKey){
    CpoMetaDescriptor metaDescriptor = null;
    if (adapterKey!=null) {
      metaDescriptor = metaDescriptorMap.get(adapterKey);
    }
    
    return metaDescriptor;
  }
  
  protected static CpoMetaDescriptor addCpoMetaDescriptor(CpoMetaDescriptor metaDescriptor){
    CpoMetaDescriptor oldMetaDescriptor = null;
    if (metaDescriptor != null && metaDescriptor.getName() != null) {
      oldMetaDescriptor = metaDescriptorMap.put(metaDescriptor.getName(), metaDescriptor);
    }
    return oldMetaDescriptor;
  }
  
}
