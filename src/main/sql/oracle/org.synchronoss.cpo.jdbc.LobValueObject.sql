delete from TEST_CPO_QUERY_PARAMETER  where query_id in (select distinct query_id from TEST_CPO_QUERY where group_id in (select distinct group_id from TEST_CPO_QUERY_GROUP where class_id=(select class_id from TEST_CPO_CLASS where name='org.synchronoss.cpo.jdbc.LobValueObject')))
/
delete from TEST_CPO_QUERY where group_id in (select distinct group_id from TEST_CPO_QUERY_GROUP where class_id=(select class_id from TEST_CPO_CLASS where name='org.synchronoss.cpo.jdbc.LobValueObject'))
/
delete from TEST_CPO_QUERY_GROUP where class_id=(select class_id from TEST_CPO_CLASS where name='org.synchronoss.cpo.jdbc.LobValueObject')
/
delete from TEST_CPO_ATTRIBUTE_MAP where class_id=(select class_id from TEST_CPO_CLASS where name='org.synchronoss.cpo.jdbc.LobValueObject')
/
delete from TEST_CPO_CLASS where name='org.synchronoss.cpo.jdbc.LobValueObject'
/
insert into TEST_cpo_class (class_id, name) values ('2563f7ef-0a07-3238-018c-6cbc1b949eab','org.synchronoss.cpo.jdbc.LobValueObject')
/
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','2563f7ef-0a07-3238-018c-6cbc1b949eab','B_LOB','bLob','BLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformGZipBlob')
/
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','2563f7ef-0a07-3238-018c-6cbc1b949eab','B_LOB2','bLob2','BLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformBlob')
/
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','2563f7ef-0a07-3238-018c-6cbc1b949eab','C_LOB','cLob','CLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformClob')
/
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2563f7ef-0a07-3238-018c-6cbc1b949eab','LOB_ID','lobId','NUMERIC',null,null,null)
/
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256c228c-0a07-3238-018c-6cbc8f38ab75','2563f7ef-0a07-3238-018c-6cbc1b949eab','CREATE','createLVO')
/
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('256e3097-0a07-3238-018c-6cbcbe7cde0b','256c228c-0a07-3238-018c-6cbc8f38ab75','0','insert into lob_test (lob_id, b_lob, c_lob, b_lob2) values (?,?,?,?)','createLVO')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256e3097-0a07-3238-018c-6cbcbe7cde0b','1','IN')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256e3097-0a07-3238-018c-6cbcbe7cde0b','2','IN')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256e3097-0a07-3238-018c-6cbcbe7cde0b','3','IN')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','256e3097-0a07-3238-018c-6cbcbe7cde0b','4','IN')
/
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256cfe18-0a07-3238-018c-6cbcccff836a','2563f7ef-0a07-3238-018c-6cbc1b949eab','DELETE','deleteLVO')
/
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('2571de03-0a07-3238-018c-6cbc4cecaa2c','256cfe18-0a07-3238-018c-6cbcccff836a','0','delete from lob_test where lob_id = ?','deleteLVO')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571de03-0a07-3238-018c-6cbc4cecaa2c','1','IN')
/
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256ccd08-0a07-3238-018c-6cbc75b1a4d4','2563f7ef-0a07-3238-018c-6cbc1b949eab','RETRIEVE','retrieveLVO')
/
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('2571626e-0a07-3238-018c-6cbc1ac0c41e','256ccd08-0a07-3238-018c-6cbc75b1a4d4','0','select * from lob_test where lob_id = ?','retrieveLVO')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571626e-0a07-3238-018c-6cbc1ac0c41e','1','IN')
/
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256c9a30-0a07-3238-018c-6cbc88836bd6','2563f7ef-0a07-3238-018c-6cbc1b949eab','UPDATE','updateLVO')
/
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('256fe070-0a07-3238-018c-6cbc92919eae','256c9a30-0a07-3238-018c-6cbc88836bd6','0','update lob_test set b_lob=?, c_lob=?, b_lob2=? where lob_id = ?','updateLVO')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256fe070-0a07-3238-018c-6cbc92919eae','1','IN')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256fe070-0a07-3238-018c-6cbc92919eae','2','IN')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','256fe070-0a07-3238-018c-6cbc92919eae','3','IN')
/
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256fe070-0a07-3238-018c-6cbc92919eae','4','IN')
/
