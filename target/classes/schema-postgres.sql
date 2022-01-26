DROP TABLE IF EXISTS call_logs;
CREATE TABLE call_logs (
  id SERIAL NOT NULL,
  date_modified datetime DEFAULT NULL,
  break_type varchar(255) DEFAULT NULL,
  date_created datetime DEFAULT NULL,
  in_time varchar(255) DEFAULT NULL,
  out_time varchar(255) DEFAULT NULL,
  user_name varchar(255) DEFAULT NULL
);
commit;