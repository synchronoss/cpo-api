package org.synchronoss.cpo.transform.jdbc;

/*-
 * [-------------------------------------------------------------------------
 * jdbc
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
import org.synchronoss.cpo.jdbc.JdbcCallableStatementFactory;
import org.synchronoss.cpo.jdbc.JdbcPreparedStatementFactory;
import org.synchronoss.cpo.transform.CpoTransform;

/**
 * Transforms are used when standard java types to data types do not work.
 *
 * @param <D> The datasource type
 * @param <J> The java type
 * @author Michael Bellomo
 * @since 9/19/10
 */
public interface JdbcCpoTransform<D, J> extends CpoTransform<D, J> {

  /**
   * Transforms a java object to a data source object for the preparedstatement
   *
   * @param jpsf the prepared statement factory that will be receiving the object
   * @param attributeObject the java object to be transformed
   * @return The datasource object
   * @throws CpoException an error transforming the java object to the data object
   */
  D transformOut(JdbcPreparedStatementFactory jpsf, J attributeObject) throws CpoException;

  /**
   * Transforms a java object to a data source object for the preparedstatement
   *
   * @param jcsf the callable statement factory that will be receiving the object
   * @param attributeObject the java object to be transformed
   * @return The datasource object
   * @throws CpoException an error transforming the java object to the data object
   */
  D transformOut(JdbcCallableStatementFactory jcsf, J attributeObject) throws CpoException;

  // TODO - add some transform in statements here.
}
