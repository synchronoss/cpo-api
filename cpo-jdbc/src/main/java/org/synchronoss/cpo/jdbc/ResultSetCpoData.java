/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.transform.CpoTransform;

/**
 *
 * @author dberry
 */
public class ResultSetCpoData extends AbstractJdbcCpoData {

  private static Logger logger = LoggerFactory.getLogger(CallableStatementCpoData.class.getSimpleName());
  private ResultSet rs = null;
  private int index = -1;
  
  public ResultSetCpoData(ResultSet rs, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.rs = rs;
  }

  @Override
  public Object invokeGetter() throws CpoException {
    Object javaObject;
    JavaSqlMethod<?> javaSqlMethod = JavaSqlMethods.getJavaSqlMethod(getDataGetterReturnType());
    if (javaSqlMethod == null) {
      throw new CpoException("Error Retrieveing Jdbc Method for type: " + getDataGetterReturnType().getName());
    }
    
    try {
      // Get the getter for the Callable Statement
      javaObject = transformIn(javaSqlMethod.getRsGetter().invoke(rs, new Object[]{new Integer(getIndex())}));
    } catch (IllegalAccessException iae) {
      logger.debug("Error Invoking ResultSet Method: " + ExceptionHelper.getLocalizedMessage(iae));
      throw new CpoException(iae);
    } catch (InvocationTargetException ite) {
      logger.debug("Error Invoking ResultSet Method: " + ExceptionHelper.getLocalizedMessage(ite));
      throw new CpoException(ite.getCause());
    }

    return javaObject;
  }

  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
