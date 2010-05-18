-- add back cpo_query_text

CREATE TABLE IF NOT EXISTS cpo_query_text (
       text_id              VARCHAR(36) NOT NULL PRIMARY KEY,
       sql_text             VARCHAR(8000) NULL,
       description          VARCHAR(1023) NULL,
       userid               varchar(50),
       createdate           date
)
ENGINE=InnoDB
default character set = utf8;

CREATE TABLE IF NOT EXISTS cpo_query_text_rev (
       text_id              VARCHAR(36) NOT NULL PRIMARY KEY,
       sql_text             VARCHAR(8000) NULL,
       description          VARCHAR(1023) NULL,
       userid               varchar(50),
       createdate           date,
       revision             numeric
)
ENGINE=InnoDB
default character set = utf8;

alter table cpo_query
 add column text_id VARCHAR(36) NOT NULL;

alter table cpo_query_rev
 add column text_id VARCHAR(36) NOT NULL;

update cpo_query set text_id = query_id;

insert into cpo_query_text (text_id, sql_text, description, userid, createdate)
 (select text_id, sql_text, description, userid, now() from cpo_query);

ALTER TABLE cpo_query
       ADD CONSTRAINT FK_CQ_TEXT_ID FOREIGN KEY (text_id) REFERENCES cpo_query_text(text_id);

-- Once you are comfortable that it worked,
-- run the deletes

-- alter table cpo_query
--  drop column sql_text,
--  drop column description;

