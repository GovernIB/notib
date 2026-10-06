-- Notib 2.1.1 - Llistat de remeses (interfície React): índexs de claus foranes i columnes noves de
-- not_notificacio_table. S'executa després de 01_update_2.1.1_ddl.sql a 04_update_2.1.1_dml.sql.

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

-- Columnes de not_notificacio_table per al llistat de remeses: PROCEDIMENT_ID (filtre de permisos per
-- procediment) i ESTAT_LLISTAT (estat que mostra la columna estat, per ordenar-hi). Les omple
-- 06_update_2.1.1_dml.sql.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
ALTER TABLE not_notificacio_table ADD COLUMN IF NOT EXISTS procediment_id BIGINT;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-56::limit
ALTER TABLE not_notificacio_table ADD COLUMN IF NOT EXISTS estat_llistat INTEGER;
