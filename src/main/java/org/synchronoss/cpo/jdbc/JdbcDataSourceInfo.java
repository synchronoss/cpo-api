/**
 *  JdbcDataSourceInfo.java
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
 */
package org.synchronoss.cpo.jdbc;

import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;
import java.util.TreeMap;

import javax.naming.Context;

/**
 * @author david.berry
 *
 */
public class JdbcDataSourceInfo {
    protected static final int               URL_CONNECTION = 1;
    protected static final int         URL_PROPS_CONNECTION = 2;
    protected static final int URL_USER_PASSWORD_CONNECTION = 3;
    protected static final int              JNDI_CONNECTION = 4;
    
    private int    connectionType_ = 0;
    
    private String       jndiName_ = null;
    private Context       jndiCtx_ = null;

    private String         driver_ = null;
    private String            url_ = null;
    private String       username_ = null;
    private String       password_ = null;
    private Properties properties_ = null;

    private int maxConnections_ = 0;
    private int initialConnections_ = 0;
    private boolean waitIfBusy_ = false;
    
    private String dataSourceName = null;
    
    private String dbTablePrefix="";


	/**
	 * 
	 */
	@SuppressWarnings("unused")
  private JdbcDataSourceInfo() {
		super();
	}
        
        /**
         * Creates a JdbcDataSourceInfo from a JNDIName that represents the 
         * datasource in the application server.
         *
         * @param JndiName The JndiName of the app server datasource
         *
         */
	public JdbcDataSourceInfo(String JndiName) {
		setConnectionType(JNDI_CONNECTION);
		setJndiName(JndiName);
		setDataSourceName(JndiName);
	}
	
    /**
     * Creates a JdbcDataSourceInfo from a JNDIName that represents the 
     * datasource in the application server.
     *
     * @param JndiName The JndiName of the app server datasource
     * @tablePrefix The prefix added to the cpo tables in the metadata source
     *
     */
	public JdbcDataSourceInfo(String JndiName, String tablePrefix) {
		setConnectionType(JNDI_CONNECTION);
		setJndiName(JndiName);
		setDataSourceName(JndiName);
		if (tablePrefix!=null){
			setDbTablePrefix(tablePrefix);
		}
	}

        /**
         * Creates a JdbcDataSourceInfo from a JNDIName that represents the 
         * datasource in the application server.
         *
         * @param JndiName The JndiName of the app server datasource
         * @param ctx - The context for which the Jndi Lookup should use.
         *
         */
	public JdbcDataSourceInfo(String JndiName, Context ctx){
		setConnectionType(JNDI_CONNECTION);
		setJndiName(JndiName);
		setJndiCtx(ctx);
		setDataSourceName(JndiName);
	}
	
    /**
     * Creates a JdbcDataSourceInfo from a JNDIName that represents the 
     * datasource in the application server.
     *
     * @param JndiName The JndiName of the app server datasource
     * @param ctx - The context for which the Jndi Lookup should use.
     * @tablePrefix The prefix added to the cpo tables in the metadata source
     *
     */
	public JdbcDataSourceInfo(String JndiName, Context ctx, String tablePrefix){
		setConnectionType(JNDI_CONNECTION);
		setJndiName(JndiName);
		setJndiCtx(ctx);
		setDataSourceName(JndiName);
		if (tablePrefix!=null){
			setDbTablePrefix(tablePrefix);
		}
	}
	
        /**
         * Creates a JdbcDataSourceInfo from a Jdbc Driver
         *
         * @param driver The text name of the driver
         * @param url - The url that points to the database.
         * @param initialConnections - The initial number of connections to be  
         *                       created in the connection pool
         * @param maxConnections - The max number of connections of the 
         *                       connection pool
         * @param waitIfBusy - If the maxConnections are in use do you wait for a 
         *                   connection to free up or throw an exception 
         */
    public JdbcDataSourceInfo(String driver, String url,
			int initialConnections, int maxConnections, boolean waitIfBusy)
			throws SQLException {

		setDriver(driver);
		setUrl(url);
		setConnectionType(URL_CONNECTION);
		setInitialConnections(initialConnections);
		setMaxConnections(maxConnections);
		setWaitIfBusy(waitIfBusy);
		setDataSourceName(url);
	}

