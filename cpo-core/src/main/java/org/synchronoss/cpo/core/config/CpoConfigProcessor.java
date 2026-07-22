package org.synchronoss.cpo.core.config;

/*-
 * [[
 * core
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

import org.synchronoss.cpo.core.CpoAdapterFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.cpoconfig.CtDataSourceConfig;

/**
 * Builds a {@link CpoAdapterFactory} from a single {@code dataConfig} entry parsed out of {@code
 * cpoConfig.xml}.
 *
 * <p>Implementations are datastore-specific (JDBC, Cassandra, etc.); each knows how to translate
 * its {@link CtDataSourceConfig} JAXB binding into a working factory for that datastore.
 *
 * @author dberry
 */
public interface CpoConfigProcessor {

  /**
   * Processes a single data source configuration and builds the adapter factory it describes.
   *
   * @param cpoConfig the JAXB-bound {@code dataConfig} element to process
   * @return a {@link CpoAdapterFactory} configured per {@code cpoConfig}
   * @throws CpoException if the configuration is invalid or the factory cannot be built
   */
  CpoAdapterFactory processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException;
}
