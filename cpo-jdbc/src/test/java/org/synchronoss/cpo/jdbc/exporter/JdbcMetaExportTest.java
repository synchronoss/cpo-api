package org.synchronoss.cpo.jdbc.exporter;

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

import java.io.StringWriter;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoArgument;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.synchronoss.cpo.core.meta.domain.CpoFunction;
import org.synchronoss.cpo.core.meta.domain.CpoFunctionGroup;
import org.synchronoss.cpo.jdbc.JdbcCpoArgument;
import org.synchronoss.cpo.jdbc.JdbcCpoAttribute;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Exports a fully-populated jdbc meta class to cover the exporter's optional branches. */
public class JdbcMetaExportTest {

  private JdbcCpoMetaDescriptor descriptor;
  private CpoClass enriched;

  @BeforeClass
  public void setUp() throws CpoException {
    for (String name : CpoMetaDescriptor.getCpoMetaDescriptorNames()) {
      if (CpoMetaDescriptor.getInstance(name) instanceof JdbcCpoMetaDescriptor found) {
        descriptor = found;
        break;
      }
    }
    assertNotNull(descriptor, "suite should have loaded a jdbc meta descriptor");

    enriched = descriptor.createCpoClass();
    enriched.setName("JdbcEnrichedBean");
    enriched.setDescription("an enriched jdbc class");

    JdbcCpoAttribute full = (JdbcCpoAttribute) descriptor.createCpoAttribute();
    full.setJavaName("blobAttr");
    full.setJavaType("byte[]");
    full.setDataName("blob_attr");
    full.setDataType("BLOB");
    full.setDescription("a blob attribute");
    full.setDbTable("value_object");
    full.setDbColumn("blob_attr");
    full.setTransformClassName("org.synchronoss.cpo.jdbc.transform.TransformGZipBytes");
    enriched.addAttribute(full);

    // a non-jdbc attribute exercises the exporter's instanceof guard
    CpoAttribute plain = new CpoAttribute();
    plain.setJavaName("plainAttr");
    plain.setJavaType("java.lang.String");
    plain.setDataName("plain_attr");
    plain.setDataType("VARCHAR");
    enriched.addAttribute(plain);

    CpoFunctionGroup group = descriptor.createCpoFunctionGroup();
    group.setName("jdbcEnrichedGroup");
    group.setType("EXECUTE");
    group.setDescription("group description");

    CpoFunction function = descriptor.createCpoFunction();
    function.setName("jdbcEnrichedFunction");
    function.setExpression("call some_proc(?, ?, ?)");
    function.setDescription("function description");

    JdbcCpoArgument inOutArg = new JdbcCpoArgument();
    inOutArg.setAttribute(full);
    inOutArg.setDescription("both argument");
    inOutArg.setScope("BOTH");
    inOutArg.setTypeInfo("java.sql.Types.BLOB");
    function.addArgument(inOutArg);

    JdbcCpoArgument outArg = new JdbcCpoArgument();
    outArg.setAttribute(full);
    outArg.setDescription("out argument");
    outArg.setScope("OUT");
    function.addArgument(outArg);

    // a non-jdbc argument exercises the argument instanceof guard
    CpoArgument plainArg = new CpoArgument();
    plainArg.setAttribute(plain);
    function.addArgument(plainArg);

    group.addFunction(function);
    enriched.addFunctionGroup(group);

    descriptor.addCpoClass(enriched);
  }

  @AfterClass
  public void tearDown() throws CpoException {
    descriptor.removeCpoClass(enriched);
  }

  @Test
  public void testExportEnrichedJdbcMeta() throws Exception {
    StringWriter writer = new StringWriter();
    descriptor.export(writer);
    String xml = writer.toString();

    assertTrue(xml.contains("JdbcEnrichedBean"), "class should be exported");
    assertTrue(xml.contains("a blob attribute"), "attribute description should be exported");
    assertTrue(xml.contains("value_object"), "db table should be exported");
    assertTrue(xml.contains("TransformGZipBytes"), "transform class should be exported");
    assertTrue(xml.contains("group description"), "group description should be exported");
    assertTrue(xml.contains("function description"), "function description should be exported");
    assertTrue(xml.contains("both argument"), "in/out argument description should be exported");
    assertTrue(xml.contains("out argument"), "out argument description should be exported");
    assertTrue(xml.contains("java.sql.Types.BLOB"), "type info should be exported");
  }
}
