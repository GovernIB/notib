-- Variis scripts relacionats amb el registre de notificacions dins regweb3
ALTER TABLE NOT_NOTIFICACIO MODIFY REGISTRE_TIPUS_ASSUMPTE VARCHAR2(255);
ALTER TABLE NOT_NOTIFICACIO MODIFY REGISTRE_CODI_ASSUMPTE VARCHAR2(255);
ALTER TABLE NOT_NOTIFICACIO DROP COLUMN PROC_CODI_SIA;

ALTER TABLE NOT_PROCEDIMENT MODIFY TIPUSASSUMPTE VARCHAR2(255);
ALTER TABLE NOT_PROCEDIMENT ADD CODIASSUMPTE VARCHAR2(255);

ALTER TABLE NOT_NOTIFICACIO MODIFY DOCUMENT_ID NUMBER(19) NULL;

ALTER TABLE NOT_DOCUMENT MODIFY METADADES VARCHAR2(3000);

ALTER TABLE NOT_PERSONA ADD INTERESSATTIPUS VARCHAR2(30);
ALTER TABLE NOT_PERSONA MODIFY LLINATGE1 VARCHAR2(100) NULL;

ALTER TABLE NOT_NOTIFICACIO DROP COLUMN REGISTRE_DOC_FISICA;
ALTER TABLE NOT_NOTIFICACIO ADD REGISTRE_DOC_FISICA NUMBER(2);
ALTER TABLE NOT_NOTIFICACIO MODIFY REGISTRE_IDIOMA VARCHAR2(10);

ALTER TABLE NOT_NOTIFICACIO MODIFY REGISTRE_NUMERO NUMBER(19);
ALTER TABLE NOT_NOTIFICACIO ADD REGISTRE_NUMERO_FORMATAT VARCHAR2(50);
