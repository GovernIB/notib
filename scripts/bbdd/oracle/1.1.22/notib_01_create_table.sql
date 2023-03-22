
CREATE TABLE NOT_ACL_CLASS
(
    ID NUMBER(19,0) NOT NULL,
	CLASS VARCHAR2(100 CHAR) NOT NULL
)
/

CREATE TABLE NOT_ACL_ENTRY 
(
    ID NUMBER(19,0) NOT NULL,
	ACL_OBJECT_IDENTITY NUMBER(19,0) NOT NULL, 
	ACE_ORDER NUMBER(19,0) NOT NULL, 
	SID NUMBER(19,0) NOT NULL, 
	MASK NUMBER(19,0) NOT NULL, 
	GRANTING NUMBER(1,0) NOT NULL, 
	AUDIT_SUCCESS NUMBER(1,0) NOT NULL, 
	AUDIT_FAILURE NUMBER(1,0) NOT NULL
)
/

CREATE TABLE NOT_ACL_OBJECT_IDENTITY 
(
    ID NUMBER(19,0) NOT NULL,
	OBJECT_ID_CLASS NUMBER(19,0) NOT NULL, 
	OBJECT_ID_IDENTITY NUMBER(19,0) NOT NULL, 
	PARENT_OBJECT NUMBER(19,0), 
	OWNER_SID NUMBER(19,0) NOT NULL, 
	ENTRIES_INHERITING NUMBER(1,0) NOT NULL
)
/

CREATE TABLE NOT_ACL_SID 
(
    ID NUMBER(19,0) NOT NULL,
	PRINCIPAL NUMBER(1,0) NOT NULL, 
	SID VARCHAR2(100 CHAR) NOT NULL
)
/

CREATE TABLE NOT_APLICACIO
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CALLBACK_URL VARCHAR2(256 CHAR),
    USUARI_CODI VARCHAR2(64 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    ACTIVA NUMBER(1,0) DEFAULT 1 NOT NULL
)
/

CREATE TABLE NOT_APLICACIO_AUDIT 
(
    ID NUMBER(19,0) NOT NULL,
	TIPUSOPERACIO VARCHAR2(20 CHAR) NOT NULL, 
	JOINPOINT VARCHAR2(256 CHAR), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	APLICACIO_ID NUMBER(19,0) NOT NULL, 
	ENTITAT_ID NUMBER(19,0) NOT NULL, 
	USUARI_CODI VARCHAR2(64 CHAR), 
	CALLBACK_URL VARCHAR2(256 CHAR), 
	ACTIVA NUMBER(1,0)
)
/

CREATE TABLE NOT_AVIS 
(
    ID NUMBER(19,0) NOT NULL,
	ASSUMPTE VARCHAR2(256 CHAR) NOT NULL,
	MISSATGE VARCHAR2(2048 CHAR) NOT NULL,
	DATA_INICI TIMESTAMP (6) NOT NULL, 
	DATA_FINAL TIMESTAMP (6) NOT NULL, 
	ACTIU NUMBER(1,0) NOT NULL, 
	AVIS_NIVELL VARCHAR2(10 CHAR) NOT NULL,
	CREATEDBY_CODI VARCHAR2(64 CHAR),
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
	LASTMODIFIEDDATE TIMESTAMP (6),
    AVIS_ADMIN NUMBER(1) DEFAULT '0',
    ENTITAT_ID NUMBER(38,0)
)
/

CREATE TABLE NOT_CALLBACK
(
    ID NUMBER(19) NOT NULL,
    USUARI_CODI VARCHAR2(64 char) NOT NULL,
    NOTIFICACIO_ID NUMBER(19) NOT NULL,
    ENVIAMENT_ID NUMBER(19) NOT NULL,
    DATA TIMESTAMP(6),
    ERROR NUMBER(1) DEFAULT 0 NOT NULL,
    ERROR_DESC VARCHAR2(2048 CHAR),
    ESTAT VARCHAR2(10 CHAR),
    INTENTS NUMBER DEFAULT 0 NOT NULL
)
/

CREATE TABLE NOT_COLUMNES
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CODI_NOTIB_ENV NUMBER(1,0),
    CONCEPTE NUMBER(1,0),
    CSV_UUID NUMBER(1,0),
    DATA_CADUCITAT NUMBER(1,0),
    CREATED_DATE NUMBER(1,0),
    DATA_PROGRAMADA NUMBER(1,0),
    DATA_REGISTRE NUMBER(1,0),
    DESCRIPCIO NUMBER(1,0),
    DESTINATARIS NUMBER(1,0),
    DIR3_CODI NUMBER(1,0),
    ENVIAMENT_TIPUS NUMBER(1,0),
    ESTAT NUMBER(1,0),
    GRUP_CODI NUMBER(1,0),
    LLIBRE_REGISTRE NUMBER(1,0),
    NOT_IDENTIFICADOR NUMBER(1,0),
    NUM_CERTIFICACIO NUMBER(1,0),
    NUMERO_REGISTRE NUMBER(1,0),
    PRO_CODI NUMBER(1,0),
    TITULAR_EMAIL NUMBER(1,0),
    TITULAR_NIF NUMBER(1,0),
    TITULAR_NOM_LLINATGE NUMBER(1,0),
    USUARI NUMBER(1,0),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    ENTITAT_ID NUMBER(19,0),
    USUARI_CODI VARCHAR2(64 CHAR),
    REFERENCIA_NOTIFICACIO NUMBER(1)
)
/

CREATE TABLE NOT_CONFIG
(
    KEY VARCHAR2(256 CHAR) not null,
    VALUE VARCHAR2(2048 CHAR),
    DESCRIPTION VARCHAR2(2048 CHAR),
    GROUP_CODE VARCHAR2(128 CHAR) not null,
    POSITION NUMBER(3) default 0 not null,
    JBOSS_PROPERTY NUMBER(1) default 0 not null,
    TYPE_CODE VARCHAR2(128 CHAR) default 'TEXT',
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP(6),
    ENTITAT_CODI VARCHAR2(64 CHAR),
    CONFIGURABLE NUMBER(1,0) DEFAULT '0'
)
/

