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
 * 
 */

package org.synchronoss.cpo.jdbc;


import java.util.ArrayList;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;



/**
 * BlobTest is a JUnit test class for testing the
 * JdbcAdapter class Constructors
 * 
 * @author david berry
 */

public class BlobTest extends TestCase {
    private static Logger logger = Logger.getLogger(BlobTest.class.getName());
    
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

    
    private boolean hasBlobSupport=true;

    private CpoAdapter jdbcIdo_ = null;

    //private byte[] anotherBlob = "This is a test of a small Blob".getBytes();
    //    private byte[] anotherBlob2 = "This is a another test of a small Blob".getBytes();
    
    private byte[] testBlob = null;
    private char[] testClob = "This is a test Clob used for testing clobs".toCharArray();
    private byte[] testBlob2 = null;
    private char[] testClob2 = "This is a second test Clob used for testing clobs".toCharArray();
    ArrayList al = new ArrayList();

    public BlobTest(String name) {
        super(name);
    }
    
    /**
     * <code>setUp</code>
     * Load the datasource from the properties in the property file jdbc_en_US.properties 
     * 
     * @author david berry
     * @version '$Id: BlobTest.java,v 1.15 2006/02/15 18:34:19 dberry Exp $'
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
        
        if ("org.hsqldb.jdbcDriver".equals(dbDriver_)){
            hasBlobSupport = false;
        }
        
        
        try{
            jdbcIdo_ = new CpoAdapterBean(new JdbcCpoAdapter(new JdbcDataSourceInfo(metaDriver_,metaUrl_, metaUser_, metaPassword_,1,1,false),new JdbcDataSourceInfo(dbDriver_,dbUrl_, dbUser_, dbPassword_,1,1,false)));
            assertNotNull(method+"IdoAdapter is null",jdbcIdo_);
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
    }

    public void testGZipBlobInsertandDelete(){
        
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
            } catch (Exception ie){
                logger.error("error deleting lob");
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                String blob1 = new String(lvo.getBLob());
                String blob2 = new String(lvo2.getBLob());
                
                assertEquals(blob1,blob2);
                
              String clob1 = new String(lvo.getCLob());
              String clob2 = new String(lvo2.getCLob());
                
              assertEquals(clob1,clob2);
                
            } catch (Exception ie){
                logger.error("error retrieving lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2.setBLob(testBlob2);
                lvo2.setCLob(testClob2);
                jdbcIdo_.updateObject("updateLVO",lvo2);
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                String blob1 = new String(testBlob2);
                String blob2 = new String(lvo2.getBLob());
                
                assertEquals(blob1,blob2);
                
              String clob1 = new String(testClob2);
              String clob2 = new String(lvo2.getCLob());
              
              assertEquals(clob1,clob2);
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
                fail(ie.getMessage());
            }
        } else {
            fail(dbDriver_+" does not support BLOBs");
        }
        
    }


    public void testBlobInsertandDelete(){
        
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
            } catch (Exception ie){
                logger.error("error deleting lob");
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
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
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                String blob1 = new String(testBlob);
                String blob2 = new String(lvo2.getBLob2());
                
                assertEquals(blob1,blob2);
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
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
    
    public void testEmptyGZipBlobInsertandDelete(){
        
        if (hasBlobSupport){
        
            testBlob = new byte[0];
            testBlob2 = new byte[0];
            
            LobValueObject lvo = new LobValueObject(1,testBlob, testClob);
            LobValueObject lvo2=null;
    
            lvo.setBLob2(testBlob2);

            try{
                jdbcIdo_.deleteObject("deleteLVO",lvo);
            } catch (Exception ie){
                logger.error("error deleting lob");
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
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
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                byte blob1[] = testBlob2;
                byte blob2[] = lvo2.getBLob();

                assertNotNull(blob1);
                assertNotNull(blob2);
                
                assertTrue(isEqual(blob1,blob2));
                
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
                fail(ie.getMessage());
            }
        } else {
            fail(dbDriver_+" does not support BLOBs");
        }
        
    }

    public void testNullGZipBlobInsertandDelete(){
        
        if (hasBlobSupport){
        
            LobValueObject lvo = new LobValueObject(1,null, null);
            LobValueObject lvo2=null;
    
            lvo.setBLob2(null);

            try{
                jdbcIdo_.deleteObject("deleteLVO",lvo);
            } catch (Exception ie){
                logger.error("error deleting lob");
                fail(ie.getMessage());
            }
            
            try{
                jdbcIdo_.insertObject("createLVO",lvo);
            } catch (Exception ie){
                logger.error("error inserting lob", ie);
                fail(ie.getMessage());
            }
            
            try{
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                
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
                lvo2 = (LobValueObject) jdbcIdo_.retrieveObject("retrieveLVO",lvo);
                
                assertNull(lvo2.getBLob());
                assertNull(lvo2.getBLob2());
                assertNull(lvo2.getCLob());
                
            } catch (Exception ie){
                logger.error("error updating lob", ie);
                fail(ie.getMessage());
            }
        } else {
            fail(dbDriver_+" does not support BLOBs");
        }
        
    }

    

    public void tearDown() {
        if (!al.isEmpty()){
            try{
                jdbcIdo_.deleteObjects("deleteLVO",al);
            } catch (Exception ie){
                logger.error("error deleting lobs");
                fail(ie.getMessage());
            }
        }
        
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