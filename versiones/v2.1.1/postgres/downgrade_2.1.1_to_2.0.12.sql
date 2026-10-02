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
