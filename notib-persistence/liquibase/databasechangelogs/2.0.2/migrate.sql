-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 29/5/24 11:08
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.9.1
-- *********************************************************************

-- Changeset db/changelog/changes/2.0.2/462.yaml::1634114082437-1::limit
ALTER TABLE not_columnes ADD data_creacio_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD data_enviament_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD num_registre_remesa NUMBER(1, 0) DEFAULT '0';

ALTER TABLE not_columnes ADD organ_emisor_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD proc_ser_codi_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD num_expedient_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD concepte_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD creada_per_remesa NUMBER(1, 0) DEFAULT '0';

ALTER TABLE not_columnes ADD interessats_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD estat_remesa NUMBER(1, 0) DEFAULT '1';

UPDATE NOT_NOTIFICACIO_TABLE SET PER_ACTUALITZAR = 1;

-- Changeset db/changelog/changes/2.0.2/769.yaml::1634114082437-1::limit
UPDATE NOT_NOTIFICACIO_TABLE SET PER_ACTUALITZAR = 1;

-- Changeset db/changelog/changes/2.0.2/773.yaml::1634114082437-1::limit
ALTER TABLE NOT_COLUMNES DROP COLUMN TITULAR_NIF;

ALTER TABLE NOT_COLUMNES ADD DATA_CREACIO NUMBER(1, 0) DEFAULT '0';

UPDATE NOT_NOTIFICACIO_ENV_TABLE t SET REGISTRE_DATA = (SELECT nne.REGISTRE_DATA FROM NOT_NOTIFICACIO_ENV nne WHERE t.id = nne.id);

-- Changeset db/changelog/changes/2.0.2/804.yaml::1634114082437-1::limit
ALTER TABLE not_usuari ADD NUM_ELEMENTS_PAGINA_DEFECTE VARCHAR2(30 CHAR) DEFAULT 'DEU';

-- Changeset db/changelog/changes/2.0.2/818.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio_table ADD REGISTRE_NUMS VARCHAR2(1024 CHAR);

UPDATE NOT_NOTIFICACIO_TABLE SET REGISTRE_NUMS = (select listagg(E.REGISTRE_NUMERO_FORMATAT , ', ') WITHIN GROUP (ORDER BY E.ID) FROM NOT_NOTIFICACIO_ENV E WHERE E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);

-- Changeset db/changelog/changes/2.0.2/835.yaml::1665140886758-2::limit
ALTER TABLE not_notificacio_env ADD SIR_FI_POOLING NUMBER(1) DEFAULT '0';

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.consulta.sir.dies.intents', '7', 'Dies que el pooling seguirà fent la consulta si aquesta no retorna un estat final', 'GENERAL', 0, 0, 'INT', 0);
