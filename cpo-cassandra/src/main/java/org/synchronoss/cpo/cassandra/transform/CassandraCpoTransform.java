package org.synchronoss.cpo.cassandra.transform;

/*-
 * [-------------------------------------------------------------------------
 * cassandra
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.CassandraBoundStatementFactory;
import org.synchronoss.cpo.transform.CpoTransform;

/**
 * Custom transforms of Java classes to Datasource classes
 *
 * @param <D> The type of the datasource class
 * @param <J> The type of the java class
 * @author Michael Bellomo
 * @since 9/19/10
 */
public interface CassandraCpoTransform<D, J> extends CpoTransform<D, J> {

  /**
   * Transforms a java class to a datasource class
   *
   * @param cbsf The CassandraBoundStatementFactory
   * @param attributeObject The java attribute to be transformed
   * @return The datasource object
   * @throws CpoException an exception occurred
   */
  D transformOut(CassandraBoundStatementFactory cbsf, J attributeObject) throws CpoException;
}
