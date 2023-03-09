UPDATE NOT_NOTIFICACIO_TABLE nt SET nt.registre_env_intent = (SELECT n.registre_env_intent FROM NOT_NOTIFICACIO n WHERE n.ID = nt.ID) WHERE nt.estat = 0;
UPDATE NOT_NOTIFICACIO_TABLE SET estat_mask = 1 WHERE estat = 0 and registre_env_intent <> 0;
UPDATE NOT_NOTIFICACIO_TABLE SET estat_mask = 2048 WHERE estat = 0 and registre_env_intent = 0;