package org.synchronoss.cpo.cassandra.exporter;

/*-
 * [[
 * cassandra
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
import org.synchronoss.cpo.cassandra.meta.CassandraCpoAttribute;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoMetaDescriptor;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoArgument;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.synchronoss.cpo.core.meta.domain.CpoFunction;
import org.synchronoss.cpo.core.meta.domain.CpoFunctionGroup;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Exports a fully-populated cassandra meta class to cover the exporter's optional branches. */
public class CassandraMetaExportTest {

  private CassandraCpoMetaDescriptor descriptor;
  private CpoClass enriched;

  @BeforeClass
  public void setUp() throws CpoException {
    for (String name : CpoMetaDescriptor.getCpoMetaDescriptorNames()) {
      if (CpoMetaDescriptor.getInstance(name) instanceof CassandraCpoMetaDescriptor found) {
        descriptor = found;
        break;
      }
    }
    assertNotNull(descriptor, "suite should have loaded a cassandra meta descriptor");

    enriched = descriptor.createCpoClass();
    enriched.setName("CassandraEnrichedBean");
    enriched.setDescription("an enriched cassandra class");

    CassandraCpoAttribute full = (CassandraCpoAttribute) descriptor.createCpoAttribute();
    full.setJavaName("mapAttr");
    full.setJavaType("java.util.Map");
    full.setDataName("map_attr");
    full.setDataType("MAP");
    full.setDescription("a map attribute");
    full.setKeyType("VARCHAR");
    full.setValueType("INT");
    full.setTransformClassName("org.synchronoss.cpo.cassandra.transform.TransformNoOp");
    enriched.addAttribute(full);

    // a non-cassandra attribute exercises the exporter's instanceof guard
    CpoAttribute plain = new CpoAttribute();
    plain.setJavaName("plainAttr");
    plain.setJavaType("java.lang.String");
    plain.setDataName("plain_attr");
    plain.setDataType("VARCHAR");
    enriched.addAttribute(plain);

    CpoFunctionGroup group = descriptor.createCpoFunctionGroup();
    group.setName("cassEnrichedGroup");
    group.setType("CREATE");
    group.setDescription("group description");

    CpoFunction function = descriptor.createCpoFunction();
    function.setName("cassEnrichedFunction");
    function.setExpression("insert into enriched (map_attr) values (?)");
    function.setDescription("function description");

    CpoArgument argument = descriptor.createCpoArgument();
    argument.setAttribute(full);
    argument.setDescription("argument description");
    function.addArgument(argument);

    group.addFunction(function);
    enriched.addFunctionGroup(group);

    descriptor.addCpoClass(enriched);
  }

  @AfterClass
  public void tearDown() throws CpoException {
    // remove the synthetic class so other tests see the original meta
    descriptor.removeCpoClass(enriched);
  }

  @Test
  public void testExportEnrichedCassandraMeta() throws Exception {
    StringWriter writer = new StringWriter();
    descriptor.export(writer);
    String xml = writer.toString();

    assertTrue(xml.contains("CassandraEnrichedBean"), "class should be exported");
    assertTrue(xml.contains("a map attribute"), "attribute description should be exported");
    assertTrue(xml.contains("VARCHAR"), "key type should be exported");
    assertTrue(xml.contains("TransformNoOp"), "transform class should be exported");
    assertTrue(xml.contains("group description"), "group description should be exported");
    assertTrue(xml.contains("function description"), "function description should be exported");
    assertTrue(xml.contains("argument description"), "argument description should be exported");
    assertTrue(xml.contains("plain_attr"), "the non-cassandra attribute should still be exported");
  }
}
