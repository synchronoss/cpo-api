/*  
 * EncryptorFactory.java
 * 
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
package org.synchronoss.cpo.encrypt;

import org.apache.log4j.Logger;

public class EncryptorFactory {
    private static String defaultEncryptor = "org.synchronoss.gateway.cpo.jdbc.GatewayEncryptor";
    private static Logger logger = Logger.getLogger(EncryptorFactory.class.getName());   

    public static Encryptor getEncryptor(String className) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        logger.debug("Entering getEncryptor(" + className + ")");
        Encryptor enc= null;        
            Class<?> c = Class.forName(className);
            Object o = c.newInstance();
            enc = (Encryptor)o;
            logger.debug("Created encryptor with class " + enc.getClass());

        return enc;
    }
    
    public static Encryptor getEncryptor()  throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        logger.debug("Entering getEncryptor()");        
        Encryptor enc = null;
        String encryptorName =  System.getProperty("synchronoss.defaultEncryptor");
        if(encryptorName != null && !encryptorName.trim().equals("")) {
            enc=getEncryptor(encryptorName);
        }
        else {
            enc = getEncryptor(defaultEncryptor);
        }
        return enc;        
    }

}
