-- /*
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

----------------------------------------------
-- CPO_QUERY_PARAMETER
----------------------------------------------
CREATE TABLE CPO_QUERY_PARAMETER (
       attribute_id         VARCHAR(36) NOT NULL,
       query_id             VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       param_type                 VARCHAR(4) DEFAULT 'IN' NULL,
       userid               varchar(50), 
       createdate           date
   ); 

----------------------------------------------
-- CPO_QUERY_PARAMETER_REV
----------------------------------------------
CREATE TABLE CPO_QUERY_PARAMETER_REV (
       attribute_id         VARCHAR(36) NOT NULL,
       query_id             VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       param_type           VARCHAR(4) DEFAULT 'IN' NULL,
       userid               varchar(50), 
       createdate           date,
       revision             NUMERIC
   ); 

----------------------------------------------
-- CPO_ATTRIBUTE_MAP
----------------------------------------------
CREATE TABLE CPO_ATTRIBUTE_MAP (
       attribute_id         VARCHAR(36) NOT NULL PRIMARY KEY,
       class_id             VARCHAR(36) NOT NULL,
       column_name          VARCHAR(40) NOT NULL,
       column_type          VARCHAR(40) NOT NULL,
       attribute            VARCHAR(40) NOT NULL,
       transform_class      VARCHAR(1023) NULL,
       db_table             VARCHAR(40) NULL,
       db_column            VARCHAR(40) NULL,
       userid               varchar(50), 
       createdate           date
);


----------------------------------------------
-- CPO_ATTRIBUTE_MAP_REV
----------------------------------------------
CREATE TABLE CPO_ATTRIBUTE_MAP_REV (
       attribute_id         VARCHAR(36) NOT NULL,
       class_id             VARCHAR(36) NOT NULL,
       column_name          VARCHAR(40) NOT NULL,
       column_type          VARCHAR(40) NOT NULL,
       attribute            VARCHAR(40) NOT NULL,
       transform_class      VARCHAR(1023) NULL,
       db_table             VARCHAR(40) NULL,
       db_column            VARCHAR(40) NULL,
       userid               varchar(50), 
       createdate           date,
       revision             NUMERIC
);

----------------------------------------------
-- CPO_QUERY
----------------------------------------------
CREATE TABLE CPO_QUERY (
       query_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       group_id             VARCHAR(36) NOT NULL,
       text_id              VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       stored_proc          VARCHAR(1) DEFAULT 'N' NULL,
       userid               varchar(50), 
       createdate           date
);

----------------------------------------------
-- CPO_QUERY_REV
----------------------------------------------
CREATE TABLE CPO_QUERY_REV (
       query_id             VARCHAR(36) NOT NULL,
       group_id             VARCHAR(36) NOT NULL,
       text_id              VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       stored_proc          VARCHAR(1) DEFAULT 'N' NULL,
       userid               varchar(50), 
       createdate           date,
       revision             NUMERIC
);

----------------------------------------------
-- CPO_QUERY_GROUP
----------------------------------------------
CREATE TABLE CPO_QUERY_GROUP (
       group_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       class_id             VARCHAR(36) NOT NULL,
       group_type           VARCHAR(10) NOT NULL,
       name                 VARCHAR(50) NULL,
       userid               varchar(50), 
       createdate           date
);

----------------------------------------------
-- CPO_QUERY_GROUP_REV
----------------------------------------------
CREATE TABLE CPO_QUERY_GROUP_REV (
       group_id             VARCHAR(36) NOT NULL,
       class_id             VARCHAR(36) NOT NULL,
       group_type           VARCHAR(10) NOT NULL,
       name                 VARCHAR(50) NULL,
       userid               varchar(50), 
       createdate           date,
       revision             numeric
);

----------------------------------------------
-- CPO_QUERY_TEXT
----------------------------------------------
CREATE TABLE CPO_QUERY_TEXT (
       text_id              VARCHAR(36) NOT NULL PRIMARY KEY,
       sql_text             VARCHAR(4000) NULL,
       description          VARCHAR(1023) NULL,
       userid               varchar(50), 
       createdate           date
);

----------------------------------------------
-- CPO_QUERY_TEXT_REV
----------------------------------------------
CREATE TABLE CPO_QUERY_TEXT_REV (
       text_id              VARCHAR(36) NOT NULL PRIMARY KEY,
       sql_text             VARCHAR(4000) NULL,
       description          VARCHAR(1023) NULL,
       userid               varchar(50), 
       createdate           date,
       revision             numeric
);

----------------------------------------------
-- CPO_CLASS
----------------------------------------------
CREATE TABLE CPO_CLASS (
       class_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       name                 VARCHAR(1023) NOT NULL,
       userid               varchar(50), 
       createdate           date
);

----------------------------------------------
-- CPO_CLASS
----------------------------------------------
CREATE TABLE CPO_CLASS_REV (
       class_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       name                 VARCHAR(1023) NOT NULL,
       userid               varchar(50), 
       createdate           date,
       revision             numeric
);

----------------------------------------------
-- Primary Keys
----------------------------------------------
ALTER TABLE CPO_QUERY_PARAMETER
        ADD CONSTRAINT PK_CQP_ATTR_QRY_SEQ PRIMARY KEY (attribute_id, query_id, seq_no);

----------------------------------------------
-- Foreign Keys
----------------------------------------------
ALTER TABLE CPO_ATTRIBUTE_MAP
       ADD CONSTRAINT FK_CAM_CLASS_ID FOREIGN KEY (class_id) REFERENCES CPO_CLASS(class_id);

ALTER TABLE CPO_ATTRIBUTE_MAP
       ADD CONSTRAINT UNIQUE_ATTR_CLASS UNIQUE(attribute_id,class_id);

ALTER TABLE CPO_QUERY
       ADD CONSTRAINT FK_CQ_GROUP_ID FOREIGN KEY (group_id) REFERENCES CPO_QUERY_GROUP(group_id);

ALTER TABLE CPO_QUERY
       ADD CONSTRAINT FK_CQ_TEXT_ID FOREIGN KEY (text_id) REFERENCES CPO_QUERY_TEXT(text_id);

ALTER TABLE CPO_QUERY_GROUP
       ADD CONSTRAINT FK_CQG_CLASS_ID FOREIGN KEY (class_id) REFERENCES CPO_CLASS(class_id);

ALTER TABLE CPO_QUERY_PARAMETER
       ADD CONSTRAINT FK_CQP_ATTRIBUTE_ID FOREIGN KEY (attribute_id) REFERENCES CPO_ATTRIBUTE_MAP(attribute_id);

ALTER TABLE CPO_QUERY_PARAMETER
       ADD CONSTRAINT FK_CQP_QUERY_ID FOREIGN KEY (query_id) REFERENCES CPO_QUERY(query_id);

COMMIT;




