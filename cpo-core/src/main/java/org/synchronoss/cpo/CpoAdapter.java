/*
 * Copyright (C) 2003-2025 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo;

import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

import java.util.*;

/**
 * CpoAdapter is an interface for a set of routines that are responsible for Creating, Retrieving, Updating, and
 * Deleting (CRUD) value objects within a datasource.
 * <p/>
 * CpoAdapter is an interface that acts as a common facade for different datasources. It is conceivable that an
 * CpoAdapter can be implemented for JDBC, CSV, XML, LDAP, and more datasources producing classes such as
 * JdbcCpoAdapter, CsvCpoAdapter, XmlCpoAdapter, LdapCpoAdapter, etc.
 *
 * @author david berry
 */
public interface CpoAdapter extends java.io.Serializable {

  /**
   * Identifies the operation to be processed by the CPO. CREATE signifies that the CPO will try to add the object to the datasource.
   */
  static final int CREATE = 0;

  /**
   * Identifies the operation to be processed by CPO. INSERT signifies that the CPO will try to add the object to the datasource.
   */
  static final int INSERT = 0;

  /**
   * Identifies the operation to be processed by CPO. UPDATE signifies that the CPO will try to update the object in the datasource.
   */
  static final int UPDATE = 1;

  /**
   * Identifies the operation to be processed by CPO. DELETE signifies that the CPO will try to delete the object in the datasource.
   */
  static final int DELETE = 2;

  /**
   * Identifies the operation to be processed by CPO. RETRIEVE signifies that the CPO will try to retrieve a single object from the datasource.
   */
  static final int RETRIEVE = 3;

  /**
   * Identifies the operation to be processed by CPO. LIST signifies that the CPO will try to retrieve one or more objects from the datasource.
   */
  static final int LIST = 4;

  /**
   * Identifies the operation to be processed by CPO. PERSIST signifies that the CPO will try to add or update the object in the datasource.
   */
  static final int PERSIST = 5;

  /**
   * Identifies the operation to be processed by CPO. EXIST signifies that the CPO will check to see if the object exists in the datasource.
   */
  static final int EXIST = 6;

  /**
   * Identifies the operation to be processed by the CPO. EXECUTE signifies that the CPO will try to execute a function or procedure in the datasource.
   */
  static final int EXECUTE = 7;

  /**
   * Identifies the string constants that goes along with the numeric constants defined above
   */
  static final String[] GROUP_IDS = {
    "CREATE", "UPDATE", "DELETE", "RETRIEVE", "LIST", "PERSIST", "EXIST", "EXECUTE"
  };

  /**
   * String constant for CREATE
   */
  static final String CREATE_GROUP = GROUP_IDS[CpoAdapter.CREATE];

  /**
   * String constant for UPDATE
   */
  static final String UPDATE_GROUP = GROUP_IDS[CpoAdapter.UPDATE];

  /**
   * String constant for DELETE
   */
  static final String DELETE_GROUP = GROUP_IDS[CpoAdapter.DELETE];

  /**
   * String constant for RETRIEVE
   */
  static final String RETRIEVE_GROUP = GROUP_IDS[CpoAdapter.RETRIEVE];

  /**
   * String constant for LIST
   */
  static final String LIST_GROUP = GROUP_IDS[CpoAdapter.LIST];

  /**
   * String constant for PERSIST
   */
  static final String PERSIST_GROUP = GROUP_IDS[CpoAdapter.PERSIST];

  /**
   * String constant for EXIST
   */
  static final String EXIST_GROUP = GROUP_IDS[CpoAdapter.EXIST];

