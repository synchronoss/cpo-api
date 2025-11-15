package org.synchronoss.cpo;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * --------------------------------------------------------------------------]
 */

import java.util.Collection;
import java.util.List;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * CpoAdapter is an interface for a set of routines that are responsible for Creating, Retrieving,
 * Updating, and Deleting (CRUD) value beans within a datasource.
 *
 * <p>CpoAdapter is an interface that acts as a common facade for different datasources. It is
 * conceivable that an CpoAdapter can be implemented for JDBC, CSV, XML, LDAP, and more datasources
 * producing classes such as JdbcCpoAdapter, CsvCpoAdapter, XmlCpoAdapter, LdapCpoAdapter, etc.
 *
 * @author david berry
 */
public interface CpoAdapter extends java.io.Serializable {

  /**
   * Creates the bean in the datasource. The assumption is that the bean does not exist in the
   * datasource. This method creates and stores the bean in the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.insertBean(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBean(T bean) throws CpoException;

  /**
   * Creates the bean in the datasource. The assumption is that the bean does not exist in the
   * datasource. This method creates and stores the bean in the datasource
   *
   * <pre>Example:
   * {@code
   *
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
   *
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
   *      cpo.insertBean("IDNameInsert",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   *
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the CREATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used which is
   *     equivalent to insertBean(Bea bean);
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBean(String groupName, T bean) throws CpoException;

  /**
   * Creates the bean in the datasource. The assumption is that the bean does not exist in the
   * datasource. This method creates and stores the bean in the datasource
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
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
   * 		cpo.insertBean("IDNameInsert",so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the CREATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used which is
   *     equivalent to insertBean(Bea bean);
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Iterates through a collection of beans, creates and stores them in the datasource. The
   * assumption is that the beans contained in the collection do not exist in the datasource.
   *
   * <p>This method creates and stores the beans in the datasource. The beans in the collection will
   * be treated as one transaction, assuming the datasource supports transactions.
   *
   * <p>This means that if one of the beans fail being created in the datasource then the CpoAdapter
   * will stop processing the remainder of the collection and rollback all the beans created thus
   * far. Rollback is on the underlying datasource's support of rollback.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.insertBeans(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBeans(Collection<T> beans) throws CpoException;

  /**
   * Iterates through a collection of beans, creates and stores them in the datasource. The
   * assumption is that the beans contained in the collection do not exist in the datasource.
   *
   * <p>This method creates and stores the beans in the datasource. The beans in the collection will
   * be treated as one transaction, assuming the datasource supports transactions.
   *
   * <p>This means that if one of the beans fail being created in the datasource then the CpoAdapter
   * should stop processing the remainder of the collection, and if supported, rollback all the
   * beans created thus far.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.insertBeans("IdNameInsert",al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the CREATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBeans(String groupName, Collection<T> beans) throws CpoException;

  /**
   * Iterates through a collection of beans, creates and stores them in the datasource. The
   * assumption is that the beans contained in the collection do not exist in the datasource.
   *
   * <p>This method creates and stores the beans in the datasource. The beans in the collection will
   * be treated as one transaction, assuming the datasource supports transactions.
   *
   * <p>This means that if one of the beans fail being created in the datasource then the CpoAdapter
   * should stop processing the remainder of the collection, and if supported, rollback all the
   * beans created thus far.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeBean();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   *  }
   * 	try{
   * 		cpo.insertBeans("IdNameInsert",al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the CREATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBeans(
      String groupName,
      Collection<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Removes the bean from the datasource. The assumption is that the bean exists in the datasource.
   * This method stores the bean in the datasource
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.deleteBean(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource an exception will be thrown.
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBean(T bean) throws CpoException;

  /**
   * Removes the bean from the datasource. The assumption is that the bean exists in the datasource.
   * This method stores the bean in the datasource
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.deleteBean("DeleteById",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the DELETE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource an exception will be thrown.
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBean(String groupName, T bean) throws CpoException;

  /**
   * Removes the bean from the datasource. The assumption is that the bean exists in the datasource.
   * This method stores the bean in the datasource
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.deleteBean("DeleteById",so);
   *  } catch (CpoException ce) {
   * 	// Handle the error
   *  }
   * }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the DELETE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Removes the beans contained in the collection from the datasource. The assumption is that the
   * bean exists in the datasource. This method stores the beans contained in the collection in the
   * datasource. The beans in the collection will be treated as one transaction, assuming the
   * datasource supports transactions.
   *
   * <p>This means that if one of the beans fail being deleted in the datasource then the CpoAdapter
   * should stop processing the remainder of the collection, and if supported, rollback all the
   * beans deleted thus far.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.deleteBeans(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBeans(Collection<T> beans) throws CpoException;

  /**
   * Removes the beans contained in the collection from the datasource. The assumption is that the
   * bean exists in the datasource. This method stores the beans contained in the collection in the
   * datasource. The beans in the collection will be treated as one transaction, assuming the
   * datasource supports transactions.
   *
   * <p>This means that if one of the beans fail being deleted in the datasource then the CpoAdapter
   * should stop processing the remainder of the collection, and if supported, rollback all the
   * beans deleted thus far.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *        cpo.deleteBeans("IdNameDelete",al);
   *    } catch (CpoException ce) {
   *        // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the DELETE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBeans(String groupName, Collection<T> beans) throws CpoException;

  /**
   * Removes the beans contained in the collection from the datasource. The assumption is that the
   * bean exists in the datasource. This method stores the beans contained in the collection in the
   * datasource. The beans in the collection will be treated as one transaction, assuming the
   * datasource supports transactions.
   *
   * <p>This means that if one of the beans fail being deleted in the datasource then the CpoAdapter
   * should stop processing the remainder of the collection, and if supported, rollback all the
   * beans deleted thus far.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeBean();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   *  }
   * 	try{
   * 		cpo.deleteBeans("IdNameDelete",al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the DELETE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBeans(
      String groupName,
      Collection<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Executes a bean whose metadata will call an executable within the datasource. It is assumed
   * that the executable bean exists in the metadatasource. If the executable does not exist, an
   * exception will be thrown.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.executeBean(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to populate the IN parameters
   *     used to execute the datasource bean.
   *     <p>A bean of this type will be created and filled with the returned data from the
   *     value_bean. This newly created bean will be returned from this method.
   * @return A bean populated with the OUT parameters returned from the executable bean
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> T executeBean(T bean) throws CpoException;

  /**
   * Executes a bean whose metadata will call an executable within the datasource. It is assumed
   * that the executable bean exists in the metadata source. If the executable does not exist, an
   * exception will be thrown.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.executeBean("execNotifyProc",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to populate the IN parameters
   *     used to retrieve the collection of beans. This bean defines the bean type that will be
   *     returned in the collection and contain the result set data or the OUT Parameters.
   * @return A result bean populate with the OUT parameters
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> T executeBean(String groupName, T bean) throws CpoException;

  /**
   * Executes a bean that represents an executable bean within the datasource. It is assumed that
   * the bean exists in the datasource. If the bean does not exist, an exception will be thrown
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class SomeResult sr = new SomeResult();
   * class CpoAdapter cpo = null;
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
   *      sr = (SomeResult)cpo.executeBean("execNotifyProc",so, sr);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The String groupName of the EXECUTE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to populate the IN parameters
   *     used to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean defines the bean type that will be
   *     created, filled with the return data and returned from this method.
   * @return A bean populated with the out parameters
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> T executeBean(String groupName, C criteria, T result) throws CpoException;

  /**
   * The CpoAdapter will check to see if this bean exists in the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * long count = 0;
   * class CpoAdapter cpo = null;
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
   *      count = cpo.existsBean(so);
   *      if (count>0) {
   *             // bean exists
   *      } else {
   *        // bean does not exist
   *      }
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. This bean will be searched for inside the
   *     datasource.
   * @return The number of beans that exist in the datasource that match the specified bean
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long existsBean(T bean) throws CpoException;

  /**
   * The CpoAdapter will check to see if this bean exists in the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * long count = 0;
   * class CpoAdapter cpo = null;
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
   *      count = cpo.existsBean("SomeExistCheck",so);
   *      if (count>0) {
   *        // bean exists
   *      } else {
   *        // bean does not exist
   *      }
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the EXISTS Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. This bean will be searched for inside the
   *     datasource.
   * @return The number of beans that exist in the datasource that match the specified bean
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long existsBean(String groupName, T bean) throws CpoException;

  /**
   * The CpoAdapter will check to see if this bean exists in the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * long count = 0;
   * class CpoAdapter cpo = null;
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
   *      CpoWhere where = cpo.newCpoWhere(Logical.NONE, id, Comparison.EQ);
   *      count = cpo.existsBean("SomeExistCheck",so, where);
   *      if (count>0) {
   *        // bean exists
   *      } else {
   *        // bean does not exist
   *      }
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the EXISTS Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. This bean will be searched for inside the
   *     datasource.
   * @param wheres A collection of CpoWhere beans that pass in run-time constraints to the function
   *     that performs the exist operation
   * @return The number of beans that exist in the datasource that match the specified bean
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long existsBean(String groupName, T bean, Collection<CpoWhere> wheres) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the beans in the resulting collection.
   * This allows you to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the JavaBean that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @return A CpoOrderBy bean to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the beans in the resulting collection.
   * This allows you to apply user input in determining the order of the collection
   *
   * @param marker the marker that will be replaced in the expression with the string representation
   *     of this orderBy
   * @param attribute The name of the attribute from the JavaBean that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @return A CpoOrderBy bean to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the beans in the resulting collection.
   * This allows you to apply user input in determining the order of the collection
   *
   * @param attribute The name of the attribute from the JavaBean that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @param function A string which represents a datasource function that will be called on the
   *     attribute. must be contained in the function string. The attribute name will be replaced at
   *     run-time with its datasource counterpart
   * @return A CpoOrderBy bean to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException;

  /**
   * newOrderBy allows you to dynamically change the order of the beans in the resulting collection.
   * This allows you to apply user input in determining the order of the collection
   *
   * @param marker the marker that will be replaced in the expression with the string representation
   *     of this orderBy
   * @param attribute The name of the attribute from the JavaBean that will be sorted.
   * @param ascending If true, sort ascending. If false sort descending.
   * @param function A string which represents a datasource function that will be called on the
   *     attribute. must be contained in the function string. The attribute name will be replaced at
   *     run-time with its datasource counterpart
   * @return A CpoOrderBy bean to be passed into retrieveBeans.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function)
      throws CpoException;

  /**
   * Creates a new CpoWhere bean
   *
   * @return A CpoWhere
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  CpoWhere newWhere() throws CpoException;

  /**
   * Creates a new CpoWhere bean
   *
   * @param <T> The type of the bean
   * @param logical The logical operator
   * @param attr The attribute name to compare
   * @param comp The compare operator
   * @param value The value to compare the attribute to.
   * @return A CpoWhere
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value) throws CpoException;

  /**
   * Creates a new CpoWhere bean
   *
   * @param <T> The type of the bean
   * @param logical The logical operator
   * @param attr The attribute name to compare
   * @param comp The compare operator
   * @param value The value to compare the attribute to.
   * @param not negate the compare
   * @return A CpoWhere
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value, boolean not)
      throws CpoException;

  /**
   * Upserts the bean into the datasource. The CpoAdapter will check to see if this bean exists in
   * the datasource. If it exists, the bean is updated in the datasource If the bean does not exist,
   * then it is created in the datasource. This method stores the bean in the datasource. This
   * method uses the default EXISTS, CREATE, and UPDATE Function Groups specified for this bean.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.upsertBean(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @return A count of the number of beans upserted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsBean
   * @see #insertBean
   * @see #updateBean
   */
  <T> long upsertBean(T bean) throws CpoException;

