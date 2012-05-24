/*
 * Copyright (C) 2003-2012 David E. Berry
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 *
 * A copy of the GNU Lesser General Public License may also be found at
 * http://www.gnu.org/licenses/lgpl.txt
 */
package org.synchronoss.cpo.jdbc.meta;

import org.slf4j.*;
import org.synchronoss.cpo.core.cpoCoreMeta.*;
import org.synchronoss.cpo.jdbc.*;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.*;
import org.synchronoss.cpo.jdbc.parser.SQLExpressionParser;
import org.synchronoss.cpo.meta.AbstractCpoMetaAdapter;
import org.synchronoss.cpo.meta.domain.*;
import org.synchronoss.cpo.parser.ExpressionParser;

import java.math.BigDecimal;
import java.util.*;

/**
 *
 * @author dberry
 */
public class JdbcCpoMetaAdapter extends AbstractCpoMetaAdapter {

//  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoMetaAdapter.class);

  // JDK 1.4.2 Values
  private static final JavaSqlType<?>[] jdbcTypes = {
    new JavaSqlType<String>(java.sql.Types.CHAR, "CHAR", String.class), // 1
    new JavaSqlType<String>(java.sql.Types.LONGVARCHAR, "LONGVARCHAR", String.class), // -1
    new JavaSqlType<String>(java.sql.Types.VARCHAR, "VARCHAR", String.class), // 12
    new JavaSqlType<BigDecimal>(java.sql.Types.DECIMAL, "DECIMAL", BigDecimal.class), // 3
    new JavaSqlType<BigDecimal>(java.sql.Types.NUMERIC, "NUMERIC", BigDecimal.class), // 2
    new JavaSqlType<Byte>(java.sql.Types.TINYINT, "TINYINT", byte.class), // -6
    new JavaSqlType<Short>(java.sql.Types.SMALLINT, "SMALLINT", short.class), // 5
    new JavaSqlType<Integer>(java.sql.Types.INTEGER, "INTEGER", int.class), // 4
    new JavaSqlType<Long>(java.sql.Types.BIGINT, "BIGINT", long.class), // -5
    new JavaSqlType<Float>(java.sql.Types.REAL, "REAL", float.class), // 7
    new JavaSqlType<Double>(java.sql.Types.FLOAT, "FLOAT", double.class), // 6
    new JavaSqlType<Double>(java.sql.Types.DOUBLE, "DOUBLE", double.class), // 8
    new JavaSqlType<byte[]>(java.sql.Types.BINARY, "BINARY", byte[].class), // -2
    new JavaSqlType<byte[]>(java.sql.Types.VARBINARY, "VARBINARY", byte[].class), // -3
    new JavaSqlType<byte[]>(java.sql.Types.LONGVARBINARY, "LONGVARBINARY", byte[].class), // -4
    new JavaSqlType<java.sql.Date>(java.sql.Types.DATE, "DATE", java.sql.Date.class), // 91
    new JavaSqlType<java.sql.Time>(java.sql.Types.TIME, "TIME", java.sql.Time.class), // 92
    new JavaSqlType<java.sql.Timestamp>(java.sql.Types.TIMESTAMP, "TIMESTAMP", java.sql.Timestamp.class), // 93
    new JavaSqlType<java.sql.Clob>(java.sql.Types.CLOB, "CLOB", java.sql.Clob.class), // 2005
    new JavaSqlType<java.sql.Blob>(java.sql.Types.BLOB, "BLOB", java.sql.Blob.class), // 2004
    new JavaSqlType<java.sql.Array>(java.sql.Types.ARRAY, "ARRAY", java.sql.Array.class), // 2003
    new JavaSqlType<java.sql.Ref>(java.sql.Types.REF, "REF", java.sql.Ref.class), // 2006
    new JavaSqlType<Object>(java.sql.Types.DISTINCT, "DISTINCT", Object.class), // 2001
    new JavaSqlType<Object>(java.sql.Types.STRUCT, "STRUCT", Object.class), // 2002
    new JavaSqlType<Object>(java.sql.Types.OTHER, "OTHER", Object.class), // 1111
    new JavaSqlType<Object>(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT", Object.class), // 2000
    new JavaSqlType<java.net.URL>(java.sql.Types.DATALINK, "DATALINK", java.net.URL.class), // 70
    new JavaSqlType<Boolean>(java.sql.Types.BIT, "BIT", boolean.class), // -7
    new JavaSqlType<Boolean>(java.sql.Types.BOOLEAN, "BOOLEAN", boolean.class), //16

    // Now for the dbspecific types needed to generate the class from a function.
    new JavaSqlType<String>(100, "VARCHAR_IGNORECASE", java.lang.String.class) // HSQLDB TYPE for VARCHAR_IGNORE_CASE
  };
  private static HashMap<Integer, JavaSqlType<?>> javaSqlTypeMap = null;
  private static HashMap<String, JavaSqlType<?>> javaSqlTypeNameMap = null;

  @Override
  protected void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute) {
    super.loadCpoAttribute(cpoAttribute, ctAttribute);

    // cast to the expected subclasses
    JdbcCpoAttribute jdbcAttribute = (JdbcCpoAttribute) cpoAttribute;
    CtJdbcAttribute ctJdbcAttribute = (CtJdbcAttribute) ctAttribute;

    jdbcAttribute.setDbTable(ctJdbcAttribute.getDbTable());
    jdbcAttribute.setDbColumn(ctJdbcAttribute.getDbColumn());

  }

