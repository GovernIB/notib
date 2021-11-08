ALTER TABLE not_notificacio ADD estat_processat_date TIMESTAMP;
ALTER TABLE not_notificacio_table ADD estat_processat_date TIMESTAMP;
ALTER TABLE not_notificacio_audit ADD estat_processat_date TIMESTAMP;

UPDATE not_notificacio SET estat_processat_date = estat_date;
UPDATE not_notificacio noti SET ESTAT_DATE = ( SELECT max(e.NOTIFICA_ESTAT_DATA) FROM not_notificacio n LEFT JOIN not_notificacio_env e ON e.NOTIFICACIO_ID = n.ID WHERE n.estat IN (3,4) AND e.NOTIFICACIO_ID = noti.ID GROUP by e.NOTIFICACIO_ID, n.id );
UPDATE not_notificacio noti SET ESTAT_DATE = ( SELECT max(e.sir_reg_desti_data) FROM not_notificacio n LEFT JOIN not_notificacio_env e ON e.NOTIFICACIO_ID = n.ID WHERE n.estat IN (3,4) AND e.NOTIFICACIO_ID = noti.ID GROUP by e.NOTIFICACIO_ID, n.id ) WHERE ESTAT_DATE IS NULL;
UPDATE NOT_NOTIFICACIO_TABLE t SET ESTAT_PROCESSAT_DATE  = (SELECT ESTAT_PROCESSAT_DATE FROM NOT_NOTIFICACIO n WHERE n.id = t.id);

