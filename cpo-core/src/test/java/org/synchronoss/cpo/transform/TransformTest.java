package org.synchronoss.cpo.transform;

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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

import org.synchronoss.cpo.CpoException;
import org.testng.annotations.Test;

public class TransformTest {

  @Test
  public void testString2Byte() {
    String testString = "This is a test string";
    byte[] bytes = null;
    String transformedString = "";
    CpoTransform<byte[], String> transform = new TransformStringByte();

    try {
      bytes = transform.transformOut(testString);
    } catch (CpoException ex) {
      fail("transformOut threw an exception" + ex.getMessage());
    }

    try {
      transformedString = transform.transformIn(bytes);
    } catch (CpoException ex) {
      fail("transformIn threw an exception" + ex.getMessage());
    }

    assertEquals(testString, transformedString);
  }
}
