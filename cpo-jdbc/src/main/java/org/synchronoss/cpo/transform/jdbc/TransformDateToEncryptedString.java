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

import java.sql.Timestamp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.encrypt.Encryptor;
import org.synchronoss.cpo.encrypt.EncryptorFactory;
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;
import org.synchronoss.cpo.transform.Transform;

public class TransformDateToEncryptedString implements JdbcTransform<String, Timestamp> {

    
    private static Logger logger = LoggerFactory.getLogger(TransformDateToEncryptedString.class.getName());
    private static Encryptor encryptor = null;
    private static String encLock = "lock";
    
    public TransformDateToEncryptedString() throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        synchronized(encLock) {
            if(encryptor==null) {
                encryptor = EncryptorFactory.getEncryptor();
            }
        }
    }    
    
    public Timestamp transformIn(String  inDateString) throws CpoException {
        logger.debug("ENTERING transformIn");
        Timestamp inDate = null;
        if(inDateString != null) {
            try {
                logger.debug("encrypted string from db is " + inDateString);                
                String dbDateString = encryptor.decrypt(inDateString);
                logger.debug("decrypted string is " + dbDateString);
                //inDate = new Timestamp(df.parse(dbDateString).getTime());
                inDate = Timestamp.valueOf(dbDateString);
                logger.debug("Timestamp is " + inDate);
            } catch (Exception e) {
                throw new CpoException(e.getMessage());
            }
        }
        return inDate;
    }


    public String transformOut(JdbcPreparedStatementFactory jpsf, Timestamp tsOut) 
    throws CpoException {
        String outString = null;
        logger.debug("ENTERING transformOut");
        if(tsOut != null) {            
            try {
                //String encString = df.format(new Date(tsOut.getDate()));  
                String encString = tsOut.toString();
                logger.debug("String to encrypt is " + encString);
                outString = encryptor.encrypt(encString);                
            } catch (Exception e) {
                throw new CpoException(e.getMessage());
            }
        }
        return outString;
    }

  public String transformOut(JdbcCallableStatementFactory jpsf, Timestamp attributeObject) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String transformOut(Timestamp j) throws CpoException, UnsupportedOperationException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
    
}
