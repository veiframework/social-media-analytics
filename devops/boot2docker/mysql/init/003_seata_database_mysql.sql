CREATE DATABASE IF NOT EXISTS seata;

-- create a new user
create user 'seata'@'%' identified by 'seata';
-- grant all permission to all databases
GRANT ALL PRIVILEGES ON *.* TO 'seata'@'%';
-- flush
flush privileges;

