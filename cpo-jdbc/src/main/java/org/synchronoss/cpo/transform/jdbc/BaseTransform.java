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
package org.synchronoss.cpo.transform.jdbc;

import org.jboss.jca.adapters.jdbc.WrappedConnection;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;

import java.sql.*;

public abstract class BaseTransform<D, J> implements JdbcCpoTransform<D, J> {

  protected Connection handleConnection(JdbcPreparedStatementFactory jpsf) throws SQLException {
    Connection connection = jpsf.getPreparedStatement().getConnection();
    if (connection instanceof WrappedConnection) {
      WrappedConnection wrappedConnection = (WrappedConnection) connection;
      connection = wrappedConnection.getUnderlyingConnection();
    }
    //ToDo- check for Apache Tomcat wrapped connection
    return connection;
  }
}

