package org.synchronoss.cpo;

import java.util.ArrayList;
import java.util.Collection;

public class CpoArrayResultSet<E> extends ArrayList<E> implements CpoResultSet<E> {
  boolean canceled=false;
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

  public void put(E e) throws InterruptedException{
    if (canceled)
      throw new InterruptedException();
    add(e);
  }
  
  public E take() throws InterruptedException{
    if (canceled)
      throw new InterruptedException();
    if (size()>0)
      return remove(0);
    else 
      return null;
  }
  
  public void cancel(){
    canceled=true;
  }
  
  public int getFetchSize(){
    return 10;
  }
}
