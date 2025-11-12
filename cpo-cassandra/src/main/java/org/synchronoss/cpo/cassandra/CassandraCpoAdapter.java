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

package org.synchronoss.cpo.cassandra;

import com.datastax.driver.core.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoAttribute;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.cassandra.meta.CassandraMethodMapper;
import org.synchronoss.cpo.cassandra.meta.CassandraResultSetCpoData;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.DataTypeMapEntry;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * CassandraCpoAdapter is an interface for a set of routines that are responsible for managing value objects from a
 * datasource.
 * @author dberry
 */
public class CassandraCpoAdapter extends CpoBaseAdapter<ClusterDataSource> {
    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
  private static final Logger logger = LoggerFactory.getLogger(CassandraCpoAdapter.class);
  /**
   * CpoMetaDescriptor allows you to get the meta data for a class.
   */
  private CassandraCpoMetaDescriptor metaDescriptor = null;

  private boolean invalidReadSession = false;

  private static int unknownModifyCount = -1;

  /**
   * Creates a CassandraCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiTrx        The datasoruce that identifies the transaction database.
   * @throws CpoException  An error occured
   */
  protected CassandraCpoAdapter(CassandraCpoMetaDescriptor metaDescriptor, DataSourceInfo<ClusterDataSource> jdsiTrx) throws CpoException {

    this.metaDescriptor = metaDescriptor;
    setWriteDataSource(jdsiTrx.getDataSource());
    setReadDataSource(jdsiTrx.getDataSource());
    setDataSourceName(jdsiTrx.getDataSourceName());
  }

  /**
   * Creates a CassandraCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiWrite      The datasource that identifies the transaction database for write transactions.
   * @param jdsiRead       The datasource that identifies the transaction database for read-only transactions.
   * @throws CpoException  An exception occurred
   */
  protected CassandraCpoAdapter(CassandraCpoMetaDescriptor metaDescriptor, DataSourceInfo<ClusterDataSource> jdsiWrite, DataSourceInfo<ClusterDataSource> jdsiRead) throws CpoException {
    this.metaDescriptor = metaDescriptor;
    setWriteDataSource(jdsiWrite.getDataSource());
    setReadDataSource(jdsiRead.getDataSource());
    setDataSourceName(jdsiWrite.getDataSourceName());
  }

