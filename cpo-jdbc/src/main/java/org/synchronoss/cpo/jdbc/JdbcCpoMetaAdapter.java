/**
 *  JdbcCpoAdapter.java    
 *
 *  Copyright (C) 2006  David E. Berry
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 *  A copy of the GNU Lesser General Public License may also be found at 
 *  http://www.gnu.org/licenses/lgpl.txt
 *
 */
package org.synchronoss.cpo.jdbc;

import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.meta.AbstractCpoMetaAdapter;
import org.synchronoss.cpo.meta.domain.CpoClass;

/**
 *
 * @author dberry
 */
public class JdbcCpoMetaAdapter extends AbstractCpoMetaAdapter {
  
  /**
   * DOCUMENT ME!
   *
   * @param objClass DOCUMENT ME!
   * @param name     DOCUMENT ME!
   * @return DOCUMENT ME!
   * @throws CpoException DOCUMENT ME!
   */
  protected <T> CpoClass<T> loadMetaClass(Class<T> objClass, String name)
      throws CpoException {
    CpoClass<T> cpoClass=null;

    cpoClass = new CpoClass<T>(objClass);
//    loadAttributeMap(name, cpoClass);
//    loadQueryGroups(cpoClass);

    return cpoClass;
  }

  
}
