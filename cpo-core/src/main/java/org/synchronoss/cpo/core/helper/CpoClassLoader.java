package org.synchronoss.cpo.core.helper;

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

import java.io.InputStream;

/**
 * This is a proxy class designed to handle the different classloaders needed when running in a
 * container as a skinny war versus running from the console.
 *
 * <p>Currently check the classloader for cpo first, then use the contextClassloader second.
 *
 * @author dberry
 */
public final class CpoClassLoader {

  private CpoClassLoader() {
    // hidden constructor: this class only exposes static helpers
  }

  /**
   * Loads a classpath resource, first via this class's own classloader, then falling back to the
   * current thread's context classloader.
   *
   * @param name the resource name to load
   * @return an open stream for the resource, or {@code null} if it could not be found by either
   *     classloader
   */
  public static InputStream getResourceAsStream(String name) {
    InputStream is = CpoClassLoader.class.getResourceAsStream(name);
    if (is == null) {
      is = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    }
    return is;
  }

  /**
   * Loads a class by name, first via the normal {@link Class#forName(String)} lookup, then falling
   * back to the current thread's context classloader.
   *
   * @param className the fully qualified name of the class to load
   * @return the loaded {@link Class}
   * @throws ClassNotFoundException if the class could not be found by either classloader
   */
  public static Class<?> forName(String className) throws ClassNotFoundException {
    Class<?> clazz = null;
    try {
      clazz = Class.forName(className);
    } catch (ClassNotFoundException cnfe) {
      clazz = Class.forName(className, true, Thread.currentThread().getContextClassLoader());
    }
    return clazz;
  }
}
