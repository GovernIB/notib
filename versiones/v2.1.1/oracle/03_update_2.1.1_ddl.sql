-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-1::limit
ALTER TABLE not_config ADD CONSTRAINT not_CONFIG_PK PRIMARY KEY (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-2::limit
ALTER TABLE not_config_group ADD CONSTRAINT not_CONFIG_GROUP_PK PRIMARY KEY (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-3::limit
ALTER TABLE not_config_type ADD CONSTRAINT not_CONFIG_TYPE_PK PRIMARY KEY (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-4::limit
ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_GROUP_ID_FK FOREIGN KEY (config_group_id) REFERENCES not_CONFIG_GROUP (ID);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_TYPE_ID_FK FOREIGN KEY (config_type_id) REFERENCES not_CONFIG_TYPE (ID);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES not_ENTITAT (ID);

ALTER TABLE not_CONFIG MODIFY config_group_id NOT NULL;

ALTER TABLE not_CONFIG MODIFY config_type_id NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-5::limit
ALTER TABLE not_CONFIG_GROUP ADD CONSTRAINT not_CONFIG_GROUP_PARENT_FK FOREIGN KEY (parent_id) REFERENCES not_CONFIG_GROUP (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-17::limit
ALTER TABLE not_notificacio_env_table MODIFY registre_numero VARCHAR2(50 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-23::limit
ALTER TABLE not_CALLBACK ADD CONSTRAINT not_CALLBACK_NOT_FK FOREIGN KEY (notificacio_id) REFERENCES not_NOTIFICACIO (id);

ALTER TABLE not_CALLBACK ADD CONSTRAINT not_CALLBACK_ENV_FK FOREIGN KEY (enviament_id) REFERENCES not_NOTIFICACIO_ENV (id);
