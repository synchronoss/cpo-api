/**
 * ConnectionPoolTest.java  
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
package org.synchronoss.cpo.jdbc;

import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ConnectionPoolTest extends TestCase {
	private static final Logger logger = Logger.getLogger(ConnectionPoolTest.class.getName());
    private static final String PROP_FILE="org.synchronoss.cpo.jdbc.jdbc";
    private static final String PROP_DBDRIVER="dbDriver";
    private static final String PROP_DBCONNECTION="dbUrl";
    private static final String PROP_DBUSER="dbUser";
    private static final String PROP_DBPASSWORD="dbPassword";
    private static final String     PROP_METADRIVER = "metaDriver";
    private static final String PROP_METACONNECTION = "metaUrl";
    private static final String       PROP_METAUSER = "metaUser";
    private static final String   PROP_METAPASSWORD = "metaPassword";
    private String      metaUrl_ = null;
    private String   metaDriver_ = null;
    private String     metaUser_ = null;
    private String metaPassword_ = null;


    private CpoAdapter jdbcCpo_=null;
    private CpoTrxAdapter jdbcIdo_=null;
    private String dbDriver_=null;
    private String dbPassword_=null;
    private String dbUrl_=null;
    private String dbUser_=null;
    private boolean hasSelect4UpdateSupport = true;
    

    /**
     * Creates a new RollbackTest object.
     *
     * @param name DOCUMENT ME!
     */
    public ConnectionPoolTest() {
    }

    /**
     * <code>setUp</code> Load the datasource from the properties in the property file
     * jdbc_en_US.properties
     */
    public void setUp() {
        String method="setUp:";
        ResourceBundle b=PropertyResourceBundle.getBundle(PROP_FILE, Locale.getDefault(),
                this.getClass().getClassLoader());
        dbUrl_=b.getString(PROP_DBCONNECTION).trim();
        dbDriver_=b.getString(PROP_DBDRIVER).trim();
        dbUser_=b.getString(PROP_DBUSER).trim();
        dbPassword_=b.getString(PROP_DBPASSWORD).trim();

        metaUrl_ = b.getString(PROP_METACONNECTION).trim();
        metaDriver_ = b.getString(PROP_METADRIVER).trim();
        metaUser_ = b.getString(PROP_METAUSER).trim();
        metaPassword_ = b.getString(PROP_METAPASSWORD).trim();
        
        if ("org.hsqldb.jdbcDriver".equals(dbDriver_)){
            hasSelect4UpdateSupport = false;
        }
        
        try {
            jdbcCpo_ = new JdbcCpoAdapter(new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,2,false));
            jdbcIdo_ = jdbcCpo_.getCpoTrxAdapter();
            assertNotNull(method+"CpoAdapter is null", jdbcIdo_);
        } catch(Exception e) {
            logger.debug(e.getLocalizedMessage());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown() {
            jdbcIdo_=null;
    }

    /**
     * DOCUMENT ME!
     */
    public void testConnectionSeperation() {
        String method = "testConnectionSeperation:";
 
        ValueObject vo = new ValueObject(1);
        try{
             jdbcIdo_.insertObject(vo);
             vo.setAttrVarChar("Trx");
             jdbcIdo_.updateObject(vo);
             vo.setAttrVarChar("Cpo");
             long count = jdbcCpo_.updateObject(vo);
             assertEquals(count,0);
             jdbcIdo_.rollback();
        } catch (Exception e) {
            try{jdbcIdo_.rollback();} catch (Exception e1){};
            e.printStackTrace();
            fail(method+e.getLocalizedMessage());

        }
    }
    
 
}
