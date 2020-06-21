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

DROP TABLE  IF EXISTS VALUE_OBJECT;
DROP TABLE  IF EXISTS LOB_TEST;

CREATE TABLE value_object (
ID                      integer primary key
,ATTR_BOOL            	 boolean  NULL
,ATTR_INTEGER            integer      NULL
,ATTR_INT                int      NULL
,ATTR_DOUBLE             double precision       NULL
,ATTR_FLOAT              real        NULL
,ATTR_VARCHAR            varchar(255) NULL
,ATTR_VARCHAR_IGNORECASE varchar(255) NULL
,ATTR_CHAR               char(255) NULL
,ATTR_CHARACTER          character(255) NULL
,ATTR_DATE               date         NULL
,ATTR_DATETIME			     timestamp	  NULL
,ATTR_TIME               time         NULL
,ATTR_TIME_ZONE          time with time zone  NULL
,ATTR_TIMESTAMP          timestamp    NULL
,ATTR_TIMESTAMP_ZONE     timestamp  with time zone  NULL
,ATTR_DECIMAL            decimal(10,0) NULL
,ATTR_NUMERIC            numeric(10) NULL
,ATTR_SMALLINT           smallint NULL
,ATTR_BIGINT             bigint NULL
,ATTR_REAL               double precision     NULL
,ATTR_LONGTEXT           text     NULL
--,ATTR_SMALLSERIAL        smallserial
--,ATTR_SERIAL             serial
--,ATTR_BIGSERIAL          bigserial
);

CREATE TABLE LOB_TEST (
  LOB_ID               INT NOT NULL
  ,B_LOB                BYTEA NULL
  ,B_LOB2               BYTEA NULL
  ,C_LOB                text NULL
);
