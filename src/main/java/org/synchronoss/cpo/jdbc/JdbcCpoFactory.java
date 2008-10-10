/* 
 * 	JdbcCpoFactory.java
 *
 *	Copyright 2003-2007 LiveProcess.
 *	271 Grove Avenue, Verona, NJ, 07044, U.S.A.
 *	All Rights Reserved.
 *
 */
package org.synchronoss.cpo.jdbc;

import java.sql.SQLException;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoFactory;
import org.synchronoss.cpo.jdbc.JdbcCpoAdapter;
import org.synchronoss.cpo.jdbc.JdbcDataSourceInfo;

/**
 * JdbcCpoFactory implements the JdbcCpoAdapter as a singleton. JdbcCpoAdapter is fully thread safe
 * so operating as a Singleton is the most efficent 
 *
 * @author david berry
 */
public class JdbcCpoFactory implements CpoFactory{
	private static CpoAdapter cpoAdapter=null;
  private Logger logger=Logger.getLogger(this.getClass().getName());
  
  private static final String PROP_FILE = "jdbcCpoFactory";
  
  private static final String   PROP_DB_DATASOURCE = "dbDatasource";
  private static final String       PROP_DB_DRIVER = "dbDriver";
  private static final String      PROP_DB_USERURL = "dbUserUrl";
  private static final String          PROP_DB_URL = "dbUrl";
  private static final String         PROP_DB_USER = "dbUser";
  private static final String     PROP_DB_PASSWORD = "dbPassword";
  private static final String     PROP_DB_INIT_CONN = "dbInitConn";
  private static final String     PROP_DB_MAX_CONN = "dbMaxConn";
  
  private static final String     PROP_TABLE_PREFIX = "tablePrefix";
  private static final String PROP_META_DATASOURCE = "metaDatasource";
  private static final String     PROP_META_DRIVER = "metaDriver";
  private static final String    PROP_META_USERURL = "metaUserUrl";
  private static final String        PROP_META_URL = "metaUrl";
  private static final String       PROP_META_USER = "metaUser";
  private static final String   PROP_META_PASSWORD = "metaPassword";
  private static final String     PROP_META_INIT_CONN = "metaInitConn";
  private static final String     PROP_META_MAX_CONN = "metaMaxConn";
	
	public JdbcCpoFactory(){
	}
	
	public CpoAdapter newCpoAdapter() throws CpoException {
		if(cpoAdapter==null) { // Check once to avoid synchronization 
			synchronized(PROP_FILE) {
				if (cpoAdapter==null){ // Double-Check to make sure another thread did not get it first.
					cpoAdapter=createCpoAdapter();
				}
			}
		}
		
		return cpoAdapter;
	}
	
	protected void finalize(){
		cpoAdapter=null;
	}
	
	protected CpoAdapter createCpoAdapter() throws CpoException {
	  String     tablePrefix_ = null;
	  String metaDatasource_ = null;
	  String     metaDriver_ = null;
	  String    metaUserUrl_ = null;
	  String        metaUrl_ = null;
	  String       metaUser_ = null;
	  String   metaPassword_ = null;
	  int      metaInitConn_ = 0;
	  int       metaMaxConn_ = 0;
	  
	  String dbDatasource_ = null;
	  String     dbDriver_ = null;
	  String    dbUserUrl_ = null;
	  String        dbUrl_ = null;
	  String       dbUser_ = null;
	  String   dbPassword_ = null;
	  int      dbInitConn_ = 0;
	  int       dbMaxConn_ = 0;
	  JdbcDataSourceInfo metaInfo=null;
	  JdbcDataSourceInfo dbInfo=null;
	  ResourceBundle b=null;
	  
    try{
      b = PropertyResourceBundle.getBundle(PROP_FILE,Locale.getDefault(), this.getClass().getClassLoader());
    } catch (Exception e){
      throw new CpoException("Error processing properties file:"+PROP_FILE+".properties :"+e.getLocalizedMessage());
    }
    
    dbDatasource_ = getResourceString(b,PROP_DB_DATASOURCE);
    dbDriver_ = getResourceString(b,PROP_DB_DRIVER);
    dbUserUrl_ = getResourceString(b,PROP_DB_USERURL);
    dbUrl_ = getResourceString(b,PROP_DB_URL);
    dbUser_ = getResourceString(b,PROP_DB_USER);
    dbPassword_ = getResourceString(b,PROP_DB_PASSWORD);
    dbInitConn_ = getResourceInt(b,PROP_DB_INIT_CONN);
    dbMaxConn_ = getResourceInt(b,PROP_DB_MAX_CONN);
    
    tablePrefix_ = getResourceString(b,PROP_TABLE_PREFIX);
    metaDatasource_ = getResourceString(b,PROP_META_DATASOURCE);
    metaDriver_ = getResourceString(b,PROP_META_DRIVER);
    metaUserUrl_ = getResourceString(b,PROP_META_USERURL);
    metaUrl_ = getResourceString(b,PROP_META_URL);
    metaUser_ = getResourceString(b,PROP_META_USER);
    metaPassword_ = getResourceString(b,PROP_META_PASSWORD);
    metaInitConn_ = getResourceInt(b,PROP_META_INIT_CONN);
    metaMaxConn_ = getResourceInt(b,PROP_META_MAX_CONN);

	  try {
  	  if (metaDatasource_!=null)
  	    metaInfo = new JdbcDataSourceInfo(metaDatasource_, tablePrefix_);
  	  else if (metaUserUrl_!=null)
  	    metaInfo = new JdbcDataSourceInfo(metaDriver_,metaUserUrl_,metaInitConn_, metaMaxConn_,false,tablePrefix_);
  	  else if (metaUrl_!=null)
        metaInfo = new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_, metaInitConn_, metaMaxConn_,false,tablePrefix_);
	  } catch (SQLException se) {
	    logger.debug("Unable to get meta datasource info");
	    metaInfo=null;
	  }
	  
    try {
      if (dbDatasource_!=null)
        dbInfo = new JdbcDataSourceInfo(dbDatasource_, tablePrefix_);
      else if (dbUserUrl_!=null)
        dbInfo = new JdbcDataSourceInfo(dbDriver_,dbUserUrl_,dbInitConn_, dbMaxConn_,false,tablePrefix_);
      else if (dbUrl_!=null)
        dbInfo = new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_, dbInitConn_, dbMaxConn_,false,tablePrefix_);
    } catch (SQLException se) {
      logger.debug("Unable to get db datasource info");
      dbInfo=null;
    }
	  
    if (metaInfo==null && dbInfo==null){
      throw new CpoException("Unable to create CpoAdapter, Invalid Datasource Information Provided");
    } else if (metaInfo==null){
      return new JdbcCpoAdapter(dbInfo);
    }
    
    return new JdbcCpoAdapter(metaInfo,dbInfo);
	  
	}
	
	protected String getResourceString(ResourceBundle b, String key){
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
	
  protected int getResourceInt(ResourceBundle b, String key){
    int i = 0;
    try {
      i = new Integer(b.getString(key));
    } catch (Exception e) {
      logger.debug("Could not load int resource:"+key);
      i=0;
    }
    return i;
  }

}
