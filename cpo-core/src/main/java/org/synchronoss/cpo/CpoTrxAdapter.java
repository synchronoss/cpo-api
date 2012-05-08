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
package org.synchronoss.cpo;

/**
 * CpoTrxAdapter adds commit, rollback, and close functionality to the methods already in CpoAdapter. This allows the
 * user to control the transaction boundries of CPO.
 *
 * @author david berry
 */
public interface CpoTrxAdapter extends CpoAdapter {

  /**
   * Commits the current transaction behind the CpoTrxAdapter
   */
  public void commit() throws CpoException;

  /**
   * Rollback the current transaction behind the CpoTrxAdapter
   */
  public void rollback() throws CpoException;

  /**
   * Closes the current transaction behind the CpoTrxAdapter. All subsequent calls to the CpoTrxAdapter will throw an
   * exception.
   */
  public void close() throws CpoException;

  /**
   * Returns true if the TrxAdapter has been closed, false if it is still active
   */
  public boolean isClosed() throws CpoException;
}
