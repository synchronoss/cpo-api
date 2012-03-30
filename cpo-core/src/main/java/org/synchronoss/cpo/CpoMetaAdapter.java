/**
 * CpoMetaAdapter.java  
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
package org.synchronoss.cpo;

import org.synchronoss.cpo.CpoException;

/**
 *
 * @author dberry
 */
public interface CpoMetaAdapter {
    /**
     * Clears the metadata for the specified object. The metadata will be reloaded
     * the next time that CPO is called to access this object
     *
     * @param obj The object whose metadata must be cleared
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public void clearMetaClass(Object obj) throws CpoException;

    /**
     * Clears the metadata for the specified fully qualifed class name. The metadata 
     * will be reloaded the next time CPO is called to access this class.
     *
     * @param className The fully qualified class name for the class that needs its
     *               metadata cleared.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
     */
    public void clearMetaClass(String className) throws CpoException;

    /**
     * Clears the metadata for all classes. The metadata will be lazy-loaded from 
     * the metadata repository as classes are accessed.
     *
     * @param all true - clear all classes for all datasources.
     *            false - clear all classes for the current datasource.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    */
    public void clearMetaClass(boolean all) throws CpoException;

    /**
     * Clears the metadata for all classes for the current datasource. The metadata will be lazy-loaded from 
     * the metadata repository as classes are accessed.
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    */
    public void clearMetaClass() throws CpoException;
    
    /**
     * Returns the meta data for the class that is contained within the meta data source
     *
     * @throws CpoException Thrown if there are errors accessing the datasource
    */
    public void getMetaClass(String className) throws CpoException;
  
}
