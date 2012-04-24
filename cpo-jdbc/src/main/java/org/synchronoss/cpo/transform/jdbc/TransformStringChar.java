/**
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or modify it under the terms of the GNU Lesser General
 * Public License as published by the Free Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this library; if not, write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * A copy of the GNU Lesser General Public License may also be found at http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.transform.jdbc;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;

/**
 * Converts a java.sql.Blob from a jdbc datasource to a byte[] and from a byte[] to a java.sql.Blob
 *
 * @author david berry
 */
public class TransformStringChar implements JdbcTransform<String, char[]> {

  public TransformStringChar() {
  }

  /**
   * Transforms the datasource object into an object required by the class
   *
   * @param cpoAdapter The CpoAdapter for the datasource where the attribute is being retrieved
   * @param parentObject The object that contains the attribute being retrieved.
   * @param The object that represents the datasource object being retrieved
   * @return The object to be stored in the attribute
   * @throws CpoException
   */
  public char[] transformIn(String lvc)
          throws CpoException {

    return lvc == null ? null : lvc.toCharArray();
  }

  /**
   * Transforms the data from the class attribute to the object required by the datasource
   *
   * @param cpoAdapter The CpoAdapter for the datasource where the attribute is being persisted
   * @param parentObject The object that contains the attribute being persisted.
   * @param attributeObject The object that represents the attribute being persisted.
   * @return The object to be stored in the datasource
   * @throws CpoException
   */
  public String transformOut(JdbcPreparedStatementFactory jpsf, char[] attrObj)
          throws CpoException {
    return attrObj == null ? null : String.valueOf(attrObj);
  }

  public String transformOut(JdbcCallableStatementFactory jpsf, char[] attributeObject) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String transformOut(char[] j) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
