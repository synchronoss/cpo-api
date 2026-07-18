package org.synchronoss.cpo.core.meta;

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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.List;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.exporter.CpoClassSourceGenerator;
import org.synchronoss.cpo.core.exporter.CpoInterfaceSourceGenerator;
import org.synchronoss.cpo.core.exporter.CpoLegacyClassSourceGenerator;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.meta.domain.CpoClass;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/** Unit tests for CpoMetaDescriptor loading, lookups, export, and source generation. */
public class CpoMetaDescriptorTest {

  private static final String META_XML = "coreTestMeta.xml";
  private static final String BEAN_CLASS = "org.synchronoss.cpo.core.meta.CoreTestBean";

  @AfterClass
  public void tearDown() throws Exception {
    CpoMetaDescriptor.clearAllInstances();
  }

  private CpoMetaDescriptor load(String name) throws CpoException {
    return CpoMetaDescriptor.getInstance(name, META_XML, false);
  }

  @Test
  public void testGetInstanceAndCache() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest1");
    assertNotNull(descriptor);
    assertTrue(CpoMetaDescriptor.isValidMetaDescriptor(descriptor));
    assertEquals(descriptor.getName(), "descTest1");
    assertFalse(descriptor.isCaseSensitive());

    assertSame(CpoMetaDescriptor.getInstance("descTest1"), descriptor, "cache should hit");
    assertTrue(CpoMetaDescriptor.getCpoMetaDescriptorNames().contains("descTest1"));

