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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cache.CpoAdapterCache;
import org.synchronoss.cpo.helper.ExceptionHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 9/10/13
 * Time: 09:00 AM
 * To change this template use File | Settings | File Templates.
 */
public abstract class CpoBaseAdapter<T> extends CpoAdapterCache implements CpoAdapter {
    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(CpoBaseAdapter.class);
  // DataSource Information

  /**
   * The datasource where read queries are executed against
   */
  private T readDataSource = null;

  /**
   * The datasource where write queries are executed against
   */
  private T writeDataSource = null;

  /**
   * The name of the datasource
   */
  private String dataSourceName = null;

  protected T getReadDataSource() {
    return readDataSource;
  }

  protected void setReadDataSource(T readDataSource) {
    this.readDataSource = readDataSource;
  }

  protected T getWriteDataSource() {
    return writeDataSource;
  }

  protected void setWriteDataSource(T writeDataSource) {
    this.writeDataSource = writeDataSource;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  protected void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.insertObject(so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   * <p/>
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long insertObject(T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.CREATE_GROUP, null, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * @param name The String name of the CREATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used which is equivalent to insertObject(Object obj);
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long insertObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.CREATE_GROUP, name, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
  @Override
  public <T> long insertObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.CREATE_GROUP, name, wheres, orderBy, nativeExpressions);
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * 		cpo.insertObjects(al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long insertObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.CREATE_GROUP, null, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * @param name The String name of the CREATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long insertObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.CREATE_GROUP, name, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
  @Override
  public <T> long insertObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.CREATE_GROUP, name, wheres, orderBy, nativeExpressions);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.deleteObject(so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long deleteObject(T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.DELETE_GROUP, null, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * @param name The String name of the DELETE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown. If the object does not exist in the datasource an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long deleteObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.DELETE_GROUP, name, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
  @Override
  public <T> long deleteObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.DELETE_GROUP, name, wheres, orderBy, nativeExpressions);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * 		cpo.deleteObjects(al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long deleteObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.DELETE_GROUP, null, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * @param name The String name of the DELETE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long deleteObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.DELETE_GROUP, name, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
  @Override
  public <T> long deleteObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.DELETE_GROUP, name, wheres, orderBy, nativeExpressions);
  }

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
   * 		cpo.executeObject(so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param object This is an Object that has been defined within the metadata of the datasource. If the class is not
   *               defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *               This object is used to populate the IN arguments used to executed the datasource object.
   *               <p/>
   *               An object of this type will be created and filled with the returned data from the value_object. This newly created
   *               object will be returned from this method.
   * @return An object populated with the OUT arguments returned from the executable object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> T executeObject(T object) throws CpoException {
    return processExecuteGroup(null, object, object);
  }

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
   * 		cpo.executeObject("execNotifyProc",so);
   *  } catch (CpoException ce) {
   * 	// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name   The filter name which tells the datasource which objects should be returned. The name also signifies
   *               what data in the object will be populated.
   * @param object This is an object that has been defined within the metadata of the datasource. If the class is not
   *               defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *               This object is used to populate the IN arguments used to retrieve the collection of objects. This object defines
   *               the object type that will be returned in the collection and contain the result set data or the OUT Parameters.
   * @return A result object populate with the OUT arguments
   * @throws CpoException if there are errors accessing the datasource
   */
  @Override
  public <T> T executeObject(String name, T object) throws CpoException {
    return processExecuteGroup(name, object, object);
  }

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
   * 		sr = (SomeResult)cpo.executeObject("execNotifyProc",so, sr);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param name     The String name of the EXECUTE Function Group that will be used to create the object in the datasource.
   *                 null signifies that the default rules will be used.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *                 This object is used to populate the IN arguments used to retrieve the collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *                 This object defines the object type that will be created, filled with the return data and returned from this
   *                 method.
   * @return An object populated with the out arguments
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T, C> T executeObject(String name, C criteria, T result) throws CpoException {
    return processExecuteGroup(name, criteria, result);
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		count = cpo.existsObject(so);
   * 		if (count>0) {
   * 			// object exists
   *    } else {
   * 			// object does not exist
   *    }
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown. This object will be searched for inside the datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long existsObject(T obj) throws CpoException {
    return this.existsObject(null, obj);
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		count = cpo.existsObject("SomeExistCheck",so);
   * 		if (count>0) {
   * 			// object exists
   *    } else {
   * 			// object does not exist
   *    }
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
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
  @Override
  public <T> long existsObject(String name, T obj) throws CpoException {
    return this.existsObject(name, obj, null);
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
  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException {
    return new BindableCpoOrderBy(attribute, ascending);
  }

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
  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending) throws CpoException {
    return new BindableCpoOrderBy(marker, attribute, ascending);
  }

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
  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException {
    return new BindableCpoOrderBy(attribute, ascending, function);
  }

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
  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function) throws CpoException {
    return new BindableCpoOrderBy(marker, attribute, ascending, function);
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
  public CpoWhere newWhere() throws CpoException {
    return new BindableCpoWhere();
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
  @Override
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value) throws CpoException {
    return new BindableCpoWhere(logical, attr, comp, value);
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
  @Override
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not) throws CpoException {
    return new BindableCpoWhere(logical, attr, comp, value, not);
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.persistObject(so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
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
  @Override
  public <T> long persistObject(T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.PERSIST_GROUP, null, null, null, null);
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.persistObject("persistSomeObject",so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
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
  @Override
  public <T> long persistObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.PERSIST_GROUP, name, null, null, null);
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * 		cpo.persistObjects(al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
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
  @Override
  public <T> long persistObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.PERSIST_GROUP, null, null, null, null);
  }

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
   * try {
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * 		cpo.persistObjects("myPersist",al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
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
  @Override
  public <T> long persistObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.PERSIST_GROUP, name, null, null, null);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param bean This is an bean that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The
   *             input bean is used to specify the search criteria, the output bean is populated with the results of the function.
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> T retrieveBean(T bean) throws CpoException {
    return processSelectGroup(bean, null, null, null, null);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name DOCUMENT ME!
   * @param bean This is an bean that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown. The
   *             input bean is used to specify the search criteria, the output bean is populated with the results of the function.
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> T retrieveBean(String name, T bean) throws CpoException {
    return processSelectGroup(bean, name, null, null, null);
  }

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
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> T retrieveBean(String name, T bean, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processSelectGroup(bean, name, wheres, orderBy, nativeExpressions);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return retrieveBean(name, criteria, result, wheres, orderBy, null);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource. If the retrieve
   * function defined for this beans returns more than one row, an exception will be thrown.
   *
   * @param name              The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                          data in the bean will be populated.
   * @param criteria          This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result            This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @return An bean of the same type as the result argument that is filled in as specified the metadata for the
   *         retireve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T, C> T retrieveBean(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    Iterator<T> it = processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, true).iterator();
    if (it.hasNext()) {
      return it.next();
    } else {
      return null;
    }
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <C> List<C> retrieveBeans(String name, C criteria) throws CpoException {
    return processSelectGroup(name, criteria, criteria, null, null, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param where    A CpoWhere bean that defines the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<>();
      wheres.add(where);
    }
    return processSelectGroup(name, criteria, criteria, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, criteria, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <C> List<C> retrieveBeans(String name, C criteria, Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, criteria, null, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result) throws CpoException {
    return processSelectGroup(name, criteria, result, null, null, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @param where    A CpoWhere bean that defines the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, CpoWhere where, Collection<CpoOrderBy> orderBy) throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<>();
      wheres.add(where);
    }
    return processSelectGroup(name, criteria, result, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name     The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                 data in the bean will be populated.
   * @param criteria This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result   This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                 This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres   A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy  The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The beans will be of the
   *         same type as the bean that was passed in. If no beans match the criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, null, false);
  }

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the datasource.
   *
   * @param name              The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                          data in the bean will be populated.
   * @param criteria          This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the arguments used to retrieve the collection of beans.
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
  @Override
  public <T, C> List<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, false);
  }

  /**
   * Retrieves the bean from the datasource. This method returns an Iterator immediately. The iterator will get filled
   * asynchronously by the cpo framework. The framework will stop supplying the iterator with beans if the
   * beanBufferSize is reached.
   * <p/>
   * If the consumer of the iterator is processing records faster than the framework is filling it, then the iterator
   * will wait until it has data to provide.
   *
   * @param name              The filter name which tells the datasource which beans should be returned. The name also signifies what
   *                          data in the bean will be populated.
   * @param criteria          This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the arguments used to retrieve the collection of beans.
   * @param result            This is an bean that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. If the bean does not exist in the datasource, an exception will be thrown.
   *                          This bean is used to specify the bean type that will be returned in the collection.
   * @param wheres            A collection of CpoWhere beans that define the constraints that should be used when retrieving beans
   * @param orderBy           The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored in the meta data. This
   *                          text will be embedded at run-time
   * @param queueSize         the maximum number of beans that the Iterator is allowed to cache. Once reached, the CPO
   *                          framework will halt processing records from the datasource.
   * @return An iterator that will be fed beans from the CPO framework.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T, C> CpoResultSet<T> retrieveBeans(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, int queueSize) {
    CpoBlockingResultSet<T> resultSet = new CpoBlockingResultSet<>(queueSize);
    RetrieverThread<T, C> retrieverThread = new RetrieverThread<T,C>(name, criteria, result, wheres, orderBy, nativeExpressions, false, resultSet);

    retrieverThread.start();
    return resultSet;
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * <p/>
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.updateObject(so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param obj This is an object that has been defined within the metadata of the datasource. If the class is not
   *            defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long updateObject(T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.UPDATE_GROUP, null, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * @param name The String name of the UPDATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long updateObject(String name, T obj) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.UPDATE_GROUP, name, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
  @Override
  public <T> long updateObject(String name, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(obj, CpoAdapter.UPDATE_GROUP, name, wheres, orderBy, nativeExpressions);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * 		cpo.updateObjects(al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * </code>
   * </pre>
   *
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long updateObjects(Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.UPDATE_GROUP, null, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
   * @param name The String name of the UPDATE Function Group that will be used to create the object in the datasource.
   *             null signifies that the default rules will be used.
   * @param coll This is a collection of objects that have been defined within the metadata of the datasource. If the
   *             class is not defined an exception will be thrown.
   * @return The number of objects updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long updateObjects(String name, Collection<T> coll) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.UPDATE_GROUP, name, null, null, null);
  }

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
   * 	cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
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
  @Override
  public <T> long updateObjects(String name, Collection<T> coll, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    return processUpdateGroup(coll, CpoAdapter.UPDATE_GROUP, name, wheres, orderBy, nativeExpressions);
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
  protected abstract <T> long processUpdateGroup(T obj, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * DOCUMENT ME!
   *
   * @param coll      DOCUMENT ME!
   * @param groupType DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected abstract <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  /**
   * Executes an Object whose MetaData contains a stored procedure. An assumption is that the object exists in the
   * datasource.
   *
   * @param name     The filter name which tells the datasource which objects should be returned. The name also signifies
   *                 what data in the object will be populated.
   * @param criteria This is an object that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *                 This object is used to populate the IN arguments used to retrieve the collection of objects.
   * @param result   This is an object that has been defined within the metadata of the datasource. If the class is not
   *                 defined an exception will be thrown. If the object does not exist in the datasource, an exception will be thrown.
   *                 This object defines the object type that will be returned in the
   * @return A result object populate with the OUT arguments
   * @throws CpoException DOCUMENT ME!
   */
  protected abstract <T, C> T processExecuteGroup(String name, C criteria, T result) throws CpoException;

  public abstract <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException;

  protected abstract <T> T processSelectGroup(T obj, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException;

  protected abstract <T, C> List<T> processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions,
                                              boolean useRetrieve) throws CpoException;

  protected abstract <T, C> void processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions,
                                             boolean useRetrieve, CpoResultSet<T> resultSet) throws CpoException;

  protected class RetrieverThread<T, C> extends Thread {

    String name;
    C criteria;
    T result;
    Collection<CpoWhere> wheres;
    Collection<CpoOrderBy> orderBy;
    Collection<CpoNativeFunction> nativeExpressions;
    boolean useRetrieve;
    CpoBlockingResultSet<T> resultSet;
    Thread callingThread = null;

    public RetrieverThread(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, boolean useRetrieve, CpoBlockingResultSet<T> resultSet) {
      this.name = name;
      this.criteria = criteria;
      this.result = result;
      this.wheres = wheres;
      this.orderBy = orderBy;
      this.useRetrieve = useRetrieve;
      this.resultSet = resultSet;
      this.nativeExpressions = nativeExpressions;
      callingThread = Thread.currentThread();
    }

    @Override
    public void run() {
      try {
        processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, false, resultSet);
      } catch (CpoException e) {
        logger.error(ExceptionHelper.getLocalizedMessage(e));
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
}
