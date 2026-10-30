-- Tornar a aquesta versió (2.0.12 --> 2.1.1) després d'haver fet un downgrade (2.1.1 --> 2.0.12)

-- Recalcula config_group_id/config_type_id per si s'han creat registres nous a NOT_CONFIG
-- mentre l'aplicació anterior (que no omple aquests camps) hi ha estat escrivint
UPDATE not_config a SET config_group_id = (SELECT b.id FROM not_config_group b WHERE a.GROUP_CODE = b.code) WHERE a.GROUP_CODE IS NOT NULL AND a.config_group_id IS NULL;
UPDATE not_config a SET config_type_id = (SELECT b.id FROM not_config_type b WHERE a.TYPE_CODE = b.code) WHERE a.TYPE_CODE IS NOT NULL AND a.config_type_id IS NULL;

ALTER TABLE not_CONFIG ALTER COLUMN config_group_id SET NOT NULL;
ALTER TABLE not_CONFIG ALTER COLUMN config_type_id SET NOT NULL;

-- ATENCIO: contracte_num i facturacio_codi_client son dades de negoci, no derivables
-- d'altres columnes. Abans d'executar les dues linies seguents, revisar manualment si
-- l'aplicació anterior ha creat files amb aquests camps a NULL i decidir quin valor els
-- correspon (l'ALTER fallara si hi queda alguna fila NULL).
ALTER TABLE not_PAGADOR_POSTAL ALTER COLUMN contracte_num SET NOT NULL;
ALTER TABLE not_PAGADOR_POSTAL ALTER COLUMN facturacio_codi_client SET NOT NULL;

-- not_notificacio_table: l'aplicació anterior no omple procediment_id ni actualitza organ_id quan canvia l'òrgan
-- de la remesa, i el llistat de remeses (interfície React) hi filtra els permisos
UPDATE not_notificacio_table t
   SET procediment_id = n.procediment_id, organ_id = n.organ_gestor
  FROM not_notificacio n
 WHERE n.id = t.id
   AND (n.procediment_id IS DISTINCT FROM t.procediment_id OR n.organ_gestor IS DISTINCT FROM t.organ_id);

-- Procés inicial: crea el registre de not_notificacio_table de les remeses que l'aplicació anterior hagi creat sense
-- (si hi falla, l'aplicació anterior no desfà l'alta de la remesa)
DELETE FROM not_PROCESSOS_INICIALS WHERE CODI = 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE';
INSERT INTO not_PROCESSOS_INICIALS (ID, CODI, INIT) VALUES (7, 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE', true);

-- estat_llistat de not_notificacio_table: l'aplicació anterior actualitza estat_string però no aquesta columna,
-- per la qual s'ordena la columna estat del llistat de remeses (interfície React)
UPDATE not_notificacio_table
   SET estat_llistat = CASE WHEN estat = 0 AND COALESCE(registre_env_intent, 0) = 0 AND notifica_error_date IS NULL THEN 11 ELSE estat END
 WHERE estat_llistat IS DISTINCT FROM (CASE WHEN estat = 0 AND COALESCE(registre_env_intent, 0) = 0 AND notifica_error_date IS NULL THEN 11 ELSE estat END);

-- Propietats de configuració: torna a crear els subgrups de GENERAL, SCHEDULLED, FIRMA i ASPECTE i hi mou les
-- propietats (inclou les que l'aplicació anterior hagi creat als grups pare). Mateixes sentències que
-- 08_update_2.1.1_dml.sql.
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (nextval('not_hibernate_sequence'), 'GENERAL_COMUNES', 'GENERAL', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'GENERAL'), 0, 'Configuracions comunes');
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (nextval('not_hibernate_sequence'), 'SCHEDULLED_COMUNES', 'SCHEDULLED', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'SCHEDULLED'), 0, 'Configuració comuna de les tasques periòdiques');
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (nextval('not_hibernate_sequence'), 'FIRMA_SERVIDOR', 'FIRMA', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'FIRMA'), 0, 'Configuració de la firma en servidor');
INSERT INTO not_CONFIG_GROUP (ID, CODE, PARENT_CODE, PARENT_ID, POSITION, DESCRIPTION) VALUES (nextval('not_hibernate_sequence'), 'ASPECTE_COMUNES', 'ASPECTE', (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'ASPECTE'), 0, 'Aspecte comú de l''aplicació');
UPDATE not_CONFIG SET GROUP_CODE = 'GENERAL_COMUNES', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'GENERAL_COMUNES') WHERE GROUP_CODE = 'GENERAL';
UPDATE not_CONFIG SET GROUP_CODE = 'SCHEDULLED_COMUNES', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'SCHEDULLED_COMUNES') WHERE GROUP_CODE = 'SCHEDULLED';
UPDATE not_CONFIG SET GROUP_CODE = 'FIRMA_SERVIDOR', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'FIRMA_SERVIDOR') WHERE GROUP_CODE = 'FIRMA';
UPDATE not_CONFIG SET GROUP_CODE = 'ASPECTE_COMUNES', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'ASPECTE_COMUNES') WHERE GROUP_CODE = 'ASPECTE';
