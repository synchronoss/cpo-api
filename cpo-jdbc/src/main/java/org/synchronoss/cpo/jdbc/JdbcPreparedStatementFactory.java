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

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map.Entry;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapEntry;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

/**
 * JdbcPreparedStatementFactory is the object that encapsulates the creation of the actual PreparedStatement for the
 * JDBC driver.
 *
 * @author david berry
 */
public class JdbcPreparedStatementFactory implements CpoReleasible {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  /**
   * DOCUMENT ME!
   */
  private static final Logger logger = LoggerFactory.getLogger(JdbcPreparedStatementFactory.class);
  private Logger localLogger = null;
  private PreparedStatement ps_ = null;

  @SuppressWarnings("unused")
  private JdbcPreparedStatementFactory() {
  }
  private List<CpoReleasible> releasibles = new ArrayList<CpoReleasible>();
  private static final String WHERE_MARKER = "__CPO_WHERE__";
  private static final String ORDERBY_MARKER = "__CPO_ORDERBY__";

  /**
   * Used to build the PreparedStatement that is used by CPO to create the actual JDBC PreparedStatement.
   *
   * The constructor is called by the internal CPO framework. This is not to be used by users of CPO. Programmers that
   * build Transforms may need to use this object to get access to the actual connection.
   *
   * @param conn The actual jdbc connection that will be used to create the callable statement.
   * @param jca The JdbcCpoAdapter that is controlling this transaction
   * @param jq The CpoFunction that is being executed
   * @param obj The pojo that is being acted upon
   * @param additionalSql Additional sql to be appended to the CpoFunction sql that is used to create the actual JDBC
   * PreparedStatement
   *
   * @throws CpoException if a CPO error occurs
   * @throws SQLException if a JDBC error occurs
   */
  public <T> JdbcPreparedStatementFactory(Connection conn, JdbcCpoAdapter jca, CpoClass criteria,
          CpoFunction function, T obj, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy,
          Collection<CpoNativeFunction> nativeQueries) throws CpoException {

    // get the list of bindValues from the function parameters
    List<BindAttribute> bindValues = getBindValues(function, obj);

    String sql = buildSql(criteria, function.getExpression(), wheres, orderBy, nativeQueries, bindValues);

    localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass());

    localLogger.debug("CpoFunction SQL = <" + sql + ">");

    PreparedStatement pstmt = null;

    try {
      pstmt = conn.prepareStatement(sql);
    } catch (SQLException se) {
      localLogger.error("Error Instantiating JdbcPreparedStatementFactory SQL=<" + sql + ">" + ExceptionHelper.getLocalizedMessage(se));
      throw new CpoException(se);
    }
    setPreparedStatement(pstmt);