    /**
     * Creates a JdbcDataSourceInfo from a Jdbc Driver
     *
     * @param driver The text name of the driver
     * @param url - The url that points to the database.
     * @param initialConnections - The initial number of connections to be  
     *                       created in the connection pool
     * @param maxConnections - The max number of connections of the 
     *                       connection pool
     * @param waitIfBusy - If the maxConnections are in use do you wait for a 
     *                   connection to free up or throw an exception 
     * @tablePrefix The prefix added to the cpo tables in the metadata source
     */
	public JdbcDataSourceInfo(String driver, String url,
			int initialConnections, int maxConnections, boolean waitIfBusy, String tablePrefix)
			throws SQLException {
	
		setDriver(driver);
		setUrl(url);
		setConnectionType(URL_CONNECTION);
		setInitialConnections(initialConnections);
		setMaxConnections(maxConnections);
		setWaitIfBusy(waitIfBusy);
		setDataSourceName(url);
		if (tablePrefix!=null){
			setDbTablePrefix(tablePrefix);
		}
	}
	
        /**
         * Creates a JdbcDataSourceInfo from a Jdbc Driver
         *
         * @param driver The text name of the driver
         * @param url - The url that points to the database.
         * @param properties - The connection properties for connecting to the database
         * @param initialConnections - The initial number of connections to be  
         *                       created in the connection pool
         * @param maxConnections - The max number of connections of the 
         *                       connection pool
         * @param waitIfBusy - If the maxConnections are in use do you wait for a 
         *                   connection to free up or throw an exception 
         */
	public JdbcDataSourceInfo(String driver, String url, Properties properties,
			int initialConnections, int maxConnections, boolean waitIfBusy)
			throws SQLException {
		setDriver(driver);
		setUrl(url);
		setProperties(properties);
		setConnectionType(URL_PROPS_CONNECTION);
		setInitialConnections(initialConnections);
		setMaxConnections(maxConnections);
		setWaitIfBusy(waitIfBusy);
		setDataSourceName(BuildDataSourceName(url, properties));
	}
	
    /**
     * Creates a JdbcDataSourceInfo from a Jdbc Driver
     *
     * @param driver The text name of the driver
     * @param url - The url that points to the database.
     * @param properties - The connection properties for connecting to the database
     * @param initialConnections - The initial number of connections to be  
     *                       created in the connection pool
     * @param maxConnections - The max number of connections of the 
     *                       connection pool
     * @param waitIfBusy - If the maxConnections are in use do you wait for a 
     *                   connection to free up or throw an exception 
     * @tablePrefix The prefix added to the cpo tables in the metadata source
     */
public JdbcDataSourceInfo(String driver, String url, Properties properties,
		int initialConnections, int maxConnections, boolean waitIfBusy, String tablePrefix)
		throws SQLException {
	setDriver(driver);
	setUrl(url);
	setProperties(properties);
	setConnectionType(URL_PROPS_CONNECTION);
	setInitialConnections(initialConnections);
	setMaxConnections(maxConnections);
	setWaitIfBusy(waitIfBusy);
	setDataSourceName(BuildDataSourceName(url, properties));
	if (tablePrefix!=null){
		setDbTablePrefix(tablePrefix);
	}
}

        /**
         * Creates a JdbcDataSourceInfo from a Jdbc Driver
         *
         * @param driver The text name of the driver
         * @param url - The url that points to the database.
         * @param username - The username for connecting to the database
         * @param password - The password for connectinf to the database
         * @param initialConnections - The initial number of connections to be  
         *                       created in the connection pool
         * @param maxConnections - The max number of connections of the 
         *                       connection pool
         * @param waitIfBusy - If the maxConnections are in use do you wait for a 
         *                   connection to free up or throw an exception 
         */
	public JdbcDataSourceInfo(String driver, String url, String username,
			String password, int initialConnections, int maxConnections,
			boolean waitIfBusy) throws SQLException {

		setConnectionType(URL_USER_PASSWORD_CONNECTION);
		setDriver(driver);
		setUrl(url);
		setUserName(username);
		setPassword(password);
		setInitialConnections(initialConnections);
		setMaxConnections(maxConnections);
		setWaitIfBusy(waitIfBusy);
		setDataSourceName(url+username);
	}

