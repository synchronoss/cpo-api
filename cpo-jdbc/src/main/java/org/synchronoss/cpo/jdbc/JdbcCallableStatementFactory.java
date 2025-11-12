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

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.*;

import java.sql.*;
import java.util.*;

/**
 * JdbcCallableStatementFactory is the object that encapsulates the creation of the actual CallableStatement for the
 * JDBC driver.
 *
 * @author david berry
 */
public class JdbcCallableStatementFactory implements CpoReleasible {

  private static final Logger logger = LoggerFactory.getLogger(JdbcCallableStatementFactory.class);
  private CallableStatement cs_ = null;

  @SuppressWarnings("unused")
  private JdbcCallableStatementFactory() {
  }
  private List<CpoReleasible> releasibles = new ArrayList<>();
  private List<CpoArgument> outArguments = new ArrayList<>();

  /**
   * Used to build the CallableStatement that is used by CPO to create the actual JDBC CallableStatement.
   * The constructor is called by the internal CPO framework. This is not to be used by users of CPO. Programmers that
   * build Transforms may need to use this object to get access to the actual connection.
   *
   * @param conn The actual jdbc connection that will be used to create the callable statement.
   * @param jca The JdbcCpoAdapter that is controlling this transaction
   * @param function The CpoFunction that is being executed
   * @param criteria The pojo that is being acted upon
   * @param resultClass An instance of the result class
   * @throws CpoException if a CPO error occurs
   */
  public JdbcCallableStatementFactory(Connection conn, JdbcCpoAdapter jca, CpoFunction function, Object criteria, CpoClass resultClass) throws CpoException {
    CallableStatement cstmt;
    JdbcCpoAttribute attribute;
    Logger localLogger = criteria == null ? logger : LoggerFactory.getLogger(criteria.getClass());

    try {
      outArguments = function.getArguments();

      localLogger.debug("SQL = <" + function.getExpression() + ">");

      // prepare the Callable Statement
      cstmt = conn.prepareCall(function.getExpression());
      setCallableStatement(cstmt);

      int j = 1;
      for (CpoArgument argument : outArguments) {
        JdbcCpoArgument jdbcArgument = (JdbcCpoArgument) argument;
        attribute = (JdbcCpoAttribute) argument.getAttribute();

        if (jdbcArgument.isInParameter()) {
          CpoData cpoData = new CallableStatementCpoData(this, attribute, j);
          cpoData.invokeSetter(criteria);
        }

        if (jdbcArgument.isOutParameter()) {
          // The function will not know the type of the attribute on the result object, so look it up now
          if (attribute==null) {
            attribute = (JdbcCpoAttribute) resultClass.getAttributeJava(argument.getAttributeName());
            if (attribute==null) {
              throw new CpoException("Attribute <"+argument.getAttributeName()+"> does not exist on class <"+resultClass.getName()+">");
            }
          }
          localLogger.debug("Setting OUT parameter " + j + " as Type " + attribute.getDataTypeInt());
          if (jdbcArgument.getTypeInfo()!=null)
            cstmt.registerOutParameter(j, attribute.getDataTypeInt(), jdbcArgument.getTypeInfo());
          else
            cstmt.registerOutParameter(j, attribute.getDataTypeInt());
        }
        j++;
      }

    } catch (Exception e) {
      localLogger.error("Error Instantiating JdbcCallableStatementFactory" + ExceptionHelper.getLocalizedMessage(e));
      throw new CpoException(e);
    }

  }

  /**
   * returns the jdbc callable statment associated with this object
   * @return The CallableStatement
   */
  public CallableStatement getCallableStatement() {
    return cs_;
  }

    /**
     * Set the callable statement for this factory.
     *
     * @param cs the callable statement
     */
  protected void setCallableStatement(CallableStatement cs) {
    cs_ = cs;
  }

  /**
   * returns the Out parameters from the callable statement
   *
   * @return The out arguments from the Callable Statement
   */
  public List<CpoArgument> getOutArguments() {
    return outArguments;
  }

  /**
   * Adds a releasible object to this object. The release method on the releasible will be called when the
   * callableStatement is executed.
   *
   * @param releasible - A CpoReleasible object. An Object whose lifetime is associated with the lifetime of the
   *                   Callable statement
   */
  public void AddReleasible(CpoReleasible releasible) {
    if (releasible != null) {
      releasibles.add(releasible);
    }

  }

  /**
   * Called by the CPO framework. This method calls the
   * {@code release} on all the CpoReleasible associated with this object
   */
  @Override
  public void release() throws CpoException {
    for (CpoReleasible releasible : releasibles) {
      try {
        releasible.release();
      } catch (CpoException ce) {
        logger.error("Error Releasing Callable Statement Transform Object", ce);
        throw ce;
      }
    }
  }
}
