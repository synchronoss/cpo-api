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
//import java.sql.NClob;
//import java.sql.RowId;
//import java.sql.SQLXML;

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

public class JdbcPreparedStatement implements PreparedStatement {
	
	private PreparedStatement ps_ = null;
	
	@SuppressWarnings("unused")
  private JdbcPreparedStatement(){
		
	}
	
	public JdbcPreparedStatement(PreparedStatement pstmt){
		ps_=pstmt;
	}
	

    /**
     * Executes the given SQL statement, which returns a single 
     * <code>ResultSet</code> object.
     *
     * @param sql an SQL statement to be sent to the database, typically a 
     *        static SQL <code>SELECT</code> statement
     * @return a <code>ResultSet</code> object that contains the data produced 
     *         by the given query; never <code>null</code> 
     * @exception SQLException if a database access error occurs or the given
     *            SQL statement produces anything other than a single
     *            <code>ResultSet</code> object
     */
	public ResultSet executeQuery(String sql) throws SQLException{
    	return getPreparedStatement().executeQuery(sql);
    }

    /**
     * Executes the given SQL statement, which may be an <code>INSERT</code>, 
     * <code>UPDATE</code>, or <code>DELETE</code> statement or an
     * SQL statement that returns nothing, such as an SQL DDL statement.
     *
     * @param sql an SQL <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code> statement or an SQL statement that returns nothing
     * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
     * or <code>DELETE</code> statements, or <code>0</code> for SQL statements 
     * that return nothing
     * @exception SQLException if a database access error occurs or the given
     *            SQL statement produces a <code>ResultSet</code> object
     */
    public int executeUpdate(String sql) throws SQLException{
    	return getPreparedStatement().executeUpdate(sql);
    }

    /**
     * Releases this <code>Statement</code> object's database 
     * and JDBC resources immediately instead of waiting for
     * this to happen when it is automatically closed.
     * It is generally good practice to release resources as soon as
     * you are finished with them to avoid tying up database
     * resources.
     * <P>
     * Calling the method <code>close</code> on a <code>Statement</code>
     * object that is already closed has no effect.
     * <P>
     * <B>Note:</B> A <code>Statement</code> object is automatically closed 
     * when it is garbage collected. When a <code>Statement</code> object is 
     * closed, its current <code>ResultSet</code> object, if one exists, is 
     * also closed.  
     *
     * @exception SQLException if a database access error occurs
     */
    public void close() throws SQLException{
    	ps_ = null;
    }

    //----------------------------------------------------------------------

    /**
     * Retrieves the maximum number of bytes that can be
     * returned for character and binary column values in a <code>ResultSet</code> 
     * object produced by this <code>Statement</code> object.
     * This limit applies only to <code>BINARY</code>,
     * <code>VARBINARY</code>, <code>LONGVARBINARY</code>, <code>CHAR</code>,
     * <code>VARCHAR</code>, and <code>LONGVARCHAR</code>
     * columns.  If the limit is exceeded, the excess data is silently
     * discarded.
     *
     * @return the current column size limit for columns storing character and 
     *         binary values; zero means there is no limit 
     * @exception SQLException if a database access error occurs
     * @see #setMaxFieldSize
     */
    public int getMaxFieldSize() throws SQLException{
    	return getPreparedStatement().getMaxFieldSize();
    }
    
    /**
     * Sets the limit for the maximum number of bytes in a <code>ResultSet</code>
     * column storing character or binary values to
     * the given number of bytes.  This limit applies
     * only to <code>BINARY</code>, <code>VARBINARY</code>,
     * <code>LONGVARBINARY</code>, <code>CHAR</code>, <code>VARCHAR</code>, and
     * <code>LONGVARCHAR</code> fields.  If the limit is exceeded, the excess data
     * is silently discarded. For maximum portability, use values
     * greater than 256.
     *
     * @param max the new column size limit in bytes; zero means there is no limit 
     * @exception SQLException if a database access error occurs 
     *            or the condition max >= 0 is not satisfied
     * @see #getMaxFieldSize
     */
    public void setMaxFieldSize(int max) throws SQLException{
    	getPreparedStatement().setMaxFieldSize(max);
    }

    /**
     * Retrieves the maximum number of rows that a
     * <code>ResultSet</code> object produced by this
     * <code>Statement</code> object can contain.  If this limit is exceeded, 
     * the excess rows are silently dropped.
     *
     * @return the current maximum number of rows for a <code>ResultSet</code>
     *         object produced by this <code>Statement</code> object; 
     *         zero means there is no limit
     * @exception SQLException if a database access error occurs
     * @see #setMaxRows
     */
    public int getMaxRows() throws SQLException{
    	return getPreparedStatement().getMaxRows();
    }

    /**
     * Sets the limit for the maximum number of rows that any
     * <code>ResultSet</code> object can contain to the given number.
     * If the limit is exceeded, the excess
     * rows are silently dropped.
     *
     * @param max the new max rows limit; zero means there is no limit 
     * @exception SQLException if a database access error occurs
     *            or the condition max >= 0 is not satisfied
     * @see #getMaxRows
     */
    public void setMaxRows(int max) throws SQLException{
    	getPreparedStatement().setMaxRows(max);
    }

    /**
     * Sets escape processing on or off.
     * If escape scanning is on (the default), the driver will do
     * escape substitution before sending the SQL statement to the database.
     *
     * Note: Since prepared statements have usually been parsed prior
     * to making this call, disabling escape processing for 
     * <code>PreparedStatements</code> objects will have no effect.
     *
     * @param enable <code>true</code> to enable escape processing;
     *       <code>false</code> to disable it
     * @exception SQLException if a database access error occurs
     */
    public void setEscapeProcessing(boolean enable) throws SQLException{
    	getPreparedStatement().setEscapeProcessing(enable);
    }

    /**
     * Retrieves the number of seconds the driver will
     * wait for a <code>Statement</code> object to execute. If the limit is exceeded, a
     * <code>SQLException</code> is thrown.
     *
     * @return the current query timeout limit in seconds; zero means there is 
     *         no limit 
     * @exception SQLException if a database access error occurs
     * @see #setQueryTimeout
     */
    public int getQueryTimeout() throws SQLException{
    	return getPreparedStatement().getQueryTimeout();
    }

    /**
     * Sets the number of seconds the driver will wait for a 
     * <code>Statement</code> object to execute to the given number of seconds.
     * If the limit is exceeded, an <code>SQLException</code> is thrown.
     *
     * @param seconds the new query timeout limit in seconds; zero means 
     *        there is no limit
     * @exception SQLException if a database access error occurs
     *            or the condition seconds >= 0 is not satisfied
     * @see #getQueryTimeout
     */
    public void setQueryTimeout(int seconds) throws SQLException{
    	getPreparedStatement().setQueryTimeout(seconds);
    }

