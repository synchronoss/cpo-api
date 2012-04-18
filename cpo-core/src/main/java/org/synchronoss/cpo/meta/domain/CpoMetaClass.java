/**
 *  Copyright (C) 2006-2012  David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.meta.domain;

import org.synchronoss.cpo.*;

public class CpoMetaClass<T> extends CpoClass implements MetaDFVisitable {

  private Class<T> metaClass = null;

  private CpoMetaClass() {
  }

  public CpoMetaClass(Class<T> metaClass) {
    super();
    this.setName(metaClass.getClass().getName());
    this.metaClass = metaClass;
  }

  public Class<T> getMetaClass() {
    return metaClass;
  }

}
