CREATE TABLE NOT_PROCEDIMENT 
(
  ID						BIGSERIAL(19)	    	NOT NULL,
  CODI          			character varying(64),		NOT NULL,
  NOM						character varying(100),
  CODISIA					character varying(64),
  ENTITAT					BIGSERIAL(19),
  PAGADORPOSTAL				BIGSERIAL(19),
  PAGADORCIE				BIGSERIAL(19),
  AGRUPAR					BOOLEAN,
  RETARD 					BIGSERIAL(19),
  LLIBRE					character varying(64),
  OFICINA					character varying(64),
  TIPUSASSUMPTE				BOOLEAN,
  DATA_PROGRAMADA 			timestamp without time zone,
  CREATEDBY_CODI       		character varying(64),
  CREATEDDATE          		timestamp without time zone,
  LASTMODIFIEDBY_CODI  		character varying(64),
  LASTMODIFIEDDATE     		timestamp without time zone
);

CREATE TABLE NOT_GRUP 
(
  ID						BIGSERIAL(19)	    	NOT NULL,
  CODI						character varying(64),		NOT NULL,
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
  DIR3_CODI            		character varying(9),  		NOT NULL,
  CONTRACTE_NUM		        character varying(20),
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
  DIR3_CODI            		character varying(9), 		NOT NULL,
  CONTRACTE_DATA_VIG      	DATE,
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

ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_PROCEDIMENT_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY NOT_GRUP ADD (
  CONSTRAINT NOT_GRUP_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY NOT_PAGADOR_POSTAL ADD COLUMN (
  CONSTRAINT NOT_PAGADOR_POSTAL_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY NOT_PAGADOR_CIE ADD (
  CONSTRAINT NOT_PAGADOR_CIE_PK PRIMARY KEY (ID));
  
ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_PAGADOR_POSTAL_FK FOREIGN KEY (PAGADORPOSTAL)
    REFERENCES NOT_PAGADOR_POSTAL (ID));
  
ALTER TABLE NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_PAGADOR_CIE_FK FOREIGN KEY (PAGADORCIE)
    REFERENCES NOT_PAGADOR_CIE (ID));
    
ALTER TABLE ONLY NOT_PROCEDIMENT ADD (
  CONSTRAINT NOT_ENTITAT_FK FOREIGN KEY (ENTITAT)
    REFERENCES NOT_ENTITAT (ID));