CREATE TABLE NOT_CONFIG_GROUP 
(
    CODE VARCHAR2(128 CHAR) NOT NULL,
	PARENT_CODE VARCHAR2(128 CHAR) DEFAULT NULL, 
	POSITION NUMBER(3,0) DEFAULT 0 NOT NULL, 
	DESCRIPTION VARCHAR2(512 CHAR) NOT NULL
)
/

CREATE TABLE NOT_CONFIG_TYPE 
(
    CODE VARCHAR2(128 CHAR) NOT NULL,
	VALUE VARCHAR2(2048 CHAR) DEFAULT NULL
)
/

CREATE TABLE NOT_DOCUMENT
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    ARXIU_GEST_DOC_ID VARCHAR2(64 CHAR),
    ARXIU_NOM VARCHAR2(200 CHAR),
    CSV VARCHAR2(256 CHAR),
    HASH VARCHAR2(256 CHAR),
    NORMALITZAT NUMBER(1,0),
    URL VARCHAR2(256 CHAR),
    UUID VARCHAR2(256 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    MEDIA VARCHAR2(256 CHAR),
    MIDA NUMBER(19,0),
    ORIGEN VARCHAR2(20 CHAR),
    VALIDESA VARCHAR2(20 CHAR),
    TIPUS_DOCUMENTAL VARCHAR2(30 CHAR),
    FIRMAT NUMBER(1,0)
);

CREATE TABLE NOT_ENTITAT
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    ACTIVA NUMBER(1,0) NOT NULL,
    AMB_ENTREGA_DEH NUMBER(1,0) NOT NULL,
    API_KEY VARCHAR2(64 CHAR) NOT NULL,
    CODI VARCHAR2(64 CHAR) NOT NULL,
    COLOR_FONS VARCHAR2(1024 CHAR),
    COLOR_LLETRA VARCHAR2(1024 CHAR),
    DESCRIPCIO VARCHAR2(1024 CHAR),
    DIR3_CODI VARCHAR2(9 CHAR) NOT NULL,
    DIR3_CODI_REG VARCHAR2(9 CHAR),
    LOGO_CAP RAW(1024),
    LOGO_PEU RAW(1024),
    NOM VARCHAR2(256 CHAR) NOT NULL,
    TIPUS VARCHAR2(32 CHAR) NOT NULL,
    TIPUS_DOC_DEFAULT NUMBER(10,0),
    VERSION NUMBER(19,0) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    NOM_OFICINA_VIRTUAL VARCHAR2(255 CHAR),
    OFICINA VARCHAR2(255 CHAR),
    LLIBRE VARCHAR2(255 CHAR),
    LLIBRE_NOM VARCHAR2(255 CHAR),
    LLIBRE_ENTITAT NUMBER(1,0),
    OFICINA_ENTITAT NUMBER(1,0) DEFAULT 1,
    ENTREGA_CIE_ID NUMBER(19,0) DEFAULT NULL,
    DATA_SINCRONITZACIO TIMESTAMP(6),
    DATA_ACTUALITZACIO TIMESTAMP(6)
)
/

CREATE TABLE NOT_ENTITAT_AUDIT 
(
    ID NUMBER(19,0) NOT NULL,
	TIPUSOPERACIO VARCHAR2(20 CHAR) NOT NULL, 
	JOINPOINT VARCHAR2(256 CHAR), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	ENTITAT_ID NUMBER(19,0) NOT NULL, 
	CODI VARCHAR2(64 CHAR) NOT NULL, 
	NOM VARCHAR2(256 CHAR) NOT NULL, 
	TIPUS VARCHAR2(32 CHAR), 
	DIR3_CODI VARCHAR2(9 CHAR) NOT NULL, 
	DIR3_CODI_REG VARCHAR2(9 CHAR), 
	API_KEY VARCHAR2(64 CHAR), 
	AMB_ENTREGA_DEH NUMBER(1,0), 
	AMB_ENTREGA_CIE NUMBER(1,0), 
	DESCRIPCIO VARCHAR2(1024 CHAR), 
	ACTIVA NUMBER(1,0), 
	OFICINA VARCHAR2(255 CHAR), 
	LLIBRE_ENTITAT NUMBER(1,0), 
	LLIBRE VARCHAR2(255 CHAR), 
	LLIBRE_NOM VARCHAR2(255 CHAR)
)
/

CREATE TABLE NOT_ENTITAT_TIPUS_DOC
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    TIPUS_DOC NUMBER(10,0),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    ENTITAT_ID NUMBER(19,0)
)
/

CREATE TABLE NOT_ENTREGA_CIE
(
    ID NUMBER(19,0) NOT NULL,
    OPERADOR_POSTAL_ID NUMBER(19,0) NOT NULL,
    CIE_ID NUMBER(19,0) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP (6)
)
/

CREATE TABLE NOT_ENTREGA_POSTAL 
(
    ID NUMBER(19,0) NOT NULL,
	DOM_TIPUS NUMBER(10,0), 
	DOM_CON_TIPUS NUMBER(10,0), 
	DOM_VIA_TIPUS NUMBER(10,0), 
	DOM_VIA_NOM VARCHAR2(50 CHAR), 
	DOM_NUM_TIPUS NUMBER(10,0), 
	DOM_NUM_NUM VARCHAR2(5 CHAR), 
	DOM_NUM_QUALIF VARCHAR2(3 CHAR),
	DOM_NUM_PUNTKM VARCHAR2(10 CHAR),
	DOM_APARTAT VARCHAR2(10 CHAR),
	DOM_BLOC VARCHAR2(50 CHAR),
	DOM_PORTAL VARCHAR2(50 CHAR),
	DOM_ESCALA VARCHAR2(50 CHAR),
	DOM_PLANTA VARCHAR2(50 CHAR),
	DOM_PORTA VARCHAR2(50 CHAR),
	DOM_COMPLEM VARCHAR2(250 CHAR),
	DOM_POBLACIO VARCHAR2(30 CHAR),
	DOM_MUN_CODINE VARCHAR2(6 CHAR),
	DOM_MUN_NOM VARCHAR2(64 CHAR),
	DOM_CODI_POSTAL VARCHAR2(10 CHAR),
	DOM_PRV_CODI VARCHAR2(2 CHAR),
	DOM_PRV_NOM VARCHAR2(64 CHAR),
	DOM_PAI_CODISO VARCHAR2(3 CHAR),
	DOM_PAI_NOM VARCHAR2(64 CHAR),
	DOM_LINEA1 VARCHAR2(50 CHAR), 
	DOM_LINEA2 VARCHAR2(50 CHAR), 
	DOM_CIE NUMBER(10,0), 
	FORMAT_SOBRE VARCHAR2(10 CHAR),
	FORMAT_FULLA VARCHAR2(10 CHAR),
	CREATEDBY_CODI VARCHAR2(64 CHAR),
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
	LASTMODIFIEDDATE TIMESTAMP (6)
)
/

