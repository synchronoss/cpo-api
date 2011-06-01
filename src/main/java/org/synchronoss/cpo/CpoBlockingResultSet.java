package org.synchronoss.cpo;

import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CpoBlockingResultSet<E> implements CpoResultSet<E>, Iterator<E> {
  private static Logger logger = LoggerFactory.getLogger(CpoBlockingResultSet.class.getName());
  private static final long serialVersionUID = 1L;
  private int capacity=0;
  private final ThreadLocal<E> tlObj = new ThreadLocal<E>();
  private LinkedBlockingQueue<E> lbq = null;
  private final Set<Thread> producers = new HashSet<Thread>();
  private final Set<Thread> consumers = new HashSet<Thread>();
  private boolean done = false;
  
  private CpoBlockingResultSet (){
  }
  
  public CpoBlockingResultSet(int capacity) {
    this.capacity = capacity;
    lbq = new LinkedBlockingQueue<E>(capacity);
  }
  
  public void put(E e) throws InterruptedException{
    producers.add(Thread.currentThread());
    logger.debug("Put Called");
    lbq.put(e);
  }
  
  public boolean hasNext(){
    logger.debug("hasNext Called");
    
    if (tlObj.get()!=null || lbq.size()>0)
      return true;
    
    if (lbq.size()==0 && Thread.currentThread().interrupted())
      return false;
    
    try{
      tlObj.set(lbq.take());
    } catch (InterruptedException ie){
      logger.error("CpoBlockingResultSet.hasNext() - Interrupted and bailing out");
      return false;
    }
    return true;
    
  }
  
  public int size(){
    return lbq.size();
  }
  
  public void remove() {
    throw new UnsupportedOperationException();
  }
  
  public E next() throws NoSuchElementException{
    logger.debug("next Called");
    E ret=tlObj.get();
    
    if (ret==null){
      if (lbq.size()==0 && Thread.currentThread().interrupted())
        throw new NoSuchElementException();
    
      try{
        ret=take();
      } catch (InterruptedException ie){
        logger.error("CpoBlockingResultSet.next() - Interrupted and bailing out");
        throw new NoSuchElementException();
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
    consumers.add(Thread.currentThread());
    logger.debug("Take Called");
    return lbq.take();
  }
  
  public void cancel(){
    for(Thread t : consumers){
        t.interrupt();
    }
    for(Thread t : producers){
        t.interrupt();
    }
  }
  
  public int getFetchSize(){
    return capacity;
  }

}