    /**
     * Cancels this <code>Statement</code> object if both the DBMS and
     * driver support aborting an SQL statement.
     * This method can be used by one thread to cancel a statement that
     * is being executed by another thread.
     *
     * @exception SQLException if a database access error occurs
     */
    public void cancel() throws SQLException{
    	getPreparedStatement().cancel();
    }

    /**
     * Retrieves the first warning reported by calls on this <code>Statement</code> object.
     * Subsequent <code>Statement</code> object warnings will be chained to this
     * <code>SQLWarning</code> object.
     *
     * <p>The warning chain is automatically cleared each time
     * a statement is (re)executed. This method may not be called on a closed
     * <code>Statement</code> object; doing so will cause an <code>SQLException</code>
     * to be thrown.
     *
     * <P><B>Note:</B> If you are processing a <code>ResultSet</code> object, any
     * warnings associated with reads on that <code>ResultSet</code> object 
     * will be chained on it rather than on the <code>Statement</code>
     * object that produced it.
     *
     * @return the first <code>SQLWarning</code> object or <code>null</code> 
     *         if there are no warnings
     * @exception SQLException if a database access error occurs or this 
     *            method is called on a closed statement
     */
    public SQLWarning getWarnings() throws SQLException{
    	return getPreparedStatement().getWarnings();
    }

    /**
     * Clears all the warnings reported on this <code>Statement</code>
     * object. After a call to this method,
     * the method <code>getWarnings</code> will return 
     * <code>null</code> until a new warning is reported for this
     * <code>Statement</code> object.  
     *
     * @exception SQLException if a database access error occurs
     */
    public void clearWarnings() throws SQLException{
    	getPreparedStatement().clearWarnings();
    }

    /**
     * Sets the SQL cursor name to the given <code>String</code>, which 
     * will be used by subsequent <code>Statement</code> object 
     * <code>execute</code> methods. This name can then be
     * used in SQL positioned update or delete statements to identify the
     * current row in the <code>ResultSet</code> object generated by this 
     * statement.  If the database does not support positioned update/delete, 
     * this method is a noop.  To insure that a cursor has the proper isolation
     * level to support updates, the cursor's <code>SELECT</code> statement 
     * should have the form <code>SELECT FOR UPDATE</code>.  If 
     * <code>FOR UPDATE</code> is not present, positioned updates may fail.
     *
     * <P><B>Note:</B> By definition, the execution of positioned updates and
     * deletes must be done by a different <code>Statement</code> object than 
     * the one that generated the <code>ResultSet</code> object being used for 
     * positioning. Also, cursor names must be unique within a connection.
     *
     * @param name the new cursor name, which must be unique within
     *             a connection
     * @exception SQLException if a database access error occurs
     */
    public void setCursorName(String name) throws SQLException{
    	getPreparedStatement().setCursorName(name);
    }
	
    //----------------------- Multiple Results --------------------------

    /**
     * Executes the given SQL statement, which may return multiple results.
     * In some (uncommon) situations, a single SQL statement may return
     * multiple result sets and/or update counts.  Normally you can ignore
     * this unless you are (1) executing a stored procedure that you know may
     * return multiple results or (2) you are dynamically executing an
     * unknown SQL string.  
     * <P>
     * The <code>execute</code> method executes an SQL statement and indicates the
     * form of the first result.  You must then use the methods 
     * <code>getResultSet</code> or <code>getUpdateCount</code>
     * to retrieve the result, and <code>getMoreResults</code> to
     * move to any subsequent result(s).
     *
     * @param sql any SQL statement
     * @return <code>true</code> if the first result is a <code>ResultSet</code> 
     *         object; <code>false</code> if it is an update count or there are 
     *         no results
     * @exception SQLException if a database access error occurs
     * @see #getResultSet
     * @see #getUpdateCount
     * @see #getMoreResults 
     */
    public boolean execute(String sql) throws SQLException{
    	return getPreparedStatement().execute(sql);
    }
	
    /**
     *  Retrieves the current result as a <code>ResultSet</code> object. 
     *  This method should be called only once per result.
     *
     * @return the current result as a <code>ResultSet</code> object or
     * <code>null</code> if the result is an update count or there are no more results
     * @exception SQLException if a database access error occurs
     * @see #execute 
     */
    public ResultSet getResultSet() throws SQLException{
    	return getPreparedStatement().getResultSet();
    } 

    /**
     *  Retrieves the current result as an update count;
     *  if the result is a <code>ResultSet</code> object or there are no more results, -1
     *  is returned. This method should be called only once per result.
     * 
     * @return the current result as an update count; -1 if the current result is a
     * <code>ResultSet</code> object or there are no more results
     * @exception SQLException if a database access error occurs
     * @see #execute 
     */
    public int getUpdateCount() throws SQLException{
    	return getPreparedStatement().getUpdateCount();
    } 

    /**
     * Moves to this <code>Statement</code> object's next result, returns
     * <code>true</code> if it is a <code>ResultSet</code> object, and
     * implicitly closes any current <code>ResultSet</code>
     * object(s) obtained with the method <code>getResultSet</code>.
     *
     * <P>There are no more results when the following is true:
     * <PRE>
     *     // stmt is a Statement object
     *     ((stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1))
     * </PRE>
     *
     * @return <code>true</code> if the next result is a <code>ResultSet</code>
     *         object; <code>false</code> if it is an update count or there are 
     *         no more results
     * @exception SQLException if a database access error occurs
     * @see #execute 
     */
    public boolean getMoreResults() throws SQLException{
    	return getPreparedStatement().getMoreResults();
    } 


    //--------------------------JDBC 2.0-----------------------------


    /**
     * Gives the driver a hint as to the direction in which
     * rows will be processed in <code>ResultSet</code>
     * objects created using this <code>Statement</code> object.  The 
     * default value is <code>ResultSet.FETCH_FORWARD</code>.
     * <P>
     * Note that this method sets the default fetch direction for 
     * result sets generated by this <code>Statement</code> object.
     * Each result set has its own methods for getting and setting
     * its own fetch direction.
     *
     * @param direction the initial direction for processing rows
     * @exception SQLException if a database access error occurs
     * or the given direction
     * is not one of <code>ResultSet.FETCH_FORWARD</code>,
     * <code>ResultSet.FETCH_REVERSE</code>, or <code>ResultSet.FETCH_UNKNOWN</code>
     * @since 1.2
     * @see #getFetchDirection
     */
    public void setFetchDirection(int direction) throws SQLException{
    	getPreparedStatement().setFetchDirection(direction);
    }

