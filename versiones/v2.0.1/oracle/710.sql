ALTER TABLE not_columnes RENAME COLUMN created_date TO data_enviament;

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

ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ESTAT_STRING VARCHAR2(512 CHAR);
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD DOCUMENT_ID NUMBER(38, 0);
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD ENV_CER_DATA TIMESTAMP;
-- ALTER TABLE NOT_NOTIFICACIO_TABLE ADD PERMIS_PROCESSAR NUMBER(1) DEFAULT '0';
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD REG_ENV_PENDENTS NUMBER(1) DEFAULT 0;
ALTER TABLE NOT_NOTIFICACIO_TABLE ADD PER_ACTUALITZAR NUMBER(1) DEFAULT 1;

CREATE TABLE not_sm_state_machine (machine_id VARCHAR2(255 CHAR) NOT NULL, state VARCHAR2(255 CHAR), state_machine_context BLOB, CONSTRAINT PK_NOT_SM_STATE_MACHINE PRIMARY KEY (machine_id));

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_STATE_MACHINE TO WWW_NOTIB;
