package org.synchronoss.cpo.meta;

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

import org.synchronoss.cpo.AbstractCpoData;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * @author dberry
 */
public abstract class AbstractBindableCpoData extends AbstractCpoData {

  private int index = -1;

  public AbstractBindableCpoData(CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute);
    this.index = index;
  }

  public int getIndex() {
    return index;
  }
}