    /**
     * Retrieves the direction for fetching rows from
     * database tables that is the default for result sets
     * generated from this <code>Statement</code> object.
     * If this <code>Statement</code> object has not set
     * a fetch direction by calling the method <code>setFetchDirection</code>,
     * the return value is implementation-specific.
     *
     * @return the default fetch direction for result sets generated
     *          from this <code>Statement</code> object
     * @exception SQLException if a database access error occurs
     * @since 1.2
     * @see #setFetchDirection
     */
    public int getFetchDirection() throws SQLException{
    	return getPreparedStatement().getFetchDirection();
    }

    /**
     * Gives the JDBC driver a hint as to the number of rows that should 
     * be fetched from the database when more rows are needed.  The number 
     * of rows specified affects only result sets created using this 
     * statement. If the value specified is zero, then the hint is ignored.
     * The default value is zero.
     *
     * @param rows the number of rows to fetch
     * @exception SQLException if a database access error occurs, or the
     *        condition 0 <= <code>rows</code> <= <code>this.getMaxRows()</code> 
     *        is not satisfied.
     * @since 1.2
     * @see #getFetchSize
     */
    public void setFetchSize(int rows) throws SQLException{
    	getPreparedStatement().setFetchSize(rows);
    }
  
    /**
     * Retrieves the number of result set rows that is the default 
     * fetch size for <code>ResultSet</code> objects
     * generated from this <code>Statement</code> object.
     * If this <code>Statement</code> object has not set
     * a fetch size by calling the method <code>setFetchSize</code>,
     * the return value is implementation-specific.
     *
     * @return the default fetch size for result sets generated
     *          from this <code>Statement</code> object
     * @exception SQLException if a database access error occurs
     * @since 1.2
     * @see #setFetchSize
     */
    public int getFetchSize() throws SQLException{
    	return getPreparedStatement().getFetchSize();
    }

    /**
     * Retrieves the result set concurrency for <code>ResultSet</code> objects
     * generated by this <code>Statement</code> object.
     *
     * @return either <code>ResultSet.CONCUR_READ_ONLY</code> or
     * <code>ResultSet.CONCUR_UPDATABLE</code>
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public int getResultSetConcurrency() throws SQLException{
    	return getPreparedStatement().getResultSetConcurrency();
    }

    /**
     * Retrieves the result set type for <code>ResultSet</code> objects
     * generated by this <code>Statement</code> object.
     *
     * @return one of <code>ResultSet.TYPE_FORWARD_ONLY</code>,
     * <code>ResultSet.TYPE_SCROLL_INSENSITIVE</code>, or	
     * <code>ResultSet.TYPE_SCROLL_SENSITIVE</code>
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public int getResultSetType()  throws SQLException{
    	return getPreparedStatement().getResultSetType();
    }

    /**
     * Adds the given SQL command to the current list of commmands for this
     * <code>Statement</code> object. The commands in this list can be
     * executed as a batch by calling the method <code>executeBatch</code>.
     * <P>
     * <B>NOTE:</B>  This method is optional.
     *
     * @param sql typically this is a static SQL <code>INSERT</code> or 
     * <code>UPDATE</code> statement
     * @exception SQLException if a database access error occurs, or the
     * driver does not support batch updates
     * @see #executeBatch
     * @since 1.2
     */
    public void addBatch( String sql ) throws SQLException{
    	getPreparedStatement().addBatch( sql );
    }

    /**
     * Empties this <code>Statement</code> object's current list of 
     * SQL commands.
     * <P>
     * <B>NOTE:</B>  This method is optional.
     *
     * @exception SQLException if a database access error occurs or the
     * driver does not support batch updates
     * @see #addBatch
     * @since 1.2
     */
    public void clearBatch() throws SQLException{
    	getPreparedStatement().clearBatch();
    }

    /**
     * Submits a batch of commands to the database for execution and
     * if all commands execute successfully, returns an array of update counts.
     * The <code>int</code> elements of the array that is returned are ordered
     * to correspond to the commands in the batch, which are ordered 
     * according to the order in which they were added to the batch.
     * The elements in the array returned by the method <code>executeBatch</code>
     * may be one of the following:
     * <OL>
     * <LI>A number greater than or equal to zero -- indicates that the
     * command was processed successfully and is an update count giving the
     * number of rows in the database that were affected by the command's
     * execution
     * <LI>A value of <code>SUCCESS_NO_INFO</code> -- indicates that the command was
     * processed successfully but that the number of rows affected is
     * unknown
     * <P> 
     * If one of the commands in a batch update fails to execute properly,
     * this method throws a <code>BatchUpdateException</code>, and a JDBC
     * driver may or may not continue to process the remaining commands in
     * the batch.  However, the driver's behavior must be consistent with a
     * particular DBMS, either always continuing to process commands or never
     * continuing to process commands.  If the driver continues processing
     * after a failure, the array returned by the method
     * <code>BatchUpdateException.getUpdateCounts</code>
     * will contain as many elements as there are commands in the batch, and
     * at least one of the elements will be the following:
     * <P> 
     * <LI>A value of <code>EXECUTE_FAILED</code> -- indicates that the command failed
     * to execute successfully and occurs only if a driver continues to
     * process commands after a command fails
     * </OL>
     * <P>
     * A driver is not required to implement this method.
     * The possible implementations and return values have been modified in
     * the Java 2 SDK, Standard Edition, version 1.3 to
     * accommodate the option of continuing to proccess commands in a batch
     * update after a <code>BatchUpdateException</code> obejct has been thrown.
     *
     * @return an array of update counts containing one element for each
     * command in the batch.  The elements of the array are ordered according 
     * to the order in which commands were added to the batch.
     * @exception SQLException if a database access error occurs or the
     * driver does not support batch statements. Throws {@link BatchUpdateException}
     * (a subclass of <code>SQLException</code>) if one of the commands sent to the
     * database fails to execute properly or attempts to return a result set.
     * @since 1.3
     */
    public int[] executeBatch() throws SQLException{
    	return getPreparedStatement().executeBatch();
    }

