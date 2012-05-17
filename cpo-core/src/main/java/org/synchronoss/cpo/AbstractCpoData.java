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
package org.synchronoss.cpo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 *
 * @author dberry
 */
public abstract class AbstractCpoData implements CpoData{
  
  private static final Logger logger = LoggerFactory.getLogger(AbstractCpoData.class);
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

  @Override
  public Object invokeGetter() throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
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
