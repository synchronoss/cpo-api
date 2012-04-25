/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoReleasible;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoFunction;

/**
 * JdbcCallableStatementFactory is the object that encapsulates the creation of the actual CallableStatement for the
 * JDBC driver.
 *
 * @author david berry
 */
public class JdbcCallableStatementFactory implements CpoReleasible {

  /**
   * Version Id for this class.
   */
  private static final long serialVersionUID = 1L;
  /**
   * DOCUMENT ME!
   */
  private static Logger logger = LoggerFactory.getLogger(JdbcCallableStatementFactory.class.getName());
  private CallableStatement cs_ = null;

  @SuppressWarnings("unused")
  private JdbcCallableStatementFactory() {
  }
  private List<CpoReleasible> releasibles = new ArrayList<CpoReleasible>();
  private List<CpoArgument> outArguments = new ArrayList<CpoArgument>();

  /**
   * Used to build the CallableStatement that is used by CPO to create the actual JDBC CallableStatement.
   *
   * The constructor is called by the internal CPO framework. This is not to be used by users of CPO. Programmers that
   * build Transforms may need to use this object to get access to the actual connection.
   *
   * @param conn The actual jdbc connection that will be used to create the callable statement.
   * @param jca The JdbcCpoAdapter that is controlling this transaction
   * @param function The CpoFunction that is being executed
   * @param obj The pojo that is being acted upon
   *
   * @throws CpoException if a CPO error occurs
   * @throws SQLException if a JDBC error occurs
   */
  public JdbcCallableStatementFactory(Connection conn, JdbcCpoAdapter jca, CpoFunction function, Object obj) throws CpoException {
    CallableStatement cstmt;
    JdbcCpoAttribute attribute;
    Logger localLogger = obj == null ? logger : LoggerFactory.getLogger(obj.getClass().getName());

    try {
      outArguments = function.getArguments();

      localLogger.debug("SQL = <" + function.getExpression() + ">");

      // prepare the Callable Statement
      cstmt = conn.prepareCall(function.getExpression());
      setCallableStatement(cstmt);

      int j = 1;
      for (CpoArgument argument : outArguments) {
        attribute = (JdbcCpoAttribute) argument.getAttribute();

        if (((JdbcCpoArgument) argument).isInParameter()) {
          attribute.invokeGetter(this, obj, j);
        }

        if (((JdbcCpoArgument) argument).isOutParameter()) {
          localLogger.debug("Setting OUT parameter " + j + " as Type " + attribute.getJavaSqlType());
          cstmt.registerOutParameter(j, attribute.getJavaSqlType());
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
   */
  public CallableStatement getCallableStatement() {
    return cs_;
  }

  protected void setCallableStatement(CallableStatement cs) {
    cs_ = cs;
  }

  /**
   * returns the Out parameters from the callable statement
   *
   */
  public List<CpoArgument> getOutArguments() {
    return outArguments;
  }

  /**
   * Adds a releasible object to this object. The release method on the releasible will be called when the
   * callableStatement is executed.
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
        logger.error("Error Releasing Callable Statement Transform Object", ce);
        throw ce;
      }
    }
  }
}
