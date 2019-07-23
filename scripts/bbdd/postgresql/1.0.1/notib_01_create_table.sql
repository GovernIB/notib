CREATE TABLE NOT_PROCEDIMENT 
(
  ID						BIGSERIAL(19)	    	NOT NULL,
  CODI          			character varying(64)		NOT NULL,
  NOM						character varying(100),
  CODISIA					character varying(64),
  ENTITAT					BIGSERIAL(19),
  PAGADORPOSTAL				BIGSERIAL(19),
  PAGADORCIE				BIGSERIAL(19),
  AGRUPAR					BIGSERIAL(1) 			NOT NULL,
  RETARD 					BIGSERIAL(19),
  LLIBRE					character varying(64),
  OFICINA					character varying(64),
  TIPUSASSUMPTE				character varying(255),
  CODIASSUMPTE				character varying(255),
  DATA_PROGRAMADA 			timestamp without time zone,
  ORGAN_GESTOR 				character varying(19),
  ORGAN_GESTOR_NOM			character varying(64),
  TIPUSASSUMPTE_NOM 		character varying(64),
  CODIASSUMPTE_NOM 			character varying(64),
  CADUCITAT					BIGSERIAL(19),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_COLUMNES
(
  ID                   		BIGSERIAL(19),
  CREATED_DATE      		BIGSERIAL(1),
  DATA_PROGRAMADA           BIGSERIAL(1),
  NOT_IDENTIFICADOR         BIGSERIAL(1),
  PRO_CODI             		BIGSERIAL(1),
  GRUP_CODI           		BIGSERIAL(1),
  DIR3_CODI					BIGSERIAL(1),
  USUARI					BIGSERIAL(1),
  ENVIAMENT_TIPUS          	BIGSERIAL(1),
  CONCEPTE       			BIGSERIAL(1),
  DESCRIPCIO   				BIGSERIAL(1),
  TITULAR_NIF      			BIGSERIAL(1),
  TITULAR_NOM_LLINATGE      BIGSERIAL(1),
  TITULAR_EMAIL      		BIGSERIAL(1),
  DESTINATARIS        	    BIGSERIAL(1),
  LLIBRE_REGISTRE        	BIGSERIAL(1),
  NUMERO_REGISTRE           BIGSERIAL(1),
  DATA_REGISTRE        		BIGSERIAL(1),
  DATA_CADUCITAT         	BIGSERIAL(1),
  CODI_NOTIB_ENV            BIGSERIAL(1),
  NUM_CERTIFICACIO      	BIGSERIAL(1),
  CSV_UUID          		BIGSERIAL(1),
  ESTAT                		BIGSERIAL(1),
  ENTITAT_ID				BIGSERIAL(19),
  USUARI_CODI				character varying(64),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_PRO_GRUP 
(
  ID						BIGSERIAL(19)	    	NOT NULL,
  GRUP						BIGSERIAL(19),			
  PROCEDIMENT				BIGSERIAL(19),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_GRUP 
(
  ID						BIGSERIAL(19)	    	NOT NULL,
  CODI						character varying(64)		NOT NULL,
  NOM						character varying(100),
  ENTITAT					BIGSERIAL(19),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_PAGADOR_POSTAL
(
  ID						BIGSERIAL(19)	    	NOT NULL,
  DIR3_CODI            		character varying(9)  		NOT NULL,
  ENTITAT					BIGSERIAL(19),
  CONTRACTE_NUM		       	character varying(20),
  CONTRACTE_DATA_VIG      	DATE,
  FACTURACIO_CODI_CLIENT   	character varying(20),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_PAGADOR_CIE
(
  ID						BIGSERIAL(19)	   	 	NOT NULL,
  DIR3_CODI            		character varying(9)  		NOT NULL,
  ENTITAT					BIGSERIAL(19),
  CONTRACTE_DATA_VIG      	DATE,
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_USUARI
(
  CODI          			character varying(64)		NOT NULL,
  NOM           			character varying(100),
  LLINATGES     			character varying(100),
  NOM_SENCER    			character varying(200),
  EMAIL         			character varying(200),
  REBRE_EMAILS 				BIGSERIAL(1,0),
  VERSION       			BIGSERIAL(19)  		NOT NULL
);

CREATE TABLE NOT_ENTITAT
(
  ID                   		BIGSERIAL(19)			NOT NULL,
  CODI                 		character varying(64) 		NOT NULL,
  NOM                  		character varying(256)		NOT NULL,
  TIPUS                		character varying(32) 		NOT NULL,
  DIR3_CODI            		character varying(9)  		NOT NULL,
  DESCRIPCIO           		character varying(1024),
  ACTIVA               		BIGSERIAL(1)			NOT NULL,
  VERSION              		BIGSERIAL(19)  		NOT NULL,
  API_KEY					character varying(64),
  AMB_ENTREGA_DEH 			BIGSERIAL(1) DEFAULT 0,
  LOGO_CAP 					BLOB,
  LOGO_PEU 					BLOB,
  COLOR_FONS 				character varying(32),
  COLOR_LLETRA 				character varying(32),
  TIPUS_DOC_DEFAULT 		BIGSERIAL(32),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_APLICACIO
(
  ID                   		BIGSERIAL(19)			NOT NULL,
  USUARI_CODI          		character varying(64)		NOT NULL,
  CALLBACK_URL         		character varying(256),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_NOTIFICACIO 
(
  ID                   		BIGSERIAL(19)			NOT NULL,
  DOCUMENT_ID				BIGSERIAL(19),
  USUARI_CODI          		character varying(64),
  PROCEDIMENT_ID			BIGSERIAL(19),
  EMISOR_DIR3CODI      		character varying(9) 		NOT NULL,
  COM_TIPUS            		BIGSERIAL(10)  		NOT NULL,
  ENV_TIPUS            		BIGSERIAL(10)  		NOT NULL,
  ENV_DATA_PROG				DATE,
  RETARD_POSTAL				BIGSERIAL(19),
  CONCEPTE             		character varying(50)		NOT NULL,
  DESCRIPCIO           		character varying(100),
  PAGADOR_POSTAL_ID			BIGSERIAL(19),
  PAGADOR_CIE_ID			BIGSERIAL(19),
  PAGCOR_DIR3          		character varying(9),
  PAGCOR_NUMCONT       		character varying(20),
  PAGCOR_CODI_CLIENT   		character varying(20),
  PAGCOR_DATA_VIG      		DATE,
  PAGCIE_DIR3          		character varying(9),
  PAGCIE_DATA_VIG      		DATE,
  PROC_CODI_NOTIB			character varying(6),
  PROC_DESC_SIA        		character varying(256),
  CADUCITAT            		DATE,
  SEU_EXP_SERDOC       		character varying(10),
  SEU_EXP_UNIORG       		character varying(10),
  SEU_EXP_IDENI        		character varying(52),
  SEU_EXP_TITOL        		character varying(256),
  SEU_REG_OFICINA      		character varying(256),
  SEU_REG_LLIBRE       		character varying(256),
  SEU_REG_ORGAN        		character varying(256),
  SEU_IDIOMA           		character varying(256),
  SEU_AVIS_TITOL       		character varying(256),
  SEU_AVIS_TEXT        		character varying(256),
  SEU_AVIS_MOBIL       		character varying(256),
  SEU_OFICI_TITOL      		character varying(256),
  SEU_OFICI_TEXT       		character varying(256),
  SEU_PROC_CODI        		character varying(256),
  ESTAT                		BIGSERIAL(10)			NOT NULL,
  MOTIU						character varying(255),
  NOT_ERROR_TIPUS      		BIGSERIAL(10),
  NOT_ENV_DATA         		timestamp without time zone,
  NOT_ENV_INTENT       		BIGSERIAL(10)			NOT NULL,
  NOT_REENV_DATA       		timestamp without time zone,
  NOT_ERROR_EVENT_ID   		BIGSERIAL(19),
  ENTITAT_ID           		BIGSERIAL(19)			NOT NULL,
  REGISTRE_ORGAN			character varying(10),
  REGISTRE_OFICINA			character varying(10),
  REGISTRE_LLIBRE			character varying(10),
  REGISTRE_NUMERO			BIGSERIAL(19),
  REGISTRE_NUMERO_FORMATAT	character varying(50),
  REGISTRE_DATA				DATE,
  REGISTRE_EXTRACTE			character varying(64),
  REGISTRE_DOC_FISICA		BIGSERIAL(2),
  REGISTRE_IDIOMA			character varying(10),
  REGISTRE_TIPUS_ASSUMPTE	character varying(255),
  REGISTRE_NUM_EXPEDIENT	character varying(20),
  REGISTRE_REF_EXTERNA		character varying(64),
  REGISTRE_CODI_ASSUMPTE	character varying(255),
  REGISTRE_OBSERVACIONS		character varying(256),
  GRUP_CODI					character varying(64),
  ORGAN_GESTOR 				character varying(19),
  CSV_UUID					character varying(64),
  ESTAT_DATE				timestamp without time zone,
  TIPUS_USUARI				BIGSERIAL(1),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_NOTIFICACIO_ENV
(
  ID                   		BIGSERIAL(19)			NOT NULL,
  TITULAR_ID				BIGSERIAL(19),
  DESTINATARI				BIGSERIAL(19),
  DOM_TIPUS            		BIGSERIAL(10),
  DOM_CON_TIPUS        		BIGSERIAL(10),
  DOM_VIA_TIPUS        		BIGSERIAL(10),
  DOM_VIA_NOM          		character varying(100),
  DOM_NUM_TIPUS        		BIGSERIAL(10),
  DOM_NUM_NUM          		character varying(10),
  DOM_NUM_QUALIF       		character varying(3),
  DOM_NUM_PUNTKM       		character varying(10),
  DOM_APARTAT          		character varying(10),
  DOM_BLOC             		character varying(50),
  DOM_PORTAL           		character varying(50),
  DOM_ESCALA           		character varying(50),
  DOM_PLANTA           		character varying(50),
  DOM_PORTA            		character varying(50),
  DOM_COMPLEM          		character varying(250),
  DOM_POBLACIO         		character varying(30),
  DOM_MUN_CODINE       		character varying(6),
  DOM_MUN_NOM          		character varying(64),
  DOM_CODI_POSTAL      		character varying(10),
  DOM_PRV_CODI         		character varying(2),
  DOM_PRV_NOM          		character varying(64),
  DOM_PAI_CODISO       		character varying(3),
  DOM_PAI_NOM          		character varying(64),
  DOM_LINEA1           		character varying(50),
  DOM_LINEA2           		character varying(50),
  DOM_CIE              		BIGSERIAL(10),
  DEH_OBLIGAT          		BIGSERIAL(1),
  DEH_NIF              		character varying(9),
  DEH_PROC_CODI        		character varying(6), 
  SERVEI_TIPUS         		BIGSERIAL(10),
  FORMAT_SOBRE         		character varying(10),
  FORMAT_FULLA         		character varying(10),
  NOTIFICA_REF         		character varying(20),
  NOTIFICA_ID          		character varying(20),
  NOTIFICA_DATCRE      		timestamp without time zone,
  NOTIFICA_DATDISP     		timestamp without time zone,
  NOTIFICA_DATCAD      		timestamp without time zone,
  NOTIFICA_EMI_DIR3CODI 	character varying(9),
  NOTIFICA_EMI_DIR3DESC 	character varying(50),
  NOTIFICA_EMI_DIR3NIF 		character varying(9),
  NOTIFICA_ARR_DIR3CODI	 	character varying(9),
  NOTIFICA_ARR_DIR3DESC 	character varying(50),
  NOTIFICA_ARR_DIR3NIF 		character varying(9),
  NOTIFICA_ESTAT       		BIGSERIAL(10),
  NOTIFICA_ESTAT_DATA  		timestamp without time zone,
  NOTIFICA_ESTAT_DATAACT 	timestamp without time zone,
  NOTIFICA_ESTAT_FINAL 		BIGSERIAL(1),
  NOTIFICA_ESTAT_DESC  		character varying(50),
  NOTIFICA_DATAT_ORIGEN 	character varying(20),
  NOTIFICA_DATAT_RECNIF 	character varying(9),
  NOTIFICA_DATAT_RECNOM 	character varying(100),
  NOTIFICA_DATAT_NUMSEG 	character varying(50),
  NOTIFICA_DATAT_ERRDES 	character varying(255),
  NOTIFICA_CER_DATA    		timestamp without time zone,
  NOTIFICA_CER_ARXIUID 		character varying(64),
  NOTIFICA_CER_HASH    		character varying(100),
  NOTIFICA_CER_ORIGEN  		character varying(50),
  NOTIFICA_CER_METAS   		character varying(255),
  NOTIFICA_CER_CSV     		character varying(50),
  NOTIFICA_CER_MIME    		character varying(20),
  NOTIFICA_CER_TAMANY  		BIGSERIAL(10),
  NOTIFICA_CER_TIPUS   		BIGSERIAL(10),
  NOTIFICA_CER_ARXTIP  		BIGSERIAL(10),
  NOTIFICA_CER_NUMSEG  		character varying(50),
  NOTIFICA_ERROR       		BIGSERIAL(1)			NOT NULL,
  NOTIFICA_ERROR_EVENT_ID  	BIGSERIAL(19),
  NOTIFICA_INTENT_DATA  	timestamp without time zone,
  SEU_REG_NUMERO       		character varying(50),
  SEU_REG_DATA         		timestamp without time zone,
  SEU_DATA_FI          		timestamp without time zone,
  SEU_ESTAT            		BIGSERIAL(10),
  SEU_ERROR            		BIGSERIAL(1),
  SEU_ERROR_EVENT_ID   		BIGSERIAL(19),
  SEU_DATA_ESTAT       		timestamp without time zone,
  SEU_DATA_NOTINF      		timestamp without time zone,
  SEU_DATA_NOTIDP      		timestamp without time zone,
  SEU_INTENT_DATA      		timestamp without time zone,
  SEU_FITXER_CODI      		BIGSERIAL(19),
  SEU_FITXER_CLAU      		character varying(20),
  REGISTRE_NUMERO_FORMATAT	character varying(50),
  REGISTRE_DATA				DATE,
  ESTAT_REGISTRE			BIGSERIAL(19),
  INTENT_NUM	   			BIGSERIAL(10, 0),
  NOTIFICACIO_ID       		BIGSERIAL(19)			NOT NULL,
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_NOTIFICACIO_EVENT
(
  ID                   		BIGSERIAL(19)			NOT NULL,
  TIPUS                		BIGSERIAL(10)			NOT NULL,
  DATA                 		timestamp without time zone		NOT NULL,
  DESCRIPCIO           		character varying(256),
  ERROR                		BIGSERIAL(1)			NOT NULL,
  ERROR_DESC           		character varying(2051),
  NOTIFICACIO_ID       		BIGSERIAL(19)			NOT NULL,
  NOTIFICACIO_ENV_ID   		BIGSERIAL(19),
  CALLBACK_ESTAT       		character varying(10),
  CALLBACK_DATA        		timestamp without time zone,
  CALLBACK_INTENTS     		BIGSERIAL(10),
  CALLBACK_ERROR_DESC  		character varying(2048),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);


CREATE TABLE NOT_ACL_CLASS
(
  ID     					BIGSERIAL(19)			NOT NULL,
  CLASS  					character varying(100)		NOT NULL
);


CREATE TABLE NOT_ACL_SID
(
  ID         				BIGSERIAL(19)			NOT NULL,
  PRINCIPAL  				BIGSERIAL(1)			NOT NULL,
  SID        				character varying(100)		NOT NULL
);


CREATE TABLE NOT_ACL_ENTRY
(
  ID                   		BIGSERIAL(19)			NOT NULL,
  ACL_OBJECT_IDENTITY  		BIGSERIAL(19)			NOT NULL,
  ACE_ORDER            		BIGSERIAL(19)			NOT NULL,
  SID                  		BIGSERIAL(19)			NOT NULL,
  MASK                 		BIGSERIAL(19)			NOT NULL,
  GRANTING             		BIGSERIAL(1)			NOT NULL,
  AUDIT_SUCCESS        		BIGSERIAL(1)			NOT NULL,
  AUDIT_FAILURE        		BIGSERIAL(1)			NOT NULL
);


CREATE TABLE NOT_ACL_OBJECT_IDENTITY
(
  ID                  		BIGSERIAL(19)			NOT NULL,
  OBJECT_ID_CLASS     		BIGSERIAL(19)			NOT NULL,
  OBJECT_ID_IDENTITY  		BIGSERIAL(19)			NOT NULL,
  PARENT_OBJECT       		BIGSERIAL(19),
  OWNER_SID           		BIGSERIAL(19)			NOT NULL,
  ENTRIES_INHERITING  		BIGSERIAL(1)			NOT NULL
);

CREATE TABLE NOT_PERSONA
(
  ID					BIGSERIAL(19)			NOT NULL,
  INTERESSATTIPUS		character varying(30),
  EMAIL					character varying(100),
  LLINATGE1				character varying(100),
  LLINATGE2				character varying(100),
  NIF					character varying(9)			NOT NULL,
  NOM					character varying(100),
  TELEFON				character varying(16),
  RAO_SOCIAL			character varying(100),
  COD_ENTITAT_DESTI		character varying(9),
  NOTIFICACIO_ENV_ID	BIGSERIAL(19),
  CREATEDBY_CODI       	character varying(64),
  CREATEDDATE          	timestamp without time zone,
  LASTMODIFIEDBY_CODI  	character varying(64),
  LASTMODIFIEDDATE     	timestamp without time zone
);

CREATE TABLE NOT_DOCUMENT
(
  ID					BIGSERIAL(19)			NOT NULL,
  ARXIU_ID				character varying(64),
  ARXIU_GEST_DOC_ID		character varying(64),
  ARXIU_NOM				character varying(100),
  CONTINGUT_BASE_64		CLOB,
  HASH					character varying(2048),
  URL					character varying(256),
  TELEFON				character varying(16),
  METADADES				character varying(3000),
  NORMALITZAT			BOOLEAN,
  GENERAR_CSV			BOOLEAN,
  UUID					character varying(64),
  CSV					character varying(256),
  CREATEDBY_CODI       	character varying(64),
  CREATEDDATE          	timestamp without time zone,
  LASTMODIFIEDBY_CODI  	character varying(64),
  LASTMODIFIEDDATE     	timestamp without time zone
);

CREATE TABLE NOT_ENTITAT_TIPUS_DOC
(
  ID            			BIGSERIAL(19),
  ENTITAT_ID				BIGSERIAL(19),
  TIPUS_DOC					BIGSERIAL(32),
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
)

