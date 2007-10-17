/**
 * JdbcDataSource.java
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

import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.Vector;

import javax.sql.DataSource;

/** A class for preallocating, recycling, and managing
 *  JDBC connections.
 *  <P>
 *  Taken from Core Servlets and JavaServer Pages
 *  from Prentice Hall and Sun Microsystems Press,
 *  http://www.coreservlets.com/.
 *  &copy; 2000 Marty Hall; may be freely used or adapted.
 */

public class JdbcDataSource implements DataSource {
    // Attributes to help satisfy the DataSource Interface
    private PrintWriter printWriter_ = null;
    private int timeout_ = 0;
    
    //HashMap for storing PreparedStatement and Callable Statment HashMaps
    private HashMap<Connection, HashMap<String, PreparedStatement>> connectionPSMap_ = new HashMap<Connection, HashMap<String, PreparedStatement>>();
    private HashMap<Connection, HashMap<String, CallableStatement>> connectionCSMap_ = new HashMap<Connection, HashMap<String, CallableStatement>>();
    
    private JdbcDataSourceInfo dataSourceInfo=null;
    
    private Vector<Connection> availableConnections = null;
    private Vector<Connection>      busyConnections = null;

    private JdbcDataSource(){
    	
    }

    public JdbcDataSource(JdbcDataSourceInfo jdsi) throws SQLException {
    	setDataSourceInfo(jdsi);
    	InitializeConnections(jdsi.getInitialConnections());
    }
    private void InitializeConnections(int initialConnections) 
    throws SQLException {

        int newConnections = initialConnections > getDataSourceInfo().getMaxConnections() ? getDataSourceInfo().getMaxConnections():initialConnections;

        availableConnections = new Vector<Connection>();
        busyConnections = new Vector<Connection>();
        for(int i=0; i<newConnections; i++) {
            availableConnections.addElement(makeNewConnection());
        }
    }

    public Connection getConnection(String userName, String password) 
    throws SQLException {
        Connection connection = null;

        if(getDataSourceInfo().getConnectionType()==JdbcDataSourceInfo.URL_USER_PASSWORD_CONNECTION) {
            if(userName == getDataSourceInfo().getUserName() && password == getDataSourceInfo().getPassword()) {
                // Same user and password as the connection pool so give it the pool
                connection = getConnection(); 
            } else {
                // Different User and Password so return an unpooled connection from the datasource
                connection=DriverManager.getConnection(getDataSourceInfo().getUrl(), userName, password);
            }
        }
        else {
            throw new SQLException("Unable to connect to DataSource");
        }

        return connection;
    }
    

    public Connection getConnection()
    throws SQLException {
        Connection connection = null;
        
        for(;;){
        
            synchronized(availableConnections) { 
                for(;;){
                    if (availableConnections.isEmpty())
                            break;
                    connection = (Connection) availableConnections.lastElement();
                    availableConnections.removeElement(connection);

                    if (connection.isClosed() || isConnectionInvalid(connection)) {
                            connection=null;
                    } else {
                        busyConnections.addElement(connection);
                        break;
                    }
                }
            }

            if(connection==null) {

                // Three possible cases:
                // 1) You haven't reached maxConnections limit. So
                //    establish one in the background if there isn't
                //    already one pending, then wait for
                //    the next available connection (whether or not
                //    it was the newly established one).
                // 2) You reached maxConnections limit and waitIfBusy
                //    flag is false. Throw SQLException in such a case.
                // 3) You reached maxConnections limit and waitIfBusy
                //    flag is true. Then do the same thing as in second
                //    part of step 1: wait for next available connection.

                synchronized(availableConnections) {        

                    if(getDataSourceInfo().getMaxConnections()<0 || totalConnections() < getDataSourceInfo().getMaxConnections()) {
                        availableConnections.addElement(makeNewConnection());
                     } else if(!getDataSourceInfo().getWaitIfBusy()) {
                        throw new SQLException("Connection limit reached");
                    }
                }

                while (availableConnections.isEmpty()) {
                    Thread.currentThread().yield();
                }
           } else {
                break; // we have a connection so get out of the outer loop
           }
        }

        return new JdbcConnection(this, connection);
    }

    // This explicitly makes a new connection. Called in
    // the foreground when initializing the JdbcDataSource,
    // and called in the background when running.

