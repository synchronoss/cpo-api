/*
 *  Copyright (C) 2003-2012 David E. Berry
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
package org.synchronoss.cpo.jdbc;

/**
 * JdbcType is a class that maps datasource datatypes to java.sql.types and java classes
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
    setJavaSqlType(javaSqlType);
    setJavaSqlTypeName(javaSqlTypeName);
    setJavaClass(javaClass);
  }

  public void setJavaSqlType(int javaSqlType) {
    javaSqlType_ = javaSqlType;
  }

  public void setJavaSqlTypeName(String javaSqlTypeName) {
    javaSqlTypeName_ = javaSqlTypeName;
  }

  public void setJavaClass(Class<T> javaClass) {
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
}