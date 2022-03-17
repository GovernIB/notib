-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 04/03/22 16:31
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.18/390.yaml::1634114082437-1::limit
ALTER TABLE not_organ_gestor ADD codi_pare VARCHAR2(32 CHAR);

-- Changeset db/changelog/changes/1.1.18/701.yaml::1638376153806-2::limit
ALTER TABLE not_notificacio MODIFY registre_data TIMESTAMP;

update not_notificacio n set registre_data = (select min(registre_data) from not_notificacio_env e where e.notificacio_id = n.id);

-- Changeset db/changelog/changes/1.1.18/709.yaml::1638376153806-3::limit
ALTER TABLE not_persona ADD document_tipus VARCHAR2(32 CHAR);
ALTER TABLE not_notificacio_audit MODIFY estat VARCHAR2(32 CHAR);
ALTER TABLE not_notificacio_env ADD per_email NUMBER(1);
ALTER TABLE not_notificacio ADD justificant_creat NUMBER(1);
UPDATE not_notificacio_env SET per_email = '0';
UPDATE not_notificacio SET justificant_creat = '0';
