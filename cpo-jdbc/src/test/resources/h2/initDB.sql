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

CREATE TABLE IF NOT EXISTS value_object
(
    id             int PRIMARY KEY,
    attr_char      char(255)        NULL,
    attr_character character(255)   NULL,
    attr_date      date             NULL,
    attr_decimal   decimal(10, 0)   NULL,
    attr_integer   integer          NULL,
    attr_numeric   numeric(10, 0)   NULL,
    attr_smallint  smallint         NULL,
    attr_timestamp timestamp        NULL,
    attr_varchar   varchar(255)     NULL,
    attr_bit       boolean          NULL,
    attr_bool      boolean          NULL,
    attr_datetime  timestamp        NULL,
    attr_tinyint   tinyint          NULL,
    attr_bigint    bigint           NULL,
    attr_double    double precision NULL,
    attr_real      real             NULL,
    attr_float     float(20)        NULL,
    attr_time      time             NULL,
    attr_binary    binary           NULL,
    attr_varbinary varbinary        NULL
);

CREATE TABLE IF NOT EXISTS lob_test
(
    LOB_ID INT  NOT NULL,
    B_LOB  BLOB NULL,
    B_LOB2 BLOB NULL,
    C_LOB  CLOB NULL
);
