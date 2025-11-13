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

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpoBlockingResultSet<E> implements CpoResultSet<E>, Iterator<E> {

  private static final Logger logger = LoggerFactory.getLogger(CpoBlockingResultSet.class);
  private static final long serialVersionUID = 1L;
  private int capacity = 0;
  private final ThreadLocal<E> tlObj = new ThreadLocal<>();
  private LinkedBlockingQueue<E> lbq = null;
  private final Set<Thread> producers = new HashSet<>();
  private final Set<Thread> consumers = new HashSet<>();

  private CpoBlockingResultSet() {}

  public CpoBlockingResultSet(int capacity) {
    this.capacity = capacity;
    lbq = new LinkedBlockingQueue<>(capacity);
  }

  @Override
  public void put(E e) throws InterruptedException {
    producers.add(Thread.currentThread());
    logger.debug("Put Called");
    lbq.put(e);
  }

  @Override
  public boolean hasNext() {
    logger.debug("hasNext Called");

    if (tlObj.get() != null || lbq.size() > 0) {
      return true;
    }

    if (lbq.size() == 0 && Thread.interrupted()) {
      return false;
    }

    try {
      tlObj.set(lbq.take());
    } catch (InterruptedException ie) {
      logger.error("CpoBlockingResultSet.hasNext() - Interrupted and bailing out");
      return false;
    }
    return true;
  }

  @Override
  public int size() {
    return lbq.size();
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public E next() throws NoSuchElementException {
    logger.debug("next Called");
    E ret = tlObj.get();

    if (ret == null) {
      if (lbq.size() == 0 && Thread.interrupted()) {
        throw new NoSuchElementException();
      }

      try {
        ret = take();
      } catch (InterruptedException ie) {
        logger.error("CpoBlockingResultSet.next() - Interrupted and bailing out");
        throw new NoSuchElementException();
      }
    } else {
      tlObj.set(null);
    }

    return ret;
  }

  @Override
  public Iterator<E> iterator() {
    return this;
  }

  @Override
  public E take() throws InterruptedException {
    consumers.add(Thread.currentThread());
    logger.debug("Take Called");
    return lbq.take();
  }

  @Override
  public void cancel() {
    for (Thread t : consumers) {
      t.interrupt();
    }
    for (Thread t : producers) {
      t.interrupt();
    }
  }

  @Override
  public int getFetchSize() {
    return capacity;
  }
}