    /**
     * Retrieves the <code>Connection</code> object
     * that produced this <code>Statement</code> object.
     * @return the connection that produced this statement
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public Connection getConnection()  throws SQLException{
    	return getPreparedStatement().getConnection();
    }


    /**
     * Moves to this <code>Statement</code> object's next result, deals with
     * any current <code>ResultSet</code> object(s) according  to the instructions
     * specified by the given flag, and returns
     * <code>true</code> if the next result is a <code>ResultSet</code> object.
     *
     * <P>There are no more results when the following is true:
     * <PRE>
     *     // stmt is a Statement object
     *     ((stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1))
     * </PRE>
     *
     * @param current one of the following <code>Statement</code>
     *        constants indicating what should happen to current 
     *        <code>ResultSet</code> objects obtained using the method
     *        <code>getResultSet</code>:
     *        <code>Statement.CLOSE_CURRENT_RESULT</code>, 
     *        <code>Statement.KEEP_CURRENT_RESULT</code>, or
     *        <code>Statement.CLOSE_ALL_RESULTS</code>
     * @return <code>true</code> if the next result is a <code>ResultSet</code> 
     *         object; <code>false</code> if it is an update count or there are no 
     *         more results
     * @exception SQLException if a database access error occurs or the argument
	 *         supplied is not one of the following:
     *        <code>Statement.CLOSE_CURRENT_RESULT</code>, 
     *        <code>Statement.KEEP_CURRENT_RESULT</code>, or
     *        <code>Statement.CLOSE_ALL_RESULTS</code>
     * @since 1.4
     * @see #execute
     */
    public boolean getMoreResults(int current) throws SQLException{
    	return getPreparedStatement().getMoreResults(current);
    }

    /**
     * Retrieves any auto-generated keys created as a result of executing this
     * <code>Statement</code> object. If this <code>Statement</code> object did 
     * not generate any keys, an empty <code>ResultSet</code>
     * object is returned.
     *
     * @return a <code>ResultSet</code> object containing the auto-generated key(s) 
     *         generated by the execution of this <code>Statement</code> object
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */
    public ResultSet getGeneratedKeys() throws SQLException{
    	return getPreparedStatement().getGeneratedKeys();
    }

    /**
     * Executes the given SQL statement and signals the driver with the
     * given flag about whether the
     * auto-generated keys produced by this <code>Statement</code> object
     * should be made available for retrieval. 
     *
     * @param sql must be an SQL <code>INSERT</code>, <code>UPDATE</code> or
     *        <code>DELETE</code> statement or an SQL statement that 
     *        returns nothing
     * @param autoGeneratedKeys a flag indicating whether auto-generated keys
     *        should be made available for retrieval;
     *         one of the following constants:
     *         <code>Statement.RETURN_GENERATED_KEYS</code>
     *         <code>Statement.NO_GENERATED_KEYS</code>
     * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>
     *         or <code>DELETE</code> statements, or <code>0</code> for SQL 
     *         statements that return nothing
     * @exception SQLException if a database access error occurs, the given
     *            SQL statement returns a <code>ResultSet</code> object, or
     *            the given constant is not one of those allowed
     * @since 1.4
     */
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException{
    	return getPreparedStatement().executeUpdate(sql, autoGeneratedKeys);
    }

    /**
     * Executes the given SQL statement and signals the driver that the
     * auto-generated keys indicated in the given array should be made available
     * for retrieval.  The driver will ignore the array if the SQL statement
     * is not an <code>INSERT</code> statement.
     *
     * @param sql an SQL <code>INSERT</code>, <code>UPDATE</code> or
     *        <code>DELETE</code> statement or an SQL statement that returns nothing,
     *        such as an SQL DDL statement
     * @param columnIndexes an array of column indexes indicating the columns
     *        that should be returned from the inserted row
     * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
     *         or <code>DELETE</code> statements, or 0 for SQL statements 
     *         that return nothing
     * @exception SQLException if a database access error occurs, the SQL
     *            statement returns a <code>ResultSet</code> object, or the
	 *            second argument supplied to this method is not an <code>int</code> array
	 *            whose elements are valid column indexes 
     * @since 1.4
     */
    public int executeUpdate(String sql, int columnIndexes[]) throws SQLException{
    	return getPreparedStatement().executeUpdate(sql, columnIndexes);
    }

    /**
     * Executes the given SQL statement and signals the driver that the
     * auto-generated keys indicated in the given array should be made available
     * for retrieval.  The driver will ignore the array if the SQL statement
     * is not an <code>INSERT</code> statement.
     *
     * @param sql an SQL <code>INSERT</code>, <code>UPDATE</code> or
     *        <code>DELETE</code> statement or an SQL statement that returns nothing
     * @param columnNames an array of the names of the columns that should be 
     *        returned from the inserted row
     * @return either the row count for <code>INSERT</code>, <code>UPDATE</code>,
     *         or <code>DELETE</code> statements, or 0 for SQL statements 
     *         that return nothing
     * @exception SQLException if a database access error occurs, the SQL
     *            statement returns a <code>ResultSet</code> object, or the
	 *            second argument supplied to this method is not a <code>String</code> array
     *            whose elements are valid column names
     *
     * @since 1.4
     */
    public int executeUpdate(String sql, String columnNames[]) throws SQLException{
    	return getPreparedStatement().executeUpdate(sql, columnNames);
    }

    /**
     * Executes the given SQL statement, which may return multiple results,
     * and signals the driver that any
     * auto-generated keys should be made available
     * for retrieval.  The driver will ignore this signal if the SQL statement
     * is not an <code>INSERT</code> statement.
     * <P>
     * In some (uncommon) situations, a single SQL statement may return
     * multiple result sets and/or update counts.  Normally you can ignore
     * this unless you are (1) executing a stored procedure that you know may
     * return multiple results or (2) you are dynamically executing an
     * unknown SQL string.  
     * <P>
     * The <code>execute</code> method executes an SQL statement and indicates the
     * form of the first result.  You must then use the methods 
     * <code>getResultSet</code> or <code>getUpdateCount</code>
     * to retrieve the result, and <code>getMoreResults</code> to
     * move to any subsequent result(s).
     *
     * @param sql any SQL statement
     * @param autoGeneratedKeys a constant indicating whether auto-generated 
     *        keys should be made available for retrieval using the method
     *        <code>getGeneratedKeys</code>; one of the following constants:
     *        <code>Statement.RETURN_GENERATED_KEYS</code> or
     *	      <code>Statement.NO_GENERATED_KEYS</code>
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     *         object; <code>false</code> if it is an update count or there are
     *         no results
     * @exception SQLException if a database access error occurs or the second 
	 *         parameter supplied to this method is not 
     *         <code>Statement.RETURN_GENERATED_KEYS</code> or
	 *         <code>Statement.NO_GENERATED_KEYS</code>.
     * @see #getResultSet
     * @see #getUpdateCount
     * @see #getMoreResults
     * @see #getGeneratedKeys
     *
     * @since 1.4 
     */
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException{
    	return getPreparedStatement().execute(sql, autoGeneratedKeys);
    }

