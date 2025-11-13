/*
 * Copyright (C) 2003-2025 David E. Berry
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

import java.math.BigDecimal;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.cpoCoreMeta.CtArgument;
import org.synchronoss.cpo.core.cpoCoreMeta.CtAttribute;
import org.synchronoss.cpo.jdbc.JdbcCpoArgument;
import org.synchronoss.cpo.jdbc.JdbcCpoAttribute;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcArgument;
import org.synchronoss.cpo.jdbc.cpoJdbcMeta.CtJdbcAttribute;
import org.synchronoss.cpo.meta.AbstractCpoMetaAdapter;
import org.synchronoss.cpo.meta.DataTypeMapEntry;
import org.synchronoss.cpo.meta.DataTypeMapper;
import org.synchronoss.cpo.meta.domain.CpoArgument;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.parser.BoundExpressionParser;
import org.synchronoss.cpo.parser.ExpressionParser;

/**
 * Builds and manages the Java to Datasource type mapping
 *
 * @author dberry
 */
public class JdbcCpoMetaAdapter extends AbstractCpoMetaAdapter {
  private static final Logger logger = LoggerFactory.getLogger(JdbcCpoMetaAdapter.class);
  private static final DataTypeMapEntry<String> defaultDataTypeMapEntry =
      new DataTypeMapEntry<>(java.sql.Types.VARCHAR, "VARCHAR", String.class); // 12
  private static final DataTypeMapper dataTypeMapper = initDataTypeMapper();

  /** Constructs a JdbcCpoMetaAdapter */
  public JdbcCpoMetaAdapter() {}

  @Override
  protected DataTypeMapper getDataTypeMapper() {
    return dataTypeMapper;
  }

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
    jdbcArgument.setTypeInfo(ctJdbcArgument.getTypeInfo());
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
    return new BoundExpressionParser();
  }

  @Override
  public List<String> getAllowableDataTypes() {
    return dataTypeMapper.getDataTypeNames();
  }

  private static DataTypeMapper initDataTypeMapper() {
    logger.debug("Initializing the DataMapper");
    DataTypeMapper dataTypeMapper = new DataTypeMapper(defaultDataTypeMapEntry);

    // JDK 1.4.2 Values
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.CHAR, "CHAR", String.class)); // 1
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.LONGVARCHAR, "LONGVARCHAR", String.class)); // -1
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.VARCHAR, "VARCHAR", String.class)); // 12
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.DECIMAL, "DECIMAL", BigDecimal.class)); // 3
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.NUMERIC, "NUMERIC", BigDecimal.class)); // 2
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.TINYINT, "TINYINT", byte.class)); // -6
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.SMALLINT, "SMALLINT", short.class)); // 5
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.INTEGER, "INTEGER", int.class)); // 4
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.BIGINT, "BIGINT", long.class)); // -5
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.REAL, "REAL", float.class)); // 7
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.FLOAT, "FLOAT", double.class)); // 6
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.DOUBLE, "DOUBLE", double.class)); // 8
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.BINARY, "BINARY", byte[].class)); // -2
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.VARBINARY, "VARBINARY", byte[].class)); // -3
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.LONGVARBINARY, "LONGVARBINARY", byte[].class)); // -4
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.DATE, "DATE", java.sql.Date.class)); // 91
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.TIME, "TIME", java.sql.Time.class)); // 92
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            java.sql.Types.TIMESTAMP, "TIMESTAMP", java.sql.Timestamp.class)); // 93
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.CLOB, "CLOB", java.sql.Clob.class)); // 2005
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.BLOB, "BLOB", java.sql.Blob.class)); // 2004
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.ARRAY, "ARRAY", java.sql.Array.class)); // 2003
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.REF, "REF", java.sql.Ref.class)); // 2006
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.DISTINCT, "DISTINCT", Object.class)); // 2001
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.STRUCT, "STRUCT", Object.class)); // 2002
    //    dataTypeMapper.addDataTypeEntry(new DataTypeMapEntry<>(java.sql.Types.NULL, "NULL",
    // Object.class)); // 0
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.OTHER, "OTHER", Object.class)); // 1111
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.JAVA_OBJECT, "JAVA_OBJECT", Object.class)); // 2000
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.DATALINK, "DATALINK", java.net.URL.class)); // 70
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.BIT, "BIT", boolean.class)); // -7
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.BOOLEAN, "BOOLEAN", boolean.class)); // 16

    // ------------------------- JDBC 4.0 -----------------------------------

    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.ROWID, "ROWID", java.sql.RowId.class)); // -8
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.NCHAR, "NCHAR", String.class)); // -15
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.NVARCHAR, "NVARCHAR", String.class)); // -9
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.LONGNVARCHAR, "LONGNVARCHAR", String.class)); // -16
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.NCLOB, "NCLOB", java.sql.NClob.class)); // 2011
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(java.sql.Types.SQLXML, "SQLXML", java.sql.SQLXML.class)); // 2009

    // --------------------------JDBC 4.2 -----------------------------

    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            java.sql.Types.REF_CURSOR, "REF_CURSOR", java.sql.ResultSet.class)); // 2012
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            java.sql.Types.TIME_WITH_TIMEZONE,
            "TIME_WITH_TIMEZONE",
            java.time.OffsetTime.class)); // 2013;
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            java.sql.Types.TIMESTAMP_WITH_TIMEZONE,
            "TIMESTAMP_WITH_TIMEZONE",
            java.time.OffsetDateTime.class)); // 2014

    // dbspecific types needed to generate the class from a function.
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            100,
            "VARCHAR_IGNORECASE",
            java.lang.String.class)); // HSQLDB TYPE for VARCHAR_IGNORE_CASE

    logger.debug("Returning the DataMapper");
    return dataTypeMapper;
  }
}
