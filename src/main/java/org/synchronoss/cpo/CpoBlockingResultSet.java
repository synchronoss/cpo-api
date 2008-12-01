package org.synchronoss.cpo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.log4j.Logger;

public class CpoBlockingResultSet<E> implements CpoResultSet<E>, Iterator<E> {
  private static Logger logger = Logger.getLogger(CpoBlockingResultSet.class.getName());
  private static final long serialVersionUID = 1L;
  private int capacity=0;
  private AtomicInteger aInt = new AtomicInteger(0);
  private ThreadLocal<E> tlObj = new ThreadLocal<E>();
  ArrayBlockingQueue<E> abe = null;
  HashMap<Thread, Thread> producers = new HashMap<Thread, Thread>();
  HashMap<Thread, Thread> consumers = new HashMap<Thread, Thread>();
  boolean done = false;
  
  public CpoBlockingResultSet(int capacity) {
    this.capacity = capacity;
    abe = new ArrayBlockingQueue<E>(capacity);
  }

  public CpoBlockingResultSet(int capacity, boolean fair,
      Collection<? extends E> c) {
    this.capacity = capacity;
    abe = new ArrayBlockingQueue<E>(capacity, fair, c);
    // TODO Auto-generated constructor stub
  }

  public CpoBlockingResultSet(int capacity, boolean fair) {
    this.capacity = capacity;
    abe = new ArrayBlockingQueue<E>(capacity, fair);
    // TODO Auto-generated constructor stub
  }
  
  public void put(E e) throws InterruptedException{
    producers.put(Thread.currentThread(), Thread.currentThread());
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
    consumers.put(Thread.currentThread(), Thread.currentThread());
    logger.debug("Take Called");
    return abe.take();
  }

  public boolean isDone() {
    return done;
  }

  public void setDone(boolean done) {
    this.done = done;
  }
  
  public void cancel(){
    setDone(true);
    for(Thread t : consumers.values()){
      if (t != Thread.currentThread()){
        t.interrupt();
      }
    }
    for(Thread t : producers.values()){
      if (t != Thread.currentThread()){
        t.interrupt();
      }
    }
  }
  
  public int getFetchSize(){
    return capacity;
  }

}
