--
-- Don't forget to add your table_prefix if you have one.
--
alter table cpo_query
 add sql_text VARCHAR(8000);

alter table cpo_query
 add     description VARCHAR(1023) ;

alter table cpo_query drop column stored_proc;

update cpo_query cq set cq.sql_text = (select cqt.sql_text from cpo_query_text cqt where cq.text_id = cqt.text_id),
cq.description = (select cqt.description from cpo_query_text cqt where cq.text_id = cqt.text_id);

