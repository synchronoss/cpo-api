package org.synchronoss.cpo.core.jta;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;
import javax.transaction.xa.Xid;

/**
 * A JTA {@link XAResource} that additionally allows the resource for a specific transaction branch
 * to be closed directly, without waiting for {@code commit}/{@code rollback}/{@code forget}.
 *
 * @author dberry
 */
public interface CpoXaResource extends XAResource {

  /**
   * Closes the resource associated with the given transaction branch.
   *
   * @param xid The id of the XAResource to close
   * @throws XAException An exception occurred closing the resource
   */
  void close(Xid xid) throws XAException;
}
