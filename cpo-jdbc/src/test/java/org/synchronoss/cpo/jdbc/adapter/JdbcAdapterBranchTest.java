package org.synchronoss.cpo.jdbc.adapter;

/*-
 * [[
 * jdbc
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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

import ch.qos.logback.classic.Level;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoAdapter;
import org.synchronoss.cpo.core.CpoAdapterFactoryManager;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.CpoNativeFunction;
import org.synchronoss.cpo.core.CpoOrderBy;
import org.synchronoss.cpo.core.CpoQuery;
import org.synchronoss.cpo.core.CpoWhere;
import org.synchronoss.cpo.jdbc.ValueObject;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Branch-focused tests for JdbcCpoAdapter batch, empty-input, and attribute-query paths. */
public class JdbcAdapterBranchTest {

  // unique id base so this class's rows never collide with another test class's
  private static final int IDB = 1600000;

  private CpoAdapter cpoAdapter = null;
  private final ArrayList<ValueObject> al = new ArrayList<>();

  @BeforeClass
  public void setUp() {
    String method = "setUp:";
    try {
      cpoAdapter = CpoAdapterFactoryManager.getCpoAdapter(JdbcStatics.ADAPTER_CONTEXT_JDBC);
      assertNotNull(cpoAdapter, method + "cpoAdapter is null");
    } catch (Exception e) {
      fail(method + e.getMessage());
    }
  }

  @AfterMethod
  public void tearDown() {
    try {
      cpoAdapter.deleteBeans(al);
    } catch (Exception e) {
      fail("tearDown:" + e.getMessage());
    } finally {
      al.clear();
    }
  }

  @Test
  public void testEmptyBeansShortCircuit() throws Exception {
    assertEquals(cpoAdapter.updateBeans(new ArrayList<ValueObject>()), 0);
    assertEquals(cpoAdapter.insertBeans(new ArrayList<ValueObject>()), 0);
    assertEquals(cpoAdapter.deleteBeans(new ArrayList<ValueObject>()), 0);
  }

  @Test
  public void testBatchBoundaryFlush() throws Exception {
    int originalBatchSize = cpoAdapter.getBatchSize();
    try {
      // a batch size smaller than the bean count forces a mid-loop flush plus a remainder
      cpoAdapter.setBatchSize(3);
      List<ValueObject> beans = new ArrayList<>();
      for (int i = 121; i <= 127; i++) {
        ValueObject vo = ValueObjectFactory.createValueObject(IDB + i);
        beans.add(vo);
        al.add(vo);
      }
      assertEquals(cpoAdapter.insertBeans(beans), 7, "all beans insert across batch flushes");
      assertEquals(cpoAdapter.deleteBeans(beans), 7);
      al.clear();
    } finally {
      cpoAdapter.setBatchSize(originalBatchSize);
    }
  }

  @Test
  public void testMultiBeanUpsertLoop() throws Exception {
    // upserts never batch, so multiple beans walk the per-bean function loop
    ValueObject vo1 = ValueObjectFactory.createValueObject(IDB + 131);
    ValueObject vo2 = ValueObjectFactory.createValueObject(IDB + 132);
    al.add(vo1);
    al.add(vo2);
    List<ValueObject> beans = new ArrayList<>();
    beans.add(vo1);
    beans.add(vo2);

    assertEquals(cpoAdapter.upsertBeans(beans), 2, "both beans should be inserted");
    assertEquals(cpoAdapter.existsBean(vo1), 1);
    assertEquals(cpoAdapter.existsBean(vo2), 1);
  }

  @Test
  public void testGetCpoAttributesVariants() throws Exception {
    assertTrue(cpoAdapter.getCpoAttributes("").isEmpty(), "empty expression yields no attributes");

    // columns aliased CPO_ATTRIBUTE/CPO_VALUE take the name-value pair path
    ValueObject vo = ValueObjectFactory.createValueObject(IDB + 141);
    vo.setAttrVarChar("notAnAttributeName");
    al.add(vo);
    cpoAdapter.insertBean(vo);

    assertNotNull(
        cpoAdapter.getCpoAttributes(
            "select attr_varchar as cpo_attribute, attr_smallint as cpo_value from value_object"),
        "cpo_attribute/cpo_value query failed");
  }

