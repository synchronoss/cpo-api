package org.synchronoss.cpo.cassandra.meta;

/*-
 * [-------------------------------------------------------------------------
 * cassandra
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

import java.io.Serial;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.transform.CassandraCpoTransform;
import org.synchronoss.cpo.helper.CpoClassLoader;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * JdbcCpoAttribute. A class that includes the Jdbc specifc attributes that are additional to the
 * CpoAttribute attributes
 *
 * @author david berry
 */
public class CassandraCpoAttribute extends CpoAttribute implements java.io.Serializable, Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(CassandraCpoAttribute.class);

  /** Version Id for this class. */
  @Serial private static final long serialVersionUID = 1L;

  private String keyType_ = null;
  private String valueType_ = null;
  private Class<?> keyTypeClass = null;
  private Class<?> valueTypeClass = null;

  // Transform attributes
  private CassandraCpoTransform cassandraTransform = null;
  private Method transformPSOutMethod = null;

  /** Constructs the CassandraCpoAttribute */
  public CassandraCpoAttribute() {}

  /**
   * Gets the keytype class name
   *
   * @return The keytype class name
   */
  public String getKeyType() {
    return keyType_;
  }

  /**
   * Sets the Key Type
   *
   * @param keyType The keytype class name
   */
  public void setKeyType(String keyType) {
    this.keyType_ = keyType;
  }

  /**
   * Gets the Value Type classname
   *
   * @return the Value Type classname
   */
  public String getValueType() {
    return valueType_;
  }

  /**
   * Sets the Value Type classname
   *
   * @param valueType the Value Type classname
   */
  public void setValueType(String valueType) {
    this.valueType_ = valueType;
  }

  /**
   * Sets the Value Type classname
   *
   * @return the Value Type classname
   */
  public Class<?> getKeyTypeClass() {
    return keyTypeClass;
  }

  /**
   * Sets the Key Type Clazz
   *
   * @param keyTypeClass the Key Type Clazz
   */
  public void setKeyTypeClass(Class<?> keyTypeClass) {
    this.keyTypeClass = keyTypeClass;
  }

  /**
   * Gets the Value Type Clazz
   *
   * @return the Value Type Clazz
   */
  public Class<?> getValueTypeClass() {
    return valueTypeClass;
  }

  /**
   * Sets the Value Type Clazz
   *
   * @param valueTypeClass the Value Type Clazz
   */
  public void setValueTypeClass(Class<?> valueTypeClass) {
    this.valueTypeClass = valueTypeClass;
  }

  //  private void dumpMethod(Method m) {
  //    logger.debug("========================");
  //    logger.debug("===> Declaring Class: " + m.getDeclaringClass().getName());
  //    logger.debug("===> Method Signature: " + m.toString());
  //    logger.debug("===> Generic Signature: " + m.toGenericString());
  //    logger.debug("===> Method isBridge: " + m.isBridge());
  //    logger.debug("===> Method isSynthetic: " + m.isSynthetic());
  //    logger.debug("========================");
  //  }
  @Override
  public void loadRunTimeInfo(CpoMetaDescriptor metaDescriptor, Class<?> metaClass)
      throws CpoException {
    super.loadRunTimeInfo(metaDescriptor, metaClass);

    setKeyTypeClass(getTypeClass(metaClass.getName(), getJavaName(), getKeyType(), "KeyType"));
    setValueTypeClass(
        getTypeClass(metaClass.getName(), getJavaName(), getValueType(), "ValueType"));
  }

  /**
   * Loads, if needed, and returns the Type Clazz for the CpoAttribute
   *
   * @param className The classname
   * @param attributeName The attribute name
   * @param contextName The context name
   * @param contextType The context type
   * @return The Clazz
   * @throws CpoException An errror occurred
   */
  protected Class getTypeClass(
      String className, String attributeName, String contextName, String contextType)
      throws CpoException {
    Class contextClass = null;
    try {
      if (contextName != null) {
        logger.debug(
            "Loading loading the " + contextType + " Class for " + className + "." + attributeName);
        contextClass = CpoClassLoader.forName(contextName);
      }
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException(
          contextType
              + " class not found: "
              + contextName
              + ": "
              + ExceptionHelper.getLocalizedMessage(cnfe));
    }
    return contextClass;
  }

  @Override
  protected void initTransformClass(CpoMetaDescriptor metaDescriptor) throws CpoException {
    super.initTransformClass(metaDescriptor);
    if (getCpoTransform() != null && getCpoTransform() instanceof CassandraCpoTransform) {
      cassandraTransform = (CassandraCpoTransform) getCpoTransform();

      for (Method m : findMethods(cassandraTransform.getClass(), TRANSFORM_OUT_NAME, 2, true)) {
        if (m.getParameterTypes()[0]
            .getName()
            .equals("org.synchronoss.cpo.cassandra.CassandraBoundStatementFactory")) {
          transformPSOutMethod = m;
        }
      }
    }

    // TODO Revisit this. Initializing the java sql type here. Not sure that this is the right
    // place.
    setDataTypeInt(metaDescriptor.getDataTypeInt(getDataType()));
  }
}
