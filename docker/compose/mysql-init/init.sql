-- -- User customized configuration

-- -- Create a backup user
-- CREATE USER 'backup'@'%' IDENTIFIED BY 'BackupPass';
-- GRANT SELECT, RELOAD, PROCESS, LOCK TABLES, REPLICATION CLIENT ON *.* TO 'backup'@'%';

-- -- Set the default time zone
-- SET GLOBAL time_zone = '+8:00';
