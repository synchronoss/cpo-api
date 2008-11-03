package org.synchronoss.cpo;

import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class CpoBlockingResultSet<E> implements CpoResultSet<E>, Iterator<E> {
  private static Logger logger = Logger.getLogger(CpoBlockingResultSet.class.getName());
  private static final long serialVersionUID = 1L;
  private AtomicInteger aInt = new AtomicInteger(0);
  private ThreadLocal<E> tlObj = new ThreadLocal<E>();
  ArrayBlockingQueue<E> abe = null;
  boolean done = false;
  
  public CpoBlockingResultSet(int capacity) {
    abe = new ArrayBlockingQueue<E>(capacity);
  }

  public CpoBlockingResultSet(int capacity, boolean fair,
      Collection<? extends E> c) {
    abe = new ArrayBlockingQueue<E>(capacity, fair, c);
    // TODO Auto-generated constructor stub
  }

  public CpoBlockingResultSet(int capacity, boolean fair) {
    abe = new ArrayBlockingQueue<E>(capacity, fair);
    // TODO Auto-generated constructor stub
  }
  
  public void put(E e) throws InterruptedException{
    logger.debug("Put Called");
    abe.put(e);
    aInt.incrementAndGet();
  }
  
  public boolean hasNext(){
    logger.debug("hasNext Called");
    E ret=tlObj.get();
    
    if (isDone() && abe.size()==0 && ret==null)
      return false;

    if (ret==null){
      try{
        tlObj.set(take());
      } catch (InterruptedException ie){
        if (isDone() && abe.size()==0)
          return false;
        else {
          try {
            tlObj.set(take());
          }catch (InterruptedException ie2){
            return false;
          }
        }
      }
    }
    return true;
    
  }
  
  public int size(){
    return aInt.get();
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  public E next() throws NoSuchElementException{
    logger.debug("next Called");
    E ret=tlObj.get();
    
    if (ret==null){
      try{
        ret=take();
      } catch (InterruptedException ie){
        // maintain the interrupt
        if (isDone()&&abe.size()==0)
          throw new NoSuchElementException();
        else {
          try {
            ret = take();
          }catch (InterruptedException ie2){
            throw new NoSuchElementException();
          }
        }
      }
    } else {
      tlObj.set(null);
    }
    return ret;
  }

  public Iterator<E> iterator() {
    return this;
  }

  public E take() throws InterruptedException {
    // TODO Auto-generated method stub
    logger.debug("Take Called");
    return abe.take();
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }
}
