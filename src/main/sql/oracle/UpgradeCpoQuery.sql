--
-- Don't forget to add your table_prefix if you have one.
--
alter table test_cpo_query
 add( sql_text VARCHAR2(4000),
      description VARCHAR2(1023) );

alter table test_cpo_query drop column stored_proc;

update test_cpo_query cq set cq.sql_text = (select cqt.sql_text from test_cpo_query_text cqt where cq.text_id = cqt.text_id),
cq.description = (select cqt.description from test_cpo_query_text cqt where cq.text_id = cqt.text_id);

