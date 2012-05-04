/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo;

import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 *
 * @author dberry
 */
public interface CpoData {
  
  public Object invokeGetter() throws CpoException;
  
  public void invokeSetter(Object instanceObject) throws CpoException;
  
  public Object transformIn(Object datasourceObject) throws CpoException;

  public Object transformOut(Object attributeObject) throws CpoException;

}
