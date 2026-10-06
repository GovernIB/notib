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
