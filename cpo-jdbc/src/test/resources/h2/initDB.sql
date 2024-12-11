--
-- Copyright (C) 2003-2012 David E. Berry
--
-- This library is free software; you can redistribute it and/or
-- modify it under the terms of the GNU Lesser General Public
-- License as published by the Free Software Foundation; either
-- version 2.1 of the License, or (at your option) any later version.
--
-- This library is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
-- Lesser General Public License for more details.
--
-- You should have received a copy of the GNU Lesser General Public
-- License along with this library; if not, write to the Free Software
-- Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
--
-- A copy of the GNU Lesser General Public License may also be found at
-- http://www.gnu.org/licenses/lgpl.txt
--

DROP TABLE IF EXISTS value_object;
DROP TABLE IF EXISTS lob_test;

CREATE TABLE IF NOT EXISTS value_object (
  id                      int           PRIMARY KEY
  ,attr_int                int           NULL
  ,attr_integer            integer       NULL
  ,attr_bit                boolean       NULL
  ,attr_bool                boolean       NULL
  ,attr_boolean            boolean       NULL
  ,attr_tinyint            tinyint       NULL
  ,attr_smallint           smallint      NULL
  ,attr_bigint             bigint        NULL
  ,attr_dec                dec           NULL
  ,attr_decimal            decimal(10,0) NULL
  ,attr_numeric            numeric(10,0) NULL
  ,attr_double             double precision NULL
  ,attr_floatdouble        float(40)     NULL
  ,attr_real               real          NULL
  ,attr_floatreal          float(20)     NULL
  ,attr_time               time          NULL
  ,attr_time_without_timezone time without time zone NULL
  ,attr_time_with_timezone time with time zone NULL
  ,attr_date               date          NULL
  ,attr_datetime           timestamp     NULL
  ,attr_timestamp          timestamp     NULL
  ,attr_timestamp_without_timezone    timestamp without time zone NULL
  ,attr_timestamp_with_timezone  timestamp with time zone NULL
  ,attr_binary             binary        NULL
  ,attr_varbinary          varbinary     NULL
  ,attr_binary_varying     binary varying NULL
  ,attr_other              other         NULL
  ,attr_object             object         NULL
  ,attr_java_object        java_object         NULL
  ,attr_varchar            varchar(255)  NULL
  ,attr_character_varying  character varying(255)  NULL
  ,attr_varchar_casesensitive varchar_casesensitive(255)  NULL
  ,attr_varchar_ignorecase varchar_ignorecase(255)  NULL
  ,attr_char		            char(255)     NULL
  ,attr_character		      character(255) NULL
  ,attr_nchar		          nchar(255)     NULL
  ,attr_blob               blob          NULL
  ,attr_binary_large_object  binary large object NULL
  ,attr_clob               clob          NULL
  ,attr_character_large_object character large object NULL
  ,attr_nclob              nclob         NULL
  ,attr_uuid               uuid          NULL
  ,attr_array              boolean array         NULL
  ,attr_enum               enum('club','diamonds','hearts','spades')
  ,attr_geometry           geometry      NULL
  ,attr_json               json          NULL
  ,attr_interval_year      INTERVAL YEAR
  ,attr_interval_month     INTERVAL MONTH NULL
  ,attr_interval_day       INTERVAL DAY NULL
  ,attr_interval_hour      INTERVAL HOUR NULL
  ,attr_interval_minute    INTERVAL MINUTE NULL
  ,attr_interval_second    INTERVAL SECOND NULL
  ,attr_interval_year_to_month INTERVAL YEAR TO MONTH NULL
  ,attr_interval_day_to_hour   INTERVAL DAY TO HOUR NULL
  ,attr_interval_day_to_minute INTERVAL DAY TO MINUTE NULL
  ,attr_interval_day_to_second INTERVAL DAY TO SECOND NULL
  ,attr_interval_hour_to_minute INTERVAL HOUR TO MINUTE NULL
  ,attr_interval_hour_to_second INTERVAL HOUR TO SECOND NULL
  ,attr_interval_minute_to_second INTERVAL MINUTE TO SECOND NULL
);

CREATE TABLE IF NOT EXISTS lob_test (
       LOB_ID               INT NOT NULL
       ,B_LOB                BLOB NULL
       ,B_LOB2               BLOB NULL
       ,C_LOB                CLOB NULL
);
