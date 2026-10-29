-- Tornar a la versió anterior (2.1.1 --> 2.0.12)
-- Elimina els NOT NULL afegits a la 2.1.1 que trenquen l'aplicació anterior
-- (l'entitat ConfigEntity no omple config_group_id/config_type_id en crear registres nous,
-- i PagadorPostalEntity no garanteix contracte_num/facturacio_codi_client)
ALTER TABLE not_CONFIG ALTER COLUMN config_group_id DROP NOT NULL;
ALTER TABLE not_CONFIG ALTER COLUMN config_type_id DROP NOT NULL;
ALTER TABLE not_PAGADOR_POSTAL ALTER COLUMN contracte_num DROP NOT NULL;
ALTER TABLE not_PAGADOR_POSTAL ALTER COLUMN facturacio_codi_client DROP NOT NULL;

-- El procés inicial CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE (2.1.1) no existeix a l'aplicació anterior: si encara
-- estava pendent, en carregar-lo fallaria l'execució de tots els processos inicials. El reupgrade el torna a crear.
DELETE FROM not_PROCESSOS_INICIALS WHERE CODI = 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE';

-- Propietats de configuració: l'aplicació anterior no té subgrups per a les propietats de GENERAL, SCHEDULLED, FIRMA
-- i ASPECTE, i el codi de FIRMA (recàrrega del plugin i propietats per entitat) depèn del codi de grup 'FIRMA'.
-- Torna les propietats als grups pare i elimina els subgrups creats a 08_update_2.1.1_dml.sql.
UPDATE not_CONFIG SET GROUP_CODE = 'GENERAL', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'GENERAL') WHERE GROUP_CODE = 'GENERAL_COMUNES';
UPDATE not_CONFIG SET GROUP_CODE = 'SCHEDULLED', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'SCHEDULLED') WHERE GROUP_CODE = 'SCHEDULLED_COMUNES';
UPDATE not_CONFIG SET GROUP_CODE = 'FIRMA', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'FIRMA') WHERE GROUP_CODE = 'FIRMA_SERVIDOR';
UPDATE not_CONFIG SET GROUP_CODE = 'ASPECTE', CONFIG_GROUP_ID = (SELECT g.ID FROM not_CONFIG_GROUP g WHERE g.CODE = 'ASPECTE') WHERE GROUP_CODE = 'ASPECTE_COMUNES';
DELETE FROM not_CONFIG_GROUP WHERE CODE IN ('GENERAL_COMUNES', 'SCHEDULLED_COMUNES', 'FIRMA_SERVIDOR', 'ASPECTE_COMUNES');
