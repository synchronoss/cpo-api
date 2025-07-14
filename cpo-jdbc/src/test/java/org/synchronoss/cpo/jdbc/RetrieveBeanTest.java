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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.*;
import org.synchronoss.cpo.*;

import java.util.*;

/**
 * RetrieveBeanTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RetrieveBeanTest extends JdbcDbContainerBase {

  private static final Logger logger = LoggerFactory.getLogger(RetrieveBeanTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  public RetrieveBeanTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeEach
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(6));
    al.add(ValueObjectFactory.createValueObject(7));
    al.add(ValueObjectFactory.createValueObject(8));
    al.add(ValueObjectFactory.createValueObject(9));
    al.add(ValueObjectFactory.createValueObject(10));
    try {
      cpoAdapter.insertObjects(ValueObject.FG_CREATE_TESTORDERBYINSERT, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testLazyOpenAutoCloseLazyOpen() throws CpoException {
    String method = "testLazyOpenAutoCloseLazyOpen:";
    Collection<ValueObject> col;
    CpoTrxAdapter cpoAdapter1 = null;
    try (CpoTrxAdapter cpoAdapter2 = CpoAdapterFactoryManager.getCpoTrxAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC)) {
      cpoAdapter1 = cpoAdapter2;

      // The adapter is closed until used
      assertTrue(cpoAdapter1.isClosed());

      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = cpoAdapter1.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertEquals(col.size(), al.size(), "Col size is " + col.size());

      // The adapter is now open
      assertFalse(cpoAdapter1.isClosed());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    // The adapter should auto close
    assertTrue(cpoAdapter1.isClosed());

    // repeat the same sequence to show that the adapter can be re-used after close
    try (CpoTrxAdapter cpoAdapter2 = cpoAdapter1) {

      // The adapter should still be closed
      assertTrue(cpoAdapter2.isClosed());

      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = cpoAdapter2.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertEquals(col.size(), al.size(), "Col size is " + col.size());

      // The adapter is now open
      assertFalse(cpoAdapter2.isClosed());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    // The adapter should auto close
    assertTrue(cpoAdapter1.isClosed());

  }

  @Test
  public void testRetrieveBeans() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertTrue(col.size() == al.size(), "Col size is " + col.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeans2() {
    String method = "testRetrieveBeans2:";
    Collection<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj);
      assertTrue(col.size() == al.size(), "Col size is " + col.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testIsClosed() {
    String method = "testIsClosed:";
    Collection<ValueObject> col;

    try (CpoTrxAdapter trx = CpoAdapterFactoryManager.getCpoTrxAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC)) {
      trx.isClosed();
      ValueObject valObj = ValueObjectFactory.createValueObject();
      col = trx.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertTrue(col.size() == al.size(), "Col size is " + col.size());
      trx.commit();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeansNoWaitSize2() {
    String method = "testRetrieveBeansNoWaitSize2:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      crs = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj, null, null, null, 2);
      logger.debug("Returned from retrieveBeans");
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
        logger.debug("Retrieved Object #" + count);
      }
      assertTrue(count == al.size(), "Result size is " + count);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeansNoWaitSize9() {
    String method = "testRetrieveBeansNoWaitSize9:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      crs = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj, null, null, null, 9);
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
      }
      assertTrue(count == al.size(), "Result size is " + count);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeansNoWaitSize10() {
    String method = "testRetrieveBeansNoWaitSize10:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      crs = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj, null, null, null, 10);
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
      }
      assertTrue(count == al.size(), "Result size is " + count);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeansNoWaitSize11() {
    String method = "testRetrieveBeansNoWaitSize11:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      crs = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj, null, null, null, 11);
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
      }
      assertTrue(count == al.size(), "Result size is " + count);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testConnectionBusy() {
    String method = "testConnectionBusy:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try (CpoTrxAdapter trx = CpoAdapterFactoryManager.getCpoTrxAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC)) {

      ValueObject valObj = ValueObjectFactory.createValueObject();
      crs = trx.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj, null, null, null, 2);

      //start this trx
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
        break;
      }

      // Let's see if it lets me do two trxs at once
      try {
        trx.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
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
      assertTrue(count == al.size(), "Result size is " + count);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeansNoWaitSize20() {
    String method = "testRetrieveBeansNoWaitSize20:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();
      crs = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj, null, null, null, 20);
      logger.debug("Returned from retrieveBeans");
      for (ValueObject vo : crs) {
        if (vo != null) {
          count++;
        }
        logger.debug("Retrieved Object #" + count);
      }
      assertTrue(count == al.size(), "Result size is " + count);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBean() {
    String method = "testRetrieveBean:";
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    ValueObject rvo;

    try {
      rvo = cpoAdapter.retrieveBean(vo);
      assertNotNull(method + "Returned Value object is null");
      assertNotSame(vo, rvo, method + "ValueObjects are the same");
      assertEquals(rvo.getAttrVarChar(), "Test", method + "Strings are not the same");
      if (rvo.getAttrVarChar().equals(vo.getAttrVarChar())) {
        fail(method + "ValueObjects are the same");
      }
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNullRetrieveBean() {
    String method = "testNullRetrieveBean:";
    ValueObject vo = ValueObjectFactory.createValueObject(100);
    ValueObject rvo;

    try {
      rvo = cpoAdapter.retrieveBean(vo);
      assertNull(rvo, method + "Returned Value object is Not Null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterEach
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteObjects(ValueObject.FG_DELETE_TESTORDERBYDELETE, al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}