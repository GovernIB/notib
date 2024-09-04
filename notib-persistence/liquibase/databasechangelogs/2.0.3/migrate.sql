-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 29/5/24 11:08
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.9.1
-- *********************************************************************

-- Changeset db/changelog/changes/2.0.3/860.yaml::1714736924374-1::limit
ALTER TABLE not_notificacio_table ADD DELETED NUMBER(1, 0) DEFAULT '0';

ALTER TABLE not_notificacio ADD DELETED NUMBER(1, 0) DEFAULT '0';

-- Changeset db/changelog/changes/2.0.3/872.yaml::1714736924374-1::limit
UPDATE NOT_CONFIG_TYPE SET value = 'es.caib.notib.plugin.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin,org.fundaciobit.plugins.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin,es.caib.notib.plugin.valsig.ValidacioFirmesPluginMock' WHERE CODE = 'VALSIG_CLASS';
