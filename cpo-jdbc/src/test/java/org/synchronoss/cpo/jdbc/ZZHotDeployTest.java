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

import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.slf4j.*;
import org.synchronoss.cpo.*;

import java.util.*;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * RetrieveBeanTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class ZZHotDeployTest {

  private static final Logger logger = LoggerFactory.getLogger(ZZHotDeployTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();
  private File metaFile = new File("metaData.xml");

  public ZZHotDeployTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @Before
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(method + "CpoAdapter is null", cpoAdapter);
      // lets save the existing config before we monkey with it
      cpoAdapter.getCpoMetaDescriptor().export(metaFile);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = new ValueObjectBean(1);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt(1);
    vo.setAttrInteger(1);
    al.add(vo);
    al.add(new ValueObjectBean(2));
    al.add(new ValueObjectBean(3));
    al.add(new ValueObjectBean(4));
    al.add(new ValueObjectBean(5));
    al.add(new ValueObjectBean(-6));
    try {
      cpoAdapter.insertObjects("TestOrderByInsert", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRefresh() {
    String method = "testRetrieveBeans:";
    List<ValueObject> col;

    try {
      ValueObject valObj = new ValueObjectBean();

      // make sure the default retrieve works
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("Col size is " + col.size(), col!=null);

      col = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      fail("Should not have gotten here:");
    } catch (Exception e) {
      logger.debug("Received an expected Exception: "+e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles);

      ValueObject valObj = new ValueObjectBean(2);

      // make sure the default retrieve still works
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("Col size is " + col.size(), col.size()==6);

      List<ValueObject> col2 = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      assertTrue("Col size is " + col2.size(), col2.size()==6);

      for (int i=0; i<col.size(); i++) {
        assertTrue("IDs must be equal", col.get(i).getId() == col2.get(i).getId());
      }

      // make sure the first objects are the same
    } catch (Exception e) {
      String msg = ExceptionHelper.getLocalizedMessage(e);
      fail("Received an unexpected exception: "+msg);
    }
  }

  @Test
  public void testRefreshOverwrite() {
    String method = "testRetrieveBeans:";
    List<ValueObject> col;

    try {
      ValueObject valObj = new ValueObjectBean();

      // make sure the default retrieve works
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertTrue("Col size is " + col.size(), col!=null);

      col = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      fail("Should not have gotten here:");
    } catch (Exception e) {
      logger.debug("Received an expected Exception: "+e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles, true);

      ValueObject valObj = new ValueObjectBean(2);

      // the old retrieve should no longer be there
      try {
        col = cpoAdapter.retrieveBeans(null, valObj);
        fail("should have thrown a cpo exception");
      } catch (CpoException ce) {
        // do nothing, this is expected
      }

      List<ValueObject> col2 = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      assertTrue("Col size is " + col2.size(), col2.size()==6);

    } catch (Exception e) {
      String msg = ExceptionHelper.getLocalizedMessage(e);
      fail("Received an unexpected exception: "+msg);
    }
  }

  @After
  public void tearDown() {
    String method = "tearDown:";
    try {
      // lets reset the metadata to before we changed it
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add(metaFile.getName());
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles, true);
      metaFile.delete();

      cpoAdapter.deleteObjects("TestOrderByDelete", al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}