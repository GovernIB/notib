-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-1::limit
ALTER TABLE not_config DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config ADD id NUMBER(38, 0);

ALTER TABLE not_config ADD CONSTRAINT not_CONFIG_KEY_UK UNIQUE (key);

ALTER TABLE not_config DROP CONSTRAINT not_CONFIG_GROUP_FK;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-2::limit
ALTER TABLE not_config_group DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config_group ADD id NUMBER(38, 0);

ALTER TABLE not_config_group ADD CONSTRAINT not_CONFIG_GROUP_CODE_UK UNIQUE (code);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-3::limit
ALTER TABLE not_config_type DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config_type ADD id NUMBER(38, 0);

ALTER TABLE not_config_type ADD CONSTRAINT not_CONFIG_TYPE_CODE_UK UNIQUE (code);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_GROUP_FK FOREIGN KEY (GROUP_CODE) REFERENCES not_CONFIG_GROUP (CODE);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-4::limit
ALTER TABLE not_CONFIG ADD config_group_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD config_type_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD entitat_id NUMBER(38, 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-5::limit
ALTER TABLE not_CONFIG_GROUP ADD parent_id NUMBER(38, 0);

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

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-12::limit
ALTER TABLE not_ORGAN_GESTOR ADD CONSTRAINT not_ORGAN_PARE_FK FOREIGN KEY (organ_pare) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-13::limit
ALTER TABLE not_USUARI ADD tema VARCHAR2(10 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-14::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD procediment_id NUMBER(38, 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-16::limit
ALTER TABLE not_PERSONA MODIFY RAO_SOCIAL VARCHAR2(255 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-18::limit
ALTER TABLE not_notificacio ADD grup_id NUMBER(38, 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-19::limit
ALTER TABLE not_ACCIO_MASSIVA ADD CONSTRAINT not_ACCIO_MASSIVA_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES not_ENTITAT (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-25::limit
ALTER TABLE not_usuari ADD estil_menu VARCHAR2(16 CHAR) DEFAULT 'TEMA' NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-27::limit
ALTER TABLE not_accio_massiva ADD estat VARCHAR2(50 CHAR);
