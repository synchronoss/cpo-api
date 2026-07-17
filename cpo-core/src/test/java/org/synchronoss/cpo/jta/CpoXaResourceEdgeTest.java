package org.synchronoss.cpo.jta;

/*-
 * [[
 * core
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

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;
import org.synchronoss.cpo.core.jta.CpoXaError;
import org.testng.annotations.Test;

/** Edge-state tests for the XA state machine beyond the happy paths. */
public class CpoXaResourceEdgeTest {

  @Test
  public void testEndWithFailureFlag() throws Exception {
    StringBuilderXaResource xaResource = new StringBuilderXaResource();
    Xid xid = new CpoXaResourceTest().new MyXid(200, new byte[] {0x21}, new byte[] {0x22});

    xaResource.start(xid, XAResource.TMNOFLAGS);
    xaResource.append("failedWork");
    // TMFAIL marks the branch as rollback-only
    xaResource.end(xid, XAResource.TMFAIL);
    xaResource.rollback(xid);
  }

  @Test
  public void testForgetUnknownXid() {
    StringBuilderXaResource xaResource = new StringBuilderXaResource();
    Xid unknown = new CpoXaResourceTest().new MyXid(201, new byte[] {0x31}, new byte[] {0x32});

    try {
      xaResource.forget(unknown);
      fail("forget of an unknown xid should fail");
    } catch (XAException xae) {
      assertTrue(xae.getLocalizedMessage().startsWith(CpoXaError.XAER_NOTA.toString()));
    }
  }

  @Test
  public void testEndOnUnassociatedXid() throws Exception {
    StringBuilderXaResource xaResource = new StringBuilderXaResource();
    Xid xid = new CpoXaResourceTest().new MyXid(202, new byte[] {0x41}, new byte[] {0x42});

    xaResource.start(xid, XAResource.TMNOFLAGS);
    xaResource.end(xid, XAResource.TMSUCCESS);

    // a second end on the already-ended xid is a protocol error
    try {
      xaResource.end(xid, XAResource.TMSUCCESS);
      fail("end of an unassociated xid should fail");
    } catch (XAException expected) {
      // expected
    } finally {
      xaResource.rollback(xid);
    }
  }
}
