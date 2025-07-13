/*
 * Copyright (C) 2003-2012 David E. Berry
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
package org.synchronoss.cpo.jdbc;

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;
import org.synchronoss.cpo.jdbc.meta.JdbcResultSetCpoData;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.DataTypeMapEntry;
import org.synchronoss.cpo.meta.domain.*;

import javax.naming.*;
import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

/**
 * JdbcCpoAdapter is an interface for a set of routines that are responsible for managing value objects from a
 * datasource.
 *
 * @author david berry
 */
public class JdbcCpoAdapter extends CpoBaseAdapter<DataSource> {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  /**
   * DOCUMENT ME!
   */
  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoAdapter.class);

  /**
   * DOCUMENT ME!
   */
  private Context context_ = null;

  /**
   * DOCUMENT ME!
   */
  private boolean invalidReadConnection_ = false;
  private boolean batchUpdatesSupported_ = false;

  /**
   * CpoMetaDescriptor allows you to get the meta data for a class.
   */
  private JdbcCpoMetaDescriptor metaDescriptor = null;

  protected JdbcCpoAdapter() {
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiTrx        The datasoruce that identifies the transaction database.
   * @throws org.synchronoss.cpo.CpoException
   *          exception
   */
  protected JdbcCpoAdapter(JdbcCpoMetaDescriptor metaDescriptor, DataSourceInfo<DataSource> jdsiTrx) throws CpoException {

    this.metaDescriptor = metaDescriptor;
    setWriteDataSource(jdsiTrx.getDataSource());
    setReadDataSource(jdsiTrx.getDataSource());
    setDataSourceName(jdsiTrx.getDataSourceName());
    processDatabaseMetaData();
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiWrite      The datasource that identifies the transaction database for write transactions.
   * @param jdsiRead       The datasource that identifies the transaction database for read-only transactions.
   * @throws org.synchronoss.cpo.CpoException
   *          exception
   */
  protected JdbcCpoAdapter(JdbcCpoMetaDescriptor metaDescriptor, DataSourceInfo<DataSource> jdsiWrite, DataSourceInfo<DataSource> jdsiRead) throws CpoException {
    this.metaDescriptor = metaDescriptor;
    setWriteDataSource(jdsiWrite.getDataSource());
    setReadDataSource(jdsiRead.getDataSource());
    setDataSourceName(jdsiWrite.getDataSourceName());
    processDatabaseMetaData();
  }

  /**
   * This constructor is used specifically to clone a JdbcCpoAdapter.
   *
   * @param jdbcCpoAdapter - the JdbcCpoAdapter to clone
   * @throws CpoException
   */
  protected JdbcCpoAdapter(JdbcCpoAdapter jdbcCpoAdapter) throws CpoException {
    this.metaDescriptor = (JdbcCpoMetaDescriptor) jdbcCpoAdapter.getCpoMetaDescriptor();
    batchUpdatesSupported_ = jdbcCpoAdapter.isBatchUpdatesSupported();
    setWriteDataSource(jdbcCpoAdapter.getWriteDataSource());
    setReadDataSource(jdbcCpoAdapter.getReadDataSource());
    setDataSourceName(jdbcCpoAdapter.getDataSourceName());
  }

  protected boolean isBatchUpdatesSupported() {
    return batchUpdatesSupported_;
  }
  private void processDatabaseMetaData() throws CpoException {
    Connection c = null;
    try {
      c = getReadConnection();
      DatabaseMetaData dmd = c.getMetaData();

      // do all the tests here
      batchUpdatesSupported_ = dmd.supportsBatchUpdates();

//      this.closeLocalConnection(c);
    } catch (Throwable t) {
      logger.error(ExceptionHelper.getLocalizedMessage(t), t);
      throw new CpoException("Could Not Retrieve Database Meta Data", t);
    } finally {
      closeLocalConnection(c);
    }
  }

  public static JdbcCpoAdapter getInstance(JdbcCpoMetaDescriptor metaDescriptor, DataSourceInfo<DataSource> jdsiTrx) throws CpoException {
    String adapterKey = metaDescriptor + ":" + jdsiTrx.getDataSourceName();
    JdbcCpoAdapter adapter = (JdbcCpoAdapter) findCpoAdapter(adapterKey);
    if (adapter == null) {
      adapter = new JdbcCpoAdapter(metaDescriptor, jdsiTrx);
      addCpoAdapter(adapterKey, adapter);
    }
    return adapter;
  }

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiWrite      The datasource that identifies the transaction database for write transactions.
   * @param jdsiRead       The datasource that identifies the transaction database for read-only transactions.
   * @throws org.synchronoss.cpo.CpoException
   *          exception
   */
  public static JdbcCpoAdapter getInstance(JdbcCpoMetaDescriptor metaDescriptor, DataSourceInfo<DataSource> jdsiWrite, DataSourceInfo<DataSource> jdsiRead) throws CpoException {
    String adapterKey = metaDescriptor + ":" + jdsiWrite.getDataSourceName() + ":" + jdsiRead.getDataSourceName();
    JdbcCpoAdapter adapter = (JdbcCpoAdapter) findCpoAdapter(adapterKey);
    if (adapter == null) {
      adapter = new JdbcCpoAdapter(metaDescriptor, jdsiWrite, jdsiRead);
      addCpoAdapter(adapterKey, adapter);
    }
    return adapter;
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
   * @param wheres A CpoWhere object that passes in run-time constraints to the function that performs the the exist
   * @return The number of objects that exist in the datasource that match the specified object
   * @throws CpoException Thrown if there are errors accessing the datasource
   */
  @Override
  public <T> long existsObject(String name, T obj, Collection<CpoWhere> wheres) throws CpoException {
    Connection c = null;
    long objCount = -1;

    try {
      c = getReadConnection();

      objCount = existsObject(name, obj, c, wheres);
    } catch (Exception e) {
      throw new CpoException("existsObjects(String, Object) failed", e);
    } finally {
      closeLocalConnection(c);
    }

    return objCount;
  }

  /**
   * The CpoAdapter will check to see if this object exists in the datasource.
   *
   * @param name The name which identifies which EXISTS, INSERT, and UPDATE Function Groups to execute to persist the
   *             object.
   * @param obj  This is an object that has been defined within the metadata of the datasource. If the class is not
   *             defined an exception will be thrown.
   * @param con  The datasource Connection with which to check if the object exists
   * @return The int value of the first column returned in the record set
   * @throws CpoException exception will be thrown if the Function Group has a function count != 1
   */
  protected <T> long existsObject(String name, T obj, Connection con, Collection<CpoWhere> wheres) throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    CpoClass cpoClass;
    long objCount = 0;
    Logger localLogger = logger;

    if (obj == null) {
      throw new CpoException("NULL Object passed into existsObject");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(obj);
      List<CpoFunction> cpoFunctions = cpoClass.getFunctionGroup(JdbcCpoAdapter.EXIST_GROUP, name).getFunctions();
      localLogger = LoggerFactory.getLogger(cpoClass.getMetaClass());

      for (CpoFunction cpoFunction : cpoFunctions) {
        localLogger.info(cpoFunction.getExpression());
        JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, cpoClass, cpoFunction, obj, wheres, null, null);
        ps = jpsf.getPreparedStatement();

        long qCount = 0; // set the results for this function to 0

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
              // EXIST function has more than one record so not a count(*)
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
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    } finally {
      resultSetClose(rs);
      statementClose(ps);
    }

    return objCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  @Override
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
  @Override
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value) throws CpoException {
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
  @Override
  public <T> CpoWhere newWhere(int logical, String attr, int comp, T value, boolean not) throws CpoException {
    return new JdbcCpoWhere(logical, attr, comp, value, not);
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
   * @param obj  DOCUMENT ME!
   * @param type DOCUMENT ME!
   * @param name DOCUMENT ME!
   * @param c    DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> String getGroupType(T obj, String type, String name, Connection c) throws CpoException {
    String retType = type;
    long objCount;

    if (JdbcCpoAdapter.PERSIST_GROUP.equals(retType)) {
      objCount = existsObject(name, obj, c, null);

      if (objCount == 0) {
        retType = JdbcCpoAdapter.CREATE_GROUP;
      } else if (objCount == 1) {
        retType = JdbcCpoAdapter.UPDATE_GROUP;
      } else {
        throw new CpoException("Persist can only UPDATE one record. Your EXISTS function returned 2 or more.");
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
    Connection connection;

    try {
      if (!(invalidReadConnection_)) {
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

    return connection;
  }

  /**
   * DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected Connection getWriteConnection() throws CpoException {
    Connection connection;

    try {
      connection = getWriteDataSource().getConnection();
      connection.setAutoCommit(false);
    } catch (SQLException e) {
      String msg = "getWriteConnection(): failed";
      logger.error(msg, e);
      throw new CpoException(msg, e);
    }

    return connection;
  }

  /**
   * DOCUMENT ME!
   *
   * @param connection DOCUMENT ME!
   */
  protected void closeLocalConnection(Connection connection) {
    try {
      if (connection != null && !connection.isClosed()) {
        connection.close();
      }
    } catch (SQLException e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getMessage());
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param connection DOCUMENT ME!
   */
  protected void commitLocalConnection(Connection connection) {
    try {
      if (connection != null ) {
        connection.commit();
      }
    } catch (SQLException e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getMessage());
      }
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param connection DOCUMENT ME!
   */
  protected void rollbackLocalConnection(Connection connection) {
    try {
      if (connection != null) {
        connection.rollback();
      }
    } catch (Exception e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getMessage());
      }
    }
  }

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
  protected <T, C> T processExecuteGroup(String name, C criteria, T result) throws CpoException {
    Connection c = null;
    T obj = null;

    try {
      c = getWriteConnection();
      obj = processExecuteGroup(name, criteria, result, c);
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(e, "processExecuteGroup(String name, Object criteria, Object result) failed");
    } finally {
      closeLocalConnection(c);
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
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> T processExecuteGroup(String name, C criteria, T result, Connection conn) throws CpoException {
    CallableStatement cstmt = null;
    CpoClass criteriaClass;
    CpoClass resultClass;
    T returnObject = null;
    Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass());

    JdbcCallableStatementFactory jcsf = null;

    if (criteria == null || result == null) {
      throw new CpoException("NULL Object passed into executeObject");
    }

    try {
      criteriaClass = metaDescriptor.getMetaClass(criteria);
      resultClass = metaDescriptor.getMetaClass(result);

      List<CpoFunction> functions = criteriaClass.getFunctionGroup(JdbcCpoAdapter.EXECUTE_GROUP, name).getFunctions();
      localLogger.info("===================processExecuteGroup (" + name + ") Count<" + functions.size() + ">=========================");

      try {
        returnObject = (T) result.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        throw new CpoException("Unable to access the constructor of the Return Object", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Object", iae);
      }

      // Loop through the queries and process each one
      for (CpoFunction function : functions) {

        localLogger.debug("Executing Call:" + criteriaClass.getName() + ":" + name);

        jcsf = new JdbcCallableStatementFactory(conn, this, function, criteria, resultClass);
        cstmt = jcsf.getCallableStatement();
        cstmt.execute();
        jcsf.release();

        localLogger.debug("Processing Call:" + criteriaClass.getName() + ":" + name);

        // Todo: Add Code here to go through the arguments, find record sets,
        // and process them
        // Process the non-record set out params and make it the first
        // object in the collection

        // Loop through the OUT Parameters and set them in the result
        // object
        int j = 1;
        for (CpoArgument cpoArgument : jcsf.getOutArguments()) {
          JdbcCpoArgument jdbcArgument = (JdbcCpoArgument) cpoArgument;
          if (jdbcArgument.isOutParameter()) {
            JdbcCpoAttribute jdbcAttribute = jdbcArgument.getAttribute();
            if (jdbcAttribute==null) {
              jdbcAttribute = (JdbcCpoAttribute) resultClass.getAttributeJava(jdbcArgument.getAttributeName());
              if (jdbcAttribute==null) {
                throw new CpoException("Attribute <"+jdbcArgument.getAttributeName()+"> does not exist on class <"+resultClass.getName()+">");
              }
            }
            jdbcAttribute.invokeSetter(returnObject, new CallableStatementCpoData(cstmt, jdbcAttribute, j));
          }
          j++;
        }

        cstmt.close();
      }
    } catch (Throwable t) {
      String msg = "ProcessExecuteGroup(String name, Object criteria, Object result, Connection conn) failed. SQL=";
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      statementClose(cstmt);
      if (jcsf != null) {
        jcsf.release();
      }
    }

    return returnObject;
  }

  /**
   * Retrieves the Object from the datasource.
   *
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
    Connection c = null;
    T result = null;

    try {
      c = getReadConnection();
      result = processSelectGroup(obj, groupName, wheres, orderBy, nativeExpressions, c);

      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(Object obj, String groupName) failed");
    } finally {
      closeLocalConnection(c);
    }

    return result;
  }

  /**
   * DOCUMENT ME!
   *
   * @param obj       DOCUMENT ME!
   * @param groupName DOCUMENT ME!
   * @param con       DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> T processSelectGroup(T obj, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Connection con) throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    CpoClass cpoClass;
    JdbcCpoAttribute attribute;
    T criteriaObj = obj;
    boolean recordsExist = false;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass());

    int recordCount = 0;
    int attributesSet = 0;

    int k;
    T rObj = null;

    if (obj == null) {
      throw new CpoException("NULL Object passed into retrieveBean");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(criteriaObj);
      List<CpoFunction> functions = cpoClass.getFunctionGroup(JdbcCpoAdapter.RETRIEVE_GROUP, groupName).getFunctions();

      localLogger.info("=================== Class=<" + criteriaObj.getClass() + "> Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + groupName + "> =========================");

      try {
        rObj = (T) obj.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        localLogger.error("=================== Could not access default constructor for Class=<" + obj.getClass() + "> ==================");
        throw new CpoException("Unable to access the constructor of the Return Object", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Object", iae);
      }

      for (CpoFunction cpoFunction : functions) {

        JdbcPreparedStatementFactory jpsf = new JdbcPreparedStatementFactory(con, this, cpoClass, cpoFunction, criteriaObj, wheres, orderBy, nativeExpressions);
        ps = jpsf.getPreparedStatement();

        // insertions on
        // selectgroup
        rs = ps.executeQuery();
        jpsf.release();

        if (rs.isBeforeFirst()) {
          rsmd = rs.getMetaData();

          if ((rsmd.getColumnCount() == 2) && "CPO_ATTRIBUTE".equalsIgnoreCase(rsmd.getColumnLabel(1)) && "CPO_VALUE".equalsIgnoreCase(rsmd.getColumnLabel(2))) {
            while (rs.next()) {
              recordsExist = true;
              recordCount++;
              attribute = (JdbcCpoAttribute) cpoClass.getAttributeData(rs.getString(1));

              if (attribute != null) {
                attribute.invokeSetter(rObj, new JdbcResultSetCpoData(JdbcMethodMapper.getMethodMapper(), rs, attribute, 2));
                attributesSet++;
              }
            }
          } else if (rs.next()) {
            recordsExist = true;
            recordCount++;
            for (k = 1; k <= rsmd.getColumnCount(); k++) {
              attribute = (JdbcCpoAttribute) cpoClass.getAttributeData(rsmd.getColumnLabel(k));

              if (attribute != null) {
                attribute.invokeSetter(rObj, new JdbcResultSetCpoData(JdbcMethodMapper.getMethodMapper(), rs, attribute, k));
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
      resultSetClose(rs);
      statementClose(ps);
    }

    return rObj;
  }

  /**
   * DOCUMENT ME!
   *
   * @param name        DOCUMENT ME!
   * @param criteria    DOCUMENT ME!
   * @param result      DOCUMENT ME!
   * @param wheres      DOCUMENT ME!
   * @param orderBy     DOCUMENT ME!
   * @param useRetrieve DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> List<T> processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions,
                                              boolean useRetrieve) throws CpoException {
    Connection con = null;
    CpoArrayResultSet<T> resultSet = new CpoArrayResultSet<>();

    try {
      con = getReadConnection();
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, con, useRetrieve, resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitLocalConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(con);
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed");
    } finally {
      closeLocalConnection(con);
    }

    return resultSet;
  }

  protected <T, C> void processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions,
                                           boolean useRetrieve, CpoResultSet<T> resultSet) throws CpoException {
    Connection con = null;

    try {
      con = getReadConnection();
      processSelectGroup(name, criteria, result, wheres, orderBy, nativeExpressions, con, useRetrieve, resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitLocalConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(con);
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(String name, Object criteria, Object result,CpoWhere where, Collection orderBy, boolean useRetrieve) failed");
    } finally {
      closeLocalConnection(con);
    }
  }

  /**
   * DOCUMENT ME!
   *
   * @param name        DOCUMENT ME!
   * @param criteria    DOCUMENT ME!
   * @param result      DOCUMENT ME!
   * @param wheres      DOCUMENT ME!
   * @param orderBy     DOCUMENT ME!
   * @param con         DOCUMENT ME!
   * @param useRetrieve DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> void processSelectGroup(String name, C criteria, T result, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Connection con, boolean useRetrieve, CpoResultSet<T> resultSet) throws CpoException {
    Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass());
    PreparedStatement ps = null;
    List<CpoFunction> cpoFunctions;
    CpoClass criteriaClass;
    CpoClass resultClass;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    int columnCount;
    int k;
    T obj;
    JdbcCpoAttribute[] attributes;
    JdbcPreparedStatementFactory jpsf;
    int i;

    if (criteria == null || result == null) {
      throw new CpoException("NULL Object passed into retrieveBean or retrieveBeans");
    }

    try {
      criteriaClass = metaDescriptor.getMetaClass(criteria);
      resultClass = metaDescriptor.getMetaClass(result);
      if (useRetrieve) {
        localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.RETRIEVE_GROUP + "> Name=<" + name + "> =========================");
        cpoFunctions = criteriaClass.getFunctionGroup(JdbcCpoAdapter.RETRIEVE_GROUP, name).getFunctions();
      } else {
        localLogger.info("=================== Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.LIST_GROUP + "> Name=<" + name + "> =========================");
        cpoFunctions = criteriaClass.getFunctionGroup(JdbcCpoAdapter.LIST_GROUP, name).getFunctions();
      }

      for (CpoFunction cpoFunction : cpoFunctions) {
        jpsf = new JdbcPreparedStatementFactory(con, this, criteriaClass, cpoFunction, criteria, wheres, orderBy, nativeExpressions);
        ps = jpsf.getPreparedStatement();
        if (resultSet.getFetchSize() != -1) {
          ps.setFetchSize(resultSet.getFetchSize());
        }

        localLogger.debug("Retrieving Records");

        rs = ps.executeQuery();
        jpsf.release();

        localLogger.debug("Processing Records");

        rsmd = rs.getMetaData();

        columnCount = rsmd.getColumnCount();

        attributes = new JdbcCpoAttribute[columnCount + 1];

        for (k = 1; k <= columnCount; k++) {
          attributes[k] = (JdbcCpoAttribute) resultClass.getAttributeData(rsmd.getColumnLabel(k));
        }

        while (rs.next()) {
          try {
            obj = (T) result.getClass().newInstance();
          } catch (IllegalAccessException iae) {
            localLogger.error("=================== Could not access default constructor for Class=<" + result.getClass() + "> ==================");
            throw new CpoException("Unable to access the constructor of the Return Object", iae);
          } catch (InstantiationException iae) {
            throw new CpoException("Unable to instantiate Return Object", iae);
          }

          for (k = 1; k <= columnCount; k++) {
            if (attributes[k] != null) {
              attributes[k].invokeSetter(obj, new JdbcResultSetCpoData(JdbcMethodMapper.getMethodMapper(), rs, attributes[k], k));
            }
          }

          try {
            resultSet.put(obj);
          } catch (InterruptedException e) {
            localLogger.error("Retriever Thread was interrupted", e);
            break;
          }
        }

        resultSetClose(rs);
        statementClose(ps);

        localLogger.info("=================== " + resultSet.size() + " Records - Class=<" + criteria.getClass() + "> Type=<" + JdbcCpoAdapter.LIST_GROUP + "> Name=<" + name + "> Result=<" + result.getClass() + "> ====================");
      }
    } catch (Throwable t) {
      String msg = "ProcessSelectGroup(String name, Object criteria, Object result, CpoWhere where, Collection orderBy, Connection con) failed. Error:";
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      resultSetClose(rs);
      statementClose(ps);
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
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    Connection c = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();
      updateCount = processUpdateGroup(obj, groupType, groupName, wheres, orderBy, nativeExpressions, c);
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(e, "processUdateGroup(Object obj, String groupType, String groupName) failed");
    } finally {
      closeLocalConnection(c);
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
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(T obj, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Connection con) throws CpoException {
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass());
    CpoClass cpoClass;
    PreparedStatement ps = null;

    JdbcPreparedStatementFactory jpsf = null;
    long updateCount = 0;

    if (obj == null) {
      throw new CpoException("NULL Object passed into insertObject, deleteObject, updateObject, or persistObject");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(obj);
      List<CpoFunction> cpoFunctions = cpoClass.getFunctionGroup(getGroupType(obj, groupType, groupName, con), groupName).getFunctions();
      localLogger.info("=================== Class=<" + obj.getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");

      int numRows = 0;

      for (CpoFunction cpoFunction : cpoFunctions) {
        jpsf = new JdbcPreparedStatementFactory(con, this, cpoClass, cpoFunction, obj, wheres, orderBy, nativeExpressions);
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
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + "," + obj.getClass().getName();
      // TODO FIX THIS
      // localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      statementClose(ps);
      if (jpsf != null) {
        jpsf.release();
      }
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
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processBatchUpdateGroup(T[] arr, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Connection con) throws CpoException {
    CpoClass jmc;
    List<CpoFunction> cpoFunctions;
    PreparedStatement ps = null;
    CpoFunction cpoFunction;
    JdbcPreparedStatementFactory jpsf = null;
    long updateCount = 0;
    int[] updates;
    Logger localLogger = logger;

    try {
      jmc = metaDescriptor.getMetaClass(arr[0]);
      cpoFunctions = jmc.getFunctionGroup(getGroupType(arr[0], groupType, groupName, con), groupName).getFunctions();
      localLogger = LoggerFactory.getLogger(jmc.getMetaClass());

      int numRows = 0;

      // Only Batch if there is only one function
      if (cpoFunctions.size() == 1) {
        localLogger.info("=================== BATCH - Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
        cpoFunction = cpoFunctions.get(0);
        jpsf = new JdbcPreparedStatementFactory(con, this, jmc, cpoFunction, arr[0], wheres, orderBy, nativeExpressions);
        ps = jpsf.getPreparedStatement();
        ps.addBatch();
        for (int j = 1; j < arr.length; j++) {
//          jpsf.bindParameters(arr[j]);
          jpsf.setBindValues(jpsf.getBindValues(cpoFunction, arr[j]));
          ps.addBatch();
        }
        updates = ps.executeBatch();
        jpsf.release();
        ps.close();
        for (int update : updates) {
          if (update < 0 && update == PreparedStatement.SUCCESS_NO_INFO) {
            // something updated but we do not know what or how many so default to one.
            numRows++;
          } else {
            numRows += update;
          }
        }
        localLogger.info("=================== BATCH - " + numRows + " Updates - Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
      } else {
        localLogger.info("=================== Class=<" + arr[0].getClass() + "> Type=<" + groupType + "> Name=<" + groupName + "> =========================");
        for (T obj : arr) {
          for (CpoFunction function : cpoFunctions) {
            jpsf = new JdbcPreparedStatementFactory(con, this, jmc, function, obj, wheres, orderBy, nativeExpressions);
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
      String msg = "ProcessUpdateGroup failed:" + groupType + "," + groupName + "," + arr[0].getClass().getName();
      // TODO FIX This
      // localLogger.error("bound values:" + this.parameterToString(jq));
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      statementClose(ps);
      if (jpsf != null) {
        jpsf.release();
      }
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
  protected <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions) throws CpoException {
    Connection c = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();

      updateCount = processUpdateGroup(coll, groupType, groupName, wheres, orderBy, nativeExpressions, c);
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(e, "processUpdateGroup(Collection coll, String groupType, String groupName) failed");
    } finally {
      closeLocalConnection(c);
    }

    return updateCount;
  }

  /**
   * DOCUMENT ME!
   *
   * @param coll              DOCUMENT ME!
   * @param groupType         DOCUMENT ME!
   * @param groupName         DOCUMENT ME!
   * @param wheres            DOCUMENT ME!
   * @param orderBy           DOCUMENT ME!
   * @param nativeExpressions DOCUMENT ME!
   * @param con               DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> long processUpdateGroup(Collection<T> coll, String groupType, String groupName, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeExpressions, Connection con) throws CpoException {
    long updateCount = 0;

    if (!coll.isEmpty()) {
      T[] arr = (T[]) coll.toArray();

      T obj1 = arr[0];
      boolean allEqual = true;
      for (int i = 1; i < arr.length; i++) {
        if (!obj1.getClass().getName().equals(arr[i].getClass().getName())) {
          allEqual = false;
          break;
        }
      }

      if (allEqual && batchUpdatesSupported_ && !JdbcCpoAdapter.PERSIST_GROUP.equals(groupType)) {
        updateCount = processBatchUpdateGroup(arr, groupType, groupName, wheres, orderBy, nativeExpressions, con);
      } else {
        for (T obj : arr) {
          updateCount += processUpdateGroup(obj, groupType, groupName, wheres, orderBy, nativeExpressions, con);
        }
      }
    }

    return updateCount;
  }


  private void statementClose(Statement s) {
    if (s != null) {
      try {
        s.close();
      } catch (Exception e) {
        if (logger.isTraceEnabled()) {
          logger.trace(e.getMessage());
        }
      }
    }
  }

  private void resultSetClose(ResultSet rs) {
    if (rs != null) {
      try {
        rs.close();
      } catch (Exception e) {
        if (logger.isTraceEnabled()) {
          logger.trace(e.getMessage());
        }
      }
    }
  }

  @Override
  public CpoMetaDescriptor getCpoMetaDescriptor() {
    return metaDescriptor;
  }

  @Override
  public List<CpoAttribute> getCpoAttributes(String expression) throws CpoException {
    List<CpoAttribute> attributes = new ArrayList<>();

    if (expression != null && !expression.isEmpty()) {
      Connection c = null;
      PreparedStatement ps = null;
      ResultSet rs = null;
      try {
        c = getReadConnection();
        ps = c.prepareStatement(expression);
        rs = ps.executeQuery();
        ResultSetMetaData rsmd = rs.getMetaData();
        for (int i = 1; i <= rsmd.getColumnCount(); i++) {
          JdbcCpoAttribute attribute = new JdbcCpoAttribute();
          attribute.setDataName(rsmd.getColumnLabel(i));
          attribute.setDbColumn(rsmd.getColumnName(i));
          try {
            attribute.setDbTable(rsmd.getTableName(i));
          } catch (Exception e) {
            // do nothing if this call is not supported
          }

          DataTypeMapEntry<?> dataTypeMapEntry = metaDescriptor.getDataTypeMapEntry(rsmd.getColumnType(i));
          attribute.setDataType(dataTypeMapEntry.getDataTypeName());
          attribute.setDataTypeInt(dataTypeMapEntry.getDataTypeInt());
          attribute.setJavaType(dataTypeMapEntry.getJavaClass().getName());
          attribute.setJavaName(dataTypeMapEntry.makeJavaName(rsmd.getColumnLabel(i)));

          attributes.add(attribute);
        }
      } catch (Throwable t) {
        logger.error(ExceptionHelper.getLocalizedMessage(t), t);
        throw new CpoException("Error Generating Attributes", t);
      } finally {
        resultSetClose(rs);
        statementClose(ps);
        closeLocalConnection(c);
      }
    }
    return attributes;
  }

}
