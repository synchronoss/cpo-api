/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo;

/**
 *
 * @author dberry
 */
public final class CpoAdapterFactory {
  private static final String DEFAULT_CONTEXT="default";

  public static CpoAdapter getCpoAdapter() throws CpoException {
    return getCpoAdapter(DEFAULT_CONTEXT);
  }

  public static CpoAdapter getCpoAdapter(String context) throws CpoException {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
}
