package org.synchronoss.cpo.core.helper;

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

import static org.testng.Assert.*;

import org.synchronoss.cpo.core.CpoException;
import org.testng.annotations.Test;

/** Unit tests for ExceptionHelper message extraction and rethrow behavior. */
public class ExceptionHelperTest {

  @Test
  public void testGetMessage() {
    assertEquals(ExceptionHelper.getMessage(null), "", "null throwable yields empty message");
    assertEquals(ExceptionHelper.getMessage(new Exception("boom")), "boom");

    // message comes from the cause when the outer exception has none
    Exception wrapped = new Exception(new Exception("inner"));
    assertTrue(ExceptionHelper.getMessage(wrapped).contains("inner"));

    // no message anywhere stays null
    assertNull(ExceptionHelper.getMessage(new Exception((String) null)));
  }

  @Test
  public void testGetLocalizedMessage() {
    assertEquals(ExceptionHelper.getLocalizedMessage(null), "");
    assertEquals(ExceptionHelper.getLocalizedMessage(new Exception("boom")), "boom");

    Exception wrapped = new Exception(new Exception("inner"));
    assertTrue(ExceptionHelper.getLocalizedMessage(wrapped).contains("inner"));

    assertNull(ExceptionHelper.getLocalizedMessage(new Exception((String) null)));
  }

  @Test
  public void testReThrowCpoException() {
    CpoException original = new CpoException("original");
    CpoException rethrown =
        expectThrows(
            CpoException.class, () -> ExceptionHelper.reThrowCpoException(original, "wrapper"));
    assertSame(rethrown, original, "an existing CpoException is rethrown as-is");

    Exception plain = new Exception("plain");
    CpoException wrapped =
        expectThrows(
            CpoException.class, () -> ExceptionHelper.reThrowCpoException(plain, "wrapper"));
    assertNotSame(wrapped, plain);
    assertTrue(wrapped.getMessage().contains("wrapper"), "non-CpoException is wrapped");
    assertSame(wrapped.getCause(), plain);
  }
}
