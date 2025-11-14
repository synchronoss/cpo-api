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

import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.Session;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.cassandra.meta.CassandraMethodMapper;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.MethodMapper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.synchronoss.cpo.meta.domain.CpoFunction;

/**
 * CassandraBoundStatementFactory is the object that encapsulates the creation of the actual
 * PreparedStatement for the JDBC driver.
 *
 * @author david berry
 */
public class CassandraBoundStatementFactory extends CpoStatementFactory implements CpoReleasible {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger =
      LoggerFactory.getLogger(CassandraBoundStatementFactory.class);
  private BoundStatement boundStatement;
  private List<CpoReleasible> releasibles = new ArrayList<>();

  /**
   * Used to build the PreparedStatement that is used by CPO to create the actual JDBC
   * PreparedStatement. The constructor is called by the internal CPO framework. This is not to be
   * used by users of CPO. Programmers that build Transforms may need to use this object to get
   * access to the actual connection.
   *
   * @param <T> The type of the object being bound
   * @param sess The actual jdbc connection that will be used to create the callable statement.
   * @param cassandraCpoAdapter The JdbcCpoAdapter that is controlling this transaction
   * @param criteria The object that will be used to look up the cpo metadata
   * @param function The CpoFunction that is being executed
   * @param obj The bean that is being acted upon
   * @param wheres A collection of wheres to find the object
   * @param orderBy A collection of orderbys to sort the objects
   * @param nativeQueries Additional sql to be embedded into the CpoFunction sql that is used to
   *     create the actual JDBC PreparedStatement
   * @throws org.synchronoss.cpo.CpoException if a CPO error occurs
   */
  public <T> CassandraBoundStatementFactory(
      Session sess,
      CassandraCpoAdapter cassandraCpoAdapter,
      CpoClass criteria,
      CpoFunction function,
      T obj,
      Collection<CpoWhere> wheres,
      Collection<CpoOrderBy> orderBy,
      Collection<CpoNativeFunction> nativeQueries)
      throws CpoException {
    super(obj == null ? logger : LoggerFactory.getLogger(obj.getClass()));
    // get the list of bindValues from the function parameters
    List<BindAttribute> bindValues = getBindValues(function, obj);

    String sql =
        buildSql(criteria, function.getExpression(), wheres, orderBy, nativeQueries, bindValues);

    getLocalLogger().debug("CpoFunction SQL = <" + sql + ">");
    try {
      setBoundStatement(sess.prepare(sql).bind());
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
    return CassandraMethodMapper
        .getMethodMapper(); // To change body of implemented methods use File | Settings | File
    // Templates.
  }

  @Override
  protected CpoData getCpoData(CpoAttribute cpoAttribute, int index) {
    return new CassandraBoundStatementCpoData(
        this,
        cpoAttribute,
        index); // To change body of implemented methods use File | Settings | File Templates.
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
   * Sets the BoundStatent associated with this factory
   *
   * @param boundStatement The BoundStatement
   */
  public void setBoundStatement(BoundStatement boundStatement) {
    this.boundStatement = boundStatement;
  }
}
