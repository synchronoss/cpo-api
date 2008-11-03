/**
 * RetrieveObjectTest.java
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
import java.util.Collection;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;
import org.synchronoss.cpo.CpoResultSet;
import org.synchronoss.cpo.CpoTrxAdapter;

/**
 * RetrieveObjectTest is a JUnit test class for testing the
 * JdbcAdapter class Constructors
 * 
 * @author david berry
 */

public class RetrieveObjectTest extends TestCase {
  private static Logger logger = Logger.getLogger(RetrieveObjectTest.class.getName());
    private CpoAdapter jdbcIdo_ = null;
    
    private ArrayList<ValueObject> al = new ArrayList<ValueObject>();
    
    public RetrieveObjectTest(String name) {
        super(name);
    }
    
    /**
     * <code>setUp</code>
     * Load the datasource from the properties in the property file jdbc_en_US.properties 
     * 
     * @author david berry
     * @version '$Id: RetrieveObjectTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
     */

    public void setUp() {
        String method = "setUp:";
        
        try{
          jdbcIdo_ = JdbcCpoFactory.getCpoAdapter();
            assertNotNull(method+"IdoAdapter is null",jdbcIdo_);
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
        ValueObject vo = new ValueObject(1); 
        vo.setAttrVarChar("Test");
        al.add(vo);
        al.add(new ValueObject(2));
        al.add(new ValueObject(3));
        al.add(new ValueObject(4));
        al.add(new ValueObject(5));
        al.add(new ValueObject(6));
        al.add(new ValueObject(7));
        al.add(new ValueObject(8));
        al.add(new ValueObject(9));
        al.add(new ValueObject(10));
        try{
            jdbcIdo_.insertObjects("TestOrderByInsert",al);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    
    public void testRetrieveObjects() {
        String method = "testRetrieveObjects:";
        Collection<ValueObject> col = null;
        
        
        try{
            ValueObject valObj = new ValueObject();
            col = jdbcIdo_.retrieveObjects(null,valObj,valObj,null,null);
            assertTrue("Col size is "+col.size(), col.size()==al.size());

        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    
    public void testRetrieveObjectsNoWaitSize2() {
      String method = "testRetrieveObjectsNoWaitSize2:";
      CpoResultSet<ValueObject> crs = null;
      int count=0;
      
      try{
          ValueObject valObj = new ValueObject();
          crs = jdbcIdo_.retrieveObjects(null,valObj,valObj,null,null, 2);
          logger.debug("Returned from retrieveObjects");
          for(ValueObject vo: crs){
            count++;
            logger.debug("Retrieved Object #"+count);
          }
          assertTrue("Result size is "+count, count==al.size());

      } catch (Exception e) {
          e.printStackTrace();
          fail(method+e.getMessage());
      }
  }
    public void testRetrieveObjectsNoWaitSize9() {
      String method = "testRetrieveObjectsNoWaitSize9:";
      CpoResultSet<ValueObject> crs = null;
      int count=0;
      
      try{
          ValueObject valObj = new ValueObject();
          crs = jdbcIdo_.retrieveObjects(null,valObj,valObj,null,null, 9);
          for(ValueObject vo: crs){
            count++;
          }
          assertTrue("Result size is "+count, count==al.size());

      } catch (Exception e) {
          e.printStackTrace();
          fail(method+e.getMessage());
      }
  }
    public void testRetrieveObjectsNoWaitSize10() {
      String method = "testRetrieveObjectsNoWaitSize10:";
      CpoResultSet<ValueObject> crs = null;
      int count=0;
      
      try{
          ValueObject valObj = new ValueObject();
          crs = jdbcIdo_.retrieveObjects(null,valObj,valObj,null,null, 10);
          for(ValueObject vo: crs){
            count++;
          }
          assertTrue("Result size is "+count, count==al.size());

      } catch (Exception e) {
          e.printStackTrace();
          fail(method+e.getMessage());
      }
  }
    public void testRetrieveObjectsNoWaitSize11() {
      String method = "testRetrieveObjectsNoWaitSize11:";
      CpoResultSet<ValueObject> crs = null;
      int count=0;
      
      try{
          ValueObject valObj = new ValueObject();
          crs = jdbcIdo_.retrieveObjects(null,valObj,valObj,null,null, 11);
          for(ValueObject vo: crs){
            count++;
          }
          assertTrue("Result size is "+count, count==al.size());

      } catch (Exception e) {
          e.printStackTrace();
          fail(method+e.getMessage());
      }
  }
 
    public void testConnectionBusy() {
      String method = "testConnectionBusy:";
      CpoResultSet<ValueObject> crs = null;
      int count=0;
      CpoTrxAdapter trx=null;
      
      try{
        trx = jdbcIdo_.getCpoTrxAdapter();

        ValueObject valObj = new ValueObject();
        crs = trx.retrieveObjects(null,valObj,valObj,null,null, 11);
        
        //start this trx
        for(ValueObject vo: crs){
          count++;
          break;
        }
        
        // Let's see if it lets me do two trxs at once
        try{
          Collection<ValueObject> coll = trx.retrieveObjects(null,valObj,valObj,null,null);
          fail(method+"Cpo allowed me to reuse a busy connection");
        } catch (Exception busy){
          // THis should happen
          logger.debug("Got the busy exception like expected");
        }
        
        //cleanup the first trx
        for(ValueObject vo: crs){
          count++;
        }
        assertTrue("Result size is "+count, count==al.size());

      } catch (Exception e) {
          e.printStackTrace();
          fail(method+e.getMessage());
      } finally {
        try {trx.close();} catch(Exception e){}
      }
  }

    public void testRetrieveObjectsNoWaitSize20() {
      String method = "testRetrieveObjectsNoWaitSize20:";
      CpoResultSet<ValueObject> crs = null;
      int count=0;
      
      try{
          ValueObject valObj = new ValueObject();
          crs = jdbcIdo_.retrieveObjects(null,valObj,valObj,null,null, 11);
          logger.debug("Returned from retrieveObjects");
          for(ValueObject vo: crs){
            count++;
            logger.debug("Retrieved Object #"+count);
          }
          assertTrue("Result size is "+count, count==al.size());

      } catch (Exception e) {
          e.printStackTrace();
          fail(method+e.getMessage());
      }
  }

    public void testRetrieveObject(){

        String method = "testRetrieveObject:";
        ValueObject vo = new ValueObject(1);
        ValueObject rvo = null;
        
        try{
            rvo = jdbcIdo_.retrieveObject(vo);
            assertNotNull(method+"Returned Value object is null");
            assertNotSame(method+"ValueObjects are the same",vo,rvo);
            assertEquals(method+"Strings are not the same", rvo.getAttrVarChar(),"Test");
            if (rvo.getAttrVarChar().equals(vo.getAttrVarChar())) {
                fail(method+"ValueObjects are the same");
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

    public void testNullRetrieveObject(){

        String method = "testNullRetrieveObject:";
        ValueObject vo = new ValueObject(100);
        ValueObject rvo = null;
        
        try{
            rvo = jdbcIdo_.retrieveObject(vo);
            assertNull(method+"Returned Value object is Not Null",rvo);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }

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

}