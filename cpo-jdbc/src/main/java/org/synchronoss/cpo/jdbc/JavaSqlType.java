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
package org.synchronoss.cpo.jdbc;

/**
 * JavaSqlType is a class that defines the mapping of  datasource datatypes to java types
 *
 * @author david berry
 */
public class JavaSqlType<T> extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  private int javaSqlType_ = java.sql.Types.NULL;
  private String javaSqlTypeName_ = null;
  private Class<T> javaClass_ = null;

  @SuppressWarnings("unused")
  private JavaSqlType() {
  }

  public JavaSqlType(int javaSqlType, String javaSqlTypeName, Class<T> javaClass) {
    javaSqlType_ = javaSqlType;
    javaSqlTypeName_ = javaSqlTypeName;
    javaClass_ = javaClass;
  }

  public int getJavaSqlType() {
    return javaSqlType_;
  }

  public String getJavaSqlTypeName() {
    return javaSqlTypeName_;
  }

  public Class<T> getJavaClass() {
    return javaClass_;
  }
  
  public String makeJavaName(String dataName){
    dataName = dataName.toLowerCase();

    int idx;
    while ((idx = dataName.indexOf("_")) > 0) {
      // remove the underscore, upper case the following character
      dataName = dataName.substring(0, idx) + dataName.substring(idx + 1, idx + 2).toUpperCase() + dataName.substring(idx + 2);
    }
    return dataName;
  }
}