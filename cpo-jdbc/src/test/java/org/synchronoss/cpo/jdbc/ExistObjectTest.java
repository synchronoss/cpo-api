/*
 *  Copyright (C) 2003-2012 David E. Berry
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

import junit.framework.TestCase;

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterBean;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoWhere;

/**
 * RetrieveObjectTest is a JUnit test class for testing the
 * JdbcAdapter class Constructors
 * 
 * @author david berry
 */

public class ExistObjectTest extends TestCase {
    
    private CpoAdapter jdbcIdo_ = null;
    
    
    public ExistObjectTest(String name) {
        super(name);
    }
    
    /**
     * <code>setUp</code>
     * Load the datasource from the properties in the property file jdbc_en_US.properties 
     * 
     * @author david berry
     * @version '$Id: ExistObjectTest.java,v 1.2 2006/01/30 19:09:23 dberry Exp $'
     */

    public void setUp() {
        String method = "setUp:";
        
        try{
          jdbcIdo_ = new CpoAdapterBean(CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT));
            assertNotNull(method+"IdoAdapter is null",jdbcIdo_);
        } catch (Exception e) {
            fail(method+e.getMessage());
        }
        ValueObject vo = new ValueObject(1);
        vo.setAttrVarChar("WHERE");
        
        try{
             jdbcIdo_.insertObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
    }
    
    public void testExistObject() {
        String method = "testExistObject:";
        
        
        try{
            ValueObject valObj = new ValueObject(1);
            long count = jdbcIdo_.existsObject(valObj);
             assertTrue("Object not Found", count==1);
        } catch (Exception e) {
            e.printStackTrace();
            fail(method+e.getMessage());
        }
        
        try{
            ValueObject valObj = new ValueObject(5);
            long count = jdbcIdo_.existsObject(valObj);
            assertTrue("Object Found", count==0);
       } catch (Exception e) {
           e.printStackTrace();
           fail(method+e.getMessage());
       }

    }

    public void testExistObjectWhere() {
      String method = "testExistObject:";
      
      
      try{
          ValueObject valObj = new ValueObject(1);
          CpoWhere where = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "attrVarChar", CpoWhere.COMP_EQ, "WHERE");
          ArrayList<CpoWhere> wheres = new ArrayList<CpoWhere>();
          wheres.add(where);
          long count = jdbcIdo_.existsObject(null,valObj, wheres);
           assertTrue("Object not Found", count==1);
      } catch (Exception e) {
          e.printStackTrace();
          fail(method+e.getMessage());
      }
      
      try{
          ValueObject valObj = new ValueObject(1);
          CpoWhere where = jdbcIdo_.newWhere(CpoWhere.LOGIC_AND, "attrVarChar", CpoWhere.COMP_EQ, "NOWHERE");
          ArrayList<CpoWhere> wheres = new ArrayList<CpoWhere>();
          wheres.add(where);
          long count = jdbcIdo_.existsObject(null,valObj,wheres);
          assertTrue("Object Found", count==0);
     } catch (Exception e) {
         e.printStackTrace();
         fail(method+e.getMessage());
     }

  }

    public void tearDown() {
        //String method="tearDown:";
        ValueObject vo = new ValueObject(1);
        try{
             jdbcIdo_.deleteObject(vo);
        } catch (Exception e) {
            e.printStackTrace();
        }
         jdbcIdo_=null;
    }

}