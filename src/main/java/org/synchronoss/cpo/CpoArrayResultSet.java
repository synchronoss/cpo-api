package org.synchronoss.cpo;

import java.util.ArrayList;
import java.util.Collection;

public class CpoArrayResultSet<E> extends ArrayList<E> implements CpoResultSet<E> {
  public CpoArrayResultSet() {
    super();
    // TODO Auto-generated constructor stub
  }

  public CpoArrayResultSet(Collection<? extends E> c) {
    super(c);
    // TODO Auto-generated constructor stub
  }

  public CpoArrayResultSet(int initialCapacity) {
    super(initialCapacity);
    // TODO Auto-generated constructor stub
  }

  private static final long serialVersionUID = 1L;

  public void put(E e) throws InterruptedException{
    add(e);
  }
  
  public E take() throws InterruptedException{
    if (size()>0)
      return remove(0);
    else 
      return null;
  }
}