  /**
   * Upserts the bean into the datasource. The CpoAdapter will check to see if this bean exists in
   * the datasource. If it exists, the bean is updated in the datasource If the bean does not exist,
   * then it is created in the datasource. This method stores the bean in the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.upsertBean("upsertSomeBean",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The groupName which identifies which EXISTS, INSERT, and UPDATE Function
   *     Groups to execute to upsert the bean.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @return A count of the number of beans upserted
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsBean
   * @see #insertBean
   * @see #updateBean
   */
  <T> long upsertBean(String groupName, T bean) throws CpoException;

  /**
   * Upserts a collection of beans into the datasource. The CpoAdapter will check to see if this
   * bean exists in the datasource. If it exists, the bean is updated in the datasource If the bean
   * does not exist, then it is created in the datasource. This method stores the bean in the
   * datasource. The beans in the collection will be treated as one transaction, meaning that if one
   * of the beans fail being inserted or updated in the datasource then the entire collection will
   * be rolled back.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.upsertBeans(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsBean
   * @see #insertBean
   * @see #updateBean
   */
  <T> long upsertBeans(Collection<T> beans) throws CpoException;

  /**
   * Upserts a collection of beans into the datasource. The CpoAdapter will check to see if this
   * bean exists in the datasource. If it exists, the bean is updated in the datasource If the bean
   * does not exist, then it is created in the datasource. This method stores the bean in the
   * datasource. The beans in the collection will be treated as one transaction, meaning that if one
   * of the beans fail being inserted or updated in the datasource then the entire collection will
   * be rolled back.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.upsertBeans("myUpsert",al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The groupName which identifies which EXISTS, INSERT, and UPDATE Function
   *     Groups to execute to upsert the bean.
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return DOCUMENT ME!
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see #existsBean
   * @see #insertBean
   * @see #updateBean
   */
  <T> long upsertBeans(String groupName, Collection<T> beans) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource. If the retrieve function defined for these beans returns more than one row, an
   * exception will be thrown.
   *
   * @param <T> The type of the JavaBean
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. The input bean is used to specify the search
   *     criteria, the output bean is populated with the results of the function.
   * @return A bean of the same type as the result parameter that is filled in as specified the
   *     metadata for the retrieve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> T retrieveBean(T bean) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource. If the retrieve function defined for this beans returns more than one row, an
   * exception will be thrown.
   *
   * @param <T> The type of the JavaBean
   * @param groupName DOCUMENT ME!
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. The input bean is used to specify the search
   *     criteria, the output bean is populated with the results of the function.
   * @return A bean of the same type as the result parameter that is filled in as specified the
   *     metadata for the retrieve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> T retrieveBean(String groupName, T bean) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource. If the retrieve function defined for this beans returns more than one row, an
   * exception will be thrown.
   *
   * @param <T> the type of the JavaBean
   * @param groupName DOCUMENT ME!
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. The input bean is used to specify the search
   *     criteria, the output bean is populated with the results of the function.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @return A bean of the same type as the result parameter that is filled in as specified the
   *     metadata for the retrieve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> T retrieveBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource. If the retrieve function defined for this beans returns more than one row, an
   * exception will be thrown.
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the bean type that
   *     will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A bean of the same type as the result parameter that is filled in as specified the
   *     metadata for the retrieve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> T retrieveBean(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource. If the retrieve function defined for this beans returns more than one row, an
   * exception will be thrown.
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the bean type that
   *     will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @return A bean of the same type as the result parameter that is filled in as specified the
   *     metadata for the retrieve.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> T retrieveBean(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <C> the type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <C> List<C> retrieveBeans(String groupName, C criteria) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <C> the type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param where A CpoWhere bean that defines the constraints that should be used when retrieving
   *     beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <C> List<C> retrieveBeans(
      String groupName, C criteria, CpoWhere where, Collection<CpoOrderBy> orderBy)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <C> the type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <C> List<C> retrieveBeans(String groupName, C criteria, Collection<CpoOrderBy> orderBy)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <C> the type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <C> List<C> retrieveBeans(
      String groupName, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the bean type that
   *     will be returned in the collection.
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> List<T> retrieveBeans(String groupName, C criteria, T result) throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the bean type that
   *     will be returned in the collection.
   * @param where A CpoWhere bean that defines the constraints that should be used when retrieving
   *     beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> List<T> retrieveBeans(
      String groupName, C criteria, T result, CpoWhere where, Collection<CpoOrderBy> orderBy)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the bean type that
   *     will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> List<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the bean type that
   *     will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @return A collection of beans will be returned that meet the criteria specified by obj. The
   *     beans will be of the same type as the bean that was passed in. If no beans match the
   *     criteria, an empty collection will be returned
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> List<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource. The assumption is that the bean exists in the
   * datasource.
   *
   * @param <T> The type of the return JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the parameters used
   *     to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to specify the bean type that
   *     will be returned in the collection.
   * @param wheres A collection of CpoWhere beans that define the constraints that should be used
   *     when retrieving beans
   * @param orderBy The CpoOrderBy bean that defines the order in which beans should be returned
   * @param nativeExpressions Native expression that will be used to augment the expression stored
   *     in the metadata. This text will be embedded at run-time
   * @param queueSize queue size of the buffer that it uses to send the beans from the producer to
   *     the consumer.
   * @return A CpoResultSet that can be iterated through
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> CpoResultSet<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      int queueSize)
      throws CpoException;

  /**
   * Update the bean in the datasource. The CpoAdapter will check to see if the bean exists in the
   * datasource. If it exists then the bean will be updated. If it does not exist, an exception will
   * be thrown
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.updateBean(so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBean(T bean) throws CpoException;

  /**
   * Update the bean in the datasource. The CpoAdapter will check to see if the bean exists in the
   * datasource. If it exists then the bean will be updated. If it does not exist, an exception will
   * be thrown
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
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
   *      cpo.updateBean("updateSomeBean",so);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the UPDATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBean(String groupName, T bean) throws CpoException;

  /**
   * Update the bean in the datasource. The CpoAdapter will check to see if the bean exists in the
   * datasource. If it exists then the bean will be updated. If it does not exist, an exception will
   * be thrown
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = new SomeBean();
   * class CpoAdapter cpo = null;
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * if (cpo!=null) {
   * 	so.setId(1);
   * 	so.setName("SomeName");
   * 	try{
   * 		cpo.updateBean("updateSomeBean",so);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the UPDATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Updates a collection of beans in the datasource. The assumption is that the beans contained in
   * the collection exist in the datasource. This method stores the bean in the datasource. The
   * beans in the collection will be treated as one transaction, meaning that if one of the beans
   * fail being updated in the datasource then the entire collection will be rolled back, if
   * supported by the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *    try{
   *      cpo.updateBeans(al);
   *    } catch (CpoException ce) {
   *      // Handle the error
   *    }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBeans(Collection<T> beans) throws CpoException;

  /**
   * Updates a collection of beans in the datasource. The assumption is that the beans contained in
   * the collection exist in the datasource. This method stores the bean in the datasource. The
   * beans in the collection will be treated as one transaction, meaning that if one of the beans
   * fail being updated in the datasource then the entire collection will be rolled back, if
   * supported by the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   *  try {
   *    cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *  if (cpo!=null) {
   *    ArrayList al = new ArrayList();
   *    for (int i=0; i<3; i++){
   *      so = new SomeBean();
   *      so.setId(1);
   *      so.setName("SomeName");
   *      al.add(so);
   *    }
   *      try{
   *        cpo.updateBeans("myUpdate",al);
   *      } catch (CpoException ce) {
   *        // Handle the error
   *      }
   *  }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the UPDATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBeans(String groupName, Collection<T> beans) throws CpoException;

  /**
   * Updates a collection of beans in the datasource. The assumption is that the beans contained in
   * the collection exist in the datasource. This method stores the bean in the datasource. The
   * beans in the collection will be treated as one transaction, meaning that if one of the beans
   * fail being updated in the datasource then the entire collection will be rolled back, if
   * supported by the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeBean so = null;
   * class CpoAdapter cpo = null;
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * if (cpo!=null) {
   * 	ArrayList al = new ArrayList();
   * 	for (int i=0; i<3; i++){
   * 		so = new SomeBean();
   * 		so.setId(1);
   * 		so.setName("SomeName");
   * 		al.add(so);
   *  }
   * 	try{
   * 		cpo.updateBeans("myUpdate",al);
   *  } catch (CpoException ce) {
   * 		// Handle the error
   *  }
   * }
   * }
   * </pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The String groupName of the UPDATE Function Group that will be used to create
   *     the bean in the datasource. null signifies that the default rules will be used.
   * @param beans This is a collection of beans that have been defined within the metadata of the
   *     datasource. If the class is not defined an exception will be thrown.
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBeans(
      String groupName,
      Collection<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Get the CpoMetaDescriptor
   *
   * @return The CpoMetaDescriptor
   */
  CpoMetaDescriptor getCpoMetaDescriptor();

  /**
   * Get the name of the datasource
   *
   * @return The name of the datasource
   */
  String getDataSourceName();

  /**
   * @param expression An expression defining the CpoAttributes that you want
   * @return A list of CpoAttributes
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  List<CpoAttribute> getCpoAttributes(String expression) throws CpoException;
}
