/*
 * Copyright (C) 2003-2025 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.jdbc;

import org.synchronoss.cpo.CpoAdapter;
import org.synchronoss.cpo.CpoAdapterFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoTrxAdapter;
import org.synchronoss.cpo.jdbc.jta.JdbcCpoXaAdapter;
import org.synchronoss.cpo.jta.CpoXaResource;

/** Created by dberry on 11/8/15. */
public class JdbcCpoAdapterFactory implements CpoAdapterFactory {

  private JdbcCpoAdapter jdbcCpoAdapter = null;

  /**
   * Constructs a JdbcCpoAdapterFactory from a JdbcCpoAdapter
   *
   * @param jdbcCpoAdapter A JdbcCpoAdapter
   */
  public JdbcCpoAdapterFactory(JdbcCpoAdapter jdbcCpoAdapter) {
    this.jdbcCpoAdapter = jdbcCpoAdapter;
  }

  @Override
  public CpoAdapter getCpoAdapter() throws CpoException {
    return jdbcCpoAdapter;
  }

  /**
   * Provides a mechanism for the user to obtain a CpoTrxAdapter object. This object allows the to
   * control when commits and rollbacks occur on CPO.
   *
   * <pre>Example:
   * {@code
   * class SomeObject so = null;
   * class CpoAdapter cpo = null;
   * class CpoTrxAdapter cpoTrx = null;
   * try {
   * 	cpo = new JdbcCpoAdapter(new JdbcDataSourceInfo(driver, url, user, password,1,1,false));
   * 	cpoTrx = cpo.getCpoTrxAdapter();
   * } catch (CpoException ce) {
   * 	// Handle the error
   * 	cpo = null;
   * }
   * if (cpo!=null) {
   * 	try{
   * 		for (int i=0; i<3; i++){
   * 			so = new SomeObject();
   * 			so.setId(1);
   * 			so.setName("SomeName");
   * 			cpo.updateObject("myUpdate",so);
   *    }
   * 		cpoTrx.commit();
   *  } catch (CpoException ce) {
   * 		// Handle the error
   * 		cpoTrx.rollback();
   *  }
   * }
   * }
   * </pre>
   *
   * @return A CpoTrxAdapter to manage the transactionality of CPO
   * @throws CpoException Thrown if there are errors accessing the datasource
   * @see CpoTrxAdapter
   */
  @Override
  public CpoTrxAdapter getCpoTrxAdapter() throws CpoException {
    return new JdbcCpoTrxAdapter(jdbcCpoAdapter);
  }

  @Override
  public CpoXaResource getCpoXaAdapter() throws CpoException {
    return new JdbcCpoXaAdapter(this);
  }
}
