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

import junit.framework.TestCase;
import org.slf4j.*;
import org.synchronoss.cpo.*;

import java.util.*;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * RetrieveBeanTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ZZHotDeployTest extends TestCase {

  private static final Logger logger = LoggerFactory.getLogger(ZZHotDeployTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<ValueObject>();

  public ZZHotDeployTest(String name) {
    super(name);
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @Override
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactory.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "IdoAdapter is null", cpoAdapter);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  public void testRefresh() {
    String method = "testRetrieveBeans:";
    Collection<ValueObject> col;


    try {
      ValueObject valObj = new ValueObject();
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("Col size is " + col.size(), col!=null);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    
    try {
      List<String> metaFiles = new ArrayList<String>();
      metaFiles.add("/EmptyClassMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles);

      ValueObject valObj = new ValueObject();
      col = cpoAdapter.retrieveBeans(null, valObj);
      fail(method + "Should have failed as the class meta data should be empty");
      
    } catch (Exception e) {
      String msg = ExceptionHelper.getLocalizedMessage(e);
      
      if (msg.indexOf("Function Group Not Found: LIST:null") == -1)
        fail("Received an unexpected exception");
      else
        logger.debug("Received an expected Exception: "+msg);
    }
  }

 
  @Override
  public void tearDown() {
    String method = "tearDown:";
    cpoAdapter = null;
  }
}