CREATE TABLE NOT_FORMATS_FULLA
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CODI VARCHAR2(64 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    PAGADOR_CIE_ID NUMBER(19,0)
)
/

CREATE TABLE NOT_FORMATS_SOBRE
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CODI VARCHAR2(64 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    PAGADOR_CIE_ID NUMBER(19,0)
)
/

CREATE TABLE NOT_GRUP
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CODI VARCHAR2(64 CHAR) NOT NULL,
    NOM VARCHAR2(100 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    ENTITAT NUMBER(19,0),
    ORGAN_GESTOR NUMBER(19,0)
)
/

CREATE TABLE NOT_GRUP_AUDIT 
(
    ID NUMBER(19,0) NOT NULL,
	TIPUSOPERACIO VARCHAR2(20 CHAR) NOT NULL, 
	JOINPOINT VARCHAR2(256 CHAR), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	GRUP_ID NUMBER(19,0) NOT NULL, 
	CODI VARCHAR2(64 CHAR) NOT NULL, 
	NOM VARCHAR2(100 CHAR), 
	ENTITAT_ID NUMBER(19,0), 
	ORGAN VARCHAR2(64 CHAR)
)
/

CREATE TABLE NOT_HIST_ENVIAMENTS
(
    ID NUMBER(19,0) NOT NULL,
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    ORGAN_ID NUMBER(19,0),
    TIPUS NUMBER(10,0) NOT NULL,
    DATA TIMESTAMP (6) NOT NULL,
    PROCEDIMENT_ID NUMBER(19,0),
    COMU NUMBER(1,0),
    GRUP_CODI NUMBER(19,0),
    USUARI_CODI VARCHAR2(64 CHAR) NOT NULL,
    ESTAT NUMBER(10,0) NOT NULL,
    ESTAT_NOT NUMBER(10,0) NOT NULL,
    ENV_TIPUS NUMBER(10,0) NOT NULL,
    N_TOTAL NUMBER(19,0) NOT NULL,
    N_CORRECTES NUMBER(19,0) NOT NULL,
    N_AMB_ERROR NUMBER(19,0) NOT NULL,
    N_ORIGEN_API NUMBER(19,0) NOT NULL,
    N_ORIGEN_WEB NUMBER(19,0) NOT NULL,
    N_DESTI_ADM NUMBER(19,0) NOT NULL,
    N_DESTI_CIUTADA NUMBER(19,0) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP (6),
    VERSION NUMBER(19,0) NOT NULL
)
/

CREATE TABLE NOT_HIST_NOTIF
(
    ID NUMBER(19,0) NOT NULL,
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    ORGAN_ID NUMBER(19,0),
    TIPUS NUMBER(10,0) NOT NULL,
    DATA TIMESTAMP (6) NOT NULL,
    PROCEDIMENT_ID NUMBER(19,0),
    COMU NUMBER(1,0),
    GRUP_CODI NUMBER(19,0),
    USUARI_CODI VARCHAR2(64 CHAR) NOT NULL,
    ESTAT NUMBER(10,0) NOT NULL,
    N_NOT_TOTAL NUMBER(19,0) NOT NULL,
    N_NOT_CORRECTES NUMBER(19,0) NOT NULL,
    N_NOT_AMB_ERROR NUMBER(19,0) NOT NULL,
    N_NOT_ORIGEN_API NUMBER(19,0) NOT NULL,
    N_NOT_ORIGEN_WEB NUMBER(19,0) NOT NULL,
    N_NOT_DESTI_ADM NUMBER(19,0) NOT NULL,
    N_NOT_DESTI_CIUTADA NUMBER(19,0) NOT NULL,
    N_COM_TOTAL NUMBER(19,0) NOT NULL,
    N_COM_CORRECTES NUMBER(19,0) NOT NULL,
    N_COM_AMB_ERROR NUMBER(19,0) NOT NULL,
    N_COM_ORIGEN_API NUMBER(19,0) NOT NULL,
    N_COM_ORIGEN_WEB NUMBER(19,0) NOT NULL,
    N_COM_DESTI_ADM NUMBER(19,0) NOT NULL,
    N_COM_DESTI_CIUTADA NUMBER(19,0) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP (6),
    VERSION NUMBER(19,0) NOT NULL
)
/

CREATE TABLE NOT_HIST_ORGAN
(
    ID NUMBER(19,0) NOT NULL,
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    ORGAN_ID NUMBER(19,0),
    TIPUS NUMBER(10,0) NOT NULL,
    DATA TIMESTAMP (6) NOT NULL,
    CODI_DIR3 VARCHAR2(64 CHAR) NOT NULL,
    NOM VARCHAR2(64 CHAR) NOT NULL,
    N_PROCEDIMENTS NUMBER(19,0) NOT NULL,
    N_GRUPS NUMBER(19,0) NOT NULL,
    N_PERM_CONSULTA NUMBER(19,0) NOT NULL,
    N_PERM_NOTIFICACIO NUMBER(19,0) NOT NULL,
    N_PERM_GESTIO NUMBER(19,0) NOT NULL,
    N_PERM_PROCESSAR NUMBER(19,0) NOT NULL,
    N_PERM_ADMINISTRAR NUMBER(19,0) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP (6),
    VERSION NUMBER(19,0) NOT NULL
)
/

