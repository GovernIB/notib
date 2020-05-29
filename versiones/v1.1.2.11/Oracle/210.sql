ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_CON_DATA TIMESTAMP(6);
ALTER TABLE NOT_NOTIFICACIO_ENV ADD SIR_CON_INTENT NUMBER(10) DEFAULT 0 NOT NULL;
ALTER TABLE NOT_NOTIFICACIO_ENV RENAME COLUMN INTENT_NUM TO NOTIFICA_INTENT_NUM;

UPDATE not_notificacio_env set notifica_intent_data = sysdate where notifica_intent_data is null;
UPDATE not_notificacio_env set sir_con_data = sysdate where sir_con_data is null;