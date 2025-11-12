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
package org.synchronoss.cpo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.MethodMapEntry;
import org.synchronoss.cpo.meta.MethodMapper;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.Map.Entry;

/**
 * JdbcPreparedStatementFactory is the object that encapsulates the creation of the actual PreparedStatement for the
 * JDBC driver.
 *
 * @author david berry
 */
public abstract class CpoStatementFactory implements CpoReleasible {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;

  /**
   * DOCUMENT ME!
   */
  private static final Logger logger = LoggerFactory.getLogger(CpoStatementFactory.class);
  private Logger localLogger = null;

  private List<CpoReleasible> releasibles = new ArrayList<>();
  private static final String WHERE_MARKER = "__CPO_WHERE__";
  private static final String ORDERBY_MARKER = "__CPO_ORDERBY__";

  private CpoStatementFactory() {
    // hidden constructor
  }

  public CpoStatementFactory(Logger localLogger){
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
   *
   * @return DOCUMENT ME!
   *
   * @throws org.synchronoss.cpo.CpoException DOCUMENT ME!
   */
  protected <T> String buildSql(CpoClass cpoClass, String sql, Collection<CpoWhere> wheres, Collection<CpoOrderBy> orderBy, Collection<CpoNativeFunction> nativeQueries, List<BindAttribute> bindValues) throws CpoException {
    StringBuilder sqlText = new StringBuilder();

    sqlText.append(sql);

    if (wheres != null) {
      for (CpoWhere where : wheres) {
        BindableWhereBuilder<T> jwb = new BindableWhereBuilder<>(cpoClass);
        BindableCpoWhere jcw = (BindableCpoWhere) where;

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
      HashMap<String, StringBuilder> mapOrderBy = new HashMap<>();
      try {
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

  private <T> StringBuilder replaceMarker(StringBuilder source, String marker, BindableWhereBuilder<T> jwb, List<BindAttribute> bindValues) {
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
   * Called by the CPO Framework. Binds all the attibutes from the class for the CPO meta parameters and the parameters
   * from the dynamic where.
   *
   */
  public void setBindValues(Collection<BindAttribute> bindValues) throws CpoException {

    if (bindValues != null) {
      int index = getStartingIndex();

      //runs through the bind attributes and binds them to the prepared statement
      // They must be in correct order.
      for (BindAttribute bindAttr : bindValues) {
        Object bindObject = bindAttr.getBindObject();
        CpoAttribute cpoAttribute = bindAttr.getCpoAttribute();

        // check to see if we are getting a cpo value object or an object that
        // can be put directly in the statement (String, BigDecimal, etc)
        MethodMapEntry<?, ?> jsm = ((MethodMapper<?>)getMethodMapper()).getDataMethodMapEntry(bindObject.getClass());

        if (jsm != null) {
          try {
            if (cpoAttribute == null) {
              localLogger.debug(bindAttr.getName() + "=" + bindObject);
            } else {
              localLogger.debug(cpoAttribute.getDataName() + "=" + bindObject);
            }
            jsm.getBsSetter().invoke(this.getBindableStatement(), index++, bindObject);
          } catch (IllegalAccessException iae) {
            localLogger.error("Error Accessing Prepared Statement Setter: " + ExceptionHelper.getLocalizedMessage(iae));
            throw new CpoException(iae);
          } catch (InvocationTargetException ite) {
            localLogger.error("Error Invoking Prepared Statement Setter: " + ExceptionHelper.getLocalizedMessage(ite));
            throw new CpoException(ite.getCause());
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
