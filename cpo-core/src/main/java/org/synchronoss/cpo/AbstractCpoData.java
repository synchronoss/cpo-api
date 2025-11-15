package org.synchronoss.cpo;

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

import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * @author dberry
 */
public abstract class AbstractCpoData implements CpoData {

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

  public Class<?> getDataGetterReturnType() {
    Class<?> returnClass = cpoAttribute.getSetterParamType();
    if (cpoAttribute.getCpoTransform() != null) {
      returnClass = cpoAttribute.getTransformInMethod().getParameterTypes()[0];
    }
    return returnClass;
  }

  public Class<?> getDataSetterParamType() {
    Class<?> returnClass = cpoAttribute.getGetterReturnType();
    if (cpoAttribute.getCpoTransform() != null) {
      returnClass = cpoAttribute.getTransformOutMethod().getReturnType();
    }
    return returnClass;
  }
}
