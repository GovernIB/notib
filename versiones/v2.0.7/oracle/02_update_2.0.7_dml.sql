INSERT INTO NOT_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_CACHE','SCHEDULLED',9,'Tàsca periòdica de nateja de caches');
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.cache.paisos.provincies', '1296000000', 'Temps en millisegons que expira la cache de paisos', 'INT', 'SCHEDULLED_CACHE');
UPDATE not_notificacio_table t SET CADUCITAT = (SELECT caducitat FROM not_notificacio n WHERE t.id = n.id);
UPDATE NOT_NOTIFICACIO_ENV set plazo_ampliado = 0;