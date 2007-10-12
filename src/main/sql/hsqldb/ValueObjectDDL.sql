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
DROP TABLE VALUE_OBJECT IF EXISTS;

CREATE TABLE VALUE_OBJECT (
   ID               INTEGER primary key,
   ATTR_INTEGER     INTEGER NULL,
   ATTR_INT         INT NULL,
   ATTR_DOUBLE      DOUBLE NULL,
   ATTR_FLOAT       FLOAT NULL,
   ATTR_VARCHAR     VARCHAR NULL,
   ATTR_VARCHAR_IGNORECASE VARCHAR_IGNORECASE NULL,
   ATTR_CHAR        CHAR NULL,
   ATTR_CHARACTER   CHARACTER NULL,
   ATTR_LONGVARCHAR LONGVARCHAR NULL,
   ATTR_DATE        DATE NULL,
   ATTR_TIME        TIME NULL,
   ATTR_TIMESTAMP   TIMESTAMP NULL,
   ATTR_DATETIME    DATETIME NULL,
   ATTR_DECIMAL     DECIMAL NULL,
   ATTR_NUMERIC     NUMERIC NULL,
   ATTR_BOOL        BOOLEAN NULL,
   ATTR_BIT         BIT NULL,
   ATTR_TINYINT     TINYINT NULL,
   ATTR_SMALLINT    SMALLINT NULL,
   ATTR_BIGINT      BIGINT NULL,
   ATTR_REAL        REAL NULL,
   ATTR_BINARY      BINARY NULL,
   ATTR_VARBINARY   VARBINARY NULL,
   ATTR_LONGVARBINARY LONGVARBINARY NULL,
   ATTR_OTHER       OTHER NULL,
   ATTR_OBJECT      OBJECT NULL
);
