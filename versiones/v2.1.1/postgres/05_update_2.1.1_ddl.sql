-- Notib 2.1.1 - Llistat de remeses (interfície React): índexs i columna PROCEDIMENT_ID de not_notificacio_table.
-- S'executa després de 01_update_2.1.1_ddl.sql a 04_update_2.1.1_dml.sql.

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

-- Llistat de remeses (interfície React): es filtra, s'ordena, es pagina i es compta només amb
-- not_notificacio_table, com al llistat JSP. El permís per procediment necessita el procediment de la remesa.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
ALTER TABLE not_notificacio_table ADD COLUMN IF NOT EXISTS procediment_id BIGINT;
UPDATE not_notificacio_table t SET procediment_id = (SELECT n.procediment_id FROM not_notificacio n WHERE n.id = t.id);

-- Índexs per ordenar el llistat de remeses i per al COUNT dels usuaris sense rol d'administrador (permisos i
-- "només les meves"). Darrere la columna de l'ordenació hi ha les dels filtres de permisos i de "només les
-- meves", i l'ID: la consulta dels ids d'una pàgina es resol llegint només l'índex.
-- A PostgreSQL l'índex per titular no es crea: una entrada d'índex B-tree no pot superar
-- ~2700 bytes i el titular pot arribar a 4000.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-45::limit
CREATE INDEX IF NOT EXISTS not_table_ent_created_i ON not_notificacio_table(entitat_id, deleted, createddate, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-46::limit
CREATE INDEX IF NOT EXISTS not_table_ent_concepte_i ON not_notificacio_table(entitat_id, deleted, concepte, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-47::limit
CREATE INDEX IF NOT EXISTS not_table_ent_numexp_i ON not_notificacio_table(entitat_id, deleted, registre_num_expedient, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-48::limit
CREATE INDEX IF NOT EXISTS not_table_ent_creatby_i ON not_notificacio_table(entitat_id, deleted, createdby_codi, organ_id, procediment_id, procediment_organ_id, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-49::limit
CREATE INDEX IF NOT EXISTS not_table_ent_envtipus_i ON not_notificacio_table(entitat_id, deleted, env_tipus, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-50::limit
CREATE INDEX IF NOT EXISTS not_table_ent_envdate_i ON not_notificacio_table(entitat_id, deleted, enviada_date, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-51::limit
CREATE INDEX IF NOT EXISTS not_table_ent_regnums_i ON not_notificacio_table(entitat_id, deleted, registre_nums, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-53::limit
CREATE INDEX IF NOT EXISTS not_table_ent_estat_i ON not_notificacio_table(entitat_id, deleted, estat, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-54::limit
CREATE INDEX IF NOT EXISTS not_table_ent_proc_i ON not_notificacio_table(entitat_id, deleted, procediment_id, organ_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-55::limit
CREATE INDEX IF NOT EXISTS not_table_ent_permis_i ON not_notificacio_table(entitat_id, deleted, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);
