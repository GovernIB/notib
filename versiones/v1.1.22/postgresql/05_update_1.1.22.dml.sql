-- INDEXOS
-- ----------------

-- MULTITHREAD CONFIG --
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) values ('es.caib.notib.multithread',0,'Permetre execucions multithread','SCHEDULLED',0,0,'BOOL');

-- optimitzacions.yaml
UPDATE NOT_NOTIFICACIO_TABLE SET TITULAR = (select listagg(NOM || ' ' || LLINATGE1 || ' ' || LLINATGE2 || ' ' || NIF || ' ' || RAO_SOCIAL,', ') WITHIN GROUP (ORDER BY NOTIFICACIO_ENV_ID) FROM NOT_PERSONA P, NOT_NOTIFICACIO_ENV E WHERE P.ID = E.TITULAR_ID AND E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
UPDATE NOT_NOTIFICACIO_TABLE SET notifica_ids = (select listagg(E.NOTIFICA_ID, ', ') WITHIN GROUP (ORDER BY E.ID) FROM NOT_NOTIFICACIO_ENV E WHERE E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = 0;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 1) WHERE ESTAT = 0;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 2) WHERE ESTAT = 1;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 4) WHERE ESTAT = 2;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 8) WHERE ESTAT = 3;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 16) WHERE ESTAT = 4;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 512) WHERE ESTAT = 9;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 1024) WHERE ESTAT = 10;
--UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 2048) WHERE ESTAT = 11;
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = ESTAT_MASK +(select SUM(DISTINCT CASE E.NOTIFICA_ESTAT WHEN 10 THEN 32 WHEN 14 THEN 64 WHEN 20 THEN 128 WHEN 27 THEN 256 ELSE 0 END) as MASK FROM NOT_NOTIFICACIO_ENV_TABLE E WHERE E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
-- INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.log.path', null,'Ruta del fitxer de log', 'GENERAL', 12, 1, 'TEXT');

-- 741
INSERT INTO not_acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) SELECT acl_object_identity, ace_order, sid, 8192, granting, audit_success, audit_failure FROM not_acl_entry WHERE mask = 1024;