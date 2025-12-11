package org.synchronoss.cpo.jdbc.adapter;

/*-
 * [[
 * jdbc
 * ==
 * Copyright (C) 2003 - 2025 David E. Berry
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import static org.testng.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactoryManager;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.ValueObject;
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
  private final ArrayList<ValueObject> al = new ArrayList<>();
  private final File metaFile = new File("metaData.xml");

  public HotDeployTest() {}

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
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
      var metaDescriptor = cpoAdapter.getCpoMetaDescriptor();
      assertNotNull(metaDescriptor, method + "metaDescriptor is null");
      // lets save the existing config before we monkey with it
      metaDescriptor.export(metaFile);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    ValueObject vo = ValueObjectFactory.createValueObject(1);
    vo.setAttrVarChar("Test");
    vo.setAttrSmallInt((short) 1);
    vo.setAttrInteger(1);
    al.add(vo);
    al.add(ValueObjectFactory.createValueObject(2));
    al.add(ValueObjectFactory.createValueObject(3));
    al.add(ValueObjectFactory.createValueObject(4));
    al.add(ValueObjectFactory.createValueObject(5));
    al.add(ValueObjectFactory.createValueObject(-6));
    try {
      cpoAdapter.insertBeans(ValueObject.FG_CREATE_TESTORDERBYINSERT, al);
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
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj); ) {
        long count = beans.count();
        assertEquals(count, 6, "Number of beans is " + count);
      }

      try (Stream<ValueObject> beans = cpoAdapter.retrieveBeans("HotDeploySelect", valObj); ) {
        long count = beans.count();
        fail(method + "Test got to unreachable code " + count);
      }
    } catch (Exception e) {
      logger.debug("Received an expected Exception: " + e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles);

      ValueObject valObj = ValueObjectFactory.createValueObject(2);

      // make sure the default retrieve still works
      List<ValueObject> list1;
      // make sure the default retrieve still works
      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj); ) {
        list1 = beans.toList();
      }
      assertNotNull(list1);
      assertEquals(list1.size(), 6, "Number of beans is " + list1.size());

      List<ValueObject> list2;
      try (Stream<ValueObject> beans = cpoAdapter.retrieveBeans("HotDeploySelect", valObj); ) {
        list2 = beans.toList();
      }
      assertNotNull(list2);
      assertEquals(list2.size(), 6, "Number of beans is " + list2.size());

      // make sure the first objects are the same
      for (int i = 0; i < list1.size(); i++) {
        assertEquals(list1.get(i).getId(), list2.get(i).getId(), "IDs must be equal");
      }

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

      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj); ) {
        long count = beans.count();
        assertEquals(count, 6, "Number of beans is " + count);
      }

      try (Stream<ValueObject> beans = cpoAdapter.retrieveBeans("HotDeploySelect", valObj); ) {
        long count = beans.count();
        fail(method + "Test got to unreachable code " + count);
      }
    } catch (Exception e) {
      logger.debug("Received an expected Exception: " + e.getLocalizedMessage());
    }

    try {
      List<String> metaFiles = new ArrayList<>();
      metaFiles.add("/hotDeployMetaData.xml");
      cpoAdapter.getCpoMetaDescriptor().refreshDescriptorMeta(metaFiles, true);

      ValueObject valObj = ValueObjectFactory.createValueObject(2);

      try (Stream<ValueObject> beans =
          cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, valObj, valObj); ) {
        long count = beans.count();
        assertEquals(count, 6, "Number of beans is " + count);
        fail(method + "Test got to unreachable code " + count);
      } catch (CpoException ce) {
        // do nothing, this is expected
      }

      try (Stream<ValueObject> beans = cpoAdapter.retrieveBeans("HotDeploySelect", valObj); ) {
        long count = beans.count();
        assertEquals(count, 6, "Number of beans is " + count);
      }
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
      assertTrue(metaFile.delete());

      cpoAdapter.deleteBeans(ValueObject.FG_DELETE_TESTORDERBYDELETE, al);
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
    cpoAdapter = null;
  }
}
