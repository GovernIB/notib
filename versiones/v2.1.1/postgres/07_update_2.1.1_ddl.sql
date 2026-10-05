-- Notib 2.1.1 - Llistat de remeses (interfície React): índexs de not_notificacio_table. S'executa després de
-- 06_update_2.1.1_dml.sql: es creen quan les columnes noves ja estan omplertes.

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

-- El changeset 2_1_1_000-52 (índex per titular) només s'aplica a Oracle.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-53::limit
CREATE INDEX IF NOT EXISTS not_table_ent_estat_i ON not_notificacio_table(entitat_id, deleted, estat, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-54::limit
CREATE INDEX IF NOT EXISTS not_table_ent_proc_i ON not_notificacio_table(entitat_id, deleted, procediment_id, organ_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-55::limit
CREATE INDEX IF NOT EXISTS not_table_ent_permis_i ON not_notificacio_table(entitat_id, deleted, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-57::limit
CREATE INDEX IF NOT EXISTS not_table_ent_estllist_i ON not_notificacio_table(entitat_id, deleted, estat_llistat, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);
