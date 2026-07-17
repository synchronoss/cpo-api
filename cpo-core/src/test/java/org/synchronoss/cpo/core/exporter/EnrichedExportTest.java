package org.synchronoss.cpo.core.exporter;

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

import static org.testng.Assert.*;

import java.io.StringWriter;
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

/**
 * Exports and generates source for a fully-populated meta class so every optional-field branch in
 * the exporters and source generators is exercised.
 */
public class EnrichedExportTest {

  private CpoMetaDescriptor descriptor;
  private CpoClass enriched;

  @BeforeClass
  public void setUp() throws Exception {
    descriptor = CpoMetaDescriptor.getInstance("enrichedExportTest", "coreTestMeta.xml", false);

    enriched = descriptor.createCpoClass();
    // a class name without a package exercises the no-package branch of the generators
    enriched.setName("PlainBean");
    enriched.setDescription("a class description");

    String[][] attrs = {
      {"flag", "boolean"},
      {"tinyNumber", "byte"},
      {"rawBytes", "byte[]"},
      {"smallNumber", "short"},
      {"count", "int"},
      {"bigCount", "long"},
      {"letter", "char"},
      {"ratio", "float"},
      {"amount", "double"},
      {"label", "java.lang.String"},
      {"created", "java.util.Date"},
    };
    CpoAttribute first = null;
    for (String[] spec : attrs) {
      CpoAttribute attribute = descriptor.createCpoAttribute();
      attribute.setJavaName(spec[0]);
      attribute.setJavaType(spec[1]);
      attribute.setDataName(spec[0].toUpperCase());
      attribute.setDataType("VARCHAR");
      attribute.setDescription("description of " + spec[0]);
      if (first == null) {
        first = attribute;
        attribute.setTransformClassName("org.synchronoss.cpo.core.transform.TransformStringByte");
      }
      enriched.addAttribute(attribute);
    }

    CpoFunctionGroup group = descriptor.createCpoFunctionGroup();
    group.setName("enrichedGroup");
    group.setType("CREATE");
    group.setDescription("a group description");

    CpoFunction function = descriptor.createCpoFunction();
    function.setName("enrichedFunction");
    function.setExpression("insert into plain_bean (flag) values (?)");
    function.setDescription("a function description");

    CpoArgument argument = descriptor.createCpoArgument();
    argument.setAttribute(first);
    argument.setDescription("an argument description");
    function.addArgument(argument);

    group.addFunction(function);
    enriched.addFunctionGroup(group);

    descriptor.addCpoClass(enriched);
  }

  @AfterClass
  public void tearDown() throws CpoException {
    CpoMetaDescriptor.clearAllInstances();
  }

  @Test
  public void testExportEnrichedMeta() throws Exception {
    StringWriter writer = new StringWriter();
    descriptor.export(writer);
    String xml = writer.toString();

    assertTrue(xml.contains("PlainBean"), "class should be exported");
    assertTrue(xml.contains("a class description"), "class description should be exported");
    assertTrue(xml.contains("description of flag"), "attribute description should be exported");
    assertTrue(xml.contains("TransformStringByte"), "transform class should be exported");
    assertTrue(xml.contains("enrichedGroup"), "group name should be exported");
    assertTrue(xml.contains("a group description"), "group description should be exported");
    assertTrue(xml.contains("a function description"), "function description should be exported");
    assertTrue(xml.contains("an argument description"), "argument description should be exported");
  }

  @Test
  public void testGenerateEnrichedClassSource() {
    CpoLegacyClassSourceGenerator generator = new CpoLegacyClassSourceGenerator(descriptor);
    enriched.acceptMetaDFVisitor(generator);
    String source = generator.getSourceCode();

    assertEquals(generator.getClassName(), "PlainBean");
    assertFalse(source.contains("package "), "a class without a package gets no package line");
    assertTrue(source.contains("boolean"), source.substring(0, Math.min(200, source.length())));
    assertTrue(source.contains("byte[]"), "array attribute type should appear");
    assertTrue(source.contains("java.util.Arrays.equals"), "array equals branch should be used");
    assertTrue(
        source.contains("java.util.Arrays.hashCode"), "array hashCode branch should be used");
    assertTrue(source.contains("char"), "char attribute should appear");
  }

  @Test
  public void testVisitorGuardsWithoutContext() {
    // visiting elements without their parent context must be a safe no-op
    CoreMetaXmlObjectExporter exporter = new CoreMetaXmlObjectExporter(descriptor);
    CpoAttribute attribute = new CpoAttribute();
    attribute.setJavaName("orphan");
    exporter.visit(attribute);
    exporter.visit(new CpoFunctionGroup());
    exporter.visit(new CpoFunction());
    exporter.visit(new CpoArgument());
    assertNotNull(exporter.getCpoMetaData());
  }

  @Test
  public void testGenerateEnrichedInterfaceSource() {
    CpoInterfaceSourceGenerator generator = new CpoInterfaceSourceGenerator(descriptor);
    enriched.acceptMetaDFVisitor(generator);
    String source = generator.getSourceCode();
    assertTrue(source.contains("interface PlainBean"), "interface should be generated");
    assertTrue(source.contains("FG_CREATE_ENRICHEDGROUP"), "function group constant expected");
  }
}