  @Test
  public void testNullCriteriaRejected() throws Exception {
    expectThrows(
        CpoException.class,
        () -> cpoAdapter.retrieveBeans(ValueObject.FG_LIST_NULL, (ValueObject) null));
    expectThrows(
        CpoException.class,
        () ->
            cpoAdapter.retrieveBean(
                CpoQuery.group(ValueObject.FG_RETRIEVE_NULL),
                ValueObjectFactory.createValueObject(IDB + 1),
                (ValueObject) null));
    expectThrows(CpoException.class, () -> cpoAdapter.insertBean((ValueObject) null));
    assertTrue(cpoAdapter.getCpoAttributes(null).isEmpty(), "null expression yields no attributes");
  }

  @Test
  public void testNativeExpressionAndEmptyCollectionVariants() throws Exception {
    ValueObject vo = ValueObjectFactory.createValueObject(IDB + 161);
    al.add(vo);
    cpoAdapter.insertBean(vo);

    // no-op native expressions: a null marker and a marker that is not in the expression
    List<CpoNativeFunction> natives = new ArrayList<>();
    natives.add(new CpoNativeFunction(null, null));
    natives.add(new CpoNativeFunction("__NO_SUCH_MARKER__", "junk"));

    // empty (not null) where and orderBy collections exercise the empty-clause branches
    try (var beans =
        cpoAdapter.retrieveBeans(
            CpoQuery.group(ValueObject.FG_LIST_NULL)
                .wheres(new ArrayList<CpoWhere>())
                .orderBys(new ArrayList<CpoOrderBy>())
                .nativeExpressions(natives),
            ValueObjectFactory.createValueObject(),
            ValueObjectFactory.createValueObject())) {
      assertEquals(
          beans
              .filter(b -> Math.abs(b.getId()) >= IDB && Math.abs(b.getId()) < IDB + 100000)
              .count(),
          1);
    }
  }

  @Test
  public void testAttributeValuePairRetrieve() throws Exception {
    // a RETRIEVE whose result set is (CPO_ATTRIBUTE, CPO_VALUE) pairs populates the bean
    // from name-value rows instead of positional columns
    var metaDescriptor = cpoAdapter.getCpoMetaDescriptor();
    var voClass = metaDescriptor.getMetaClass(ValueObjectFactory.createValueObject(IDB + 0));

    var group = metaDescriptor.createCpoFunctionGroup();
    group.setName("AttrValuePair");
    group.setType("RETRIEVE");
    var function = metaDescriptor.createCpoFunction();
    function.setName("attrValuePairFunction");
    function.setExpression(
        "select attr_varchar as cpo_attribute, attr_integer as cpo_value from value_object"
            + " where id = ?");
    var argument = metaDescriptor.createCpoArgument();
    argument.setAttribute(voClass.getAttributeJava("id"));
    function.addArgument(argument);
    group.addFunction(function);
    voClass.addFunctionGroup(group);

    try {
      ValueObject known = ValueObjectFactory.createValueObject(IDB + 171);
      known.setAttrVarChar("ATTR_INTEGER");
      known.setAttrInteger(42);
      al.add(known);
      cpoAdapter.insertBean(known);

      ValueObject retrieved =
          cpoAdapter.retrieveBean("AttrValuePair", ValueObjectFactory.createValueObject(IDB + 171));
      assertNotNull(retrieved, "pair-style retrieve should return a bean");
      assertEquals(
          retrieved.getAttrInteger(), 42, "value should be applied to the named attribute");

      // a row naming an unknown attribute is skipped without failing
      ValueObject unknown = ValueObjectFactory.createValueObject(IDB + 172);
      unknown.setAttrVarChar("NO_SUCH_ATTRIBUTE");
      al.add(unknown);
      cpoAdapter.insertBean(unknown);
      assertNotNull(
          cpoAdapter.retrieveBean(
              "AttrValuePair", ValueObjectFactory.createValueObject(IDB + 172)));
    } finally {
      voClass.removeFunctionGroup(group);
    }
  }

  @Test
  public void testTraceLoggingPaths() throws Exception {
    ch.qos.logback.classic.Logger cpoLogger =
        (ch.qos.logback.classic.Logger) LoggerFactory.getLogger("org.synchronoss.cpo");
    Level originalLevel = cpoLogger.getLevel();
    try {
      cpoLogger.setLevel(Level.TRACE);
      ValueObject vo = ValueObjectFactory.createValueObject(IDB + 151);
      al.add(vo);
      cpoAdapter.insertBean(vo);
      assertNotNull(cpoAdapter.retrieveBean(ValueObjectFactory.createValueObject(IDB + 151)));
      cpoAdapter.deleteBean(vo);
      al.clear();
    } finally {
      cpoLogger.setLevel(originalLevel);
    }
  }
}
