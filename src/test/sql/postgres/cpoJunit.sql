INSERT INTO TEST_cpo_class(class_id, name, userid, createdate)
  VALUES('2563f7ef-0a07-3238-018c-6cbc1b949eab', 'org.synchronoss.cpo.jdbc.LobValueObject', NULL, NULL);
INSERT INTO TEST_cpo_class(class_id, name, userid, createdate)
  VALUES('b079d2fa-0a07-2346-00fb-cb702782dbc4', 'org.synchronoss.cpo.jdbc.ValueObject', NULL, NULL);


INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('04a0cb29-0a0e-0090-0049-57c743ffb250', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_bool', 'BOOLEAN', 'attrBit', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('2563f899-0a07-3238-018c-6cbc07ebc5b1', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'lob_id', 'NUMERIC', 'lobId', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('2563fb80-0a07-3238-018c-6cbc7b29779d', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'b_lob', 'LONGVARBINARY', 'bLob', 'org.synchronoss.cpo.transform.jdbc.TransformGZipBytes', NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'c_lob', 'LONGVARCHAR', 'cLob', 'org.synchronoss.cpo.transform.jdbc.TransformStringChar', NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('9b299ad2-0a00-0129-00de-b1ad7e15b210', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_datetime', 'TIMESTAMP', 'attrDatetime', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'id', 'NUMERIC', 'id', NULL, 'value_object', 'id', NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d354-0a07-2346-00fb-cb7051c25736', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_char', 'CHAR', 'attrChar', NULL, 'value_object', 'attr_char', NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb70670a52a4', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_date', 'DATE', 'attrDate', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb7075f3f34e', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_character', 'CHAR', 'attrCharacter', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb708d7950f3', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_timestamp', 'TIMESTAMP', 'attrTimestamp', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb70993afd00', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_varchar', 'VARCHAR', 'attrVarChar', NULL, 'value_object', 'attr_varchar', NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb701e351cd9', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_smallint', 'NUMERIC', 'attrSmallInt', NULL, 'value_object', 'attr_smallint', NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb708ff4c25f', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_numeric', 'NUMERIC', 'attrNumeric', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb709f88f688', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_decimal', 'NUMERIC', 'attrDecimal', NULL, NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb70d1452673', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_integer', 'NUMERIC', 'attrInteger', 'org.synchronoss.cpo.transform.jdbc.TransformNoOp', NULL, NULL, NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('e8858c36-c0a8-0103-00eb-5cdc4802491b', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'attr_double', 'DOUBLE', 'attrDouble', '', '', '', NULL, NULL);
INSERT INTO TEST_cpo_attribute_map(attribute_id, class_id, column_name, column_type, attribute, transform_class, db_table, db_column, userid, createdate)
  VALUES('f3af5c6e-0a07-2346-010d-4f27016a1707', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'b_lob2', 'LONGVARBINARY', 'bLob2', '', NULL, NULL, NULL, NULL);


INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('046a594d-0a0e-0090-0049-57c70aadd01d', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'EXIST', NULL, NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('048f3b5d-0a0e-0090-0049-57c75431bb8f', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'RETRIEVE', NULL, NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('049278db-0a0e-0090-0049-57c7aa799618', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'DELETE', NULL, NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('0493aca0-0a0e-0090-0049-57c7c658b355', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'LIST', NULL, NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('04968c2c-0a0e-0090-0049-57c7d2374cac', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'CREATE', NULL, NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('04a83022-0a0e-0090-0049-57c7f3284546', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'RETRIEVE', 'SelectForUpdate', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('04aa3721-0a0e-0090-0049-57c7cf24bc3a', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'RETRIEVE', 'Select4UpdateNoWait', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('256c228c-0a07-3238-018c-6cbc8f38ab75', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'CREATE', 'createLVO', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('256c9a30-0a07-3238-018c-6cbc88836bd6', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'UPDATE', 'updateLVO', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('256ccd08-0a07-3238-018c-6cbc75b1a4d4', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'RETRIEVE', 'retrieveLVO', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('256cfe18-0a07-3238-018c-6cbcccff836a', '2563f7ef-0a07-3238-018c-6cbc1b949eab', 'DELETE', 'deleteLVO', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('3f484b34-0a0e-0056-00db-ac1164b7f134', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'UPDATE', NULL, NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('624df027-0a0e-0045-0036-50ed1d04c8f6', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'CREATE', 'TestOrderByInsert', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('624fa5b0-0a0e-0045-0036-50ed119998d6', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'LIST', 'TestOrderByRetrieve', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('6251919f-0a0e-0045-0036-50ed1f320768', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'DELETE', 'TestOrderByDelete', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('6ace813c-0a0e-0043-00e4-d0cdec32bc22', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'EXECUTE', 'TestExecuteObject', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('9b0e87dd-0a00-0129-0025-965bdc2cb51d', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'EXIST', 'SelectForUpdateExistZero', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('9b10e510-0a00-0129-0025-965bc63e5158', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'EXIST', 'SelectForUpdateExistSingle', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('9b12b35a-0a00-0129-0025-965b4a771d14', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'EXIST', 'SelectForUpdateExistAll', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('b07f0cf8-0a07-2346-00fb-cb701d7f186a', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'RETRIEVE', 'TestRetrieveObject', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('bcff832c-c0a8-0103-007c-808d70952559', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'LIST', 'TestWhereRetrieve', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('d2b1e7f5-0a0e-0045-00ba-6c13136edb10', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'CREATE', 'TestRollback', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('d32c0ca6-0a0e-0045-00ba-6c13efc1e520', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'CREATE', 'TestSingleRollback', NULL, NULL);
INSERT INTO TEST_cpo_query_group(group_id, class_id, group_type, name, userid, createdate)
  VALUES('f398efae-0a07-2346-010d-4f27f58a3f44', 'b079d2fa-0a07-2346-00fb-cb702782dbc4', 'EXECUTE', 'TestExecuteObjectNoTransform', NULL, NULL);


INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('046a7517-0a0e-0090-0049-57c7ef77f5ab', '046a594d-0a0e-0090-0049-57c70aadd01d', 0, NULL, NULL, 'select count(0) from value_object where id = ?', 'ValueObject - Exists');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('048f4ca0-0a0e-0090-0049-57c789785362', '048f3b5d-0a0e-0090-0049-57c75431bb8f', 0, NULL, NULL, 'select * from value_object where id = ?', 'ValueObject - Retrieve');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('0492843e-0a0e-0090-0049-57c77cb5633d', '049278db-0a0e-0090-0049-57c7aa799618', 0, NULL, NULL, 'delete from value_object where id = ?', 'ValueObject - Delete');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('0493bd9e-0a0e-0090-0049-57c7b588746a', '0493aca0-0a0e-0090-0049-57c7c658b355', 0, NULL, NULL, 'select * from value_object', 'ValueObject - list');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('04969bf0-0a0e-0090-0049-57c72eae809f', '04968c2c-0a0e-0090-0049-57c7d2374cac', 0, NULL, NULL, 'insert into value_object(id, attr_char, attr_character, attr_date, attr_decimal, attr_integer, attr_numeric, attr_smallint, attr_timestamp, attr_varchar, attr_bool, attr_datetime) values (?,?,?,?,?,?,?,?,?,?,?,?)', 'ValueObject - Create');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('04a8541c-0a0e-0090-0049-57c7ffebb167', '04a83022-0a0e-0090-0049-57c7f3284546', 0, NULL, NULL, 'select * from value_object where id = ? for update of value_object', 'ValueObject - SelectForUpdate');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('04aa5200-0a0e-0090-0049-57c7f2036125', '04aa3721-0a0e-0090-0049-57c7cf24bc3a', 0, NULL, NULL, 'select * from value_object where id = ? for update of value_object NOWAIT', 'ValueObject - SelectForUpdateNoWait');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('256e3097-0a07-3238-018c-6cbcbe7cde0b', '256c228c-0a07-3238-018c-6cbc8f38ab75', 0, NULL, NULL, 'insert into lob_test (lob_id, b_lob, c_lob, b_lob2) values (?,?,?,?)', 'createLVO');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('256fe070-0a07-3238-018c-6cbc92919eae', '256c9a30-0a07-3238-018c-6cbc88836bd6', 0, NULL, NULL, 'update lob_test set b_lob=?, c_lob=?, b_lob2=? where lob_id = ?', 'updateLVO');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('2571626e-0a07-3238-018c-6cbc1ac0c41e', '256ccd08-0a07-3238-018c-6cbc75b1a4d4', 0, NULL, NULL, 'select * from lob_test where lob_id = ?', 'retrieveLVO');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('2571de03-0a07-3238-018c-6cbc4cecaa2c', '256cfe18-0a07-3238-018c-6cbcccff836a', 0, NULL, NULL, 'delete from lob_test where lob_id = ?', 'deleteLVO');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('3f487e05-0a0e-0056-00db-ac11d6d53a30', '3f484b34-0a0e-0056-00db-ac1164b7f134', 0, NULL, NULL, 'update value_object set attr_varchar=?', 'ValueObject - Default Update');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('624e5354-0a0e-0045-0036-50ed0e2cc289', '624df027-0a0e-0045-0036-50ed1d04c8f6', 0, NULL, NULL, 'insert into value_object (id,attr_varchar,attr_smallint) values (?,?,?)', 'TestOrderByInsert');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('624ffe33-0a0e-0045-0036-50ed61ed3195', '624fa5b0-0a0e-0045-0036-50ed119998d6', 0, NULL, NULL, 'select * from value_object __CPO_ORDERBY__', 'TestOrderByRetrieve');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('6251b40d-0a0e-0045-0036-50ed1c4d6a84', '6251919f-0a0e-0045-0036-50ed1f320768', 0, NULL, NULL, 'delete from value_object where id = ?', 'TestOrderByDelete');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('6ace92a5-0a0e-0043-00e4-d0cd9cba8889', '6ace813c-0a0e-0043-00e4-d0cdec32bc22', 0, NULL, NULL, '{? = call power(?,?)}', 'ValueObject - Function test');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('9b0f7966-0a00-0129-0025-965bdca130ea', '9b0e87dd-0a00-0129-0025-965bdc2cb51d', 0, NULL, NULL, 'select * from value_object where id is null for update', 'ValueObject - SelectForUpdateExistZero');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('9b113bbc-0a00-0129-0025-965be82f233a', '9b10e510-0a00-0129-0025-965bc63e5158', 0, NULL, NULL, 'select * from value_object where id = ? for update', 'ValueObject - SelectForUpdateExistSingle');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('9b1311e4-0a00-0129-0025-965b25007bed', '9b12b35a-0a00-0129-0025-965b4a771d14', 0, NULL, NULL, 'select * from value_object for update', 'ValueObject - SelectForUpdateExistAll');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('b07f24d5-0a07-2346-00fb-cb704600a48b', 'b07f0cf8-0a07-2346-00fb-cb701d7f186a', 0, NULL, NULL, 'select * from value_object where id = ?', 'ValueObject - TestRetrieveObject');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('bcff9726-c0a8-0103-007c-808d19ebd7b3', 'bcff832c-c0a8-0103-007c-808d70952559', 0, NULL, NULL, 'select * from value_object __CPO_WHERE__ order by id', 'Value Object - TestWhereRetrieve');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('d2b224fe-0a0e-0045-00ba-6c133cf4fa7a', 'd2b1e7f5-0a0e-0045-00ba-6c13136edb10', 0, NULL, NULL, 'insert into value_object (id, attr_varchar) values (?,?)', 'ValueObject - TestRollback');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('d32c1c74-0a0e-0045-00ba-6c1359304eea', 'd32c0ca6-0a0e-0045-00ba-6c13efc1e520', 0, NULL, NULL, 'insert into value_object (id, attr_varchar) values (2,''test'')', 'TestSingleRollback1');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('d32e12a4-0a0e-0045-00ba-6c1319a1de72', 'd32c0ca6-0a0e-0045-00ba-6c13efc1e520', 1, NULL, NULL, 'insert into value_object (id, attr_varchar) values (1,''test'')', 'TestSingleRollback2');
INSERT INTO TEST_cpo_query(query_id, group_id, seq_no, userid, createdate, sql_text, description)
  VALUES('f3990886-0a07-2346-010d-4f27f3a16567', 'f398efae-0a07-2346-010d-4f27f58a3f44', 0, NULL, NULL, '{? = call power(?,?)}', 'ValueObject - Function test');


INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('04a0cb29-0a0e-0090-0049-57c743ffb250', '04969bf0-0a0e-0090-0049-57c72eae809f', 11, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563f899-0a07-3238-018c-6cbc07ebc5b1', '256e3097-0a07-3238-018c-6cbcbe7cde0b', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563f899-0a07-3238-018c-6cbc07ebc5b1', '256fe070-0a07-3238-018c-6cbc92919eae', 4, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563f899-0a07-3238-018c-6cbc07ebc5b1', '2571626e-0a07-3238-018c-6cbc1ac0c41e', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563f899-0a07-3238-018c-6cbc07ebc5b1', '2571de03-0a07-3238-018c-6cbc4cecaa2c', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563fb80-0a07-3238-018c-6cbc7b29779d', '256e3097-0a07-3238-018c-6cbcbe7cde0b', 2, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563fb80-0a07-3238-018c-6cbc7b29779d', '256fe070-0a07-3238-018c-6cbc92919eae', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8', '256e3097-0a07-3238-018c-6cbcbe7cde0b', 3, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8', '256fe070-0a07-3238-018c-6cbc92919eae', 2, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('9b299ad2-0a00-0129-00de-b1ad7e15b210', '04969bf0-0a0e-0090-0049-57c72eae809f', 12, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '046a7517-0a0e-0090-0049-57c7ef77f5ab', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '048f4ca0-0a0e-0090-0049-57c789785362', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '0492843e-0a0e-0090-0049-57c77cb5633d', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '04969bf0-0a0e-0090-0049-57c72eae809f', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '04a8541c-0a0e-0090-0049-57c7ffebb167', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '04aa5200-0a0e-0090-0049-57c7f2036125', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '624e5354-0a0e-0045-0036-50ed0e2cc289', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '6251b40d-0a0e-0045-0036-50ed1c4d6a84', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', '9b113bbc-0a00-0129-0025-965be82f233a', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', 'b07f24d5-0a07-2346-00fb-cb704600a48b', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d30e-0a07-2346-00fb-cb70284b51ce', 'd2b224fe-0a0e-0045-00ba-6c133cf4fa7a', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d354-0a07-2346-00fb-cb7051c25736', '04969bf0-0a0e-0090-0049-57c72eae809f', 2, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb70670a52a4', '04969bf0-0a0e-0090-0049-57c72eae809f', 4, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb7075f3f34e', '04969bf0-0a0e-0090-0049-57c72eae809f', 3, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb708d7950f3', '04969bf0-0a0e-0090-0049-57c72eae809f', 9, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb70993afd00', '04969bf0-0a0e-0090-0049-57c72eae809f', 10, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb70993afd00', '3f487e05-0a0e-0056-00db-ac11d6d53a30', 1, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb70993afd00', '624e5354-0a0e-0045-0036-50ed0e2cc289', 2, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d37c-0a07-2346-00fb-cb70993afd00', 'd2b224fe-0a0e-0045-00ba-6c133cf4fa7a', 2, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb701e351cd9', '04969bf0-0a0e-0090-0049-57c72eae809f', 8, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb701e351cd9', '624e5354-0a0e-0045-0036-50ed0e2cc289', 3, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb701e351cd9', 'f3990886-0a07-2346-010d-4f27f3a16567', 2, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb701e351cd9', 'f3990886-0a07-2346-010d-4f27f3a16567', 3, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb708ff4c25f', '04969bf0-0a0e-0090-0049-57c72eae809f', 7, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb709f88f688', '04969bf0-0a0e-0090-0049-57c72eae809f', 5, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb70d1452673', '04969bf0-0a0e-0090-0049-57c72eae809f', 6, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb70d1452673', '6ace92a5-0a0e-0043-00e4-d0cd9cba8889', 2, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('b079d386-0a07-2346-00fb-cb70d1452673', '6ace92a5-0a0e-0043-00e4-d0cd9cba8889', 3, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('e8858c36-c0a8-0103-00eb-5cdc4802491b', '6ace92a5-0a0e-0043-00e4-d0cd9cba8889', 1, 'OUT', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('e8858c36-c0a8-0103-00eb-5cdc4802491b', 'f3990886-0a07-2346-010d-4f27f3a16567', 1, 'OUT', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('f3af5c6e-0a07-2346-010d-4f27016a1707', '256e3097-0a07-3238-018c-6cbcbe7cde0b', 4, 'IN', NULL, NULL);
INSERT INTO TEST_cpo_query_parameter(attribute_id, query_id, seq_no, param_type, userid, createdate)
  VALUES('f3af5c6e-0a07-2346-010d-4f27016a1707', '256fe070-0a07-3238-018c-6cbc92919eae', 3, 'IN', NULL, NULL);
