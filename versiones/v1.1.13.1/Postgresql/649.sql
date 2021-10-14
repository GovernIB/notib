-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 13/10/21 12:15
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelogPostgresql.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.13.1/649.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio_massiva ADD estat_validacio VARCHAR(32);

ALTER TABLE not_notificacio_massiva ADD estat_proces VARCHAR(32);

ALTER TABLE not_notificacio_massiva ADD num_notificacions INTEGER;

ALTER TABLE not_notificacio_massiva ADD num_validades INTEGER;

ALTER TABLE not_notificacio_massiva ADD num_processades INTEGER;

ALTER TABLE not_notificacio_massiva ADD num_error INTEGER;

update not_notificacio_massiva set num_notificacions = 0;

update not_notificacio_massiva m set num_validades = (select count(id) from not_notificacio_table n where n.notificacio_massiva_id = m.id);

update not_notificacio_massiva m set num_processades = (select count(id) from not_notificacio_table n where n.estat not in (0, 40) and n.notificacio_massiva_id = m.id and n.notifica_error_date is null);

update not_notificacio_massiva m set num_error = (select count(id) from not_notificacio_table n where n.notificacio_massiva_id = m.id and n.notifica_error_date is not null);

update not_notificacio_massiva set estat_validacio = 'ERRONIA' where progress < 0;

update not_notificacio_massiva set estat_validacio = 'FINALITZAT' where progress >= 0;

update not_notificacio_massiva set estat_proces = 'PENDENT' where (num_processades + num_error) = 0;

update not_notificacio_massiva set estat_proces = 'EN_PROCES' where num_processades > 0 and num_error = 0 and num_processades < num_validades;

update not_notificacio_massiva set estat_proces = 'EN_PROCES_AMB_ERRORS' where num_error > 0 and (num_processades + num_error) > 0 and (num_processades + num_error) < num_validades;

update not_notificacio_massiva set estat_proces = 'FINALITZAT' where num_processades = num_validades and num_error = 0;

update not_notificacio_massiva set estat_proces = 'FINALITZAT_AMB_ERRORS' where num_error > 0 and (num_processades + num_error) = num_validades;

