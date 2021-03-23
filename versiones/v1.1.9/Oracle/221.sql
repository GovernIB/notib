CREATE INDEX NOT_NOTIF_DOCUMENT_ID_INDEX
    ON NOT_NOTIFICACIO(DOCUMENT_ID);

CREATE INDEX NOT_NOTENV_TITULAR_ID_INDEX
    ON NOT_NOTIFICACIO_ENV(TITULAR_ID);

CREATE INDEX NOT_PERSONA_NOTENV_ID_INDEX
    ON NOT_PERSONA(NOTIFICACIO_ENV_ID);

ALTER TABLE NOT_NOTIFICACIO
    ADD IS_ERROR_LAST_EVENT number(1, 0) default 0;
