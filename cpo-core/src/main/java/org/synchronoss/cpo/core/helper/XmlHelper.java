package org.synchronoss.cpo.core.helper;

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

import jakarta.xml.bind.*;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

/**
 * @author dberry
 */
public class XmlHelper {
  public static final String CPO_META_XSD = "xsd/CpoMeta.xsd";
  public static final String CPO_CONFIG_XSD = "xsd/CpoConfig.xsd";

  public static InputStream loadXmlStream(String xmlStr, StringBuilder errorBuilder) {
    InputStream is = null;
    int errBuilderLen = errorBuilder.length();

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
    if (is != null) errorBuilder.setLength(errBuilderLen);

    return is;
  }

  public static void setMarshallerProperties(Marshaller marshaller) throws PropertyException {
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
  }

  public static <T> T unmarshalXmlObject(
      String xsd, String xml, Class<T> objClass, StringBuilder errorBuilder) {
    try (var xsdStream = loadXmlStream(xsd, errorBuilder);
        var xmlStream = loadXmlStream(xml, errorBuilder)) {
      StreamSource configSource = new StreamSource(xsdStream);
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(configSource);

      JAXBContext jaxbContext = JAXBContext.newInstance(objClass);
      Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
      unmarshaller.setSchema(schema);
      unmarshaller.setEventHandler(
          (ValidationEventHandler)
              event -> {
                ValidationEventLocator locator = event.getLocator();
                errorBuilder.append("Validation Error: " + event.getMessage() + "\n");
                errorBuilder.append(
                    "Line: "
                        + locator.getLineNumber()
                        + ", Column: "
                        + locator.getColumnNumber()
                        + "\n");
                return true; // Return true to continue processing, false to stop
              });

      Object xmlObject = unmarshaller.unmarshal(xmlStream);
      @SuppressWarnings("unchecked")
      T obj =
          (xmlObject instanceof JAXBElement)
              ? (T) ((JAXBElement<?>) xmlObject).getValue()
              : (T) xmlObject;

      return obj;
    } catch (Exception e) {
      errorBuilder.append("Could not reading config xml " + xml + "or xsd " + xsd + "\n");
    }
    return null;
  }
}
