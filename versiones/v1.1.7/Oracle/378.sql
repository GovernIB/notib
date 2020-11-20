-- #378

-- Taules d'auditoria
CREATE TABLE "NOT_APLICACIO_AUDIT" 
(	
	"ID" NUMBER(19,0) NOT NULL, 
   	"TIPUSOPERACIO" VARCHAR2 (20 CHAR) NOT NULL,
   	"JOINPOINT" VARCHAR2 (256 CHAR),
	"CREATEDBY_CODI" VARCHAR2(64 CHAR), 
	"CREATEDDATE" TIMESTAMP (6), 
	"LASTMODIFIEDBY_CODI" VARCHAR2(64 CHAR), 
	"LASTMODIFIEDDATE" TIMESTAMP (6),
	"APLICACIO_ID" NUMBER(19,0) NOT NULL,
	"ENTITAT_ID" NUMBER(19,0) NOT NULL,
	"USUARI_CODI" VARCHAR2(64 CHAR), 
	"CALLBACK_URL" VARCHAR2(256 CHAR) ,
	"ACTIVA" NUMBER(1,0)
);
   
CREATE TABLE "NOT_ENTITAT_AUDIT" 
(	
	"ID" NUMBER(19,0) NOT NULL, 
	"TIPUSOPERACIO" VARCHAR2 (20 CHAR) NOT NULL,
   	"JOINPOINT" VARCHAR2 (256 CHAR),
	"CREATEDBY_CODI" VARCHAR2(64 CHAR), 
	"CREATEDDATE" TIMESTAMP (6), 
	"LASTMODIFIEDBY_CODI" VARCHAR2(64 CHAR), 
	"LASTMODIFIEDDATE" TIMESTAMP (6),
	"ENTITAT_ID" NUMBER(19,0) NOT NULL,
	"CODI" VARCHAR2(64 CHAR) NOT NULL, 
	"NOM" VARCHAR2(256 CHAR) NOT NULL, 
	"TIPUS" VARCHAR2(32 CHAR), 
	"DIR3_CODI" VARCHAR2(9 CHAR) NOT NULL, 
	"DIR3_CODI_REG" VARCHAR2(9 CHAR), 
	"API_KEY" VARCHAR2(64 CHAR), 
	"AMB_ENTREGA_DEH" NUMBER(1,0), 
	"AMB_ENTREGA_CIE" NUMBER(1,0), 
	"DESCRIPCIO" VARCHAR2(1024 CHAR), 
	"ACTIVA" NUMBER(1,0), 
	"OFICINA" VARCHAR2(255 CHAR), 
	"LLIBRE_ENTITAT" NUMBER(1,0),
	"LLIBRE" VARCHAR2(255 CHAR),
	"LLIBRE_NOM" VARCHAR2(255 CHAR) 
);
   
CREATE TABLE "NOT_GRUP_AUDIT" 
(	
	"ID" NUMBER(19,0) NOT NULL, 
	"TIPUSOPERACIO" VARCHAR2 (20 CHAR) NOT NULL,
   	"JOINPOINT" VARCHAR2 (256 CHAR),
	"CREATEDBY_CODI" VARCHAR2(64 CHAR), 
	"CREATEDDATE" TIMESTAMP (6), 
	"LASTMODIFIEDBY_CODI" VARCHAR2(64 CHAR), 
	"LASTMODIFIEDDATE" TIMESTAMP (6),
	"GRUP_ID" NUMBER(19,0) NOT NULL, 
	"CODI" VARCHAR2(64 CHAR) NOT NULL, 
	"NOM" VARCHAR2(100 CHAR), 
	"ENTITAT_ID" NUMBER(19,0),
	"ORGAN" VARCHAR2(64 CHAR)
);
   
CREATE TABLE "NOT_PRO_GRUP_AUDIT" 
(	
	"ID" NUMBER(19,0) NOT NULL, 
	"TIPUSOPERACIO" VARCHAR2 (20 CHAR) NOT NULL,
   	"JOINPOINT" VARCHAR2 (256 CHAR),
	"CREATEDBY_CODI" VARCHAR2(64 CHAR), 
	"CREATEDDATE" TIMESTAMP (6), 
	"LASTMODIFIEDBY_CODI" VARCHAR2(64 CHAR), 
	"LASTMODIFIEDDATE" TIMESTAMP (6),
	"PROGRUP_ID" NUMBER(19,0) NOT NULL,  
	"PROCEDIMENT" VARCHAR2(64 CHAR) NOT NULL,
	"GRUP" VARCHAR2(64 CHAR) NOT NULL
);
   
