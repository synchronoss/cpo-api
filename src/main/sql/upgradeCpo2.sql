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
drop table cpo_datatype;

alter table cpo_attribute_map add (transform_class varchar2(1023));

update cpo_attribute_map set column_type = 'VARCHAR' where column_type = 'VARCHAR2';

update cpo_attribute_map set column_type = 'NUMERIC' where column_type = 'NUMBER';

update cpo_attribute_map set transform_class = 'org.synchronoss.cpo.transform.jdbc.TransformGZipBlob' where column_type = 'BLOB';

update cpo_attribute_map set transform_class = 'org.synchronoss.cpo.transform.jdbc.TransformClob' where column_type = 'CLOB';

commit;
