package org.synchronoss.cpo.plugin;

/*-
 * [[
 * plugin
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

import java.io.File;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.maven.model.Build;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/** Unit tests for the generatejavasource mojo. */
public class GenerateJavaSourcesTest {

  private Path workDir;
  private Path metaFile;
  private MavenProject project;

  @BeforeMethod
  public void setUp() throws Exception {
    workDir = Files.createTempDirectory("cpo-plugin-test");
    // the mojo locates the meta XML on the filesystem, so copy it out of the classpath
    metaFile = workDir.resolve("testPluginMeta.xml");
    try (var in = getClass().getResourceAsStream("/testPluginMeta.xml")) {
      assertNotNull(in, "testPluginMeta.xml must be on the test classpath");
      Files.copy(in, metaFile);
    }

    project = new MavenProject();
    Build build = new Build();
    build.setOutputDirectory(workDir.resolve("classes").toString());
    project.getModel().setBuild(build);
  }

  @AfterMethod
  public void tearDown() throws Exception {
    // each mojo instance registers a meta descriptor in the global cache; clear it so
    // repeated runs in the same millisecond cannot collide
    CpoMetaDescriptor.clearAllInstances();
  }

  private GenerateJavaSources newMojo(
      String cpoConfig,
      String scope,
      String filter,
      boolean generateInterface,
      boolean generateClass)
      throws Exception {
    GenerateJavaSources mojo = new GenerateJavaSources();
    set(mojo, "cpoConfig", cpoConfig);
    set(mojo, "outputDir", workDir.resolve("generated-sources").toString());
    set(mojo, "testOutputDir", workDir.resolve("generated-test-sources").toString());
    set(mojo, "scope", scope);
    set(mojo, "filter", filter);
    set(mojo, "generateInterface", generateInterface);
    set(mojo, "generateClass", generateClass);
    set(mojo, "project", project);
    return mojo;
  }

  private static void set(GenerateJavaSources mojo, String field, Object value) throws Exception {
    Field f = GenerateJavaSources.class.getDeclaredField(field);
    f.setAccessible(true);
    f.set(mojo, value);
  }

  private File generatedFile(String rootDir, String name) {
    return workDir
        .resolve(rootDir)
        .resolve("org/synchronoss/cpo/plugin/generated")
        .resolve(name)
        .toFile();
  }

  @Test
  public void testGenerateClassOnly() throws Exception {
    newMojo(metaFile.toString(), "compile", ".*", false, true).execute();

    File bean = generatedFile("generated-sources", "TestBean.java");
    assertTrue(bean.exists(), "TestBean.java should be generated");
    String source = Files.readString(bean.toPath());
    assertTrue(source.contains("class TestBean"), "generated source should declare the class");
    assertTrue(source.contains("getId"), "generated source should have a getter for id");
    assertTrue(source.contains("setAge"), "generated source should have a setter for age");

    assertTrue(
        project.getCompileSourceRoots().stream().anyMatch(r -> r.contains("generated-sources")),
        "output dir should be added to the compile source roots");
  }

  @Test
  public void testGenerateInterfaceAndClass() throws Exception {
    newMojo(metaFile.toString(), "compile", ".*", true, true).execute();

    File iface = generatedFile("generated-sources", "TestBean.java");
    File bean = generatedFile("generated-sources", "TestBeanBean.java");
    assertTrue(iface.exists(), "interface TestBean.java should be generated");
    assertTrue(bean.exists(), "class TestBeanBean.java should be generated");
    assertTrue(
        Files.readString(iface.toPath()).contains("interface TestBean"),
        "generated interface source should declare the interface");
  }

  @Test
  public void testGenerateInterfaceOnly() throws Exception {
    newMojo(metaFile.toString(), "compile", ".*", true, false).execute();

    File iface = generatedFile("generated-sources", "TestBean.java");
    assertTrue(iface.exists(), "interface TestBean.java should be generated");
  }

  @Test
  public void testTestScopeUsesTestOutputDir() throws Exception {
    newMojo(metaFile.toString(), "test", ".*", false, true).execute();

    assertTrue(
        generatedFile("generated-test-sources", "TestBean.java").exists(),
        "test scope should generate into the test output dir");
    assertTrue(
        project.getTestCompileSourceRoots().stream()
            .anyMatch(r -> r.contains("generated-test-sources")),
        "test output dir should be added to the test compile source roots");
  }

  @Test
  public void testWildcardConfigFileName() throws Exception {
    newMojo(workDir.resolve("testPluginMeta*.xml").toString(), "compile", ".*", false, true)
        .execute();

    assertTrue(
        generatedFile("generated-sources", "TestBean.java").exists(),
        "wildcard config name should locate the meta file");
  }

  @Test
  public void testNonMatchingFilterGeneratesNothing() throws Exception {
    newMojo(metaFile.toString(), "compile", "com\\.nomatch\\..*", false, true).execute();

    assertFalse(
        generatedFile("generated-sources", "TestBean.java").exists(),
        "a non-matching filter should generate no sources");
  }

  @Test
  public void testNeitherInterfaceNorClassFails() throws Exception {
    GenerateJavaSources mojo = newMojo(metaFile.toString(), "compile", ".*", false, false);
    expectThrows(MojoExecutionException.class, mojo::execute);
  }

  @Test
  public void testMissingDirectoryFails() throws Exception {
    String badConfig = workDir.resolve("no-such-dir").resolve("meta.xml").toString();
    GenerateJavaSources mojo = newMojo(badConfig, "compile", ".*", false, true);
    expectThrows(MojoExecutionException.class, mojo::execute);
  }

  @Test
  public void testMissingFileFails() throws Exception {
    String badConfig = workDir.resolve("no-such-file.xml").toString();
    GenerateJavaSources mojo = newMojo(badConfig, "compile", ".*", false, true);
    expectThrows(MojoExecutionException.class, mojo::execute);
  }
}
