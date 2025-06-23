INSERT INTO NOT_PROCESSOS_INICIALS (CODI, INIT, ID) VALUES ('AFEGIR_CALLBACKS_CUA_JMS', 1, 6);

UPDATE NOT_NOTIFICACIO_TABLE SET estat_mask = 4096 WHERE id IN (SELECT t.id FROM not_notificacio_table t JOIN NOT_NOTIFICACIO_ENV nne ON t.id = nne.NOTIFICACIO_ID WHERE t.ENV_TIPUS  = 2 AND nne.ESTAT_REGISTRE = 5);

UPDATE NOT_NOTIFICACIO_TABLE SET estat_mask = 8192 WHERE id IN (SELECT t.id FROM not_notificacio_table t JOIN NOT_NOTIFICACIO_ENV nne ON t.id = nne.NOTIFICACIO_ID WHERE t.ENV_TIPUS  = 2 AND nne.ESTAT_REGISTRE = 9);

update not_callback set data_creacio = data;

UPDATE NOT_EXPLOT_DIM SET entitat_codi = (SELECT e.codi FROM NOT_ENTITAT e WHERE entitat_id = e.id);

UPDATE NOT_EXPLOT_DIM SET procediment_codi = (SELECT p.codi FROM NOT_PROCEDIMENT p WHERE procediment_id = p.id) WHERE procediment_id IS NOT NULL;
