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

import org.slf4j.*;
import org.synchronoss.cpo.*;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapEntry;
import org.synchronoss.cpo.jdbc.meta.JdbcMethodMapper;
import org.synchronoss.cpo.meta.AbstractBindableCpoData;
import org.synchronoss.cpo.meta.MethodMapEntry;
import org.synchronoss.cpo.meta.MethodMapper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.CpoTransform;
import org.synchronoss.cpo.transform.jdbc.JdbcCpoTransform;

import java.io.*;

/**
 *
 * @author dberry
 */
public class JdbcPreparedStatementCpoData extends AbstractBindableCpoData {

  private static final Logger logger = LoggerFactory.getLogger(JdbcPreparedStatementCpoData.class);
  private JdbcPreparedStatementFactory cpoStatementFactory = null;

  public JdbcPreparedStatementCpoData(JdbcPreparedStatementFactory cpoStatementFactory, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.cpoStatementFactory = cpoStatementFactory;
  }

  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    Logger localLogger = instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());
    CpoAttribute cpoAttribute = getCpoAttribute();
    Object param = transformOut(cpoAttribute.invokeGetter(instanceObject));
    JdbcMethodMapEntry<?,?> methodMapEntry = JdbcMethodMapper.getJavaSqlMethod(getDataSetterParamType());
    if (methodMapEntry == null) {
      throw new CpoException("Error Retrieveing Jdbc Method for type: " + getDataSetterParamType().getName());
    }
    localLogger.info(cpoAttribute.getDataName() + "=" + param);
    try {
      switch (methodMapEntry.getMethodType()) {
        case JdbcMethodMapEntry.METHOD_TYPE_BASIC:
          methodMapEntry.getBsSetter().invoke(cpoStatementFactory.getPreparedStatement(), getIndex(), param);
          break;
        case JdbcMethodMapEntry.METHOD_TYPE_STREAM:
          CpoByteArrayInputStream cbais = CpoByteArrayInputStream.getCpoStream((InputStream) param);
          // Get the length of the InputStream in param
          methodMapEntry.getBsSetter().invoke(cpoStatementFactory.getPreparedStatement(), getIndex(), cbais, cbais.getLength());
          break;
        case JdbcMethodMapEntry.METHOD_TYPE_READER:
          CpoCharArrayReader ccar = CpoCharArrayReader.getCpoReader((Reader) param);
          // Get the length of the Reader in param
          methodMapEntry.getBsSetter().invoke(cpoStatementFactory.getPreparedStatement(), getIndex(), ccar, ccar.getLength());
          break;
      }
    } catch (Exception e) {
      throw new CpoException("Error Invoking Jdbc Method: " + methodMapEntry.getBsSetter().getName() + ":" + ExceptionHelper.getLocalizedMessage(e));
    }
  }

  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;
    CpoTransform cpoTransform = getCpoAttribute().getCpoTransform();

    if (cpoTransform != null) {
      if (cpoTransform instanceof JdbcCpoTransform) {
        retObj = ((JdbcCpoTransform)cpoTransform).transformOut(cpoStatementFactory, attributeObject);
      } else {
        retObj = cpoTransform.transformOut(attributeObject);
      }
    }
    return retObj;
  }

}
