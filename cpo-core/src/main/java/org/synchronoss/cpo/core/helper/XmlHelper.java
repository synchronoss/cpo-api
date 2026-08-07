package org.synchronoss.cpo.core.helper;

/*-
 * [[
 * core
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
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
 * Static helpers for locating and safely unmarshalling CPO's XML configuration and meta data files.
 *
 * <p>{@link #loadXmlStream} resolves an XML source by classpath resource, {@code file:} URL, or
 * plain filesystem path — never over the network, so a hostile {@code CPO_CONFIG} value cannot pull
 * configuration from a remote host. {@link #unmarshalXmlObject} then parses and schema-validates
 * that XML through a hardened SAX/JAXB pipeline with DOCTYPE declarations and external entities
 * disabled, to prevent XXE and entity-expansion attacks from untrusted config or meta XML.
 *
 * @author dberry
 */
public class XmlHelper {
  private static final Logger logger = LoggerFactory.getLogger(XmlHelper.class);

  private XmlHelper() {
    // hidden constructor: this class only exposes static helpers
  }

  /** Classpath-relative location of the CPO meta data XML schema. */
  public static final String CPO_META_XSD = "xsd/CpoMeta.xsd";

  /** Classpath-relative location of the CPO config XML schema. */
  public static final String CPO_CONFIG_XSD = "xsd/CpoConfig.xsd";

  /**
   * Resolves {@code xmlStr} to an open {@link InputStream}, trying (in order) a {@code file:} URL,
   * a classpath resource, and finally a plain filesystem path. Remote URLs are deliberately not
   * supported.
   *
   * @param xmlStr the classpath resource name, {@code file:} URL, or filesystem path to load
   * @param errorBuilder buffer that error details are appended to when the source cannot be
   *     resolved; left unchanged on success
   * @return an open stream for {@code xmlStr}, or {@code null} if it could not be resolved by any
   *     of the supported means
   */
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

  /**
   * Resolves {@code xmlStr} to a system ID (base URI) usable by a {@link StreamSource}, so that an
   * XSD loaded via {@link #loadXmlStream} can resolve its own relative {@code <xs:import
   * schemaLocation="...">} references (e.g. {@code CpoUtilConfig.xsd} importing {@code
   * CpoConfig.xsd} from the same classpath location). Follows the same resolution order as {@link
   * #loadXmlStream}.
   *
   * @param xmlStr the classpath resource name, {@code file:} URL, or filesystem path to resolve
   * @return a system ID for {@code xmlStr}; never {@code null}
   */
  private static String resolveSystemId(String xmlStr) {
    if (xmlStr.regionMatches(true, 0, "file:", 0, 5)) {
      return xmlStr;
    }
    URL url = CpoClassLoader.getResource(xmlStr);
    if (url != null) {
      return url.toString();
    }
    return new File(xmlStr).toURI().toString();
  }

  /**
   * Configures a JAXB {@link Marshaller} with CPO's standard output settings: UTF-8 encoding and
   * formatted (indented) output.
   *
   * @param marshaller the marshaller to configure
   * @throws PropertyException if the marshaller does not support one of the properties being set
   */
  public static void setMarshallerProperties(Marshaller marshaller) throws PropertyException {
    marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
  }

  /**
   * Loads, schema-validates, and unmarshals an XML document into an instance of {@code objClass},
   * using a hardened SAX parser (DOCTYPE and external entities disabled) to guard against XXE and
   * entity-expansion attacks from untrusted input.
   *
   * @param <T> the JAXB-bound type to unmarshal into
   * @param xsd the classpath resource, {@code file:} URL, or path of the XSD to validate against
   * @param xml the classpath resource, {@code file:} URL, or path of the XML document to parse
   * @param objClass the JAXB-bound class to unmarshal the document into
   * @param errorBuilder buffer that validation and I/O error details are appended to
   * @return the unmarshalled object, or {@code null} if the XSD/XML could not be loaded or parsed
   */
  public static <T> T unmarshalXmlObject(
      String xsd, String xml, Class<T> objClass, StringBuilder errorBuilder) {
    try (var xsdStream = loadXmlStream(xsd, errorBuilder);
        var xmlStream = loadXmlStream(xml, errorBuilder)) {
      StreamSource configSource = new StreamSource(xsdStream);
      configSource.setSystemId(resolveSystemId(xsd));
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      trySetSchemaFeature(schemaFactory, XMLConstants.FEATURE_SECURE_PROCESSING, true);
      trySetSchemaProperty(schemaFactory, XMLConstants.ACCESS_EXTERNAL_DTD, "");
      // Schemas ship inside cpo-core's own jar/classpath and may <xs:import> a sibling schema
      // (e.g. CpoUtilConfig.xsd importing CpoConfig.xsd) - allow the schema compiler to resolve
      // those local, trusted imports. This is separate from - and does not relax - the XXE
      // hardening below on the untrusted XML *document* being parsed.
      trySetSchemaProperty(schemaFactory, XMLConstants.ACCESS_EXTERNAL_SCHEMA, "file,jar");
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
          unmarshaller.unmarshal(new SAXSource(xmlReader, new InputSource(xmlStream)), objClass);
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
