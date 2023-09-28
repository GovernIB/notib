-- SQLINES LICENSE FOR EVALUATION USE ONLY
-- Taules de ACLs

CREATE TABLE not_acl_sid
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY,
    principal BOOLEAN,
    sid       VARCHAR(100),
);

CREATE TABLE not_acl_class
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY,
    class VARCHAR(100),
);

CREATE TABLE not_acl_object_identity
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY,
    object_id_class    BIGINT,
    object_id_identity VARCHAR(36),
    parent_object      BIGINT,
    owner_sid          BIGINT,
    entries_inheriting BOOLEAN,
);

CREATE TABLE not_acl_entry
(
    id                  BIGINT GENERATED BY DEFAULT AS IDENTITY,
    acl_object_identity BIGINT,
    ace_order           BIGINT,
    sid                 BIGINT,
    mask                INTEGER,
    granting            BOOLEAN,
    audit_success       BOOLEAN,
    audit_failure       BOOLEAN,
);

-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_APLICACIO
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_APLICACIO"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CALLBACK_URL" VARCHAR(256),
     "USUARI_CODI" VARCHAR(64),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "ENTITAT_ID" DECIMAL(19,0),
     "ACTIVA" BOOLEAN DEFAULT 1
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_APLICACIO_AUDIT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_APLICACIO_AUDIT"
(	"ID" DECIMAL(19,0),
     "TIPUSOPERACIO" VARCHAR(20),
     "JOINPOINT" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "APLICACIO_ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "USUARI_CODI" VARCHAR(64),
     "CALLBACK_URL" VARCHAR(256),
     "ACTIVA" BOOLEAN
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_AVIS
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_AVIS"
(	"ID" DECIMAL(19,0),
     "ASSUMPTE" VARCHAR(256),
     "MISSATGE" VARCHAR(2048),
     "DATA_INICI" TIMESTAMP (6),
     "DATA_FINAL" TIMESTAMP (6),
     "ACTIU" BOOLEAN,
     "AVIS_NIVELL" VARCHAR(10),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "AVIS_ADMIN" BOOLEAN DEFAULT '0',
     "ENTITAT_ID" DECIMAL(38,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_CALLBACK
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_CALLBACK"
(	"ID" DECIMAL(19,0),
     "USUARI_CODI" VARCHAR(64),
     "NOTIFICACIO_ID" DECIMAL(19,0),
     "ENVIAMENT_ID" DECIMAL(19,0),
     "DATA" TIMESTAMP (6),
     "ERROR" BOOLEAN DEFAULT 0,
     "ERROR_DESC" VARCHAR(2048),
     "ESTAT" VARCHAR(10),
     "INTENTS" DECIMAL(38,0) DEFAULT 0
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_COLUMNES
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_COLUMNES"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CODI_NOTIB_ENV" BOOLEAN,
     "CONCEPTE" BOOLEAN,
     "CSV_UUID" BOOLEAN,
     "DATA_CADUCITAT" BOOLEAN,
     "DATA_ENVIAMENT" BOOLEAN,
     "DATA_PROGRAMADA" BOOLEAN,
     "DATA_REGISTRE" BOOLEAN,
     "DESCRIPCIO" BOOLEAN,
     "DESTINATARIS" BOOLEAN,
     "DIR3_CODI" BOOLEAN,
     "ENVIAMENT_TIPUS" BOOLEAN,
     "ESTAT" BOOLEAN,
     "GRUP_CODI" BOOLEAN,
     "LLIBRE_REGISTRE" BOOLEAN,
     "NOT_IDENTIFICADOR" BOOLEAN,
     "NUM_CERTIFICACIO" BOOLEAN,
     "NUMERO_REGISTRE" BOOLEAN,
     "PRO_CODI" BOOLEAN,
     "TITULAR_EMAIL" BOOLEAN,
     "TITULAR_NIF" BOOLEAN,
     "TITULAR_NOM_LLINATGE" BOOLEAN,
     "USUARI" BOOLEAN,
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "ENTITAT_ID" DECIMAL(19,0),
     "USUARI_CODI" VARCHAR(64),
     "REFERENCIA_NOTIFICACIO" BOOLEAN
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_CONFIG
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_CONFIG"
(	"KEY" VARCHAR(256),
     "VALUE" VARCHAR(2048),
     "DESCRIPTION" VARCHAR(2048),
     "GROUP_CODE" VARCHAR(128),
     "POSITION" BOOLEAN DEFAULT 0,
     "JBOSS_PROPERTY" BOOLEAN DEFAULT 0,
     "TYPE_CODE" VARCHAR(128) DEFAULT 'TEXT',
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "ENTITAT_CODI" VARCHAR(64),
     "CONFIGURABLE" BOOLEAN DEFAULT '0'
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_CONFIG_GROUP
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_CONFIG_GROUP"
(	"CODE" VARCHAR(128),
     "PARENT_CODE" VARCHAR(128) DEFAULT NULL,
     "POSITION" BOOLEAN DEFAULT 0,
     "DESCRIPTION" VARCHAR(512)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_CONFIG_TYPE
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_CONFIG_TYPE"
(	"CODE" VARCHAR(128),
     "VALUE" VARCHAR(2048) DEFAULT NULL
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_DOCUMENT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_DOCUMENT"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "ARXIU_GEST_DOC_ID" VARCHAR(64),
     "ARXIU_NOM" VARCHAR(200),
     "CSV" VARCHAR(256),
     "HASH" VARCHAR(256),
     "NORMALITZAT" BOOLEAN,
     "URL" VARCHAR(256),
     "UUID" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "MEDIA" VARCHAR(256),
     "MIDA" DECIMAL(19,0),
     "ORIGEN" VARCHAR(20),
     "VALIDESA" VARCHAR(20),
     "TIPUS_DOCUMENTAL" VARCHAR(30),
     "FIRMAT" BOOLEAN
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_ENTITAT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_ENTITAT"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "ACTIVA" BOOLEAN,
     "AMB_ENTREGA_DEH" BOOLEAN,
     "API_KEY" VARCHAR(64),
     "CODI" VARCHAR(64),
     "COLOR_FONS" VARCHAR(1024),
     "COLOR_LLETRA" VARCHAR(1024),
     "DESCRIPCIO" VARCHAR(1024),
     "DIR3_CODI" VARCHAR(9),
     "DIR3_CODI_REG" VARCHAR(9),
     "LOGO_CAP" BYTEA,
     "LOGO_PEU" BYTEA,
     "NOM" VARCHAR(256),
     "TIPUS" VARCHAR(32),
     "TIPUS_DOC_DEFAULT" BIGINT,
     "VERSION" DECIMAL(19,0),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "NOM_OFICINA_VIRTUAL" VARCHAR(255),
     "OFICINA" VARCHAR(255),
     "LLIBRE" VARCHAR(255),
     "LLIBRE_NOM" VARCHAR(255),
     "LLIBRE_ENTITAT" BOOLEAN,
     "OFICINA_ENTITAT" BOOLEAN DEFAULT 1,
     "ENTREGA_CIE_ID" DECIMAL(19,0) DEFAULT NULL,
     "DATA_SINCRONITZACIO" TIMESTAMP (6),
     "DATA_ACTUALITZACIO" TIMESTAMP (6)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_ENTITAT_AUDIT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_ENTITAT_AUDIT"
(	"ID" DECIMAL(19,0),
     "TIPUSOPERACIO" VARCHAR(20),
     "JOINPOINT" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "ENTITAT_ID" DECIMAL(19,0),
     "CODI" VARCHAR(64),
     "NOM" VARCHAR(256),
     "TIPUS" VARCHAR(32),
     "DIR3_CODI" VARCHAR(9),
     "DIR3_CODI_REG" VARCHAR(9),
     "API_KEY" VARCHAR(64),
     "AMB_ENTREGA_DEH" BOOLEAN,
     "AMB_ENTREGA_CIE" BOOLEAN,
     "DESCRIPCIO" VARCHAR(1024),
     "ACTIVA" BOOLEAN,
     "OFICINA" VARCHAR(255),
     "LLIBRE_ENTITAT" BOOLEAN,
     "LLIBRE" VARCHAR(255),
     "LLIBRE_NOM" VARCHAR(255)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_ENTITAT_TIPUS_DOC
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_ENTITAT_TIPUS_DOC"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "TIPUS_DOC" BIGINT,
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "ENTITAT_ID" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_ENTREGA_CIE
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_ENTREGA_CIE"
(	"ID" DECIMAL(19,0),
     "OPERADOR_POSTAL_ID" DECIMAL(19,0),
     "CIE_ID" DECIMAL(19,0),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_ENTREGA_POSTAL
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_ENTREGA_POSTAL"
(	"ID" DECIMAL(19,0),
     "DOM_TIPUS" BIGINT,
     "DOM_CON_TIPUS" BIGINT,
     "DOM_VIA_TIPUS" BIGINT,
     "DOM_VIA_NOM" VARCHAR(50),
     "DOM_NUM_TIPUS" BIGINT,
     "DOM_NUM_NUM" VARCHAR(5),
     "DOM_NUM_QUALIF" VARCHAR(3),
     "DOM_NUM_PUNTKM" VARCHAR(10),
     "DOM_APARTAT" VARCHAR(10),
     "DOM_BLOC" VARCHAR(50),
     "DOM_PORTAL" VARCHAR(50),
     "DOM_ESCALA" VARCHAR(50),
     "DOM_PLANTA" VARCHAR(50),
     "DOM_PORTA" VARCHAR(50),
     "DOM_COMPLEM" VARCHAR(250),
     "DOM_POBLACIO" VARCHAR(30),
     "DOM_MUN_CODINE" VARCHAR(6),
     "DOM_MUN_NOM" VARCHAR(64),
     "DOM_CODI_POSTAL" VARCHAR(10),
     "DOM_PRV_CODI" VARCHAR(2),
     "DOM_PRV_NOM" VARCHAR(64),
     "DOM_PAI_CODISO" VARCHAR(3),
     "DOM_PAI_NOM" VARCHAR(64),
     "DOM_LINEA1" VARCHAR(50),
     "DOM_LINEA2" VARCHAR(50),
     "DOM_CIE" BIGINT,
     "FORMAT_SOBRE" VARCHAR(10),
     "FORMAT_FULLA" VARCHAR(10),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_FORMATS_FULLA
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_FORMATS_FULLA"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CODI" VARCHAR(64),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "PAGADOR_CIE_ID" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_FORMATS_SOBRE
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_FORMATS_SOBRE"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CODI" VARCHAR(64),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "PAGADOR_CIE_ID" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_GRUP
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_GRUP"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CODI" VARCHAR(64),
     "NOM" VARCHAR(100),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "ENTITAT" DECIMAL(19,0),
     "ORGAN_GESTOR" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_GRUP_AUDIT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_GRUP_AUDIT"
(	"ID" DECIMAL(19,0),
     "TIPUSOPERACIO" VARCHAR(20),
     "JOINPOINT" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "GRUP_ID" DECIMAL(19,0),
     "CODI" VARCHAR(64),
     "NOM" VARCHAR(100),
     "ENTITAT_ID" DECIMAL(19,0),
     "ORGAN" VARCHAR(64)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_HIST_ENVIAMENTS
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_HIST_ENVIAMENTS"
(	"ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "ORGAN_ID" DECIMAL(19,0),
     "TIPUS" BIGINT,
     "DATA" TIMESTAMP (6),
     "PROCEDIMENT_ID" DECIMAL(19,0),
     "COMU" BOOLEAN,
     "GRUP_CODI" DECIMAL(19,0),
     "USUARI_CODI" VARCHAR(64),
     "ESTAT" BIGINT,
     "ESTAT_NOT" BIGINT,
     "ENV_TIPUS" BIGINT,
     "N_TOTAL" DECIMAL(19,0),
     "N_CORRECTES" DECIMAL(19,0),
     "N_AMB_ERROR" DECIMAL(19,0),
     "N_ORIGEN_API" DECIMAL(19,0),
     "N_ORIGEN_WEB" DECIMAL(19,0),
     "N_DESTI_ADM" DECIMAL(19,0),
     "N_DESTI_CIUTADA" DECIMAL(19,0),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "VERSION" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_HIST_NOTIF
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_HIST_NOTIF"
(	"ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "ORGAN_ID" DECIMAL(19,0),
     "TIPUS" BIGINT,
     "DATA" TIMESTAMP (6),
     "PROCEDIMENT_ID" DECIMAL(19,0),
     "COMU" BOOLEAN,
     "GRUP_CODI" DECIMAL(19,0),
     "USUARI_CODI" VARCHAR(64),
     "ESTAT" BIGINT,
     "N_NOT_TOTAL" DECIMAL(19,0),
     "N_NOT_CORRECTES" DECIMAL(19,0),
     "N_NOT_AMB_ERROR" DECIMAL(19,0),
     "N_NOT_ORIGEN_API" DECIMAL(19,0),
     "N_NOT_ORIGEN_WEB" DECIMAL(19,0),
     "N_NOT_DESTI_ADM" DECIMAL(19,0),
     "N_NOT_DESTI_CIUTADA" DECIMAL(19,0),
     "N_COM_TOTAL" DECIMAL(19,0),
     "N_COM_CORRECTES" DECIMAL(19,0),
     "N_COM_AMB_ERROR" DECIMAL(19,0),
     "N_COM_ORIGEN_API" DECIMAL(19,0),
     "N_COM_ORIGEN_WEB" DECIMAL(19,0),
     "N_COM_DESTI_ADM" DECIMAL(19,0),
     "N_COM_DESTI_CIUTADA" DECIMAL(19,0),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "VERSION" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_HIST_ORGAN
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_HIST_ORGAN"
(	"ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "ORGAN_ID" DECIMAL(19,0),
     "TIPUS" BIGINT,
     "DATA" TIMESTAMP (6),
     "CODI_DIR3" VARCHAR(64),
     "NOM" VARCHAR(64),
     "N_PROCEDIMENTS" DECIMAL(19,0),
     "N_GRUPS" DECIMAL(19,0),
     "N_PERM_CONSULTA" DECIMAL(19,0),
     "N_PERM_NOTIFICACIO" DECIMAL(19,0),
     "N_PERM_GESTIO" DECIMAL(19,0),
     "N_PERM_PROCESSAR" DECIMAL(19,0),
     "N_PERM_ADMINISTRAR" DECIMAL(19,0),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "VERSION" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_HIST_PROCEDIMENT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_HIST_PROCEDIMENT"
(	"ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "ORGAN_ID" DECIMAL(19,0),
     "TIPUS" BIGINT,
     "DATA" TIMESTAMP (6),
     "PROCEDIMENT_ID" DECIMAL(19,0),
     "COMU" BOOLEAN,
     "CODI_SIA" VARCHAR(64),
     "NOM" VARCHAR(64),
     "N_GRUPS" DECIMAL(19,0),
     "N_PERM_CONSULTA" DECIMAL(19,0),
     "N_PERM_NOTIFICACIO" DECIMAL(19,0),
     "N_PERM_GESTIO" DECIMAL(19,0),
     "N_PERM_PROCESSAR" DECIMAL(19,0),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "VERSION" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_MON_INT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_MON_INT"
(	"ID" DECIMAL(19,0),
     "CODI" VARCHAR(100),
     "DATA" TIMESTAMP (6),
     "DESCRIPCIO" VARCHAR(1024),
     "TIPUS" VARCHAR(10),
     "TEMPS_RESPOSTA" DECIMAL(19,0),
     "ESTAT" VARCHAR(5),
     "CODI_USUARI" VARCHAR(64),
     "ERROR_DESCRIPCIO" VARCHAR(1024),
     "EXCEPCIO_MSG" VARCHAR(1024),
     "EXCEPCIO_STACKTRACE" VARCHAR(2048),
     "CODI_ENTITAT" VARCHAR(64),
     "EXCEPCIO_STACKTRACE_BLOB" BYTEA,
     "APLICACIO" VARCHAR(64)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_MON_INT_PARAM
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_MON_INT_PARAM"
(	"ID" DECIMAL(19,0),
     "MON_INT_ID" DECIMAL(19,0),
     "CODI" VARCHAR(256),
     "VALOR" VARCHAR(1024)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CADUCITAT" TIMESTAMP(0),
     "COM_TIPUS" BIGINT,
     "CONCEPTE" VARCHAR(240),
     "DESCRIPCIO" VARCHAR(1000),
     "EMISOR_DIR3CODI" VARCHAR(9),
     "ENV_DATA_PROG" TIMESTAMP(0),
     "ENV_TIPUS" BIGINT,
     "CALLBACK_ERROR" BOOLEAN,
     "ESTAT" BIGINT,
     "ESTAT_DATE" TIMESTAMP (6),
     "GRUP_CODI" VARCHAR(64),
     "MOTIU" VARCHAR(255),
     "NOT_ENV_DATA" TIMESTAMP (6),
     "NOT_ENV_INTENT" BIGINT,
     "NOT_ERROR_TIPUS" BIGINT,
     "REGISTRE_NUM_EXPEDIENT" VARCHAR(80),
     "PROC_CODI_NOTIB" VARCHAR(9),
     "REGISTRE_DATA" TIMESTAMP(0),
     "REGISTRE_ENV_INTENT" BIGINT,
     "REGISTRE_NUMERO" BIGINT,
     "REGISTRE_NUMERO_FORMATAT" VARCHAR(200),
     "RETARD_POSTAL" BIGINT,
     "TIPUS_USUARI" BIGINT,
     "USUARI_CODI" VARCHAR(64),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "DOCUMENT_ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "PROCEDIMENT_ID" DECIMAL(19,0),
     "ORGAN_GESTOR" VARCHAR(64),
     "PROCEDIMENT_ORGAN_ID" DECIMAL(19,0),
     "NOT_ENV_DATA_NOTIFICA" TIMESTAMP (6),
     "IDIOMA" BIGINT,
     "DOCUMENT2_ID" DECIMAL(19,0),
     "DOCUMENT3_ID" DECIMAL(19,0),
     "DOCUMENT4_ID" DECIMAL(19,0),
     "DOCUMENT5_ID" DECIMAL(19,0),
     "REGISTRE_OFICINA_NOM" VARCHAR(255),
     "REGISTRE_LLIBRE_NOM" VARCHAR(255),
     "IS_ERROR_LAST_EVENT" BOOLEAN DEFAULT 0,
     "NOTIFICACIO_MASSIVA_ID" DECIMAL(19,0) DEFAULT NULL,
     "ESTAT_PROCESSAT_DATE" TIMESTAMP (6),
     "REFERENCIA" VARCHAR(255),
     "JUSTIFICANT_CREAT" BOOLEAN
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO_AUDIT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO_AUDIT"
(	"ID" DECIMAL(19,0),
     "TIPUSOPERACIO" VARCHAR(20),
     "JOINPOINT" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "NOTIFICACIO_ID" DECIMAL(19,0),
     "SINCRON" VARCHAR(20),
     "TIPUS_USUARI" VARCHAR(20),
     "USUARI" VARCHAR(64),
     "EMISOR" VARCHAR(9),
     "TIPUS" VARCHAR(20),
     "ENTITAT_ID" DECIMAL(19,0),
     "ORGAN" VARCHAR(64),
     "PROCEDIMENT" VARCHAR(64),
     "GRUP" VARCHAR(64),
     "CONCEPTE" VARCHAR(255),
     "DESCRIPCIO" VARCHAR(1000),
     "NUM_EXPEDIENT" VARCHAR(80),
     "ENV_DATA_PROG" TIMESTAMP(0),
     "RETARD_POSTAL" BIGINT,
     "CADUCITAT" TIMESTAMP(0),
     "DOCUMENT_ID" DECIMAL(19,0),
     "ESTAT" VARCHAR(32),
     "ESTAT_DATE" TIMESTAMP (6),
     "MOTIU" VARCHAR(255),
     "REGISTRE_ENV_INTENT" BIGINT,
     "REGISTRE_NUMERO" BIGINT,
     "REGISTRE_NUMERO_FORMATAT" VARCHAR(200),
     "REGISTRE_DATA" TIMESTAMP(0),
     "NOT_ENV_DATA" TIMESTAMP (6),
     "NOT_ENV_INTENT" BIGINT,
     "NOT_ERROR_TIPUS" VARCHAR(30),
     "CALLBACK_ERROR" BOOLEAN,
     "EVENT_ERROR" DECIMAL(19,0),
     "ESTAT_PROCESSAT_DATE" TIMESTAMP (6),
     "REFERENCIA" VARCHAR(36)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO_ENV
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO_ENV"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "DEH_NIF" VARCHAR(9),
     "DEH_OBLIGAT" BOOLEAN,
     "DEH_PROC_CODI" VARCHAR(64),
     "NOTIFICA_ARR_DIR3DESC" VARCHAR(100),
     "NOTIFICA_ARR_DIR3CODI" VARCHAR(9),
     "NOTIFICA_ARR_DIR3NIF" VARCHAR(9),
     "NOTIFICA_CER_ARXIUID" VARCHAR(50),
     "NOTIFICA_CER_ARXTIP" BIGINT,
     "NOTIFICA_CER_CSV" VARCHAR(50),
     "NOTIFICA_CER_DATA" TIMESTAMP (6),
     "NOTIFICA_CER_HASH" VARCHAR(50),
     "NOTIFICA_CER_METAS" VARCHAR(255),
     "NOTIFICA_CER_MIME" VARCHAR(20),
     "NOTIFICA_CER_NUMSEG" VARCHAR(50),
     "NOTIFICA_CER_ORIGEN" VARCHAR(20),
     "NOTIFICA_CER_TAMANY" BIGINT,
     "NOTIFICA_CER_TIPUS" BIGINT,
     "NOTIFICA_DATCAD" TIMESTAMP (6),
     "NOTIFICA_DATCRE" TIMESTAMP (6),
     "NOTIFICA_DATDISP" TIMESTAMP (6),
     "NOTIFICA_DATAT_ERRDES" VARCHAR(255),
     "NOTIFICA_DATAT_NUMSEG" VARCHAR(50),
     "NOTIFICA_DATAT_ORIGEN" VARCHAR(20),
     "NOTIFICA_DATAT_RECNIF" VARCHAR(9),
     "NOTIFICA_DATAT_RECNOM" VARCHAR(400),
     "NOTIFICA_EMI_DIR3DESC" VARCHAR(100),
     "NOTIFICA_EMI_DIR3CODI" VARCHAR(9),
     "NOTIFICA_EMI_DIR3NIF" VARCHAR(9),
     "NOTIFICA_ERROR" BOOLEAN,
     "NOTIFICA_ESTAT" BIGINT,
     "NOTIFICA_ESTAT_DATA" TIMESTAMP (6),
     "NOTIFICA_ESTAT_DATAACT" TIMESTAMP (6),
     "NOTIFICA_ESTAT_DESC" VARCHAR(255),
     "NOTIFICA_ESTAT_FINAL" BOOLEAN,
     "NOTIFICA_ID" VARCHAR(20),
     "NOTIFICA_INTENT_DATA" TIMESTAMP (6),
     "NOTIFICA_INTENT_NUM" BIGINT,
     "NOTIFICA_REF" VARCHAR(36),
     "NOTIFICACIO_ID" DECIMAL(19,0),
     "REGISTRE_DATA" TIMESTAMP (6),
     "ESTAT_REGISTRE" BIGINT,
     "REGISTRE_ESTAT_FINAL" BOOLEAN,
     "REGISTRE_NUMERO_FORMATAT" VARCHAR(50),
     "SERVEI_TIPUS" BIGINT,
     "SIR_CON_DATA" TIMESTAMP (6),
     "SIR_CON_INTENT" BIGINT,
     "SIR_REC_DATA" TIMESTAMP (6),
     "SIR_REG_DESTI_DATA" TIMESTAMP (6),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "NOTIFICA_ERROR_EVENT_ID" DECIMAL(19,0),
     "TITULAR_ID" DECIMAL(19,0),
     "DEH_CERT_INTENT_NUM" BIGINT DEFAULT 0,
     "DEH_CERT_INTENT_DATA" TIMESTAMP (6),
     "CIE_CERT_INTENT_NUM" BIGINT DEFAULT 0,
     "CIE_CERT_INTENT_DATA" TIMESTAMP (6),
     "ENTREGA_POSTAL_ID" DECIMAL(19,0),
     "PER_EMAIL" BOOLEAN,
     "CALLBACK_ERROR" BOOLEAN DEFAULT 0
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO_ENV_AUDIT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO_ENV_AUDIT"
(	"ID" DECIMAL(19,0),
     "TIPUSOPERACIO" VARCHAR(20),
     "JOINPOINT" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "ENVIAMENT_ID" DECIMAL(19,0),
     "NOTIFICACIO_ID" DECIMAL(19,0),
     "TITULAR_ID" DECIMAL(19,0),
     "DESTINATARIS_ID" VARCHAR(200),
     "DOMICILI_TIPUS" VARCHAR(20),
     "DOMICILI" VARCHAR(500),
     "SERVEI_TIPUS" VARCHAR(20),
     "CIE" BIGINT,
     "FORMAT_SOBRE" VARCHAR(10),
     "FORMAT_FULLA" VARCHAR(10),
     "DEH_OBLIGAT" BOOLEAN,
     "DEH_NIF" VARCHAR(9),
     "NOTIFICA_REF" VARCHAR(36),
     "NOTIFICA_ID" VARCHAR(20),
     "NOTIFICA_DATCRE" TIMESTAMP (6),
     "NOTIFICA_DATDISP" TIMESTAMP (6),
     "NOTIFICA_DATCAD" TIMESTAMP (6),
     "NOTIFICA_EMI_DIR3CODI" VARCHAR(9),
     "NOTIFICA_ARR_DIR3CODI" VARCHAR(9),
     "NOTIFICA_ESTAT" BIGINT,
     "NOTIFICA_ESTAT_DATA" TIMESTAMP (6),
     "NOTIFICA_ESTAT_FINAL" BOOLEAN,
     "NOTIFICA_DATAT_ORIGEN" VARCHAR(20),
     "NOTIFICA_DATAT_RECNIF" VARCHAR(9),
     "NOTIFICA_DATAT_NUMSEG" VARCHAR(50),
     "NOTIFICA_CER_DATA" TIMESTAMP (6),
     "NOTIFICA_CER_ARXIUID" VARCHAR(50),
     "NOTIFICA_CER_ORIGEN" VARCHAR(20),
     "NOTIFICA_CER_TIPUS" VARCHAR(20),
     "NOTIFICA_CER_ARXTIP" VARCHAR(20),
     "NOTIFICA_CER_NUMSEG" VARCHAR(50),
     "REGISTRE_NUMERO_FORMATAT" VARCHAR(50),
     "REGISTRE_DATA" TIMESTAMP (6),
     "REGISTRE_ESTAT" VARCHAR(20),
     "REGISTRE_ESTAT_FINAL" BOOLEAN,
     "SIR_CON_DATA" TIMESTAMP (6),
     "SIR_REC_DATA" TIMESTAMP (6),
     "SIR_REG_DESTI_DATA" TIMESTAMP (6),
     "NOTIFICA_ERROR_EVENT_ID" DECIMAL(19,0),
     "NOTIFICA_ERROR" BOOLEAN,
     "NOTIFICA_DATAT_ERRDES" VARCHAR(255)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO_ENV_TABLE
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO_ENV_TABLE"
(	"ID" DECIMAL(19,0),
     "NOTIFICACIO_ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "DESTINATARIS" VARCHAR(4000) DEFAULT '',
     "TIPUS_ENVIAMENT" BIGINT,
     "TITULAR_NIF" VARCHAR(9),
     "TITULAR_NOM" VARCHAR(256),
     "TITULAR_EMAIL" VARCHAR(160),
     "TITULAR_LLINATGE1" VARCHAR(40),
     "TITULAR_LLINATGE2" VARCHAR(40),
     "TITULAR_RAOSOCIAL" VARCHAR(100),
     "DATA_PROGRAMADA" TIMESTAMP(0),
     "PROCEDIMENT_CODI_NOTIB" VARCHAR(9),
     "GRUP_CODI" VARCHAR(64),
     "EMISOR_DIR3" VARCHAR(9),
     "USUARI_CODI" VARCHAR(64),
     "NOT_ORGAN_CODI" VARCHAR(64),
     "CONCEPTE" VARCHAR(240),
     "DESCRIPCIO" VARCHAR(1000),
     "LLIBRE" VARCHAR(255),
     "NOT_ESTAT" BIGINT,
     "CSV_UUID" VARCHAR(256),
     "HAS_ERRORS" BOOLEAN DEFAULT 0,
     "PROCEDIMENT_IS_COMU" BOOLEAN DEFAULT 0,
     "PROCEDIMENT_PROCORGAN_ID" DECIMAL(19,0),
     "REGISTRE_NUMERO" BIGINT,
     "REGISTRE_DATA" TIMESTAMP(0),
     "REGISTRE_ENVIAMENT_INTENT" BIGINT,
     "NOTIFICA_DATA_CADUCITAT" TIMESTAMP (6),
     "NOTIFICA_IDENTIFICADOR" VARCHAR(20),
     "NOTIFICA_CERT_NUM_SEGUIMENT" VARCHAR(50),
     "NOTIFICA_ESTAT" BIGINT,
     "NOTIFICA_REF" VARCHAR(36),
     "CREATEDDATE" TIMESTAMP (6),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "ORGAN_ESTAT" BIGINT DEFAULT 0,
     "PROCEDIMENT_REQUIRE_PERMISSION" BOOLEAN DEFAULT 0,
     "PROCEDIMENT_TIPUS" VARCHAR(32),
     "NO_VIGENT" BOOLEAN,
     "CALLBACK_ERROR" BOOLEAN DEFAULT 0,
     "TITULAR_NIF_bar" VARCHAR(100)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO_EVENT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO_EVENT"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CALLBACK_DATA" TIMESTAMP (6),
     "CALLBACK_ERROR_DESC" VARCHAR(2048),
     "CALLBACK_ESTAT" VARCHAR(10),
     "CALLBACK_INTENTS" BIGINT,
     "DATA" TIMESTAMP (6),
     "DESCRIPCIO" VARCHAR(256),
     "ERROR" BOOLEAN,
     "ERROR_DESC" VARCHAR(2048),
     "NOTIFICACIO_ID" DECIMAL(19,0),
     "TIPUS" BIGINT,
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "NOTIFICACIO_ENV_ID" DECIMAL(19,0),
     "NOTIFICA_ERROR_TIPUS" BIGINT,
     "FI_REINTENTS" BOOLEAN DEFAULT '0',
     "INTENTS" BIGINT DEFAULT '0'
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO_MASSIVA
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO_MASSIVA"
(	"ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "CSV_GESDOC_ID" VARCHAR(64),
     "ZIP_GESDOC_ID" VARCHAR(64),
     "CSV_FILENAME" VARCHAR(200),
     "ZIP_FILENAME" VARCHAR(200),
     "CADUCITAT" TIMESTAMP (6),
     "EMAIL" VARCHAR(64),
     "PAGADOR_POSTAL_ID" DECIMAL(19,0),
     "RESUM_GESDOC_ID" VARCHAR(64),
     "ERRORS_GESDOC_ID" VARCHAR(64),
     "PROGRESS" BOOLEAN DEFAULT 0,
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "ESTAT_VALIDACIO" VARCHAR(32),
     "ESTAT_PROCES" VARCHAR(32),
     "NUM_NOTIFICACIONS" DECIMAL(38,0),
     "NUM_VALIDADES" DECIMAL(38,0),
     "NUM_PROCESSADES" DECIMAL(38,0),
     "NUM_ERROR" DECIMAL(38,0),
     "NUM_CANCELADES" DECIMAL(38,0) DEFAULT 0
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_NOTIFICACIO_TABLE
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_NOTIFICACIO_TABLE"
(	"ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "ENTITAT_NOM" VARCHAR(256),
     "PROC_CODI_NOTIB" VARCHAR(9),
     "PROCEDIMENT_ORGAN_ID" DECIMAL(19,0),
     "GRUP_CODI" VARCHAR(64),
     "USUARI_CODI" VARCHAR(64),
     "TIPUS_USUARI" BIGINT,
     "ERROR_LAST_CALLBACK" BOOLEAN DEFAULT 0,
     "ERROR_LAST_EVENT" BOOLEAN DEFAULT 0,
     "REGISTRE_ENV_INTENT" BIGINT,
     "REGISTRE_NUM_EXPEDIENT" VARCHAR(80),
     "NOTIFICA_ERROR_DATE" TIMESTAMP(0),
     "NOTIFICA_ERROR_DESCRIPCIO" VARCHAR(2048),
     "ENV_TIPUS" BIGINT,
     "CONCEPTE" VARCHAR(255),
     "ESTAT" BIGINT,
     "ESTAT_DATE" TIMESTAMP (6),
     "PROCEDIMENT_CODI" VARCHAR(64),
     "PROCEDIMENT_NOM" VARCHAR(256),
     "PROCEDIMENT_IS_COMU" BOOLEAN DEFAULT 0,
     "ORGAN_CODI" VARCHAR(64),
     "ORGAN_NOM" VARCHAR(1000),
     "CREATEDDATE" TIMESTAMP (6),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "NOTIFICACIO_MASSIVA_ID" DECIMAL(19,0) DEFAULT NULL,
     "ORGAN_ESTAT" BIGINT DEFAULT 0,
     "PROCEDIMENT_REQUIRE_PERMISSION" BOOLEAN DEFAULT 0,
     "ESTAT_PROCESSAT_DATE" TIMESTAMP (6),
     "PROCEDIMENT_TIPUS" VARCHAR(32),
     "ENVIADA_DATE" TIMESTAMP (6),
     "REFERENCIA" VARCHAR(36),
     "TITULAR" VARCHAR(1024),
     "NOTIFICA_IDS" VARCHAR(1024),
     "ESTAT_MASK" DECIMAL(38,0),
     "PER_ACTUALITZAR" BOOLEAN DEFAULT '1',
     "REG_ENV_PENDENTS" BOOLEAN DEFAULT '0',
     "ESTAT_STRING" VARCHAR(2000),
     "DOCUMENT_ID" DECIMAL(38,0),
     "ENV_CER_DATA" TIMESTAMP (6)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_OFICINA
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_OFICINA"
(	"CODI" VARCHAR(64),
     "NOM" VARCHAR(1024),
     "SIR" BOOLEAN,
     "ACTIU" BOOLEAN,
     "ORGAN_CODI" VARCHAR(16),
     "ENTITAT_ID" DECIMAL(38,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_OG_SINC_REL
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_OG_SINC_REL"
(	"ANTIC_OG" DECIMAL(38,0),
     "NOU_OG" DECIMAL(38,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_ORGAN_GESTOR
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_ORGAN_GESTOR"
(	"ID" DECIMAL(19,0),
     "CODI" VARCHAR(64),
     "ENTITAT" DECIMAL(19,0),
     "NOM" VARCHAR(1000),
     "LLIBRE" VARCHAR(255),
     "LLIBRE_NOM" VARCHAR(255),
     "OFICINA" VARCHAR(255),
     "OFICINA_NOM" VARCHAR(255),
     "ENTREGA_CIE_ID" DECIMAL(19,0) DEFAULT NULL,
     "CODI_PARE" VARCHAR(64),
     "SIR" BOOLEAN DEFAULT '0',
     "TIPUS_TRANSICIO" VARCHAR(12),
     "ESTAT" VARCHAR(1) DEFAULT 'V',
     "NO_VIGENT" BOOLEAN,
     "NOM_ES" VARCHAR(1000)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PAGADOR_CIE
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PAGADOR_CIE"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CONTRACTE_DATA_VIG" TIMESTAMP(0),
     "DIR3_CODI" VARCHAR(9),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "ENTITAT" DECIMAL(19,0),
     "ORGAN_GESTOR" DECIMAL(19,0),
     "NOM" VARCHAR(256) DEFAULT NULL
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PAGADOR_POSTAL
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PAGADOR_POSTAL"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CONTRACTE_DATA_VIG" TIMESTAMP(0),
     "CONTRACTE_NUM" VARCHAR(20),
     "DIR3_CODI" VARCHAR(9),
     "FACTURACIO_CODI_CLIENT" VARCHAR(20),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "ENTITAT" DECIMAL(19,0),
     "ORGAN_GESTOR" DECIMAL(19,0),
     "NOM" VARCHAR(256) DEFAULT NULL
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PERSONA
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PERSONA"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "COD_ENTITAT_DESTI" VARCHAR(9),
     "EMAIL" VARCHAR(160),
     "INCAPACITAT" BOOLEAN,
     "INTERESSATTIPUS" VARCHAR(255),
     "LLINATGE1" VARCHAR(40),
     "LLINATGE2" VARCHAR(40),
     "NIF" VARCHAR(9),
     "NOM" VARCHAR(255),
     "RAO_SOCIAL" VARCHAR(100),
     "TELEFON" VARCHAR(16),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "NOTIFICACIO_ENV_ID" DECIMAL(19,0),
     "DOCUMENT_TIPUS" VARCHAR(32),
     "NIF_bar" VARCHAR(100)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PROCEDIMENT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PROCEDIMENT"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "AGRUPAR" BOOLEAN,
     "CADUCITAT" BIGINT,
     "CODI" VARCHAR(64),
     "CODIASSUMPTE" VARCHAR(255),
     "CODIASSUMPTE_NOM" VARCHAR(255),
     "NOM" VARCHAR(256),
     "RETARD" BIGINT,
     "TIPUSASSUMPTE" VARCHAR(255),
     "TIPUSASSUMPTE_NOM" VARCHAR(255),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "ENTITAT" DECIMAL(19,0),
     "ORGAN_GESTOR" VARCHAR(64),
     "COMU" BOOLEAN DEFAULT 0,
     "ULTIMA_ACT" TIMESTAMP(0),
     "DIRECT_PERMISSION_REQUIRED" BOOLEAN DEFAULT 0,
     "ENTREGA_CIE_ID" DECIMAL(19,0) DEFAULT NULL,
     "TIPUS" VARCHAR(32),
     "ORGAN_NO_SINC" BOOLEAN,
     "ACTIU" BOOLEAN DEFAULT '1',
     "MANUAL" BOOLEAN DEFAULT '0'
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PROCEDIMENT_AUDIT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PROCEDIMENT_AUDIT"
(	"ID" DECIMAL(19,0),
     "TIPUSOPERACIO" VARCHAR(20),
     "JOINPOINT" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "PROCEDIMENT_ID" DECIMAL(19,0),
     "ENTITAT_ID" DECIMAL(19,0),
     "ORGAN" VARCHAR(64),
     "CODI" VARCHAR(64),
     "NOM" VARCHAR(256),
     "RETARD" BIGINT,
     "CADUCITAT" BIGINT,
     "AGRUPAR" BOOLEAN,
     "COMU" BOOLEAN,
     "PAGADORPOSTAL_ID" DECIMAL(19,0),
     "PAGADORCIE_ID" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PROCESSOS_INICIALS
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PROCESSOS_INICIALS"
(	"CODI" VARCHAR(100),
     "INIT" BOOLEAN DEFAULT 0,
     "ID" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PRO_GRUP
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PRO_GRUP"
(	"ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "GRUP" DECIMAL(19,0),
     "PROCEDIMENT" DECIMAL(19,0)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PRO_GRUP_AUDIT
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PRO_GRUP_AUDIT"
(	"ID" DECIMAL(19,0),
     "TIPUSOPERACIO" VARCHAR(20),
     "JOINPOINT" VARCHAR(256),
     "CREATEDBY_CODI" VARCHAR(64),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "PROGRUP_ID" DECIMAL(19,0),
     "PROCEDIMENT" VARCHAR(64),
     "GRUP" VARCHAR(64)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_PRO_ORGAN
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_PRO_ORGAN"
(	"ID" DECIMAL(19,0),
     "PROCEDIMENT_ID" DECIMAL(19,0),
     "ORGANGESTOR_ID" DECIMAL(19,0),
     "CREATEDDATE" TIMESTAMP (6),
     "LASTMODIFIEDDATE" TIMESTAMP (6),
     "CREATEDBY_CODI" VARCHAR(64),
     "LASTMODIFIEDBY_CODI" VARCHAR(64)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** T_USUARI
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."NOT_USUARI"
(	"CODI" VARCHAR(64),
     "EMAIL" VARCHAR(200),
     "IDIOMA" VARCHAR(2),
     "LLINATGES" VARCHAR(100),
     "NOM" VARCHAR(100),
     "NOM_SENCER" VARCHAR(200),
     "REBRE_EMAILS" BOOLEAN,
     "VERSION" DECIMAL(19,0),
     "ULTIM_ROL" VARCHAR(40),
     "ULTIMA_ENTITAT" DECIMAL(19,0),
     "REBRE_EMAILS_CREATS" BOOLEAN DEFAULT 0,
     "EMAIL_ALT" VARCHAR(200)
);
-- SQLINES DEMO *** ------------------------------------
-- SQLINES DEMO *** ATE_MACHINE
-- SQLINES DEMO *** ------------------------------------

-- SQLINES LICENSE FOR EVALUATION USE ONLY
CREATE TABLE "NOTIB"."STATE_MACHINE"
(	"MACHINE_ID" VARCHAR(255),
     "STATE" VARCHAR(255),
     "STATE_MACHINE_CONTEXT" BYTEA
);