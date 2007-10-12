/**
 * JdbcParameter.java
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


/**
 * JdbcParameter is a class that maps traditional java classes to tables in a 
 * jdbc database. 
 * 
 * @author david berry
 */

public class JdbcParameter extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

	/**
     * Version Id for this class.
     */
	private static final long serialVersionUID = 1L;
	

    private static final String IN_PARAMETER = "IN";
    private static final String OUT_PARAMETER = "OUT";
    private static final String INOUT_PARAMETER = "BOTH";

    private JdbcAttribute attribute_ = null;
    private String type_ = null;

    public JdbcParameter(){
    }

    public JdbcParameter(JdbcAttribute attribute, String type)
    throws CpoException {
        setAttribute(attribute);
        setType(type);
    }

    public void setAttribute(JdbcAttribute attribute){
        attribute_ = attribute;
    }

    public void setType(String type){
        type_ = type;
    }

    public JdbcAttribute getAttribute(){
        return attribute_;
    }

    public String getType(){
        return type_;
    }

    public boolean isInParameter(){
        return IN_PARAMETER.equals(getType()) || INOUT_PARAMETER.equals(getType());
    }

    public boolean isOutParameter(){
        return OUT_PARAMETER.equals(getType()) || INOUT_PARAMETER.equals(getType());
    }
}