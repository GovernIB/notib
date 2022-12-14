-- Changeset db/changelog/changes/1.1.22/704.yaml::1665140886760-1::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.codi.entorn', 'NOTIB','Codi de l''entorn on s''executa NOTIB', 'GENERAL', 0, 0, 'TEXT');

-- Changeset db/changelog/changes/1.1.22/727.yaml::1665140886759-1::limit
UPDATE NOT_USUARI SET REBRE_EMAILS_CREATS = 1 WHERE REBRE_EMAILS_CREATS = 0;

-- Changeset db/changelog/changes/1.1.22/747.yaml::1665140886759-2::limit
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.monitor.integracions.eliminar.periode', '3', 'Periode execució en dies de la nateja del monitor integracions', 'GENERAL', 0, 0, 'INT');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE) VALUES ('es.caib.notib.monitor.integracions.eliminar.anterior.dies', '3', 'Llindar en dies pel procés de nateja del monitor integracions', 'GENERAL', 0, 0, 'INT');

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

UPDATE NOT_PROCESSOS_INICIALS SET INIT = 1 WHERE codi = 'PROPIETATS_CONFIG_ENTITATS';
