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

import org.codehaus.classworlds.ClassRealm;
import org.codehaus.plexus.component.configurator.*;
import org.codehaus.plexus.component.configurator.converters.composite.ObjectWithFieldsConverter;
import org.codehaus.plexus.component.configurator.converters.special.ClassRealmConverter;
import org.codehaus.plexus.component.configurator.expression.*;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.slf4j.*;

import java.io.File;
import java.net.*;
import java.util.*;

/**
 * A custom ComponentConfigurator which adds the project's runtime classpath elements
 *
 * @plexus.component role="org.codehaus.plexus.component.configurator.ComponentConfigurator"
 * role-hint="include-project-dependencies"
 * @plexus.requirement role="org.codehaus.plexus.component.configurator.converters.lookup.ConverterLookup"
 * role-hint="default"
 */
public class IncludeProjectDependencies extends AbstractComponentConfigurator {

  private static final Logger LOGGER = LoggerFactory.getLogger(IncludeProjectDependencies.class);

  public void configureComponent(Object component, PlexusConfiguration configuration, ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm, ConfigurationListener listener) throws ComponentConfigurationException {

    addProjectDependenciesToClassRealm(expressionEvaluator, containerRealm);
    converterLookup.registerConverter(new ClassRealmConverter(containerRealm));
    ObjectWithFieldsConverter converter = new ObjectWithFieldsConverter();
    converter.processConfiguration(converterLookup, component, containerRealm.getClassLoader(), configuration, expressionEvaluator, listener);
  }

  private void addProjectDependenciesToClassRealm(ExpressionEvaluator expressionEvaluator, ClassRealm containerRealm) throws ComponentConfigurationException {
    List<String> myClasspathElements = new ArrayList<>();
    try {
      List<String> runtimeClasspathElements = (List<String>)expressionEvaluator.evaluate("${project.runtimeClasspathElements}");
      if (runtimeClasspathElements != null) {
        for (String elem : runtimeClasspathElements) {
          myClasspathElements.add(elem);
        }
      }
    } catch (ExpressionEvaluationException e) {
      throw new ComponentConfigurationException("There was a problem evaluating: ${project.runtimeClasspathElements}", e);
    }
    // Add the project dependencies to the ClassRealm
    final URL[] urls = buildURLs(myClasspathElements);
    for (URL url : urls) {
      containerRealm.addConstituent(url);
    }
  }

  private URL[] buildURLs(List<String> runtimeClasspathElements) throws ComponentConfigurationException {
    // Add the projects classes and dependencies
    List<URL> urls = new ArrayList<>(runtimeClasspathElements.size());
    for (String element : runtimeClasspathElements) {
      try {
        final URL url = new File(element).toURI().toURL();
        urls.add(url);
        if (LOGGER.isDebugEnabled()) {
          LOGGER.debug("Added to project class loader: " + url);
        }
      } catch (MalformedURLException e) {
        throw new ComponentConfigurationException("Unable to access project dependency: " + element, e);
      }
    }
    return urls.toArray(new URL[urls.size()]);
  }
}
