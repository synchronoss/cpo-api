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
delete from cpo_query_parameter where query_id = '256fe070-0a07-3238-018c-6cbc92919eae';
delete from cpo_query where query_id = '256fe070-0a07-3238-018c-6cbc92919eae';
delete from cpo_query where group_id = '256c9a30-0a07-3238-018c-6cbc88836bd6';
delete from cpo_query_group where group_id = '256c9a30-0a07-3238-018c-6cbc88836bd6';
delete from cpo_query_parameter where query_id = '2571626e-0a07-3238-018c-6cbc1ac0c41e';
delete from cpo_query where query_id = '2571626e-0a07-3238-018c-6cbc1ac0c41e';
delete from cpo_query where group_id = '256ccd08-0a07-3238-018c-6cbc75b1a4d4';
delete from cpo_query_group where group_id = '256ccd08-0a07-3238-018c-6cbc75b1a4d4';
delete from cpo_query_parameter where query_id = '2571de03-0a07-3238-018c-6cbc4cecaa2c';
delete from cpo_query where query_id = '2571de03-0a07-3238-018c-6cbc4cecaa2c';
delete from cpo_query where group_id = '256cfe18-0a07-3238-018c-6cbcccff836a';
delete from cpo_query_group where group_id = '256cfe18-0a07-3238-018c-6cbcccff836a';
delete from cpo_query_parameter where query_id = '256e3097-0a07-3238-018c-6cbcbe7cde0b';
delete from cpo_query where query_id = '256e3097-0a07-3238-018c-6cbcbe7cde0b';
delete from cpo_query where group_id = '256c228c-0a07-3238-018c-6cbc8f38ab75';
delete from cpo_query_group where group_id = '256c228c-0a07-3238-018c-6cbc8f38ab75';
delete from cpo_attribute_map where class_id = '2563f7ef-0a07-3238-018c-6cbc1b949eab';
delete from cpo_query_group where class_id = '2563f7ef-0a07-3238-018c-6cbc1b949eab';
delete from cpo_class where class_id = '2563f7ef-0a07-3238-018c-6cbc1b949eab' or name = 'org.synchronoss.cpo.jdbc.LobValueObject';
delete from cpo_query_text where text_id = '256d3c3d-0a07-3238-018c-6cbc94b4d4a6';
delete from cpo_query_text where text_id = '256e1b1d-0a07-3238-018c-6cbce874a3b8';
delete from cpo_query_text where text_id = '256dbf37-0a07-3238-018c-6cbcc297d3f3';
delete from cpo_query_text where text_id = '256d8789-0a07-3238-018c-6cbc9d297be4';
insert into cpo_query_text (text_id, sql_text, description) values ('256d3c3d-0a07-3238-018c-6cbc94b4d4a6','insert into lob_test (lob_id, b_lob, c_lob) values (?,?,?)','createLVO');
update cpo_query_text set sql_text = 'insert into lob_test (lob_id, b_lob, c_lob) values (?,?,?)', description = 'createLVO' where text_id = '256d3c3d-0a07-3238-018c-6cbc94b4d4a6';
insert into cpo_query_text (text_id, sql_text, description) values ('256e1b1d-0a07-3238-018c-6cbce874a3b8','delete from lob_test where lob_id = ?','deleteLVO');
update cpo_query_text set sql_text = 'delete from lob_test where lob_id = ?', description = 'deleteLVO' where text_id = '256e1b1d-0a07-3238-018c-6cbce874a3b8';
insert into cpo_query_text (text_id, sql_text, description) values ('256dbf37-0a07-3238-018c-6cbcc297d3f3','select * from lob_test where lob_id = ?','retrieveLVO');
update cpo_query_text set sql_text = 'select * from lob_test where lob_id = ?', description = 'retrieveLVO' where text_id = '256dbf37-0a07-3238-018c-6cbcc297d3f3';
insert into cpo_query_text (text_id, sql_text, description) values ('256d8789-0a07-3238-018c-6cbc9d297be4','update lob_test set b_lob=?, c_lob=? where lob_id = ?','updateLVO');
update cpo_query_text set sql_text = 'update lob_test set b_lob=?, c_lob=? where lob_id = ?', description = 'updateLVO' where text_id = '256d8789-0a07-3238-018c-6cbc9d297be4';
insert into cpo_class (class_id, name) values ('2563f7ef-0a07-3238-018c-6cbc1b949eab','org.synchronoss.cpo.jdbc.LobValueObject');
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','2563f7ef-0a07-3238-018c-6cbc1b949eab','b_lob','bLob','BLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformGZipBlob');
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','2563f7ef-0a07-3238-018c-6cbc1b949eab','c_lob','cLob','CLOB',null,null,'org.synchronoss.cpo.transform.jdbc.TransformClob');
insert into cpo_attribute_map (attribute_id, class_id, column_name, attribute, column_type, db_column, db_table,transform_class) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2563f7ef-0a07-3238-018c-6cbc1b949eab','lob_id','lobId','NUMERIC',null,null,null);
insert into cpo_query_group (group_id, class_id, group_type, name) values ('256c228c-0a07-3238-018c-6cbc8f38ab75','2563f7ef-0a07-3238-018c-6cbc1b949eab','CREATE','createLVO');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('256e3097-0a07-3238-018c-6cbcbe7cde0b','256c228c-0a07-3238-018c-6cbc8f38ab75','256d3c3d-0a07-3238-018c-6cbc94b4d4a6','0');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256e3097-0a07-3238-018c-6cbcbe7cde0b','1','IN');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256e3097-0a07-3238-018c-6cbcbe7cde0b','2','IN');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256e3097-0a07-3238-018c-6cbcbe7cde0b','3','IN');
insert into cpo_query_group (group_id, class_id, group_type, name) values ('256cfe18-0a07-3238-018c-6cbcccff836a','2563f7ef-0a07-3238-018c-6cbc1b949eab','DELETE','deleteLVO');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('2571de03-0a07-3238-018c-6cbc4cecaa2c','256cfe18-0a07-3238-018c-6cbcccff836a','256e1b1d-0a07-3238-018c-6cbce874a3b8','0');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571de03-0a07-3238-018c-6cbc4cecaa2c','1','IN');
insert into cpo_query_group (group_id, class_id, group_type, name) values ('256ccd08-0a07-3238-018c-6cbc75b1a4d4','2563f7ef-0a07-3238-018c-6cbc1b949eab','RETRIEVE','retrieveLVO');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('2571626e-0a07-3238-018c-6cbc1ac0c41e','256ccd08-0a07-3238-018c-6cbc75b1a4d4','256dbf37-0a07-3238-018c-6cbcc297d3f3','0');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','2571626e-0a07-3238-018c-6cbc1ac0c41e','1','IN');
insert into cpo_query_group (group_id, class_id, group_type, name) values ('256c9a30-0a07-3238-018c-6cbc88836bd6','2563f7ef-0a07-3238-018c-6cbc1b949eab','UPDATE','updateLVO');
insert into cpo_query (query_id, group_id, text_id, seq_no) values ('256fe070-0a07-3238-018c-6cbc92919eae','256c9a30-0a07-3238-018c-6cbc88836bd6','256d8789-0a07-3238-018c-6cbc9d297be4','0');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb80-0a07-3238-018c-6cbc7b29779d','256fe070-0a07-3238-018c-6cbc92919eae','1','IN');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563fb8a-0a07-3238-018c-6cbc1bcd0ed8','256fe070-0a07-3238-018c-6cbc92919eae','2','IN');
insert into cpo_query_parameter (attribute_id, query_id, seq_no,param_type) values ('2563f899-0a07-3238-018c-6cbc07ebc5b1','256fe070-0a07-3238-018c-6cbc92919eae','3','IN');
