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
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.CpoTransform;
import org.synchronoss.cpo.transform.jdbc.JdbcCpoTransform;

import java.io.*;

/**
 *
 * @author dberry
 */
public class PreparedStatementCpoData extends AbstractJdbcCpoData {

  private static final Logger logger = LoggerFactory.getLogger(PreparedStatementCpoData.class);
  private JdbcPreparedStatementFactory jpsf = null;
  private int index = -1;
  
  public PreparedStatementCpoData(JdbcPreparedStatementFactory jpsf, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.jpsf = jpsf;
  }

  @Override
  public Object invokeGetter() throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    Logger localLogger = instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass());
    CpoAttribute cpoAttribute = getCpoAttribute();
    Object param = transformOut(cpoAttribute.invokeGetter(instanceObject));
    JavaSqlMethod<?> javaSqlMethod = JavaSqlMethods.getJavaSqlMethod(getDataSetterParamType());
    if (javaSqlMethod == null) {
      throw new CpoException("Error Retrieveing Jdbc Method for type: " + getDataSetterParamType().getName());
    }
    localLogger.info(cpoAttribute.getDataName() + "=" + param);
    try {
      switch (javaSqlMethod.getMethodType()) {
        case JavaSqlMethod.METHOD_TYPE_BASIC:
          javaSqlMethod.getPsSetter().invoke(jpsf.getPreparedStatement(), new Object[]{new Integer(getIndex()), param});
          break;
        case JavaSqlMethod.METHOD_TYPE_STREAM:
          CpoByteArrayInputStream cbais = CpoByteArrayInputStream.getCpoStream((InputStream) param);
          // Get the length of the InputStream in param
          javaSqlMethod.getPsSetter().invoke(jpsf.getPreparedStatement(), new Object[]{new Integer(getIndex()), (InputStream) cbais, new Integer(cbais.getLength())});
          break;
        case JavaSqlMethod.METHOD_TYPE_READER:
          CpoCharArrayReader ccar = CpoCharArrayReader.getCpoReader((Reader) param);
          // Get the length of the Reader in param
          javaSqlMethod.getPsSetter().invoke(jpsf.getPreparedStatement(), new Object[]{new Integer(getIndex()), (Reader) ccar, new Integer(ccar.getLength())});
          break;
      }
    } catch (Exception e) {
      throw new CpoException("Error Invoking Jdbc Method: " + javaSqlMethod.getPsSetter().getName() + ":" + ExceptionHelper.getLocalizedMessage(e));
    }
  }
  
  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;
    CpoTransform cpoTransform = getCpoAttribute().getCpoTransform();
    
    if (cpoTransform != null) {
      if (cpoTransform instanceof JdbcCpoTransform) {
        retObj = ((JdbcCpoTransform)cpoTransform).transformOut(jpsf, attributeObject);
      } else {
        retObj = cpoTransform.transformOut(attributeObject);
      }
    }
    return retObj;
  }

}
