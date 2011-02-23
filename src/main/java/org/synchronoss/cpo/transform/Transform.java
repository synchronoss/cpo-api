package org.synchronoss.cpo.transform;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.*;

/**
 * User: michael
 * Date: Sep 19, 2010
 * Time: 12:37:17 AM
 */
public interface Transform<D, J> {

  public J transformIn(D inObject) throws CpoException, UnsupportedOperationException;

  public D transformOut(JdbcPreparedStatementFactory jpsf, J attributeObject) throws CpoException, UnsupportedOperationException;

  public D transformOut(JdbcCallableStatementFactory jpsf, J attributeObject) throws CpoException, UnsupportedOperationException;

}
