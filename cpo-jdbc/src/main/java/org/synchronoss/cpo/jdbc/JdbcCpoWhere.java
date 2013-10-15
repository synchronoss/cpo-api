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

import org.synchronoss.cpo.BindableCpoWhere;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 13/10/13
 * Time: 14:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class JdbcCpoWhere extends BindableCpoWhere {

  public JdbcCpoWhere() {
  }

  public <T> JdbcCpoWhere(int logical, String attr, int comp, T value) {
    super(logical, attr, comp, value);
  }

  public <T> JdbcCpoWhere(int logical, String attr, int comp, T value, boolean not) {
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
