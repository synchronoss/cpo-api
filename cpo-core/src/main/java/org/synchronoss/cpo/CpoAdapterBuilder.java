/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo;

import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;

/**
 *
 * @author dberry
 */
public interface CpoAdapterBuilder {
  
	public CpoAdapter buildCpoAdapter(CtDataSourceConfig dataSourceConfig) throws CpoException;
  
}