CREATE TABLE NOT_HIST_PROCEDIMENT
(
    ID NUMBER(19,0) NOT NULL,
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    ORGAN_ID NUMBER(19,0),
    TIPUS NUMBER(10,0) NOT NULL,
    DATA TIMESTAMP (6) NOT NULL,
    PROCEDIMENT_ID NUMBER(19,0) NOT NULL,
    COMU NUMBER(1,0) NOT NULL,
    CODI_SIA VARCHAR2(64 CHAR) NOT NULL,
    NOM VARCHAR2(64 CHAR) NOT NULL,
    N_GRUPS NUMBER(19,0) NOT NULL,
    N_PERM_CONSULTA NUMBER(19,0) NOT NULL,
    N_PERM_NOTIFICACIO NUMBER(19,0) NOT NULL,
    N_PERM_GESTIO NUMBER(19,0) NOT NULL,
    N_PERM_PROCESSAR NUMBER(19,0) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP (6),
    VERSION NUMBER(19,0) NOT NULL
)
/

CREATE TABLE NOT_MON_INT
(
    ID NUMBER(19) not null,
    CODI VARCHAR2(100 char) not null,
    DATA TIMESTAMP(6),
    DESCRIPCIO VARCHAR2(1024 char),
    TIPUS VARCHAR2(10 char),
    APLICACIO VARCHAR2(64 char),
    TEMPS_RESPOSTA NUMBER(19),
    ESTAT VARCHAR2(5 char),
    CODI_USUARI VARCHAR2(64 char),
    CODI_ENTITAT VARCHAR2(64 char),
    ERROR_DESCRIPCIO VARCHAR2(1024 char),
    EXCEPCIO_MSG VARCHAR2(1024 char),
    EXCEPCIO_STACKTRACE VARCHAR2(2048 char)
)
/

CREATE TABLE NOT_MON_INT_PARAM
(
    ID NUMBER(19, 0) NOT NULL,
    MON_INT_ID NUMBER(19, 0) NOT NULL,
    CODI VARCHAR2(64 CHAR) NOT NULL,
    VALOR VARCHAR2(1024 CHAR)
)
/

CREATE TABLE NOT_NOTIFICACIO
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CADUCITAT DATE,
    COM_TIPUS NUMBER(10,0) NOT NULL,
    CONCEPTE VARCHAR2(240 CHAR) NOT NULL,
    DESCRIPCIO VARCHAR2(1000 CHAR),
    EMISOR_DIR3CODI VARCHAR2(9 CHAR) NOT NULL,
    ENV_DATA_PROG DATE,
    ENV_TIPUS NUMBER(10,0) NOT NULL,
    CALLBACK_ERROR NUMBER(1,0),
    ESTAT NUMBER(10,0) NOT NULL,
    ESTAT_DATE TIMESTAMP (6),
    GRUP_CODI VARCHAR2(64 CHAR),
    MOTIU VARCHAR2(255 CHAR),
    NOT_ENV_DATA TIMESTAMP (6),
    NOT_ENV_INTENT NUMBER(10,0),
    NOT_ERROR_TIPUS NUMBER(10,0),
    REGISTRE_NUM_EXPEDIENT VARCHAR2(80 CHAR),
    PROC_CODI_NOTIB VARCHAR2(9 CHAR),
    REGISTRE_DATA TIMESTAMP (6),
    REGISTRE_ENV_INTENT NUMBER(10,0),
    REGISTRE_NUMERO NUMBER(10,0),
    REGISTRE_NUMERO_FORMATAT VARCHAR2(200 CHAR),
    RETARD_POSTAL NUMBER(10,0),
    TIPUS_USUARI NUMBER(10,0),
    USUARI_CODI VARCHAR2(64 CHAR) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    DOCUMENT_ID NUMBER(19,0) NOT NULL,
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    PROCEDIMENT_ID NUMBER(19,0),
    ORGAN_GESTOR VARCHAR2(64 CHAR),
    PROCEDIMENT_ORGAN_ID NUMBER(19,0),
    NOT_ENV_DATA_NOTIFICA TIMESTAMP (6),
    IDIOMA NUMBER(10,0),
    DOCUMENT2_ID NUMBER(19,0),
    DOCUMENT3_ID NUMBER(19,0),
    DOCUMENT4_ID NUMBER(19,0),
    DOCUMENT5_ID NUMBER(19,0),
    REGISTRE_OFICINA_NOM VARCHAR2(255 CHAR),
    REGISTRE_LLIBRE_NOM VARCHAR2(255 CHAR),
    IS_ERROR_LAST_EVENT NUMBER(1,0) DEFAULT 0,
    NOTIFICACIO_MASSIVA_ID NUMBER(19,0) DEFAULT NULL,
    ESTAT_PROCESSAT_DATE TIMESTAMP(6),
    JUSTIFICANT_CREAT NUMBER(1,0),
    REFERENCIA VARCHAR2(36 CHAR)
)
/

