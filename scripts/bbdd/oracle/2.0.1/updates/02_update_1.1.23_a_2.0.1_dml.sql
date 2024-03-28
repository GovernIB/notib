UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.EntitatEntity' WHERE class = 'es.caib.notib.core.entity.EntitatEntity';
UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.OrganGestorEntity' WHERE class = 'es.caib.notib.core.entity.OrganGestorEntity';
UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.ProcedimentEntity' WHERE class = 'es.caib.notib.core.entity.ProcedimentEntity';
UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.ProcSerOrganEntity' WHERE class = 'es.caib.notib.core.entity.ProcSerOrganEntity';

INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl', NULL, 'Url del servidor de keycloak', 'USUARIS', '6', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm', NULL, 'Realm del keycloak', 'USUARIS', '7', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id', NULL, 'Client ID del keycloak', 'USUARIS', '8', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication', NULL, 'Client ID per autenticació del keycloak', 'USUARIS', '9', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret', NULL, 'Secret del client de keycloak', 'USUARIS', '10', '1', 'PASSWORD', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID', 'NIF', 'Mapeig del administrationID de keycloak', 'USUARIS', '11', '0', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug', 'false', 'Activar el debug del plugin de keycloak', 'USUARIS', '12', '0', 'BOOL', '0');

UPDATE NOT_CONFIG_TYPE SET VALUE = 'es.caib.notib.plugin.usuari.DadesUsuariPluginJdbc,es.caib.notib.plugin.usuari.DadesUsuariPluginLdap,es.caib.notib.plugin.usuari.DadesUsuariPluginMock,es.caib.notib.plugin.usuari.DadesUsuariPluginKeycloak' WHERE CODE = 'USUARIS_CLASS';
UPDATE NOT_CONFIG SET VALUE = 'es.caib.notib.plugin.usuari.DadesUsuariPluginKeycloak' WHERE KEY = 'es.caib.notib.plugin.dades.usuari.class';

DELETE FROM NOT_ENTITAT_TIPUS_DOC netd WHERE TIPUS_DOC = 3;

INSERT INTO NOT_PROCESSOS_INICIALS (id, codi, init) VALUES (5, 'AFEGIR_NOTIFICACIONS_MAQUINA_ESTATS', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.pooling.delay', '', 'Temps espera abans no inicia el pooling de la remesa', 'GENERAL', 10, 0, 'INT', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.state.machine.delay', '', 'Temps espera entre reintents en milisegons. (Format: 1er;2n;3er)', 'GENERAL', 11, 0, 'TEXT', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.massives.state.machine.inici.delay', '', 'Temps espera (ms) per iniciar cada fila de les notificacions massives.', 'GENERAL', 14, 0, 'INT', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.massives.maxim.files', '', 'Nombre màxim de files en les notificacions massives', 'GENERAL', 15, 0, 'INT', 0);

UPDATE NOT_CONFIG SET POSITION = 0, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.app.base.url';
UPDATE NOT_CONFIG SET POSITION = 1, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.default.user.language';
UPDATE NOT_CONFIG SET POSITION = 2, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.codi.entorn';
UPDATE NOT_CONFIG SET POSITION = 3, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.comunicacio.tipus.defecte';
UPDATE NOT_CONFIG SET POSITION = 4, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.metriques.generar';
UPDATE NOT_CONFIG SET POSITION = 5, CONFIGURABLE = 1 WHERE KEY = 'es.caib.notib.plugin.registre.generar.justificant';
UPDATE NOT_CONFIG SET POSITION = 6, CONFIGURABLE = 1 WHERE KEY = 'es.caib.notib.notifica.dir3.entitat.permes';
UPDATE NOT_CONFIG SET POSITION = 7, CONFIGURABLE = 1 WHERE KEY = 'es.caib.notib.plugin.codi.dir3.entitat';
UPDATE NOT_CONFIG SET POSITION = 8, CONFIGURABLE = 1 WHERE KEY = 'es.caib.notib.emprar.sir';
UPDATE NOT_CONFIG SET POSITION = 9, CONFIGURABLE = 1 WHERE KEY = 'es.caib.notib.comunicacions.sir.internes';
UPDATE NOT_CONFIG SET POSITION = 10, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.pooling.delay';
UPDATE NOT_CONFIG SET POSITION = 11, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.state.machine.delay';
UPDATE NOT_CONFIG SET POSITION = 12, CONFIGURABLE = 1 WHERE KEY = 'es.caib.notib.adviser.actiu';
UPDATE NOT_CONFIG SET POSITION = 13, CONFIGURABLE = 1 WHERE KEY = 'es.caib.notib.enviament.massiu.prioritat';
UPDATE NOT_CONFIG SET POSITION = 14, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.massives.state.machine.inici.delay';
UPDATE NOT_CONFIG SET POSITION = 15, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.massives.maxim.files';
UPDATE NOT_CONFIG SET POSITION = 16, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.monitor.integracions.eliminar.periode';
UPDATE NOT_CONFIG SET POSITION = 17, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.monitor.integracions.eliminar.anterior.dies';
UPDATE NOT_CONFIG SET POSITION = 18, CONFIGURABLE = 0 WHERE KEY = 'es.caib.notib.document.consulta.id.csv.mida.min';

INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.notifica.sincronitzar.url', null, 'URL de sincronització de Notific@', 'NOTIFICA', '2', '1', 'TEXT', '0');
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.adviser.forcar.resposta.ok', 'false', 'Forçar adviser resposta OK', 'GENERAL', 0, 0, 'BOOL', 1);

INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.TAULA_REMESES', 'false', 'Mostrar logs de la taula de remeses', 'LOGS', 0, 1, 'BOOL', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.REGISTRE', 'false', 'Mostrar logs de registre', 'LOGS', 0, 3, 'BOOL', 0);
INSERT INTO NOT_CONFIG (KEY, VALUE, DESCRIPTION, GROUP_CODE, POSITION, JBOSS_PROPERTY, TYPE_CODE, CONFIGURABLE) VALUES ('es.caib.notib.log.tipus.STATE_MACHINE', 'false', 'Mostrar logs de la state machine', 'LOGS', 0, 6, 'BOOL', 0);