    /**
     * Executes the given SQL statement, which may return multiple results,
     * and signals the driver that the
     * auto-generated keys indicated in the given array should be made available
     * for retrieval.  This array contains the indexes of the columns in the 
     * target table that contain the auto-generated keys that should be made
     * available. The driver will ignore the array if the given SQL statement
     * is not an <code>INSERT</code> statement.
     * <P>
     * Under some (uncommon) situations, a single SQL statement may return
     * multiple result sets and/or update counts.  Normally you can ignore
     * this unless you are (1) executing a stored procedure that you know may
     * return multiple results or (2) you are dynamically executing an
     * unknown SQL string.  
     * <P>
     * The <code>execute</code> method executes an SQL statement and indicates the
     * form of the first result.  You must then use the methods 
     * <code>getResultSet</code> or <code>getUpdateCount</code>
     * to retrieve the result, and <code>getMoreResults</code> to
     * move to any subsequent result(s).
     *
     * @param sql any SQL statement
     * @param columnIndexes an array of the indexes of the columns in the 
     *        inserted row that should be  made available for retrieval by a
     *        call to the method <code>getGeneratedKeys</code>
     * @return <code>true</code> if the first result is a <code>ResultSet</code> 
     *         object; <code>false</code> if it is an update count or there 
     *         are no results
     * @exception SQLException if a database access error occurs or the 
     *            elements in the <code>int</code> array passed to this method
     *            are not valid column indexes
     * @see #getResultSet
     * @see #getUpdateCount
     * @see #getMoreResults
     *
     * @since 1.4
     */
    public boolean execute(String sql, int columnIndexes[]) throws SQLException{
    	return getPreparedStatement().execute(sql, columnIndexes);
    }

    /**
     * Executes the given SQL statement, which may return multiple results,
     * and signals the driver that the
     * auto-generated keys indicated in the given array should be made available
     * for retrieval. This array contains the names of the columns in the 
     * target table that contain the auto-generated keys that should be made
     * available. The driver will ignore the array if the given SQL statement
     * is not an <code>INSERT</code> statement.
     * <P>
     * In some (uncommon) situations, a single SQL statement may return
     * multiple result sets and/or update counts.  Normally you can ignore
     * this unless you are (1) executing a stored procedure that you know may
     * return multiple results or (2) you are dynamically executing an
     * unknown SQL string.  
     * <P>
     * The <code>execute</code> method executes an SQL statement and indicates the
     * form of the first result.  You must then use the methods 
     * <code>getResultSet</code> or <code>getUpdateCount</code>
     * to retrieve the result, and <code>getMoreResults</code> to
     * move to any subsequent result(s).
     *
     * @param sql any SQL statement
     * @param columnNames an array of the names of the columns in the inserted
     *        row that should be made available for retrieval by a call to the
     *        method <code>getGeneratedKeys</code>
     * @return <code>true</code> if the next result is a <code>ResultSet</code> 
     *         object; <code>false</code> if it is an update count or there 
     *         are no more results
     * @exception SQLException if a database access error occurs or the 
	 *          elements of the <code>String</code> array passed to this
     *          method are not valid column names
     * @see #getResultSet
     * @see #getUpdateCount
     * @see #getMoreResults
     * @see #getGeneratedKeys
     *
     * @since 1.4 
     */
    public boolean execute(String sql, String columnNames[]) throws SQLException{
    	return getPreparedStatement().execute(sql, columnNames);
    }

   /**
     * Retrieves the result set holdability for <code>ResultSet</code> objects
     * generated by this <code>Statement</code> object.
     *
     * @return either <code>ResultSet.HOLD_CURSORS_OVER_COMMIT</code> or
     *         <code>ResultSet.CLOSE_CURSORS_AT_COMMIT</code>
     * @exception SQLException if a database access error occurs
     *
     * @since 1.4
     */
    public int getResultSetHoldability() throws SQLException{
    	return getPreparedStatement().getResultSetHoldability();
    }

    /**
     * Executes the SQL query in this <code>PreparedStatement</code> object
     * and returns the <code>ResultSet</code> object generated by the query.
     *
     * @return a <code>ResultSet</code> object that contains the data produced by the
     *         query; never <code>null</code>
     * @exception SQLException if a database access error occurs or the SQL
     *            statement does not return a <code>ResultSet</code> object
     */
	public ResultSet executeQuery() throws SQLException {
    	return getPreparedStatement().executeQuery();
    }

    /**
     * Executes the SQL statement in this <code>PreparedStatement</code> object,
     * which must be an SQL <code>INSERT</code>, <code>UPDATE</code> or
     * <code>DELETE</code> statement; or an SQL statement that returns nothing, 
     * such as a DDL statement.
     *
     * @return either (1) the row count for <code>INSERT</code>, <code>UPDATE</code>,
     *         or <code>DELETE</code> statements
     *         or (2) 0 for SQL statements that return nothing
     * @exception SQLException if a database access error occurs or the SQL
     *            statement returns a <code>ResultSet</code> object
     */
	public int executeUpdate() throws SQLException {
    	return getPreparedStatement().executeUpdate();
    }


    /**
     * Sets the designated parameter to SQL <code>NULL</code>.
     *
     * <P><B>Note:</B> You must specify the parameter's SQL type.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param sqlType the SQL type code defined in <code>java.sql.Types</code>
     * @exception SQLException if a database access error occurs
     */
	public void setNull(int parameterIndex, int sqlType) throws SQLException{
    	getPreparedStatement().setNull(parameterIndex, sqlType);
    }


