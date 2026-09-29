-- Tornar a la versió anterior (2.1.1 --> 2.0.12)
-- Elimina els NOT NULL afegits a la 2.1.1 que trenquen l'aplicació anterior
-- (l'entitat ConfigEntity no omple config_group_id/config_type_id en crear registres nous,
-- i PagadorPostalEntity no garanteix contracte_num/facturacio_codi_client)
ALTER TABLE not_CONFIG MODIFY config_group_id NULL;
ALTER TABLE not_CONFIG MODIFY config_type_id NULL;
ALTER TABLE not_PAGADOR_POSTAL MODIFY contracte_num NULL;
ALTER TABLE not_PAGADOR_POSTAL MODIFY facturacio_codi_client NULL;
