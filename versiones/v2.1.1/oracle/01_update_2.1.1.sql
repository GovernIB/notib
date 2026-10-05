-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-1::limit
ALTER TABLE not_config DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config ADD id NUMBER(38, 0);

UPDATE not_CONFIG SET id = not_HIBERNATE_SEQ.nextval;

ALTER TABLE not_config ADD CONSTRAINT not_CONFIG_PK PRIMARY KEY (id);

ALTER TABLE not_config ADD CONSTRAINT not_CONFIG_KEY_UK UNIQUE (key);

ALTER TABLE not_config DROP CONSTRAINT not_CONFIG_GROUP_FK;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-2::limit
ALTER TABLE not_config_group DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config_group ADD id NUMBER(38, 0);

UPDATE not_CONFIG_GROUP SET id = not_HIBERNATE_SEQ.nextval;

ALTER TABLE not_config_group ADD CONSTRAINT not_CONFIG_GROUP_PK PRIMARY KEY (id);

ALTER TABLE not_config_group ADD CONSTRAINT not_CONFIG_GROUP_CODE_UK UNIQUE (code);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-3::limit
ALTER TABLE not_config_type DROP PRIMARY KEY DROP INDEX;

ALTER TABLE not_config_type ADD id NUMBER(38, 0);

UPDATE not_CONFIG_TYPE SET id = not_HIBERNATE_SEQ.nextval;

ALTER TABLE not_config_type ADD CONSTRAINT not_CONFIG_TYPE_PK PRIMARY KEY (id);

ALTER TABLE not_config_type ADD CONSTRAINT not_CONFIG_TYPE_CODE_UK UNIQUE (code);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_GROUP_FK FOREIGN KEY (GROUP_CODE) REFERENCES not_CONFIG_GROUP (CODE);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-4::limit
ALTER TABLE not_CONFIG ADD config_group_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD config_type_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD entitat_id NUMBER(38, 0);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_GROUP_ID_FK FOREIGN KEY (config_group_id) REFERENCES not_CONFIG_GROUP (ID);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_TYPE_ID_FK FOREIGN KEY (config_type_id) REFERENCES not_CONFIG_TYPE (ID);

ALTER TABLE not_CONFIG ADD CONSTRAINT not_CONFIG_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES not_ENTITAT (ID);

UPDATE not_config a SET config_group_id = (SELECT b.id FROM not_config_group b WHERE a.GROUP_CODE = b.code) WHERE a.GROUP_CODE IS NOT NULL;

UPDATE not_config a SET config_type_id = (SELECT b.id FROM not_config_type b WHERE a.TYPE_CODE = b.code) WHERE a.TYPE_CODE IS NOT NULL;

UPDATE not_config a SET entitat_id = (SELECT b.id FROM not_entitat b WHERE a.ENTITAT_CODI = b.CODI) WHERE a.ENTITAT_CODI IS NOT NULL;

ALTER TABLE not_CONFIG MODIFY config_group_id NOT NULL;

ALTER TABLE not_CONFIG MODIFY config_type_id NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-5::limit
ALTER TABLE not_CONFIG_GROUP ADD parent_id NUMBER(38, 0);

ALTER TABLE not_CONFIG_GROUP ADD CONSTRAINT not_CONFIG_GROUP_PARENT_FK FOREIGN KEY (parent_id) REFERENCES not_CONFIG_GROUP (ID);

UPDATE not_config_group a SET parent_id = (SELECT b.id FROM not_config_group b WHERE a.PARENT_CODE  = b.code) WHERE a.parent_code IS NOT NULL;

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

