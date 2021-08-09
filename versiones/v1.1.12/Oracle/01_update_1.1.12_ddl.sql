-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- #377 Implementar una nova secció de configuració general de l'aplicació
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
CREATE TABLE NOT_CONFIG
(
    KEY                  VARCHAR2(256 CHAR)     NOT NULL,
    VALUE                VARCHAR2(2048 CHAR),
    DESCRIPTION          VARCHAR2(2048 CHAR),
    GROUP_CODE           VARCHAR2(128 CHAR)     NOT NULL,
    POSITION             NUMBER(3)              DEFAULT 0 NOT NULL,
    JBOSS_PROPERTY       NUMBER(1)              DEFAULT 0 NOT NULL,
    TYPE_CODE            VARCHAR2(128 CHAR)     DEFAULT 'TEXT',
    LASTMODIFIEDBY_CODI  VARCHAR2(64),
    LASTMODIFIEDDATE     TIMESTAMP(6)
);

CREATE TABLE NOT_CONFIG_GROUP
(
    CODE                 VARCHAR2(128 CHAR)     NOT NULL,
    PARENT_CODE          VARCHAR2(128 CHAR)     DEFAULT NULL,
    POSITION             NUMBER(3)              DEFAULT 0 NOT NULL,
    DESCRIPTION          VARCHAR2(512 CHAR)     NOT NULL
);

CREATE TABLE NOT_CONFIG_TYPE
(
    CODE                 VARCHAR2(128 CHAR)     NOT NULL,
    VALUE                VARCHAR2(2048 CHAR)   DEFAULT NULL
);

ALTER TABLE NOT_CONFIG ADD (
    CONSTRAINT NOT_CONFIG_PK PRIMARY KEY (KEY));

ALTER TABLE NOT_CONFIG_GROUP ADD (
    CONSTRAINT NOT_CONFIG_GROUP_PK PRIMARY KEY (CODE));

ALTER TABLE NOT_CONFIG_TYPE ADD (
    CONSTRAINT NOT_CONFIG_TYPE_PK PRIMARY KEY (CODE));


ALTER TABLE NOT_CONFIG
    ADD CONSTRAINT NOT_CONFIG_GROUP_FK FOREIGN KEY (GROUP_CODE) REFERENCES NOT_CONFIG_GROUP(CODE);

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_CONFIG TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_CONFIG_GROUP TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_CONFIG_TYPE TO WWW_NOTIB;

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- #549 Modificar el sistema d'entrega CIE
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

-- --
-- Creació taula per als atributs de l'entrega CIE de les entitats, procediments i organs gestors
-- --
CREATE TABLE NOT_ENTREGA_CIE
(
    ID                      NUMBER(19) NOT NULL,

    OPERADOR_POSTAL_ID      NUMBER(19) NOT NULL,
    CIE_ID                  NUMBER(19) NOT NULL,

    CREATEDBY_CODI          VARCHAR2(64),
    CREATEDDATE             TIMESTAMP(6),
    LASTMODIFIEDBY_CODI     VARCHAR2(64),
    LASTMODIFIEDDATE        TIMESTAMP(6)
);

ALTER TABLE NOT_ENTREGA_CIE ADD (
    CONSTRAINT NOT_ENTREGA_CIE_PK PRIMARY KEY (ID));

CREATE INDEX NOT_ENTREGA_CIE_CIE_EK ON NOT_ENTREGA_CIE(CIE_ID);
ALTER TABLE NOT_ENTREGA_CIE
    ADD CONSTRAINT NOT_ENTREGA_CIE_CIE_FK FOREIGN KEY (CIE_ID) REFERENCES NOT_PAGADOR_CIE(ID);

CREATE INDEX NOT_ENTREGA_CIE_OPERADOR_EK ON NOT_ENTREGA_CIE(OPERADOR_POSTAL_ID);
ALTER TABLE NOT_ENTREGA_CIE
    ADD CONSTRAINT NOT_ENTREGA_CIE_OPERADOR_FK FOREIGN KEY (OPERADOR_POSTAL_ID) REFERENCES NOT_PAGADOR_POSTAL(ID);