  /**
   * Creates a CassandraCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param cdsiTrx        The datasource that identifies the transaction database for read and write transactions.
   * @throws CpoException  An exception occurred
   * @return               The CassandraCpoAdapter
   */
  public static CassandraCpoAdapter getInstance(CassandraCpoMetaDescriptor metaDescriptor, DataSourceInfo<ClusterDataSource> cdsiTrx) throws CpoException {
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
   * @param cdsiWrite      The datasource that identifies the transaction database for write transactions.
   * @param cdsiRead       The datasource that identifies the transaction database for read-only transactions.
   * @throws CpoException  An exception occurred
   * @return               The CassandraCpoAdapter
   */
  public static CassandraCpoAdapter getInstance(CassandraCpoMetaDescriptor metaDescriptor, DataSourceInfo<ClusterDataSource> cdsiWrite, DataSourceInfo<ClusterDataSource> cdsiRead) throws CpoException {
    String adapterKey = metaDescriptor + ":" + cdsiWrite.getDataSourceName() + ":" + cdsiRead.getDataSourceName();
    CassandraCpoAdapter adapter = (CassandraCpoAdapter) findCpoAdapter(adapterKey);
    if (adapter == null) {
      adapter = new CassandraCpoAdapter(metaDescriptor, cdsiWrite, cdsiRead);
      addCpoAdapter(adapterKey, adapter);
    }
    return adapter;
  }

  /**
   * <p>
   * The CpoAdapter will check to see if this object exists in the datasource.
   * </p>
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
   * }
   * </pre>
   *
   * @param name          The String name of the EXISTS Function Group that will be used to create the object in the datasource.
   *                      null signifies that the default rules will be used.
   * @param obj           This is an object that has been defined within the metadata of the datasource. If the class is not
   *                      defined an exception will be thrown. This object will be searched for inside the datasource.
   * @param wheres        A CpoWhere object that passes in run-time constraints to the function that performs the the exist
   * @return              The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException {
    Session session = null;
    long objCount = -1;

    try {
      session = getReadSession();

      objCount = existsObject(name, obj, session, wheres);
    } catch (Exception e) {
      throw new CpoException("existsObjects(String, Object) failed", e);
    }

    return objCount;
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * @param <T>           The type of object being checked
   * @param name          The name which identifies which EXISTS, INSERT, and UPDATE Function Groups to execute to persist the
   *                      object.
   * @param obj           This is an object that has been defined within the metadata of the datasource. If the class is not
   *                      defined an exception will be thrown.
   * @param session       The session with which to check if the object exists
   * @param wheres        A collection of CpoWheres used to find the T
   * @return              The int value of the first column returned in the record set
   * @throws CpoException Thrown if the Function Group has a function count != 1
   */
  protected <T> long existsObject(String name, T obj, Session session, Collection<CpoWhere> wheres) throws CpoException {
    long objCount = 0;
    Logger localLogger = logger;

    if (obj == null) {
      throw new CpoException("NULL Object passed into existsObject");
    }

    try {
      CpoClass cpoClass = metaDescriptor.getMetaClass(obj);
      List<CpoFunction> cpoFunctions = cpoClass.getFunctionGroup(CpoAdapter.EXIST_GROUP, name).getFunctions();
      localLogger = LoggerFactory.getLogger(cpoClass.getMetaClass());

      for (CpoFunction cpoFunction : cpoFunctions) {
        localLogger.info(cpoFunction.getExpression());
        CassandraBoundStatementFactory boundStatementFactory = new CassandraBoundStatementFactory(session, this, cpoClass, cpoFunction, obj, wheres, null, null);
        BoundStatement boundStatement = boundStatementFactory.getBoundStatement();

        long qCount = 0; // set the results for this function to 0

        ResultSet rs = session.execute(boundStatement);
        boundStatementFactory.release();
        ColumnDefinitions columnDefinitions = rs.getColumnDefinitions();

        // see if they are using the count(*) logic
        if (columnDefinitions.size() == 1) {
          Row next = rs.one();
          if (next!=null) {
            try {
              qCount = next.getLong(0); // get the number of objects
              // that exist
            } catch (Exception e) {
              // Exists result not an int so bail to record counter
              qCount = 1;
            }
            next = rs.one();
            if (next!=null) {
              // EXIST function has more than one record so not a count(*)
              qCount = 2;
            }
          }
        }

        for (Row row : rs) {
          qCount++;
        }

        objCount += qCount;
      }
    } catch (Exception e) {
      String msg = "existsObject(name, obj, session) failed:";
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    }

    return objCount;
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
   * @return              A Session object for reading
   * @throws CpoException An exception occurred
   */
  protected Session getReadSession() throws CpoException {
    Session session;

    try {
      if (!(invalidReadSession)) {
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
   * @return A Session object for writing
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

          DataTypeMapEntry<?> dataTypeMapEntry = metaDescriptor.getDataTypeMapEntry(columnDefs.getType(i).getName().ordinal());
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

  private ResultSet executeBatchStatements(Session session, ArrayList<CassandraBoundStatementFactory> statementFactories) throws Exception {
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

  private ResultSet executeBoundStatement(Session session, CassandraBoundStatementFactory boundStatementFactory) throws Exception {
    ResultSet resultSet;
    try {
      resultSet = session.execute(boundStatementFactory.getBoundStatement());
    } finally {
      boundStatementFactory.release();
    }
    return resultSet;
  }

  /**
   * Updates objects in the datasource
   *
   * @param <T>       The object type
   * @param obj       The object instance
   * @param groupType The query group type
   * @param groupName The query group type
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    Session sess = null;
    long updateCount = 0;

    try {
      sess = getWriteSession();
      updateCount = processUpdateGroup(obj, groupType, groupName, wheres, orderBy, nativeExpressions, sess);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      ExceptionHelper.reThrowCpoException(e, "processUpdateGroup(Object obj, String groupType, String groupName) failed");
    }

    return updateCount;
  }
  /**
   * Updates objects in the datasource
   *
   * @param <T>       The object type
   * @param obj       The object instance
   * @param groupType The query group type
   * @param groupName The query group type
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @param sess              The session to use for the updates
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Session sess) throws CpoException {
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass());
    CpoClass cpoClass;

    if (obj == null) {
      throw new CpoException("NULL Object passed into insertObject, deleteObject, updateObject, or persistObject");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(obj);
      List<CpoFunction> cpoFunctions = cpoClass.getFunctionGroup(getGroupType(obj, groupType, groupName, sess), groupName).getFunctions();
      localLogger.info("=================== Class=<" + obj.getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

      for (CpoFunction cpoFunction : cpoFunctions) {
        CassandraBoundStatementFactory boundStatementFactory = new CassandraBoundStatementFactory(sess, this, cpoClass, cpoFunction, obj, wheres, orderBy, nativeExpressions);
        executeBoundStatement(sess, boundStatementFactory);
      }
      localLogger.info("=================== " + " Updates - Class=<" + obj.getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
    } catch (Throwable t) {
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + "," + obj.getClass().getName();
      // TODO FIX THIS
      // localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return unknownModifyCount;
  }

  /**
   * Updates objects in the datasource
   *
   * @param <T>       The object type
   * @param coll      The collection of T to update
   * @param groupType The query group type
   * @param groupName The query group type
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    Session sess;
    long updateCount = 0;

    try {
      sess = getWriteSession();
      updateCount = processUpdateGroup(coll.toArray(), groupType, groupName, wheres, orderBy, nativeExpressions, sess);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      ExceptionHelper.reThrowCpoException(e, "processUpdateGroup(Collection coll, String groupType, String groupName) failed");
    }

    return updateCount;
  }

  /**
   * Updates objects in the datasource
   *
   * @param <T>       The object type
   * @param arr       The array of T to update
   * @param groupType The query group type
   * @param groupName The query group type
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @param sess       The session to use for the update
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processUpdateGroup(T[] arr, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Session sess) throws CpoException {
    CpoClass cpoClass;
    List<CpoFunction> cpoFunctions;
    CpoFunction cpoFunction;
    CassandraBoundStatementFactory boundStatementFactory = null;
    Logger localLogger = logger;

    try {
      cpoClass = metaDescriptor.getMetaClass(arr[0]);
      cpoFunctions = cpoClass.getFunctionGroup(getGroupType(arr[0], groupType, groupName, sess), groupName).getFunctions();
      localLogger = LoggerFactory.getLogger(cpoClass.getMetaClass());

      int numStatements=0;
      localLogger.info("=================== Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
      ArrayList<CassandraBoundStatementFactory> statemetnFactories = new ArrayList<>();
      for (T obj : arr) {
        for (CpoFunction function : cpoFunctions) {
          boundStatementFactory = new CassandraBoundStatementFactory(sess, this, cpoClass, function, obj, wheres, orderBy, nativeExpressions);
          statemetnFactories.add(boundStatementFactory);
          numStatements++;
        }
      }

      executeBatchStatements(sess, statemetnFactories);

      localLogger.info("=================== " + numStatements + " Updates - Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

    } catch (Throwable t) {
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + "," + arr[0].getClass().getName();
      // TODO FIX This
      // localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return unknownModifyCount;
  }

  /**
   * Executes an Object whose MetaData contains a stored procedure. An assumption is that the object exists in the
   * datasource.
   *
   * @param <T>      The result object type
   * @param <C>      The criteria object type
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
  protected <T, C> T processExecuteGroup(String name, C criteria, T result) throws CpoException {
    throw new UnsupportedOperationException("Execute Functions not supported in Cassandra");
  }


  /**
   * Retrieves the Object from the datasource.
   *
   * @param <T>               The object type
   * @param obj               This is an object that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. The input object is used to specify the search criteria.
   * @param groupName         The name which identifies which RETRIEVE Function Group to execute to retrieve the object.
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @return A populated object of the same type as the Object passed in as a argument. If no objects match the criteria
   *         a NULL will be returned.
   * @throws CpoException the retrieve function defined for this objects returns more than one row, an exception will be
   *                      thrown.
   */
  protected <T> T processSelectGroup(T obj, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    Session session = null;
    T result = null;

    try {
      session = getReadSession();
      result = processSelectGroup(obj, groupName, wheres, orderBy, nativeExpressions, session);
    } catch (Exception e) {
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(Object obj, String groupName) failed");
    }

    return result;
  }

  /**
   * Retrieves the Object from the datasource.
   *
   * @param <T>      The object type
   * @param obj               This is an object that has been defined within the metadata of the datasource. If the class is not
   *                          defined an exception will be thrown. The input object is used to specify the search criteria.
   * @param groupName         The name which identifies which RETRIEVE Function Group to execute to retrieve the object.
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @param sess               The session to use for this select
   * @return A populated object of the same type as the Object passed in as a argument. If no objects match the criteria
   *         a NULL will be returned.
   * @throws CpoException the retrieve function defined for this objects returns more than one row, an exception will be
   *                      thrown.
   */
  protected <T> T processSelectGroup(T obj, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Session sess) throws CpoException {
    T criteriaObj = obj;
    boolean recordsExist = false;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass());

    int recordCount = 0;
    int attributesSet = 0;

    T rObj = null;

    if (obj == null) {
      throw new CpoException("NULL Object passed into retrieveBean");
    }

    try {
      CpoClass cpoClass = metaDescriptor.getMetaClass(criteriaObj);
      List<CpoFunction> functions = cpoClass.getFunctionGroup(CpoAdapter.RETRIEVE_GROUP, groupName).getFunctions();

      localLogger.info("=================== Class=<" + criteriaObj.getClass() + "> Type=<" + CpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");

      try {
        rObj = (T) obj.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        localLogger.error("=================== Could not access default constructor for Class=<" + obj.getClass() + "> ==================");
        throw new CpoException("Unable to access the constructor of the Return Object", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Object", iae);
      }

      for (CpoFunction cpoFunction : functions) {

        CassandraBoundStatementFactory cbsf = new CassandraBoundStatementFactory(sess, this, cpoClass, cpoFunction, criteriaObj, wheres, orderBy, nativeExpressions);
        BoundStatement boundStatement = cbsf.getBoundStatement();

        // insertions on
        // selectgroup
        ResultSet rs = sess.execute(boundStatement);
        cbsf.release();

        ColumnDefinitions columnDefs = rs.getColumnDefinitions();

        if ((columnDefs.size() == 2) && "CPO_ATTRIBUTE".equalsIgnoreCase(columnDefs.getName(1)) && "CPO_VALUE".equalsIgnoreCase(columnDefs.getName(2))) {
          for(Row row : rs) {
            recordsExist = true;
            recordCount++;
            CassandraCpoAttribute attribute = (CassandraCpoAttribute) cpoClass.getAttributeData(row.getString(0));

            if (attribute != null) {
              attribute.invokeSetter(rObj, new CassandraResultSetCpoData(CassandraMethodMapper.getMethodMapper(), row, attribute, 1));
              attributesSet++;
            }
          }
        } else if (!rs.isExhausted()) {
          recordsExist = true;
          recordCount++;
          Row row = rs.one();
          for (int k = 0; k < columnDefs.size(); k++) {
            CassandraCpoAttribute attribute = (CassandraCpoAttribute) cpoClass.getAttributeData(columnDefs.getName(k));

            if (attribute != null) {
              attribute.invokeSetter(rObj, new CassandraResultSetCpoData(CassandraMethodMapper.getMethodMapper(), row, attribute, k));
              attributesSet++;
            }
          }

          if (rs.one() != null) {
            String msg = "ProcessSelectGroup(Object, String) failed: Multiple Records Returned";
            localLogger.error(msg);
            throw new CpoException(msg);
          }
        }
        criteriaObj = rObj;
      }

      if (!recordsExist) {
        rObj = null;
        localLogger.info("=================== 0 Records - 0 Attributes - Class=<" + criteriaObj.getClass() + "> Type=<" + CpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");
      } else {
        localLogger.info("=================== " + recordCount + " Records - " + attributesSet + " Attributes - Class=<" + criteriaObj.getClass() + ">  Type=<" + CpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");
      }
    } catch (Throwable t) {
      String msg = "ProcessSeclectGroup(Object) failed: " + ExceptionHelper.getLocalizedMessage(t);
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    }

    return rObj;
  }

  /**
   * Retrieves Objects from the datasource.
   *
   * @param <T>               The result object type
   * @param <C>               The criteria object type
   * @param name              Query group name
   * @param criteria          The criteria object
   * @param result            The result object
   * @param wheres            A collection of CpoWhere objects to be used by the function
   * @param orderBy           A collection of CpoOrderBy objects to be used by the function
   * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
   * @param useRetrieve       Use the RETRIEVE_GROUP instead of the LIST_GROUP
   * @return A List of T or an Empty List.
   * @throws CpoException Any errors retrieving the data from the datasource
   */
  protected <T, C> List<T> processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, boolean useRetrieve) throws CpoException {
    Session session = null;
    CpoArrayResultSet<T> resultSet = new CpoArrayResultSet<>();

    try {
      session = getWriteSession();
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, session, useRetrieve, resultSet);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed");
    }
    return resultSet;
  }

    /**
     * Retrieves Objects from the datasource.
     *
     * @param <T>               The result object type
     * @param <C>               The criteria object type
     * @param name              Query group name
     * @param criteria          The criteria object
     * @param result            The result object
     * @param wheres            A collection of CpoWhere objects to be used by the function
     * @param orderBy           A collection of CpoOrderBy objects to be used by the function
     * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
     * @param useRetrieve       Use the RETRIEVE_GROUP instead of the LIST_GROUP
     * @param resultSet         The result set to add the results to.
     * @throws CpoException Any errors retrieving the data from the datasource
     */
  protected <T, C> void processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, boolean useRetrieve, CpoResultSet<T> resultSet) throws CpoException {
    Session session = null;

    try {
      session = getReadSession();
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, session, useRetrieve, resultSet);
    } catch (Exception e) {
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed");
    }
  }

    /**
     * Retrieves Objects from the datasource.
     *
     * @param <T>               The result object type
     * @param <C>               The criteria object type
     * @param name              Query group name
     * @param criteria          The criteria object
     * @param result            The result object
     * @param wheres            A collection of CpoWhere objects to be used by the function
     * @param orderBy           A collection of CpoOrderBy objects to be used by the function
     * @param nativeExpressions A collection of CpoNativeFunction objects to be used by the function
     * @param sess              The session to use for this select
     * @param useRetrieve       Use the RETRIEVE_GROUP instead of the LIST_GROUP
     * @param cpoResultSet         The result set to add the results to.
     * @throws CpoException Any errors retrieving the data from the datasource
     */
   protected <T, C> void processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions,
                                            Session sess, boolean useRetrieve, CpoResultSet<T> cpoResultSet) throws CpoException {
     Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass());
     CassandraBoundStatementFactory boundStatementFactory=null;
     List<CpoFunction> cpoFunctions;
     CpoClass criteriaClass;
     CpoClass resultClass;

     ColumnDefinitions columnDefs;
     int columnCount;
     T obj;
     CpoAttribute[] attributes;

     if (criteria == null || result == null) {
       throw new CpoException("NULL Object passed into retrieveBean or retrieveBeans");
     }

     try {
       criteriaClass = metaDescriptor.getMetaClass(criteria);
       resultClass = metaDescriptor.getMetaClass(result);
       if (useRetrieve) {
         localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + CpoAdapter.RETRIEVE_GROUP + "> Name=<" + name + "> =========================");
         cpoFunctions = criteriaClass.getFunctionGroup(CpoAdapter.RETRIEVE_GROUP, name).getFunctions();
       } else {
         localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + CpoAdapter.LIST_GROUP + "> Name=<" + name + "> =========================");
         cpoFunctions = criteriaClass.getFunctionGroup(CpoAdapter.LIST_GROUP, name).getFunctions();
       }

       for (CpoFunction cpoFunction : cpoFunctions) {
         boundStatementFactory = new CassandraBoundStatementFactory(sess, this, criteriaClass, cpoFunction, criteria, wheres, orderBy, nativeExpressions);
         BoundStatement boundStatement = boundStatementFactory.getBoundStatement();

         if (cpoResultSet.getFetchSize() != -1) {
           boundStatement.setFetchSize(cpoResultSet.getFetchSize());
         }

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

         for(Row row : rs) {
           try {
             obj = (T) result.getClass().newInstance();
           } catch (IllegalAccessException iae) {
             localLogger.error("=================== Could not access default constructor for Class=<" + result.getClass() + "> ==================");
             throw new CpoException("Unable to access the constructor of the Return Object", iae);
           } catch (InstantiationException iae) {
             throw new CpoException("Unable to instantiate Return Object", iae);
           }

           for (int k = 0; k < columnCount; k++) {
             if (attributes[k] != null) {
               attributes[k].invokeSetter(obj, new CassandraResultSetCpoData(CassandraMethodMapper.getMethodMapper(), row, attributes[k], k));
             }
           }

           try {
             cpoResultSet.put(obj);
           } catch (InterruptedException e) {
             localLogger.error("Retriever Thread was interrupted", e);
             break;
           }
         }

         localLogger.info("=================== " + cpoResultSet.size() + " Records - Class=<" + criteria.getClass() + "> Type=<" + CpoAdapter.LIST_GROUP + "> Name=<" + name + "> Result=<" + result.getClass() + "> ====================");
       }
     } catch (Throwable t) {
       String msg = "ProcessSelectGroup(String name, Object criteria, Object result, CpoWhere where, Collection orderBy, Session sess) failed. Error:";
       localLogger.error(msg, t);
       throw new CpoException(msg, t);
     } finally {
       if (boundStatementFactory!=null)
         boundStatementFactory.release();
     }
   }

  /**
   * Validates the type of query being performed. If it is a Persist Group,
   * it checks the database to see if this is an update or an insert, and returns the query group. Otherwise, it sends
   * back the original query group. Upserts only work for single objects.
   *
   * @param <T>  The type of the object
   * @param obj  The obj to insert or update
   * @param type The group type
   * @param name The group name
   * @param session The session to use
   * @return The selected group name
   * @throws CpoException An exception occurred
   */
  protected <T> String getGroupType(T obj, String type, String name, Session session) throws CpoException {
    String retType = type;
    long objCount;

    if (CpoAdapter.PERSIST_GROUP.equals(retType)) {
      objCount = existsObject(name, obj, session, null);

      if (objCount == 0) {
        retType = CpoAdapter.CREATE_GROUP;
      } else if (objCount == 1) {
        retType = CpoAdapter.UPDATE_GROUP;
      } else {
        throw new CpoException("Persist can only UPDATE one record. Your EXISTS function returned 2 or more.");
      }
    }

    return retType;
  }


}
