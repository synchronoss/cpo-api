/*
 * Copyright (C) 2003-2025 David E. Berry
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
package org.synchronoss.cpo.jta;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.testng.annotations.*;
import static org.testng.Assert.*;


/**
 * Created by dberry on 8/9/15.
 */
public class CpoXaResourceTest {
  private static final String LOCAL_RESOURCE = "LocalResource";
  private static final String GLOBAL_RESOURCE = "GlobalResource";
  private static final String LOCAL_RESOURCE1 = "LocalResource1";
  private static final String GLOBAL_RESOURCE1 = "GlobalResource1";
  private static final String LOCAL_RESOURCE2 = "LocalResource2";
  private static final String GLOBAL_RESOURCE2 = "GlobalResource2";

  @Test
  public void testStart(){
    StringBuilderXaResource xaResource = new StringBuilderXaResource();
    Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

    xaResource.append(LOCAL_RESOURCE);
    assertEquals(xaResource.toString(), LOCAL_RESOURCE);

    // simulate busy
    xaResource.setBusy(true);

    try {
      // should not allow a start if local is busy
      xaResource.start(xid1, XAResource.TMNOFLAGS);
      fail("Start not allowed when local is busy");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_OUTSIDE.toString()));
    }

    // make it unbusy
    xaResource.setBusy(false);

    try {
      // should not allow a start join if the xid is new
      xaResource.start(xid1, XAResource.TMJOIN);
      fail("Start join not allowed for new xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_NOTA.toString()));
    }

    try {
      // should not allow a start resume if xid is new
      xaResource.start(xid1, XAResource.TMRESUME);
      fail("Start resume not allowed for new xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_NOTA.toString()));
    }

    try {
      xaResource.start(xid1, XAResource.TMNOFLAGS);
      assertEquals(0, xaResource.length());
      xaResource.append(GLOBAL_RESOURCE);
      xaResource.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("Start should not have thrown an exception");
    }

    // we should be local again
    assertEquals(xaResource.toString(), LOCAL_RESOURCE);

    try {
      // should not allow a start no flags on an unassigned xid
      xaResource.start(xid1, XAResource.TMNOFLAGS);
      fail("Start not allowed when local is busy");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_DUPID.toString()));
    }

    try {
      // should not allow a start resume on an unassigned xid
      xaResource.start(xid1, XAResource.TMRESUME);
      fail("Start resume not allowed for an unassigned xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_PROTO.toString()));
    }

    try {
      // should allow a start join on an unassigned xid
      xaResource.start(xid1, XAResource.TMJOIN);
      // check for the global value
      assertEquals(GLOBAL_RESOURCE, xaResource.toString());
      // suspend the transaction for the next tests
      xaResource.end(xid1, XAResource.TMSUSPEND);
    } catch (XAException xae) {
      fail("Start join should be allowed for an unassigned xid");
    }

    // make sure local value is there
    assertEquals(xaResource.toString(), LOCAL_RESOURCE);

    try {
      // should not allow a start no flags on a suspended xid
      xaResource.start(xid1, XAResource.TMNOFLAGS);
      fail("Start NO flags not allowed on a suspended xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_DUPID.toString()));
    }

    try {
      // should not allow a start join on a suspended xid
      xaResource.start(xid1, XAResource.TMJOIN);
      fail("Start join not allowed on a suspended xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_PROTO.toString()));
    }

    try {
      // should allow a start resume on a suspended xid
      xaResource.start(xid1, XAResource.TMRESUME);
      // check for the global value
      assertEquals(GLOBAL_RESOURCE, xaResource.toString());
      // suspend the transaction for the next tests
      xaResource.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("Start resume should be allowed for a suspended xid");
    }

    try {
      xaResource.close(xid1);
    }  catch (XAException xae) {
      fail("Close should not have thrown an exception");
    }
  }

  public void testEnd(){
    StringBuilderXaResource xaResource = new StringBuilderXaResource();
    Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

    xaResource.append(LOCAL_RESOURCE);
    assertEquals(xaResource.toString(), LOCAL_RESOURCE);

    try {
      // should not allow an end success on an unknown xid
      xaResource.end(xid1, XAResource.TMSUCCESS);
      fail("End success not allowed on unknown xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_NOTA.toString()));
    }

    try {
      // should not allow an end fail on an unknown xid
      xaResource.end(xid1, XAResource.TMFAIL);
      fail("End fail not allowed on unknown xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_NOTA.toString()));
    }

    try {
      // should not allow an end suspend on an unknown xid
      xaResource.end(xid1, XAResource.TMSUSPEND);
      fail("End suspend not allowed on unknown xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_NOTA.toString()));
    }

    // create the global transaction and test end success
    try {
      xaResource.start(xid1, XAResource.TMNOFLAGS);
      assertEquals(0, xaResource.length());
      xaResource.append(GLOBAL_RESOURCE);
      xaResource.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("End should not have thrown an exception");
    }

    try {
      // should not allow an end success on an unassigned xid
      xaResource.end(xid1, XAResource.TMSUCCESS);
      fail("End success not allowed on unassigned xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_PROTO.toString()));
    }

    try {
      // should not allow an end fail on an unassigned xid
      xaResource.end(xid1, XAResource.TMFAIL);
      fail("End fail not allowed on unassigned xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_PROTO.toString()));
    }

    try {
      // should not allow an end suspend on an unassigned xid
      xaResource.end(xid1, XAResource.TMSUSPEND);
      fail("End suspend not allowed on unassigned xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_PROTO.toString()));
    }

    // join the xid and fail
    try {
      xaResource.start(xid1, XAResource.TMJOIN);
      xaResource.end(xid1, XAResource.TMFAIL);
    } catch (XAException xae) {
      fail("End should not have thrown an exception");
    }

    // join the xid and suspend
    try {
      xaResource.start(xid1, XAResource.TMJOIN);
      xaResource.end(xid1, XAResource.TMSUSPEND);
    } catch (XAException xae) {
      fail("End should not have thrown an exception");
    }


    try {
      // you can't suspend a suspended
      xaResource.end(xid1, XAResource.TMSUSPEND);
      fail("End suspend not allowed on suspended xid");
    } catch (XAException xae) {
      // exception expected
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XAER_PROTO.toString()));
    }

    // test failing a suspend
    try {
      xaResource.end(xid1, XAResource.TMFAIL);
    } catch (XAException xae) {
      fail("End should not have thrown an exception");
    }

    // join the xid and suspend
    try {
      xaResource.start(xid1, XAResource.TMJOIN);
      xaResource.end(xid1, XAResource.TMSUSPEND);
    } catch (XAException xae) {
      fail("End should not have thrown an exception");
    }

    // test success on  a suspend
    try {
      xaResource.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("End should not have thrown an exception");
    }

    try {
      xaResource.close(xid1);
    }  catch (XAException xae) {
      fail("Close should not have thrown an exception");
    }
  }

  public void testOutsideEnd() {
    StringBuilderXaResource xaResource1 = new StringBuilderXaResource();
    Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

    StringBuilderXaResource xaResource2 = new StringBuilderXaResource();
    Xid xid2 = new MyXid(100, new byte[]{0x11}, new byte[]{0x22});

    // Setup the local values
    xaResource1.append(LOCAL_RESOURCE1);
    xaResource2.append(LOCAL_RESOURCE2);

    // Setup the global values
    try {
      xaResource1.start(xid1, XAResource.TMNOFLAGS);
      xaResource2.start(xid2, XAResource.TMNOFLAGS);

      // set the global values
      xaResource1.append(GLOBAL_RESOURCE1);
      xaResource2.append(GLOBAL_RESOURCE2);

      // spec says that any XaResource can call end on any transaction
      // so x1 ends x2 an x2 ends x1
      xaResource1.end(xid2, XAResource.TMSUCCESS);
      // make sure x2 is local now
      assertEquals(xaResource2.toString(),LOCAL_RESOURCE2);

      xaResource2.end(xid1, XAResource.TMSUCCESS);
      // make sure x1 is local now
      assertEquals(xaResource1.toString(), LOCAL_RESOURCE1);

    } catch (XAException xae) {
      fail("testOutsideEnd should not have thrown an exception");
    }

    try {
      xaResource1.close(xid1);
    }  catch (XAException xae) {
      fail("Close should not have thrown an exception");
    }

    try {
      xaResource1.close(xid2);
    }  catch (XAException xae) {
      fail("Close should not have thrown an exception");
    }
  }

  public void testMultiResourcesEnd() {
    // We need to make sure that different Resource types that share CpoBaseXaResource do not interfere with each other
    // So two different XAs sharing CpoBaseXaResource should be able to take part of the same transaction and work independently

    StringBuilderXaResource sbxa = new StringBuilderXaResource();
    EnhancedStringBuilderXaResource esbxa = new EnhancedStringBuilderXaResource();
    Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

    // Setup the local values
    sbxa.append(LOCAL_RESOURCE1);
    esbxa.append(LOCAL_RESOURCE2);

    assertEquals(sbxa.toString(), LOCAL_RESOURCE1);
    assertEquals(esbxa.toString(), LOCAL_RESOURCE2);

    // have sbxa go global and test
    try {
      sbxa.start(xid1, XAResource.TMNOFLAGS);
      // set the global values
      sbxa.append(GLOBAL_RESOURCE1);

      // make sure x2 is global now
      assertEquals(sbxa.toString(),GLOBAL_RESOURCE1);
      assertEquals(esbxa.toString(), LOCAL_RESOURCE2);

      sbxa.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("testOutsideEnd should not have thrown an exception");
    }

    assertEquals(sbxa.toString(), LOCAL_RESOURCE1);
    assertEquals(esbxa.toString(), LOCAL_RESOURCE2);


    // have esbxa go global and test
    try {
      esbxa.start(xid1, XAResource.TMNOFLAGS);
      // set the global values
      esbxa.append(GLOBAL_RESOURCE2);

      // make sure x2 is global now
      assertEquals(sbxa.toString(), LOCAL_RESOURCE1);
      assertEquals(esbxa.toString(), GLOBAL_RESOURCE2);

      esbxa.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("testOutsideEnd should not have thrown an exception");
    }

    assertEquals(sbxa.toString(), LOCAL_RESOURCE1);
    assertEquals(esbxa.toString(), LOCAL_RESOURCE2);

    try {
      sbxa.close(xid1);
    }  catch (XAException xae) {
      fail("Close should not have thrown an exception");
    }
  }

  public void testRecoverAndForget() {
    StringBuilderXaResource sbxa = new StringBuilderXaResource();
    Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

    // Setup the local values
    sbxa.append(LOCAL_RESOURCE1);

    assertEquals(sbxa.toString(), LOCAL_RESOURCE1);

    // have sbxa go global and test
    try {
      sbxa.start(xid1, XAResource.TMNOFLAGS);
      // set the global values
      sbxa.append(GLOBAL_RESOURCE1);

      // make sure x2 is global now
      assertEquals(sbxa.toString(),GLOBAL_RESOURCE1);
      sbxa.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("Start End should not have thrown an exception");
    }

    assertEquals(sbxa.toString(), LOCAL_RESOURCE1);

    try {
      sbxa.prepare(xid1);
    } catch (XAException xae) {
      fail("prepare should not have thrown an exception");
    }

    try {
      Xid[] xids = sbxa.recover(XAResource.TMNOFLAGS);
      assertEquals(1, xids.length);
      assertEquals(xid1, xids[0]);

      sbxa.forget(xids[0]);
    } catch (XAException xae) {
      fail("recover and forget should not have thrown an exception");
    }

    // start no flags should work again
    try {
      sbxa.start(xid1, XAResource.TMNOFLAGS);
      // set the global values
      sbxa.append(GLOBAL_RESOURCE1);

      // make sure x2 is global now
      assertEquals(sbxa.toString(),GLOBAL_RESOURCE1);
      sbxa.end(xid1, XAResource.TMSUCCESS);
    } catch (XAException xae) {
      fail("Start End should not have thrown an exception");
    }

    try {
      sbxa.close(xid1);
    }  catch (XAException xae) {
      fail("Close should not have thrown an exception");
    }
  }

  public void testFail() {
    // We need to make sure that different Resource types that share CpoBaseXaResource do not interfere with each other
    // So two different XAs sharing CpoBaseXaResource should be able to take part of the same transaction and work independently

    StringBuilderXaResource sbxa = new StringBuilderXaResource();
    Xid xid1 = new MyXid(100, new byte[]{0x01}, new byte[]{0x02});

    // Setup the local values
    sbxa.append(LOCAL_RESOURCE1);

    assertEquals(sbxa.toString(), LOCAL_RESOURCE1);

    // Do a fail
    try {
      sbxa.start(xid1, XAResource.TMNOFLAGS);
      // set the global values
      sbxa.append(GLOBAL_RESOURCE1);

      // make sure x2 is global now
      assertEquals(sbxa.toString(), GLOBAL_RESOURCE1);
      sbxa.end(xid1, XAResource.TMFAIL);
      sbxa.prepare(xid1);
      fail("prepare should have thrown an exception");
    } catch (XAException xae) {
      // should be a rollback
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XA_RBROLLBACK.toString()));
    }

    try {
      Xid[] xids = sbxa.recover(XAResource.TMNOFLAGS);
      assertEquals(0, xids.length);
    } catch (XAException xae) {
      fail("recover should not have thrown an exception");
    }

    // Do a rollback now to clean up transaction
    try {
      sbxa.rollback(xid1);
    } catch (XAException xae) {
      fail("Rollback should not have thrown an exception");
    }

        // Do a fail
    try {
      sbxa.start(xid1, XAResource.TMJOIN);
      // make sure x2 is global now
      assertEquals(sbxa.toString(), GLOBAL_RESOURCE1);
      sbxa.end(xid1, XAResource.TMFAIL);
    } catch (XAException xae) {
      fail("Start End should not have thrown an exception");
    }

        // Do a Success
    try {
      sbxa.start(xid1, XAResource.TMJOIN);
      // make sure x2 is global now
      assertEquals(sbxa.toString(), GLOBAL_RESOURCE1);
      sbxa.end(xid1, XAResource.TMSUCCESS);
      sbxa.prepare(xid1);
      fail("prepare should have thrown an exception");
    } catch (XAException xae) {
      // should be a rollback
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaResource.CpoXaError.XA_RBROLLBACK.toString()));
    }

    try {
      Xid[] xids = sbxa.recover(XAResource.TMNOFLAGS);
      assertEquals(0, xids.length);
    } catch (XAException xae) {
      fail("recover should not have thrown an exception");
    }

    try {
      sbxa.close(xid1);
    }  catch (XAException xae) {
      fail("Close should not have thrown an exception");
    }

  }

  public class MyXid implements Xid {
    protected int formatId;
    protected byte gtrid[];
    protected byte bqual[];

    public MyXid() {
    }

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
