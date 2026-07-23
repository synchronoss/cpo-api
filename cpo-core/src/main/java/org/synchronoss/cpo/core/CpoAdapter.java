package org.synchronoss.cpo.core;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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

import java.util.List;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;

/**
 * CpoAdapter is an interface for a set of routines that are responsible for Creating, Retrieving,
 * Updating, and Deleting (CRUD) value beans within a datasource.
 *
 * <p>CpoAdapter is an interface that acts as a common facade for different datasources. It is
 * conceivable that an CpoAdapter can be implemented for JDBC, CSV, XML, LDAP, and more datasources.
 *
 * <p>Every operation exists in a canonical form taking a {@link CpoQuery} — which carries the
 * function group name and any run-time where constraints, orderings, and native expressions — plus
 * convenience forms for the common no-clause cases. Example:
 *
 * <pre>{@code
 * CpoAdapter cpo = CpoAdapterFactoryManager.getCpoAdapter("myContext");
 * CpoWhere where = cpo.whereBuilder().where("id", Comparison.EQ, 42).build();
 * try (Stream<SomeBean> beans =
 *     cpo.retrieveBeans(CpoQuery.group("byId").where(where), criteria)) {
 *   beans.forEach(...);
 * }
 * }</pre>
 *
 * <p>The examples on the methods below assume a {@code cpo} adapter obtained as above and a {@code
 * SomeBean} class mapped in the meta data.
 *
 * @author david berry
 */
public interface CpoAdapter extends java.io.Serializable {

  // ==================================== INSERT ====================================