    private Connection makeNewConnection() throws SQLException {
        Connection connection = null;
        try {
            // Load database driver if not already loaded
            Class.forName(getDataSourceInfo().getDriver());

            switch(getDataSourceInfo().getConnectionType()) {
            case JdbcDataSourceInfo.URL_CONNECTION: 
                connection=DriverManager.getConnection(getDataSourceInfo().getUrl());
                break;
            case JdbcDataSourceInfo.URL_PROPS_CONNECTION:
                connection=DriverManager.getConnection(getDataSourceInfo().getUrl(), getDataSourceInfo().getProperties());
                break;
            case JdbcDataSourceInfo.URL_USER_PASSWORD_CONNECTION:
                connection=DriverManager.getConnection(getDataSourceInfo().getUrl(), getDataSourceInfo().getUserName(), getDataSourceInfo().getPassword());
                break;
            default: throw new SQLException("Invalid Connection Type");
            }
        } catch(ClassNotFoundException cnfe) {
        	throw new SQLException("Could Not Load Driver"+getDataSourceInfo().getDriver());
        }
        return connection;
    }

    public void free(Connection connection) {
    	boolean b;
    	
        synchronized(availableConnections) {
	        b = busyConnections.removeElement(connection);
	        
	        //Only make it available if we found it to be busy
	        if (b) {
	            availableConnections.addElement(connection);
	        }
        }
    }

    protected synchronized int totalConnections() {
        return(availableConnections.size() +
               busyConnections.size());
    }

    /** Close all the connections. Use with caution:
     *  be sure no connections are in use before
     *  calling. Note that you are not <I>required</I> to
     *  call this when done with a JdbcDataSource, since
     *  connections are guaranteed to be closed when
     *  garbage collected. But this method gives more control
     *  regarding when the connections are closed.
     */

    protected synchronized void closeAllConnections() {
        closeConnections(availableConnections);
        availableConnections = new Vector<Connection>();
        closeConnections(busyConnections);
        busyConnections = new Vector<Connection>();
    }

    private void closeConnections(Vector<Connection> connections) {
        try {
            for(Connection connection:connections){
                if(!connection.isClosed()) {
                    connection.close();
                }
            }
            connections.removeAllElements();
        } catch(SQLException sqle) {
            // Ignore errors; garbage collect anyhow
        }
    }

    public synchronized String toString() {
        StringBuffer info = new StringBuffer();
        info.append("JdbcDataSource(");
        info.append(getDataSourceInfo().getUrl());
        info.append(",");
        info.append(getDataSourceInfo().getUserName());
        info.append(")");
        info.append(", available=");
        info.append(availableConnections.size());
        info.append(", busy=");
        info.append(busyConnections.size());
        info.append(", max=");
        info.append(getDataSourceInfo().getMaxConnections());
        return(info.toString());
    }

    public PrintWriter getLogWriter()
    throws SQLException{
        return printWriter_;
    }

    public void setLogWriter(PrintWriter out)
    throws SQLException{
        printWriter_ = out;

    }

    public void setLoginTimeout(int seconds)
    throws SQLException {
        timeout_ = seconds;
    }

    public int getLoginTimeout()
    throws SQLException {
        return timeout_;
    }

    private boolean isConnectionInvalid(Connection conn){
        boolean invalid = true;
        try{
            conn.getMetaData();
            invalid = false;
        } 
        catch (Exception e){
            // do nothing 
        }
        return invalid;
    }
    
    public CallableStatement getCachedCallableStatement(Connection c, String sql, String key) throws SQLException {
    	CallableStatement cs = null;
    	HashMap<String, CallableStatement> csMap = connectionCSMap_.get(c);
    	
    	if (csMap!=null){
    		cs = (CallableStatement)csMap.get(key);
    	} else {
    		csMap = new HashMap<String, CallableStatement>();
    		connectionCSMap_.put(c, csMap);
    	}
    	
    	if (cs==null){
    		cs = c.prepareCall(sql);
    		csMap.put(key,cs);
    	}else{
    		//clean up the CallableStatement from the last use.
    		cs.clearWarnings();
    		cs.clearParameters();
    		try {cs.clearBatch();} catch (Exception e){}
    	}
   	
    	return cs;
    }
    
