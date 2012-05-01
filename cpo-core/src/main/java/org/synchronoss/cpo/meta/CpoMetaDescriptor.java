/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.meta;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cache.CpoMetaDescriptorCache;
import org.synchronoss.cpo.core.cpoCoreMeta.CpoMetaDataDocument;
import org.synchronoss.cpo.core.cpoCoreMeta.CtCpoMetaData;
import org.synchronoss.cpo.exporter.CoreMetaXmlObjectExporter;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.helper.XmlBeansHelper;
import org.synchronoss.cpo.meta.domain.*;
import org.synchronoss.cpo.parser.ExpressionParser;

/**
 *
 * @author dberry
 */
public class CpoMetaDescriptor extends CpoMetaDescriptorCache implements CpoMetaAdapter, CpoMetaExportable {

  private static Logger logger = LoggerFactory.getLogger(CpoMetaDescriptor.class.getName());
  private String name=null;
  private AbstractCpoMetaAdapter metaAdapter = null;
  
  private CpoMetaDescriptor(){}
  
  protected CpoMetaDescriptor(String name) throws CpoException {
    this.name = name;
    
    // Lets create the metaAdapter
    try {
      Class metaAdapterClass = getMetaAdapterClass();
      metaAdapter = (AbstractCpoMetaAdapter) metaAdapterClass.newInstance();
      logger.debug("Created MetaAdapter: "+metaAdapterClass.getName());
    } catch (InstantiationException ie) {
      throw new CpoException("Could not instantiate CpoMetaAdapter: " + ExceptionHelper.getLocalizedMessage(ie));
    } catch (IllegalAccessException iae) {
      throw new CpoException("Could not access CpoMetaAdapter: " + ExceptionHelper.getLocalizedMessage(iae));
    }catch (CpoException ce) {
      throw ce;
    }
  }
  
  protected Class getMetaAdapterClass() throws CpoException {
    throw new CpoException("getMetaAdapterClass() must be implemented");
  }
  
  public static boolean isValidMetaDescriptor(CpoMetaDescriptor metaDescriptor) {
    return (findCpoMetaDescriptor(metaDescriptor.getName()) != null);
  }
  
  public static CpoMetaDescriptor getInstance(String name) throws CpoException {
    CpoMetaDescriptor metaDescriptor = findCpoMetaDescriptor(name);
    return metaDescriptor;
  }
  
  public static CpoMetaDescriptor getInstance(String name, String metaXml) throws CpoException {
    List<String> metaXmls = new ArrayList<String>();
    metaXmls.add(metaXml);
    return createUpdateInstance(name,metaXmls);
  }
    
  public static CpoMetaDescriptor getInstance(String name, List<String> metaXmls) throws CpoException {
    return createUpdateInstance(name,metaXmls);
  }
    
  public static CpoMetaDescriptor getInstance(String name, String[] metaXmls) throws CpoException {
    return createUpdateInstance(name,metaXmls);
  }
    
  protected static CpoMetaDescriptor createUpdateInstance(String name, List<String> metaXmls) throws CpoException {
    return createUpdateInstance(name, metaXmls.toArray(new String[metaXmls.size()]));
  }
    
