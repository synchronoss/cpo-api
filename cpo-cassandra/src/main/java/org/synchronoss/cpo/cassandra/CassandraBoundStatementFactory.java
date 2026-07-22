package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cassandra.meta.CassandraMethodMapper;
import org.synchronoss.cpo.core.*;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.core.meta.MethodMapEntry;
import org.synchronoss.cpo.core.meta.MethodMapper;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.synchronoss.cpo.core.meta.domain.CpoFunction;

/**
 * CassandraBoundStatementFactory is the object that encapsulates the creation of the actual
 * Cassandra {@link BoundStatement} used to execute a CpoFunction against the datastax driver.
 *
 * @author david berry
 */
public class CassandraBoundStatementFactory extends CpoStatementFactory implements CpoReleasable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger =
      LoggerFactory.getLogger(CassandraBoundStatementFactory.class);
  private BoundStatement boundStatement;

  /**
   * Used to build the Cassandra {@link BoundStatement} that CPO executes for a given CpoFunction.
   * The constructor is called by the internal CPO framework. This is not to be used by users of
   * CPO. Programmers that build Transforms may need to use this object to get access to the actual
   * bound statement.
   *
   * @param <T> The type of the object being bound
   * @param sess The Cassandra session that will be used to prepare and bind the statement.
   * @param cassandraCpoAdapter The CassandraCpoAdapter that is controlling this transaction
   * @param criteria The object that will be used to look up the cpo metadata
   * @param function The CpoFunction that is being executed
   * @param bean The bean that is being acted upon
   * @param wheres A collection of wheres to find the object
   * @param orderBy A collection of orderbys to sort the objects
   * @param nativeQueries Additional CQL to be embedded into the CpoFunction CQL that is used to
   *     create the actual Cassandra BoundStatement
   * @throws CpoException if a CPO error occurs
   */
  public <T> CassandraBoundStatementFactory(
      CqlSession sess,
      CassandraCpoAdapter cassandraCpoAdapter,
      CpoClass criteria,
      CpoFunction function,
      T bean,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeQueries)
      throws CpoException {
    super(bean == null ? logger : LoggerFactory.getLogger(bean.getClass()));
    // get the list of bindValues from the function parameters
    List<BindAttribute> bindValues = getBindValues(function, bean);

    String sql =
        buildSql(criteria, function.getExpression(), wheres, orderBy, nativeQueries, bindValues);

    getLocalLogger().debug("CpoFunction SQL = <" + sql + ">");
    try {
      boundStatement = sess.prepare(sql).bind();
      boundStatement = boundStatement.setPageSize(cassandraCpoAdapter.getFetchSize());
      setBindValues(bindValues);
    } catch (Throwable t) {
      getLocalLogger()
          .error(
              "Error Instantiating CassandraBoundStatementFactory SQL=<"
                  + sql
                  + ">"
                  + ExceptionHelper.getLocalizedMessage(t));
      throw new CpoException(t);
    }
  }

  @Override
  protected MethodMapper getMethodMapper() {
    return CassandraMethodMapper.getMethodMapper();
  }

  /**
   * Binds dynamic where-clause values (raw datastore-typed literals, not backed by a CpoAttribute)
   * directly to the BoundStatement. Overridden from {@link CpoStatementFactory#setBindValues}
   * because that base implementation invokes the setter and discards its return value, which is
   * correct for JDBC's mutating {@code PreparedStatement} but silently drops the bind value on
   * driver 4.x's immutable {@code BoundStatement} -- every {@code setXxx(index, value)} call there
   * returns a new instance that must be written back.
   *
   * @param bindValues the bind values to apply to the underlying statement, in parameter order; if
   *     {@code null} the call is a no-op
   * @throws CpoException if a value could not be bound to the underlying statement
   */
  @Override
  public void setBindValues(Collection<BindAttribute> bindValues) throws CpoException {
    if (bindValues == null) {
      return;
    }

    int index = getStartingIndex();

    for (BindAttribute bindAttr : bindValues) {
      Object bindObject = bindAttr.bindObject();
      CpoAttribute cpoAttribute = bindAttr.cpoAttribute();

      MethodMapEntry<?, ?> jsm = getMethodMapper().getDataMethodMapEntry(bindObject.getClass());

      if (jsm != null) {
        getLocalLogger()
            .debug(
                "{}={}",
                cpoAttribute == null ? bindAttr.name() : cpoAttribute.getDataName(),
                bindObject);
        try {
          boundStatement =
              (BoundStatement) jsm.getBsSetter().invoke(boundStatement, index++, bindObject);
        } catch (IllegalAccessException iae) {
          throw new CpoException("Error Accessing Prepared Statement Setter: ", iae);
        } catch (InvocationTargetException ite) {
          throw new CpoException("Error Invoking Prepared Statement Setter: ", ite);
        }
      } else {
        CpoData cpoData = getCpoData(cpoAttribute, index++);
        cpoData.invokeSetter(bindObject);
      }
    }
  }

  @Override
  protected CpoData getCpoData(CpoAttribute cpoAttribute, int index) {
    return new CassandraBoundStatementCpoData(this, cpoAttribute, index);
  }

  @Override
  protected Object getBindableStatement() {
    return getBoundStatement();
  }

  @Override
  protected int getStartingIndex() {
    return 0;
  }

  /**
   * Gets the BoundStatent associated with this factory
   *
   * @return The BoundStatement
   */
  public BoundStatement getBoundStatement() {
    return boundStatement;
  }

  /**
   * Replaces the BoundStatement held by this factory. Driver 4.x's BoundStatement is immutable:
   * every {@code setXxx(index, value)} call returns a new instance rather than mutating in place,
   * so each bind-value assignment must write its result back here.
   *
   * @param boundStatement The new BoundStatement instance
   */
  void setBoundStatement(BoundStatement boundStatement) {
    this.boundStatement = boundStatement;
  }
}
