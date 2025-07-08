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
package org.synchronoss.cpo.jdbc.test;

import java.util.ArrayList;
import java.util.Collection;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoNativeFunction;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.jdbc.JdbcStatics;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.synchronoss.cpo.jdbc.ValueObjectFactory;
import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * BlobTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class NativeExpressionTest {

  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();
  private final String className = this.getClass().getSimpleName();

  public NativeExpressionTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(61, className);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt((short)1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(62, className));
    al.add(ValueObjectFactory.createValueObject(63, className));
    al.add(ValueObjectFactory.createValueObject(64, className));
    al.add(ValueObjectFactory.createValueObject(65, className));
    al.add(ValueObjectFactory.createValueObject(-66, className));
    try {
      cpoAdapter.insertObjects(ValueObject.FG_CREATE_TESTORDERBYINSERT, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  /**
   * DOCUMENT ME!
   */
  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteObjects(ValueObject.FG_DELETE_TESTORDERBYDELETE, al);

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }

  /**
   * DOCUMENT ME!
   */
  @Test
  public void testNativeOrWhere() {
    String method = "testNativeOrWhere:";
    Collection<ValueObject> col;
    CpoWhere cw = null;
    CpoWhere cw1 = null;
    CpoWhere cw2 = null;

    try {
      ArrayList<CpoNativeFunction> cnqAl = new ArrayList<>();

      cnqAl.add(new CpoNativeFunction("__CPO_WHERE__", "WHERE ID = 62 OR ID = 63"));

      ValueObject valObj = ValueObjectFactory.createValueObject(63, className);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, valObj, valObj, null, null, cnqAl);

      int count = 0;
      for (ValueObject rvo : col){
        if (className.equals(rvo.getName()))
          count++;
      }
      assertEquals(count, 2, "Col size is " + col.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testNullNative() {
    String method = "testNullNative:";
    Collection<ValueObject> col;
    CpoWhere cw = null;
    CpoWhere cw1 = null;
    CpoWhere cw2 = null;

    try {
      ArrayList<CpoNativeFunction> cnqAl = new ArrayList<>();

      cnqAl.add(new CpoNativeFunction("__CPO_WHERE__", null));

      ValueObject valObj = ValueObjectFactory.createValueObject(63, className);
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTNULLWHERERETRIEVE, valObj, valObj, null, null, cnqAl);

      assertEquals(col.size(), 6, "Col size is " + col.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }
}
