-- 492

CREATE TABLE NOT_NOTIFICACIO_TABLE
(
    ID                   		NUMBER(19)			NOT NULL,

    ENTITAT_ID             		NUMBER(19)			NOT NULL,
    ENTITAT_NOM			        VARCHAR2(256 CHAR),

    PROC_CODI_NOTIB		    	VARCHAR2(9),
    PROCEDIMENT_ORGAN_ID        NUMBER(19,0),
    GRUP_CODI                   VARCHAR2(64 CHAR),

    USUARI_CODI                 VARCHAR2(64 CHAR),
    TIPUS_USUARI				NUMBER(10),

    ERROR_LAST_CALLBACK         NUMBER(1) DEFAULT 0 NOT NULL,
    ERROR_LAST_EVENT            NUMBER(1) DEFAULT 0 NOT NULL,

    REGISTRE_ENV_INTENT 		NUMBER(10,0),
    REGISTRE_NUM_EXPEDIENT	    VARCHAR2(80 CHAR),

    NOTIFICA_ERROR_DATE         DATE,
    NOTIFICA_ERROR_DESCRIPCIO   VARCHAR2(2048 CHAR),

    ENV_TIPUS            		NUMBER(10)  		NOT NULL,
    CONCEPTE             		VARCHAR2(255 CHAR)	NOT NULL,
    ESTAT                		NUMBER(10)			NOT NULL,
    ESTAT_DATE				    TIMESTAMP(6),

    PROCEDIMENT_CODI			VARCHAR2(64 CHAR),
    PROCEDIMENT_NOM			    VARCHAR2(256 CHAR),
    PROCEDIMENT_IS_COMU		    NUMBER(1) DEFAULT 0 NOT NULL,

    ORGAN_CODI			        VARCHAR2(64),
    ORGAN_NOM			        VARCHAR2(1000),

    CREATEDDATE          		TIMESTAMP(6),
    CREATEDBY_CODI              VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI         VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE            TIMESTAMP (6)
);



CREATE TABLE NOT_NOTIFICACIO_ENV_TABLE
(
    ID                   		NUMBER(19)			NOT NULL,
    NOTIFICACIO_ID              NUMBER(19)			NOT NULL,
    ENTITAT_ID             		NUMBER(19)			NOT NULL,
    DESTINATARIS                VARCHAR2(4000 CHAR)	DEFAULT '',

    TIPUS_ENVIAMENT             NUMBER(10,0) NOT NULL,

    -- TITULAR
    TITULAR_NIF                 VARCHAR2(9 CHAR),
    TITULAR_NOM                 VARCHAR2(256 CHAR),
    TITULAR_EMAIL               VARCHAR2(160 CHAR),
    TITULAR_LLINATGE1           VARCHAR2(40 CHAR),
    TITULAR_LLINATGE2           VARCHAR2(40 CHAR),
    TITULAR_RAOSOCIAL           VARCHAR2(100 CHAR),

    -- INFO NOTIFICACIO
    DATA_PROGRAMADA             DATE,
    PROCEDIMENT_CODI_NOTIB      VARCHAR2(9 BYTE),
    GRUP_CODI                   VARCHAR2(64 CHAR),
    EMISOR_DIR3                 VARCHAR2(9 CHAR),
    USUARI_CODI                 VARCHAR2(64 CHAR),
    NOT_ORGAN_CODI			    VARCHAR2(64 BYTE),

    CONCEPTE             		VARCHAR2(240 CHAR),
    DESCRIPCIO             		VARCHAR2(1000 CHAR),
    LLIBRE                      VARCHAR2(255 CHAR),
    NOT_ESTAT      		        NUMBER(10,0)		NOT NULL,

    CSV_UUID                    VARCHAR2(256 CHAR),

    HAS_ERRORS              NUMBER(1) DEFAULT 0 NOT NULL,

    -- INFO PROCEDIMENT
    PROCEDIMENT_IS_COMU		    NUMBER(1) DEFAULT 0,
    PROCEDIMENT_PROCORGAN_ID    NUMBER(19),

    -- REGISTRE
    REGISTRE_NUMERO             NUMBER(10,0),
    REGISTRE_DATA               DATE,
    REGISTRE_ENVIAMENT_INTENT   NUMBER(10,0),

    -- NOTIFICA
    NOTIFICA_DATA_CADUCITAT     TIMESTAMP(6),
    NOTIFICA_IDENTIFICADOR      VARCHAR2(20 CHAR),
    NOTIFICA_CERT_NUM_SEGUIMENT VARCHAR2(50 CHAR),
    NOTIFICA_ESTAT              NUMBER(10,0) NOT NULL,
    NOTIFICA_REF                VARCHAR2(20 CHAR),

    CREATEDDATE          		TIMESTAMP(6),
    CREATEDBY_CODI              VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI         VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE            TIMESTAMP (6)
);

-- Primary keys
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD (CONSTRAINT NOT_NOTIFICACIO_TABLE_PK PRIMARY KEY (ID));
ALTER TABLE NOT_NOTIFICACIO_ENV_TABLE ADD (CONSTRAINT NOT_NOTIFICACIO_ENV_TABLE_PK PRIMARY KEY (ID));

ALTER TABLE NOT_NOTIFICACIO_EVENT ADD NOTIFICA_ERROR_TIPUS NUMBER(10, 0);

-- 508
ALTER TABLE NOT_NOTIFICACIO_ENV MODIFY NOTIFICA_DATAT_RECNOM VARCHAR2(400);


GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_NOTIFICACIO_TABLE TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_NOTIFICACIO_ENV_TABLE TO WWW_NOTIB;