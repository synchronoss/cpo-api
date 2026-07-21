package org.synchronoss.cpo.core.meta.domain;

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

/**
 * {@link CpoClass} variant whose datastore-name lookups are case-insensitive: attribute data names
 * are upper-cased before being used as map keys.
 *
 * @author dberry
 */
public class CpoClassCaseInsensitive extends CpoClass {

  /** Version Id for this class. */
  private static final long serialVersionUID = 1L;

  /** Creates an empty instance. */
  public CpoClassCaseInsensitive() {}

  /**
   * {@inheritDoc}
   *
   * <p>Matches {@code dataName} case-insensitively.
   */
  @Override
  public CpoAttribute getAttributeData(String dataName) {
    if (dataName == null) {
      return null;
    }
    return getDataMap().get(dataName.toUpperCase());
  }

  /** {@inheritDoc} */
  @Override
  public void addDataNameToMap(String dataName, CpoAttribute cpoAttribute) {
    getDataMap().put(dataName.toUpperCase(), cpoAttribute);
  }

  /** {@inheritDoc} */
  @Override
  public void removeDataNameFromMap(String dataName) {
    getDataMap().remove(dataName.toUpperCase());
  }
}
