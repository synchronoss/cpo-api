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
package org.synchronoss.cpo.cassandra;

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.cassandra.meta.CassandraCpoAttribute;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.testng.annotations.*;
import static org.testng.Assert.*;

import java.util.List;

/**
 * ConstructorTest is a test class for testing the JdbcAdapter class Constructors
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
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      List<CpoAttribute> attributes = cpoAdapter.getCpoAttributes("select * from value_object");
      for (CpoAttribute attribute : attributes) {
        if (!(attribute instanceof CassandraCpoAttribute))
          fail(attribute.toString()+" Attribute is not a CassandraCpoAttribute");
        dumpAttribute((CassandraCpoAttribute)attribute);
      }
      assertEquals(20, attributes.size(), "List size is " + attributes.size());
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