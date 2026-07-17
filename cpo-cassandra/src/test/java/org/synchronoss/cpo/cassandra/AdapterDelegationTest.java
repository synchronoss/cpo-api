package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.CpoNativeFunction;
import org.synchronoss.cpo.core.CpoOrderBy;
import org.synchronoss.cpo.core.CpoWhere;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Exercises every CpoAdapter CRUD overload through the CassandraCpoAdapter, mirroring the
 * delegation coverage of the JDBC XA adapter tests.
 */
public class AdapterDelegationTest {
  private CpoAdapter cpoAdapter = null;
  private final ArrayList<ValueObject> al = new ArrayList<>();

  public AdapterDelegationTest() {}

  @BeforeClass
  public void setUp() {
    String method = "setUp:";
    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @AfterMethod
  public void tearDown() {
    String method = "tearDown:";
    try {
      cpoAdapter.deleteBeans(al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    } finally {
      al.clear();
    }
  }

  /** Exercises every insertBean/insertBeans overload and the existsBean overloads. */
  @Test
  public void testInsertAndExistsDelegation() {
    String method = "testInsertAndExistsDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(61);
    ValueObject vo2 = ValueObjectFactory.createValueObject(62);
    ValueObject vo3 = ValueObjectFactory.createValueObject(63);
    ValueObject vo4 = ValueObjectFactory.createValueObject(64);
    ValueObject vo5 = ValueObjectFactory.createValueObject(65);
    ValueObject vo6 = ValueObjectFactory.createValueObject(66);
    ValueObject vo7 = ValueObjectFactory.createValueObject(67);
    vo3.setAttrInt(3);
    al.add(vo1);
    al.add(vo2);
    al.add(vo3);
    al.add(vo4);
    al.add(vo5);
    al.add(vo6);
    al.add(vo7);

    try {
      cpoAdapter.insertBean(vo1);
      cpoAdapter.insertBean(ValueObject.FG_CREATE_NULL, vo2);
      cpoAdapter.insertBean(ValueObject.FG_CREATE_NULL, vo3, null, null, null);

      List<ValueObject> beans1 = new ArrayList<>();
      beans1.add(vo4);
      cpoAdapter.insertBeans(beans1);

      List<ValueObject> beans2 = new ArrayList<>();
      beans2.add(vo5);
      cpoAdapter.insertBeans(ValueObject.FG_CREATE_NULL, beans2);

      List<ValueObject> beans3 = new ArrayList<>();
      beans3.add(vo6);
      beans3.add(vo7);
      cpoAdapter.insertBeans(ValueObject.FG_CREATE_NULL, beans3, null, null, null);

      assertEquals(cpoAdapter.existsBean(vo1), 1, method + "vo1 should exist");
      assertEquals(cpoAdapter.existsBean(ValueObject.FG_EXIST_NULL, vo2), 1);

      Collection<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cpoAdapter.newWhere(Logical.AND, "attrInt", Comparison.EQ, 3));
      assertEquals(cpoAdapter.existsBean(ValueObject.FG_EXIST_NULL, vo3, wheres), 1);
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises every retrieveBean/retrieveBeans overload. */
  @Test
  public void testRetrieveDelegation() {
    String method = "testRetrieveDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(71);
    ValueObject vo2 = ValueObjectFactory.createValueObject(72);
    al.add(vo1);
    al.add(vo2);

    try {
      List<ValueObject> beans = new ArrayList<>();
      beans.add(vo1);
      beans.add(vo2);
      cpoAdapter.insertBeans(beans);

      CpoWhere where = cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 71);
      Collection<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 71));
      ValueObject criteria = ValueObjectFactory.createValueObject();

      // retrieveBean overloads
      assertNotNull(
          cpoAdapter.retrieveBean(ValueObjectFactory.createValueObject(71)),
          method + "retrieveBean(bean) returned null");
      assertNotNull(
          cpoAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL, ValueObjectFactory.createValueObject(71)),
          method + "retrieveBean(group, bean) returned null");
      assertNotNull(
          cpoAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL,
              ValueObjectFactory.createValueObject(71),
              (Collection<CpoWhere>) null,
              (Collection<CpoOrderBy>) null,
              (Collection<CpoNativeFunction>) null),
          method + "retrieveBean(group, bean, wheres, orderBy, native) returned null");
      assertNotNull(
          cpoAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL,
              ValueObjectFactory.createValueObject(71),
              ValueObjectFactory.createValueObject(),
              null,
              null),
          method + "retrieveBean(group, criteria, result, wheres, orderBy) returned null");
      assertNotNull(
          cpoAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL,
              ValueObjectFactory.createValueObject(71),
              ValueObjectFactory.createValueObject(),
              null,
              null,
              null),
          method + "retrieveBean 6-arg criteria overload returned null");

      // retrieveBeans overloads
      try (Stream<ValueObject> s = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, criteria)) {
        assertEquals(s.count(), 2);
      }
      try (Stream<ValueObject> s =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, criteria, where, null)) {
        assertEquals(s.count(), 1);
      }
      try (Stream<ValueObject> s =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_NULL, criteria, (Collection<CpoOrderBy>) null)) {
        assertEquals(s.count(), 2);
      }
      try (Stream<ValueObject> s =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_TESTWHERERETRIEVE, criteria, wheres, null)) {
        assertEquals(s.count(), 1);
      }
      try (Stream<ValueObject> s =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_NULL, criteria, ValueObjectFactory.createValueObject())) {
        assertEquals(s.count(), 2);
      }
      try (Stream<ValueObject> s =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_TESTWHERERETRIEVE,
              criteria,
              ValueObjectFactory.createValueObject(),
              where,
              null)) {
        assertEquals(s.count(), 1);
      }
      try (Stream<ValueObject> s =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_TESTWHERERETRIEVE,
              criteria,
              ValueObjectFactory.createValueObject(),
              wheres,
              null)) {
        assertEquals(s.count(), 1);
      }
      try (Stream<ValueObject> s =
          cpoAdapter.retrieveBeans(
              ValueObject.FG_LIST_NULL,
              criteria,
              ValueObjectFactory.createValueObject(),
              null,
              null,
              null)) {
        assertEquals(s.count(), 2);
      }
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises every updateBean/updateBeans and deleteBean/deleteBeans overload. */
  @Test
  public void testUpdateAndDeleteDelegation() {
    String method = "testUpdateAndDeleteDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(81);
    ValueObject vo2 = ValueObjectFactory.createValueObject(82);
    ValueObject vo3 = ValueObjectFactory.createValueObject(83);
    ValueObject vo4 = ValueObjectFactory.createValueObject(84);
    ValueObject vo5 = ValueObjectFactory.createValueObject(85);
    ValueObject vo6 = ValueObjectFactory.createValueObject(86);
    al.add(vo1);
    al.add(vo2);
    al.add(vo3);
    al.add(vo4);
    al.add(vo5);
    al.add(vo6);

    try {
      cpoAdapter.insertBean(vo1);

      // The null UPDATE group's expression is "update value_object set attr_int=?" with no
      // WHERE clause — CQL requires one, so updates only work with programmatic wheres.
      vo1.setAttrInt(9);
      Collection<CpoWhere> cws = new ArrayList<>();
      cws.add(cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 81));
      cpoAdapter.updateBean(ValueObject.FG_UPDATE_NULL, vo1, cws, null, null);

      List<ValueObject> updBeans = new ArrayList<>();
      updBeans.add(vo1);
      Collection<CpoWhere> cws2 = new ArrayList<>();
      cws2.add(cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 81));
      cpoAdapter.updateBeans(ValueObject.FG_UPDATE_NULL, updBeans, cws2, null, null);

      ValueObject rvo = cpoAdapter.retrieveBean(ValueObjectFactory.createValueObject(81));
      assertEquals(rvo.getAttrInt(), 9, method + "update did not stick");

      // without wheres the missing WHERE clause must surface as a CpoException
      expectThrows(CpoException.class, () -> cpoAdapter.updateBean(vo1));
      expectThrows(
          CpoException.class, () -> cpoAdapter.updateBean(ValueObject.FG_UPDATE_NULL, vo1));
      expectThrows(CpoException.class, () -> cpoAdapter.updateBeans(updBeans));
      expectThrows(
          CpoException.class, () -> cpoAdapter.updateBeans(ValueObject.FG_UPDATE_NULL, updBeans));

      // delete overloads
      cpoAdapter.deleteBean(vo1);
      assertEquals(cpoAdapter.existsBean(vo1), 0, method + "vo1 should be deleted");

      cpoAdapter.insertBean(vo2);
      cpoAdapter.deleteBean(ValueObject.FG_DELETE_NULL, vo2);

      cpoAdapter.insertBean(vo3);
      cpoAdapter.deleteBean(ValueObject.FG_DELETE_NULL, vo3, null, null, null);

      cpoAdapter.insertBean(vo4);
      List<ValueObject> delBeans1 = new ArrayList<>();
      delBeans1.add(vo4);
      cpoAdapter.deleteBeans(delBeans1);

      cpoAdapter.insertBean(vo5);
      List<ValueObject> delBeans2 = new ArrayList<>();
      delBeans2.add(vo5);
      cpoAdapter.deleteBeans(ValueObject.FG_DELETE_NULL, delBeans2);

      cpoAdapter.insertBean(vo6);
      List<ValueObject> delBeans3 = new ArrayList<>();
      delBeans3.add(vo6);
      cpoAdapter.deleteBeans(ValueObject.FG_DELETE_NULL, delBeans3, null, null, null);

      assertEquals(cpoAdapter.existsBean(vo6), 0, method + "vo6 should be deleted");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises the upsertBean/upsertBeans overloads: insert when absent, update when present. */
  @Test
  public void testUpsertDelegation() {
    String method = "testUpsertDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(91);
    ValueObject vo2 = ValueObjectFactory.createValueObject(92);
    ValueObject vo3 = ValueObjectFactory.createValueObject(93);
    ValueObject vo4 = ValueObjectFactory.createValueObject(94);
    al.add(vo1);
    al.add(vo2);
    al.add(vo3);
    al.add(vo4);

    try {
      // absent beans take the CREATE branch of the upsert
      cpoAdapter.upsertBean(vo1);
      assertEquals(cpoAdapter.existsBean(vo1), 1, method + "upserted bean should exist");
      cpoAdapter.upsertBean((String) null, vo2);
      assertEquals(cpoAdapter.existsBean(vo2), 1);

      List<ValueObject> beans3 = new ArrayList<>();
      beans3.add(vo3);
      cpoAdapter.upsertBeans(beans3);
      assertEquals(cpoAdapter.existsBean(vo3), 1);

      List<ValueObject> beans4 = new ArrayList<>();
      beans4.add(vo4);
      cpoAdapter.upsertBeans((String) null, beans4);
      assertEquals(cpoAdapter.existsBean(vo4), 1);

      // an existing bean takes the UPDATE branch, and the null UPDATE group's expression
      // has no WHERE clause, so it must surface as a CpoException (see update test)
      expectThrows(CpoException.class, () -> cpoAdapter.upsertBean(vo1));
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Execute functions are not supported in Cassandra; every overload must say so. */
  @Test
  public void testExecuteUnsupported() {
    ValueObject vo = ValueObjectFactory.createValueObject(95);
    expectThrows(UnsupportedOperationException.class, () -> cpoAdapter.executeBean(vo));
    expectThrows(UnsupportedOperationException.class, () -> cpoAdapter.executeBean("AnyGroup", vo));
    expectThrows(
        UnsupportedOperationException.class, () -> cpoAdapter.executeBean("AnyGroup", vo, vo));
  }

  /** Transaction and XA adapters are not supported by the Cassandra adapter factory. */
  @Test
  public void testTrxAndXaUnsupported() {
    String method = "testTrxAndXaUnsupported:";
    try {
      CpoAdapterFactoryManager.getCpoTrxAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      fail(method + "getCpoTrxAdapter should not be supported");
    } catch (UnsupportedOperationException | CpoException expected) {
      // expected
    }
    try {
      CpoAdapterFactoryManager.getCpoXaAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      fail(method + "getCpoXaAdapter should not be supported");
    } catch (UnsupportedOperationException | CpoException expected) {
      // expected
    }
  }

  /** Exercises the newWhere and newOrderBy factory overloads. */
  @Test
  public void testWhereAndOrderByFactories() {
    String method = "testWhereAndOrderByFactories:";
    try {
      assertNotNull(cpoAdapter.newWhere(), method + "newWhere() is null");
      assertNotNull(cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 1));
      assertNotNull(cpoAdapter.newWhere(Logical.NONE, "id", Comparison.EQ, 1, true));

      assertNotNull(cpoAdapter.newOrderBy("id", true));
      assertNotNull(cpoAdapter.newOrderBy("marker", "id", true));
      assertNotNull(cpoAdapter.newOrderBy("id", true, null));
      assertNotNull(cpoAdapter.newOrderBy("marker", "id", true, null));
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises the accessor methods. */
  @Test
  public void testAccessors() {
    String method = "testAccessors:";
    try {
      assertNotNull(cpoAdapter.getCpoMetaDescriptor(), method + "meta descriptor is null");
      assertNotNull(cpoAdapter.getDataSourceName(), method + "datasource name is null");

      int fetchSize = cpoAdapter.getFetchSize();
      cpoAdapter.setFetchSize(fetchSize);
      int batchSize = cpoAdapter.getBatchSize();
      cpoAdapter.setBatchSize(batchSize);

      assertNotNull(
          cpoAdapter.getCpoAttributes("select * from value_object"),
          method + "getCpoAttributes is null");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }
}
