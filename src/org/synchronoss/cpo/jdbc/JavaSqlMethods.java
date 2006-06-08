/*
 * @(#)JavaSqlMethods.java      6/4/2001 10:38p
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

import java.util.HashMap;

/**
 * JdbcTypeClasses is a class that maps datasource datatypes to java.sql.types and java classes
 * 
 * @author david berry
 */

public class JavaSqlMethods extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

	/**
     * Version Id for this class.
     */
	private static final long serialVersionUID = 1L;
	
                                                                     // JDK 1.4.2 Values

   private static JavaSqlMethod[] javaSqlMethods = {   
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.String.class, java.lang.String.class, "getString", "setString"), // 12
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.math.BigDecimal.class,java.math.BigDecimal.class, "getBigDecimal","setBigDecimal"), // 3
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,byte.class, byte.class,"getByte","setByte"), // -6
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Byte.class, byte.class, "getByte","setByte"), // -6
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,short.class,short.class, "getShort","setShort"), // 5
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Short.class,short.class, "getShort","setShort"), // 5
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,int.class,int.class, "getInt","setInt"), // 4
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Integer.class,int.class, "getInt","setInt"), // 4
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,long.class,long.class, "getLong","setLong"), // -5
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Long.class,long.class, "getLong","setLong"), // -5
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,float.class,float.class, "getFloat","setFloat"), // 7
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Float.class,float.class, "getFloat","setFloat"), // 7
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,double.class,double.class, "getDouble","setDouble"), // 6
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Double.class,double.class, "getDouble","setDouble"), // 8
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,byte[].class, byte[].class, "getBytes","setBytes"), // -2
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.sql.Date.class,java.sql.Date.class, "getDate","setDate"), // 91
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.sql.Time.class,java.sql.Time.class, "getTime","setTime"), // 92
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.sql.Timestamp.class,java.sql.Timestamp.class, "getTimestamp","setTimestamp"), // 93
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.sql.Clob.class,java.sql.Clob.class, "getClob","setClob"), // 2005
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.sql.Blob.class,java.sql.Blob.class, "getBlob","setBlob"), // 2004
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.sql.Array.class,java.sql.Array.class, "getArray", "setArray"), // 2003
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.sql.Ref.class,java.sql.Ref.class, "getRef","setRef"), // 2006
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Object.class,java.lang.Object.class, "getObject","setObject"), // 2001
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.net.URL.class,java.net.URL.class, "getURL","setURL"), // 70
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,boolean.class,boolean.class,"getBoolean", "setBoolean"), // -7
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_BASIC,java.lang.Boolean.class, boolean.class,"getBoolean","setBoolean"), // 16
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_STREAM,java.io.InputStream.class,java.io.InputStream.class, "getBlob","setBinaryStream"), // 16
    	new JavaSqlMethod(JavaSqlMethod.METHOD_TYPE_READER,java.io.Reader.class,java.io.Reader.class, "getClob","setCharacterStream") // 16
    };
   
    private static HashMap javaSqlMethodMap = null;

    private JavaSqlMethods(){
    }

    static public JavaSqlMethod getJavaSqlMethod(Class c) {
    	JavaSqlMethod javaSqlMethod = (JavaSqlMethod)getJavaSqlMethodMap().get(c);
    	return javaSqlMethod;
    	
    }
    
    static private void initMaps(){
	    int i;
	
	    synchronized(javaSqlMethods) {
	        if(javaSqlMethodMap==null) {
	        	javaSqlMethodMap = new HashMap();
	            for(i=0;i<javaSqlMethods.length;i++) {
	            	javaSqlMethodMap.put(javaSqlMethods[i].getJavaClass(), javaSqlMethods[i]);
	            }
	        }
	    }
    }
    
    private static HashMap getJavaSqlMethodMap(){
    	if (javaSqlMethodMap==null)
    		initMaps();
    	return javaSqlMethodMap;
    }
    


}