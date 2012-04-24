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
package org.synchronoss.cpo.transform.jdbc;

import java.io.CharArrayWriter;
import java.io.Reader;
import java.io.Writer;
import java.sql.Clob;
import oracle.sql.CLOB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;

/**
 * Converts a java.sql.Blob from a jdbc datasource to a byte[] and from a byte[] to a java.sql.Blob
 *
 * @author david berry
 */
public class TransformClob implements JdbcTransform<Clob, char[]> {

  private static Logger logger = LoggerFactory.getLogger(TransformClob.class.getName());

  public TransformClob() {
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
  @Override
  public char[] transformIn(Clob clob)
          throws CpoException {

    char[] buffChars = new char[1024];
    char[] retChars = null;
    int length;
    CharArrayWriter caw = new CharArrayWriter();

    if (clob != null) {
      try {
        Reader is = clob.getCharacterStream();

        while ((length = is.read(buffChars)) != -1) {
          caw.write(buffChars, 0, length);
        }
        is.close();
        retChars = caw.toCharArray();
      } catch (Exception e) {
        logger.debug("Error in transform blob", e);
        throw new CpoException(e);
      }
    }
    return retChars;
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
  @Override
  public Clob transformOut(JdbcPreparedStatementFactory jpsf, char[] attributeObject)
          throws CpoException {
    CLOB newClob = null;

    try {
      if (attributeObject != null) {
        newClob = CLOB.createTemporary(jpsf.getPreparedStatement().getConnection(), false, CLOB.DURATION_SESSION);
        jpsf.AddReleasible(new OracleTemporaryClob(newClob));

        Writer cos = newClob.setCharacterStream(0);
        cos.write(attributeObject);
        cos.close();
      }
    } catch (Exception e) {
      String msg = "Error CLOBing Char Array";
      logger.error(msg, e);
      throw new CpoException(msg, e);
    }
    return newClob;
  }

  @Override
  public Clob transformOut(JdbcCallableStatementFactory jpsf, char[] attributeObject) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Clob transformOut(char[] j) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
