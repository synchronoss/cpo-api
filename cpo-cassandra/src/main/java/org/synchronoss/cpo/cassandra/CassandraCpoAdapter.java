package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import com.datastax.driver.core.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoAttribute;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.cassandra.meta.CassandraMethodMapper;
import org.synchronoss.cpo.cassandra.meta.CassandraResultSetCpoData;
import org.synchronoss.cpo.enums.Crud;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.DataTypeMapEntry;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

/**
 * CassandraCpoAdapter is an interface for a set of routines that are responsible for managing value
 * beans from a datasource.
 *
 * @author dberry
 */
public class CassandraCpoAdapter extends CpoBaseAdapter<ClusterDataSource> {
  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CassandraCpoAdapter.class);

  /** CpoMetaDescriptor allows you to get the metadata for a class. */
  private CassandraCpoMetaDescriptor metaDescriptor = null;

  private boolean invalidReadSession = false;

  private static int unknownModifyCount = -1;

  /**
   * Creates a CassandraCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiTrx The datasoruce that identifies the transaction database.
   * @throws CpoException An error occured
   */
  protected CassandraCpoAdapter(
      CassandraCpoMetaDescriptor metaDescriptor, DataSourceInfo<ClusterDataSource> jdsiTrx)
      throws CpoException {
    super(jdsiTrx.getDataSourceName(), jdsiTrx.getFetchSize(), jdsiTrx.getBatchSize());
    this.metaDescriptor = metaDescriptor;
    setWriteDataSource(jdsiTrx.getDataSource());
    setReadDataSource(jdsiTrx.getDataSource());
  }

  /**
   * Creates a CassandraCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiWrite The datasource that identifies the transaction database for write
   *     transactions.
   * @param jdsiRead The datasource that identifies the transaction database for read-only
   *     transactions.
   * @throws CpoException An exception occurred
   */
  protected CassandraCpoAdapter(
      CassandraCpoMetaDescriptor metaDescriptor,
      DataSourceInfo<ClusterDataSource> jdsiWrite,
      DataSourceInfo<ClusterDataSource> jdsiRead)
      throws CpoException {
    super(jdsiWrite.getDataSourceName(), jdsiWrite.getFetchSize(), jdsiWrite.getBatchSize());
    this.metaDescriptor = metaDescriptor;
    setWriteDataSource(jdsiWrite.getDataSource());
    setReadDataSource(jdsiRead.getDataSource());
  }

  /**
   * Creates a CassandraCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param cdsiTrx The datasource that identifies the transaction database for read and write
   *     transactions.
   * @throws CpoException An exception occurred
   * @return The CassandraCpoAdapter
   */
  public static CassandraCpoAdapter getInstance(
      CassandraCpoMetaDescriptor metaDescriptor, DataSourceInfo<ClusterDataSource> cdsiTrx)
      throws CpoException {
    String adapterKey = metaDescriptor + ":" + cdsiTrx.getDataSourceName();
    CassandraCpoAdapter adapter = (CassandraCpoAdapter) findCpoAdapter(adapterKey);
    if (adapter == null) {
      adapter = new CassandraCpoAdapter(metaDescriptor, cdsiTrx);
      addCpoAdapter(adapterKey, adapter);
    }
    return adapter;
  }

  /**
   * Creates a CassandraCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param cdsiWrite The datasource that identifies the transaction database for write
   *     transactions.
   * @param cdsiRead The datasource that identifies the transaction database for read-only
   *     transactions.
   * @throws CpoException An exception occurred
   * @return The CassandraCpoAdapter
   */
  public static CassandraCpoAdapter getInstance(
      CassandraCpoMetaDescriptor metaDescriptor,
      DataSourceInfo<ClusterDataSource> cdsiWrite,
      DataSourceInfo<ClusterDataSource> cdsiRead)
      throws CpoException {
    String adapterKey =
        metaDescriptor + ":" + cdsiWrite.getDataSourceName() + ":" + cdsiRead.getDataSourceName();
    CassandraCpoAdapter adapter = (CassandraCpoAdapter) findCpoAdapter(adapterKey);
    if (adapter == null) {
      adapter = new CassandraCpoAdapter(metaDescriptor, cdsiWrite, cdsiRead);
      addCpoAdapter(adapterKey, adapter);
    }
    return adapter;
  }

  @Override
  public <T> long existsBean(String groupName, T bean, Collection<CpoWhere> wheres)
      throws CpoException {
    Session session = null;
    long objCount = -1;

    try {
      session = getReadSession();

      objCount = existsBean(groupName, bean, session, wheres);
    } catch (Exception e) {
      throw new CpoException("existsBeans(String, T) failed", e);
    }

    return objCount;
  }

  /**
   * The CpoAdapter will check to see if this bean exists in the datasource.
   *
   * @param <T> The type of bean being checked
   * @param groupName The groupName which identifies which EXISTS, INSERT, and UPDATE Function
   *     Groups to execute to upsert the bean.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @param session The session with which to check if the bean exists
   * @param wheres A collection of CpoWheres used to find the T
   * @return The int value of the first column returned in the record set
   * @throws CpoException Thrown if the Function Group has a function count != 1
   */
  protected <T> long existsBean(
      String groupName, T bean, Session session, Collection<CpoWhere> wheres) throws CpoException {
    long count = 0;
    Logger localLogger = logger;

    if (bean == null) {
      throw new CpoException("NULL bean passed into existsBean");
    }

    try {
      CpoClass cpoClass = metaDescriptor.getMetaClass(bean);
      List<CpoFunction> cpoFunctions =
          cpoClass.getFunctionGroup(Crud.EXIST, groupName).getFunctions();
      localLogger = LoggerFactory.getLogger(cpoClass.getMetaClass());

      for (CpoFunction cpoFunction : cpoFunctions) {
        localLogger.info(cpoFunction.getExpression());
        CassandraBoundStatementFactory boundStatementFactory =
            new CassandraBoundStatementFactory(
                session, this, cpoClass, cpoFunction, bean, wheres, null, null);
        BoundStatement boundStatement = boundStatementFactory.getBoundStatement();

        long qCount = 0; // set the results for this function to 0

        ResultSet rs = session.execute(boundStatement);
        boundStatementFactory.release();
        ColumnDefinitions columnDefinitions = rs.getColumnDefinitions();

        // see if they are using the count(*) logic
        if (columnDefinitions.size() == 1) {
          Row next = rs.one();
          if (next != null) {
            try {
              qCount = next.getLong(0); // get the number of beans
              // that exist
            } catch (Exception e) {
              // Exists result not an int so bail to record counter
              qCount = 1;
            }
            next = rs.one();
            if (next != null) {
              // EXIST function has more than one record so not a count(*)
              qCount = 2;
            }
          }
        }

        qCount += rs.all().size();

        count += qCount;
      }
    } catch (Exception e) {
      String msg = "existsBean(groupName, bean, session) failed:";
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    }

    return count;
  }

  /**
   * getCpoMetaDescriptor returns the CpoMetaDescriptor associated with this CpoAdapter
   *
   * @return The CpoMetaDescriptor
   */
  @Override
  public CpoMetaDescriptor getCpoMetaDescriptor() {
    return metaDescriptor;
  }

  /**
   * getReadSession returns the read session for Cassandra
   *
   * @return A Session bean for reading
   * @throws CpoException An exception occurred
   */
  protected Session getReadSession() throws CpoException {
    Session session;

    try {
      if (!invalidReadSession) {
        session = getReadDataSource().getSession();
      } else {
        session = getWriteDataSource().getSession();
      }
    } catch (Exception e) {
      invalidReadSession = true;

      String msg = "getReadConnection(): failed";
      logger.error(msg, e);

      session = getWriteSession();
    }

    return session;
  }

  /**
   * getWriteSession returns the write session for Cassandra
   *
   * @return A Session bean for writing
   * @throws CpoException An exception occurred
   */
  protected Session getWriteSession() throws CpoException {
    Session session;

    try {
      session = getWriteDataSource().getSession();
    } catch (Throwable t) {
      String msg = "getWriteConnection(): failed";
      logger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return session;
  }

  /**
   * Get the cpo attributes for this expression
   *
   * @param expression A string expression
   * @return A List of CpoAttribute
   * @throws CpoException An exception occurred
   */
  @Override
  public List<CpoAttribute> getCpoAttributes(String expression) throws CpoException {
    List<CpoAttribute> attributes = new ArrayList<>();

    if (expression != null && !expression.isEmpty()) {
      Session session;
      ResultSet rs;
      try {
        session = getWriteSession();
        rs = session.execute(expression);
        ColumnDefinitions columnDefs = rs.getColumnDefinitions();
        for (int i = 0; i < columnDefs.size(); i++) {
          CpoAttribute attribute = new CassandraCpoAttribute();
          attribute.setDataName(columnDefs.getName(i));

          DataTypeMapEntry<?> dataTypeMapEntry =
              metaDescriptor.getDataTypeMapEntry(columnDefs.getType(i).getName().ordinal());
          attribute.setDataType(dataTypeMapEntry.getDataTypeName());
          attribute.setDataTypeInt(dataTypeMapEntry.getDataTypeInt());
          attribute.setJavaType(dataTypeMapEntry.getJavaClass().getName());
          attribute.setJavaName(dataTypeMapEntry.makeJavaName(columnDefs.getName(i)));

          attributes.add(attribute);
        }
      } catch (Throwable t) {
        logger.error(ExceptionHelper.getLocalizedMessage(t), t);
        throw new CpoException("Error Generating Attributes", t);
      }
    }
    return attributes;
  }

  private ResultSet executeBatchStatements(
      Session session, ArrayList<CassandraBoundStatementFactory> statementFactories)
      throws Exception {
    ResultSet resultSet;

    ArrayList<BoundStatement> boundStatements = new ArrayList<>(statementFactories.size());

    for (CassandraBoundStatementFactory factory : statementFactories) {
      boundStatements.add(factory.getBoundStatement());
    }

    try {
      BatchStatement batchStatement = new BatchStatement();
      batchStatement.addAll(boundStatements);
      resultSet = session.execute(batchStatement);
    } finally {
      for (CassandraBoundStatementFactory factory : statementFactories) {
        factory.release();
      }
    }
    return resultSet;
  }

  private ResultSet executeBoundStatement(
      Session session, CassandraBoundStatementFactory boundStatementFactory) throws Exception {
    ResultSet resultSet;
    try {
      resultSet = session.execute(boundStatementFactory.getBoundStatement());
    } finally {
      boundStatementFactory.release();
    }
    return resultSet;
  }

  @Override
  protected <T> long processUpdateGroup(
      T bean,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    Session sess = null;
    long updateCount = 0;

    try {
      sess = getWriteSession();
      updateCount =
          processUpdateGroup(bean, crud, groupName, wheres, orderBy, nativeExpressions, sess);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      ExceptionHelper.reThrowCpoException(
          e, "processUpdateGroup(T bean, Crud crud, String groupName) failed");
    }

    return updateCount;
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
   * @param sess The session to use for the updates
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processUpdateGroup(
      T bean,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      Session sess)
      throws CpoException {
    Logger localLogger = bean == null ? logger : LoggerFactory.getLogger(bean.getClass());
    CpoClass cpoClass;

    if (bean == null) {
      throw new CpoException(
          "NULL bean passed into insertBean, deleteBean, updateBean, or upsertBean");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(bean);
      List<CpoFunction> cpoFunctions =
          cpoClass
              .getFunctionGroup(adjustCrud(bean, crud, groupName, sess), groupName)
              .getFunctions();
      localLogger.info(buildCpoClassLogLine(bean.getClass(), crud, groupName));

      for (CpoFunction cpoFunction : cpoFunctions) {
        CassandraBoundStatementFactory boundStatementFactory =
            new CassandraBoundStatementFactory(
                sess, this, cpoClass, cpoFunction, bean, wheres, orderBy, nativeExpressions);
        executeBoundStatement(sess, boundStatementFactory);
      }
      localLogger.info(buildExecutedLogLine(bean.getClass(), crud, groupName));
    } catch (Throwable t) {
      String msg =
          "ProcessUpdateGroup failed:"
              + crud.operation
              + ","
              + groupName
              + ","
              + bean.getClass().getName();
      // TODO FIX THIS
      // localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return unknownModifyCount;
  }

  @Override
  protected <T> long processUpdateGroup(
      List<T> beans,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    Session sess;
    long updateCount = 0;

    try {
      sess = getWriteSession();
      updateCount =
          processUpdateGroup(beans, crud, groupName, wheres, orderBy, nativeExpressions, sess);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      ExceptionHelper.reThrowCpoException(
          e, "processUpdateGroup(Collection beans, Crud crud, String groupName) failed");
    }

    return updateCount;
  }

  /**
   * Updates beans in the datasource
   *
   * @param <T> The bean type
   * @param beans The array of T to update
   * @param crud The query group type
   * @param groupName The query group type
   * @param wheres A collection of CpoWhere beans to be used by the function
   * @param orderBy A collection of CpoOrderBy beans to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction beans to be used by the function
   * @param sess The session to use for the update
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processUpdateGroup(
      List<T> beans,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      Session sess)
      throws CpoException {
    CpoClass cpoClass;
    List<CpoFunction> cpoFunctions;
    CassandraBoundStatementFactory boundStatementFactory = null;
    Logger localLogger = logger;

    if (beans.isEmpty()) return 0;
    var beanInstance = beans.getFirst();

    try {
      cpoClass = metaDescriptor.getMetaClass(beanInstance);
      cpoFunctions =
          cpoClass
              .getFunctionGroup(adjustCrud(beanInstance, crud, groupName, sess), groupName)
              .getFunctions();
      localLogger = LoggerFactory.getLogger(cpoClass.getMetaClass());

      int numStatements = 0;
      localLogger.info(buildCpoClassLogLine(beanInstance.getClass(), crud, groupName));
      ArrayList<CassandraBoundStatementFactory> statemetnFactories = new ArrayList<>();
      for (T bean : beans) {
        for (CpoFunction function : cpoFunctions) {
          boundStatementFactory =
              new CassandraBoundStatementFactory(
                  sess, this, cpoClass, function, bean, wheres, orderBy, nativeExpressions);
          statemetnFactories.add(boundStatementFactory);
          numStatements++;
        }
      }

      executeBatchStatements(sess, statemetnFactories);

      localLogger.info(
          buildUpdatesLogLine(numStatements, beanInstance.getClass(), crud, groupName));

    } catch (Throwable t) {
      String msg =
          "ProcessUpdateGroup failed:"
              + crud.operation
              + ","
              + groupName
              + ","
              + beanInstance.getClass().getName();
      // TODO FIX This
      // localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return unknownModifyCount;
  }

  @Override
  protected <T, C> T processExecuteGroup(String groupName, C criteria, T result)
      throws CpoException {
    throw new UnsupportedOperationException("Execute Functions not supported in Cassandra");
  }

  @Override
  protected <T> T processSelectGroup(
      T bean,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    Session session = null;
    T result = null;

    try {
      session = getReadSession();
      result = processSelectGroup(bean, groupName, wheres, orderBy, nativeExpressions, session);
    } catch (Exception e) {
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(T bean, String groupName) failed");
    }

    return result;
  }

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
   * @param sess The session to use for this select
   * @return A populated bean of the same type as the bean passed in as a argument. If no beans
   *     match the criteria a NULL will be returned.
   * @throws CpoException the retrieve function defined for this beans returns more than one row, an
   *     exception will be thrown.
   */
  protected <T> T processSelectGroup(
      T bean,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      Session sess)
      throws CpoException {
    T criteriaObj = bean;
    boolean recordsExist = false;
    Logger localLogger = bean == null ? logger : LoggerFactory.getLogger(bean.getClass());

    int recordCount = 0;
    int attributesSet = 0;

    T rObj = null;

    if (bean == null) {
      throw new CpoException("NULL bean passed into retrieveBean");
    }

    try {
      CpoClass cpoClass = metaDescriptor.getMetaClass(criteriaObj);
      List<CpoFunction> functions =
          cpoClass.getFunctionGroup(Crud.RETRIEVE, groupName).getFunctions();

      localLogger.info(buildCpoClassLogLine(criteriaObj.getClass(), Crud.RETRIEVE, groupName));

      try {
        rObj = (T) bean.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        localLogger.error(
            "=================== Could not access default constructor for Class=<"
                + bean.getClass()
                + "> ==================");
        throw new CpoException("Unable to access the constructor of the Return bean", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return bean", iae);
      }

      for (CpoFunction cpoFunction : functions) {

        CassandraBoundStatementFactory cbsf =
            new CassandraBoundStatementFactory(
                sess, this, cpoClass, cpoFunction, criteriaObj, wheres, orderBy, nativeExpressions);
        BoundStatement boundStatement = cbsf.getBoundStatement();

        // insertions on
        // selectgroup
        ResultSet rs = sess.execute(boundStatement);
        cbsf.release();

        ColumnDefinitions columnDefs = rs.getColumnDefinitions();

        if ((columnDefs.size() == 2)
            && "CPO_ATTRIBUTE".equalsIgnoreCase(columnDefs.getName(1))
            && "CPO_VALUE".equalsIgnoreCase(columnDefs.getName(2))) {
          for (Row row : rs) {
            recordsExist = true;
            recordCount++;
            CassandraCpoAttribute attribute =
                (CassandraCpoAttribute) cpoClass.getAttributeData(row.getString(0));

            if (attribute != null) {
              attribute.invokeSetter(
                  rObj,
                  new CassandraResultSetCpoData(
                      CassandraMethodMapper.getMethodMapper(), row, attribute, 1));
              attributesSet++;
            }
          }
        } else if (!rs.isExhausted()) {
          recordsExist = true;
          recordCount++;
          Row row = rs.one();
          for (int k = 0; k < columnDefs.size(); k++) {
            CassandraCpoAttribute attribute =
                (CassandraCpoAttribute) cpoClass.getAttributeData(columnDefs.getName(k));

            if (attribute != null) {
              attribute.invokeSetter(
                  rObj,
                  new CassandraResultSetCpoData(
                      CassandraMethodMapper.getMethodMapper(), row, attribute, k));
              attributesSet++;
            }
          }

          if (rs.one() != null) {
            String msg = "processSelectGroup(T, String) failed: Multiple Records Returned";
            localLogger.error(msg);
            throw new CpoException(msg);
          }
        }
        criteriaObj = rObj;
      }

      if (!recordsExist) {
        rObj = null;
        localLogger.info(
            buildRecordsLogLine(0, 0, criteriaObj.getClass(), Crud.RETRIEVE, groupName));
      } else {
        localLogger.info(
            buildRecordsLogLine(
                recordCount, attributesSet, criteriaObj.getClass(), Crud.RETRIEVE, groupName));
      }
    } catch (Throwable t) {
      String msg = "processSelectGroup(T) failed: " + ExceptionHelper.getLocalizedMessage(t);
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return rObj;
  }

  @Override
  protected <T, C> Stream<T> processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      boolean useRetrieve)
      throws CpoException {
    Session session = null;

    try {
      session = getReadSession();
      return processSelectGroup(
          groupName, criteria, result, wheres, orderBy, nativeExpressions, session, useRetrieve);
    } catch (Exception e) {
      ExceptionHelper.reThrowCpoException(
          e,
          "processSelectGroup(String groupName, C criteria, T result,CpoWhere where,"
              + " Collection orderBy, boolean useRetrieve) failed");
    }
    return Stream.empty();
  }

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
   * @param sess The session to use for this select
   * @param useRetrieve Use the RETRIEVE_GROUP instead of the LIST_GROUP
   * @return A stream of T
   * @throws CpoException Any errors retrieving the data from the datasource
   */
  protected <T, C> Stream<T> processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      Session sess,
      boolean useRetrieve)
      throws CpoException {
    Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass());
    CassandraBoundStatementFactory boundStatementFactory = null;
    List<CpoFunction> cpoFunctions;
    CpoClass criteriaClass;
    CpoClass resultClass;

    ColumnDefinitions columnDefs;
    int columnCount;
    CpoAttribute[] attributes;

    if (criteria == null || result == null) {
      throw new CpoException("NULL bean passed into retrieveBean or retrieveBeans");
    }

    try {
      criteriaClass = metaDescriptor.getMetaClass(criteria);
      resultClass = metaDescriptor.getMetaClass(result);
      if (useRetrieve) {
        localLogger.info(buildCpoClassLogLine(criteria.getClass(), Crud.RETRIEVE, groupName));
        cpoFunctions = criteriaClass.getFunctionGroup(Crud.RETRIEVE, groupName).getFunctions();
      } else {
        localLogger.info(buildCpoClassLogLine(criteria.getClass(), Crud.LIST, groupName));
        cpoFunctions = criteriaClass.getFunctionGroup(Crud.LIST, groupName).getFunctions();
      }

      CpoFunction cpoFunction = cpoFunctions.getFirst();
      boundStatementFactory =
          new CassandraBoundStatementFactory(
              sess, this, criteriaClass, cpoFunction, criteria, wheres, orderBy, nativeExpressions);
      BoundStatement boundStatement = boundStatementFactory.getBoundStatement();

      localLogger.debug("Retrieving Records");

      ResultSet rs = sess.execute(boundStatement);
      boundStatementFactory.release();

      localLogger.debug("Processing Records");

      columnDefs = rs.getColumnDefinitions();

      columnCount = columnDefs.size();

      attributes = new CpoAttribute[columnCount];

      for (int k = 0; k < columnCount; k++) {
        attributes[k] = resultClass.getAttributeData(columnDefs.getName(k));
      }

      CassandraBoundStatementFactory finalBoundStatementFactory = boundStatementFactory;
      return StreamSupport.stream(
              new Spliterators.AbstractSpliterator<T>(Long.MAX_VALUE, Spliterator.ORDERED) {
                @Override
                public boolean tryAdvance(Consumer<? super T> action) {
                  try {
                    if (rs.isExhausted()) return false;
                    Row row = rs.one();
                    T bean = null;
                    try {
                      bean = (T) result.getClass().newInstance();
                    } catch (IllegalAccessException iae) {
                      localLogger.error(
                          "=================== Could not access default constructor for Class=<"
                              + result.getClass()
                              + "> ==================");
                      throw new CpoException(
                          "Unable to access the constructor of the Return bean", iae);
                    } catch (InstantiationException iae) {
                      throw new CpoException("Unable to instantiate Return bean", iae);
                    }

                    for (int k = 0; k < columnCount; k++) {
                      if (attributes[k] != null) {
                        attributes[k].invokeSetter(
                            bean,
                            new CassandraResultSetCpoData(
                                CassandraMethodMapper.getMethodMapper(), row, attributes[k], k));
                      }
                    }
                    action.accept(bean);
                    return true;
                  } catch (CpoException ex) {
                    throw new RuntimeException(ex);
                  }
                }
              },
              false)
          .onClose(
              () -> {
                try {
                  finalBoundStatementFactory.release();
                } catch (CpoException e) {
                  throw new RuntimeException(e);
                }
              });
    } catch (Throwable t) {
      if (boundStatementFactory != null) boundStatementFactory.release();
      String msg =
          "processSelectGroup(String groupName, C criteria, T result, CpoWhere where,"
              + " Collection orderBy, Session sess) failed. Error:";
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    }
  }

  /**
   * Validates the crud of query being performed. If it is a UPSERT Group, it checks the database to
   * see if this is an update or an insert, and returns the query group. Otherwise, it sends back
   * the original query group. Upserts only work for single beans.
   *
   * @param <T> The crud of the bean
   * @param bean The bean to insert or update
   * @param crud The group crud
   * @param groupName The group groupName
   * @param session The session to use
   * @return The selected group groupName
   * @throws CpoException An exception occurred
   */
  protected <T> Crud adjustCrud(T bean, Crud crud, String groupName, Session session)
      throws CpoException {
    Crud retType = crud;
    long objCount;

    if (Crud.UPSERT == retType) {
      objCount = existsBean(groupName, bean, session, null);

      if (objCount == 0) {
        retType = Crud.CREATE;
      } else if (objCount == 1) {
        retType = Crud.UPDATE;
      } else {
        throw new CpoException(
            "UPSERT can only UPDATE one record. Your EXISTS function returned 2 or more.");
      }
    }

    return retType;
  }
}
