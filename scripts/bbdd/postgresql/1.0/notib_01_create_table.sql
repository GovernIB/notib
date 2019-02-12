
CREATE TABLE NOT_USUARI
(
  CODI          character varying(64)           NOT NULL,
  NOM           character varying(100),
  LLINATGES     character varying(100),
  NOM_SENCER    character varying(200),
  EMAIL         character varying(200),
  VERSION       bigint                     		NOT NULL
);

CREATE TABLE NOT_ENTITAT
(
  ID                   BIGSERIAL                NOT NULL,
  CODI                 character varying(64)    NOT NULL,
  NOM                  character varying(256)   NOT NULL,
  DESCRIPCIO           character varying(1024),
  TIPUS                character varying(32), 
  CIF                  character varying(9)     NOT NULL,
  DIR3_CODI            character varying(9),
  ACTIVA               boolean                  NOT NULL,
  VERSION              bigint                   NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);

CREATE TABLE NOT_APLICACIO
(
  ID                   BIGSERIAL                NOT NULL,
  USUARI_CODI          character varying(64)    NOT NULL,
  CALLBACK_URL         character varying(256),
  TIPUS_AUTENTICACIO   integer,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);

CREATE TABLE NOT_NOTIFICACIO 
(
  ID						BIGSERIAL					NOT NULL,
  EMISOR_DIR3CODI			character varying(9)		NOT NULL,
  COM_TIPUS					integer						NOT NULL,
  ENV_TIPUS					integer						NOT NULL,
  ENV_DATA_PROG				date,
  RETARD_POSTAL				integer(19),
  CONCEPTE					character varying(50)		NOT NULL,
  DESCRIPCIO           		character varying(100),
  PAGCOR_DIR3				character varying(9),
  PAGCOR_NUMCONT			character varying(20),
  PAGCOR_CODI_CLIENT		character varying(20),
  PAGCOR_DATA_VIG			date,
  PAGCIE_DIR3				character varying(9),
  PAGCIE_DATA_VIG			date,
  PROC_CODI_NOTIB			character varying(6)		NOT NULL,
  PROC_CODI_SIA				character varying(6)    	NOT NULL,
  PROC_DESC_SIA				character varying(256),
  CADUCITAT            		date,
  DOC_ARXIU_NOM				character varying(256)  	NOT NULL,
  DOC_ARXIU_ID				character varying(64)   	NOT NULL,
  DOC_HASH					character varying(40)   	NOT NULL,
  DOC_NORMALITZAT			boolean                 	NOT NULL,
  DOC_GEN_CSV				boolean                 	NOT NULL,
  SEU_EXP_SERDOC			character varying(10)   	NOT NULL,
  SEU_EXP_UNIORG			character varying(10)   	NOT NULL,
  SEU_EXP_IDENI				character varying(52)   	NOT NULL,
  SEU_EXP_TITOL				character varying(256)  	NOT NULL,
  SEU_REG_OFICINA			character varying(256)  	NOT NULL,
  SEU_REG_LLIBRE			character varying(256)  	NOT NULL,
  SEU_REG_ORGAN        		character varying(256),
  SEU_IDIOMA				character varying(256)  	NOT NULL,
  SEU_AVIS_TITOL			character varying(256)  	NOT NULL,
  SEU_AVIS_TEXT				character varying(256)  	NOT NULL,
  SEU_AVIS_MOBIL			character varying(256),
  SEU_OFICI_TITOL			character varying(256)  	NOT NULL,
  SEU_OFICI_TEXT			character varying(256)  	NOT NULL,
  SEU_PROC_CODI        		character varying(256),
  ESTAT						integer                 	NOT NULL,
  NOT_ERROR_TIPUS      		integer,
  NOT_ENV_DATA         		timestamp without time zone,
  NOT_ENV_INTENT       		integer						NOT NULL,
  NOT_REENV_DATA       		timestamp without time zone,
  NOT_ERROR_EVENT_ID   		integer,
  ENTITAT_ID				bigint                  	NOT NULL,
  REGISTRE_ORGAN			character varying(10),
  REGISTRE_OFICINA			character varying(10),
  REGISTRE_LLIBRE			character varying(10),
  REGISTRE_EXTRACTE			character varying(52)		NOT NULL,
  REGISTRE_DOC_FISICA		character varying(256)		NOT NULL,
  REGISTRE_IDIOMA			character varying(52)		NOT NULL,
  REGISTRE_TIPUS_ASSUMPTE	character varying(256)		NOT NULL,
  REGISTRE_NUM_EXPEDIENT	character varying(256)		NOT NULL,
  REGISTRE_REF_EXTERNA		character varying(52)		NOT NULL,
  REGISTRE_CODI_ASSUMPTE	character varying(256)		NOT NULL,
  REGISTRE_OBSERVACIONS		character varying(256)		NOT NULL,
  GRUP_CODI					character varying(64),
  CSV_UUID					character varying(64),
  CREATEDBY_CODI			character varying(64),
  CREATEDDATE				timestamp without time zone,
  LASTMODIFIEDBY_CODI		character varying(64),
  LASTMODIFIEDDATE			timestamp without time zone
);