    /**
     * Sets the designated parameter to the given Java <code>boolean</code> value.
     * The driver converts this
     * to an SQL <code>BIT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setBoolean(int parameterIndex, boolean x) throws SQLException{
    	getPreparedStatement().setBoolean(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java <code>byte</code> value.  
     * The driver converts this
     * to an SQL <code>TINYINT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setByte(int parameterIndex, byte x) throws SQLException{
    	getPreparedStatement().setByte(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java <code>short</code> value. 
     * The driver converts this
     * to an SQL <code>SMALLINT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setShort(int parameterIndex, short x) throws SQLException{
    	getPreparedStatement().setShort(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java <code>int</code> value.  
     * The driver converts this
     * to an SQL <code>INTEGER</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setInt(int parameterIndex, int x) throws SQLException{
    	getPreparedStatement().setInt(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java <code>long</code> value. 
     * The driver converts this
     * to an SQL <code>BIGINT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setLong(int parameterIndex, long x) throws SQLException{
    	getPreparedStatement().setLong(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java <code>float</code> value. 
     * The driver converts this
     * to an SQL <code>FLOAT</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setFloat(int parameterIndex, float x) throws SQLException{
    	getPreparedStatement().setFloat(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java <code>double</code> value.  
     * The driver converts this
     * to an SQL <code>DOUBLE</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setDouble(int parameterIndex, double x) throws SQLException{
    	getPreparedStatement().setDouble(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given <code>java.math.BigDecimal</code> value.  
     * The driver converts this to an SQL <code>NUMERIC</code> value when
     * it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException{
    	getPreparedStatement().setBigDecimal(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java <code>String</code> value. 
     * The driver converts this
     * to an SQL <code>VARCHAR</code> or <code>LONGVARCHAR</code> value
     * (depending on the argument's
     * size relative to the driver's limits on <code>VARCHAR</code> values)
     * when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setString(int parameterIndex, String x) throws SQLException{
    	getPreparedStatement().setString(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given Java array of bytes.  The driver converts
     * this to an SQL <code>VARBINARY</code> or <code>LONGVARBINARY</code>
     * (depending on the argument's size relative to the driver's limits on
     * <code>VARBINARY</code> values) when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value 
     * @exception SQLException if a database access error occurs
     */
	public void setBytes(int parameterIndex, byte x[]) throws SQLException{
    	getPreparedStatement().setBytes(parameterIndex, x);
    }


    /**
     * Sets the designated parameter to the given <code>java.sql.Date</code> value.  
     * The driver converts this
     * to an SQL <code>DATE</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setDate(int parameterIndex, java.sql.Date x)
	    throws SQLException{
	    	getPreparedStatement().setDate(parameterIndex, x);
	    }


    /**
     * Sets the designated parameter to the given <code>java.sql.Time</code> value.  
     * The driver converts this
     * to an SQL <code>TIME</code> value when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @exception SQLException if a database access error occurs
     */
	public void setTime(int parameterIndex, java.sql.Time x) 
	    throws SQLException{
	    	getPreparedStatement().setTime(parameterIndex, x);
	    }


    /**
     * Sets the designated parameter to the given <code>java.sql.Timestamp</code> value.  
     * The driver
     * converts this to an SQL <code>TIMESTAMP</code> value when it sends it to the
     * database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value 
     * @exception SQLException if a database access error occurs
     */
	public void setTimestamp(int parameterIndex, java.sql.Timestamp x)
	    throws SQLException{
	    	getPreparedStatement().setTimestamp(parameterIndex, x);
	    }


    /**
     * Sets the designated parameter to the given input stream, which will have 
     * the specified number of bytes.
     * When a very large ASCII value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code>. Data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from ASCII to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the Java input stream that contains the ASCII parameter value
     * @param length the number of bytes in the stream 
     * @exception SQLException if a database access error occurs
     */
	public void setAsciiStream(int parameterIndex, java.io.InputStream x, int length)
	    throws SQLException{
	    	getPreparedStatement().setAsciiStream(parameterIndex, x, length);
	    }


    /**
     * Sets the designated parameter to the given input stream, which 
     * will have the specified number of bytes. A Unicode character has
     * two bytes, with the first byte being the high byte, and the second
     * being the low byte.
     *
     * When a very large Unicode value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code> object. The data will be read from the 
     * stream as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from Unicode to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...  
     * @param x a <code>java.io.InputStream</code> object that contains the
     *        Unicode parameter value as two-byte Unicode characters
     * @param length the number of bytes in the stream 
     * @exception SQLException if a database access error occurs
     * @deprecated
     */
	public void setUnicodeStream(int parameterIndex, java.io.InputStream x, 
			  int length) throws SQLException {
		getPreparedStatement().setUnicodeStream(parameterIndex, x,length);
    }


    /**
     * Sets the designated parameter to the given input stream, which will have 
     * the specified number of bytes.
     * When a very large binary value is input to a <code>LONGVARBINARY</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.InputStream</code> object. The data will be read from the 
     * stream as needed until end-of-file is reached.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the java input stream which contains the binary parameter value
     * @param length the number of bytes in the stream 
     * @exception SQLException if a database access error occurs
     */
	public void setBinaryStream(int parameterIndex, java.io.InputStream  x, int length) throws SQLException {
    	getPreparedStatement().setBinaryStream(parameterIndex, x, length);
    }


    /**
     * Clears the current parameter values immediately.
     * <P>In general, parameter values remain in force for repeated use of a
     * statement. Setting a parameter value automatically clears its
     * previous value.  However, in some cases it is useful to immediately
     * release the resources used by the current parameter values; this can
     * be done by calling the method <code>clearParameters</code>.
     *
     * @exception SQLException if a database access error occurs
     */
	public void clearParameters() throws SQLException{
    	getPreparedStatement().clearParameters();
    }


    //----------------------------------------------------------------------
    // Advanced features:

