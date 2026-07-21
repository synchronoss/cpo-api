package org.synchronoss.cpo.core.helper;

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

import org.synchronoss.cpo.core.CpoException;

/**
 * Static helpers for extracting readable messages from a {@link Throwable} and for normalizing
 * arbitrary exceptions into {@link CpoException}.
 *
 * <p>{@code getMessage}/{@code getLocalizedMessage} fall back to the exception's {@link
 * Throwable#getCause() cause} when the exception itself carries no message, since CPO frequently
 * wraps lower-level exceptions (e.g. {@code SQLException}) whose message lives on the cause.
 *
 * @author dberry
 */
public class ExceptionHelper {

  private ExceptionHelper() {
    // hidden constructor: this class only exposes static helpers
  }

  /**
   * Gets the message of the given exception, falling back to its cause's message when the exception
   * itself has none.
   *
   * @param e the exception to extract a message from; if {@code null} an empty string is returned
   * @return the exception's message, its cause's message, or {@code ""} if neither is present
   */
  public static String getMessage(Throwable e) {
    String msg = "";

    if (e != null) {
      msg = e.getMessage();
    }

    if (msg == null && e.getCause() != null) {
      msg = e.getCause().getMessage();
    }

    return msg;
  }

  /**
   * Gets the localized message of the given exception, falling back to its cause's localized
   * message when the exception itself has none.
   *
   * @param e the exception to extract a localized message from; if {@code null} an empty string is
   *     returned
   * @return the exception's localized message, its cause's localized message, or {@code ""} if
   *     neither is present
   */
  public static String getLocalizedMessage(Throwable e) {
    String msg = "";

    if (e != null) {
      msg = e.getLocalizedMessage();
    }

    if (msg == null && e.getCause() != null) {
      msg = e.getCause().getLocalizedMessage();
    }

    return msg;
  }

  /**
   * Rethrows {@code e} as a {@link CpoException}: if it already is one it is rethrown unchanged,
   * otherwise it is wrapped in a new {@code CpoException} with the given message.
   *
   * @param e the exception to rethrow or wrap
   * @param message the message to use when {@code e} must be wrapped; ignored if {@code e} is
   *     already a {@link CpoException}
   * @throws CpoException always, either {@code e} itself or a new instance wrapping it
   */
  public static void reThrowCpoException(Throwable e, String message) throws CpoException {
    if (e instanceof CpoException) {
      throw (CpoException) e;
    } else {
      throw new CpoException(message, e);
    }
  }
}
