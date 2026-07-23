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
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.cassandra.meta.CassandraMethodMapper;
import org.synchronoss.cpo.core.*;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
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
  private final PreparedStatement preparedStatement;
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
      preparedStatement = sess.prepare(sql);
      setBindValues(bindValues);
      boundStatement = boundStatement.setPageSize(cassandraCpoAdapter.getFetchSize());
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
   * Binds every value in a single call, rather than one call per bind variable. Overridden from
   * {@link CpoStatementFactory#setBindValues} because driver 4.x's {@code BoundStatement} is
   * immutable -- every individual {@code setXxx(index, value)} call returns a new instance instead
   * of mutating in place -- so building the full value array once and calling {@link
   * PreparedStatement#bind(Object...)} is both simpler and avoids that pitfall entirely.
   *
   * @param bindValues the bind values to apply to the underlying statement, in parameter order;
   *     {@code null} is treated as no bind values
   * @throws CpoException if a value could not be resolved for binding
   */
  @Override
  public void setBindValues(Collection<BindAttribute> bindValues) throws CpoException {
    Object[] values = new Object[bindValues == null ? 0 : bindValues.size()];

    if (bindValues != null) {
      int i = 0;
      for (BindAttribute bindAttr : bindValues) {
        Object bindObject = bindAttr.bindObject();
        CpoAttribute cpoAttribute = bindAttr.cpoAttribute();

        if (getMethodMapper().getDataMethodMapEntry(bindObject.getClass()) != null) {
          // a raw datastore-typed literal (e.g. a dynamic where-clause value): bind as-is
          getLocalLogger()
              .debug(
                  "{}={}",
                  cpoAttribute == null ? bindAttr.name() : cpoAttribute.getDataName(),
                  bindObject);
          values[i] = bindObject;
        } else {
          // bindObject is the bean; extract and transform the attribute's value
          CpoData cpoData = getCpoData(cpoAttribute, i);
          Object param = cpoData.transformOut(cpoAttribute.invokeGetter(bindObject));
          getLocalLogger().debug("{}={}", cpoAttribute.getDataName(), param);
          values[i] = param;
        }
        i++;
      }
    }

    boundStatement = preparedStatement.bind(values);
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
   * so callers that individually adjust a single bound value (e.g. paging) must write the result
   * back here.
   *
   * @param boundStatement The new BoundStatement instance
   */
  void setBoundStatement(BoundStatement boundStatement) {
    this.boundStatement = boundStatement;
  }
}
