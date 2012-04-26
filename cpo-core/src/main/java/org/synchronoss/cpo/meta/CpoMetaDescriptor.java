/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.meta;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreMeta.CpoMetaDataDocument;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.parser.ExpressionParser;

/**
 *
 * @author dberry
 */
public class CpoMetaDescriptor implements CpoMetaAdapter {
  private static final SortedMap<String, CpoMetaAdapter> metaMap = new TreeMap<String, CpoMetaAdapter>();
  private String name=null;
  
  private CpoMetaDescriptor(){}
  
  protected CpoMetaDescriptor(String name) {
    this.name = name;
  }
  
  public static CpoMetaDescriptor newInstance(String name) throws CpoException {
    CpoMetaDescriptor metaDescriptor = new CpoMetaDescriptor(name);
    
    return findInstance(metaDescriptor);
  }
  
  public static CpoMetaDescriptor newInstance(String name, String metaXml) throws CpoException {
    List<String> metaXmls = new ArrayList<String>();
    metaXmls.add(metaXml);
    CpoMetaDescriptor metaDescriptor = new CpoMetaDescriptor(name);
    return createInstance(metaDescriptor,metaXmls);
  }
    
  public static CpoMetaDescriptor newInstance(String name, List<String> metaXmls) throws CpoException {
    CpoMetaDescriptor metaDescriptor = new CpoMetaDescriptor(name);
    return createInstance(metaDescriptor,metaXmls);
  }
    
  public static CpoMetaDescriptor newInstance(String name, String[] metaXmls) throws CpoException {
    CpoMetaDescriptor metaDescriptor = new CpoMetaDescriptor(name);
    return createInstance(metaDescriptor,metaXmls);
  }
    
  public static CpoMetaDescriptor createInstance(CpoMetaDescriptor metaDescriptor, List<String> metaXmls) throws CpoException {
    CpoMetaAdapter metaAdapter = CpoCoreMetaAdapter.newInstance(metaDescriptor, metaXmls);
    
    if (metaAdapter != null) {
      metaMap.put(metaDescriptor.name, metaAdapter);
      return metaDescriptor;
    }
    
    return null;
  }
    
  public static CpoMetaDescriptor createInstance(CpoMetaDescriptor metaDescriptor, String[] metaXmls) throws CpoException {
    CpoMetaAdapter metaAdapter = CpoCoreMetaAdapter.newInstance(metaDescriptor, metaXmls);
    
    if (metaAdapter != null) {
      metaMap.put(metaDescriptor.name, metaAdapter);
      return metaDescriptor;
    }
    
    return null;
  }
    
  public static CpoMetaDescriptor createInstance(CpoMetaDescriptor metaDescriptor, CpoMetaAdapter metaAdapter) throws CpoException {
    if (metaAdapter != null) {
      metaMap.put(metaDescriptor.name, metaAdapter);
      return metaDescriptor;
    }
    return null;
  }
    
  protected static CpoMetaDescriptor findInstance(CpoMetaDescriptor metaDescriptor) {
    if (metaMap.get(metaDescriptor.name)!=null)
      return metaDescriptor;
    return null;
  }

  protected CpoMetaAdapter getCpoMetaAdapter() throws CpoException {
    CpoMetaAdapter metaAdapter = metaMap.get(name);
    if (metaAdapter == null) {
      throw new CpoException("Invalid MetaDescriptor: "+name);
    }
    return metaAdapter;
  }

  @Override
  public <T> CpoClass getMetaClass(T obj) throws CpoException {
    return getCpoMetaAdapter().getMetaClass(obj);
  }

  @Override
  public List<CpoClass> getCpoClasses() throws CpoException {
    return getCpoMetaAdapter().getCpoClasses();
  }

  @Override
  public CpoMetaDataDocument export() throws CpoException {
    return getCpoMetaAdapter().export();
  }

  @Override
  public ExpressionParser getExpressionParser() throws CpoException {
    return getCpoMetaAdapter().getExpressionParser();
  }

  @Override
  public String getJavaTypeName(CpoAttribute attribute) throws CpoException {
    return getCpoMetaAdapter().getJavaTypeName(attribute);
  }

  @Override
  public Class getJavaTypeClass(CpoAttribute attribute) throws CpoException {
    return getCpoMetaAdapter().getJavaTypeClass(attribute);
  }

  @Override
  public List<String> getAllowableDataTypes() throws CpoException {
    return getCpoMetaAdapter().getAllowableDataTypes();
  }
  
}
