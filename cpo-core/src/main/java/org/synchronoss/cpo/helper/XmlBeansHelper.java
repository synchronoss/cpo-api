/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.helper;

import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlValidationError;

/**
 *
 * @author dberry
 */
public class XmlBeansHelper {
  public static String validateXml(XmlObject xmlObj) {
    StringBuilder sb = new StringBuilder();
    String errMsg = null;
    ArrayList<XmlValidationError> validationErrors = new ArrayList<XmlValidationError>(); 
    XmlOptions validationOptions = new XmlOptions(); 
    validationOptions.setErrorListener(validationErrors); 
    boolean isValid = xmlObj.validate(validationOptions); // to display error we should pass options.
    if (!isValid) {
      for (XmlValidationError es : validationErrors) {
        sb.append(es.getMessage());
      }
      if (sb.length()>0)
        errMsg=sb.toString();
    }
    return errMsg;
  } 
}
