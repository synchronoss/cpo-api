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
package org.synchronoss.cpo.jdbc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.jdbc.JdbcCpoTransform;

import java.io.Serial;
import java.lang.reflect.Method;

/**
 * JdbcCpoAttribute. A class that includes the Jdbc specifc attributes that are additional to the CpoAttribute attributes
 *
 * @author david berry
 */
public class JdbcCpoAttribute extends CpoAttribute implements java.io.Serializable, Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoAttribute.class);
  /**
   * Version Id for this class.
   */
  @Serial
  private static final long serialVersionUID = 1L;

  private String dbTable_ = null;
  private String dbColumn_ = null;
  //Transform attributes
  private JdbcCpoTransform jdbcTransform = null;
  private Method transformPSOutMethod = null;
  private Method transformCSOutMethod = null;

    /**
     * Constructs a JdbcCpoAttribute
     */
  public JdbcCpoAttribute() {
  }

    /**
     * Set the db table associated with this attribute
     *
     * @param dbTable - The db table name to set.
     */
  public void setDbTable(String dbTable) {
    dbTable_ = dbTable;
  }

    /**
     * Set the db column associated with this attribute
     *
     * @param dbColumn  - The db column name to set.
     */
  public void setDbColumn(String dbColumn) {
    dbColumn_ = dbColumn;
  }

    /**
     * Get the db table associated with this attribute
     *
     * @return The db table associated with this attribute.
     */
  public String getDbTable() {
    return dbTable_;
  }

    /**
     * Get the db column associated with this attribute
     *
     * @return The db column associated with this attribute
     */
  public String getDbColumn() {
    return dbColumn_;
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
  protected void initTransformClass(CpoMetaDescriptor metaDescriptor) throws CpoException {
    super.initTransformClass(metaDescriptor);
    if (getCpoTransform() != null && getCpoTransform() instanceof JdbcCpoTransform) {
      jdbcTransform = (JdbcCpoTransform) getCpoTransform();

      for (Method m : findMethods(jdbcTransform.getClass(), TRANSFORM_OUT_NAME, 2, true)) {
        if (m.getParameterTypes()[0].getName().equals("org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory")) {
          transformPSOutMethod = m;
        } else if (m.getParameterTypes()[0].getName().equals("org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory")) {
          transformCSOutMethod = m;
        }
      }
    }

    // TODO Revisit this. Initializing the java sql type here. Not sure that this is the right place.
    setDataTypeInt(metaDescriptor.getDataTypeInt(getDataType()));
  }
}
