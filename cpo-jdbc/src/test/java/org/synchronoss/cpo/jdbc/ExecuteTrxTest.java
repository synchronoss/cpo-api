/**
 * ExecuteTest.java  
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

import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ExecuteTrxTest extends TestCase {
    private static Logger logger = LoggerFactory.getLogger(ExecuteTrxTest.class.getName());
    private static final String PROP_FILE="jdbcCpoFactory";
    private static final String PROP_DBDRIVER="default.dbDriver";
    private static final String PROP_DB_CALLS_SUPPORTED="default.dbCallsSupported";

    private CpoAdapter jdbcIdo_=null;
    private String dbDriver_=null;
    private boolean hasCallSupport = true;

    /**
     * Creates a new RollbackTest object.
     *
     * @param name DOCUMENT ME!
     */
    public ExecuteTrxTest() {
    }

    /**
     * <code>setUp</code> Load the datasource from the properties in the property file
     * jdbc_en_US.properties
     */
    public void setUp() {
        String method="setUp:";
        ResourceBundle b=PropertyResourceBundle.getBundle(PROP_FILE, Locale.getDefault(),
                this.getClass().getClassLoader());
        dbDriver_=b.getString(PROP_DBDRIVER).trim();
        hasCallSupport = new Boolean(b.getString(PROP_DB_CALLS_SUPPORTED).trim());
        
        try {
          jdbcIdo_ = new CpoAdapterBean(new JdbcCpoFactory());
            assertNotNull(method+"CpoAdapter is null", jdbcIdo_);
        } catch(Exception e) {
            fail(method+e.getMessage());
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
    public void testExecuteTrx() {
        if (hasCallSupport==true){
            String method = "testExecuteObject:";
            ValueObject vo = new ValueObject(1);
            vo.setAttrInteger(3);
            ValueObject rvo = null;
            
            try{
                rvo = (ValueObject) jdbcIdo_.executeObject("TestExecuteObject",vo);
                assertNotNull(method+"Returned Value object is null");
                assertTrue("power(3,3)="+rvo.getAttrDouble(),rvo.getAttrDouble()==27);
            } catch (Exception e) {
                e.printStackTrace();
                fail(method+e.getMessage());
            }


            try{
                vo = new ValueObject(1);
                vo.setAttrSmallInt(3);
                rvo = (ValueObject) jdbcIdo_.executeObject("TestExecuteObjectNoTransform",vo);
                assertNotNull(method+"Returned Value object is null");
                assertTrue("power(3,3)="+rvo.getAttrDouble(),rvo.getAttrDouble()==27);
            } catch (Exception e) {
                e.printStackTrace();
                fail(method+e.getMessage());
            }
        } else {
        	logger.error(dbDriver_+" does not support CallableStatements");
        }
    }
    
}
