-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 29/5/24 11:08
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.9.1
-- *********************************************************************

-- Changeset db/changelog/changes/2.0.4/883.yaml::1634114082437-1::limit
UPDATE NOT_CONFIG SET DESCRIPTION = 'Nombre màxim de vegades que una mateixa comunicació SIR s''intentarà enviar a registre abans d''obtenir una resposta satisfactòria' WHERE KEY = 'es.caib.notib.tasca.enviament.actualitzacio.estat.registre.reintents.maxim';

UPDATE NOT_CONFIG SET DESCRIPTION = 'Dies que el pooling seguirà fent la consulta si aquesta no retorna un estat final' WHERE KEY = 'es.caib.notib.consulta.sir.dies.intents';

UPDATE NOT_CONFIG SET DESCRIPTION = 'Mostrar logs del plugin de validació de signatura' WHERE KEY = 'es.caib.notib.log.tipus.VALIDATE_SIGNATURE';

