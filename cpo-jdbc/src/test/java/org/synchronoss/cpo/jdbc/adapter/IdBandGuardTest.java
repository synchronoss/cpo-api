package org.synchronoss.cpo.jdbc.adapter;

/*-
 * [[
 * jdbc
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

import static org.testng.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import org.testng.annotations.Test;

/**
 * Guards the id-band registry. Test classes that run in parallel each claim a 100,000-wide band of
 * row ids via a {@code private static final int IDB} field; there is no central list of bands, so
 * this test is the enforcement: every declared band must be 100,000-aligned, at or above the first
 * band, and claimed by exactly one class. A duplicate or misaligned band causes data collisions
 * that only surface as intermittent failures under parallel runs.
 *
 * @author david berry
 */
public class IdBandGuardTest {

  private static final String BAND_FIELD = "IDB";
  private static final int BAND_WIDTH = 100000;

  @Test
  public void testIdBandsAreUniqueAndAligned() throws Exception {
    Map<Integer, String> claimedBands = new HashMap<>();
    Path root =
        Paths.get(
            IdBandGuardTest.class.getProtectionDomain().getCodeSource().getLocation().toURI());

    try (Stream<Path> paths = Files.walk(root)) {
      for (Path path : (Iterable<Path>) paths::iterator) {
        String fileName = path.getFileName() == null ? "" : path.getFileName().toString();
        if (!fileName.endsWith(".class") || fileName.contains("$")) {
          continue;
        }

        String className =
            root.relativize(path)
                .toString()
                .replace(java.io.File.separatorChar, '.')
                .replaceAll("\\.class$", "");

        Class<?> clazz;
        try {
          // initialize=false: reading a constant must not run static initializers
          clazz = Class.forName(className, false, IdBandGuardTest.class.getClassLoader());
        } catch (LinkageError e) {
          continue;
        }

        Field bandField;
        try {
          bandField = clazz.getDeclaredField(BAND_FIELD);
        } catch (NoSuchFieldException e) {
          continue;
        }
        if (!Modifier.isStatic(bandField.getModifiers()) || bandField.getType() != int.class) {
          continue;
        }

        bandField.setAccessible(true);
        int band = bandField.getInt(null);

        assertTrue(
            band >= BAND_WIDTH,
            className + " declares id band " + band + " below the first usable band " + BAND_WIDTH);
        assertEquals(
            band % BAND_WIDTH,
            0,
            className + " declares id band " + band + " that is not aligned to " + BAND_WIDTH);

        String otherClass = claimedBands.put(band, className);
        assertNull(
            otherClass,
            "id band " + band + " is claimed by both " + otherClass + " and " + className);
      }
    }

    assertFalse(claimedBands.isEmpty(), "no " + BAND_FIELD + " id bands found; scan is broken");
  }
}
