/**
 * WhereTest.java  
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
import org.synchronoss.cpo.CpoWhere;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class WhereTest extends TestCase {

	private static final String PROP_FILE="org.synchronoss.cpo.jdbc.jdbc";
    private static final String PROP_DBDRIVER="dbDriver";
    private static final String PROP_DBCONNECTION="dbUrl";
    private static final String PROP_DBUSER="dbUser";
    private static final String PROP_DBPASSWORD="dbPassword";
    private static final String     PROP_METADRIVER = "metaDriver";
    private static final String PROP_METACONNECTION = "metaUrl";
    private static final String       PROP_METAUSER = "metaUser";
    private static final String   PROP_METAPASSWORD = "metaPassword";
    private static final String   PROP_METAPREFIX = "metaPrefix";

    private String   metaPrefix_ = null;
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
    public WhereTest() {
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
        metaPrefix_ = b.getString(PROP_METAPREFIX).trim();
        
        try {
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false, metaPrefix_),new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,1,false)));
            assertNotNull(method+"CpoAdapter is null", jdbcIdo_);
        } catch(Exception e) {
            fail(method+e.getMessage());
        }
        ValueObject vo = new ValueObject(1);
        vo.setAttrVarChar("Test");
        vo.setAttrSmallInt(1);
        al.add(vo);
        al.add(new ValueObject(2));
        al.add(new ValueObject(3));
        al.add(new ValueObject(4));
        al.add(new ValueObject(5));
        al.add(new ValueObject(-6));
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
    
    public void testStaticWhere() {
        String method = "testStaticWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        
        
        try{
            ValueObject valObj = new ValueObject();
             cw = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_GT, null);
             cw.setStaticValue("3");
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==2);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    public void testValueWhere() {
        String method = "testValueWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        
        
        try{
            ValueObject valObj = new ValueObject(3);
            cw = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_GT, valObj);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==2);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    public void testIsNullWhere() {
        String method = "testIsNullWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        
        
        try{
            ValueObject valObj = new ValueObject(3);
            cw = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "attrChar", CpoWhere.COMP_ISNULL, null);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==6);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    
    public void testAttributeFunction() {
        String method = "testAttributeFunction:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        
        
        try{
            ValueObject valObj = new ValueObject(6);
            cw = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, valObj);
            cw.setAttributeFunction("ABS(id)");
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
          
            assertTrue("Col size is "+col.size(), col.size()==1);
            ValueObject rvo = (ValueObject) col.iterator().next();
            assertTrue(rvo.getId() == -6);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

    public void testValueFunction() {
        String method = "testValueFunction:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        
        
        try{
            ValueObject valObj = new ValueObject(-1);
            cw = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, valObj, false);
            cw.setValueFunction("abs(id)");
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
          
            assertTrue("Col size is "+col.size(), col.size()==1);
            ValueObject rvo = (ValueObject) col.iterator().next();
            assertTrue(rvo.getId() == 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

   
    public void testAndWhere() {
        String method = "testAndWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        CpoWhere cw1 = null;
        CpoWhere cw2 = null;
        
        
        try{
            ValueObject valObj = new ValueObject(3);
            cw = jdbcIdo_.newWhere();
            cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "attrChar", CpoWhere.COMP_ISNULL, null);
            cw2 = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "attrChar", CpoWhere.COMP_ISNULL, null, true);
            
            cw.addWhere(cw1);
            cw.addWhere(cw2);
            
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==0);
            
            cw = jdbcIdo_.newWhere();
            cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "attrChar", CpoWhere.COMP_ISNULL, null);
            cw2 = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "id", CpoWhere.COMP_EQ, valObj);
            
            cw.addWhere(cw1);
            cw.addWhere(cw2);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==1);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

    public void testOrWhere() {
        String method = "testOrWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        CpoWhere cw1 = null;
        CpoWhere cw2 = null;
        
        
        try{
            ValueObject valObj = new ValueObject(3);
            cw = jdbcIdo_.newWhere();
            cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, null);
            cw2 = jdbcIdo_.newWhere(CpoWhere.LOGIC_OR, "id", CpoWhere.COMP_EQ, valObj);
            
            cw1.setStaticValue("2");
            cw.addWhere(cw1);
            cw.addWhere(cw2);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==2);
            
            cw = jdbcIdo_.newWhere();
            cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "id", CpoWhere.COMP_EQ, null);
            cw2 = jdbcIdo_.newWhere(CpoWhere.LOGIC_OR, "id", CpoWhere.COMP_EQ, valObj, true);
            
            cw1.setStaticValue("3");
            cw.addWhere(cw1);
            cw.addWhere(cw2);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==6);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }


    public void testRightAttributeFunction() {
        String method = "testRightAttributeFunction:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        
        
        try{
            ValueObject valObj = new ValueObject(-1);
            cw = jdbcIdo_.newWhere();
            cw.setAttribute("id");
            cw.setRightAttribute("attrSmallInt");
            cw.setAttributeFunction("ABS(id)");
            cw.setComparison(CpoWhere.COMP_EQ);
            cw.setRightAttributeFunction("ABS(attrSmallInt)");
            cw.setLogical(CpoWhere.LOGIC_NONE);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
          
            assertTrue("Col size is "+col.size(), col.size()==1);
            ValueObject rvo = (ValueObject) col.iterator().next();
            assertTrue(rvo.getId() == 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

    public void testRightAttribute() {
        String method = "testRightAttribute:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        
        
        try{
            ValueObject valObj = new ValueObject(-1);
            cw = jdbcIdo_.newWhere();
            cw.setAttribute("id");
            cw.setRightAttribute("attrSmallInt");
            cw.setComparison(CpoWhere.COMP_EQ);
            cw.setLogical(CpoWhere.LOGIC_NONE);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
          
            assertTrue("Col size is "+col.size(), col.size()==1);
            ValueObject rvo = (ValueObject) col.iterator().next();
            assertTrue(rvo.getId() == 1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

    
    public void testMultipleBindWhere() {
        String method = "testAndWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        CpoWhere cw1 = null;
        CpoWhere cw2 = null;
        
        
        try{
            ValueObject valObj = new ValueObject(1);
            valObj.setAttrVarChar("Test");

            cw = jdbcIdo_.newWhere();
            cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "attrVarChar", CpoWhere.COMP_EQ, valObj);
            cw2 = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "id", CpoWhere.COMP_EQ, valObj);
            
            cw.addWhere(cw1);
            cw.addWhere(cw2);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==1);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    
    public void testLikeWhere() {
        String method = "testAndWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        CpoWhere cw1 = null;
        CpoWhere cw2 = null;
        
        
        try{
            ValueObject valObj = new ValueObject(1);
            valObj.setAttrVarChar("T%");

            cw = jdbcIdo_.newWhere();
            cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "attrVarChar", CpoWhere.COMP_LIKE, valObj);
            cw2 = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "id", CpoWhere.COMP_EQ, valObj);
            
            cw.addWhere(cw1);
            cw.addWhere(cw2);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==1);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    public void testLikeWhereStrings() {
        String method = "testAndWhere:";
        Collection<ValueObject> col = null;
        CpoWhere cw = null;
        CpoWhere cw1 = null;
        CpoWhere cw2 = null;
        
        
        try{
            ValueObject valObj = new ValueObject(1);
            valObj.setAttrVarChar("T%");

            cw = jdbcIdo_.newWhere();
            cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "attrVarChar", CpoWhere.COMP_LIKE, "T%");
            cw2 = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "id", CpoWhere.COMP_EQ, "1");
            
            cw.addWhere(cw1);
            cw.addWhere(cw2);
             
            col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
            
            assertTrue("Col size is "+col.size(), col.size()==1);
            
            
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
        public void testNonAttributeWhere() {
            String method = "testAndWhere:";
            Collection<ValueObject> col = null;
            CpoWhere cw = null;
            CpoWhere cw1 = null;
            
            try{
                ValueObject valObj = new ValueObject(1);

                cw = jdbcIdo_.newWhere();
                cw1 = jdbcIdo_.newWhere(CpoWhere.LOGIC_NONE, "value_object.id", CpoWhere.COMP_LT, new Integer(1));
                
                cw.addWhere(cw1);
                 
                col = jdbcIdo_.retrieveObjects("TestWhereRetrieve",valObj,valObj,cw,null);
                
                assertTrue("Col size is "+col.size(), col.size()==1);
                
                
            } catch (Exception e) {
                e.printStackTrace();
                fail(method+e.getMessage());
            }

    }
    
    
}