  /**
   * Creates the bean in the datasource using the query's CREATE function group. The assumption is
   * that the bean does not exist in the datasource.
   *
   * <pre>{@code
   * SomeBean bean = new SomeBean();
   * bean.setId(1);
   * bean.setName("SomeName");
   * cpo.insertBean(CpoQuery.group("createBean"), bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBean(CpoQuery query, T bean) throws CpoException;

  /**
   * Creates the beans in the datasource using the query's CREATE function group, batching where the
   * datasource supports it. This is an all-or-nothing transaction: if one bean fails, no beans are
   * created.
   *
   * <pre>{@code
   * List<SomeBean> beans = List.of(bean1, bean2);
   * long created = cpo.insertBeans(CpoQuery.group("createBean"), beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param beans The beans to create
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long insertBeans(CpoQuery query, List<T> beans) throws CpoException;

  /**
   * Creates the bean in the datasource using the default CREATE function group.
   *
   * <pre>{@code
   * cpo.insertBean(bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long insertBean(T bean) throws CpoException {
    return insertBean(CpoQuery.defaultGroup(), bean);
  }

  /**
   * Creates the bean in the datasource using the named CREATE function group.
   *
   * <pre>{@code
   * cpo.insertBean("createBean", bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The CREATE function group name; null signifies the default group
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long insertBean(String groupName, T bean) throws CpoException {
    return insertBean(CpoQuery.group(groupName), bean);
  }

  /**
   * Creates the beans in the datasource using the default CREATE function group.
   *
   * <pre>{@code
   * cpo.insertBeans(beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans The beans to create
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long insertBeans(List<T> beans) throws CpoException {
    return insertBeans(CpoQuery.defaultGroup(), beans);
  }

  /**
   * Creates the beans in the datasource using the named CREATE function group.
   *
   * <pre>{@code
   * cpo.insertBeans("createBean", beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The CREATE function group name; null signifies the default group
   * @param beans The beans to create
   * @return The number of beans created in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long insertBeans(String groupName, List<T> beans) throws CpoException {
    return insertBeans(CpoQuery.group(groupName), beans);
  }

  // ==================================== DELETE ====================================

  /**
   * Removes the bean from the datasource using the query's DELETE function group. The assumption is
   * that the bean exists in the datasource.
   *
   * <pre>{@code
   * SomeBean bean = new SomeBean();
   * bean.setId(1);
   * cpo.deleteBean(CpoQuery.group("deleteBean"), bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBean(CpoQuery query, T bean) throws CpoException;

  /**
   * Removes the beans from the datasource using the query's DELETE function group, batching where
   * the datasource supports it. This is an all-or-nothing transaction: if one bean fails, no beans
   * are deleted.
   *
   * <pre>{@code
   * long deleted = cpo.deleteBeans(CpoQuery.group("deleteBean"), beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param beans The beans to delete
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long deleteBeans(CpoQuery query, List<T> beans) throws CpoException;

  /**
   * Removes the bean from the datasource using the default DELETE function group.
   *
   * <pre>{@code
   * cpo.deleteBean(bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long deleteBean(T bean) throws CpoException {
    return deleteBean(CpoQuery.defaultGroup(), bean);
  }

  /**
   * Removes the bean from the datasource using the named DELETE function group.
   *
   * <pre>{@code
   * cpo.deleteBean("deleteBean", bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The DELETE function group name; null signifies the default group
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long deleteBean(String groupName, T bean) throws CpoException {
    return deleteBean(CpoQuery.group(groupName), bean);
  }

  /**
   * Removes the beans from the datasource using the default DELETE function group.
   *
   * <pre>{@code
   * cpo.deleteBeans(beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans The beans to delete
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long deleteBeans(List<T> beans) throws CpoException {
    return deleteBeans(CpoQuery.defaultGroup(), beans);
  }

  /**
   * Removes the beans from the datasource using the named DELETE function group.
   *
   * <pre>{@code
   * cpo.deleteBeans("deleteBean", beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The DELETE function group name; null signifies the default group
   * @param beans The beans to delete
   * @return The number of beans deleted from the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long deleteBeans(String groupName, List<T> beans) throws CpoException {
    return deleteBeans(CpoQuery.group(groupName), beans);
  }

  // ==================================== UPDATE ====================================

  /**
   * Updates the bean in the datasource using the query's UPDATE function group. The assumption is
   * that the bean exists in the datasource.
   *
   * <pre>{@code
   * SomeBean bean = new SomeBean();
   * bean.setId(1);
   * bean.setName("NewName");
   * cpo.updateBean(CpoQuery.group("updateBean"), bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBean(CpoQuery query, T bean) throws CpoException;

  /**
   * Updates the beans in the datasource using the query's UPDATE function group, batching where the
   * datasource supports it. This is an all-or-nothing transaction: if one bean fails, no beans are
   * updated.
   *
   * <pre>{@code
   * long updated = cpo.updateBeans(CpoQuery.group("updateBean"), beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param beans The beans to update
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long updateBeans(CpoQuery query, List<T> beans) throws CpoException;

  /**
   * Updates the bean in the datasource using the default UPDATE function group.
   *
   * <pre>{@code
   * cpo.updateBean(bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long updateBean(T bean) throws CpoException {
    return updateBean(CpoQuery.defaultGroup(), bean);
  }

  /**
   * Updates the bean in the datasource using the named UPDATE function group.
   *
   * <pre>{@code
   * cpo.updateBean("updateBean", bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The UPDATE function group name; null signifies the default group
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long updateBean(String groupName, T bean) throws CpoException {
    return updateBean(CpoQuery.group(groupName), bean);
  }

  /**
   * Updates the beans in the datasource using the default UPDATE function group.
   *
   * <pre>{@code
   * cpo.updateBeans(beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans The beans to update
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long updateBeans(List<T> beans) throws CpoException {
    return updateBeans(CpoQuery.defaultGroup(), beans);
  }

  /**
   * Updates the beans in the datasource using the named UPDATE function group.
   *
   * <pre>{@code
   * cpo.updateBeans("updateBean", beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The UPDATE function group name; null signifies the default group
   * @param beans The beans to update
   * @return The number of beans updated in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long updateBeans(String groupName, List<T> beans) throws CpoException {
    return updateBeans(CpoQuery.group(groupName), beans);
  }

  // ==================================== UPSERT ====================================

  /**
   * Inserts or updates the bean using the query's UPSERT function group: the EXIST function decides
   * whether the CREATE or UPDATE function is executed.
   *
   * <pre>{@code
   * SomeBean bean = new SomeBean();
   * bean.setId(1);
   * bean.setName("SomeName");
   * cpo.upsertBean(CpoQuery.group("upsertBean"), bean); // inserts or updates as needed
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans upserted in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource, or if the EXIST
   *     function matches more than one record
   */
  <T> long upsertBean(CpoQuery query, T bean) throws CpoException;

