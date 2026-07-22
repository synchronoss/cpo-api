package org.synchronoss.cpo.core.meta.bean;

/*-
 * [[
 * core
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

import org.testng.annotations.Test;

/** Unit tests for the meta bean value classes: accessors, equals, hashCode, toString. */
public class MetaBeanTest {

  private CpoAttributeBean attribute(
      String javaName,
      String javaType,
      String dataName,
      String dataType,
      String transform,
      String description) {
    CpoAttributeBean bean = new CpoAttributeBean();
    bean.setJavaName(javaName);
    bean.setJavaType(javaType);
    bean.setDataName(dataName);
    bean.setDataType(dataType);
    bean.setTransformClassName(transform);
    bean.setDescription(description);
    return bean;
  }

  @Test
  public void testAttributeBeanAccessors() {
    CpoAttributeBean bean = attribute("id", "java.lang.String", "ID", "VARCHAR", "t", "desc");
    assertEquals(bean.getJavaName(), "id");
    assertEquals(bean.getJavaType(), "java.lang.String");
    assertEquals(bean.getDataName(), "ID");
    assertEquals(bean.getDataType(), "VARCHAR");
    assertEquals(bean.getTransformClassName(), "t");
    assertEquals(bean.getDescription(), "desc");

    String str = bean.toString();
    assertTrue(str.contains("id"), "toString should include the java name");
    assertTrue(str.contains("VARCHAR"), "toString should include the data type");
  }

  @Test
  public void testAttributeBeanEqualsAndHashCode() {
    CpoAttributeBean a = attribute("id", "java.lang.String", "ID", "VARCHAR", "t", "d");
    CpoAttributeBean same = attribute("id", "java.lang.String", "ID", "VARCHAR", "t", "d");
    CpoAttributeBean empty = new CpoAttributeBean();
    CpoAttributeBean emptyToo = new CpoAttributeBean();

    assertEquals(a, a, "reflexive");
    assertEquals(a, same, "same field values are equal");
    assertEquals(a.hashCode(), same.hashCode(), "equal beans share a hash code");
    assertEquals(empty, emptyToo, "all-null beans are equal");
    assertEquals(empty.hashCode(), emptyToo.hashCode());

    assertNotEquals(a, null);
    assertNotEquals(a, "not a bean");
    assertNotEquals(a, empty);
    assertNotEquals(empty, a);

    // one differing field at a time
    assertNotEquals(a, attribute("x", "java.lang.String", "ID", "VARCHAR", "t", "d"));
    assertNotEquals(a, attribute("id", "int", "ID", "VARCHAR", "t", "d"));
    assertNotEquals(a, attribute("id", "java.lang.String", "X", "VARCHAR", "t", "d"));
    assertNotEquals(a, attribute("id", "java.lang.String", "ID", "INT", "t", "d"));
    assertNotEquals(a, attribute("id", "java.lang.String", "ID", "VARCHAR", "x", "d"));
    assertNotEquals(a, attribute("id", "java.lang.String", "ID", "VARCHAR", "t", "x"));
  }

  @Test
  public void testClassBean() {
    CpoClassBean a = new CpoClassBean();
    a.setName("org.example.Bean");
    a.setDescription("desc");
    assertEquals(a.getName(), "org.example.Bean");
    assertEquals(a.getDescription(), "desc");

    CpoClassBean same = new CpoClassBean();
    same.setName("org.example.Bean");
    same.setDescription("desc");
    assertEquals(a, same);
    assertEquals(a.hashCode(), same.hashCode());

    CpoClassBean otherName = new CpoClassBean();
    otherName.setName("org.example.Other");
    otherName.setDescription("desc");
    assertNotEquals(a, otherName);

    CpoClassBean otherDesc = new CpoClassBean();
    otherDesc.setName("org.example.Bean");
    otherDesc.setDescription("other");
    assertNotEquals(a, otherDesc);

    CpoClassBean empty = new CpoClassBean();
    CpoClassBean emptyToo = new CpoClassBean();
    assertEquals(empty, emptyToo);
    assertNotEquals(a, empty);
    assertNotEquals(a, null);
    assertNotEquals(a, "not a bean");
  }

  @Test
  public void testFunctionBean() {
    CpoFunctionBean a = new CpoFunctionBean();
    a.setName("createFunction");
    a.setExpression("insert into t values (?)");
    a.setDescription("desc");
    assertEquals(a.getName(), "createFunction");
    assertEquals(a.getExpression(), "insert into t values (?)");
    assertEquals(a.getDescription(), "desc");

    CpoFunctionBean same = new CpoFunctionBean();
    same.setName("createFunction");
    same.setExpression("insert into t values (?)");
    same.setDescription("desc");
    assertEquals(a, same);
    assertEquals(a.hashCode(), same.hashCode());

    // equality is defined on expression and description only; name is excluded
    CpoFunctionBean otherName = new CpoFunctionBean();
    otherName.setName("x");
    otherName.setExpression("insert into t values (?)");
    otherName.setDescription("desc");
    assertEquals(a, otherName, "name should not participate in equality");

    CpoFunctionBean otherExpr = new CpoFunctionBean();
    otherExpr.setName("createFunction");
    otherExpr.setExpression("x");
    otherExpr.setDescription("desc");
    assertNotEquals(a, otherExpr);

    CpoFunctionBean otherDesc = new CpoFunctionBean();
    otherDesc.setName("createFunction");
    otherDesc.setExpression("insert into t values (?)");
    otherDesc.setDescription("x");
    assertNotEquals(a, otherDesc);

    CpoFunctionBean empty = new CpoFunctionBean();
    CpoFunctionBean emptyToo = new CpoFunctionBean();
    assertEquals(empty, emptyToo);
    assertNotEquals(a, empty);
    assertNotEquals(a, null);
    assertNotEquals(a, "not a bean");
  }

  @Test
  public void testFunctionGroupBean() {
    CpoFunctionGroupBean a = new CpoFunctionGroupBean();
    a.setName("group");
    a.setType("CREATE");
    a.setDescription("desc");
    assertEquals(a.getName(), "group");
    assertEquals(a.getType(), "CREATE");

    CpoFunctionGroupBean same = new CpoFunctionGroupBean();
    same.setName("group");
    same.setType("CREATE");
    same.setDescription("desc");
    assertEquals(a, same);
    assertEquals(a.hashCode(), same.hashCode());

    CpoFunctionGroupBean otherType = new CpoFunctionGroupBean();
    otherType.setName("group");
    otherType.setType("DELETE");
    otherType.setDescription("desc");
    assertNotEquals(a, otherType);

    CpoFunctionGroupBean empty = new CpoFunctionGroupBean();
    CpoFunctionGroupBean emptyToo = new CpoFunctionGroupBean();
    assertEquals(empty, emptyToo);
    assertNotEquals(a, empty);
    assertNotEquals(a, null);

    // a CpoClassBean with the same name is not equal to a function group bean
    CpoClassBean classBean = new CpoClassBean();
    classBean.setName("group");
    classBean.setDescription("desc");
    assertNotEquals(a, classBean);
  }
}
