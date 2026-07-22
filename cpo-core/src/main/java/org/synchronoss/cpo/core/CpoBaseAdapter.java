package org.synchronoss.cpo.core;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
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
 * ]]
 */

import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.cache.CpoAdapterCache;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Crud;
import org.synchronoss.cpo.core.enums.Logical;

/**
 * The CpoBaseAdapter has common functionality needed by all Adapter implementations
 *
 * @param <D> The type of the Datasource
 */
public abstract class CpoBaseAdapter<D> extends CpoAdapterCache implements CpoAdapter {
  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  // DataSource Information

  /** The datasource where read queries are executed against */
  private D readDataSource = null;

  /** The datasource where write queries are executed against */
  private D writeDataSource = null;

  /** The name of the datasource */
  private String dataSourceName = null;

  /** The fetchSize used when getting data from the datasource */
  private int fetchSize = 0;

  /** The batchSize used when updating the datasource */
  private int batchSize = 0;

  /**
   * Constructs the adapter with the given data source name and default fetch/batch sizes.
   *
   * @param dataSourceName the name of the datasource this adapter is bound to
   * @param fetchSize the default fetch size to use when reading from the datasource
   * @param batchSize the default batch size to use when writing to the datasource
   */
  public CpoBaseAdapter(String dataSourceName, int fetchSize, int batchSize) {
    this.dataSourceName = dataSourceName;
    this.fetchSize = fetchSize;
    this.batchSize = batchSize;
  }

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

  public int getFetchSize() {
    return fetchSize;
  }

  public void setFetchSize(int fetchSize) {
    this.fetchSize = fetchSize;
  }

  public int getBatchSize() {
    return batchSize;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  @Override
  public <T> long insertBean(CpoQuery query, T bean) throws CpoException {
    return processUpdateGroup(
        bean,
        Crud.CREATE,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T> long insertBeans(CpoQuery query, List<T> beans) throws CpoException {
    return processUpdateGroup(
        beans,
        Crud.CREATE,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T> long deleteBean(CpoQuery query, T bean) throws CpoException {
    return processUpdateGroup(
        bean,
        Crud.DELETE,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T> long deleteBeans(CpoQuery query, List<T> beans) throws CpoException {
    return processUpdateGroup(
        beans,
        Crud.DELETE,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T> long updateBean(CpoQuery query, T bean) throws CpoException {
    return processUpdateGroup(
        bean,
        Crud.UPDATE,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T> long updateBeans(CpoQuery query, List<T> beans) throws CpoException {
    return processUpdateGroup(
        beans,
        Crud.UPDATE,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T> long upsertBean(CpoQuery query, T bean) throws CpoException {
    return processUpdateGroup(
        bean,
        Crud.UPSERT,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T> long upsertBeans(CpoQuery query, List<T> beans) throws CpoException {
    return processUpdateGroup(
        beans,
        Crud.UPSERT,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T, C> T executeBean(CpoQuery query, C criteria, T result) throws CpoException {
    return processExecuteGroup(query.getGroupName(), criteria, result);
  }

  @Override
  public <T> T retrieveBean(CpoQuery query, T bean) throws CpoException {
    return processSelectGroup(
        bean,
        query.getGroupName(),
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions());
  }

  @Override
  public <T, C> T retrieveBean(CpoQuery query, C criteria, T result) throws CpoException {
    // findFirst() short-circuits, so the stream must be closed to release the
    // datastore resources backing it (statement, result set, pooled connection)
    try (Stream<T> beans =
        processSelectGroup(
            query.getGroupName(),
            criteria,
            result,
            query.getWheres(),
            query.getOrderBys(),
            query.getNativeExpressions(),
            true)) {
      return beans.findFirst().orElse(null);
    }
  }

  @Override
  public <T, C> Stream<T> retrieveBeans(CpoQuery query, C criteria, T result) throws CpoException {
    return processSelectGroup(
        query.getGroupName(),
        criteria,
        result,
        query.getWheres(),
        query.getOrderBys(),
        query.getNativeExpressions(),
        false);
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
      List<T> beans,
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
   * @param query The function group and clauses to apply
   * @param bean This is an object that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. This object will be searched for
   *     inside the datasource.
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  public abstract <T> long existsBean(CpoQuery query, T bean) throws CpoException;

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
   * @return A Stream of T or an Empty Stream.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  protected abstract <T, C> Stream<T> processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      boolean useRetrieve)
      throws CpoException;

  private String buildClassInfo(Class<?> clazz, Crud crud, String groupName) {
    return "Class=<"
        + clazz.getSimpleName()
        + "> Function=<"
        + crud.operation
        + "> Group=<"
        + groupName
        + "> ==========";
  }

  protected String buildCpoClassLogLine(Class<?> clazz, Crud crud, String groupName) {
    return "========== " + buildClassInfo(clazz, crud, groupName);
  }

  protected String buildExecutedLogLine(Class<?> clazz, Crud crud, String groupName) {
    return "========== Executed - " + buildClassInfo(clazz, crud, groupName);
  }

  protected String buildBatchLogLine(Class<?> clazz, Crud crud, String groupName) {
    return "========== BATCH - " + buildClassInfo(clazz, crud, groupName);
  }

  protected String buildUpdatesLogLine(long updates, Class<?> clazz, Crud crud, String groupName) {
    return "========== " + updates + " Updates - " + buildClassInfo(clazz, crud, groupName);
  }

  protected String buildRecordsLogLine(
      long records, int attributes, Class<?> clazz, Crud crud, String groupName) {
    return "========== "
        + records
        + " Records - "
        + attributes
        + " Attributes - "
        + buildClassInfo(clazz, crud, groupName);
  }
}
