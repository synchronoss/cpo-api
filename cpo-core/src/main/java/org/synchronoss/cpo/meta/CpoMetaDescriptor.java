/*
 * Copyright (C) 2003-2025 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.meta;

import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cache.CpoMetaDescriptorCache;
import org.synchronoss.cpo.core.cpoCoreMeta.CpoMetaDataDocument;
import org.synchronoss.cpo.exporter.CoreMetaXmlObjectExporter;
import org.synchronoss.cpo.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.helper.CpoClassLoader;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.helper.XmlBeansHelper;
import org.synchronoss.cpo.meta.domain.*;
import org.synchronoss.cpo.parser.ExpressionParser;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author dberry
 */
public class CpoMetaDescriptor extends CpoMetaDescriptorCache implements CpoMetaAdapter, CpoMetaExportable {
  private static final Logger logger = LoggerFactory.getLogger(CpoMetaDescriptor.class);
  private String name = null;
  private boolean caseSensitive = true;
  private AbstractCpoMetaAdapter metaAdapter = null;

  // used by cpo util
  private String defaultPackageName;

  private CpoMetaDescriptor() {
  }

  protected CpoMetaDescriptor(String name, boolean caseSensitive) throws CpoException {
    this.name = name;
    this.caseSensitive = caseSensitive;

    // Lets create the metaAdapter
    try {
      Class<?> metaAdapterClass = getMetaAdapterClass();
      logger.debug("Creating MetaAdapter: " + metaAdapterClass.getName());
      metaAdapter = (AbstractCpoMetaAdapter)metaAdapterClass.newInstance();
      logger.debug("Created MetaAdapter: " + metaAdapterClass.getName());
    } catch (InstantiationException ie) {
      throw new CpoException("Could not instantiate CpoMetaAdapter: " + ExceptionHelper.getLocalizedMessage(ie));
    } catch (IllegalAccessException iae) {
      throw new CpoException("Could not access CpoMetaAdapter: " + ExceptionHelper.getLocalizedMessage(iae));
    } catch (ClassCastException cce) {
      throw new CpoException("CpoMetaAdapter must extend AbstractCpoMetaAdapter: " + getMetaAdapterClass().getName() + ":" + ExceptionHelper.getLocalizedMessage(cce));
    }
  }

  protected Class<?> getMetaAdapterClass() throws CpoException {
    throw new CpoException("getMetaAdapterClass() must be implemented");
  }

  public static boolean isValidMetaDescriptor(CpoMetaDescriptor metaDescriptor) {
    return findCpoMetaDescriptor(metaDescriptor.getName()) != null;
  }

  public static CpoMetaDescriptor getInstance(String name) throws CpoException {
    return findCpoMetaDescriptor(name);
  }

  public static void removeInstance(String name) throws CpoException {
    removeCpoMetaDescriptor(name);
  }

  public static void clearAllInstances() throws CpoException {
    clearCpoMetaDescriptorCache();
  }

