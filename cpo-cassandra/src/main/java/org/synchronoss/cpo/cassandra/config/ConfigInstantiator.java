package org.synchronoss.cpo.cassandra.config;

/*-
 * [[
 * cassandra
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.helper.ExceptionHelper;

/**
 * Instantiates the Config file processor
 *
 * @param <T> The type of CpoConfigProcessor to instantiate
 */
public class ConfigInstantiator<T> {
  private static final Logger logger = LoggerFactory.getLogger(ConfigInstantiator.class);

  /** Construct a ConfigInstantiator */
  public ConfigInstantiator() {}

  /**
   * Instantiates the CpoConfigProcessor
   *
   * @param className The class to instantiate
   * @return The instantiated CpoConfigProcessor
   * @throws CpoException an error occurred
   */
  public T instantiate(String className) throws CpoException {
    FactoryMethodName factoryMethodName = null;

    // Lets create the Factory
    try {
      Class factoryClass = Class.forName(className);
      factoryMethodName = (FactoryMethodName) factoryClass.getDeclaredConstructor().newInstance();
      logger.debug("Created factory: " + className);
      Method factoryMethod = factoryClass.getMethod(factoryMethodName.getFactoryMethodName());
      return (T) factoryMethod.invoke(factoryMethodName);
    } catch (InstantiationException ie) {
      throw new CpoException(
          "Could not instantiate Factory Class: "
              + className
              + ":"
              + ExceptionHelper.getLocalizedMessage(ie));
    } catch (IllegalAccessException iae) {
      throw new CpoException(
          "Could not access Factory Class: "
              + className
              + ":"
              + ExceptionHelper.getLocalizedMessage(iae));
    } catch (ClassCastException | NoSuchMethodException cce) {
      throw new CpoException(
          "Factory class must implement FactoryMethodName: "
              + className
              + ":"
              + ExceptionHelper.getLocalizedMessage(cce));
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException(
          "Could not find Factory Class: "
              + className
              + ":"
              + ExceptionHelper.getLocalizedMessage(cnfe));
    } catch (InvocationTargetException ite) {
      throw new CpoException(
          "Factory class method threw an exception: "
              + className
              + ":"
              + ExceptionHelper.getLocalizedMessage(ite));
    }
  }
}
