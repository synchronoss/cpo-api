/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.synchronoss.cpo.jdbc;

import org.synchronoss.cpo.AbstractCpoData;
import org.synchronoss.cpo.meta.domain.CpoAttribute;

/**
 *
 * @author dberry
 */
public abstract class AbstractJdbcCpoData extends AbstractCpoData {
  
  private int index = -1;


  public AbstractJdbcCpoData(CpoAttribute cpoAttribute, int index) {
    super(cpoAttribute);
    this.index = index;
  }

  public int getIndex() {
    return index;
  }

  public void setIndex(int index) {
    this.index = index;
  }

}
