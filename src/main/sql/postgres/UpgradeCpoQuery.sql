--
-- Don't forget to add your table_prefix if you have one.
--
alter table test_cpo_query
 add sql_text VARCHAR(8000),
 add description VARCHAR(1023);

alter table test_cpo_query drop column stored_proc;

update test_cpo_query set sql_text = (select sql_text from test_cpo_query_text where test_cpo_query.text_id = test_cpo_query_text.text_id),
description = (select description from test_cpo_query_text where test_cpo_query.text_id = test_cpo_query_text.text_id);

