CREATE KEYSPACE cpokeyspace WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 3 };

USE cpokeyspace;

CREATE TABLE value_object (
  id int PRIMARY KEY,
  attr_ascii ascii,
  attr_bigint bigint,
  attr_blob blob,
  attr_blob2 blob,
  attr_bool boolean,
  attr_decimal decimal,
  attr_double double,
  attr_float float,
  attr_inet inet,
  attr_int int,
  attr_text text,
  attr_timestamp timestamp,
  attr_timeuuid timeuuid,
  attr_uuid uuid,
  attr_varchar text,
  attr_varint varint
);
