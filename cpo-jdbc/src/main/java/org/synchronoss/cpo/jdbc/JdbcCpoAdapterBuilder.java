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
package org.synchronoss.cpo.jdbc;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.ResourceBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBuilder;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;

/**
 * JdbcCpoFactory implements the Factory pattern for creating a CpoAdatper for use with JDBC
 *
 * @author david berry
 */
public class JdbcCpoAdapterBuilder implements CpoAdapterBuilder {

  private static HashMap<String, CpoAdapter> propMap = new HashMap<String, CpoAdapter>();
  private static Logger logger = LoggerFactory.getLogger(JdbcCpoAdapterBuilder.class.getName());
  private static final String PROP_FILE = "jdbcCpoFactory";
  private static final String DEFAULT_CONTEXT = "default";
  private static final String PROP_DB_DATASOURCE = ".dbDatasource";
  private static final String PROP_DB_DRIVER = ".dbDriver";
  private static final String PROP_DB_USERURL = ".dbUserUrl";
  private static final String PROP_DB_URL = ".dbUrl";
  private static final String PROP_DB_USER = ".dbUser";
  private static final String PROP_DB_PASSWORD = ".dbPassword";
  private static final String PROP_DB_DATASOURCE_CLASS = ".dbDatasourceClass";
  private static final String PROP_TABLE_PREFIX = ".tablePrefix";
  private static final String PROP_META_DATASOURCE = ".metaDatasource";
  private static final String PROP_META_DRIVER = ".metaDriver";
  private static final String PROP_META_USERURL = ".metaUserUrl";
  private static final String PROP_META_URL = ".metaUrl";
  private static final String PROP_META_USER = ".metaUser";
  private static final String PROP_META_PASSWORD = ".metaPassword";
  private static final String PROP_META_DATASOURCE_CLASS = ".metaDatasourceClass";

