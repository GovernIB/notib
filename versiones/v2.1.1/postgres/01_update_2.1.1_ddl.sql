-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-1::limit
ALTER TABLE not_config DROP CONSTRAINT not_CONFIG_PK;

ALTER TABLE not_config ADD COLUMN id BIGINT;

ALTER TABLE not_config ADD CONSTRAINT not_CONFIG_KEY_UK UNIQUE (key);

ALTER TABLE not_config DROP CONSTRAINT not_CONFIG_GROUP_FK;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-2::limit
ALTER TABLE not_config_group DROP CONSTRAINT not_CONFIG_GROUP_PK;

ALTER TABLE not_config_group ADD COLUMN id BIGINT;

ALTER TABLE not_config_group ADD CONSTRAINT not_CONFIG_GROUP_CODE_UK UNIQUE (code);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-3::limit
ALTER TABLE not_config_type DROP CONSTRAINT not_CONFIG_TYPE_PK;

ALTER TABLE not_config_type ADD COLUMN id BIGINT;

ALTER TABLE not_config_type ADD CONSTRAINT not_CONFIG_TYPE_CODE_UK UNIQUE (code);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_GROUP_FK FOREIGN KEY (GROUP_CODE) REFERENCES not_CONFIG_GROUP (CODE);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-4::limit
ALTER TABLE not_CONFIG ADD COLUMN config_group_id BIGINT;

ALTER TABLE not_CONFIG ADD COLUMN config_type_id BIGINT;

ALTER TABLE not_CONFIG ADD COLUMN entitat_id BIGINT;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-5::limit
ALTER TABLE not_CONFIG_GROUP ADD COLUMN parent_id BIGINT;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-6::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD CONSTRAINT not_ENV_TABLE_ORGAN_FK FOREIGN KEY (organ_id) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-7::limit
ALTER TABLE not_NOTIFICACIO_TABLE ADD CONSTRAINT not_NOT_TABLE_ORGAN_FK FOREIGN KEY (organ_id) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-8::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD PRIMARY KEY (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-9::limit
ALTER TABLE not_PAGADOR_POSTAL ALTER COLUMN contracte_num SET NOT NULL;

ALTER TABLE not_PAGADOR_POSTAL ALTER COLUMN facturacio_codi_client SET NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-10::limit
ALTER TABLE not_NOTIFICACIO_ENV ALTER COLUMN NOTIFICA_DATAT_RECNIF TYPE VARCHAR(50);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-11::limit
ALTER TABLE not_ORGAN_GESTOR ADD COLUMN organ_pare BIGINT;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-12::limit
ALTER TABLE not_ORGAN_GESTOR ADD CONSTRAINT not_ORGAN_PARE_FK FOREIGN KEY (organ_pare) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-13::limit
ALTER TABLE not_USUARI ADD COLUMN tema VARCHAR(10);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-14::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD COLUMN procediment_id BIGINT;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-16::limit
ALTER TABLE not_PERSONA ALTER COLUMN RAO_SOCIAL TYPE VARCHAR(255);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-18::limit
ALTER TABLE not_notificacio ADD COLUMN grup_id BIGINT;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-19::limit
ALTER TABLE not_ACCIO_MASSIVA ADD CONSTRAINT not_ACCIO_MASSIVA_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES not_ENTITAT (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-25::limit
ALTER TABLE not_usuari ADD COLUMN estil_menu VARCHAR(16) DEFAULT 'TEMA' NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-27::limit
ALTER TABLE not_accio_massiva ADD COLUMN estat VARCHAR(50);
