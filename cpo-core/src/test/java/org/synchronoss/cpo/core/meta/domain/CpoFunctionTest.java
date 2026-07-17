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

import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.CoreTestBean;
import org.synchronoss.cpo.core.meta.CoreTestMetaDescriptor;
import org.synchronoss.cpo.core.meta.CpoMetaDescriptor;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

/** Unit tests for CpoFunction argument management and parameter reporting. */
public class CpoFunctionTest {

  @AfterClass
  public void tearDown() throws CpoException {
    CpoMetaDescriptor.clearAllInstances();
  }

  @Test
  public void testArgumentManagement() {
    CpoFunction function = new CpoFunction();
    assertTrue(function.getArguments().isEmpty());

    CpoArgument argument = new CpoArgument();
    argument.setDescription("tracked");
    function.addArgument(argument);
    assertEquals(function.getArguments().size(), 1);

    function.addArgument(null); // no-op
    assertEquals(function.getArguments().size(), 1);

    // argument equality is value-based, so the unknown argument must differ in content
    CpoArgument unknown = new CpoArgument();
    unknown.setDescription("unknown");
    assertFalse(function.removeArgument((CpoArgument) null), "null is not removed");
    assertFalse(function.removeArgument(unknown), "unknown argument is not removed");
    assertTrue(function.removeArgument(argument), "known argument is removed");
    assertTrue(function.getArguments().isEmpty());
  }

  @Test
  public void testRemoveArgumentByIndex() {
    CpoFunction function = new CpoFunction();
    CpoArgument argument = new CpoArgument();
    function.addArgument(argument);

    assertFalse(function.removeArgument(-1), "negative index is rejected");
    assertFalse(function.removeArgument(1), "out-of-range index is rejected");
    assertTrue(function.removeArgument(0), "valid index is removed");
    assertFalse(function.removeArgument(0), "empty list index is rejected");
  }

  @Test
  public void testParameterToString() throws Exception {
    CpoFunction function = new CpoFunction();
    assertEquals(function.parameterToString(null), " null function.");

    // a function with a runtime-loaded attribute reports the getter type
    CpoMetaDescriptor descriptor =
        CpoMetaDescriptor.getInstance("cpoFunctionTest", "coreTestMeta.xml", false);
    assertTrue(descriptor instanceof CoreTestMetaDescriptor);
    var cpoClass = descriptor.getMetaClass(new CoreTestBean());
    CpoAttribute idAttribute = cpoClass.getAttributeJava("id");

    CpoArgument loadedArgument = new CpoArgument();
    loadedArgument.setAttribute(idAttribute);
    function.addArgument(loadedArgument);

    // a null entry in the argument list is skipped
    function.getArguments().add(null);

    String report = function.parameterToString(function);
    assertTrue(
        report.contains("java.lang.String"), "report should name the getter type: " + report);
  }
}
