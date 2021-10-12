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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.slf4j.*;
import org.synchronoss.cpo.*;

import java.util.*;
import org.synchronoss.cpo.helper.ExceptionHelper;

/**
 * RetrieveBeanTest is a JUnit test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class HotDeployTest extends JdbcDbContainerBase {

  private static final Logger logger = LoggerFactory.getLogger(HotDeployTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();
  private File metaFile = new File("metaData.xml");

  public HotDeployTest() {
  }

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeEach
  public void setUp() {
    String method = "setUp:";

    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      // lets save the existing config before we monkey with it
      cpoAdapter.getCpoMetaDescriptor().export(metaFile);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt((short)1);
    vo.setAttrInteger(1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(-6));
    try {
      cpoAdapter.insertObjects(ValueObject.FG_CREATE_TESTORDERBYINSERT, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @Test
  public void testRefresh() {
    String method = "testRetrieveBeans:";
    List<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();

      // make sure the default retrieve works
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertTrue(col!=null, "Col size is " + col.size());

      col = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      fail("Should not have gotten here:");
    } catch (Exception e) {
      logger.debug("Received an expected Exception: "+e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles);

      ValueObject valObj = ValueObjectFactory.createValueObject(2);

      // make sure the default retrieve still works
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertTrue(col.size()==6, "Col size is " + col.size());

      List<ValueObject> col2 = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      assertTrue(col2.size()==6, "Col size is " + col2.size());

      for (int i=0; i<col.size(); i++) {
        assertTrue(col.get(i).getId() == col2.get(i).getId(), "IDs must be equal");
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
      ValueObject valObj = ValueObjectFactory.createValueObject();

      // make sure the default retrieve works
      col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
      assertTrue(col!=null, "Col size is " + col.size());

      col = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      fail("Should not have gotten here:");
    } catch (Exception e) {
      logger.debug("Received an expected Exception: "+e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles, true);

      ValueObject valObj = ValueObjectFactory.createValueObject(2);

      // the old retrieve should no longer be there
      try {
        col = cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj);
        fail("should have thrown a cpo exception");
      } catch (CpoException ce) {
        // do nothing, this is expected
      }

      List<ValueObject> col2 = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      assertTrue(col2.size()==6, "Col size is " + col2.size());

    } catch (Exception e) {
      String msg = ExceptionHelper.getLocalizedMessage(e);
      fail("Received an unexpected exception: "+msg);
    }
  }

  @AfterEach
  public void tearDown() {
    String method = "tearDown:";
    try {
      // lets reset the metadata to before we changed it
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add(metaFile.getName());
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles, true);
      metaFile.delete();

      cpoAdapter.deleteObjects(ValueObject.FG_DELETE_TESTORDERBYDELETE, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}