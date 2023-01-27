-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 07/12/22 10:51
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.22/704.yaml::1665140886760-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.codi.entorn', 'NOTIB','Codi de l''entorn on s''executa NOTIB', 'GENERAL', 0, 0, 'TEXT');

-- Changeset db/changelog/changes/1.1.22/727.yaml::1665140886759-1::limit
UPDATE NOT_USUARI SET REBRE_EMAILS_CREATS = 1 WHERE REBRE_EMAILS_CREATS = 0;

-- Changeset db/changelog/changes/1.1.22/747.yaml::1665140886759-2::limit
CREATE TABLE NOT_MON_INT (ID NUMBER(19, 0) NOT NULL, CODI VARCHAR2(100 CHAR) NOT NULL, data TIMESTAMP(6), descripcio VARCHAR2(1024 CHAR), tipus VARCHAR2(10 CHAR), aplicacio VARCHAR2(64 CHAR), temps_resposta NUMBER(19, 0), estat VARCHAR2(5 CHAR), codi_usuari VARCHAR2(64 CHAR), codi_entitat VARCHAR2(64 CHAR), error_descripcio VARCHAR2(1024 CHAR), excepcio_msg VARCHAR2(1024 CHAR), excepcio_stacktrace VARCHAR2(2048 CHAR), CONSTRAINT NOT_MON_INT_PK PRIMARY KEY (ID));

CREATE TABLE NOT_MON_INT_PARAM (ID NUMBER(19, 0) NOT NULL, MON_INT_ID NUMBER(19, 0) NOT NULL, codi VARCHAR2(64 CHAR) NOT NULL, valor VARCHAR2(1024 CHAR), CONSTRAINT NOT_MON_INT_PARAM_PK PRIMARY KEY (ID));

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_MON_INT TO WWW_NOTIB;

ALTER TABLE NOT_MON_INT_PARAM ADD CONSTRAINT NOT_MONINTPARAM_MONINT_FK FOREIGN KEY (MON_INT_ID) REFERENCES NOT_MON_INT(ID);

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_MON_INT_PARAM TO WWW_NOTIB;

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.monitor.integracions.eliminar.periode', '3', 'Periode execució en dies de la neteja del monitor integracions', 'GENERAL', 0, 0, 'INT');

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.monitor.integracions.eliminar.anterior.dies', '3', 'Llindar en dies pel procés de neteja del monitor integracions', 'GENERAL', 0, 0, 'INT');

-- Changeset db/changelog/changes/1.1.22/755.yaml::1665140886759-3::limit
INSERT INTO NOT_CONFIG_TYPE (CODE, VALUE) VALUES ('VALSIG_CLASS', 'org.fundaciobit.plugins.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin,es.caib.notib.plugin.valsig.ValidacioFirmesPluginMock');

INSERT INTO NOT_CONFIG_GROUP (POSITION, PARENT_CODE, CODE, DESCRIPTION) VALUES (1, 'FIRMA', 'VALIDATE_SIGNATURE', 'Configuració del plugin validació de firmes' );

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) values ('es.caib.notib.plugin.validatesignature.class','org.fundaciobit.plugins.validatesignature.afirmacxf.AfirmaCxfValidateSignaturePlugin','Classe del plugin','VALIDATE_SIGNATURE',0,0,'VALSIG_CLASS',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) values ('es.caib.notib.plugins.validatesignature.afirmacxf.endpoint',null,'Endpoint de AfirmaCxf','VALIDATE_SIGNATURE',1,1,'TEXT',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) values ('es.caib.notib.plugins.validatesignature.afirmacxf.authorization.username',null,'Usuari de AfirmaCxf','VALIDATE_SIGNATURE',2,1,'TEXT',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) values ('es.caib.notib.plugins.validatesignature.afirmacxf.authorization.password',null,'Password de AfirmaCxf','VALIDATE_SIGNATURE',3,1,'PASSWORD',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) values ('es.caib.notib.plugins.validatesignature.afirmacxf.applicationID','CAIBDEV2.RIPEA','ID de l’aplicació','VALIDATE_SIGNATURE',4,0,'TEXT',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.afirmacxf.ignoreservercertificates', 'true', 'Ignorar els certificats', 'VALIDATE_SIGNATURE',5,0,'BOOL',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.afirmacxf.TransformersTemplatesPath', null, 'Path dels transformers', 'VALIDATE_SIGNATURE',6,1,'TEXT',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.afirmacxf.authorization.ks.path', null, 'Path del magatzem de claus amb el certificat per l''autenticació a Afirm@', 'VALIDATE_SIGNATURE',7,1,'TEXT',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.afirmacxf.authorization.ks.type', null, 'Tipus de magatzem de claus per l''autenticació a Afirm@', 'VALIDATE_SIGNATURE',8,1,'TEXT',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.afirmacxf.authorization.ks.password', null, 'Password del magatzem de claus per l''autenticació a Afirm@', 'VALIDATE_SIGNATURE',9,1,'PASSWORD',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.afirmacxf.authorization.ks.cert.alias', null, 'Alies del certificat a emprar del magatzem de claus per l''autenticació a Afirm@', 'VALIDATE_SIGNATURE',10,1,'TEXT',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.afirmacxf.authorization.ks.cert.password', null, 'Password del certificat del magatzem de claus per l''autenticació a Afirm@', 'VALIDATE_SIGNATURE',11,1,'PASSWORD',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.enable.web', 'true', 'Habilitar validació de signatura de documents en peticions via WEB','VALIDATE_SIGNATURE',12,0,'BOOL',1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.plugins.validatesignature.enable.rest', 'true', 'Habilitar validació de signatura de documents en peticions via REST','VALIDATE_SIGNATURE',13,0,'BOOL',1);

UPDATE NOT_PROCESSOS_INICIALS SET INIT = 1 WHERE codi = 'PROPIETATS_CONFIG_ENTITATS';

-- Changeset db/changelog/changes/1.1.22/756.yaml::1668153867375-2::limit
UPDATE NOT_PAGADOR_POSTAL  SET ORGAN_GESTOR = (SELECT nog.id FROM NOT_ORGAN_GESTOR nog WHERE DIR3_CODI IS NOT NULL AND DIR3_CODI = codi) WHERE ORGAN_GESTOR IS null;

ALTER TABLE not_pagador_postal DROP COLUMN dir3_codi;

UPDATE NOT_PAGADOR_CIE  SET ORGAN_GESTOR = (SELECT nog.id FROM NOT_ORGAN_GESTOR nog WHERE DIR3_CODI IS NOT NULL AND DIR3_CODI = codi) WHERE ORGAN_GESTOR IS null;

ALTER TABLE not_pagador_cie DROP COLUMN dir3_codi;

-- Changeset db/changelog/changes/1.1.22/762.yaml::1668153867375-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.comunicacions.sir.internes', 'false','Permetre realitzar comunicacions SIR dins la pròpia entitat', 'GENERAL', 0, 0, 'BOOL', 1);

