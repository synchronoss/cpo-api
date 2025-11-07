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
package org.synchronoss.cpo.helper;

import org.synchronoss.cpo.CpoException;

/**
 * @author dberry
 */
public class ExceptionHelper {

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

  public static void reThrowCpoException(Throwable e, String message) throws CpoException {
    if (e instanceof CpoException) {
      throw (CpoException)e;
    } else {
      throw new CpoException(message, e);
    }
  }
}
