ALTER TABLE not_columnes RENAME COLUMN created_date TO data_enviament;

UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.EntitatEntity' WHERE class = 'es.caib.notib.core.entity.EntitatEntity';
UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.OrganGestorEntity' WHERE class = 'es.caib.notib.core.entity.OrganGestorEntity';
UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.ProcedimentEntity' WHERE class = 'es.caib.notib.core.entity.ProcedimentEntity';
UPDATE not_acl_class SET class = 'es.caib.notib.persist.entity.ProcSerOrganEntity' WHERE class = 'es.caib.notib.core.entity.ProcSerOrganEntity';

INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.serverurl', NULL, 'Url del servidor de keycloak', 'USUARIS', '6', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.realm', NULL, 'Realm del keycloak', 'USUARIS', '7', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id', NULL, 'Client ID del keycloak', 'USUARIS', '8', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.client_id_for_user_autentication', NULL, 'Client ID per autenticació del keycloak', 'USUARIS', '9', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.password_secret', NULL, 'Secret del client de keycloak', 'USUARIS', '10', '1', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.mapping.administrationID', 'NIF', 'Mapeig del administrationID de keycloak', 'USUARIS', '11', '0', 'TEXT', '0');
INSERT INTO not_config (key, value, description, group_code, position, jboss_property, type_code, configurable) VALUES ('es.caib.notib.plugin.dades.usuari.pluginsib.userinformation.keycloak.debug', 'false', 'Activar el debug del plugin de keycloak', 'USUARIS', '12', '0', 'BOOL', '0');


