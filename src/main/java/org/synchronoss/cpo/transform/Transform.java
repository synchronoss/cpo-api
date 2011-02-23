package org.synchronoss.cpo.transform;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;

/**
 * User: michael
 * Date: Sep 19, 2010
 * Time: 12:37:17 AM
 */
public interface Transform<D, J> {

  public J transformIn(D inObject) throws CpoException;

  public D transformOut(JdbcPreparedStatementFactory jpsf, J attributeObject) throws CpoException;

}
