/**
 * JdbcCallableStatement.java
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
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Statement;

/**
 * The interface used to execute SQL stored procedures.  The JDBC API 
 * provides a stored procedure SQL escape syntax that allows stored procedures 
 * to be called in a standard way for all RDBMSs. This escape syntax has one 
 * form that includes a result parameter and one that does not. If used, the result 
 * parameter must be registered as an OUT parameter. The other parameters
 * can be used for input, output or both. Parameters are referred to 
 * sequentially, by number, with the first parameter being 1.
 * <PRE>
 *   {?= call &lt;procedure-name&gt;[&lt;arg1&gt;,&lt;arg2&gt;, ...]}
 *   {call &lt;procedure-name&gt;[&lt;arg1&gt;,&lt;arg2&gt;, ...]}
 * </PRE>
 * <P>
 * IN parameter values are set using the <code>set</code> methods inherited from
 * {@link PreparedStatement}.  The type of all OUT parameters must be
 * registered prior to executing the stored procedure; their values
 * are retrieved after execution via the <code>get</code> methods provided here.
 * <P>
 * A <code>CallableStatement</code> can return one {@link ResultSet} object or 
 * multiple <code>ResultSet</code> objects.  Multiple 
 * <code>ResultSet</code> objects are handled using operations
 * inherited from {@link Statement}.
 * <P>
 * For maximum portability, a call's <code>ResultSet</code> objects and 
 * update counts should be processed prior to getting the values of output
 * parameters.
 * <P>
 *
 * @see Connection#prepareCall
 * @see ResultSet 
 */

public class JdbcCallableStatement extends AbstractJdbcCallableStatement implements CallableStatement {
  public JdbcCallableStatement(CallableStatement cs){
		super(cs);
	}
	
  public RowId getRowId(int parameterIndex) throws SQLException {
    return getCallableStatement().getRowId(parameterIndex);
  }

  public RowId getRowId(String parameterName) throws SQLException {
      return getCallableStatement().getRowId(parameterName);
  }

  public void setRowId(String parameterName, RowId x) throws SQLException {
      getCallableStatement().setRowId(parameterName, x);
  }

  public void setNString(String parameterName, String value) throws SQLException {
      getCallableStatement().setNString(parameterName, value);
  }

  public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
      getCallableStatement().setNCharacterStream(parameterName, value, length);
  }

  public void setNClob(String parameterName, NClob value) throws SQLException {
      getCallableStatement().setNClob(parameterName, value);
  }

  public void setClob(String parameterName, Reader reader, long length) throws SQLException {
      getCallableStatement().setClob(parameterName, reader, length);
  }

  public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
      getCallableStatement().setBlob(parameterName, inputStream, length);
  }

  public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
      getCallableStatement().setNClob(parameterName, reader, length);
  }

  public NClob getNClob(int parameterIndex) throws SQLException {
      return getCallableStatement().getNClob(parameterIndex);
  }

  public NClob getNClob(String parameterName) throws SQLException {
      return getCallableStatement().getNClob(parameterName);
  }

  public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
      getCallableStatement().setSQLXML(parameterName, xmlObject);
  }

  public SQLXML getSQLXML(int parameterIndex) throws SQLException {
      return getCallableStatement().getSQLXML(parameterIndex);
  }

  public SQLXML getSQLXML(String parameterName) throws SQLException {
      return getCallableStatement().getSQLXML(parameterName);
  }

  public String getNString(int parameterIndex) throws SQLException {
      return getCallableStatement().getNString(parameterIndex);
  }

  public String getNString(String parameterName) throws SQLException {
      return getCallableStatement().getNString(parameterName);
  }

  public Reader getNCharacterStream(int parameterIndex) throws SQLException {
      return getCallableStatement().getNCharacterStream(parameterIndex);
  }

  public Reader getNCharacterStream(String parameterName) throws SQLException {
      return getCallableStatement().getNCharacterStream(parameterName);
  }

  public Reader getCharacterStream(int parameterIndex) throws SQLException {
      return getCallableStatement().getCharacterStream(parameterIndex);
  }

  public Reader getCharacterStream(String parameterName) throws SQLException {
      return getCallableStatement().getCharacterStream(parameterName);
  }

  public void setBlob(String parameterName, Blob x) throws SQLException {
      getCallableStatement().setBlob(parameterName, x);
  }

  public void setClob(String parameterName, Clob x) throws SQLException {
      getCallableStatement().setClob(parameterName, x);
  }

  public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
      getCallableStatement().setAsciiStream(parameterName, x, length);
  }

  public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
      getCallableStatement().setBinaryStream(parameterName, x, length);
  }

  public void setCharacterStream(String parameterName, Reader reader, long length) throws SQLException {
      getCallableStatement().setCharacterStream(parameterName, reader, length);
  }

  public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
      getCallableStatement().setAsciiStream(parameterName, x);
  }

  public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
      getCallableStatement().setBinaryStream(parameterName, x);
  }

  public void setCharacterStream(String parameterName, Reader reader) throws SQLException {
      getCallableStatement().setCharacterStream(parameterName, reader);
  }

  public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
      getCallableStatement().setNCharacterStream(parameterName, value);
  }

  public void setClob(String parameterName, Reader reader) throws SQLException {
      getCallableStatement().setClob(parameterName, reader);
  }

  public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
      getCallableStatement().setBlob(parameterName, inputStream);
  }

  public void setNClob(String parameterName, Reader reader) throws SQLException {
      getCallableStatement().setNClob(parameterName, reader);
  }
}




       
