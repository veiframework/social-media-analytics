CREATE DATABASE IF NOT EXISTS nacos;

-- create a new user
create user 'nacos'@'%' identified by 'nacos';
-- grant all permission to all databases
GRANT ALL PRIVILEGES ON *.* TO 'nacos'@'%';
-- flush
flush privileges;

