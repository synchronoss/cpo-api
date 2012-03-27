/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo;

/**
 *
 * @author dberry
 */
public interface CpoFactory {
  
  public CpoAdapter getCpoAdapter() throws CpoException;
	
	public CpoAdapter getCpoAdapter(String context) throws CpoException;
  
}
