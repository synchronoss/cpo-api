/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.config;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.core.cpoCoreConfig.CtDataSourceConfig;

/**
 *
 * @author dberry
 */
public interface CpoConfigProcessor {
  public void processCpoConfig(CtDataSourceConfig cpoConfig) throws CpoException;
}
