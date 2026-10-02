-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-1::limit
UPDATE not_CONFIG SET id = nextval('not_hibernate_sequence');

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-2::limit
UPDATE not_CONFIG_GROUP SET id = nextval('not_hibernate_sequence');

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-3::limit
UPDATE not_CONFIG_TYPE SET id = nextval('not_hibernate_sequence');

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-4::limit
UPDATE not_config a SET config_group_id = (SELECT b.id FROM not_config_group b WHERE a.GROUP_CODE = b.code) WHERE a.GROUP_CODE IS NOT NULL;

UPDATE not_config a SET config_type_id = (SELECT b.id FROM not_config_type b WHERE a.TYPE_CODE = b.code) WHERE a.TYPE_CODE IS NOT NULL;

UPDATE not_config a SET entitat_id = (SELECT b.id FROM not_entitat b WHERE a.ENTITAT_CODI = b.CODI) WHERE a.ENTITAT_CODI IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-5::limit
UPDATE not_config_group a SET parent_id = (SELECT b.id FROM not_config_group b WHERE a.PARENT_CODE  = b.code) WHERE a.parent_code IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-11::limit
UPDATE not_organ_gestor a SET organ_pare = (select b.id from not_organ_gestor b where b.entitat = a.entitat and b.codi = a.codi_pare);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-14::limit
UPDATE not_NOTIFICACIO_ENV_TABLE t SET procediment_id = (SELECT n.procediment_id FROM not_notificacio n WHERE n.id = t.notificacio_id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-17::limit
UPDATE not_notificacio_env_table SET registre_numero = NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-18::limit
UPDATE not_notificacio n SET grup_id = (SELECT id FROM not_grup g WHERE n.GRUP_CODI = g.CODI) WHERE n.GRUP_CODI IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-21::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID) VALUES (nextval('not_hibernate_sequence'), 1, 'es.caib.notib.plugin.cie.https', 'true', 0, 'El WS del plugin CIE es via https', 'CIE', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'CIE'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL') );

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-23::limit
DELETE FROM NOT_CALLBACK c
WHERE c.NOTIFICACIO_ID IS NOT NULL
  AND NOT EXISTS (
	SELECT 1
	FROM NOT_NOTIFICACIO n
	WHERE n.ID = c.NOTIFICACIO_ID
);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-24::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (nextval('not_hibernate_sequence'), 20, 'es.caib.notib.app.maxresults.selects', '20', 0, 'Nombre de resultats màxim a mostrar per les llistes desplegables abans de paginar', 'GENERAL', 'INT', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'INT'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-25::limit
UPDATE not_usuari SET tema = NULL WHERE tema IS NOT NULL AND tema NOT IN ('LIGHT','DARK','DRACULA','SISTEMA');

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-26::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (nextval('not_hibernate_sequence'), 21, 'es.caib.notib.app.interficie.react.defecte', 'false', 0, 'Indica si en accedir a l''aplicació (/notibback) s''ha de mostrar per defecte la interfície nova (React) enlloc de la clàssica (JSP)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);
