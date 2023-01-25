-- INDEXOS
-- ----------------

-- NOT_NOTIFICACIO
CREATE INDEX not_not_estatrint_idx on NOT_NOTIFICACIO (ESTAT, REGISTRE_ENV_INTENT);

-- NOT_NOTIFICACIO_ENV
CREATE INDEX not_notenv_estat_idx on NOT_NOTIFICACIO_ENV (NOTIFICA_ESTAT);
CREATE INDEX not_notenv_regpen_idx on NOT_NOTIFICACIO_ENV (REGISTRE_ESTAT_FINAL, NOTIFICA_ESTAT, SIR_CON_INTENT);
CREATE INDEX not_notenv_estatsrint_idx on NOT_NOTIFICACIO_ENV (NOTIFICA_ESTAT_FINAL, NOTIFICA_ESTAT, NOTIFICA_INTENT_NUM);


-- MULTITHREAD CONFIG --
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) values ('es.caib.notib.multithread',0,'Permetre execucions multithread','SCHEDULLED',0,0,'BOOL');


-- optimitzacions.yaml
UPDATE NOT_NOTIFICACIO_TABLE SET TITULAR = (select listagg(NOM || ' ' || LLINATGE1 || ' ' || LLINATGE2 || ' ' || NIF || ' ' || RAO_SOCIAL,', ') WITHIN GROUP (ORDER BY NOTIFICACIO_ENV_ID) FROM NOT_PERSONA P, NOT_NOTIFICACIO_ENV E WHERE P.ID = E.TITULAR_ID AND E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
UPDATE NOT_NOTIFICACIO_TABLE SET notifica_ids = (select listagg(E.NOTIFICA_ID, ', ') WITHIN GROUP (ORDER BY E.ID) FROM NOT_NOTIFICACIO_ENV E WHERE E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (select SUM(DISTINCT CASE E.NOTIFICA_ESTAT WHEN 15 THEN 1 WHEN 23 THEN 2 WHEN 24 THEN 4 WHEN 22 THEN 8 WHEN 25 THEN 16 WHEN 10 THEN 32 WHEN 14 THEN 64 WHEN 20 THEN 128 WHEN 27 THEN 256 WHEN 28 THEN 512 WHEN 29 THEN 1024 ELSE 0 END) as MASK FROM NOT_NOTIFICACIO_ENV_TABLE E WHERE E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 2048) WHERE ESTAT = 15 AND registre_env_intent = 0 AND NOTIFICA_ERROR_DATE IS NULL;

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.log.path', null,'Ruta del fitxer de log', 'GENERAL', 12, 1, 'TEXT');

CREATE INDEX NOT_TABLE_ENTITAT_INDEX ON not_notificacio_table(ENTITAT_ID);
CREATE INDEX NOT_TABLE_ENV_ENTITAT_INDEX ON not_notificacio_env_table(ENTITAT_ID);

