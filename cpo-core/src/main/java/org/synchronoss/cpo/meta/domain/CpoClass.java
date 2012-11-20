/*
 * Copyright (C) 2003-2012 David E. Berry
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
package org.synchronoss.cpo.meta.domain;

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.bean.CpoClassBean;

import java.util.*;
import org.synchronoss.cpo.helper.CpoClassLoader;

public class CpoClass extends CpoClassBean implements Comparable<CpoClass>, MetaDFVisitable {

  private static final Logger logger = LoggerFactory.getLogger(CpoClass.class);
  private boolean runTimeLoaded = false;
  private Class<?> metaClass = null;
  /**
   * javaMap contains a Map of CpoAttribute Objects the key is the javaName of the attribute
   */
  private Map<String, CpoAttribute> javaMap = new HashMap<String, CpoAttribute>();
  /**
   * dataMap contains a Map of CpoAttribute Objects the key is the dataName of the attribute
   */
  private Map<String, CpoAttribute> dataMap = new HashMap<String, CpoAttribute>();
  /**
   * functionGroups is a hashMap that contains a hashMap of CpoFunctionGroup Lists that are used by this object to persist and
   * retrieve it into a datasource.
   */
  private Map<String, CpoFunctionGroup> functionGroups = new HashMap<String, CpoFunctionGroup>();

  public CpoClass() {
  }

  public Class<?> getMetaClass() {
    return metaClass;
  }

  public void addAttribute(CpoAttribute cpoAttribute) {
    if (cpoAttribute != null) {
      logger.debug("Adding Attribute: " + cpoAttribute.getJavaName() + ":" + cpoAttribute.getDataName());
      javaMap.put(cpoAttribute.getJavaName(), cpoAttribute);
      dataMap.put(cpoAttribute.getDataName(), cpoAttribute);
    }
  }

  public void removeAttribute(CpoAttribute cpoAttribute) {
    if (cpoAttribute != null) {
      logger.debug("Removing Attribute: " + cpoAttribute.getJavaName() + ":" + cpoAttribute.getDataName());
      javaMap.remove(cpoAttribute.getJavaName());
      dataMap.remove(cpoAttribute.getDataName());
    }
  }

  public Map<String, CpoFunctionGroup> getFunctionGroups() {
    return this.functionGroups;
  }

  public CpoFunctionGroup getFunctionGroup(String groupType, String groupName) throws CpoException {
    String key = buildFunctionGroupKey(groupType, groupName);
    CpoFunctionGroup group = functionGroups.get(key);
    if (group == null) {
      throw new CpoException("Function Group Not Found: " + groupType + ":" + groupName);
    }
    return group;
  }

  public CpoFunctionGroup addFunctionGroup(CpoFunctionGroup group) {
    if (group == null) {
      return null;
    }

    String key = buildFunctionGroupKey(group.getType(), group.getName());
    logger.debug("Adding function group: " + key);
    return this.functionGroups.put(key, group);
  }

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

  @Override
  public int compareTo(CpoClass anotherCpoClass) {
    return getName().compareTo(anotherCpoClass.getName());
  }

  @Override
  public void acceptMetaDFVisitor(MetaVisitor visitor) {
    visitor.visit(this);

    // visit attributes -- need these sorted
    TreeMap<String, CpoAttribute> attributeMap = new TreeMap<String, CpoAttribute>(javaMap);
    for (CpoAttribute cpoAttribute : attributeMap.values()) {
      visitor.visit(cpoAttribute);
    }

    // visit function groups -- need these sorted
    TreeMap<String, CpoFunctionGroup> functionGroupMap = new TreeMap<String, CpoFunctionGroup>(functionGroups);
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

  public void loadRunTimeInfo(CpoMetaDescriptor metaDescriptor) throws CpoException {
      logger.debug("Loading run-time info for:"+getName());
      try {
        metaClass = CpoClassLoader.forName(getName());
      } catch (ClassNotFoundException cnfe) {
        throw new CpoException("Class not found: " + getName() + ": " + ExceptionHelper.getLocalizedMessage(cnfe));
      }

      for (CpoAttribute attribute : javaMap.values()) {
        attribute.loadRunTimeInfo(metaDescriptor, this);
      }
      logger.debug("Loaded run-time info for:"+getName());
      runTimeLoaded=true;
  }

  public CpoAttribute getAttributeJava(String javaName) {
    if (javaName == null) {
      return null;
    }
    return javaMap.get(javaName);
  }

  public CpoAttribute getAttributeData(String dataName) {
    if (dataName == null) {
      return null;
    }
    return dataMap.get(dataName);
  }

  @Override
  public String toString() {
    return this.getName();
  }

  public String toStringFull() {
    return super.toString();
  }

  public boolean isRunTimeLoaded() {
    return runTimeLoaded;
  }
}
