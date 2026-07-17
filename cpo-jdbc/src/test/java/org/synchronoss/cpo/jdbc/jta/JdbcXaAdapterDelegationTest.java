package org.synchronoss.cpo.jdbc.jta;

/*-
 * [[
 * jdbc
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
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.CpoNativeFunction;
import org.synchronoss.cpo.core.CpoOrderBy;
import org.synchronoss.cpo.core.CpoWhere;
import org.synchronoss.cpo.core.enums.Comparison;
import org.synchronoss.cpo.core.enums.Logical;
import org.synchronoss.cpo.core.helper.ExceptionHelper;
import org.synchronoss.cpo.core.jta.CpoXaError;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.synchronoss.cpo.jdbc.adapter.JdbcStatics;
import org.synchronoss.cpo.jdbc.adapter.ValueObjectFactory;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/**
 * Exercises the CpoTrxAdapter delegation methods of JdbcCpoXaAdapter in local (non-global
 * transaction) mode, plus the global-transaction commit/rollback/isClosed paths.
 */
public class JdbcXaAdapterDelegationTest {

  // unique id base so this class's rows never collide with another test class's
  private static final int IDB = 2500000;
  private static final Logger logger = LoggerFactory.getLogger(JdbcXaAdapterDelegationTest.class);
  private CpoAdapter cpoAdapter = null;
  private JdbcCpoXaAdapter cpoXaAdapter = null;
  private final ArrayList<ValueObject> al = new ArrayList<>();
  private boolean isXaSupported = true;
  private boolean isSupportsCalls = true;

  public JdbcXaAdapterDelegationTest() {}

  @Parameters({"db.xasupport", "db.callsupport"})
  @BeforeMethod
  public void setUp(boolean xaSupport, boolean callSupport) {
    String method = "setUp:";
    isXaSupported = xaSupport;
    isSupportsCalls = callSupport;

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      cpoXaAdapter =
          (JdbcCpoXaAdapter)
              CpoAdapterFactoryManager.getCpoXaAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      assertNotNull(cpoXaAdapter, method + "cpoXaAdapter is null");
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

    cpoAdapter = null;
    cpoXaAdapter = null;
  }

