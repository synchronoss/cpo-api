-- new way
select cqg.group_type,
       cqg.name,
       cq.query_id,
       cq.seq_no as query_seq,
       cqt.sql_text,
       cqp.seq_no as param_seq,
       cam.attribute, 
       cam.column_name, 
       cam.column_type, 
       cqp.param_type
from test_cpo_query_group cqg
    left outer join test_cpo_query cq on cqg.group_id = cq.group_id
    left outer join test_cpo_query_text cqt on cq.text_id = cqt.text_id
    left outer join test_cpo_query_parameter cqp on cq.query_id = cqp.query_id
    left outer join test_cpo_attribute_map cam on cqp.attribute_id = cam.attribute_id
order by cqg.group_id, cq.seq_no, cqp.seq_no

-- old way
select innr.group_type,
       innr.name,
       innr.query_id,
       innr.query_seq as query_seq,
       cqt.sql_text,
       innr.param_seq as param_seq,  
       cam.attribute, 
       cam.column_name, 
       cam.column_type, 
       innr.param_type
from test_cpo_query_text cqt,  
     (select cqg.group_type, 
             cqg.name, 
             cq.query_id, 
             cq.seq_no as query_seq,
             cqp.seq_no as param_seq, 
             cqp.attribute_id, 
             cqp.param_type,
             cq.text_id,
             cq.seq_no,
             cqg.group_id 
       from  test_cpo_query_group cqg, 
             test_cpo_query cq 
             left outer join test_cpo_query_parameter cqp on cq.query_id = cqp.query_id where cqg.group_id = cq.group_id ) innr 
       left outer join test_cpo_attribute_map cam on innr.attribute_id = cam.attribute_id where cqt.text_id = innr.text_id
order by innr.group_id asc, innr.query_seq asc, innr.param_seq  asc
