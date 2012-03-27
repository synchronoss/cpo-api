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

import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * RetrieveObjectTest is a JUnit test class for testing the
 * JdbcAdapter class Constructors
 * 
 * @author david berry
 */

public class BigBatchTest extends TestCase {
    private static Logger logger = LoggerFactory.getLogger(BigBatchTest.class.getName());
    private static final String PROP_FILE = "jdbcCpoFactory";

    private static final String PROP_DB_MILLI_SUPPORTED="default.dbMilliSupport";
        
    private ArrayList<ValueObject> al = new ArrayList<ValueObject>();

    private CpoAdapter jdbcIdo_ = null;
    
    private boolean hasMilliSupport = true;
    
    public BigBatchTest(String name) {
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

        hasMilliSupport = new Boolean(b.getString(PROP_DB_MILLI_SUPPORTED).trim());
        
        try{
          jdbcIdo_ = new CpoAdapterBean(new JdbcCpoFactory());
            assertNotNull(method+"IdoAdapter is null",jdbcIdo_);
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }
    
    /**
     * So oracle seems to fail on a batch size of 100,000 but does not throw an error.
     * 
     * lets try to break it to fix it to return a good message.
     * 
     */

    public void testTooManyInserts(){

        String method = "testTooManyInserts:";
        int numInserts=100000;

        for (int i=0;i<numInserts;i++){
          al.add(new ValueObject(i));
        }
        
        try{
            long inserts = jdbcIdo_.insertObjects(al);
            assertEquals("inserts performed do not equal inserts requested: ", inserts, numInserts);
        } catch (CpoException ce){
          logger.debug("Received a CpoException:"+ExceptionHelper.getLocalizedMessage(ce));
        } catch (Exception e){
          e.printStackTrace();
          fail(method+":Received an Exception instead of a CpoException: "+ExceptionHelper.getLocalizedMessage(e));
        } catch (Throwable t) {
            t.printStackTrace();
          fail(method+":Received a Throwable instead of a CpoException: "+ExceptionHelper.getLocalizedMessage(t));
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