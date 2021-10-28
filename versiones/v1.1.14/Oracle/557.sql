-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 28/10/21 12:42
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.14/557.yaml::1634569326780-1::limit
ALTER TABLE not_procediment ADD tipus VARCHAR2(32);
ALTER TABLE not_notificacio_table ADD procediment_tipus VARCHAR2(32);
ALTER TABLE not_notificacio_env_table ADD procediment_tipus VARCHAR2(32);

update not_procediment set tipus = 'PROCEDIMENT';
update not_notificacio_table set procediment_tipus = 'PROCEDIMENT' where procediment_codi is not null;
update not_notificacio_env_table set procediment_tipus = 'PROCEDIMENT' where procediment_codi_notib is not null;
update NOT_ACL_CLASS set CLASS = 'es.caib.notib.core.entity.ProcSerOrganEntity' WHERE CLASS = 'es.caib.notib.core.entity.ProcedimentOrganEntity';
