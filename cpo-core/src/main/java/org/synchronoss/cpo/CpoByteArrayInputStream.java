package org.synchronoss.cpo;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * CpoByteArrayInputStream is a utility class to help process ByteArrayInputStreams
 *
 * @author david berry
 */
public class CpoByteArrayInputStream extends ByteArrayInputStream
    implements java.io.Serializable, Cloneable {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  private static final Logger logger = LoggerFactory.getLogger(CpoByteArrayInputStream.class);
  private byte[] buffer_ = null; // The buffer for the byte Array
  private int offset_ = 0;
  private int size_ = 0;

  public CpoByteArrayInputStream(byte[] buffer) {
    super(buffer);
    buffer_ = buffer;
  }

  public CpoByteArrayInputStream(byte[] buffer, int offset, int length) {
    super(buffer, offset, length);
    buffer_ = buffer;
    offset_ = offset;
    size_ = length;
  }

  protected void setBuffer(byte[] buffer) {
    buffer_ = buffer;
  }

  protected byte[] getBuffer() {
    return buffer_;
  }

  protected void setOffset(int offset) {
    offset_ = offset;
  }

  protected int getOffset() {
    return offset_;
  }

  protected void setSize(int size) {
    size_ = size;
  }

  protected int getSize() {
    return size_;
  }

  public int getLength() {
    int l;

    if (getOffset() == 0) {
      l = getBuffer().length;
    } else {
      l =
          getSize() < getBuffer().length - getOffset()
              ? getSize()
              : getBuffer().length - getOffset();
    }
    return l;
  }

  public static CpoByteArrayInputStream getCpoStream(InputStream is) {
    CpoByteArrayInputStream cbais = null;

    if (is instanceof CpoByteArrayInputStream) {
      cbais = ((CpoByteArrayInputStream) is);
    } else {
      // Need to determine the length of the InputStream
      int b;
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while ((b = is.read()) != -1) {
          baos.write(b);
        }
        cbais = new CpoByteArrayInputStream(baos.toByteArray());
      } catch (IOException ioe) {
        logger.error(
            "Error processing input stream",
            ioe); // do nothing for now. The null should get someone's attention.
      } finally {
        try {
          is.close();
        } catch (IOException ioe) {
          logger.error("Error closing input stream", ioe);
        }
      }
    }

    return cbais;
  }
}