    public PreparedStatement getCachedPreparedStatement(Connection c, String sql, String key)  throws SQLException {
    	PreparedStatement ps = null;
    	HashMap<String, PreparedStatement> psMap = connectionPSMap_.get(c);
    	
    	if (psMap!=null){
    		ps = (PreparedStatement)psMap.get(key);
    	} else {
    		psMap = new HashMap<String, PreparedStatement> ();
    		connectionPSMap_.put(c, psMap);
    	}
    	
    	if (ps==null){
    		ps = c.prepareStatement(sql);
    		psMap.put(key,ps);
    	}else{
    		//clean up the PreparedStatement from the last use.
    		ps.clearWarnings();
    		try {ps.clearBatch();} catch (Exception e){}
    		ps.clearParameters();
    	}
    	
    	return ps;
    }
    
    public PreparedStatement getCachedPreparedStatement(Connection c, String sql) throws SQLException {
    	return getCachedPreparedStatement(c, sql, sql);
    }
    
    public PreparedStatement getCachedPreparedStatement(Connection c, String sql, int autoGeneratedKeys) throws SQLException {
    	String key = sql+"|"+autoGeneratedKeys;
    	return getCachedPreparedStatement(c, sql, key);
    }
    
    public PreparedStatement getCachedPreparedStatement(Connection c, String sql, int[] columns) throws SQLException {
    	return getCachedPreparedStatement(c, sql, buildStatement(sql, columns));
    }
    
    public PreparedStatement getCachedPreparedStatement(Connection c, String sql, String[] columns) throws SQLException {
    	return getCachedPreparedStatement(c, sql, buildStatement(sql, columns));
    }
    
    public PreparedStatement getCachedPreparedStatement(Connection c, String sql, int resultSetType,
    		int resultSetConcurrency) throws SQLException {
    	String key = sql+"|"+resultSetType+"|"+resultSetConcurrency;
    	return getCachedPreparedStatement(c, sql, key);
    }
    
    public PreparedStatement getCachedPreparedStatement(Connection c, String sql, int resultSetType,
    		int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    	String key = sql+"|"+resultSetType+"|"+resultSetConcurrency+"|"+resultSetHoldability;
    	return getCachedPreparedStatement(c, sql, key);
    }

    public CallableStatement getCachedCallableStatement(Connection c, String sql) throws SQLException {
        return getCachedCallableStatement(c, sql, sql);
    }

    public CallableStatement getCachedCallableStatement(Connection c, String sql, int resultSetType,
    		int resultSetConcurrency) throws SQLException {
    	String key = sql+"|"+resultSetType+"|"+resultSetConcurrency;
    	return getCachedCallableStatement(c, sql, key);
    }
    
    public CallableStatement getCachedCallableStatement(Connection c, String sql, int resultSetType,
    		int resultSetConcurrency, int resultSetHoldability) throws SQLException {
    	String key = sql+"|"+resultSetType+"|"+resultSetConcurrency+"|"+resultSetHoldability;
    	return getCachedCallableStatement(c, sql, key);
    }
    
    private String buildStatement(String sql, int[] columnIdx) {
        StringBuffer stmnt=new StringBuffer(sql);
        TreeMap<Integer, Integer> treeMap=new TreeMap<Integer, Integer>();
        
        for (int i=0; i<columnIdx.length; i++){
        	Integer col = new Integer(columnIdx[i]);
        	treeMap.put(col,col);
        }
        
        // Use a tree map so that the properties are sorted. This way if we have
        // the same datasource with the same properties but in different order,
        // we will generate the same key.
        for(Integer i:treeMap.values()){
        	stmnt.append("|");
        	stmnt.append(i);
        }

        return stmnt.toString();
    }
    
    private String buildStatement(String sql, String[] columnNames) {
        StringBuffer stmnt=new StringBuffer(sql);
        TreeMap<String, String> treeMap=new TreeMap<String, String>();
        
        for (int i=0; i<columnNames.length; i++)
        	treeMap.put(columnNames[i],columnNames[i]);
        
        // Use a tree map so that the properties are sorted. This way if we have
        // the same datasource with the same properties but in different order,
        // we will generate the same key.
        for(String s:treeMap.values()){
        	stmnt.append("|");
        	stmnt.append(s);
        }

        return stmnt.toString();
    }

	/**
	 * @return Returns the dataSourceInfo.
	 */
	public JdbcDataSourceInfo getDataSourceInfo() {
		return dataSourceInfo;
	}

	/**
	 * @param dataSourceInfo The dataSourceInfo to set.
	 */
	public void setDataSourceInfo(JdbcDataSourceInfo dataSourceInfo) {
		this.dataSourceInfo = dataSourceInfo;
	}



}
