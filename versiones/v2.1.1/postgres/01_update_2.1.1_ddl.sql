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

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-28::limit
CREATE INDEX not_NOTIF_ENT_CREATED_I ON not_NOTIFICACIO(ENTITAT_ID, CREATEDDATE);

-- Índexs de clau forana que ja creaven els scripts històrics però no el changelog inicial. Si ja existeixen
-- (amb aquest nom o amb un índex que comenci per la mateixa columna), no es crea cap índex duplicat.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-30::limit
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_index i
          JOIN pg_class t ON t.oid = i.indrelid
          JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = i.indkey[0]
         WHERE t.relname = 'not_notificacio_env' AND a.attname = 'notificacio_id') THEN
        CREATE INDEX IF NOT EXISTS not_notificacio_notdest_fk_i ON not_notificacio_env(notificacio_id);
    END IF;
END $$;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-31::limit
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_index i
          JOIN pg_class t ON t.oid = i.indrelid
          JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = i.indkey[0]
         WHERE t.relname = 'not_persona' AND a.attname = 'notificacio_env_id') THEN
        CREATE INDEX IF NOT EXISTS not_persona_notenv_id_index ON not_persona(notificacio_env_id);
    END IF;
END $$;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-32::limit
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_index i
          JOIN pg_class t ON t.oid = i.indrelid
          JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = i.indkey[0]
         WHERE t.relname = 'not_notificacio_event' AND a.attname = 'notificacio_env_id') THEN
        CREATE INDEX IF NOT EXISTS not_notdest_notevent_fk_i ON not_notificacio_event(notificacio_env_id);
    END IF;
END $$;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-33::limit
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_index i
          JOIN pg_class t ON t.oid = i.indrelid
          JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = i.indkey[0]
         WHERE t.relname = 'not_notificacio' AND a.attname = 'organ_gestor') THEN
        CREATE INDEX IF NOT EXISTS not_notif_organ_fk_i ON not_notificacio(organ_gestor);
    END IF;
END $$;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-34::limit
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_index i
          JOIN pg_class t ON t.oid = i.indrelid
          JOIN pg_attribute a ON a.attrelid = t.oid AND a.attnum = i.indkey[0]
         WHERE t.relname = 'not_notificacio' AND a.attname = 'createdby_codi') THEN
        CREATE INDEX IF NOT EXISTS not_notificacio_creatby_i ON not_notificacio(createdby_codi);
    END IF;
END $$;

-- Índexs per ordenar el llistat de remeses per concepte, número d'expedient, usuari creador i tipus d'enviament

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-35::limit
CREATE INDEX IF NOT EXISTS not_notif_ent_concepte_i ON not_notificacio(entitat_id, deleted, concepte);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-36::limit
CREATE INDEX IF NOT EXISTS not_notif_ent_numexp_i ON not_notificacio(entitat_id, deleted, registre_num_expedient);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-37::limit
CREATE INDEX IF NOT EXISTS not_notif_ent_creatby_i ON not_notificacio(entitat_id, deleted, createdby_codi);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-38::limit
CREATE INDEX IF NOT EXISTS not_notif_ent_envtipus_i ON not_notificacio(entitat_id, deleted, env_tipus);

-- Índexs per ordenar el llistat de remeses per data d'enviament, números de registre i titular (camps de
-- not_notificacio_table). Inclouen ID perquè també s'indexin les files amb el camp nul.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-39::limit
CREATE INDEX IF NOT EXISTS not_table_envdate_i ON not_notificacio_table(enviada_date, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-40::limit
CREATE INDEX IF NOT EXISTS not_table_regnums_i ON not_notificacio_table(registre_nums, id);

-- El changeset 2_1_1_000-41 (índex per titular) només s'aplica a Oracle: a PostgreSQL una entrada d'índex
-- B-tree no pot superar ~2700 bytes i el titular pot arribar a 4000.

-- Índex per al COUNT del llistat de remeses dels usuaris sense rol d'administrador (filtres de permisos i
-- "només les meves"): es resol llegint només l'índex

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-43::limit
CREATE INDEX IF NOT EXISTS not_notif_ent_permis_i ON not_notificacio(entitat_id, deleted, organ_gestor, procediment_id, procediment_organ_id, createdby_codi);
