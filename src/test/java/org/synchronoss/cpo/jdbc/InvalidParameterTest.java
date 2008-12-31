package org.synchronoss.cpo.jdbc;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoException;

import junit.framework.TestCase;

public class InvalidParameterTest extends TestCase {
  private static Logger logger = Logger.getLogger(InvalidParameterTest.class.getName());
  private CpoAdapter jdbcIdo_ = null;
  
  public InvalidParameterTest(String name) {
      super(name);
  }
  
  /**
   * <code>setUp</code>
   * Load the datasource from the properties in the property file jdbc_en_US.properties 
   * 
   * @author david berry
   * @version '$Id: RetrieveObjectTest.java,v 1.6 2006/01/30 19:09:23 dberry Exp $'
   */

  public void setUp() {
      String method = "setUp:";
      
      try{
        jdbcIdo_ = JdbcCpoFactory.getCpoAdapter();
          assertNotNull(method+"IdoAdapter is null",jdbcIdo_);
      } catch (Exception e) {
          fail(method+e.getMessage());
      }
  }

  public void testRetrieveObjectBadContext() {
    String method = "testRetrieveObjectBadContext:";
    Collection<ValueObject> col = null;
    
    
    try{
        ValueObject valObj = new ValueObject();
        col = jdbcIdo_.retrieveObjects("BadContext",valObj,valObj,null,null);
        fail(method+"Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
      ce.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
      fail(method+"Unexpected Exception"+e.getLocalizedMessage());
    } 
  }
  
  public void testRetrieveObjectsNullBean() {
    String method = "testRetrieveObjectsNullBean:";
    Collection<ValueObject> col = null;
    
    
    try{
        ValueObject valObj = null;
        col = jdbcIdo_.retrieveObjects(null,valObj,valObj,null,null);
        fail(method+"Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
      ce.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
      fail(method+"Unexpected Exception"+e.getLocalizedMessage());
    } 
  }
  
  public void testRetrieveObjectNullBean() {
    String method = "testRetrieveObjectNullBean:";
    Collection<ValueObject> col = null;
    
    
    try{
        ValueObject valObj = null;
        valObj = jdbcIdo_.retrieveObject(null,valObj);
        fail(method+"Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
      ce.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
      fail(method+"Unexpected Exception"+e.getLocalizedMessage());
    } 
  }
  
  public void testInsertObjectNullBean() {
    String method = "testInsertObjectNullBean:";
    Collection<ValueObject> col = null;
    
    
    try{
        ValueObject valObj = null;
        jdbcIdo_.insertObject(null,valObj);
        fail(method+"Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
      ce.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
      fail(method+"Unexpected Exception"+e.getLocalizedMessage());
    } 
  }
  
  
  public void testRetrieveObjectNullContext() {
    String method = "testRetrieveObjectNullContext:";
    Collection<LobValueObject> lvos = null;
    
    
    try{
      LobValueObject lvo = new LobValueObject();
      logger.debug("Calling the NULL List");
      lvos = jdbcIdo_.retrieveObjects(null,lvo,lvo,null,null);
      logger.debug("Called the NULL List");
      fail(method+"Test got to unreachable code");
    } catch (CpoException ce) {
      //This is what I am expecting so let it go
      //This is what I am expecting so let it go
      logger.debug("Got a cpo exception");
      ce.printStackTrace();
    } catch (Exception e) {
      e.printStackTrace();
      fail(method+"Unexpected Exception"+e.getLocalizedMessage());
    } 
  }

}
