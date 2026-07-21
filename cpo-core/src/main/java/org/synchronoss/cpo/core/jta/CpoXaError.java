package org.synchronoss.cpo.core.jta;

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

/**
 * The XA error codes used by Cpo, mirroring the {@code int} constants declared on {@link
 * XAException} so error codes can be passed around as a type-safe enum instead of a bare {@code
 * int}.
 *
 * @author dberry
 */
public enum CpoXaError {
  /** Mirrors {@link XAException#XA_RBBASE}: the inclusive lower bound of the rollback codes. */
  XA_RBBASE(XAException.XA_RBBASE),
  /**
   * Mirrors {@link XAException#XA_RBROLLBACK}: the rollback was caused by an unspecified reason.
   */
  XA_RBROLLBACK(XAException.XA_RBROLLBACK),
  /**
   * Mirrors {@link XAException#XA_RBCOMMFAIL}: the rollback was caused by a communication failure.
   */
  XA_RBCOMMFAIL(XAException.XA_RBCOMMFAIL),
  /** Mirrors {@link XAException#XA_RBDEADLOCK}: a deadlock was detected. */
  XA_RBDEADLOCK(XAException.XA_RBDEADLOCK),
  /**
   * Mirrors {@link XAException#XA_RBINTEGRITY}: a condition that violates the integrity of the
   * resource was detected.
   */
  XA_RBINTEGRITY(XAException.XA_RBINTEGRITY),
  /**
   * Mirrors {@link XAException#XA_RBOTHER}: the resource manager rolled back for a reason not on
   * this list.
   */
  XA_RBOTHER(XAException.XA_RBOTHER),
  /** Mirrors {@link XAException#XA_RBPROTO}: a protocol error occurred in the resource manager. */
  XA_RBPROTO(XAException.XA_RBPROTO),
  /** Mirrors {@link XAException#XA_RBTIMEOUT}: a transaction branch took too long. */
  XA_RBTIMEOUT(XAException.XA_RBTIMEOUT),
  /** Mirrors {@link XAException#XA_RBTRANSIENT}: may retry the transaction branch. */
  XA_RBTRANSIENT(XAException.XA_RBTRANSIENT),
  /** Mirrors {@link XAException#XA_RBEND}: the inclusive upper bound of the rollback codes. */
  XA_RBEND(XAException.XA_RBEND),
  /**
   * Mirrors {@link XAException#XA_NOMIGRATE}: resumption must occur where the suspension occurred.
   */
  XA_NOMIGRATE(XAException.XA_NOMIGRATE),
  /**
   * Mirrors {@link XAException#XA_HEURHAZ}: the transaction branch may have been heuristically
   * completed.
   */
  XA_HEURHAZ(XAException.XA_HEURHAZ),
  /**
   * Mirrors {@link XAException#XA_HEURCOM}: the transaction branch has been heuristically
   * committed.
   */
  XA_HEURCOM(XAException.XA_HEURCOM),
  /**
   * Mirrors {@link XAException#XA_HEURRB}: the transaction branch has been heuristically rolled
   * back.
   */
  XA_HEURRB(XAException.XA_HEURRB),
  /**
   * Mirrors {@link XAException#XA_HEURMIX}: the transaction branch has been heuristically committed
   * and rolled back.
   */
  XA_HEURMIX(XAException.XA_HEURMIX),
  /** Mirrors {@link XAException#XA_RETRY}: routine returned with no effect and may be reissued. */
  XA_RETRY(XAException.XA_RETRY),
  /**
   * Mirrors {@link XAException#XA_RDONLY}: the transaction branch was read-only and has been
   * committed.
   */
  XA_RDONLY(XAException.XA_RDONLY),
  /** Mirrors {@link XAException#XAER_ASYNC}: an asynchronous operation is already outstanding. */
  XAER_ASYNC(XAException.XAER_ASYNC),
  /** Mirrors {@link XAException#XAER_RMERR}: a resource manager error occurred. */
  XAER_RMERR(XAException.XAER_RMERR),
  /** Mirrors {@link XAException#XAER_NOTA}: the {@code Xid} is not valid. */
  XAER_NOTA(XAException.XAER_NOTA),
  /** Mirrors {@link XAException#XAER_INVAL}: invalid arguments were given. */
  XAER_INVAL(XAException.XAER_INVAL),
  /** Mirrors {@link XAException#XAER_PROTO}: the routine was invoked in an improper context. */
  XAER_PROTO(XAException.XAER_PROTO),
  /**
   * Mirrors {@link XAException#XAER_RMFAIL}: the resource manager has failed and is unavailable.
   */
  XAER_RMFAIL(XAException.XAER_RMFAIL),
  /** Mirrors {@link XAException#XAER_DUPID}: the {@code Xid} already exists. */
  XAER_DUPID(XAException.XAER_DUPID),
  /**
   * Mirrors {@link XAException#XAER_OUTSIDE}: the resource manager is doing work outside a global
   * transaction.
   */
  XAER_OUTSIDE(XAException.XAER_OUTSIDE);

  private final int xaErrorCode;

  CpoXaError(int xaErrorCode) {
    this.xaErrorCode = xaErrorCode;
  }

  /**
   * Builds an {@link XAException} carrying a message that embeds this error code's name and numeric
   * value alongside the caller-supplied detail text.
   *
   * @param errCode The error code for the XAException
   * @param errString The message for the XAException
   * @return The XAException
   */
  public static XAException createXAException(CpoXaError errCode, String errString) {
    return new XAException(
        String.format("%s(%d): %s", errCode.name(), errCode.xaErrorCode, errString));
  }
}
