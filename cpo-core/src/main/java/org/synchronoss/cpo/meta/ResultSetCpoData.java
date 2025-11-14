package org.synchronoss.cpo.meta;

/*-
 * #%L
 * core
 * %%
 * Copyright (C) 2003 - 2025 David E. Berry
 * %%
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
 * #L%
 */

import java.lang.reflect.InvocationTargetException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.helper.ExceptionHelper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * @author dberry
 */
public abstract class ResultSetCpoData extends AbstractBindableCpoData {

  private static final Logger logger = LoggerFactory.getLogger(ResultSetCpoData.class);
  private Object rs = null;
  MethodMapper<?> methodMapper;

  public ResultSetCpoData(
      MethodMapper<?> methodMapper, Object rs, CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute, index);
    this.methodMapper = methodMapper;
    this.rs = rs;
  }

  public Object getRs() {
    return rs;
  }

  protected abstract Object invokeGetterImpl(
      CpoAttribute cpoAttribute, MethodMapEntry<?, ?> methodMapEntry)
      throws IllegalAccessException, InvocationTargetException;

  @Override
  public Object invokeGetter() throws CpoException {
    Object javaObject;
    MethodMapEntry<?, ?> methodMapEntry =
        methodMapper.getDataMethodMapEntry(getDataGetterReturnType());
    if (methodMapEntry == null) {
      if (Object.class.isAssignableFrom(getDataGetterReturnType())) {
        methodMapEntry = methodMapper.getDataMethodMapEntry(Object.class);
      }
      if (methodMapEntry == null) {
        throw new CpoException(
            "Error Retrieving Jdbc Method for type: " + getDataGetterReturnType().getName());
      }
    }

    try {
      // Get the getter for the Callable Statement
      javaObject = transformIn(invokeGetterImpl(getCpoAttribute(), methodMapEntry));
    } catch (IllegalAccessException iae) {
      logger.debug("Error Invoking ResultSet Method: " + ExceptionHelper.getLocalizedMessage(iae));
      throw new CpoException(iae);
    } catch (InvocationTargetException ite) {
      logger.debug("Error Invoking ResultSet Method: " + ExceptionHelper.getLocalizedMessage(ite));
      throw new CpoException(ite.getCause());
    }

    return javaObject;
  }
}