CREATE TABLE "NOT_PROCEDIMENT_AUDIT" 
(	
	"ID" NUMBER(19,0) NOT NULL, 
	"TIPUSOPERACIO" VARCHAR2 (20 CHAR) NOT NULL,
   	"JOINPOINT" VARCHAR2 (256 CHAR),
	"CREATEDBY_CODI" VARCHAR2(64 CHAR), 
	"CREATEDDATE" TIMESTAMP (6), 
	"LASTMODIFIEDBY_CODI" VARCHAR2(64 CHAR), 
	"LASTMODIFIEDDATE" TIMESTAMP (6),
	"PROCEDIMENT_ID" NUMBER(19,0) NOT NULL,
	"ENTITAT_ID" NUMBER(19,0), 
	"ORGAN" VARCHAR2(64 CHAR), 
	"CODI" VARCHAR2(64 CHAR) NOT NULL, 
	"NOM" VARCHAR2(100 CHAR), 
	"RETARD" NUMBER(10,0), 
	"CADUCITAT" NUMBER(10,0), 
	"AGRUPAR" NUMBER(1,0),
	"COMU" NUMBER(1,0),
	"PAGADORPOSTAL_ID" NUMBER(19,0),
	"PAGADORCIE_ID" NUMBER(19,0)
);
   
CREATE TABLE "NOT_NOTIFICACIO_AUDIT" 
(	
	"ID" NUMBER(19,0) NOT NULL, 
	"TIPUSOPERACIO" VARCHAR2 (20 CHAR) NOT NULL,
   	"JOINPOINT" VARCHAR2 (256 CHAR),
	"CREATEDBY_CODI" VARCHAR2(64 CHAR), 
	"CREATEDDATE" TIMESTAMP (6), 
	"LASTMODIFIEDBY_CODI" VARCHAR2(64 CHAR), 
	"LASTMODIFIEDDATE" TIMESTAMP (6),
	"NOTIFICACIO_ID" NUMBER(19,0) NOT NULL, 
	"SINCRON" VARCHAR2(20 CHAR), 
	"TIPUS_USUARI" VARCHAR2(20 CHAR),
	"USUARI" VARCHAR2(64 CHAR), 
	"EMISOR" VARCHAR2(9 CHAR) NOT NULL, 
	"TIPUS" VARCHAR2(20 CHAR) NOT NULL, 
	"ENTITAT_ID" NUMBER(19,0), 
	"ORGAN" VARCHAR2(64 CHAR),
	"PROCEDIMENT" VARCHAR2(64 CHAR), 
	"GRUP" VARCHAR2(64 CHAR), 
	"CONCEPTE" VARCHAR2(255 CHAR), 
	"DESCRIPCIO" VARCHAR2(1000 CHAR),
	"NUM_EXPEDIENT" VARCHAR2(80 CHAR), 
	"ENV_DATA_PROG" DATE, 
	"RETARD_POSTAL" NUMBER(10,0), 
	"CADUCITAT" DATE, 
	"DOCUMENT_ID" NUMBER(19,0) NOT NULL,
	"ESTAT" VARCHAR2(20 CHAR) NOT NULL, 
	"ESTAT_DATE" TIMESTAMP (6), 
	"MOTIU" VARCHAR2(255 CHAR), 
	"PAGADOR_POSTAL_ID" NUMBER(19,0), 
	"PAGADOR_CIE_ID" NUMBER(19,0), 
	"REGISTRE_ENV_INTENT" NUMBER(10,0), 
	"REGISTRE_NUMERO" NUMBER(10,0), 
	"REGISTRE_NUMERO_FORMATAT" VARCHAR2(200 CHAR), 
	"REGISTRE_DATA" DATE, 
	"NOT_ENV_DATA" TIMESTAMP (6), 
	"NOT_ENV_INTENT" NUMBER(10,0), 
	"NOT_ERROR_TIPUS" VARCHAR2(30 CHAR), 
	"CALLBACK_ERROR" NUMBER(1,0), 
	"EVENT_ERROR" NUMBER(19,0)
);
   
