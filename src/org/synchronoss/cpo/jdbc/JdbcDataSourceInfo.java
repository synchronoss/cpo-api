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
    public static final int               URL_CONNECTION = 1;
    public static final int         URL_PROPS_CONNECTION = 2;
    public static final int URL_USER_PASSWORD_CONNECTION = 3;
    public static final int              JNDI_CONNECTION = 4;
    
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


	/**
	 * 
	 */
	private JdbcDataSourceInfo() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public JdbcDataSourceInfo(String JndiName) {
		setConnectionType(JNDI_CONNECTION);
		setJndiName(JndiName);
		setDataSourceName(JndiName);
	}
	
	public JdbcDataSourceInfo(String JndiName, Context ctx){
		setConnectionType(JNDI_CONNECTION);
		setJndiName(JndiName);
		setJndiCtx(ctx);
		setDataSourceName(JndiName);
	}
	
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

	public String getDriver() {
		return driver_;
	}

	public String getUrl() {
		return url_;
	}

	public String getUserName() {
		return username_;
	}

	public String getPassword() {
		return password_;
	}

	public Properties getProperties() {
		return properties_;
	}

	public int getMaxConnections() {
		return maxConnections_;
	}

	public boolean getWaitIfBusy() {
		return waitIfBusy_;
	}

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
	 * @return Returns the initialConnections_.
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
        Iterator it=treeMap.values().iterator();

        // Use a tree map so that the properties are sorted. This way if we have
        // the same datasource with the same properties but in different order,
        // we will generate the same key.
        while(it.hasNext()) {
            dsName.append((String) it.next());
        }

        return dsName.toString();
    }


}
