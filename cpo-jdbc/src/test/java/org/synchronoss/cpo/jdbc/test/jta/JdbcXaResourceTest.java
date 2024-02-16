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
package org.synchronoss.cpo.jdbc.test.jta;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoOrderBy;
import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.jta.JdbcCpoXaAdapter;
import org.synchronoss.cpo.jdbc.test.ExecuteTrxTest;
import org.synchronoss.cpo.jta.CpoXaResource;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.testng.annotations.*;
import static org.testng.Assert.*;

/**
 * Created by dberry on 12/8/15.
 */
public class JdbcXaResourceTest extends JdbcDbContainerBase {
  private static final Logger logger = LoggerFactory.getLogger(ExecuteTrxTest.class);
  private CpoAdapter cpoAdapter = null;
  private JdbcCpoXaAdapter cpoXaAdapter1 = null;
  private JdbcCpoXaAdapter cpoXaAdapter2 = null;
  private final String className = this.getClass().getSimpleName();

  /**
   * Creates a new XaResourceTest object.
   *
   */
  public JdbcXaResourceTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   */
  @BeforeMethod
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      cpoXaAdapter1 = (JdbcCpoXaAdapter) CpoAdapterFactoryManager.getCpoXaAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      cpoXaAdapter2 = (JdbcCpoXaAdapter) CpoAdapterFactoryManager.getCpoXaAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoXaAdapter1,method + "CpoXaAdapter1 is null");
      assertNotNull(cpoXaAdapter2,method + "CpoXaAdapter2 is null");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  private void deleteTestRecords(ArrayList<ValueObject> al){
    String method = "deleteTestRecords:";
    try {
      cpoAdapter.deleteObjects(al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    } finally {
      al.clear();
    }
  }

  /**
   * DOCUMENT ME!
   */
  @AfterMethod
  public void tearDown() {
		String method = "tearDown:";
    cpoAdapter = null;
    cpoXaAdapter1 = null;
    cpoXaAdapter2 = null;
  }

  /**
 	 * Tests that simple distributed transaction processing works as expected.
 	 *
 	 * @throws Exception
 	 *             if the test fails.
 	 */
  @Test
 	public void testCoordination() {
    String method = "testCoordination:";
    ValueObject valObj1 = ValueObjectFactory.createValueObject(11, className);
    ValueObject valObj2 = ValueObjectFactory.createValueObject(12, className);
    ArrayList<ValueObject> al = new ArrayList<>();
    al.add(valObj1);
    al.add(valObj2);

		Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
  	Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});

    try {
      cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
			cpoXaAdapter2.start(xid2, XAResource.TMNOFLAGS);
			cpoXaAdapter1.insertObject(valObj1);
      cpoXaAdapter2.insertObject(valObj2);
			cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);
      cpoXaAdapter2.end(xid2, XAResource.TMSUCCESS);

      cpoXaAdapter1.prepare(xid1);
      cpoXaAdapter2.prepare(xid2);

      cpoXaAdapter1.commit(xid1, false);
      cpoXaAdapter2.commit(xid2, false);

			CpoOrderBy cob = cpoXaAdapter1.newOrderBy(ValueObject.ATTR_ID, true);
			Collection<CpoOrderBy> colCob = new ArrayList<>();
			colCob.add(cob);

      ValueObject valObj = ValueObjectFactory.createValueObject(className);
      List<ValueObject> list = cpoXaAdapter1.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, colCob);
      assertEquals(list.size(), 2, "list size is " + list.size());
      assertEquals(list.get(0).getId(), 11, "ValuObject(1) is missing");
      assertEquals(list.get(1).getId(), 12, "ValuObject(2) is missing");


      assertEquals(2, cpoXaAdapter1.deleteObjects(al));
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    } finally {
			try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
			try {cpoXaAdapter1.close(xid2);} catch(Exception e) {}
      deleteTestRecords(al);
		}
	}

  /**
 	 * Tests that simple distributed transaction processing works as expected.
 	 *
 	 * @throws Exception
 	 *             if the test fails.
 	 */
  @Test
 	public void testRollback()  {
    String method = "testRollback:";
    ValueObject valObj1 = ValueObjectFactory.createValueObject(13, className);
    ValueObject valObj2 = ValueObjectFactory.createValueObject(14, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj1);
  	al.add(valObj2);

		Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
		Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});

    try {
      cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
      cpoXaAdapter2.start(xid2, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj1);
      cpoXaAdapter2.insertObject(valObj2);
      cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);
      cpoXaAdapter2.end(xid2, XAResource.TMSUCCESS);

      cpoXaAdapter1.prepare(xid1);
      cpoXaAdapter2.prepare(xid2);


      cpoXaAdapter1.rollback(xid1);
      cpoXaAdapter2.rollback(xid2);

      ValueObject valObj = ValueObjectFactory.createValueObject(className);
      List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertTrue(list.isEmpty(),"list SHOULD BE EMPTY");
    } catch (Exception e) {
     fail(method + ExceptionHelper.getLocalizedMessage(e));
    } finally {
			try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
			try {cpoXaAdapter1.close(xid2);} catch(Exception e) {}
      deleteTestRecords(al);
		}
 	}

 	/**
 	 * Tests that XA RECOVER works as expected.
 	 *
 	 * @throws Exception
 	 *             if test fails
 	 */
  @Test
 	public void testRecover() {
    String method = "testRecover:";
    ValueObject valObj = ValueObjectFactory.createValueObject(15, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj);

    Xid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
 		try {

      cpoXaAdapter1.start(xid, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj);
      cpoXaAdapter1.end(xid, XAResource.TMSUCCESS);
      cpoXaAdapter1.prepare(xid);

 			// Now try and recover

 			Xid[] recoveredXids = cpoXaAdapter2.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);

      assertNotNull(recoveredXids);
 			assertTrue(recoveredXids.length > 0);

 			boolean xidFound = false;

 			for (int i = 0; i < recoveredXids.length; i++) {
 				if (recoveredXids[i] != null &&
 					recoveredXids[i].equals(xid)) {
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
 				if (recoveredXids[i] != null &&
 						recoveredXids[i].equals(xid)) {
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
        assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_INVAL.toString()));
 			}
    } catch (Exception e) {
     fail(method + ExceptionHelper.getLocalizedMessage(e));
    } finally {
  			try {cpoXaAdapter1.close(xid);} catch(Exception e) {}
      deleteTestRecords(al);
  		}
 	}


  @Test
 	public void testSuspendableTx() throws Exception {
		String method = "testSuspendableTx:";
  	ValueObject valObj = ValueObjectFactory.createValueObject(16, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj);

		Xid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

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
			try {cpoXaAdapter1.close(xid);} catch(Exception e) {}
      deleteTestRecords(al);
		}
 	}

  @Test
	public void testCommit() {
		String method = "testCommit:";
  	ValueObject valObj = ValueObjectFactory.createValueObject(17, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj);
    int ret;

		Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
		try {

			cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj);
			cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

      // make sure a cpoAdapter can now see the insert
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
			try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
      deleteTestRecords(al);
		}
	}

  @Test
	public void testRollback2() {
		String method = "testRollback2:";
  	ValueObject valObj = ValueObjectFactory.createValueObject(18, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj);
    int ret;

		Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
		try {

			cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj);
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
			try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
      deleteTestRecords(al);
		}

	}

  @Test
	public void testSuspend() {
    String method = "testSuspend:";
    ValueObject valObj1 = ValueObjectFactory.createValueObject(10, className);
    ValueObject valObj2 = ValueObjectFactory.createValueObject(19, className);
    ValueObject valObj3 = ValueObjectFactory.createValueObject(20, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj1);
  	al.add(valObj2);
		al.add(valObj3);
    int ret;

		Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

    try {
      cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj1);
      cpoXaAdapter1.end(xid1, XAResource.TMSUSPEND);

      // This update is done outside of transaction scope, so it
      // is not affected by the XA rollback.
      cpoXaAdapter1.insertObject(valObj2);

      cpoXaAdapter1.start(xid1, XAResource.TMRESUME);
      cpoXaAdapter1.insertObject(valObj3);
      cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

      ret = cpoXaAdapter1.prepare(xid1);
      if (ret == XAResource.XA_OK) {
        cpoXaAdapter1.rollback(xid1);
      }
      // make sure that only valobj2 is in the database
      ValueObject valObj = ValueObjectFactory.createValueObject(className);
      List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertEquals(list.size(), 1, "list size is " + list.size());
      assertEquals(valObj2.getId(), list.get(0).getId(), "valObj2 is missing");

    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    } finally {
			try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
      deleteTestRecords(al);
		}
	}

  @Test
	public void testMultiTrx() {
    String method = "testMultiTrx:";
    ValueObject valObj1 = ValueObjectFactory.createValueObject(27, className);
    ValueObject valObj2 = ValueObjectFactory.createValueObject(28, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj1);
  	al.add(valObj2);
    int ret;

		Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
    Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
    try {

      cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj1);
      cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

      cpoXaAdapter1.start(xid2, XAResource.TMNOFLAGS);

      // Should allow XA resource to do two-phase commit on
      // transaction 1 while associated to transaction 2
      ret = cpoXaAdapter1.prepare(xid1);
      if (ret == XAResource.XA_OK) {
        cpoXaAdapter1.commit(xid1, false);
      }

      cpoXaAdapter1.insertObject(valObj2);
      cpoXaAdapter1.end(xid2, XAResource.TMSUCCESS);

      ret = cpoXaAdapter1.prepare(xid2);
      if (ret == XAResource.XA_OK) {
        cpoXaAdapter1.rollback(xid2);
      }
      // make sure the xid1 insert can be seen
      // make sure the xid2 insert cannot be seen
      ValueObject valObj = ValueObjectFactory.createValueObject(className);
      List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertEquals(list.size(), 1, "list size is " + list.size());
      assertEquals(valObj1.getId(), list.get(0).getId(), "valObj1 is missing");
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
		} finally {
			try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
			try {cpoXaAdapter1.close(xid2);} catch(Exception e) {}
      deleteTestRecords(al);
		}
	}

  @Test
	public void testJoin() {
    String method = "testJoin:";
    ValueObject valObj1 = ValueObjectFactory.createValueObject(29, className);
    ValueObject valObj2 = ValueObjectFactory.createValueObject(30, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj1);
  	al.add(valObj2);
    int ret;

		Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
    try {

      cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj1);
      cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);

      if (cpoXaAdapter2.isSameRM(cpoXaAdapter1)) {
        cpoXaAdapter2.start(xid1, XAResource.TMJOIN);
        cpoXaAdapter2.insertObject(valObj2);
        cpoXaAdapter2.end(xid1, XAResource.TMSUCCESS);
      }
      else {
        fail("Unable to join XAResources with the same resource manager");
      }

      ret = cpoXaAdapter1.prepare(xid1);
      if (ret == XAResource.XA_OK) {
        cpoXaAdapter1.commit(xid1, false);
      }

      // make sure both records exist
      ValueObject valObj = ValueObjectFactory.createValueObject(className);
      List<ValueObject> list = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertEquals(list.size(), 2, "list size is " + list.size());
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    } finally {
			try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
      deleteTestRecords(al);
		}
	}

  @Test
  public void testRecover2() {
    String method = "testRecover2:";
    ValueObject valObj1 = ValueObjectFactory.createValueObject(211, className);
    ValueObject valObj2 = ValueObjectFactory.createValueObject(212, className);
    ValueObject valObj3 = ValueObjectFactory.createValueObject(213, className);
    ArrayList<ValueObject> al = new ArrayList<>();
		al.add(valObj1);
  	al.add(valObj2);
		al.add(valObj3);

    Xid[] xids;

    Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
    Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
    Xid xid3 = new MyXid(100, new byte[]{0x13}, new byte[]{0x23});

    // TODO - need to create some unfinished transactions
    try {

      cpoXaAdapter1.start(xid1, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj1);
      cpoXaAdapter1.end(xid1, XAResource.TMSUCCESS);
      cpoXaAdapter1.prepare(xid1);

      cpoXaAdapter1.start(xid2, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj2);
      cpoXaAdapter1.end(xid2, XAResource.TMSUSPEND);

      cpoXaAdapter1.start(xid3, XAResource.TMNOFLAGS);
      cpoXaAdapter1.insertObject(valObj3);
      cpoXaAdapter1.end(xid3, XAResource.TMFAIL);

      xids = cpoXaAdapter1.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);
      assertTrue(xids.length>0, "In progress resources must be created");
      assertEquals(1,xids.length, "There should be 1 in progress transaction");
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
      try {cpoXaAdapter1.close(xid1);} catch(Exception e) {}
      try {cpoXaAdapter1.close(xid2);} catch(Exception e) {}
      try {cpoXaAdapter1.close(xid3);} catch(Exception e) {}
      deleteTestRecords(al);
    }

  }

  @Test
  public void testNothing() {

  }

	public class MyXid implements Xid
	{
	 protected int formatId;
	 protected byte gtrid[];
	 protected byte bqual[];
	 public MyXid()
	 {
	 }
	 public MyXid(int formatId, byte gtrid[], byte bqual[])
	 {
	 this.formatId = formatId;
	 this.gtrid = gtrid;
	 this.bqual = bqual;
	 }
	 public int getFormatId()
	 {
	 return formatId;
	 }
	 public byte[] getBranchQualifier()
	 {
	 return bqual;
	 }
	 public byte[] getGlobalTransactionId()
	 {
	 return gtrid;
	 }
	}
}
