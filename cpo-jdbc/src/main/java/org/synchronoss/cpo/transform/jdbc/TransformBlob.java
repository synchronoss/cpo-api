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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import oracle.sql.BLOB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;

/**
 * Converts a java.sql.Blob from a jdbc datasource to a byte[] and
 *
 * from a byte[] to a java.sql.Blob
 *
 * @author david berry
 */
public class TransformBlob implements JdbcCpoTransform<Blob, byte[]> {

  private static Logger logger = LoggerFactory.getLogger(TransformBlob.class.getName());

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
  public byte[] transformIn(Blob blob)
          throws CpoException {

    byte[] buffBytes = new byte[1024];
    byte[] retBytes = null;
    int length;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    if (blob != null) {
      logger.debug("BLOB IS NOT NULL");
      try {
        InputStream is = blob.getBinaryStream();

        while ((length = is.read(buffBytes)) != -1) {
          bos.write(buffBytes, 0, length);
        }
        is.close();
        retBytes = bos.toByteArray();
        logger.debug("Got " + retBytes.length + " bytes");
      } catch (Exception e) {
        logger.debug("Error in transform blob", e);
        throw new CpoException(e);
      }
    }
    return retBytes;
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
  public Blob transformOut(JdbcPreparedStatementFactory jpsf, byte[] attributeObject)
          throws CpoException {

    BLOB newBlob = null;

    try {
      if (attributeObject != null) {
        newBlob = BLOB.createTemporary(jpsf.getPreparedStatement().getConnection(), false, BLOB.DURATION_SESSION);
        jpsf.AddReleasible(new OracleTemporaryBlob(newBlob));

        //OutputStream os = newBlob.getBinaryOutputStream();
        OutputStream os = newBlob.setBinaryStream(0);
        os.write(attributeObject);
        os.close();
      }
    } catch (Exception e) {
      String msg = "Error BLOBing Byte Array";
      logger.error(msg, e);
      throw new CpoException(msg, e);
    }
    return newBlob;
  }

  @Override
  public Blob transformOut(JdbcCallableStatementFactory jpsf, byte[] attributeObject) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Blob transformOut(byte[] j) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
