/*
 * @(#)JdbcTypeClasses.java      6/4/2001 10:38p
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
import java.util.Collection;
import java.util.HashMap;



/**
 * JdbcTypeClasses is a class that maps datasource datatypes to java.sql.types and java classes
 * 
 * @author david berry
 */

public class JavaSqlTypes extends java.lang.Object implements java.io.Serializable, java.lang.Cloneable {

    /**
     * Version Id for this class.
     */
    private static final long serialVersionUID = 1L;
    
                                                                     // JDK 1.4.2 Values

   private static JavaSqlType[] jdbcTypes = {   
        new JavaSqlType(java.sql.Types.CHAR, "CHAR", java.lang.String.class), // 1
        new JavaSqlType(java.sql.Types.LONGVARCHAR, "LONGVARCHAR", java.lang.String.class), // -1
        new JavaSqlType(java.sql.Types.VARCHAR, "VARCHAR", java.lang.String.class), // 12
        new JavaSqlType(java.sql.Types.DECIMAL, "DECIMAL", java.math.BigDecimal.class), // 3
        new JavaSqlType(java.sql.Types.NUMERIC, "NUMERIC", java.math.BigDecimal.class), // 2
        new JavaSqlType(java.sql.Types.TINYINT, "TINYINT", byte.class), // -6
        new JavaSqlType(java.sql.Types.SMALLINT, "SMALLINT", short.class), // 5
        new JavaSqlType(java.sql.Types.INTEGER, "INTEGER", int.class), // 4
        new JavaSqlType(java.sql.Types.BIGINT, "BIGINT", long.class), // -5
        new JavaSqlType(java.sql.Types.REAL, "REAL", float.class), // 7
        new JavaSqlType(java.sql.Types.FLOAT, "FLOAT", double.class), // 6
        new JavaSqlType(java.sql.Types.DOUBLE, "DOUBLE", double.class), // 8
        new JavaSqlType(java.sql.Types.BINARY, "BINARY", byte[].class), // -2
        new JavaSqlType(java.sql.Types.VARBINARY, "VARBINARY", byte[].class), // -3
        new JavaSqlType(java.sql.Types.LONGVARBINARY, "LONGVARBINARY", byte[].class), // -4
        new JavaSqlType(java.sql.Types.DATE, "DATE", java.sql.Date.class), // 91
        new JavaSqlType(java.sql.Types.TIME, "TIME", java.sql.Time.class), // 92
        new JavaSqlType(java.sql.Types.TIMESTAMP, "TIMESTAMP", java.sql.Timestamp.class), // 93
        new JavaSqlType(java.sql.Types.CLOB, "CLOB", java.sql.Clob.class), // 2005
        new JavaSqlType(java.sql.Types.BLOB, "BLOB", java.sql.Blob.class), // 2004
        new JavaSqlType(java.sql.Types.ARRAY, "ARRAY", java.sql.Array.class), // 2003
        new JavaSqlType(java.sql.Types.REF, "REF", java.sql.Ref.class), // 2006
        new JavaSqlType(java.sql.Types.DISTINCT, "DISTINCT", java.lang.Object.class), // 2001
        new JavaSqlType(java.sql.Types.STRUCT, "STRUCT", java.lang.Object.class), // 2002
        new JavaSqlType(java.sql.Types.OTHER, "OTHER", java.lang.Object.class), // 1111
        new JavaSqlType(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT", java.lang.Object.class), // 2000
        new JavaSqlType(java.sql.Types.DATALINK, "DATALINK", java.net.URL.class), // 70
        new JavaSqlType(java.sql.Types.BIT, "BIT", boolean.class), // -7
        new JavaSqlType(java.sql.Types.BOOLEAN, "BOOLEAN", boolean.class), //16
        
        // Now for the dbspecific types needed to generate the class from a query.
        new JavaSqlType(100, "VARCHAR_IGNORECASE", java.lang.String.class) // HSQLDB TYPE for VARCHAR_IGNORE_CASE

    };
   
    private static HashMap javaSqlTypeMap = null;
    private static HashMap javaSqlTypeNameMap = null;

    private JavaSqlTypes(){
    }
/*
    public static JavaSqlType getJdbcType(int javaSqlType){
        return getJdbcType(new Integer(javaSqlType));
    }

    public static JavaSqlType getJdbcType(Integer javaSqlType){
        JavaSqlType jdbcType = (JavaSqlType) getJdbcTypeMap().get(javaSqlType);
        return jdbcType;
    }
*/    
    public static int getJavaSqlType(String javaSqlTypeName){
        JavaSqlType jdbcType = (JavaSqlType) getJdbcTypeNameMap().get(javaSqlTypeName);
        if (jdbcType == null)
            return java.sql.Types.NULL;
        else
            return jdbcType.getJavaSqlType();
    }
    
    public static Class getSqlTypeClass(int javaSqlType){
        return getSqlTypeClass(new Integer(javaSqlType));
    }
    
    
    public static Class getSqlTypeClass(Integer javaSqlType){
        JavaSqlType jdbcType = (JavaSqlType) getJdbcTypeMap().get(javaSqlType);
        if (jdbcType == null)
            return null;
        else
            return jdbcType.getJavaClass();
    }
    
    
    public static Class getSqlTypeClass(String javaSqlTypeName){
        JavaSqlType jdbcType = (JavaSqlType) getJdbcTypeNameMap().get(javaSqlTypeName);
        if (jdbcType == null)
            return null;
        else
            return jdbcType.getJavaClass();
    }
    
    public static Collection getSqlTypes() {
        ArrayList al = new ArrayList();
        // need to put the keySet into an arraylist. The inner class is not serializable 
        al.addAll(getJdbcTypeNameMap().keySet());
        return al;
    }
    
    private static void initMaps(){
        int i;
    
        synchronized(jdbcTypes) {
            if(javaSqlTypeMap==null) {
                javaSqlTypeMap = new HashMap();
                javaSqlTypeNameMap = new HashMap();
                for(i=0;i<jdbcTypes.length;i++) {
                    javaSqlTypeMap.put(new Integer(jdbcTypes[i].getJavaSqlType()), jdbcTypes[i]);
                    javaSqlTypeNameMap.put(jdbcTypes[i].getJavaSqlTypeName(), jdbcTypes[i]);
                }
            }
        }
    }
    
    
    private static HashMap getJdbcTypeMap(){
        if (javaSqlTypeMap==null)
            initMaps();
        return javaSqlTypeMap;
    }
    

    private static HashMap getJdbcTypeNameMap(){
        if (javaSqlTypeNameMap==null)
            initMaps();
        return javaSqlTypeNameMap;
    }
    


}