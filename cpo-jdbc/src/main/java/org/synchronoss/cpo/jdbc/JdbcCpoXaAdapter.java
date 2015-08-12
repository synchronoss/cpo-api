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
package org.synchronoss.cpo.jdbc;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoXaAdapter;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * Created by dberry on 11/8/15.
 */
public class JdbcCpoXaAdapter extends JdbcCpoTrxAdapter implements CpoXaAdapter {

  protected JdbcCpoXaAdapter(JdbcCpoAdapter jdbcCpoAdapter) throws CpoException {
    super(jdbcCpoAdapter);
  }

  @Override
  public void commit(Xid xid, boolean b) throws XAException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void end(Xid xid, int i) throws XAException {
    throw new UnsupportedOperationException();
  }

  @Override
  public void forget(Xid xid) throws XAException {
    throw new UnsupportedOperationException();
  }

  @Override
  public int getTransactionTimeout() throws XAException {
    return 0;
  }

  @Override
  public boolean isSameRM(XAResource xaResource) throws XAException {
    return false;
  }

  @Override
  public int prepare(Xid xid) throws XAException {
    return 0;
  }

  @Override
  public Xid[] recover(int i) throws XAException {
    return new Xid[0];
  }

  @Override
  public void rollback(Xid xid) throws XAException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean setTransactionTimeout(int i) throws XAException {
    return false;
  }

  @Override
  public void start(Xid xid, int i) throws XAException {
    throw new UnsupportedOperationException();
  }
}
