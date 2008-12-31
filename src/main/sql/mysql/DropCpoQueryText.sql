--
-- Don't forget to add your table_prefix if you have one.
--

alter table cpo_query drop foreign key FK_CQ_TEXT_ID;

alter table cpo_query drop column text_id;

drop table cpo_query_text;

drop table cpo_query_text_rev;
