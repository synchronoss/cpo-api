package org.synchronoss.cpo.cassandra.config;

/*-
 * [[
 * cassandra
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

import com.datastax.oss.driver.api.core.metadata.schema.SchemaChangeListener;
import java.util.Collection;

/**
 * FactoryMethod for creating the collection of SchemaChangeListener to register with the Cassandra
 * session
 *
 * @author dberry
 */
public abstract class SchemaChangeListenerFactory implements FactoryMethodName {

  /** Constructs a SchemaChangeListenerFactory */
  public SchemaChangeListenerFactory() {}

  /**
   * Gets the factory method name
   *
   * @return The factory method name
   */
  public String getFactoryMethodName() {
    return "createSchemaChangeListeners";
  }

  /**
   * Create the listeners
   *
   * @return A collection of SchemaChangeListener
   */
  public abstract Collection<SchemaChangeListener> createSchemaChangeListeners();
}
