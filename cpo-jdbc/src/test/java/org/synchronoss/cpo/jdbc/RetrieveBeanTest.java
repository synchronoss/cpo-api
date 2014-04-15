/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.jdbc;

import junit.framework.TestCase;
import org.slf4j.*;
import org.synchronoss.cpo.*;

import java.util.*;

/**
 * RetrieveBeanTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RetrieveBeanTest extends TestCase {

  private static final Logger logger = LoggerFactory.getLogger(RetrieveBeanTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  public RetrieveBeanTest(String name) {
    super(name);
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @Override
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
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
    try {
      cpoAdapter.insertObjects("TestOrderByInsert", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testAutoCloseable() throws CpoException {
    String method = "testAutoCloseable:";
    CpoTrxAdapter cpoAdapter1 = null;
    try (CpoTrxAdapter cpoAdapter2 = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC).getCpoTrxAdapter()) {
      cpoAdapter1 = cpoAdapter2;
      assertFalse(cpoAdapter1.isClosed());
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    assertTrue(cpoAdapter1.isClosed());
  }

  public void testRetrieveBeans() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;


    try {
      ValueObject valObj = new ValueObject();
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("Col size is " + col.size(), col.size() == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRetrieveBeans2() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;


    try {
      ValueObject valObj = new ValueObject();
      col = cpoAdapter.retrieveBeans(null, valObj, valObj);
      assertTrue("Col size is " + col.size(), col.size() == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testIsClosed() {
    String method = "testIsClosed:";
    Collection<ValueObject> col;

    try (CpoTrxAdapter trx = cpoAdapter.getCpoTrxAdapter()) {
      trx.isClosed();
      ValueObject valObj = new ValueObject();
      col = trx.retrieveBeans(null, valObj);
      assertTrue("Col size is " + col.size(), col.size() == al.size());
      trx.commit();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRetrieveBeansNoWaitSize2() {
    String method = "testRetrieveBeansNoWaitSize2:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObject();
      crs = cpoAdapter.retrieveBeans(null, valObj, valObj, null, null, null, 2);
      logger.debug("Returned from retrieveBeans");
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
        logger.debug("Retrieved Object #" + count);
      }
      assertTrue("Result size is " + count, count == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRetrieveBeansNoWaitSize9() {
    String method = "testRetrieveBeansNoWaitSize9:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObject();
      crs = cpoAdapter.retrieveBeans(null, valObj, valObj, null, null, null, 9);
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
      }
      assertTrue("Result size is " + count, count == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRetrieveBeansNoWaitSize10() {
    String method = "testRetrieveBeansNoWaitSize10:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObject();
      crs = cpoAdapter.retrieveBeans(null, valObj, valObj, null, null, null, 10);
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
      }
      assertTrue("Result size is " + count, count == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRetrieveBeansNoWaitSize11() {
    String method = "testRetrieveBeansNoWaitSize11:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObject();
      crs = cpoAdapter.retrieveBeans(null, valObj, valObj, null, null, null, 11);
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
      }
      assertTrue("Result size is " + count, count == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testConnectionBusy() {
    String method = "testConnectionBusy:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try (CpoTrxAdapter trx = cpoAdapter.getCpoTrxAdapter()) {

      ValueObject valObj = new ValueObject();
      crs = trx.retrieveBeans(null, valObj, valObj, null, null, null, 2);

      //start this trx
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
        break;
      }

      // Let's see if it lets me do two trxs at once
      try {
        trx.retrieveBeans(null, valObj);
        fail(method + "Cpo allowed me to reuse a busy connection");
      } catch (Exception busy) {
        // THis should happen
        logger.debug("Got the busy exception like expected");
      }

      //cleanup the first trx
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
      }
      assertTrue("Result size is " + count, count == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRetrieveBeansNoWaitSize20() {
    String method = "testRetrieveBeansNoWaitSize20:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObject();
      crs = cpoAdapter.retrieveBeans(null, valObj, valObj, null, null, null, 20);
      logger.debug("Returned from retrieveBeans");
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
        logger.debug("Retrieved Object #" + count);
      }
      assertTrue("Result size is " + count, count == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRetrieveBean() {

    String method = "testRetrieveBean:";
    ValueObject vo = new ValueObject(1);
    ValueObject rvo;

    try {
      rvo = cpoAdapter.retrieveBean(vo);
      assertNotNull(method + "Returned Value object is null");
      assertNotSame(method + "ValueObjects are the same", vo, rvo);
      assertEquals(method + "Strings are not the same", rvo.getAttrVarChar(), "Test");
      if (rvo.getAttrVarChar().equals(vo.getAttrVarChar())) {
        fail(method + "ValueObjects are the same");
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testNullRetrieveBean() {

    String method = "testNullRetrieveBean:";
    ValueObject vo = new ValueObject(100);
    ValueObject rvo;

    try {
      rvo = cpoAdapter.retrieveBean(vo);
      assertNull(method + "Returned Value object is Not Null", rvo);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Override
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteObjects("TestOrderByDelete", al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}