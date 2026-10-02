-- Notib 2.1.1 - Llistat de remeses (interfície React): propietat de configuració i procés inicial.
-- S'executa després de 05_update_2.1.1_ddl.sql.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-29::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 22, 'es.caib.notib.app.llistat.remeses.estat.asincron', 'true', 0, 'Indica si la columna estat del llistat de remeses (interfície React) pendent d''actualitzar s''ha de calcular de manera asíncrona i enviar al navegador quan estigui llesta (true), o calcular-la abans de retornar el llistat (false)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-42::limit
-- Procés inicial: crea el registre de not_notificacio_table de les remeses que no en tenen
INSERT INTO not_PROCESSOS_INICIALS (ID, CODI, INIT) VALUES (7, 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE', 1);
