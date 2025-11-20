package org.synchronoss.cpo.jdbc;

/*-
 * [[
 * jdbc
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
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

import java.io.Serial;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * JdbcCpoAttribute. A class that includes the Jdbc specifc attributes that are additional to the
 * CpoAttribute attributes
 *
 * @author david berry
 */
public class JdbcCpoAttribute extends CpoAttribute implements java.io.Serializable, Cloneable {

  /** Version Id for this class. */
  @Serial private static final long serialVersionUID = 1L;

  private String dbTable_ = null;
  private String dbColumn_ = null;

  /** Constructs a JdbcCpoAttribute */
  public JdbcCpoAttribute() {}

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
   * @param dbColumn - The db column name to set.
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
    // TODO Revisit this. Initializing the java sql type here. Not sure that this is the right
    // place.
    setDataTypeInt(metaDescriptor.getDataTypeInt(getDataType()));
  }
}
