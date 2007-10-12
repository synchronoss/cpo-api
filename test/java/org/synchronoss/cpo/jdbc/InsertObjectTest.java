/**
 * InsertObjectTest.java
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;

/**
 * RetrieveObjectTest is a JUnit test class for testing the
 * JdbcAdapter class Constructors
 * 
 * @author david berry
 */

public class InsertObjectTest extends TestCase {
    private static final String PROP_FILE = "org.synchronoss.cpo.jdbc.jdbc";

    private static final String     PROP_DBDRIVER = "dbDriver";
    private static final String PROP_DBCONNECTION = "dbUrl";
    private static final String       PROP_DBUSER = "dbUser";
    private static final String   PROP_DBPASSWORD = "dbPassword";
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
    private String dbPassword_ = null;
    
    private ArrayList al = new ArrayList();

    private CpoAdapter jdbcIdo_ = null;
    
    private boolean hasMilliSupport = true;
    
    public InsertObjectTest(String name) {
        super(name);
    }
    
    /**
     * <code>setUp</code>
     * Load the datasource from the properties in the property file jdbc_en_US.properties 
     * 
     * @author david berry
     * @version '$Id: InsertObjectTest.java,v 1.3 2006/01/30 19:09:23 dberry Exp $'
     */

    public void setUp() {
        String method = "setUp:";
        ResourceBundle b = PropertyResourceBundle.getBundle(PROP_FILE,Locale.getDefault(), this.getClass().getClassLoader());
        dbUrl_ = b.getString(PROP_DBCONNECTION).trim();
        dbDriver_ = b.getString(PROP_DBDRIVER).trim();
        dbUser_ = b.getString(PROP_DBUSER).trim();
        dbPassword_ = b.getString(PROP_DBPASSWORD).trim();
        metaUrl_ = b.getString(PROP_METACONNECTION).trim();
        metaDriver_ = b.getString(PROP_METADRIVER).trim();
        metaUser_ = b.getString(PROP_METAUSER).trim();
        metaPassword_ = b.getString(PROP_METAPASSWORD).trim();

        if ("com.mysql.jdbc.Driver".equals(dbDriver_)){
        	hasMilliSupport = false;
        }

        
        
        try{
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,1,false)));
            assertNotNull(method+"IdoAdapter is null",jdbcIdo_);
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }
    
    public void testInsertObject() {
        String method = "testInsertObject:";
        ValueObject valObj = new ValueObject(5);
        
        valObj.setAttrVarChar("testInsert");
        valObj.setAttrInteger(3);
        Timestamp ts = new Timestamp(System.currentTimeMillis());
        
        if (!hasMilliSupport)
        	ts.setNanos(0);

        valObj.setAttrDatetime(ts);
        
        valObj.setAttrBit(true);
        
        al.add(valObj);
        
        try{
             jdbcIdo_.insertObject(valObj);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
        
        try{
            ValueObject vo = (ValueObject)jdbcIdo_.retrieveObject(null,valObj,valObj,null,null);
            assertTrue("Ids do not match", vo.getId()==valObj.getId());
            assertTrue("Integers do not match", vo.getAttrInteger()==valObj.getAttrInteger());
            assertEquals("Strings do not match", vo.getAttrVarChar(), valObj.getAttrVarChar());
            assertEquals("Timestamps do not match", vo.getAttrDatetime(), valObj.getAttrDatetime());
            assertTrue("boolean not stored correctly", vo.getAttrBit());
            
       } catch (Exception e) {
           e.printStackTrace();
           fail(method+e.getMessage());
       }
        
        
    }

    public void testInsertObjects(){

        String method = "testInsertObjects:";
        ValueObject vo = new ValueObject(1);
        vo.setAttrVarChar("Test");
        
        al.add(vo);
        al.add(new ValueObject(2));
        al.add(new ValueObject(3));
        al.add(new ValueObject(4));
        try{
            long inserts = jdbcIdo_.insertObjects(al);
            assertEquals("inserts performed do not equal inserts requested", inserts, 4);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }

        try{
            Collection col = jdbcIdo_.retrieveObjects(null,vo,vo,null,null);

            assertTrue(method+"Invalid number of objects returned", col.size()==al.size());
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
        
 
    }

    public void tearDown() {
        String method="tearDown:";
        try{
            jdbcIdo_.deleteObjects(al);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
       jdbcIdo_=null;
    }

}