  /**
   * String constant for EXECUTE
   */
  static final String EXECUTE_GROUP = GROUP_IDS[CpoAdapter.EXECUTE];

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in the datasource. This
   * method creates and stores the object in the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.insertObject(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(T obj) throws CpoException;

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in the datasource. This
   * method creates and stores the object in the datasource
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.insertObject("IDNameInsert",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name The String name of the CREATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used which is equivalent to insertObject(Object obj);
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(String name, T obj) throws CpoException;

  /**
   * Creates the Object in the datasource. The assumption is that the object does not exist in the datasource. This
   * method creates and stores the object in the datasource
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.insertObject("IDNameInsert",so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name              The String name of the CREATE Function Group that will be used to create the object in the datasource.
   *                          null signifies that the default rules will be used which is equivalent to insertObject(Object obj);
   * @param obj               This is an object that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Iterates through a collection of Objects, creates and stores them in the datasource. The assumption is that the
   * objects contained in the collection do not exist in the datasource.
   * <p/>
   * This method creates and stores the objects in the datasource. The objects in the collection will be treated as one
   * transaction, assuming the datasource supports transactions.
   * <p/>
   * This means that if one of the objects fail being created in the datasource then the CpoAdapter will stop processing
   * the remainder of the collection and rollback all the objects created thus far. Rollback is on the underlying
   * datasource's support of rollback.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   * <p/>
   *    try{
   *      cpo.insertObjects(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(Collection<T> coll) throws CpoException;

  /**
   * Iterates through a collection of Objects, creates and stores them in the datasource. The assumption is that the
   * objects contained in the collection do not exist in the datasource.
   * <p/>
   * This method creates and stores the objects in the datasource. The objects in the collection will be treated as one
   * transaction, assuming the datasource supports transactions.
   * <p/>
   * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.insertObjects("IdNameInsert",al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name The String name of the CREATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(String name, Collection<T> coll) throws CpoException;

  /**
   * Iterates through a collection of Objects, creates and stores them in the datasource. The assumption is that the
   * objects contained in the collection do not exist in the datasource.
   * <p/>
   * This method creates and stores the objects in the datasource. The objects in the collection will be treated as one
   * transaction, assuming the datasource supports transactions.
   * <p/>
   * This means that if one of the objects fail being created in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects created thus far.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
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
   *  }
   * 	try{
   * 		cpo.insertObjects("IdNameInsert",al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name              The String name of the CREATE Function Group that will be used to create the object in the datasource.
   *                          null signifies that the default rules will be used.
   * @param coll              This is a collection of objects that have been defined within the metadata of the datasource. If the
   *                          class is not defined an exception will be thrown.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long insertObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the datasource. This method
   * stores the object in the datasource
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.deleteObject(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(T obj) throws CpoException;

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the datasource. This method
   * stores the object in the datasource
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.deleteObject("DeleteById",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name The String name of the DELETE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(String name, T obj) throws CpoException;

  /**
   * Removes the Object from the datasource. The assumption is that the object exists in the datasource. This method
   * stores the object in the datasource
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.deleteObject("DeleteById",so);
   *  } catch (CpoException ce) {
   * 	// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name              The String name of the DELETE Function Group that will be used to create the object in the datasource.
   *                          null signifies that the default rules will be used.
   * @param obj               This is an object that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Removes the Objects contained in the collection from the datasource. The assumption is that the object exists in
   * the datasource. This method stores the objects contained in the collection in the datasource. The objects in the
   * collection will be treated as one transaction, assuming the datasource supports transactions.
   * <p/>
   * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   * <p/>
   *    try{
   *      cpo.deleteObjects(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   * <p/>
   *  }
   * </code>
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(Collection<T> coll) throws CpoException;

  /**
   * Removes the Objects contained in the collection from the datasource. The assumption is that the object exists in
   * the datasource. This method stores the objects contained in the collection in the datasource. The objects in the
   * collection will be treated as one transaction, assuming the datasource supports transactions.
   * <p/>
   * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   * <p/>
   *    try{
   *        cpo.deleteObjects("IdNameDelete",al);
   *    } catch (CpoException ce) {
   *        // Handle the error
   *    }
   * <p/>
   *  }
   * </code>
   * </pre>
   *
   * @param name The String name of the DELETE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(String name, Collection<T> coll) throws CpoException;

  /**
   * Removes the Objects contained in the collection from the datasource. The assumption is that the object exists in
   * the datasource. This method stores the objects contained in the collection in the datasource. The objects in the
   * collection will be treated as one transaction, assuming the datasource supports transactions.
   * <p/>
   * This means that if one of the objects fail being deleted in the datasource then the CpoAdapter should stop
   * processing the remainder of the collection, and if supported, rollback all the objects deleted thus far.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   *  }
   * <p/>
   * 	try{
   * 		cpo.deleteObjects("IdNameDelete",al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name              The String name of the DELETE Function Group that will be used to create the object in the datasource.
   *                          null signifies that the default rules will be used.
   * @param coll              This is a collection of objects that have been defined within the metadata of the datasource. If the
   *                          class is not defined an exception will be thrown.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long deleteObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Executes an Object whose metadata will call an executable within the datasource. It is assumed that the executable
   * object exists in the metadatasource. If the executable does not exist, an exception will be thrown.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.executeObject(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param obj This is an Object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *            This object is used to populate the IN parameters used to executed the datasource object.
   *            <p/>
   *            An object of this type will be created and filled with the returned data from the value_object. This newly created
   *            object will be returned from this method.
   * @return An object populated with the OUT parameters returned from the executable object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T executeObject(T obj) throws CpoException;

  /**
   * Executes an Object whose metadata will call an executable within the datasource. It is assumed that the executable
   * object exists in the metadatasource. If the executable does not exist, an exception will be thrown.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.executeObject("execNotifyProc",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name   The filter name which tells the datasource which objects should be returned. The name also signifies
   *               what data in the object will be populated.
   * @param object This is an object that has been defined within the metadata of the datasource. If the class is not
   *               defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *               This object is used to populate the IN parameters used to retrieve the collection of objects. This object defines
   *               the object type that will be returned in the collection and contain the result set data or the OUT Parameters.
   * @return A result object populate with the OUT parameters
   * @throws CpoException DOCUMENT ME!
   */
  public <T> T executeObject(String name, T object) throws CpoException;

  /**
   * Executes an Object that represents an executable object within the datasource. It is assumed that the object exists
   * in the datasource. If the object does not exist, an exception will be thrown
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class SomeResult sr = new SomeResult();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      sr = (SomeResult)cpo.executeObject("execNotifyProc",so, sr);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name     The String name of the EXECUTE Function Group that will be used to create the object in the datasource.
   *                 null signifies that the default rules will be used.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *                 This object is used to populate the IN parameters used to retrieve the collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *                 This object defines the object type that will be created, filled with the return data and returned from this
   *                 method.
   * @return An object populated with the out parameters
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T executeObject(String name, C criteria, T result) throws CpoException;

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * long count = 0;
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      count = cpo.existsObject(so);
   *      if (count>0) {
   *             // object exists
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
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown. This object will be searched for inside the datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(T obj) throws CpoException;

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * long count = 0;
   * class CpoAdapter cpo = null;
   * <p/>
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      count = cpo.existsObject("SomeExistCheck",so);
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
   * @param name The String name of the EXISTS Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown. This object will be searched for inside the datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(String name, T obj) throws CpoException;

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * long count = 0;
   * class CpoAdapter cpo = null;
   * <p/>
   * <p/>
   *  try {
   * <p/>
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
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
   * @param name   The String name of the EXISTS Function Group that will be used to create the object in the datasource.
   *               null signifies that the default rules will be used.
   * @param obj    This is an object that has been defined within the metadata of the datasource. If the class is not
   *               defined an exception will be thrown. This object will be searched for inside the datasource.
   * @param wheres A collection of CpoWhere objects that pass in run-time constraints to the function that performs the the
   *               exist
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the objects in the resulting collection. This allows you
   * to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @return A CpoOrderBy object to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the objects in the resulting collection. This allows you
   * to apply user input in determining the order of the collection
   *
   * @param marker    the marker that will be replaced in the expression with the string representation of this orderBy
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @return A CpoOrderBy object to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the objects in the resulting collection. This allows you
   * to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @param function  A string which represents a datasource function that will be called on the attribute. must be
   *                  contained in the function string. The attribute name will be replaced at run-time with its datasource counterpart
   * @return A CpoOrderBy object to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the objects in the resulting collection. This allows you
   * to apply user input in determining the order of the collection
   *
   * @param marker    the marker that will be replaced in the expression with the string representation of this orderBy
   * @param attribute The name of the attribute from the pojo that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @param function  A string which represents a datasource function that will be called on the attribute. must be
   *                  contained in the function string. The attribute name will be replaced at run-time with its datasource counterpart
   * @return A CpoOrderBy object to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function) throws CpoException;

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public CpoWhere newWhere() throws CpoException;

  /**
   * DOCUMENT ME!
   *
   * @param logical DOCUMENT ME!
   * @param attr    DOCUMENT ME!
   * @param comp    DOCUMENT ME!
   * @param value   DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value) throws CpoException;

  /**
   * DOCUMENT ME!
   *
   * @param logical DOCUMENT ME!
   * @param attr    DOCUMENT ME!
   * @param comp    DOCUMENT ME!
   * @param value   DOCUMENT ME!
   * @param not     DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not) throws CpoException;

  /**
   * Persists the Object into the datasource. The CpoAdapter will check to see if this object exists in the datasource.
   * If it exists, the object is updated in the datasource If the object does not exist, then it is created in the
   * datasource. This method stores the object in the datasource. This method uses the default EXISTS, CREATE, and
   * UPDATE Function Groups specified for this object.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.persistObject(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown.
   * @return A count of the number of objects persisted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObject(T obj) throws CpoException;

  /**
   * Persists the Object into the datasource. The CpoAdapter will check to see if this object exists in the datasource.
   * If it exists, the object is updated in the datasource If the object does not exist, then it is created in the
   * datasource. This method stores the object in the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.persistObject("persistSomeObject",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Function Groups to execute to persist the
   *             object.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown.
   * @return A count of the number of objects persisted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObject(String name, T obj) throws CpoException;

  /**
   * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if this object exists in the
   * datasource. If it exists, the object is updated in the datasource If the object does not exist, then it is created
   * in the datasource. This method stores the object in the datasource. The objects in the collection will be treated
   * as one transaction, meaning that if one of the objects fail being inserted or updated in the datasource then the
   * entire collection will be rolled back.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.persistObjects(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObjects(Collection<T> coll) throws CpoException;

  /**
   * Persists a collection of Objects into the datasource. The CpoAdapter will check to see if this object exists in the
   * datasource. If it exists, the object is updated in the datasource If the object does not exist, then it is created
   * in the datasource. This method stores the object in the datasource. The objects in the collection will be treated
   * as one transaction, meaning that if one of the objects fail being inserted or updated in the datasource then the
   * entire collection will be rolled back.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   * <p/>
   *    try{
   *      cpo.persistObjects("myPersist",al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Function Groups to execute to persist the
   *             object.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsObject
   * @see #insertObject
   * @see #updateObject
   */
  public <T> long persistObjects(String name, Collection<T> coll) throws CpoException;

  /**
   * Retrieves the Bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for these beans returns more than one row, an exception will be thrown.
   *
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the class is not defined
   *             an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The input
   *             bean is used to specify the search criteria, the output bean is populated with the results of the function.
   * @return A bean of the same type as the result parameter that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveBean(T bean) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name DOCUMENT ME!
   * @param bean This is an bean that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The
   *             input bean is used to specify the search criteria, the output bean is populated with the results of the function.
   * @return An bean of the same type as the result parameter that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveBean(String name, T bean) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name              DOCUMENT ME!
   * @param bean              This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The
   *                          input bean is used to specify the search criteria, the output bean is populated with the results of the function.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return An bean of the same type as the result parameter that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> T retrieveBean(String name, T bean, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return An bean of the same type as the result parameter that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name              The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                          data in the bean will be populated.
   * @param criteria          This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param result            This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return An bean of the same type as the result parameter that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param where    A CpoWhere bean that defines the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoOrderBy> orderBy) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @param where    A CpoWhere bean that defines the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name              The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                          data in the bean will be populated.
   * @param criteria          This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param result            This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name              The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                          data in the bean will be populated.
   * @param criteria          This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the parameters used to retrieve the collection of beans.
   * @param result            This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @param queueSize         queue size of the buffer that it uses to send the beans from the producer to the consumer.
   * @return A CpoResultSet that can be iterated through
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T, C> CpoResultSet<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, int queueSize) throws CpoException;

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object exists in the datasource. If it
   * exists then the object will be updated. If it does not exist, an exception will be thrown
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.updateObject(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(T obj) throws CpoException;

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object exists in the datasource. If it
   * exists then the object will be updated. If it does not exist, an exception will be thrown
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      cpo.updateObject("updateSomeObject",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param name The String name of the UPDATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(String name, T obj) throws CpoException;

  /**
   * Update the Object in the datasource. The CpoAdapter will check to see if the object exists in the datasource. If it
   * exists then the object will be updated. If it does not exist, an exception will be thrown
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = new SomeObject();
   * class CpoAdapter cpo = null;
   * <p/>
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.updateObject("updateSomeObject",so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name              The String name of the UPDATE Function Group that will be used to create the object in the datasource.
   *                          null signifies that the default rules will be used.
   * @param obj               This is an object that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown.
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects contained in the collection
   * exist in the datasource. This method stores the object in the datasource. The objects in the collection will be
   * treated as one transaction, meaning that if one of the objects fail being updated in the datasource then the entire
   * collection will be rolled back, if supported by the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   * <p/>
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   * <p/>
   *    try{
   *      cpo.updateObjects(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * </code>
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(Collection<T> coll) throws CpoException;

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects contained in the collection
   * exist in the datasource. This method stores the object in the datasource. The objects in the collection will be
   * treated as one transaction, meaning that if one of the objects fail being updated in the datasource then the entire
   * collection will be rolled back, if supported by the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   * <p/>
   *  try {
   * <p/>
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * <p/>
   *  } catch (CpoException ce) {
   * <p/>
   *    // Handle the error
   *    cpo = null;
   * <p/>
   *  }
   * <p/>
   *  if (cpo!=null) {
   * <p/>
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   * <p/>
   *      so = new SomeObject();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   * <p/>
   *      try{
   * <p/>
   *        cpo.updateObjects("myUpdate",al);
   * <p/>
   *      } catch (CpoException ce) {
   * <p/>
   *        // Handle the error
   * <p/>
   *      }
   * <p/>
   *  }
   * </code>
   * </pre>
   *
   * @param name The String name of the UPDATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(String name, Collection<T> coll) throws CpoException;

  /**
   * Updates a collection of Objects in the datasource. The assumption is that the objects contained in the collection
   * exist in the datasource. This method stores the object in the datasource. The objects in the collection will be
   * treated as one transaction, meaning that if one of the objects fail being updated in the datasource then the entire
   * collection will be rolled back, if supported by the datasource.
   * <p/>
   * <pre>Example:
   * <code>
   * <p/>
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * <p/>
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeObject();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   *  }
   * 	try{
   * 		cpo.updateObjects("myUpdate",al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name              The String name of the UPDATE Function Group that will be used to create the object in the datasource.
   *                          null signifies that the default rules will be used.
   * @param coll              This is a collection of objects that have been defined within the metadata of the datasource. If the
   *                          class is not defined an exception will be thrown.
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public <T> long updateObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  public CpoMetaDescriptor getCpoMetaDescriptor();

  public String getDataSourceName();

  public List<CpoAttribute> getCpoAttributes(String expression) throws CpoException;
}
