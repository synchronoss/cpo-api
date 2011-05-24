/**
 * BlobTest.java
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
 * license agreement you entered into with Synchronoss Technologies.
 * 
 */

package org.synchronoss.cpo.jdbc;


import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoTrxAdapter;



/**
 * BlobTest is a JUnit test class for testing the
 * JdbcAdapter class Constructors
 * 
 * @author david berry
 */

public class BlobTestTrx extends TestCase {
    private static Logger logger = LoggerFactory.getLogger(BlobTestTrx.class.getName());
    private static final String PROP_FILE="jdbcCpoFactory";
    private static final String PROP_DBDRIVER="dbDriver";
    private static final String PROP_DB_BLOBS_SUPPORTED="dbBlobsSupported";
    
    private String dbDriver_=null;
    private boolean hasBlobSupport=true;

    private CpoAdapter jdbcCpo_ = null;
    private CpoTrxAdapter jdbcIdo_ = null;

    //private byte[] anotherBlob = "This is a test of a small Blob".getBytes();
    //    private byte[] anotherBlob2 = "This is a another test of a small Blob".getBytes();
    
    private byte[] testBlob = null;
    private char[] testClob = "This is a test Clob used for testing clobs".toCharArray();
    private byte[] testBlob2 = null;
    private char[] testClob2 = "This is a second test Clob used for testing clobs".toCharArray();

    public BlobTestTrx(String name) {
        super(name);
    }
    
    /**
     * <code>setUp</code>
     * Load the datasource from the properties in the property file jdbc_en_US.properties 
     * 
     * @author david berry
     * @version '$Id: BlobTestTrx.java,v 1.2 2006/01/31 22:31:06 dberry Exp $'
     */

