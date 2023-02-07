-- Changeset db/changelog/changes/1.1.22/704.yaml::1665140886760-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.codi.entorn', 'NOTIB','Codi de l''entorn on s''executa NOTIB', 'GENERAL', 0, 0, 'TEXT');

-- Changeset db/changelog/changes/1.1.22/727.yaml::1665140886759-1::limit
UPDATE NOT_USUARI SET REBRE_EMAILS_CREATS = 1 WHERE REBRE_EMAILS_CREATS = 0;

-- Changeset db/changelog/changes/1.1.22/747.yaml::1665140886759-2::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.monitor.integracions.eliminar.periode', '0 30 1 * * *', 'Periode execució en dies de la neteja del monitor integracions', 'GENERAL', 0, 0, 'CRON');
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

-- Changeset db/changelog/changes/1.1.22/756.yaml::1668153867375-2::limit
UPDATE NOT_PAGADOR_POSTAL  SET ORGAN_GESTOR = (SELECT nog.id FROM NOT_ORGAN_GESTOR nog WHERE DIR3_CODI IS NOT NULL AND DIR3_CODI = codi) WHERE ORGAN_GESTOR IS null;
UPDATE NOT_PAGADOR_CIE  SET ORGAN_GESTOR = (SELECT nog.id FROM NOT_ORGAN_GESTOR nog WHERE DIR3_CODI IS NOT NULL AND DIR3_CODI = codi) WHERE ORGAN_GESTOR IS null;

-- Changeset db/changelog/changes/1.1.22/762.yaml::1668153867375-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.comunicacions.sir.internes', 'false','Permetre realitzar comunicacions SIR dins la pròpia entitat', 'GENERAL', 0, 0, 'BOOL', 1);

-- MULTITHREAD CONFIG --
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) values ('es.caib.notib.multithread',0,'Permetre execucions multithread','SCHEDULLED',0,0,'BOOL');

-- optimitzacions
UPDATE NOT_NOTIFICACIO_TABLE SET TITULAR = (select listagg(NOM || ' ' || LLINATGE1 || ' ' || LLINATGE2 || ' ' || NIF || ' ' || RAO_SOCIAL,', ') WITHIN GROUP (ORDER BY NOTIFICACIO_ENV_ID) FROM NOT_PERSONA P, NOT_NOTIFICACIO_ENV E WHERE P.ID = E.TITULAR_ID AND E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
UPDATE NOT_NOTIFICACIO_TABLE SET notifica_ids = (select listagg(E.NOTIFICA_ID, ', ') WITHIN GROUP (ORDER BY E.ID) FROM NOT_NOTIFICACIO_ENV E WHERE E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
-- Estat enviament
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (select SUM(DISTINCT CASE E.NOTIFICA_ESTAT WHEN 15 THEN 1 WHEN 23 THEN 2 WHEN 24 THEN 4 WHEN 22 THEN 8 WHEN 25 THEN 16 WHEN 10 THEN 32 WHEN 14 THEN 64 WHEN 20 THEN 128 WHEN 27 THEN 256 ELSE 0 END) as MASK FROM NOT_NOTIFICACIO_ENV_TABLE E WHERE E.NOTIFICACIO_ID = NOT_NOTIFICACIO_TABLE.ID);
-- Estat notificacio
-- Pendent
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 1) WHERE ESTAT = 0;
-- Enviada
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 2) WHERE ESTAT = 1;
-- Registrada
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 4) WHERE ESTAT = 2;
-- Processada
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 16) WHERE ESTAT = 4;
-- Enviada amb errors
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 512) WHERE ESTAT = 9;
-- Finalitzada amb errors
UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 1024) WHERE ESTAT = 10;

UPDATE NOT_NOTIFICACIO_TABLE SET ESTAT_MASK = (ESTAT_MASK + 2048) WHERE ESTAT = 11 AND registre_env_intent = 0 AND NOTIFICA_ERROR_DATE IS NULL;
-- 741
-- INSERT INTO not_acl_entry (acl_object_identity, ace_order, sid, mask, granting, audit_success, audit_failure) SELECT acl_object_identity, ace_order, sid, 8192, granting, audit_success, audit_failure FROM not_acl_entry WHERE mask = 1024;

UPDATE NOT_PROCESSOS_INICIALS SET INIT = 1 WHERE codi = 'PROPIETATS_CONFIG_ENTITATS';
