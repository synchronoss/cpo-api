/**
 * JdbcMetaClass.java
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

import java.util.ArrayList;
import java.util.HashMap;

import org.synchronoss.cpo.CpoException;


/**
 * JdbcMetaClass is a class that maps traditional java classes to tables in a 
 * jdbc database. 
 * 
 * @author david berry
 */

public class JdbcMetaClass extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    
    private Class objClass = null;

    //private Constructor objCtor = null;

    /**
     * The guid assigned to this class.
     */
    private String classId = null;

    /**
     * The fully qualifed class name that this meta is used for. i.e.
     */
    private String name = null;

    /**
     * attributeMap contains a Map of String Objects
     * the id is the columnName of the attribute in the database
     * the value is the attribute name for the class being described 
     */
    private HashMap attributeMap = new HashMap();

    /**
     * columnMap contains a Map of String Objects
     * the id is the attributeName of the attribute in the database
     * the value is the column name for the class being described 
     */
    private HashMap columnMap = new HashMap();

    /**
     * queryGroup is a hashMap that contains a hashMap of jdbcQuery Lists that are used 
     * by this object to persist and retrieve it into a jdbc datasource. 
     */
    private HashMap queryGroups = new HashMap();

    public JdbcMetaClass(Class c, String s) throws CpoException{
        setJmcClass(c);
        setName(s);
    }

    public String getClassId(){
        return this.classId;
    }

    public void setClassId(String s){
        this.classId=s;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String s){
        this.name=s;
    }

    public HashMap getAttributeMap(){
        return this.attributeMap;
    }


    public HashMap getColumnMap(){
        return this.columnMap;
    }

  
    public HashMap getQueryGroups(){
        return this.queryGroups;
    }

    public Class getJmcClass(){
        return objClass;
    }

    public void setJmcClass(Class c){
        objClass = c;
    }

    public void addQueryToGroup(JdbcQuery jq) {
        HashMap qgs = this.getQueryGroups();
        String qgType = jq.getType();
        String qgName = jq.getName();
        HashMap qg = (HashMap)qgs.get(qgType);
        ArrayList al = null;

        if(qg==null) {
            qg=new HashMap();
            qgs.put(qgType,qg);
        }

        al = (ArrayList)qg.get(qgName);

        if(al==null) {
            al=new ArrayList();
            qg.put(qgName, al);
        }

        al.add(jq);
    }

    public ArrayList getQueryGroup(String qgType, String qgName) throws CpoException {
        HashMap qgs = this.getQueryGroups();
        HashMap qg = (HashMap)qgs.get(qgType);
        ArrayList al = null;

        if(qg==null) {
            throw new CpoException("No <"+qgType+"> Query Group defined for "+name );
        }

        al = (ArrayList)qg.get(qgName);

        if(al==null) {
            throw new CpoException(qgType+" Query Group <"+qgName+"> not defined for "+name );
        }

        return al;
    }
}