    /**
     * <p>Sets the value of the designated parameter with the given object. The second
     * argument must be an object type; for integral values, the
     * <code>java.lang</code> equivalent objects should be used.
     *
     * <p>The given Java object will be converted to the given targetSqlType
     * before being sent to the database.
     *
     * If the object has a custom mapping (is of a class implementing the 
     * interface <code>SQLData</code>),
     * the JDBC driver should call the method <code>SQLData.writeSQL</code> to 
     * write it to the SQL data stream.
     * If, on the other hand, the object is of a class implementing
     * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>, 
     * or <code>Array</code>, the driver should pass it to the database as a 
     * value of the corresponding SQL type.
     *
     * <p>Note that this method may be used to pass database-specific
     * abstract data types. 
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the object containing the input parameter value
     * @param targetSqlType the SQL type (as defined in java.sql.Types) to be 
     * sent to the database. The scale argument may further qualify this type.
     * @param scale for java.sql.Types.DECIMAL or java.sql.Types.NUMERIC types,
     *          this is the number of digits after the decimal point.  For all other
     *          types, this value will be ignored.
     * @exception SQLException if a database access error occurs
     * @see Types 
     */
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale)
            throws SQLException{
            	getPreparedStatement().setObject(parameterIndex, x, targetSqlType, scale);
            }


   /**
    * Sets the value of the designated parameter with the given object.
    * This method is like the method <code>setObject</code>
    * above, except that it assumes a scale of zero.
    *
    * @param parameterIndex the first parameter is 1, the second is 2, ...
    * @param x the object containing the input parameter value
    * @param targetSqlType the SQL type (as defined in java.sql.Types) to be 
    *                      sent to the database
    * @exception SQLException if a database access error occurs
    */
    public void setObject(int parameterIndex, Object x, int targetSqlType) 
      throws SQLException {
    	getPreparedStatement().setObject(parameterIndex, x, targetSqlType);
      }


    /**
     * <p>Sets the value of the designated parameter using the given object. 
     * The second parameter must be of type <code>Object</code>; therefore, the
     * <code>java.lang</code> equivalent objects should be used for built-in types.
     *
     * <p>The JDBC specification specifies a standard mapping from
     * Java <code>Object</code> types to SQL types.  The given argument 
     * will be converted to the corresponding SQL type before being
     * sent to the database.
     *
     * <p>Note that this method may be used to pass datatabase-
     * specific abstract data types, by using a driver-specific Java
     * type.
     *
     * If the object is of a class implementing the interface <code>SQLData</code>,
     * the JDBC driver should call the method <code>SQLData.writeSQL</code>
     * to write it to the SQL data stream.
     * If, on the other hand, the object is of a class implementing
     * <code>Ref</code>, <code>Blob</code>, <code>Clob</code>, <code>Struct</code>, 
     * or <code>Array</code>, the driver should pass it to the database as a 
     * value of the corresponding SQL type.
     * <P>
     * This method throws an exception if there is an ambiguity, for example, if the
     * object is of a class implementing more than one of the interfaces named above.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the object containing the input parameter value 
     * @exception SQLException if a database access error occurs or the type 
     *            of the given object is ambiguous
     */
    public void setObject(int parameterIndex, Object x) throws SQLException{
    	getPreparedStatement().setObject(parameterIndex, x);
    }


    /**
     * Executes the SQL statement in this <code>PreparedStatement</code> object,
     * which may be any kind of SQL statement.
     * Some prepared statements return multiple results; the <code>execute</code>
     * method handles these complex statements as well as the simpler
     * form of statements handled by the methods <code>executeQuery</code>
     * and <code>executeUpdate</code>.
     * <P>
     * The <code>execute</code> method returns a <code>boolean</code> to
     * indicate the form of the first result.  You must call either the method
     * <code>getResultSet</code> or <code>getUpdateCount</code>
     * to retrieve the result; you must call <code>getMoreResults</code> to
     * move to any subsequent result(s).
     *
     * @return <code>true</code> if the first result is a <code>ResultSet</code>
     *         object; <code>false</code> if the first result is an update
     *         count or there is no result
     * @exception SQLException if a database access error occurs or an argument
     *            is supplied to this method
     * @see Statement#execute
     * @see Statement#getResultSet
     * @see Statement#getUpdateCount
     * @see Statement#getMoreResults

     */
    public boolean execute() throws SQLException{
    	return getPreparedStatement().execute();
    }


    //--------------------------JDBC 2.0-----------------------------

    /**
     * Adds a set of parameters to this <code>PreparedStatement</code>
     * object's batch of commands.
     * 
     * @exception SQLException if a database access error occurs
     * @see Statement#addBatch
     * @since 1.2
     */
    public void addBatch() throws SQLException{
    	getPreparedStatement().addBatch();
    }


    /**
     * Sets the designated parameter to the given <code>Reader</code>
     * object, which is the given number of characters long.
     * When a very large UNICODE value is input to a <code>LONGVARCHAR</code>
     * parameter, it may be more practical to send it via a
     * <code>java.io.Reader</code> object. The data will be read from the stream
     * as needed until end-of-file is reached.  The JDBC driver will
     * do any necessary conversion from UNICODE to the database char format.
     * 
     * <P><B>Note:</B> This stream object can either be a standard
     * Java stream object or your own subclass that implements the
     * standard interface.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param reader the <code>java.io.Reader</code> object that contains the 
     *        Unicode data
     * @param length the number of characters in the stream 
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setCharacterStream(int parameterIndex,
       			  java.io.Reader reader,
			  int length) throws SQLException{
    	getPreparedStatement().setCharacterStream(parameterIndex,reader,length);
    }


    /**
     * Sets the designated parameter to the given
     *  <code>REF(&lt;structured-type&gt;)</code> value.
     * The driver converts this to an SQL <code>REF</code> value when it
     * sends it to the database.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x an SQL <code>REF</code> value
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setRef (int i, Ref x) throws SQLException{
    	getPreparedStatement().setRef (i, x);
    }


    /**
     * Sets the designated parameter to the given <code>Blob</code> object.
     * The driver converts this to an SQL <code>BLOB</code> value when it
     * sends it to the database.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x a <code>Blob</code> object that maps an SQL <code>BLOB</code> value
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setBlob (int i, Blob x) throws SQLException{
    	getPreparedStatement().setBlob(i, x);
    }


    /**
     * Sets the designated parameter to the given <code>Clob</code> object.
     * The driver converts this to an SQL <code>CLOB</code> value when it
     * sends it to the database.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x a <code>Clob</code> object that maps an SQL <code>CLOB</code> value
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setClob (int i, Clob x) throws SQLException{
    	getPreparedStatement().setClob(i, x);
    }


    /**
     * Sets the designated parameter to the given <code>Array</code> object.
     * The driver converts this to an SQL <code>ARRAY</code> value when it
     * sends it to the database.
     *
     * @param i the first parameter is 1, the second is 2, ...
     * @param x an <code>Array</code> object that maps an SQL <code>ARRAY</code> value
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setArray (int i, Array x) throws SQLException{
    	getPreparedStatement().setArray(i, x);
    }


    /**
     * Retrieves a <code>ResultSetMetaData</code> object that contains
     * information about the columns of the <code>ResultSet</code> object
     * that will be returned when this <code>PreparedStatement</code> object 
     * is executed.
     * <P>
     * Because a <code>PreparedStatement</code> object is precompiled, it is
     * possible to know about the <code>ResultSet</code> object that it will
     * return without having to execute it.  Consequently, it is possible
     * to invoke the method <code>getMetaData</code> on a
     * <code>PreparedStatement</code> object rather than waiting to execute
     * it and then invoking the <code>ResultSet.getMetaData</code> method
     * on the <code>ResultSet</code> object that is returned.
     * <P>
     * <B>NOTE:</B> Using this method may be expensive for some drivers due
     * to the lack of underlying DBMS support.
     *
     * @return the description of a <code>ResultSet</code> object's columns or
     *         <code>null</code> if the driver cannot return a
     *         <code>ResultSetMetaData</code> object
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public ResultSetMetaData getMetaData() throws SQLException{
    	return getPreparedStatement().getMetaData();
    }


    /**
     * Sets the designated parameter to the given <code>java.sql.Date</code> value,
     * using the given <code>Calendar</code> object.  The driver uses
     * the <code>Calendar</code> object to construct an SQL <code>DATE</code> value,
     * which the driver then sends to the database.  With 
     * a <code>Calendar</code> object, the driver can calculate the date
     * taking into account a custom timezone.  If no
     * <code>Calendar</code> object is specified, the driver uses the default
     * timezone, which is that of the virtual machine running the application.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the date
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal)
	    throws SQLException{
	    	getPreparedStatement().setDate(parameterIndex, x, cal);
	}


    /**
     * Sets the designated parameter to the given <code>java.sql.Time</code> value,
     * using the given <code>Calendar</code> object.  The driver uses
     * the <code>Calendar</code> object to construct an SQL <code>TIME</code> value,
     * which the driver then sends to the database.  With 
     * a <code>Calendar</code> object, the driver can calculate the time
     * taking into account a custom timezone.  If no
     * <code>Calendar</code> object is specified, the driver uses the default
     * timezone, which is that of the virtual machine running the application.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the time
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setTime(int parameterIndex, java.sql.Time x, Calendar cal) 
	    throws SQLException{
	    	getPreparedStatement().setTime(parameterIndex, x, cal);
	    }


    /**
     * Sets the designated parameter to the given <code>java.sql.Timestamp</code> value,
     * using the given <code>Calendar</code> object.  The driver uses
     * the <code>Calendar</code> object to construct an SQL <code>TIMESTAMP</code> value,
     * which the driver then sends to the database.  With a
     *  <code>Calendar</code> object, the driver can calculate the timestamp
     * taking into account a custom timezone.  If no
     * <code>Calendar</code> object is specified, the driver uses the default
     * timezone, which is that of the virtual machine running the application.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the parameter value 
     * @param cal the <code>Calendar</code> object the driver will use
     *            to construct the timestamp
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
    public void setTimestamp(int parameterIndex, java.sql.Timestamp x, Calendar cal)
	    throws SQLException{
	    	getPreparedStatement().setTimestamp(parameterIndex, x, cal);
	    }


    /**
     * Sets the designated parameter to SQL <code>NULL</code>.
     * This version of the method <code>setNull</code> should
     * be used for user-defined types and REF type parameters.  Examples
     * of user-defined types include: STRUCT, DISTINCT, JAVA_OBJECT, and 
     * named array types.
     *
     * <P><B>Note:</B> To be portable, applications must give the
     * SQL type code and the fully-qualified SQL type name when specifying
     * a NULL user-defined or REF parameter.  In the case of a user-defined type 
     * the name is the type name of the parameter itself.  For a REF 
     * parameter, the name is the type name of the referenced type.  If 
     * a JDBC driver does not need the type code or type name information, 
     * it may ignore it.     
     *
     * Although it is intended for user-defined and Ref parameters,
     * this method may be used to set a null parameter of any JDBC type.
     * If the parameter does not have a user-defined or REF type, the given
     * typeName is ignored.
     *
     *
     * @param paramIndex the first parameter is 1, the second is 2, ...
     * @param sqlType a value from <code>java.sql.Types</code>
     * @param typeName the fully-qualified name of an SQL user-defined type;
     *  ignored if the parameter is not a user-defined type or REF 
     * @exception SQLException if a database access error occurs
     * @since 1.2
     */
  public void setNull (int paramIndex, int sqlType, String typeName) 
    throws SQLException{
    	getPreparedStatement().setNull(paramIndex, sqlType, typeName);
    }


    //------------------------- JDBC 3.0 -----------------------------------

    /**
     * Sets the designated parameter to the given <code>java.net.URL</code> value. 
     * The driver converts this to an SQL <code>DATALINK</code> value
     * when it sends it to the database.
     *
     * @param parameterIndex the first parameter is 1, the second is 2, ...
     * @param x the <code>java.net.URL</code> object to be set
     * @exception SQLException if a database access error occurs
     * @since 1.4
     */ 
    public void setURL(int parameterIndex, java.net.URL x) throws SQLException{
    	getPreparedStatement().setURL(parameterIndex, x);
    }


    /**
     * Retrieves the number, types and properties of this 
     * <code>PreparedStatement</code> object's parameters.
     *
     * @return a <code>ParameterMetaData</code> object that contains information
     *         about the number, types and properties of this 
     *         <code>PreparedStatement</code> object's parameters
     * @exception SQLException if a database access error occurs
     * @see ParameterMetaData
     * @since 1.4
     */
    public ParameterMetaData getParameterMetaData() throws SQLException{
    	return getPreparedStatement().getParameterMetaData();
    }
    
    public PreparedStatement getPreparedStatement() throws SQLException{
    	if (ps_!=null)
    		return ps_;
    	throw new SQLException("Prepared Statement has been closed. No Further action allowed");
    }

