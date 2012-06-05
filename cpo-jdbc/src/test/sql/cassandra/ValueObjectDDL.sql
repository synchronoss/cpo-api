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

USE cpo;

CREATE COLUMNFAMILY value_object (
id                      varint primary key, 
attr_integer            int,
attr_int                int,
attr_double             double,
attr_float              float,
attr_varchar            varchar,
attr_varchar_ignorecase varchar,
attr_char               text,
attr_character          text,
attr_date               timestamp,
attr_time               timestamp,
attr_timestamp          timestamp,
attr_datetime           timestamp,
attr_decimal            decimal,
attr_numeric            decimal,
attr_bit                boolean,
attr_bool               boolean,
attr_boolean            boolean,
attr_tinyint            int,
attr_smallint           int,
attr_mediumint          int,
attr_bigint             bigint,
attr_real               double,
attr_blob               blob,
attr_tinyblob           blob,
attr_mediumblob         blob,
attr_longblob           blob,
attr_text               text,
attr_tinytext           text,
attr_mediumtext         text,
attr_longtext           text)

