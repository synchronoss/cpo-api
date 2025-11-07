/*
 * Copyright (C) 2003-2025 David E. Berry
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

import java.util.ArrayList;
import java.util.Collection;

public class CpoArrayResultSet<E> extends ArrayList<E> implements CpoResultSet<E> {

  boolean canceled = false;

  public CpoArrayResultSet() {
    super();
  }

  public CpoArrayResultSet(Collection<? extends E> c) {
    super(c);
  }

  public CpoArrayResultSet(int initialCapacity) {
    super(initialCapacity);
  }
  private static final long serialVersionUID = 1L;

  @Override
  public void put(E e) throws InterruptedException {
    if (canceled) {
      throw new InterruptedException();
    }
    add(e);
  }

  @Override
  public E take() throws InterruptedException {
    if (canceled) {
      throw new InterruptedException();
    }
    if (size() > 0) {
      return remove(0);
    } else {
      return null;
    }
  }

  @Override
  public void cancel() {
    canceled = true;
  }

  @Override
  public int getFetchSize() {
    return -1;
  }
}
