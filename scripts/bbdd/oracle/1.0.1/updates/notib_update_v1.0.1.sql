-- Update for version (1.0 -> 1.0.1)
-- #92
ALTER TABLE NOT_PROCEDIMENT ADD CADUCITAT NUMBER(19);

-- #94
ALTER TABLE NOT_ENTITAT ADD LOGO_CAP BLOB;
ALTER TABLE NOT_ENTITAT ADD LOGO_PEU BLOB;
ALTER TABLE NOT_ENTITAT ADD COLOR_FONS VARCHAR2(32);
ALTER TABLE NOT_ENTITAT ADD COLOR_LLETRA VARCHAR2(32);
ALTER TABLE NOT_ENTITAT ADD TIPUS_DOC_DEFAULT NUMBER(32);

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

ALTER TABLE NOT_ENTITAT_TIPUS_DOC ADD (
  CONSTRAINT NOT_ENTITAT_TIPUS_DOC_PK PRIMARY KEY (ID));

ALTER TABLE NOT_ENTITAT_TIPUS_DOC ADD (
  CONSTRAINT NOT_ENTITAT_TIPUS_DOC_FK FOREIGN KEY (ENTITAT_ID)
    REFERENCES NOT_ENTITAT (ID));
	
CREATE INDEX NOT_ENTITAT_TIPUS_DOC_FK_I ON NOT_ENTITAT_TIPUS_DOC(ENTITAT_ID);

-- #96
ALTER TABLE NOT_NOTIFICACIO ADD TIPUS_USUARI NUMBER(1);

-- #98
ALTER TABLE NOT_APLICACIO DROP COLUMN TIPUS_AUTENTICACIO;

-- #99
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

ALTER TABLE NOT_FORMATS_FULLA ADD (
  CONSTRAINT NOT_FORMATS_FULLA_PK PRIMARY KEY (ID));

ALTER TABLE NOT_FORMATS_SOBRE ADD (
  CONSTRAINT NOT_FORMATS_SOBRE_PK PRIMARY KEY (ID));
  
ALTER TABLE NOT_FORMATS_FULLA ADD (
  CONSTRAINT NOT_FORMATS_FULLA_FK FOREIGN KEY (PAGADOR_CIE_ID)
    REFERENCES NOT_PAGADOR_CIE (ID));
    
ALTER TABLE NOT_FORMATS_SOBRE ADD (
  CONSTRAINT NOT_FORMATS_SOBRE_FK FOREIGN KEY (PAGADOR_CIE_ID)
    REFERENCES NOT_PAGADOR_CIE (ID));
    
-- #104
ALTER TABLE NOT_PAGADOR_POSTAL ADD ENTITAT NUMBER(19);
ALTER TABLE NOT_PAGADOR_CIE ADD ENTITAT NUMBER(19);

UPDATE NOT_PAGADOR_POSTAL SET ENTITAT = 1;
UPDATE NOT_PAGADOR_CIE SET ENTITAT = 1;

ALTER TABLE NOT_PAGADOR_POSTAL ADD (
  CONSTRAINT NOT_PAGADOR_POSTAL_ENTITAT_FK FOREIGN KEY (ENTITAT)
    REFERENCES NOT_ENTITAT (ID));

ALTER TABLE NOT_PAGADOR_CIE ADD (
  CONSTRAINT NOT_PAGADOR_CIE_ENTITAT_FK FOREIGN KEY (ENTITAT)
    REFERENCES NOT_ENTITAT (ID));