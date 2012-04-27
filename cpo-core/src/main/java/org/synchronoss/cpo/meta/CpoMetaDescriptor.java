/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.meta;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.domain.*;
import org.synchronoss.cpo.parser.ExpressionParser;

import java.io.*;
import java.util.*;
import org.synchronoss.cpo.cache.CpoMetaAdapterCache;

/**
 *
 * @author dberry
 */
public class CpoMetaDescriptor extends CpoMetaAdapterCache implements CpoMetaAdapter {
  private String name=null;
  
  private CpoMetaDescriptor(){}
  
  protected CpoMetaDescriptor(String name) {
    this.name = name;
  }
  
  public static boolean isValidMetaDescriptor(CpoMetaDescriptor metaDescriptor) {
    return (findCpoMetaAdapter(metaDescriptor.getName()) != null);
  }
  
  public static CpoMetaDescriptor getInstance(String name) throws CpoException {
    CpoMetaDescriptor metaDescriptor = null;
    
    if (findCpoMetaAdapter(name) != null)
      metaDescriptor = new CpoMetaDescriptor(name);
    
    return metaDescriptor;
  }
  
  public static CpoMetaDescriptor getInstance(String name, String metaXml) throws CpoException {
    List<String> metaXmls = new ArrayList<String>();
    metaXmls.add(metaXml);
    CpoMetaDescriptor metaDescriptor = new CpoMetaDescriptor(name);
    return createUpdateInstance(metaDescriptor,metaXmls);
  }
    
  public static CpoMetaDescriptor getInstance(String name, List<String> metaXmls) throws CpoException {
    CpoMetaDescriptor metaDescriptor = new CpoMetaDescriptor(name);
    return createUpdateInstance(metaDescriptor,metaXmls);
  }
    
  public static CpoMetaDescriptor getInstance(String name, String[] metaXmls) throws CpoException {
    CpoMetaDescriptor metaDescriptor = new CpoMetaDescriptor(name);
    return createUpdateInstance(metaDescriptor,metaXmls);
  }
    
  protected static CpoMetaDescriptor createUpdateInstance(CpoMetaDescriptor metaDescriptor, List<String> metaXmls) throws CpoException {
    return createUpdateInstance(metaDescriptor, metaXmls.toArray(new String[metaXmls.size()]));
  }
    
  protected static CpoMetaDescriptor createUpdateInstance(CpoMetaDescriptor metaDescriptor, String[] metaXmls) throws CpoException {
    CpoMetaAdapter metaAdapter = findCpoMetaAdapter(metaDescriptor.getName());
    CpoMetaDescriptor retDescriptor = null;

    if (metaAdapter == null) {
      metaAdapter = CpoCoreMetaAdapter.newInstance(metaDescriptor, metaXmls);
      if (metaAdapter != null) {
        addCpoMetaAdapter(metaDescriptor.getName(), metaAdapter);
        retDescriptor = metaDescriptor;
      }
    } else {
      CpoCoreMetaAdapter.updateInstance(metaAdapter, metaXmls);
      retDescriptor = metaDescriptor;
    }
         
    return retDescriptor;
  }
    
  protected static CpoMetaDescriptor createUpdateInstance(CpoMetaDescriptor metaDescriptor, CpoMetaAdapter metaAdapter) throws CpoException {
    if (metaDescriptor != null && metaAdapter != null) {
      addCpoMetaAdapter(metaDescriptor.getName(), metaAdapter);
    }
    return metaDescriptor;
  }
    
  protected CpoMetaAdapter getCpoMetaAdapter() throws CpoException {
    CpoMetaAdapter metaAdapter = findCpoMetaAdapter(name);
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
  public void export(File file) throws CpoException {
    getCpoMetaAdapter().export(file);
  }

  @Override
  public void export(Writer writer) throws CpoException {
    getCpoMetaAdapter().export(writer);
  }

  @Override
  public void export(OutputStream outputStream) throws CpoException {
    getCpoMetaAdapter().export(outputStream);
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

  public CpoClass createCpoClass() throws CpoException {
    return ((AbstractCpoMetaAdapter)getCpoMetaAdapter()).createCpoClass();
  }

  public CpoAttribute createCpoAttribute() throws CpoException {
    return ((AbstractCpoMetaAdapter)getCpoMetaAdapter()).createCpoAttribute();
  }

  public CpoFunctionGroup createCpoFunctionGroup() throws CpoException {
    return ((AbstractCpoMetaAdapter)getCpoMetaAdapter()).createCpoFunctionGroup();
  }

  public CpoFunction createCpoFunction() throws CpoException {
    return ((AbstractCpoMetaAdapter)getCpoMetaAdapter()).createCpoFunction();
  }

  public CpoArgument createCpoArgument() throws CpoException {
    return ((AbstractCpoMetaAdapter)getCpoMetaAdapter()).createCpoArgument();
  }

  public String getName() {
    return name;
  }
  
}
