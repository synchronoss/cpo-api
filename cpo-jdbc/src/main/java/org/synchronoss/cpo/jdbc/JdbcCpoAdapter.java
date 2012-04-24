/*
 *  Copyright (C) 2003-2012 David E. Berry
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

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaAdapter;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

/**
 * JdbcCpoAdapter is an interface for a set of routines that are responsible for managing value objects from a
 * datasource.
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
  private static Logger logger = LoggerFactory.getLogger(JdbcCpoAdapter.class.getName());
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
  private boolean invalidReadConnection_ = false;
  private boolean metaEqualsWrite_ = false;
  private boolean batchUpdatesSupported_ = false;
  /**
   * CpoMetaAdapter allows you to get the meta data for a class.
   */
  private CpoMetaAdapter metaAdapter = null;

  protected JdbcCpoAdapter() {
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param jdsiMeta This datasource that identifies the cpo metadata datasource
   * @param jdsiTrx The datasoruce that identifies the transaction database.
   * @throws org.synchronoss.cpo.CpoException exception
   */
  public JdbcCpoAdapter(CpoMetaAdapter metaAdapter, DataSourceInfo jdsiTrx)
          throws CpoException {

    this.metaAdapter = metaAdapter;
    setWriteDataSource(jdsiTrx.getDataSource());
    setReadDataSource(getWriteDataSource());
    processDatabaseMetaData();
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param jdsiMeta This datasource that identifies the cpo metadata datasource
   * @param jdsiWrite The datasource that identifies the transaction database for write transactions.
   * @param jdsiRead The datasource that identifies the transaction database for read-only transactions.
   * @throws org.synchronoss.cpo.CpoException exception
   */
  public JdbcCpoAdapter(CpoMetaAdapter metaAdapter, DataSourceInfo jdsiWrite, DataSourceInfo jdsiRead)
          throws CpoException {
    this.metaAdapter = metaAdapter;
    setWriteDataSource(jdsiWrite.getDataSource());
    setReadDataSource(jdsiRead.getDataSource());
    processDatabaseMetaData();
  }

  protected JdbcCpoAdapter(CpoMetaAdapter metaAdapter, boolean batchSupported)
          throws CpoException {
    this.metaAdapter = metaAdapter;
    batchUpdatesSupported_ = batchSupported;
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
    } catch (Throwable t) {
      logger.error(ExceptionHelper.getLocalizedMessage(t), t);
      throw new CpoException("Could Not Retrieve Database Meta Data", t);
    } finally {
      closeConnection(c);
    }
  }

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in the datasource. This
   * method creates and stores the object in the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.CREATE_GROUP, null, null, null, null);
  }

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in the datasource. This
   * method creates and stores the object in the datasource
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   *
   * @param name The String name of the CREATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used which is equivalent to insertObject(Object obj);
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.CREATE_GROUP, name, null, null, null);
  }

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in the datasource. This
   * method creates and stores the object in the datasource
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   *
   * @param name The String name of the CREATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used which is equivalent to insertObject(Object obj);
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(String name, T obj, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.CREATE_GROUP, name, wheres, orderBy, nativeQueries);
  }

  /**
   * Iterates through a collection of Objects, creates and stores them in the datasource. The assumption is that the
   * objects contained in the collection do not exist in the datasource.
   *
   * This method creates and stores the objects in the datasource. The objects in the collection will be treated as one
   * transaction, assuming the datasource supports transactions.
   *
   * This means that if one of the objects fail being created in the datasource then the CpoAdapter will stop processing
   * the remainder of the collection and rollback all the objects created thus far. Rollback is on the underlying
   * datasource's support of rollback.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.CREATE_GROUP, null, null, null, null);
  }

  /**
   * Iterates through a collection of Objects, creates and stores them in the datasource. The assumption is that the
   * objects contained in the collection do not exist in the datasource.
   *
   * This method creates and stores the objects in the datasource. The objects in the collection will be treated as one
   * transaction, assuming the datasource supports transactions.
   *
   * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param name The String name of the CREATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(String name, Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.CREATE_GROUP, name, null, null, null);
  }

  /**
   * Iterates through a collection of Objects, creates and stores them in the datasource. The assumption is that the
   * objects contained in the collection do not exist in the datasource.
   *
   * This method creates and stores the objects in the datasource. The objects in the collection will be treated as one
   * transaction, assuming the datasource supports transactions.
   *
   * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param name The String name of the CREATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.CREATE_GROUP, name, wheres, orderBy, nativeQueries);
  }

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the datasource. This method
   * stores the object in the datasource
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.DELETE_GROUP, null, null, null, null);
  }

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the datasource. This method
   * stores the object in the datasource
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The String name of the DELETE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.DELETE_GROUP, name, null, null, null);
  }

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the datasource. This method
   * stores the object in the datasource
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The String name of the DELETE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(String name, T obj, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.DELETE_GROUP, name, wheres, orderBy, nativeQueries);
  }

  /**
   * Removes the Objects contained in the collection from the datasource. The assumption is that the object exists in
   * the datasource. This method stores the objects contained in the collection in the datasource. The objects in the
   * collection will be treated as one transaction, assuming the datasource supports transactions.
   *
   * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.DELETE_GROUP, null, null, null, null);
  }

  /**
   * Removes the Objects contained in the collection from the datasource. The assumption is that the object exists in
   * the datasource. This method stores the objects contained in the collection in the datasource. The objects in the
   * collection will be treated as one transaction, assuming the datasource supports transactions.
   *
   * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param name The String name of the DELETE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(String name, Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.DELETE_GROUP, name, null, null, null);
  }

  /**
   * Removes the Objects contained in the collection from the datasource. The assumption is that the object exists in
   * the datasource. This method stores the objects contained in the collection in the datasource. The objects in the
   * collection will be treated as one transaction, assuming the datasource supports transactions.
   *
   * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param name The String name of the DELETE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.DELETE_GROUP, name, wheres, orderBy, nativeQueries);
  }

  /**
   * Executes an Object whose metadata will call an executable within the datasource. It is assumed that the executable
   * object exists in the metadatasource. If the executable does not exist, an exception will be thrown.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param object This is an Object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to populate the IN arguments used to executed the datasource object.
   *
   * An object of this type will be created and filled with the returned data from the value_object. This newly created
   * object will be returned from this method.
   * @return An object populated with the OUT arguments returned from the executable object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T executeObject(T object)
          throws CpoException {
    return processExecuteGroup(null, object, object);
  }

  /**
   * Executes an Object whose metadata will call an executable within the datasource. It is assumed that the executable
   * object exists in the metadatasource. If the executable does not exist, an exception will be thrown.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The filter name which tells the datasource which objects should be returned. The name also signifies
   * what data in the object will be populated.
   * @param object This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to populate the IN arguments used to retrieve the collection of objects. This object defines
   * the object type that will be returned in the collection and contain the result set data or the OUT Parameters.
   * @return A result object populate with the OUT arguments
   * @throws CpoException if there are errors accessing the datasource
   */
  public <T> T executeObject(String name, T object)
          throws CpoException {
    return processExecuteGroup(name, object, object);
  }

  /**
   * Executes an Object that represents an executable object within the datasource. It is assumed that the object exists
   * in the datasource. If the object does not exist, an exception will be thrown
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The String name of the EXECUTE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to populate the IN arguments used to retrieve the collection of objects.
   * @param result This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object defines the object type that will be created, filled with the return data and returned from this
   * method.
   * @return An object populated with the out arguments
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T executeObject(String name, C criteria, T result)
          throws CpoException {
    return processExecuteGroup(name, criteria, result);
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. This object will be searched for inside the datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(T obj) throws CpoException {
    return this.existsObject(null, obj);
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The String name of the EXISTS Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. This object will be searched for inside the datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(String name, T obj) throws CpoException {
    return this.existsObject(name, obj, null);
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The String name of the EXISTS Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. This object will be searched for inside the datasource.
   * @param where A CpoWhere object that passes in run-time constraints to the query that performs the the exist
   * @return The number of objects that exist in the datasource that match the specified object
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException {
    Connection c = null;
    long objCount = -1;

    try {
      c = getReadConnection();

      objCount = existsObject(name, obj, c, wheres);
    } catch (Exception e) {
      throw new CpoException("existsObjects(String, Object) failed", e);
    } finally {
      closeConnection(c);
    }

    return objCount;
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to execute to persist the
   * object.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @param con The datasource Connection with which to check if the object exists
   * @return The int value of the first column returned in the record set
   * @throws CpoException exception will be thrown if the Query Group has a query count != 1
   */
  protected <T> long existsObject(String name, T obj, Connection con, Collection<CpoWhere> wheres)
          throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    CpoFunction jq = null;
    CpoClass jmc;
    List<CpoFunction> functions;
    long objCount = 0;
    int i;
    Logger localLogger = logger;

    if (obj == null) {
      throw new CpoException("NULL Object passed into existsObject");
    }

    try {
      jmc = metaAdapter.getMetaClass(obj);
      functions = jmc.getFunctionGroup(JdbcCpoAdapter.EXIST_GROUP, name).getFunctions();
      localLogger = LoggerFactory.getLogger(jmc.getMetaClass().getName());

      for (i = 0; i < functions.size(); i++) {
        jq = functions.get(i);
        JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, obj, wheres, null, null);
        ps = jpsf.getPreparedStatement();

        long qCount = 0; // set the results for this query to 0

        localLogger.info(jq.getExpression());
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
      if (jq != null) {
        msg += jq.getExpression();
      }

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
   * newOrderBy allows you to dynamically change the order of the objects in the resulting collection. This allows you
   * to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @return A CpoOrderBy object to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public CpoOrderBy newOrderBy(String attribute, boolean ascending)
          throws CpoException {
    return new JdbcCpoOrderBy(attribute, ascending);
  }

  /**
   * newOrderBy allows you to dynamically change the order of the objects in the resulting collection. This allows you
   * to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @param function A string which represents a datasource function that will be called on the attribute. must be
   * contained in the function string. The attribute name will be replaced at run-time with its datasource counterpart
   * @return A CpoOrderBy object to be passed into retrieveBeans.
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
   * @param attr DOCUMENT ME!
   * @param comp DOCUMENT ME!
   * @param value DOCUMENT ME!
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
   * @param attr DOCUMENT ME!
   * @param comp DOCUMENT ME!
   * @param value DOCUMENT ME!
   * @param not DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not) throws CpoException {
    return new JdbcCpoWhere(logical, attr, comp, value, not);
  }

  /**
   * Persists the Object into the datasource. The CpoAdapter will check to see if this object exists in the datasource.
   * If it exists, the object is updated in the datasource If the object does not exist, then it is created in the
   * datasource. This method stores the object in the datasource. This method uses the default EXISTS, CREATE, and
   * UPDATE query groups specified for this object.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @return A count of the number of objects persisted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObject(T obj)
          throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.PERSIST_GROUP, null, null, null, null);
  }

  /**
   * Persists the Object into the datasource. The CpoAdapter will check to see if this object exists in the datasource.
   * If it exists, the object is updated in the datasource If the object does not exist, then it is created in the
   * datasource. This method stores the object in the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to execute to persist the
   * object.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @return A count of the number of objects persisted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObject(String name, T obj)
          throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.PERSIST_GROUP, name, null, null, null);
  }

  /**
   * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if this object exists in the
   * datasource. If it exists, the object is updated in the datasource If the object does not exist, then it is created
   * in the datasource. This method stores the object in the datasource. The objects in the collection will be treated
   * as one transaction, meaning that if one of the objects fail being inserted or updated in the datasource then the
   * entire collection will be rolled back.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObjects(Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.PERSIST_GROUP, null, null, null, null);
  }

  /**
   * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if this object exists in the
   * datasource. If it exists, the object is updated in the datasource If the object does not exist, then it is created
   * in the datasource. This method stores the object in the datasource. The objects in the collection will be treated
   * as one transaction, meaning that if one of the objects fail being inserted or updated in the datasource then the
   * entire collection will be rolled back.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Query groups to execute to persist the
   * object.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObjects(String name, Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.PERSIST_GROUP, name, null, null, null);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * query defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param bean This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The
   * input bean is used to specify the search criteria, the output bean is populated with the results of the query.
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveBean(T bean)
          throws CpoException {
    T o = processSelectGroup(bean, null, null, null, null);

    return (o);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * query defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name DOCUMENT ME!
   * @param bean This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The
   * input bean is used to specify the search criteria, the output bean is populated with the results of the query.
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveBean(String name, T bean)
          throws CpoException {
    T o = processSelectGroup(bean, name, null, null, null);

    return (o);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * query defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name DOCUMENT ME!
   * @param bean This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The
   * input bean is used to specify the search criteria, the output bean is populated with the results of the query.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveBean(String name, T bean, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
          throws CpoException {
    T o = processSelectGroup(bean, name, wheres, orderBy, nativeQueries);

    return (o);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * query defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy) throws CpoException {
    return retrieveBean(name, criteria, result, wheres, orderBy, null);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * query defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    Iterator<T> it = processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, true).iterator();
    if (it.hasNext()) {
      return it.next();
    } else {
      return null;
    }
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria) throws CpoException {
    return processSelectGroup(name, criteria, criteria, null, null, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param where A CpoWhere bean that defines the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria, CpoWhere where,
          Collection<CpoOrderBy> orderBy) throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<CpoWhere>();
      wheres.add(where);
    }
    return processSelectGroup(name, criteria, criteria, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, criteria, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, criteria, null, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the bean type that will be returned in the collection.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result) throws CpoException {
    return processSelectGroup(name, criteria, result, null, null, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the bean type that will be returned in the collection.
   * @param where A CpoWhere bean that defines the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, CpoWhere where,
          Collection<CpoOrderBy> orderBy) throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<CpoWhere>();
      wheres.add(where);
    }
    return processSelectGroup(name, criteria, result, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   * same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, false);
  }

  /**
   * Retrieves the bean from the datasource. This method returns an Iterator immediately. The iterator will get filled
   * asynchronously by the cpo framework. The framework will stop supplying the iterator with beans if the
   * beanBufferSize is reached.
   *
   * If the consumer of the iterator is processing records faster than the framework is filling it, then the iterator
   * will wait until it has data to provide.
   *
   * @param name The filter name which tells the datasource which beans should be returned. The name also signifies what
   * data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result This is an bean that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   * This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @param beanBufferSize the maximum number of beans that the Iterator is allowed to cache. Once reached, the CPO
   * framework will halt processing records from the datasource.
   *
   * @return An iterator that will be fed beans from the CPO framework.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> CpoResultSet<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, int queueSize) throws CpoException {
    CpoBlockingResultSet<T> resultSet = new CpoBlockingResultSet<T>(queueSize);
    RetrieverThread<T, C> retrieverThread = new RetrieverThread<T, C>(name, criteria, result, wheres, orderBy, nativeQueries, false, resultSet);

    retrieverThread.start();
    return resultSet;
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the datasource. If the
   * retrieve query defined for this objects returns more than one row, an exception will be thrown.
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * The input object is used to specify the search criteria, the output object is populated with the results of the
   * query.
   * @return An object of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   *
   * @deprecated use retrieveBean
   */
  @Deprecated
  public <T> T retrieveObject(T obj)
          throws CpoException {
    return retrieveBean(obj);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the datasource. If the
   * retrieve query defined for this objects returns more than one row, an exception will be thrown.
   *
   * @param name DOCUMENT ME!
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * The input object is used to specify the search criteria, the output object is populated with the results of the
   * query.
   * @return An object of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   *
   * @deprecated use retrieveBean
   */
  @Deprecated
  public <T> T retrieveObject(String name, T obj)
          throws CpoException {
    return retrieveBean(name, obj);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the datasource. If the
   * retrieve query defined for this objects returns more than one row, an exception will be thrown.
   *
   * @param name The filter name which tells the datasource which objects should be returned. The name also signifies
   * what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the arguments used to retrieve the collection of objects.
   * @param result This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the object type that will be returned in the collection.
   * @param wheres A collection of CpoWhere objects that define the constraints that should be used when retrieving
   * objects
   * @param orderBy The CpoOrderBy object that defines the order in which objects should be returned
   * @return An object of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   *
   * @deprecated use retrieveBean
   */
  @Deprecated
  public <T, C> T retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy) throws CpoException {
    return retrieveBean(name, criteria, result, wheres, orderBy);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the datasource. If the
   * retrieve query defined for this objects returns more than one row, an exception will be thrown.
   *
   * @param name The filter name which tells the datasource which objects should be returned. The name also signifies
   * what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the arguments used to retrieve the collection of objects.
   * @param result This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the object type that will be returned in the collection.
   * @param wheres A collection of CpoWhere objects that define the constraints that should be used when retrieving
   * objects
   * @param orderBy The CpoOrderBy object that defines the order in which objects should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return An object of the same type as the result argument that is filled in as specified the metadata for the
   * retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   *
   * @deprecated use retrieveBean
   */
  @Deprecated
  public <T, C> T retrieveObject(String name, C criteria, T result, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    return retrieveBean(name, criteria, result, wheres, orderBy, nativeQueries);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the datasource.
   *
   * @param name The filter name which tells the datasource which objects should be returned. The name also signifies
   * what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the arguments used to retrieve the collection of objects.
   * @param result This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the object type that will be returned in the collection.
   * @param wheres A collection of CpoWhere objects that define the constraints that should be used when retrieving
   * objects
   * @param orderBy The CpoOrderBy object that defines the order in which objects should be returned
   * @return A collection of objects will be returned that meet the criteria specified by obj. The objects will be of
   * the same type as the Object that was passed in. If no objects match the criteria, an empty collection will be
   * returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   *
   * @deprecated use retrieveBeans
   */
  @Deprecated
  public <T, C> Collection<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, T result) throws CpoException {
    return retrieveBeans(name, criteria, result, wheres, orderBy);
  }

  /**
   * Retrieves the Object from the datasource. The assumption is that the object exists in the datasource.
   *
   * @param name The filter name which tells the datasource which objects should be returned. The name also signifies
   * what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the arguments used to retrieve the collection of objects.
   * @param result This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the object type that will be returned in the collection.
   * @param wheres A collection of CpoWhere objects that define the constraints that should be used when retrieving
   * objects
   * @param orderBy The CpoOrderBy object that defines the order in which objects should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @return A collection of objects will be returned that meet the criteria specified by obj. The objects will be of
   * the same type as the Object that was passed in. If no objects match the criteria, an empty collection will be
   * returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   *
   * @deprecated use retrieveBeans
   */
  @Deprecated
  public <T, C> Collection<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, T result) throws CpoException {
    return retrieveBeans(name, criteria, result, wheres, orderBy, nativeQueries);
  }

  /**
   * Retrieves the Object from the datasource. This method returns an Iterator immediately. The iterator will get filled
   * asynchronously by the cpo framework. The framework will stop supplying the iterator with objects if the
   * objectBufferSize is reached.
   *
   * If the consumer of the iterator is processing records faster than the framework is filling it, then the iterator
   * will wait until it has data to provide.
   *
   * @param name The filter name which tells the datasource which objects should be returned. The name also signifies
   * what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the arguments used to retrieve the collection of objects.
   * @param result This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to specify the object type that will be returned in the collection.
   * @param wheres A collection of CpoWhere objects that define the constraints that should be used when retrieving
   * objects
   * @param orderBy The CpoOrderBy object that defines the order in which objects should be returned
   * @param nativeQueries Native query text that will be used to augment the query text stored in the meta data. This
   * text will be embedded at run-time
   * @param objectBufferSize the maximum number of objects that the Iterator is allowed to cache. Once reached, the CPO
   * framework will halt processing records from the datasource.
   *
   * @return An iterator that will be fed objects from the CPO framework.
   *
   * @throws CpoException Thrown if there are errors accessing the datasource
   *
   * @deprecated use retrieveBeans
   */
  @Deprecated
  public <T, C> CpoResultSet<T> retrieveObjects(String name, C criteria, Collection<CpoWhere> wheres,
          Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, T result, int queueSize) throws CpoException {
    return retrieveBeans(name, criteria, result, wheres, orderBy, nativeQueries, queueSize);
  }

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object exists in the datasource. If it
   * exists then the object will be updated. If it does not exist, an exception will be thrown
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.UPDATE_GROUP, null, null, null, null);
  }

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object exists in the datasource. If it
   * exists then the object will be updated. If it does not exist, an exception will be thrown
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The String name of the UPDATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.UPDATE_GROUP, name, null, null, null);
  }

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object exists in the datasource. If it
   * exists then the object will be updated. If it does not exist, an exception will be thrown
   *
   * <pre>Example:
   * <code>
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
   * </code>
   * </pre>
   *
   * @param name The String name of the UPDATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown.
   * @param wheres A collection of CpoWhere objects to be used by the query
   * @param orderBy A collection of CpoOrderBy objects to be used by the query
   * @param nativeQueries A collection of CpoNativeQuery objects to be used by the query
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries) throws CpoException {
    return processUpdateGroup(obj, JdbcCpoAdapter.UPDATE_GROUP, name, wheres, orderBy, nativeQueries);
  }

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects contained in the collection
   * exist in the datasource. This method stores the object in the datasource. The objects in the collection will be
   * treated as one transaction, meaning that if one of the objects fail being updated in the datasource then the entire
   * collection will be rolled back, if supported by the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.UPDATE_GROUP, null, null, null, null);
  }

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects contained in the collection
   * exist in the datasource. This method stores the object in the datasource. The objects in the collection will be
   * treated as one transaction, meaning that if one of the objects fail being updated in the datasource then the entire
   * collection will be rolled back, if supported by the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param name The String name of the UPDATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(String name, Collection<T> coll)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.UPDATE_GROUP, name, null, null, null);
  }

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects contained in the collection
   * exist in the datasource. This method stores the object in the datasource. The objects in the collection will be
   * treated as one transaction, meaning that if one of the objects fail being updated in the datasource then the entire
   * collection will be rolled back, if supported by the datasource.
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @param name The String name of the UPDATE Query group that will be used to create the object in the datasource.
   * null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   * class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere objects to be used by the query
   * @param orderBy A collection of CpoOrderBy objects to be used by the query
   * @param nativeQueries A collection of CpoNativeQuery objects to be used by the query
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
          throws CpoException {
    return processUpdateGroup(coll, JdbcCpoAdapter.UPDATE_GROUP, name, wheres, orderBy, nativeQueries);
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
   * @param obj DOCUMENT ME!
   * @param type DOCUMENT ME!
   * @param name DOCUMENT ME!
   * @param c DOCUMENT ME!
   * @param meta metadata conn
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> String getGroupType(T obj, String type, String name, Connection c)
          throws CpoException {
    String retType = type;
    long objCount;

    if (JdbcCpoAdapter.PERSIST_GROUP.equals(retType) == true) {
      objCount = existsObject(name, obj, c, null);

      if (objCount == 0) {
        retType = JdbcCpoAdapter.CREATE_GROUP;
      } else if (objCount == 1) {
        retType = JdbcCpoAdapter.UPDATE_GROUP;
      } else {
        throw new CpoException("Persist can only UPDATE one record. Your EXISTS query returned 2 or more.");
      }
    }

    return retType;
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
   * @param connection DOCUMENT ME!
   */
  protected void closeConnection(Connection connection) {
    try {
      clearConnectionBusy(connection);
      if (connection != null && !isStaticConnection(connection) && !connection.isClosed()) {
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
   * Executes an Object whose MetaData contains a stored procedure. An assumption is that the object exists in the
   * datasource.
   *
   * @param name The filter name which tells the datasource which objects should be returned. The name also signifies
   * what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object is used to populate the IN arguments used to retrieve the collection of objects.
   * @param result This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   * This object defines the object type that will be returned in the
   * @return A result object populate with the OUT arguments
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> T processExecuteGroup(String name, C criteria, T result)
          throws CpoException {
    Connection c = null;
    T obj = null;

    try {
      c = getWriteConnection();
      obj = processExecuteGroup(name, criteria, result, c);
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException) {
        throw (CpoException) e;
      } else {
        throw new CpoException("processExecuteGroup(String name, Object criteria, Object result) failed",
                e);
      }
    } finally {
      closeConnection(c);
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
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> T processExecuteGroup(String name, C criteria, T result,
          Connection conn) throws CpoException {
    CallableStatement cstmt = null;
    List<CpoFunction> functions;
    CpoFunction jq = null;
    CpoClass jmcCriteria;
    CpoClass jmcResult;
    T returnObject = null;
    Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass().getName());

    //Object[] setterArgs = {null};
    Class<T> jmcClass;
    List<CpoArgument> arguments;
    JdbcArgument argument;
    JdbcAttribute attribute;
    JdbcCallableStatementFactory jcsf = null;

    //Object[] getterArgs = {};
    int j;
    int i;

    if (criteria == null || result == null) {
      throw new CpoException("NULL Object passed into executeObject");
    }

    try {
      jmcCriteria = metaAdapter.getMetaClass(criteria);
      jmcResult = metaAdapter.getMetaClass(result);
      functions = jmcCriteria.getFunctionGroup(JdbcCpoAdapter.EXECUTE_GROUP, name).getFunctions();
      localLogger.info("===================processExecuteGroup (" + name + ") Count<"
              + functions.size() + ">=========================");

      jmcClass = (Class<T>) jmcResult.getMetaClass();
      try {
        returnObject = (T) result.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        throw new CpoException("Unable to access the constructor of the Return Object", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Object", iae);
      }

      // Loop through the queries and process each one
      for (i = 0; i < functions.size(); i++) {
        // Get the current call
        jq = functions.get(i);

        jcsf = new JdbcCallableStatementFactory(conn, this, jq, criteria);

        localLogger.debug("Executing Call:" + jmcCriteria.getName() + ":" + name);

        cstmt = jcsf.getCallableStatement();

        cstmt.execute();

        jcsf.release();

        localLogger.debug("Processing Call:" + jmcCriteria.getName() + ":" + name);

        // Add Code here to go through the arguments, find record sets,
        // and process them
        // Process the non-record set out params and make it the first
        // object in the collection

        // Loop through the OUT Parameters and set them in the result
        // object
        arguments = jcsf.getOutArguments();
        if (!arguments.isEmpty()) {
          for (j = 0; j < arguments.size(); j++) {
            argument = (JdbcArgument) arguments.get(j);

            if (argument.isOutParameter()) {
              attribute = argument.getAttribute();
              attribute.invokeSetter(returnObject, cstmt, j + 1);
            }
          }
        }

        cstmt.close();
      }
    } catch (Throwable t) {
      String msg = "ProcessExecuteGroup(String name, Object criteria, Object result, Connection conn) failed. SQL=";
      if (jq != null) {
        msg += jq.getExpression();
      }
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      if (cstmt != null) {
        try {
          cstmt.close();
        } catch (Exception e) {
        }
      }
      if (jcsf != null) {
        jcsf.release();
      }
    }

    return returnObject;
  }

  /**
   * Retrieves the Object from the datasource.
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   * defined an exception will be thrown. The input object is used to specify the search criteria.
   * @param groupName The name which identifies which RETRIEVE Query group to execute to retrieve the object.
   * @param wheres A collection of CpoWhere objects to be used by the query
   * @param orderBy A collection of CpoOrderBy objects to be used by the query
   * @param nativeQueries A collection of CpoNativeQuery objects to be used by the query
   * @return A populated object of the same type as the Object passed in as a argument. If no objects match the criteria
   * a NULL will be returned.
   * @throws CpoException the retrieve query defined for this objects returns more than one row, an exception will be
   * thrown.
   */
  protected <T> T processSelectGroup(T obj, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
          throws CpoException {
    Connection c = null;
    T result = null;

    try {
      c = getReadConnection();
      result = processSelectGroup(obj, groupName, wheres, orderBy, nativeQueries, c);

      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException) {
        throw (CpoException) e;
      } else {
        throw new CpoException("processSelectGroup(Object obj, String groupName) failed",
                e);
      }
    } finally {
      closeConnection(c);
    }


    return result;
  }

  /**
   * DOCUMENT ME!
   *
   * @param obj DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @param con DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> T processSelectGroup(T obj, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, Connection con)
          throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    CpoFunction jq;
    CpoClass jmc;
    List<CpoFunction> functions;
    JdbcAttribute attribute;
    T criteriaObj = obj;
    boolean recordsExist = false;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());

    int recordCount = 0;
    int attributesSet = 0;

    int i;
    int k;
    T rObj = null;

    if (obj == null) {
      throw new CpoException("NULL Object passed into retrieveBean");
    }

    try {
      jmc = metaAdapter.getMetaClass(criteriaObj);
      functions = jmc.getFunctionGroup(JdbcCpoAdapter.RETRIEVE_GROUP, groupName).getFunctions();

      localLogger.info("=================== Class=<" + criteriaObj.getClass() + "> Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");

      try {
        rObj = (T) obj.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        if (obj != null) {
          localLogger.error("=================== Could not access default constructor for Class=<" + obj.getClass() + "> ==================");
        } else {
          localLogger.error("=================== Could not access default constructor for class ==================");
        }

        throw new CpoException("Unable to access the constructor of the Return Object", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Object", iae);
      }


      for (i = 0; i < functions.size(); i++) {
        jq = functions.get(i);

        JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, criteriaObj, wheres, orderBy, nativeQueries);
        ps = jpsf.getPreparedStatement();

        // insertions on
        // selectgroup
        rs = ps.executeQuery();
        jpsf.release();

        if (rs.isBeforeFirst() == true) {
          rsmd = rs.getMetaData();

          if ((rsmd.getColumnCount() == 2)
                  && "CPO_ATTRIBUTE".equalsIgnoreCase(rsmd.getColumnLabel(1))
                  && "CPO_VALUE".equalsIgnoreCase(rsmd.getColumnLabel(2))) {
            while (rs.next()) {
              recordsExist = true;
              recordCount++;
              attribute = (JdbcAttribute) jmc.getAttributeData(rs.getString(1));

              if (attribute != null) {
                attribute.invokeSetter(rObj, rs, 2);
                attributesSet++;
              }
            }
          } else if (rs.next()) {
            recordsExist = true;
            recordCount++;
            for (k = 1; k <= rsmd.getColumnCount(); k++) {
              attribute = (JdbcAttribute) jmc.getAttributeData(rsmd.getColumnLabel(k));

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

    } catch (Throwable t) {
      String msg = "ProcessSeclectGroup(Object) failed: " + ExceptionHelper.getLocalizedMessage(t);
      localLogger.error(msg, t);
      rObj = null;
      throw new CpoException(msg, t);
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
   * @param name DOCUMENT ME!
   * @param criteria DOCUMENT ME!
   * @param result DOCUMENT ME!
   * @param where DOCUMENT ME!
   * @param orderBy DOCUMENT ME!
   * @param useRetrieve DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> List<T> processSelectGroup(String name, C criteria, T result,
          Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, boolean useRetrieve) throws CpoException {
    Connection con = null;
    CpoArrayResultSet<T> resultSet = new CpoArrayResultSet<T>();

    try {
      con = getReadConnection();
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, con, useRetrieve, resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(con);
      } catch (Exception re) {
      }

      if (e instanceof CpoException) {
        throw (CpoException) e;
      } else {
        throw new CpoException("processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed",
                e);
      }
    } finally {
      closeConnection(con);
    }

    return resultSet;
  }

  protected <T, C> void processSelectGroup(String name, C criteria, T result,
          Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, boolean useRetrieve, CpoResultSet<T> resultSet) throws CpoException {
    Connection con = null;

    try {
      con = getReadConnection();
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeQueries, con, useRetrieve, resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(con);
      } catch (Exception re) {
      }

      if (e instanceof CpoException) {
        throw (CpoException) e;
      } else {
        throw new CpoException("processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed",
                e);
      }
    } finally {
      closeConnection(con);
    }
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
   * @param useRetrieve DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> void processSelectGroup(String name, C criteria, T result,
          Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, Connection con, boolean useRetrieve, CpoResultSet<T> resultSet)
          throws CpoException {
    Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass().getName());
    PreparedStatement ps = null;
    List<CpoFunction> queryGroup;
    CpoFunction jq;
    CpoClass jmcCriteria;
    CpoClass jmcResult;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    int columnCount;
    int k;
    T obj;
    Class<T> jmcClass;
    JdbcAttribute[] attributes;
    JdbcPreparedStatementFactory jpsf;
    int i;

    if (criteria == null || result == null) {
      throw new CpoException("NULL Object passed into retrieveBean or retrieveBeans");
    }

    try {
      jmcCriteria = metaAdapter.getMetaClass(criteria);
      jmcResult = metaAdapter.getMetaClass(result);
      if (useRetrieve) {
        localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + name + "> =========================");
        queryGroup = jmcCriteria.getFunctionGroup(JdbcCpoAdapter.RETRIEVE_GROUP, name).getFunctions();
      } else {
        localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.LIST_GROUP + "> Name=<" + name + "> =========================");
        queryGroup = jmcCriteria.getFunctionGroup(JdbcCpoAdapter.LIST_GROUP, name).getFunctions();
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

        jmcClass = (Class<T>) jmcResult.getMetaClass();
        columnCount = rsmd.getColumnCount();

        attributes = new JdbcAttribute[columnCount + 1];

        for (k = 1; k <= columnCount; k++) {
          attributes[k] = (JdbcAttribute) jmcResult.getAttributeData(rsmd.getColumnLabel(k));
        }

        while (rs.next()) {
          try {
            obj = (T) result.getClass().newInstance();
          } catch (IllegalAccessException iae) {
            if (result != null) {
              localLogger.error("=================== Could not access default constructor for Class=<" + result.getClass() + "> ==================");
            } else {
              localLogger.error("=================== Could not access default constructor for class ==================");
            }

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
    } catch (Throwable t) {
      String msg =
              "ProcessSelectGroup(String name, Object criteria, Object result, CpoWhere where, Collection orderBy, Connection con) failed. Error:";
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
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
   * @param obj DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
          throws CpoException {
    Connection c = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();
      updateCount = processUpdateGroup(obj, groupType, groupName, wheres, orderBy, nativeQueries, c);
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException) {
        throw (CpoException) e;
      } else {
        throw new CpoException("processUdateGroup(Object obj, String groupType, String groupName) failed",
                e);
      }
    } finally {
      closeConnection(c);
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
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, Connection con)
          throws CpoException {
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());
    CpoClass jmc;
    List<CpoFunction> queryGroup;
    PreparedStatement ps = null;
    CpoFunction jq = null;
    JdbcPreparedStatementFactory jpsf = null;
    int i;
    long updateCount = 0;

    if (obj == null) {
      throw new CpoException("NULL Object passed into insertObject, deleteObject, updateObject, or persistObject");
    }

    try {
      jmc = metaAdapter.getMetaClass(obj);
      queryGroup = jmc.getFunctionGroup(getGroupType(obj, groupType, groupName, con), groupName).getFunctions();
      localLogger.info("=================== Class=<" + obj.getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

      int numRows = 0;

      for (i = 0; i < queryGroup.size(); i++) {
        jq = queryGroup.get(i);
        jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, obj, wheres, orderBy, nativeQueries);
        ps = jpsf.getPreparedStatement();
        numRows += ps.executeUpdate();
        jpsf.release();
        ps.close();
      }
      localLogger.info("=================== " + numRows + " Updates - Class=<" + obj.getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

      if (numRows > 0) {
        updateCount++;
      }
    } catch (Throwable t) {
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + ","
              + obj.getClass().getName();
      // TODO: FIX THIS
//      localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }
      if (jpsf != null) {
        jpsf.release();
      }

    }

    return updateCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @param arr DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @param con DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processBatchUpdateGroup(T[] arr, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, Connection con)
          throws CpoException {
    CpoClass jmc;
    List<CpoFunction> queryGroup;
    PreparedStatement ps = null;
    CpoFunction jq = null;
    JdbcPreparedStatementFactory jpsf = null;
    long updateCount = 0;
    int[] updates;
    Logger localLogger = logger;

    try {
      jmc = metaAdapter.getMetaClass(arr[0]);
      queryGroup = jmc.getFunctionGroup(getGroupType(arr[0], groupType, groupName, con), groupName).getFunctions();
      localLogger = LoggerFactory.getLogger(jmc.getMetaClass().getName());


      int numRows = 0;

      // Only Batch if there is only one query
      if (queryGroup.size() == 1) {
        localLogger.info("=================== BATCH - Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
        jq = queryGroup.get(0);
        jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, arr[0], wheres, orderBy, nativeQueries);
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
            jpsf = new JdbcPreparedStatementFactory(con, this, jmc, jq, arr[j], wheres, orderBy, nativeQueries);
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
    } catch (Throwable t) {
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + ","
              + arr[0].getClass().getName();
// TODO: FIX This
//      localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      if (ps != null) {
        try {
          ps.close();
        } catch (Exception e) {
        }
      }
      if (jpsf != null) {
        jpsf.release();
      }

    }

    return updateCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @param coll DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries)
          throws CpoException {
    Connection c = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();

      updateCount = processUpdateGroup(coll, groupType, groupName, wheres, orderBy, nativeQueries, c);
      commitConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      try {
        rollbackConnection(c);
      } catch (Exception re) {
      }

      if (e instanceof CpoException) {
        throw (CpoException) e;
      } else {
        throw new CpoException("processUpdateGroup(Collection coll, String groupType, String groupName) failed",
                e);
      }
    } finally {
      closeConnection(c);
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
   * @param meta DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName,
          Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, Connection con) throws CpoException {
    long updateCount = 0;

    if (!coll.isEmpty()) {
      Object[] arr = coll.toArray();

      Object obj1 = arr[0];
      boolean allEqual = true;
      for (int i = 1; i < arr.length; i++) {
        if (!obj1.getClass().getName().equals(arr[i].getClass().getName())) {
          allEqual = false;
          break;
        }
      }

      if (allEqual && batchUpdatesSupported_ && !JdbcCpoAdapter.PERSIST_GROUP.equals(groupType)) {
        updateCount = processBatchUpdateGroup(arr, groupType, groupName, wheres, orderBy, nativeQueries, con);
      } else {
        for (int i = 0; i < arr.length; i++) {
          updateCount += processUpdateGroup(arr[i], groupType, groupName, wheres, orderBy, nativeQueries, con);
        }
      }
    }

    return updateCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @param name DOCUMENT ME!
   * @param c DOCUMENT ME!
   * @param jmc DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
// TODO: Need to do this from xml now
//  private <T> void loadAttributeMap(String name, Connection c, CpoClass<T> jmc)
//      throws CpoException {
//    String select = "select cam.column_name, cam.attribute, cc.class_id, cam.column_type, cam.db_table, cam.db_column, cam.transform_class from ";
//    String table1 = "cpo_attribute_map cam, ";
//    String table2 = "cpo_class cc where cc.name = ? and cam.class_id = cc.class_id";
//    String sql = select + getDbTablePrefix() + table1 + getDbTablePrefix() + table2;
//    PreparedStatement ps = null;
//    ResultSet rs = null;
//    Map<String, CpoAttribute> aMap;
//    Map<String, CpoAttribute> cMap;
//    String classId;
//    String dbType;
//
//    logger.debug("loadAttribute Sql <" + sql + ">");
//
//    //CpoArgument jp=null;
//    JdbcAttribute attribute;
//    boolean failed = false;
//    StringBuilder failedMessage = new StringBuilder();
//
//    if ((c != null) && (jmc != null)) {
//      Logger localLogger = LoggerFactory.getLogger(jmc.getMetaClass().getName());
//      try {
//        ps = c.prepareStatement(sql);
//        ps.setString(1, name);
//        rs = ps.executeQuery();
//        aMap = jmc.getAttributeMap();
//        cMap = jmc.getColumnMap();
//
//        if (rs.next()) {
//          classId = rs.getString(3);
//          jmc.setClassId(classId);
//
//          do {
//            try {
//              dbType = rs.getString(4);
//              attribute = new JdbcAttribute(jmc, rs.getString(2), dbType, rs.getString(1),
//                  rs.getString(5), rs.getString(6), rs.getString(7));
//              aMap.put(rs.getString(1), attribute);
//              cMap.put(attribute.getName(), attribute);
//            } catch (CpoException ce) {
//              failed = true;
//              String msg = ExceptionHelper.getLocalizedMessage(ce);
//              failedMessage.append(msg);
//            }
//          } while (rs.next());
//
//          if (failed == true) {
//            throw new CpoException("Error processing Attributes for:" + name + failedMessage.toString());
//          }
//        } else {
//          throw new CpoException("No Attributes found for class:" + name);
//        }
//      } catch (CpoException ce) {
//        String msg = "loadAttributeMap() failed:'" + sql + "' classname:" + name;
//        throw ce;
//      } catch (Exception e) {
//        String msg = "loadAttributeMap() failed:'" + sql + "' classname:" + name;
//        localLogger.error(msg, e);
//        throw new CpoException(msg, e);
//      } finally {
//        if (rs != null) {
//          try {
//            rs.close();
//          } catch (Exception e) {
//          }
//        }
//
//        if (ps != null) {
//          try {
//            ps.close();
//          } catch (Exception e) {
//          }
//        }
//      }
//    }
//  }
  /**
   * DOCUMENT ME!
   *
   * @param c DOCUMENT ME!
   * @param jmc DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
// TODO: need to load from xml now
//  private <T> void loadQueryGroups(Connection c, CpoClass<T> jmc)
//      throws CpoException {
//    String id = null;
//    StringBuilder sqlBuffer = new StringBuilder();
//    //Uncomment when we remove the query_text_table
//    //sqlBuffer.append("select cqg.group_type,cqg.name,cq.query_id,cq.seq_no as query_seq,cq.sql_text,cqp.seq_no as param_seq,");
//    // Comment this out when we remove the query_text table
//    sqlBuffer.append("select cqg.group_type,cqg.name,cq.query_id,cq.seq_no as query_seq,cqt.sql_text,cqp.seq_no as param_seq,");
//    sqlBuffer.append("cam.attribute,cam.column_name,cam.column_type,cqp.param_type from ");
//    sqlBuffer.append(getDbTablePrefix());
//    sqlBuffer.append("cpo_query_group cqg left outer join ");
//    sqlBuffer.append(getDbTablePrefix());
//    sqlBuffer.append("cpo_query cq on cqg.group_id = cq.group_id left outer join ");
//    // Comment this out when we remove the query_text table
//    sqlBuffer.append(getDbTablePrefix());
//    sqlBuffer.append("cpo_query_text cqt on cq.text_id = cqt.text_id left outer join ");
//    // End query_text table
//    sqlBuffer.append(getDbTablePrefix());
//    sqlBuffer.append("cpo_query_parameter cqp on cq.query_id = cqp.query_id left outer join ");
//    sqlBuffer.append(getDbTablePrefix());
//    sqlBuffer.append("cpo_attribute_map cam on cqp.attribute_id = cam.attribute_id where cqg.class_id=? ");
//    sqlBuffer.append("order by cqg.group_id, cq.seq_no, cqp.seq_no");
//    
//    String sql = sqlBuffer.toString();
//    logger.debug("loadQueryGroup Sql <" + sql + ">");
//
//    PreparedStatement ps = null;
//    ResultSet rs = null;
//    int oldSeq = 1000;
//    int newSeq;
//    CpoFunction function = null;
//    String groupType = null;
//
//    if ((c != null) && (jmc != null)) {
//      Logger localLogger = LoggerFactory.getLogger(jmc.getMetaClass().getName());
//      try {
//        id = jmc.getClassId();
//        ps = c.prepareStatement(sql);
//        ps.setString(1, id);
//        rs = ps.executeQuery();
//
//        while (rs.next()) {
//          newSeq = rs.getInt(6);
//
//          if (newSeq <= oldSeq) {
//            function = new CpoFunction();
//            function.setFunctionId(rs.getString(3));
//            function.setText(rs.getString(5));
//            function.setName(rs.getString(2));
//            function.setType(rs.getString(1));
//
//            jmc.addQueryToGroup(function);
//            localLogger.debug("Added QueryGroup:" + jmc.getName() + ":" + function.getType() + ":" +
//                function.getName());
//          }
//
//          JdbcAttribute attribute = (JdbcAttribute) jmc.getAttributeMap().get(rs.getString(8));
//
//          if (attribute == null) {
//            // There may be queries with no params
//            newSeq = 1000;
//            localLogger.debug("No Parameters for " + groupType + ":" + function.getName());
//
//            //throw new CpoException("Cannot Add Null Parameter to
//            // Parameter List");
//          } else {
//            JdbcArgument argument = new JdbcArgument(attribute, rs.getString(10));
//            function.getArguments().add((CpoArgument)argument);
//            localLogger.debug("Added Parameter:" +
//                attribute.getName() //+ ":" + attribute.getDbName() + ":"
//                //+ attribute.getDbType() + ":"
//                + argument.getArgumentType());
//          }
//
//          oldSeq = newSeq;
//        }
//      } catch (SQLException e) {
//        String msg = "loadQueryGroups() falied:" + sql + ":" + id;
//        localLogger.error(msg, e);
//        throw new CpoException(msg, e);
//      } finally {
//        if (rs != null) {
//          try {
//            rs.close();
//          } catch (Exception e) {
//          }
//        }
//
//        if (ps != null) {
//          try {
//            ps.close();
//          } catch (Exception e) {
//          }
//        }
//      }
//    }
//  }
  /**
   * Provides a mechanism for the user to obtain a CpoTrxAdapter object. This object allows the to control when commits
   * and rollbacks occur on CPO.
   *
   *
   * <pre>Example:
   * <code>
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
   * </pre>
   *
   * @return A CpoTrxAdapter to manage the transactionality of CPO
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see CpoTrxAdapter
   */
  public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    return new JdbcCpoTrxAdapter(metaAdapter, getWriteConnection(), batchUpdatesSupported_);
  }

  private class RetrieverThread<T, C> extends Thread {

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
            Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeQuery> nativeQueries, boolean useRetrieve, CpoBlockingResultSet<T> resultSet) {
      this.name = name;
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
        e.printStackTrace();
      } finally {
        //wait until the calling thread is finished processing the records
        while (resultSet.size() > 0) {
          Thread.yield();
        }
        //Tell the calling thread that it should not wait on the blocking queue any longer.
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