  public static CpoMetaDescriptor getInstance(String name, String metaXml, boolean caseSensitive) throws CpoException {
    List<String> metaXmls = new ArrayList<>();
    metaXmls.add(metaXml);
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  public static CpoMetaDescriptor getInstance(String name, List<String> metaXmls, boolean caseSensitive) throws CpoException {
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  public static CpoMetaDescriptor getInstance(String name, String[] metaXmls, boolean caseSensitive) throws CpoException {
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  /**
   * @return A collection of names of all meta descriptors currently loaded
   */
  public static Collection<String> getCpoMetaDescriptorNames() {
    return CpoMetaDescriptorCache.getCpoMetaDescriptorNames();
  }

  public static void refreshDescriptorMeta(String name, List<String> metaXmls) throws CpoException {
    refreshDescriptorMeta(name,metaXmls, false);
  }

  public static void refreshDescriptorMeta(String name, List<String> metaXmls, boolean overwrite) throws CpoException {
    CpoMetaDescriptor metaDescriptor = findCpoMetaDescriptor(name);
    if (metaDescriptor != null) {
      metaDescriptor.refreshDescriptorMeta(metaXmls, overwrite);
    }
  }

  public void refreshDescriptorMeta(List<String> metaXmls) throws CpoException {
    refreshDescriptorMeta(metaXmls, false);
  }

  public void refreshDescriptorMeta(List<String> metaXmls, boolean overwrite) throws CpoException {
    if (overwrite) {
      getCpoMetaAdapter().removeAllCpoClass();
    }
    createUpdateInstance(this.getName(), metaXmls, caseSensitive);
  }

  protected static CpoMetaDescriptor createUpdateInstance(String name, List<String> metaXmls, boolean caseSensitive) throws CpoException {
    return createUpdateInstance(name, metaXmls.toArray(new String[metaXmls.size()]), caseSensitive);
  }

  protected static CpoMetaDescriptor createUpdateInstance(String name, String[] metaXmls, boolean caseSensitive) throws CpoException {
    CpoMetaDescriptor metaDescriptor = findCpoMetaDescriptor(name);
    String metaDescriptorClassName = null;
      var errBuilder = new StringBuilder();

    for (String metaXml : metaXmls) {
      InputStream is = null;

      // See if the file is a uri
      try {
        URL cpoConfigUrl = new URL(metaXml);
        is = cpoConfigUrl.openStream();
      } catch (IOException e) {
          errBuilder.append("Uri Not Found: ").append(metaXml).append("\n");
      }

      // See if the file is a resource in the jar
      if (is == null)
        is = CpoClassLoader.getResourceAsStream(metaXml);

      if (is == null) {
          errBuilder.append("Resource Not Found: ").append(metaXml).append("\n");
        try {
          //See if the file is a local file on the server
          is = new FileInputStream(metaXml);
        } catch (FileNotFoundException fnfe) {
            errBuilder.append("File Not Found: ").append(metaXml).append("\n");
          is = null;
        }
      }
      try {
        CpoMetaDataDocument metaDataDoc;
        if (is == null) {
          //See if the config is sent in as a string
            try {
                metaDataDoc = CpoMetaDataDocument.Factory.parse(metaXml);
            } catch (XmlException e) {
                throw new CpoException(errBuilder.toString(), e);
            }
        } else {
          metaDataDoc = CpoMetaDataDocument.Factory.parse(is);
        }

        String errMsg = XmlBeansHelper.validateXml(metaDataDoc);
        if (errMsg != null) {
          throw new CpoException("Invalid metaXml: " + metaXml + ":" + errMsg);
        }

        if (metaDescriptor == null) {
          logger.debug("Getting descriptor name");
          metaDescriptorClassName = metaDataDoc.getCpoMetaData().getMetaDescriptor();
          logger.debug("Getting the Class");
          Class<?> clazz = CpoClassLoader.forName(metaDescriptorClassName);
          logger.debug("Getting the Constructor");
          if (clazz == null) {
            logger.debug("clazz==null");
          }
          Constructor<?> cons = clazz.getConstructor(String.class, boolean.class);
          logger.debug("Creating the instance");
          metaDescriptor = (CpoMetaDescriptor)cons.newInstance(name, caseSensitive);
          logger.debug("Adding the MetaDescriptor");
          addCpoMetaDescriptor(metaDescriptor);
        } else if (!metaDescriptor.getClass().getName().equals(metaDataDoc.getCpoMetaData().getMetaDescriptor())) {
          throw new CpoException("Error processing multiple metaXml files. All files must have the same CpoMetaDescriptor class name.");
        }

        metaDescriptor.setDefaultPackageName(metaDataDoc.getCpoMetaData().getDefaultPackageName());
        metaDescriptor.getCpoMetaAdapter().loadCpoMetaDataDocument(metaDataDoc, caseSensitive);
      } catch (IOException ioe) {
        throw new CpoException("Error processing metaData from InputStream: " + metaXml + ": " + ExceptionHelper.getLocalizedMessage(ioe));
      } catch (XmlException xe) {
        throw new CpoException("Error processing metaData from String: " + metaXml + ": " + ExceptionHelper.getLocalizedMessage(xe));
      } catch (ClassNotFoundException cnfe) {
        throw new CpoException("CpoMetaAdapter not found: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(cnfe));
      } catch (IllegalAccessException iae) {
        throw new CpoException("Could not access CpoMetaAdapter: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(iae));
      } catch (InstantiationException ie) {
        throw new CpoException("Could not instantiate CpoMetaAdapter: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(ie));
      } catch (InvocationTargetException ite) {
        throw new CpoException("Could not invoke constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(ite));
      } catch (IllegalArgumentException iae) {
        throw new CpoException("Illegal Argument to constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(iae));
      } catch (NoSuchMethodException nsme) {
        throw new CpoException("Could not find constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(nsme));
      } catch (SecurityException se) {
        throw new CpoException("Not allowed to access constructor: " + metaDescriptorClassName + ": " + ExceptionHelper.getLocalizedMessage(se));
      } catch (ClassCastException cce) {
        throw new CpoException("Class is not instance of CpoMetaDescriptor: " + metaDescriptorClassName + ":" + ExceptionHelper.getLocalizedMessage(cce));
      } finally {
        if (is != null) {
          try {
            is.close();
          } catch (Exception e) {
            if (logger.isTraceEnabled()) {
              logger.trace(e.getLocalizedMessage());
            }
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

  protected AbstractCpoMetaAdapter getCpoMetaAdapter() {
    return metaAdapter;
  }

  @Override
  public <T> CpoClass getMetaClass(T obj) throws CpoException {
    CpoClass cpoClass = getCpoMetaAdapter().getMetaClass(obj);
    if (cpoClass != null) {
      cpoClass.loadRunTimeInfo(this);
    }

    return cpoClass;
  }

  @Override
  public List<CpoClass> getCpoClasses() throws CpoException {
    return getCpoMetaAdapter().getCpoClasses();
  }

  public void addCpoClass(CpoClass cpoClass) throws CpoException {
    getCpoMetaAdapter().addCpoClass(cpoClass);
  }

  public void removeCpoClass(CpoClass cpoClass) throws CpoException {
    getCpoMetaAdapter().removeCpoClass(cpoClass);
  }

  @Override
  public ExpressionParser getExpressionParser() throws CpoException {
    return getCpoMetaAdapter().getExpressionParser();
  }

  @Override
  public String getDataTypeName(CpoAttribute attribute) throws CpoException {
    return getCpoMetaAdapter().getDataTypeName(attribute);
  }

  @Override
  public Class<?> getDataTypeJavaClass(CpoAttribute attribute) throws CpoException {
    return getCpoMetaAdapter().getDataTypeJavaClass(attribute);
  }

  @Override
  public int getDataTypeInt(String dataTypeName) throws CpoException {
    return getCpoMetaAdapter().getDataTypeInt(dataTypeName);
  }

  @Override
  public DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) throws CpoException {
    return getCpoMetaAdapter().getDataTypeMapEntry(dataTypeInt);
  }

  @Override
  public List<String> getAllowableDataTypes() throws CpoException {
    return getCpoMetaAdapter().getAllowableDataTypes();
  }

  public CpoClass createCpoClass() throws CpoException {
    return getCpoMetaAdapter().createCpoClass(caseSensitive);
  }

  public CpoAttribute createCpoAttribute() throws CpoException {
    return getCpoMetaAdapter().createCpoAttribute();
  }

  public CpoFunctionGroup createCpoFunctionGroup() throws CpoException {
    return getCpoMetaAdapter().createCpoFunctionGroup();
  }

  public CpoFunction createCpoFunction() throws CpoException {
    return getCpoMetaAdapter().createCpoFunction();
  }

  public CpoArgument createCpoArgument() throws CpoException {
    return getCpoMetaAdapter().createCpoArgument();
  }

  public String getDefaultPackageName() {
    return defaultPackageName;
  }

  public void setDefaultPackageName(String packageName) {
    if (packageName != null) {
      defaultPackageName = packageName;
    }
  }

  public String getName() {
    return name;
  }

  protected MetaXmlObjectExporter getMetaXmlObjectExporter() {
    return new CoreMetaXmlObjectExporter(this);
  }

  protected final CpoMetaDataDocument export() {
    MetaXmlObjectExporter metaXmlObjectExporter = getMetaXmlObjectExporter();

    // need these sorted
    List<CpoClass> classList = new ArrayList<>();
    classList.addAll(getCpoMetaAdapter().getCpoClasses());
    Collections.sort(classList);
    for (CpoClass cpoClass : classList) {
      cpoClass.acceptMetaDFVisitor(metaXmlObjectExporter);
    }
    return metaXmlObjectExporter.getCpoMetaDataDocument();
  }

  public final void export(File file) throws CpoException {
    try {
      CpoMetaDataDocument doc = export();
      doc.save(file, XmlBeansHelper.getXmlOptions());
    } catch (IOException ex) {
      throw new CpoException(ex.getMessage(), ex);
    }
  }

  public final void export(Writer writer) throws CpoException {
    try {
      CpoMetaDataDocument doc = export();
      doc.save(writer, XmlBeansHelper.getXmlOptions());
    } catch (IOException ex) {
      throw new CpoException(ex.getMessage(), ex);
    }
  }

  public final void export(OutputStream outputStream) throws CpoException {
    try {
      CpoMetaDataDocument doc = export();
      doc.save(outputStream, XmlBeansHelper.getXmlOptions());
    } catch (IOException ex) {
      throw new CpoException(ex.getMessage(), ex);
    }
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }
}
