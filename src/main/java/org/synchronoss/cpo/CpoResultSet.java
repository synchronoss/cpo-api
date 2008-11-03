package org.synchronoss.cpo;


public interface CpoResultSet<E> extends Iterable<E>{
  public void put(E e) throws InterruptedException;
  public E take() throws InterruptedException;
  public int size();
}
