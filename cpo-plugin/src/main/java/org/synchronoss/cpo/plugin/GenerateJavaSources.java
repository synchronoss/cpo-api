/*
 * Copyright (C) 2003-2025 David E. Berry
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
package org.synchronoss.cpo.plugin;

import org.apache.maven.plugin.*;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.project.MavenProject;
import org.synchronoss.cpo.exporter.*;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoClass;

import java.io.*;
import java.util.StringTokenizer;
import org.apache.commons.io.filefilter.WildcardFileFilter;

/**
 * Plugin goal that will generate the cpo classes based on the xml configuration file
 */
@Mojo (name = "generatejavasource")
public class GenerateJavaSources extends AbstractMojo {

  private enum Scopes {
    test
  }

  @Parameter (property = "cpoConfig", required = true)
  private String cpoConfig;

  /**
   * Default output directory
   */
  @Parameter (property = "outputDir", required = true, defaultValue = "${project.build.directory}/generated-sources/cpo")
  private String outputDir;

  /**
   * Output directory for test scope executions
   */
  @Parameter (property = "testOutputDir", required = true, defaultValue = "${project.build.directory}/generated-test-sources/cpo")
  private String testOutputDir;

  @Parameter (property = "scope", required = true, defaultValue = "compile")
  private String scope;

  @Parameter (property = "filter", defaultValue = ".*")
  private String filter;

  @Parameter (property = "generateInterface", defaultValue = "false")
  private boolean generateInterface = false;

  @Parameter (property = "generateClass", defaultValue = "true")
  private boolean generateClass = true;

  /**
   * A reference to the Maven Project metadata.
   */
  @Component
  protected MavenProject project;

  private final String JAVA_EXT = ".java";
  private final String META_DESCRIPTOR_NAME = "Generator-" + System.currentTimeMillis();

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
      getLog().debug("Adding " + srcDir.getAbsolutePath() + " to the project's test compile sources.");
    } else {
      // default scope, so use output directory and add to compile path
      srcDir = new File(outputDir);
      project.addCompileSourceRoot(srcDir.getAbsolutePath());
      getLog().debug("Adding " + srcDir.getAbsolutePath() + " to the project's compile sources.");
    }

    getLog().info("Generating cpo java sources to " + srcDir);

    File outputDirectory = new File(project.getBuild().getOutputDirectory());
    if (!outputDirectory.exists()) {
      if (!outputDirectory.mkdirs()) {
        throw new MojoExecutionException("Unable to create output directory: " + outputDirectory.getAbsolutePath());
      }
    }

    try {
      int filenameStart = cpoConfig.lastIndexOf(File.separator);
      String directory = cpoConfig.substring(0, filenameStart);
      getLog().info("Directory name: " + directory);
      File dir = new File(directory);
      if (!dir.exists()) {
        throw new MojoExecutionException("Could not find directory: " + directory);
      }
      String fileName = cpoConfig.substring(filenameStart+1);
      getLog().info("Filename: " + fileName);

      FileFilter fileFilter = new WildcardFileFilter(fileName);
      File foundFiles[] = dir.listFiles(fileFilter);
      if (foundFiles==null || foundFiles.length==0) {
        throw new MojoExecutionException("Could not find file: " + cpoConfig);
      }
      for (File file : foundFiles) {

        CpoMetaDescriptor metaDescriptor = CpoMetaDescriptor.getInstance(META_DESCRIPTOR_NAME, file.getAbsolutePath(), true);

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

            if (!classDir.exists()) {
              if (!classDir.mkdirs()) {
                throw new MojoExecutionException("Unable to create class directories: " + classDir.getAbsolutePath());
              }
            }

            if (generateInterface) {
              CpoInterfaceSourceGenerator interfaceSourceGenerator = new CpoInterfaceSourceGenerator(metaDescriptor);
              cpoClass.acceptMetaDFVisitor(interfaceSourceGenerator);

              File interfaceFile = new File(classDir, interfaceSourceGenerator.getInterfaceName() + JAVA_EXT);
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