UPDATE not_organ_gestor a SET organ_pare = (select b.id from not_organ_gestor b where b.entitat = a.entitat and b.codi = a.codi_pare);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-12::limit
ALTER TABLE not_ORGAN_GESTOR ADD CONSTRAINT not_ORGAN_PARE_FK FOREIGN KEY (organ_pare) REFERENCES not_ORGAN_GESTOR (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-13::limit
ALTER TABLE not_USUARI ADD tema VARCHAR2(10 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-14::limit
ALTER TABLE not_NOTIFICACIO_ENV_TABLE ADD procediment_id NUMBER(38, 0);

UPDATE not_NOTIFICACIO_ENV_TABLE t SET t.PROCEDIMENT_ID = (SELECT n.procediment_id FROM not_notificacio n WHERE n.id= t.notificacio_id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-16::limit
ALTER TABLE not_PERSONA MODIFY RAO_SOCIAL VARCHAR2(255 CHAR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-17::limit
UPDATE not_notificacio_env_table SET registre_numero = NULL;

ALTER TABLE not_notificacio_env_table MODIFY registre_numero VARCHAR2(50 CHAR);

UPDATE not_notificacio_env_table t SET registre_numero = (SELECT registre_numero_formatat FROM not_NOTIFICACIO_ENV e WHERE t.id = e.id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-18::limit
ALTER TABLE not_notificacio ADD grup_id NUMBER(38, 0);

UPDATE not_notificacio n SET grup_id = (SELECT id FROM not_grup g WHERE n.GRUP_CODI = g.CODI) WHERE n.GRUP_CODI IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-19::limit
ALTER TABLE not_ACCIO_MASSIVA ADD CONSTRAINT not_ACCIO_MASSIVA_ENTITAT_FK FOREIGN KEY (entitat_id) REFERENCES not_ENTITAT (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-21::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 1, 'es.caib.notib.plugin.cie.https', 'true', 0, 'El WS del plugin CIE es via https', 'CIE', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'CIE'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL') );

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-23::limit
DELETE FROM NOT_CALLBACK c
WHERE c.NOTIFICACIO_ID IS NOT NULL
  AND NOT EXISTS (
	SELECT 1
	FROM NOT_NOTIFICACIO n
	WHERE n.ID = c.NOTIFICACIO_ID
);

ALTER TABLE not_CALLBACK ADD CONSTRAINT not_CALLBACK_NOT_FK FOREIGN KEY (notificacio_id) REFERENCES not_NOTIFICACIO (id);

ALTER TABLE not_CALLBACK ADD CONSTRAINT not_CALLBACK_ENV_FK FOREIGN KEY (enviament_id) REFERENCES not_NOTIFICACIO_ENV (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-24::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 20, 'es.caib.notib.app.maxresults.selects', '20', 0, 'Nombre de resultats màxim a mostrar per les llistes desplegables abans de paginar', 'GENERAL', 'INT', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'INT'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-25::limit
ALTER TABLE not_usuari ADD estil_menu VARCHAR2(16 CHAR) DEFAULT 'TEMA' NOT NULL;

UPDATE not_usuari SET tema = NULL WHERE tema IS NOT NULL AND tema NOT IN ('LIGHT','DARK','DRACULA','SISTEMA');

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-26::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 21, 'es.caib.notib.app.interficie.react.defecte', 'false', 0, 'Indica si en accedir a l''aplicació (/notibback) s''ha de mostrar per defecte la interfície nova (React) enlloc de la clàssica (JSP)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-27::limit
ALTER TABLE not_accio_massiva ADD estat VARCHAR2(50 CHAR);

-- 05_update_2.1.1_ddl.sql

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

-- 06_update_2.1.1_dml.sql

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-29::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 22, 'es.caib.notib.app.llistat.remeses.estat.asincron', 'true', 0, 'Indica si la columna estat del llistat de remeses (interfície React) pendent d''actualitzar s''ha de calcular de manera asíncrona i enviar al navegador quan estigui llesta (true), o calcular-la abans de retornar el llistat (false)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-42::limit
-- Procés inicial: crea el registre de not_notificacio_table de les remeses que no en tenen
INSERT INTO not_PROCESSOS_INICIALS (ID, CODI, INIT) VALUES (7, 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE', 1);

-- Columnes noves de not_notificacio_table, en una sola passada per la taula i abans de crear-ne els índexs
-- (07_update_2.1.1_ddl.sql): PROCEDIMENT_ID, el de la remesa, i ESTAT_LLISTAT, ENVIANT (ordinal 11) si la
-- remesa està pendent sense intents de registre ni error de Notifica, o si no l'estat de la remesa.
-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-56::limit
UPDATE not_NOTIFICACIO_TABLE t
   SET PROCEDIMENT_ID = (SELECT n.PROCEDIMENT_ID FROM not_NOTIFICACIO n WHERE n.ID = t.ID),
       ESTAT_LLISTAT = CASE WHEN ESTAT = 0 AND COALESCE(REGISTRE_ENV_INTENT, 0) = 0 AND NOTIFICA_ERROR_DATE IS NULL THEN 11 ELSE ESTAT END;

-- 07_update_2.1.1_ddl.sql

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

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-57::limit
CREATE INDEX not_TABLE_ENT_ESTLLIST_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ESTAT_LLISTAT, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);