  /**
   * Inserts or updates the beans using the query's UPSERT function group: for each bean the EXIST
   * function decides whether the CREATE or UPDATE function is executed.
   *
   * <pre>{@code
   * long upserted = cpo.upsertBeans(CpoQuery.group("upsertBean"), beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param beans The beans to upsert
   * @return The number of beans upserted in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource, or if the EXIST
   *     function matches more than one record
   */
  <T> long upsertBeans(CpoQuery query, List<T> beans) throws CpoException;

  /**
   * Inserts or updates the bean using the default UPSERT function group.
   *
   * <pre>{@code
   * cpo.upsertBean(bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans upserted in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource, or if the EXIST
   *     function matches more than one record
   */
  default <T> long upsertBean(T bean) throws CpoException {
    return upsertBean(CpoQuery.defaultGroup(), bean);
  }

  /**
   * Inserts or updates the bean using the named UPSERT function group.
   *
   * <pre>{@code
   * cpo.upsertBean("upsertBean", bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The UPSERT function group name; null signifies the default group
   * @param bean A bean defined within the metadata of the datasource
   * @return The number of beans upserted in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource, or if the EXIST
   *     function matches more than one record
   */
  default <T> long upsertBean(String groupName, T bean) throws CpoException {
    return upsertBean(CpoQuery.group(groupName), bean);
  }

  /**
   * Inserts or updates the beans using the default UPSERT function group.
   *
   * <pre>{@code
   * cpo.upsertBeans(beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param beans The beans to upsert
   * @return The number of beans upserted in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource, or if the EXIST
   *     function matches more than one record
   */
  default <T> long upsertBeans(List<T> beans) throws CpoException {
    return upsertBeans(CpoQuery.defaultGroup(), beans);
  }

  /**
   * Inserts or updates the beans using the named UPSERT function group.
   *
   * <pre>{@code
   * cpo.upsertBeans("upsertBean", beans);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The UPSERT function group name; null signifies the default group
   * @param beans The beans to upsert
   * @return The number of beans upserted in the datasource
   * @throws CpoException Thrown if there are errors accessing the datasource, or if the EXIST
   *     function matches more than one record
   */
  default <T> long upsertBeans(String groupName, List<T> beans) throws CpoException {
    return upsertBeans(CpoQuery.group(groupName), beans);
  }

  // ==================================== EXISTS ====================================

  /**
   * Checks whether beans matching the given bean exist in the datasource, using the query's EXIST
   * function group and where constraints.
   *
   * <pre>{@code
   * CpoWhere where = cpo.whereBuilder().where("id", Comparison.EQ, 1).build();
   * long count = cpo.existsBean(CpoQuery.group("existsBean").where(where), bean);
   * if (count > 0) {
   *   // the bean exists
   * }
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param bean The bean to search for in the datasource
   * @return The number of beans that exist in the datasource that match the specified bean. An
   *     EXIST function must either return a count as a single row with a single numeric column
   *     (count(*) style) or return one row per matching bean; a single-row result with one numeric
   *     column is always interpreted as a count
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T> long existsBean(CpoQuery query, T bean) throws CpoException;

  /**
   * Checks whether beans matching the given bean exist, using the default EXIST function group.
   *
   * <pre>{@code
   * long count = cpo.existsBean(bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean The bean to search for in the datasource
   * @return The number of beans that exist in the datasource that match the specified bean
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long existsBean(T bean) throws CpoException {
    return existsBean(CpoQuery.defaultGroup(), bean);
  }

  /**
   * Checks whether beans matching the given bean exist, using the named EXIST function group.
   *
   * <pre>{@code
   * long count = cpo.existsBean("existsBean", bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The EXIST function group name; null signifies the default group
   * @param bean The bean to search for in the datasource
   * @return The number of beans that exist in the datasource that match the specified bean
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> long existsBean(String groupName, T bean) throws CpoException {
    return existsBean(CpoQuery.group(groupName), bean);
  }

  // ==================================== EXECUTE ====================================

  /**
   * Executes the EXECUTE function group identified by the query — typically a stored procedure —
   * using the criteria bean to populate the IN arguments and the result bean type for the OUT
   * arguments.
   *
   * <pre>{@code
   * SomeResult result =
   *     cpo.executeBean(CpoQuery.group("calculateTotals"), criteria, new SomeResult());
   * }</pre>
   *
   * @param <T> The type of the result JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param query The function group to execute
   * @param criteria A bean defined within the metadata of the datasource used to populate the IN
   *     arguments
   * @param result A bean defined within the metadata of the datasource that defines the bean type
   *     populated with the OUT arguments
   * @return A result bean populated with the OUT arguments
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> T executeBean(CpoQuery query, C criteria, T result) throws CpoException;

  /**
   * Executes the default EXECUTE function group with the bean as both criteria and result.
   *
   * <pre>{@code
   * SomeBean result = cpo.executeBean(bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean The bean used for the IN arguments and populated with the OUT arguments
   * @return A result bean populated with the OUT arguments
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> T executeBean(T bean) throws CpoException {
    return executeBean(CpoQuery.defaultGroup(), bean, bean);
  }

  /**
   * Executes the named EXECUTE function group with the bean as both criteria and result.
   *
   * <pre>{@code
   * SomeBean result = cpo.executeBean("calculateTotals", bean);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The EXECUTE function group name; null signifies the default group
   * @param bean The bean used for the IN arguments and populated with the OUT arguments
   * @return A result bean populated with the OUT arguments
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T> T executeBean(String groupName, T bean) throws CpoException {
    return executeBean(CpoQuery.group(groupName), bean, bean);
  }

  // ==================================== RETRIEVE ====================================

  /**
   * Retrieves a single bean from the datasource using the query's RETRIEVE function group, with the
   * given bean supplying the search criteria and receiving the result. If the function returns more
   * than one row, an exception is thrown.
   *
   * <pre>{@code
   * SomeBean criteria = new SomeBean();
   * criteria.setId(1);
   * SomeBean bean = cpo.retrieveBean(CpoQuery.group("retrieveBean"), criteria);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param query The function group and clauses to apply
   * @param bean A bean defined within the metadata of the datasource whose attributes supply the
   *     search criteria
   * @return A populated bean of the same type as the bean passed in, or null if no beans match
   * @throws CpoException Thrown if there are errors accessing the datasource or more than one row
   *     is returned
   */
  <T> T retrieveBean(CpoQuery query, T bean) throws CpoException;

