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

import java.util.SortedMap;
import java.util.TreeMap;
import org.synchronoss.cpo.meta.bean.CpoClassBean;

public class CpoClass<T> extends CpoClassBean {
  
  private Class<T> metaClass = null;
  /**
    * attributeMap contains a Map of CpoAttribute Objects
    * the id is the dataName of the attribute in the database
    * the value is the attribute name for the class being described 
    */
  private TreeMap<String,CpoAttribute> attributeMap = new TreeMap<String,CpoAttribute>();

  /**
    * columnMap contains a Map of CpoAttribute Objects
    * the id is the attributeName of the attribute in the database
    * the value is the dataname for the class being described 
    */
  private TreeMap<String,CpoAttribute> columnMap = new TreeMap<String,CpoAttribute>();

  /**
    * queryGroup is a hashMap that contains a hashMap of jdbcQuery Lists that are used 
    * by this object to persist and retrieve it into a jdbc datasource. 
    */
  private TreeMap<String, CpoFunctionGroup> functionGroups = new TreeMap<String, CpoFunctionGroup>();

  public CpoClass() {
  }

  public CpoClass(Class<T> metaClass, String className) {
    super(className);
    this.metaClass=metaClass;
  }

  public SortedMap<String,CpoAttribute> getAttributeMap(){
      return this.attributeMap;
  }


  public SortedMap<String,CpoAttribute> getColumnMap(){
      return this.columnMap;
  }

  public SortedMap<String, CpoFunctionGroup> getFunctionGroups(){
      return this.functionGroups;
  }

  public CpoFunctionGroup getFunctionGroup(String groupType, String groupName){
      return this.functionGroups.get(groupType+"@"+groupName);
  }

  public CpoFunctionGroup addFunctionGroup(String groupType, String groupName, CpoFunctionGroup group){
      return this.functionGroups.put(groupType+"@"+groupName, group);
  }

  public Class<T> getMetaClass(){
      return metaClass;
  }

  public void setMetaClass(Class<T> c){
      metaClass = c;
  }

}
