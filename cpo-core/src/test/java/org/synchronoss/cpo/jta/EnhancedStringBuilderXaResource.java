package org.synchronoss.cpo.jta;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

import javax.transaction.xa.XAException;
import javax.transaction.xa.XAResource;

/** Created by dberry on 8/9/15. */
public class EnhancedStringBuilderXaResource extends StringBuilderXaResource {
  /**
   * This method is called to determine if the resource manager instance represented by the target
   * object is the same as the resouce manager instance represented by the parameter xares.
   *
   * @param xaResource - An XAResource object whose resource manager instance is to be compared with
   *     the resource manager instance of the target object.
   * @return - true if it's the same RM instance; otherwise false.
   * @throws XAException - An error has occurred. Possible exception values are XAER_RMERR and
   *     XAER_RMFAIL.
   */
  @Override
  public boolean isSameRM(XAResource xaResource) throws XAException {
    if (xaResource == null) throw new XAException(XAException.XAER_INVAL);

    return xaResource instanceof EnhancedStringBuilderXaResource;
  }
}