    public void setUp() {
    
        String method = "setUp:";
        ResourceBundle b=PropertyResourceBundle.getBundle(PROP_FILE, Locale.getDefault(),
            this.getClass().getClassLoader());
        dbDriver_=b.getString(PROP_DBDRIVER).trim();
        hasBlobSupport = new Boolean(b.getString(PROP_DB_BLOBS_SUPPORTED).trim());

        try{
            jdbcCpo_ = JdbcCpoFactory.getCpoAdapter();
            jdbcIdo_=jdbcCpo_.getCpoTrxAdapter();
            assertNotNull(method+"IdoAdapter is null",jdbcIdo_);
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }

    public void testGZipBlobInsertandDeleteTrx(){
        
        if (hasBlobSupport){
        
            testBlob = new byte[64999];
            for (int i = 0; i< 64999; i++){
                testBlob[i]= (byte)(((int)'a') + (i%26));
            }
    
            testBlob2 = new byte[64999];
            for (int i = 0; i< 64999; i++){
                testBlob2[i]=(byte)(((int)'z') - (i%26));
            }
            
            LobValueObject lvo = new LobValueObject(1,testBlob, testClob);
            LobValueObject lvo2=null;
    
            lvo.setBLob2(testBlob2);

            try{
                jdbcIdo_.deleteObject("deleteLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error deleting lob");
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                String blob1 = new String(lvo.getBLob());
                String blob2 = new String(lvo2.getBLob());
                
                assertEquals(blob1,blob2);
                
    //          String clob1 = new String(lvo.getCLob());
    //          String clob2 = new String(lvo2.getCLob());
                
    //          assertEquals(clob1,clob2);
                
            } catch (Exception ie){
                logger.error("error retrieving lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2.setBLob(testBlob2);
                lvo2.setCLob(testClob2);
                jdbcIdo_.updateObject("updateLVO",lvo2);
                jdbcIdo_.commit();

                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                String blob1 = new String(testBlob2);
                String blob2 = new String(lvo2.getBLob());
                
                assertEquals(blob1,blob2);
                
    //          String clob1 = new String(testClob2);
    //          String clob2 = new String(lvo2.getCLob());
    //          
    //          assertEquals(clob1,clob2);
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
        } else {
            fail(dbDriver_+" does not support BLOBs");
        }
        
    }


    public void testBlobInsertandDeleteTrx(){
        
        if (hasBlobSupport){
        
            testBlob = new byte[64999];
            for (int i = 0; i< 64999; i++){
                testBlob[i]= (byte)(((int)'a') + (i%26));
            }
    
            testBlob2 = new byte[64999];
            for (int i = 0; i< 64999; i++){
                testBlob2[i]=(byte)(((int)'z') - (i%26));
            }
            
            LobValueObject lvo = new LobValueObject(1,testBlob, testClob);
            LobValueObject lvo2=null;

            //lvo.setBLob2(anotherBlob);
            lvo.setBLob2(testBlob2);
    
            try{
                jdbcIdo_.deleteObject("deleteLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error deleting lob");
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                String blob1 = new String(lvo.getBLob2());
                String blob2 = new String(lvo2.getBLob2());
                
                assertEquals(blob1,blob2);
                
            } catch (Exception ie){
                logger.error("error retrieving lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2.setBLob2(testBlob);
                jdbcIdo_.updateObject("updateLVO",lvo2);
                jdbcIdo_.commit();
                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                String blob1 = new String(testBlob);
                String blob2 = new String(lvo2.getBLob2());
                
                assertEquals(blob1,blob2);
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
        } else {
            fail(dbDriver_+" does not support BLOBs");
        }
        
    }

    /*
    public void testBlobLeakage(){
    	Connection c1_=null;
    	JdbcCpoAdapter jca = null;
        
        if (hasBlobSupport){
            // Load database driver if not already loaded
        	try{
            Class.forName(dbDriver_);

            c1_ = DriverManager.getConnection(dbUrl_, dbUser_, dbPassword_);
            c1_.setAutoCommit(false);
            
            jca = new JdbcCpoAdapter(c1_);
            
        	} catch(Exception e){
        		fail(e.getLocalizedMessage());
        	}
	
            testBlob = new byte[64999];
            for (int j = 0; j< 40000; j++){
                testBlob[j]= (byte)(((int)'a') + (j%26));
            }
            
        	
        	for (int i=2; i<1000; i++){
                LobValueObject lvo = new LobValueObject(i,testBlob, testClob);
                al.add(lvo);
                try{
                    jca.insertObject("createLVO",lvo,c1_);
                    c1_.commit();
                    jca.retrieveObject("retrieveLVO",lvo,c1_);
                } catch (Exception ie){
                    logger.error("error inserting lob", ie);
                    fail(ie.getMessage());
                }
                
        	}
       }
        
    }
*/
    
    public void testEmptyGZipBlobInsertandDeleteTrx(){
        
        if (hasBlobSupport){
        
            testBlob = new byte[0];
            testBlob2 = new byte[0];
            
            LobValueObject lvo = new LobValueObject(1,testBlob, testClob);
            LobValueObject lvo2=null;
    
            lvo.setBLob2(testBlob2);

            try{
                jdbcIdo_.deleteObject("deleteLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error deleting lob");
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                byte blob1[] = lvo.getBLob();
                byte blob2[] = lvo2.getBLob();
                
                assertNotNull(blob1);
                assertNotNull(blob2);
                
                assertTrue(isEqual(blob1,blob2));
                
                
            } catch (Exception ie){
                logger.error("error retrieving lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2.setBLob(testBlob2);
                lvo2.setCLob(testClob2);
                jdbcIdo_.updateObject("updateLVO",lvo2);
                jdbcIdo_.commit();
                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                byte blob1[] = testBlob2;
                byte blob2[] = lvo2.getBLob();

                assertNotNull(blob1);
                assertNotNull(blob2);
                
                assertTrue(isEqual(blob1,blob2));
                
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
        } else {
            fail(dbDriver_+" does not support BLOBs");
        }
        
    }

    public void testNullGZipBlobInsertandDeleteTrx(){
        
        if (hasBlobSupport){
        
            LobValueObject lvo = new LobValueObject(1,null, null);
            LobValueObject lvo2=null;
    
            lvo.setBLob2(null);

            try{
                jdbcIdo_.deleteObject("deleteLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error deleting lob");
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
                jdbcIdo_.commit();
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                
                assertNull(lvo2.getBLob());
                assertNull(lvo2.getBLob2());
                assertNull(lvo2.getCLob());
               
            } catch (Exception ie){
                logger.error("error retrieving lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2.setBLob(null);
                lvo2.setCLob(null);
                jdbcIdo_.updateObject("updateLVO",lvo2);
                jdbcIdo_.commit();
                lvo2 = jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                
                assertNull(lvo2.getBLob());
                assertNull(lvo2.getBLob2());
                assertNull(lvo2.getCLob());
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
                try{jdbcIdo_.rollback();}catch(Exception e){}
                fail(ie.getMessage());
            }
        } else {
            fail(dbDriver_+" does not support BLOBs");
        }
        
    }

    

    public void tearDown() {
      try {jdbcIdo_.close();} catch (Exception e){}
      jdbcIdo_=null;
    }
    
    private boolean isEqual(byte[] b1, byte[] b2){
    	
    	if (b1==b2) return true;
    	
    	if (b1==null || b2==null) return false;
    	
    	if (b1.length != b2.length) return false;
    	
    	for (int i=0; i<b1.length; i++) {
    		if (b1[i] != b2[i]) return false;
    	}
    	
    	return true;
    	
    }

}