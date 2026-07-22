package org.synchronoss.cpo.core.meta;

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
 * {@code CpoMetaDescriptor} is the runtime, named handle on a loaded CPO metadata source: it owns
 * the datastore-specific {@link AbstractCpoMetaAdapter} that holds the actual {@link CpoClass}
 * metadata, and delegates the {@link CpoMetaAdapter} contract to it. Instances are singletons per
 * name, cached in the inherited {@link CpoMetaDescriptorCache} and looked up/created via the static
 * {@code getInstance}/{@code createUpdateInstance} factory methods rather than constructed
 * directly.
 *
 * <p>A descriptor can be built up from one or more meta XML documents (via {@link
 * #getInstance(String, List, boolean)} and friends); loading multiple documents into the same named
 * descriptor merges their {@link CpoClass} definitions, which is what enables the polymorphic
 * override pattern described in the project documentation.
 *
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

  /**
   * Gets whether {@code metaDescriptor} is registered in the descriptor cache under its own name.
   *
   * @param metaDescriptor the descriptor to check
   * @return {@code true} if a descriptor with the same name is currently cached, {@code false}
   *     otherwise
   */
  public static boolean isValidMetaDescriptor(CpoMetaDescriptor metaDescriptor) {
    return findCpoMetaDescriptor(metaDescriptor.getName()) != null;
  }

  /**
   * Gets the already-loaded descriptor registered under the given name.
   *
   * @param name the descriptor name
   * @return the cached descriptor, or {@code null} if none is registered under {@code name}
   * @throws CpoException if the descriptor cannot be looked up
   */
  public static CpoMetaDescriptor getInstance(String name) throws CpoException {
    return findCpoMetaDescriptor(name);
  }

  /**
   * Removes the descriptor registered under the given name from the cache.
   *
   * @param name the descriptor name
   * @throws CpoException if the descriptor cannot be removed
   */
  public static void removeInstance(String name) throws CpoException {
    removeCpoMetaDescriptor(name);
  }

  /**
   * Removes all descriptors from the cache.
   *
   * @throws CpoException if the cache cannot be cleared
   */
  public static void clearAllInstances() throws CpoException {
    clearCpoMetaDescriptorCache();
  }

  /**
   * Gets the descriptor registered under {@code name}, creating and loading it from a single meta
   * XML document if it does not already exist.
   *
   * @param name the descriptor name
   * @param metaXml the meta XML document (or classpath/file reference) to load
   * @param caseSensitive whether attribute data names should be matched case-sensitively
   * @return the loaded (or previously cached) descriptor
   * @throws CpoException if the meta XML cannot be parsed or loaded
   */
  public static CpoMetaDescriptor getInstance(String name, String metaXml, boolean caseSensitive)
      throws CpoException {
    List<String> metaXmls = new ArrayList<>();
    metaXmls.add(metaXml);
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  /**
   * Gets the descriptor registered under {@code name}, creating and loading it from one or more
   * meta XML documents if it does not already exist. Loading multiple documents into the same
   * descriptor merges their class definitions.
   *
   * @param name the descriptor name
   * @param metaXmls the meta XML documents (or classpath/file references) to load, in order
   * @param caseSensitive whether attribute data names should be matched case-sensitively
   * @return the loaded (or previously cached) descriptor
   * @throws CpoException if the meta XML cannot be parsed or loaded
   */
  public static CpoMetaDescriptor getInstance(
      String name, List<String> metaXmls, boolean caseSensitive) throws CpoException {
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  /**
   * Gets the descriptor registered under {@code name}, creating and loading it from one or more
   * meta XML documents if it does not already exist. Loading multiple documents into the same
   * descriptor merges their class definitions.
   *
   * @param name the descriptor name
   * @param metaXmls the meta XML documents (or classpath/file references) to load, in order
   * @param caseSensitive whether attribute data names should be matched case-sensitively
   * @return the loaded (or previously cached) descriptor
   * @throws CpoException if the meta XML cannot be parsed or loaded
   */
  public static CpoMetaDescriptor getInstance(String name, String[] metaXmls, boolean caseSensitive)
      throws CpoException {
    return createUpdateInstance(name, metaXmls, caseSensitive);
  }

  /**
   * Gets the names of all meta descriptors currently loaded.
   *
   * @return A collection of names of all meta descriptors currently loaded
   */
  public static Collection<String> getCpoMetaDescriptorNames() {
    return CpoMetaDescriptorCache.getCpoMetaDescriptorNames();
  }

  /**
   * Hot-reloads the descriptor registered under {@code name} by merging in the given meta XML
   * documents, if that descriptor is currently loaded. A no-op if no descriptor is registered under
   * {@code name}.
   *
   * @param name the descriptor name
   * @param metaXmls the meta XML documents (or classpath/file references) to (re)load
   * @throws CpoException if the meta XML cannot be parsed or loaded
   */
  public static void refreshDescriptorMeta(String name, List<String> metaXmls) throws CpoException {
    refreshDescriptorMeta(name, metaXmls, false);
  }

  /**
   * Hot-reloads the descriptor registered under {@code name} by merging in (or, if {@code
   * overwrite} is set, replacing with) the given meta XML documents, if that descriptor is
   * currently loaded. A no-op if no descriptor is registered under {@code name}.
   *
   * @param name the descriptor name
   * @param metaXmls the meta XML documents (or classpath/file references) to (re)load
   * @param overwrite whether to discard the descriptor's existing classes before loading
   * @throws CpoException if the meta XML cannot be parsed or loaded
   */
  public static void refreshDescriptorMeta(String name, List<String> metaXmls, boolean overwrite)
      throws CpoException {
    CpoMetaDescriptor metaDescriptor = findCpoMetaDescriptor(name);
    if (metaDescriptor != null) {
      metaDescriptor.refreshDescriptorMeta(metaXmls, overwrite);
    }
  }

  /**
   * Hot-reloads this descriptor by merging in the given meta XML documents.
   *
   * @param metaXmls the meta XML documents (or classpath/file references) to (re)load
   * @throws CpoException if the meta XML cannot be parsed or loaded
   */
  public void refreshDescriptorMeta(List<String> metaXmls) throws CpoException {
    refreshDescriptorMeta(metaXmls, false);
  }

  /**
   * Hot-reloads this descriptor by merging in (or, if {@code overwrite} is set, replacing with) the
   * given meta XML documents.
   *
   * @param metaXmls the meta XML documents (or classpath/file references) to (re)load
   * @param overwrite whether to discard this descriptor's existing classes before loading
   * @throws CpoException if the meta XML cannot be parsed or loaded
   */
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

  /** {@inheritDoc} */
  @Override
  public <T> CpoClass getMetaClass(T bean) throws CpoException {
    CpoClass cpoClass = getCpoMetaAdapter().getMetaClass(bean);
    if (cpoClass != null) {
      cpoClass.loadRunTimeInfo(this);
    }

    return cpoClass;
  }

  /** {@inheritDoc} */
  @Override
  public List<CpoClass> getCpoClasses() throws CpoException {
    return getCpoMetaAdapter().getCpoClasses();
  }

  /**
   * Adds a {@link CpoClass} to this descriptor's metadata.
   *
   * @param cpoClass the class metadata to add
   * @throws CpoException if the class cannot be added
   */
  public void addCpoClass(CpoClass cpoClass) throws CpoException {
    getCpoMetaAdapter().addCpoClass(cpoClass);
  }

  /**
   * Removes a {@link CpoClass} from this descriptor's metadata.
   *
   * @param cpoClass the class metadata to remove
   * @throws CpoException if the class cannot be removed
   */
  public void removeCpoClass(CpoClass cpoClass) throws CpoException {
    getCpoMetaAdapter().removeCpoClass(cpoClass);
  }

  /** {@inheritDoc} */
  @Override
  public ExpressionParser getExpressionParser() throws CpoException {
    return getCpoMetaAdapter().getExpressionParser();
  }

  /** {@inheritDoc} */
  @Override
  public String getDataTypeName(CpoAttribute attribute) throws CpoException {
    return getCpoMetaAdapter().getDataTypeName(attribute);
  }

  /** {@inheritDoc} */
  @Override
  public Class<?> getDataTypeJavaClass(CpoAttribute attribute) throws CpoException {
    return getCpoMetaAdapter().getDataTypeJavaClass(attribute);
  }

  /** {@inheritDoc} */
  @Override
  public int getDataTypeInt(String dataTypeName) throws CpoException {
    return getCpoMetaAdapter().getDataTypeInt(dataTypeName);
  }

  /** {@inheritDoc} */
  @Override
  public DataTypeMapEntry<?> getDataTypeMapEntry(int dataTypeInt) throws CpoException {
    return getCpoMetaAdapter().getDataTypeMapEntry(dataTypeInt);
  }

  /** {@inheritDoc} */
  @Override
  public List<String> getAllowableDataTypes() throws CpoException {
    return getCpoMetaAdapter().getAllowableDataTypes();
  }

  /**
   * Creates a new, empty {@link CpoClass} of the concrete type appropriate for this descriptor's
   * case-sensitivity setting.
   *
   * @return a new, unpopulated class metadata instance
   * @throws CpoException if the instance cannot be created
   */
  public CpoClass createCpoClass() throws CpoException {
    return getCpoMetaAdapter().createCpoClass(caseSensitive);
  }

  /**
   * Creates a new, empty {@link CpoAttribute}.
   *
   * @return a new, unpopulated attribute metadata instance
   * @throws CpoException if the instance cannot be created
   */
  public CpoAttribute createCpoAttribute() throws CpoException {
    return getCpoMetaAdapter().createCpoAttribute();
  }

  /**
   * Creates a new, empty {@link CpoFunctionGroup}.
   *
   * @return a new, unpopulated function group metadata instance
   * @throws CpoException if the instance cannot be created
   */
  public CpoFunctionGroup createCpoFunctionGroup() throws CpoException {
    return getCpoMetaAdapter().createCpoFunctionGroup();
  }

  /**
   * Creates a new, empty {@link CpoFunction}.
   *
   * @return a new, unpopulated function metadata instance
   * @throws CpoException if the instance cannot be created
   */
  public CpoFunction createCpoFunction() throws CpoException {
    return getCpoMetaAdapter().createCpoFunction();
  }

  /**
   * Creates a new, empty {@link CpoArgument}.
   *
   * @return a new, unpopulated argument metadata instance
   * @throws CpoException if the instance cannot be created
   */
  public CpoArgument createCpoArgument() throws CpoException {
    return getCpoMetaAdapter().createCpoArgument();
  }

  /**
   * Gets the default Java package name used by the CPO tooling (e.g. {@code cpo-plugin} code
   * generation) when a meta XML document does not otherwise specify one.
   *
   * @return the default package name, or {@code null} if none has been set
   */
  public String getDefaultPackageName() {
    return defaultPackageName;
  }

  /**
   * Sets the default Java package name used by the CPO tooling. A {@code null} argument is ignored,
   * leaving any previously set value unchanged.
   *
   * @param packageName the default package name
   */
  public void setDefaultPackageName(String packageName) {
    if (packageName != null) {
      defaultPackageName = packageName;
    }
  }

  /**
   * Gets the name this descriptor is registered under.
   *
   * @return the descriptor name
   */
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

  /** {@inheritDoc} */
  @Override
  public final void export(File file) throws CpoException {
    try {
      CtCpoMetaData ctCpoMetaData = buildCpoMetaData();
      Marshaller marshaller = createMarshaller();
      marshaller.marshal(getJaxbElement(ctCpoMetaData), file);
    } catch (JAXBException ex) {
      throw new CpoException(ex);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void export(Writer writer) throws CpoException {
    try {
      CtCpoMetaData ctCpoMetaData = buildCpoMetaData();
      Marshaller marshaller = createMarshaller();
      marshaller.marshal(getJaxbElement(ctCpoMetaData), writer);
    } catch (JAXBException ex) {
      throw new CpoException(ex);
    }
  }

  /** {@inheritDoc} */
  @Override
  public final void export(OutputStream outputStream) throws CpoException {
    try {
      CtCpoMetaData ctCpoMetaData = buildCpoMetaData();
      Marshaller marshaller = createMarshaller();
      marshaller.marshal(getJaxbElement(ctCpoMetaData), outputStream);
    } catch (JAXBException ex) {
      throw new CpoException(ex);
    }
  }

  /**
   * Gets whether this descriptor matches attribute data names case-sensitively.
   *
   * @return {@code true} if data names are matched case-sensitively, {@code false} otherwise
   */
  public boolean isCaseSensitive() {
    return caseSensitive;
  }
}
