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

import junit.framework.TestCase;

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoWhere;

/**
 * BlobTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class InterleavedWhereTest extends TestCase {

    private CpoAdapter jdbcIdo_=null;
    private ArrayList<ValueObject> al = new ArrayList<ValueObject>();

    /**
     * Creates a new RollbackTest object.
     *
     * @param name DOCUMENT ME!
     */
    public InterleavedWhereTest() {
    }

    /**
     * <code>setUp</code> Load the datasource from the properties in the property file
     * jdbc_en_US.properties
     */
    public void setUp() {
        String method="setUp:";
        
        try {
          jdbcIdo_ = new CpoAdapterBean(CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT));
            assertNotNull(method+"CpoAdapter is null", jdbcIdo_);
        } catch(Exception e) {
            fail(method+e.getMessage());
        }
        ValueObject vo1 = new ValueObject(1);
        vo1.setAttrVarChar("Test");
        vo1.setAttrBit(true);
        al.add(vo1);
        
        ValueObject vo3 = new ValueObject(3);
        vo3.setAttrVarChar("Test");
        vo3.setAttrBit(true);
        al.add(vo3);

        ValueObject vo5 = new ValueObject(5);
        vo5.setAttrVarChar("Test");
        vo5.setAttrBit(true);
        al.add(vo5);
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
    
  public void testInterleavedInWhereCollection() {
    String method = "testInterleavedInWhereCollection:";
    Collection<ValueObject> coll = null;
    CpoWhere cw = null;
    CpoWhere cw1 = null;

    try {
      ValueObject valObj = new ValueObject(1);
      valObj.setAttrBit(true);
      valObj.setAttrVarChar("Test");
      Collection<Integer> inColl = new ArrayList<Integer>();
      inColl.add(new Integer(1));
      inColl.add(new Integer(3));
      inColl.add(new Integer(5));
      
      cw = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "id", CpoWhere.COMP_IN, inColl);

      ArrayList<CpoWhere> wheres = new ArrayList<CpoWhere>();
      wheres.add(cw);
      coll = jdbcIdo_.retrieveBeans("InterleavedWhere", valObj, wheres, null);

      assertTrue("Collection size is " + coll.size(), coll.size() == 3);


    } catch (Exception e) {
      e.printStackTrace();
      fail(method + e.getMessage());
    }

  }
}
