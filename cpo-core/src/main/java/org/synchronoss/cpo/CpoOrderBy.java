/*
 *  Copyright (C) 2003-2012 David E. Berry
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

/**
 * CpoOrderBy is an interface for specifying the sort order in which 
 * objects are returned from the Datasource.
 * 
 * @author david berry
 */

public interface CpoOrderBy {

    /**
     * Gets the boolean that determines if the objects will be returned from
     * from the CpoAdapter in Ascending order or Descending order
     * 
     * @return boolean true if it is to sort in Ascensing Order
     *                 false if it is to be sorted in Descending Order
     */
    public boolean getAscending();

    /**
     * Sets the boolean that determines if the objects will be returned from
     * from the CpoAdapter in Ascending order or Descending order
     * 
     * @param b true if it is to sort in Ascensing Order
     *          false if it is to be sorted in Descending Order
     */
    public void setAscending(boolean b);

    /**
     * Gets the name of the attribute that is to be used to sort the results 
     * from the CpoAdapter.
     * 
     * @return String The name of the attribute
     */
    public String getAttribute();

    /**
     * Sets the name of the attribute that is to be used to sort the results 
     * from the CpoAdapter.
     * 
     * @param s The name of the attribute
     */
    public void setAttribute(String s);

    /**
     * Gets a string representing a datasource specific function call that 
     * must be applied to the attribute that will be used for sorting.
     * 
     * i.e. - "upper(attribute_name)"
     * 
     * @return String The name of the function
     */
    public String getFunction();

    /**
     * Sets a string representing a datasource specific function call that 
     * must be applied to the attribute that will be used for sorting.
     * 
     * i.e. - "upper(attribute_name)"
     * 
     * @param s The name of the function
     */
    public void setFunction(String s);
    
    /**
     * Gets a string representing the name of this instance of the 
     * CpoOrderBy
     * 
     * @return String The name of the CpoOrderBy
     */
    public String getName();

    /**
     * Sets a string representing the name of this instance of the 
     * CpoOrderBy
     * 
     * @param s The name of the CpoOrderBy
     */
    public void setName(String s);

}
