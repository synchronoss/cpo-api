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
package org.synchronoss.cpo.meta.bean;

public class CpoAttributeBean implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Properties
   */
  private String javaName;
  private String javaType;
  private String dataName;
  private String dataType;
  private String transformClassName;
  private String description;

  public CpoAttributeBean() {}

  public String getDataName() {
    return dataName;
  }

  public void setDataName(String dataName) {
    this.dataName = dataName;
  }

  public String getDataType() {
    return dataType;
  }

  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getJavaName() {
    return javaName;
  }

  public void setJavaName(String javaName) {
    this.javaName = javaName;
  }

  public String getJavaType() {
    return javaType;
  }

  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  public String getTransformClassName() {
    return transformClassName;
  }

  public void setTransformClassName(String transformClassName) {
    this.transformClassName = transformClassName;
  }

  /*
   * Getters and Setters
   */
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    CpoAttributeBean that = (CpoAttributeBean) o;

    if (getJavaName() != null
        ? !getJavaName().equals(that.getJavaName())
        : that.getJavaName() != null) {
      return false;
    }
    if (getJavaType() != null
        ? !getJavaType().equals(that.getJavaType())
        : that.getJavaType() != null) {
      return false;
    }
    if (getDataName() != null
        ? !getDataName().equals(that.getDataName())
        : that.getDataName() != null) {
      return false;
    }
    if (getDataType() != null
        ? !getDataType().equals(that.getDataType())
        : that.getDataType() != null) {
      return false;
    }
    if (getTransformClassName() != null
        ? !getTransformClassName().equals(that.getTransformClassName())
        : that.getTransformClassName() != null) {
      return false;
    }
    if (getDescription() != null
        ? !getDescription().equals(that.getDescription())
        : that.getDescription() != null) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = 0;
    result = 31 * result + getClass().getName().hashCode();
    result = 31 * result + (getJavaName() != null ? getJavaName().hashCode() : 0);
    result = 31 * result + (getJavaType() != null ? getJavaType().hashCode() : 0);
    result = 31 * result + (getDataName() != null ? getDataName().hashCode() : 0);
    result = 31 * result + (getDataType() != null ? getDataType().hashCode() : 0);
    result =
        31 * result + (getTransformClassName() != null ? getTransformClassName().hashCode() : 0);
    result = 31 * result + (getDescription() != null ? getDescription().hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append("javaName = " + getJavaName() + "\n");
    str.append("javaType = " + getJavaType() + "\n");
    str.append("dataName = " + getDataName() + "\n");
    str.append("dataType = " + getDataType() + "\n");
    str.append("transformClass = " + getTransformClassName() + "\n");
    str.append("description = " + getDescription() + "\n");
    return str.toString();
  }
}
