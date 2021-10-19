-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 19/10/21 09:22
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.14/557.yaml::1634569326780-1::limit
ALTER TABLE not_procediment ADD tipus VARCHAR2(32);

update not_procediment set tipus = 'PROCEDIMENT';

