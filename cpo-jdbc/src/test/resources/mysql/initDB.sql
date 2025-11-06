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

DROP TABLE  IF EXISTS value_object;
DROP TABLE  IF EXISTS lob_test;

CREATE TABLE IF NOT EXISTS value_object
(
    id                      int(11) primary key,
    name                    varchar(255) unique,
    attr_integer            int(11)      NULL,
    attr_double             double       NULL,
    attr_float              float        NULL,
    attr_varchar            varchar(255) NULL,
    attr_char		        char(255) NULL,
    attr_date               date         NULL,
    attr_time               time         NULL,
    attr_timestamp          timestamp    NULL,
    attr_datetime           datetime     NULL,
    attr_decimal            decimal(10,0) NULL,
    attr_numeric            numeric(10,0) NULL,
    attr_bit                bit(1)       NULL,
    attr_boolean            boolean       NULL,
    attr_tinyint            tinyint(4)   NULL,
    attr_smallint           smallint(6)  NULL,
    attr_bigint             bigint(20)   NULL,
    attr_real               real         NULL
)
ENGINE = 'InnoDB';

CREATE TABLE IF NOT EXISTS lob_test (
       lob_id               INT NOT NULL,
       b_lob                MEDIUMBLOB NULL,
       b_lob2                MEDIUMBLOB NULL,
       c_lob                MEDIUMTEXT NULL
)
ENGINE = 'InnoDB';


COMMIT;