CREATE TABLE NOT_NOTIFICACIO_AUDIT
(
    ID NUMBER(19,0) NOT NULL,
	TIPUSOPERACIO VARCHAR2(20 CHAR) NOT NULL, 
	JOINPOINT VARCHAR2(256 CHAR), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	NOTIFICACIO_ID NUMBER(19,0) NOT NULL, 
	SINCRON VARCHAR2(20 CHAR), 
	TIPUS_USUARI VARCHAR2(20 CHAR), 
	USUARI VARCHAR2(64 CHAR), 
	EMISOR VARCHAR2(9 CHAR) NOT NULL, 
	TIPUS VARCHAR2(20 CHAR) NOT NULL, 
	ENTITAT_ID NUMBER(19,0), 
	ORGAN VARCHAR2(64 CHAR), 
	PROCEDIMENT VARCHAR2(64 CHAR), 
	GRUP VARCHAR2(64 CHAR), 
	CONCEPTE VARCHAR2(255 CHAR), 
	DESCRIPCIO VARCHAR2(1000 CHAR), 
	NUM_EXPEDIENT VARCHAR2(80 CHAR), 
	ENV_DATA_PROG DATE, 
	RETARD_POSTAL NUMBER(10,0), 
	CADUCITAT DATE, 
	DOCUMENT_ID NUMBER(19,0) NOT NULL, 
	ESTAT VARCHAR2(32 CHAR) NOT NULL,
	ESTAT_DATE TIMESTAMP (6), 
	MOTIU VARCHAR2(255 CHAR), 
	REGISTRE_ENV_INTENT NUMBER(10,0), 
	REGISTRE_NUMERO NUMBER(10,0), 
	REGISTRE_NUMERO_FORMATAT VARCHAR2(200 CHAR), 
	REGISTRE_DATA DATE, 
	NOT_ENV_DATA TIMESTAMP (6), 
	NOT_ENV_INTENT NUMBER(10,0), 
	NOT_ERROR_TIPUS VARCHAR2(30 CHAR), 
	CALLBACK_ERROR NUMBER(1,0), 
	EVENT_ERROR NUMBER(19,0),
    ESTAT_PROCESSAT_DATE TIMESTAMP(6),
    REFERENCIA VARCHAR2(36 CHAR)
)
/

CREATE TABLE NOT_NOTIFICACIO_ENV
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    DEH_NIF VARCHAR2(9 CHAR),
    DEH_OBLIGAT NUMBER(1,0),
    DEH_PROC_CODI VARCHAR2(64 CHAR),
    NOTIFICA_ARR_DIR3DESC VARCHAR2(100 CHAR),
    NOTIFICA_ARR_DIR3CODI VARCHAR2(9 CHAR),
    NOTIFICA_ARR_DIR3NIF VARCHAR2(9 CHAR),
    NOTIFICA_CER_ARXIUID VARCHAR2(50 CHAR),
    NOTIFICA_CER_ARXTIP NUMBER(10,0),
    NOTIFICA_CER_CSV VARCHAR2(50 CHAR),
    NOTIFICA_CER_DATA TIMESTAMP (6),
    NOTIFICA_CER_HASH VARCHAR2(50 CHAR),
    NOTIFICA_CER_METAS VARCHAR2(255 CHAR),
    NOTIFICA_CER_MIME VARCHAR2(20 CHAR),
    NOTIFICA_CER_NUMSEG VARCHAR2(50 CHAR),
    NOTIFICA_CER_ORIGEN VARCHAR2(20 CHAR),
    NOTIFICA_CER_TAMANY NUMBER(10,0),
    NOTIFICA_CER_TIPUS NUMBER(10,0),
    NOTIFICA_DATCAD TIMESTAMP (6),
    NOTIFICA_DATCRE TIMESTAMP (6),
    NOTIFICA_DATDISP TIMESTAMP (6),
    NOTIFICA_DATAT_ERRDES VARCHAR2(255 CHAR),
    NOTIFICA_DATAT_NUMSEG VARCHAR2(50 CHAR),
    NOTIFICA_DATAT_ORIGEN VARCHAR2(20 CHAR),
    NOTIFICA_DATAT_RECNIF VARCHAR2(9 CHAR),
    NOTIFICA_DATAT_RECNOM VARCHAR2(400 CHAR),
    NOTIFICA_EMI_DIR3DESC VARCHAR2(100 CHAR),
    NOTIFICA_EMI_DIR3CODI VARCHAR2(9 CHAR),
    NOTIFICA_EMI_DIR3NIF VARCHAR2(9 CHAR),
    NOTIFICA_ERROR NUMBER(1,0) NOT NULL,
    NOTIFICA_ESTAT NUMBER(10,0) NOT NULL,
    NOTIFICA_ESTAT_DATA TIMESTAMP (6),
    NOTIFICA_ESTAT_DATAACT TIMESTAMP (6),
    NOTIFICA_ESTAT_DESC VARCHAR2(255 CHAR),
    NOTIFICA_ESTAT_FINAL NUMBER(1,0),
    NOTIFICA_ID VARCHAR2(20 CHAR),
    NOTIFICA_INTENT_DATA TIMESTAMP (6),
    NOTIFICA_INTENT_NUM NUMBER(10,0),
    NOTIFICA_REF VARCHAR2(36 CHAR),
    NOTIFICACIO_ID NUMBER(19,0),
    REGISTRE_DATA TIMESTAMP (6),
    ESTAT_REGISTRE NUMBER(10,0),
    REGISTRE_ESTAT_FINAL NUMBER(1,0),
    REGISTRE_NUMERO_FORMATAT VARCHAR2(50 CHAR),
    SERVEI_TIPUS NUMBER(10,0),
    SIR_CON_DATA TIMESTAMP (6),
    SIR_CON_INTENT NUMBER(10,0),
    SIR_REC_DATA TIMESTAMP (6),
    SIR_REG_DESTI_DATA TIMESTAMP (6),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    NOTIFICA_ERROR_EVENT_ID NUMBER(19,0),
    TITULAR_ID NUMBER(19,0),
    DEH_CERT_INTENT_NUM NUMBER(10,0) DEFAULT 0,
    DEH_CERT_INTENT_DATA TIMESTAMP (6),
    CIE_CERT_INTENT_NUM NUMBER(10,0) DEFAULT 0,
    CIE_CERT_INTENT_DATA TIMESTAMP (6),
    ENTREGA_POSTAL_ID NUMBER(19,0),
    PER_EMAIL NUMBER(1)
)
/

