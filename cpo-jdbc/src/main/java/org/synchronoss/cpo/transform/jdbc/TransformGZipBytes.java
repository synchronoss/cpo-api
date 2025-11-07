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

import java.io.*;
import java.util.zip.*;

/**
 * Converts a compressed byte[] from a jdbc datasource to an uncompressed byte[] and from a byte[] to a compressed
 * byte[] in a datasource
 *
 * @author david berry
 */
public class TransformGZipBytes implements JdbcCpoTransform<byte[], byte[]> {

  private static final Logger logger = LoggerFactory.getLogger(TransformGZipBytes.class);

  public TransformGZipBytes() {
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
  public byte[] transformIn(byte[] inBytes) throws CpoException {

    byte[] buffBytes = new byte[1024];
    byte[] retBytes = null;
    int length;
    ByteArrayOutputStream bos = new ByteArrayOutputStream();

    if (inBytes != null) {

      if (inBytes.length > 0) {
        try {
          InputStream bis = new ByteArrayInputStream(inBytes);
          GZIPInputStream gzis = new GZIPInputStream(bis);

          while ((length = gzis.read(buffBytes)) != -1) {
            bos.write(buffBytes, 0, length);
          }
          bos.flush();
          bos.close();
          gzis.close();
          bis.close();
          retBytes = bos.toByteArray();
        } catch (Exception e) {
          logger.error("Error in transform GZipBytes", e);
          throw new CpoException(e);
        }
      } else {
        retBytes = new byte[0];
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
  public byte[] transformOut(JdbcPreparedStatementFactory jpsf, byte[] attributeObject) throws CpoException {

    byte[] retBytes = null;

    ByteArrayOutputStream baos = new ByteArrayOutputStream();

    try {
      if (attributeObject != null) {
        if (attributeObject.length > 0) {
          GZIPOutputStream os = new GZIPOutputStream(baos);

          os.write(attributeObject);
          os.flush();
          os.close();

          baos.flush();
          baos.close();
          retBytes = baos.toByteArray();
        } else {
          retBytes = new byte[0];
        }
      }
    } catch (Exception e) {
      String msg = "Error GZipping Byte Array";
      logger.error(msg, e);
      throw new CpoException(msg, e);
    }
    return retBytes;
  }

  @Override
  public byte[] transformOut(JdbcCallableStatementFactory jpsf, byte[] attributeObject) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public byte[] transformOut(byte[] j) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
