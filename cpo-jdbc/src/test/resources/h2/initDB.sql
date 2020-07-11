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

DROP TABLE value_object IF EXISTS ;
DROP TABLE lob_test IF EXISTS ;

CREATE TABLE IF NOT EXISTS value_object (
id                      int           UNIQUE,
attr_int                int           NULL,
attr_int4               int4          NULL,
attr_signed             signed        NULL,
attr_integer            integer       NULL,
attr_mediumint          mediumint     NULL,
attr_bit                bit           NULL,
attr_bool               bool          NULL,
attr_boolean            boolean       NULL,
attr_tinyint            tinyint       NULL,
attr_int2               int2          NULL,
attr_year               year          NULL,
attr_smallint           smallint      NULL,
attr_int8               int8          NULL,
attr_bigint             bigint        NULL,
attr_identity           identity      PRIMARY KEY,
attr_dec                dec           NULL,
attr_decimal            decimal(10,0) NULL,
attr_number             number        NULL,
attr_numeric            numeric(10,0) NULL,
attr_double             double        NULL,
attr_floatdouble        float(40)     NULL,
attr_float8             float8        NULL,
attr_real               real          NULL,
attr_floatreal          float(20)     NULL,
attr_float4             float4        NULL,
attr_time               time          NULL,
attr_time_without_timezone time without time zone NULL,
attr_time_with_timezone time with time zone NULL,
attr_date               date          NULL,
attr_datetime           datetime      NULL,
attr_timestamp          timestamp     NULL,
attr_timestamp_without_timezone    timestamp without time zone NULL,
attr_smalldatetime      smalldatetime NULL,
attr_timestamp_with_timezone  timestamp with time zone NULL,
attr_binary             binary        NULL,
attr_varbinary          varbinary     NULL,
attr_binary_varying     binary varying NULL,
attr_longvarbinary      longvarbinary  NULL,
attr_raw                raw           NULL,
attr_bytea              bytea         NULL,
attr_other              other         NULL,
attr_varchar            varchar(255)  NULL,
attr_character_varying  character varying(255)  NULL,
attr_longvarchar        longvarchar(2048)  NULL,
attr_varchar2           varchar2(255)  NULL,
attr_nvarchar           nvarchar(255)  NULL,
attr_nvarchar2          nvarchar2(255)  NULL,
attr_varchar_casesensitive varchar_casesensitive(255)  NULL,
attr_varchar_ignorecase varchar_ignorecase(255)  NULL,
attr_char		            char(255)     NULL,
attr_character		      character(255)     NULL,
attr_nchar		          nchar(255)     NULL,
attr_blob               blob          NULL,
attr_binary_large_object  binary large object NULL,
attr_tinyblob           tinyblob      NULL,
attr_mediumblob         mediumblob    NULL,
attr_longblob           longblob      NULL,
attr_image              image         NULL,
attr_oid                oid           NULL,
attr_clob               clob          NULL,
attr_character_large_object character large object NULL,
attr_text               text          NULL,
attr_tinytext           tinytext      NULL,
attr_mediumtext         mediumtext    NULL,
attr_longtext           longtext      NULL,
attr_ntext              ntext         NULL,
attr_nclob              nclob         NULL,
attr_uuid               uuid          NULL,
attr_array              array         NULL,
attr_enum               enum('club','diamonds','hearts','spades'),
attr_geometry           geometry      NULL,
attr_geometry2          geometry      NULL,
attr_json               json          NULL,
attr_interval_year      INTERVAL YEAR NULL,
attr_interval_month     INTERVAL MONTH NULL,
attr_interval_day       INTERVAL DAY NULL,
attr_interval_hour      INTERVAL HOUR NULL,
attr_interval_minute    INTERVAL MINUTE NULL,
attr_interval_second    INTERVAL SECOND NULL,
attr_interval_year_to_month INTERVAL YEAR TO MONTH NULL,
attr_interval_day_to_hour   INTERVAL DAY TO HOUR NULL,
attr_interval_day_to_minute INTERVAL DAY TO MINUTE NULL,
attr_interval_day_to_second INTERVAL DAY TO SECOND NULL,
attr_interval_hour_to_minute INTERVAL HOUR TO MINUTE NULL,
attr_interval_hour_to_second INTERVAL HOUR TO SECOND NULL,
attr_interval_minute_to_second INTERVAL MINUTE TO SECOND NULL
);

CREATE TABLE IF NOT EXISTS lob_test (
       LOB_ID               INT NOT NULL
       ,B_LOB                BLOB NULL
       ,B_LOB2               BLOB NULL
       ,C_LOB                CLOB NULL
);
