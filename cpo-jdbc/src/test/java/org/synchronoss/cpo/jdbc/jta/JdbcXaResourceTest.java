package org.synchronoss.cpo.jdbc.jta;

/*-
 * #%L
 * jdbc
 * %%
 * Copyright (C) 2003 - 2025 David E. Berry
 * %%
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
 * #L%
 */

import static org.testng.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoOrderBy;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.JdbcStatics;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.synchronoss.cpo.jdbc.ValueObjectFactory;
import org.synchronoss.cpo.jta.CpoXaError;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

/** Created by dberry on 12/8/15. */
public class JdbcXaResourceTest {
  private static final Logger logger = LoggerFactory.getLogger(JdbcXaResourceTest.class);
  private CpoAdapter cpoAdapter = null;
  private JdbcCpoXaAdapter cpoXaAdapter1 = null;
  private JdbcCpoXaAdapter cpoXaAdapter2 = null;
  private ArrayList<ValueObject> al = new ArrayList<>();
  private boolean isXaSupported = true;

  /** Creates a new XaResourceTest object. */
  public JdbcXaResourceTest() {}

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   */
  @Parameters({"db.xasupport"})
  @BeforeMethod
  public void setUp(boolean xaSupport) {
    String method = "setUp:";
    isXaSupported = xaSupport;

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      cpoXaAdapter1 =
          (JdbcCpoXaAdapter)
              CpoAdapterFactoryManager.getCpoXaAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      cpoXaAdapter2 =
          (JdbcCpoXaAdapter)
              CpoAdapterFactoryManager.getCpoXaAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoXaAdapter1, method + "CpoXaAdapter1 is null");
      assertNotNull(cpoXaAdapter2, method + "CpoXaAdapter2 is null");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /** DOCUMENT ME! */
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
    cpoXaAdapter1 = null;
    cpoXaAdapter2 = null;
  }

  /** Tests that simple distributed transaction processing works as expected. */
  @Test
  public void testCoordination() {
    if (isXaSupported) {
      String method = "testCoordination:";
      ValueObject valObj1 = ValueObjectFactory.createValueObject(1);
      ValueObject valObj2 = ValueObjectFactory.createValueObject(2);
      al.add(valObj1);
      al.add(valObj2);

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      Xid xid2 = new MyXid(100, new byte[] {0x11}, new byte[] {0x22});

      try {
        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter2.start(xid2, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj1);
        cpoXaAdapter2.insertBean(valObj2);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);
        cpoXaAdapter2.end(xid2, XAResource.TMSUCCESS);

        cpoXaAdapter1.prepare(xid1);
        cpoXaAdapter2.prepare(xid2);

        cpoXaAdapter1.commit(xid1, false);
        cpoXaAdapter2.commit(xid2, false);

        CpoOrderBy cob = cpoXaAdapter1.newOrderBy(ValueObject.ATTR_ID, true);
        Collection<CpoOrderBy> colCob = new ArrayList<>();
        colCob.add(cob);

        ValueObject valObj = ValueObjectFactory.createValueObject();
        List<ValueObject> list =
            cpoXaAdapter1.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, colCob);
        assertEquals(2, list.size(), "list size is " + list.size());
        assertEquals(1, list.get(0).getId(), "ValuObject(1) is missing");
        assertEquals(2, list.get(1).getId(), "ValuObject(2) is missing");

        assertEquals(2, cpoXaAdapter1.deleteBeans(al));
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
        try {
          cpoXaAdapter1.close(xid2);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  /** Tests that simple distributed transaction processing works as expected. */
  @Test
  public void testRollback() {
    if (isXaSupported) {
      String method = "testRollback:";
      ValueObject valObj1 = ValueObjectFactory.createValueObject(1);
      ValueObject valObj2 = ValueObjectFactory.createValueObject(2);
      al.add(valObj1);
      al.add(valObj2);

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      Xid xid2 = new MyXid(100, new byte[] {0x11}, new byte[] {0x22});

      try {
        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter2.start(xid2, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj1);
        cpoXaAdapter2.insertBean(valObj2);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);
        cpoXaAdapter2.end(xid2, XAResource.TMSUCCESS);

        cpoXaAdapter1.prepare(xid1);
        cpoXaAdapter2.prepare(xid2);

        cpoXaAdapter1.rollback(xid1);
        cpoXaAdapter2.rollback(xid2);

        ValueObject valObj = ValueObjectFactory.createValueObject();
        List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
        assertTrue(list.isEmpty(), "list SHOULD BE EMPTY");
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
        try {
          cpoXaAdapter1.close(xid2);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  /** Tests that XA RECOVER works as expected. */
  @Test
  public void testRecover() {
    if (isXaSupported) {
      String method = "testRecover:";
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      al.add(valObj);

      Xid xid = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      try {

        cpoXaAdapter1.start(xid, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj);
        cpoXaAdapter1.end(xid, XAResource.TMSUCCESS);
        cpoXaAdapter1.prepare(xid);

        // Now try and recover

        Xid[] recoveredXids =
            cpoXaAdapter2.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);

        assertNotNull(recoveredXids);
        assertTrue(recoveredXids.length > 0);

        boolean xidFound = false;

        for (int i = 0; i < recoveredXids.length; i++) {
          if (recoveredXids[i] != null && recoveredXids[i].equals(xid)) {
            xidFound = true;

            break;
          }
        }

        assertTrue(xidFound);

        recoveredXids = cpoXaAdapter1.recover(XAResource.TMSTARTRSCAN);

        assertNotNull(recoveredXids);
        assertTrue(recoveredXids.length > 0);

        xidFound = false;

        for (int i = 0; i < recoveredXids.length; i++) {
          if (recoveredXids[i] != null && recoveredXids[i].equals(xid)) {
            xidFound = true;

            break;
          }
        }

        assertTrue(xidFound);

        // Test flags
        cpoXaAdapter1.recover(XAResource.TMSTARTRSCAN);
        cpoXaAdapter1.recover(XAResource.TMENDRSCAN);
        cpoXaAdapter1.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);

        // This should fail
        try {
          cpoXaAdapter1.recover(XAResource.TMSUCCESS);
          fail("XAException should have been thrown");
        } catch (XAException xae) {
          assertTrue(xae.getLocalizedMessage().startsWith(CpoXaError.XAER_INVAL.toString()));
        }
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testSuspendableTx() throws Exception {
    if (isXaSupported) {
      String method = "testSuspendableTx:";
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      al.add(valObj);

      Xid xid = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});

      try {
        cpoXaAdapter1.start(xid, XAResource.TMNOFLAGS);
        cpoXaAdapter1.retrieveBean(valObj);
        cpoXaAdapter1.end(xid, XAResource.TMSUSPEND);
        cpoXaAdapter1.start(xid, XAResource.TMRESUME);
        cpoXaAdapter1.retrieveBean(valObj);
        cpoXaAdapter1.end(xid, XAResource.TMSUCCESS);
        cpoXaAdapter1.commit(xid, true);

        cpoXaAdapter1.close(xid);

        cpoXaAdapter1.start(xid, XAResource.TMNOFLAGS);
        cpoXaAdapter1.retrieveBean(valObj);
        cpoXaAdapter1.end(xid, XAResource.TMSUCCESS);
        cpoXaAdapter1.start(xid, XAResource.TMJOIN);
        cpoXaAdapter1.retrieveBean(valObj);
        cpoXaAdapter1.end(xid, XAResource.TMSUCCESS);
        cpoXaAdapter1.commit(xid, true);
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testCommit() {
    if (isXaSupported) {
      String method = "testCommit:";
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      al.add(valObj);
      int ret;

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      try {

        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

        // make sure a cpoAdapter cannot see the insert
        assertNull(cpoAdapter.retrieveBean(valObj));

        ret = cpoXaAdapter1.prepare(xid1);
        if (ret == XAResource.XA_OK) {
          cpoXaAdapter1.commit(xid1, false);
        }

        // make sure a cpoAdapter can now see the insert
        assertNotNull(cpoAdapter.retrieveBean(valObj));

      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testRollback2() {
    if (isXaSupported) {
      String method = "testRollback2:";
      ValueObject valObj = ValueObjectFactory.createValueObject(1);
      al.add(valObj);
      int ret;

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      try {

        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

        // make sure a cpoAdapter cannot see the insert
        assertNull(cpoAdapter.retrieveBean(valObj));

        ret = cpoXaAdapter1.prepare(xid1);
        if (ret == XAResource.XA_OK) {
          cpoXaAdapter1.rollback(xid1);
        }

        // make sure a cpoAdapter still cannot see the insert
        assertNull(cpoAdapter.retrieveBean(valObj));
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testSuspend() {
    if (isXaSupported) {
      String method = "testSuspend:";
      ValueObject valObj1 = ValueObjectFactory.createValueObject(1);
      ValueObject valObj2 = ValueObjectFactory.createValueObject(2);
      ValueObject valObj3 = ValueObjectFactory.createValueObject(3);
      al.add(valObj1);
      al.add(valObj2);
      al.add(valObj3);
      int ret;

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});

      try {
        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj1);
        cpoXaAdapter1.end(xid1, XAResource.TMSUSPEND);

        // This update is done outside of transaction scope, so it
        // is not affected by the XA rollback.
        cpoXaAdapter1.insertBean(valObj2);

        cpoXaAdapter1.start(xid1, XAResource.TMRESUME);
        cpoXaAdapter1.insertBean(valObj3);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

        ret = cpoXaAdapter1.prepare(xid1);
        if (ret == XAResource.XA_OK) {
          cpoXaAdapter1.rollback(xid1);
        }
        // make sure that only valobj2 is in the database
        ValueObject valObj = ValueObjectFactory.createValueObject();
        List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
        assertEquals(1, list.size(), "list size is " + list.size());
        assertEquals(list.get(0).getId(), valObj2.getId(), "valObj2 is missing");

      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testMultiTrx() {
    if (isXaSupported) {
      String method = "testMultiTrx:";
      ValueObject valObj1 = ValueObjectFactory.createValueObject(1);
      ValueObject valObj2 = ValueObjectFactory.createValueObject(2);
      al.add(valObj1);
      al.add(valObj2);
      int ret;

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      Xid xid2 = new MyXid(100, new byte[] {0x11}, new byte[] {0x22});
      try {

        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj1);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

        cpoXaAdapter1.start(xid2, XAResource.TMNOFLAGS);

        // Should allow XA resource to do two-phase commit on
        // transaction 1 while associated to transaction 2
        ret = cpoXaAdapter1.prepare(xid1);
        if (ret == XAResource.XA_OK) {
          cpoXaAdapter1.commit(xid1, false);
        }

        cpoXaAdapter1.insertBean(valObj2);
        cpoXaAdapter1.end(xid2, XAResource.TMSUCCESS);

        ret = cpoXaAdapter1.prepare(xid2);
        if (ret == XAResource.XA_OK) {
          cpoXaAdapter1.rollback(xid2);
        }
        // make sure the xid1 insert can be seen
        // make sure the xid2 insert cannot be seen
        ValueObject valObj = ValueObjectFactory.createValueObject();
        List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
        assertEquals(1, list.size(), "list size is " + list.size());
        assertEquals(list.get(0).getId(), valObj1.getId(), "valObj1 is missing");
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
        try {
          cpoXaAdapter1.close(xid2);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testJoin() {
    if (isXaSupported) {
      String method = "testJoin:";
      ValueObject valObj1 = ValueObjectFactory.createValueObject(1);
      ValueObject valObj2 = ValueObjectFactory.createValueObject(2);
      al.add(valObj1);
      al.add(valObj2);
      int ret;

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      try {

        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj1);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

        if (cpoXaAdapter2.isSameRM(cpoXaAdapter1)) {
          cpoXaAdapter2.start(xid1, XAResource.TMJOIN);
          cpoXaAdapter2.insertBean(valObj2);
          cpoXaAdapter2.end(xid1, XAResource.TMSUCCESS);
        } else {
          fail("Unable to join XAResources with the same resource manager");
        }

        ret = cpoXaAdapter1.prepare(xid1);
        if (ret == XAResource.XA_OK) {
          cpoXaAdapter1.commit(xid1, false);
        }

        // make sure both records exist
        ValueObject valObj = ValueObjectFactory.createValueObject();
        List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
        assertEquals(2, list.size(), "list size is " + list.size());
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testRecover2() {
    if (isXaSupported) {
      String method = "testRecover2:";
      ValueObject valObj1 = ValueObjectFactory.createValueObject(1);
      ValueObject valObj2 = ValueObjectFactory.createValueObject(2);
      ValueObject valObj3 = ValueObjectFactory.createValueObject(3);
      al.add(valObj1);
      al.add(valObj2);
      al.add(valObj3);

      Xid[] xids;

      Xid xid1 = new MyXid(100, new byte[] {0x01}, new byte[] {0x02});
      Xid xid2 = new MyXid(100, new byte[] {0x11}, new byte[] {0x22});
      Xid xid3 = new MyXid(100, new byte[] {0x13}, new byte[] {0x23});

      // TODO - need to create some unfinished transactions
      try {

        cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj1);
        cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);
        cpoXaAdapter1.prepare(xid1);

        cpoXaAdapter1.start(xid2, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj2);
        cpoXaAdapter1.end(xid2, XAResource.TMSUSPEND);

        cpoXaAdapter1.start(xid3, XAResource.TMNOFLAGS);
        cpoXaAdapter1.insertBean(valObj3);
        cpoXaAdapter1.end(xid3, XAResource.TMFAIL);

        xids = cpoXaAdapter1.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);
        assertTrue(xids.length > 0, "In progress resources must be created");
        assertEquals(1, xids.length, "There should be 1 in progress transaction");
        for (int i = 0; xids != null && i < xids.length; i++) {
          try {
            cpoXaAdapter1.rollback(xids[i]);
          } catch (XAException ex) {
            try {
              cpoXaAdapter1.forget(xids[i]);
            } catch (XAException ex1) {
              fail("rollback/forget failed: " + ex1.getMessage());
            }
          }
        }
      } catch (Exception e) {
        fail(method + ExceptionHelper.getLocalizedMessage(e));
      } finally {
        try {
          cpoXaAdapter1.close(xid1);
        } catch (Exception ignored) {
        }
        try {
          cpoXaAdapter1.close(xid2);
        } catch (Exception ignored) {
        }
        try {
          cpoXaAdapter1.close(xid3);
        } catch (Exception ignored) {
        }
      }
    } else {
      logger.error(cpoAdapter.getDataSourceName() + " does not support XA Transactions");
    }
  }

  @Test
  public void testNothing() {}

  public class MyXid implements Xid {
    protected int formatId;
    protected byte[] gtrid;
    protected byte[] bqual;

    public MyXid() {}

    public MyXid(int formatId, byte gtrid[], byte bqual[]) {
      this.formatId = formatId;
      this.gtrid = gtrid;
      this.bqual = bqual;
    }

    public int getFormatId() {
      return formatId;
    }

    public byte[] getBranchQualifier() {
      return bqual;
    }

    public byte[] getGlobalTransactionId() {
      return gtrid;
    }
  }
}
