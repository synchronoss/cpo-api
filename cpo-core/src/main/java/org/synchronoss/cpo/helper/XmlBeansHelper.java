package org.synchronoss.cpo.helper;

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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;

/**
 * @author dberry
 */
public class XmlBeansHelper {
  public static String validateXml(XmlObject xmlObj) {
    StringBuilder sb = new StringBuilder();
    String errMsg = null;
    ArrayList<XmlError> validationErrors = new ArrayList<>();
    XmlOptions validationOptions = new XmlOptions();
    validationOptions.setErrorListener(validationErrors);
    boolean isValid =
        xmlObj.validate(validationOptions); // to display error we should pass options.
    if (!isValid) {
      for (XmlError es : validationErrors) {
        sb.append(es.getMessage());
      }
      if (sb.length() > 0) errMsg = sb.toString();
    }
    return errMsg;
  }

  public static XmlOptions getXmlOptions() {
    XmlOptions xo = new XmlOptions();
    xo.setCharacterEncoding("utf-8");
    xo.setSaveAggressiveNamespaces();
    xo.setSaveNamespacesFirst();
    xo.setSavePrettyPrint();
    xo.setUseDefaultNamespace();
    return xo;
  }

  public static InputStream loadXmlStream(String xmlStr, StringBuilder errorBuilder) {
    InputStream is = null;
    // See if the file is a uri
    try {
      URL cpoConfigUrl = new URL(xmlStr);
      is = cpoConfigUrl.openStream();
    } catch (IOException e) {
      errorBuilder.append("Uri Not Found: ").append(xmlStr).append("\n");
    }

    // See if the file is a resource in the jar
    if (is == null) is = CpoClassLoader.getResourceAsStream(xmlStr);

    if (is == null) {
      errorBuilder.append("Resource Not Found: ").append(xmlStr).append("\n");
      try {
        // See if the file is a local file on the server
        is = new FileInputStream(xmlStr);
      } catch (FileNotFoundException fnfe) {
        errorBuilder.append("File Not Found: ").append(xmlStr).append("\n");
        is = null;
      }
    }

    return is;
  }
}
