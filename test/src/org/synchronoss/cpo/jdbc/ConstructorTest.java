/**
 * ConstructorTest.java
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

package org.synchronoss.cpo.jdbc;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;

/**
 * ConstructorTest is a JUnit test class for testing the
 * JdbcAdapter class Constructors
 * 
 * @author david berry
 */

public class ConstructorTest extends TestCase {
    private static final String PROP_FILE = "org.synchronoss.cpo.jdbc.jdbc";

    private static final String   PROP_DBDRIVER = "dbDriver";
    private static final String      PROP_DBURL = "dbUrl";
    private static final String      PROP_DBUSERURL = "dbUserUrl";
    private static final String     PROP_DBUSER = "dbUser";
    private static final String PROP_DBPASSWORD = "dbPassword";
    private static final String     PROP_METADRIVER = "metaDriver";
    private static final String PROP_METACONNECTION = "metaUrl";
    private static final String       PROP_METAUSER = "metaUser";
    private static final String   PROP_METAPASSWORD = "metaPassword";
    private String      metaUrl_ = null;
    private String   metaDriver_ = null;
    private String     metaUser_ = null;
    private String metaPassword_ = null;


    
    private String      dbUrl_ = null;
    private String   dbDriver_ = null;
    private String     dbUser_ = null;
    private String  dbUserUrl_ = null;
    private String dbPassword_ = null;
    
    private CpoAdapter jdbcIdo_ = null;
    

    public ConstructorTest(String name) {
        super(name);
    }
    
    /**
     * <code>setUp</code>
     * Load the datasource from the properties in the property file jdbc_en_US.properties 
     * 
     * @author david berry
     * @version '$Id: ConstructorTest.java,v 1.7 2006/01/31 22:55:03 dberry Exp $'
     */

    public void setUp() {
        ResourceBundle b = PropertyResourceBundle.getBundle(PROP_FILE,Locale.getDefault(), this.getClass().getClassLoader());
        dbUrl_ = b.getString(PROP_DBURL).trim();
        dbDriver_ = b.getString(PROP_DBDRIVER).trim();
        dbUser_ = b.getString(PROP_DBUSER).trim();
        dbPassword_ = b.getString(PROP_DBPASSWORD).trim();
        
        metaUrl_ = b.getString(PROP_METACONNECTION).trim();
        metaDriver_ = b.getString(PROP_METADRIVER).trim();
        metaUser_ = b.getString(PROP_METAUSER).trim();
        metaPassword_ = b.getString(PROP_METAPASSWORD).trim();
        
        try {
        	dbUserUrl_ = b.getString(PROP_DBUSERURL).trim();
        }catch (MissingResourceException mre){
        	dbUserUrl_=null;
        }
    }

    public void testConstructorWriteUrlUserPwd(){

        String method = "testConstructorWriteUrlUserPwd:";
        try{
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,1,false)));
            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }
    
    public void testConstructorWriteUrlUserPwdSettings(){

        String method = "testConstructorWriteUrlUserPwdSettings:";
        try{
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,1,false)));
            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }

    
    public void testConstructorWriteUrl(){

        String method = "testConstructorWriteUrl:";
        
        if (dbUserUrl_!=null){
	        try{
	            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUserUrl_,1,1,false)));
	            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
	        } catch (Exception e) {
	            fail(method+e.getMessage());
	        }
        }
    }
    
    public void testConstructorWriteUrlSettings(){

        String method = "testConstructorWriteUrlSettings:";
        if (dbUserUrl_!=null){
	        try{
	            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUserUrl_,1,1,false)));
	            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
	        } catch (Exception e) {
	            fail(method+e.getMessage());
	        }
        }
    }
    
    public void testConstructorReadWriteUrl(){

        String method = "testConstructorReadWriteUrl:";
        JdbcDataSourceInfo jdsi = null;
        if (dbUserUrl_!=null){
	        try{
	        	jdsi = new JdbcDataSourceInfo(dbDriver_,dbUserUrl_,1,1,false);
	            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),jdsi,jdsi));
	            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
	        } catch (Exception e) {
	            fail(method+e.getMessage());
	        }
        }
    }

    public void testConstructorReadWriteUrlUserPwd(){

        String method = "testConstructorReadWriteUrlUserPwd:";
        JdbcDataSourceInfo jdsi = null; 
        try{
        	jdsi = new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,1,false);
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),jdsi,jdsi));
            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }
    
     
    public void testConstructorWriteUrlProps(){

        String method = "testConstructorWriteUrlProps:";
        Properties props = new Properties();
        props.put("user", dbUser_);
        props.put("password", dbPassword_);
        
        try{
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUrl_, props,1,1,false)));
            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }

    public void testConstructorWriteUrlPropsSettings(){

        String method = "testConstructorWriteUrlPropsSettings:";
        Properties props = new Properties();
        props.put("user", dbUser_);
        props.put("password", dbPassword_);

        try{
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUrl_, props,1,1,false)));
            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }
    
    public void testConstructorReadWriteUrlPropsSettings(){

        String method = "testConstructorReadWriteUrlPropsSettings:";
        Properties props = new Properties();
        props.put("user", dbUser_);
        props.put("password", dbPassword_);
        JdbcDataSourceInfo jdsi = null;
        
        try{
        	jdsi = new JdbcDataSourceInfo(dbDriver_,dbUrl_, props,1,1,false);
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(jdsi,jdsi,jdsi));
            if (jdbcIdo_==null) fail(method+"Unable to create CpoAdapter Bean");
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }

    public void tearDown() {
        jdbcIdo_=null;
    }

}