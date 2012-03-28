/* 
 * 	CassandraCpoFactory.java
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
 *
 */
package org.synchronoss.cpo.cassandra;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoFactory;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * CassandraCpoFactory implements the Factory pattern for creating a CpoAdatper for use with Cassandra
 *
 * @author david berry
 */
public class CassandraCpoFactory implements CpoFactory {
  private static HashMap<String, CpoAdapter> propMap = new HashMap<String, CpoAdapter>();
  private static Logger logger=LoggerFactory.getLogger(CassandraCpoFactory.class.getName());
  
  private static final String PROP_FILE = "cassandraCpoFactory";
  private static final String DEFAULT_CONTEXT="default";
  private static final String DATA_CONTEXT="data";
  private static final String META_CONTEXT="meta";
  private static final String PERSISTENCE_CONTEXT="cassandra";
  
  private static final String   PROP_KEYSTORE = ".keyStore";
  private static final String       PROP_USER = ".user";
  private static final String   PROP_PASSWORD = ".password";
	
	public CassandraCpoFactory(){
	}
	
  public CpoAdapter getCpoAdapter() throws CpoException {
    return getCpoAdapter(DEFAULT_CONTEXT);
  }
	
	public CpoAdapter getCpoAdapter(String context) throws CpoException {
	  if (context==null)
	    context = DEFAULT_CONTEXT;
	  
	  CpoAdapter cpo = propMap.get(context);
    if (cpo!=null)
      return cpo;
	  
	  synchronized(propMap){
	    // check again
	    cpo= propMap.get(context);
	    if (cpo!=null)
	      return cpo;
	    
  	  String metaKeyStore_ = null;
  	  String     metaUser_ = null;
  	  String metaPassword_ = null;
  	  
  	  String dataKeyStore_ = null;
  	  String     dataUser_ = null;
  	  String dataPassword_ = null;
  	  
  	  ResourceBundle b=null;
  	  
      try{
        b = ResourceBundle.getBundle(PROP_FILE,Locale.getDefault(), CassandraCpoFactory.class.getClassLoader());
      } catch (Exception e){
        throw new CpoException("Error processing properties file:"+PROP_FILE+".properties :"+ExceptionHelper.getLocalizedMessage(e));
      }
      
      dataKeyStore_ = getResourceString(b,context+DATA_CONTEXT+PROP_KEYSTORE);
      dataUser_ = getResourceString(b,context+DATA_CONTEXT+PROP_USER);
      dataPassword_ = getResourceString(b,context+DATA_CONTEXT+PROP_PASSWORD);
      
      metaKeyStore_ = getResourceString(b,context+META_CONTEXT+PROP_KEYSTORE);
      metaUser_ = getResourceString(b,context+META_CONTEXT+PROP_USER);
      metaPassword_ = getResourceString(b,context+META_CONTEXT+PROP_PASSWORD);
      
      // TODO: Assign a cpoAdapter to cpo
      propMap.put(context, cpo);
      
      return cpo;
	  }
	}
	
  // TODO: Refactor into cpo-core
	protected static String getResourceString(ResourceBundle b, String key){
	  String s = null;
	  try {
	    s = b.getString(key).trim();
	    if (s.length()<1)
	      s=null;
	  } catch (Exception e) {
	    logger.debug("Could not load string resource:"+key);
	    s=null;
	  }
	  return s;
	}
	
  // TODO: Refactor into cpo-core
  protected static int getResourceInt(ResourceBundle b, String key){
    int i = 0;
    try {
      i = new Integer(b.getString(key));
    } catch (Exception e) {
      logger.debug("Could not load int resource:"+key);
      i=0;
    }
    return i;
  }
  
  // TODO: Refactor into cpo-core
  private static HashMap<String, String> getProperties(ResourceBundle b, String propPrefix){
    HashMap<String, String> propMap = new HashMap<String, String>();
    Enumeration enumKeys = b.getKeys();
    while (enumKeys.hasMoreElements()){
      String key = (String)enumKeys.nextElement();
      if (key.startsWith(propPrefix)){
        String value = getResourceString(b,key);
        if (value!=null) {
          propMap.put(key.substring(propPrefix.length()), value);
          logger.debug("Adding prop:("+key.substring(propPrefix.length())+","+value+")");
        }
      }
    }
    return propMap;
  }
}
