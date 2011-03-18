/**
 * JdbcConnection.java
 *
 *  Copyright (C) 2006  David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *  
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *  
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc;

import java.sql.Array;
import java.sql.Blob;
import java.sql.ClientInfoStatus;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ResultSet;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Struct;
import java.util.HashMap;
import java.util.Properties;


/**
 * <P>A connection (session) with a specific
 * database. SQL statements are executed and results are returned
 * within the context of a connection.
 * <P>
 * A <code>Connection</code> object's database is able to provide information
 * describing its tables, its supported SQL grammar, its stored
 * procedures, the capabilities of this connection, and so on. This
 * information is obtained with the <code>getMetaData</code> method.
 *
 * <P><B>Note:</B> By default a <code>Connection</code> object is in
 * auto-commit mode, which means that it automatically commits changes
 * after executing each statement. If auto-commit mode has been
 * disabled, the method <code>commit</code> must be called explicitly in
 * order to commit changes; otherwise, database changes will not be saved.
 * <P>
 * A new <code>Connection</code> object created using the JDBC 2.1 core API
 * has an initially empty type map associated with it. A user may enter a
 * custom mapping for a UDT in this type map.
 * When a UDT is retrieved from a data source with the
 * method <code>ResultSet.getObject</code>, the <code>getObject</code> method
 * will check the connection's type map to see if there is an entry for that
 * UDT.  If so, the <code>getObject</code> method will map the UDT to the
 * class indicated.  If there is no entry, the UDT will be mapped using the
 * standard mapping.
 * <p>
 * A user may create a new type map, which is a <code>java.util.Map</code>
 * object, make an entry in it, and pass it to the <code>java.sql</code>
 * methods that can perform custom mapping.  In this case, the method
 * will use the given type map instead of the one associated with
 * the connection.
 * <p>
 * For example, the following code fragment specifies that the SQL
 * type <code>ATHLETES</code> will be mapped to the class
 * <code>Athletes</code> in the Java programming language.
 * The code fragment retrieves the type map for the <code>Connection
 * </code> object <code>con</code>, inserts the entry into it, and then sets
 * the type map with the new entry as the connection's type map.
 * <pre>
 *      java.util.Map map = con.getTypeMap();
 *      map.put("mySchemaName.ATHLETES", Class.forName("Athletes"));
 *      con.setTypeMap(map);
 * </pre>
 *
 * @see DriverManager#getConnection
 * @see Statement
 * @see ResultSet
 * @see DatabaseMetaData
 */
public class JdbcConnection extends  AbstractJdbcConnection {

  public JdbcConnection(JdbcDataSource jds, Connection connection) {
      super(jds, connection);
  }

  public Clob createClob() throws SQLException {
      return getConnection().createClob();
  }

  public Blob createBlob() throws SQLException {
      return getConnection().createBlob();
  }

  public NClob createNClob() throws SQLException {
      return getConnection().createNClob();
  }

  public SQLXML createSQLXML() throws SQLException {
      return getConnection().createSQLXML();
  }

  public boolean isValid(int timeout) throws SQLException {
      return getConnection().isValid(timeout);
  }

  public void setClientInfo(String name, String value) throws SQLClientInfoException {
      try {
        getConnection().setClientInfo(name, value);
      } catch (SQLException e) {
        throw new SQLClientInfoException(e.getLocalizedMessage(), new HashMap<String,ClientInfoStatus>());
      }
  }

  public void setClientInfo(Properties properties) throws SQLClientInfoException {
      try {
        getConnection().setClientInfo(properties);
      } catch (SQLException e) {
        throw new SQLClientInfoException(e.getLocalizedMessage(), new HashMap<String,ClientInfoStatus>());
      }
  }

  public String getClientInfo(String name) throws SQLException {
      return getConnection().getClientInfo(name);
  }

  public Properties getClientInfo() throws SQLException {
      return getConnection().getClientInfo();
  }

  public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
      return getConnection().createArrayOf(typeName, elements);
  }

  public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
      return getConnection().createStruct(typeName, attributes);
  }

  public <T> T unwrap(Class<T> iface) throws SQLException {
      return getConnection().unwrap(iface);
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return getConnection().isWrapperFor(iface);
  }
}
