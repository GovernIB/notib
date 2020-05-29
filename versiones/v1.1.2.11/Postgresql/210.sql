ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_CON_DATA timestamp without time zone;
ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_CON_INTENT BIGSERIAL(10) DEFAULT 0 NOT NULL;
ALTER TABLE NOT_NOTIFICACIO_ENV RENAME COLUMN INTENT_NUM TO NOTIFICA_INTENT_NUM;

UPDATE not_notificacio_env set notifica_intent_data = current_timestamp where notifica_intent_data is null;
UPDATE not_notificacio_env set sir_con_data = current_timestamp where sir_con_data is null;