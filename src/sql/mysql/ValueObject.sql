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
delete from cpo_query_parameter where query_id = 'd32e12a4-0a0e-0045-00ba-6c1319a1de72';
delete from cpo_query where query_id = 'd32e12a4-0a0e-0045-00ba-6c1319a1de72';
delete from cpo_query_parameter where query_id = 'd32c1c74-0a0e-0045-00ba-6c1359304eea';
delete from cpo_query where query_id = 'd32c1c74-0a0e-0045-00ba-6c1359304eea';
delete from cpo_query where group_id = 'd32c0ca6-0a0e-0045-00ba-6c13efc1e520';
delete from cpo_query_group where group_id = 'd32c0ca6-0a0e-0045-00ba-6c13efc1e520';
delete from cpo_query_parameter where query_id = 'd2b224fe-0a0e-0045-00ba-6c133cf4fa7a';
delete from cpo_query where query_id = 'd2b224fe-0a0e-0045-00ba-6c133cf4fa7a';
delete from cpo_query where group_id = 'd2b1e7f5-0a0e-0045-00ba-6c13136edb10';
delete from cpo_query_group where group_id = 'd2b1e7f5-0a0e-0045-00ba-6c13136edb10';
delete from cpo_query_parameter where query_id = 'b07f24d5-0a07-2346-00fb-cb704600a48b';
delete from cpo_query where query_id = 'b07f24d5-0a07-2346-00fb-cb704600a48b';
delete from cpo_query where group_id = 'b07f0cf8-0a07-2346-00fb-cb701d7f186a';
delete from cpo_query_group where group_id = 'b07f0cf8-0a07-2346-00fb-cb701d7f186a';
delete from cpo_query_parameter where query_id = '6ace92a5-0a0e-0043-00e4-d0cd9cba8889';
delete from cpo_query where query_id = '6ace92a5-0a0e-0043-00e4-d0cd9cba8889';
delete from cpo_query where group_id = '6ace813c-0a0e-0043-00e4-d0cdec32bc22';
delete from cpo_query_group where group_id = '6ace813c-0a0e-0043-00e4-d0cdec32bc22';
delete from cpo_attribute_map where class_id = 'b079d2fa-0a07-2346-00fb-cb702782dbc4';
delete from cpo_query_group where class_id = 'b079d2fa-0a07-2346-00fb-cb702782dbc4';
delete from cpo_class where class_id = 'b079d2fa-0a07-2346-00fb-cb702782dbc4' or name = 'org.synchronoss.cpo.jdbc.ValueObject';
delete from cpo_query_text where text_id = '6ace397e-0a0e-0043-00e4-d0cd3e857349';
delete from cpo_query_text where text_id = 'b07ebd5c-0a07-2346-00fb-cb7021f181bf';
delete from cpo_query_text where text_id = 'd2abbeee-0a0e-0045-00ba-6c139775eae1';
delete from cpo_query_text where text_id = 'd329696e-0a0e-0045-00ba-6c13279e1c3e';
delete from cpo_query_text where text_id = 'd329975c-0a0e-0045-00ba-6c1397d6e8c9';
insert into cpo_query_text (text_id, sql_text, description) values ('6ace397e-0a0e-0043-00e4-d0cd3e857349','{?= call sysdate}','ValueObject - Function test');
update cpo_query_text set sql_text = '{?= call sysdate}', description = 'ValueObject - Function test' where text_id = '6ace397e-0a0e-0043-00e4-d0cd3e857349';
insert into cpo_query_text (text_id, sql_text, description) values ('b07ebd5c-0a07-2346-00fb-cb7021f181bf','Select * from value_object where id = ?','ValueObject - TestRetrieveObject');
update cpo_query_text set sql_text = 'Select * from value_object where id = ?', description = 'ValueObject - TestRetrieveObject' where text_id = 'b07ebd5c-0a07-2346-00fb-cb7021f181bf';
insert into cpo_query_text (text_id, sql_text, description) values ('d2abbeee-0a0e-0045-00ba-6c139775eae1','insert into value_object (id, attr_varchar) values (?,?)','ValueObject - TestRollback');
update cpo_query_text set sql_text = 'insert into value_object (id, attr_varchar) values (?,?)', description = 'ValueObject - TestRollback' where text_id = 'd2abbeee-0a0e-0045-00ba-6c139775eae1';
insert into cpo_query_text (text_id, sql_text, description) values ('d329696e-0a0e-0045-00ba-6c13279e1c3e','insert into value_object (id, attr_varchar) values (2,''Test'')','TestSingleRollback1');
update cpo_query_text set sql_text = 'insert into value_object (id, attr_varchar) values (2,''Test'')', description = 'TestSingleRollback1' where text_id = 'd329696e-0a0e-0045-00ba-6c13279e1c3e';
insert into cpo_query_text (text_id, sql_text, description) values ('d329975c-0a0e-0045-00ba-6c1397d6e8c9','insert into value_object (id, attr_varchar) values (1,''Test'')','TestSingleRollback2');
update cpo_query_text set sql_text = 'insert into value_object (id, attr_varchar) values (1,''Test'')', description = 'TestSingleRollback2' where text_id = 'd329975c-0a0e-0045-00ba-6c1397d6e8c9';
insert into cpo_class (class_id, name) values ('b079d2fa-0a07-2346-00fb-cb702782dbc4','org.synchronoss.cpo.jdbc.ValueObject');
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d354-0a07-2346-00fb-cb7051c25736','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_date','attrChar','CHAR',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb7075f3f34e','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_character','attrCharacter','CHAR',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb70670a52a4','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_date','attrDate','DATE',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb709f88f688','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_decimal','attrDecimal','NUMERIC',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb70d1452673','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_integer','attrInteger','NUMERIC',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb708ff4c25f','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_numeric','attrNumeric','NUMERIC',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d386-0a07-2346-00fb-cb701e351cd9','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_smallint','attrSmallInt','NUMERIC',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb708d7950f3','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_timestamp','attrTimestamp','TIMESTAMP',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d37c-0a07-2346-00fb-cb70993afd00','b079d2fa-0a07-2346-00fb-cb702782dbc4','attr_varchar','attrVarChar','VARCHAR',null,null,null);
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','b079d2fa-0a07-2346-00fb-cb702782dbc4','id','id','NUMERIC',null,null,null);
insert into cpo_query_group (group_id, class_id, group_type, name) values ('6ace813c-0a0e-0043-00e4-d0cdec32bc22','b079d2fa-0a07-2346-00fb-cb702782dbc4','EXECUTE','TestExecuteObject');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('6ace92a5-0a0e-0043-00e4-d0cd9cba8889','6ace813c-0a0e-0043-00e4-d0cdec32bc22','6ace397e-0a0e-0043-00e4-d0cd3e857349','0');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb708d7950f3','6ace92a5-0a0e-0043-00e4-d0cd9cba8889','1','OUT');
insert into cpo_query_group (group_id, class_id, group_type, name) values ('b07f0cf8-0a07-2346-00fb-cb701d7f186a','b079d2fa-0a07-2346-00fb-cb702782dbc4','RETRIEVE','TestRetrieveObject');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('b07f24d5-0a07-2346-00fb-cb704600a48b','b07f0cf8-0a07-2346-00fb-cb701d7f186a','b07ebd5c-0a07-2346-00fb-cb7021f181bf','0');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','b07f24d5-0a07-2346-00fb-cb704600a48b','1','IN');
insert into cpo_query_group (group_id, class_id, group_type, name) values ('d2b1e7f5-0a0e-0045-00ba-6c13136edb10','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestRollback');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','d2b1e7f5-0a0e-0045-00ba-6c13136edb10','d2abbeee-0a0e-0045-00ba-6c139775eae1','0');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d30e-0a07-2346-00fb-cb70284b51ce','d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','1','IN');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('b079d37c-0a07-2346-00fb-cb70993afd00','d2b224fe-0a0e-0045-00ba-6c133cf4fa7a','2','IN');
insert into cpo_query_group (group_id, class_id, group_type, name) values ('d32c0ca6-0a0e-0045-00ba-6c13efc1e520','b079d2fa-0a07-2346-00fb-cb702782dbc4','CREATE','TestSingleRollback');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('d32c1c74-0a0e-0045-00ba-6c1359304eea','d32c0ca6-0a0e-0045-00ba-6c13efc1e520','d329696e-0a0e-0045-00ba-6c13279e1c3e','0');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('d32e12a4-0a0e-0045-00ba-6c1319a1de72','d32c0ca6-0a0e-0045-00ba-6c13efc1e520','d329975c-0a0e-0045-00ba-6c1397d6e8c9','1');