CREATE TABLE NOT_NOTIFICACIO_ENV_AUDIT 
(
    ID NUMBER(19,0) NOT NULL,
	TIPUSOPERACIO VARCHAR2(20 CHAR) NOT NULL, 
	JOINPOINT VARCHAR2(256 CHAR), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	ENVIAMENT_ID NUMBER(19,0) NOT NULL, 
	NOTIFICACIO_ID NUMBER(19,0) NOT NULL, 
	TITULAR_ID NUMBER(19,0), 
	DESTINATARIS_ID VARCHAR2(200 CHAR), 
	DOMICILI_TIPUS VARCHAR2(20 CHAR), 
	DOMICILI VARCHAR2(500 CHAR), 
	SERVEI_TIPUS VARCHAR2(20 CHAR), 
	CIE NUMBER(10,0), 
	FORMAT_SOBRE VARCHAR2(10 CHAR), 
	FORMAT_FULLA VARCHAR2(10 CHAR), 
	DEH_OBLIGAT NUMBER(1,0), 
	DEH_NIF VARCHAR2(9 CHAR), 
	NOTIFICA_REF VARCHAR2(36 CHAR),
	NOTIFICA_ID VARCHAR2(20 CHAR), 
	NOTIFICA_DATCRE TIMESTAMP (6), 
	NOTIFICA_DATDISP TIMESTAMP (6), 
	NOTIFICA_DATCAD TIMESTAMP (6), 
	NOTIFICA_EMI_DIR3CODI VARCHAR2(9 CHAR), 
	NOTIFICA_ARR_DIR3CODI VARCHAR2(9 CHAR), 
	NOTIFICA_ESTAT NUMBER(10,0), 
	NOTIFICA_ESTAT_DATA TIMESTAMP (6), 
	NOTIFICA_ESTAT_FINAL NUMBER(1,0), 
	NOTIFICA_DATAT_ORIGEN VARCHAR2(20 CHAR), 
	NOTIFICA_DATAT_RECNIF VARCHAR2(9 CHAR), 
	NOTIFICA_DATAT_NUMSEG VARCHAR2(50 CHAR), 
	NOTIFICA_CER_DATA TIMESTAMP (6), 
	NOTIFICA_CER_ARXIUID VARCHAR2(50 CHAR), 
	NOTIFICA_CER_ORIGEN VARCHAR2(20 CHAR), 
	NOTIFICA_CER_TIPUS VARCHAR2(20 CHAR), 
	NOTIFICA_CER_ARXTIP VARCHAR2(20 CHAR), 
	NOTIFICA_CER_NUMSEG VARCHAR2(50 CHAR), 
	REGISTRE_NUMERO_FORMATAT VARCHAR2(50 CHAR), 
	REGISTRE_DATA TIMESTAMP (6), 
	REGISTRE_ESTAT VARCHAR2(20 CHAR), 
	REGISTRE_ESTAT_FINAL NUMBER(1,0), 
	SIR_CON_DATA TIMESTAMP (6), 
	SIR_REC_DATA TIMESTAMP (6), 
	SIR_REG_DESTI_DATA TIMESTAMP (6), 
	NOTIFICA_ERROR_EVENT_ID NUMBER(19,0), 
	NOTIFICA_ERROR NUMBER(1,0), 
	NOTIFICA_DATAT_ERRDES VARCHAR2(255 CHAR)
)
/
	
CREATE TABLE NOT_NOTIFICACIO_ENV_TABLE 
(
    ID NUMBER(19,0) NOT NULL,
	NOTIFICACIO_ID NUMBER(19,0) NOT NULL, 
	ENTITAT_ID NUMBER(19,0) NOT NULL, 
	DESTINATARIS VARCHAR2(4000 CHAR) DEFAULT '', 
	TIPUS_ENVIAMENT NUMBER(10,0) NOT NULL, 
	TITULAR_NIF VARCHAR2(9 CHAR), 
	TITULAR_NOM VARCHAR2(256 CHAR), 
	TITULAR_EMAIL VARCHAR2(160 CHAR), 
	TITULAR_LLINATGE1 VARCHAR2(40 CHAR), 
	TITULAR_LLINATGE2 VARCHAR2(40 CHAR), 
	TITULAR_RAOSOCIAL VARCHAR2(100 CHAR), 
	DATA_PROGRAMADA DATE, 
	PROCEDIMENT_CODI_NOTIB VARCHAR2(9 CHAR),
	GRUP_CODI VARCHAR2(64 CHAR), 
	EMISOR_DIR3 VARCHAR2(9 CHAR), 
	USUARI_CODI VARCHAR2(64 CHAR), 
	NOT_ORGAN_CODI VARCHAR2(64 CHAR),
	CONCEPTE VARCHAR2(240 CHAR), 
	DESCRIPCIO VARCHAR2(1000 CHAR), 
	LLIBRE VARCHAR2(255 CHAR), 
	NOT_ESTAT NUMBER(10,0) NOT NULL, 
	CSV_UUID VARCHAR2(256 CHAR), 
	HAS_ERRORS NUMBER(1,0) DEFAULT 0 NOT NULL, 
	PROCEDIMENT_IS_COMU NUMBER(1,0) DEFAULT 0, 
	PROCEDIMENT_PROCORGAN_ID NUMBER(19,0), 
	REGISTRE_NUMERO NUMBER(10,0), 
	REGISTRE_DATA DATE, 
	REGISTRE_ENVIAMENT_INTENT NUMBER(10,0), 
	NOTIFICA_DATA_CADUCITAT TIMESTAMP (6), 
	NOTIFICA_IDENTIFICADOR VARCHAR2(20 CHAR), 
	NOTIFICA_CERT_NUM_SEGUIMENT VARCHAR2(50 CHAR), 
	NOTIFICA_ESTAT NUMBER(10,0) NOT NULL, 
	NOTIFICA_REF VARCHAR2(36 CHAR),
	CREATEDDATE TIMESTAMP (6), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	ORGAN_ESTAT NUMBER(10,0) DEFAULT 0 NOT NULL,
    PROCEDIMENT_REQUIRE_PERMISSION NUMBER(1) DEFAULT 0 NOT NULL,
    PROCEDIMENT_TIPUS VARCHAR2(32 CHAR)
)
/

