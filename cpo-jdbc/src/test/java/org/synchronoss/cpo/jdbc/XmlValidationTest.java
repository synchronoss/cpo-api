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

import junit.framework.TestCase;
import org.apache.xmlbeans.XmlException;
import org.slf4j.*;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.core.cpoCoreConfig.CpoConfigDocument;
import org.synchronoss.cpo.helper.XmlBeansHelper;

import java.io.*;

/**
 *
 * @author dberry
 */
public class XmlValidationTest extends TestCase {

  private static Logger logger = LoggerFactory.getLogger(XmlValidationTest.class.getName());
  static final String CPO_CONFIG_XML = "/cpoConfig.xml";
  static final String BAD_CPO_CONFIG_XML = "/badConfig.xml";
  
  public XmlValidationTest(String testName) {
    super(testName);
  }
  
  public void testBadXml(){
    InputStream is = CpoAdapterFactory.class.getResourceAsStream(BAD_CPO_CONFIG_XML);

    try {
      CpoConfigDocument configDoc = CpoConfigDocument.Factory.parse(is);
      String errMsg = XmlBeansHelper.validateXml(configDoc);
      if (errMsg==null) {
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
  
  public void testGoodXml(){
    InputStream is = CpoAdapterFactory.class.getResourceAsStream(CPO_CONFIG_XML);

    try {
      CpoConfigDocument configDoc = CpoConfigDocument.Factory.parse(is);
      String errMsg = XmlBeansHelper.validateXml(configDoc);
      if (errMsg!=null) {
        fail("Should have received an error message:" + errMsg);
      }
    } catch (IOException ioe) {
        fail("Could not read config xml");
    } catch (XmlException xe) {
        fail("Config xml was not well formed");
    }
  }
  
  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }
  
  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }
}
