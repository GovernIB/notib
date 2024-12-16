-- Changeset db/changelog/changes/2.0.7/902.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.cache.paisos.provincies', '5000', 'Temps en millisegons que expira la cache de paisos', 'INT', 'SCHEDULLED_CACHE');

INSERT INTO NOT_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_CACHE','SCHEDULLED',9,'Tàsca periòdica de nateja de caches');

-- Changeset db/changelog/changes/2.0.7/961.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio_table ADD caducitat TIMESTAMP;

UPDATE not_notificacio_table t SET CADUCITAT = (SELECT caducitat FROM not_notificacio n WHERE t.id = n.id);