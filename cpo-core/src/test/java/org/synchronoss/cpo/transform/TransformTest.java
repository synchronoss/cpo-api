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
package org.synchronoss.cpo.transform;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.synchronoss.cpo.CpoException;

public class TransformTest {

  @Test
  public void testString2Byte() {
    String testString="This is a test string";
    byte[] bytes=null;
    String transformedString="";
    CpoTransform<byte[], String> transform = new TransformStringByte();

    try {
      bytes = transform.transformOut(testString);
    }catch (CpoException ex) {
      fail("transformOut threw an exception"+ex.getMessage());
    }

    try {
      transformedString = transform.transformIn(bytes);
    }catch (CpoException ex) {
      fail("transformIn threw an exception"+ex.getMessage());
    }

    assertEquals(testString, transformedString);
  }
}
