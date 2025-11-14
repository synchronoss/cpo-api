-- #%L
-- jdbc
-- %%
-- Copyright (C) 2003 - 2025 David E. Berry
-- %%
-- This program is free software: you can redistribute it and/or modify
-- it under the terms of the GNU Lesser General Public License as
-- published by the Free Software Foundation, either version 3 of the
-- License, or (at your option) any later version.
-- 
-- This program is distributed in the hope that it will be useful,
-- but WITHOUT ANY WARRANTY; without even the implied warranty of
-- MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
-- GNU General Lesser Public License for more details.
-- 
-- You should have received a copy of the GNU General Lesser Public
-- License along with this program.  If not, see
-- <http://www.gnu.org/licenses/lgpl-3.0.html>.
-- #L%DROP TABLE  IF EXISTS value_object;
DROP TABLE  IF EXISTS lob_test;

CREATE TABLE IF NOT EXISTS value_object (
    ID              int(11) primary key,
    ATTR_CHAR		char(255) NULL,
    ATTR_CHARACTER	char(255) NULL,
    ATTR_DATE       date         NULL,
    ATTR_DECIMAL    decimal(10,0) NULL,
    ATTR_INTEGER    int(11)      NULL,
    ATTR_NUMERIC    decimal(10,0) NULL,
    ATTR_SMALLINT   smallint(6)  NULL,
    ATTR_TIMESTAMP  timestamp    NULL,
    ATTR_VARCHAR    varchar(255) NULL,
    ATTR_BIT        bit(1)       NULL,
    ATTR_BOOL       bool         NULL,
    ATTR_DATETIME   datetime     NULL,
    ATTR_BIGINT     bigint(20)   NULL,
    ATTR_DOUBLE     double       NULL,
    ATTR_REAL       double       NULL,
    ATTR_FLOAT      float        NULL,
    ATTR_TIME       time         NULL
)
ENGINE = 'InnoDB';

CREATE TABLE IF NOT EXISTS lob_test (
    LOB_ID  INT NOT NULL,
    B_LOB   MEDIUMBLOB NULL,
    B_LOB2  MEDIUMBLOB NULL,
    C_LOB   MEDIUMTEXT NULL
)
ENGINE = 'InnoDB';

COMMIT;
