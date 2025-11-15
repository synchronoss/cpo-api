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

import java.math.BigInteger;
import java.net.InetAddress;
import java.security.SecureRandom;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GUID {

  private static GUID guid_ = new GUID();
  private SecureRandom seeder;
  SecureRandom sr;
  String guidMidValue;
  private static final Logger logger = LoggerFactory.getLogger(GUID.class);

  private GUID() {
    initGuid();
  }

  private void initGuid() {
    try {
      seeder = SecureRandom.getInstance("SHA1PRNG");
      seeder.generateSeed(20);
      sr = SecureRandom.getInstance("SHA1PRNG");
      byte[] newSeed = new byte[20];
      seeder.nextBytes(newSeed);
      sr.setSeed(newSeed);

      StringBuilder tmpBuffer = new StringBuilder();
      // get the inet address
      InetAddress inet = InetAddress.getLocalHost();
      byte[] bytes = inet.getAddress();
      String hexInetAddress = hexFormat(new BigInteger(bytes).intValue());

      // get the hashcode
      String thisHashCode = hexFormat(this.hashCode());

      /*
       * set up a cached midValue as this is the same per method / call as is object specific and is the /
       * ...-xxxx-xxxx-xxxx-xxxx.. mid part of the sequence
       */
      tmpBuffer.append("-");
      tmpBuffer.append(hexInetAddress.substring(0, 4));
      tmpBuffer.append("-");
      tmpBuffer.append(hexInetAddress.substring(4));
      tmpBuffer.append("-");
      tmpBuffer.append(thisHashCode.substring(0, 4));
      tmpBuffer.append("-");
      tmpBuffer.append(thisHashCode.substring(4));
      guidMidValue = tmpBuffer.toString();
    } catch (Exception e) {
      logger.debug("initGuid: " + e.getMessage());
    }
  }

  static GUID getInstance() {
    return guid_;
  }

  public static String getGUID() {
    GUID guid = getInstance();
    long timeNow = System.currentTimeMillis();
    int timeLow = (int) timeNow & 0xFFFFFFFF;
    int node = guid.sr.nextInt();
    String retVal = hexFormat(timeLow) + guid.guidMidValue + hexFormat(node);
    logger.debug("getGUID(): " + retVal);
    return retVal;
  }

  /**
   * Returns an 8 character hexidecimal representation of trgt. If the result is not equal to eight
   * characters leading zeros are prefixed.
   *
   * @return 8 character hex representation of trgt
   */
  private static String hexFormat(int trgt) {
    String s = Integer.toHexString(trgt);
    int sz = s.length();

    if (sz == 8) {
      return s;
    }
    int fill = 8 - sz;
    StringBuilder buf = new StringBuilder();

    for (int i = 0; i < fill; ++i) {
      // add leading zeros
      buf.append('0');
    }
    buf.append(s);
    return buf.toString();
  }
}