----
-- Modificació atributs taules pre-existents
----
ALTER TABLE NOT_PAGADOR_POSTAL
    ADD NOM VARCHAR2(256 CHAR) DEFAULT 'NO ESPECIFICAT' NOT NULL;

ALTER TABLE NOT_PAGADOR_CIE
    ADD NOM VARCHAR2(256 CHAR) DEFAULT 'NO ESPECIFICAT' NOT NULL;

ALTER TABLE NOT_PAGADOR_POSTAL
    MODIFY NOM DEFAULT NULL;

ALTER TABLE NOT_PAGADOR_CIE
    MODIFY NOM DEFAULT NULL;

-- ENTITAT
ALTER TABLE NOT_ENTITAT
    DROP COLUMN AMB_ENTREGA_CIE;

ALTER TABLE NOT_ENTITAT
    ADD ENTREGA_CIE_ID NUMBER(19) DEFAULT NULL;

CREATE INDEX NOT_ENTITAT_ENTREGA_CIE_EK ON NOT_ENTITAT(ENTREGA_CIE_ID);
ALTER TABLE NOT_ENTITAT
    ADD CONSTRAINT NOT_ENTITAT_ENTREGA_CIE_FK FOREIGN KEY (ENTREGA_CIE_ID) REFERENCES NOT_ENTREGA_CIE(ID) ON DELETE CASCADE;


-- ORGAN GESTOR

ALTER TABLE NOT_ORGAN_GESTOR
    ADD ENTREGA_CIE_ID NUMBER(19) DEFAULT NULL;

CREATE INDEX NOT_ORGAN_ENTREGA_CIE_EK ON NOT_ORGAN_GESTOR(ENTREGA_CIE_ID);
ALTER TABLE NOT_ORGAN_GESTOR
    ADD CONSTRAINT NOT_ORGAN_ENTREGA_CIE_FK FOREIGN KEY (ENTREGA_CIE_ID) REFERENCES NOT_ENTREGA_CIE(ID) ON DELETE CASCADE;

-- PROCEDIMENT

ALTER TABLE NOT_PROCEDIMENT
    ADD ENTREGA_CIE_ID NUMBER(19) DEFAULT NULL;

CREATE INDEX NOT_PROCEDIMENT_ENTREGA_CIE_EK ON NOT_PROCEDIMENT(ENTREGA_CIE_ID);
ALTER TABLE NOT_PROCEDIMENT
    ADD CONSTRAINT NOT_PROCEDIMENT_ENTREGA_CIE_FK FOREIGN KEY (ENTREGA_CIE_ID) REFERENCES NOT_ENTREGA_CIE(ID) ON DELETE CASCADE;

-- --
-- Creació taula per a les dades de l'entrega postal definida a una notificació.
-- --
CREATE TABLE NOT_ENTREGA_POSTAl
(
    ID                   		NUMBER(19)			NOT NULL,

    DOM_TIPUS            		NUMBER(10),
    DOM_CON_TIPUS        		NUMBER(10),
    DOM_VIA_TIPUS        		NUMBER(10),
    DOM_VIA_NOM          		VARCHAR2(50 CHAR),
    DOM_NUM_TIPUS        		NUMBER(10),
    DOM_NUM_NUM          		VARCHAR2(10 CHAR),
    DOM_NUM_QUALIF       		VARCHAR2(3),
    DOM_NUM_PUNTKM       		VARCHAR2(10),
    DOM_APARTAT          		VARCHAR2(10),
    DOM_BLOC             		VARCHAR2(50),
    DOM_PORTAL           		VARCHAR2(50),
    DOM_ESCALA           		VARCHAR2(50),
    DOM_PLANTA           		VARCHAR2(50),
    DOM_PORTA            		VARCHAR2(50),
    DOM_COMPLEM          		VARCHAR2(250),
    DOM_POBLACIO         		VARCHAR2(255),
    DOM_MUN_CODINE       		VARCHAR2(6),
    DOM_MUN_NOM          		VARCHAR2(64),
    DOM_CODI_POSTAL      		VARCHAR2(10),
    DOM_PRV_CODI         		VARCHAR2(2),
    DOM_PRV_NOM          		VARCHAR2(64),
    DOM_PAI_CODISO       		VARCHAR2(3),
    DOM_PAI_NOM          		VARCHAR2(64),
    DOM_LINEA1           		VARCHAR2(50 CHAR),
    DOM_LINEA2           		VARCHAR2(50 CHAR),
    DOM_CIE              		NUMBER(10),
    FORMAT_SOBRE         		VARCHAR2(10),
    FORMAT_FULLA         		VARCHAR2(10),

    CREATEDBY_CODI       		VARCHAR2(64),
    CREATEDDATE          		TIMESTAMP(6),
    LASTMODIFIEDBY_CODI  		VARCHAR2(64),
    LASTMODIFIEDDATE     		TIMESTAMP(6)
);

