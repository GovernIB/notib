-- Tornar a aquesta versió (2.0.12 --> 2.1.1) després d'haver fet un downgrade (2.1.1 --> 2.0.12)

-- Recalcula config_group_id/config_type_id per si s'han creat registres nous a NOT_CONFIG
-- mentre l'aplicació anterior (que no omple aquests camps) hi ha estat escrivint
UPDATE not_config a SET config_group_id = (SELECT b.id FROM not_config_group b WHERE a.GROUP_CODE = b.code) WHERE a.GROUP_CODE IS NOT NULL AND a.config_group_id IS NULL;
UPDATE not_config a SET config_type_id = (SELECT b.id FROM not_config_type b WHERE a.TYPE_CODE = b.code) WHERE a.TYPE_CODE IS NOT NULL AND a.config_type_id IS NULL;

ALTER TABLE not_CONFIG MODIFY config_group_id NOT NULL;
ALTER TABLE not_CONFIG MODIFY config_type_id NOT NULL;

-- ATENCIO: contracte_num i facturacio_codi_client son dades de negoci, no derivables
-- d'altres columnes. Abans d'executar les dues linies seguents, revisar manualment si
-- l'aplicació anterior ha creat files amb aquests camps a NULL i decidir quin valor els
-- correspon (l'ALTER fallara si hi queda alguna fila NULL).
ALTER TABLE not_PAGADOR_POSTAL MODIFY contracte_num NOT NULL;
ALTER TABLE not_PAGADOR_POSTAL MODIFY facturacio_codi_client NOT NULL;

-- not_notificacio_table: l'aplicació anterior no omple PROCEDIMENT_ID ni actualitza ORGAN_ID quan canvia l'òrgan
-- de la remesa, i el llistat de remeses (interfície React) hi filtra els permisos
UPDATE not_NOTIFICACIO_TABLE t
   SET (PROCEDIMENT_ID, ORGAN_ID) = (SELECT n.PROCEDIMENT_ID, n.ORGAN_GESTOR FROM not_NOTIFICACIO n WHERE n.ID = t.ID)
 WHERE EXISTS (SELECT 1 FROM not_NOTIFICACIO n WHERE n.ID = t.ID
                 AND (DECODE(n.PROCEDIMENT_ID, t.PROCEDIMENT_ID, 0, 1) = 1 OR DECODE(n.ORGAN_GESTOR, t.ORGAN_ID, 0, 1) = 1));

-- Procés inicial: crea el registre de not_notificacio_table de les remeses que l'aplicació anterior hagi creat sense
-- (si hi falla, l'aplicació anterior no desfà l'alta de la remesa)
DELETE FROM not_PROCESSOS_INICIALS WHERE CODI = 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE';
INSERT INTO not_PROCESSOS_INICIALS (ID, CODI, INIT) VALUES (7, 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE', 1);

-- ESTAT_LLISTAT de not_notificacio_table: l'aplicació anterior actualitza ESTAT_STRING però no aquesta columna,
-- per la qual s'ordena la columna estat del llistat de remeses (interfície React)
UPDATE not_NOTIFICACIO_TABLE
   SET ESTAT_LLISTAT = CASE WHEN ESTAT = 0 AND COALESCE(REGISTRE_ENV_INTENT, 0) = 0 AND NOTIFICA_ERROR_DATE IS NULL THEN 11 ELSE ESTAT END
 WHERE DECODE(ESTAT_LLISTAT, CASE WHEN ESTAT = 0 AND COALESCE(REGISTRE_ENV_INTENT, 0) = 0 AND NOTIFICA_ERROR_DATE IS NULL THEN 11 ELSE ESTAT END, 0, 1) = 1;
