package org.synchronoss.cpo.plugin;

/*-
 * [[
 * plugin
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

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.util.StringTokenizer;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.synchronoss.cpo.core.exporter.CpoClassSourceGenerator;
import org.synchronoss.cpo.core.exporter.CpoInterfaceSourceGenerator;
import org.synchronoss.cpo.core.exporter.CpoLegacyClassSourceGenerator;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.core.meta.domain.CpoClass;

/**
 * Maven goal ({@code cpo:generatejavasource}) that reads one or more CPO meta XML files and
 * generates the corresponding Java interface and/or class source files, adding the generated
 * directory to the project's compile (or test-compile) source roots.
 *
 * <p>Which source kinds are generated is controlled by {@link #generateInterface} and {@link
 * #generateClass}; when only a class is generated (no interface), {@link
 * CpoLegacyClassSourceGenerator} is used instead of {@link CpoClassSourceGenerator} so the class
 * stands alone without an accompanying interface.
 */
@Mojo(
    name = "generatejavasource",
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
    defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateJavaSources extends AbstractMojo {

  /** Creates the mojo. Maven populates its {@code @Parameter} fields via reflection. */
  public GenerateJavaSources() {}

  /** The values {@link #scope} may take. */
  private enum Scopes {
    /** Generate sources into {@link #testOutputDir} and register them as test sources. */
    test
  }

  /** Path to the CPO meta XML file (or a wildcard pattern matching one or more files). */
  @Parameter(property = "cpoConfig", required = true)
  private String cpoConfig;

  /** Default output directory */
  @Parameter(
      property = "outputDir",
      required = true,
      defaultValue = "${project.build.directory}/generated-sources/cpo")
  private String outputDir;

  /** Output directory for test scope executions */
  @Parameter(
      property = "testOutputDir",
      required = true,
      defaultValue = "${project.build.directory}/generated-test-sources/cpo")
  private String testOutputDir;

  /** Which source root to generate into: {@code compile} (default) or {@code test}. */
  @Parameter(property = "scope", required = true, defaultValue = "compile")
  private String scope;

  /** A regular expression; only CPO classes whose name matches it are generated. */
  @Parameter(property = "filter", defaultValue = ".*")
  private String filter;

  /** Whether to generate a Java interface for each matching CPO class. */
  @Parameter(property = "generateInterface", defaultValue = "false")
  private boolean generateInterface = false;

  /** Whether to generate a Java class for each matching CPO class. */
  @Parameter(property = "generateClass", defaultValue = "true")
  private boolean generateClass = true;

  /** A reference to the Maven Project metadata. */
  @Parameter(defaultValue = "${project}", readonly = true)
  protected MavenProject project;

  /** File extension appended to generated source file names. */
  private final String JAVA_EXT = ".java";

  /** A unique name for the {@link CpoMetaDescriptor} instance loaded by this execution. */
  private final String META_DESCRIPTOR_NAME = "Generator-" + System.currentTimeMillis();

  /**
   * Loads the configured CPO meta XML file(s), and for each matching {@link CpoClass} generates the
   * requested interface and/or class source file under the configured output directory.
   *
   * @throws MojoExecutionException if neither {@link #generateInterface} nor {@link #generateClass}
   *     is set, the config file/directory can't be found, an output directory can't be created, or
   *     source generation otherwise fails
   */
  public void execute() throws MojoExecutionException {
    getLog().info("Cpo config: " + cpoConfig);

    if (!generateInterface && !generateClass) {
      throw new MojoExecutionException("You must generate interfaces, classes or both");
    }

    File srcDir;

    if (Scopes.test.toString().equals(scope)) {
      // test scope, so use test output directory and add to test compile path
      srcDir = new File(testOutputDir);
      project.addTestCompileSourceRoot(srcDir.getAbsolutePath());
      getLog()
          .debug("Adding " + srcDir.getAbsolutePath() + " to the project's test compile sources.");
    } else {
      // default scope, so use output directory and add to compile path
      srcDir = new File(outputDir);
      project.addCompileSourceRoot(srcDir.getAbsolutePath());
      getLog().debug("Adding " + srcDir.getAbsolutePath() + " to the project's compile sources.");
    }

    getLog().info("Generating cpo java sources to " + srcDir);

    File outputDirectory = new File(project.getBuild().getOutputDirectory());
    if (!outputDirectory.exists() && !outputDirectory.mkdirs()) {
      throw new MojoExecutionException(
          "Unable to create output directory: " + outputDirectory.getAbsolutePath());
    }

    try {
      int filenameStart = cpoConfig.lastIndexOf(File.separator);
      String directory = cpoConfig.substring(0, filenameStart);
      getLog().info("Directory name: " + directory);
      File dir = new File(directory);
      if (!dir.exists()) {
        throw new MojoExecutionException("Could not find directory: " + directory);
      }
      String fileName = cpoConfig.substring(filenameStart + 1);
      getLog().info("Filename: " + fileName);

      FileFilter fileFilter = new WildcardFileFilter(fileName);
      File foundFiles[] = dir.listFiles(fileFilter);
      if (foundFiles == null || foundFiles.length == 0) {
        throw new MojoExecutionException("Could not find file: " + cpoConfig);
      }
      for (File file : foundFiles) {

        CpoMetaDescriptor metaDescriptor =
            CpoMetaDescriptor.getInstance(META_DESCRIPTOR_NAME, file.getAbsolutePath(), true);

        for (CpoClass cpoClass : metaDescriptor.getCpoClasses()) {

          String className = cpoClass.getName();

          // check the filter
          if (filter != null && className.matches(filter)) {
            File classDir = srcDir;
            if (className.lastIndexOf(".") != -1) {
              String packageName = className.substring(0, className.lastIndexOf("."));
              StringTokenizer tok = new StringTokenizer(packageName, ".");
              while (tok.hasMoreTokens()) {
                String dirName = tok.nextToken();
                classDir = new File(classDir, dirName);
              }
            }

            if (!classDir.exists() && !classDir.mkdirs()) {
              throw new MojoExecutionException(
                  "Unable to create class directories: " + classDir.getAbsolutePath());
            }

            if (generateInterface) {
              CpoInterfaceSourceGenerator interfaceSourceGenerator =
                  new CpoInterfaceSourceGenerator(metaDescriptor);
              cpoClass.acceptMetaDFVisitor(interfaceSourceGenerator);

              File interfaceFile =
                  new File(classDir, interfaceSourceGenerator.getInterfaceName() + JAVA_EXT);
              getLog().info("cpo-plugin generated " + interfaceFile.getAbsolutePath());

              FileWriter iw = new FileWriter(interfaceFile);
              iw.write(interfaceSourceGenerator.getSourceCode());
              iw.flush();
              iw.close();
            }

            if (generateClass) {
              CpoClassSourceGenerator classSourceGenerator;
              if (generateInterface) {
                classSourceGenerator = new CpoClassSourceGenerator(metaDescriptor);
              } else {
                classSourceGenerator = new CpoLegacyClassSourceGenerator(metaDescriptor);
              }
              cpoClass.acceptMetaDFVisitor(classSourceGenerator);

              File javaFile = new File(classDir, classSourceGenerator.getClassName() + JAVA_EXT);
              getLog().info("cpo-plugin generated " + javaFile.getAbsolutePath());

              FileWriter cw = new FileWriter(javaFile);
              cw.write(classSourceGenerator.getSourceCode());
              cw.flush();
              cw.close();
            }
          }
        }
      }
    } catch (Exception ex) {
      ex.printStackTrace();
      throw new MojoExecutionException("Exception caught", ex);
    }
  }
}
