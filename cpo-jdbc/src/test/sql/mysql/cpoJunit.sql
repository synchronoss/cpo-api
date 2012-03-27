delete from TEST_cpo_query_parameter;
delete from TEST_cpo_attribute_map;
delete from TEST_cpo_query;
delete from TEST_cpo_query_text;
delete from TEST_cpo_query_group;
delete from TEST_cpo_class;
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('256d3c3d-0a07-3238-018c-6cbc94b4d4a6','insert into lob_test (lob_id, b_lob, c_lob, b_lob2) values (?,?,?,?)','createLVO',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('256e1b1d-0a07-3238-018c-6cbce874a3b8','delete from lob_test where lob_id = ?','deleteLVO',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('256dbf37-0a07-3238-018c-6cbcc297d3f3','select * from lob_test where lob_id = ?','retrieveLVO',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('256d8789-0a07-3238-018c-6cbc9d297be4','update lob_test set b_lob=?, c_lob=?, b_lob2=? where lob_id = ?','updateLVO',null);
insert into TEST_cpo_class (class_id, name, userid) values ('2563f7ef-0a07-3238-018c-6cbc1b949eab','org.synchronoss.cpo.jdbc.LobValueObject',null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','2563f7ef-0a07-3238-018c-6cbc1b949eab','b_lob','bLob','LONGVARBINARY',null,null,'org.synchronoss.cpo.transform.jdbc.TransformGZipBytes',null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','2563f7ef-0a07-3238-018c-6cbc1b949eab','b_lob2','bLob2','LONGVARBINARY',null,null,'',null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','2563f7ef-0a07-3238-018c-6cbc1b949eab','c_lob','cLob','LONGVARCHAR',null,null,'org.synchronoss.cpo.transform.jdbc.TransformStringChar',null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2563f7ef-0a07-3238-018c-6cbc1b949eab','lob_id','lobId','NUMERIC',null,null,null,null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('256c228c-0a07-3238-018c-6cbc8f38ab75','2563f7ef-0a07-3238-018c-6cbc1b949eab','CREATE','createLVO',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('256e3097-0a07-3238-018c-6cbcbe7cde0b','256c228c-0a07-3238-018c-6cbc8f38ab75','256d3c3d-0a07-3238-018c-6cbc94b4d4a6','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256e3097-0a07-3238-018c-6cbcbe7cde0b','1','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256e3097-0a07-3238-018c-6cbcbe7cde0b','2','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256e3097-0a07-3238-018c-6cbcbe7cde0b','3','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','256e3097-0a07-3238-018c-6cbcbe7cde0b','4','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('256cfe18-0a07-3238-018c-6cbcccff836a','2563f7ef-0a07-3238-018c-6cbc1b949eab','DELETE','deleteLVO',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('2571de03-0a07-3238-018c-6cbc4cecaa2c','256cfe18-0a07-3238-018c-6cbcccff836a','256e1b1d-0a07-3238-018c-6cbce874a3b8','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571de03-0a07-3238-018c-6cbc4cecaa2c','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('256ccd08-0a07-3238-018c-6cbc75b1a4d4','2563f7ef-0a07-3238-018c-6cbc1b949eab','RETRIEVE','retrieveLVO',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('2571626e-0a07-3238-018c-6cbc1ac0c41e','256ccd08-0a07-3238-018c-6cbc75b1a4d4','256dbf37-0a07-3238-018c-6cbcc297d3f3','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571626e-0a07-3238-018c-6cbc1ac0c41e','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('256c9a30-0a07-3238-018c-6cbc88836bd6','2563f7ef-0a07-3238-018c-6cbc1b949eab','UPDATE','updateLVO',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('256fe070-0a07-3238-018c-6cbc92919eae','256c9a30-0a07-3238-018c-6cbc88836bd6','256d8789-0a07-3238-018c-6cbc9d297be4','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256fe070-0a07-3238-018c-6cbc92919eae','1','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256fe070-0a07-3238-018c-6cbc92919eae','2','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('f3af5c6e-0a07-2346-010d-4f27016a1707','256fe070-0a07-3238-018c-6cbc92919eae','3','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256fe070-0a07-3238-018c-6cbc92919eae','4','IN',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('0495cfd0-0a0e-0090-0049-57c746c0185c','insert into value_object(id, attr_char, attr_character, attr_date, attr_decimal, attr_integer, attr_numeric, attr_smallint, attr_timestamp, attr_varchar, attr_bool, attr_datetime) values (?,?,?,?,?,?,?,?,?,?,?,?)','ValueObject - Create',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('04924cec-0a0e-0090-0049-57c79f28fda1','delete from value_object where id = ?','ValueObject - Delete',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('046ad592-0a0e-0090-0049-57c7c124b567','select count(0) from value_object where id = ?','ValueObject - Exists',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('0493643f-0a0e-0090-0049-57c77f3a67ba','select * from value_object','ValueObject - list',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('048f12ab-0a0e-0090-0049-57c769b25986','select * from value_object where id = ?','ValueObject - Retrieve',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('3f48061d-0a0e-0056-00db-ac11a92bad99','update value_object set attr_varchar=?','ValueObject - Default Update',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('020da868-c0a8-0107-6202-9d7533c40500','select * from value_object where attr_varchar=? __CPO_WHERE__ and attr_varchar=?','ValueObject - InterleavedWhere','dberry');
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('04a9da9a-0a0e-0090-0049-57c7c5cfc23c','select * from value_object where id = ? for update','ValueObject - SelectForUpdateNoWait',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('04a7cf22-0a0e-0090-0049-57c7cb5f60d2','select * from value_object where id = ? for update','ValueObject - SelectForUpdate',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('9b13059a-0a00-0129-0025-965b3875ed16','select * from value_object for update','ValueObject - SelectForUpdateExistAll',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('9b112de3-0a00-0129-0025-965b54eea170','select * from value_object where id = ? for update','ValueObject - SelectForUpdateExistSingle',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('9b0f5e3e-0a00-0129-0025-965b394e6319','select * from value_object where id is null for update','ValueObject - SelectForUpdateExistZero',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('6ace397e-0a0e-0043-00e4-d0cd3e857349','{? = call power(?,?)}','ValueObject - Function test',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('62515f1c-0a0e-0045-0036-50ed3b568fed','delete from value_object where id = ?','TestOrderByDelete',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('624e3572-0a0e-0045-0036-50ed1c27e9f8','insert into value_object (id,attr_varchar,attr_smallint) values (?,?,?)','TestOrderByInsert',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('624f55c4-0a0e-0045-0036-50ed07db4da2','select * from value_object __CPO_ORDERBY__','TestOrderByRetrieve',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('b07ebd5c-0a07-2346-00fb-cb7021f181bf','select * from value_object where id = ?','ValueObject - TestRetrieveObject',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('d2abbeee-0a0e-0045-00ba-6c139775eae1','insert into value_object (id, attr_varchar) values (?,?)','ValueObject - TestRollback',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('d329696e-0a0e-0045-00ba-6c13279e1c3e','insert into value_object (id, attr_varchar) values (2,''test'')','TestSingleRollback1',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('d329975c-0a0e-0045-00ba-6c1397d6e8c9','insert into value_object (id, attr_varchar) values (1,''test'')','TestSingleRollback2',null);
insert into TEST_cpo_query_text (text_id, sql_text, description, userid) values ('bcff2a45-c0a8-0103-007c-808db8d898f5','select * from value_object __CPO_WHERE__ order by id','Value Object - TestWhereRetrieve',null);
insert into TEST_cpo_class (class_id, name, userid) values ('b079d2fa-0a07-2346-00fb-cb702782dbc4','org.synchronoss.cpo.jdbc.ValueObject',null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('04a0cb29-0a0e-0090-0049-57c743ffb250','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_bool','attrBit','BOOLEAN',null,null,null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d354-0a07-2346-00fb-cb7051c25736','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_char','attrChar','CHAR','attr_char','value_object',null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d37c-0a07-2346-00fb-cb7075f3f34e','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_character','attrCharacter','CHAR',null,null,null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d37c-0a07-2346-00fb-cb70670a52a4','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_date','attrDate','DATE',null,null,null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('9b299ad2-0a00-0129-00de-b1ad7e15b210','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_datetime','attrDatetime','TIMESTAMP',null,null,null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d386-0a07-2346-00fb-cb709f88f688','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_decimal','attrDecimal','NUMERIC',null,null,null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('e8858c36-c0a8-0103-00eb-5cdc4802491b','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_double','attrDouble','DOUBLE','','','',null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d386-0a07-2346-00fb-cb70d1452673','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_integer','attrInteger','INTEGER',null,null,'org.synchronoss.cpo.transform.jdbc.TransformNoOp',null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d386-0a07-2346-00fb-cb708ff4c25f','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_numeric','attrNumeric','NUMERIC',null,null,null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d386-0a07-2346-00fb-cb701e351cd9','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_smallint','attrSmallInt','SMALLINT','attr_smallint','value_object',null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d37c-0a07-2346-00fb-cb708d7950f3','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_timestamp','attrTimestamp','TIMESTAMP',null,null,null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d37c-0a07-2346-00fb-cb70993afd00','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_varchar','attrVarChar','VARCHAR','attr_varchar','value_object',null,null);
insert into TEST_cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','b079d2fa-0a07-2346-00fb-cb702782dbc4','id','id','NUMERIC','id','value_object',null,null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('04968c2c-0a0e-0090-0049-57c7d2374cac','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE',null,null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('04969bf0-0a0e-0090-0049-57c72eae809f','04968c2c-0a0e-0090-0049-57c7d2374cac','0495cfd0-0a0e-0090-0049-57c746c0185c','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','04969bf0-0a0e-0090-0049-57c72eae809f','1','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d354-0a07-2346-00fb-cb7051c25736','04969bf0-0a0e-0090-0049-57c72eae809f','2','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb7075f3f34e','04969bf0-0a0e-0090-0049-57c72eae809f','3','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb70670a52a4','04969bf0-0a0e-0090-0049-57c72eae809f','4','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb709f88f688','04969bf0-0a0e-0090-0049-57c72eae809f','5','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb70d1452673','04969bf0-0a0e-0090-0049-57c72eae809f','6','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb708ff4c25f','04969bf0-0a0e-0090-0049-57c72eae809f','7','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb701e351cd9','04969bf0-0a0e-0090-0049-57c72eae809f','8','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb708d7950f3','04969bf0-0a0e-0090-0049-57c72eae809f','9','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb70993afd00','04969bf0-0a0e-0090-0049-57c72eae809f','10','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('04a0cb29-0a0e-0090-0049-57c743ffb250','04969bf0-0a0e-0090-0049-57c72eae809f','11','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('9b299ad2-0a00-0129-00de-b1ad7e15b210','04969bf0-0a0e-0090-0049-57c72eae809f','12','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('049278db-0a0e-0090-0049-57c7aa799618','b079d2fa-0a07-2346-00fb-cb702782dbc4','DELETE',null,null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('0492843e-0a0e-0090-0049-57c77cb5633d','049278db-0a0e-0090-0049-57c7aa799618','04924cec-0a0e-0090-0049-57c79f28fda1','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','0492843e-0a0e-0090-0049-57c77cb5633d','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('046a594d-0a0e-0090-0049-57c70aadd01d','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST',null,null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('046a7517-0a0e-0090-0049-57c7ef77f5ab','046a594d-0a0e-0090-0049-57c70aadd01d','046ad592-0a0e-0090-0049-57c7c124b567','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','046a7517-0a0e-0090-0049-57c7ef77f5ab','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('0493aca0-0a0e-0090-0049-57c7c658b355','b079d2fa-0a07-2346-00fb-cb702782dbc4','LIST',null,null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('0493bd9e-0a0e-0090-0049-57c7b588746a','0493aca0-0a0e-0090-0049-57c7c658b355','0493643f-0a0e-0090-0049-57c77f3a67ba','0',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('048f3b5d-0a0e-0090-0049-57c75431bb8f','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE',null,null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('048f4ca0-0a0e-0090-0049-57c789785362','048f3b5d-0a0e-0090-0049-57c75431bb8f','048f12ab-0a0e-0090-0049-57c769b25986','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','048f4ca0-0a0e-0090-0049-57c789785362','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('3f484b34-0a0e-0056-00db-ac1164b7f134','b079d2fa-0a07-2346-00fb-cb702782dbc4','UPDATE',null,null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('3f487e05-0a0e-0056-00db-ac11d6d53a30','3f484b34-0a0e-0056-00db-ac1164b7f134','3f48061d-0a0e-0056-00db-ac11a92bad99','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb70993afd00','3f487e05-0a0e-0056-00db-ac11d6d53a30','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('020df627-c0a8-0107-6202-9d7545ac4de2','b079d2fa-0a07-2346-00fb-cb702782dbc4','LIST','InterleavedWhere','dberry');
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('020df629-c0a8-0107-6202-9d75a1cd87c0','020df627-c0a8-0107-6202-9d7545ac4de2','020da868-c0a8-0107-6202-9d7533c40500','0','dberry');
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb70993afd00','020df629-c0a8-0107-6202-9d75a1cd87c0','1','IN','dberry');
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb70993afd00','020df629-c0a8-0107-6202-9d75a1cd87c0','2','IN','dberry');
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('04aa3721-0a0e-0090-0049-57c7cf24bc3a','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE','Select4UpdateNoWait',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('04aa5200-0a0e-0090-0049-57c7f2036125','04aa3721-0a0e-0090-0049-57c7cf24bc3a','04a9da9a-0a0e-0090-0049-57c7c5cfc23c','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','04aa5200-0a0e-0090-0049-57c7f2036125','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('04a83022-0a0e-0090-0049-57c7f3284546','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE','SelectForUpdate',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('04a8541c-0a0e-0090-0049-57c7ffebb167','04a83022-0a0e-0090-0049-57c7f3284546','04a7cf22-0a0e-0090-0049-57c7cb5f60d2','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','04a8541c-0a0e-0090-0049-57c7ffebb167','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('9b12b35a-0a00-0129-0025-965b4a771d14','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST','SelectForUpdateExistAll',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('9b1311e4-0a00-0129-0025-965b25007bed','9b12b35a-0a00-0129-0025-965b4a771d14','9b13059a-0a00-0129-0025-965b3875ed16','0',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('9b10e510-0a00-0129-0025-965bc63e5158','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST','SelectForUpdateExistSingle',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('9b113bbc-0a00-0129-0025-965be82f233a','9b10e510-0a00-0129-0025-965bc63e5158','9b112de3-0a00-0129-0025-965b54eea170','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','9b113bbc-0a00-0129-0025-965be82f233a','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('9b0e87dd-0a00-0129-0025-965bdc2cb51d','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXIST','SelectForUpdateExistZero',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('9b0f7966-0a00-0129-0025-965bdca130ea','9b0e87dd-0a00-0129-0025-965bdc2cb51d','9b0f5e3e-0a00-0129-0025-965b394e6319','0',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('6ace813c-0a0e-0043-00e4-d0cdec32bc22','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXECUTE','TestExecuteObject',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('6ace92a5-0a0e-0043-00e4-d0cd9cba8889','6ace813c-0a0e-0043-00e4-d0cdec32bc22','6ace397e-0a0e-0043-00e4-d0cd3e857349','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('e8858c36-c0a8-0103-00eb-5cdc4802491b','6ace92a5-0a0e-0043-00e4-d0cd9cba8889','1','OUT',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb70d1452673','6ace92a5-0a0e-0043-00e4-d0cd9cba8889','2','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb70d1452673','6ace92a5-0a0e-0043-00e4-d0cd9cba8889','3','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('f398efae-0a07-2346-010d-4f27f58a3f44','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXECUTE','TestExecuteObjectNoTransform',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('f3990886-0a07-2346-010d-4f27f3a16567','f398efae-0a07-2346-010d-4f27f58a3f44','6ace397e-0a0e-0043-00e4-d0cd3e857349','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('e8858c36-c0a8-0103-00eb-5cdc4802491b','f3990886-0a07-2346-010d-4f27f3a16567','1','OUT',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb701e351cd9','f3990886-0a07-2346-010d-4f27f3a16567','2','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb701e351cd9','f3990886-0a07-2346-010d-4f27f3a16567','3','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('6251919f-0a0e-0045-0036-50ed1f320768','b079d2fa-0a07-2346-00fb-cb702782dbc4','DELETE','TestOrderByDelete',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('6251b40d-0a0e-0045-0036-50ed1c4d6a84','6251919f-0a0e-0045-0036-50ed1f320768','62515f1c-0a0e-0045-0036-50ed3b568fed','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','6251b40d-0a0e-0045-0036-50ed1c4d6a84','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('624df027-0a0e-0045-0036-50ed1d04c8f6','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestOrderByInsert',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('624e5354-0a0e-0045-0036-50ed0e2cc289','624df027-0a0e-0045-0036-50ed1d04c8f6','624e3572-0a0e-0045-0036-50ed1c27e9f8','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','624e5354-0a0e-0045-0036-50ed0e2cc289','1','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb70993afd00','624e5354-0a0e-0045-0036-50ed0e2cc289','2','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d386-0a07-2346-00fb-cb701e351cd9','624e5354-0a0e-0045-0036-50ed0e2cc289','3','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('624fa5b0-0a0e-0045-0036-50ed119998d6','b079d2fa-0a07-2346-00fb-cb702782dbc4','LIST','TestOrderByRetrieve',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('624ffe33-0a0e-0045-0036-50ed61ed3195','624fa5b0-0a0e-0045-0036-50ed119998d6','624f55c4-0a0e-0045-0036-50ed07db4da2','0',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('b07f0cf8-0a07-2346-00fb-cb701d7f186a','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE','TestRetrieveObject',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('b07f24d5-0a07-2346-00fb-cb704600a48b','b07f0cf8-0a07-2346-00fb-cb701d7f186a','b07ebd5c-0a07-2346-00fb-cb7021f181bf','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','b07f24d5-0a07-2346-00fb-cb704600a48b','1','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('d2b1e7f5-0a0e-0045-00ba-6c13136edb10','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestRollback',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','d2b1e7f5-0a0e-0045-00ba-6c13136edb10','d2abbeee-0a0e-0045-00ba-6c139775eae1','0',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','1','IN',null);
insert into TEST_cpo_query_parameter (attribute_id, query_id, seq_no, param_type, userid) values ('b079d37c-0a07-2346-00fb-cb70993afd00','d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','2','IN',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('d32c0ca6-0a0e-0045-00ba-6c13efc1e520','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestSingleRollback',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('d32c1c74-0a0e-0045-00ba-6c1359304eea','d32c0ca6-0a0e-0045-00ba-6c13efc1e520','d329696e-0a0e-0045-00ba-6c13279e1c3e','0',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('d32e12a4-0a0e-0045-00ba-6c1319a1de72','d32c0ca6-0a0e-0045-00ba-6c13efc1e520','d329975c-0a0e-0045-00ba-6c1397d6e8c9','1',null);
insert into TEST_cpo_query_group (group_id, class_id, group_type, name, userid) values ('bcff832c-c0a8-0103-007c-808d70952559','b079d2fa-0a07-2346-00fb-cb702782dbc4','LIST','TestWhereRetrieve',null);
insert into TEST_cpo_query (query_id, group_id, text_id, seq_no, userid) values ('bcff9726-c0a8-0103-007c-808d19ebd7b3','bcff832c-c0a8-0103-007c-808d70952559','bcff2a45-c0a8-0103-007c-808db8d898f5','0',null);
