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

----------------------------------------------
-- CPO_QUERY_PARAMETER
----------------------------------------------
DROP TABLE  IF EXISTS cpo_query_parameter
;
CREATE TABLE IF NOT EXISTS cpo_query_parameter (
       attribute_id         VARCHAR(36) NOT NULL,
       query_id             VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       param_type           VARCHAR(4) DEFAULT 'IN' NULL,
       userid               varchar(50), 
       createdate           date
   ) 
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_QUERY_PARAMETER_REV
----------------------------------------------
DROP TABLE  IF EXISTS cpo_query_parameter_rev
;
CREATE TABLE IF NOT EXISTS cpo_query_parameter_rev (
       attribute_id         VARCHAR(36) NOT NULL,
       query_id             VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       param_type           VARCHAR(4) DEFAULT 'IN' NULL,
       userid               varchar(50), 
       createdate           date,
       revision             NUMERIC
   ) 
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_ATTRIBUTE_MAP
----------------------------------------------
DROP TABLE IF EXISTS cpo_attribute_map
;
CREATE TABLE IF NOT EXISTS cpo_attribute_map (
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
)
ENGINE = 'InnoDB';


----------------------------------------------
-- CPO_ATTRIBUTE_MAP_REV
----------------------------------------------
DROP TABLE IF EXISTS cpo_attribute_map_rev
;
CREATE TABLE IF NOT EXISTS cpo_attribute_map_rev (
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
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_QUERY
----------------------------------------------
DROP  TABLE IF EXISTS cpo_query
;
CREATE TABLE IF NOT EXISTS cpo_query (
       query_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       group_id             VARCHAR(36) NOT NULL,
       text_id              VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       stored_proc          VARCHAR(1) DEFAULT 'N' NULL,
       userid               varchar(50), 
       createdate           date
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_QUERY_REV
----------------------------------------------
DROP  TABLE IF EXISTS cpo_query_rev
;
CREATE TABLE IF NOT EXISTS cpo_query_rev (
       query_id             VARCHAR(36) NOT NULL,
       group_id             VARCHAR(36) NOT NULL,
       text_id              VARCHAR(36) NOT NULL,
       seq_no               NUMERIC(9) NOT NULL,
       stored_proc          VARCHAR(1) DEFAULT 'N' NULL,
       userid               varchar(50), 
       createdate           date,
       revision             NUMERIC
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_QUERY_GROUP
----------------------------------------------
DROP TABLE IF EXISTS cpo_query_group
;
CREATE TABLE IF NOT EXISTS cpo_query_group (
       group_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       class_id             VARCHAR(36) NOT NULL,
       group_type           VARCHAR(10) NOT NULL,
       name                 VARCHAR(50) NULL,
       userid               varchar(50), 
       createdate           date
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_QUERY_GROUP_REV
----------------------------------------------
DROP TABLE IF EXISTS cpo_query_group_rev
;
CREATE TABLE IF NOT EXISTS cpo_query_group_rev (
       group_id             VARCHAR(36) NOT NULL,
       class_id             VARCHAR(36) NOT NULL,
       group_type           VARCHAR(10) NOT NULL,
       name                 VARCHAR(50) NULL,
       userid               varchar(50), 
       createdate           date,
       revision             numeric
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_QUERY_TEXT
----------------------------------------------
DROP TABLE IF EXISTS cpo_query_text
 ;

CREATE TABLE IF NOT EXISTS cpo_query_text (
       text_id              VARCHAR(36) NOT NULL PRIMARY KEY,
       sql_text             VARCHAR(8000) NULL,
       description          VARCHAR(1023) NULL,
       userid               varchar(50), 
       createdate           date
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_QUERY_TEXT_REV
----------------------------------------------
DROP TABLE IF EXISTS cpo_query_text_rev
 ;

CREATE TABLE IF NOT EXISTS cpo_query_text_rev (
       text_id              VARCHAR(36) NOT NULL PRIMARY KEY,
       sql_text             VARCHAR(8000) NULL,
       description          VARCHAR(1023) NULL,
       userid               varchar(50), 
       createdate           date,
       revision             numeric
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_CLASS
----------------------------------------------
DROP  TABLE IF EXISTS cpo_class 
;

CREATE TABLE IF NOT EXISTS cpo_class (
       class_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       name                 VARCHAR(1023) NOT NULL,
       userid               varchar(50), 
       createdate           date
)
ENGINE = 'InnoDB';

----------------------------------------------
-- CPO_CLASS
----------------------------------------------
DROP  TABLE IF EXISTS cpo_class_rev 
;

CREATE TABLE IF NOT EXISTS cpo_class_rev (
       class_id             VARCHAR(36) NOT NULL PRIMARY KEY,
       name                 VARCHAR(1023) NOT NULL,
       userid               varchar(50), 
       createdate           date,
       revision             numeric
)
ENGINE = 'InnoDB';

----------------------------------------------
-- Multi Part Primary Keys
----------------------------------------------

ALTER TABLE cpo_query_parameter 
       ADD CONSTRAINT PK_CQP_ATTR_QUERY_SEQ Primary KEY (attribute_id, query_id, seq_no);



----------------------------------------------
-- Foreign Keys
----------------------------------------------
ALTER TABLE cpo_query_group
       ADD CONSTRAINT FK_CQG_CLASS_ID FOREIGN KEY (class_id) REFERENCES cpo_class(class_id);

ALTER TABLE cpo_query_parameter
       ADD CONSTRAINT FK_ATTRIBUTE_ID FOREIGN KEY (attribute_id) REFERENCES cpo_attribute_map(attribute_id);

ALTER TABLE cpo_query_parameter
       ADD CONSTRAINT FK_QUERY_ID FOREIGN KEY (query_id) REFERENCES cpo_query(query_id);

ALTER TABLE cpo_query
       ADD CONSTRAINT FK_GROUP_ID FOREIGN KEY (group_id) REFERENCES cpo_query_group(group_id);

ALTER TABLE cpo_query
       ADD CONSTRAINT FK_CQ_TEXT_ID FOREIGN KEY (text_id) REFERENCES cpo_query_text(text_id);

ALTER TABLE cpo_attribute_map
       ADD CONSTRAINT UNIQUE_ATTR_CLASS UNIQUE(attribute_id,class_id);

ALTER TABLE cpo_attribute_map
       ADD CONSTRAINT FK_CAM_CLASS_ID FOREIGN KEY (class_id) REFERENCES cpo_class(class_id);

COMMIT;




