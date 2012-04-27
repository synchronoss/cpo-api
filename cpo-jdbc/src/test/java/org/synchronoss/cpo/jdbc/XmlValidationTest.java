/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.util.SortedMap;
import java.util.TreeMap;
import junit.framework.TestCase;
import org.apache.xmlbeans.XmlException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.core.cpoCoreConfig.CpoConfigDocument;
import org.synchronoss.cpo.core.cpoCoreConfig.CtCpoConfig;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;
import org.synchronoss.cpo.helper.XmlBeansHelper;

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
