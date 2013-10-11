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

import org.slf4j.*;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.jdbc.JdbcCpoTransform;

import java.lang.reflect.Method;
import java.sql.Types;

/**
 * JdbcCpoAttribute. A class that includes the Jdbc specifc attributes that are additional to the CpoAttribute attributes
 *
 * @author david berry
 */
public class JdbcCpoAttribute extends CpoAttribute implements java.io.Serializable, java.lang.Cloneable {

  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoAttribute.class);
  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  private String dbTable_ = null;
  private String dbColumn_ = null;
  private int javaSqlType_ = Types.NULL;
  //Transform attributes
  private JdbcCpoTransform jdbcTransform = null;
  private Method transformPSOutMethod = null;
  private Method transformCSOutMethod = null;

  public JdbcCpoAttribute() {
  }

  public void setDbTable(String dbTable) {
    dbTable_ = dbTable;
  }

  public void setDbColumn(String dbColumn) {
    dbColumn_ = dbColumn;
  }

  public String getDbTable() {
    return dbTable_;
  }

  public String getDbColumn() {
    return dbColumn_;
  }

  protected void setJavaSqlType(int type) {
    javaSqlType_ = type;

  }

  protected int getJavaSqlType() {
    return this.javaSqlType_;
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
    setJavaSqlType(((JdbcCpoMetaDescriptor)metaDescriptor).getDataTypeInt(getDataType()));
  }
}
