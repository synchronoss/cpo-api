package org.synchronoss.cpo.jdbc;

/*-
 * [-------------------------------------------------------------------------
 * jdbc
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

import java.io.InputStream;
import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoByteArrayInputStream;
import org.synchronoss.cpo.CpoCharArrayReader;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapEntry;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;
import org.synchronoss.cpo.meta.AbstractBindableCpoData;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.CpoTransform;
import org.synchronoss.cpo.transform.jdbc.JdbcCpoTransform;

/**
 * The data handler for a prepared statement
 *
 * @author dberry
 */
public class JdbcPreparedStatementCpoData extends AbstractBindableCpoData {

  private static final Logger logger = LoggerFactory.getLogger(JdbcPreparedStatementCpoData.class);
  private JdbcPreparedStatementFactory cpoStatementFactory = null;

  /**
   * Construct a JdbcPreparedStatementCpoData
   *
   * @param cpoStatementFactory - The JdbcPreparedStatementFactory
   * @param cpoAttribute - The CpoAttribute to manage
   * @param index - The index of the CpoAttribute
   */
  public JdbcPreparedStatementCpoData(
      JdbcPreparedStatementFactory cpoStatementFactory, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.cpoStatementFactory = cpoStatementFactory;
  }

  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    Logger localLogger =
        instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());
    CpoAttribute cpoAttribute = getCpoAttribute();
    Object param = transformOut(cpoAttribute.invokeGetter(instanceObject));
    JdbcMethodMapEntry<?, ?> methodMapEntry =
        JdbcMethodMapper.getJavaSqlMethod(getDataSetterParamType());
    if (methodMapEntry == null) {
      if (Object.class.isAssignableFrom(getDataSetterParamType())) {
        methodMapEntry = JdbcMethodMapper.getJavaSqlMethod(Object.class);
      }
      if (methodMapEntry == null) {
        throw new CpoException(
            "Error Retrieving Jdbc Method for type: " + getDataSetterParamType().getName());
      }
    }
    localLogger.debug(cpoAttribute.getDataName() + "=" + param);
    try {
      switch (methodMapEntry.getMethodType()) {
        case JdbcMethodMapEntry.METHOD_TYPE_BASIC:
        case JdbcMethodMapEntry.METHOD_TYPE_OBJECT:
        default:
          methodMapEntry
              .getBsSetter()
              .invoke(cpoStatementFactory.getPreparedStatement(), getIndex(), param);
          break;
        case JdbcMethodMapEntry.METHOD_TYPE_STREAM:
          CpoByteArrayInputStream cbais = CpoByteArrayInputStream.getCpoStream((InputStream) param);
          // Get the length of the InputStream in param
          methodMapEntry
              .getBsSetter()
              .invoke(
                  cpoStatementFactory.getPreparedStatement(), getIndex(), cbais, cbais.getLength());
          break;
        case JdbcMethodMapEntry.METHOD_TYPE_READER:
          CpoCharArrayReader ccar = CpoCharArrayReader.getCpoReader((Reader) param);
          // Get the length of the Reader in param
          methodMapEntry
              .getBsSetter()
              .invoke(
                  cpoStatementFactory.getPreparedStatement(), getIndex(), ccar, ccar.getLength());
          break;
      }
    } catch (Exception e) {
      throw new CpoException(
          "Error Invoking Jdbc Method: "
              + cpoAttribute.getDataName()
              + ":"
              + cpoAttribute.getJavaName()
              + ":"
              + methodMapEntry.getBsSetter().getName()
              + ":"
              + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;
    CpoTransform cpoTransform = getCpoAttribute().getCpoTransform();

    if (cpoTransform != null) {
      if (cpoTransform instanceof JdbcCpoTransform) {
        retObj =
            ((JdbcCpoTransform) cpoTransform).transformOut(cpoStatementFactory, attributeObject);
      } else {
        retObj = cpoTransform.transformOut(attributeObject);
      }
    }
    return retObj;
  }
}