CREATE TABLE NOT_NOTIFICACIO_DEST 
(
  ID                   BIGSERIAL                NOT NULL,
  TITULAR_NOM          character varying(125)   NOT NULL,
  TITULAR_LLINATGES    character varying(125),
  TITULAR_NIF          character varying(9)     NOT NULL,
  TITULAR_TELEFON      character varying(16),
  TITULAR_EMAIL        character varying(100)   NOT NULL,
  DESTINATARI_NOM      character varying(125)   NOT NULL,
  DESTINATARI_LLINATGES  character varying(125),
  DESTINATARI_NIF      character varying(9)     NOT NULL,
  DESTINATARI_TELEFON  character varying(16),
  DESTINATARI_EMAIL    character varying(100)   NOT NULL,
  DOM_TIPUS            integer,
  DOM_CON_TIPUS        integer,
  DOM_VIA_TIPUS        character varying(5),
  DOM_VIA_NOM          character varying(100),
  DOM_NUM_TIPUS        integer,
  DOM_NUM_NUM          character varying(10),
  DOM_NUM_PUNTKM       character varying(10),
  DOM_APARTAT          character varying(10),
  DOM_BLOC             character varying(50),
  DOM_PORTAL           character varying(50),
  DOM_ESCALA           character varying(50),
  DOM_PLANTA           character varying(50),
  DOM_PORTA            character varying(50),
  DOM_COMPLEM          character varying(250),
  DOM_POBLACIO         character varying(30),
  DOM_MUN_CODINE       character varying(6),
  DOM_MUN_NOM          character varying(64),
  DOM_CODI_POSTAL      character varying(10),
  DOM_PRV_CODI         character varying(2),
  DOM_PRV_NOM          character varying(64),
  DOM_PAI_CODISO       character varying(3),
  DOM_PAI_NOM          character varying(64),
  DOM_LINEA1           character varying(50),
  DOM_LINEA2           character varying(50),
  DOM_CIE              integer,
  DEH_OBLIGAT          boolean                  NOT NULL,
  DEH_NIF              character varying(9)     NOT NULL,
  DEH_PROC_CODI        character varying(6), 
  SERVEI_TIPUS         integer                  NOT NULL,
  RETARD_POSTAL        integer                  NOT NULL,
  CADUCITAT            date,
  REFERENCIA           character varying(20),
  NOTIFICA_ID          character varying(20),
  NOTIFICA_EST_ESTAT   integer                  NOT NULL,
  NOTIFICA_EST_DATA    date,
  NOTIFICA_EST_RECNOM  character varying(100),
  NOTIFICA_EST_RECNIF  character varying(9),
  NOTIFICA_EST_ORIGEN  character varying(50),
  NOTIFICA_EST_NUMSEG  character varying(50),
  NOTIFICA_CER_TIPUS   integer,
  NOTIFICA_CER_ARXTIP  integer,
  NOTIFICA_CER_ARXID   character varying(50),
  NOTIFICA_CER_NUMSEG  character varying(50),
  NOTIFICA_CER_DATACT  date,
  NOTIFICA_ERROR       boolean                  NOT NULL,
  NOTIFICA_ERROR_EVENT_ID  integer,
  SEU_REG_NUMERO       character varying(50),
  SEU_REG_DATA         timestamp without time zone,
  SEU_DATA_FI          timestamp without time zone,
  SEU_ESTAT            integer                  NOT NULL,
  SEU_ERROR            boolean                  NOT NULL,
  SEU_ERROR_EVENT_ID   integer,
  SEU_DATA_ENVIAM      timestamp without time zone,
  SEU_DATA_ESTAT       timestamp without time zone,
  SEU_DATA_NOTINF      timestamp without time zone,
  SEU_DATA_NOTIDP      timestamp without time zone,
  NOTIFICACIO_ID       bigint                   NOT NULL,
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);

