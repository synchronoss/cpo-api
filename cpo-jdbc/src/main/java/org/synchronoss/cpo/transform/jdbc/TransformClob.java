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

import org.slf4j.*;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.*;

import javax.sql.rowset.serial.SerialClob;
import java.io.*;
import java.sql.Clob;

/**
 * Converts a java.sql.Clob from a jdbc datasource to a byte[] and from a byte[] to a java.sql.clob
 *
 * @author david berry
 */
public class TransformClob implements JdbcCpoTransform<Clob, char[]> {

  private static final Logger logger = LoggerFactory.getLogger(TransformClob.class);

  public TransformClob() {
  }

  /**
   * Transforms the datasource object into an object required by the class
   *
   * @param clob The Clob from the database to be transformed into a byte array
   * @return The object to be stored in the attribute
   * @throws CpoException
   */
  @Override
  public char[] transformIn(Clob clob) throws CpoException {

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
        logger.debug("Error in transform clob", e);
        throw new CpoException(e);
      }
    }
    return retChars;
  }

  /**
   * Transforms the data from the class attribute to the object required by the datasource
   *
   * @param jpsf The JdbcPreparedStatementFactory to have access to the actual connection and be able to work with closeable items
   * @param attributeObject The object that represents the attribute being persisted.
   * @return The object to be stored in the datasource
   * @throws CpoException
   */
  @Override
  public Clob transformOut(JdbcPreparedStatementFactory jpsf, char[] attributeObject) throws CpoException {
    Clob newClob = null;

    try {
      if (attributeObject != null) {
        newClob = new SerialClob(attributeObject);
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