  /**
   * Retrieves the first bean produced by the query's RETRIEVE function group, using separate
   * criteria and result beans.
   *
   * <pre>{@code
   * SomeResult result =
   *     cpo.retrieveBean(CpoQuery.group("retrieveResult"), criteria, new SomeResult());
   * }</pre>
   *
   * @param <T> The type of the result JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param query The function group and clauses to apply
   * @param criteria A bean defined within the metadata of the datasource that supplies the
   *     retrieval parameters
   * @param result A bean defined within the metadata of the datasource that specifies the returned
   *     bean type
   * @return A bean of the same type as the result parameter, or null if no beans match
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> T retrieveBean(CpoQuery query, C criteria, T result) throws CpoException;

  /**
   * Retrieves a single bean using the default RETRIEVE function group; the bean supplies the search
   * criteria and receives the result.
   *
   * <pre>{@code
   * SomeBean bean = cpo.retrieveBean(criteria);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param bean A bean defined within the metadata of the datasource whose attributes supply the
   *     search criteria
   * @return A populated bean of the same type as the bean passed in, or null if no beans match
   * @throws CpoException Thrown if there are errors accessing the datasource or more than one row
   *     is returned
   */
  default <T> T retrieveBean(T bean) throws CpoException {
    return retrieveBean(CpoQuery.defaultGroup(), bean);
  }

  /**
   * Retrieves a single bean using the named RETRIEVE function group; the bean supplies the search
   * criteria and receives the result.
   *
   * <pre>{@code
   * SomeBean bean = cpo.retrieveBean("retrieveBean", criteria);
   * }</pre>
   *
   * @param <T> The type of the JavaBean
   * @param groupName The RETRIEVE function group name; null signifies the default group
   * @param bean A bean defined within the metadata of the datasource whose attributes supply the
   *     search criteria
   * @return A populated bean of the same type as the bean passed in, or null if no beans match
   * @throws CpoException Thrown if there are errors accessing the datasource or more than one row
   *     is returned
   */
  default <T> T retrieveBean(String groupName, T bean) throws CpoException {
    return retrieveBean(CpoQuery.group(groupName), bean);
  }

