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
package org.synchronoss.cpo.jdbc;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

import java.util.List;

/**
 * ConstructorTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class EntityTest extends JdbcDbContainerBase {

  private static final Logger logger = LoggerFactory.getLogger(EntityTest.class);

  public EntityTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: ConstructorTest.java,v 1.7 2006/01/31 22:55:03 dberry Exp $'
   */
  @BeforeEach
  public void setUp() {
  }

  @Test
  public void testGetDataSourceEntities() {
    String method = "testGetDataSourceEntities:";
    try {
      CpoAdapter cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_CLASS);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");

      List<CpoAttribute> attributes = cpoAdapter.getCpoAttributes("select * from lob_test");
      for (CpoAttribute attribute : attributes) {
        if (!(attribute instanceof JdbcCpoAttribute))
          fail("Attribute is not a JdbcCpoAttribute");
        dumpAttribute((JdbcCpoAttribute)attribute);
      }
      assertEquals(4, attributes.size(), "List size is " + attributes);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  private void dumpAttribute(JdbcCpoAttribute attribute) {
    logger.debug("DataName: "+attribute.getDataName());
    logger.debug("DataType: "+attribute.getDataType());
    logger.debug("DbColumn: "+attribute.getDbColumn());
    logger.debug("DbTable: "+attribute.getDbTable());
    logger.debug("JavaName: "+attribute.getJavaName());
    logger.debug("JavaType: "+attribute.getJavaType());
    logger.debug("DataTypeMapEntry: "+attribute.getDataTypeInt());
  }

  @AfterEach
  public void tearDown() {
  }
}