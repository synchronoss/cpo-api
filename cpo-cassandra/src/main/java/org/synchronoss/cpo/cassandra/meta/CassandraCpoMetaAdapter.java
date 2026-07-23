package org.synchronoss.cpo.cassandra.meta;

/*-
 * [[
 * cassandra
 * ==
 * Copyright (C) 2003 - 2026 Exaxis LLC, Synchronoss Technologies Inc
 * ==
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * ]]
 */

import com.datastax.oss.driver.api.core.data.TupleValue;
import com.datastax.oss.driver.api.core.data.UdtValue;
import com.datastax.oss.driver.api.core.type.DataTypes;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.nio.ByteBuffer;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.synchronoss.cpo.core.CpoException;
import org.synchronoss.cpo.core.meta.AbstractCpoMetaAdapter;
import org.synchronoss.cpo.core.meta.DataTypeMapEntry;
import org.synchronoss.cpo.core.meta.DataTypeMapper;
import org.synchronoss.cpo.core.meta.domain.CpoAttribute;
import org.synchronoss.cpo.core.parser.BoundExpressionParser;
import org.synchronoss.cpo.core.parser.ExpressionParser;
import org.synchronoss.cpo.cpometa.CtAttribute;
import org.synchronoss.cpo.cpometa.CtCassandraAttribute;

/**
 * CassandraCpoMetaAdapter is the Cassandra-specific {@link AbstractCpoMetaAdapter}. It supplies the
 * CQL {@link DataTypeMapper}, the {@link BoundExpressionParser} used to parse Cassandra meta
 * expressions, and builds {@link CassandraCpoAttribute} instances for each mapped attribute.
 *
 * @author dberry
 */
public class CassandraCpoMetaAdapter extends AbstractCpoMetaAdapter {
  private static final Logger logger = LoggerFactory.getLogger(CassandraCpoMetaAdapter.class);

  // Native protocol wire type ids for the parameterized CQL types that DataTypes has no bare
  // singleton constant for (LIST/MAP/SET/TUPLE need element types, UDT needs a keyspace/type
  // definition). These ids are part of the CQL binary protocol spec and are stable across driver
  // versions. See com.datastax.oss.protocol.internal.ProtocolConstants.DataType.
  private static final int PROTOCOL_CODE_UDT = 0x30;

  private static final DataTypeMapEntry<String> defaultDataTypeMapEntry =
      new DataTypeMapEntry<>(DataTypes.TEXT.getProtocolCode(), "VARCHAR", String.class);
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

    // CQL DataTypes, keyed by the CQL native protocol wire type id (DataType#getProtocolCode()).
    // TEXT and VARCHAR are protocol aliases (same wire type id) and collapse onto the default
    // entry above.
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.ASCII.getProtocolCode(), "ASCII", String.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.BIGINT.getProtocolCode(), "BIGINT", long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.BLOB.getProtocolCode(), "BLOB", ByteBuffer.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.BOOLEAN.getProtocolCode(), "BOOLEAN", boolean.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.COUNTER.getProtocolCode(), "COUNTER", long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataTypes.custom("org.apache.cassandra.db.marshal.BytesType").getProtocolCode(),
            "CUSTOM",
            Object.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.DATE.getProtocolCode(), "DATE", LocalDate.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.DECIMAL.getProtocolCode(), "DECIMAL", BigDecimal.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.DOUBLE.getProtocolCode(), "DOUBLE", double.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.DURATION.getProtocolCode(), "DURATION", long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.FLOAT.getProtocolCode(), "FLOAT", float.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.INET.getProtocolCode(), "INET", InetAddress.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.INT.getProtocolCode(), "INT", int.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataTypes.listOf(DataTypes.TEXT).getProtocolCode(), "LIST", List.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataTypes.mapOf(DataTypes.TEXT, DataTypes.TEXT).getProtocolCode(), "MAP", Map.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataTypes.setOf(DataTypes.TEXT).getProtocolCode(), "SET", Set.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.SMALLINT.getProtocolCode(), "SMALLINT", short.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.TIME.getProtocolCode(), "TIME", long.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.TIMESTAMP.getProtocolCode(), "TIMESTAMP", Instant.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.TIMEUUID.getProtocolCode(), "TIMEUUID", UUID.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.TINYINT.getProtocolCode(), "TINYINT", byte.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(
            DataTypes.tupleOf(DataTypes.TEXT).getProtocolCode(), "TUPLE", TupleValue.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(PROTOCOL_CODE_UDT, "UDT", UdtValue.class));
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.UUID.getProtocolCode(), "UUID", UUID.class));
    dataTypeMapper.addDataTypeEntry(defaultDataTypeMapEntry);
    dataTypeMapper.addDataTypeEntry(
        new DataTypeMapEntry<>(DataTypes.VARINT.getProtocolCode(), "VARINT", BigInteger.class));

    logger.debug("Returning the DataMapper");
    return dataTypeMapper;
  }

  @Override
  protected CpoAttribute createCpoAttribute() {
    return new CassandraCpoAttribute();
  }
}