ALTER TABLE NOT_ENTREGA_POSTAl ADD (
    CONSTRAINT NOT_ENTREGA_POSTAl_PK PRIMARY KEY (ID));

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_ENTREGA_POSTAl TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_ENTREGA_CIE TO WWW_NOTIB;

-- --
-- Reajustament dades preexistents en la base de dades
-- --
INSERT INTO NOT_ENTREGA_CIE (ID, CIE_ID, OPERADOR_POSTAL_ID)
    (SELECT ID, PAGADORCIE, PAGADORPOSTAL FROM NOT_PROCEDIMENT WHERE PAGADORCIE IS NOT NULL AND PAGADORPOSTAL IS NOT NULL);

UPDATE NOT_PROCEDIMENT P
SET ENTREGA_CIE_ID = (SELECT DISTINCT P.ID FROM NOT_ENTREGA_CIE WHERE OPERADOR_POSTAL_ID = P.PAGADORPOSTAL AND CIE_ID = P.PAGADORCIE)
WHERE PAGADORCIE IS NOT NULL AND PAGADORPOSTAL IS NOT NULL;

ALTER TABLE NOT_PROCEDIMENT
    DROP COLUMN PAGADORPOSTAL;
ALTER TABLE NOT_PROCEDIMENT
    DROP COLUMN PAGADORCIE;



-- Còpia de dades preexistens a la nova taula
INSERT INTO NOT_ENTREGA_POSTAl
(
    ID,

    DOM_TIPUS,
    DOM_CON_TIPUS,
    DOM_VIA_TIPUS,
    DOM_VIA_NOM,
    DOM_NUM_TIPUS,
    DOM_NUM_NUM,
    DOM_NUM_QUALIF,
    DOM_NUM_PUNTKM,
    DOM_APARTAT   ,
    DOM_BLOC      ,
    DOM_PORTAL    ,
    DOM_ESCALA    ,
    DOM_PLANTA    ,
    DOM_PORTA,
    DOM_COMPLEM,
    DOM_POBLACIO,
    DOM_MUN_CODINE,
    DOM_MUN_NOM,
    DOM_CODI_POSTAL,
    DOM_PRV_CODI ,
    DOM_PRV_NOM,
    DOM_PAI_CODISO,
    DOM_PAI_NOM   ,
    DOM_LINEA1    ,
    DOM_LINEA2    ,
    DOM_CIE       ,
    FORMAT_FULLA       ,
    FORMAT_SOBRE       ,

    CREATEDBY_CODI,
    CREATEDDATE   ,
    LASTMODIFIEDBY_CODI
) SELECT
      ENV.ID,

      ENV.DOM_TIPUS,
      ENV.DOM_CON_TIPUS,
      ENV.DOM_VIA_TIPUS,
      ENV.DOM_VIA_NOM,
      ENV.DOM_NUM_TIPUS,
      ENV.DOM_NUM_NUM,
      ENV.DOM_NUM_QUALIF,
      ENV.DOM_NUM_PUNTKM,
      ENV.DOM_APARTAT   ,
      ENV.DOM_BLOC      ,
      ENV.DOM_PORTAL    ,
      ENV.DOM_ESCALA    ,
      ENV.DOM_PLANTA    ,
      ENV.DOM_PORTA,
      ENV.DOM_COMPLEM,
      ENV.DOM_POBLACIO,
      ENV.DOM_MUN_CODINE,
      ENV.DOM_MUN_NOM,
      ENV.DOM_CODI_POSTAL,
      ENV.DOM_PRV_CODI ,
      ENV.DOM_PRV_NOM,
      ENV.DOM_PAI_CODISO,
      ENV.DOM_PAI_NOM   ,
      ENV.DOM_LINEA1    ,
      ENV.DOM_LINEA2    ,
      ENV.DOM_CIE       ,
      ENV.FORMAT_FULLA       ,
      ENV.FORMAT_SOBRE       ,

      ENV.CREATEDBY_CODI,
      ENV.CREATEDDATE   ,
      ENV.LASTMODIFIEDBY_CODI
