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
