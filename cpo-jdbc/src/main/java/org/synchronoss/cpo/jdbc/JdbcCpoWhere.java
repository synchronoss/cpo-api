package org.synchronoss.cpo.jdbc;

/*-
 * [-------------------------------------------------------------------------
 * jdbc
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

import org.synchronoss.cpo.BindableCpoWhere;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * Created with IntelliJ IDEA. User: dberry Date: 13/10/13 Time: 14:05 PM To change this template
 * use File | Settings | File Templates.
 */
public class JdbcCpoWhere extends BindableCpoWhere {
  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  /** Create a JdbcCpoWhere */
  public JdbcCpoWhere() {}

  /**
   * Create a JdbcCpoWhere
   *
   * @param <T> - The type of the value being compared
   * @param logical - Logical operator
   * @param attr - Attribute being compared
   * @param comp - Compare operator
   * @param value - The value to compare against the attribute
   */
  public <T> JdbcCpoWhere(Logical logical, String attr, Comparison comp, T value) {
    super(logical, attr, comp, value);
  }

  /**
   * Create a JdbcCpoWhere
   *
   * @param <T> - The type of the value being compared
   * @param logical - Logical operator
   * @param attr - Attribute being compared
   * @param comp - Compare operator
   * @param value - The value to compare against the attribute
   * @param not - add the logical not operator
   */
  public <T> JdbcCpoWhere(Logical logical, String attr, Comparison comp, T value, boolean not) {
    super(logical, attr, comp, value, not);
  }

  @Override
  protected String buildColumnName(CpoAttribute attribute) {
    JdbcCpoAttribute jdbcCpoAttribute = (JdbcCpoAttribute) attribute;

    StringBuilder columnName = new StringBuilder();

    if (jdbcCpoAttribute.getDbTable() != null) {
      columnName.append(jdbcCpoAttribute.getDbTable());
      columnName.append(".");
    }
    if (jdbcCpoAttribute.getDbColumn() != null) {
      columnName.append(jdbcCpoAttribute.getDbColumn());
    } else {
      columnName.append(jdbcCpoAttribute.getDataName());
    }

    return columnName.toString();
  }
}
