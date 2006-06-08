--/*
-- *  Copyright (C) 2006  David E. Berry
-- *
-- *  This library is free software; you can redistribute it and/or
-- *  modify it under the terms of the GNU Lesser General Public
-- *  License as published by the Free Software Foundation; either
-- *  version 2.1 of the License, or (at your option) any later version.
-- *  
-- *  This library is distributed in the hope that it will be useful,
-- *  but WITHOUT ANY WARRANTY; without even the implied warranty of
-- *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
-- *  Lesser General Public License for more details.
-- *
-- *  You should have received a copy of the GNU Lesser General Public
-- *  License along with this library; if not, write to the Free Software
-- *  Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
-- *  
-- *  A copy of the GNU Lesser General Public License may also be found at 
-- *  http://www.gnu.org/licenses/lgpl.txt
-- */
DROP TABLE IF EXISTS value_object;

CREATE TABLE IF NOT EXISTS value_object (
ID                      int(11) primary key, 
ATTR_INTEGER            int(11)      NULL,
ATTR_INT                int(11)      NULL,
ATTR_DOUBLE             double       NULL,
ATTR_FLOAT              float        NULL,
ATTR_VARCHAR            varchar(255) NULL,
ATTR_VARCHAR_IGNORECASE varchar(255) NULL,
ATTR_CHAR               varchar(255) NULL,
ATTR_DATE               date         NULL,
ATTR_TIME               time         NULL,
ATTR_TIMESTAMP          timestamp    NULL,
ATTR_DATETIME           datetime     NULL,
ATTR_DECIMAL            decimal(10,0) NULL,
ATTR_NUMERIC            decimal(10,0) NULL,
ATTR_BIT                tinyint(1)   NULL,
ATTR_TINYINT            tinyint(4)   NULL,
ATTR_SMALLINT           smallint(6)  NULL,
ATTR_MEDUIMINT          mediumint(9) NULL,
ATTR_BIGINT             bigint(20)   NULL,
ATTR_REAL               double       NULL,
ATTR_BLOB               blob         NULL,
ATTR_TINYBLOB           tinyblob     NULL,
ATTR_MEDIUMBLOB         mediumblob   NULL,
ATTR_LONGBLOB           longblob     NULL,
ATTR_TEXT               text         NULL,
ATTR_TINYTEXT           tinytext     NULL,
ATTR_MEDIUMTEXT         mediumtext   NULL,
ATTR_LONGTEXT           longtext     NULL)
ENGINE = 'InnoDB';

