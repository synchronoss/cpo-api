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
import org.synchronoss.cpo.CpoArrayResultSet;
import org.synchronoss.cpo.CpoBlockingResultSet;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoNativeQuery;
import org.synchronoss.cpo.CpoObject;
import org.synchronoss.cpo.CpoOrderBy;
import org.synchronoss.cpo.CpoResultSet;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.CpoWhere;

/**
 * JdbcCpoAdapter is an interface for a set of routines that are responsible for managing value
 * objects from a datasource.
 *
 * @author david berry
 */
public class JdbcCpoAdapter implements CpoAdapter {
  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;

  /**
   * DOCUMENT ME!
   */
  private static Logger logger = Logger.getLogger(JdbcCpoAdapter.class.getName());

  /**
   * DOCUMENT ME!
   */
  private static final String[] GROUP_IDS = {
      "CREATE", "UPDATE", "DELETE", "RETRIEVE", "LIST", "PERSIST", "EXIST", "EXECUTE"
  };

  // Query Group Name Constants

  /**
   * DOCUMENT ME!
   */
  private static final String CREATE_GROUP = GROUP_IDS[CpoAdapter.CREATE];

  /**
   * DOCUMENT ME!
   */
  private static final String UPDATE_GROUP = GROUP_IDS[CpoAdapter.UPDATE];

  /**
   * DOCUMENT ME!
   */
  private static final String DELETE_GROUP = GROUP_IDS[CpoAdapter.DELETE];

  /**
   * DOCUMENT ME!
   */
  private static final String RETRIEVE_GROUP = GROUP_IDS[CpoAdapter.RETRIEVE];

  /**
   * DOCUMENT ME!
   */
  private static final String LIST_GROUP = GROUP_IDS[CpoAdapter.LIST];

  /**
   * DOCUMENT ME!
   */
  private static final String PERSIST_GROUP = GROUP_IDS[CpoAdapter.PERSIST];

  /**
   * DOCUMENT ME!
   */
  private static final String EXIST_GROUP = GROUP_IDS[CpoAdapter.EXIST];

  /**
   * DOCUMENT ME!
   */
  private static final String EXECUTE_GROUP = GROUP_IDS[CpoAdapter.EXECUTE];

  // Metadata Cache Objects

  
  /**
   * DOCUMENT ME!
   */
  private static HashMap<String, HashMap<String, JdbcMetaClass<?>>> dataSourceMap_ = new HashMap<String, HashMap<String, JdbcMetaClass<?>>>(); // Contains the
  // metaClassMap for
  // each datasource

  /**
   * DOCUMENT ME!
   */
  private Context context_ = null;

  // DataSource Information
  // These used to be static, but that prevented Cpo from supporting Multiple
  // DataSources.
  // The classMap will be accessed via the dataSourceMap now.

  /**
   * DOCUMENT ME!
   */
  private DataSource readDataSource_ = null;

  /**
   * DOCUMENT ME!
   */
  private DataSource writeDataSource_ = null;

  /**
   * DOCUMENT ME!
   */
  private DataSource metaDataSource_ = null;

  /**
   * DOCUMENT ME!
   */
  private String metaDataSourceName_ = null;

  private String dbTablePrefix = "";

  /** DOCUMENT ME! */

  /**
   * DOCUMENT ME!
   */
  private boolean invalidReadConnection_ = false;

  private boolean metaEqualsWrite_ = false;

  private boolean batchUpdatesSupported_ = false;
  
