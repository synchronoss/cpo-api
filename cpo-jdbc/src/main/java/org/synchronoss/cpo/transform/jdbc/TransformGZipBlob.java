/*
 * Copyright (C) 2003-2012 David E. Berry
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

import oracle.sql.BLOB;
import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.jdbc.*;

import java.io.*;
import java.sql.Blob;
import java.util.zip.*;

/**
 * Converts a java.sql.Blob from a jdbc datasource to a byte[] and from a byte[] to a java.sql.Blob
 *
 * @author david berry
 */
public class TransformGZipBlob implements JdbcCpoTransform<Blob, byte[]> {

  private static final Logger logger = LoggerFactory.getLogger(TransformGZipBlob.class);

  public TransformGZipBlob() {
  }

  /**
   * Transforms the datasource object into an object required by the class
   *
   * @param cpoAdapter The CpoAdapter for the datasource where the attribute is being retrieved
   * @param parentObject The object that contains the attribute being retrieved.
   * @param The object that represents the datasource object being retrieved
   *
   * @return The object to be stored in the attribute
   *
   * @throws org.synchronoss.cpo.CpoException
   */
  @Override
  public byte[] transformIn(Blob blob) throws CpoException {

    byte[] buffBytes = new byte[1024];
    byte[] retBytes = null;
    int length;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    if (blob != null) {
      try {
        InputStream bis = blob.getBinaryStream();
        if (bis != null) {
          CpoByteArrayInputStream cbais = CpoByteArrayInputStream.getCpoStream(bis);

          if (cbais.getLength() > 0) {
            GZIPInputStream gzis = new GZIPInputStream(cbais);

            while ((length = gzis.read(buffBytes)) != -1) {
              bos.write(buffBytes, 0, length);
            }
            bos.flush();
            bos.close();
            gzis.close();
            cbais.close();
            retBytes = bos.toByteArray();
          } else {
            retBytes = new byte[0];
          }
        }
      } catch (Exception e) {
        logger.error("Error in transform GZipBlob", e);
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
   *
   * @return The object to be stored in the datasource
   *
   * @throws org.synchronoss.cpo.CpoException
   */
  @Override
  public Blob transformOut(JdbcPreparedStatementFactory jpsf, byte[] attributeObject) throws CpoException {

    BLOB newBlob = null;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      if (attributeObject != null) {
        GZIPOutputStream os = new GZIPOutputStream(baos);

        os.write(attributeObject);
        os.flush();
        os.close();
        newBlob = BLOB.createTemporary(jpsf.getPreparedStatement().getConnection().unwrap(java.sql.Connection.class), false, BLOB.DURATION_SESSION);
        jpsf.AddReleasible(new OracleTemporaryBlob(newBlob));

        OutputStream bos = newBlob.setBinaryStream(0);
        bos.write(baos.toByteArray());
        bos.close();
      }
    } catch (Exception e) {
      String msg = "Error GZipping Byte Array";
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
