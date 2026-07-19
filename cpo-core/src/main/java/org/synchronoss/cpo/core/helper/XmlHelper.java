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
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Paths;
import javax.xml.XMLConstants;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

/**
 * @author dberry
 */
public class XmlHelper {
  private static final Logger logger = LoggerFactory.getLogger(XmlHelper.class);
  public static final String CPO_META_XSD = "xsd/CpoMeta.xsd";
  public static final String CPO_CONFIG_XSD = "xsd/CpoConfig.xsd";

  public static InputStream loadXmlStream(String xmlStr, StringBuilder errorBuilder) {
    int errBuilderLen = errorBuilder.length();
    InputStream is = null;

    // Only classpath resources and local files (plain paths or file: urls) are
    // supported; remote urls are deliberately not fetched so a hostile CPO_CONFIG
    // value cannot pull config from the network.
    if (xmlStr.regionMatches(true, 0, "file:", 0, 5)) {
      try {
        is = new FileInputStream(Paths.get(URI.create(xmlStr)).toFile());
      } catch (Exception e) {
        errorBuilder.append("File Not Found: ").append(xmlStr).append("\n");
      }
    } else {
      is = CpoClassLoader.getResourceAsStream(xmlStr);

      if (is == null) {
        errorBuilder.append("Resource Not Found: ").append(xmlStr).append("\n");
        try {
          // See if the file is a local file on the server
          is = new FileInputStream(xmlStr);
        } catch (FileNotFoundException fnfe) {
          errorBuilder.append("File Not Found: ").append(xmlStr).append("\n");
        }
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
      trySetSchemaFeature(schemaFactory, XMLConstants.FEATURE_SECURE_PROCESSING, true);
      trySetSchemaProperty(schemaFactory, XMLConstants.ACCESS_EXTERNAL_DTD, "");
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

      // Parse through a hardened SAX reader: DOCTYPE and external entities are
      // rejected so untrusted config/meta XML cannot trigger XXE or entity expansion.
      // Each knob is applied best-effort because the resolved parser implementation
      // (JDK-internal vs standalone Xerces) recognizes different subsets of them.
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParserFactory.setNamespaceAware(true);
      trySetSaxFeature(saxParserFactory, XMLConstants.FEATURE_SECURE_PROCESSING, true);
      trySetSaxFeature(
          saxParserFactory, "http://apache.org/xml/features/disallow-doctype-decl", true);
      trySetSaxFeature(
          saxParserFactory, "http://xml.org/sax/features/external-general-entities", false);
      trySetSaxFeature(
          saxParserFactory, "http://xml.org/sax/features/external-parameter-entities", false);
      XMLReader xmlReader = saxParserFactory.newSAXParser().getXMLReader();

      Object xmlObject =
          unmarshaller.unmarshal(new SAXSource(xmlReader, new InputSource(xmlStream)));
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

  private static void trySetSaxFeature(SAXParserFactory factory, String feature, boolean value) {
    try {
      factory.setFeature(feature, value);
    } catch (Exception e) {
      logger.debug("XML parser does not support feature " + feature);
    }
  }

  private static void trySetSchemaFeature(SchemaFactory factory, String feature, boolean value) {
    try {
      factory.setFeature(feature, value);
    } catch (Exception e) {
      logger.debug("Schema factory does not support feature " + feature);
    }
  }

  private static void trySetSchemaProperty(SchemaFactory factory, String property, String value) {
    try {
      factory.setProperty(property, value);
    } catch (Exception e) {
      logger.debug("Schema factory does not support property " + property);
    }
  }
}
