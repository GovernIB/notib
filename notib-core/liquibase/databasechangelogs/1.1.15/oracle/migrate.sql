UPDATE NOT_CONFIG_TYPE SET value = value || ',es.caib.notib.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib' WHERE CODE = 'FIRMA_CLASS';
UPDATE NOT_CONFIG SET JBOSS_PROPERTY = 1 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
UPDATE NOT_CONFIG SET DESCRIPTION = 'Usuari per accedir a portafirmes' WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
UPDATE NOT_CONFIG SET POSITION = 1 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.password', null, 'Password per accedir al Portafirmes', 'FIRMA', 2, 1, 'PASSWORD');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.perfil', 'PADES', 'Perfil de firma', 'FIRMA', 3, 0, 'TEXT');
UPDATE NOT_CONFIG SET POSITION = 4 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.signerEmail';
UPDATE NOT_CONFIG SET POSITION = 5 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.location';
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.certificat', null, 'Carpeta contenidora del certificat de firma', 'FIRMA', 6, 0, 'TEXT');

ALTER TABLE not_notificacio_table ADD enviada_date TIMESTAMP;
UPDATE NOT_NOTIFICACIO_TABLE t SET ENVIADA_DATE = ( SELECT (CASE WHEN p.INTERESSATTIPUS = 'ADMINISTRACIO' THEN n.REGISTRE_DATA ELSE n.NOT_ENV_DATA_NOTIFICA end) AS enviada_date FROM NOT_NOTIFICACIO n JOIN NOT_NOTIFICACIO_ENV e ON e.NOTIFICACIO_ID = n.ID JOIN NOT_PERSONA p ON p.ID  = e.TITULAR_ID WHERE ((p.INTERESSATTIPUS = 'ADMINISTRACIO' AND (n.ESTAT != 0  OR n.estat != 40)) OR (p.INTERESSATTIPUS != 'ADMINISTRACIO' AND (n.ESTAT != 0 OR n.ESTAT != 40 OR n.ESTAT != 2))) AND n.id = t.id AND ROWNUM = 1 );

