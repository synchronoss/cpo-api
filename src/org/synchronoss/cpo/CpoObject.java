/**
 * CpoObject.java
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


/**
 * CpoObject is a class that maps datasource datatypes to java.sql.types and java classes
 * 
 * @author david berry
 */

public class CpoObject extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

//    private static Logger logger = Logger.getLogger(CpoObject.class.getName());

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    

    private int operation_ = -1;
    private Object object_ = null;
    private String   name_ = null;

    private CpoObject(){}

    public CpoObject(int operation, String name, Object object){
        setOperation(operation);
        setName(name);
        setObject(object);
    }

    protected void setOperation(int operation){
        operation_ = operation;
    }

    protected void setName(String name){
        name_ = name;
    }

    protected void setObject(Object object){
        object_ = object;
    }

    public int getOperation(){
        return operation_;
    }

    public String getName(){
        return name_;
    }

    public Object getObject(){
        return object_;
    }

}