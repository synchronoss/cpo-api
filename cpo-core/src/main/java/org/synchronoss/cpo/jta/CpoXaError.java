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

import javax.transaction.xa.XAException;

/** The XA Error Codes used by Cpo */
public enum CpoXaError {
  XA_RBBASE(XAException.XA_RBBASE), // public static final int XA_RBBASE = 100;
  XA_RBROLLBACK(XAException.XA_RBROLLBACK), // public static final int XA_RBROLLBACK = 100;
  XA_RBCOMMFAIL(XAException.XA_RBCOMMFAIL), // public static final int XA_RBCOMMFAIL = 101;
  XA_RBDEADLOCK(XAException.XA_RBDEADLOCK), // public static final int XA_RBDEADLOCK = 102;
  XA_RBINTEGRITY(XAException.XA_RBINTEGRITY), // public static final int XA_RBINTEGRITY = 103;
  XA_RBOTHER(XAException.XA_RBOTHER), // public static final int XA_RBOTHER = 104;
  XA_RBPROTO(XAException.XA_RBPROTO), // public static final int XA_RBPROTO = 105;
  XA_RBTIMEOUT(XAException.XA_RBTIMEOUT), // public static final int XA_RBTIMEOUT = 106;
  XA_RBTRANSIENT(XAException.XA_RBTRANSIENT), // public static final int XA_RBTRANSIENT = 107;
  XA_RBEND(XAException.XA_RBEND), // public static final int XA_RBEND = 107;
  XA_NOMIGRATE(XAException.XA_NOMIGRATE), // public static final int XA_NOMIGRATE = 9;
  XA_HEURHAZ(XAException.XA_HEURHAZ), // public static final int XA_HEURHAZ = 8;
  XA_HEURCOM(XAException.XA_HEURCOM), // public static final int XA_HEURCOM = 7;
  XA_HEURRB(XAException.XA_HEURRB), // public static final int XA_HEURRB = 6;
  XA_HEURMIX(XAException.XA_HEURMIX), // public static final int XA_HEURMIX = 5;
  XA_RETRY(XAException.XA_RETRY), // public static final int XA_RETRY = 4;
  XA_RDONLY(XAException.XA_RDONLY), // public static final int XA_RDONLY = 3;
  XAER_ASYNC(XAException.XAER_ASYNC), // public static final int XAER_ASYNC = -2;
  XAER_RMERR(XAException.XAER_RMERR), // public static final int XAER_RMERR = -3;
  XAER_NOTA(XAException.XAER_NOTA), // public static final int XAER_NOTA = -4;
  XAER_INVAL(XAException.XAER_INVAL), // public static final int XAER_INVAL = -5;
  XAER_PROTO(XAException.XAER_PROTO), // public static final int XAER_PROTO = -6;
  XAER_RMFAIL(XAException.XAER_RMFAIL), // public static final int XAER_RMFAIL = -7;
  XAER_DUPID(XAException.XAER_DUPID), // public static final int XAER_DUPID = -8;
  XAER_OUTSIDE(XAException.XAER_OUTSIDE); // public static final int XAER_OUTSIDE = -9;

  private final int xaErrorCode;

  CpoXaError(int xaErrorCode) {
    this.xaErrorCode = xaErrorCode;
  }

  /**
   * @param errCode The error code for the XAException
   * @param errString The message for the XAException
   * @return The XAException
   */
  public static XAException createXAException(CpoXaError errCode, String errString) {
    return new XAException(
        String.format("%s(%d): %s", errCode.name(), errCode.xaErrorCode, errString));
  }
}