  protected JdbcCpoAdapter() {
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param jdsi This datasource will be used for both the metadata
   *             and the transaction database.
   * @throws org.synchronoss.cpo.CpoException
   *          exception
   */
  public JdbcCpoAdapter(JdbcDataSourceInfo jdsi)
      throws CpoException {
    this(null,jdsi);
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param jdsiMeta This datasource that identifies the cpo metadata datasource
   * @param jdsiTrx  The datasoruce that identifies the transaction database.
   * @throws org.synchronoss.cpo.CpoException
   *          exception
   */
  public JdbcCpoAdapter(JdbcDataSourceInfo jdsiMeta, JdbcDataSourceInfo jdsiTrx)
      throws CpoException {
    
    if (jdsiMeta!=null) {
      setDbTablePrefix(jdsiMeta.getDbTablePrefix());
      setMetaDataSource(getDataSource(jdsiMeta));
      setMetaDataSourceName(jdsiMeta.getDataSourceName());
    } else {
      setDbTablePrefix(jdsiTrx.getDbTablePrefix());
      setMetaDataSource(getDataSource(jdsiTrx));
      setMetaDataSourceName(jdsiTrx.getDataSourceName());
      metaEqualsWrite_ = true;
    }
    
    setWriteDataSource(getDataSource(jdsiTrx));
    setReadDataSource(getWriteDataSource());
    processDatabaseMetaData();
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param jdsiMeta  This datasource that identifies the cpo metadata datasource
   * @param jdsiWrite The datasource that identifies the transaction database
   *                  for write transactions.
   * @param jdsiRead  The datasource that identifies the transaction database
   *                  for read-only transactions.
   * @throws org.synchronoss.cpo.CpoException
   *          exception
   */
  public JdbcCpoAdapter(JdbcDataSourceInfo jdsiMeta, JdbcDataSourceInfo jdsiWrite, JdbcDataSourceInfo jdsiRead)
      throws CpoException {
    setDbTablePrefix(jdsiMeta.getDbTablePrefix());
    setMetaDataSource(getDataSource(jdsiMeta));
    setWriteDataSource(getDataSource(jdsiWrite));
    setReadDataSource(getDataSource(jdsiRead));
    setMetaDataSourceName(jdsiMeta.getDataSourceName());
    processDatabaseMetaData();
  }

  protected JdbcCpoAdapter(DataSource metaSource, String metaSourceName, boolean batchSupported, String dbTablePrefix)
      throws CpoException {
    setDbTablePrefix(dbTablePrefix);
    setMetaDataSource(metaSource);
    setMetaDataSourceName(metaSourceName);
    batchUpdatesSupported_ = batchSupported;
  }

  private DataSource getDataSource(JdbcDataSourceInfo jdsi) throws CpoException {
    DataSource ds;

    try {
      if (jdsi.getConnectionType() == JdbcDataSourceInfo.JNDI_CONNECTION) {
        Context ctx = jdsi.getJndiCtx();
        if (ctx == null) {
          ctx = new InitialContext();
        }
        ds = (DataSource) ctx.lookup(jdsi.getJndiName());

      } else {
        ds = new JdbcDataSource(jdsi);
      }
    } catch (Exception e) {
      throw new CpoException("Error instantiating DataSource", e);
    }

    return ds;
  }

  private void processDatabaseMetaData() throws CpoException {
    Connection c = null;
    try {
      c = getWriteConnection();
      DatabaseMetaData dmd = c.getMetaData();

      // do all the tests here
            batchUpdatesSupported_ = dmd.supportsBatchUpdates();
//      batchUpdatesSupported_ = false;

      this.closeConnection(c);
    } catch (Exception e) {
      logger.fatal(e, e);
      throw new CpoException("Could Not Retrieve Database Meta Data", e);
    } finally {
      closeConnection(c);
    }
  }


  /**
   * Clears the metadata for the specified object. The metadata will be reloaded
   * the next time that CPO is called to access this object
   *
   * @param obj The object whose metadata must be cleared
   */
  public void clearMetaClass(Object obj) {
    String className;
    Class<?> objClass;

    if (obj != null) {
      objClass = obj.getClass();
      className = objClass.getName();
      clearMetaClass(className);
    }
  }

  /**
   * Clears the metadata for the specified fully qualifed class name. The metadata
   * will be reloaded the next time CPO is called to access this class.
   *
   * @param className The fully qualified class name for the class that needs its
   *                  metadata cleared.
   */
  public void clearMetaClass(String className) {
    HashMap<String, JdbcMetaClass<?>> metaClassMap;

    synchronized (getDataSourceMap()) {
      metaClassMap = getMetaClassMap();
      metaClassMap.remove(className);
    }
  }

  /**
   * Clears the metadata for all classes. The metadata will be lazy-loaded from 
   * the metadata repository as classes are accessed.
   *
   * @param all true - clear all classes for all datasources.
   *            false - clear all classes for the current datasource.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
  */
  public void clearMetaClass(boolean all) {
    synchronized (getDataSourceMap()) {
      if (all==false) {
        HashMap<String, JdbcMetaClass<?>> metaClassMap = getMetaClassMap();
        metaClassMap.clear();
      } else {
        for (HashMap<String, JdbcMetaClass<?>> metamap : getDataSourceMap().values()){
          metamap.clear();
        }
      }
    }
  }
  
  /**
   * Clears the metadata for all classes for the current datasource. The metadata will be lazy-loaded from 
   * the metadata repository as classes are accessed.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
  */
  public void clearMetaClass() {
    clearMetaClass(false);
  }

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in
   * the datasource.  This method creates and stores the object in the datasource.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.insertObject(so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 
   * 	}
   * }
   *</code>
   *</pre>
   * 
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If
   *            the class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.CREATE_GROUP, null);
  }

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in
   * the datasource.  This method creates and stores the object in the datasource
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.insertObject("IDNameInsert",so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   * 
   *
   * @param name The String name of the CREATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used which is
   *             equivalent to insertObject(Object obj);
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(String name, T obj) throws CpoException {
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
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 	try{
   * 		cpo.insertObjects(al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(Collection<T> coll)
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
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 	try{
   * 		cpo.insertObjects("IdNameInsert",al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The String name of the CREATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(String name, Collection<T> coll)
      throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.CREATE_GROUP, name);
  }

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the
   * datasource.  This method stores the object in the datasource
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.deleteObject(so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If
   *            the class is not defined an exception will be thrown. If the object does not exist
   *            in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.DELETE_GROUP, null);
  }

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the
   * datasource.  This method stores the object in the datasource
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.deleteObject("DeleteById",so);
   * 	} catch (CpoException ce) {
   * 	// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The String name of the DELETE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown. If the object does not exist
   *             in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(String name, T obj) throws CpoException {
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
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 	try{
   * 		cpo.deleteObjects(al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(Collection<T> coll)
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
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 
   * 	try{
   * 		cpo.deleteObjects("IdNameDelete",al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The String name of the DELETE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(String name, Collection<T> coll)
      throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.DELETE_GROUP, name);
  }

  /**
   * Executes an Object whose metadata will call an executable within the datasource.
   * It is assumed that the executable object exists in the metadatasource. If the executable does not exist,
   * an exception will be thrown.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.executeObject(so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param object This is an Object that has been defined within the metadata of the
   *               datasource. If the class is not defined an exception will be thrown. If the object
   *               does not exist in the datasource, an exception will be thrown. This object is used
   *               to populate the IN parameters used to executed the datasource object.
   *               
   *               An object of this type will be created and filled with the returned data from the value_object.
   *               This newly created object will be returned from this method.
   * @return An object populated with the OUT parameters returned from the executable object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T executeObject(T object)
      throws CpoException {
    return processExecuteGroup(null, object, object);
  }

  /**
   * Executes an Object whose metadata will call an executable within the datasource.
   * It is assumed that the executable object exists in the metadatasource. If the executable does not exist,
   * an exception will be thrown.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.executeObject("execNotifyProc",so);
   * 	} catch (CpoException ce) {
   * 	// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name   The filter name which tells the datasource which objects should be returned. The
   *               name also signifies what data in the object will be populated.
   * @param object This is an object that has been defined within the metadata of the
   *               datasource. If the class is not defined an exception will be thrown. If the object
   *               does not exist in the datasource, an exception will be thrown. This object is used
   *               to populate the IN parameters used to retrieve the collection of objects.
   *               This object defines the object type that will be returned in the collection and
   *               contain the result set data or the OUT Parameters.
   * @return A result object populate with the OUT parameters
   * @throws CpoException if there are errors accessing the datasource
   */
  public <T> T executeObject(String name, T object)
      throws CpoException {
    return processExecuteGroup(name, object, object);
  }

  /**
   * Executes an Object that represents an executable object within the datasource.
   * It is assumed that the object exists in the datasource. If the object does not exist, an exception will be thrown
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class SomeResult sr = new SomeResult();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		sr = (SomeResult)cpo.executeObject("execNotifyProc",so, sr);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name     The String name of the EXECUTE Query group that will be used to create the object
   *                 in the datasource. null signifies that the default rules will be used.
   * @param criteria This is an object that has been defined within the metadata of the
   *                 datasource. If the class is not defined an exception will be thrown. If the object
   *                 does not exist in the datasource, an exception will be thrown. This object is used
   *                 to populate the IN parameters used to retrieve the  collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource.
   *                 If the class is not defined an exception will be thrown. If the object does not
   *                 exist in the datasource, an exception will be thrown. This object defines  the
   *                 object type that will be created, filled with the return data and returned from this
   *                 method.
   * @return An object populated with the out parameters
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T executeObject(String name, C criteria, T result)
      throws CpoException {
    return processExecuteGroup(name, criteria, result);
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * long count = 0;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		count = cpo.existsObject(so);
   * 		if (count>0) {
   * 			// object exists
   * 		} else {
   * 			// object does not exist
   * 		}
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If
   *            the class is not defined an exception will be thrown. This object will be searched for inside the
   *            datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(T obj) throws CpoException {
    return this.existsObject(null, obj);
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * long count = 0;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		count = cpo.existsObject("SomeExistCheck",so);
   * 		if (count>0) {
   * 			// object exists
   * 		} else {
   * 			// object does not exist
   * 		}
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The String name of the EXISTS Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown. This object will be searched for inside the
   *             datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(String name, T obj) throws CpoException {
    return this.existsObject(name, obj, null);
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * long count = 0;
   * class CpoAdapter cpo = null;
   * 
   *  
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      CpoWhere where = cpo.newCpoWhere(CpoWhere.LOGIC_NONE, id, CpoWhere.COMP_EQ);
   *      count = cpo.existsObject("SomeExistCheck",so, where);
   *      if (count>0) {
   *        // object exists
   *      } else {
   *        // object does not exist
   *      }
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   *</code>
   *</pre>
   * 
   * @param name The String name of the EXISTS Query group that will be used to create the object
   *          in the datasource. null signifies that the default rules will be used.
   * @param obj This is an object that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. This object will be searched for inside the
   *     datasource.
   * @param where A CpoWhere object that passes in run-time constraints to the query that performs the 
   *      the exist
   * @return The number of objects that exist in the datasource that match the specified object
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException {
    Connection c = null;
    Connection meta = null;
    long objCount = -1;

    try {
      c = getReadConnection();

      if (metaEqualsWrite_) {
        meta = c;
      } else {
        meta = getMetaConnection();
      }
      objCount = existsObject(name, obj, c, meta, wheres);
    } catch (Exception e) {
      throw new CpoException("existsObjects(String, Object) failed", e);
    } finally {
      closeConnection(c);
      closeConnection(meta);
    }

    return objCount;
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * @param name    The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
   *                execute to persist the object.
   * @param obj     This is an object that has been defined within the metadata of the datasource. If
   *                the class is not defined an exception will be thrown.
   * @param con     The datasource Connection with which to check if the object exists
   * @param metaCon metadataconnection
   * @return The int value of the first column returned in the record set
   * @throws CpoException exception will be thrown if the Query Group has a query count != 1
   */
  protected <T> long existsObject(String name, T obj, Connection con, Connection metaCon, Collection<CpoWhere> wheres)
      throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    JdbcQuery jq = null;
    JdbcMetaClass<T> jmc;
    ArrayList<JdbcQuery> queryGroup;
    long objCount = 0;
    int i;
    Logger localLogger = logger;

    try {
      jmc = getMetaClass(obj, metaCon);
      queryGroup = jmc.getQueryGroup(JdbcCpoAdapter.EXIST_GROUP, name);
      localLogger = Logger.getLogger(jmc.getJmcClass().getName());

      for (i = 0; i < queryGroup.size(); i++) {
        jq = queryGroup.get(i);
        JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, obj, wheres, null, null);
        ps = jpsf.getPreparedStatement();

        long qCount = 0; // set the results for this query to 0

        localLogger.info(jq.getText());
        rs = ps.executeQuery();
        jpsf.release();
        rsmd = rs.getMetaData();

        // see if they are using the count(*) logic
        if (rsmd.getColumnCount() == 1) {
          if (rs.next()) {
            try {
              qCount = rs.getLong(1); // get the number of objects
              // that exist
            } catch (Exception e) {
              // Exists result not an int so bail to record counter
              qCount = 1;
            }
            if (rs.next()) {
              // EXIST query has more than one record so not a count(*)
              qCount = 2;
            }
          }
        }

        while (rs.next()) {
          qCount++;
        }

        objCount += qCount;

        rs.close();
        ps.close();
        rs = null;
        ps = null;
      }


    } catch (SQLException e) {
      String msg = "existsObject(name, obj, con) failed:";
      if (jq != null)
        msg += jq.getText();

      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }
    }

    return objCount;
  }

  /**
   * newOrderBy allows you to dynamically change the order of the objects in the resulting
   * collection. This allows you to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @return A CpoOrderBy object to be passed into retrieveObjects.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public CpoOrderBy newOrderBy(String attribute, boolean ascending)
      throws CpoException {
    return new JdbcCpoOrderBy(attribute, ascending);
  }

  /**
   * newOrderBy allows you to dynamically change the order of the objects in the resulting
   * collection. This allows you to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @param function  A string which represents a datasource function that will be called on the attribute.
   *                  must be contained in the function string. The attribute name will be replaced at run-time with its
   *                  datasource counterpart
   * @return A CpoOrderBy object to be passed into retrieveObjects.
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
   * @throws CpoException DOCUMENT ME!
   */
  public CpoWhere newWhere() throws CpoException {
    return new JdbcCpoWhere();
  }

  /**
   * DOCUMENT ME!
   *
   * @param logical DOCUMENT ME!
   * @param attr    DOCUMENT ME!
   * @param comp    DOCUMENT ME!
   * @param value   DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value)
      throws CpoException {
    return new JdbcCpoWhere(logical, attr, comp, value);
  }

  /**
   * DOCUMENT ME!
   *
   * @param logical DOCUMENT ME!
   * @param attr    DOCUMENT ME!
   * @param comp    DOCUMENT ME!
   * @param value   DOCUMENT ME!
   * @param not     DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not) throws CpoException {
    return new JdbcCpoWhere(logical, attr, comp, value, not);
  }

  /**
   * Persists the Object into the datasource. The CpoAdapter will check to see if this object
   * exists in the datasource. If it exists, the object is updated in the datasource If the
   * object does not exist, then it is created in the datasource.  This method stores the object
   * in the datasource. This method uses the default EXISTS, CREATE, and UPDATE query groups specified
   * for this object.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.persistObject(so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If
   *            the class is not defined an exception will be thrown.
   * @return A count of the number of objects persisted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObject(T obj)
      throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.PERSIST_GROUP, null);
  }

  /**
   * Persists the Object into the datasource. The CpoAdapter will check to see if this object
   * exists in the datasource. If it exists, the object is updated in the datasource If the
   * object does not exist, then it is created in the datasource.  This method stores the object
   * in the datasource.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.persistObject("persistSomeObject",so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
   *             execute to persist the object.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown.
   * @return A count of the number of objects persisted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObject(String name, T obj)
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
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 	try{
   * 		cpo.persistObjects(al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObjects(Collection<T> coll)
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
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 	try{
   * 		cpo.persistObjects("myPersist",al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to
   *             execute to persist the object.
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObjects(String name, Collection<T> coll)
      throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.PERSIST_GROUP, name);
  }


  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the
   * datasource.  If the retrieve query defined for this objects returns more than one row, an
   * exception will be thrown.
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If
   *            the class is not defined an exception will be thrown. If the object does not exist
   *            in the datasource, an exception will be thrown. The input  object is used to specify
   *            the search criteria, the output  object is populated with the results of the query.
   * @return An object of the same type as the result parameter that is filled in as specified
   *         the metadata for the retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveObject(T obj)
      throws CpoException {
    T o = processSelectGroup(obj, null);

    return (o);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the
   * datasource.  If the retrieve query defined for this objects returns more than one row, an
   * exception will be thrown.
   *
   * @param name DOCUMENT ME!
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown. If the object does not exist
   *             in the datasource, an exception will be thrown. The input  object is used to specify
   *             the search criteria, the output  object is populated with the results of the query.
   * @return An object of the same type as the result parameter that is filled in as specified
   *         the metadata for the retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveObject(String name, T obj)
      throws CpoException {
    T o = processSelectGroup(obj, name);

    return (o);
  }

  
  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the
   * datasource.  If the retrieve query defined for this objects returns more than one row, an
   * exception will be thrown.
   *
   * @param name     The filter name which tells the datasource which objects should be returned. The
   *                 name also signifies what data in the object will be  populated.
   * @param criteria This is an object that has been defined within the metadata of the
   *                 datasource. If the class is not defined an exception will be thrown. If the object
   *                 does not exist in the datasource, an exception will be thrown. This object is used
   *                 to specify the parameters used to retrieve the  collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource.
   *                 If the class is not defined an exception will be thrown. If the object does not
   *                 exist in the datasource, an exception will be thrown. This object is used to specify
   *                 the object type that will be returned in the  collection.
   * @param wheres   A collection of CpoWhere objects that define the constraints that should be
   *                 used when retrieving objects
   * @param orderBy  The CpoOrderBy object that defines the order in which objects
   *                 should be returned
   * @return An object of the same type as the result parameter that is filled in as specified
   *         the metadata for the retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres,
                                 Collection<CpoOrderBy> orderBy) throws CpoException {
      return retrieveObject(name, criteria, result, wheres, orderBy, null);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the
   * datasource.  If the retrieve query defined for this objects returns more than one row, an
   * exception will be thrown.
   *
   * @param name     The filter name which tells the datasource which objects should be returned. The
   *                 name also signifies what data in the object will be  populated.
   * @param criteria This is an object that has been defined within the metadata of the
   *                 datasource. If the class is not defined an exception will be thrown. If the object
   *                 does not exist in the datasource, an exception will be thrown. This object is used
   *                 to specify the parameters used to retrieve the  collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource.
   *                 If the class is not defined an exception will be thrown. If the object does not
   *                 exist in the datasource, an exception will be thrown. This object is used to specify
   *                 the object type that will be returned in the  collection.
   * @param wheres   A collection of CpoWhere objects that define the constraints that should be
   *                 used when retrieving objects
   * @param orderBy  The CpoOrderBy object that defines the order in which objects
   *                 should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @return An object of the same type as the result parameter that is filled in as specified
   *         the metadata for the retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T,C> T  retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    Iterator<T> it = processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, true).iterator();
    if (it.hasNext())
      return it.next();
    else
      return null;
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the
   * datasource.
   *
   * @param name     The filter name which tells the datasource which objects should be returned. The
   *                 name also signifies what data in the object will be  populated.
   * @param criteria This is an object that has been defined within the metadata of the
   *                 datasource. If the class is not defined an exception will be thrown. If the object
   *                 does not exist in the datasource, an exception will be thrown. This object is used
   *                 to specify the parameters used to retrieve the  collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource.
   *                 If the class is not defined an exception will be thrown. If the object does not
   *                 exist in the datasource, an exception will be thrown. This object is used to specify
   *                 the object type that will be returned in the  collection.
   * @param wheres   A collection of CpoWhere objects that define the constraints that should be
   *                 used when retrieving objects
   * @param orderBy  The CpoOrderBy object that defines the order in which objects
   *                 should be returned
   * @return A collection of objects will be returned that meet the criteria  specified by obj.
   *         The objects will be of the same type as the Object  that was passed in. If no
   *         objects match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> Collection<T> retrieveObjects(String name, C criteria, T result, Collection<CpoWhere> wheres,
                                              Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the
   * datasource.
   *
   * @param name     The filter name which tells the datasource which objects should be returned. The
   *                 name also signifies what data in the object will be  populated.
   * @param criteria This is an object that has been defined within the metadata of the
   *                 datasource. If the class is not defined an exception will be thrown. If the object
   *                 does not exist in the datasource, an exception will be thrown. This object is used
   *                 to specify the parameters used to retrieve the  collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource.
   *                 If the class is not defined an exception will be thrown. If the object does not
   *                 exist in the datasource, an exception will be thrown. This object is used to specify
   *                 the object type that will be returned in the  collection.
   * @param wheres   A collection of CpoWhere objects that define the constraints that should be
   *                 used when retrieving objects
   * @param orderBy  The CpoOrderBy object that defines the order in which objects
   *                 should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @return A collection of objects will be returned that meet the criteria  specified by obj.
   *         The objects will be of the same type as the Object  that was passed in. If no
   *         objects match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> Collection<T> retrieveObjects(String name, C criteria, T result, Collection<CpoWhere> wheres,
                                              Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, false);
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
   * @param wheres   A collection of CpoWhere objects that define the constraints that should be
   *                 used when retrieving objects
   * @param orderBy The CpoOrderBy object that defines the order in which objects
   *                should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in 
   *             the meta data. This text will be embedded at run-time
   * @param objectBufferSize the maximum number of objects that the Iterator is allowed to cache.
   *        Once reached, the CPO framework will halt processing records from the datasource.
   *
   * @return An iterator that will be fed objects from the CPO framework.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
    public <T,C> CpoResultSet<T> retrieveObjects(String name, C criteria, T result, Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, int queueSize) throws CpoException {
      CpoBlockingResultSet<T> resultSet = new CpoBlockingResultSet<T>(queueSize);
      RetrieverThread<T,C> retrieverThread = new RetrieverThread<T,C>(name, criteria, result, wheres, orderBy, nativeQueries, false, resultSet);
        
      retrieverThread.start();
      return resultSet;
    }

  /**
   * Allows you to perform a series of object interactions with the database. This method
   * pre-dates CpoTrxAdapter and can be used without a programmer needing to remember to call
   * commit() or rollback().
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	so = new SomeObject();
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	CpoObject cobj = new CpoObject(CpoAdapter.CREATE,"MyCreate",so);
   * 	al.add(cobj);
   * 	so = new SomeObject();
   * 	so.setId(3);
   * 	so.setName("New Name");
   * 	CpoObject cobj = new CpoObject(CpoAdapter.PERSIST,"MyPersist",so);
   * 	al.add(cobj);
   * 	try{
   * 		cpo.transactObjects(al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param coll This is a collection of CpoObject objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @deprecated
   */
    @Deprecated
  public <T> long transactObjects(Collection<CpoObject<T>> coll) throws CpoException {
    Connection c = null;
    Connection meta = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();
      if (metaEqualsWrite_) {
        meta = c;
      } else {
        meta = getMetaConnection();
      }

      updateCount = transactObjects(coll, c, meta);
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException)
        throw (CpoException) e;
      else
        throw new CpoException("transactObjects(Collection coll) failed", e);
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
   * @param meta metadata conn
   * @param c    DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   * @deprecated
   */
    @Deprecated
  protected <T> long transactObjects(Collection<CpoObject<T>> coll, Connection c, Connection meta)
      throws CpoException {
    long updateCount = 0;

    for (CpoObject<T> cpoObject : coll) {

      if (cpoObject.getObject() instanceof java.util.Collection) {
        updateCount += processUpdateGroup(cpoObject.getObject(),
            GROUP_IDS[cpoObject.getOperation()], cpoObject.getName(), c, meta);
      } else {
        updateCount += processUpdateGroup(cpoObject.getObject(),
            GROUP_IDS[cpoObject.getOperation()], cpoObject.getName(), c, meta);
      }
    }

    return updateCount;
  }

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object
   * exists in the datasource. If it exists then the object will be updated. If it does not exist,
   * an exception will be thrown
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.updateObject(so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If
   *            the class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.UPDATE_GROUP, null);
  }

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object
   * exists in the datasource. If it exists then the object will be updated. If it does not exist,
   * an exception will be thrown
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.updateObject("updateSomeObject",so);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The String name of the UPDATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If
   *             the class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.UPDATE_GROUP, name);
  }

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects
   * contained in the collection exist in the datasource.  This method stores the object in the
   * datasource. The objects in the collection will be treated as one transaction, meaning that
   * if one of the objects fail being updated in the datasource then the entire collection will
   * be rolled back, if supported by the datasource.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 	try{
   * 		cpo.updateObjects(al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(Collection<T> coll)
      throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.UPDATE_GROUP, null);
  }

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects
   * contained in the collection exist in the datasource.  This method stores the object in the
   * datasource. The objects in the collection will be treated as one transaction, meaning that
   * if one of the objects fail being updated in the datasource then the entire collection will
   * be rolled back, if supported by the datasource.
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   * 	}
   * 	try{
   * 		cpo.updateObjects("myUpdate",al);
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @param name The String name of the UPDATE Query group that will be used to create the object
   *             in the datasource. null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within  the metadata of
   *             the datasource. If the class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(String name, Collection<T> coll)
      throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.UPDATE_GROUP, name);
  }

  /**
   * DOCUMENT ME!
   *
   * @param context DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected void setContext(Context context) throws CpoException {
    try {
      if (context == null) {
        context_ = new InitialContext();
      } else {
        context_ = context;
      }
    } catch (NamingException e) {
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
  protected HashMap<String, HashMap<String, JdbcMetaClass<?>>> getDataSourceMap() {
    return dataSourceMap_;
  }

  protected void setDataSourceMap(HashMap<String, HashMap<String, JdbcMetaClass<?>>> dsMap) {
    dataSourceMap_ = dsMap;
  }

  /**
   * DOCUMENT ME!
   *
   * @param obj  DOCUMENT ME!
   * @param type DOCUMENT ME!
   * @param name DOCUMENT ME!
   * @param c    DOCUMENT ME!
   * @param meta metadata conn
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> String getGroupType(T obj, String type, String name, Connection c, Connection meta)
      throws CpoException {
    String retType = type;
    long objCount;

    if (JdbcCpoAdapter.PERSIST_GROUP.equals(retType) == true) {
      objCount = existsObject(name, obj, c, meta, null);

      if (objCount == 0) {
        retType = JdbcCpoAdapter.CREATE_GROUP;
      } else if (objCount == 1) {
        retType = JdbcCpoAdapter.UPDATE_GROUP;
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
   * @param c   connection
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> JdbcMetaClass<T> getMetaClass(T obj, Connection c) throws CpoException {
    JdbcMetaClass<T> jmc = null;
    String className;
    String requestedName;
    Class<?> classObj;
    Class<?> requestedClass;
    HashMap<String, JdbcMetaClass<?>> metaClassMap;

    if (obj != null) {
      requestedClass = obj.getClass();
      classObj = requestedClass;
      requestedName = requestedClass.getName();
      className = requestedName;

      synchronized (getDataSourceMap()) {
        metaClassMap = getMetaClassMap();
        jmc = (JdbcMetaClass<T>) metaClassMap.get(className);
        while(jmc==null && classObj!=null){
          try {
            jmc = (JdbcMetaClass<T>) loadMetaClass(requestedClass, className, c);
            // reset the class name to the original 
            jmc.setName(requestedName);
            metaClassMap.put(requestedName, jmc);
            Logger.getLogger(requestedName).debug("Loading Class:" + requestedName);
          } catch (CpoException ce) {
            jmc = null;
            classObj = classObj.getSuperclass();
            className = classObj.getName();
          }
        }
        if (jmc==null){
          throw new CpoException("No Metadata found for class:" + requestedName);
        }
      }
    }

    return jmc;
  }

  // All meta data will come from the meta datasource.
  protected HashMap<String, JdbcMetaClass<?>> getMetaClassMap() {
    HashMap<String, HashMap<String, JdbcMetaClass<?>>> dataSourceMap = getDataSourceMap();
    String dataSourceName = getMetaDataSourceName();
    HashMap<String, JdbcMetaClass<?>> metaClassMap = dataSourceMap.get(dataSourceName);

    if (metaClassMap == null) {
      metaClassMap = new HashMap<String, JdbcMetaClass<?>>();
      dataSourceMap.put(dataSourceName, metaClassMap);
    }

    return metaClassMap;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected Connection getReadConnection() throws CpoException {
    Connection connection = getStaticConnection();

    if (connection == null) {
      try {
        if (invalidReadConnection_ == false) {
          connection = getReadDataSource().getConnection();
        } else {
          connection = getWriteDataSource().getConnection();
        }
        connection.setAutoCommit(false);
      } catch (Exception e) {
        invalidReadConnection_ = true;

        String msg = "getReadConnection(): failed";
        logger.error(msg, e);

        try {
          connection = getWriteDataSource().getConnection();
          connection.setAutoCommit(false);
        } catch (SQLException e2) {
          msg = "getWriteConnection(): failed";
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
    readDataSource_ = readDataSource;
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
   * @throws CpoException DOCUMENT ME!
   */
  protected Connection getWriteConnection() throws CpoException {
    Connection connection = getStaticConnection();

    if (connection == null) {
      try {
        connection = getWriteDataSource().getConnection();
        connection.setAutoCommit(false);
      } catch (SQLException e) {
        String msg = "getWriteConnection(): failed";
        logger.error(msg, e);
        throw new CpoException(msg, e);
      }
    }

    return connection;
  }

  protected Connection getStaticConnection() throws CpoException {
    // do nothing for JdbcCpoAdapter
    // overridden by JdbcTrxAdapter
   return null;
  }

  protected boolean isStaticConnection(Connection c) {
    // do nothing for JdbcCpoAdapter
    // overridden by JdbcTrxAdapter
    return false;  
  }
  
  protected void setStaticConnection(Connection c) {
    // do nothing for JdbcCpoAdapter
    // overridden by JdbcTrxAdapter
  }

  /**
   * DOCUMENT ME!
   *
   * @param writeDataSource DOCUMENT ME!
   */
  protected void setWriteDataSource(DataSource writeDataSource) {
    writeDataSource_ = writeDataSource;
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
   * @throws CpoException DOCUMENT ME!
   */
  protected Connection getMetaConnection() throws CpoException {
    Connection connection;

    try {
      connection = getMetaDataSource().getConnection();
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      String msg = "getMetaConnection(): failed";
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
    metaDataSource_ = metaDataSource;
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
    metaDataSourceName_ = metaDataSourceName;
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
      if (isStaticConnection(connection)){
        clearConnectionBusy(connection);
      } else if ((connection != null) && (connection.isClosed() == false)) {
        connection.close();
      }
    } catch (SQLException e) {
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param connection DOCUMENT ME!
   */
  protected void commitConnection(Connection connection) {
    try {
      if (connection != null && !isStaticConnection(connection)) {
        connection.commit();
      }
    } catch (SQLException e) {
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param connection DOCUMENT ME!
   */
  protected void rollbackConnection(Connection connection) {
    try {
      if (connection != null && !isStaticConnection(connection)) {
        connection.rollback();
      }
    } catch (SQLException e) {
    }
  }


  /**
   * Executes an Object whose MetaData contains a stored procedure. An assumption is that the
   * object exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which objects should be returned. The
   *                 name also signifies what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the
   *                 datasource. If the class is not defined an exception will be thrown. If the object
   *                 does not exist in the datasource, an exception will be thrown. This object is used
   *                 to populate the IN parameters used to retrieve the collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource.
   *                 If the class is not defined an exception will be thrown. If the object does not
   *                 exist in the datasource, an exception will be thrown. This object defines the object
   *                 type that will be returned in the
   * @return A result object populate with the OUT parameters
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> T processExecuteGroup(String name, C criteria, T result)
      throws CpoException {
    Connection c = null;
    Connection meta = null;
    T obj = null;

    try {
      c = getWriteConnection();
      if (metaEqualsWrite_) {
        meta = c;
      } else {
        meta = getMetaConnection();
      }
      obj = processExecuteGroup(name, criteria, result, c, meta);
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException)
        throw (CpoException) e;
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
   * @param name     DOCUMENT ME!
   * @param criteria DOCUMENT ME!
   * @param result   DOCUMENT ME!
   * @param conn     DOCUMENT ME!
   * @param metaCon  metadata connection
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> T processExecuteGroup(String name, C criteria, T result,
                                         Connection conn, Connection metaCon) throws CpoException {
    CallableStatement cstmt = null;
    ArrayList<JdbcQuery> queryGroup;
    JdbcQuery jq = null;
    JdbcMetaClass<C> jmcCriteria;
    JdbcMetaClass<T> jmcResult;
    T returnObject = null;
    Logger localLogger = criteria == null ? logger : Logger.getLogger(criteria.getClass().getName());

    //Object[] setterArgs = {null};
    Class<T> jmcClass;
    ArrayList<JdbcParameter> parameters;
    JdbcParameter parameter;
    JdbcAttribute attribute;
    JdbcCallableStatementFactory jcsf = null;

    //Object[] getterArgs = {};
    int j;
    int i;

    try {
      jmcCriteria = getMetaClass(criteria, metaCon);
      jmcResult = getMetaClass(result, metaCon);
      queryGroup = jmcCriteria.getQueryGroup(JdbcCpoAdapter.EXECUTE_GROUP, name);
      localLogger.info("===================processExecuteGroup (" + name + ") Count<" +
          queryGroup.size() + ">=========================");

      jmcClass = jmcResult.getJmcClass();
      try {
        returnObject = jmcClass.newInstance();
      } catch (IllegalAccessException iae) {
        throw new CpoException("Unable to access the constructor of the Return Object", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Object", iae);
      }

      // Loop through the queries and process each one
      for (i = 0; i < queryGroup.size(); i++) {
        // Get the current call
        jq = queryGroup.get(i);

        jcsf = new JdbcCallableStatementFactory(conn, this, jq, criteria);

        localLogger.debug("Executing Call:" + jmcCriteria.getName() + ":" + name);

        cstmt = jcsf.getCallableStatement();

        cstmt.execute();

        jcsf.release();

        localLogger.debug("Processing Call:" + jmcCriteria.getName() + ":" + name);

        // Add Code here to go through the parameters, find record sets,
        // and process them
        // Process the non-record set out params and make it the first
        // object in the collection

        // Loop through the OUT Parameters and set them in the result
        // object
        parameters = jcsf.getOutParameters();
        if (!parameters.isEmpty()) {
          for (j = 0; j < parameters.size(); j++) {
            parameter = parameters.get(j);

            if (parameter.isOutParameter()) {
              attribute = parameter.getAttribute();
              attribute.invokeSetter(returnObject, cstmt, j + 1);
            }
          }
        }

        cstmt.close();
      }
    } catch (SQLException e) {
      String msg = "ProcessExecuteGroup(String name, Object criteria, Object result, Connection conn) failed. SQL=";
      if (jq != null) msg += jq.getText();
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    } finally {
      if (cstmt != null) {
        try {
          cstmt.close();
        } catch (Exception e) {
        }
      }
      if (jcsf != null)
        jcsf.release();
    }

    return returnObject;
  }

  /**
   * Retrieves the Object from the datasource.
   *
   * @param obj       This is an object that has been defined within the metadata of the datasource. If
   *                  the class is not defined an exception will be thrown. The input object is used to
   *                  specify the search criteria.
   * @param groupName The name which identifies which RETRIEVE Query group to execute to retrieve
   *                  the object.
   * @return A populated object of the same type as the Object passed in as a parameter. If no
   *         objects match the criteria a NULL will be returned.
   * @throws CpoException the retrieve query defined for this objects returns more than one
   *                      row, an exception will be thrown.
   */
  protected <T> T processSelectGroup(T obj, String groupName)
      throws CpoException {
    Connection c = null;
    Connection meta = null;
    T result = null;

    try {
      c = getReadConnection();
      if (metaEqualsWrite_) {
        meta = c;
      } else {
        meta = getMetaConnection();
      }
      result = processSelectGroup(obj, groupName, c, meta);

      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException)
        throw (CpoException) e;
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
   * @param obj       DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @param con       DOCUMENT ME!
   * @param metaCon   metadata connection
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> T processSelectGroup(T obj, String groupName, Connection con, Connection metaCon)
      throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    JdbcQuery jq;
    JdbcMetaClass<T> jmc;
    ArrayList<JdbcQuery> queryGroup;
    JdbcAttribute attribute;
    T criteriaObj = obj;
    boolean recordsExist = false;
    Logger localLogger = obj == null ? logger : Logger.getLogger(obj.getClass().getName());

    int recordCount = 0;
    int attributesSet = 0;

    int i;
    int k;
    HashMap<String, JdbcAttribute> jmcAttrMap;
    T rObj = null;

    try {
      jmc = getMetaClass(criteriaObj, metaCon);
      queryGroup = jmc.getQueryGroup(JdbcCpoAdapter.RETRIEVE_GROUP, groupName);
      jmcAttrMap = jmc.getAttributeMap();

      localLogger.info("=================== Class=<" + criteriaObj.getClass() + "> Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");

      try {
        rObj = jmc.getJmcClass().newInstance();
      } catch (IllegalAccessException iae) {
        if (obj != null)
          localLogger.error("=================== Could not access default constructor for Class=<" + obj.getClass() + "> ==================");
        else
          localLogger.error("=================== Could not access default constructor for class ==================");

        throw new CpoException("Unable to access the constructor of the Return Object", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Object", iae);
      }


      for (i = 0; i < queryGroup.size(); i++) {
        jq = queryGroup.get(i);

        JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, criteriaObj);
        ps = jpsf.getPreparedStatement();

        // insertions on
        // selectgroup
        rs = ps.executeQuery();
        jpsf.release();

        if (rs.isBeforeFirst() == true) {
          rsmd = rs.getMetaData();

          if ((rsmd.getColumnCount() == 2) &&
              "CPO_ATTRIBUTE".equalsIgnoreCase(rsmd.getColumnName(1)) &&
              "CPO_VALUE".equalsIgnoreCase(rsmd.getColumnName(2))) {
            while (rs.next()) {
              recordsExist = true;
              recordCount++;
              attribute = jmcAttrMap.get(rs.getString(1));

              if (attribute != null) {
                attribute.invokeSetter(rObj, rs, 2);
                attributesSet++;
              }
            }
          } else if (rs.next()) {
            recordsExist = true;
            recordCount++;
            for (k = 1; k <= rsmd.getColumnCount(); k++) {
              attribute = jmcAttrMap.get(rsmd.getColumnName(k));

              if (attribute != null) {
                attribute.invokeSetter(rObj, rs, k);
                attributesSet++;
              }
            }

            if (rs.next()) {
              String msg = "ProcessSelectGroup(Object, String) failed: Multiple Records Returned";
              localLogger.error(msg);
              throw new CpoException(msg);
            }
          }
          criteriaObj = rObj;

        }

        rs.close();
        rs = null;
        ps.close();
        ps = null;
      }

      if (!recordsExist) {
        rObj = null;
        localLogger.info("=================== 0 Records - 0 Attributes - Class=<" + criteriaObj.getClass() + "> Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");
      } else {
        localLogger.info("=================== " + recordCount + " Records - " + attributesSet + " Attributes - Class=<" + criteriaObj.getClass() + ">  Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");
      }

    } catch (SQLException e) {
      String msg = "ProcessSeclectGroup(Object) failed: " + e.getMessage();
      localLogger.error(msg, e);
      rObj = null;
      throw new CpoException(msg, e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }

    }

    return rObj;
  }

  /**
   * DOCUMENT ME!
   *
   * @param name        DOCUMENT ME!
   * @param criteria    DOCUMENT ME!
   * @param result      DOCUMENT ME!
   * @param where       DOCUMENT ME!
   * @param orderBy     DOCUMENT ME!
   * @param useRetrieve DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> Collection<T> processSelectGroup(String name, C criteria, T result,
      Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, boolean useRetrieve) throws CpoException {
    Connection con = null;
    Connection meta = null;
    CpoArrayResultSet<T> resultSet = new CpoArrayResultSet<T>();

    try {
      con = getReadConnection();
      if (metaEqualsWrite_) {
        meta = con;
      } else {
        meta = getMetaConnection();
      }
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, con, meta, useRetrieve, resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(con);
      } catch (Exception re) {
      }

      if (e instanceof CpoException)
        throw (CpoException) e;
      else
        throw new CpoException("processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed",
            e);
    } finally {
      closeConnection(con);
      closeConnection(meta);
    }

    return resultSet;
  }

  protected <T, C> void processSelectGroup(String name, C criteria, T result,
      Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, boolean useRetrieve, CpoResultSet<T> resultSet) throws CpoException {
    Connection con = null;
    Connection meta = null;

    try {
      con = getReadConnection();
      if (metaEqualsWrite_) {
        meta = con;
      } else {
        meta = getMetaConnection();
      }
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, con, meta, useRetrieve, resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(con);
      } catch (Exception re) {
      }

      if (e instanceof CpoException)
        throw (CpoException) e;
      else
        throw new CpoException("processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed",
            e);
    } finally {
      closeConnection(con);
      closeConnection(meta);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param name        DOCUMENT ME!
   * @param criteria    DOCUMENT ME!
   * @param result      DOCUMENT ME!
   * @param where       DOCUMENT ME!
   * @param orderBy     DOCUMENT ME!
   * @param con         DOCUMENT ME!
   * @param metaCon     DOCUMENT ME!
   * @param useRetrieve DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> void processSelectGroup(String name, C criteria, T result,
      Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, Connection con, Connection metaCon, boolean useRetrieve, CpoResultSet<T> resultSet)
      throws CpoException {
    Logger localLogger = criteria == null ? logger : Logger.getLogger(criteria.getClass().getName());
    PreparedStatement ps = null;
    ArrayList<JdbcQuery> queryGroup;
    JdbcQuery jq;
    JdbcMetaClass<C> jmcCriteria;
    JdbcMetaClass<T> jmcResult;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    int columnCount;
    int k;
    T obj;
    Class<T> jmcClass;
    HashMap<String, JdbcAttribute> jmcAttrMap;
    JdbcAttribute[] attributes;
    JdbcPreparedStatementFactory jpsf;
    int i;
    
    try {
      jmcCriteria = getMetaClass(criteria, metaCon);
      jmcResult = getMetaClass(result, metaCon);
      if (useRetrieve) {
        localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + name + "> =========================");
        queryGroup = jmcCriteria.getQueryGroup(JdbcCpoAdapter.RETRIEVE_GROUP, name);
      } else {
        localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.LIST_GROUP + "> Name=<" + name + "> =========================");
        queryGroup = jmcCriteria.getQueryGroup(JdbcCpoAdapter.LIST_GROUP, name);
      }

      for (i = 0; i < queryGroup.size(); i++) {
        jq = queryGroup.get(i);

        jpsf = new JdbcPreparedStatementFactory(con, this, jmcCriteria, jq, criteria, wheres, orderBy, nativeQueries);
        ps = jpsf.getPreparedStatement();
        ps.setFetchSize(resultSet.getFetchSize());

        localLogger.debug("Retrieving Records");

        rs = ps.executeQuery();
        jpsf.release();

        localLogger.debug("Processing Records");

        rsmd = rs.getMetaData();

        jmcClass = jmcResult.getJmcClass();
        jmcAttrMap = jmcResult.getAttributeMap();
        columnCount = rsmd.getColumnCount();

        attributes = new JdbcAttribute[columnCount + 1];

        for (k = 1; k <= columnCount; k++) {
          attributes[k] = jmcAttrMap.get(rsmd.getColumnName(k));
        }

        while (rs.next()) {
          try {
            obj = jmcClass.newInstance();
          } catch (IllegalAccessException iae) {
            if (result != null)
              localLogger.error("=================== Could not access default constructor for Class=<" + result.getClass() + "> ==================");
            else
              localLogger.error("=================== Could not access default constructor for class ==================");

            throw new CpoException("Unable to access the constructor of the Return Object", iae);
          } catch (InstantiationException iae) {
            throw new CpoException("Unable to instantiate Return Object", iae);
          }

          for (k = 1; k <= columnCount; k++) {
            if (attributes[k] != null) {
              attributes[k].invokeSetter(obj, rs, k);
            }
          }

          try {
            resultSet.put(obj);
          } catch (InterruptedException e) {
            localLogger.error("Retriever Thread was interrupted", e);
            break;
          }
        }

        try {
          rs.close();
        } catch (Exception e) {
        }

        try {
          ps.close();
        } catch (Exception e) {
        }

        localLogger.info("=================== " + resultSet.size() + " Records - Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.LIST_GROUP + "> Name=<" + name + "> Result=<" + result.getClass() + "> ====================");
      }
    } catch (SQLException e) {
      String msg =
          "ProcessSelectGroup(String name, Object criteria, Object result, CpoWhere where, Collection orderBy, Connection con) failed. Error:";
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    } finally {
      if (rs != null) {
        try {
          rs.close();
        } catch (Exception e) {
        }
      }

      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param obj       DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName)
      throws CpoException {
    Connection c = null;
    Connection meta = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();

      if (metaEqualsWrite_) {
        meta = c;
      } else {
        meta = getMetaConnection();
      }
      updateCount = processUpdateGroup(obj, groupType, groupName, c, meta);
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException)
        throw (CpoException) e;
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
   * @param obj       DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @param con       DOCUMENT ME!
   * @param metaCon   DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName, Connection con, Connection metaCon)
      throws CpoException {
    Logger localLogger = obj == null ? logger : Logger.getLogger(obj.getClass().getName());
    JdbcMetaClass<T> jmc;
    ArrayList<JdbcQuery> queryGroup;
    PreparedStatement ps = null;
    JdbcQuery jq = null;
    JdbcPreparedStatementFactory jpsf = null;
    int i;
    long updateCount = 0;

    try {
      jmc = getMetaClass(obj, metaCon);
      queryGroup = jmc.getQueryGroup(getGroupType(obj, groupType, groupName, con, metaCon), groupName);
      localLogger.info("=================== Class=<" + obj.getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

      int numRows = 0;

      for (i = 0; i < queryGroup.size(); i++) {
        jq = queryGroup.get(i);
        jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, obj);
        ps = jpsf.getPreparedStatement();
        numRows += ps.executeUpdate();
        jpsf.release();
        ps.close();
      }
      localLogger.info("=================== " + numRows + " Updates - Class=<" + obj.getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

      if (numRows > 0) {
        updateCount++;
      }
    } catch (SQLException e) {
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + "," +
          obj.getClass().getName();
      localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }
      if (jpsf != null)
        jpsf.release();

    }

    return updateCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @param arr       DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @param con       DOCUMENT ME!
   * @param metaCon   DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processBatchUpdateGroup(T[] arr, String groupType, String groupName, Connection con, Connection metaCon)
      throws CpoException {
    JdbcMetaClass<T> jmc;
    ArrayList<JdbcQuery> queryGroup;
    PreparedStatement ps = null;
    JdbcQuery jq = null;
    JdbcPreparedStatementFactory jpsf = null;
    long updateCount = 0;
    int[] updates;
    Logger localLogger = logger;

    try {
      jmc = getMetaClass(arr[0], metaCon);
      queryGroup = jmc.getQueryGroup(getGroupType(arr[0], groupType, groupName, con, metaCon), groupName);
      localLogger = Logger.getLogger(jmc.getJmcClass().getName());


      int numRows = 0;

      // Only Batch if there is only one query
      if (queryGroup.size() == 1) {
        localLogger.info("=================== BATCH - Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
        jq = queryGroup.get(0);
        jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, arr[0]);
        ps = jpsf.getPreparedStatement();
        ps.addBatch();
        for (int j = 1; j < arr.length; j++) {
//          jpsf.bindParameters(arr[j]);
          jpsf.setBindValues(jpsf.getBindValues(jq, arr[j]));
          ps.addBatch();
        }
        updates = ps.executeBatch();
        jpsf.release();
        ps.close();
        for (int j = 0; j < updates.length; j++) {
          if (updates[j] > 0) {
            numRows += updates[j];
          } else if (updates[j] == PreparedStatement.SUCCESS_NO_INFO) {
            // something updated but we do not know what or how many so default to one.
            numRows++;
          }
        }
        localLogger.info("=================== BATCH - " + numRows + " Updates - Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

      } else {
        localLogger.info("=================== Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
        for (int j = 0; j < arr.length; j++) {
          for (int i = 0; i < queryGroup.size(); i++) {
            jq = queryGroup.get(i);
            jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, arr[j]);
            ps = jpsf.getPreparedStatement();
            numRows += ps.executeUpdate();
            jpsf.release();
            ps.close();
          }
        }
        localLogger.info("=================== " + numRows + " Updates - Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
      }

      if (numRows > 0) {
        updateCount = numRows;
      }
    } catch (SQLException e) {
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + "," +
          arr[0].getClass().getName();
      localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }
      if (jpsf != null)
        jpsf.release();

    }

    return updateCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @param coll      DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName)
      throws CpoException {
    Connection c = null;
    Connection meta = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();
      if (metaEqualsWrite_) {
        meta = c;
      } else {
        meta = getMetaConnection();
      }

      updateCount = processUpdateGroup(coll, groupType, groupName, c, meta);
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException)
        throw (CpoException) e;
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
   * @param coll      DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @param con       DOCUMENT ME!
   * @param meta      DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName,
                                        Connection con, Connection meta) throws CpoException {
    long updateCount = 0;

    if (!coll.isEmpty()) {
      Object[] arr = coll.toArray();
      
      Object obj1 = arr[0];
      boolean allEqual=true;
      for (int i=1; i<arr.length; i++){
          if (!obj1.getClass().getName().equals(arr[i].getClass().getName())){
              allEqual=false;
              break;
          }
      }

      if (allEqual && batchUpdatesSupported_ && !JdbcCpoAdapter.PERSIST_GROUP.equals(groupType)) {
        updateCount = processBatchUpdateGroup(arr, groupType, groupName, con, meta);
      } else {
        for (int i = 0; i < arr.length; i++) {
          updateCount += processUpdateGroup(arr[i], groupType, groupName, con, meta);
        }
      }
    }

    return updateCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @param name DOCUMENT ME!
   * @param c    DOCUMENT ME!
   * @param jmc  DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  private <T> void loadAttributeMap(String name, Connection c, JdbcMetaClass<T> jmc)
      throws CpoException {
    String select = "select cam.column_name, cam.attribute, cc.class_id, cam.column_type, cam.db_table, cam.db_column, cam.transform_class from ";
    String table1 = "cpo_attribute_map cam, ";
    String table2 = "cpo_class cc where cc.name = ? and cam.class_id = cc.class_id";
    String sql = select + getDbTablePrefix() + table1 + getDbTablePrefix() + table2;
    PreparedStatement ps = null;
    ResultSet rs = null;
    HashMap<String, JdbcAttribute> aMap;
    HashMap<String, JdbcAttribute> cMap;
    String classId;
    String dbType;

    logger.debug("loadAttribute Sql <" + sql + ">");

    //JdbcParameter jp=null;
    JdbcAttribute attribute;
    boolean failed = false;
    StringBuffer failedMessage = new StringBuffer();

    if ((c != null) && (jmc != null)) {
      Logger localLogger = Logger.getLogger(jmc.getJmcClass().getName());
      try {
        ps = c.prepareStatement(sql);
        ps.setString(1, name);
        rs = ps.executeQuery();
        aMap = jmc.getAttributeMap();
        cMap = jmc.getColumnMap();

        if (rs.next()) {
          classId = rs.getString(3);
          jmc.setClassId(classId);

          do {
            try {
              dbType = rs.getString(4);
              attribute = new JdbcAttribute(jmc, rs.getString(2), dbType, rs.getString(1),
                  rs.getString(5), rs.getString(6), rs.getString(7));
              aMap.put(rs.getString(1), attribute);
              cMap.put(attribute.getName(), attribute);
            } catch (CpoException ce) {
              failed = true;
              String msg = ce.getLocalizedMessage();
              if (msg == null && ce.getCause() != null)
                msg = ce.getCause().getLocalizedMessage();
              failedMessage.append(msg);
            }
          } while (rs.next());

          if (failed == true) {
            throw new CpoException("Error processing Attributes for:" + name + failedMessage.toString());
          }
        } else {
          throw new CpoException("No Attributes found for class:" + name);
        }
      } catch (CpoException ce) {
        String msg = "loadAttributeMap() failed:'" + sql + "' classname:" + name;
        localLogger.error(msg, ce);
        throw ce;
      } catch (Exception e) {
        String msg = "loadAttributeMap() failed:'" + sql + "' classname:" + name;
        localLogger.error(msg, e);
        throw new CpoException(msg, e);
      } finally {
        if (rs != null) {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }

        if (ps != null) {
          try {
            ps.close();
          } catch (Exception e) {
          }
        }
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param objClass DOCUMENT ME!
   * @param name     DOCUMENT ME!
   * @param c        connection to be used
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  private <T> JdbcMetaClass<T> loadMetaClass(Class<T> objClass, String name, Connection c)
      throws CpoException {
    JdbcMetaClass<T> jmc;

    jmc = new JdbcMetaClass<T>(objClass, name);
    loadAttributeMap(name, c, jmc);
    loadQueryGroups(c, jmc);

    return jmc;
  }

  /**
   * DOCUMENT ME!
   *
   * @param c   DOCUMENT ME!
   * @param jmc DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  private <T> void loadQueryGroups(Connection c, JdbcMetaClass<T> jmc)
      throws CpoException {
    String id = null;
    StringBuffer sqlBuffer = new StringBuffer();
    sqlBuffer.append("select ");
    sqlBuffer.append(
        " innr.group_type,innr.name,innr.query_id,innr.query_seq as query_seq,cqt.sql_text,innr.param_seq as param_seq,  cam.attribute, cam.column_name, cam.column_type, innr.param_type ");
    sqlBuffer.append("from ");
    sqlBuffer.append(getDbTablePrefix());
    sqlBuffer.append("cpo_query_text cqt,  ");
    sqlBuffer.append(
        "  (select cqg.group_type, cqg.name, cq.query_id, cq.seq_no as query_seq,cqp.seq_no as param_seq, cqp.attribute_id, cqp.param_type,cq.text_id,cq.seq_no,cqg.group_id ");
    sqlBuffer.append("from ");
    sqlBuffer.append(getDbTablePrefix());
    sqlBuffer.append("cpo_query_group cqg, ");
    sqlBuffer.append(getDbTablePrefix());
    sqlBuffer.append("cpo_query cq ");
    sqlBuffer.append("   left outer join ");
    sqlBuffer.append(getDbTablePrefix());
    sqlBuffer.append("cpo_query_parameter cqp ");
    sqlBuffer.append("   on cq.query_id = cqp.query_id ");
    sqlBuffer.append("   where cqg.class_id = ? ");
    sqlBuffer.append("   and cqg.group_id = cq.group_id ) innr ");
    sqlBuffer.append(" left outer join ");
    sqlBuffer.append(getDbTablePrefix());
    sqlBuffer.append("cpo_attribute_map cam on innr.attribute_id = cam.attribute_id ");
    sqlBuffer.append("where cqt.text_id = innr.text_id ");
    sqlBuffer.append("order by innr.group_id asc, innr.query_seq asc, innr.param_seq  asc");

    String sql = sqlBuffer.toString();
    logger.debug("loadQueryGroup Sql <" + sql + ">");

    PreparedStatement ps = null;
    ResultSet rs = null;
    int oldSeq = 1000;
    int newSeq;
    JdbcQuery jq = null;
    String groupType = null;

    if ((c != null) && (jmc != null)) {
      Logger localLogger = Logger.getLogger(jmc.getJmcClass().getName());
      try {
        id = jmc.getClassId();
        ps = c.prepareStatement(sql);
        ps.setString(1, id);
        rs = ps.executeQuery();

        while (rs.next()) {
          newSeq = rs.getInt(6);

          if (newSeq <= oldSeq) {
            jq = new JdbcQuery();
            jq.setQueryId(rs.getString(3));
            jq.setText(rs.getString(5));
            jq.setName(rs.getString(2));
            jq.setType(rs.getString(1));

            jmc.addQueryToGroup(jq);
            localLogger.debug("Added QueryGroup:" + jmc.getName() + ":" + jq.getType() + ":" +
                jq.getName());
          }

          JdbcAttribute attribute = jmc.getAttributeMap().get(rs.getString(8));

          if (attribute == null) {
            // There may be queries with no params
            newSeq = 1000;
            localLogger.debug("No Parameters for " + groupType + ":" + jq.getName());

            //throw new CpoException("Cannot Add Null Parameter to
            // Parameter List");
          } else {
            JdbcParameter parameter = new JdbcParameter(attribute, rs.getString(10));
            jq.getParameterList().add(parameter);
            localLogger.debug("Added Parameter:" +
                attribute.getName() //+ ":" + attribute.getDbName() + ":"
                //+ attribute.getDbType() + ":"
                + parameter.getType());
          }

          oldSeq = newSeq;
        }
      } catch (SQLException e) {
        String msg = "loadQueryGroups() falied:" + sql + ":" + id;
        localLogger.error(msg, e);
        throw new CpoException(msg, e);
      } finally {
        if (rs != null) {
          try {
            rs.close();
          } catch (Exception e) {
          }
        }

        if (ps != null) {
          try {
            ps.close();
          } catch (Exception e) {
          }
        }
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param jq DOCUMENT ME!
   * @return DOCUMENT ME!
   */
  private String parameterToString(JdbcQuery jq) {
    ArrayList<JdbcParameter> parameters;
    int j;
    JdbcParameter parameter;
    JdbcAttribute attribute;
    int type;
    Class<?> c;
    StringBuffer sb = new StringBuffer("Parameter list for ");

    if (jq == null) {
      return " null query.";
    }

    sb.append(jq.getName() + " " + jq.getType());
    parameters = jq.getParameterList();

    for (j = 1; j <= parameters.size(); j++) {
      parameter = parameters.get(j - 1);

      if (parameter != null) {
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
        } catch (Exception e) {
          String msg = "parameterToString() Failed:";
          logger.error(msg);
        }
      }
    }

    return sb.toString();
  }

  /**
   * Provides a mechanism for the user to obtain a CpoTrxAdapter object. This object allows the
   * to control when commits and rollbacks occur on CPO.
   * 
   * 
   * <pre>Example:<code>
   * 
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * class CpoTrxAdapter cpoTrx = null;
   * 
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * 	cpoTrx = cpo.getCpoTrxAdapter();
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * 
   * if (cpo!=null) {
   * 	try{
   * 		for (int i=0; i<3; i++){
   * 			so = new SomeObject();
   * 			so.setId(1);
   * 			so.setName("SomeName");
   * 			cpo.updateObject("myUpdate",so);
   * 		}
   * 		cpoTrx.commit();
   * 	} catch (CpoException ce) {
   * 		// Handle the error
   * 		cpoTrx.rollback();
   * 	}
   * }
   *</code>
   *</pre>
   *
   * @return A CpoTrxAdapter to manage the transactionality of CPO
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see CpoTrxAdapter
   */
  public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    return new JdbcCpoTrxAdapter(getMetaDataSource(), getMetaDataSourceName(), getWriteConnection(), batchUpdatesSupported_, getDbTablePrefix());
  }

  public String getDbTablePrefix() {
    return dbTablePrefix;
  }

  public void setDbTablePrefix(String dbTablePrefix) {
    this.dbTablePrefix = dbTablePrefix;
  }
  
  private class RetrieverThread<T,C> extends Thread {
    String name;
    C criteria;
    T result;
    Collection<CpoWhere> wheres;
    Collection<CpoOrderBy> orderBy;
    Collection<CpoNativeQuery> nativeQueries;
    boolean useRetrieve;
    CpoBlockingResultSet<T> resultSet;
    Thread callingThread = null;
    
    public RetrieverThread(String name, C criteria, T result,
        Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, boolean useRetrieve, CpoBlockingResultSet<T> resultSet){
      this.name=name;
      this.criteria = criteria;
      this.result = result;
      this.wheres = wheres;
      this.orderBy = orderBy;
      this.useRetrieve = useRetrieve;
      this.resultSet = resultSet;
      this.nativeQueries = nativeQueries;
      callingThread = Thread.currentThread();
    }

    public void run() {
      try {
        processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, false, resultSet);
      } catch (CpoException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } finally {
        resultSet.setDone(true);
        // Interrupt the thread in case it is in a wait
        callingThread.interrupt();

      }
    }
  }

  protected boolean isConnectionBusy(Connection c) {
    // do nothing for JdbcCpoAdapter
    // overridden by JdbcTrxAdapter
    return false;
  }

  protected void setConnectionBusy(Connection c) {
    // do nothing for JdbcCpoAdapter
    // overridden by JdbcTrxAdapter
  }
  
  protected void clearConnectionBusy(Connection c) {
    // do nothing for JdbcCpoAdapter
    // overridden by JdbcTrxAdapter
  }
    
}
