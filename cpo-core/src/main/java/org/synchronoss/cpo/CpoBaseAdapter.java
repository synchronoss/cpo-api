package org.synchronoss.cpo;

/*-
 * #%L
 * core
 * %%
 * Copyright (C) 2003 - 2025 David E. Berry
 * %%
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
 * #L%
 */

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cache.CpoAdapterCache;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Crud;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * The CpoBaseAdapter has common functionality needed by all Adapter implementations
 *
 * @param <D> The type of the Datasource
 */
public abstract class CpoBaseAdapter<D> extends CpoAdapterCache implements CpoAdapter {
  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CpoBaseAdapter.class);

  // DataSource Information

  /** The datasource where read queries are executed against */
  private D readDataSource = null;

  /** The datasource where write queries are executed against */
  private D writeDataSource = null;

  /** The name of the datasource */
  private String dataSourceName = null;

  protected D getReadDataSource() {
    return readDataSource;
  }

  protected void setReadDataSource(D readDataSource) {
    this.readDataSource = readDataSource;
  }

  protected D getWriteDataSource() {
    return writeDataSource;
  }

  protected void setWriteDataSource(D writeDataSource) {
    this.writeDataSource = writeDataSource;
  }

  public String getDataSourceName() {
    return dataSourceName;
  }

  protected void setDataSourceName(String dataSourceName) {
    this.dataSourceName = dataSourceName;
  }

  @Override
  public <T> long insertBean(T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.CREATE, null, null, null, null);
  }

  @Override
  public <T> long insertBean(String groupName, T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.CREATE, groupName, null, null, null);
  }

  @Override
  public <T> long insertBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processUpdateGroup(bean, Crud.CREATE, groupName, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long insertBeans(Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.CREATE, null, null, null, null);
  }

  @Override
  public <T> long insertBeans(String groupName, Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.CREATE, groupName, null, null, null);
  }

  @Override
  public <T> long insertBeans(
      String groupName,
      Collection<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processUpdateGroup(beans, Crud.CREATE, groupName, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long deleteBean(T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.DELETE, null, null, null, null);
  }

  @Override
  public <T> long deleteBean(String groupName, T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.DELETE, groupName, null, null, null);
  }

  @Override
  public <T> long deleteBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processUpdateGroup(bean, Crud.DELETE, groupName, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long deleteBeans(Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.DELETE, null, null, null, null);
  }

  @Override
  public <T> long deleteBeans(String groupName, Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.DELETE, groupName, null, null, null);
  }

  @Override
  public <T> long deleteBeans(
      String groupName,
      Collection<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processUpdateGroup(beans, Crud.DELETE, groupName, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> T executeBean(T bean) throws CpoException {
    return processExecuteGroup(null, bean, bean);
  }

  @Override
  public <T> T executeBean(String groupName, T bean) throws CpoException {
    return processExecuteGroup(groupName, bean, bean);
  }

  @Override
  public <T, C> T executeBean(String groupName, C criteria, T result) throws CpoException {
    return processExecuteGroup(groupName, criteria, result);
  }

  @Override
  public <T> long existsBean(T bean) throws CpoException {
    return this.existsBean(null, bean);
  }

  @Override
  public <T> long existsBean(String groupName, T bean) throws CpoException {
    return this.existsBean(groupName, bean, null);
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException {
    return new BindableCpoOrderBy(attribute, ascending);
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending)
      throws CpoException {
    return new BindableCpoOrderBy(marker, attribute, ascending);
  }

  @Override
  public CpoOrderBy newOrderBy(String attribute, boolean ascending, String function)
      throws CpoException {
    return new BindableCpoOrderBy(attribute, ascending, function);
  }

  @Override
  public CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function)
      throws CpoException {
    return new BindableCpoOrderBy(marker, attribute, ascending, function);
  }

  @Override
  public CpoWhere newWhere() throws CpoException {
    return new BindableCpoWhere();
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value)
      throws CpoException {
    return new BindableCpoWhere(logical, attr, comp, value);
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value, boolean not)
      throws CpoException {
    return new BindableCpoWhere(logical, attr, comp, value, not);
  }

  @Override
  public <T> long upsertBean(T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.UPSERT, null, null, null, null);
  }

  @Override
  public <T> long upsertBean(String groupName, T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.UPSERT, groupName, null, null, null);
  }

  @Override
  public <T> long upsertBeans(Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.UPSERT, null, null, null, null);
  }

  @Override
  public <T> long upsertBeans(String groupName, Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.UPSERT, groupName, null, null, null);
  }

  @Override
  public <T> T retrieveBean(T bean) throws CpoException {
    return processSelectGroup(bean, null, null, null, null);
  }

  @Override
  public <T> T retrieveBean(String groupName, T bean) throws CpoException {
    return processSelectGroup(bean, groupName, null, null, null);
  }

  @Override
  public <T> T retrieveBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processSelectGroup(bean, groupName, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T, C> T retrieveBean(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return retrieveBean(groupName, criteria, result, wheres, orderBy, null);
  }

  @Override
  public <T, C> T retrieveBean(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    Iterator<T> it =
        processSelectGroup(groupName, criteria, result, wheres, orderBy, nativeExpressions, true)
            .iterator();
    if (it.hasNext()) {
      return it.next();
    } else {
      return null;
    }
  }

  @Override
  public <C> List<C> retrieveBeans(String groupName, C criteria) throws CpoException {
    return processSelectGroup(groupName, criteria, criteria, null, null, null, false);
  }

  @Override
  public <C> List<C> retrieveBeans(
      String groupName, C criteria, CpoWhere where, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<>();
      wheres.add(where);
    }
    return processSelectGroup(groupName, criteria, criteria, wheres, orderBy, null, false);
  }

  @Override
  public <C> List<C> retrieveBeans(
      String groupName, C criteria, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return processSelectGroup(groupName, criteria, criteria, wheres, orderBy, null, false);
  }

  @Override
  public <C> List<C> retrieveBeans(String groupName, C criteria, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return processSelectGroup(groupName, criteria, criteria, null, orderBy, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(String groupName, C criteria, T result) throws CpoException {
    return processSelectGroup(groupName, criteria, result, null, null, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(
      String groupName, C criteria, T result, CpoWhere where, Collection<CpoOrderBy> orderBy)
      throws CpoException {
    ArrayList<CpoWhere> wheres = null;
    if (where != null) {
      wheres = new ArrayList<>();
      wheres.add(where);
    }
    return processSelectGroup(groupName, criteria, result, wheres, orderBy, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy)
      throws CpoException {
    return processSelectGroup(groupName, criteria, result, wheres, orderBy, null, false);
  }

  @Override
  public <T, C> List<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processSelectGroup(
        groupName, criteria, result, wheres, orderBy, nativeExpressions, false);
  }

  @Override
  public <T, C> CpoResultSet<T> retrieveBeans(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      int queueSize) {
    CpoBlockingResultSet<T> resultSet = new CpoBlockingResultSet<>(queueSize);
    RetrieverThread<T, C> retrieverThread =
        new RetrieverThread<T, C>(
            groupName, criteria, result, wheres, orderBy, nativeExpressions, false, resultSet);

    retrieverThread.start();
    return resultSet;
  }

  @Override
  public <T> long updateBean(T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.UPDATE, null, null, null, null);
  }

  @Override
  public <T> long updateBean(String groupName, T bean) throws CpoException {
    return processUpdateGroup(bean, Crud.UPDATE, groupName, null, null, null);
  }

  @Override
  public <T> long updateBean(
      String groupName,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processUpdateGroup(bean, Crud.UPDATE, groupName, wheres, orderBy, nativeExpressions);
  }

  @Override
  public <T> long updateBeans(Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.UPDATE, null, null, null, null);
  }

  @Override
  public <T> long updateBeans(String groupName, Collection<T> beans) throws CpoException {
    return processUpdateGroup(beans, Crud.UPDATE, groupName, null, null, null);
  }

  @Override
  public <T> long updateBeans(
      String groupName,
      Collection<T> beans,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    return processUpdateGroup(beans, Crud.UPDATE, groupName, wheres, orderBy, nativeExpressions);
  }

  /**
   * Updates beans in the datasource
   *
   * @param <T> The bean type
   * @param bean The bean instance
   * @param crud The query group type
   * @param groupName The query group type
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @return The number of records updated
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  protected abstract <T> long processUpdateGroup(
      T bean,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Updates beans in the datasource
   *
   * @param <T> The bean type
   * @param beans The collection of T to update
   * @param crud The query group type
   * @param groupName The query group type
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @return The number of records updated
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  protected abstract <T> long processUpdateGroup(
      Collection<T> beans,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Executes a bean whose MetaData contains a stored procedure. An assumption is that the bean
   * exists in the datasource.
   *
   * @param <T> The result bean type
   * @param <C> The criteria bean type
   * @param groupName The filter groupName which tells the datasource which beans should be
   *     returned. The groupName also signifies what data in the bean will be populated.
   * @param criteria This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean is used to populate the IN arguments
   *     used to retrieve the collection of beans.
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean defines the bean type that will be
   *     returned in the
   * @return A result bean populate with the OUT arguments
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  protected abstract <T, C> T processExecuteGroup(String groupName, C criteria, T result)
      throws CpoException;

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * <pre>Example:
   * {@code
   * class SomeObject so = new SomeObject();
   * long count = 0;
   * class CpoAdapter cpo = null;
   *
   *  try {
   *    cpo = new CpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   *  } catch (CpoException ce) {
   *    // Handle the error
   *    cpo = null;
   *  }
   *
   *  if (cpo!=null) {
   *    so.setId(1);
   *    so.setName("SomeName");
   *    try{
   *      CpoWhere where = cpo.newCpoWhere(Logical.NONE, id, Comparison.EQ);
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
   * }
   * </pre>
   *
   * @param groupName The String name of the EXISTS Function Group that will be used to create the
   *     object in the datasource. null signifies that the default rules will be used.
   * @param bean This is an object that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. This object will be searched for
   *     inside the datasource.
   * @param wheres A CpoWhere object that passes in run-time constraints to the function that
   *     performs the exist
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public abstract <T> long existsBean(String groupName, T bean, Collection<CpoWhere> wheres)
      throws CpoException;

  /**
   * Retrieves the bean from the datasource.
   *
   * @param <T> The bean type
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown. The input bean is used to specify the
   *     search criteria.
   * @param groupName The name which identifies which RETRIEVE Function Group to execute to retrieve
   *     the bean.
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @return A populated bean of the same type as the bean passed in as a argument. If no beans
   *     match the criteria a NULL will be returned.
   * @throws CpoException Thrown if there are errors accessing the datasource or more than one row
   *     is returned
   */
  protected abstract <T> T processSelectGroup(
      T bean,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException;

  /**
   * Retrieves beans from the datasource.
   *
   * @param <T> The result bean type
   * @param <C> The criteria bean type
   * @param groupName Query group groupName
   * @param criteria The criteria bean
   * @param result The result bean
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @param useRetrieve Use the RETRIEVE_GROUP instead of the LIST_GROUP
   * @return A List of T or an Empty List.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  protected abstract <T, C> List<T> processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      boolean useRetrieve)
      throws CpoException;

  /**
   * Retrieves beans from the datasource.
   *
   * @param <T> The result bean type
   * @param <C> The criteria bean type
   * @param groupName Query group groupName
   * @param criteria The criteria bean
   * @param result The result bean
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @param useRetrieve Use the RETRIEVE_GROUP instead of the LIST_GROUP
   * @param resultSet The result set to add the results to.
   * @throws CpoException Any errors retrieving the data from the datasource
   */
  protected abstract <T, C> void processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      boolean useRetrieve,
      CpoResultSet<T> resultSet)
      throws CpoException;

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

    public RetrieverThread(
        String name,
        C criteria,
        T result,
        Collection<CpoWhere> wheres,
        Collection<CpoOrderBy> orderBy,
        Collection<CpoNativeFunction> nativeExpressions,
        boolean useRetrieve,
        CpoBlockingResultSet<T> resultSet) {
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
        processSelectGroup(
            name, criteria, result, wheres, orderBy, nativeExpressions, false, resultSet);
      } catch (CpoException e) {
        logger.error(ExceptionHelper.getLocalizedMessage(e));
      } finally {
        // wait until the calling thread is finished processing the records
        while (resultSet.size() > 0) {
          Thread.yield();
        }
        // Tell the calling thread that it should not wait on the blocking queue any longer.
        callingThread.interrupt();
      }
    }
  }
}
