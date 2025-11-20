package org.synchronoss.cpo.cassandra;

/*-
 * [[
 * cassandra
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

import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.cpoCoreConfig.CpoConfigDocument;
import org.synchronoss.cpo.helper.CpoClassLoader;
import org.synchronoss.cpo.helper.XmlBeansHelper;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.testng.Assert.fail;

/**
 * @author dberry
 */
public class XmlValidationTest {

  private static final Logger logger = LoggerFactory.getLogger(XmlValidationTest.class);
  static final String CPO_CONFIG_XML = "/cpoConfig.xml";
  static final String BAD_CPO_CONFIG_XML = "/badConfig.xml";

  @Test
  public void testBadXml() {
    InputStream is = CpoClassLoader.getResourceAsStream(BAD_CPO_CONFIG_XML);

    try {
      CpoConfigDocument configDoc = CpoConfigDocument.Factory.parse(is);
      String errMsg = XmlBeansHelper.validateXml(configDoc);
      if (errMsg == null) {
        fail("Should have received an error message");
      } else {
        logger.debug(errMsg);
      }
    } catch (IOException ioe) {
      fail("Could not read config xml");
    } catch (XmlException xe) {
      fail("Config xml was not well formed");
    }
  }

  @Test
  public void testGoodXml() {
    InputStream is = CpoClassLoader.getResourceAsStream(CPO_CONFIG_XML);

    try {
      CpoConfigDocument configDoc = CpoConfigDocument.Factory.parse(is);
      String errMsg = XmlBeansHelper.validateXml(configDoc);
      if (errMsg != null) {
        fail("Should have received an error message:" + errMsg);
      }
    } catch (IOException ioe) {
      fail("Could not read config xml");
    } catch (XmlException xe) {
      fail("Config xml was not well formed");
    }
  }
}