  /**
   * Retrieves beans from the datasource using the query's LIST function group, with separate
   * criteria and result beans.
   *
   * <pre>{@code
   * CpoWhere where = cpo.whereBuilder().where("dept", Comparison.EQ, "sales").build();
   * CpoOrderBy orderBy = cpo.newOrderBy("name", true);
   * try (Stream<SomeResult> beans =
   *     cpo.retrieveBeans(
   *         CpoQuery.group("listResults").where(where).orderBy(orderBy),
   *         criteria,
   *         new SomeResult())) {
   *   beans.forEach(...);
   * }
   * }</pre>
   *
   * @param <T> The type of the result JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param query The function group and clauses to apply
   * @param criteria A bean defined within the metadata of the datasource that supplies the
   *     retrieval parameters
   * @param result A bean defined within the metadata of the datasource that specifies the returned
   *     bean type
   * @return A stream of beans that meet the criteria; empty if none match. The stream is backed by
   *     open datastore resources (statement, result set, and connection) that are released only
   *     when the stream is closed; terminal operations do not close it. Always close the returned
   *     stream, preferably with try-with-resources.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  <T, C> Stream<T> retrieveBeans(CpoQuery query, C criteria, T result) throws CpoException;

  /**
   * Retrieves beans from the datasource using the query's LIST function group; the criteria bean
   * type is also the result type.
   *
   * <pre>{@code
   * CpoWhere where = cpo.whereBuilder().where("dept", Comparison.EQ, "sales").build();
   * try (Stream<SomeBean> beans =
   *     cpo.retrieveBeans(CpoQuery.group("listBeans").where(where), criteria)) {
   *   beans.forEach(...);
   * }
   * }</pre>
   *
   * @param <C> The type of the criteria JavaBean
   * @param query The function group and clauses to apply
   * @param criteria A bean defined within the metadata of the datasource that supplies the
   *     retrieval parameters
   * @return A stream of beans that meet the criteria; empty if none match. The stream is backed by
   *     open datastore resources (statement, result set, and connection) that are released only
   *     when the stream is closed; terminal operations do not close it. Always close the returned
   *     stream, preferably with try-with-resources.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <C> Stream<C> retrieveBeans(CpoQuery query, C criteria) throws CpoException {
    return retrieveBeans(query, criteria, criteria);
  }

  /**
   * Retrieves beans using the named LIST function group; the criteria bean type is also the result
   * type.
   *
   * <pre>{@code
   * try (Stream<SomeBean> beans = cpo.retrieveBeans("listBeans", criteria)) {
   *   beans.forEach(...);
   * }
   * }</pre>
   *
   * @param <C> The type of the criteria JavaBean
   * @param groupName The LIST function group name; null signifies the default group
   * @param criteria A bean defined within the metadata of the datasource that supplies the
   *     retrieval parameters
   * @return A stream of beans that meet the criteria; empty if none match. Always close the
   *     returned stream, preferably with try-with-resources.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <C> Stream<C> retrieveBeans(String groupName, C criteria) throws CpoException {
    return retrieveBeans(CpoQuery.group(groupName), criteria, criteria);
  }

  /**
   * Retrieves beans using the named LIST function group with separate criteria and result beans.
   *
   * <pre>{@code
   * try (Stream<SomeResult> beans =
   *     cpo.retrieveBeans("listResults", criteria, new SomeResult())) {
   *   beans.forEach(...);
   * }
   * }</pre>
   *
   * @param <T> The type of the result JavaBean
   * @param <C> The type of the criteria JavaBean
   * @param groupName The LIST function group name; null signifies the default group
   * @param criteria A bean defined within the metadata of the datasource that supplies the
   *     retrieval parameters
   * @param result A bean defined within the metadata of the datasource that specifies the returned
   *     bean type
   * @return A stream of beans that meet the criteria; empty if none match. Always close the
   *     returned stream, preferably with try-with-resources.
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  default <T, C> Stream<T> retrieveBeans(String groupName, C criteria, T result)
      throws CpoException {
    return retrieveBeans(CpoQuery.group(groupName), criteria, result);
  }

  // ==================================== FACTORIES ====================================

  /**
   * Creates a CpoOrderBy for the attribute and direction.
   *
   * <pre>{@code
   * CpoOrderBy byNameAscending = cpo.newOrderBy("name", true);
   * }</pre>
   *
   * @param attribute The metadata attribute name to order by
   * @param ascending true for ascending, false for descending
   * @return A CpoOrderBy
   * @throws CpoException An error occurred creating the CpoOrderBy
   */
  CpoOrderBy newOrderBy(String attribute, boolean ascending) throws CpoException;