    setBindValues(bindValues);

  }

  /**
   * DOCUMENT ME!
   *
   * @param cpoClass DOCUMENT ME!
   * @param sql DOCUMENT ME!
   * @param wheres DOCUMENT ME!
   * @param orderBy DOCUMENT ME!
   *
   * @return DOCUMENT ME!
   *
   * @throws CpoException DOCUMENT ME!
   */
  private <T> String buildSql(CpoClass cpoClass, String sql, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeQueries, List<BindAttribute> bindValues) throws CpoException {
    StringBuilder sqlText = new StringBuilder();

    sqlText.append(sql);

    if (wheres != null) {
      for (CpoWhere where : wheres) {
        JdbcWhereBuilder<T> jwb = new JdbcWhereBuilder<T>(cpoClass);
        JdbcCpoWhere jcw = (JdbcCpoWhere) where;

        // do the where stuff here when ready
        try {
          jcw.acceptDFVisitor(jwb);
        } catch (Exception e) {
          throw new CpoException("Unable to build WHERE clause", e);
        }

        if (sqlText.indexOf(jcw.getName()) == -1) {
          sqlText.append(" ");
          sqlText.append(jwb.getWhereClause());
          bindValues.addAll(jwb.getBindValues());
        } else {
          sqlText = replaceMarker(sqlText, jcw.getName(), jwb, bindValues);
        }
      }
    }

    // do the order by stuff now
    if (orderBy != null) {
      HashMap<String, StringBuilder> mapOrderBy = new HashMap<String, StringBuilder>();
      try {
        for (CpoOrderBy ob : orderBy) {
          StringBuilder sb = mapOrderBy.get(ob.getMarker());
          if (sb == null) {
            sb = new StringBuilder(" ORDER BY ");
            mapOrderBy.put(ob.getMarker(), sb);
          } else {
            sb.append(",");
          }
          sb.append(((JdbcCpoOrderBy) ob).toString(cpoClass));
        }
      } catch (CpoException ce) {
        throw new CpoException("Error Processing OrderBy Attribute<" + ExceptionHelper.getLocalizedMessage(ce) + "> not Found. JDBC Expression=<" + sqlText.toString() + ">");
      }

      Set<Entry<String, StringBuilder>> entries = mapOrderBy.entrySet();
      for (Entry<String, StringBuilder> entry : entries) {
        if (sqlText.indexOf(entry.getKey()) == -1) {
          sqlText.append(entry.getValue().toString());
        } else {
          sqlText = replaceMarker(sqlText, entry.getKey(), entry.getValue().toString());
        }
      }
    }

    if (nativeQueries != null) {
      for (CpoNativeFunction cnq : nativeQueries) {
        if (cnq.getMarker() == null || sqlText.indexOf(cnq.getMarker()) == -1) {
          if (cnq.getExpression() != null && cnq.getExpression().length() > 0) {
            sqlText.append(" ");
            sqlText.append(cnq.getExpression());
          }
        } else {
          sqlText = replaceMarker(sqlText, cnq.getMarker(), cnq.getExpression());
        }
      }
    }

    // left for backwards compatibility
    sqlText = replaceMarker(sqlText, WHERE_MARKER, "");
    sqlText = replaceMarker(sqlText, ORDERBY_MARKER, "");

    return sqlText.toString();
  }

  private StringBuilder replaceMarker(StringBuilder source, String marker, String replace) {
    int attrOffset;
    int fromIndex = 0;
    int mLength = marker.length();
    String replaceText = replace == null ? "" : replace;
    int rLength = replaceText.length();

    //OUT.debug("starting string <"+source.toString()+">");
    if (source != null && source.length() > 0) {
      while ((attrOffset = source.indexOf(marker, fromIndex)) != -1) {
        source.replace(attrOffset, attrOffset + mLength, replaceText);
        fromIndex = attrOffset + rLength;
      }
    }
    //OUT.debug("ending string <"+source.toString()+">");

    return source;
  }

  private <T> StringBuilder replaceMarker(StringBuilder source, String marker, JdbcWhereBuilder<T> jwb, List<BindAttribute> bindValues) {
    int attrOffset;
    int fromIndex = 0;
    int mLength = marker.length();
    String replace = jwb.getWhereClause();
    int rLength = replace.length();
    Collection<BindAttribute> jwbBindValues = jwb.getBindValues();

    //OUT.debug("starting string <"+source.toString()+">");
    if (source != null && source.length() > 0) {
      while ((attrOffset = source.indexOf(marker, fromIndex)) != -1) {
        source.replace(attrOffset, attrOffset + mLength, replace);
        fromIndex = attrOffset + rLength;
        bindValues.addAll(countBindMarkers(source.substring(0, attrOffset)), jwbBindValues);
      }
    }
    //OUT.debug("ending string <"+source.toString()+">");

    return source;
  }

  private int countBindMarkers(String source) {
    StringReader reader;
    int rc;
    int qMarks = 0;
    boolean inDoubleQuotes = false;
    boolean inSingleQuotes = false;

    if (source != null) {
      reader = new StringReader(source);

      try {
        do {
          rc = reader.read();
          if (((char) rc) == '\'') {
            inSingleQuotes = !inSingleQuotes;
          } else if (((char) rc) == '"') {
            inDoubleQuotes = !inDoubleQuotes;
          } else if (!inSingleQuotes && !inDoubleQuotes && ((char) rc) == '?') {
            qMarks++;
          }
        } while (rc != -1);
      } catch (Exception e) {
        logger.error("error counting bind markers");
      }
    }

    return qMarks;
  }

  /**
   * Returns the jdbc prepared statment associated with this object
   */
  public PreparedStatement getPreparedStatement() {
    return ps_;
  }

  protected void setPreparedStatement(PreparedStatement ps) {
    ps_ = ps;
  }

  /**
   * Adds a releasible object to this object. The release method on the releasible will be called when the
   * PreparedStatement is executed.
   *
   */
  public void AddReleasible(CpoReleasible releasible) {
    if (releasible != null) {
      releasibles.add(releasible);
    }
  }

  /**
   * Called by the CPO framework. This method calls the
   * <code>release</code> on all the CpoReleasible associated with this object
   */
  @Override
  public void release() throws CpoException {
    for (CpoReleasible releasible : releasibles) {
      try {
        releasible.release();
      } catch (CpoException ce) {
        localLogger.error("Error Releasing Prepared Statement Transform Object", ce);
        throw ce;
      }
    }
  }

  /**
   * Called by the CPO Framework. Binds all the attibutes from the class for the CPO meta parameters and the parameters
   * from the dynamic where.
   *
   */
  protected List<BindAttribute> getBindValues(CpoFunction function, Object obj) throws CpoException {
    List<BindAttribute> bindValues = new ArrayList<BindAttribute>();
    List<CpoArgument> arguments = function.getArguments();
    for (CpoArgument argument : arguments) {
      if (argument == null) {
        throw new CpoException("CpoArgument is null!");
      }
      bindValues.add(new BindAttribute((JdbcCpoAttribute) argument.getAttribute(), obj));
    }
    return bindValues;
  }

  /**
   * Called by the CPO Framework. Binds all the attibutes from the class for the CPO meta parameters and the parameters
   * from the dynamic where.
   *
   */
  protected void setBindValues(Collection<BindAttribute> bindValues) throws CpoException {

    if (bindValues != null) {
      int index = 1;

      //runs through the bind attributes and binds them to the prepared statement
      // They must be in correct order.
      for (BindAttribute bindAttr : bindValues) {
        Object bindObject = bindAttr.getBindObject();
        JdbcCpoAttribute ja = bindAttr.getJdbcAttribute();

        // check to see if we are getting a cpo value object or an object that
        // can be put directly in the statement (String, BigDecimal, etc)
        JdbcMethodMapEntry<?> jsm = null;
        jsm = JdbcMethodMapper.getJavaSqlMethod(bindObject.getClass());

        if (jsm != null) {
          try {
            if (ja == null) {
              localLogger.debug(bindAttr.getName() + "=" + bindObject);
            } else {
              localLogger.debug(ja.getDataName() + "=" + bindObject);
            }
            jsm.getPsSetter().invoke(this.getPreparedStatement(), new Object[]{index++, bindObject});
          } catch (IllegalAccessException iae) {
            localLogger.error("Error Accessing Prepared Statement Setter: " + ExceptionHelper.getLocalizedMessage(iae));
            throw new CpoException(iae);
          } catch (InvocationTargetException ite) {
            localLogger.error("Error Invoking Prepared Statement Setter: " + ExceptionHelper.getLocalizedMessage(ite));
            throw new CpoException(ite.getCause());
          }
        } else {
          CpoData cpoData = new PreparedStatementCpoData(this, ja, index++);
          cpoData.invokeSetter(bindObject);
        }
      }
    }

  }
}