  protected static CpoMetaDescriptor createUpdateInstance(String name, String[] metaXmls) throws CpoException {
    CpoMetaDescriptor metaDescriptor = findCpoMetaDescriptor(name);
    String metaDescriptorClassName = null;

    for (String metaXml : metaXmls) {
      InputStream is = AbstractCpoMetaAdapter.class.getResourceAsStream(metaXml);
      if (is == null) {
        logger.info("Resource Not Found: "+metaXml);
        try {
          is = new FileInputStream(metaXml);
        } catch (FileNotFoundException fnfe) {
          logger.info("File Not Found: "+metaXml);
          is = null;
        }
      }
      try {
        CpoMetaDataDocument metaDataDoc;
        if (is == null) {
          metaDataDoc = CpoMetaDataDocument.Factory.parse(metaXml);
        } else {
          metaDataDoc = CpoMetaDataDocument.Factory.parse(is);
        }

        String errMsg = XmlBeansHelper.validateXml(metaDataDoc);
        if (errMsg!=null) {
          throw new CpoException("Invalid metaXml: "+metaXml+":"+errMsg);
        }

        if (metaDescriptor == null) {
          logger.debug("Getting descriptor name");
          metaDescriptorClassName = metaDataDoc.getCpoMetaData().getMetaDescriptor();
          logger.debug("Getting the Class");
          Class<?> clazz = Class.forName(metaDescriptorClassName);
          logger.debug("Getting the Constructor");
          if (clazz==null)
            logger.debug("clazz==null");
          Constructor<?> cons = clazz.getConstructor(String.class);
          logger.debug("Creating the instance");
          metaDescriptor = (CpoMetaDescriptor) cons.newInstance(name);
          addCpoMetaDescriptor(metaDescriptor);
        } else if (!metaDescriptorClassName.equals(metaDataDoc.getCpoMetaData().getMetaDescriptor())){
          throw new CpoException("Error processing multiple metaXml files. All files must have the same CpoMetaDescriptor class name.");
        }

        metaDescriptor.metaAdapter.loadCpoMetaDataDocument(metaDataDoc);

      } catch (IOException ioe) {
        throw new CpoException("Error processing metaData from InputStream: "+metaXml + ": " + ExceptionHelper.getLocalizedMessage(ioe));
      } catch (XmlException xe) {
        throw new CpoException("Error processing metaData from String: "+metaXml + ": " + ExceptionHelper.getLocalizedMessage(xe));
      } catch (ClassNotFoundException cnfe) {
        throw new CpoException("CpoMetaAdapter not found: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(cnfe));
      } catch (IllegalAccessException iae) {
        throw new CpoException("Could not access CpoMetaAdapter: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(iae));
      } catch (InstantiationException ie) {
        throw new CpoException("Could not instantiate CpoMetaAdapter: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(ie));
      } catch (CpoException ce) {
        throw ce;
      } catch (InvocationTargetException ite) {
        throw new CpoException("Could not invoke constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(ite));
      } catch (IllegalArgumentException iae) {
        throw new CpoException("Illegal Argument to constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(iae));
      } catch (NoSuchMethodException nsme) {
        throw new CpoException("Could not find constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(nsme));
      } catch (SecurityException se) {
        throw new CpoException("Not allowed to access constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(se));
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (Exception e) {

          }
        }
      }
    } 
         
    return metaDescriptor;
  }
    
  protected static CpoMetaDescriptor createUpdateInstance(CpoMetaDescriptor metaDescriptor, CpoMetaAdapter metaAdapter) throws CpoException {
    if (metaDescriptor != null && metaAdapter != null) {
      addCpoMetaDescriptor(metaDescriptor);
    }
    return metaDescriptor;
  }
      
  protected CpoMetaAdapter getCpoMetaAdapter() throws CpoException {
    return metaAdapter;
  }

  @Override
  public <T> CpoClass getMetaClass(T obj) throws CpoException {
    CpoClass cpoClass = getCpoMetaAdapter().getMetaClass(obj);
    if (cpoClass != null && cpoClass.getMetaClass() == null) {
      cpoClass.loadRunTimeInfo(this);
    }

    return cpoClass;
  }

  @Override
  public List<CpoClass> getCpoClasses() throws CpoException {
    return getCpoMetaAdapter().getCpoClasses();
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
  
  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new CoreMetaXmlObjectExporter(this);
  }

  protected final CpoMetaDataDocument export() {
    MetaXmlObjectExporter metaXmlObjectExporter = getMetaXmlObjectExporter();
    for (CpoClass cpoClass : metaAdapter.getCpoClasses()) {
      cpoClass.acceptMetaDFVisitor(metaXmlObjectExporter);
    }
    return metaXmlObjectExporter.getCpoMetaDataDocument();
  }

  public final void export(File file) throws CpoException {
    try {
      CpoMetaDataDocument doc = export();
      doc.save(file, getXmlOptions());
    } catch (IOException ex) {
      throw new CpoException(ex.getMessage(), ex);
    }
  }

  public final void export(Writer writer) throws CpoException {
    try {
      CpoMetaDataDocument doc = export();
      doc.save(writer, getXmlOptions());
    } catch (IOException ex) {
      throw new CpoException(ex.getMessage(), ex);
    }
  }

  public final void export(OutputStream outputStream) throws CpoException {
    try {
      CpoMetaDataDocument doc = export();
      doc.save(outputStream, getXmlOptions());
    } catch (IOException ex) {
      throw new CpoException(ex.getMessage(), ex);
    }
  }

  protected final XmlOptions getXmlOptions() {
    XmlOptions xo = new XmlOptions();
    xo.setCharacterEncoding("utf-8");
    xo.setSaveAggressiveNamespaces();
    xo.setSaveNamespacesFirst();
    xo.setSavePrettyPrint();
    xo.setUseDefaultNamespace();
    return xo;
  }

}
