delete from TEST_cpo_query_parameter
;
delete from TEST_cpo_attribute_map
;
delete from TEST_cpo_query
;
delete from TEST_cpo_query_group
;
delete from TEST_cpo_class
;
insert into TEST_cpo_class (class_id, name) values ('2563f7ef-0a07-3238-018c-6cbc1b949eab','org.synchronoss.cpo.jdbc.LobValueObject')
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','2563f7ef-0a07-3238-018c-6cbc1b949eab','B_LOB','bLob','BLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformGZipBlob')
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','2563f7ef-0a07-3238-018c-6cbc1b949eab','B_LOB2','bLob2','BLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformBlob')
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','2563f7ef-0a07-3238-018c-6cbc1b949eab','C_LOB','cLob','CLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformClob')
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2563f7ef-0a07-3238-018c-6cbc1b949eab','LOB_ID','lobId','NUMERIC',null,null,null)
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256c228c-0a07-3238-018c-6cbc8f38ab75','2563f7ef-0a07-3238-018c-6cbc1b949eab','CREATE','createLVO')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('256e3097-0a07-3238-018c-6cbcbe7cde0b','256c228c-0a07-3238-018c-6cbc8f38ab75','0','insert into lob_test (lob_id, b_lob, c_lob, b_lob2) values (?,?,?,?)','createLVO')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256e3097-0a07-3238-018c-6cbcbe7cde0b','1','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256e3097-0a07-3238-018c-6cbcbe7cde0b','2','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256e3097-0a07-3238-018c-6cbcbe7cde0b','3','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','256e3097-0a07-3238-018c-6cbcbe7cde0b','4','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256cfe18-0a07-3238-018c-6cbcccff836a','2563f7ef-0a07-3238-018c-6cbc1b949eab','DELETE','deleteLVO')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('2571de03-0a07-3238-018c-6cbc4cecaa2c','256cfe18-0a07-3238-018c-6cbcccff836a','0','delete from lob_test where lob_id = ?','deleteLVO')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571de03-0a07-3238-018c-6cbc4cecaa2c','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256ccd08-0a07-3238-018c-6cbc75b1a4d4','2563f7ef-0a07-3238-018c-6cbc1b949eab','RETRIEVE','retrieveLVO')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('2571626e-0a07-3238-018c-6cbc1ac0c41e','256ccd08-0a07-3238-018c-6cbc75b1a4d4','0','select * from lob_test where lob_id = ?','retrieveLVO')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571626e-0a07-3238-018c-6cbc1ac0c41e','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('256c9a30-0a07-3238-018c-6cbc88836bd6','2563f7ef-0a07-3238-018c-6cbc1b949eab','UPDATE','updateLVO')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('256fe070-0a07-3238-018c-6cbc92919eae','256c9a30-0a07-3238-018c-6cbc88836bd6','0','update lob_test set b_lob=?, c_lob=?, b_lob2=? where lob_id = ?','updateLVO')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256fe070-0a07-3238-018c-6cbc92919eae','1','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256fe070-0a07-3238-018c-6cbc92919eae','2','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','256fe070-0a07-3238-018c-6cbc92919eae','3','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256fe070-0a07-3238-018c-6cbc92919eae','4','IN')
;
insert into TEST_cpo_class (class_id, name) values ('b079d2fa-0a07-2346-00fb-cb702782dbc4','org.synchronoss.cpo.jdbc.ValueObject')
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('04a0cb29-0a0e-0090-0049-57c743ffb250','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_BOOL','attrBit','BOOLEAN',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d354-0a07-2346-00fb-cb7051c25736','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_CHAR','attrChar','CHAR','attr_char','value_object',null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb7075f3f34e','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_CHARACTER','attrCharacter','CHAR',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb70670a52a4','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_DATE','attrDate','DATE',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('8f5d1c34-0a0e-1e77-0050-152f7c8719ee','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_DATETIME','attrDatetime','TIMESTAMP',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb709f88f688','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_DECIMAL','attrDecimal','NUMERIC',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('e88aa143-c0a8-0103-00da-89a001268a75','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_DOUBLE','attrDouble','DOUBLE',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb70d1452673','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_INTEGER','attrInteger','NUMERIC',null,null,'org.synchronoss.cpo.transform.jdbc.TransformNoOp')
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb708ff4c25f','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_NUMERIC','attrNumeric','NUMERIC',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb701e351cd9','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_SMALLINT','attrSmallInt','NUMERIC','attr_smallint',null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb708d7950f3','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_TIMESTAMP','attrTimestamp','TIMESTAMP',null,null,null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb70993afd00','b079d2fa-0a07-2346-00fb-cb702782dbc4','ATTR_VARCHAR','attrVarChar','VARCHAR',null,'value_object',null)
;
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','b079d2fa-0a07-2346-00fb-cb702782dbc4','ID','id','NUMERIC',null,null,null)
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('04aa3721-0a0e-0090-0049-57c7cf24bc3a','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE','Select4UpdateNoWait')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('04aa5200-0a0e-0090-0049-57c7f2036125','04aa3721-0a0e-0090-0049-57c7cf24bc3a','0','select * from value_object where id = ? for update of id NOWAIT','ValueObject - SelectForUpdateNoWait')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','04aa5200-0a0e-0090-0049-57c7f2036125','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('04a83022-0a0e-0090-0049-57c7f3284546','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE','SelectForUpdate')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('04a8541c-0a0e-0090-0049-57c7ffebb167','04a83022-0a0e-0090-0049-57c7f3284546','0','select * from value_object where id = ? for update of id','ValueObject - SelectForUpdate')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','04a8541c-0a0e-0090-0049-57c7ffebb167','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('8f6b2a61-0a0e-1e77-0050-152fbe1dcb2f','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST','SelectForUpdateExistAll')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('8f6b3b71-0a0e-1e77-0050-152f772d8a43','8f6b2a61-0a0e-1e77-0050-152fbe1dcb2f','0','select * from value_object for update','ValueObject - SelectForUpdateExistAll')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('8f6be4a8-0a0e-1e77-0050-152f23cd77ba','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST','SelectForUpdateExistSingle')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('8f6bf3c3-0a0e-1e77-0050-152f82f8cd6d','8f6be4a8-0a0e-1e77-0050-152f23cd77ba','0','select * from value_object where id = ? for update','ValueObject - SelectForUpdateExistSingle')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','8f6bf3c3-0a0e-1e77-0050-152f82f8cd6d','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('8f6cea91-0a0e-1e77-0050-152fa41aa0b0','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST','SelectForUpdateExistZero')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('8f6cf7dd-0a0e-1e77-0050-152faa2073ea','8f6cea91-0a0e-1e77-0050-152fa41aa0b0','0','select * from value_object where id is null for update','ValueObject - SelectForUpdateExistZero')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('6ace813c-0a0e-0043-00e4-d0cdec32bc22','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXECUTE','TestExecuteObject')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('6ace92a5-0a0e-0043-00e4-d0cd9cba8889','6ace813c-0a0e-0043-00e4-d0cdec32bc22','0','{? = call power(?,?)}','ValueObject - Function test')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('e88aa143-c0a8-0103-00da-89a001268a75','6ace92a5-0a0e-0043-00e4-d0cd9cba8889','1','OUT')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb70d1452673','6ace92a5-0a0e-0043-00e4-d0cd9cba8889','2','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb70d1452673','6ace92a5-0a0e-0043-00e4-d0cd9cba8889','3','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('f398efae-0a07-2346-010d-4f27f58a3f44','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXECUTE','TestExecuteObjectNoTransform')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('f3990886-0a07-2346-010d-4f27f3a16567','f398efae-0a07-2346-010d-4f27f58a3f44','0','{? = call power(?,?)}','ValueObject - Function test')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('e88aa143-c0a8-0103-00da-89a001268a75','f3990886-0a07-2346-010d-4f27f3a16567','1','OUT')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb701e351cd9','f3990886-0a07-2346-010d-4f27f3a16567','2','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb701e351cd9','f3990886-0a07-2346-010d-4f27f3a16567','3','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('6251919f-0a0e-0045-0036-50ed1f320768','b079d2fa-0a07-2346-00fb-cb702782dbc4','DELETE','TestOrderByDelete')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('6251b40d-0a0e-0045-0036-50ed1c4d6a84','6251919f-0a0e-0045-0036-50ed1f320768','0','delete from value_object where id = ?','TestOrderByDelete')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','6251b40d-0a0e-0045-0036-50ed1c4d6a84','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('624df027-0a0e-0045-0036-50ed1d04c8f6','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestOrderByInsert')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('624e5354-0a0e-0045-0036-50ed0e2cc289','624df027-0a0e-0045-0036-50ed1d04c8f6','0','insert into value_object (id,attr_varchar,attr_smallint) values (?,?,?)','TestOrderByInsert')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','624e5354-0a0e-0045-0036-50ed0e2cc289','1','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb70993afd00','624e5354-0a0e-0045-0036-50ed0e2cc289','2','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb701e351cd9','624e5354-0a0e-0045-0036-50ed0e2cc289','3','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('624fa5b0-0a0e-0045-0036-50ed119998d6','b079d2fa-0a07-2346-00fb-cb702782dbc4','LIST','TestOrderByRetrieve')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('624ffe33-0a0e-0045-0036-50ed61ed3195','624fa5b0-0a0e-0045-0036-50ed119998d6','0','select * from value_object','TestOrderByRetrieve')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('b07f0cf8-0a07-2346-00fb-cb701d7f186a','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE','TestRetrieveObject')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('b07f24d5-0a07-2346-00fb-cb704600a48b','b07f0cf8-0a07-2346-00fb-cb701d7f186a','0','Select * from value_object where id = ?','ValueObject - TestRetrieveObject')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','b07f24d5-0a07-2346-00fb-cb704600a48b','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('d2b1e7f5-0a0e-0045-00ba-6c13136edb10','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestRollback')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','d2b1e7f5-0a0e-0045-00ba-6c13136edb10','0','insert into value_object (id, attr_varchar) values (?,?)','ValueObject - TestRollback')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','1','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb70993afd00','d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','2','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('d32c0ca6-0a0e-0045-00ba-6c13efc1e520','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestSingleRollback')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('d32c1c74-0a0e-0045-00ba-6c1359304eea','d32c0ca6-0a0e-0045-00ba-6c13efc1e520','0','insert into value_object (id, attr_varchar) values (2,''Test'')','TestSingleRollback1')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('d32e12a4-0a0e-0045-00ba-6c1319a1de72','d32c0ca6-0a0e-0045-00ba-6c13efc1e520','1','insert into value_object (id, attr_varchar) values (1,''Test'')','TestSingleRollback2')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('8f676a85-0a0e-1e77-0050-152fd4abf546','b079d2fa-0a07-2346-00fb-cb702782dbc4','LIST','TestWhereRetrieve')
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('8f677c89-0a0e-1e77-0050-152fe78cf60a','8f676a85-0a0e-1e77-0050-152fd4abf546','0','select * from value_object __CPO_WHERE__ order by id','Value Object - TestWhereRetrieve')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('04968c2c-0a0e-0090-0049-57c7d2374cac','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE',null)
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('04969bf0-0a0e-0090-0049-57c72eae809f','04968c2c-0a0e-0090-0049-57c7d2374cac','0','insert into value_object(id, attr_char, attr_character, attr_date, ATTR_DECIMAL, ATTR_INTEGER, ATTR_NUMERIC, ATTR_SMALLINT, ATTR_TIMESTAMP, ATTR_VARCHAR, ATTR_BOOL, ATTR_DATETIME) VALUES (?,?,?,?,?,?,?,?,?,?,?,?)','ValueObject - Create')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','04969bf0-0a0e-0090-0049-57c72eae809f','1','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d354-0a07-2346-00fb-cb7051c25736','04969bf0-0a0e-0090-0049-57c72eae809f','2','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb7075f3f34e','04969bf0-0a0e-0090-0049-57c72eae809f','3','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb70670a52a4','04969bf0-0a0e-0090-0049-57c72eae809f','4','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb709f88f688','04969bf0-0a0e-0090-0049-57c72eae809f','5','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb70d1452673','04969bf0-0a0e-0090-0049-57c72eae809f','6','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb708ff4c25f','04969bf0-0a0e-0090-0049-57c72eae809f','7','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d386-0a07-2346-00fb-cb701e351cd9','04969bf0-0a0e-0090-0049-57c72eae809f','8','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb708d7950f3','04969bf0-0a0e-0090-0049-57c72eae809f','9','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb70993afd00','04969bf0-0a0e-0090-0049-57c72eae809f','10','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('04a0cb29-0a0e-0090-0049-57c743ffb250','04969bf0-0a0e-0090-0049-57c72eae809f','11','IN')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('8f5d1c34-0a0e-1e77-0050-152f7c8719ee','04969bf0-0a0e-0090-0049-57c72eae809f','12','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('049278db-0a0e-0090-0049-57c7aa799618','b079d2fa-0a07-2346-00fb-cb702782dbc4','DELETE',null)
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('0492843e-0a0e-0090-0049-57c77cb5633d','049278db-0a0e-0090-0049-57c7aa799618','0','delete from value_object where id = ?','ValueObject - Delete')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','0492843e-0a0e-0090-0049-57c77cb5633d','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('046a594d-0a0e-0090-0049-57c70aadd01d','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST',null)
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('046a7517-0a0e-0090-0049-57c7ef77f5ab','046a594d-0a0e-0090-0049-57c70aadd01d','0','select count(0) from value_object where id = ?','ValueObject - Exists')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','046a7517-0a0e-0090-0049-57c7ef77f5ab','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('0493aca0-0a0e-0090-0049-57c7c658b355','b079d2fa-0a07-2346-00fb-cb702782dbc4','LIST',null)
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('0493bd9e-0a0e-0090-0049-57c7b588746a','0493aca0-0a0e-0090-0049-57c7c658b355','0','select * from value_object','ValueObject - list')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('048f3b5d-0a0e-0090-0049-57c75431bb8f','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE',null)
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('048f4ca0-0a0e-0090-0049-57c789785362','048f3b5d-0a0e-0090-0049-57c75431bb8f','0','select * from value_object where id = ?','ValueObject - Retrieve')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','048f4ca0-0a0e-0090-0049-57c789785362','1','IN')
;
insert into TEST_cpo_query_group (group_id, class_id, group_type, name) values ('3f484b34-0a0e-0056-00db-ac1164b7f134','b079d2fa-0a07-2346-00fb-cb702782dbc4','UPDATE',null)
;
insert into TEST_cpo_query (query_id, group_id, seq_no, sql_text, description) values ('3f487e05-0a0e-0056-00db-ac11d6d53a30','3f484b34-0a0e-0056-00db-ac1164b7f134','0','update value_object set ATTR_VARCHAR=?','ValueObject - Default Update')
;
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb70993afd00','3f487e05-0a0e-0056-00db-ac11d6d53a30','1','IN')
;
