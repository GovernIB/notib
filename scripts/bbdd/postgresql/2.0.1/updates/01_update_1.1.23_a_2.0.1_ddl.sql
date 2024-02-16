CREATE TABLE state_machine (machine_id VARCHAR(255) NOT NULL, state VARCHAR(255), state_machine_context OID, CONSTRAINT state_machine_pkey PRIMARY KEY (machine_id));

ALTER TABLE not_notificacio_env ADD CONSTRAINT not_enviament_ref_uk UNIQUE (notifica_ref);

ALTER TABLE not_columnes RENAME COLUMN created_date TO data_enviament;

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ESTAT_STRING VARCHAR(2000);
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD DOCUMENT_ID BIGINT;
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ENV_CER_DATA TIMESTAMP WITHOUT TIME ZONE;
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD REG_ENV_PENDENTS BOOLEAN DEFAULT FALSE;
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD PER_ACTUALITZAR BOOLEAN DEFAULT TRUE;

ALTER TABLE not_procediment ADD actiu BOOLEAN DEFAULT TRUE;

GRANT SELECT, UPDATE, INSERT, DELETE ON STATE_MACHINE TO WWW_NOTIB;

ALTER TABLE NOT_ENTREGA_POSTAL ALTER COLUMN DOM_POBLACIO VARCHAR(255);
ALTER TABLE NOT_NOTIFICACIO_ENV ALTER COLUMN NOTIFICA_CER_HASH VARCHAR(255);

ALTER TABLE NOT_ENTREGA_POSTAL MODIFY DOM_POBLACIO VARCHAR2(255);
