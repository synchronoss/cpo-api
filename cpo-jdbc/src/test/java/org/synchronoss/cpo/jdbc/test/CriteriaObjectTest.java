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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;

import java.util.ArrayList;
import java.util.Collection;

import org.synchronoss.cpo.jdbc.*;
import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * RetrieveBeanTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class CriteriaObjectTest {

  private static final Logger logger = LoggerFactory.getLogger(CriteriaObjectTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();
  private boolean isSupportsCalls = Boolean.valueOf(JdbcTestProperty.getProperty(JdbcTestProperty.PROP_CALLS_SUPPORTED));
  private final String className = this.getClass().getSimpleName();

  public CriteriaObjectTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
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
    ValueObject vo = ValueObjectFactory.createValueObject(81, className);
    vo.setAttrVarChar("Test");
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(82, className));
    al.add(ValueObjectFactory.createValueObject(83, className));
    al.add(ValueObjectFactory.createValueObject(84, className));
    al.add(ValueObjectFactory.createValueObject(85, className));
    al.add(ValueObjectFactory.createValueObject(86, className));
    al.add(ValueObjectFactory.createValueObject(87, className));
    al.add(ValueObjectFactory.createValueObject(88, className));
    al.add(ValueObjectFactory.createValueObject(89, className));
    al.add(ValueObjectFactory.createValueObject(90, className));
    try {
      cpoAdapter.insertObjects(ValueObjectBean.FG_CREATE_TESTORDERBYINSERT, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRetrieveBeanWithCriteria() {
    String method = "testRetrieveBeanWithCriteria:";
    Collection<ValueObject> col;

    try {
      CriteriaObject critObject = new CriteriaObjectBean();
      critObject.setMinId(83);
      critObject.setMaxId(87);
      col = cpoAdapter.retrieveBeans(CriteriaObject.FG_LIST_SELECTBETWEEN, critObject, new ValueObjectBean());
      assertEquals(col.size(), 5, "Col size is " + col.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testDynamicWhereWithCriteria() {
    String method = "testDynamicWhereWithCriteria:";

    try {
      CriteriaObject critObject = new CriteriaObjectBean();
      critObject.setMinId(83);
      critObject.setMaxId(87);
      CpoWhere cw = cpoAdapter.newWhere();
      CpoWhere cw1 = cpoAdapter.newWhere(CpoWhere.LOGIC_NONE, CriteriaObject.ATTR_MINID, CpoWhere.COMP_GT, critObject);
      CpoWhere cw2 = cpoAdapter.newWhere(CpoWhere.LOGIC_AND, CriteriaObject.ATTR_MAXID, CpoWhere.COMP_LT, critObject);

      cw.addWhere(cw1);
      cw.addWhere(cw2);

      ArrayList<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cw);
      Collection<ValueObject> col = cpoAdapter.retrieveBeans(CriteriaObject.FG_LIST_SELECTALL, critObject, new ValueObjectBean(), wheres, null);

      assertEquals(col.size(), 3, "Col size is " + col.size());

    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testExecuteWithCriteria() {
    if (isSupportsCalls) {
      String method = "testExecuteWithCriteria:";
      CriteriaObject critObject = new CriteriaObjectBean();
      critObject.setInt1(3);
      critObject.setInt2(3);
      ValueObject rvo;

      try {
        rvo = cpoAdapter.executeObject(CriteriaObject.FG_EXECUTE_EXECUTECRITERIA, critObject, new ValueObjectBean());
        assertNotNull(method + "Returned Value object is null");
        assertEquals(rvo.getAttrDouble(), 27, "power(3,3)=" + rvo.getAttrDouble());
      } catch (Exception e) {
        logger.error(ExceptionHelper.getLocalizedMessage(e));
        fail(method + e.getMessage());
      }
    }
  }

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
}