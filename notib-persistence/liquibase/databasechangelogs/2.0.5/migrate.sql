-- Changeset db/changelog/changes/2.0.5/911.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugin.gesdoc.document.llindar.dies.comprimir', '10', 'Comprimeix els documents anteriors al valor en dies', 'GES_DOC', 0, 0, 'INT', 0);

-- Changeset db/changelog/changes/2.0.5/916.yaml::1634114082437-1::limit
ALTER TABLE NOTIB.NOT_NOTIFICACIO_ENV RENAME COLUMN NOTIFICA_ERROR_EVENT_ID TO "ULTIM_EVENT";

ALTER TABLE NOTIB.NOT_NOTIFICACIO_ENV DROP CONSTRAINT NOT_NOTENV_NOTEVENT_ERROR_FK;

ALTER TABLE NOTIB.NOT_NOTIFICACIO_ENV ADD CONSTRAINT NOT_NOTEVENT_ULTIM_EVENT_FK FOREIGN KEY (ULTIM_EVENT) REFERENCES NOTIB.NOT_NOTIFICACIO_EVENT(ID);

UPDATE NOT_NOTIFICACIO_ENV env SET env.ULTIM_EVENT = ( SELECT nne2.id FROM NOT_NOTIFICACIO_EVENT nne2 WHERE env.id = nne2.NOTIFICACIO_ENV_ID  AND nne2."DATA" = (SELECT max(nne."DATA") FROM NOT_NOTIFICACIO_EVENT nne WHERE nne.NOTIFICACIO_ENV_ID = env.id AND nne.TIPUS != 8 and nne.TIPUS != 9)) WHERE env.CREATEDDATE > TO_DATE('25/03/2024', 'dd/MM/yyyy');

UPDATE NOT_NOTIFICACIO_TABLE n SET ENVIADA_DATE = (SELECT MAX(DATA) FROM NOT_NOTIFICACIO_EVENT nne WHERE nne.NOTIFICACIO_ID = n.id AND nne.TIPUS != 8 and nne.TIPUS != 9) WHERE n.CREATEDDATE > TO_DATE('25/03/2024', 'dd/MM/yyyy');

-- Changeset db/changelog/changes/2.0.5/conCsvDocumentsOriginals.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE,CONFIGURABLE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.conversio.original.url.csv', 1, 'Url on generar imprimible per CSV', 'ARXIU',1 );

INSERT INTO NOT_CONFIG (POSITION, KEY, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE,CONFIGURABLE) VALUES (0, 'es.caib.notib.plugin.arxiu.caib.conversio.original.url.uuid', 1, 'Url on generar original per uuId', 'ARXIU',1 );

-- Changeset db/changelog/changes/2.0.5/logs.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.EMAIL', 'false', 'Mostrar logs dels enviaments per email', 'LOGS', 16, 0, 'BOOL', 0);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.ADVISER', 'false', 'Mostrar logs de adviser', 'LOGS', 17, 0, 'BOOL', 0);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.METRIQUES_SISTEMA', 'false', 'Mostra els logs del estat del sistema', 'LOGS', 19, 0, 'BOOL', 0);

-- Changeset db/changelog/changes/2.0.5/maxAccionsMassives.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.maxim.accions.massives', '250', 'Nombre máxim de files que es pot seleccionar com a usuari, per fer accions massives. Per defecte 250.', 'GENERAL', 0, 0, 'INT', 0);
