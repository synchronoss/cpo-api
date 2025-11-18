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

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import org.synchronoss.cpo.CpoException;

/**
 * Collects the info required to instantiate a DataSource stored as a JNDI Resource.
 *
 * <p>Provides the DataSourceInfo factory method getDataSource which instantiates the DataSource
 *
 * @author dberry
 */
public class JndiJdbcDataSourceInfo extends AbstractJdbcDataSourceInfo {

  private String jndiName = null;
  private Context jndiCtx = null;

  /**
   * Creates a JndiJdbcDataSourceInfo from a JNDIName that represents the datasource in the
   * application server.
   *
   * @param jndiName The JndiName of the app server datasource
   */
  public JndiJdbcDataSourceInfo(String jndiName, int fetchSize) {
    super(jndiName, fetchSize);
    this.jndiName = jndiName;
  }

  /**
   * Creates a JndiJdbcDataSourceInfo from a JNDIName that represents the datasource in the
   * application server.
   *
   * @param jndiName The JndiName of the app server datasource
   * @param ctx - The context for which the Jndi Lookup should use.
   */
  public JndiJdbcDataSourceInfo(String jndiName, int fetchSize, Context ctx) {
    super(jndiName, fetchSize);
    this.jndiName = jndiName;
    jndiCtx = ctx;
  }

  @Override
  protected DataSource createDataSource() throws CpoException {
    DataSource datasource = null;
    try {
      if (jndiCtx == null) {
        jndiCtx = new InitialContext();
      }
      datasource = (DataSource) jndiCtx.lookup(jndiName);
    } catch (Exception e) {
      throw new CpoException("Error instantiating DataSource", e);
    }
    return datasource;
  }
}