  /** Exercises every insertBean/insertBeans overload and the existsBean overloads. */
  @Test
  public void testInsertAndExistsDelegation() {
    String method = "testInsertAndExistsDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(IDB + 1);
    ValueObject vo2 = ValueObjectFactory.createValueObject(IDB + 2);
    ValueObject vo3 = ValueObjectFactory.createValueObject(IDB + 3);
    ValueObject vo4 = ValueObjectFactory.createValueObject(IDB + 4);
    ValueObject vo5 = ValueObjectFactory.createValueObject(IDB + 5);
    ValueObject vo6 = ValueObjectFactory.createValueObject(IDB + 6);
    ValueObject vo7 = ValueObjectFactory.createValueObject(IDB + 7);
    al.add(vo1);
    al.add(vo2);
    al.add(vo3);
    al.add(vo4);
    al.add(vo5);
    al.add(vo6);
    al.add(vo7);

    try {
      assertEquals(cpoXaAdapter.insertBean(vo1), 1);
      assertEquals(cpoXaAdapter.insertBean(ValueObject.FG_CREATE_NULL, vo2), 1);
      assertEquals(cpoXaAdapter.insertBean(ValueObject.FG_CREATE_NULL, vo3, null, null, null), 1);

      List<ValueObject> beans1 = new ArrayList<>();
      beans1.add(vo4);
      assertEquals(cpoXaAdapter.insertBeans(beans1), 1);

      List<ValueObject> beans2 = new ArrayList<>();
      beans2.add(vo5);
      assertEquals(cpoXaAdapter.insertBeans(ValueObject.FG_CREATE_NULL, beans2), 1);

      List<ValueObject> beans3 = new ArrayList<>();
      beans3.add(vo6);
      beans3.add(vo7);
      assertEquals(
          cpoXaAdapter.insertBeans(ValueObject.FG_CREATE_NULL, beans3, null, null, null), 2);

      assertEquals(cpoXaAdapter.existsBean(vo1), 1);
      assertEquals(cpoXaAdapter.existsBean(ValueObject.FG_EXIST_NULL, vo2), 1);

      // the EXIST expression already has a WHERE clause, so extra wheres must use Logical.AND
      Collection<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cpoXaAdapter.newWhere(Logical.AND, ValueObject.ATTR_ID, Comparison.EQ, IDB + 3));
      assertEquals(cpoXaAdapter.existsBean(ValueObject.FG_EXIST_NULL, vo3, wheres), 1);
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises every retrieveBean/retrieveBeans overload. */
  @Test
  public void testRetrieveDelegation() {
    String method = "testRetrieveDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(IDB + 1);
    ValueObject vo2 = ValueObjectFactory.createValueObject(IDB + 2);
    al.add(vo1);
    al.add(vo2);

    try {
      List<ValueObject> beans = new ArrayList<>();
      beans.add(vo1);
      beans.add(vo2);
      assertEquals(cpoXaAdapter.insertBeans(beans), 2);

      Collection<CpoOrderBy> orderBy = new ArrayList<>();
      orderBy.add(cpoXaAdapter.newOrderBy(ValueObject.ATTR_ID, true));
      Collection<CpoWhere> wheres = new ArrayList<>();
      wheres.add(cpoXaAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.GT, IDB));
      CpoWhere where = cpoXaAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.GT, IDB);
      ValueObject criteria = ValueObjectFactory.createValueObject();

      // retrieveBean overloads
      ValueObject rvo = cpoXaAdapter.retrieveBean(ValueObjectFactory.createValueObject(IDB + 1));
      assertNotNull(rvo, method + "retrieveBean(bean) returned null");

      rvo =
          cpoXaAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL, ValueObjectFactory.createValueObject(IDB + 1));
      assertNotNull(rvo, method + "retrieveBean(group, bean) returned null");

