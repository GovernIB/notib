-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 10/05/22 14:45
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.19/685.yaml::1634114082437-3::limit
ALTER TABLE not_usuari ADD email_alt VARCHAR(200);

-- Changeset db/changelog/changes/1.1.19/693.yaml::1634114082437-3::limit
ALTER TABLE not_config ADD entitat_codi VARCHAR(64);

