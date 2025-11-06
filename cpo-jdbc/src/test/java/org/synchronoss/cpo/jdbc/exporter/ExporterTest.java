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
package org.synchronoss.cpo.jdbc.exporter;

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.exporter.*;
import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.jdbc.meta.JdbcCpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoClass;
import org.testng.annotations.*;
import static org.testng.Assert.*;

import javax.tools.*;
import java.io.*;
import java.util.Arrays;

/**
 * test class for testing the ExporterTest
 *
 * @author Michael Bellomo
 */
public class ExporterTest extends JdbcDbContainerBase {

  private static final Logger logger = LoggerFactory.getLogger(ExporterTest.class);

  private CpoAdapter cpoAdapter = null;
  private JdbcCpoMetaDescriptor metaDescriptor = null;

  public ExporterTest() {
  }

  @BeforeMethod
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter,method + "CpoAdapter is null");
      metaDescriptor = (JdbcCpoMetaDescriptor) cpoAdapter.getCpoMetaDescriptor();
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterMethod
  public void tearDown() {
    cpoAdapter = null;
  }

  @Test
  public void testXmlExport() {
    logger.debug("testXmlExport");
    try {
      MetaXmlObjectExporter metaXmlObjectExporter = new JdbcMetaXmlObjectExporter(metaDescriptor);
      for (CpoClass cpoClass : metaDescriptor.getCpoClasses()) {
        cpoClass.acceptMetaDFVisitor(metaXmlObjectExporter);
      }
      CpoMetaDataDocument doc = metaXmlObjectExporter.getCpoMetaDataDocument();

      // doc better be valid
      logger.debug("validating doc");
      assertTrue(doc.validate());

      // make sure it saved the data right

      // should be 3 classes in here
      assertEquals(3, doc.getCpoMetaData().getCpoClassArray().length);

      boolean found = false;
      for (CtClass ctClass : doc.getCpoMetaData().getCpoClassArray()) {
        // validate the ValueObject
        if (ctClass.getName().equals(ValueObject.class.getName())) {
          found = true;
        }
      }
      assertTrue(found);
    } catch (CpoException e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    logger.debug("testXmlExport complete");
  }

  @Test
  public void testLegacyClassSourceExport() {
    logger.debug("testLegacyClassSourceExport");
    try {
      CpoLegacyClassSourceGenerator classSourceGenerator = new CpoLegacyClassSourceGenerator(metaDescriptor);

      logger.debug("Generating java source");
      CpoClass cpoClass = metaDescriptor.getMetaClass(ValueObjectFactory.createValueObject());
      cpoClass.acceptMetaDFVisitor(classSourceGenerator);
      String classSource = classSourceGenerator.getSourceCode();

      // validate that we got something
      assertNotNull(classSource);
      assertFalse(classSource.isEmpty());

      // write the file
      File javaFile = new File("target", classSourceGenerator.getClassName() + ".java");
      logger.debug("Saving class source to " + javaFile.getAbsolutePath());
      FileWriter cw = new FileWriter(javaFile);
      cw.write(classSource);
      cw.flush();
      cw.close();

      // let's try to compile the file
      logger.debug("Compiling class source");
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile));
      boolean result = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

      // validate the result
      assertTrue(result);

    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    logger.debug("testClassSourceExport complete");
  }

  @Test
  public void testInterfaceSourceExport() {
    logger.debug("testInterfaceSourceExport");
    try {
      CpoInterfaceSourceGenerator interfaceSourceGenerator = new CpoInterfaceSourceGenerator(metaDescriptor);

      logger.debug("Generating interface source");
      CpoClass cpoClass = metaDescriptor.getMetaClass(ValueObjectFactory.createValueObject());
      cpoClass.acceptMetaDFVisitor(interfaceSourceGenerator);
      String interfaceSource = interfaceSourceGenerator.getSourceCode();

      // validate that we got something
      assertNotNull(interfaceSource);
      assertFalse(interfaceSource.isEmpty());

      // write the file
      File javaFile = new File("target", interfaceSourceGenerator.getInterfaceName() + ".java");
      logger.debug("Saving interface source to " + javaFile.getAbsolutePath());
      FileWriter cw = new FileWriter(javaFile);
      cw.write(interfaceSource);
      cw.flush();
      cw.close();

      // let's try to compile the file
      logger.debug("Compiling class source");
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(javaFile));
      boolean result = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

      // validate the result
      assertTrue(result);

    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    logger.debug("testClassSourceExport complete");
  }

  @Test
  public void testClassSourceExport() {
    logger.debug("testClassSourceExport");
    try {
      CpoInterfaceSourceGenerator interfaceSourceGenerator = new CpoInterfaceSourceGenerator(metaDescriptor);

      logger.debug("Generating interface source");
      CpoClass cpoClass = metaDescriptor.getMetaClass(ValueObjectFactory.createValueObject());
      cpoClass.acceptMetaDFVisitor(interfaceSourceGenerator);
      String interfaceSource = interfaceSourceGenerator.getSourceCode();

      // validate that we got something
      assertNotNull(interfaceSource);
      assertFalse(interfaceSource.isEmpty());

      // write the file
      File interfaceFile = new File("target", interfaceSourceGenerator.getInterfaceName() + ".java");
      logger.debug("Saving interface source to " + interfaceFile.getAbsolutePath());
      FileWriter iw = new FileWriter(interfaceFile);
      iw.write(interfaceSource);
      iw.flush();
      iw.close();

      CpoClassSourceGenerator interfaceClassSourceGenerator = new CpoClassSourceGenerator(metaDescriptor);

      logger.debug("Generating java source");
      cpoClass.acceptMetaDFVisitor(interfaceClassSourceGenerator);
      String classSource = interfaceClassSourceGenerator.getSourceCode();

      // validate that we got something
      assertNotNull(classSource);
      assertFalse(classSource.isEmpty());

      // write the file
      File javaFile = new File("target", interfaceClassSourceGenerator.getClassName() + ".java");
      logger.debug("Saving class source to " + javaFile.getAbsolutePath());
      FileWriter cw = new FileWriter(javaFile);
      cw.write(interfaceClassSourceGenerator.getSourceCode());
      cw.flush();
      cw.close();

      // let's try to compile the files
      logger.debug("Compiling interface and class source");
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null);
      Iterable<? extends JavaFileObject> compilationUnits = fileManager.getJavaFileObjectsFromFiles(Arrays.asList(interfaceFile, javaFile));
      boolean result = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();

      // validate the result
      assertTrue(result);

    } catch (Exception e) {
      e.printStackTrace();
      fail(e.getMessage());
    }
    logger.debug("testClassSourceExport complete");
  }
}
