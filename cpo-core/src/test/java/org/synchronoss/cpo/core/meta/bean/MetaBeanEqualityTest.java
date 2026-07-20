package org.synchronoss.cpo.core.meta.bean;

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
import java.util.function.BiConsumer;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.testng.annotations.Test;

/**
 * Equality-contract tests for the handwritten meta beans, exercising every per-field comparison in
 * both the value-differs and null-versus-value directions, plus the primitive/wrapper assignability
 * check on CpoAttribute.
 *
 * @author david berry
 */
public class MetaBeanEqualityTest {

  private static final List<BiConsumer<CpoAttributeBean, String>> ATTRIBUTE_FIELD_SETTERS =
      List.of(
          CpoAttributeBean::setJavaName,
          CpoAttributeBean::setJavaType,
          CpoAttributeBean::setDataName,
          CpoAttributeBean::setDataType,
          CpoAttributeBean::setTransformClassName,
          CpoAttributeBean::setDescription);

  // name is deliberately absent: CpoFunctionBean identity is its expression and description
  private static final List<BiConsumer<CpoFunctionBean, String>> FUNCTION_FIELD_SETTERS =
      List.of(CpoFunctionBean::setExpression, CpoFunctionBean::setDescription);

  private static CpoAttributeBean attributeBean(String value) {
    CpoAttributeBean bean = new CpoAttributeBean();
    for (BiConsumer<CpoAttributeBean, String> setter : ATTRIBUTE_FIELD_SETTERS) {
      setter.accept(bean, value);
    }
    return bean;
  }

  private static CpoFunctionBean functionBean(String value) {
    CpoFunctionBean bean = new CpoFunctionBean();
    for (BiConsumer<CpoFunctionBean, String> setter : FUNCTION_FIELD_SETTERS) {
      setter.accept(bean, value);
    }
    return bean;
  }

  @Test
  public void testAttributeBeanEqualsContract() {
    CpoAttributeBean base = attributeBean("v");

    assertEquals(base, base, "reflexive");
    assertNotEquals(base, null, "null is never equal");
    assertNotEquals(base, "not a bean", "different class is never equal");
    assertEquals(base, attributeBean("v"), "all fields equal");
    assertEquals(base.hashCode(), attributeBean("v").hashCode(), "equal beans share a hash");
    assertEquals(new CpoAttributeBean(), new CpoAttributeBean(), "all-null beans are equal");
    assertEquals(
        new CpoAttributeBean().hashCode(),
        new CpoAttributeBean().hashCode(),
        "all-null beans share a hash");

    // each field: differing value, null-vs-value, and value-vs-null must all break equality
    for (BiConsumer<CpoAttributeBean, String> setter : ATTRIBUTE_FIELD_SETTERS) {
      CpoAttributeBean differs = attributeBean("v");
      setter.accept(differs, "other");
      assertNotEquals(base, differs, "differing field value");
      assertNotEquals(differs, base, "differing field value, symmetric");

      CpoAttributeBean nulled = attributeBean("v");
      setter.accept(nulled, null);
      assertNotEquals(base, nulled, "value vs null field");
      assertNotEquals(nulled, base, "null vs value field");
    }
  }

  @Test
  public void testFunctionBeanEqualsContract() {
    CpoFunctionBean base = functionBean("v");

    assertEquals(base, base, "reflexive");
    assertNotEquals(base, null, "null is never equal");
    assertNotEquals(base, "not a bean", "different class is never equal");
    assertEquals(base, functionBean("v"), "all fields equal");
    assertEquals(base.hashCode(), functionBean("v").hashCode(), "equal beans share a hash");
    assertEquals(new CpoFunctionBean(), new CpoFunctionBean(), "all-null beans are equal");
    assertEquals(
        new CpoFunctionBean().hashCode(),
        new CpoFunctionBean().hashCode(),
        "all-null beans share a hash");

    for (BiConsumer<CpoFunctionBean, String> setter : FUNCTION_FIELD_SETTERS) {
      CpoFunctionBean differs = functionBean("v");
      setter.accept(differs, "other");
      assertNotEquals(base, differs, "differing field value");
      assertNotEquals(differs, base, "differing field value, symmetric");

      CpoFunctionBean nulled = functionBean("v");
      setter.accept(nulled, null);
      assertNotEquals(base, nulled, "value vs null field");
      assertNotEquals(nulled, base, "null vs value field");
    }

    // the function name is a label, not identity: it does not participate in equals/hashCode
    CpoFunctionBean renamed = functionBean("v");
    renamed.setName("some other name");
    assertEquals(base, renamed, "name must not affect equality");
    assertEquals(base.hashCode(), renamed.hashCode(), "name must not affect the hash");
  }

  @Test
  public void testPrimitiveAssignableFrom() {
    assertTrue(CpoAttribute.isPrimitiveAssignableFrom(int.class, Integer.class));
    assertTrue(CpoAttribute.isPrimitiveAssignableFrom(Integer.class, int.class));
    assertTrue(CpoAttribute.isPrimitiveAssignableFrom(boolean.class, Boolean.class));
    assertTrue(CpoAttribute.isPrimitiveAssignableFrom(long.class, Long.class));

    // wrapper whose simple name does not start with the primitive's name
    assertFalse(CpoAttribute.isPrimitiveAssignableFrom(int.class, Long.class));
    // neither side primitive
    assertFalse(CpoAttribute.isPrimitiveAssignableFrom(String.class, Integer.class));
    // both sides primitive
    assertFalse(CpoAttribute.isPrimitiveAssignableFrom(int.class, long.class));
  }
}
