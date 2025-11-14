package org.synchronoss.cpo.helper;

/*-
 * #%L
 * core
 * %%
 * Copyright (C) 2003 - 2025 David E. Berry
 * %%
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
 * #L%
 */

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
}
