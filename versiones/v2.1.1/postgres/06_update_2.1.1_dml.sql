-- Notib 2.1.1 - Llistat de remeses (interfície React): propietat de configuració, procés inicial i
-- columnes noves de not_notificacio_table. S'executa després de 05_update_2.1.1_ddl.sql.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-29::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (nextval('not_hibernate_sequence'), 22, 'es.caib.notib.app.llistat.remeses.estat.asincron', 'true', 0, 'Indica si la columna estat del llistat de remeses (interfície React) pendent d''actualitzar s''ha de calcular de manera asíncrona i enviar al navegador quan estigui llesta (true), o calcular-la abans de retornar el llistat (false)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-42::limit
-- Procés inicial: crea el registre de not_notificacio_table de les remeses que no en tenen
INSERT INTO not_PROCESSOS_INICIALS (ID, CODI, INIT) VALUES (7, 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE', true);

-- Columnes noves de not_notificacio_table, en una sola passada per la taula i abans de crear-ne els índexs
-- (07_update_2.1.1_ddl.sql): PROCEDIMENT_ID, el de la remesa, i ESTAT_LLISTAT, ENVIANT (ordinal 11) si la
-- remesa està pendent sense intents de registre ni error de Notifica, o si no l'estat de la remesa.
-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-56::limit
UPDATE not_notificacio_table t
   SET procediment_id = (SELECT n.procediment_id FROM not_notificacio n WHERE n.id = t.id),
       estat_llistat = CASE WHEN estat = 0 AND COALESCE(registre_env_intent, 0) = 0 AND notifica_error_date IS NULL THEN 11 ELSE estat END;