  /**
   * Creates a CpoOrderBy bound to a marker within the expression.
   *
   * @param marker The marker in the expression that this order-by replaces
   * @param attribute The metadata attribute name to order by
   * @param ascending true for ascending, false for descending
   * @return A CpoOrderBy
   * @throws CpoException An error occurred creating the CpoOrderBy
   */
  CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending) throws CpoException;

  /**
   * Creates a CpoOrderBy applying a datasource function to the attribute.
   *
   * @param attribute The metadata attribute name to order by
   * @param ascending true for ascending, false for descending
   * @param function A datasource function to apply to the attribute
   * @return A CpoOrderBy
   * @throws CpoException An error occurred creating the CpoOrderBy
   */
  CpoOrderBy newOrderBy(String attribute, boolean ascending, String function) throws CpoException;

  /**
   * Creates a CpoOrderBy bound to a marker, applying a datasource function to the attribute.
   *
   * @param marker The marker in the expression that this order-by replaces
   * @param attribute The metadata attribute name to order by
   * @param ascending true for ascending, false for descending
   * @param function A datasource function to apply to the attribute
   * @return A CpoOrderBy
   * @throws CpoException An error occurred creating the CpoOrderBy
   */
  CpoOrderBy newOrderBy(String marker, String attribute, boolean ascending, String function)
      throws CpoException;

  /**
   * Creates an empty CpoWhere.
   *
   * <pre>{@code
   * CpoWhere where = cpo.newWhere();
   * }</pre>
   *
   * @return A CpoWhere
   * @throws CpoException An error occurred creating the CpoWhere
   */
  CpoWhere newWhere() throws CpoException;

  /**
   * Creates a CpoWhere comparing the attribute to the value.
   *
   * <pre>{@code
   * CpoWhere where = cpo.newWhere(Logical.NONE, "id", Comparison.EQ, 42);
   * }</pre>
   *
   * @param <T> The type of the value
   * @param logical How this where combines with the preceding where (AND, OR, NONE)
   * @param attr The metadata attribute name to constrain
   * @param comp The comparison operator
   * @param value The value to compare against
   * @return A CpoWhere
   * @throws CpoException An error occurred creating the CpoWhere
   */
  <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value) throws CpoException;

  /**
   * Creates a CpoWhere comparing the attribute to the value, optionally negated.
   *
   * @param <T> The type of the value
   * @param logical How this where combines with the preceding where (AND, OR, NONE)
   * @param attr The metadata attribute name to constrain
   * @param comp The comparison operator
   * @param value The value to compare against
   * @param not true to negate the comparison
   * @return A CpoWhere
   * @throws CpoException An error occurred creating the CpoWhere
   */
  <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value, boolean not)
      throws CpoException;

  /**
   * Starts a fluent {@link CpoWhereBuilder} chain for assembling a {@link CpoWhere} tree, including
   * nested AND/OR groups, without hand-placing {@link Logical} operators.
   *
   * <pre>{@code
   * CpoWhere where = cpo.whereBuilder()
   *     .where("id", Comparison.EQ, 42)
   *     .and(g -> g.where("dept", Comparison.EQ, "sales").or("dept", Comparison.EQ, "marketing"))
   *     .build();
   * }</pre>
   *
   * @return a new CpoWhereBuilder with no conditions yet
   * @throws CpoException An error occurred creating the underlying CpoWhere
   */
  default CpoWhereBuilder whereBuilder() throws CpoException {
    return CpoWhereBuilder.start(this);
  }

  // ==================================== ACCESSORS ====================================

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
   * Get the fetch size for the datasource
   *
   * @return The fetchsize
   */
  int getFetchSize();

  /**
   * set the fetch size for the datasource
   *
   * @param fetchSize The fetchsize to set for retrieving data
   */
  void setFetchSize(int fetchSize);

  /**
   * Get the batch size for updating the datasource
   *
   * @return The batchsize
   */
  int getBatchSize();

  /**
   * set the batch size for updating the datasource
   *
   * @param batchsize The batchsize for updating the datasource
   */
  void setBatchSize(int batchsize);

  /**
   * Gets the {@link CpoAttribute} definitions matching a comma-separated expression of attribute
   * names, as declared in the meta data for this bean's class.
   *
   * @param expression An expression defining the CpoAttributes that you want
   * @return A list of CpoAttributes
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  List<CpoAttribute> getCpoAttributes(String expression) throws CpoException;
}