CREATE TABLE NOT_NOTIFICACIO_EVENT
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CALLBACK_DATA TIMESTAMP (6),
    CALLBACK_ERROR_DESC VARCHAR2(2048 CHAR),
    CALLBACK_ESTAT VARCHAR2(10 CHAR),
    CALLBACK_INTENTS NUMBER(10,0),
    DATA TIMESTAMP (6) NOT NULL,
    DESCRIPCIO VARCHAR2(256 CHAR),
    ERROR NUMBER(1,0) NOT NULL,
    ERROR_DESC VARCHAR2(2048 CHAR),
    NOTIFICACIO_ID NUMBER(19,0),
    TIPUS NUMBER(10,0) NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    NOTIFICACIO_ENV_ID NUMBER(19,0),
    NOTIFICA_ERROR_TIPUS NUMBER(10,0),
    FI_REINTENTS NUMBER(1) DEFAULT '0'
)
/

CREATE TABLE NOT_NOTIFICACIO_MASSIVA
(
    ID NUMBER(19,0) NOT NULL,
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    CSV_GESDOC_ID VARCHAR2(64 CHAR) NOT NULL,
    ZIP_GESDOC_ID VARCHAR2(64 CHAR),
    CSV_FILENAME VARCHAR2(200 CHAR) NOT NULL,
    ZIP_FILENAME VARCHAR2(200 CHAR),
    CADUCITAT TIMESTAMP (6) NOT NULL,
    EMAIL VARCHAR2(64 CHAR),
    PAGADOR_POSTAL_ID NUMBER(19,0),
    RESUM_GESDOC_ID VARCHAR2(64 CHAR),
    ERRORS_GESDOC_ID VARCHAR2(64 CHAR),
    PROGRESS NUMBER(3,0) DEFAULT 0 NOT NULL,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP (6),
    ESTAT_VALIDACIO     VARCHAR2(32),
    ESTAT_PROCES        VARCHAR2(32),
    NUM_NOTIFICACIONS   NUMBER(10),
    NUM_VALIDADES       NUMBER(10),
    NUM_PROCESSADES     NUMBER(10),
    NUM_ERROR           NUMBER(10),
    NUM_CANCELADES      NUMBER(38)
)
/

CREATE TABLE NOT_NOTIFICACIO_TABLE
(
    ID NUMBER(19,0) NOT NULL,
    ENTITAT_ID NUMBER(19,0) NOT NULL,
    ENTITAT_NOM VARCHAR2(256 CHAR),
    PROC_CODI_NOTIB VARCHAR2(9 CHAR),
    PROCEDIMENT_ORGAN_ID NUMBER(19,0),
    GRUP_CODI VARCHAR2(64 CHAR),
    USUARI_CODI VARCHAR2(64 CHAR),
    TIPUS_USUARI NUMBER(10,0),
    ERROR_LAST_CALLBACK NUMBER(1,0) DEFAULT 0 NOT NULL,
    ERROR_LAST_EVENT NUMBER(1,0) DEFAULT 0 NOT NULL,
    REGISTRE_ENV_INTENT NUMBER(10,0),
    REGISTRE_NUM_EXPEDIENT VARCHAR2(80 CHAR),
    NOTIFICA_ERROR_DATE DATE,
    NOTIFICA_ERROR_DESCRIPCIO VARCHAR2(2048 CHAR),
    ENV_TIPUS NUMBER(10,0) NOT NULL,
    CONCEPTE VARCHAR2(255 CHAR) NOT NULL,
    ESTAT NUMBER(10,0) NOT NULL,
    ESTAT_DATE TIMESTAMP (6),
    PROCEDIMENT_CODI VARCHAR2(64 CHAR),
    PROCEDIMENT_NOM VARCHAR2(256 CHAR),
    PROCEDIMENT_IS_COMU NUMBER(1,0) DEFAULT 0 NOT NULL,
    ORGAN_CODI VARCHAR2(64 CHAR),
    ORGAN_NOM VARCHAR2(1000 CHAR),
    CREATEDDATE TIMESTAMP (6),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDDATE TIMESTAMP (6),
    NOTIFICACIO_MASSIVA_ID NUMBER(19,0) DEFAULT NULL,
    ORGAN_ESTAT NUMBER(10,0) DEFAULT 0 NOT NULL,
    PROCEDIMENT_REQUIRE_PERMISSION NUMBER(1)  default 0 not null,
    ESTAT_PROCESSAT_DATE TIMESTAMP(6),
    PROCEDIMENT_TIPUS VARCHAR2(32 CHAR),
    ENVIADA_DATE TIMESTAMP(6),
    REFERENCIA VARCHAR2(36 CHAR),
    TITULAR VARCHAR2(2000 CHAR),
    NOTIFICA_IDS VARCHAR2(2000 CHAR),
    ESTAT_MASK NUMBER
)
/

CREATE TABLE NOT_OFICINA
(
    CODI VARCHAR2(16 CHAR) NOT NULL,
    NOM VARCHAR2(1024 CHAR),
    SIR NUMBER(1) NOT NULL,
    ACTIU NUMBER(1) NOT NULL,
    ORGAN_CODI VARCHAR2(16 CHAR),
    ENTITAT_ID NUMBER(38)
)
/

CREATE TABLE NOT_ORGAN_GESTOR
(
    ID NUMBER(19,0) NOT NULL,
    CODI VARCHAR2(64 CHAR) NOT NULL,
    ENTITAT NUMBER(19,0),
    NOM VARCHAR2(1000 CHAR),
    LLIBRE VARCHAR2(255 CHAR),
    LLIBRE_NOM VARCHAR2(255 CHAR),
    OFICINA VARCHAR2(255 CHAR),
    OFICINA_NOM VARCHAR2(255 CHAR),
    ENTREGA_CIE_ID NUMBER(19,0) DEFAULT NULL,
    CODI_PARE VARCHAR2(32 CHAR),
    ESTAT VARCHAR2(1 CHAR) DEFAULT 'V',
    SIR NUMBER(1) DEFAULT '0',
    TIPUS_TRANSICIO VARCHAR2(12 CHAR),
    NO_VIGENT NUMBER(1)
)
/

CREATE TABLE NOT_OG_SINC_REL
(
    ANTIC_OG NUMBER(38) NOT NULL,
    NOU_OG   NUMBER(38) NOT NULL
)
/

