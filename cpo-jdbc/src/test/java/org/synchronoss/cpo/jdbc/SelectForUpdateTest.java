/**
 * SelectForUpdateTest.java  
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
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class SelectForUpdateTest extends TestCase {
	private static final Logger logger = LoggerFactory.getLogger(SelectForUpdateTest.class.getName());
    private CpoAdapter jdbcCpo_=null;
    private CpoTrxAdapter jdbcIdo_=null;
    private String dbDriver_=null;
    private boolean hasSelect4UpdateSupport = true;
    

    /**
     * Creates a new RollbackTest object.
     *
     * @param name DOCUMENT ME!
     */
    public SelectForUpdateTest() {
    }

    /**
     * <code>setUp</code> Load the datasource from the properties in the property file
     * jdbc_en_US.properties
     */
    public void setUp() {
        String method="setUp:";
        ResourceBundle b=PropertyResourceBundle.getBundle(JdbcStatics.PROP_FILE, Locale.getDefault(),
                this.getClass().getClassLoader());
        dbDriver_=b.getString(JdbcStatics.PROP_DBDRIVER).trim();
        
        hasSelect4UpdateSupport = new Boolean(b.getString(JdbcStatics.PROP_DB_SELECT4UPDATE).trim());
        
        try {
          jdbcCpo_ = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT);
            assertNotNull(method+"CpoAdapter is null", jdbcCpo_);
            jdbcIdo_ = jdbcCpo_.getCpoTrxAdapter();
            assertNotNull(method+"CpoTrxAdapter is null", jdbcIdo_);
        } catch(Exception e) {
            logger.debug(ExceptionHelper.getLocalizedMessage(e));
        }
        ValueObject vo = new ValueObject(1);
        ValueObject vo2 = new ValueObject(2);
        try{
            jdbcIdo_.insertObject(vo);
            jdbcIdo_.insertObject(vo2);
             jdbcIdo_.commit();
        } catch (Exception e) {
            try{jdbcIdo_.rollback();} catch (Exception e1){};
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void tearDown() {
        ValueObject vo = new ValueObject(1);
        ValueObject vo2 = new ValueObject(2);
       try{
           jdbcIdo_.deleteObject(vo);
           jdbcIdo_.deleteObject(vo2);
             jdbcIdo_.commit();
        } catch (Exception e) {
            try{jdbcIdo_.rollback();} catch (Exception e1){};
            e.printStackTrace();
        } finally {
            try{jdbcIdo_.close();}catch (Exception e1){}
            jdbcIdo_=null;
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void testSelect4UpdateSingleObject() {
        if (hasSelect4UpdateSupport) {
            String method = "testSelect4UpdateSingleObject:";
            ValueObject vo2 = new ValueObject(1);
 
            try{
                jdbcIdo_.retrieveObject("SelectForUpdate",vo2);
            } catch (Exception e) {
                fail(method+"Select For Update should work:"+ExceptionHelper.getLocalizedMessage(e));
            }
            
            try{
                jdbcIdo_.retrieveObject("SelectForUpdate",vo2);
            } catch (Exception e) {
                fail(method+"Select For Update should work:"+ExceptionHelper.getLocalizedMessage(e));
            }

            try{
                jdbcCpo_.retrieveObject("Select4UpdateNoWait",vo2);
                fail(method+"SelectForUpdateNoWait should fail:");
            } catch (Exception e) {
                logger.debug(ExceptionHelper.getLocalizedMessage(e));
            }

            try{
                jdbcIdo_.commit();
            } catch (Exception e) {
                try {
                    jdbcIdo_.rollback();
                } catch (CpoException ce){
                    fail(method+"Rollback failed:"+ExceptionHelper.getLocalizedMessage(e));

                }
                fail(method+"Commit should have worked.");
            }
            try{
                jdbcCpo_.retrieveObject("Select4UpdateNoWait",vo2);
            } catch (Exception e) {
                fail(method+"SelectForUpdateNoWait should success:"+ExceptionHelper.getLocalizedMessage(e));
            }
        } else {
        	logger.error(dbDriver_+" does not support Select For Update");
        }
    }
    
    public void testSelect4UpdateExists() {
        if (hasSelect4UpdateSupport) {
            String method = "testSelect4UpdateExists:";
            ValueObject vo2 = new ValueObject(1);
 
            try{
            	long count = jdbcIdo_.existsObject("SelectForUpdateExistZero",vo2);
                assertTrue("Zero objects should have been returned", count==0);
            } catch (Exception e) {
                fail(method+"Select For Update should work:"+ExceptionHelper.getLocalizedMessage(e));
            }

            try{
                long count = jdbcIdo_.existsObject("SelectForUpdateExistSingle",vo2);
                assertTrue("One object should have been returned, got "+count, count==1);
            } catch (Exception e) {
                fail(method+"Select For Update should work:"+ExceptionHelper.getLocalizedMessage(e));
            }
            
            try{
            	long count = jdbcIdo_.existsObject("SelectForUpdateExistAll",vo2);
                assertTrue("Two objects should have been returned, got "+count, count==2);
            } catch (Exception e) {
                fail(method+"Select For Update should work:"+ExceptionHelper.getLocalizedMessage(e));
            }

            try{
                jdbcIdo_.commit();
            } catch (Exception e) {
                try {
                    jdbcIdo_.rollback();
                } catch (CpoException ce){
                    fail(method+"Rollback failed:"+ExceptionHelper.getLocalizedMessage(ce));

                }
                fail(method+"Commit should have worked.");
            }
        } else {
        	logger.error(dbDriver_+" does not support Select For Update");
        }
    }    
 
}
