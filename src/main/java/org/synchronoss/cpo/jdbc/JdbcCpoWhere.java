/**
 * JdbcCpoWhere.java
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

import java.util.Collection;
import org.synchronoss.cpo.ChildNodeException;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.CpoWhere;
import org.synchronoss.cpo.Node;

/**
 * JdbcCpoWhere is an interface for specifying the sort order in which
 * objects are returned from the Datasource.
 *
 * @author david berry
 */

public class JdbcCpoWhere extends Node implements CpoWhere{

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    

    static final String comparisons[] = {
        "=",        //COMP_EQ
        "<",        //COMP_LT
        ">",        //COMP_GT
        "<>",       //COMP_NEQ
        "IN",       //COMP_IN
        "LIKE",     //COMP_LIKE
        "<=",        //COMP_LTEQ
        ">=",        //COMP_GTEQ
        "EXISTS",    //COMP_EXISTS
        "IS NULL"    //COMP_ISNULL
    };

    static final String logicals[] = {
        "AND",        //LOGIC_AND
        "OR"          //LOGIC_OR
    };

    private int comparison = CpoWhere.COMP_NONE;
    private int logical = CpoWhere.LOGIC_NONE;
    private String attribute = null;
    private String rightAttribute = null;
    private Object value = null;
    private String attributeFunction = null;
    private String rightAttributeFunction = null;
    private String valueFunction = null;
    private boolean not = false;
    private String staticValue_ = null;
    private String name = "__CPO_WHERE__";


    public <T> JdbcCpoWhere(int logical, String attr, int comp, T value){
        setLogical(logical);
        setAttribute(attr);
        setComparison(comp);
        setValue(value);
    }

    public <T> JdbcCpoWhere(int logical, String attr, int comp, T value, boolean not){
        setLogical(logical);
        setAttribute(attr);
        setComparison(comp);
        setValue(value);
        setNot(not);
    }

    public JdbcCpoWhere(){
    }

    public void setComparison(int i){
        if(i<0 || i>=comparisons.length) {
            this.comparison=CpoWhere.COMP_NONE;
        } else {
            this.comparison = i;
        }
    }
    public int getComparison(){
        return this.comparison;
    }

    public void setLogical(int i){
        if(i<0 || i>=logicals.length) {
            this.logical=CpoWhere.LOGIC_NONE;
        } else {
            this.logical = i;
        }
    }
    public int getLogical(){
        return this.logical;
    }

    public void setAttribute(String s){
        this.attribute = s;
    }
    public String getAttribute(){
        return this.attribute;
    }

    public void setRightAttribute(String s){
        this.rightAttribute = s;
    }
    public String getRightAttribute(){
        return this.rightAttribute;
    }

    public void setValue(Object s){
        this.value = s;
    }
    public Object getValue(){
        return this.value;
    }

    public void setStaticValue(String staticValue){
        this.staticValue_ = staticValue;
    }
    public String getStaticValue(){
        return this.staticValue_;
    }

    public boolean getNot(){
        return this.not;
    }
    public void setNot(boolean b){
        this.not=b;
    }

