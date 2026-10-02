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

-- Índexs per ordenar el llistat de remeses per concepte, número d'expedient, usuari creador i tipus d'enviament
-- Els de text són índexs NLSSORT: la sessió ordena amb l'NLS_SORT de la JVM (CATALAN amb una JVM ca_ES) i un
-- ORDER BY de text només pot fer servir un índex amb el mateix NLS_SORT.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-35::limit
CREATE INDEX not_NOTIF_ENT_CONCEPTE_I ON not_NOTIFICACIO(ENTITAT_ID, DELETED, NLSSORT(CONCEPTE, 'NLS_SORT=CATALAN'));

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-36::limit
CREATE INDEX not_NOTIF_ENT_NUMEXP_I ON not_NOTIFICACIO(ENTITAT_ID, DELETED, NLSSORT(REGISTRE_NUM_EXPEDIENT, 'NLS_SORT=CATALAN'));

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-37::limit
CREATE INDEX not_NOTIF_ENT_CREATBY_I ON not_NOTIFICACIO(ENTITAT_ID, DELETED, NLSSORT(CREATEDBY_CODI, 'NLS_SORT=CATALAN'));

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-38::limit
CREATE INDEX not_NOTIF_ENT_ENVTIPUS_I ON not_NOTIFICACIO(ENTITAT_ID, DELETED, ENV_TIPUS);

-- Índexs per ordenar el llistat de remeses per data d'enviament, números de registre i titular (camps de
-- not_notificacio_table). Inclouen ID perquè també s'indexin les files amb el camp nul.

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-39::limit
CREATE INDEX not_TABLE_ENVDATE_I ON not_NOTIFICACIO_TABLE(ENVIADA_DATE, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-40::limit
CREATE INDEX not_TABLE_REGNUMS_I ON not_NOTIFICACIO_TABLE(NLSSORT(REGISTRE_NUMS, 'NLS_SORT=CATALAN'), ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-41::limit
CREATE INDEX not_TABLE_TITULAR_I ON not_NOTIFICACIO_TABLE(NLSSORT(TITULAR, 'NLS_SORT=CATALAN'), ID);

-- Índex per al COUNT del llistat de remeses dels usuaris sense rol d'administrador (filtres de permisos i
-- "només les meves"): es resol llegint només l'índex

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-43::limit
CREATE INDEX not_NOTIF_ENT_PERMIS_I ON not_NOTIFICACIO(ENTITAT_ID, DELETED, ORGAN_GESTOR, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI);
