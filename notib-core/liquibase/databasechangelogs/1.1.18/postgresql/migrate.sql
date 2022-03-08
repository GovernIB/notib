-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 04/03/22 16:40
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.18/390.yaml::1634114082437-1::limit
ALTER TABLE not_organ_gestor ADD codi_pare VARCHAR(32);

-- Changeset db/changelog/changes/1.1.18/701.yaml::1638376153806-2::limit
ALTER TABLE not_notificacio ALTER COLUMN registre_data TYPE TIMESTAMP WITHOUT TIME ZONE USING (registre_data::TIMESTAMP WITHOUT TIME ZONE);

update not_notificacio n set registre_data = (select min(registre_data) from not_notificacio_env e where e.notificacio_id = n.id);

-- Changeset db/changelog/changes/1.1.18/709.yaml::1638376153806-3::limit
ALTER TABLE not_persona ADD document_tipus VARCHAR(32);

