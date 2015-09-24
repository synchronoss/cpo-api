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
package org.synchronoss.cpo.jdbc.jta;

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.jdbc.ExecuteTrxTest;
import org.synchronoss.cpo.jdbc.JdbcStatics;
import org.synchronoss.cpo.jta.CpoXaResource;
import org.synchronoss.cpo.helper.ExceptionHelper;

import javax.transaction.xa.Xid;

/**
 * Created by dberry on 12/8/15.
 */
public class JdbcXaResourceTest extends TestCase {
  private static final Logger logger = LoggerFactory.getLogger(ExecuteTrxTest.class);
  private CpoAdapter cpoAdapter = null;
  private CpoXaResource cpoXaResource1 = null;
  private CpoXaResource cpoXaResource2 = null;

  /**
   * Creates a new XaResourceTest object.
   *
   */
  public JdbcXaResourceTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   */
  @Override
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      cpoXaResource1 = CpoAdapterFactoryManager.getCpoXaAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      cpoXaResource2 = CpoAdapterFactoryManager.getCpoXaAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "CpoXaAdapter1 is null", cpoXaResource1);
      assertNotNull(method + "CpoXaAdapter2 is null", cpoXaResource2);
    } catch (Exception e) {
      fail(method + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  /**
   * DOCUMENT ME!
   */
  @Override
  public void tearDown() {
    cpoAdapter = null;
    cpoXaResource1 = null;
    cpoXaResource2 = null;
  }

  /**
 	 * Tests that simple distributed transaction processing works as expected.
 	 *
 	 * @throws Exception
 	 *             if the test fails.
 	 */
// 	public void testCoordination() {
//    String method = "testCoordination:";
//    ArrayList<ValueObject> al = new ArrayList<>();
//    ValueObject valObj1 = new ValueObjectBean(1);
//    ValueObject valObj2 = new ValueObjectBean(2);
//    al.add(valObj1);
//    al.add(valObj2);
//
//    try {
//      Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//      Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
//
//      cpoXaResource1.start(xid1, XAResource.TMNOFLAGS);
//			cpoXaResource2.start(xid2, XAResource.TMNOFLAGS);
//			cpoXaResource1.insertObject(valObj1);
//      cpoXaResource2.insertObject(valObj2);
//			cpoXaResource1.end(xid1, XAResource.TMSUCCESS);
//      cpoXaResource2.end(xid2, XAResource.TMSUCCESS);
//
//      cpoXaResource1.prepare(xid1);
//      cpoXaResource2.prepare(xid2);
//
//      cpoXaResource1.commit(xid1, false);
//      cpoXaResource2.commit(xid2, false);
//
//      ValueObject valObj = new ValueObjectBean();
//      List<ValueObject> list = cpoAdapter.retrieveBeans(null, valObj);
//			assertTrue("list size is " + list.size(), list.size() == 2);
//			assertTrue("ValuObject(1) is missing", list.get(0).getId() == 1);
//			assertTrue("ValuObject(2) is missing", list.get(1).getId() == 2);
//
//
//      assertEquals(2, cpoAdapter.deleteObjects(al));
//    } catch (Exception e) {
//      fail(method + ExceptionHelper.getLocalizedMessage(e));
//    }
//  }

  /**
 	 * Tests that simple distributed transaction processing works as expected.
 	 *
 	 * @throws Exception
 	 *             if the test fails.
 	 */
// 	public void testRollback()  {
//    String method = "testRollback:";
//    ValueObject valObj1 = new ValueObjectBean(1);
//    ValueObject valObj2 = new ValueObjectBean(2);
//
//    try {
//			Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//      Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
//
//      cpoXaResource1.start(xid1, XAResource.TMNOFLAGS);
//      cpoXaResource2.start(xid2, XAResource.TMNOFLAGS);
//      cpoXaResource1.insertObject(valObj1);
//      cpoXaResource2.insertObject(valObj2);
//      cpoXaResource1.end(xid1, XAResource.TMSUCCESS);
//      cpoXaResource2.end(xid2, XAResource.TMSUCCESS);
//
//      cpoXaResource1.prepare(xid1);
//      cpoXaResource2.prepare(xid2);
//
//
//      cpoXaResource1.rollback(xid1);
//      cpoXaResource2.rollback(xid2);
//
//      ValueObject valObj = new ValueObjectBean();
//      List<ValueObject> list = cpoAdapter.retrieveBeans(null, valObj);
//      assertTrue("list SHOULD BE EMPTY", list.isEmpty());
//    } catch (Exception e) {
//     fail(method + ExceptionHelper.getLocalizedMessage(e));
//    }
// 	}

 	/**
 	 * Tests that XA RECOVER works as expected.
 	 *
 	 * @throws Exception
 	 *             if test fails
 	 */
// 	public void testRecover() {
//    String method = "testRecover:";
//    ValueObject valObj = new ValueObjectBean(1);
//
// 		try {
//			Xid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//
//      cpoXaResource1.start(xid, XAResource.TMNOFLAGS);
//      cpoXaResource1.insertObject(valObj);
//      cpoXaResource1.end(xid, XAResource.TMSUCCESS);
//      cpoXaResource1.prepare(xid);
//
// 			// Now try and recover
//
// 			Xid[] recoveredXids = cpoXaResource2.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);
//
// 			assertTrue(recoveredXids != null);
// 			assertTrue(recoveredXids.length > 0);
//
// 			boolean xidFound = false;
//
// 			for (int i = 0; i < recoveredXids.length; i++) {
// 				if (recoveredXids[i] != null &&
// 					recoveredXids[i].equals(xid)) {
// 					xidFound = true;
//
// 					break;
// 				}
// 			}
//
// 			assertTrue(xidFound);
//
// 			recoveredXids = cpoXaResource1.recover(XAResource.TMSTARTRSCAN);
//
// 			assertTrue(recoveredXids != null);
// 			assertTrue(recoveredXids.length > 0);
//
// 			xidFound = false;
//
// 			for (int i = 0; i < recoveredXids.length; i++) {
// 				if (recoveredXids[i] != null &&
// 						recoveredXids[i].equals(xid)) {
// 					xidFound = true;
//
// 					break;
// 				}
// 			}
//
// 			assertTrue(xidFound);
//
// 			// Test flags
//      cpoXaResource1.recover(XAResource.TMSTARTRSCAN);
//      cpoXaResource1.recover(XAResource.TMENDRSCAN);
//      cpoXaResource1.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);
//
// 			// This should fail
// 			try {
//        cpoXaResource1.recover(XAResource.TMSUCCESS);
// 				fail("XAException should have been thrown");
// 			} catch (XAException xaEx) {
// 				assertEquals(XAException.XAER_INVAL, xaEx.errorCode);
// 			}
//    } catch (Exception e) {
//     fail(method + ExceptionHelper.getLocalizedMessage(e));
//    }
// 	}


// 	public void testSuspendableTx() throws Exception {
//		String method = "testSuspendableTx:";
//  	ValueObject valObj = new ValueObjectBean(1);
//
//		Xid xid = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//
// 		try {
//			cpoXaResource1.start(xid, XAResource.TMNOFLAGS);
//			cpoXaResource1.retrieveBean(valObj);
//			cpoXaResource1.end(xid, XAResource.TMSUCCESS);
//			cpoXaResource1.start(xid, XAResource.TMRESUME);
//			cpoXaResource1.retrieveBean(valObj);
//			cpoXaResource1.end(xid, XAResource.TMSUCCESS);
//			cpoXaResource1.commit(xid, true);
//
//
//			cpoXaResource1.start(xid, XAResource.TMNOFLAGS);
//			cpoXaResource1.retrieveBean(valObj);
//			cpoXaResource1.end(xid, XAResource.TMSUCCESS);
//			cpoXaResource1.start(xid, XAResource.TMJOIN);
//			cpoXaResource1.retrieveBean(valObj);
//			cpoXaResource1.end(xid, XAResource.TMSUCCESS);
//			cpoXaResource1.commit(xid, true);
//		} catch (Exception e) {
//		 fail(method + ExceptionHelper.getLocalizedMessage(e));
//		}
// 	}

//	public void testCommit() {
//		String method = "testCommit:";
//  	ValueObject valObj = new ValueObjectBean(1);
//    int ret;
//
//		try {
//      Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//
//			cpoXaResource1.start(xid1, XAResource.TMNOFLAGS);
//      cpoXaResource1.insertObject(valObj);
//			cpoXaResource1.end(xid1, XAResource.TMSUCCESS);
//
//      // make sure a cpoAdapter can now see the insert
//      assertTrue(cpoAdapter.retrieveBean(valObj)==null);
//      ret = cpoXaResource1.prepare(xid1);
//      if (ret == XAResource.XA_OK) {
//       cpoXaResource1.commit(xid1, false);
//      }
//
//      // make sure a cpoAdapter can now see the insert
//      assertTrue(cpoAdapter.retrieveBean(valObj)!=null);
//
//		} catch (Exception e) {
//		 	fail(method + ExceptionHelper.getLocalizedMessage(e));
//		}
//	}

//	public void testRollback2() {
//		String method = "testRollback2:";
//  	ValueObject valObj = new ValueObjectBean(1);
//    int ret;
//
//		try {
//      Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//      Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
//
//			cpoXaResource1.start(xid1, XAResource.TMNOFLAGS);
//      cpoXaResource1.insertObject(valObj);
//			cpoXaResource1.end(xid1, XAResource.TMSUCCESS);
//
//      // make sure a cpoAdapter cannot see the insert
//      assertTrue(cpoAdapter.retrieveBean(valObj)==null);
//
//			ret = cpoXaResource1.prepare(xid1);
//			if (ret == XAResource.XA_OK) {
//				cpoXaResource1.rollback(xid1);
//			}
//
//      // make sure a cpoAdapter still cannot see the insert
//      assertTrue(cpoAdapter.retrieveBean(valObj)==null);
//		} catch (Exception e) {
//		 	fail(method + ExceptionHelper.getLocalizedMessage(e));
//		}
//
//	}

//	public void testSuspend() {
//    String method = "testSuspend:";
//    ValueObject valObj1 = new ValueObjectBean(1);
//    ValueObject valObj2 = new ValueObjectBean(2);
//    ValueObject valObj3 = new ValueObjectBean(3);
//    int ret;
//
//    try {
//      Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//
//      cpoXaResource1.start(xid1, XAResource.TMNOFLAGS);
//      cpoXaResource1.insertObject(valObj1);
//      cpoXaResource1.end(xid1, XAResource.TMSUSPEND);
//
//      // This update is done outside of transaction scope, so it
//      // is not affected by the XA rollback.
//      cpoXaResource1.insertObject(valObj2);
//
//      cpoXaResource1.start(xid1, XAResource.TMRESUME);
//      cpoXaResource1.insertObject(valObj3);
//      cpoXaResource1.end(xid1, XAResource.TMSUCCESS);
//
//      ret = cpoXaResource1.prepare(xid1);
//      if (ret == XAResource.XA_OK) {
//        cpoXaResource1.rollback(xid1);
//      }
//      // make sure that only valobj2 is in the database
//      ValueObject valObj = new ValueObjectBean();
//      List<ValueObject> list = cpoAdapter.retrieveBeans(null, valObj);
//			assertTrue("list size is " + list.size(), list.size() == 1);
//			assertTrue("valObj2 is missing", list.get(0).getId() == valObj2.getId());
//
//    } catch (Exception e) {
//      fail(method + ExceptionHelper.getLocalizedMessage(e));
//    }
//	}

//	public void testMultiTrx() {
//    String method = "testMultiTrx:";
//    ValueObject valObj1 = new ValueObjectBean(1);
//    ValueObject valObj2 = new ValueObjectBean(2);
//    int ret;
//
//    try {
//      Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//      Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
//
//      cpoXaResource1.start(xid1, XAResource.TMNOFLAGS);
//      cpoXaResource1.insertObject(valObj1);
//      cpoXaResource1.end(xid1, XAResource.TMSUCCESS);
//
//      cpoXaResource1.start(xid2, XAResource.TMNOFLAGS);
//
//      // Should allow XA resource to do two-phase commit on
//      // transaction 1 while associated to transaction 2
//      ret = cpoXaResource1.prepare(xid1);
//      if (ret == XAResource.XA_OK) {
//        cpoXaResource1.commit(xid1, false);
//      }
//
//      cpoXaResource1.insertObject(valObj2);
//      cpoXaResource1.end(xid2, XAResource.TMSUCCESS);
//
//      ret = cpoXaResource1.prepare(xid2);
//      if (ret == XAResource.XA_OK) {
//        cpoXaResource1.rollback(xid2);
//      }
//      // make sure the xid1 insert can be seen
//      // make sure the xid2 insert cannot be seen
//      ValueObject valObj = new ValueObjectBean();
//      List<ValueObject> list = cpoAdapter.retrieveBeans(null, valObj);
//			assertTrue("list size is " + list.size(), list.size() == 1);
//			assertTrue("valObj1 is missing", list.get(0).getId() == valObj1.getId());
//    } catch (Exception e) {
//      fail(method + ExceptionHelper.getLocalizedMessage(e));
//    }
//	}

//	public void testJoin() {
//    String method = "testJoin:";
//    ValueObject valObj1 = new ValueObjectBean(1);
//    ValueObject valObj2 = new ValueObjectBean(2);
//    int ret;
//
//    try {
//      Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});
//      Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});
//
//      cpoXaResource1.start(xid1, XAResource.TMNOFLAGS);
//      cpoXaResource1.insertObject(valObj1);
//      cpoXaResource1.end(xid1, XAResource.TMSUCCESS);
//
//      if (cpoXaResource2.isSameRM(cpoXaResource1)) {
//        cpoXaResource2.start(xid1, XAResource.TMJOIN);
//        cpoXaResource2.insertObject(valObj2);
//        cpoXaResource2.end(xid1, XAResource.TMSUCCESS);
//      }
//      else {
//        fail("Unable to join XAResources with the same resource manager");
//      }
//
//      ret = cpoXaResource1.prepare(xid1);
//      if (ret == XAResource.XA_OK) {
//        cpoXaResource1.commit(xid1, false);
//      }
//
//      // make sure both records exist
//      ValueObject valObj = new ValueObjectBean();
//      List<ValueObject> list = cpoAdapter.retrieveBeans(null, valObj);
//			assertTrue("list size is " + list.size(), list.size() == 2);
//    } catch (Exception e) {
//      fail(method + ExceptionHelper.getLocalizedMessage(e));
//    }
//
//	}

//  public void testRecover2() {
//    String method = "testRecover2:";
//    Xid[] xids;
//
//    // TODO - need to create some unfinished transactions
//    try {
//      xids = cpoXaResource1.recover(XAResource.TMSTARTRSCAN | XAResource.TMENDRSCAN);
//      assertTrue("In progress resources must be created",xids.length>0);
//      for (int i = 0; xids != null && i < xids.length; i++) {
//        try {
//          cpoXaResource1.rollback(xids[i]);
//        } catch (XAException ex) {
//          try {
//            cpoXaResource1.forget(xids[i]);
//          } catch (XAException ex1) {
//            fail("rollback/forget failed: " + ex1.getMessage());
//          }
//        }
//      }
//    } catch (Exception e) {
//      fail(method + ExceptionHelper.getLocalizedMessage(e));
//    }
//
//  }

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