CREATE TABLE NOT_NOTIFICACIO_EVENT
(
  ID                   BIGSERIAL                NOT NULL,
  TIPUS                integer                  NOT NULL,
  DATA                 timestamp without time zone             NOT NULL,
  DESCRIPCIO           character varying(256),
  ERROR                boolean                  NOT NULL,
  ERROR_DESC           character varying(2048),
  NOTIFICACIO_ID       bigint                   NOT NULL,
  NOTIFICACIO_DEST_ID  bigint,
  CALLBACK_ESTAT       character varying(10),
  CALLBACK_DATA        timestamp without time zone,
  CALLBACK_INTENTS     integer,
  CALLBACK_ERROR_DESC  caracter varying(2048),
  CREATEDBY_CODI       character varying(64),
  CREATEDDATE          timestamp without time zone,
  LASTMODIFIEDBY_CODI  character varying(64),
  LASTMODIFIEDDATE     timestamp without time zone
);


CREATE TABLE NOT_ACL_CLASS
(
  ID     BIGSERIAL                              NOT NULL,
  CLASS  character varying(100)                 NOT NULL
);


CREATE TABLE NOT_ACL_SID
(
  ID         BIGSERIAL                          NOT NULL,
  PRINCIPAL  boolean                            NOT NULL,
  SID        character varying(100)             NOT NULL
);


CREATE TABLE NOT_ACL_ENTRY
(
  ID                   BIGSERIAL                NOT NULL,
  ACL_OBJECT_IDENTITY  bigint                   NOT NULL,
  ACE_ORDER            bigint                   NOT NULL,
  SID                  bigint                   NOT NULL,
  MASK                 bigint                   NOT NULL,
  GRANTING             boolean                  NOT NULL,
  AUDIT_SUCCESS        boolean                  NOT NULL,
  AUDIT_FAILURE        boolean                  NOT NULL
);


CREATE TABLE NOT_ACL_OBJECT_IDENTITY
(
  ID                  BIGSERIAL                 NOT NULL,
  OBJECT_ID_CLASS     bigint                    NOT NULL,
  OBJECT_ID_IDENTITY  bigint                    NOT NULL,
  PARENT_OBJECT       bigint,
  OWNER_SID           bigint                    NOT NULL,
  ENTRIES_INHERITING  boolean                   NOT NULL
);

CREATE TABLE NOT_PERSONA
(
  ID					BIGSERIAL					NOT NULL,
  EMAIL					character varying(100),
  LLINATGE1				character varying(100)		NOT NULL,
  LLINATGE2				character varying(100),
  NIF					character varying(9)		NOT NULL,
  NOM					character varying(100),
  TELEFON				character varying(16),
  RAO_SOCIAL			character varying(100),
  COD_ENTITAT_DESTI		character varying(9),
  NOTIFICACIO_ENV_ID	BIGSERIAL
);