CREATE TABLE "NOT_NOTIFICACIO_ENV_AUDIT" 
(	
	"ID" NUMBER(19,0) NOT NULL, 
	"TIPUSOPERACIO" VARCHAR2 (20 CHAR) NOT NULL,
   	"JOINPOINT" VARCHAR2 (256 CHAR),
	"CREATEDBY_CODI" VARCHAR2(64 CHAR), 
	"CREATEDDATE" TIMESTAMP (6), 
	"LASTMODIFIEDBY_CODI" VARCHAR2(64 CHAR), 
	"LASTMODIFIEDDATE" TIMESTAMP (6),
	"ENVIAMENT_ID" NUMBER(19,0) NOT NULL, 
	"NOTIFICACIO_ID" NUMBER(19,0) NOT NULL,
	"TITULAR_ID" NUMBER(19,0),
	"DESTINATARIS_ID" VARCHAR2(200 CHAR),
	"DOMICILI_TIPUS" VARCHAR2(20 CHAR), 
	"DOMICILI" VARCHAR2(500 CHAR), 
	"SERVEI_TIPUS" VARCHAR2(20 CHAR), 
	"CIE" NUMBER(10,0), 
	"FORMAT_SOBRE" VARCHAR2(10 CHAR), 
	"FORMAT_FULLA" VARCHAR2(10 CHAR), 
	"DEH_OBLIGAT" NUMBER(1,0), 
	"DEH_NIF" VARCHAR2(9 CHAR), 
	"NOTIFICA_REF" VARCHAR2(20 CHAR), 
	"NOTIFICA_ID" VARCHAR2(20 CHAR), 
	"NOTIFICA_DATCRE" TIMESTAMP (6), 
	"NOTIFICA_DATDISP" TIMESTAMP (6), 
	"NOTIFICA_DATCAD" TIMESTAMP (6), 
	"NOTIFICA_EMI_DIR3CODI" VARCHAR2(9 CHAR), 
	"NOTIFICA_ARR_DIR3CODI" VARCHAR2(9 CHAR), 
	"NOTIFICA_ESTAT" NUMBER(10,0), 
	"NOTIFICA_ESTAT_DATA" TIMESTAMP (6), 
	"NOTIFICA_ESTAT_FINAL" NUMBER(1,0), 
	"NOTIFICA_DATAT_ORIGEN" VARCHAR2(20 CHAR), 
	"NOTIFICA_DATAT_RECNIF" VARCHAR2(9 CHAR), 
	"NOTIFICA_DATAT_NUMSEG" VARCHAR2(50 CHAR), 
	"NOTIFICA_CER_DATA" TIMESTAMP (6), 
	"NOTIFICA_CER_ARXIUID" VARCHAR2(50 CHAR), 
	"NOTIFICA_CER_ORIGEN" VARCHAR2(20 CHAR), 
	"NOTIFICA_CER_TIPUS" VARCHAR2(20 CHAR), 
	"NOTIFICA_CER_ARXTIP" VARCHAR2(20 CHAR), 
	"NOTIFICA_CER_NUMSEG" VARCHAR2(50 CHAR), 
	"REGISTRE_NUMERO_FORMATAT" VARCHAR2(50 CHAR), 
	"REGISTRE_DATA" TIMESTAMP (6), 
	"REGISTRE_ESTAT" VARCHAR2(20 CHAR),
	"REGISTRE_ESTAT_FINAL" NUMBER(1,0), 
	"SIR_CON_DATA" TIMESTAMP (6), 
	"SIR_REC_DATA" TIMESTAMP (6), 
	"SIR_REG_DESTI_DATA" TIMESTAMP (6), 
	"NOTIFICA_ERROR_EVENT_ID" NUMBER(19,0), 
	"NOTIFICA_ERROR" NUMBER(1,0), 
	"NOTIFICA_DATAT_ERRDES" VARCHAR2(255 CHAR) 
);

-- Primary keys
ALTER TABLE NOT_APLICACIO_AUDIT ADD (CONSTRAINT NOT_APLICACIO_AUDIT_PK PRIMARY KEY (ID));
ALTER TABLE NOT_ENTITAT_AUDIT ADD (CONSTRAINT NOT_ENTITAT_AUDIT_PK PRIMARY KEY (ID));
ALTER TABLE NOT_PROCEDIMENT_AUDIT ADD (CONSTRAINT NOT_PROCEDIMENT_AUDIT_PK PRIMARY KEY (ID));
ALTER TABLE NOT_GRUP_AUDIT ADD (CONSTRAINT NOT_GRUP_AUDIT_PK PRIMARY KEY (ID));
ALTER TABLE NOT_PRO_GRUP_AUDIT ADD (CONSTRAINT NOT_PRO_GRUP_AUDIT_PK PRIMARY KEY (ID));
ALTER TABLE NOT_NOTIFICACIO_AUDIT ADD (CONSTRAINT NOT_NOTIFICACIO_AUDIT_PK PRIMARY KEY (ID));
ALTER TABLE NOT_NOTIFICACIO_ENV_AUDIT ADD (CONSTRAINT NOT_NOTIFICACIO_ENV_AUDIT_PK PRIMARY KEY (ID));


-- Indexos
CREATE INDEX "NOT_APLICACIO_AUDIT_EK" ON "NOT_APLICACIO_AUDIT" ("APLICACIO_ID");
CREATE INDEX "NOT_ENTITAT_AUDIT_EK" ON "NOT_ENTITAT_AUDIT" ("ENTITAT_ID");
CREATE INDEX "NOT_PROCEDIMENT_AUDIT_EK" ON "NOT_PROCEDIMENT_AUDIT" ("PROCEDIMENT_ID");
CREATE INDEX "NOT_GRUP_AUDIT_EK" ON "NOT_GRUP_AUDIT" ("GRUP_ID");
CREATE INDEX "NOT_PRO_GRUP_AUDIT_EK" ON "NOT_PRO_GRUP_AUDIT" ("PROGRUP_ID") ;
CREATE INDEX "NOT_NOTIFICACIO_AUDIT_EK" ON "NOT_NOTIFICACIO_AUDIT" ("NOTIFICACIO_ID");
CREATE INDEX "NOT_NOTIFICACIO_ENV_AUDIT_EK" ON "NOT_NOTIFICACIO_ENV_AUDIT" ("ENVIAMENT_ID");

-- Grants
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_APLICACIO_AUDIT TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_ENTITAT_AUDIT TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_PROCEDIMENT_AUDIT TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_GRUP_AUDIT TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_PRO_GRUP_AUDIT TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_NOTIFICACIO_AUDIT TO WWW_NOTIB;
GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_NOTIFICACIO_ENV_AUDIT TO WWW_NOTIB;



