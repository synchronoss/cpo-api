/**
 *  JdbcCpoAdapter.java    
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


import java.rmi.RemoteException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoObject;
import org.synchronoss.cpo.CpoOrderBy;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.CpoWhere;

/**
 * JdbcCpoAdapter is an interface for a set of routines that are responsible for managing value
 * objects from a datasource.
 *
 * @author david berry
 */
public class JdbcCpoAdapter implements CpoAdapter{
    /** Version Id for this class. */
    private static final long serialVersionUID=1L;

    /** DOCUMENT ME! */
    private static Logger logger=Logger.getLogger(JdbcCpoAdapter.class.getName());

    /** DOCUMENT ME! */
    private static final String[] GROUP_IDS={
            "CREATE", "UPDATE", "DELETE", "RETRIEVE", "LIST", "PERSIST", "EXIST", "EXECUTE"
        };
    
    private static final String WHERE_MARKER = "__CPO_WHERE__";
    private static final String ORDERBY_MARKER = "__CPO_ORDERBY__";
    

    // Query Group Name Constants

    /** DOCUMENT ME! */
    private static final String CREATE_GROUP=GROUP_IDS[CpoAdapter.CREATE];

    /** DOCUMENT ME! */
    private static final String UPDATE_GROUP=GROUP_IDS[CpoAdapter.UPDATE];

    /** DOCUMENT ME! */
    private static final String DELETE_GROUP=GROUP_IDS[CpoAdapter.DELETE];

    /** DOCUMENT ME! */
    private static final String RETRIEVE_GROUP=GROUP_IDS[CpoAdapter.RETRIEVE];

    /** DOCUMENT ME! */
    private static final String LIST_GROUP=GROUP_IDS[CpoAdapter.LIST];

    /** DOCUMENT ME! */
    private static final String PERSIST_GROUP=GROUP_IDS[CpoAdapter.PERSIST];

    /** DOCUMENT ME! */
    private static final String EXIST_GROUP=GROUP_IDS[CpoAdapter.EXIST];

    /** DOCUMENT ME! */
    private static final String EXECUTE_GROUP=GROUP_IDS[CpoAdapter.EXECUTE];

    // Metadata Cache Objects

    /** DOCUMENT ME! */
    private static HashMap dataSourceMap_=new HashMap(); // Contains the
                                                         // metaClassMap for
                                                         // each datasource

    // Default Connection. Only used for one constructor.

    /** DOCUMENT ME! */
    private Connection writeConnection_=null;

    /** DOCUMENT ME! */
    private Context context_=null;

    // DataSource Information
    // These used to be static, but that prevented Cpo from supporting Multiple
    // DataSources.
    // The classMap will be accessed via the dataSourceMap now.

    /** DOCUMENT ME! */
    private DataSource readDataSource_=null;

    /** DOCUMENT ME! */
    private DataSource writeDataSource_=null;
    
    /** DOCUMENT ME! */
    private DataSource metaDataSource_=null;
    
    /** DOCUMENT ME! */
    private String metaDataSourceName_=null;

    /** DOCUMENT ME! */

    /** DOCUMENT ME! */
    private boolean invalidReadConnection_=false;
    
    private boolean metaEqualsWrite_=false;
    
    private boolean batchUpdatesSupported_=false;
    
    protected JdbcCpoAdapter(){}
    
/**
 * Creates a JdbcCpoAdapter.
 *
 * @param jdsi This datasource will be used for both the metadata 
 * and the transaction database.
 */
    public JdbcCpoAdapter(JdbcDataSourceInfo jdsi)
            throws CpoException {
            
        setMetaDataSource(getDataSource(jdsi));
        setMetaDataSourceName(jdsi.getDataSourceName());
        setWriteDataSource(getMetaDataSource());
        setReadDataSource(getMetaDataSource());
        processDatabaseMetaData();
        metaEqualsWrite_=true;
        
    }
    
/**
 * Creates a JdbcCpoAdapter.
 *
 * @param jdsiMeta This datasource that identifies the cpo metadata datasource 
 * @param jdsiTrx The datasoruce that identifies the transaction database.
 */
    public JdbcCpoAdapter(JdbcDataSourceInfo jdsiMeta, JdbcDataSourceInfo jdsiTrx)
            throws CpoException {
        setMetaDataSource(getDataSource(jdsiMeta));
        setWriteDataSource(getDataSource(jdsiTrx));
        setMetaDataSourceName(jdsiMeta.getDataSourceName());
        setReadDataSource(getWriteDataSource());
        processDatabaseMetaData();
    }

/**
 * Creates a JdbcCpoAdapter.
 *
 * @param jdsiMeta This datasource that identifies the cpo metadata datasource 
 * @param jdsiWrite The datasource that identifies the transaction database
 *                  for write transactions.
 * @param jdsiRead The datasource that identifies the transaction database
 *                  for read-only transactions.
 */
    public JdbcCpoAdapter(JdbcDataSourceInfo jdsiMeta, JdbcDataSourceInfo jdsiWrite, JdbcDataSourceInfo jdsiRead)
            throws CpoException {
        setMetaDataSource(getDataSource(jdsiMeta));
        setWriteDataSource(getDataSource(jdsiWrite));
        setReadDataSource(getDataSource(jdsiRead));
        setMetaDataSourceName(jdsiMeta.getDataSourceName());
        processDatabaseMetaData();
    }
    
    protected JdbcCpoAdapter(DataSource metaSource, String metaSourceName, Connection c, boolean batchSupported)
        throws CpoException {
        setMetaDataSource(metaSource);
        setStaticConnection(c);
        setMetaDataSourceName(metaSourceName);
        batchUpdatesSupported_ = batchSupported;
    }

    private DataSource getDataSource(JdbcDataSourceInfo jdsi) throws CpoException {
        DataSource ds = null;
        
        try{
            if (jdsi.getConnectionType()==JdbcDataSourceInfo.JNDI_CONNECTION){
                Context ctx = jdsi.getJndiCtx();
                if (ctx==null){
                    ctx=new InitialContext();
                }
                ds = (DataSource) ctx.lookup(jdsi.getJndiName());
                
            } else {
                ds = new JdbcDataSource(jdsi);
            }
        } catch(Exception e) {
            throw new CpoException("Error instantiating DataSource", e);
        }

        return ds;
    }
    
    private void processDatabaseMetaData() throws CpoException {
        Connection c=null;
        try{
            c = getWriteConnection();
            DatabaseMetaData dmd = c.getMetaData();
            
            // do all the tests here
            batchUpdatesSupported_ = dmd.supportsBatchUpdates();
            
            this.closeConnection(c);
        }catch (Exception e){
            throw new CpoException("Could Not Retrieve Database Meta Data");
        }finally {
            closeConnection(c);
        }
    }

    
    /**
     * Clears the metadata for the specified object. The metadata will be reloaded
     * the next time that CPO is called to access this object
     *
     * @param obj The object whose metadata must be cleared
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public void clearMetaClass(Object obj) {
        String className=null;
        Class objClass=null;

        if(obj!=null) {
            objClass=obj.getClass();
            className=objClass.getName();
            clearMetaClass(className);
        }
    }

    /**
     * Clears the metadata for the specified fully qualifed class name. The metadata 
     * will be reloaded the next time CPO is called to access this class.
     *
     * @param className The fully qualified class name for the class that needs its
     *                  metadata cleared.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public void clearMetaClass(String className) {
        HashMap metaClassMap=null;

        synchronized(getDataSourceMap()) {
            metaClassMap=getMetaClassMap();
            metaClassMap.remove(className);
        }
    }

    /**
     * Clears the metadata for all classes. The metadata will be lazy-loaded from 
     * the metadata repository as classes are accessed.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    */
    public void clearMetaClass() {
        HashMap metaClassMap=null;

        synchronized(getDataSourceMap()) {
            metaClassMap=getMetaClassMap();
            metaClassMap.clear();
        }
    }

