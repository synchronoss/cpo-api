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

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.synchronoss.cpo.core.meta.MethodMapEntry;
import org.synchronoss.cpo.core.meta.MethodMapper;
import org.synchronoss.cpo.core.meta.domain.CpoArgument;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.synchronoss.cpo.core.meta.domain.CpoFunction;
import org.synchronoss.cpo.core.parser.BoundExpressionParser;

/**
 * JdbcPreparedStatementFactory is the object that encapsulates the creation of the actual
 * PreparedStatement for the JDBC driver.
 *
 * @author david berry
 */
public abstract class CpoStatementFactory implements CpoReleasable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private Logger localLogger = null;

  private List<CpoReleasable> releasables = new ArrayList<>();
  private static final String WHERE_MARKER = "__CPO_WHERE__";
  private static final String ORDERBY_MARKER = "__CPO_ORDERBY__";

  private CpoStatementFactory() {
    // hidden constructor
  }

  public CpoStatementFactory(Logger localLogger) {
    this.localLogger = localLogger;
  }

  public Logger getLocalLogger() {
    return localLogger;
  }

  /**
   * DOCUMENT ME!
   *
   * @param cpoClass DOCUMENT ME!
   * @param sql DOCUMENT ME!
   * @param wheres DOCUMENT ME!
   * @param orderBy DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> String buildSql(
      CpoClass cpoClass,
      String sql,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeQueries,
      List<BindAttribute> bindValues)
      throws CpoException {
    StringBuilder sqlText = new StringBuilder();

    sqlText.append(sql);

    if (wheres != null) {
      for (CpoWhere where : wheres) {
        BindableWhereBuilder<T> jwb = new BindableWhereBuilder<>(cpoClass);
        BindableCpoWhere jcw = (BindableCpoWhere) where;

        // do the where stuff here when ready
        jcw.acceptDFVisitor(jwb);

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
      HashMap<String, StringBuilder> mapOrderBy = new HashMap<>();
      for (CpoOrderBy ob : orderBy) {
        StringBuilder sb = mapOrderBy.get(ob.getMarker());
        if (sb == null) {
          sb = new StringBuilder(" ORDER BY ");
          mapOrderBy.put(ob.getMarker(), sb);
        } else {
          sb.append(",");
        }
        sb.append(ob.toString(cpoClass));
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
          if (cnq.getExpression() != null && !cnq.getExpression().isEmpty()) {
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

    // OUT.debug("starting string <"+source.toString()+">");
    if (source != null && !source.isEmpty()) {
      while ((attrOffset = source.indexOf(marker, fromIndex)) != -1) {
        source.replace(attrOffset, attrOffset + mLength, replaceText);
        fromIndex = attrOffset + rLength;
      }
    }
    // OUT.debug("ending string <"+source.toString()+">");

    return source;
  }

  private <T> StringBuilder replaceMarker(
      StringBuilder source,
      String marker,
      BindableWhereBuilder<T> jwb,
      List<BindAttribute> bindValues) {
    int attrOffset;
    int fromIndex = 0;
    int mLength = marker.length();
    String replace = jwb.getWhereClause();
    int rLength = replace.length();
    Collection<BindAttribute> jwbBindValues = jwb.getBindValues();

    // OUT.debug("starting string <"+source.toString()+">");
    if (source != null && !source.isEmpty()) {
      while ((attrOffset = source.indexOf(marker, fromIndex)) != -1) {
        source.replace(attrOffset, attrOffset + mLength, replace);
        fromIndex = attrOffset + rLength;
        bindValues.addAll(
            BoundExpressionParser.getBindMarkerIndexes(source.substring(0, attrOffset)).size(),
            jwbBindValues);
      }
    }
    // OUT.debug("ending string <"+source.toString()+">");

    return source;
  }

  /**
   * Adds a releasable object to this object. The release method on the releasable will be called
   * when the PreparedStatement is executed.
   */
  public void AddReleasable(CpoReleasable releasable) {
    if (releasable != null) {
      releasables.add(releasable);
    }
  }

  /**
   * Called by the CPO framework. This method calls the <code>release</code> on all the
   * CpoReleasable associated with this object
   */
  @Override
  public void release() throws CpoException {
    for (CpoReleasable releasable : releasables) {
      try {
        releasable.release();
      } catch (CpoException ce) {
        localLogger.error("Error Releasing Prepared Statement Transform Object", ce);
        throw ce;
      }
    }
  }

  /**
   * Called by the CPO Framework. Binds all the attibutes from the class for the CPO meta parameters
   * and the parameters from the dynamic where.
   */
  public List<BindAttribute> getBindValues(CpoFunction function, Object obj) throws CpoException {
    List<BindAttribute> bindValues = new ArrayList<>();
    List<CpoArgument> arguments = function.getArguments();
    for (CpoArgument argument : arguments) {
      if (argument == null) {
        throw new CpoException("CpoArgument is null!");
      }
      bindValues.add(new BindAttribute(argument.getAttribute(), obj));
    }
    return bindValues;
  }

  /**
   * Called by the CPO Framework. Binds all the attibutes from the class for the CPO meta parameters
   * and the parameters from the dynamic where.
   */
  public void setBindValues(Collection<BindAttribute> bindValues) throws CpoException {

    if (bindValues != null) {
      int index = getStartingIndex();

      // runs through the bind attributes and binds them to the prepared statement
      // They must be in correct order.
      for (BindAttribute bindAttr : bindValues) {
        Object bindObject = bindAttr.getBindObject();
        CpoAttribute cpoAttribute = bindAttr.getCpoAttribute();

        // check to see if we are getting a cpo value object or an object that
        // can be put directly in the statement (String, BigDecimal, etc)
        MethodMapEntry<?, ?> jsm =
            ((MethodMapper<?>) getMethodMapper()).getDataMethodMapEntry(bindObject.getClass());

        if (jsm != null) {
          try {
            if (cpoAttribute == null) {
              localLogger.debug(bindAttr.getName() + "=" + bindObject);
            } else {
              localLogger.debug(cpoAttribute.getDataName() + "=" + bindObject);
            }
            jsm.getBsSetter().invoke(this.getBindableStatement(), index++, bindObject);
          } catch (IllegalAccessException iae) {
            String msg = "Error Accessing Prepared Statement Setter: ";
            throw new CpoException(msg, iae);
          } catch (InvocationTargetException ite) {
            String msg = "Error Invoking Prepared Statement Setter: ";
            throw new CpoException(msg, ite);
          }
        } else {
          CpoData cpoData = getCpoData(cpoAttribute, index++);
          cpoData.invokeSetter(bindObject);
        }
      }
    }
  }

  protected abstract MethodMapper getMethodMapper();

  protected abstract CpoData getCpoData(CpoAttribute cpoAttribute, int index);

  protected abstract Object getBindableStatement();

  protected abstract int getStartingIndex();
}
