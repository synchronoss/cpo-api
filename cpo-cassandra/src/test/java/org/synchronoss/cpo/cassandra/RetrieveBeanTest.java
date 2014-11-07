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
package org.synchronoss.cpo.cassandra;

import org.junit.*;
import org.slf4j.*;
import org.synchronoss.cpo.*;

import java.util.*;

import static org.junit.Assert.*;

/**
 * RetrieveBeanTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class RetrieveBeanTest {

  private static final Logger logger = LoggerFactory.getLogger(RetrieveBeanTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @Before
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactory.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = new ValueObjectBean(1);
    vo.setAttrVarChar("Test");
    al.add(vo);
    al.add(new ValueObjectBean(2));
    al.add(new ValueObjectBean(3));
    al.add(new ValueObjectBean(4));
    al.add(new ValueObjectBean(5));
    al.add(new ValueObjectBean(6));
    al.add(new ValueObjectBean(7));
    al.add(new ValueObjectBean(8));
    al.add(new ValueObjectBean(9));
    al.add(new ValueObjectBean(10));
    try {
      cpoAdapter.insertObjects("TestOrderByInsert", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeans() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;


    try {
      ValueObject valObj = new ValueObjectBean();
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("Col size is " + col.size(), col.size() == al.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeans2() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;


    try {
      ValueObject valObj = new ValueObjectBean();
      col = cpoAdapter.retrieveBeans(null, valObj, valObj);
      assertTrue("Col size is " + col.size(), col.size() == al.size());

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
      ValueObject valObj = new ValueObjectBean();
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

  @Test
  public void testRetrieveBeansNoWaitSize9() {
    String method = "testRetrieveBeansNoWaitSize9:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObjectBean();
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

  @Test
  public void testRetrieveBeansNoWaitSize10() {
    String method = "testRetrieveBeansNoWaitSize10:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObjectBean();
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

  @Test
  public void testRetrieveBeansNoWaitSize11() {
    String method = "testRetrieveBeansNoWaitSize11:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObjectBean();
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

  @Test
  public void testRetrieveBeansNoWaitSize20() {
    String method = "testRetrieveBeansNoWaitSize20:";
    CpoResultSet<ValueObject> crs;
    int count = 0;

    try {
      ValueObject valObj = new ValueObjectBean();
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

  @Test
  public void testRetrieveBean() {

    String method = "testRetrieveBean:";
    ValueObject vo = new ValueObjectBean(1);
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

  @Test
  public void testNullRetrieveBean() {

    String method = "testNullRetrieveBean:";
    ValueObject vo = new ValueObjectBean(100);
    ValueObject rvo;

    try {
      rvo = cpoAdapter.retrieveBean(vo);
      assertNull(method + "Returned Value object is Not Null", rvo);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @After
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