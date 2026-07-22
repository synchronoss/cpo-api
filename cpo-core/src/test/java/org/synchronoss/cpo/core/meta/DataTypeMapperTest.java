package org.synchronoss.cpo.core.meta;

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

import org.testng.annotations.Test;

/** Unit tests for DataTypeMapper lookups and default handling. */
public class DataTypeMapperTest {

  private DataTypeMapper newMapper() {
    DataTypeMapper mapper = new DataTypeMapper(new DataTypeMapEntry<>(0, "VARCHAR", String.class));
    mapper.addDataTypeEntry(new DataTypeMapEntry<>(1, "INT", int.class));
    return mapper;
  }

  @Test
  public void testLookupByInt() {
    DataTypeMapper mapper = newMapper();
    assertEquals(mapper.getDataTypeMapEntry(1).getDataTypeName(), "INT");
    assertEquals(mapper.getDataTypeJavaClass(1), int.class);
    assertEquals(mapper.getDataTypeJavaClass(Integer.valueOf(1)), int.class);

    // unknown ints fall back to the default entry
    assertEquals(mapper.getDataTypeMapEntry(99).getDataTypeName(), "VARCHAR");
    assertEquals(mapper.getDataTypeJavaClass(99), String.class);
  }

  @Test
  public void testLookupByName() {
    DataTypeMapper mapper = newMapper();
    assertEquals(mapper.getDataTypeMapEntry("INT").getDataTypeInt(), 1);
    assertEquals(mapper.getDataTypeInt("INT"), 1);
    assertEquals(mapper.getDataTypeJavaClass("INT"), int.class);

    // unknown names fall back to the default entry
    assertEquals(mapper.getDataTypeMapEntry("NOPE").getDataTypeName(), "VARCHAR");
    assertEquals(mapper.getDataTypeInt("NOPE"), 0);
    assertEquals(mapper.getDataTypeJavaClass("NOPE"), String.class);
  }

  @Test
  public void testDataTypeNames() {
    DataTypeMapper mapper = newMapper();
    assertTrue(mapper.getDataTypeNames().contains("INT"));
    assertFalse(mapper.getDataTypeNames().isEmpty());
  }

  @Test
  public void testDataTypeMapEntry() throws Exception {
    DataTypeMapEntry<String> entry = new DataTypeMapEntry<>(7, "TEXT", String.class);
    assertEquals(entry.getDataTypeInt(), 7);
    assertEquals(entry.getDataTypeName(), "TEXT");
    assertEquals(entry.getJavaClass(), String.class);
    assertNotNull(entry.toString());
  }
}