  public JdbcCpoAdapterBuilder() {
  }

//  public CpoAdapter getCpoAdapter() throws CpoException {
//    return getCpoAdapter(DEFAULT_CONTEXT);
//  }
//  
//  public JdbcCpoMetaAdapter getMetaCpoAdapter(String context) throws CpoException {
//	  if (context==null)
//	    context = DEFAULT_CONTEXT;
//	  
//	  JdbcCpoMetaAdapter cpo = null;
//    if (cpo!=null)
//      return cpo;
//	  
//	  synchronized(propMap){
//	    // check again
////	    cpo= propMap.get(context);
//	    if (cpo!=null)
//	      return cpo;
//	    
//  	  String     tablePrefix_ = null;
//  	  String metaDatasource_ = null;
//  	  String     metaDriver_ = null;
//  	  String    metaUserUrl_ = null;
//  	  String        metaUrl_ = null;
//  	  String       metaUser_ = null;
//  	  String   metaPassword_ = null;
//  	  String metaDatasourceClass_ = null;
//  	  
//  	  String dbDatasource_ = null;
//  	  String     dbDriver_ = null;
//  	  String    dbUserUrl_ = null;
//  	  String        dbUrl_ = null;
//  	  String       dbUser_ = null;
//  	  String   dbPassword_ = null;
//  	  String dbDatasourceClass_ = null;
//
//      DataSourceInfo metaInfo=null;
//  	  DataSourceInfo dbInfo=null;
//  	  ResourceBundle b=null;
//  	  
//      try{
//        b = ResourceBundle.getBundle(PROP_FILE,Locale.getDefault(), JdbcCpoAdapterBuilder.class.getClassLoader());
//      } catch (Exception e){
//        throw new CpoException("Error processing properties file:"+PROP_FILE+".properties :"+ExceptionHelper.getLocalizedMessage(e));
//      }
//      
//      dbDatasource_ = getResourceString(b,context+PROP_DB_DATASOURCE);
//      dbDriver_ = getResourceString(b,context+PROP_DB_DRIVER);
//      dbUserUrl_ = getResourceString(b,context+PROP_DB_USERURL);
//      dbUrl_ = getResourceString(b,context+PROP_DB_URL);
//      dbUser_ = getResourceString(b,context+PROP_DB_USER);
//      dbPassword_ = getResourceString(b,context+PROP_DB_PASSWORD);
//      dbDatasourceClass_ = getResourceString(b,context+PROP_DB_DATASOURCE_CLASS);
//      
//      tablePrefix_ = getResourceString(b,context+PROP_TABLE_PREFIX);
//      metaDatasource_ = getResourceString(b,context+PROP_META_DATASOURCE);
//      metaDriver_ = getResourceString(b,context+PROP_META_DRIVER);
//      metaUserUrl_ = getResourceString(b,context+PROP_META_USERURL);
//      metaUrl_ = getResourceString(b,context+PROP_META_URL);
//      metaUser_ = getResourceString(b,context+PROP_META_USER);
//      metaPassword_ = getResourceString(b,context+PROP_META_PASSWORD);
//      metaDatasourceClass_ = getResourceString(b,context+PROP_META_DATASOURCE_CLASS);
//  
//  	  try {
//    	  if (metaDatasource_!=null)
//    	    metaInfo = new JndiDataSourceInfo(metaDatasource_, tablePrefix_);
//    	  else if (metaDatasourceClass_!=null)
//    	    metaInfo = new ClassDataSourceInfo(metaDatasourceClass_,getProperties(b, context+PROP_META_DATASOURCE_CLASS+"."),tablePrefix_);
//    	  else if (metaUserUrl_!=null)
//    	    metaInfo = new DriverDataSourceInfo(metaDriver_,metaUserUrl_,tablePrefix_);
//    	  else if (metaUrl_!=null)
//          metaInfo = new DriverDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_, tablePrefix_);
//  	  } catch (CpoException se) {
//  	    logger.debug("Unable to get meta datasource info: "+ExceptionHelper.getLocalizedMessage(se));
//  	    metaInfo=null;
//  	  }
//  	  
//      try {
//        if (dbDatasource_!=null)
//          dbInfo = new JndiDataSourceInfo(dbDatasource_, tablePrefix_);
//        else if (dbDatasourceClass_!=null)
//    	    dbInfo = new ClassDataSourceInfo(metaDatasourceClass_,getProperties(b, context+PROP_DB_DATASOURCE_CLASS+"."),tablePrefix_);
//    	  else if (dbUserUrl_!=null)
//          dbInfo = new DriverDataSourceInfo(dbDriver_,dbUserUrl_, tablePrefix_);
//        else if (dbUrl_!=null)
//          dbInfo = new DriverDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_, tablePrefix_);
//      } catch (CpoException se) {
//        logger.debug("Unable to get db datasource info: "+ExceptionHelper.getLocalizedMessage(se));
//        dbInfo=null;
//      }
//  	  
////      if (metaInfo==null && dbInfo==null){
////        throw new CpoException("Unable to create CpoAdapter, Invalid Datasource Information Provided: "+context);
////      } else if (metaInfo==null){
////        cpo = new JdbcCpoAdapter(new JdbcCpoMetaAdapter(dbInfo),dbInfo);
////        propMap.put(context, cpo);
////        return cpo;
////      }
////      cpo = new JdbcCpoAdapter(metaInfo,dbInfo);
////      propMap.put(context, cpo);
//      return cpo;
//	  }
//  }
//	
//	public CpoAdapter getCpoAdapter(String context) throws CpoException {
//	  if (context==null)
//	    context = DEFAULT_CONTEXT;
//	  
//	  CpoAdapter cpo = propMap.get(context);
//    if (cpo!=null)
//      return cpo;
//	  
//	  synchronized(propMap){
//	    // check again
//	    cpo= propMap.get(context);
//	    if (cpo!=null)
//	      return cpo;
//	    
//  	  String     tablePrefix_ = null;
//  	  String metaDatasource_ = null;
//  	  String     metaDriver_ = null;
//  	  String    metaUserUrl_ = null;
//  	  String        metaUrl_ = null;
//  	  String       metaUser_ = null;
//  	  String   metaPassword_ = null;
//  	  String metaDatasourceClass_ = null;
//  	  
//  	  String dbDatasource_ = null;
//  	  String     dbDriver_ = null;
//  	  String    dbUserUrl_ = null;
//  	  String        dbUrl_ = null;
//  	  String       dbUser_ = null;
//  	  String   dbPassword_ = null;
//  	  String dbDatasourceClass_ = null;
//
//      DataSourceInfo metaInfo=null;
//  	  DataSourceInfo dbInfo=null;
//  	  ResourceBundle b=null;
//  	  
//      try{
//        b = ResourceBundle.getBundle(PROP_FILE,Locale.getDefault(), JdbcCpoAdapterBuilder.class.getClassLoader());
//      } catch (Exception e){
//        throw new CpoException("Error processing properties file:"+PROP_FILE+".properties :"+ExceptionHelper.getLocalizedMessage(e));
//      }
//      
//      dbDatasource_ = getResourceString(b,context+PROP_DB_DATASOURCE);
//      dbDriver_ = getResourceString(b,context+PROP_DB_DRIVER);
//      dbUserUrl_ = getResourceString(b,context+PROP_DB_USERURL);
//      dbUrl_ = getResourceString(b,context+PROP_DB_URL);
//      dbUser_ = getResourceString(b,context+PROP_DB_USER);
//      dbPassword_ = getResourceString(b,context+PROP_DB_PASSWORD);
//      dbDatasourceClass_ = getResourceString(b,context+PROP_DB_DATASOURCE_CLASS);
//      
//      tablePrefix_ = getResourceString(b,context+PROP_TABLE_PREFIX);
//      metaDatasource_ = getResourceString(b,context+PROP_META_DATASOURCE);
//      metaDriver_ = getResourceString(b,context+PROP_META_DRIVER);
//      metaUserUrl_ = getResourceString(b,context+PROP_META_USERURL);
//      metaUrl_ = getResourceString(b,context+PROP_META_URL);
//      metaUser_ = getResourceString(b,context+PROP_META_USER);
//      metaPassword_ = getResourceString(b,context+PROP_META_PASSWORD);
//      metaDatasourceClass_ = getResourceString(b,context+PROP_META_DATASOURCE_CLASS);
//  
//  	  try {
//    	  if (metaDatasource_!=null)
//    	    metaInfo = new JndiDataSourceInfo(metaDatasource_, tablePrefix_);
//    	  else if (metaDatasourceClass_!=null)
//    	    metaInfo = new ClassDataSourceInfo(metaDatasourceClass_,getProperties(b, context+PROP_META_DATASOURCE_CLASS+"."),tablePrefix_);
//    	  else if (metaUserUrl_!=null)
//    	    metaInfo = new DriverDataSourceInfo(metaDriver_,metaUserUrl_,tablePrefix_);
//    	  else if (metaUrl_!=null)
//          metaInfo = new DriverDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_, tablePrefix_);
//  	  } catch (CpoException se) {
//  	    logger.debug("Unable to get meta datasource info: "+ExceptionHelper.getLocalizedMessage(se));
//  	    metaInfo=null;
//  	  }
//  	  
//      try {
//        if (dbDatasource_!=null)
//          dbInfo = new JndiDataSourceInfo(dbDatasource_, tablePrefix_);
//        else if (dbDatasourceClass_!=null)
//    	    dbInfo = new ClassDataSourceInfo(metaDatasourceClass_,getProperties(b, context+PROP_DB_DATASOURCE_CLASS+"."),tablePrefix_);
//    	  else if (dbUserUrl_!=null)
//          dbInfo = new DriverDataSourceInfo(dbDriver_,dbUserUrl_, tablePrefix_);
//        else if (dbUrl_!=null)
//          dbInfo = new DriverDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_, tablePrefix_);
//      } catch (CpoException se) {
//        logger.debug("Unable to get db datasource info: "+ExceptionHelper.getLocalizedMessage(se));
//        dbInfo=null;
//      }
//  	  
//      if (metaInfo==null && dbInfo==null){
//        throw new CpoException("Unable to create CpoAdapter, Invalid Datasource Information Provided: "+context);
//      } else if (metaInfo==null){
////        cpo = new JdbcCpoAdapter(new JdbcCpoMetaAdapter(dbInfo),dbInfo);
//        propMap.put(context, cpo);
//        return cpo;
//      }
////      cpo = new JdbcCpoAdapter(metaInfo,dbInfo);
//      propMap.put(context, cpo);
//      return cpo;
//	  }
//	}
  protected static String getResourceString(ResourceBundle b, String key) {
    String s = null;
    try {
      s = b.getString(key).trim();
      if (s.length() < 1) {
        s = null;
      }
    } catch (Exception e) {
      logger.debug("Could not load string resource:" + key);
      s = null;
    }
    return s;
  }

  protected static int getResourceInt(ResourceBundle b, String key) {
    int i = 0;
    try {
      i = new Integer(b.getString(key));
    } catch (Exception e) {
      logger.debug("Could not load int resource:" + key);
      i = 0;
    }
    return i;
  }

  private static HashMap<String, String> getProperties(ResourceBundle b, String propPrefix) {
    HashMap<String, String> propMap = new HashMap<String, String>();
    Enumeration enumKeys = b.getKeys();
    while (enumKeys.hasMoreElements()) {
      String key = (String) enumKeys.nextElement();
      if (key.startsWith(propPrefix)) {
        String value = getResourceString(b, key);
        if (value != null) {
          propMap.put(key.substring(propPrefix.length()), value);
          logger.debug("Adding prop:(" + key.substring(propPrefix.length()) + "," + value + ")");
        }
      }
    }
    return propMap;
  }

  @Override
  public CpoAdapter buildCpoAdapter(CtDataSourceConfig dataSourceConfig) throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
}
