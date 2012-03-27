package org.synchronoss.cpo.transform.jdbc;

import org.synchronoss.cpo.transform.*;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.*;

/**
 * User: michael
 * Date: Sep 19, 2010
 * Time: 12:37:17 AM
 */
public interface JdbcTransform<D, J> extends Transform<D, J> {

  public D transformOut(JdbcPreparedStatementFactory jpsf, J attributeObject) throws CpoException, UnsupportedOperationException;

  public D transformOut(JdbcCallableStatementFactory jpsf, J attributeObject) throws CpoException, UnsupportedOperationException;

}
