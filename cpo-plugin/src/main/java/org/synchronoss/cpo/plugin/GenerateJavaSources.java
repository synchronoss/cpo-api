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
package org.synchronoss.cpo.plugin;

import org.apache.maven.plugin.*;
import org.apache.maven.project.MavenProject;
import org.synchronoss.cpo.exporter.CpoClassSourceGenerator;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoClass;

import java.io.*;
import java.util.StringTokenizer;

/**
 * Plugin goal that will generate the cpo classes based on the xml configuration file
 *
 * @requiresDependencyResolution
 * @goal generatejavasource
 * @phase generate-sources
 * @configurator include-project-dependencies
 */
public class GenerateJavaSources extends AbstractMojo {

  private enum Scopes {
    test
  }

  /**
   * @parameter expression="${cpoConfig}"
   * @required
   */
  private String cpoConfig;

  /**
   * Default output directory
   *
   * @parameter expression="${project.build.directory}/generated-sources/cpo"
   * @required
   */
  private String outputDir;

  /**
   * Output directory for test scope executions
   *
   * @parameter expression="${project.build.directory}/generated-test-sources/cpo"
   * @required
   */
  private String testOutputDir;

  /**
   * @parameter expression="${scope}" default-value="compile"
   * @required
   */
  private String scope;

  /**
   * @parameter expression="${filter}" default-value=".*"
   */
  private String filter;

  /**
   * A reference to the Maven Project metadata.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project;

  private final String JAVA_EXT = ".java";
  private final String META_DESCRIPTOR_NAME = "Generator-" + System.currentTimeMillis();

	public void execute() throws MojoExecutionException {
    getLog().info("Cpo config: " + cpoConfig);

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
      CpoMetaDescriptor metaDescriptor = CpoMetaDescriptor.getInstance(META_DESCRIPTOR_NAME, cpoConfig, true);

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
            className = className.substring(className.lastIndexOf(".") + 1);
          }

          if (!classDir.exists()) {
            if (!classDir.mkdirs()) {
              throw new MojoExecutionException("Unable to create class directories: " + classDir.getAbsolutePath());
            }
          }
          File javaFile = new File(classDir, className + JAVA_EXT);

          getLog().info("cpo-plugin generated " + javaFile.getAbsolutePath());

          CpoClassSourceGenerator classSourceGenerator = new CpoClassSourceGenerator(metaDescriptor);
          cpoClass.acceptMetaDFVisitor(classSourceGenerator);

          FileWriter cw = new FileWriter(javaFile);
          cw.write(classSourceGenerator.getSourceCode());
          cw.flush();
          cw.close();
        }
      }
    } catch (Exception ex) {
      throw new MojoExecutionException("Exception caught", ex);
    }
	}
}