    /**
     * Creates the Object in the datasource. The assumption is that the object does not exist in
     * the datasource.  This method creates and stores the object in the datasource.<br>
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.insertObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long insertObject(Object obj) throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.CREATE_GROUP, null);
    }

    /**
     * Creates the Object in the datasource. The assumption is that the object does not exist in
     * the datasource.  This method creates and stores the object in the datasource
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.insertObject("IDNameInsert",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the CREATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used which is
     *             equivalent to <code>insertObject(Object obj);</code>
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * 
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long insertObject(String name, Object obj) throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.CREATE_GROUP, name);
    }

    /**
     * Iterates through a collection of Objects, creates and stores them in the datasource.  The
     * assumption is that the objects contained in the collection do not exist in the  datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects in the
     * collection will be treated as one transaction, assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being created in the datasource then the CpoAdapter will stop
     * processing the remainder of the collection and rollback all the objects created thus far. Rollback is
     * on the underlying datasource's support of rollback.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.insertObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param coll This is a collection of objects that have been defined within the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long insertObjects(Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.CREATE_GROUP, null);
    }

    /**
     * Iterates through a collection of Objects, creates and stores them in the datasource.  The
     * assumption is that the objects contained in the collection do not exist in the  datasource.
     * 
     * This method creates and stores the objects in the datasource. The objects in the
     * collection will be treated as one transaction, assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.insertObjects("IdNameInsert",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the CREATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * 
     * @return The number of objects created in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long insertObjects(String name, Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.CREATE_GROUP, name);
    }

    /**
     * Removes the Object from the datasource. The assumption is that the object exists in the
     * datasource.  This method stores the object in the datasource
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.deleteObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource an exception will be thrown.
     *
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long deleteObject(Object obj) throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.DELETE_GROUP, null);
    }

    /**
     * Removes the Object from the datasource. The assumption is that the object exists in the
     * datasource.  This method stores the object in the datasource
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.deleteObject("DeleteById",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the DELETE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource an exception will be thrown.
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    */
    public long deleteObject(String name, Object obj) throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.DELETE_GROUP, name);
    }

    /**
     * Removes the Objects contained in the collection from the datasource. The  assumption is that
     * the object exists in the datasource.  This method stores the objects contained in the
     * collection in the datasource. The objects in the collection will be treated as one transaction, 
     * assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.deleteObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long deleteObjects(Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.DELETE_GROUP, null);
    }

    /**
     * Removes the Objects contained in the collection from the datasource. The  assumption is that
     * the object exists in the datasource.  This method stores the objects contained in the
     * collection in the datasource. The objects in the collection will be treated as one transaction, 
     * assuming the datasource supports transactions.
     * 
     * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
     * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.deleteObjects("IdNameDelete",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the DELETE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * @return The number of objects deleted from the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long deleteObjects(String name, Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.DELETE_GROUP, name);
    }

    /**
     * Executes an Object whose metadata will call an executable within the datasource. 
     * It is assumed that the executable object exists in the metadatasource. If the executable does not exist, 
     * an exception will be thrown.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.executeObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param object This is an <code>Object</code> that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to executed the datasource object.
     *        
     *        An object of this type will be created and filled with the returned data from the value_object. 
     *        This newly created object will be returned from this method.
     *
     * @return An object populated with the OUT parameters returned from the executable object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public Object executeObject(Object object)
        throws CpoException {
        return processExecuteGroup(null, object, object);
    }
    
    /**
     * Executes an Object whose metadata will call an executable within the datasource. 
     * It is assumed that the executable object exists in the metadatasource. If the executable does not exist, 
     * an exception will be thrown.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.executeObject("execNotifyProc",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The filter name which tells the datasource which objects should be returned. The
     *        name also signifies what data in the object will be populated.
     *
     * @param object This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to retrieve the collection of objects.
     *        This object defines the object type that will be returned in the collection and 
     *        contain the result set data or the OUT Parameters.
     * @return A result object populate with the OUT parameters
     *
     * @throws CpoException if there are errors accessing the datasource
     */
    public Object executeObject(String name, Object object)
        throws CpoException {
        return processExecuteGroup(name, object, object);
    }

    /**
     * Executes an Object that represents an executable object within the datasource. 
     * It is assumed that the object exists in the datasource. If the object does not exist, an exception will be thrown
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class SomeResult sr = new SomeResult();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				sr = (SomeResult)cpo.executeObject("execNotifyProc",so, sr);
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the EXECUTE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param criteria This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to retrieve the  collection of objects.
     * @param result This is an object that has been defined within the metadata of the datasource.
     *        If the class is not defined an exception will be thrown. If the object does not
     *        exist in the datasource, an exception will be thrown. This object defines  the
     *        object type that will be created, filled with the return data and returned from this
     *        method.
     * @return An object populated with the out parameters
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public Object executeObject(String name, Object criteria, Object result)
        throws CpoException {
        return processExecuteGroup(name, criteria, result);
    }

    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * long count = 0;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				count = cpo.existsObject(so);<br>
     * 				if (count>0) {<br>
     * 					</code><ul><code>
     *              	// object exists<br>
     * 					</code></ul><code>
     *              } else {<br>
     * 					</code><ul><code>
     *              	// object does not exist<br>
     * 					</code></ul><code>
     *              }<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. This object will be searched for inside the
     *        datasource.
	 *
     * @return The number of objects that exist in the datasource that match the specified object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long existsObject(Object obj) throws CpoException {
        return this.existsObject(null,obj);
    }

    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * long count = 0;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				count = cpo.existsObject("SomeExistCheck",so);<br>
     * 				if (count>0) {<br>
     * 					</code><ul><code>
     *              	// object exists<br>
     * 					</code></ul><code>
     *              } else {<br>
     * 					</code><ul><code>
     *              	// object does not exist<br>
     * 					</code></ul><code>
     *              }<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the EXISTS Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. This object will be searched for inside the
     *        datasource.
     * @return The number of objects that exist in the datasource that match the specified object
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long existsObject(String name, Object obj) throws CpoException {
        Connection c=null;
        Connection meta=null;
        long objCount=-1;

        try {
            c=getReadConnection();
            
            if (metaEqualsWrite_){
                meta=c;
            } else {
                meta=getMetaConnection();
            }
            objCount=existsObject(name, obj, c, meta);
        } catch(Exception e) {
            throw new CpoException("existsObjects(String, Object) failed", e);
        } finally {
            closeConnection(c);
            closeConnection(meta);
        }

        return objCount;
    }

    /**
     * The CpoAdapter will check to see if this object exists in the datasource.
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * @param con The datasource Connection with which to check if the object exists
     * @return The int value of the first column returned in the record set
     *
     * @exception CpoException exception will be thrown if the Query Group has a query count != 1
     */
    protected long existsObject(String name, Object obj, Connection con, Connection metaCon)
        throws CpoException {
        PreparedStatement ps=null;
        ResultSet rs=null;
        ResultSetMetaData rsmd=null;
        JdbcQuery jq=null;
        JdbcMetaClass jmc=null;
        ArrayList queryGroup=null;
        long objCount=0;
        int i=0;

        try {
            jmc=getMetaClass(obj, metaCon);
            queryGroup=jmc.getQueryGroup(JdbcCpoAdapter.EXIST_GROUP, name);

            for(i=0; i<queryGroup.size(); i++) {
                jq=(JdbcQuery) queryGroup.get(i);
                JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, jq, obj, null);
                ps=jpsf.getPreparedStatement();

                // insertion in
                // exists
                logger.info(jq.getText());
                rs=ps.executeQuery();
                jpsf.release();
                rsmd=rs.getMetaData();

                if(rsmd.getColumnCount()!=1) {
                    throw new CpoException("EXIST query group must return exactly one column:"+
                        jq.getText());
                }

                if(rs.next()) {
                    try {
                        objCount+=rs.getLong(1); // get the number of objects
                                                 // that exist
                    } catch(Exception e) {
                        throw new CpoException("EXISTS result not an int:"+jq.getText(), e);
                    }
                } else {
                    throw new CpoException("EXIST query must return exactly one record:"+
                        jq.getText());
                }

                if(rs.next()) {
                    throw new CpoException("EXIST query must return exactly one record:"+
                        jq.getText());
                }

                rs.close();
                ps.close();
                rs=null;
                ps=null;
            }
        } catch(SQLException e) {
            String msg="existsObject(name, obj, con) failed:";
            if (jq!=null) 
                msg+=jq.getText();
            
            logger.error(msg, e);
            throw new CpoException(msg, e);
        } finally {
            if(rs!=null) {
                try {
                    rs.close();
                } catch(Exception e) {
                }
            }

            if(ps!=null) {
                try {
                    ps.close();
                } catch(Exception e) {
                }
            }
        }

        return objCount;
    }

    /**
     * <code>newOrderBy</code> allows you to dynamically change the order of the objects in the resulting 
     * collection. This allows you to apply user input in determining the order of the collection
     *
     * @param attribute The name of the attribute from the pojo that will be sorted.
     * @param ascending If true, sort ascending. If false sort descending.
     *
     * @return A CpoOrderBy object to be passed into retrieveObjects.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public CpoOrderBy newOrderBy(String attribute, boolean ascending)
        throws CpoException {
            return new JdbcCpoOrderBy(attribute, ascending);
    }

    /**
     * <code>newOrderBy</code> allows you to dynamically change the order of the objects in the resulting 
     * collection. This allows you to apply user input in determining the order of the collection
     *
     * @param attribute The name of the attribute from the pojo that will be sorted.
     * @param ascending If true, sort ascending. If false sort descending.
     * @param function A string which represents a datasource function that will be called on the attribute.
     * 					must be contained in the function string. The attribute name will be replaced at run-time with its 
     * 					datasource counterpart
     *
     * @return A CpoOrderBy object to be passed into retrieveObjects.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function)
        throws CpoException {
        return new JdbcCpoOrderBy(attribute, ascending, function);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    public CpoWhere newWhere() throws CpoException {
        return new JdbcCpoWhere();
    }

    /**
     * DOCUMENT ME!
     *
     * @param logical DOCUMENT ME!
     * @param attr DOCUMENT ME!
     * @param comp DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    public CpoWhere newWhere(int logical, String attr, int comp, Object value)
        throws CpoException {
            return new JdbcCpoWhere(logical, attr, comp, value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param logical DOCUMENT ME!
     * @param attr DOCUMENT ME!
     * @param comp DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param not DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    public CpoWhere newWhere(int logical, String attr, int comp, Object value, boolean not) throws CpoException {
        return new JdbcCpoWhere(logical, attr, comp, value, not);
    }

    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this object
     * exists in the datasource. If it exists, the object is updated in the datasource If the
     * object does not exist, then it is created in the datasource.  This method stores the object
     * in the datasource. This method uses the default EXISTS, CREATE, and UPDATE query groups specified 
     * for this object.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.persistObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *
     * @return A count of the number of objects persisted
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public long persistObject(Object obj)
        throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.PERSIST_GROUP, null);
    }

    /**
     * Persists the Object into the datasource. The CpoAdapter will check to see if this object
     * exists in the datasource. If it exists, the object is updated in the datasource If the
     * object does not exist, then it is created in the datasource.  This method stores the object
     * in the datasource.<br>
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.persistObject("persistSomeObject",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * @return A count of the number of objects persisted
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public long persistObject(String name, Object obj)
        throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.PERSIST_GROUP, name);
    }

    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if
     * this object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.  This method stores the
     * object in the datasource. The objects in the collection will be treated as one transaction,
     * meaning that if one  of the objects fail being inserted or updated in the datasource then
     * the entire collection will be rolled back.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.persistObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public long persistObjects(Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.PERSIST_GROUP, null);
    }

    /**
     * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if
     * this object exists in the datasource. If it exists, the object is updated in the datasource
     * If the object does not exist, then it is created in the datasource.  This method stores the
     * object in the datasource. The objects in the collection will be treated as one transaction,
     * meaning that if one  of the objects fail being inserted or updated in the datasource then
     * the entire collection will be rolled back.
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.persistObjects("myPersist",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
     *        execute to persist the object.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * @return DOCUMENT ME!
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see #existsObject
     * @see #insertObject
     * @see #updateObject
     */
    public long persistObjects(String name, Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.PERSIST_GROUP, name);
    }


    /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource, an exception will be thrown. The input  object is used to specify
     *        the search criteria, the output  object is populated with the results of the query.
     *
     * @return An object of the same type as the result parameter that is filled in as specified
     *         the metadata for the retireve.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public Object retrieveObject(Object obj)
        throws CpoException {
        Object o=processSelectGroup(obj, null);

        return (o);
    }

     /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
     * @param name DOCUMENT ME!
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. If the object does not exist
     *        in the datasource, an exception will be thrown. The input  object is used to specify
     *        the search criteria, the output  object is populated with the results of the query.
     * @return An object of the same type as the result parameter that is filled in as specified
     *         the metadata for the retireve.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public Object retrieveObject(String name, Object obj)
        throws CpoException {
        Object o=processSelectGroup(obj, name);

        return (o);
    }
    
    /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.  If the retrieve query defined for this objects returns more than one row, an
     * exception will be thrown.
     * @param name The filter name which tells the datasource which objects should be returned. The
     *        name also signifies what data in the object will be  populated.
     * @param criteria This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to specify the parameters used to retrieve the  collection of objects.
     * @param result This is an object that has been defined within the metadata of the datasource.
     *        If the class is not defined an exception will be thrown. If the object does not
     *        exist in the datasource, an exception will be thrown. This object is used to specify
     *        the object type that will be returned in the  collection.
     * @param where The <code>CpoWhere</code> object that defines the constraints that should be
     *              used when retrieving objects
     * @param orderBy The <code>CpoOrderBy</code> object that defines the order in which objects
     *                should be returned
     * @return An object of the same type as the result parameter that is filled in as specified
     *         the metadata for the retireve.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */    
    public Object retrieveObject(String name, Object criteria, Object result, CpoWhere where,
        Collection orderBy) throws CpoException {
      Iterator it = processSelectGroup(name, criteria, result, where,orderBy, true).iterator();
      if (it.hasNext())
        return it.next();
      else
        return null;
    }
    
    /**
     * Retrieves the Object from the datasource. The assumption is that the object exists in the
     * datasource.
     * @param name The filter name which tells the datasource which objects should be returned. The
     *        name also signifies what data in the object will be  populated.
     * @param criteria This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to specify the parameters used to retrieve the  collection of objects.
     * @param result This is an object that has been defined within the metadata of the datasource.
     *        If the class is not defined an exception will be thrown. If the object does not
     *        exist in the datasource, an exception will be thrown. This object is used to specify
     *        the object type that will be returned in the  collection.
     * @param where The <code>CpoWhere</code> object that defines the constraints that should be
     *              used when retrieving objects
     * @param orderBy The <code>CpoOrderBy</code> object that defines the order in which objects
     *                should be returned
     * @return A collection of objects will be returned that meet the criteria  specified by obj.
     *         The objects will be of the same type as the Object  that was passed in. If no
     *         objects match the criteria, an empty collection will be returned
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public Collection retrieveObjects(String name, Object criteria, Object result, CpoWhere where,
        Collection orderBy) throws CpoException {
        return processSelectGroup(name, criteria, result, where, orderBy, false);
    }
    
    /**
     * Retrieves the Object from the datasource. This method returns an Iterator immediately. The 
     * iterator will get filled asynchronously by the cpo framework. The framework will stop supplying
     * the iterator with objects if the objectBufferSize is reached.
     * 
     * If the consumer of the iterator is processing records faster than the framework is filling it,
     * then the iterator will wait until it has data to provide.
     *
     * @param name The filter name which tells the datasource which objects should be returned. The
     *        name also signifies what data in the object will be  populated.
     * @param criteria This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to specify the parameters used to retrieve the  collection of objects.
     * @param result This is an object that has been defined within the metadata of the datasource.
     *        If the class is not defined an exception will be thrown. If the object does not
     *        exist in the datasource, an exception will be thrown. This object is used to specify
     *        the object type that will be returned in the  collection.
     * @param where The <code>CpoWhere</code> object that defines the constraints that should be
     *              used when retrieving objects
     * @param orderBy The <code>CpoOrderBy</code> object that defines the order in which objects
     *                should be returned
     * @param objectBufferSize the maximum number of objects that the Iterator is allowed to cache.
     *        Once reached, the CPO framework will halt processing records from the datasource.
     *
     * @return An iterator that will be fed objects from the CPO framework.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public Iterator retrieveObjects(String name, Object criteria, Object result, CpoWhere where,
        Collection orderBy, int objectBufferSize) throws CpoException {
        return new JdbcCpoIterator(100);
        //return processSelectGroup(name, criteria, result, where, orderBy, false);
    }


    /**
     * Allows you to perform a series of object interactions with the database. This method
     * pre-dates CpoTrxAdapter and can be used without a programmer needing to remember to call
     * <code>commit()</code> or <code>rollback()</code>. 
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			so = new SomeObject();<br>
     *	 		so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     *                  CpoObject cobj = new CpoObject(CpoAdapter.CREATE,"MyCreate",so);<br>
     * 			al.add(cobj);<br>
     *                  so = new SomeObject();<br>
     *	 		so.setId(3);<br>
     * 			so.setName("New Name");<br>
     *                  CpoObject cobj = new CpoObject(CpoAdapter.PERSIST,"MyPersist",so);<br>
     * 			al.add(cobj);<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.transactObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * 
     * @param coll This is a collection of <code>CpoObject</code> objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.

     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long transactObjects(Collection coll) throws CpoException {
        Connection c=null;
        Connection meta=null;
        long updateCount=0;

        try {
            c=getWriteConnection();
            if (metaEqualsWrite_){
                meta=c;
            } else {
                meta=getMetaConnection();
            }

            updateCount=transactObjects(coll, c, meta);
            commitConnection(c);
        } catch(Exception e) {
            // Any exception has to try to rollback the work;
            try {
                rollbackConnection(c);
            } catch(Exception re) {
            }
            
            if (e instanceof CpoException)
                throw (CpoException)e;
            else
                throw new CpoException("transactObjects(Collection coll) failed", e);
        }finally {
            closeConnection(c);
            closeConnection(meta);
        }

        return updateCount;
    }

    /**
     * DOCUMENT ME!
     *
     * @param coll DOCUMENT ME!
     * @param c DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     * @throws RemoteException DOCUMENT ME!
     *
     */
    protected long transactObjects(Collection coll, Connection c, Connection meta)
        throws CpoException {
        long updateCount=0;
        Iterator it=null;
        CpoObject cpoObject=null;

        it=coll.iterator();

        while(it.hasNext()) {
            cpoObject=(CpoObject) it.next();

            if(cpoObject.getObject() instanceof java.util.Collection) {
                updateCount+=processUpdateGroup((Collection) cpoObject.getObject(),
                    GROUP_IDS[cpoObject.getOperation()], cpoObject.getName(), c, meta);
            } else {
                updateCount+=processUpdateGroup((Object) cpoObject.getObject(),
                    GROUP_IDS[cpoObject.getOperation()], cpoObject.getName(), c, meta);
            }
        }

        return updateCount;
    }

    /**
     * Update the Object in the datasource. The CpoAdapter will check to see if the object
     * exists in the datasource. If it exists then the object will be updated. If it does not exist,
     * an exception will be thrown
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.updateObject(so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     *        
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long updateObject(Object obj) throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.UPDATE_GROUP, null);
    }

    /**
     * Update the Object in the datasource. The CpoAdapter will check to see if the object
     * exists in the datasource. If it exists then the object will be updated. If it does not exist,
     * an exception will be thrown
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = new SomeObject();<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			so.setId(1);<br>
     * 			so.setName("SomeName");<br>
     * 			try{<br>
     * 				</code><ul><code>
     * 				cpo.updateObject("updateSomeObject",so);<br>
     * 				</code></ul><code>
     * 			} catch (CpoException ce) {<br>
     * 				</code><ul><code>
     * 				// Handle the error
     * 				</code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the UPDATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown.
     * 
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long updateObject(String name, Object obj) throws CpoException {
        return processUpdateGroup(obj, JdbcCpoAdapter.UPDATE_GROUP, name);
    }

    /**
     * Updates a collection of Objects in the datasource. The assumption is that the objects
     * contained in the collection exist in the datasource.  This method stores the object in the
     * datasource. The objects in the collection will be treated as one transaction, meaning that
     * if one of the objects fail being updated in the datasource then the entire collection will
     * be rolled back, if supported by the datasource.  
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.updateObjects(al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * 
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     *
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long updateObjects(Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.UPDATE_GROUP, null);
    }

    /**
     * Updates a collection of Objects in the datasource. The assumption is that the objects
     * contained in the collection exist in the datasource.  This method stores the object in the
     * datasource. The objects in the collection will be treated as one transaction, meaning that
     * if one of the objects fail being updated in the datasource then the entire collection will
     * be rolled back, if supported by the datasource.  
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     * 			ArrayList al = new ArrayList();<br>
     * 			for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				al.add(so);<br>
     *	 		}<br>
     * 			</code></ul><code>
     *	 			try{<br>
     * 					</code><ul><code>
     * 					cpo.updateObjects("myUpdate",al);<br>
     * 					</code></ul><code>
     *	 			} catch (CpoException ce) {<br>
     * 					</code><ul><code>
     *	 				// Handle the error<br>
     * 					</code></ul><code>
     * 				}<br>
     * 				</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @param name The <code>String</code> name of the UPDATE Query group that will be used to create the object
     *             in the datasource. <code>null</code> signifies that the default rules will be used.
     * @param coll This is a collection of objects that have been defined within  the metadata of
     *        the datasource. If the class is not defined an exception will be thrown.
     * 
     * @return The number of objects updated in the datasource
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public long updateObjects(String name, Collection coll)
        throws CpoException {
        return processUpdateGroup(coll, JdbcCpoAdapter.UPDATE_GROUP, name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected void setContext(Context context) throws CpoException {
        try {
            if(context==null) {
                context_=new InitialContext();
            } else {
                context_=context;
            }
        } catch(NamingException e) {
            throw new CpoException("Error setting Context", e);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Context getContext() {
        return context_;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected HashMap getDataSourceMap() {
        return dataSourceMap_;
    }
    
    protected void setDataSourceMap(HashMap dsMap) {
        dataSourceMap_=dsMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @param type DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param c DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected String getGroupType(Object obj, String type, String name, Connection c, Connection meta)
        throws CpoException {
        String retType=type;
        long objCount=-1;

        if(JdbcCpoAdapter.PERSIST_GROUP.equals(retType)==true) {
            objCount=existsObject(name, obj, c, meta);

            if(objCount==0) {
                retType=JdbcCpoAdapter.CREATE_GROUP;
            } else if(objCount==1) {
                retType=JdbcCpoAdapter.UPDATE_GROUP;
            } else {
                throw new CpoException("Cannot Persist Object To Multiple DataSource Objects");
            }
        }

        return retType;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected JdbcMetaClass getMetaClass(Object obj, Connection c) throws CpoException {
        JdbcMetaClass jmc=null;
        String className=null;
        Class objClass=null;
        HashMap metaClassMap=null;

        if(obj!=null) {
            objClass=obj.getClass();
            className=objClass.getName();

            synchronized(getDataSourceMap()) {
                metaClassMap=getMetaClassMap();
                jmc=(JdbcMetaClass) metaClassMap.get(className);

                if(jmc==null) {
                    jmc=loadMetaClass(objClass, className, c);
                    metaClassMap.put(className, jmc);
                    logger.debug("Loading Class:"+className);
                }
            }
        }

        return jmc;
    }

    // All meta data will come from the meta datasource.
    protected HashMap getMetaClassMap() {
        HashMap dataSourceMap=getDataSourceMap();
        String dataSourceName=getMetaDataSourceName();
        HashMap metaClassMap=(HashMap) dataSourceMap.get(dataSourceName);

        if(metaClassMap==null) {
            metaClassMap=new HashMap();
            dataSourceMap.put(dataSourceName, metaClassMap);
        }

        return metaClassMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Connection getReadConnection() throws CpoException {
        Connection connection=writeConnection_;

        if(connection==null) {
            try {
                if(invalidReadConnection_==false) {
                    connection=getReadDataSource().getConnection();
                } else {
                    connection=getWriteDataSource().getConnection();
                }
                connection.setAutoCommit(false);
            } catch(Exception e) {
                invalidReadConnection_=true;

                String msg="getReadConnection(): failed";
                logger.error(msg, e);

                try {
                    connection=getWriteDataSource().getConnection();
                    connection.setAutoCommit(false);
                } catch(SQLException e2) {
                    msg="getWriteConnection(): failed";
                    logger.error(msg, e2);
                    throw new CpoException(msg, e2);
                }
           }
        }

        return connection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param readDataSource DOCUMENT ME!
     */
    protected void setReadDataSource(DataSource readDataSource) {
        readDataSource_=readDataSource;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected DataSource getReadDataSource() {
        return readDataSource_;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Connection getWriteConnection() throws CpoException {
        Connection connection=writeConnection_;

        if(connection==null) {
            try {
                connection=getWriteDataSource().getConnection();
                connection.setAutoCommit(false);
            } catch(SQLException e) {
                String msg="getWriteConnection(): failed";
                logger.error(msg, e);
                throw new CpoException(msg, e);
            }
        }

        return connection;
    }
    
    protected Connection getStaticConnection(){
        return writeConnection_;
    }

    
    protected void setStaticConnection(Connection c){
            writeConnection_=c;
    }

    /**
     * DOCUMENT ME!
     *
     * @param writeDataSource DOCUMENT ME!
     */
    protected void setWriteDataSource(DataSource writeDataSource) {
        writeDataSource_=writeDataSource;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected DataSource getWriteDataSource() {
        return writeDataSource_;
    }
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Connection getMetaConnection() throws CpoException {
        Connection connection=null;

        try {
            connection=getMetaDataSource().getConnection();
            connection.setAutoCommit(false);
        } catch(SQLException e) {
            String msg="getMetaConnection(): failed";
            logger.error(msg, e);
            throw new CpoException(msg, e);
        }
        return connection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param metaDataSource DOCUMENT ME!
     */
    protected void setMetaDataSource(DataSource metaDataSource) {
        metaDataSource_=metaDataSource;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected DataSource getMetaDataSource() {
        return metaDataSource_;
    }

    /**
     * DOCUMENT ME!
     *
     * @param metaDataSourceName DOCUMENT ME!
     */
    protected void setMetaDataSourceName(String metaDataSourceName) {
        metaDataSourceName_=metaDataSourceName;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String getMetaDataSourceName() {
        return metaDataSourceName_;
    }

    /**
     * DOCUMENT ME!
     *
     * @param connection DOCUMENT ME!
     */
    protected void closeConnection(Connection connection) {
        try {
            if((connection!=null) && (connection.isClosed()==false) && connection!=writeConnection_) {
                connection.close();
            }
        } catch(SQLException e) {
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param connection DOCUMENT ME!
     */
    protected void commitConnection(Connection connection) {
        try {
            if(connection!=null&&connection!=writeConnection_) {
                connection.commit();
            }
        } catch(SQLException e) {
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param connection DOCUMENT ME!
     */
    protected void rollbackConnection(Connection connection) {
        try {
            if(connection!=null&&connection!=writeConnection_) {
                connection.rollback();
            }
        } catch(SQLException e) {
        }
    }
  
  


    /**
     * Executes an Object whose MetaData contains a stored procedure. An assumption is that the
     * object exists in the datasource.
     *
     * @param name The filter name which tells the datasource which objects should be returned. The
     *        name also signifies what data in the object will be populated.
     * @param criteria This is an object that has been defined within the metadata of the
     *        datasource. If the class is not defined an exception will be thrown. If the object
     *        does not exist in the datasource, an exception will be thrown. This object is used
     *        to populate the IN parameters used to retrieve the collection of objects.
     * @param result This is an object that has been defined within the metadata of the datasource.
     *        If the class is not defined an exception will be thrown. If the object does not
     *        exist in the datasource, an exception will be thrown. This object defines the object
     *        type that will be returned in the
     *
     * @return A result object populate with the OUT parameters
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Object processExecuteGroup(String name, Object criteria, Object result)
        throws CpoException {
        Connection c=null;
        Connection meta=null;
        Object obj=null;

        try {
            c=getWriteConnection();
            if (metaEqualsWrite_){
                meta=c;
            } else {
                meta=getMetaConnection();
            }
            obj=processExecuteGroup(name, criteria, result, c, meta);
            commitConnection(c);
        } catch(Exception e) {
            // Any exception has to try to rollback the work;
            try {
                rollbackConnection(c);
            } catch(Exception re) {
            }
            
            if (e instanceof CpoException)
                throw (CpoException)e;
            else
            throw new CpoException("processExecuteGroup(String name, Object criteria, Object result) failed",
                e);
        } finally {
            closeConnection(c);
            closeConnection(meta);
        }

        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param criteria DOCUMENT ME!
     * @param result DOCUMENT ME!
     * @param conn DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Object processExecuteGroup(String name, Object criteria, Object result,
        Connection conn, Connection metaCon) throws CpoException {
        CallableStatement cstmt=null;
        ArrayList queryGroup=null;
        JdbcQuery jq=null;
        JdbcMetaClass jmcCriteria=null;
        JdbcMetaClass jmcResult=null;
        Object returnObject=null;

        //Object[] setterArgs = {null};
        Class jmcClass=null;
        ArrayList parameters=null;
        JdbcParameter parameter=null;
        JdbcAttribute attribute=null;
        JdbcCallableStatementFactory jcsf=null;

        //Object[] getterArgs = {};
        int j=0;
        int i=0;

        try {
            jmcCriteria=getMetaClass(criteria, metaCon);
            jmcResult=getMetaClass(result, metaCon);
            queryGroup=(ArrayList) jmcCriteria.getQueryGroup(JdbcCpoAdapter.EXECUTE_GROUP, name);
            logger.info("===================processExecuteGroup ("+name+") Count<"+
                queryGroup.size()+">=========================");

            jmcClass=jmcResult.getJmcClass();
            try{
                returnObject=jmcClass.newInstance();
            }catch(IllegalAccessException iae){
                throw new CpoException("Unable to access the constructor of the Return Object",iae);
            }catch(InstantiationException iae){
                throw new CpoException("Unable to instantiate Return Object",iae);
            }

            // Loop through the queries and process each one
            for(i=0; i<queryGroup.size(); i++) {
                // Get the current call
                jq=(JdbcQuery) queryGroup.get(i);
                
                jcsf = new JdbcCallableStatementFactory(conn, this, jq, criteria);
                
                logger.debug("Executing Call:"+jmcCriteria.getName()+":"+name);
                
                cstmt = jcsf.getCallableStatement();

                cstmt.execute();
                
                jcsf.release();

                logger.debug("Processing Call:"+jmcCriteria.getName()+":"+name);

                // Add Code here to go through the parameters, find record sets,
                // and process them
                // Process the non-record set out params and make it the first
                // object in the collection
                
                
                
                // Loop through the OUT Parameters and set them in the result
                // object
                parameters = jcsf.getOutParameters();
                if(!parameters.isEmpty()) {
                    for(j=0; j<parameters.size(); j++) {
                        parameter=(JdbcParameter) parameters.get(j);

                        if(parameter.isOutParameter()) {
                            attribute=parameter.getAttribute();
                            attribute.invokeSetter(returnObject, cstmt, j+1);
                        }
                    }
                }
                
                cstmt.close();
            }
        }catch(SQLException e) {
            String msg="ProcessExecuteGroup(String name, Object criteria, Object result, Connection conn) failed. SQL=";
                if (jq!=null) msg+=jq.getText();
            logger.error(msg, e);
            throw new CpoException(msg, e);
        } finally {
            if (cstmt!=null){
                try{ cstmt.close(); } catch(Exception e) {}
            }
            if (jcsf!=null)
                jcsf.release();
        }

        return returnObject;
    }

    /**
     * Retrieves the Object from the datasource.
     *
     * @param obj This is an object that has been defined within the metadata of the datasource. If
     *        the class is not defined an exception will be thrown. The input object is used to
     *        specify the search criteria.
     * @param groupName The name which identifies which RETRIEVE Query group to execute to retrieve
     *        the object.
     *
     * @return A populated object of the same type as the Object passed in as a parameter. If no
     *         objects match the criteria a NULL will be returned.
     *
     * @exception CpoException the retrieve query defined for this objects returns more than one
     *            row, an exception will be thrown.
     */
    protected Object processSelectGroup(Object obj, String groupName)
        throws CpoException {
        Connection c=null;
        Connection meta=null;
        Object result=null;

        try {
            c=getReadConnection();
            if (metaEqualsWrite_){
                meta=c;
            } else {
                meta=getMetaConnection();
            }
            result=processSelectGroup(obj, groupName, c, meta);
            
            // The select may have a for update clause on it
            // Since the connection is cached we need to get rid of this
            commitConnection(c);
        } catch(Exception e) {
            // Any exception has to try to rollback the work;
            try {
                rollbackConnection(c);
            } catch(Exception re) {
            }
            
            if (e instanceof CpoException)
                throw (CpoException)e;
            else
            throw new CpoException("processSelectGroup(Object obj, String groupName) failed",
                e);
        } finally {
            closeConnection(c);
            closeConnection(meta);
        }



        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @param groupName DOCUMENT ME!
     * @param con DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Object processSelectGroup(Object obj, String groupName, Connection con, Connection metaCon)
        throws CpoException {
        PreparedStatement ps=null;
        ResultSet rs=null;
        ResultSetMetaData rsmd=null;
        JdbcQuery jq=null;
        JdbcMetaClass jmc=null;
        ArrayList queryGroup=null;
        JdbcAttribute attribute=null;
        Object criteriaObj = obj;
        boolean recordsExist=false;
        
        int recordCount=0;
        int attributesSet=0;

        int i;
        int k;
        HashMap jmcAttrMap=null;
        Object rObj=null;

        try {
            jmc=getMetaClass(criteriaObj, metaCon);
            queryGroup=jmc.getQueryGroup(JdbcCpoAdapter.RETRIEVE_GROUP, groupName);
            jmcAttrMap=jmc.getAttributeMap();

            logger.info("=================== Class=<"+criteriaObj.getClass()+"> Type=<"+JdbcCpoAdapter.RETRIEVE_GROUP+"> Name=<"+groupName+"> =========================");
           
            try{
                rObj=jmc.getJmcClass().newInstance();
            }catch(IllegalAccessException iae){
                if (obj!=null)
                    logger.error("=================== Could not access default constructor for Class=<"+obj.getClass()+"> ==================");
                else
                    logger.error("=================== Could not access default constructor for class ==================");

                throw new CpoException("Unable to access the constructor of the Return Object",iae);
            }catch(InstantiationException iae){
                throw new CpoException("Unable to instantiate Return Object",iae);
            }


            for(i=0; i<queryGroup.size(); i++) {
                jq=(JdbcQuery) queryGroup.get(i);

                JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, jq, criteriaObj, null);
                ps=jpsf.getPreparedStatement();

                // insertions on
                // selectgroup
                rs=ps.executeQuery();
                jpsf.release();

                if(rs.isBeforeFirst()==true) {
                    rsmd=rs.getMetaData();

                    if((rsmd.getColumnCount()==2) &&
                            "CPO_ATTRIBUTE".equalsIgnoreCase(rsmd.getColumnName(1)) &&
                            "CPO_VALUE".equalsIgnoreCase(rsmd.getColumnName(2))) {
                        while(rs.next()) {
                            recordsExist=true;
                            recordCount++;
                            attribute=(JdbcAttribute) (jmcAttrMap.get(rs.getString(1)));

                            if(attribute!=null) {
                                attribute.invokeSetter(rObj, rs, 2);
                                attributesSet++;
                            }
                        }
                    } else if(rs.next()) {
                        recordsExist=true;
                        recordCount++;
                        for(k=1; k<=rsmd.getColumnCount(); k++) {
                            attribute=(JdbcAttribute) (jmcAttrMap.get(rsmd.getColumnName(k).toUpperCase()));
                            
                            if(attribute!=null) {
                                attribute.invokeSetter(rObj, rs, k);
                                attributesSet++;
                            }
                        }

                        if(rs.next()) {
                            String msg="ProcessSelectGroup(Object, String) failed: Multiple Records Returned";
                            logger.error(msg);
                            throw new CpoException(msg);
                        }
                    }
                    criteriaObj = rObj;

                }
                
                rs.close();
                rs=null;
                ps.close();
                ps=null;
            }
            
            if (!recordsExist) {
                rObj=null;
                logger.info("=================== 0 Records - 0 Attributes - Class=<"+criteriaObj.getClass()+"> Type=<"+JdbcCpoAdapter.RETRIEVE_GROUP+"> Name=<"+groupName+"> =========================");
            } else {
                logger.info("=================== "+recordCount+" Records - "+attributesSet+" Attributes - Class=<"+criteriaObj.getClass()+">  Type=<"+JdbcCpoAdapter.RETRIEVE_GROUP+"> Name=<"+groupName+"> =========================");
            }

        } catch(SQLException e) {
            String msg="ProcessSeclectGroup(Object) failed: "+e.getMessage();
            logger.error(msg, e);
            rObj=null;
            throw new CpoException(msg, e);
        } finally {
            if(rs!=null) {
                try {
                    rs.close();
                } catch(Exception e) {
                }
            }

            if(ps!=null) {
                try {
                    ps.close();
                } catch(Exception e) {
                }
            }

        }

        return rObj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param criteria DOCUMENT ME!
     * @param result DOCUMENT ME!
     * @param where DOCUMENT ME!
     * @param orderBy DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Collection processSelectGroup(String name, Object criteria, Object result,
        CpoWhere where, Collection orderBy, boolean useRetrieve) throws CpoException {
        Connection con=null;
        Connection meta=null;
        Collection resultSet=new ArrayList();

        try {
            con=getReadConnection();
            if (metaEqualsWrite_){
                meta=con;
            } else {
                meta=getMetaConnection();
            }
            resultSet=processSelectGroup(name, criteria, result, where, orderBy, con, meta, useRetrieve);
            // The select may have a for update clause on it
            // Since the connection is cached we need to get rid of this
            commitConnection(con);
        } catch(Exception e) {
            // Any exception has to try to rollback the work;
            try {
                rollbackConnection(con);
            } catch(Exception re) {
            }
            
            if (e instanceof CpoException)
                throw (CpoException)e;
            else
            throw new CpoException("processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed",
                e);
        } finally {
            closeConnection(con);
            closeConnection(meta);
        }

        return resultSet;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param criteria DOCUMENT ME!
     * @param result DOCUMENT ME!
     * @param where DOCUMENT ME!
     * @param orderBy DOCUMENT ME!
     * @param con DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected Collection processSelectGroup(String name, Object criteria, Object result,
        CpoWhere where, Collection orderBy, Connection con, Connection metaCon, boolean useRetrieve)
        throws CpoException {
        PreparedStatement ps=null;
        ArrayList queryGroup=null;
        JdbcQuery jq=null;
        JdbcMetaClass jmcCriteria=null;
        JdbcMetaClass jmcResult=null;
        ResultSet rs=null;
        ResultSetMetaData rsmd=null;
        int columnCount=0;
        int k=0;
        Object obj=null;
        ArrayList resultSet=new ArrayList();
        Class jmcClass=null;
        HashMap jmcAttrMap=null;
        String sqlText=null;
        Collection bindValues=new ArrayList();
        JdbcAttribute[] attributes=null;
        JdbcPreparedStatementFactory jpsf=null;
        int i=0;

        try {
            jmcCriteria=getMetaClass(criteria, metaCon);
            jmcResult=getMetaClass(result, metaCon);
            if (useRetrieve){
                logger.info("=================== Class=<"+criteria.getClass()+"> Type=<"+JdbcCpoAdapter.RETRIEVE_GROUP+"> Name=<"+name+"> =========================");
                queryGroup=(ArrayList) jmcCriteria.getQueryGroup(JdbcCpoAdapter.RETRIEVE_GROUP, name);
            } else {
                logger.info("=================== Class=<"+criteria.getClass()+"> Type=<"+JdbcCpoAdapter.LIST_GROUP+"> Name=<"+name+"> =========================");
                queryGroup=(ArrayList) jmcCriteria.getQueryGroup(JdbcCpoAdapter.LIST_GROUP, name);
            }
            sqlText=buildSql(jmcCriteria, "", where, orderBy, bindValues);

            for(i=0; i<queryGroup.size(); i++) {
                jq=(JdbcQuery) queryGroup.get(i);

                jpsf = new JdbcPreparedStatementFactory(con, this, jq, criteria, sqlText, bindValues);
                ps=jpsf.getPreparedStatement();

                logger.debug("Retrieving Records");

                rs=ps.executeQuery();
                jpsf.release();

                logger.debug("Processing Records");

                rsmd=rs.getMetaData();

                jmcClass=jmcResult.getJmcClass();
                jmcAttrMap=jmcResult.getAttributeMap();
                columnCount=rsmd.getColumnCount();

                attributes=new JdbcAttribute[columnCount+1];

                for(k=1; k<=columnCount; k++) {
                    attributes[k]=(JdbcAttribute) jmcAttrMap.get(rsmd.getColumnName(k));
                }

                while(rs.next()) {
                    try{
                        obj=jmcClass.newInstance();
                    }catch(IllegalAccessException iae){
                        if (result!=null)
                            logger.error("=================== Could not access default constructor for Class=<"+result.getClass()+"> ==================");
                        else
                            logger.error("=================== Could not access default constructor for class ==================");

                        throw new CpoException("Unable to access the constructor of the Return Object",iae);
                    }catch(InstantiationException iae){
                        throw new CpoException("Unable to instantiate Return Object",iae);
                    }

                    for(k=1; k<=columnCount; k++) {
                        if(attributes[k]!=null) {
                            attributes[k].invokeSetter(obj, rs, k);
                        }
                    }

                    resultSet.add(obj);
                }

                try {
                    rs.close();
                } catch(Exception e) {
                }

                try {
                    ps.close();
                } catch(Exception e) {
                }

                logger.info("=================== "+resultSet.size()+" Records - Class=<"+criteria.getClass()+"> Type=<"+JdbcCpoAdapter.LIST_GROUP+"> Name=<"+name+"> ====================");
            }
        } catch(SQLException e) {
            String msg=
                "ProcessSelectGroup(String name, Object criteria, Object result, CpoWhere where, Collection orderBy, Connection con) failed. SQL="+
                sqlText+" Error:";
            logger.error(msg, e);
            throw new CpoException(msg, e);
        } finally {
            if(rs!=null) {
                try {
                    rs.close();
                } catch(Exception e) {
                }
            }

            if(ps!=null) {
                try {
                    ps.close();
                } catch(Exception e) {
                }
            }
        }

        return resultSet;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @param groupType DOCUMENT ME!
     * @param groupName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected long processUpdateGroup(Object obj, String groupType, String groupName)
        throws CpoException {
        Connection c=null;
        Connection meta=null;
        long updateCount=0;

        try {
            c=getWriteConnection();

            if (metaEqualsWrite_){
                meta=c;
            } else {
                meta=getMetaConnection();
            }
            updateCount=processUpdateGroup(obj, groupType, groupName, c, meta);
            commitConnection(c);
        } catch(Exception e) {
            // Any exception has to try to rollback the work;
            try {
                rollbackConnection(c);
            } catch(Exception re) {
            }
            
            if (e instanceof CpoException)
                throw (CpoException)e;
            else
            throw new CpoException("processUdateGroup(Object obj, String groupType, String groupName) failed",
                e);
        } finally {
            closeConnection(c);
            closeConnection(meta);
        }

        return updateCount;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @param groupType DOCUMENT ME!
     * @param groupName DOCUMENT ME!
     * @param con DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected long processUpdateGroup(Object obj, String groupType, String groupName, Connection con, Connection metaCon)
        throws CpoException {
        JdbcMetaClass jmc=null;
        ArrayList queryGroup=null;
        PreparedStatement ps=null;
        JdbcQuery jq=null;
        JdbcPreparedStatementFactory jpsf=null;
        int i;
        long updateCount=0;

        try {
            jmc=getMetaClass(obj, metaCon);
            queryGroup=jmc.getQueryGroup(getGroupType(obj, groupType, groupName, con, metaCon), groupName);
            logger.info("=================== Class=<"+obj.getClass()+"> Type=<"+groupType+"> Name=<"+groupName+"> =========================");

            int numRows=0;

            for(i=0; i<queryGroup.size(); i++) {
                jq=(JdbcQuery) queryGroup.get(i);
                jpsf = new JdbcPreparedStatementFactory(con, this, jq, obj, null);
                ps=jpsf.getPreparedStatement();
                numRows+=ps.executeUpdate();
                jpsf.release();
                ps.close();
            }
            logger.info("=================== "+numRows+" Updates - Class=<"+obj.getClass()+"> Type=<"+groupType+"> Name=<"+groupName+"> =========================");

            if(numRows>0) {
                updateCount++;
            }
        } catch (CpoException ce) {
            throw ce; // just send it on. No need to attach anymore info
        } catch(SQLException e) {
            String msg="ProcessUpdateGroup failed:"+groupType+","+groupName+","+
                obj.getClass().getName();
            logger.error("bound values:"+this.parameterToString(jq, obj));
            logger.error(msg, e);
            throw new CpoException(msg, e);
        } finally {
            if(ps!=null) {
                try {
                    ps.close();
                } catch(Exception e) {
                }
            }
            if (jpsf!=null)
                jpsf.release();

        }

        return updateCount;
    }
    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     * @param groupType DOCUMENT ME!
     * @param groupName DOCUMENT ME!
     * @param con DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected long processBatchUpdateGroup(Object[] arr, String groupType, String groupName, Connection con, Connection metaCon)
        throws CpoException {
        JdbcMetaClass jmc=null;
        ArrayList queryGroup=null;
        PreparedStatement ps=null;
        JdbcQuery jq=null;
        JdbcPreparedStatementFactory jpsf=null;
        long updateCount=0;
        int[] updates=null;

        try {
            jmc=getMetaClass(arr[0], metaCon);
            queryGroup=jmc.getQueryGroup(getGroupType(arr[0], groupType, groupName, con, metaCon), groupName);
            logger.info("=================== BATCH - Class=<"+arr[0].getClass()+"> Type=<"+groupType+"> Name=<"+groupName+"> =========================");

            int numRows=0;
            
            // Only Batch if there is only one query 
            if (queryGroup.size()==1){
                jq = (JdbcQuery) queryGroup.get(0);
                jpsf = new JdbcPreparedStatementFactory(con, this, jq, arr[0], null);
                ps=jpsf.getPreparedStatement();
                ps.addBatch();
                for (int j=1; j<arr.length; j++){
                    jpsf.bindParameters(arr[j]);
                    ps.addBatch();
                }
                updates=ps.executeBatch();
                jpsf.release();
                ps.close();
                for (int j=0; j<updates.length; j++){
                    if (updates[j]>0){
                        numRows+=updates[j];
                    } else if (updates[j]==PreparedStatement.SUCCESS_NO_INFO) {
                        // something updated but we do not know what or how many so default to one.
                        numRows++;
                    }
                }
            
            } else {
                for(int j=0; j<arr.length; j++){
                    for(int i=0; i<queryGroup.size(); i++) {
                        jq=(JdbcQuery) queryGroup.get(i);
                        jpsf = new JdbcPreparedStatementFactory(con, this, jq, arr[j], null);
                        ps=jpsf.getPreparedStatement();
                        numRows+=ps.executeUpdate();
                        jpsf.release();
                        ps.close();
                    }
                }
            }
            logger.info("=================== BATCH - "+numRows+" Updates - Class=<"+arr[0].getClass()+"> Type=<"+groupType+"> Name=<"+groupName+"> =========================");

            if(numRows>0) {
                updateCount++;
            }
        } catch (CpoException ce) {
            throw ce; // just send it on. No need to attach anymore info
        } catch(SQLException e) {
            String msg="ProcessUpdateGroup failed:"+groupType+","+groupName+","+
            arr[0].getClass().getName();
            logger.error("bound values:"+this.parameterToString(jq, arr[0]));
            logger.error(msg, e);
            throw new CpoException(msg, e);
        } finally {
            if(ps!=null) {
                try {
                    ps.close();
                } catch(Exception e) {
                }
            }
            if (jpsf!=null)
                jpsf.release();

        }

        return updateCount;
    }

    /**
     * DOCUMENT ME!
     *
     * @param coll DOCUMENT ME!
     * @param groupType DOCUMENT ME!
     * @param groupName DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected long processUpdateGroup(Collection coll, String groupType, String groupName)
        throws CpoException {
        Connection c=null;
        Connection meta=null;
        long updateCount=0;

        try {
            c=getWriteConnection();
            if (metaEqualsWrite_){
                meta=c;
            } else {
                meta=getMetaConnection();
            }
            
            updateCount=processUpdateGroup(coll, groupType, groupName, c, meta);
            commitConnection(c);
        } catch(Exception e) {
            // Any exception has to try to rollback the work;
            try {
                rollbackConnection(c);
            } catch(Exception re) {
            }
            
            if (e instanceof CpoException)
                throw (CpoException)e;
            else
            throw new CpoException("processUpdateGroup(Collection coll, String groupType, String groupName) failed",
                e);
        } finally {
            closeConnection(c);
            closeConnection(meta);
        }

        return updateCount;
    }

    /**
     * DOCUMENT ME!
     *
     * @param coll DOCUMENT ME!
     * @param groupType DOCUMENT ME!
     * @param groupName DOCUMENT ME!
     * @param con DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    protected long processUpdateGroup(Collection coll, String groupType, String groupName,
        Connection con, Connection meta) throws CpoException {
        long updateCount=0;
        boolean batchProcessed = false;
        
        if(!coll.isEmpty()) {
            Object[] arr = coll.toArray();
            
            if (batchUpdatesSupported_&&!JdbcCpoAdapter.PERSIST_GROUP.equals(groupType)){
                Object obj1 = arr[0];
                boolean allEqual=true;
                for (int i=1; i<arr.length; i++){
                    if (!obj1.getClass().getName().equals(arr[i].getClass().getName())){
                        allEqual=false;
                        break;
                    }
                }
                if (allEqual){
                    updateCount=processBatchUpdateGroup(arr, groupType, groupName, con, meta);
                    batchProcessed=true;
                }
            } 

            if (!batchProcessed){
                for (int i=0; i<arr.length; i++){
                    updateCount+=processUpdateGroup(arr[i], groupType, groupName, con, meta);
                }
            }
        }

        return updateCount;
    }

    /**
     * DOCUMENT ME!
     *
     * @param jmc DOCUMENT ME!
     * @param sql DOCUMENT ME!
     * @param where DOCUMENT ME!
     * @param orderBy DOCUMENT ME!
     * @param bindValues DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    private String buildSql(JdbcMetaClass jmc, String sql, CpoWhere where, Collection orderBy,
        Collection bindValues) throws CpoException {
        StringBuffer sqlText=new StringBuffer();

        Iterator obIt=null;
        JdbcCpoOrderBy ob=null;
        JdbcWhereBuilder jwb=new JdbcWhereBuilder(jmc);
        JdbcCpoWhere jcw=(JdbcCpoWhere) where;

        sqlText.append(sql);

        // do the where stuff here when ready
        if(jcw!=null) {
            try{
                jcw.acceptDFVisitor(jwb);
            } catch (Exception e){
                throw new CpoException("Unable to build WHERE clause",e);
            }
            
            if (sqlText.indexOf(WHERE_MARKER)==-1)
                sqlText.append(jwb.getWhereClause());
            else 
                sqlText = replaceMarker(sqlText, WHERE_MARKER,jwb.getWhereClause());
            
            bindValues.addAll(jwb.getBindValues());
        }

        // do the order by stuff now
        if(orderBy!=null) {
            StringBuffer obBuff = new StringBuffer();
            obIt=orderBy.iterator();

            if(obIt.hasNext()) {
                obBuff.append(" ORDER BY ");
                ob=(JdbcCpoOrderBy) obIt.next();
                obBuff.append(ob.toString(jmc));
            }

            while(obIt.hasNext()) {
                obBuff.append(", ");
                ob=(JdbcCpoOrderBy) obIt.next();
                obBuff.append(ob.toString(jmc));
            }
            
            if (sqlText.indexOf(ORDERBY_MARKER)==-1)
                sqlText.append(obBuff);
            else
                sqlText=replaceMarker(sqlText, ORDERBY_MARKER, obBuff.toString());
        }

        return sqlText.toString();
    }
    
    protected StringBuffer replaceMarker(StringBuffer source, String marker, String replace){
        int attrOffset = 0;
        int fromIndex = 0;
        int mLength=marker.length();

        if(source!=null && source.length()>0) {
            while((attrOffset=source.indexOf(marker, fromIndex))!=-1){
                     source.replace(attrOffset,attrOffset+mLength-1, replace);
                     fromIndex+=attrOffset+mLength;
            }
        }

        return source;

    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param jmc DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    private void loadAttributeMap(String name, Connection c, JdbcMetaClass jmc)
        throws CpoException {
        String sql="select UPPER(cam.column_name), cam.attribute, cc.class_id, cam.column_type, cam.db_table, cam.db_column, cam.transform_class from cpo_attribute_map cam, cpo_class cc where cc.name = ? and cam.class_id = cc.class_id";
        PreparedStatement ps=null;
        ResultSet rs=null;
        HashMap aMap=null;
        HashMap cMap=null;
        String classId=null;
        String dbType=null;

        //JdbcParameter jp=null;
        JdbcAttribute attribute=null;
        boolean failed=false;
        StringBuffer failedMessage=new StringBuffer();

        if((c!=null) && (jmc!=null)) {
            try {
                ps=c.prepareStatement(sql);
                ps.setString(1, name);
                rs=ps.executeQuery();
                aMap=jmc.getAttributeMap();
                cMap=jmc.getColumnMap();

                if(rs.next()) {
                    classId=rs.getString(3);
                    jmc.setClassId(classId);

                    do {
                        try {
                            dbType=rs.getString(4);
                            attribute=new JdbcAttribute(jmc, rs.getString(2), dbType, rs.getString(1),
                                    rs.getString(5), rs.getString(6), rs.getString(7));
                            aMap.put(rs.getString(1), attribute);
                            cMap.put(attribute.getName(), attribute);
                        } catch(CpoException ce) {
                            failed=true;
                            failedMessage.append(ce.getMessage());
                            failedMessage.append("\r\n");
                        }
                    } while(rs.next());

                    if(failed==true) {
                        throw new CpoException("\r\nError processing Attributes for:"+name+"\r\n"+
                            failedMessage.toString());
                    }
                } else {
                    throw new CpoException("No Attributes found for class:"+name);
                }
            } catch(CpoException ce) {
                String msg="loadAttributeMap() failed:'"+sql+"' classname:"+name;
                logger.error(msg, ce);
                throw ce;
            } catch(Exception e) {
                String msg="loadAttributeMap() failed:'"+sql+"' classname:"+name;
                logger.error(msg, e);
                throw new CpoException(msg, e);
            } finally {
                if(rs!=null) {
                    try {
                        rs.close();
                    } catch(Exception e) {
                    }
                }

                if(ps!=null) {
                    try {
                        ps.close();
                    } catch(Exception e) {
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param objClass DOCUMENT ME!
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    private JdbcMetaClass loadMetaClass(Class objClass, String name, Connection c)
        throws CpoException {
        JdbcMetaClass jmc=null;

            jmc=new JdbcMetaClass(objClass, name);
            loadAttributeMap(name, c, jmc);
            loadQueryGroups(c, jmc);

        return jmc;
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param c DOCUMENT ME!
     * @param jmc DOCUMENT ME!
     *
     * @throws CpoException DOCUMENT ME!
     */
    private void loadQueryGroups(Connection c, JdbcMetaClass jmc)
        throws CpoException {
        String id = null;
        StringBuffer sqlBuffer=new StringBuffer();
        sqlBuffer.append("select ");
        sqlBuffer.append(
            " innr.group_type,innr.name,innr.query_id,innr.query_seq query_seq,cqt.sql_text,innr.param_seq param_seq,  cam.attribute, UPPER(cam.column_name), cam.column_type, innr.param_type ");
        sqlBuffer.append("from ");
        sqlBuffer.append("  cpo_query_text cqt,  ");
        sqlBuffer.append(
            "  (select cqg.group_type, cqg.name, cq.query_id, cq.seq_no query_seq,cqp.seq_no param_seq, cqp.attribute_id, cqp.param_type,cq.text_id,cq.seq_no,cqg.group_id ");
        sqlBuffer.append("   from cpo_query_group cqg, cpo_query cq ");
        sqlBuffer.append("   left outer join cpo_query_parameter cqp ");
        sqlBuffer.append("   on cq.query_id = cqp.query_id ");
        sqlBuffer.append("   where cqg.class_id = ? ");
        sqlBuffer.append("   and cqg.group_id = cq.group_id ) innr ");
        sqlBuffer.append(
            "left outer join cpo_attribute_map cam on innr.attribute_id = cam.attribute_id ");
        sqlBuffer.append("where cqt.text_id = innr.text_id ");
        sqlBuffer.append("order by innr.group_id asc, innr.query_seq asc, innr.param_seq  asc");

        String sql=sqlBuffer.toString();

        PreparedStatement ps=null;
        ResultSet rs=null;
        int oldSeq=1000;
        int newSeq=0;
        JdbcQuery jq=null;
        String groupType=null;

        if((c!=null) && (jmc!=null)) {
            try {
                id = jmc.getClassId();
                ps=c.prepareStatement(sql);
                ps.setString(1, id);
                rs=ps.executeQuery();

                while(rs.next()) {
                    newSeq=rs.getInt(6);

                    if(newSeq<=oldSeq) {
                        jq=new JdbcQuery();
                        jq.setQueryId(rs.getString(3));
                        jq.setText(rs.getString(5));
                        jq.setName(rs.getString(2));
                        jq.setType(rs.getString(1));

                        jmc.addQueryToGroup(jq);
                        logger.debug("Added QueryGroup:"+jmc.getName()+":"+jq.getType()+":"+
                            jq.getName());
                    }

                    JdbcAttribute attribute=(JdbcAttribute) jmc.getAttributeMap().get(rs.getString(
                                8));

                    if(attribute==null) {
                        // There may be queries with no params
                        newSeq=1000;
                        logger.debug("No Parameters for "+groupType+":"+jq.getName());

                        //throw new CpoException("Cannot Add Null Parameter to
                        // Parameter List");
                    } else {
                        JdbcParameter parameter=new JdbcParameter(attribute, rs.getString(10));
                        jq.getParameterList().add(parameter);
                        logger.debug("Added Parameter:"+
                            attribute.getName() //+ ":" + attribute.getDbName() + ":"
                            //+ attribute.getDbType() + ":"
                            +parameter.getType());
                    }

                    oldSeq=newSeq;
                }
            }  catch(SQLException e) {
                String msg="loadQueryGroups() falied:"+sql+":"+id;
                logger.error(msg, e);
                throw new CpoException(msg, e);
            } finally {
                if(rs!=null) {
                    try {
                        rs.close();
                    } catch(Exception e) {
                    }
                }

                if(ps!=null) {
                    try {
                        ps.close();
                    } catch(Exception e) {
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param jq DOCUMENT ME!
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private String parameterToString(JdbcQuery jq, Object obj) {
        ArrayList parameters=null;
        int j;
        JdbcParameter parameter=null;
        JdbcAttribute attribute=null;
        int type=-1;
        Class c=null;
        StringBuffer sb=new StringBuffer("Parameter list for ");

        if(jq==null) {
            return " null query.";
        }

        if(obj==null) {
            return " null object.";
        }

        sb.append(jq.getName()+" "+jq.getType());
        parameters=jq.getParameterList();

        for(j=1; j<=parameters.size(); j++) {
            parameter=(JdbcParameter) parameters.get(j-1);

            if(parameter!=null) {
                try {
                       attribute = parameter.getAttribute();
                       c = attribute.getGetters()[0].getReturnType();
                       type = attribute.getJavaSqlType();
                       if (c != null) {
                           sb.append(" col" + j + ":" + c.getName() + " type:"
                                   + type + " ");
                       } else {
                           sb.append(j + ":null type:" + type + " ");
                       }
                } catch(Exception e) {
                    String msg="parameterToString() Failed:";
                    logger.error(msg);
                }
            }
        }

        return sb.toString();
    }

//    public Class getSqlTypeClass(String javaSqlTypeName){
//        return JavaSqlTypes.getSqlTypeClass(javaSqlTypeName);
//    }

//    public Collection getSqlTypes() {
//        return JavaSqlTypes.getSqlTypes();
//    }
    
    /**
     * Provides a mechanism for the user to obtain a CpoTrxAdapter object. This object allows the
     * to control when commits and rollbacks occur on CPO.
     *   
     * <br>
     * Example:<br>
     * <code><br>
     * class SomeObject so = null;<br>
     * class CpoAdapter cpo = null;<br>
     * class CpoTrxAdapter cpoTrx = null;<br>
     * <br>
     * 		</code><ul><code>
     * 		try {<br>
     * 			</code><ul><code>
     * 			cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));<br>
     *                  cpoTrx = cpo.getCpoTrxAdapter();<br>
     * 			</code></ul><code>
     * 		} catch (CpoException ce) {<br>
     * 			</code><ul><code>
     * 			// Handle the error<br>
     * 			cpo = null;<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		<br>
     * 		if (cpo!=null) {<br>
     * 			</code><ul><code>
     *	 		try{<br>
     *                      </code><ul><code>
     *                      for (int i=0; i<3; i++){<br>
     * 				</code><ul><code>
     * 				so = new SomeObject();<br>
     *	 			so.setId(1);<br>
     * 				so.setName("SomeName");<br>
     * 				cpo.updateObject("myUpdate",so);<br>
     *                                  
     * 				</code></ul><code>
     *                      }<br>
     *                      cpoTrx.commit();<br>
     * 			    </code></ul><code>
     *	 		} catch (CpoException ce) {<br>
     *                      </code><ul><code>
     *                      // Handle the error<br>
     *                      cpoTrx.rollback();<br>
     *                      </code></ul><code>
     * 			}<br>
     * 			</code></ul><code>
     * 		}<br>
     * 		</code></ul><code>
     *</code> 		
     * <br>
     * 
     * @return A CpoTrxAdapter to manage the transactionality of CPO
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     *
     * @see CpoTrxAdapter
     */
    public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
        return new JdbcCpoTrxAdapter(getMetaDataSource(), getMetaDataSourceName(), getWriteConnection(), batchUpdatesSupported_);
    }
}
