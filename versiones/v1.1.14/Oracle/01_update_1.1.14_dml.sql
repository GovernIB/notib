--#429
UPDATE NOT_CONFIG_TYPE SET value = value || ',es.caib.notib.plugin.firmaservidor.FirmaSimplePluginPortafib' WHERE CODE = 'FIRMA_CLASS';
INSERT INTO NOT_CONFIG ("KEY", VALUE, DESCRIPTION, GROUP_CODE,  "POSITION", JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.plugin.api.firma.en.servidor.simple.perfil', 'PADES', 'Perfil de firma', 'FIRMA',0, 0, 'TEXT');

--#644
UPDATE not_notificacio SET estatProcessatDate = estatDate;
UPDATE not_notificacio noti SET ESTAT_DATE = ( SELECT max(e.NOTIFICA_ESTAT_DATA) FROM not_notificacio n LEFT JOIN not_notificacio_env e ON e.NOTIFICACIO_ID = n.ID WHERE n.estat IN (3,4) AND e.NOTIFICACIO_ID = noti.ID GROUP by e.NOTIFICACIO_ID, n.id );
UPDATE not_notificacio noti SET ESTAT_DATE = ( SELECT max(e.sir_reg_desti_data) FROM not_notificacio n LEFT JOIN not_notificacio_env e ON e.NOTIFICACIO_ID = n.ID WHERE n.estat IN (3,4) AND e.NOTIFICACIO_ID = noti.ID GROUP by e.NOTIFICACIO_ID, n.id ) WHERE ESTAT_DATE IS NULL;
UPDATE NOT_NOTIFICACIO_TABLE t SET ESTAT_PROCESSAT_DATE  = (SELECT ESTAT_PROCESSAT_DATE FROM NOT_NOTIFICACIO n WHERE n.id = t.id);