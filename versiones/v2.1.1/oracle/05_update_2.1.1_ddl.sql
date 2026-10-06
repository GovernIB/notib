-- Notib 2.1.1 - Llistat de remeses (interfície React): índexs de claus foranes i columnes noves de
-- not_notificacio_table. S'executa després de 01_update_2.1.1_ddl.sql a 04_update_2.1.1_dml.sql.

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

-- Columnes de not_notificacio_table per al llistat de remeses: PROCEDIMENT_ID (filtre de permisos per
-- procediment) i ESTAT_LLISTAT (estat que mostra la columna estat, per ordenar-hi). Les omple
-- 06_update_2.1.1_dml.sql.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
ALTER TABLE not_NOTIFICACIO_TABLE ADD PROCEDIMENT_ID NUMBER(19,0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-56::limit
ALTER TABLE not_NOTIFICACIO_TABLE ADD ESTAT_LLISTAT INTEGER;
