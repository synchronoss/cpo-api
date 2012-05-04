/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 *
 * @author dberry
 */
public abstract class AbstractCpoData implements CpoData{
  
  private static Logger logger = LoggerFactory.getLogger(AbstractCpoData.class.getSimpleName());
  private CpoAttribute cpoAttribute = null;
  
  public AbstractCpoData(CpoAttribute cpoAttribute) {
    this.cpoAttribute = cpoAttribute;
  }

  public CpoAttribute getCpoAttribute() {
    return cpoAttribute;
  }

  public void setCpoAttribute(CpoAttribute cpoAttribute) {
    this.cpoAttribute = cpoAttribute;
  }

  @Override
  public Object transformIn(Object datasourceObject) throws CpoException {
    Object retObj = datasourceObject;

    if (cpoAttribute.getCpoTransform() != null) {
      retObj = cpoAttribute.getCpoTransform().transformIn(datasourceObject);
    }
    return retObj;
  }

  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;

    if (cpoAttribute.getCpoTransform() != null) {
      retObj = cpoAttribute.getCpoTransform().transformOut(attributeObject);
    }
    return retObj;
  }

  public Class getDataGetterReturnType() {
    Class returnClass = cpoAttribute.getSetterParamType();
    if (cpoAttribute.getCpoTransform()!=null){
      returnClass=cpoAttribute.getTransformInMethod().getParameterTypes()[0];
    }
    return returnClass;
  }
  
  public Class getDataSetterParamType() {
    Class returnClass = cpoAttribute.getGetterReturnType();
    if (cpoAttribute.getCpoTransform()!=null){
      returnClass=cpoAttribute.getTransformOutMethod().getReturnType();
    }
    return returnClass;
  }

}