      rvo =
          cpoXaAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL,
              ValueObjectFactory.createValueObject(IDB + 1),
              (Collection<CpoWhere>) null,
              (Collection<CpoOrderBy>) null,
              (Collection<CpoNativeFunction>) null);
      assertNotNull(rvo, method + "retrieveBean(group, bean, wheres, orderBy, native) is null");

      rvo =
          cpoXaAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL,
              ValueObjectFactory.createValueObject(IDB + 1),
              ValueObjectFactory.createValueObject(),
              null,
              null);
      assertNotNull(rvo, method + "retrieveBean(group, criteria, result, wheres, orderBy) is null");

      rvo =
          cpoXaAdapter.retrieveBean(
              ValueObject.FG_RETRIEVE_NULL,
              ValueObjectFactory.createValueObject(IDB + 1),
              ValueObjectFactory.createValueObject(),
              null,
              null,
              null);
      assertNotNull(rvo, method + "retrieveBean 6-arg criteria overload is null");

      // retrieveBeans overloads
      try (Stream<ValueObject> s = cpoXaAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, criteria)) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
      try (Stream<ValueObject> s =
          cpoXaAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, criteria, where, orderBy)) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
      try (Stream<ValueObject> s =
          cpoXaAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, criteria, orderBy)) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
      try (Stream<ValueObject> s =
          cpoXaAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, criteria, wheres, orderBy)) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
      try (Stream<ValueObject> s =
          cpoXaAdapter.retrieveBeans(
              ValueObject.FG_LIST_NULL, criteria, ValueObjectFactory.createValueObject())) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
      try (Stream<ValueObject> s =
          cpoXaAdapter.retrieveBeans(
              ValueObject.FG_LIST_NULL,
              criteria,
              ValueObjectFactory.createValueObject(),
              where,
              orderBy)) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
      try (Stream<ValueObject> s =
          cpoXaAdapter.retrieveBeans(
              ValueObject.FG_LIST_NULL,
              criteria,
              ValueObjectFactory.createValueObject(),
              wheres,
              orderBy)) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
      try (Stream<ValueObject> s =
          cpoXaAdapter.retrieveBeans(
              ValueObject.FG_LIST_NULL,
              criteria,
              ValueObjectFactory.createValueObject(),
              wheres,
              orderBy,
              null)) {
        assertEquals(
            s.filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000).count(),
            2);
      }
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises every updateBean/updateBeans and deleteBean/deleteBeans overload. */
  @Test
  public void testUpdateAndDeleteDelegation() {
    String method = "testUpdateAndDeleteDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(IDB + 1);
    ValueObject vo2 = ValueObjectFactory.createValueObject(IDB + 2);
    ValueObject vo3 = ValueObjectFactory.createValueObject(IDB + 3);
    ValueObject vo4 = ValueObjectFactory.createValueObject(IDB + 4);
    ValueObject vo5 = ValueObjectFactory.createValueObject(IDB + 5);
    ValueObject vo6 = ValueObjectFactory.createValueObject(IDB + 6);
    al.add(vo1);
    al.add(vo2);
    al.add(vo3);
    al.add(vo4);
    al.add(vo5);
    al.add(vo6);

    try {
      // The null UPDATE group's expression has no WHERE clause and updates every row in the
      // table. With other classes' rows coexisting, only the wheres-scoped update variant
      // can be exercised without corrupting foreign data.
      assertEquals(cpoXaAdapter.insertBean(vo1), 1);

      vo1.setAttrVarChar("updated");
      Collection<CpoWhere> updWheres = new ArrayList<>();
      updWheres.add(
          cpoXaAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, IDB + 1));
      assertEquals(
          cpoXaAdapter.updateBean(ValueObject.FG_UPDATE_NULL, vo1, updWheres, null, null), 1);

      List<ValueObject> updBeans = new ArrayList<>();
      updBeans.add(vo1);
      Collection<CpoWhere> updWheres2 = new ArrayList<>();
      updWheres2.add(
          cpoXaAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, IDB + 1));
      assertEquals(
          cpoXaAdapter.updateBeans(ValueObject.FG_UPDATE_NULL, updBeans, updWheres2, null, null),
          1);

      // delete overloads
      assertEquals(cpoXaAdapter.deleteBean(vo1), 1);

      assertEquals(cpoXaAdapter.insertBean(vo2), 1);
      assertEquals(cpoXaAdapter.deleteBean(ValueObject.FG_DELETE_NULL, vo2), 1);

      assertEquals(cpoXaAdapter.insertBean(vo3), 1);
      assertEquals(cpoXaAdapter.deleteBean(ValueObject.FG_DELETE_NULL, vo3, null, null, null), 1);

      assertEquals(cpoXaAdapter.insertBean(vo4), 1);
      List<ValueObject> delBeans1 = new ArrayList<>();
      delBeans1.add(vo4);
      assertEquals(cpoXaAdapter.deleteBeans(delBeans1), 1);

      assertEquals(cpoXaAdapter.insertBean(vo5), 1);
      List<ValueObject> delBeans2 = new ArrayList<>();
      delBeans2.add(vo5);
      assertEquals(cpoXaAdapter.deleteBeans(ValueObject.FG_DELETE_NULL, delBeans2), 1);

      assertEquals(cpoXaAdapter.insertBean(vo6), 1);
      List<ValueObject> delBeans3 = new ArrayList<>();
      delBeans3.add(vo6);
      assertEquals(
          cpoXaAdapter.deleteBeans(ValueObject.FG_DELETE_NULL, delBeans3, null, null, null), 1);
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /**
   * Exercises the upsertBean/upsertBeans overloads. Each call uses a fresh id so only the insert
   * branch runs; the update branch would hit the WHERE-less null UPDATE group, which updates every
   * row in the shared table.
   */
  @Test
  public void testUpsertDelegation() {
    String method = "testUpsertDelegation:";
    ValueObject vo1 = ValueObjectFactory.createValueObject(IDB + 11);
    ValueObject vo2 = ValueObjectFactory.createValueObject(IDB + 12);
    ValueObject vo3 = ValueObjectFactory.createValueObject(IDB + 13);
    ValueObject vo4 = ValueObjectFactory.createValueObject(IDB + 14);
    al.add(vo1);
    al.add(vo2);
    al.add(vo3);
    al.add(vo4);

    try {
      assertEquals(cpoXaAdapter.upsertBean(vo1), 1);
      assertEquals(cpoXaAdapter.existsBean(vo1), 1, method + "upserted bean should exist");

      assertEquals(cpoXaAdapter.upsertBean((String) null, vo2), 1);
      assertEquals(cpoXaAdapter.existsBean(vo2), 1);

      List<ValueObject> beans3 = new ArrayList<>();
      beans3.add(vo3);
      assertEquals(cpoXaAdapter.upsertBeans(beans3), 1);

      List<ValueObject> beans4 = new ArrayList<>();
      beans4.add(vo4);
      assertEquals(cpoXaAdapter.upsertBeans((String) null, beans4), 1);
      assertEquals(cpoXaAdapter.existsBean(vo4), 1);
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises the executeBean overloads. */
  @Test
  public void testExecuteDelegation() {
    String method = "testExecuteDelegation:";

    // No null-named EXECUTE group is defined in the test meta, so the no-group overload
    // must surface a CpoException regardless of CallableStatement support.
    expectThrows(
        CpoException.class,
        () -> cpoXaAdapter.executeBean(ValueObjectFactory.createValueObject(IDB + 1)));

    if (isSupportsCalls) {
      try {
        ValueObject vo = ValueObjectFactory.createValueObject(IDB + 1);
        vo.setAttrInteger(3);
        ValueObject rvo = cpoXaAdapter.executeBean(ValueObject.FG_EXECUTE_TESTEXECUTEOBJECT, vo);
        assertNotNull(rvo, method + "Returned Value object is null");
        assertEquals(rvo.getAttrDouble(), 27, "power(3,3)=" + rvo.getAttrDouble());

        vo = ValueObjectFactory.createValueObject(IDB + 1);
        vo.setAttrInteger(3);
        rvo = cpoXaAdapter.executeBean(ValueObject.FG_EXECUTE_TESTEXECUTEOBJECT, vo, vo);
        assertNotNull(rvo, method + "Returned Value object is null");
        assertEquals(rvo.getAttrDouble(), 27, "power(3,3)=" + rvo.getAttrDouble());
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support CallableStatements");
    }
  }

  /** Exercises the newWhere and newOrderBy factory overloads. */
  @Test
  public void testWhereAndOrderByFactories() {
    String method = "testWhereAndOrderByFactories:";
    try {
      assertNotNull(cpoXaAdapter.newWhere(), method + "newWhere() is null");
      assertNotNull(
          cpoXaAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, 1),
          method + "newWhere(logical, attr, comp, value) is null");
      assertNotNull(
          cpoXaAdapter.newWhere(Logical.NONE, ValueObject.ATTR_ID, Comparison.EQ, 1, true),
          method + "newWhere(logical, attr, comp, value, not) is null");

      assertNotNull(
          cpoXaAdapter.newOrderBy(ValueObject.ATTR_ID, true),
          method + "newOrderBy(attr, asc) is null");
      assertNotNull(
          cpoXaAdapter.newOrderBy("marker", ValueObject.ATTR_ID, true),
          method + "newOrderBy(marker, attr, asc) is null");
      assertNotNull(
          cpoXaAdapter.newOrderBy(ValueObject.ATTR_ID, true, "UPPER"),
          method + "newOrderBy(attr, asc, function) is null");
      assertNotNull(
          cpoXaAdapter.newOrderBy("marker", ValueObject.ATTR_ID, true, "UPPER"),
          method + "newOrderBy(marker, attr, asc, function) is null");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises the accessor delegation methods. */
  @Test
  public void testAccessorDelegation() {
    String method = "testAccessorDelegation:";
    try {
      assertNotNull(cpoXaAdapter.getCpoMetaDescriptor(), method + "meta descriptor is null");
      assertNotNull(cpoXaAdapter.getDataSourceName(), method + "datasource name is null");

      int fetchSize = cpoXaAdapter.getFetchSize();
      cpoXaAdapter.setFetchSize(fetchSize);
      int batchSize = cpoXaAdapter.getBatchSize();
      cpoXaAdapter.setBatchSize(batchSize);

      assertNotNull(
          cpoXaAdapter.getCpoAttributes("select * from value_object"),
          method + "getCpoAttributes is null");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** In local mode commit/rollback are no-ops, the adapter reports closed, and close is safe. */
  @Test
  public void testLocalModeTrxMethods() {
    String method = "testLocalModeTrxMethods:";
    try {
      cpoXaAdapter.commit();
      cpoXaAdapter.rollback();
      assertTrue(cpoXaAdapter.isClosed(), method + "local mode should report closed");
      cpoXaAdapter.close();
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Exercises isSameRM: null is invalid, JdbcCpoXaAdapters match, other XAResources do not. */
  @Test
  public void testIsSameRM() {
    String method = "testIsSameRM:";
    try {
      cpoXaAdapter.isSameRM(null);
      fail(method + "XAException should have been thrown");
    } catch (XAException xae) {
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaError.XAER_INVAL.toString()));
    }

    try {
      assertTrue(cpoXaAdapter.isSameRM(cpoXaAdapter), method + "same adapter should be same RM");
      assertFalse(
          cpoXaAdapter.isSameRM(new NonCpoXaResource()),
          method + "foreign resource is not same RM");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** Within a global transaction the commit/rollback/isClosed paths hit the trx adapter. */
  @Test
  public void testGlobalTrxCommitAndRollback() {
    if (isXaSupported) {
      String method = "testGlobalTrxCommitAndRollback:";
      ValueObject vo1 = ValueObjectFactory.createValueObject(IDB + 1);
      ValueObject vo2 = ValueObjectFactory.createValueObject(IDB + 2);
      al.add(vo1);
      al.add(vo2);

      Xid xid1 = new JdbcXaResourceTest().new MyXid(100, new byte[] {0x41}, new byte[] {0x42});
      Xid xid2 = new JdbcXaResourceTest().new MyXid(100, new byte[] {0x51}, new byte[] {0x52});

      try {
        cpoXaAdapter.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter.insertBean(vo1);
        // the trx adapter's connection is opened lazily, so isClosed is meaningful only
        // after the first datastore operation
        assertFalse(cpoXaAdapter.isClosed(), method + "in-trx adapter should not be closed");
        cpoXaAdapter.commit();
        cpoXaAdapter.end(xid1, XAResource.TMSUCCESS);
        cpoXaAdapter.rollback(xid1);

        assertEquals(cpoAdapter.existsBean(vo1), 1, method + "committed bean should exist");

        cpoXaAdapter.start(xid2, XAResource.TMNOFLAGS);
        cpoXaAdapter.insertBean(vo2);
        cpoXaAdapter.rollback();
        cpoXaAdapter.end(xid2, XAResource.TMSUCCESS);
        cpoXaAdapter.rollback(xid2);

        assertEquals(cpoAdapter.existsBean(vo2), 0, method + "rolled back bean should not exist");
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter.close(xid1);
        } catch (Exception ignored) {
        }
        try {
          cpoXaAdapter.close(xid2);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  /** An XAResource implementation that is not a JdbcCpoXaAdapter, for isSameRM. */
  private static class NonCpoXaResource implements XAResource {
    @Override
    public void commit(Xid xid, boolean onePhase) {}

    @Override
    public void end(Xid xid, int flags) {}

    @Override
    public void forget(Xid xid) {}

    @Override
    public int getTransactionTimeout() {
      return 0;
    }

    @Override
    public boolean isSameRM(XAResource xares) {
      return false;
    }

    @Override
    public int prepare(Xid xid) {
      return XA_OK;
    }

    @Override
    public Xid[] recover(int flag) {
      return new Xid[0];
    }

    @Override
    public void rollback(Xid xid) {}

    @Override
    public boolean setTransactionTimeout(int seconds) {
      return false;
    }

    @Override
    public void start(Xid xid, int flags) {}
  }
}