  @Override
  protected void loadCpoArgument(CpoArgument cpoArgument, CtArgument ctArgument) {
    super.loadCpoArgument(cpoArgument, ctArgument);

    // cast to the expected subclasses
    JdbcCpoArgument jdbcArgument = (JdbcCpoArgument) cpoArgument;
    CtJdbcArgument ctJdbcArgument = (CtJdbcArgument) ctArgument;

//    logger.debug("Setting argument scope to: "+ctJdbcArgument.getScope().toString());
    jdbcArgument.setScope(ctJdbcArgument.getScope().toString());

  }

  @Override
  protected CpoAttribute createCpoAttribute() {
    return new JdbcCpoAttribute();
  }

  @Override
  protected CpoArgument createCpoArgument() {
    return new JdbcCpoArgument();
  }

  @Override
  public ExpressionParser getExpressionParser() {
    return new SQLExpressionParser();
  }

  @Override
  public String getJavaTypeName(CpoAttribute attribute) {
   String clazzName = getJavaTypeClass(attribute).getName();
   byte[] b = new byte[0];
   char[] c = new char[0];

   if (b.getClass().getName().equals(clazzName)) {
     clazzName = "byte[]";
   } else if (c.getClass().getName().equals(clazzName)) {
     clazzName = "char[]";
   }
   return clazzName;
  }

  @Override
  public Class<?> getJavaTypeClass(CpoAttribute attribute) {
    Class<?> clazz = String.class;
    JavaSqlType<?> jdbcType = getJdbcTypeNameMap().get(attribute.getDataType());
    
    if (attribute.getTransformInMethod() != null)
      clazz = attribute.getTransformInMethod().getReturnType();
    else if (jdbcType != null)
      clazz = jdbcType.getJavaClass();
    
    return clazz;
  }

  @Override
  public List<String> getAllowableDataTypes() {
    return getSqlTypes();
  }
  
  /*
   * public static JavaSqlType getJdbcType(int javaSqlType){ return getJdbcType(new Integer(javaSqlType)); }
   *
   * public static JavaSqlType getJdbcType(Integer javaSqlType){ JavaSqlType jdbcType = (JavaSqlType)
   * getJdbcTypeMap().get(javaSqlType); return jdbcType; }
   */

  public JavaSqlType<?> getJavaSqlType(int javaSqlType) {
    JavaSqlType<?> jdbcType = getJdbcTypeMap().get(javaSqlType);
    if (jdbcType == null) // oracle likes to send back crazy SQL Types sometimes
    // Sending back null just breaks the requesting code. 
    // Will send back VarChar to use get string. This should work
    {
      return getJdbcTypeMap().get(java.sql.Types.VARCHAR);
    } else {
      return jdbcType;
    }
  }

  public int getJavaSqlType(String javaSqlTypeName) {
    JavaSqlType<?> jdbcType = getJdbcTypeNameMap().get(javaSqlTypeName);
    if (jdbcType == null) // oracle likes to send back crazy SQL Types sometimes
    // Sending back null just breaks the requesting code. 
    // Will send back VarChar to use get string. This should work
    {
      return java.sql.Types.VARCHAR;
    } else {
      return jdbcType.getJavaSqlType();
    }
  }

  public Class<?> getSqlTypeClass(int javaSqlType) {
    return getSqlTypeClass(new Integer(javaSqlType));
  }

  public Class<?> getSqlTypeClass(Integer javaSqlType) {
    JavaSqlType<?> jdbcType = getJdbcTypeMap().get(javaSqlType);
    if (jdbcType == null) {
      // oracle likes to send back crazy SQL Types sometimes
      // Sending back null just breaks the requesting code. 
      // Will send back VarChar to use get string. This should work
      jdbcType = getJdbcTypeMap().get(java.sql.Types.VARCHAR); //
    }

    return jdbcType.getJavaClass();
  }

  public Class<?> getSqlTypeClass(String javaSqlTypeName) {
    JavaSqlType<?> jdbcType = getJdbcTypeNameMap().get(javaSqlTypeName);
    if (jdbcType == null) {
      // oracle likes to send back crazy SQL Types sometimes
      // Sending back null just breaks the requesting code. 
      // Will send back VarChar to use get string. This should work
      jdbcType = getJdbcTypeMap().get(java.sql.Types.VARCHAR); //
    }
    return jdbcType.getJavaClass();
  }

  private List<String> getSqlTypes() {
    ArrayList<String> al = new ArrayList<String>();
    // need to put the keySet into an arraylist. The inner class is not serializable 
    al.addAll(getJdbcTypeNameMap().keySet());
    return al;
  }

  private static void initMaps() {
    synchronized (jdbcTypes) {
      if (javaSqlTypeNameMap == null) {
        javaSqlTypeMap = new HashMap<Integer, JavaSqlType<?>>();
        javaSqlTypeNameMap = new HashMap<String, JavaSqlType<?>>();
        for (JavaSqlType<?> jst : jdbcTypes) {
          javaSqlTypeMap.put(new Integer(jst.getJavaSqlType()), jst);
          javaSqlTypeNameMap.put(jst.getJavaSqlTypeName(), jst);
        }
      }
    }
  }

  private static HashMap<Integer, JavaSqlType<?>> getJdbcTypeMap() {
    if (javaSqlTypeMap == null) {
      initMaps();
    }
    return javaSqlTypeMap;
  }

  private static HashMap<String, JavaSqlType<?>> getJdbcTypeNameMap() {
    if (javaSqlTypeNameMap == null) {
      initMaps();
    }
    return javaSqlTypeNameMap;
  }

}
