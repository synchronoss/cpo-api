/**
 * OrderByTest.java  
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
import java.util.Collection;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;
import org.synchronoss.cpo.CpoOrderBy;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class OrderByTest extends TestCase {
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


    private CpoAdapter jdbcIdo_=null;
    private String dbDriver_=null;
    private String dbPassword_=null;
    private String dbUrl_=null;
    private String dbUser_=null;
    private ArrayList<ValueObject> al = new ArrayList<ValueObject>();

    /**
     * Creates a new RollbackTest object.
     *
     * @param name DOCUMENT ME!
     */
    public OrderByTest() {
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
        
        try {
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false, "TEST_"),new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,1,false, "TEST_")));
            assertNotNull(method+"CpoAdapter is null", jdbcIdo_);
            // Add the test valueObjects
        } catch(Exception e) {
            fail(method+e.getMessage());
        }
        
        al.add(new ValueObject(1));
        al.add(new ValueObject(2));
        al.add(new ValueObject(3));
        al.add(new ValueObject(4));
        al.add(new ValueObject(5));
        try{
            jdbcIdo_.insertObjects("TestOrderByInsert",al);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
        

    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown() {
        String method="tearDown:";
        try{
            jdbcIdo_.deleteObjects("TestOrderByDelete",al);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
        jdbcIdo_=null;
    }

    /**
     * DOCUMENT ME!
     */
    public void testOrderByAscending() {
        String method = "testOrderByAscending:";
        Collection<ValueObject> col = null;
        
        
        try{
            CpoOrderBy cob = jdbcIdo_.newOrderBy("id",true);
            Collection<CpoOrderBy> colCob = new ArrayList<CpoOrderBy>();
            colCob.add(cob);
            ValueObject valObj = new ValueObject();
            col = jdbcIdo_.retrieveObjects("TestOrderByRetrieve",valObj,valObj,null,colCob);
            
            int id = 1;
            for(ValueObject vo:col) {
                assertEquals(id,vo.getId());
                id++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    /**
     * DOCUMENT ME!
     */
    public void testOrderByDescending() {
        String method = "testOrderByDescending:";
        Collection<ValueObject> col = null;
        
        try{
            CpoOrderBy cob = jdbcIdo_.newOrderBy("id",false,null);
            Collection<CpoOrderBy> colCob = new ArrayList<CpoOrderBy>();
            colCob.add(cob);
            ValueObject valObj = new ValueObject();
            col = jdbcIdo_.retrieveObjects("TestOrderByRetrieve",valObj,valObj,null,colCob);
            int id = 5;
            for(ValueObject vo:col) {
                assertEquals(id,vo.getId());
                id--;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }


    public void testOrderByFunction() {
        String method = "testOrderByAscending:";
        Collection<ValueObject> col = null;
        
        ValueObject vobj = new ValueObject(-6);
        try{
            jdbcIdo_.insertObject("TestOrderByInsert",vobj);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
        try{
            CpoOrderBy cob = jdbcIdo_.newOrderBy("id",true,"ABS(id)");
            Collection<CpoOrderBy> colCob = new ArrayList<CpoOrderBy>();
            colCob.add(cob);
            ValueObject valObj = new ValueObject();
            col = jdbcIdo_.retrieveObjects("TestOrderByRetrieve",valObj,valObj,null,colCob);
            
            int id = 1;
            for(ValueObject vo:col) {
                int voId = vo.getId();
                if(voId < 0) {
                    voId*=-1;
                }
                assertEquals(id,voId);
                id++;
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }

        try{
            jdbcIdo_.deleteObject("TestOrderByDelete",vobj);
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    
}