    public String toString(JdbcMetaClass<?> jmc)  throws CpoException {
        StringBuilder sb = new StringBuilder();
        JdbcAttribute attribute = null;


        if(getLogical()!=CpoWhere.LOGIC_NONE){
            sb.append(" ");
            sb.append(logicals[getLogical()]);
        } else if (!hasParent()){
          // This is the root where clause
            sb.append("WHERE");
        }

        if(getNot()==true) {
            sb.append(" NOT");
        }

        if(getAttribute()!=null){
            if (sb.length()>0)
              sb.append(" ");
            String fullyQualifiedColumn=null;

            attribute = (JdbcAttribute) jmc.getColumnMap().get(getAttribute());
            if(attribute==null) {
                // This is not an attribute on the cpo bean passed to the retrieveObjects method.
                // treat it as the column name
                fullyQualifiedColumn=getAttribute();
            } else {
              fullyQualifiedColumn=buildColumnName(attribute);
            }
            
            if (getAttributeFunction()!=null){
            	if (attribute!=null)
            		sb.append(buildFunction(getAttributeFunction(),attribute.getName(),fullyQualifiedColumn.toString()));
            	else
            		sb.append(getAttributeFunction());
            } else {
                sb.append(fullyQualifiedColumn);
            }
        }

        if(getComparison()!=CpoWhere.COMP_NONE){
            sb.append(" ");
            sb.append(comparisons[getComparison()]);
        }

        if(getComparison()!=CpoWhere.COMP_ISNULL && (getValue()!=null || getRightAttribute()!=null || getStaticValue()!=null)) {
            sb.append(" ");

            if(getValue()!=null) {
                if (getValueFunction()!=null){
                    sb.append(buildFunction(getValueFunction(), attribute==null?getAttribute():attribute.getName(),"?"));
                } else if(getComparison()==CpoWhere.COMP_IN && getValue() instanceof Collection) {
                  Collection coll = (Collection) getValue();
                  sb.append("(");
                  if (coll.size()>0){
                    sb.append("?"); // add the parameter, we will bind it later.
                    for (int i=1; i<coll.size(); i++){
                      sb.append(", ?"); // add the parameter, we will bind it later.
                    }
                  }
                  sb.append(")");
                } else {
                    sb.append("?"); // add the parameter, we will bind it later.
                }
            } else if(getRightAttribute()!=null) {
                attribute = (JdbcAttribute) jmc.getColumnMap().get(getRightAttribute());
                String fullyQualifiedColumn = null;
                if (attribute==null){
                	fullyQualifiedColumn=getRightAttribute();
                } else {
                  fullyQualifiedColumn=buildColumnName(attribute);
                }

                if (getRightAttributeFunction()!=null) {
                    sb.append(buildFunction(getRightAttributeFunction(),attribute==null?getAttribute():attribute.getName(),fullyQualifiedColumn));
                } else {
                    sb.append(fullyQualifiedColumn);
                }
             } else if(getStaticValue()!=null) {
                sb.append(getStaticValue());
             }
        }
        return sb.toString();
    }

    public void addWhere(CpoWhere cw) throws CpoException{
        try{
            this.addChild((Node)cw);
        }catch(ChildNodeException cne){
            throw new CpoException("Error Adding Where Statement");
        }
    }

    public void setAttributeFunction(String s){
        this.attributeFunction=s;
    }
    public String getAttributeFunction(){
        return this.attributeFunction;
    }

    public void setValueFunction(String s){
        this.valueFunction=s;
    }
    public String getValueFunction(){
        return this.valueFunction;
    }

    public void setRightAttributeFunction(String s){
        this.rightAttributeFunction=s;
    }
    public String getRightAttributeFunction(){
        return this.rightAttributeFunction;
    }

    private String buildFunction(String function, String match, String value){
        StringBuilder sb = new StringBuilder();
        int attrOffset = 0;
        int fromIndex = 0;

        if(function!=null && function.length()>0) {
            while((attrOffset=function.indexOf(match, fromIndex))!=-1){
                     sb.append(function.substring(0,attrOffset));
                     sb.append(value);
                     fromIndex+=attrOffset+match.length();
            }
            sb.append(function.substring(fromIndex));
        }

        return sb.toString();
    }
    
    private String buildColumnName(JdbcAttribute attribute){
      StringBuilder columnName = new StringBuilder();
      
      if (attribute.getDbTable()!=null){
        columnName.append(attribute.getDbTable());
        columnName.append(".");
      }
      if (attribute.getDbColumn()!=null) {
        columnName.append(attribute.getDbColumn());
      } else {
        columnName.append(attribute.getDbName());
      }
      
      return columnName.toString();
    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }
}
