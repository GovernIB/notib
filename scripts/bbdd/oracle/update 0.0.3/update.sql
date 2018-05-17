ALTER TABLE NOT_NOTIFICACIO ADD notifica_datenv TIMESTAMP(6);
ALTER TABLE NOT_NOTIFICACIO ADD not_reintents_env NUMBER(10, 0);
ALTER TABLE NOT_NOTIFICACIO_ENV ADD seu_reintents_env NUMBER(10, 0);
ALTER TABLE NOT_ENTITAT MODIFY (CIF NULL);
ALTER TABLE NOT_NOTIFICACIO DROP COLUMN ERROR_NOT;

UPDATE NOT_NOTIFICACIO SET notifica_datenv = sysdate where notifica_datenv is null;
UPDATE NOT_NOTIFICACIO SET not_reintents_env = 0 where not_reintents_env is null;
UPDATE NOT_NOTIFICACIO_ENV SET seu_data_enviam = sysdate where seu_data_enviam is null;
UPDATE NOT_NOTIFICACIO_ENV SET seu_reintents_env = 0 where seu_reintents_env is null;

