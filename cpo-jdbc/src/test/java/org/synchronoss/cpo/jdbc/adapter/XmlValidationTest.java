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

import org.synchronoss.cpo.cpoconfig.CtCpoConfig;
import org.synchronoss.cpo.helper.XmlHelper;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author dberry
 */
public class XmlValidationTest {

  static final String CPO_CONFIG_XML = "/h2/cpoConfig.xml";
  static final String BAD_CPO_CONFIG_XML = "/badConfig.xml";

  public XmlValidationTest() {}

  @Test
  public void testBadXml() {
    var errBuilder = new StringBuilder();

    CtCpoConfig cpoConfig =
        XmlHelper.unmarshalXmlObject(
            XmlHelper.CPO_CONFIG_XSD, BAD_CPO_CONFIG_XML, CtCpoConfig.class, errBuilder);
    Assert.assertFalse(errBuilder.isEmpty(), "Should have received an error message");
  }

  @Test
  public void testGoodXml() {
    var errBuilder = new StringBuilder();

    CtCpoConfig cpoConfig =
        XmlHelper.unmarshalXmlObject(
            XmlHelper.CPO_CONFIG_XSD, CPO_CONFIG_XML, CtCpoConfig.class, errBuilder);
    Assert.assertTrue(
        errBuilder.isEmpty(),
        "Should not have received an error message: " + errBuilder.toString());
  }
}