    /**
     * Creates a JdbcDataSourceInfo from a Jdbc Driver
     *
     * @param driver The text name of the driver
     * @param url - The url that points to the database.
     * @param username - The username for connecting to the database
     * @param password - The password for connectinf to the database
     * @param initialConnections - The initial number of connections to be  
     *                       created in the connection pool
     * @param maxConnections - The max number of connections of the 
     *                       connection pool
     * @param waitIfBusy - If the maxConnections are in use do you wait for a 
     *                   connection to free up or throw an exception 
     * @tablePrefix The prefix added to the cpo tables in the metadata source
     */
	public JdbcDataSourceInfo(String driver, String url, String username,
			String password, int initialConnections, int maxConnections,
			boolean waitIfBusy, String tablePrefix) throws SQLException {
	
		setConnectionType(URL_USER_PASSWORD_CONNECTION);
		setDriver(driver);
		setUrl(url);
		setUserName(username);
		setPassword(password);
		setInitialConnections(initialConnections);
		setMaxConnections(maxConnections);
		setWaitIfBusy(waitIfBusy);
		setDataSourceName(url+username);
		if (tablePrefix!=null){
			setDbTablePrefix(tablePrefix);
		}
	}

        /**
         * Returns the name of the jdbc driver
         */
	public String getDriver() {
		return driver_;
	}

        /**
         * Returns the url to be used to connect to the database
         */
	public String getUrl() {
		return url_;
	}

        /**
         * Returns the username to connect to the database
         */
	public String getUserName() {
		return username_;
	}

        /**
         * Returns the password to connect to the database
         */
	public String getPassword() {
		return password_;
	}

        /**
         * Returns the database connection properties
         */
	public Properties getProperties() {
		return properties_;
	}

        /**
         * Returns the max connections for the connection pool
         */
	public int getMaxConnections() {
		return maxConnections_;
	}

        /**
         * Returns whether to wait for an available connection
         */
	public boolean getWaitIfBusy() {
		return waitIfBusy_;
	}

        /**
         * Returns the type of connection to the database.
         */
	public int getConnectionType() {
		return connectionType_;
	}

	protected void setDriver(String driver) {
		driver_ = driver;
	}

	protected void setUrl(String url) {
		url_ = url;
	}

	protected void setUserName(String username) {
		username_ = username;
	}

	protected void setPassword(String password) {
		password_ = password;
	}

	protected void setProperties(Properties properties) {
		properties_ = properties;
	}

	protected void setMaxConnections(int maxConnections) {
		maxConnections_ = maxConnections;
	}

	protected void setWaitIfBusy(boolean waitIfBusy) {
		waitIfBusy_ = waitIfBusy;
	}

	protected void setConnectionType(int connectionType) {
		connectionType_ = connectionType;
	}
	/**
	 * @return Returns the jndiCtx_.
	 */
	public Context getJndiCtx() {
		return jndiCtx_;
	}

	/**
	 * @param jndiCtx_ The jndiCtx_ to set.
	 */
	protected void setJndiCtx(Context jndiCtx_) {
		this.jndiCtx_ = jndiCtx_;
	}

	/**
	 * @return Returns the jndiName_.
	 */
	public String getJndiName() {
		return jndiName_;
	}

	/**
	 * @param jndiName_ The jndiName_ to set.
	 */
	protected void setJndiName(String jndiName_) {
		this.jndiName_ = jndiName_;
	}

        /**
         * Returns the number of initial connections to the database.
         */
	public int getInitialConnections() {
		return initialConnections_;
	}

	/**
	 * @param initialConnections_ The initialConnections_ to set.
	 */
	protected void setInitialConnections(int initialConnections_) {
		this.initialConnections_ = initialConnections_;
	}

	/**
	 * @return Returns the dataSourceName.
	 */
	public String getDataSourceName() {
		return dataSourceName;
	}

	/**
	 * @param dataSourceName The dataSourceName to set.
	 */
	protected void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	/**
	 * @return Returns the dataSourceName.
	 */
	public String getDbTablePrefix() {
		return this.dbTablePrefix;
	}

	/**
	 * @param dbTablePrefix The table prefix to set.
	 */
	protected void setDbTablePrefix(String dbTablePrefix) {
		this.dbTablePrefix = dbTablePrefix;
	}
	
    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param properties DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String BuildDataSourceName(String url, Properties properties) {
        StringBuffer dsName=new StringBuffer(url);
        TreeMap treeMap=new TreeMap(properties);
        Iterator<String> it=treeMap.values().iterator();

        // Use a tree map so that the properties are sorted. This way if we have
        // the same datasource with the same properties but in different order,
        // we will generate the same key.
        while(it.hasNext()) {
            dsName.append((String) it.next());
        }

        return dsName.toString();
    }


}