    CpoMetaDescriptor.removeInstance("descTest1");
    assertNull(CpoMetaDescriptor.getInstance("descTest1"), "removed descriptor is gone");
  }

  @Test
  public void testCaseSensitiveInstance() throws Exception {
    CpoMetaDescriptor descriptor = CpoMetaDescriptor.getInstance("descTestCs", META_XML, true);
    assertTrue(descriptor.isCaseSensitive());
  }

  @Test
  public void testClassAndAttributeLookups() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest2");

    List<CpoClass> classes = descriptor.getCpoClasses();
    assertEquals(classes.size(), 1);
    CpoClass cpoClass = classes.get(0);
    assertEquals(cpoClass.getName(), BEAN_CLASS);

    CpoClass byBean = descriptor.getMetaClass(new CoreTestBean());
    assertNotNull(byBean, "meta class should be found from a bean instance");
    assertEquals(byBean.getName(), BEAN_CLASS);

    CpoAttribute idAttr = cpoClass.getAttributeJava("id");
    assertNotNull(idAttr);
    // getDataTypeName reports the mapped java class name, not the datastore type name
    assertEquals(descriptor.getDataTypeName(idAttr), "java.lang.String");
    assertEquals(descriptor.getDataTypeJavaClass(idAttr), String.class);
    assertEquals(descriptor.getDataTypeInt("INT"), 1);
    assertEquals(descriptor.getDataTypeMapEntry(1).getDataTypeName(), "INT");
    assertTrue(descriptor.getAllowableDataTypes().contains("INT"));

    assertNotNull(descriptor.getExpressionParser());
    assertNotNull(descriptor.getDefaultPackageName());

    // a byte[] getter reports the special array type name
    CpoAttribute dataAttr = byBean.getAttributeJava("data");
    assertNotNull(dataAttr);
    assertEquals(descriptor.getDataTypeName(dataAttr), "byte[]");
  }

  @Test
  public void testCpoClassLookups() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest9");
    CpoClass cpoClass = descriptor.getCpoClasses().get(0);

    assertTrue(
        cpoClass.existsFunctionGroup(org.synchronoss.cpo.core.enums.Crud.CREATE, "createGroup"));
    assertFalse(cpoClass.existsFunctionGroup(org.synchronoss.cpo.core.enums.Crud.DELETE, "nope"));
    expectThrows(
        CpoException.class,
        () -> cpoClass.getFunctionGroup(org.synchronoss.cpo.core.enums.Crud.DELETE, "nope"));

    assertEquals(cpoClass.compareTo(cpoClass), 0, "a class compares equal to itself");

    CpoClass other = descriptor.createCpoClass();
    other.setName("org.other.Bean");
    assertNotEquals(cpoClass.compareTo(other), 0);

    CpoAttribute extra = descriptor.createCpoAttribute();
    extra.setJavaName("extra");
    extra.setJavaType("java.lang.String");
    extra.setDataName("extra");
    extra.setDataType("VARCHAR");
    cpoClass.addAttribute(extra);
    assertNotNull(cpoClass.getAttributeData("extra"), "data-name lookup should find it");
    cpoClass.removeAttribute(extra);
    assertNull(cpoClass.getAttributeData("extra"), "removed attribute is gone");
  }

  @Test
  public void testGetMetaClassEdges() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest10");

    // a null bean cannot be resolved
    try {
      assertNull(descriptor.getMetaClass(null));
    } catch (CpoException acceptable) {
      // acceptable: rejecting a null bean outright
    }

    // an unregistered bean class walks the superclass chain and comes up empty
    try {
      assertNull(descriptor.getMetaClass(new StringBuilder()));
    } catch (CpoException acceptable) {
      // acceptable: unknown classes may be rejected with an exception
    }
  }

  @Test
  public void testFactoryMethods() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest3");
    assertNotNull(descriptor.createCpoClass());
    assertNotNull(descriptor.createCpoAttribute());
    assertNotNull(descriptor.createCpoFunctionGroup());
    assertNotNull(descriptor.createCpoFunction());
    assertNotNull(descriptor.createCpoArgument());
  }

  @Test
  public void testAddAndRemoveCpoClass() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest4");
    CpoClass added = descriptor.createCpoClass();
    added.setName("org.example.Added");
    descriptor.addCpoClass(added);
    assertEquals(descriptor.getCpoClasses().size(), 2);

    descriptor.removeCpoClass(added);
    assertEquals(descriptor.getCpoClasses().size(), 1);
  }

  @Test
  public void testExportVariants() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest5");

    StringWriter writer = new StringWriter();
    descriptor.export(writer);
    String xml = writer.toString();
    assertTrue(xml.contains(BEAN_CLASS), "exported XML should contain the class name");
    assertTrue(xml.contains("createGroup"), "exported XML should contain the function group");

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    descriptor.export(baos);
    assertTrue(baos.toString().contains(BEAN_CLASS));

    File exportFile = Files.createTempFile("cpo-meta-export", ".xml").toFile();
    try {
      descriptor.export(exportFile);
      assertTrue(Files.readString(exportFile.toPath()).contains(BEAN_CLASS));
    } finally {
      assertTrue(exportFile.delete());
    }
  }

  @Test
  public void testRefreshDescriptorMeta() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest6");
    descriptor.refreshDescriptorMeta(List.of(META_XML));
    assertEquals(descriptor.getCpoClasses().size(), 1, "refresh should reload the same meta");

    CpoMetaDescriptor.refreshDescriptorMeta("descTest6", List.of(META_XML), true);
    assertEquals(descriptor.getCpoClasses().size(), 1);

    // refreshing an unknown name is a no-op
    CpoMetaDescriptor.refreshDescriptorMeta("noSuchDescriptor", List.of(META_XML));
  }

  @Test
  public void testSetDefaultPackageName() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest7");
    descriptor.setDefaultPackageName("org.example.pkg");
    assertEquals(descriptor.getDefaultPackageName(), "org.example.pkg");
  }

  @Test
  public void testSourceGenerators() throws Exception {
    CpoMetaDescriptor descriptor = load("descTest8");
    CpoClass cpoClass = descriptor.getCpoClasses().get(0);

    CpoInterfaceSourceGenerator interfaceGen = new CpoInterfaceSourceGenerator(descriptor);
    cpoClass.acceptMetaDFVisitor(interfaceGen);
    assertEquals(interfaceGen.getInterfaceName(), "CoreTestBean");
    String interfaceSource = interfaceGen.getSourceCode();
    assertTrue(interfaceSource.contains("interface CoreTestBean"));
    assertTrue(interfaceSource.contains("getId"));

    CpoClassSourceGenerator classGen = new CpoClassSourceGenerator(descriptor);
    cpoClass.acceptMetaDFVisitor(classGen);
    assertEquals(classGen.getClassName(), "CoreTestBeanBean");
    String classSource = classGen.getSourceCode();
    assertTrue(classSource.contains("class CoreTestBeanBean"));
    assertTrue(classSource.contains("setAge"));

    CpoLegacyClassSourceGenerator legacyGen = new CpoLegacyClassSourceGenerator(descriptor);
    cpoClass.acceptMetaDFVisitor(legacyGen);
    assertEquals(legacyGen.getClassName(), "CoreTestBean");
    assertTrue(legacyGen.getSourceCode().contains("class CoreTestBean"));
  }

  @Test
  public void testBadMetaXmlFails() {
    expectThrows(
        Exception.class, () -> CpoMetaDescriptor.getInstance("descBad", "no-such-meta.xml", false));
  }
}
