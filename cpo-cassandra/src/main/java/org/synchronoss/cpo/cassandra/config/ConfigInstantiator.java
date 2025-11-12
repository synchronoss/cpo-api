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
package org.synchronoss.cpo.cassandra.config;

import org.slf4j.*;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;

import java.lang.reflect.*;

/**
 * Instantiates the Config file processor
 *
 * @param <T> The type of CpoConfigProcessor to instantiate
 */
public class ConfigInstantiator<T> {
  private static final Logger logger = LoggerFactory.getLogger(ConfigInstantiator.class);

    /**
     * Construct a ConfigInstantiator
     */
  public ConfigInstantiator(){}

    /**
     * Instantiates the CpoConfigProcessor
     *
     * @param className The class to instantiate
     * @return The instantiated CpoConfigProcessor
     * @throws CpoException an error occurred
     */
  public T instantiate(String className) throws CpoException {
    FactoryMethodName factoryMethodName=null;

    // Lets create the Factory
    try {
      Class factoryClass = Class.forName(className);
      factoryMethodName = (FactoryMethodName)factoryClass.newInstance();
      logger.debug("Created factory: " + className);
      Method factoryMethod = factoryClass.getMethod(factoryMethodName.getFactoryMethodName());
      return (T) factoryMethod.invoke(factoryMethodName);
    } catch (InstantiationException ie) {
      throw new CpoException("Could not instantiate Factory Class: " + className + ":" + ExceptionHelper.getLocalizedMessage(ie));
    } catch (IllegalAccessException iae) {
      throw new CpoException("Could not access Factory Class: " + className + ":" + ExceptionHelper.getLocalizedMessage(iae));
    } catch (ClassCastException | NoSuchMethodException cce) {
      throw new CpoException("Factory class must implement FactoryMethodName: " + className + ":" + ExceptionHelper.getLocalizedMessage(cce));
    } catch (ClassNotFoundException cnfe) {
      throw new CpoException("Could not find Factory Class: " + className + ":" + ExceptionHelper.getLocalizedMessage(cnfe));
    } catch (InvocationTargetException ite) {
      throw new CpoException("Factory class method threw an exception: " + className + ":" + ExceptionHelper.getLocalizedMessage(ite));
    }

  }
}
