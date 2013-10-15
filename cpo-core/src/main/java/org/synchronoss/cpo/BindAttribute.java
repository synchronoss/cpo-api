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

import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 * @author david.berry
 *
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
