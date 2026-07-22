package org.synchronoss.cpo.core;

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

import org.synchronoss.cpo.core.meta.domain.CpoAttribute;

/**
 * Base {@link CpoData} implementation that owns the {@link CpoAttribute} being bound and handles
 * the transform half of the contract ({@link #transformIn(Object)}/{@link #transformOut(Object)}).
 * Datastore-specific subclasses supply the actual reflective getter/setter invocation.
 *
 * @author dberry
 */
public abstract class AbstractCpoData implements CpoData {

  private CpoAttribute cpoAttribute = null;

  /**
   * Creates an instance bound to the given attribute.
   *
   * @param cpoAttribute the attribute this instance moves data for
   */
  public AbstractCpoData(CpoAttribute cpoAttribute) {
    this.cpoAttribute = cpoAttribute;
  }

  /**
   * Gets the attribute this instance moves data for.
   *
   * @return the bound attribute
   */
  public CpoAttribute getCpoAttribute() {
    return cpoAttribute;
  }

  /**
   * Sets the attribute this instance moves data for.
   *
   * @param cpoAttribute the attribute to bind
   */
  public void setCpoAttribute(CpoAttribute cpoAttribute) {
    this.cpoAttribute = cpoAttribute;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Applies the attribute's configured {@code CpoTransform}, if any; otherwise returns the value
   * unchanged.
   */
  @Override
  public Object transformIn(Object datasourceObject) throws CpoException {
    Object retObj = datasourceObject;

    if (cpoAttribute.getCpoTransform() != null) {
      retObj = cpoAttribute.getCpoTransform().transformIn(datasourceObject);
    }
    return retObj;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Applies the attribute's configured {@code CpoTransform}, if any; otherwise returns the value
   * unchanged.
   */
  @Override
  public Object transformOut(Object attributeObject) throws CpoException {
    Object retObj = attributeObject;

    if (cpoAttribute.getCpoTransform() != null) {
      retObj = cpoAttribute.getCpoTransform().transformOut(attributeObject);
    }
    return retObj;
  }

  /**
   * {@inheritDoc}
   *
   * <p>Not implemented by this base class; subclasses that support reflective getter invocation
   * must override this method.
   *
   * @throws UnsupportedOperationException always, unless overridden by a subclass
   */
  @Override
  public Object invokeGetter() throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * {@inheritDoc}
   *
   * <p>Not implemented by this base class; subclasses that support reflective setter invocation
   * must override this method.
   *
   * @throws UnsupportedOperationException always, unless overridden by a subclass
   */
  @Override
  public void invokeSetter(Object instanceObject) throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  /**
   * Gets the type that the datastore-side value should be coerced to before being handed to the
   * bean's setter: the setter's own parameter type, or the transform's input type if the attribute
   * has a configured {@code CpoTransform}.
   *
   * @return the expected type of the raw datastore value
   */
  public Class<?> getDataGetterReturnType() {
    Class<?> returnClass = cpoAttribute.getSetterParamType();
    if (cpoAttribute.getCpoTransform() != null) {
      returnClass = cpoAttribute.getTransformInParamType();
    }
    return returnClass;
  }

  /**
   * Gets the type that the bean attribute's value is coerced to before being bound to the
   * datastore: the getter's own return type, or the transform's output type if the attribute has a
   * configured {@code CpoTransform}.
   *
   * @return the expected type of the value to bind to the datastore
   */
  public Class<?> getDataSetterParamType() {
    Class<?> returnClass = cpoAttribute.getGetterReturnType();
    if (cpoAttribute.getCpoTransform() != null) {
      returnClass = cpoAttribute.getTransformOutMethod().getReturnType();
    }
    return returnClass;
  }
}
