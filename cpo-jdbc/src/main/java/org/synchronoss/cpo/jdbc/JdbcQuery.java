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

package org.synchronoss.cpo.jdbc;

import java.util.ArrayList;

/**
 * JdbcQuery is a class that maps traditional java classes to tables in a 
 * jdbc database. 
 * 
 * @author david berry
 */

public class JdbcQuery extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

	/**
     * Version Id for this class.
     */
	private static final long serialVersionUID = 1L;
	

    /**
     * The guid assigned to this query.
     */
    private String queryId = null;

    /**
     * The sql to be used for this query 
     */
    private String text = null;

    private String name = null;
    private String type = null;

    /**
     * parameterList is a list of attribute names to get the data from
     * to use as parameters for the query
     */
    private ArrayList<JdbcArgument> parameterList = new ArrayList<JdbcArgument>();

    public JdbcQuery(){
    }

    public String getQueryId(){
        return this.queryId;
    }

    public void setQueryId(String s){
        this.queryId = s;
    }

    public String getText(){
        return this.text;
    }

    public void setText(String s){
        this.text = s;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String s){
        this.name = s;
    }

    public String getType(){
        return this.type;
    }

    public void setType(String s){
        this.type = s;
    }

    public ArrayList<JdbcArgument> getParameterList(){
        return this.parameterList;
    }

}