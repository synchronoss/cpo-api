package org.synchronoss.cpo.core.meta.bean;

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

/**
 * Plain-data holder for the fields of a CPO attribute as loaded from meta XML: the Java-side
 * name/type, the datastore-side name/type, and an optional {@code CpoTransform} class name. {@link
 * org.synchronoss.cpo.core.meta.domain.CpoAttribute} extends this with the runtime behavior
 * (reflective getter/setter resolution, transform instantiation, etc).
 *
 * @author dberry
 */
public class CpoAttributeBean implements java.io.Serializable {

  private static final long serialVersionUID = 1L;

  /*
   * Properties
   */
  /** The name of the Java bean property. */
  private String javaName;

  /** The fully-qualified Java type of the bean property. */
  private String javaType;

  /** The name of the datastore-side column/field. */
  private String dataName;

  /** The native datastore type of the column/field. */
  private String dataType;

  /** The fully-qualified class name of a custom {@code CpoTransform}, if any. */
  private String transformClassName;

  /** The description of this attribute. */
  private String description;

  /** Creates an empty instance. */
  public CpoAttributeBean() {}

  /*
   * Getters and Setters
   */

  /**
   * Gets the name of the datastore-side column/field this attribute binds to.
   *
   * @return the datastore-side name
   */
  public String getDataName() {
    return dataName;
  }

  /**
   * Sets the name of the datastore-side column/field this attribute binds to.
   *
   * @param dataName the datastore-side name
   */
  public void setDataName(String dataName) {
    this.dataName = dataName;
  }

  /**
   * Gets the name of the native datastore data type of this attribute.
   *
   * @return the native data type name
   */
  public String getDataType() {
    return dataType;
  }

  /**
   * Sets the name of the native datastore data type of this attribute.
   *
   * @param dataType the native data type name
   */
  public void setDataType(String dataType) {
    this.dataType = dataType;
  }

  /**
   * Gets the human-readable description of this attribute, as loaded from the meta XML.
   *
   * @return the description, or {@code null} if none was specified
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sets the human-readable description of this attribute.
   *
   * @param description the description
   */
  public void setDescription(String description) {
    this.description = description;
  }

  /**
   * Gets the name of the JavaBean property this attribute binds to.
   *
   * @return the JavaBean property name
   */
  public String getJavaName() {
    return javaName;
  }

  /**
   * Sets the name of the JavaBean property this attribute binds to.
   *
   * @param javaName the JavaBean property name
   */
  public void setJavaName(String javaName) {
    this.javaName = javaName;
  }

  /**
   * Gets the Java type name of the bound JavaBean property.
   *
   * @return the Java type name
   */
  public String getJavaType() {
    return javaType;
  }

  /**
   * Sets the Java type name of the bound JavaBean property.
   *
   * @param javaType the Java type name
   */
  public void setJavaType(String javaType) {
    this.javaType = javaType;
  }

  /**
   * Gets the fully-qualified class name of the {@code CpoTransform} to apply to this attribute's
   * value, if any.
   *
   * @return the transform class name, or {@code null} if no transform is configured
   */
  public String getTransformClassName() {
    return transformClassName;
  }

  /**
   * Sets the fully-qualified class name of the {@code CpoTransform} to apply to this attribute's
   * value.
   *
   * @param transformClassName the transform class name
   */
  public void setTransformClassName(String transformClassName) {
    this.transformClassName = transformClassName;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (o == null || getClass() != o.getClass()) return false;

    CpoAttributeBean that = (CpoAttributeBean) o;

    if (getJavaName() != null
        ? !getJavaName().equals(that.getJavaName())
        : that.getJavaName() != null) return false;

    if (getJavaType() != null
        ? !getJavaType().equals(that.getJavaType())
        : that.getJavaType() != null) return false;

    if (getDataName() != null
        ? !getDataName().equals(that.getDataName())
        : that.getDataName() != null) return false;

    if (getDataType() != null
        ? !getDataType().equals(that.getDataType())
        : that.getDataType() != null) return false;

    if (getTransformClassName() != null
        ? !getTransformClassName().equals(that.getTransformClassName())
        : that.getTransformClassName() != null) return false;

    return getDescription() != null
        ? getDescription().equals(that.getDescription())
        : that.getDescription() == null;
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
