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
