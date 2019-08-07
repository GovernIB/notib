CREATE TABLE NOT_PROCEDIMENT 
(
  ID						NUMBER(19)	    	NOT NULL,
  CODI          			VARCHAR2(64)		NOT NULL,
  NOM						VARCHAR2(100),
  CODISIA					VARCHAR2(64),
  ENTITAT					NUMBER(19),
  PAGADORPOSTAL				NUMBER(19),
  PAGADORCIE				NUMBER(19),
  AGRUPAR					NUMBER(1) 			NOT NULL,
  RETARD 					NUMBER(19),
  LLIBRE					VARCHAR2(64),
  OFICINA					VARCHAR2(64),
  TIPUSASSUMPTE				VARCHAR2(255),
  CODIASSUMPTE				VARCHAR2(255),
  DATA_PROGRAMADA 			TIMESTAMP(6),
  ORGAN_GESTOR 				VARCHAR2(19),
  ORGAN_GESTOR_NOM			VARCHAR2(64),
  TIPUSASSUMPTE_NOM 		VARCHAR2(64),
  CODIASSUMPTE_NOM 			VARCHAR2(64),
  CADUCITAT					NUMBER(19),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_COLUMNES
(
  ID                   		NUMBER(19),
  CREATED_DATE      		NUMBER(1),
  DATA_PROGRAMADA           NUMBER(1),
  NOT_IDENTIFICADOR         NUMBER(1),
  PRO_CODI             		NUMBER(1),
  GRUP_CODI           		NUMBER(1),
  DIR3_CODI					NUMBER(1),
  USUARI					NUMBER(1),
  ENVIAMENT_TIPUS          	NUMBER(1),
  CONCEPTE       			NUMBER(1),
  DESCRIPCIO   				NUMBER(1),
  TITULAR_NIF      			NUMBER(1),
  TITULAR_NOM_LLINATGE      NUMBER(1),
  TITULAR_EMAIL      		NUMBER(1),
  DESTINATARIS        	    NUMBER(1),
  LLIBRE_REGISTRE        	NUMBER(1),
  NUMERO_REGISTRE           NUMBER(1),
  DATA_REGISTRE        		NUMBER(1),
  DATA_CADUCITAT         	NUMBER(1),
  CODI_NOTIB_ENV            NUMBER(1),
  NUM_CERTIFICACIO      	NUMBER(1),
  CSV_UUID          		NUMBER(1),
  ESTAT                		NUMBER(1),
  ENTITAT_ID				NUMBER(19),
  USUARI_CODI				VARCHAR2(64),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_PRO_GRUP 
(
  ID						NUMBER(19)	    	NOT NULL,
  GRUP						NUMBER(19),			
  PROCEDIMENT				NUMBER(19),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_GRUP 
(
  ID						NUMBER(19)	    	NOT NULL,
  CODI						VARCHAR2(64)		NOT NULL,
  NOM						VARCHAR2(100),
  ENTITAT					NUMBER(19),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_PAGADOR_POSTAL
(
  ID						NUMBER(19)	    	NOT NULL,
  DIR3_CODI            		VARCHAR2(9)  		NOT NULL,
  ENTITAT					NUMBER(19),
  CONTRACTE_NUM		       	VARCHAR2(20),
  CONTRACTE_DATA_VIG      	DATE,
  FACTURACIO_CODI_CLIENT   	VARCHAR2(20),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_PAGADOR_CIE
(
  ID						NUMBER(19)	   	 	NOT NULL,
  DIR3_CODI            		VARCHAR2(9)  		NOT NULL,
  ENTITAT					NUMBER(19),
  CONTRACTE_DATA_VIG      	DATE,
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_USUARI
(
  CODI          			VARCHAR2(64)		NOT NULL,
  NOM           			VARCHAR2(100),
  LLINATGES     			VARCHAR2(100),
  NOM_SENCER    			VARCHAR2(200),
  EMAIL         			VARCHAR2(200),
  REBRE_EMAILS 				NUMBER(1,0),
  VERSION       			NUMBER(19)  		NOT NULL
);

CREATE TABLE NOT_ENTITAT
(
  ID                   		NUMBER(19)			NOT NULL,
  CODI                 		VARCHAR2(64) 		NOT NULL,
  NOM                  		VARCHAR2(256)		NOT NULL,
  TIPUS                		VARCHAR2(32) 		NOT NULL,
  DIR3_CODI            		VARCHAR2(9)  		NOT NULL,
  DESCRIPCIO           		VARCHAR2(1024),
  ACTIVA               		NUMBER(1)			NOT NULL,
  VERSION              		NUMBER(19)  		NOT NULL,
  API_KEY					VARCHAR2(64),
  AMB_ENTREGA_DEH 			NUMBER(1) DEFAULT 0,
  LOGO_CAP 					BLOB,
  LOGO_PEU 					BLOB,
  COLOR_FONS 				VARCHAR2(32),
  COLOR_LLETRA 				VARCHAR2(32),
  TIPUS_DOC_DEFAULT 		NUMBER(32),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_APLICACIO
(
  ID                   		NUMBER(19)			NOT NULL,
  USUARI_CODI          		VARCHAR2(64)		NOT NULL,
  CALLBACK_URL         		VARCHAR2(256),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_NOTIFICACIO 
(
  ID                   		NUMBER(19)			NOT NULL,
  DOCUMENT_ID				NUMBER(19),
  USUARI_CODI          		VARCHAR2(64),
  PROCEDIMENT_ID			NUMBER(19),
  EMISOR_DIR3CODI      		VARCHAR2(9) 		NOT NULL,
  COM_TIPUS            		NUMBER(10)  		NOT NULL,
  ENV_TIPUS            		NUMBER(10)  		NOT NULL,
  ENV_DATA_PROG				DATE,
  RETARD_POSTAL				NUMBER(19),
  CONCEPTE             		VARCHAR2(50)		NOT NULL,
  DESCRIPCIO           		VARCHAR2(100),
  PAGADOR_POSTAL_ID			NUMBER(19),
  PAGADOR_CIE_ID			NUMBER(19),
  PAGCOR_DIR3          		VARCHAR2(9),
  PAGCOR_NUMCONT       		VARCHAR2(20),
  PAGCOR_CODI_CLIENT   		VARCHAR2(20),
  PAGCOR_DATA_VIG      		DATE,
  PAGCIE_DIR3          		VARCHAR2(9),
  PAGCIE_DATA_VIG      		DATE,
  PROC_CODI_NOTIB			VARCHAR2(6),
  PROC_DESC_SIA        		VARCHAR2(256),
  CADUCITAT            		DATE,
  ESTAT                		NUMBER(10)			NOT NULL,
  MOTIU						VARCHAR2(255),
  NOT_ERROR_TIPUS      		NUMBER(10),
  NOT_ENV_DATA         		TIMESTAMP(6),
  NOT_ENV_INTENT       		NUMBER(10)			NOT NULL,
  NOT_REENV_DATA       		TIMESTAMP(6),
  NOT_ERROR_EVENT_ID   		NUMBER(19),
  ENTITAT_ID           		NUMBER(19)			NOT NULL,
  REGISTRE_NUMERO			NUMBER(19),
  REGISTRE_NUMERO_FORMATAT	VARCHAR2(50),
  REGISTRE_DATA				DATE,
  REGISTRE_NUM_EXPEDIENT	VARCHAR2(20),
  GRUP_CODI					VARCHAR2(64),
  ORGAN_GESTOR 				VARCHAR2(19),
  CSV_UUID					VARCHAR2(64),
  ESTAT_DATE				TIMESTAMP(6),
  TIPUS_USUARI				NUMBER(1),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_NOTIFICACIO_ENV
(
  ID                   		NUMBER(19)			NOT NULL,
  TITULAR_ID				NUMBER(19),
  DESTINATARI				NUMBER(19),
  DOM_TIPUS            		NUMBER(10),
  DOM_CON_TIPUS        		NUMBER(10),
  DOM_VIA_TIPUS        		NUMBER(10),
  DOM_VIA_NOM          		VARCHAR2(100),
  DOM_NUM_TIPUS        		NUMBER(10),
  DOM_NUM_NUM          		VARCHAR2(10),
  DOM_NUM_QUALIF       		VARCHAR2(3),
  DOM_NUM_PUNTKM       		VARCHAR2(10),
  DOM_APARTAT          		VARCHAR2(10),
  DOM_BLOC             		VARCHAR2(50),
  DOM_PORTAL           		VARCHAR2(50),
  DOM_ESCALA           		VARCHAR2(50),
  DOM_PLANTA           		VARCHAR2(50),
  DOM_PORTA            		VARCHAR2(50),
  DOM_COMPLEM          		VARCHAR2(250),
  DOM_POBLACIO         		VARCHAR2(30),
  DOM_MUN_CODINE       		VARCHAR2(6),
  DOM_MUN_NOM          		VARCHAR2(64),
  DOM_CODI_POSTAL      		VARCHAR2(10),
  DOM_PRV_CODI         		VARCHAR2(2),
  DOM_PRV_NOM          		VARCHAR2(64),
  DOM_PAI_CODISO       		VARCHAR2(3),
  DOM_PAI_NOM          		VARCHAR2(64),
  DOM_LINEA1           		VARCHAR2(50),
  DOM_LINEA2           		VARCHAR2(50),
  DOM_CIE              		NUMBER(10),
  DEH_OBLIGAT          		NUMBER(1),
  DEH_NIF              		VARCHAR2(9),
  DEH_PROC_CODI        		VARCHAR2(6), 
  SERVEI_TIPUS         		NUMBER(10),
  FORMAT_SOBRE         		VARCHAR2(10),
  FORMAT_FULLA         		VARCHAR2(10),
  NOTIFICA_REF         		VARCHAR2(20),
  NOTIFICA_ID          		VARCHAR2(20),
  NOTIFICA_DATCRE      		TIMESTAMP(6),
  NOTIFICA_DATDISP     		TIMESTAMP(6),
  NOTIFICA_DATCAD      		TIMESTAMP(6),
  NOTIFICA_EMI_DIR3CODI 	VARCHAR2(9),
  NOTIFICA_EMI_DIR3DESC 	VARCHAR2(50),
  NOTIFICA_EMI_DIR3NIF 		VARCHAR2(9),
  NOTIFICA_ARR_DIR3CODI	 	VARCHAR2(9),
  NOTIFICA_ARR_DIR3DESC 	VARCHAR2(50),
  NOTIFICA_ARR_DIR3NIF 		VARCHAR2(9),
  NOTIFICA_ESTAT       		NUMBER(10),
  NOTIFICA_ESTAT_DATA  		TIMESTAMP(6),
  NOTIFICA_ESTAT_DATAACT 	TIMESTAMP(6),
  NOTIFICA_ESTAT_FINAL 		NUMBER(1),
  NOTIFICA_ESTAT_DESC  		VARCHAR2(50),
  NOTIFICA_DATAT_ORIGEN 	VARCHAR2(20),
  NOTIFICA_DATAT_RECNIF 	VARCHAR2(9),
  NOTIFICA_DATAT_RECNOM 	VARCHAR2(100),
  NOTIFICA_DATAT_NUMSEG 	VARCHAR2(50),
  NOTIFICA_DATAT_ERRDES 	VARCHAR2(255),
  NOTIFICA_CER_DATA    		TIMESTAMP(6),
  NOTIFICA_CER_ARXIUID 		VARCHAR2(64),
  NOTIFICA_CER_HASH    		VARCHAR2(100),
  NOTIFICA_CER_ORIGEN  		VARCHAR2(50),
  NOTIFICA_CER_METAS   		VARCHAR2(255),
  NOTIFICA_CER_CSV     		VARCHAR2(50),
  NOTIFICA_CER_MIME    		VARCHAR2(20),
  NOTIFICA_CER_TAMANY  		NUMBER(10),
  NOTIFICA_CER_TIPUS   		NUMBER(10),
  NOTIFICA_CER_ARXTIP  		NUMBER(10),
  NOTIFICA_CER_NUMSEG  		VARCHAR2(50),
  NOTIFICA_ERROR       		NUMBER(1)			NOT NULL,
  NOTIFICA_ERROR_EVENT_ID  	NUMBER(19),
  NOTIFICA_INTENT_DATA  	TIMESTAMP(6),
  REGISTRE_NUMERO_FORMATAT	VARCHAR2(50),
  REGISTRE_DATA				DATE,
  ESTAT_REGISTRE			NUMBER(19),
  INTENT_NUM	   			NUMBER(10, 0),
  NOTIFICACIO_ID       		NUMBER(19)			NOT NULL,
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_NOTIFICACIO_EVENT
(
  ID                   		NUMBER(19)			NOT NULL,
  TIPUS                		NUMBER(10)			NOT NULL,
  DATA                 		TIMESTAMP(6)		NOT NULL,
  DESCRIPCIO           		VARCHAR2(256),
  ERROR                		NUMBER(1)			NOT NULL,
  ERROR_DESC           		VARCHAR2(2051),
  NOTIFICACIO_ID       		NUMBER(19)			NOT NULL,
  NOTIFICACIO_ENV_ID   		NUMBER(19),
  CALLBACK_ESTAT       		VARCHAR2(10),
  CALLBACK_DATA        		TIMESTAMP(6),
  CALLBACK_INTENTS     		NUMBER(10),
  CALLBACK_ERROR_DESC  		VARCHAR2(2048),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);


CREATE TABLE NOT_ACL_CLASS
(
  ID     					NUMBER(19)			NOT NULL,
  CLASS  					VARCHAR2(100)		NOT NULL
);


CREATE TABLE NOT_ACL_SID
(
  ID         				NUMBER(19)			NOT NULL,
  PRINCIPAL  				NUMBER(1)			NOT NULL,
  SID        				VARCHAR2(100)		NOT NULL
);


CREATE TABLE NOT_ACL_ENTRY
(
  ID                   		NUMBER(19)			NOT NULL,
  ACL_OBJECT_IDENTITY  		NUMBER(19)			NOT NULL,
  ACE_ORDER            		NUMBER(19)			NOT NULL,
  SID                  		NUMBER(19)			NOT NULL,
  MASK                 		NUMBER(19)			NOT NULL,
  GRANTING             		NUMBER(1)			NOT NULL,
  AUDIT_SUCCESS        		NUMBER(1)			NOT NULL,
  AUDIT_FAILURE        		NUMBER(1)			NOT NULL
);


CREATE TABLE NOT_ACL_OBJECT_IDENTITY
(
  ID                  		NUMBER(19)			NOT NULL,
  OBJECT_ID_CLASS     		NUMBER(19)			NOT NULL,
  OBJECT_ID_IDENTITY  		NUMBER(19)			NOT NULL,
  PARENT_OBJECT       		NUMBER(19),
  OWNER_SID           		NUMBER(19)			NOT NULL,
  ENTRIES_INHERITING  		NUMBER(1)			NOT NULL
);

CREATE TABLE NOT_PERSONA
(
  ID					NUMBER(19)			NOT NULL,
  INTERESSATTIPUS		VARCHAR2(30),
  EMAIL					VARCHAR2(100),
  LLINATGE1				VARCHAR2(100),
  LLINATGE2				VARCHAR2(100),
  NIF					VARCHAR2(9)			NOT NULL,
  NOM					VARCHAR2(100),
  TELEFON				VARCHAR2(16),
  RAO_SOCIAL			VARCHAR2(100),
  COD_ENTITAT_DESTI		VARCHAR2(9),
  NOTIFICACIO_ENV_ID	NUMBER(19),
  INCAPACITAT			NUMBER(1,0),
  CREATEDBY_CODI       	VARCHAR2(64),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64),
  LASTMODIFIEDDATE     	TIMESTAMP(6)
);

CREATE TABLE NOT_DOCUMENT
(
  ID					NUMBER(19)			NOT NULL,
  ARXIU_ID				VARCHAR2(64),
  ARXIU_GEST_DOC_ID		VARCHAR2(64),
  ARXIU_NOM				VARCHAR2(100),
  CONTINGUT_BASE_64		CLOB,
  HASH					VARCHAR2(2048),
  URL					VARCHAR2(256),
  TELEFON				VARCHAR2(16),
  METADADES				VARCHAR2(3000),
  NORMALITZAT			NUMBER(1, 0),
  GENERAR_CSV			NUMBER(1, 0),
  UUID					VARCHAR2(64),
  CSV					VARCHAR2(256),
  CREATEDBY_CODI       	VARCHAR2(64),
  CREATEDDATE          	TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  	VARCHAR2(64),
  LASTMODIFIEDDATE     	TIMESTAMP(6)
);

CREATE TABLE NOT_ENTITAT_TIPUS_DOC
(
  ID            			NUMBER(19),
  ENTITAT_ID				NUMBER(19),
  TIPUS_DOC					NUMBER(32),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_FORMATS_FULLA
(
  ID            			NUMBER(19),
  PAGADOR_CIE_ID			NUMBER(19),
  CODI						VARCHAR2(64),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

CREATE TABLE NOT_FORMATS_SOBRE
(
  ID            			NUMBER(19),
  PAGADOR_CIE_ID			NUMBER(19),
  CODI						VARCHAR2(64),
  CREATEDBY_CODI       		VARCHAR2(64),
  CREATEDDATE          		TIMESTAMP(6),
  LASTMODIFIEDBY_CODI  		VARCHAR2(64),
  LASTMODIFIEDDATE     		TIMESTAMP(6)
);