//  public void setRowId(int parameterIndex, RowId x) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setRowId(parameterIndex, x);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setNString(int parameterIndex, String value) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setNString(parameterIndex, value);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setNCharacterStream(parameterIndex, value, length);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setNClob(int parameterIndex, NClob value) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setNClob(parameterIndex, value);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setClob(parameterIndex, reader, length);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setBlob(parameterIndex, inputStream, length);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setNClob(parameterIndex, reader, length);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setSQLXML(parameterIndex, xmlObject);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setAsciiStream(parameterIndex, x, length);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setBinaryStream(parameterIndex, x, length);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setCharacterStream(parameterIndex, reader, length);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setAsciiStream(parameterIndex, x);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setBinaryStream(parameterIndex, x);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setCharacterStream(parameterIndex, reader);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setNCharacterStream(parameterIndex, value);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setClob(int parameterIndex, Reader reader) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setClob(parameterIndex, reader);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setBlob(parameterIndex, inputStream);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setNClob(int parameterIndex, Reader reader) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setNClob(parameterIndex, reader);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public boolean isClosed() throws SQLException {
//    if (JavaVersion.isJava6)
//      return getPreparedStatement().isClosed();
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public void setPoolable(boolean poolable) throws SQLException {
//    if (JavaVersion.isJava6)
//      getPreparedStatement().setPoolable(poolable);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public boolean isPoolable() throws SQLException {
//    if (JavaVersion.isJava6)
//      return getPreparedStatement().isPoolable();
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public <T> T unwrap(Class<T> iface) throws SQLException {
//    if (JavaVersion.isJava6)
//      return getPreparedStatement().unwrap(iface);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }
//
//  public boolean isWrapperFor(Class<?> iface) throws SQLException {
//    if (JavaVersion.isJava6)
//      return getPreparedStatement().isWrapperFor(iface);
//    else
//      throw new UnsupportedOperationException("Not supported yet.");
//  }

}
