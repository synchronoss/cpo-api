/*
 *  Copyright (C) 2006  David E. Berry
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
 */
package org.synchronoss.cpo.transform.jdbc;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.encrypt.Encryptor;
import org.synchronoss.cpo.encrypt.EncryptorFactory;
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;
import org.synchronoss.cpo.transform.Transform;

public class TransformStringToEncryptedString implements Transform<String, String> {

  private static Logger logger = Logger.getLogger(TransformStringToEncryptedString.class.getName());
  private static Encryptor encryptor = null;
  private static String encLock = "lock";

  public TransformStringToEncryptedString() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
    //System.out.println("in contructor TransformStringToEncryptedString()");
    logger.debug("in contructor TransformStringToEncryptedString()");
    synchronized (encLock) {
      if (encryptor == null) {
        encryptor = EncryptorFactory.getEncryptor();
      }
    }
  }

  public String transformIn(String inString) throws CpoException {
    logger.debug("ENTERING transformIn: " + inString);
    //System.out.println("ENTERING transformIn: " + inString);
    if (inString != null) {
      try {
        inString = encryptor.decrypt(inString);
      } catch (Exception ex) {
        throw new CpoException(ex.getMessage());
      }
    }
    return inString;
  }

  public String transformOut(JdbcPreparedStatementFactory jpsf, String outString)
          throws CpoException {
    logger.debug("ENTERING transformOut: " + outString);
    //System.out.println("ENTERING transformOut: " + outString);
    if (outString != null) {
      try {
        logger.debug("....");
        //System.out.println("....");
        outString = encryptor.encrypt(outString);
        logger.debug("EXITING transformOut: " + outString);
        //System.out.println("EXITING transformOut: " +outString);
      } catch (Exception ex) {
        throw new CpoException(ex.getMessage());
      }
    }
    return outString;
  }

  public String transformOut(JdbcCallableStatementFactory jpsf, String attributeObject) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