CREATE TABLE NOT_PAGADOR_CIE
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CONTRACTE_DATA_VIG DATE,
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    ENTITAT NUMBER(19,0),
    ORGAN_GESTOR NUMBER(19,0),
    NOM VARCHAR2(256 CHAR) NOT NULL
)
/

CREATE TABLE NOT_PAGADOR_POSTAL
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CONTRACTE_DATA_VIG DATE,
    CONTRACTE_NUM VARCHAR2(20 CHAR),
    FACTURACIO_CODI_CLIENT VARCHAR2(20 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    ENTITAT NUMBER(19,0),
    ORGAN_GESTOR NUMBER(19,0),
    NOM VARCHAR2(256 CHAR) NOT NULL
)
/

CREATE TABLE NOT_PERSONA
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    COD_ENTITAT_DESTI VARCHAR2(9 CHAR),
    EMAIL VARCHAR2(160 CHAR),
    INCAPACITAT NUMBER(1,0),
    INTERESSATTIPUS VARCHAR2(255 CHAR) NOT NULL,
    LLINATGE1 VARCHAR2(40 CHAR),
    LLINATGE2 VARCHAR2(40 CHAR),
    NIF VARCHAR2(9 CHAR),
    NOM VARCHAR2(255 CHAR),
    RAO_SOCIAL VARCHAR2(100 CHAR),
    TELEFON VARCHAR2(16 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    NOTIFICACIO_ENV_ID NUMBER(19,0),
    DOCUMENT_TIPUS VARCHAR2(32 CHAR)
)
/

CREATE TABLE NOT_PROCEDIMENT
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    AGRUPAR NUMBER(1,0) NOT NULL,
    CADUCITAT NUMBER(10,0),
    CODI VARCHAR2(64 CHAR) NOT NULL,
    CODIASSUMPTE VARCHAR2(255 CHAR),
    CODIASSUMPTE_NOM VARCHAR2(255 CHAR),
    NOM VARCHAR2(256 CHAR) NOT NULL,
    RETARD NUMBER(10,0),
    TIPUSASSUMPTE VARCHAR2(255 CHAR),
    TIPUSASSUMPTE_NOM VARCHAR2(255 CHAR),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    ENTITAT NUMBER(19,0),
    ORGAN_GESTOR VARCHAR2(64 CHAR),
    COMU NUMBER(1,0) DEFAULT 0 NOT NULL,
    ULTIMA_ACT DATE,
    DIRECT_PERMISSION_REQUIRED NUMBER(1,0) DEFAULT 0 NOT NULL,
    ENTREGA_CIE_ID NUMBER(19,0) DEFAULT NULL,
    TIPUS VARCHAR2(32 CHAR),
    ORGAN_NO_SINC NUMBER(1) NOT NULL,
    ACTIU NUMBER(1) DEFAULT 1 NOT NULL
)
/

CREATE TABLE NOT_PROCEDIMENT_AUDIT 
(
    ID NUMBER(19,0) NOT NULL,
	TIPUSOPERACIO VARCHAR2(20 CHAR) NOT NULL, 
	JOINPOINT VARCHAR2(256 CHAR), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	PROCEDIMENT_ID NUMBER(19,0) NOT NULL, 
	ENTITAT_ID NUMBER(19,0), 
	ORGAN VARCHAR2(64 CHAR), 
	CODI VARCHAR2(64 CHAR) NOT NULL, 
	NOM VARCHAR2(256 CHAR), 
	RETARD NUMBER(10,0), 
	CADUCITAT NUMBER(10,0), 
	AGRUPAR NUMBER(1,0), 
	COMU NUMBER(1,0), 
	PAGADORPOSTAL_ID NUMBER(19,0), 
	PAGADORCIE_ID NUMBER(19,0)
)
/

CREATE TABLE NOT_PROCESSOS_INICIALS
(
    ID   NUMBER(19) NOT NULL,
    CODI VARCHAR2(100 CHAR) NOT NULL,
    INIT NUMBER(1) NOT NULL
)
/

CREATE TABLE NOT_PRO_GRUP
(
    ID NUMBER(19,0) NOT NULL,
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR),
    GRUP NUMBER(19,0),
    PROCEDIMENT NUMBER(19,0)
)
/

CREATE TABLE NOT_PRO_GRUP_AUDIT 
(
    ID NUMBER(19,0) NOT NULL,
	TIPUSOPERACIO VARCHAR2(20 CHAR) NOT NULL, 
	JOINPOINT VARCHAR2(256 CHAR), 
	CREATEDBY_CODI VARCHAR2(64 CHAR), 
	CREATEDDATE TIMESTAMP (6), 
	LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), 
	LASTMODIFIEDDATE TIMESTAMP (6), 
	PROGRUP_ID NUMBER(19,0) NOT NULL, 
	PROCEDIMENT VARCHAR2(64 CHAR) NOT NULL, 
	GRUP VARCHAR2(64 CHAR) NOT NULL
)
/

CREATE TABLE NOT_PRO_ORGAN
(
    ID NUMBER(19,0),
    PROCEDIMENT_ID NUMBER(19,0),
    ORGANGESTOR_ID NUMBER(19,0),
    CREATEDDATE TIMESTAMP (6),
    LASTMODIFIEDDATE TIMESTAMP (6),
    CREATEDBY_CODI VARCHAR2(64 CHAR),
    LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR)
)
/

CREATE TABLE NOT_USUARI 
(
    CODI VARCHAR2(64 CHAR) NOT NULL,
	EMAIL VARCHAR2(200 CHAR), 
	IDIOMA VARCHAR2(2 CHAR), 
	LLINATGES VARCHAR2(100 CHAR), 
	NOM VARCHAR2(100 CHAR), 
	NOM_SENCER VARCHAR2(200 CHAR), 
	REBRE_EMAILS NUMBER(1,0), 
	VERSION NUMBER(19,0) NOT NULL, 
	ULTIM_ROL VARCHAR2(40 CHAR), 
	ULTIMA_ENTITAT NUMBER(19,0), 
	REBRE_EMAILS_CREATS NUMBER(1,0) DEFAULT 0 NOT NULL,
    EMAIL_ALT VARCHAR2(200 CHAR)
)
/
