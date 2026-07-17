package org.synchronoss.cpo.core.meta.domain;

/*-
 * [[
 * core
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

import java.util.List;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.CoreTestBean;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/** Unit tests for CpoAttribute method discovery, primitive mapping, and transforms. */
public class CpoAttributeTest {

  private CpoMetaDescriptor descriptor;

  @BeforeClass
  public void setUp() throws CpoException {
    descriptor = CpoMetaDescriptor.getInstance("cpoAttributeTest", "coreTestMeta.xml", false);
  }

  @AfterClass
  public void tearDown() throws CpoException {
    CpoMetaDescriptor.clearAllInstances();
  }

  @Test
  public void testIsPrimitiveAssignableFrom() {
    assertTrue(CpoAttribute.isPrimitiveAssignableFrom(int.class, Integer.class));
    assertTrue(CpoAttribute.isPrimitiveAssignableFrom(Integer.class, int.class));
    assertTrue(CpoAttribute.isPrimitiveAssignableFrom(boolean.class, Boolean.class));
    assertFalse(
        CpoAttribute.isPrimitiveAssignableFrom(int.class, int.class),
        "two primitives are not a boxing pair");
    assertFalse(
        CpoAttribute.isPrimitiveAssignableFrom(String.class, Integer.class),
        "two object types are not a boxing pair");
    assertFalse(
        CpoAttribute.isPrimitiveAssignableFrom(long.class, Integer.class),
        "mismatched primitive and box are not assignable");
  }

  @Test
  public void testFindMethods() throws Exception {
    CpoAttribute attribute = new CpoAttribute();
    List<java.lang.reflect.Method> getters =
        attribute.findMethods(CoreTestBean.class, "getId", 0, true);
    assertEquals(getters.size(), 1, "getId getter should be found");

    List<java.lang.reflect.Method> setters =
        attribute.findMethods(CoreTestBean.class, "setId", 1, false);
    assertEquals(setters.size(), 1, "setId setter should be found");

    assertTrue(
        attribute.findMethods(CoreTestBean.class, "getId", 0, false).isEmpty(),
        "getId has a return value, so a void match should find nothing");
    assertTrue(
        attribute.findMethods(CoreTestBean.class, "noSuchMethod", 0, true).isEmpty(),
        "unknown method should find nothing");
  }

  @Test
  public void testBuildMethodName() {
    CpoAttribute attribute = new CpoAttribute();
    assertEquals(attribute.buildMethodName("get", "id"), "getId");
    assertEquals(attribute.buildMethodName("set", "name"), "setName");
  }

  @Test
  public void testInvokeGetter() throws Exception {
    CpoClass cpoClass = descriptor.getMetaClass(new CoreTestBean());
    CpoAttribute idAttribute = cpoClass.getAttributeJava("id");
    assertNotNull(idAttribute);

    CoreTestBean bean = new CoreTestBean();
    bean.setId("theId");
    assertEquals(idAttribute.invokeGetter(bean), "theId");

    // invoking against a null instance escapes as an unchecked exception
    expectThrows(NullPointerException.class, () -> idAttribute.invokeGetter(null));

    assertNotNull(idAttribute.getGetterReturnType());
    assertNotNull(idAttribute.getSetterParamType());
  }

  @Test
  public void testLoadRunTimeInfoWithTransform() throws Exception {
    CpoAttribute attribute = new CpoAttribute();
    attribute.setJavaName("name");
    attribute.setJavaType("java.lang.String");
    attribute.setDataName("name");
    attribute.setDataType("VARCHAR");
    attribute.setTransformClassName("org.synchronoss.cpo.core.transform.TransformStringByte");

    attribute.loadRunTimeInfo(descriptor, CoreTestBean.class);

    assertNotNull(attribute.getCpoTransform(), "transform should be instantiated");
    assertNotNull(attribute.getTransformInMethod(), "transformIn method should be found");
    assertNotNull(attribute.getTransformOutMethod(), "transformOut method should be found");
  }

  @Test
  public void testLoadRunTimeInfoMissingGetterFails() {
    CpoAttribute attribute = new CpoAttribute();
    attribute.setJavaName("noSuchProperty");
    attribute.setJavaType("java.lang.String");
    attribute.setDataName("nope");
    attribute.setDataType("VARCHAR");

    expectThrows(
        CpoException.class, () -> attribute.loadRunTimeInfo(descriptor, CoreTestBean.class));
  }
}
