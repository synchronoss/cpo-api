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
package org.synchronoss.cpo.transform.jdbc;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;

/**
 * Converts a char[] from a jdbc datasource getString() call and from a char[] to a setString() in a datasource
 *
 * @author david berry
 */
public class TransformCharArray implements JdbcCpoTransform<String, char[]> {

    /**
     * Constructs a TransformCharArray
     */
  public TransformCharArray() {
  }

  /**
   * Transforms the datasource object into an object required by the class
   *
   * @param inStr - The string from the db to be a char array
   * @return The object to be stored in the attribute
   * @throws CpoException An error occurred during the transform
   */
  @Override
  public char[] transformIn(String inStr) throws CpoException {
    char[] retChars = null;

    if (inStr != null) {
      retChars = inStr.toCharArray();
    }

    return retChars;
  }

  /**
   * Transforms the data from the class attribute to the object required by the datasource
   *
   * @param jpsf The JdbcPreparedStatementFactory to have access to the actual connection and be able to work with closeable items
   * @param attributeObject The object that represents the attribute being persisted.
   * @return The object to be stored in the datasource
   * @throws CpoException An error occurred during the transform
   */
  @Override
  public String transformOut(JdbcPreparedStatementFactory jpsf, char[] attributeObject) throws CpoException {

    String retStr = null;
    if (attributeObject != null) {
      retStr = new String(attributeObject);
    }

    return retStr;
  }

  @Override
  public String transformOut(JdbcCallableStatementFactory jpsf, char[] attributeObject) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String transformOut(char[] j) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
