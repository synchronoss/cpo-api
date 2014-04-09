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
package org.synchronoss.cpo.cassandra;

import org.cassandraunit.AbstractCassandraUnit4TestCase;
import org.cassandraunit.dataset.DataSet;
import org.cassandraunit.dataset.xml.ClassPathXmlDataSet;

/**
 * FIXME - add description
 *
 * @author Michael Bellomo
 * @since 04/05/2014
 */
public abstract class AbstractCassandraTest extends AbstractCassandraUnit4TestCase {

  public AbstractCassandraTest() {
//    super("embeddedCassandra.yaml");
    super();
  }

  @Override
  public DataSet getDataSet() {
    return new ClassPathXmlDataSet("cpoDataSet.xml");
    //return new ClassPathXmlDataSet("simpleDataSet.xml");
  }
}
