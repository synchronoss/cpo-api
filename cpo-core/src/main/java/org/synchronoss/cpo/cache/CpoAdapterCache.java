package org.synchronoss.cpo.cache;

import java.util.SortedMap;
import java.util.TreeMap;
import org.synchronoss.cpo.CpoAdapter;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author dberry
 */
public class CpoAdapterCache {
  
  private static final SortedMap<String, CpoAdapter> adapterMap = new TreeMap<String, CpoAdapter>();
  
  protected static CpoAdapter findCpoAdapter(String adapterKey){
    CpoAdapter adapter=null;
    
    if (adapterKey!=null)
      adapter = adapterMap.get(adapterKey);
    
    return adapter;
  }
  
  protected static CpoAdapter addCpoAdapter(String adapterKey, CpoAdapter adapter){
    CpoAdapter oldAdapter=null;
    
    if (adapterKey!=null && adapter != null)
      oldAdapter = adapterMap.put(adapterKey, adapter);
    
    return oldAdapter;
  }
  
}
