-- Notib 2.1.1 - Llistat de remeses (interfície React): índexs de not_notificacio_table. S'executa després de
-- 06_update_2.1.1_dml.sql: es creen quan les columnes noves ja estan omplertes.

-- Índexs per ordenar el llistat de remeses i per al COUNT dels usuaris sense rol d'administrador (permisos i
-- "només les meves"). Darrere la columna de l'ordenació hi ha les dels filtres de permisos i de "només les
-- meves", i l'ID: la consulta dels ids d'una pàgina es resol llegint només l'índex.
-- Els de text són índexs NLSSORT amb l'ordenació alfabètica multilingüe GENERIC_M, la mateixa que fa servir el
-- llistat de remeses: l'ordre no depèn de l'idioma de la sessió ni de la JVM.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-45::limit
CREATE INDEX not_TABLE_ENT_CREATED_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, CREATEDDATE, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-46::limit
CREATE INDEX not_TABLE_ENT_CONCEPTE_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(CONCEPTE, 'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-47::limit
CREATE INDEX not_TABLE_ENT_NUMEXP_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(REGISTRE_NUM_EXPEDIENT, 'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-48::limit
CREATE INDEX not_TABLE_ENT_CREATBY_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(CREATEDBY_CODI, 'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-49::limit
CREATE INDEX not_TABLE_ENT_ENVTIPUS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ENV_TIPUS, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-50::limit
CREATE INDEX not_TABLE_ENT_ENVDATE_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ENVIADA_DATE, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-51::limit
CREATE INDEX not_TABLE_ENT_REGNUMS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(REGISTRE_NUMS, 'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-52::limit
CREATE INDEX not_TABLE_ENT_TITULAR_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(TITULAR, 'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-53::limit
CREATE INDEX not_TABLE_ENT_ESTAT_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ESTAT, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-54::limit
CREATE INDEX not_TABLE_ENT_PROC_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, PROCEDIMENT_ID, ORGAN_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-55::limit
CREATE INDEX not_TABLE_ENT_PERMIS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-57::limit
CREATE INDEX not_TABLE_ENT_ESTLLIST_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ESTAT_LLISTAT, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);
