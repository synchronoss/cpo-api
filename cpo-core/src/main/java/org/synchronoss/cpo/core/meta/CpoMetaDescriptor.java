package org.synchronoss.cpo.core.meta;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
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

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import java.io.File;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.cache.CpoMetaDescriptorCache;
import org.synchronoss.cpo.core.exporter.CoreMetaXmlObjectExporter;
import org.synchronoss.cpo.core.exporter.MetaXmlObjectExporter;
import org.synchronoss.cpo.core.helper.CpoClassLoader;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.core.helper.XmlHelper;
import org.synchronoss.cpo.core.meta.domain.*;
import org.synchronoss.cpo.core.parser.ExpressionParser;
import org.synchronoss.cpo.cpometa.CtCpoMetaData;
import org.synchronoss.cpo.cpometa.ObjectFactory;

/**
 * @author dberry
 */
public class CpoMetaDescriptor extends CpoMetaDescriptorCache
    implements CpoMetaAdapter, CpoMetaExportable {
  private static final Logger logger = LoggerFactory.getLogger(CpoMetaDescriptor.class);
  private String name = null;
  private boolean caseSensitive = true;
  private AbstractCpoMetaAdapter metaAdapter = null;

  // used by cpo util
  private String defaultPackageName;

  private CpoMetaDescriptor() {}

  protected CpoMetaDescriptor(String name, boolean caseSensitive) throws CpoException {
    this.name = name;
    this.caseSensitive = caseSensitive;

    // Lets create the metaAdapter
    try {
      Class<?> metaAdapterClass = getMetaAdapterClass();
      logger.debug("Creating MetaAdapter: " + metaAdapterClass.getName());
      metaAdapter =
          (AbstractCpoMetaAdapter) metaAdapterClass.getDeclaredConstructor().newInstance();
      logger.debug("Created MetaAdapter: " + metaAdapterClass.getName());
    } catch (InstantiationException ie) {
      throw new CpoException("Could not instantiate CpoMetaAdapter: ", ie);
    } catch (IllegalAccessException iae) {
      throw new CpoException("Could not access CpoMetaAdapter: ", iae);
    } catch (ClassCastException cce) {
      throw new CpoException(
          "CpoMetaAdapter must extend AbstractCpoMetaAdapter: "
              + getMetaAdapterClass().getName()
              + ":",
          cce);
    } catch (InvocationTargetException e) {
      throw new CpoException(
          "Could not invoke constructor for CpoMetaAdapter: "
              + getMetaAdapterClass().getName()
              + ":",
          e);
    } catch (NoSuchMethodException e) {
      throw new CpoException(
          "Could not find a default constructor for CpoMetaAdapter: "
              + getMetaAdapterClass().getName()
              + ":",
          e);
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

  public static CpoMetaDescriptor getInstance(String name, String metaXml, boolean caseSensitive)
      throws CpoException {
    List<String> metaXmls = new ArrayList<>();
    metaXmls.add(metaXml);
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  public static CpoMetaDescriptor getInstance(
      String name, List<String> metaXmls, boolean caseSensitive) throws CpoException {
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  public static CpoMetaDescriptor getInstance(String name, String[] metaXmls, boolean caseSensitive)
      throws CpoException {
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  /**
   * @return A collection of names of all meta descriptors currently loaded
   */
  public static Collection<String> getCpoMetaDescriptorNames() {
    return CpoMetaDescriptorCache.getCpoMetaDescriptorNames();
  }

  public static void refreshDescriptorMeta(String name, List<String> metaXmls) throws CpoException {
    refreshDescriptorMeta(name, metaXmls, false);
  }

  public static void refreshDescriptorMeta(String name, List<String> metaXmls, boolean overwrite)
      throws CpoException {
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

  protected static CpoMetaDescriptor createUpdateInstance(
      String name, List<String> metaXmls, boolean caseSensitive) throws CpoException {
    return createUpdateInstance(name, metaXmls.toArray(new String[0]), caseSensitive);
  }

  protected static CpoMetaDescriptor createUpdateInstance(
      String name, String[] metaXmls, boolean caseSensitive) throws CpoException {
    CpoMetaDescriptor metaDescriptor = findCpoMetaDescriptor(name);
    String metaDescriptorClassName = null;
    var errBuilder = new StringBuilder();

    logger.debug("CpoMetaDescriptor: " + metaDescriptor);

    for (String metaXml : metaXmls) {
      logger.debug("Processing: " + metaXml);
      errBuilder.setLength(0);
      CtCpoMetaData ctCpoMetaData =
          XmlHelper.unmarshalXmlObject(
              XmlHelper.CPO_META_XSD, metaXml, CtCpoMetaData.class, errBuilder);
      if (!errBuilder.isEmpty()) {
        throw new RuntimeException("Error parsing CPO meta XML: " + errBuilder.toString());
      }

      try {
        if (metaDescriptor == null) {
          logger.debug("Getting descriptor name");
          metaDescriptorClassName = ctCpoMetaData.getMetaDescriptor();
          logger.debug("Getting the Class");
          Class<?> clazz = CpoClassLoader.forName(metaDescriptorClassName);
          logger.debug("Getting the Constructor");
          Constructor<?> cons = clazz.getConstructor(String.class, boolean.class);
          logger.debug("Creating the instance");
          metaDescriptor = (CpoMetaDescriptor) cons.newInstance(name, caseSensitive);
          logger.debug("Adding the MetaDescriptor");
          addCpoMetaDescriptor(metaDescriptor);
        } else if (!metaDescriptor.getClass().getName().equals(ctCpoMetaData.getMetaDescriptor())) {
          throw new CpoException(
              "Error processing multiple metaXml files. All files must have the same"
                  + " CpoMetaDescriptor class name.");
        }

        metaDescriptor.setDefaultPackageName(ctCpoMetaData.getDefaultPackageName());
        metaDescriptor.getCpoMetaAdapter().loadCpoMetaDataDocument(ctCpoMetaData, caseSensitive);
      } catch (ClassNotFoundException cnfe) {
        throw new CpoException(
            "CpoMetaAdapter not found: "
                + metaDescriptorClassName
                + ": "
                + ExceptionHelper.getLocalizedMessage(cnfe));
      } catch (IllegalAccessException iae) {
        throw new CpoException(
            "Could not access CpoMetaAdapter: "
                + metaDescriptorClassName
                + ": "
                + ExceptionHelper.getLocalizedMessage(iae));
      } catch (InstantiationException ie) {
        throw new CpoException(
            "Could not instantiate CpoMetaAdapter: "
                + metaDescriptorClassName
                + ": "
                + ExceptionHelper.getLocalizedMessage(ie));
      } catch (InvocationTargetException ite) {
        throw new CpoException(
            "Could not invoke constructor: "
                + metaDescriptorClassName
                + ": "
                + ExceptionHelper.getLocalizedMessage(ite));
      } catch (IllegalArgumentException iae) {
        throw new CpoException(
            "Illegal Argument to constructor: "
                + metaDescriptorClassName
                + ": "
                + ExceptionHelper.getLocalizedMessage(iae));
      } catch (NoSuchMethodException nsme) {
        throw new CpoException(
            "Could not find constructor: "
                + metaDescriptorClassName
                + ": "
                + ExceptionHelper.getLocalizedMessage(nsme));
      } catch (SecurityException se) {
        throw new CpoException(
            "Not allowed to access constructor: "
                + metaDescriptorClassName
                + ": "
                + ExceptionHelper.getLocalizedMessage(se));
      } catch (ClassCastException cce) {
        throw new CpoException(
            "Class is not instance of CpoMetaDescriptor: "
                + metaDescriptorClassName
                + ":"
                + ExceptionHelper.getLocalizedMessage(cce));
      }
    }

    return metaDescriptor;
  }

  protected static CpoMetaDescriptor createUpdateInstance(
      CpoMetaDescriptor metaDescriptor, CpoMetaAdapter metaAdapter) throws CpoException {
    if (metaDescriptor != null && metaAdapter != null) {
      addCpoMetaDescriptor(metaDescriptor);
    }
    return metaDescriptor;
  }

  protected AbstractCpoMetaAdapter getCpoMetaAdapter() {
    return metaAdapter;
  }

  @Override
  public <T> CpoClass getMetaClass(T bean) throws CpoException {
    CpoClass cpoClass = getCpoMetaAdapter().getMetaClass(bean);
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

  protected final CtCpoMetaData buildCpoMetaData() {
    MetaXmlObjectExporter metaXmlObjectExporter = getMetaXmlObjectExporter();

    // need these sorted
    List<CpoClass> classList = new ArrayList<>();
    classList.addAll(getCpoMetaAdapter().getCpoClasses());
    Collections.sort(classList);
    for (CpoClass cpoClass : classList) {
      cpoClass.acceptMetaDFVisitor(metaXmlObjectExporter);
    }
    return metaXmlObjectExporter.getCpoMetaData();
  }

  private JAXBElement<CtCpoMetaData> getJaxbElement(CtCpoMetaData ctCpoMetaData) {
    ObjectFactory factory = new ObjectFactory();
    return factory.createCpoMetaData(ctCpoMetaData);
  }

  private Marshaller createMarshaller() throws JAXBException {
    JAXBContext jaxbContext = JAXBContext.newInstance(CtCpoMetaData.class);
    Marshaller marshaller = jaxbContext.createMarshaller();
    XmlHelper.setMarshallerProperties(marshaller);
    return marshaller;
  }

  public final void export(File file) throws CpoException {
    try {
      CtCpoMetaData ctCpoMetaData = buildCpoMetaData();
      Marshaller marshaller = createMarshaller();
      marshaller.marshal(getJaxbElement(ctCpoMetaData), file);
    } catch (JAXBException ex) {
      throw new CpoException(ex);
    }
  }

  public final void export(Writer writer) throws CpoException {
    try {
      CtCpoMetaData ctCpoMetaData = buildCpoMetaData();
      Marshaller marshaller = createMarshaller();
      marshaller.marshal(getJaxbElement(ctCpoMetaData), writer);
    } catch (JAXBException ex) {
      throw new CpoException(ex);
    }
  }

  public final void export(OutputStream outputStream) throws CpoException {
    try {
      CtCpoMetaData ctCpoMetaData = buildCpoMetaData();
      Marshaller marshaller = createMarshaller();
      marshaller.marshal(getJaxbElement(ctCpoMetaData), outputStream);
    } catch (JAXBException ex) {
      throw new CpoException(ex);
    }
  }

  public boolean isCaseSensitive() {
    return caseSensitive;
  }
}
