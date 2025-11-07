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

CREATE TABLE VALUE_OBJECT (
    ID                      int primary key
    ,ATTR_CHAR               varchar(255) NULL
    ,ATTR_CHARACTER          varchar(255) NULL
    ,ATTR_DATE               date         NULL
    ,ATTR_DECIMAL            decimal(10,0) NULL
    ,ATTR_INTEGER            int      NULL
    ,ATTR_NUMERIC            decimal(10,0) NULL
    ,ATTR_SMALLINT           smallint  NULL
    ,ATTR_TIMESTAMP          timestamp    NULL
    ,ATTR_VARCHAR            varchar(255) NULL
    ,ATTR_BIT            	 char(1)  NULL
    ,ATTR_BOOL            	 char(1)  NULL
    ,ATTR_DATETIME			     timestamp	  NULL
    ,ATTR_BIGINT             numeric NULL
    ,ATTR_DOUBLE             double precision       NULL
    ,ATTR_REAL               double precision     NULL
    ,ATTR_FLOAT              float        NULL
)
;

CREATE TABLE LOB_TEST (
       LOB_ID               INT NOT NULL
       ,B_LOB                BLOB NULL
       ,B_LOB2               BLOB NULL
       ,C_LOB                CLOB NULL
)
;
