-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 28/10/21 12:42
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.14/557.yaml::1634569326780-1::limit
ALTER TABLE not_procediment ADD tipus VARCHAR(32);
ALTER TABLE not_notificacio_table ADD procediment_tipus VARCHAR(32);
ALTER TABLE not_notificacio_env_table ADD procediment_tipus VARCHAR(32);

update not_procediment set tipus = 'PROCEDIMENT';
update not_notificacio_table set procediment_tipus = 'PROCEDIMENT' where procediment_codi is not null;
update not_notificacio_env_table set procediment_tipus = 'PROCEDIMENT' where procediment_codi_notib is not null;
update NOT_ACL_CLASS set CLASS = 'es.caib.notib.logic.entity.ProcSerOrganEntity' WHERE CLASS = 'es.caib.notib.logic.entity.ProcedimentOrganEntity';

ALTER TABLE not_notificacio ADD estat_processat_date TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE not_notificacio_table ADD estat_processat_date TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE not_notificacio_audit ADD estat_processat_date TIMESTAMP WITHOUT TIME ZONE;

UPDATE not_notificacio SET estatProcessatDate = estatDate;
UPDATE not_notificacio noti SET ESTAT_DATE = ( SELECT max(e.NOTIFICA_ESTAT_DATA) FROM not_notificacio n LEFT JOIN not_notificacio_env e ON e.NOTIFICACIO_ID = n.ID WHERE n.estat IN (3,4) AND e.NOTIFICACIO_ID = noti.ID GROUP by e.NOTIFICACIO_ID, n.id );
UPDATE not_notificacio noti SET ESTAT_DATE = ( SELECT max(e.sir_reg_desti_data) FROM not_notificacio n LEFT JOIN not_notificacio_env e ON e.NOTIFICACIO_ID = n.ID WHERE n.estat IN (3,4) AND e.NOTIFICACIO_ID = noti.ID GROUP by e.NOTIFICACIO_ID, n.id ) WHERE ESTAT_DATE IS NULL;
UPDATE NOT_NOTIFICACIO_TABLE t SET ESTAT_PROCESSAT_DATE  = (SELECT ESTAT_PROCESSAT_DATE FROM NOT_NOTIFICACIO n WHERE n.id = t.id);FICACIO_TABLE t SET ESTAT_PROCESSAT_DATE  = (SELECT ESTAT_PROCESSAT_DATE FROM NOT_NOTIFICACIO n WHERE n.id = t.id);
