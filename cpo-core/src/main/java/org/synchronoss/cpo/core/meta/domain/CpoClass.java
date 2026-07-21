package org.synchronoss.cpo.core.meta.domain;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.ReentrantLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.MetaDFVisitable;
import org.synchronoss.cpo.core.MetaVisitor;
import org.synchronoss.cpo.core.enums.Crud;
import org.synchronoss.cpo.core.helper.CpoClassLoader;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.bean.CpoClassBean;

/**
 * Runtime metadata for a JavaBean class mapped by CPO: its {@link CpoAttribute}s, keyed both by
 * Java property name and by datastore column name, and its {@link CpoFunctionGroup}s (one per named
 * CRUD operation group). Subclasses ({@link CpoClassCaseSensitive}, {@link
 * CpoClassCaseInsensitive}) determine how the datastore-name lookup treats case.
 *
 * @author dberry
 */
public abstract class CpoClass extends CpoClassBean
    implements Comparable<CpoClass>, MetaDFVisitable {
  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CpoClass.class);

  /** Guards lazy initialization of {@link #javaMap}/{@link #dataMap}. */
  private final ReentrantLock lock = new ReentrantLock();

  /** The JavaBean class this metadata describes. */
  private Class<?> metaClass = null;

  /** javaMap contains a Map of CpoAttribute Objects the key is the javaName of the attribute */
  private Map<String, CpoAttribute> javaMap = new HashMap<>();

  /** dataMap contains a Map of CpoAttribute Objects the key is the dataName of the attribute */
  private Map<String, CpoAttribute> dataMap = new HashMap<>();

  /**
   * functionGroups is a hashMap that contains a hashMap of CpoFunctionGroup Lists that are used by
   * this bean to create and retrieve it into a datasource.
   */
  private Map<String, CpoFunctionGroup> functionGroups = new HashMap<>();

  /** Creates an empty instance. */
  public CpoClass() {}

  /**
   * Gets the reflectively-resolved Java class this metadata describes, once {@link
   * #loadRunTimeInfo(CpoMetaDescriptor)} has been called.
   *
   * @return the resolved Java class, or {@code null} if runtime info has not been loaded
   */
  public Class<?> getMetaClass() {
    return metaClass;
  }

  /**
   * Adds an attribute to the datastore-name lookup map, using whatever case-sensitivity policy the
   * concrete subclass implements.
   *
   * @param dataName the datastore-side name to key on
   * @param cpoAttribute the attribute to add
   */
  public abstract void addDataNameToMap(String dataName, CpoAttribute cpoAttribute);

  /**
   * Removes an attribute from the datastore-name lookup map.
   *
   * @param dataName the datastore-side name to remove
   */
  public abstract void removeDataNameFromMap(String dataName);

  /**
   * Gets the attribute bound to the given datastore-side name.
   *
   * @param dataName the datastore-side name to look up
   * @return the matching attribute, or {@code null} if none is bound to {@code dataName}
   */
  public abstract CpoAttribute getAttributeData(String dataName);

  /**
   * Gets the underlying datastore-name-to-attribute map, for use by subclasses implementing the
   * abstract lookup methods.
   *
   * @return the datastore-name-keyed attribute map
   */
  protected Map<String, CpoAttribute> getDataMap() {
    return dataMap;
  }

  /**
   * Adds an attribute to this class, indexing it by both its Java name and its datastore name. A
   * no-op if {@code cpoAttribute} is {@code null}.
   *
   * @param cpoAttribute the attribute to add
   */
  public void addAttribute(CpoAttribute cpoAttribute) {
    if (cpoAttribute != null) {
      logger.debug(
          "Adding Attribute: " + cpoAttribute.getJavaName() + ":" + cpoAttribute.getDataName());
      javaMap.put(cpoAttribute.getJavaName(), cpoAttribute);
      addDataNameToMap(cpoAttribute.getDataName(), cpoAttribute);
    }
  }

  /**
   * Removes an attribute from this class's Java-name and datastore-name indexes. A no-op if {@code
   * cpoAttribute} is {@code null}.
   *
   * @param cpoAttribute the attribute to remove
   */
  public void removeAttribute(CpoAttribute cpoAttribute) {
    if (cpoAttribute != null) {
      logger.debug(
          "Removing Attribute: " + cpoAttribute.getJavaName() + ":" + cpoAttribute.getDataName());
      javaMap.remove(cpoAttribute.getJavaName());
      removeDataNameFromMap(cpoAttribute.getDataName());
    }
  }

  /**
   * Gets all function groups defined on this class, keyed internally by CRUD type and group name.
   *
   * @return the function groups map
   */
  public Map<String, CpoFunctionGroup> getFunctionGroups() {
    return this.functionGroups;
  }

  /**
   * Gets the function group for the given CRUD type and group name.
   *
   * @param crud the CRUD operation type
   * @param groupName the function group name
   * @return the matching function group
   * @throws CpoException if no function group matches {@code crud} and {@code groupName}
   */
  public CpoFunctionGroup getFunctionGroup(Crud crud, String groupName) throws CpoException {
    String key = buildFunctionGroupKey(crud.operation, groupName);
    CpoFunctionGroup group = functionGroups.get(key);
    if (group == null) {
      throw new CpoException("Function Group Not Found: " + crud.operation + ":" + groupName);
    }
    return group;
  }

  /**
   * Gets whether a function group exists for the given CRUD type and group name.
   *
   * @param crud the CRUD operation type
   * @param groupName the function group name
   * @return {@code true} if a matching function group exists, {@code false} otherwise
   * @throws CpoException if the lookup fails
   */
  public boolean existsFunctionGroup(Crud crud, String groupName) throws CpoException {
    String key = buildFunctionGroupKey(crud.operation, groupName);
    CpoFunctionGroup group = functionGroups.get(key);
    return group != null;
  }

  /**
   * Adds a function group to this class, keyed by its CRUD type and name. A no-op (returning {@code
   * null}) if {@code group} is {@code null}.
   *
   * @param group the function group to add
   * @return the function group previously registered under the same key, or {@code null} if none
   */
  public CpoFunctionGroup addFunctionGroup(CpoFunctionGroup group) {
    if (group == null) {
      return null;
    }

    String key = buildFunctionGroupKey(group.getType(), group.getName());
    logger.debug("Adding function group: " + key);
    return this.functionGroups.put(key, group);
  }

  /**
   * Removes a function group from this class. A no-op if {@code group} is {@code null}.
   *
   * @param group the function group to remove
   */
  public void removeFunctionGroup(CpoFunctionGroup group) {
    if (group != null) {
      String key = buildFunctionGroupKey(group.getType(), group.getName());
      functionGroups.remove(key);
    }
  }

  private String buildFunctionGroupKey(String groupType, String groupName) {
    StringBuilder builder = new StringBuilder();
    if (groupType != null) {
      builder.append(groupType);
    }
    builder.append("@");
    if (groupName != null) {
      builder.append(groupName);
    }
    return builder.toString();
  }

  /**
   * {@inheritDoc}
   *
   * <p>Orders classes by {@link #getName()}.
   */
  @Override
  public int compareTo(CpoClass anotherCpoClass) {
    return getName().compareTo(anotherCpoClass.getName());
  }

  /**
   * {@inheritDoc}
   *
   * <p>Visits this class, then its attributes (sorted by Java name), then each function group
   * (sorted by name) together with its functions and each function's arguments, all in their
   * natural/declared order.
   */
  @Override
  public void acceptMetaDFVisitor(MetaVisitor visitor) {
    visitor.visit(this);

    // visit attributes -- need these sorted
    TreeMap<String, CpoAttribute> attributeMap = new TreeMap<>(javaMap);
    for (CpoAttribute cpoAttribute : attributeMap.values()) {
      visitor.visit(cpoAttribute);
    }

    // visit function groups -- need these sorted
    TreeMap<String, CpoFunctionGroup> functionGroupMap = new TreeMap<>(functionGroups);
    for (CpoFunctionGroup cpoFunctionGroup : functionGroupMap.values()) {
      visitor.visit(cpoFunctionGroup);

      // visit the functions
      List<CpoFunction> functions = cpoFunctionGroup.getFunctions();
      if (functions != null) {
        for (CpoFunction cpoFunction : functions) {
          visitor.visit(cpoFunction);

          // visit the arguments
          List<CpoArgument> arguments = cpoFunction.getArguments();
          if (arguments != null) {
            for (CpoArgument cpoArgument : arguments) {
              visitor.visit(cpoArgument);
            }
          }
        }
      }
    }
  }

  /**
   * Resolves this class's reflective metadata (the concrete Java class, and each attribute's
   * getter/setter/transform) against the classpath, if not already resolved. Idempotent and
   * thread-safe; subsequent calls after the first successful resolution are no-ops.
   *
   * @param metaDescriptor the owning descriptor, passed through to each attribute's resolution
   * @throws CpoException if the class cannot be found, or an attribute cannot be resolved
   */
  public void loadRunTimeInfo(CpoMetaDescriptor metaDescriptor) throws CpoException {
    lock.lock();
    try {
      if (metaClass == null) {
        Class<?> tmpMetaClass = null;

        try {
          logger.debug("Loading runtimeinfo for " + getName());
          tmpMetaClass = CpoClassLoader.forName(getName());
        } catch (ClassNotFoundException cnfe) {
          throw new CpoException(
              "Class not found: " + getName() + ": " + ExceptionHelper.getLocalizedMessage(cnfe));
        }

        for (CpoAttribute attribute : javaMap.values()) {
          attribute.loadRunTimeInfo(metaDescriptor, tmpMetaClass);
        }
        logger.debug("Loaded runtimeinfo for " + getName());

        metaClass = tmpMetaClass;
      }
    } finally {
      lock.unlock();
    }
  }

  /**
   * Gets the attribute bound to the given JavaBean property name.
   *
   * @param javaName the JavaBean property name to look up
   * @return the matching attribute, or {@code null} if {@code javaName} is {@code null} or no
   *     attribute is bound to it
   */
  public CpoAttribute getAttributeJava(String javaName) {
    if (javaName == null) {
      return null;
    }
    return javaMap.get(javaName);
  }

  /**
   * {@inheritDoc}
   *
   * <p>Returns this class's {@link #getName() name}.
   */
  @Override
  public String toString() {
    return this.getName();
  }

  /**
   * Gets the full field-by-field string representation of this class, as produced by {@link
   * CpoClassBean#toString()}.
   *
   * @return the full string representation
   */
  public String toStringFull() {
    return super.toString();
  }

  /** Clears this class's attribute and function group maps, discarding all metadata. */
  public void emptyMaps() {
    javaMap.clear();
    dataMap.clear();
    functionGroups.clear();
  }
}
