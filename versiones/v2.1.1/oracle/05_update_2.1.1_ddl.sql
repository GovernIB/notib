-- Notib 2.1.1 - Llistat de remeses (interfície React): índexs i columna PROCEDIMENT_ID de not_notificacio_table.
-- S'executa després de 01_update_2.1.1_ddl.sql a 04_update_2.1.1_dml.sql.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-28::limit
CREATE INDEX not_NOTIF_ENT_CREATED_I ON not_NOTIFICACIO(ENTITAT_ID, CREATEDDATE);

-- Índexs de clau forana que ja creaven els scripts històrics però no el changelog inicial. Si ja existeixen
-- (amb aquest nom o sobre les mateixes columnes), s'ignora l'error i no es crea cap índex duplicat.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-30::limit
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX not_NOTIFICACIO_NOTDEST_FK_I ON not_NOTIFICACIO_ENV(NOTIFICACIO_ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-955, -1408) THEN RAISE; END IF;
END;
/

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-31::limit
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX not_PERSONA_NOTENV_ID_INDEX ON not_PERSONA(NOTIFICACIO_ENV_ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-955, -1408) THEN RAISE; END IF;
END;
/

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-32::limit
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX not_NOTDEST_NOTEVENT_FK_I ON not_NOTIFICACIO_EVENT(NOTIFICACIO_ENV_ID)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-955, -1408) THEN RAISE; END IF;
END;
/

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-33::limit
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX not_NOTIF_ORGAN_FK_I ON not_NOTIFICACIO(ORGAN_GESTOR)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-955, -1408) THEN RAISE; END IF;
END;
/

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-34::limit
BEGIN
    EXECUTE IMMEDIATE 'CREATE INDEX not_NOTIFICACIO_CREATBY_I ON not_NOTIFICACIO(CREATEDBY_CODI)';
EXCEPTION WHEN OTHERS THEN
    IF SQLCODE NOT IN (-955, -1408) THEN RAISE; END IF;
END;
/

-- Llistat de remeses (interfície React): es filtra, s'ordena, es pagina i es compta només amb
-- not_notificacio_table, com al llistat JSP. El permís per procediment necessita el procediment de la remesa.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
ALTER TABLE not_NOTIFICACIO_TABLE ADD PROCEDIMENT_ID NUMBER(19,0);
UPDATE not_NOTIFICACIO_TABLE t SET PROCEDIMENT_ID = (SELECT n.PROCEDIMENT_ID FROM not_NOTIFICACIO n WHERE n.ID = t.ID);

-- Índexs per ordenar el llistat de remeses i per al COUNT dels usuaris sense rol d'administrador (permisos i
-- "només les meves"). Darrere la columna de l'ordenació hi ha les dels filtres de permisos i de "només les
-- meves", i l'ID: la consulta dels ids d'una pàgina es resol llegint només l'índex.
-- Els de text són índexs NLSSORT: la sessió ordena amb l'NLS_SORT de la JVM (CATALAN
-- amb una JVM ca_ES) i un ORDER BY de text només pot fer servir un índex amb el mateix NLS_SORT.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-45::limit
CREATE INDEX not_TABLE_ENT_CREATED_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, CREATEDDATE, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-46::limit
CREATE INDEX not_TABLE_ENT_CONCEPTE_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(CONCEPTE, 'NLS_SORT=CATALAN'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-47::limit
CREATE INDEX not_TABLE_ENT_NUMEXP_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(REGISTRE_NUM_EXPEDIENT, 'NLS_SORT=CATALAN'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-48::limit
CREATE INDEX not_TABLE_ENT_CREATBY_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(CREATEDBY_CODI, 'NLS_SORT=CATALAN'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-49::limit
CREATE INDEX not_TABLE_ENT_ENVTIPUS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ENV_TIPUS, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-50::limit
CREATE INDEX not_TABLE_ENT_ENVDATE_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ENVIADA_DATE, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-51::limit
CREATE INDEX not_TABLE_ENT_REGNUMS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(REGISTRE_NUMS, 'NLS_SORT=CATALAN'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-52::limit
CREATE INDEX not_TABLE_ENT_TITULAR_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(TITULAR, 'NLS_SORT=CATALAN'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-53::limit
CREATE INDEX not_TABLE_ENT_ESTAT_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ESTAT, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-54::limit
CREATE INDEX not_TABLE_ENT_PROC_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, PROCEDIMENT_ID, ORGAN_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-55::limit
CREATE INDEX not_TABLE_ENT_PERMIS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);
