package org.synchronoss.cpo;

/*-
 * [-------------------------------------------------------------------------
 * core
 * --------------------------------------------------------------------------
 * Copyright (C) 2003 - 2025 David E. Berry
 * --------------------------------------------------------------------------
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
 * --------------------------------------------------------------------------]
 */

import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * @author david.berry
 */
public class BindAttribute {

  private CpoAttribute cpoAttribute = null;
  private Object bindObject = null;
  private String name = null;

  public BindAttribute(CpoAttribute cpoAttribute, Object bindObject) {
    this.cpoAttribute = cpoAttribute;
    this.bindObject = bindObject;
  }

  public BindAttribute(String name, Object bindObject) {
    this.name = name;
    this.bindObject = bindObject;
  }

  public CpoAttribute getCpoAttribute() {
    return cpoAttribute;
  }

  public Object getBindObject() {
    return bindObject;
  }

  public String getName() {
    return name;
  }
}
