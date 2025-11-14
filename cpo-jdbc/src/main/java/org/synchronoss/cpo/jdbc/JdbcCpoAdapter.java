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
package org.synchronoss.cpo.jdbc;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.enums.Comparison;
import org.synchronoss.cpo.enums.Crud;
import org.synchronoss.cpo.enums.Logical;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;
import org.synchronoss.cpo.jdbc.meta.JdbcResultSetCpoData;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.DataTypeMapEntry;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

/**
 * JdbcCpoAdapter is an interface for a set of routines that are responsible for managing value
 * beans from a jdbc datasource.
 *
 * @author david berry
 */
public class JdbcCpoAdapter extends CpoBaseAdapter<DataSource> {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoAdapter.class);

  private Context context_ = null;

  private boolean invalidReadConnection_ = false;

  private boolean batchUpdatesSupported_ = false;

  /** CpoMetaDescriptor allows you to get the metadata for a class. */
  private JdbcCpoMetaDescriptor metaDescriptor = null;

  /** Creates a JdbcCpoAdapter. */
  protected JdbcCpoAdapter() {}

  /**
   * Creates a JdbcCpoAdapter.
   *
   * @param metaDescriptor This datasource that identifies the cpo metadata datasource
   * @param jdsiTrx The datasoruce that identifies the transaction database.
   * @throws org.synchronoss.cpo.CpoException exception
   */
  protected JdbcCpoAdapter(JdbcCpoMetaDescriptor metaDescriptor, DataSourceInfo<DataSource> jdsiTrx)
      throws CpoException {

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
   * @param jdsiWrite The datasource that identifies the transaction database for write
   *     transactions.
   * @param jdsiRead The datasource that identifies the transaction database for read-only
   *     transactions.
   * @throws org.synchronoss.cpo.CpoException exception
   */
  protected JdbcCpoAdapter(
      JdbcCpoMetaDescriptor metaDescriptor,
      DataSourceInfo<DataSource> jdsiWrite,
      DataSourceInfo<DataSource> jdsiRead)
      throws CpoException {
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
   * @throws CpoException An exception occurred copying the datasource.
   */
  protected JdbcCpoAdapter(JdbcCpoAdapter jdbcCpoAdapter) throws CpoException {
    this.metaDescriptor = (JdbcCpoMetaDescriptor) jdbcCpoAdapter.getCpoMetaDescriptor();
    batchUpdatesSupported_ = jdbcCpoAdapter.isBatchUpdatesSupported();
    setWriteDataSource(jdbcCpoAdapter.getWriteDataSource());
    setReadDataSource(jdbcCpoAdapter.getReadDataSource());
    setDataSourceName(jdbcCpoAdapter.getDataSourceName());
  }

  /**
   * Are batch updates supported
   *
   * @return true if batch updates are supported
   */
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
      throw new CpoException("Could Not Retrieve Database Metadata", t);
    } finally {
      closeLocalConnection(c);
    }
  }

  /**
   * Finds or Creates a JdbcCpoAdapter.
   *
   * @param metaDescriptor - The meta descriptor for the datasource
   * @param jdsiTrx - The datasurce info
   * @return A JdbcCpoAdapter
   * @throws CpoException - An error occurred finding of creating the datasource adapter
   */
  public static JdbcCpoAdapter getInstance(
      JdbcCpoMetaDescriptor metaDescriptor, DataSourceInfo<DataSource> jdsiTrx)
      throws CpoException {
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
   * @param jdsiWrite The datasource that identifies the transaction database for write
   *     transactions.
   * @param jdsiRead The datasource that identifies the transaction database for read-only
   *     transactions.
   * @return - A JdbcCpoAdapter
   * @throws org.synchronoss.cpo.CpoException exception
   */
  public static JdbcCpoAdapter getInstance(
      JdbcCpoMetaDescriptor metaDescriptor,
      DataSourceInfo<DataSource> jdsiWrite,
      DataSourceInfo<DataSource> jdsiRead)
      throws CpoException {
    String adapterKey =
        metaDescriptor + ":" + jdsiWrite.getDataSourceName() + ":" + jdsiRead.getDataSourceName();
    JdbcCpoAdapter adapter = (JdbcCpoAdapter) findCpoAdapter(adapterKey);
    if (adapter == null) {
      adapter = new JdbcCpoAdapter(metaDescriptor, jdsiWrite, jdsiRead);
      addCpoAdapter(adapterKey, adapter);
    }
    return adapter;
  }

  @Override
  public <T> long existsBean(String groupName, T bean, Collection<CpoWhere> wheres)
      throws CpoException {
    Connection c = null;
    long objCount = -1;

    try {
      c = getReadConnection();

      objCount = existsBean(groupName, bean, c, wheres);
    } catch (Exception e) {
      throw new CpoException("existsBean(String, T) failed", e);
    } finally {
      closeLocalConnection(c);
    }

    return objCount;
  }

  /**
   * The CpoAdapter will check to see if this bean exists in the datasource.
   *
   * @param <T> The type of the bean
   * @param groupName The groupName which identifies which EXISTS, INSERT, and UPDATE Function
   *     Groups to execute to upsert the bean.
   * @param bean This is a bean that has been defined within the metadata of the datasource. If the
   *     class is not defined an exception will be thrown.
   * @param con The datasource Connection with which to check if the bean exists
   * @param wheres A collection of where clauses
   * @return The int value of the first column returned in the record set
   * @throws CpoException exception will be thrown if the Function Group has a function count != 1
   */
  protected <T> long existsBean(
      String groupName, T bean, Connection con, Collection<CpoWhere> wheres) throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    CpoClass cpoClass;
    long objCount = 0;
    Logger localLogger = logger;

    if (bean == null) {
      throw new CpoException("NULL Bean passed into existsBean");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(bean);
      List<CpoFunction> cpoFunctions =
          cpoClass.getFunctionGroup(Crud.EXIST, groupName).getFunctions();
      localLogger = LoggerFactory.getLogger(cpoClass.getMetaClass());

      for (CpoFunction cpoFunction : cpoFunctions) {
        localLogger.info(cpoFunction.getExpression());
        JdbcPreparedStatementFactory jpsf =
            new JdbcPreparedStatementFactory(
                con, this, cpoClass, cpoFunction, bean, wheres, null, null);
        ps = jpsf.getPreparedStatement();

        long qCount = 0; // set the results for this function to 0

        rs = ps.executeQuery();
        jpsf.release();
        rsmd = rs.getMetaData();

        // see if they are using the count(*) logic
        if (rsmd.getColumnCount() == 1) {
          if (rs.next()) {
            try {
              qCount = rs.getLong(1); // get the number of beans
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
      String msg = "existsBean(groupName, bean, con) failed:";
      localLogger.error(msg, e);
      throw new CpoException(msg, e);
    } finally {
      resultSetClose(rs);
      statementClose(ps);
    }

    return objCount;
  }

  @Override
  public CpoWhere newWhere() {
    return new JdbcCpoWhere();
  }

  @Override
  public <T> CpoWhere newWhere(Logical logical, String attr, Comparison comp, T value) {
    return new JdbcCpoWhere(logical, attr, comp, value);
  }

  @Override
  public <T> CpoWhere newWhere(
      Logical logical, String attr, Comparison comp, T value, boolean not) {
    return new JdbcCpoWhere(logical, attr, comp, value, not);
  }

  /**
   * Sets the JNDI context
   *
   * @param context The JNDI Context
   * @throws CpoException An exception setting the context
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
   * Gets the JNDI Context
   *
   * @return The current context
   */
  protected Context getContext() {
    return context_;
  }

  /**
   * Validates the crud of query being performed. If it is an UPSERT Group, it checks the database
   * to see if this is an update or an insert, and returns the query group. Otherwise, it sends back
   * the original query group. Upserts only work for single beans.
   *
   * @param <T> The crud of the bean
   * @param bean The bean to insert or update
   * @param crud The group crud
   * @param groupName The groupName
   * @param c The connection to use
   * @return The crud operation to use
   * @throws CpoException An exception occurred
   */
  protected <T> Crud adjustCrud(T bean, Crud crud, String groupName, Connection c)
      throws CpoException {
    Crud retType = crud;
    long objCount;

    if (Crud.UPSERT == retType) {
      objCount = existsBean(groupName, bean, c, null);

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

  /**
   * Gets the read connection for this CpoAdapter
   *
   * @return A read connection
   * @throws CpoException An error has occurred.
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
   * Gets the write connection for this CpoAdapter
   *
   * @return A write connection
   * @throws CpoException An error has occurred.
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
   * Closes the local connection.
   *
   * @param connection The connection to close
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
   * Commits the local connection
   *
   * @param connection The connection to commit
   */
  protected void commitLocalConnection(Connection connection) {
    try {
      if (connection != null) {
        connection.commit();
      }
    } catch (SQLException e) {
      if (logger.isTraceEnabled()) {
        logger.trace(e.getMessage());
      }
    }
  }

  /**
   * Rollback the local connection
   *
   * @param connection The connection to rollback
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

  @Override
  protected <T, C> T processExecuteGroup(String groupName, C criteria, T result)
      throws CpoException {
    Connection c = null;
    T obj = null;

    try {
      c = getWriteConnection();
      obj = processExecuteGroup(groupName, criteria, result, c);
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(
          e, "processExecuteGroup(String groupName, C criteria, T result) failed");
    } finally {
      closeLocalConnection(c);
    }

    return obj;
  }

  /**
   * Executes a Bean whose MetaData contains a stored procedure. An assumption is that the bean
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
   * @param conn The connection to use to execute this group
   * @param result This is a bean that has been defined within the metadata of the datasource. If
   *     the class is not defined an exception will be thrown. If the bean does not exist in the
   *     datasource, an exception will be thrown. This bean defines the bean type that will be
   *     returned in the
   * @return A result bean populate with the OUT arguments
   * @throws CpoException DOCUMENT ME!
   */
  protected <T, C> T processExecuteGroup(String groupName, C criteria, T result, Connection conn)
      throws CpoException {
    CallableStatement cstmt = null;
    CpoClass criteriaClass;
    CpoClass resultClass;
    T returnBean = null;
    Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass());

    JdbcCallableStatementFactory jcsf = null;

    if (criteria == null || result == null) {
      throw new CpoException("NULL Bean passed into executeBean");
    }

    try {
      criteriaClass = metaDescriptor.getMetaClass(criteria);
      resultClass = metaDescriptor.getMetaClass(result);

      List<CpoFunction> functions =
          criteriaClass.getFunctionGroup(Crud.EXECUTE, groupName).getFunctions();
      localLogger.info(
          "===================processExecuteGroup ("
              + groupName
              + ") Count<"
              + functions.size()
              + ">=========================");

      try {
        returnBean = (T) result.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        throw new CpoException("Unable to access the constructor of the Return Bean", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Bean", iae);
      }

      // Loop through the queries and process each one
      for (CpoFunction function : functions) {

        localLogger.debug("Executing Call:" + criteriaClass.getName() + ":" + groupName);

        jcsf = new JdbcCallableStatementFactory(conn, this, function, criteria, resultClass);
        cstmt = jcsf.getCallableStatement();
        cstmt.execute();
        jcsf.release();

        localLogger.debug("Processing Call:" + criteriaClass.getName() + ":" + groupName);

        // Todo: Add Code here to go through the arguments, find record sets,
        // and process them
        // Process the non-record set out params and make it the first
        // bean in the collection

        // Loop through the OUT Parameters and set them in the result
        // bean
        int j = 1;
        for (CpoArgument cpoArgument : jcsf.getOutArguments()) {
          JdbcCpoArgument jdbcArgument = (JdbcCpoArgument) cpoArgument;
          if (jdbcArgument.isOutParameter()) {
            JdbcCpoAttribute jdbcAttribute = jdbcArgument.getAttribute();
            if (jdbcAttribute == null) {
              jdbcAttribute =
                  (JdbcCpoAttribute) resultClass.getAttributeJava(jdbcArgument.getAttributeName());
              if (jdbcAttribute == null) {
                throw new CpoException(
                    "Attribute <"
                        + jdbcArgument.getAttributeName()
                        + "> does not exist on class <"
                        + resultClass.getName()
                        + ">");
              }
            }
            jdbcAttribute.invokeSetter(
                returnBean, new CallableStatementCpoData(cstmt, jdbcAttribute, j));
          }
          j++;
        }

        cstmt.close();
      }
    } catch (Throwable t) {
      String msg =
          "processExecuteGroup(String groupName, C criteria, T result, Connection conn)"
              + " failed. SQL=";
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      statementClose(cstmt);
      if (jcsf != null) {
        jcsf.release();
      }
    }

    return returnBean;
  }

  @Override
  protected <T> T processSelectGroup(
      T bean,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    Connection c = null;
    T result = null;

    try {
      c = getReadConnection();
      result = processSelectGroup(bean, groupName, wheres, orderBy, nativeExpressions, c);

      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(e, "processSelectGroup(T bean, String groupName) failed");
    } finally {
      closeLocalConnection(c);
    }

    return result;
  }

  /**
   * Retrieves the Bean from the datasource.
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
   * @param con The connection to use for this select
   * @return A populated bean of the same type as the Bean passed in as a argument. If no beans
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
      Connection con)
      throws CpoException {
    PreparedStatement ps = null;
    ResultSet rs = null;
    ResultSetMetaData rsmd;
    CpoClass cpoClass;
    JdbcCpoAttribute attribute;
    T criteriaObj = bean;
    boolean recordsExist = false;
    Logger localLogger = bean == null ? logger : LoggerFactory.getLogger(bean.getClass());

    int recordCount = 0;
    int attributesSet = 0;

    int k;
    T rObj = null;

    if (bean == null) {
      throw new CpoException("NULL Bean passed into retrieveBean");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(criteriaObj);
      List<CpoFunction> functions =
          cpoClass.getFunctionGroup(Crud.RETRIEVE, groupName).getFunctions();

      localLogger.info(
          "=================== Class=<"
              + criteriaObj.getClass()
              + "> Type=<"
              + Crud.RETRIEVE.operation
              + "> Name=<"
              + groupName
              + "> =========================");

      try {
        rObj = (T) bean.getClass().newInstance();
      } catch (IllegalAccessException iae) {
        localLogger.error(
            "=================== Could not access default constructor for Class=<"
                + bean.getClass()
                + "> ==================");
        throw new CpoException("Unable to access the constructor of the Return Bean", iae);
      } catch (InstantiationException iae) {
        throw new CpoException("Unable to instantiate Return Bean", iae);
      }

      for (CpoFunction cpoFunction : functions) {

        JdbcPreparedStatementFactory jpsf =
            new JdbcPreparedStatementFactory(
                con, this, cpoClass, cpoFunction, criteriaObj, wheres, orderBy, nativeExpressions);
        ps = jpsf.getPreparedStatement();

        // insertions on
        // selectgroup
        rs = ps.executeQuery();
        jpsf.release();

        if (rs.isBeforeFirst()) {
          rsmd = rs.getMetaData();

          if ((rsmd.getColumnCount() == 2)
              && "CPO_ATTRIBUTE".equalsIgnoreCase(rsmd.getColumnLabel(1))
              && "CPO_VALUE".equalsIgnoreCase(rsmd.getColumnLabel(2))) {
            while (rs.next()) {
              recordsExist = true;
              recordCount++;
              attribute = (JdbcCpoAttribute) cpoClass.getAttributeData(rs.getString(1));

              if (attribute != null) {
                attribute.invokeSetter(
                    rObj,
                    new JdbcResultSetCpoData(JdbcMethodMapper.getMethodMapper(), rs, attribute, 2));
                attributesSet++;
              }
            }
          } else if (rs.next()) {
            recordsExist = true;
            recordCount++;
            for (k = 1; k <= rsmd.getColumnCount(); k++) {
              attribute = (JdbcCpoAttribute) cpoClass.getAttributeData(rsmd.getColumnLabel(k));

              if (attribute != null) {
                attribute.invokeSetter(
                    rObj,
                    new JdbcResultSetCpoData(JdbcMethodMapper.getMethodMapper(), rs, attribute, k));
                attributesSet++;
              }
            }

            if (rs.next()) {
              String msg =
                  "processSelectGroup(T bean, String groupName) failed: Multiple Records Returned";
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
        localLogger.info(
            "=================== 0 Records - 0 Attributes - Class=<"
                + criteriaObj.getClass()
                + "> Type=<"
                + Crud.RETRIEVE.operation
                + "> Name=<"
                + groupName
                + "> =========================");
      } else {
        localLogger.info(
            "=================== "
                + recordCount
                + " Records - "
                + attributesSet
                + " Attributes - Class=<"
                + criteriaObj.getClass()
                + ">  Type=<"
                + Crud.RETRIEVE.operation
                + "> Name=<"
                + groupName
                + "> =========================");
      }
    } catch (Throwable t) {
      String msg = "processSeclectGroup(T bean) failed: " + ExceptionHelper.getLocalizedMessage(t);
      localLogger.error(msg, t);
      rObj = null;
      throw new CpoException(msg, t);
    } finally {
      resultSetClose(rs);
      statementClose(ps);
    }

    return rObj;
  }

  @Override
  protected <T, C> List<T> processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      boolean useRetrieve)
      throws CpoException {
    Connection con = null;
    CpoArrayResultSet<T> resultSet = new CpoArrayResultSet<>();

    try {
      con = getReadConnection();
      processSelectGroup(
          groupName,
          criteria,
          result,
          wheres,
          orderBy,
          nativeExpressions,
          con,
          useRetrieve,
          resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitLocalConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(con);
      ExceptionHelper.reThrowCpoException(
          e,
          "processSelectGroup(String groupName, C criteria, T result,C poWhere where,"
              + " Collection orderBy, boolean useRetrieve) failed");
    } finally {
      closeLocalConnection(con);
    }

    return resultSet;
  }

  @Override
  protected <T, C> void processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      boolean useRetrieve,
      CpoResultSet<T> resultSet)
      throws CpoException {
    Connection con = null;

    try {
      con = getReadConnection();
      processSelectGroup(
          groupName,
          criteria,
          result,
          wheres,
          orderBy,
          nativeExpressions,
          con,
          useRetrieve,
          resultSet);
      // The select may have a for update clause on it
      // Since the connection is cached we need to get rid of this
      commitLocalConnection(con);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(con);
      ExceptionHelper.reThrowCpoException(
          e,
          "processSelectGroup(String groupName, C criteria, T result,CpoWhere where,"
              + " Collection orderBy, boolean useRetrieve) failed");
    } finally {
      closeLocalConnection(con);
    }
  }

  /**
   * Retrieves Beans from the datasource.
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
   * @param con The connection to use for this select
   * @param resultSet The result set to add the results to.
   * @throws CpoException Any errors retrieving the data from the datasource
   */
  protected <T, C> void processSelectGroup(
      String groupName,
      C criteria,
      T result,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      Connection con,
      boolean useRetrieve,
      CpoResultSet<T> resultSet)
      throws CpoException {
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
      throw new CpoException("NULL Bean passed into retrieveBean or retrieveBeans");
    }

    try {
      criteriaClass = metaDescriptor.getMetaClass(criteria);
      resultClass = metaDescriptor.getMetaClass(result);
      if (useRetrieve) {
        localLogger.info(
            "=================== Class=<"
                + criteria.getClass()
                + "> Type=<"
                + Crud.RETRIEVE.operation
                + "> Name=<"
                + groupName
                + "> =========================");
        cpoFunctions = criteriaClass.getFunctionGroup(Crud.RETRIEVE, groupName).getFunctions();
      } else {
        localLogger.info(
            "=================== Class=<"
                + criteria.getClass()
                + "> Type=<"
                + Crud.LIST.operation
                + "> Name=<"
                + groupName
                + "> =========================");
        cpoFunctions = criteriaClass.getFunctionGroup(Crud.LIST, groupName).getFunctions();
      }

      for (CpoFunction cpoFunction : cpoFunctions) {
        jpsf =
            new JdbcPreparedStatementFactory(
                con,
                this,
                criteriaClass,
                cpoFunction,
                criteria,
                wheres,
                orderBy,
                nativeExpressions);
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
            localLogger.error(
                "=================== Could not access default constructor for Class=<"
                    + result.getClass()
                    + "> ==================");
            throw new CpoException("Unable to access the constructor of the Return Bean", iae);
          } catch (InstantiationException iae) {
            throw new CpoException("Unable to instantiate Return Bean", iae);
          }

          for (k = 1; k <= columnCount; k++) {
            if (attributes[k] != null) {
              attributes[k].invokeSetter(
                  obj,
                  new JdbcResultSetCpoData(
                      JdbcMethodMapper.getMethodMapper(), rs, attributes[k], k));
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

        localLogger.info(
            "=================== "
                + resultSet.size()
                + " Records - Class=<"
                + criteria.getClass()
                + "> Type=<"
                + Crud.LIST.operation
                + "> Name=<"
                + groupName
                + "> Result=<"
                + result.getClass()
                + "> ====================");
      }
    } catch (Throwable t) {
      String msg =
          "processSelectGroup(String groupName, C criteria, T result, CpoWhere where,"
              + " Collection orderBy, Connection con) failed. Error:";
      localLogger.error(msg, t);
      throw new CpoException(msg, t);
    } finally {
      resultSetClose(rs);
      statementClose(ps);
    }
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
    Connection c = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();
      updateCount =
          processUpdateGroup(bean, crud, groupName, wheres, orderBy, nativeExpressions, c);
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(
          e, "processUdateGroup(T bean, String crud, String groupName) failed");
    } finally {
      closeLocalConnection(c);
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
   * @param con The connection to use for the update
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
      Connection con)
      throws CpoException {
    Logger localLogger = bean == null ? logger : LoggerFactory.getLogger(bean.getClass());
    CpoClass cpoClass;
    PreparedStatement ps = null;

    JdbcPreparedStatementFactory jpsf = null;
    long updateCount = 0;

    if (bean == null) {
      throw new CpoException(
          "NULL Bean passed into insertBean, deleteBean, updateBean, or upsertBean");
    }

    try {
      cpoClass = metaDescriptor.getMetaClass(bean);
      List<CpoFunction> cpoFunctions =
          cpoClass
              .getFunctionGroup(adjustCrud(bean, crud, groupName, con), groupName)
              .getFunctions();
      localLogger.info(
          "=================== Class=<"
              + bean.getClass()
              + "> Type=<"
              + crud.operation
              + "> Name=<"
              + groupName
              + "> =========================");

      int numRows = 0;

      for (CpoFunction cpoFunction : cpoFunctions) {
        jpsf =
            new JdbcPreparedStatementFactory(
                con, this, cpoClass, cpoFunction, bean, wheres, orderBy, nativeExpressions);
        ps = jpsf.getPreparedStatement();
        numRows += ps.executeUpdate();
        jpsf.release();
        ps.close();
      }
      localLogger.info(
          "=================== "
              + numRows
              + " Updates - Class=<"
              + bean.getClass()
              + "> Type=<"
              + crud.operation
              + "> Name=<"
              + groupName
              + "> =========================");

      if (numRows > 0) {
        updateCount++;
      }
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
    } finally {
      statementClose(ps);
      if (jpsf != null) {
        jpsf.release();
      }
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
   * @param con The connection to use for the update
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processBatchUpdateGroup(
      T[] beans,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      Connection con)
      throws CpoException {
    CpoClass jmc;
    List<CpoFunction> cpoFunctions;
    PreparedStatement ps = null;
    CpoFunction cpoFunction;
    JdbcPreparedStatementFactory jpsf = null;
    long updateCount = 0;
    int[] updates;
    Logger localLogger = logger;

    try {
      jmc = metaDescriptor.getMetaClass(beans[0]);
      cpoFunctions =
          jmc.getFunctionGroup(adjustCrud(beans[0], crud, groupName, con), groupName)
              .getFunctions();
      localLogger = LoggerFactory.getLogger(jmc.getMetaClass());

      int numRows = 0;

      // Only Batch if there is only one function
      if (cpoFunctions.size() == 1) {
        localLogger.info(
            "=================== BATCH - Class=<"
                + beans[0].getClass()
                + "> Type=<"
                + crud.operation
                + "> Name=<"
                + groupName
                + "> =========================");
        cpoFunction = cpoFunctions.get(0);
        jpsf =
            new JdbcPreparedStatementFactory(
                con, this, jmc, cpoFunction, beans[0], wheres, orderBy, nativeExpressions);
        ps = jpsf.getPreparedStatement();
        ps.addBatch();
        for (int j = 1; j < beans.length; j++) {
          //          jpsf.bindParameters(beans[j]);
          jpsf.setBindValues(jpsf.getBindValues(cpoFunction, beans[j]));
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
        localLogger.info(
            "=================== BATCH - "
                + numRows
                + " Updates - Class=<"
                + beans[0].getClass()
                + "> Type=<"
                + crud.operation
                + "> Name=<"
                + groupName
                + "> =========================");
      } else {
        localLogger.info(
            "=================== Class=<"
                + beans[0].getClass()
                + "> Type=<"
                + crud.operation
                + "> Name=<"
                + groupName
                + "> =========================");
        for (T obj : beans) {
          for (CpoFunction function : cpoFunctions) {
            jpsf =
                new JdbcPreparedStatementFactory(
                    con, this, jmc, function, obj, wheres, orderBy, nativeExpressions);
            ps = jpsf.getPreparedStatement();
            numRows += ps.executeUpdate();
            jpsf.release();
            ps.close();
          }
        }
        localLogger.info(
            "=================== "
                + numRows
                + " Updates - Class=<"
                + beans[0].getClass()
                + "> Type=<"
                + crud.operation
                + "> Name=<"
                + groupName
                + "> =========================");
      }

      if (numRows > 0) {
        updateCount = numRows;
      }
    } catch (Throwable t) {
      String msg =
          "ProcessUpdateGroup failed:"
              + crud.operation
              + ","
              + groupName
              + ","
              + beans[0].getClass().getName();
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

  @Override
  protected <T> long processUpdateGroup(
      Collection<T> beans,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions)
      throws CpoException {
    Connection c = null;
    long updateCount = 0;

    try {
      c = getWriteConnection();

      updateCount =
          processUpdateGroup(beans, crud, groupName, wheres, orderBy, nativeExpressions, c);
      commitLocalConnection(c);
    } catch (Exception e) {
      // Any exception has to try to rollback the work;
      rollbackLocalConnection(c);
      ExceptionHelper.reThrowCpoException(
          e, "processUpdateGroup(Collection beans, String crud, String groupName) failed");
    } finally {
      closeLocalConnection(c);
    }

    return updateCount;
  }

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
   * @param con The connection to use for the update
   * @return The number of records updated
   * @throws CpoException any errors processing the update
   */
  protected <T> long processUpdateGroup(
      Collection<T> beans,
      Crud crud,
      String groupName,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeExpressions,
      Connection con)
      throws CpoException {
    long updateCount = 0;

    if (!beans.isEmpty()) {
      T[] arr = (T[]) beans.toArray();

      T obj1 = arr[0];
      boolean allEqual = true;
      for (int i = 1; i < arr.length; i++) {
        if (!obj1.getClass().getName().equals(arr[i].getClass().getName())) {
          allEqual = false;
          break;
        }
      }

      if (allEqual && batchUpdatesSupported_ && !Crud.UPSERT.equals(crud)) {
        updateCount =
            processBatchUpdateGroup(arr, crud, groupName, wheres, orderBy, nativeExpressions, con);
      } else {
        for (T obj : arr) {
          updateCount +=
              processUpdateGroup(obj, crud, groupName, wheres, orderBy, nativeExpressions, con);
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

          DataTypeMapEntry<?> dataTypeMapEntry =
              metaDescriptor.getDataTypeMapEntry(rsmd.getColumnType(i));
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
