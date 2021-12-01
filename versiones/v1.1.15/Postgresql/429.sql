UPDATE NOT_CONFIG_TYPE SET value = value || ',es.caib.notib.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib' WHERE CODE = 'FIRMA_CLASS';
UPDATE NOT_CONFIG SET JBOSS_PROPERTY = 1 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
UPDATE NOT_CONFIG SET DESCRIPTION = 'Usuari per accedir a portafirmes' WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
UPDATE NOT_CONFIG SET POSITION = 1 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.password', null, 'Password per accedir al Portafirmes', 'FIRMA', 2, 1, 'PASSWORD');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.perfil', 'PADES', 'Perfil de firma', 'FIRMA', 3, 0, 'TEXT');
UPDATE NOT_CONFIG SET POSITION = 4 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.signerEmail';
UPDATE NOT_CONFIG SET POSITION = 5 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.location';
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.certificat', null, 'Carpeta contenidora del certificat de firma', 'FIRMA', 6, 0, 'TEXT');
