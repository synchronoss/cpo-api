/**
 *  Copyright (C) 2006-2012  David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.meta.domain;

import org.synchronoss.cpo.*;
import org.synchronoss.cpo.meta.bean.CpoClassBean;

import java.util.*;

public class CpoClass extends CpoClassBean implements MetaDFVisitable {

  /**
   * attributeMap contains a Map of CpoAttribute Objects
   * the id is the dataName of the attribute in the database
   * the value is the attribute name for the class being described
   */
  private SortedMap<String, CpoAttribute> attributeMap = new TreeMap<String, CpoAttribute>();

  /**
   * columnMap contains a Map of CpoAttribute Objects
   * the id is the attributeName of the attribute in the database
   * the value is the dataname for the class being described
   */
  private SortedMap<String, CpoAttribute> columnMap = new TreeMap<String, CpoAttribute>();

  /**
   * queryGroup is a hashMap that contains a hashMap of jdbcQuery Lists that are used
   * by this object to persist and retrieve it into a jdbc datasource.
   */
  private SortedMap<String, CpoFunctionGroup> functionGroups = new TreeMap<String, CpoFunctionGroup>();

  public CpoClass() {
  }

  public SortedMap<String, CpoAttribute> getAttributeMap() {
    return this.attributeMap;
  }

  public SortedMap<String, CpoAttribute> getColumnMap() {
    return this.columnMap;
  }

  public SortedMap<String, CpoFunctionGroup> getFunctionGroups() {
    return this.functionGroups;
  }

  public CpoFunctionGroup getFunctionGroup(String groupType, String groupName) {
    return this.functionGroups.get(groupType + "@" + groupName);
  }

  public CpoFunctionGroup addFunctionGroup(CpoFunctionGroup group) {
    if (group == null)
      return null;

    return this.functionGroups.put(group.getType() + "@" + group.getName(), group);
  }

  @Override
  public void acceptMetaDFVisitor(MetaVisitor visitor) {
    visitor.visit(this);

    // visit attributes
    for (CpoAttribute cpoAttribute : getAttributeMap().values()) {
      visitor.visit(cpoAttribute);
    }

    // visit function groups
    for (CpoFunctionGroup cpoFunctionGroup : getFunctionGroups().values()) {
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
}