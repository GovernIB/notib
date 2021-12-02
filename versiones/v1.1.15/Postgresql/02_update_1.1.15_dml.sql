-- #646
UPDATE NOT_NOTIFICACIO_TABLE t SET ENVIADA_DATE = ( SELECT (CASE WHEN p.INTERESSATTIPUS = 'ADMINISTRACIO' THEN n.REGISTRE_DATA ELSE n.NOT_ENV_DATA_NOTIFICA end) AS enviada_date FROM NOT_NOTIFICACIO n JOIN NOT_NOTIFICACIO_ENV e ON e.NOTIFICACIO_ID = n.ID JOIN NOT_PERSONA p ON p.ID  = e.TITULAR_ID WHERE ((p.INTERESSATTIPUS = 'ADMINISTRACIO' AND (n.ESTAT != 0  OR n.estat != 40)) OR (p.INTERESSATTIPUS != 'ADMINISTRACIO' AND (n.ESTAT != 0 OR n.ESTAT != 40 OR n.ESTAT != 2))) AND n.id = t.id AND ROWNUM = 1 );

-- #429
UPDATE NOT_CONFIG_TYPE SET value = value || ',es.caib.notib.plugin.firmaservidor.FirmaSimpleServidorPluginPortafib' WHERE CODE = 'FIRMA_CLASS';
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.endpoint', null, 'Url de l''API REST del portafirmes', 'FIRMA', 1, 1, 'TEXT');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.auth.username', null, 'Usuari per accedir a portafirmes', 'FIRMA', 2, 1, 'TEXT');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.auth.password', null, 'Password per accedir al Portafirmes', 'FIRMA', 3, 1, 'PASSWORD');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.firmaservidor.portafib.perfil', 'PADES', 'Perfil de firma', 'FIRMA', 4, 0, 'TEXT');
UPDATE NOT_CONFIG SET POSITION = 5 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.signerEmail';
UPDATE NOT_CONFIG SET POSITION = 6 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.location';
UPDATE NOT_CONFIG SET POSITION = 7 WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
UPDATE NOT_CONFIG SET DESCRIPTION = 'Nom d''usuari per a la firma de servidor emprant PortaFIB' WHERE KEY = 'es.caib.notib.plugin.firmaservidor.portafib.username';
