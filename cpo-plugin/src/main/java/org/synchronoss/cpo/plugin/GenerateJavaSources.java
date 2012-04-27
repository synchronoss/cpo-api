/*
 *  Copyright (C) 2003-2012 David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.plugin;

import java.io.File;
import java.io.FileWriter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.synchronoss.cpo.exporter.CpoClassSourceGenerator;
import org.synchronoss.cpo.meta.CpoMetaDescriptor;
import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 * Plugin goal that will generate the cpo classes based on the xml configuration file
 *
 * @goal generatejavasource
 * @phase generate-sources
 */
public class GenerateJavaSources extends AbstractMojo {

  /**
   * @parameter expression="${cpoConfig}"
   * @required
   */
  private String cpoConfig;

  /**
   * @parameter expression="${project.build.directory}/generated-sources/cpo"
   * @required
   */
  private String outputDir;

  /**
   * A reference to the Maven Project metadata.
   *
   * @parameter expression="${project}"
   * @required
   * @readonly
   */
  protected MavenProject project;

	public void execute() throws MojoExecutionException {
    getLog().info("Cpo config: " + cpoConfig);
		getLog().info("Generating cpo java sources to " + outputDir);

    File srcDir = new File(outputDir);

    getLog().debug("Adding " + srcDir.getAbsolutePath() + " to the project's compile sources.");
    project.addCompileSourceRoot(srcDir.getAbsolutePath());

    File outputDirectory = new File(project.getBuild().getOutputDirectory());
    if (!outputDirectory.exists()) {
      if (!outputDirectory.mkdirs()) {
        throw new MojoExecutionException("Unable to create output directory: " + outputDirectory.getAbsolutePath());
      }
    }

    try {
      CpoMetaDescriptor metaDescriptor = CpoMetaDescriptor.getInstance("Generator", cpoConfig);

      for (CpoClass cpoClass : metaDescriptor.getCpoClasses()) {
        String className = cpoClass.getName();
        File classDir = srcDir;
        if (className.lastIndexOf(".") != -1) {
          String packageName = className.substring(0, className.lastIndexOf("."));
          classDir = new File(srcDir, packageName.replaceAll("\\.", File.separator));
          className = className.substring(className.lastIndexOf(".") + 1);
        }

        if (!classDir.exists()) {
          if (!classDir.mkdirs()) {
            throw new MojoExecutionException("Unable to create class directories: " + classDir.getAbsolutePath());
          }
        }
        File javaFile = new File(classDir, className + ".java");

        CpoClassSourceGenerator classSourceGenerator = new CpoClassSourceGenerator(metaDescriptor);
        cpoClass.acceptMetaDFVisitor(classSourceGenerator);

        FileWriter cw = new FileWriter(javaFile);
        cw.write(classSourceGenerator.getSourceCode());
        cw.flush();
        cw.close();
      }
    } catch (Exception ex) {
      throw new MojoExecutionException("Exception caught", ex);
    }
	}
}

