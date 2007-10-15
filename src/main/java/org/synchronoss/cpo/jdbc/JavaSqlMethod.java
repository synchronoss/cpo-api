/*
 * @(#)JavaSqlMethod.java      6/4/2001 10:38p
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

import java.lang.reflect.Method;

import org.apache.log4j.Logger;
import org.synchronoss.cpo.CpoException;

/**
 * JdbcType is a class that maps datasource datatypes to java.sql.types and java classes
 * 
 * @author david berry
 */

public class JavaSqlMethod extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {


	private static Logger logger = Logger.getLogger(JavaSqlMethod.class.getName());

	/**
     * Version Id for this class.
     */
	private static final long serialVersionUID = 1L;

	public static final int METHOD_TYPE_BASIC = 0;
	public static final int METHOD_TYPE_STREAM = 1;
	public static final int METHOD_TYPE_READER = 2;
	
	
    private Class     javaClass_ = null;
    private Class	  javaSqlMethodClass_ = null;
    private Method       rsGetter_ = null;
    private Method       psSetter_ = null;
    private Method       csGetter_ = null;
    private Method       csSetter_ = null;
    private String         dbType_ = null;
    private int        methodType_ = METHOD_TYPE_BASIC;

    private static final Class psc = java.sql.PreparedStatement.class;
    private static final Class rsc = java.sql.ResultSet.class;
	private static final Class csc = java.sql.CallableStatement.class;
	
    
    private JavaSqlMethod(){}
    

    public JavaSqlMethod(int methodType, Class javaClass,Class javaSqlMethodClass,String getterName, String setterName) {
    	
    	try {
	    	setMethodType(methodType);
	        setJavaClass(javaClass);
	        setJavaSqlMethodClass(javaSqlMethodClass);
	        setRsGetter(getterName);
	        setPsSetter(setterName);
	        setCsGetter(getterName);
	        setCsSetter(setterName);
    	} catch (CpoException ce){
    		logger.error("Error In JavaSqlMethod", ce);
	    }
    }

    public void setJavaClass(Class javaClass){
    	this.javaClass_=javaClass;
    }
    public void setJavaSqlMethodClass(Class javaSqlMethodClass){
    	this.javaSqlMethodClass_=javaSqlMethodClass;
    }
    
    public void setPsSetter(String setterName) throws CpoException {
    	try{
    		if (getMethodType()==METHOD_TYPE_BASIC)
    			psSetter_ = psc.getMethod(setterName,new Class[] {int.class, getJavaSqlMethodClass()});
    		else 
    			psSetter_ = psc.getMethod(setterName,new Class[] {int.class, getJavaSqlMethodClass(), int.class});
    			
    	} catch (NoSuchMethodException nsme){
    		logger.error("Error loading Setter"+setterName, nsme);
    		throw new CpoException(nsme);
    	}
    }
    
    public void setRsGetter(String getterName) throws CpoException {
    	try{
        	rsGetter_ = rsc.getMethod(getterName,new Class[] {int.class});
    	} catch (NoSuchMethodException nsme){
    		logger.error("Error loading Getter"+getterName, nsme);
    		throw new CpoException(nsme);
    	}
    }
    
    public void setCsSetter(String setterName) throws CpoException {
    	try{
    		if (getMethodType()==METHOD_TYPE_BASIC)
    			csSetter_ = csc.getMethod(setterName,new Class[] {int.class, getJavaSqlMethodClass()});
    		else 
    			csSetter_ = csc.getMethod(setterName,new Class[] {int.class, getJavaSqlMethodClass(), int.class});
    	} catch (NoSuchMethodException nsme){
    		logger.error("Error loading Setter"+setterName, nsme);
    		throw new CpoException(nsme);
    	}
    }
    
    public void setCsGetter(String getterName) throws CpoException {
    	try{
        	csGetter_ = csc.getMethod(getterName,new Class[] {int.class});
    	} catch (NoSuchMethodException nsme){
    		logger.error("Error loading Getter"+getterName, nsme);
    		throw new CpoException(nsme);
    	}
    }
   

    public Class getJavaClass(){
        return javaClass_;
    }
    
    public Class getJavaSqlMethodClass(){
        return javaSqlMethodClass_;
    }
    
    public Method getRsGetter(){
    	return rsGetter_;
    }
        
    public Method getPsSetter(){
    	return psSetter_;
    }
    
    public Method getCsGetter(){
    	return csGetter_;
    }
    
    public Method getCsSetter(){
    	return csSetter_;
    }
    
    public String getDatabaseType(){
    	return dbType_;
    }
    
    public void setDatabaseType(String dbType){
    	dbType_=dbType;
    }
    
    public int getMethodType(){
    	return methodType_;
    }
    
    public void setMethodType(int methodType){
    	methodType_=methodType;
    }

}