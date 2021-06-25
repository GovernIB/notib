-- #379 Afegir un sistema d'avisos generals
CREATE TABLE NOT_NOTIFICACIO_MASSIVA
(
    ID                   NUMBER(19)               NOT NULL,
    ENTITAT_ID           NUMBER(19)               NOT NULL,
    CSV_GESDOC_ID        VARCHAR2(64 CHAR)        NOT NULL,
    ZIP_GESDOC_ID        VARCHAR2(64 CHAR),
    CSV_FILENAME         VARCHAR2(200 CHAR)       NOT NULL,
    ZIP_FILENAME         VARCHAR2(200 CHAR),
    CADUCITAT            TIMESTAMP(6)             NOT NULL,
    EMAIL                VARCHAR2(64 CHAR),
    PAGADOR_POSTAL_ID    NUMBER(19, 0),
    RESUM_GESDOC_ID      VARCHAR2(64 CHAR),
    ERRORS_GESDOC_ID     VARCHAR2(64 CHAR),
    PROGRESS             NUMBER(3)                DEFAULT 0 NOT NULL,

    CREATEDBY_CODI       VARCHAR2(64),
    CREATEDDATE          TIMESTAMP(6),
    LASTMODIFIEDBY_CODI  VARCHAR2(64),
    LASTMODIFIEDDATE     TIMESTAMP(6)
);

ALTER TABLE NOT_NOTIFICACIO_MASSIVA ADD (
    CONSTRAINT NOT_NOTIFICACIO_MASSIVA_PK PRIMARY KEY (ID));

CREATE INDEX NOT_PAGADOR_EK ON NOT_NOTIFICACIO_MASSIVA(PAGADOR_POSTAL_ID);
ALTER TABLE NOT_NOTIFICACIO_MASSIVA
    ADD CONSTRAINT NOT_MASSIVA_PAGADOR_POSTAL_FK FOREIGN KEY (PAGADOR_POSTAL_ID) REFERENCES NOT_PAGADOR_POSTAL(ID);

CREATE INDEX NOT_ENTITAT_EK ON NOT_NOTIFICACIO_MASSIVA(ENTITAT_ID);
ALTER TABLE NOT_NOTIFICACIO_MASSIVA
    ADD CONSTRAINT NOT_MASSIVA_ENTITAT_FK FOREIGN KEY (ENTITAT_ID) REFERENCES NOT_ENTITAT(ID);

ALTER TABLE NOT_NOTIFICACIO
    ADD NOTIFICACIO_MASSIVA_ID NUMBER(19) DEFAULT NULL;

ALTER TABLE NOT_NOTIFICACIO_TABLE
    ADD NOTIFICACIO_MASSIVA_ID NUMBER(19) DEFAULT NULL;

ALTER TABLE NOT_NOTIFICACIO_TABLE
    ADD CONSTRAINT NOT_NOT_TABLE_NOT_MASSIVA_FK FOREIGN KEY (NOTIFICACIO_MASSIVA_ID) REFERENCES NOT_NOTIFICACIO_MASSIVA(ID);

CREATE INDEX NOT_NOT_TABLE_NOT_MASSIVA_EK ON NOT_NOTIFICACIO_TABLE(NOTIFICACIO_MASSIVA_ID);

ALTER TABLE NOT_NOTIFICACIO
    ADD CONSTRAINT NOT_NOTIF_NOTIF_MASSIVA_FK FOREIGN KEY (NOTIFICACIO_MASSIVA_ID) REFERENCES NOT_NOTIFICACIO_MASSIVA(ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_NOTIFICACIO_MASSIVA TO WWW_NOTIB;