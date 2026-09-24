-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-1::limit
ALTER TABLE not_config DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config ADD id NUMBER(38, 0);

UPDATE not_CONFIG SET id = not_HIBERNATE_SEQ.nextval;

ALTER TABLE not_config ADD CONSTRAINT not_CONFIG_PK PRIMARY KEY (id);

ALTER TABLE not_config ADD CONSTRAINT not_CONFIG_KEY_UK UNIQUE (key);

ALTER TABLE not_config DROP CONSTRAINT not_CONFIG_GROUP_FK;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-2::limit
ALTER TABLE not_config_group DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config_group ADD id NUMBER(38, 0);

UPDATE not_CONFIG_GROUP SET id = not_HIBERNATE_SEQ.nextval;

ALTER TABLE not_config_group ADD CONSTRAINT not_CONFIG_GROUP_PK PRIMARY KEY (id);

ALTER TABLE not_config_group ADD CONSTRAINT not_CONFIG_GROUP_CODE_UK UNIQUE (code);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-3::limit
ALTER TABLE not_config_type DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config_type ADD id NUMBER(38, 0);

UPDATE not_CONFIG_TYPE SET id = not_HIBERNATE_SEQ.nextval;

ALTER TABLE not_config_type ADD CONSTRAINT not_CONFIG_TYPE_PK PRIMARY KEY (id);

ALTER TABLE not_config_type ADD CONSTRAINT not_CONFIG_TYPE_CODE_UK UNIQUE (code);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_GROUP_FK FOREIGN KEY (GROUP_CODE) REFERENCES not_CONFIG_GROUP (CODE);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-4::limit
ALTER TABLE not_CONFIG ADD config_group_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD config_type_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD entitat_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_GROUP_ID_FK FOREIGN KEY (config_group_id) REFERENCES not_CONFIG_GROUP (ID);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_TYPE_ID_FK FOREIGN KEY (config_type_id) REFERENCES not_CONFIG_TYPE (ID);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES not_ENTITAT (ID);

UPDATE not_config a SET config_group_id = (SELECT b.id FROM not_config_group b WHERE a.GROUP_CODE = b.code) WHERE a.GROUP_CODE IS NOT NULL;

UPDATE not_config a SET config_type_id = (SELECT b.id FROM not_config_type b WHERE a.TYPE_CODE = b.code) WHERE a.TYPE_CODE IS NOT NULL;

UPDATE not_config a SET entitat_id = (SELECT b.id FROM not_entitat b WHERE a.ENTITAT_CODI = b.CODI) WHERE a.ENTITAT_CODI IS NOT NULL;

ALTER TABLE not_CONFIG MODIFY config_group_id NOT NULL;

ALTER TABLE not_CONFIG MODIFY config_type_id NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-5::limit
ALTER TABLE not_CONFIG_GROUP ADD parent_id NUMBER(38, 0);

ALTER TABLE not_CONFIG_GROUP ADD CONSTRAINT not_CONFIG_GROUP_PARENT_FK FOREIGN KEY (parent_id) REFERENCES not_CONFIG_GROUP (ID);

UPDATE not_config_group a SET parent_id = (SELECT b.id FROM not_config_group b WHERE a.PARENT_CODE  = b.code) WHERE a.parent_code IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-6::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD CONSTRAINT not_ENV_TABLE_ORGAN_FK FOREIGN KEY (organ_id) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-7::limit
ALTER TABLE not_NOTIFICACIO_TABLE ADD CONSTRAINT not_NOT_TABLE_ORGAN_FK FOREIGN KEY (organ_id) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-8::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD PRIMARY KEY (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-9::limit
ALTER TABLE not_PAGADOR_POSTAL MODIFY contracte_num NOT NULL;

ALTER TABLE not_PAGADOR_POSTAL MODIFY facturacio_codi_client NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-10::limit
ALTER TABLE not_NOTIFICACIO_ENV MODIFY NOTIFICA_DATAT_RECNIF VARCHAR2(50 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-11::limit
ALTER TABLE not_ORGAN_GESTOR ADD organ_pare NUMBER(38, 0);

UPDATE not_organ_gestor a SET organ_pare = (select b.id from not_organ_gestor b where b.entitat = a.entitat and b.codi = a.codi_pare);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-12::limit
ALTER TABLE not_ORGAN_GESTOR ADD CONSTRAINT not_ORGAN_PARE_FK FOREIGN KEY (organ_pare) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-13::limit
ALTER TABLE not_USUARI ADD tema VARCHAR2(10 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-14::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD procediment_id NUMBER(38, 0);

UPDATE not_NOTIFICACIO_ENV_TABLE t SET t.PROCEDIMENT_ID = (SELECT n.procediment_id FROM not_notificacio n WHERE n.id= t.notificacio_id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-16::limit
ALTER TABLE not_PERSONA MODIFY RAO_SOCIAL VARCHAR2(255 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-17::limit
UPDATE not_notificacio_env_table SET registre_numero = NULL;

ALTER TABLE not_notificacio_env_table MODIFY registre_numero VARCHAR2(50 CHAR);

UPDATE not_notificacio_env_table t SET registre_numero = (SELECT registre_numero_formatat FROM not_NOTIFICACIO_ENV e WHERE t.id = e.id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-18::limit
ALTER TABLE not_notificacio ADD grup_id NUMBER(38, 0);

UPDATE not_notificacio n SET grup_id = (SELECT id FROM not_grup g WHERE n.GRUP_CODI = g.CODI) WHERE n.GRUP_CODI IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-19::limit
ALTER TABLE not_ACCIO_MASSIVA ADD CONSTRAINT not_ACCIO_MASSIVA_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES not_ENTITAT (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-21::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 1, 'es.caib.notib.plugin.cie.https', 'true', 0, 'El WS del plugin CIE es via https', 'CIE', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'CIE'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL') );

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-23::limit
DELETE FROM NOT_CALLBACK c
WHERE c.NOTIFICACIO_ID IS NOT NULL
  AND NOT EXISTS (
	SELECT 1
	FROM NOT_NOTIFICACIO n
	WHERE n.ID = c.NOTIFICACIO_ID
);

ALTER TABLE not_CALLBACK ADD CONSTRAINT not_CALLBACK_NOT_FK FOREIGN KEY (notificacio_id) REFERENCES not_NOTIFICACIO (id);

ALTER TABLE not_CALLBACK ADD CONSTRAINT not_CALLBACK_ENV_FK FOREIGN KEY (enviament_id) REFERENCES not_NOTIFICACIO_ENV (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-24::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 20, 'es.caib.notib.app.maxresults.selects', '20', 0, 'Nombre de resultats màxim a mostrar per les llistes desplegables abans de paginar', 'GENERAL', 'INT', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'INT'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-25::limit
ALTER TABLE not_usuari ADD estil_menu VARCHAR2(16 CHAR) DEFAULT 'TEMA' NOT NULL;

UPDATE not_usuari SET tema = NULL WHERE tema IS NOT NULL AND tema NOT IN ('LIGHT','DARK','DRACULA','SISTEMA');

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-26::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 21, 'es.caib.notib.app.interficie.react.defecte', 'true', 0, 'Indica si en accedir a l''aplicació (/notibback) s''ha de mostrar per defecte la interfície nova (React) enlloc de la clàssica (JSP)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-27::limit
ALTER TABLE not_accio_massiva ADD estat VARCHAR2(50 CHAR);
