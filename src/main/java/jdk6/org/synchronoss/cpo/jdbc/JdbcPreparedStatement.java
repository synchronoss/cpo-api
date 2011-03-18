/**
 * JdbcPreparedStatement.java
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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.BatchUpdateException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import org.synchronoss.cpo.JavaVersion;
import java.sql.NClob;
import java.sql.RowId;
import java.sql.SQLXML;

/**
 * An object that represents a precompiled SQL statement.
 * <P>A SQL statement is precompiled and stored in a
 * <code>PreparedStatement</code> object. This object can then be used to
 * efficiently execute this statement multiple times. 
 *
 * <P><B>Note:</B> The setter methods (<code>setShort</code>, <code>setString</code>,
 * and so on) for setting IN parameter values
 * must specify types that are compatible with the defined SQL type of
 * the input parameter. For instance, if the IN parameter has SQL type
 * <code>INTEGER</code>, then the method <code>setInt</code> should be used.
 *
 * <p>If arbitrary parameter type conversions are required, the method
 * <code>setObject</code> should be used with a target SQL type.
 * <P>
 * In the following example of setting a parameter, <code>con</code> represents
 * an active connection:  
 * <PRE>
 *   PreparedStatement pstmt = con.prepareStatement("UPDATE EMPLOYEES
 *                                     SET SALARY = ? WHERE ID = ?");
 *   pstmt.setBigDecimal(1, 153833.00)
 *   pstmt.setInt(2, 110592)
 * </PRE>
 *
 * @see Connection#prepareStatement
 * @see ResultSet 
 */

public class JdbcPreparedStatement extends AbstractJdbcPreparedStatement{

  public JdbcPreparedStatement(PreparedStatement pstmt){
		super(pstmt);
	}
  
  public void setRowId(int parameterIndex, RowId x) throws SQLException {
      getPreparedStatement().setRowId(parameterIndex, x);
  }

  public void setNString(int parameterIndex, String value) throws SQLException {
      getPreparedStatement().setNString(parameterIndex, value);
  }

  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
      getPreparedStatement().setNCharacterStream(parameterIndex, value, length);
  }

  public void setNClob(int parameterIndex, NClob value) throws SQLException {
      getPreparedStatement().setNClob(parameterIndex, value);
  }

  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
      getPreparedStatement().setClob(parameterIndex, reader, length);
  }

  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
      getPreparedStatement().setBlob(parameterIndex, inputStream, length);
  }

  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
      getPreparedStatement().setNClob(parameterIndex, reader, length);
  }

  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
      getPreparedStatement().setSQLXML(parameterIndex, xmlObject);
  }

  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
      getPreparedStatement().setAsciiStream(parameterIndex, x, length);
  }

  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
      getPreparedStatement().setBinaryStream(parameterIndex, x, length);
  }

  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
      getPreparedStatement().setCharacterStream(parameterIndex, reader, length);
  }

  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
      getPreparedStatement().setAsciiStream(parameterIndex, x);
  }

  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
      getPreparedStatement().setBinaryStream(parameterIndex, x);
  }

  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
      getPreparedStatement().setCharacterStream(parameterIndex, reader);
  }

  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
      getPreparedStatement().setNCharacterStream(parameterIndex, value);
  }

  public void setClob(int parameterIndex, Reader reader) throws SQLException {
      getPreparedStatement().setClob(parameterIndex, reader);
  }

  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
      getPreparedStatement().setBlob(parameterIndex, inputStream);
  }

  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
      getPreparedStatement().setNClob(parameterIndex, reader);
  }

  public boolean isClosed() throws SQLException {
      return getPreparedStatement().isClosed();
  }

  public void setPoolable(boolean poolable) throws SQLException {
      getPreparedStatement().setPoolable(poolable);
  }

  public boolean isPoolable() throws SQLException {
      return getPreparedStatement().isPoolable();
  }

  public <T> T unwrap(Class<T> iface) throws SQLException {
      return getPreparedStatement().unwrap(iface);
  }

  public boolean isWrapperFor(Class<?> iface) throws SQLException {
      return getPreparedStatement().isWrapperFor(iface);
  }

}
