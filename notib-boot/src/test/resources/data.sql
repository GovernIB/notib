INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('BOOL');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('TEXT');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('INT');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('FLOAT');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('CRON');
INSERT INTO NOT_CONFIG_TYPE (CODE) VALUES ('CREDENTIALS');

INSERT INTO NOT_CONFIG_GROUP (POSITION, CODE, DESCRIPTION) VALUES (0, 'GENERAL', 'Configuracions generals' );

INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.metriques.generar', 'false', 'false', 'BOOL', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasques.actives', 'false', 'false', 'TEXT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.registre.enviaments.periode', '300000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.registre.enviaments.retard.inicial', '3000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.notifica.enviaments.periode', '300000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.notifica.enviaments.retard.inicial', '3000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.periode', '300000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.retard.inicial', '3000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.registre.periode', '300000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.actualitzacio.estat.registre.retard.inicial', '3000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.actualitzacio.procediments.cron', '0 53 16 * * *', 'false', 'TEXT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.actualitzacio.serveis.cron', '0 58 16 * * *', 'false', 'TEXT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.refrescar.notificacions.expirades.cron', '0 0 0 * * ?', 'false', 'TEXT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.callback.pendents.periode', '300000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.callback.pendents.retard.inicial', '3000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.periode', '300000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.deh.actualitzacio.certificacio.retard.inicial', '3000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.periode', '432000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.tasca.enviament.cie.actualitzacio.certificacio.retard.inicial', '3000000', 'false', 'INT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.unitats.dir3.protocol', 'REST', 'false', 'TEXT', 'false', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE, POSITION) VALUES ('es.caib.notib.plugin.unitats.fitxer', '', 'false', 'TEXT', 'false', '0');

INSERT INTO NOT_ENTITAT (id, codi, nom, tipus, dir3_codi, descripcio, activa, version, api_key,  tipus_doc_default, amb_entrega_deh, dir3_codi_reg, llibre_entitat, oficina_entitat) VALUES (1000001, 'ENTITAT_TESTS', 'ENTITAT_TESTS', 'GOVERN', 'EA0004518', 'Descripció', 1, 1, '', 1, 1, '', 0, 0);

INSERT INTO NOT_ACL_CLASS (ID, CLASS) VALUES (1000004, 'es.caib.notib.logic.entity.EntitatEntity');
INSERT INTO NOT_ACL_SID (ID, PRINCIPAL, SID) VALUES (1000005, 1, 'admin');
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (1000006, 1000004, 1000001, NULL, 1000005, 1);

-- Permís usuari
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (1000007, 1000006, 0, 1000005, 32, 1, 0, 0);
-- Permís administrador entitat
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (1000008, 1000006, 1, 1000005, 128, 1, 0, 0);

INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM) VALUES (1000002, 'A00000000', 1000001, 'òrgan per defecte');
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM) VALUES (1000003, 'A04013511', 1000001, 'òrgan base de dades');
-- CREACIÓ D'UN ÒRGAN GESTOR SENSE CAP PERMÍS
INSERT INTO NOT_ORGAN_GESTOR (id, CODI, ENTITAT, NOM) VALUES (10000086, 'A00000002', 1000001, 'òrgan sense permisos');

-- -- -- -- -- --
-- Permisos de l'òrgan gestor
-- -- -- -- -- --
INSERT INTO NOT_ACL_CLASS (ID, CLASS) VALUES (1000009, 'es.caib.notib.logic.entity.OrganGestorEntity');
INSERT INTO NOT_ACL_OBJECT_IDENTITY (ID, OBJECT_ID_CLASS, OBJECT_ID_IDENTITY, PARENT_OBJECT, OWNER_SID, ENTRIES_INHERITING)  VALUES (1000010, 1000009, 1000002, NULL, 1000005, 1);

INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (1000011, 1000010, 0, 1000005, 32, 1, 0, 0);
INSERT INTO NOT_ACL_ENTRY (ID, ACL_OBJECT_IDENTITY, ACE_ORDER, SID, MASK, GRANTING, AUDIT_SUCCESS, AUDIT_FAILURE) VALUES (1000012, 1000010, 1, 1000005, 64, 1, 0, 0);

