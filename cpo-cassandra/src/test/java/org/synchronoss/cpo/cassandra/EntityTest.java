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

import org.junit.Test;
import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

import java.util.List;

import static org.junit.Assert.*;

/**
 * ConstructorTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class EntityTest {

  private static final Logger logger = LoggerFactory.getLogger(EntityTest.class);

  @Test
  public void testGetDataSourceEntities() {
    String method = "testGetDataSourceEntities:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(method + "cpoAdapter is null", cpoAdapter);

      List<CpoAttribute> attributes = cpoAdapter.getCpoAttributes("select * from value_object");
      for (CpoAttribute attribute : attributes) {
        if (!(attribute instanceof CassandraCpoAttribute))
          fail(attribute.toString()+" Attribute is not a CassandraCpoAttribute");
        dumpAttribute((CassandraCpoAttribute)attribute);
      }
      assertTrue("List size is " + attributes.size(), attributes.size() == 20);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  private void dumpAttribute(CassandraCpoAttribute attribute) {
    logger.debug("DataName: "+attribute.getDataName());
    logger.debug("DataType: "+attribute.getDataType());
    logger.debug("JavaName: "+attribute.getJavaName());
    logger.debug("JavaType: "+attribute.getJavaType());
    logger.debug("DataTypeMapEntry: "+attribute.getDataTypeInt());
  }
}