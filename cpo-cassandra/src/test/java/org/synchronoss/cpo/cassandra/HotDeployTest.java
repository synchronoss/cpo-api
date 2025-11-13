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

import static org.testng.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * RetrieveBeanTest is a test class for testing the JdbcAdapter class Constructors
 *
 * @author david berry
 */
public class HotDeployTest {
  private static final Logger logger = LoggerFactory.getLogger(HotDeployTest.class);
  private CpoAdapter cpoAdapter = null;
  private ArrayList<ValueObject> al = new ArrayList<>();
  private File metaFile;

  /**
   * <code>setUp</code> Load the datasource from the properties in the property file
   * jdbc_en_US.properties
   *
   * @author david berry
   * @version '$Id: RetrieveBeanTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */
  @BeforeClass
  public void setUp() {
    String method = "setUp:";
    try {
      metaFile = File.createTempFile("metaData", ".xml", new File("."));
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(CassandraStatics.ADAPTER_CONTEXT_DEFAULT);
      assertNotNull(cpoAdapter, method + "CpoAdapter is null");
      // lets save the existing config before we monkey with it
      cpoAdapter.getCpoMetaDescriptor().export(metaFile);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    vo.setAttrInt(1);
    vo.setAttrBigInt(1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(-6));
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
      ValueObject valObj = ValueObjectFactory.createValueObject();

      // make sure the default retrieve works
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertNotNull(col, "Col size is " + col.size());

      col = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      fail("Should not have gotten here:");
    } catch (Exception e) {
      logger.debug("Received an expected Exception: " + e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles);

      ValueObject valObj = ValueObjectFactory.createValueObject(2);

      // make sure the default retrieve still works
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertEquals(6, col.size(), "Col size is " + col.size());

      List<ValueObject> col2 = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      assertEquals(6, col2.size(), "Col size is " + col2.size());

      for (int i = 0; i < col.size(); i++) {
        assertEquals(col.get(i).getId(), col2.get(i).getId(), "IDs must be equal");
      }

      // make sure the first objects are the same

    } catch (Exception e) {
      String msg = ExceptionHelper.getLocalizedMessage(e);

      fail("Received an unexpected exception: " + msg);
    }
  }

  @Test
  public void testRefreshOverwrite() {
    String method = "testRetrieveBeans:";
    List<ValueObject> col;

    try {
      ValueObject valObj = ValueObjectFactory.createValueObject();

      // make sure the default retrieve works
      col = cpoAdapter.retrieveBeans(null, valObj);
      assertNotNull(col, "Col size is " + col.size());

      col = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      fail("Should not have gotten here:");
    } catch (Exception e) {
      logger.debug("Received an expected Exception: " + e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles, true);

      ValueObject valObj = ValueObjectFactory.createValueObject(2);

      // the old retrieve should no longer be there
      try {
        col = cpoAdapter.retrieveBeans(null, valObj);
        fail("should have thrown a cpo exception");
      } catch (CpoException ce) {
        // do nothing, this is expected
      }

      List<ValueObject> col2 = cpoAdapter.retrieveBeans("HotDeploySelect", valObj);
      assertEquals(6, col2.size(), "Col size is " + col2.size());

    } catch (Exception e) {
      String msg = ExceptionHelper.getLocalizedMessage(e);

      fail("Received an unexpected exception: " + msg);
    }
  }

  @AfterMethod
  public void resetMetaData() {
    String method = "resetMetaData:";
    try {
      // lets reset the metadata to before we changed it
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add(metaFile.getName());
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles, true);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterClass
  public void tearDown() {
    String method = "tearDown:";
    try {
      metaFile.delete();

      cpoAdapter.deleteObjects(ValueObject.FG_DELETE_TESTORDERBYDELETE, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}
