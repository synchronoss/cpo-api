package org.synchronoss.cpo;

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

/**
 * CpoTrxAdapter adds commit, rollback, and close functionality to the methods already in
 * CpoAdapter. This allows the user to control the transaction boundries of CPO.
 *
 * @author david berry
 */
public interface CpoTrxAdapter extends CpoAdapter, AutoCloseable {

  /**
   * Commits the current transaction behind the CpoTrxAdapter
   *
   * @throws CpoException An error occurred
   */
  void commit() throws CpoException;

  /**
   * Rollback the current transaction behind the CpoTrxAdapter
   *
   * @throws CpoException An error occurred
   */
  void rollback() throws CpoException;

  /**
   * Closes the current transaction behind the CpoTrxAdapter. All subsequent calls to the
   * CpoTrxAdapter will throw an exception.
   */
  void close() throws CpoException;

  /**
   * Returns true if the TrxAdapter has been closed, false if it is still active
   *
   * @return true if it is closed
   * @throws CpoException An error occurred
   */
  boolean isClosed() throws CpoException;

  /**
   * Returns true if the TrxAdapter is processing a request, false if it is not
   *
   * @return true if the adapter is busy
   * @throws CpoException An error occurred
   */
  boolean isBusy() throws CpoException;
}
