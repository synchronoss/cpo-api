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
package org.synchronoss.cpo.cassandra.transform;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.CassandraBoundStatementFactory;
import org.synchronoss.cpo.transform.CpoTransform;

/**
 * @author Michael Bellomo
 * @since 9/19/10
 */
public interface CassandraCpoTransform<D, J> extends CpoTransform<D, J> {

  public D transformOut(CassandraBoundStatementFactory cbsf, J attributeObject) throws CpoException;

}
