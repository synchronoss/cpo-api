/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import java.io.InputStream;
import java.io.Reader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoByteArrayInputStream;
import org.synchronoss.cpo.CpoCharArrayReader;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.CpoTransform;
import org.synchronoss.cpo.transform.jdbc.JdbcCpoTransform;

/**
 *
 * @author dberry
 */
public class PreparedStatementCpoData extends AbstractJdbcCpoData {

  private static Logger logger = LoggerFactory.getLogger(PreparedStatementCpoData.class.getSimpleName());
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
    Logger localLogger = instanceObject == null ? logger : LoggerFactory.getLogger(instanceObject.getClass().getSimpleName()+":"+logger.getName());
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
