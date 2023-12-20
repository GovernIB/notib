CREATE TABLE state_machine (machine_id VARCHAR2(255 CHAR) NOT NULL, state VARCHAR2(255 CHAR), state_machine_context BLOB, CONSTRAINT PK_STATE_MACHINE PRIMARY KEY (machine_id));

ALTER TABLE not_notificacio_env ADD CONSTRAINT not_enviament_ref_uk UNIQUE (notifica_ref);

ALTER TABLE not_columnes RENAME COLUMN created_date TO data_enviament;

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ESTAT_STRING VARCHAR2(2000 CHAR);
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD DOCUMENT_ID NUMBER(38, 0);
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ENV_CER_DATA TIMESTAMP;
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD REG_ENV_PENDENTS NUMBER(1) DEFAULT '0';
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD PER_ACTUALITZAR NUMBER(1) DEFAULT '1';

ALTER TABLE not_procediment ADD actiu NUMBER(1) DEFAULT '1';

GRANT SELECT, UPDATE, INSERT, DELETE ON STATE_MACHINE TO WWW_NOTIB;

ALTER TABLE NOT_ENTREGA_POSTAL MODIFY DOM_POBLACIO VARCHAR2(255 CHAR);
ALTER TABLE NOT_NOTIFICACIO_ENV ALTER COLUMN NOTIFICA_CER_HASH VARCHAR(255);