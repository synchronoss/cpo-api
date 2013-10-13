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

import junit.framework.TestCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: dberry
 * Date: 12/10/13
 * Time: 19:41 PM
 * To change this template use File | Settings | File Templates.
 */
public class GetCpoAttributesTest extends TestCase {
  private static final Logger logger = LoggerFactory.getLogger(ConstructorTest.class);

   public GetCpoAttributesTest(String name) {
     super(name);
   }

   /**
    * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
    *
    * @author david berry
    * @version '$Id: ConstructorTest.java,v 1.7 2006/01/31 22:55:03 dberry Exp $'
    */
   @Override
   public void setUp() {
   }

   public void testGetCpoAttributes() {
     String method = "testGetCpoAttributes:";
     try {
       CpoAdapter cpoAdapter = CpoAdapterFactory.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
       assertNotNull(method + "cpoAdapter is null", cpoAdapter);
       assertNotNull(method + "DataSourceName is null", cpoAdapter.getDataSourceName());

       List<CpoAttribute> attributes = cpoAdapter.getCpoAttributes("select * from value_object");
       assertEquals("There should be 15 attributes in the database",15,attributes.size());

       logger.debug("=====> DatasourceName: "+cpoAdapter.getDataSourceName());
     } catch (Exception e) {
       fail(method + e.getMessage());
     }
   }

   @Override
   public void tearDown() {
   }
}
