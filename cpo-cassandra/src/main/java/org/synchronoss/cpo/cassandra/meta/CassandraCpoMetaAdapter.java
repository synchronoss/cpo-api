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
package org.synchronoss.cpo.cassandra.meta;

import com.datastax.driver.core.DataType;
import com.datastax.driver.core.LocalDate;
import com.datastax.driver.core.TupleValue;
import com.datastax.driver.core.UDTValue;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.CpoException;
import org.synchronoss.cpo.cassandra.cpoCassandraMeta.CtCassandraAttribute;
import org.synchronoss.cpo.core.cpoCoreMeta.CtAttribute;
import org.synchronoss.cpo.meta.AbstractCpoMetaAdapter;
import org.synchronoss.cpo.meta.DataTypeMapEntry;
import org.synchronoss.cpo.meta.DataTypeMapper;
import org.synchronoss.cpo.meta.domain.CpoAttribute;
import org.synchronoss.cpo.parser.BoundExpressionParser;
import org.synchronoss.cpo.parser.ExpressionParser;

/**
 * Created with IntelliJ IDEA. User: dberry Date: 9/10/13 Time: 08:14 AM To change this template use
 * File | Settings | File Templates.
 */
public class CassandraCpoMetaAdapter extends AbstractCpoMetaAdapter {
  private static final Logger logger = LoggerFactory.getLogger(CassandraCpoMetaAdapter.class);
  private static final DataTypeMapEntry<String> defaultDataTypeMapEntry =
      new DataTypeMapEntry<>(
          DataType.Name.VARCHAR.ordinal(), DataType.Name.VARCHAR.toString(), String.class);
  private static final DataTypeMapper dataTypeMapper = initDataTypeMapper();

  /** Constructs a CassandraCpoMetaAdapter */
  public CassandraCpoMetaAdapter() {}

  @Override
  protected DataTypeMapper getDataTypeMapper() {
    return dataTypeMapper;
  }

  @Override
  public ExpressionParser getExpressionParser() throws CpoException {
    return new BoundExpressionParser();
  }

  @Override
  protected void loadCpoAttribute(CpoAttribute cpoAttribute, CtAttribute ctAttribute) {
    super.loadCpoAttribute(cpoAttribute, ctAttribute);

    // cast to the expected subclasses
    CassandraCpoAttribute cassandraAttribute = (CassandraCpoAttribute) cpoAttribute;
    CtCassandraAttribute ctCassandraAttribute = (CtCassandraAttribute) ctAttribute;

    cassandraAttribute.setKeyType(ctCassandraAttribute.getKeyType());
    cassandraAttribute.setValueType(ctCassandraAttribute.getValueType());
  }

  private static DataTypeMapper initDataTypeMapper() {
    logger.debug("Initializing the DataMapper");
    DataTypeMapper dataTypeMapper = new DataTypeMapper(defaultDataTypeMapEntry);

    // CQL DataTypes
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.ASCII.ordinal(), DataType.Name.ASCII.name(), String.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.BIGINT.ordinal(), DataType.Name.BIGINT.name(), long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.BLOB.ordinal(), DataType.Name.BLOB.name(), ByteBuffer.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.BOOLEAN.ordinal(), DataType.Name.BOOLEAN.name(), boolean.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.COUNTER.ordinal(), DataType.Name.COUNTER.name(), long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.CUSTOM.ordinal(), DataType.Name.CUSTOM.toString(), Object.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.DATE.ordinal(), DataType.Name.DATE.name(), LocalDate.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.DECIMAL.ordinal(), DataType.Name.DECIMAL.name(), BigDecimal.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.DOUBLE.ordinal(), DataType.Name.DOUBLE.name(), double.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.DURATION.ordinal(), DataType.Name.DURATION.name(), long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.FLOAT.ordinal(), DataType.Name.FLOAT.name(), float.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.INET.ordinal(), DataType.Name.INET.name(), InetAddress.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataType.Name.INT.ordinal(), DataType.Name.INT.name(), int.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.LIST.ordinal(), DataType.Name.LIST.name(), List.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataType.Name.MAP.ordinal(), DataType.Name.MAP.name(), Map.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataType.Name.SET.ordinal(), DataType.Name.SET.name(), Set.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.SMALLINT.ordinal(), DataType.Name.SMALLINT.name(), short.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.TEXT.ordinal(), DataType.Name.TEXT.name(), String.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.TIME.ordinal(), DataType.Name.TIME.name(), long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.TIMESTAMP.ordinal(), DataType.Name.TIMESTAMP.name(), Date.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.TIMEUUID.ordinal(), DataType.Name.TIMEUUID.name(), UUID.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.TINYINT.ordinal(), DataType.Name.TINYINT.name(), byte.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.TUPLE.ordinal(), DataType.Name.TUPLE.name(), TupleValue.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.UDT.ordinal(), DataType.Name.UDT.name(), UDTValue.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.UUID.ordinal(), DataType.Name.UUID.name(), UUID.class));
    dataTypeMapper.addDataTypeEntry(defaultDataTypeMapEntry);
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataType.Name.VARINT.ordinal(), DataType.Name.VARINT.toString(), BigInteger.class));

    logger.debug("Returning the DataMapper");
    return dataTypeMapper;
  }

  @Override
  protected CpoAttribute createCpoAttribute() {
    return new CassandraCpoAttribute();
  }
}
