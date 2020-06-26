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
id                      int primary key,
attr_integer            int      NULL,
attr_int                int      NULL,
attr_double             double       NULL,
attr_float              float        NULL,
attr_varchar            varchar(255) NULL,
attr_varchar_ignorecase varchar(255) NULL,
attr_char		char(255) NULL,
attr_character		char(255) NULL,
attr_date               date         NULL,
attr_time               time         NULL,
attr_timestamp          timestamp    NULL,
attr_datetime           datetime     NULL,
attr_decimal            decimal(10,0) NULL,
attr_numeric            decimal(10,0) NULL,
attr_bit                bit       NULL,
attr_bool               bool       NULL,
attr_boolean            boolean       NULL,
attr_tinyint            tinyint   NULL,
attr_smallint           smallint  NULL,
attr_mediumint          mediumint NULL,
attr_bigint             bigint   NULL,
attr_real               double       NULL,
attr_blob               blob         NULL,
attr_tinyblob           tinyblob     NULL,
attr_mediumblob         mediumblob   NULL,
attr_longblob           longblob     NULL,
attr_text               text         NULL,
attr_tinytext           tinytext     NULL,
attr_mediumtext         mediumtext   NULL,
attr_longtext           longtext     NULL
);

CREATE TABLE IF NOT EXISTS lob_test (
       LOB_ID               INT NOT NULL
       ,B_LOB                BLOB NULL
       ,B_LOB2               BLOB NULL
       ,C_LOB                CLOB NULL
);