FROM NOT_NOTIFICACIO_ENV ENV
WHERE ENV.DOM_VIA_NOM IS NOT NULL OR ENV.DOM_LINEA1 IS NOT NULL;

-- --
-- Eliminació de columnes inútils
-- --
ALTER TABLE NOT_NOTIFICACIO_ENV DROP (  DOM_TIPUS,
                                        DOM_CON_TIPUS,
                                        DOM_VIA_TIPUS,
                                        DOM_VIA_NOM,
                                        DOM_NUM_TIPUS,
                                        DOM_NUM_NUM,
                                        DOM_NUM_QUALIF,
                                        DOM_NUM_PUNTKM,
                                        DOM_APARTAT   ,
                                        DOM_BLOC      ,
                                        DOM_PORTAL    ,
                                        DOM_ESCALA    ,
                                        DOM_PLANTA    ,
                                        DOM_PORTA,
                                        DOM_COMPLEM,
                                        DOM_POBLACIO,
                                        DOM_MUN_CODINE,
                                        DOM_MUN_NOM,
                                        DOM_CODI_POSTAL,
                                        DOM_PRV_CODI ,
                                        DOM_PRV_NOM,
                                        DOM_PAI_CODISO,
                                        DOM_PAI_NOM   ,
                                        DOM_LINEA1    ,
                                        DOM_LINEA2    ,
                                        FORMAT_FULLA,
                                        FORMAT_SOBRE,
                                        DOM_CIE);


-- --
-- Definició relació entre la taula d'enviaments i les entregues postals
-- --
ALTER TABLE NOT_NOTIFICACIO_ENV add ENTREGA_POSTAL_ID         		NUMBER(19);
CREATE INDEX NOT_ENV_ENTREGA_POSTAL_EK ON NOT_NOTIFICACIO_ENV(ENTREGA_POSTAL_ID);

ALTER TABLE NOT_NOTIFICACIO_ENV
    ADD CONSTRAINT NOT_ENV_ENTREGA_POSTAL_FK FOREIGN KEY (ENTREGA_POSTAL_ID) REFERENCES NOT_ENTREGA_POSTAl(ID);

UPDATE NOT_NOTIFICACIO_ENV
SET NOT_NOTIFICACIO_ENV.ENTREGA_POSTAL_ID = ID
WHERE ID IN (SELECT ID FROM NOT_ENTREGA_POSTAl);

UPDATE NOT_NOTIFICACIO_ENV
SET NOT_NOTIFICACIO_ENV.ENTREGA_POSTAL_ID = null
WHERE ID not IN (SELECT ID FROM NOT_ENTREGA_POSTAl);

ALTER TABLE NOT_NOTIFICACIO DROP (pagador_postal_id, pagador_cie_id);
ALTER TABLE NOT_NOTIFICACIO_AUDIT DROP (pagador_postal_id, pagador_cie_id);

COMMIT;

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- #568 Permetre donar permisos exclusius sobre un procediment
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
ALTER TABLE NOT_PROCEDIMENT
    ADD DIRECT_PERMISSION_REQUIRED NUMBER(1) DEFAULT 0 NOT NULL;

-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- #591 Revisar descàrrega i notificació de certificació d'enviaments DEH/CIE
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --
-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --

ALTER TABLE NOT_NOTIFICACIO_ENV ADD DEH_CERT_INTENT_NUM NUMBER (10,0) DEFAULT 0;
ALTER TABLE NOT_NOTIFICACIO_ENV ADD DEH_CERT_INTENT_DATA TIMESTAMP(6);
ALTER TABLE NOT_NOTIFICACIO_ENV ADD CIE_CERT_INTENT_NUM NUMBER (10,0) DEFAULT 0;
ALTER TABLE NOT_NOTIFICACIO_ENV ADD CIE_CERT_INTENT_DATA TIMESTAMP(6);
