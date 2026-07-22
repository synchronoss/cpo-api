package org.synchronoss.cpo.core.meta;

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

import org.synchronoss.cpo.core.AbstractCpoData;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;

/**
 * Base {@link org.synchronoss.cpo.core.CpoData} implementation for datastore bindings that are
 * addressed by a positional index (e.g. a JDBC parameter or column index) in addition to the {@link
 * CpoAttribute} they move data for.
 *
 * @author dberry
 */
public abstract class AbstractBindableCpoData extends AbstractCpoData {

  private int index = -1;

  /**
   * Creates an instance bound to the given attribute and positional index.
   *
   * @param cpoAttribute the attribute this instance moves data for
   * @param index the positional index (e.g. parameter or column index) of this binding
   */
  public AbstractBindableCpoData(CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute);
    this.index = index;
  }

  /**
   * Gets the positional index of this binding.
   *
   * @return the positional index (e.g. parameter or column index) of this binding
   */
  public int getIndex() {
    return index;
  }
}
