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
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 21, 'es.caib.notib.app.interficie.react.defecte', 'true', 0, 'Indica si en accedir a l''aplicació (/notibback) s''ha de mostrar per defecte la interfície nova (React) enlloc de la clàssica (JSP)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-27::limit
ALTER TABLE not_accio_massiva ADD estat VARCHAR2(50 CHAR);


-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-28::limit
CREATE INDEX not_NOTIF_ENT_CREATED_I ON not_NOTIFICACIO(ENTITAT_ID, CREATEDDATE);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-29::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID, CONFIGURABLE) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 22, 'es.caib.notib.app.llistat.remeses.estat.asincron', 'true', 0, 'Indica si la columna estat del llistat de remeses (interfície React) pendent d''actualitzar s''ha de calcular de manera asíncrona i enviar al navegador quan estigui llesta (true), o calcular-la abans de retornar el llistat (false)', 'GENERAL', 'BOOL', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'GENERAL'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-30::limit
CREATE INDEX not_NOTIFICACIO_NOTDEST_FK_I ON not_NOTIFICACIO_ENV(NOTIFICACIO_ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-31::limit
CREATE INDEX not_PERSONA_NOTENV_ID_INDEX ON not_PERSONA(NOTIFICACIO_ENV_ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-32::limit
CREATE INDEX not_NOTDEST_NOTEVENT_FK_I ON not_NOTIFICACIO_EVENT(NOTIFICACIO_ENV_ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-33::limit
CREATE INDEX not_NOTIF_ORGAN_FK_I ON not_NOTIFICACIO(ORGAN_GESTOR);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-34::limit
CREATE INDEX not_NOTIFICACIO_CREATBY_I ON not_NOTIFICACIO(CREATEDBY_CODI);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-42::limit
INSERT INTO not_PROCESSOS_INICIALS (ID, CODI, INIT) VALUES (7, 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE', 1);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
ALTER TABLE not_NOTIFICACIO_TABLE ADD PROCEDIMENT_ID NUMBER(19, 0);

UPDATE not_NOTIFICACIO_TABLE t SET PROCEDIMENT_ID = (SELECT n.PROCEDIMENT_ID FROM not_NOTIFICACIO n WHERE n.ID = t.ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-45::limit
CREATE INDEX not_TABLE_ENT_CREATED_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, CREATEDDATE, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-46::limit
CREATE INDEX not_TABLE_ENT_CONCEPTE_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(CONCEPTE,'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-47::limit
CREATE INDEX not_TABLE_ENT_NUMEXP_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(REGISTRE_NUM_EXPEDIENT,'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-48::limit
CREATE INDEX not_TABLE_ENT_CREATBY_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(CREATEDBY_CODI,'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-49::limit
CREATE INDEX not_TABLE_ENT_ENVTIPUS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ENV_TIPUS, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-50::limit
CREATE INDEX not_TABLE_ENT_ENVDATE_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ENVIADA_DATE, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-51::limit
CREATE INDEX not_TABLE_ENT_REGNUMS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(REGISTRE_NUMS,'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-52::limit
CREATE INDEX not_TABLE_ENT_TITULAR_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, NLSSORT(TITULAR,'NLS_SORT=GENERIC_M'), ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-53::limit
CREATE INDEX not_TABLE_ENT_ESTAT_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ESTAT, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-54::limit
CREATE INDEX not_TABLE_ENT_PROC_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, PROCEDIMENT_ID, ORGAN_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-55::limit
CREATE INDEX not_TABLE_ENT_PERMIS_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-56::limit
ALTER TABLE not_NOTIFICACIO_TABLE ADD ESTAT_LLISTAT INTEGER;

UPDATE not_NOTIFICACIO_TABLE SET ESTAT_LLISTAT = CASE WHEN ESTAT = 0 AND COALESCE(REGISTRE_ENV_INTENT, 0) = 0 AND NOTIFICA_ERROR_DATE IS NULL THEN 11 ELSE ESTAT END;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-57::limit
CREATE INDEX not_TABLE_ENT_ESTLLIST_I ON not_NOTIFICACIO_TABLE(ENTITAT_ID, DELETED, ESTAT_LLISTAT, ORGAN_ID, PROCEDIMENT_ID, PROCEDIMENT_ORGAN_ID, CREATEDBY_CODI, ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-58::limit
-- Subgrups per a les propietats que penjaven directament dels grups GENERAL, SCHEDULLED, FIRMA i ASPECTE, que a més tenen subgrups. El menú de propietats de la interfície React només ha de tenir pares que s'expandeixen i fills amb les propietats. Es mouen també les còpies per entitat (comparteixen el grup).
INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (not_HIBERNATE_SEQ.nextval, 'GENERAL_COMUNES', 'GENERAL', (SELECT g.id FROM not_config_group g WHERE g.code = 'GENERAL'), 0, 'Configuracions comunes');

INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (not_HIBERNATE_SEQ.nextval, 'SCHEDULLED_COMUNES', 'SCHEDULLED', (SELECT g.id FROM not_config_group g WHERE g.code = 'SCHEDULLED'), 0, 'Configuració comuna de les tasques periòdiques');

INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (not_HIBERNATE_SEQ.nextval, 'FIRMA_SERVIDOR', 'FIRMA', (SELECT g.id FROM not_config_group g WHERE g.code = 'FIRMA'), 0, 'Configuració de la firma en servidor');

INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (not_HIBERNATE_SEQ.nextval, 'ASPECTE_COMUNES', 'ASPECTE', (SELECT g.id FROM not_config_group g WHERE g.code = 'ASPECTE'), 0, 'Aspecte comú de l''aplicació');

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'GENERAL_COMUNES'), group_code = 'GENERAL_COMUNES' WHERE group_code = 'GENERAL';

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'SCHEDULLED_COMUNES'), group_code = 'SCHEDULLED_COMUNES' WHERE group_code = 'SCHEDULLED';

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'FIRMA_SERVIDOR'), group_code = 'FIRMA_SERVIDOR' WHERE group_code = 'FIRMA';

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'ASPECTE_COMUNES'), group_code = 'ASPECTE_COMUNES' WHERE group_code = 'ASPECTE';

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-59::limit
CREATE TABLE not_EXCEPCIO_LOG (ID NUMBER(19, 0) NOT NULL, DATA TIMESTAMP(6) NOT NULL, TIPUS VARCHAR2(512 CHAR), OBJECT_ID VARCHAR2(512 CHAR), OBJECT_CLASS VARCHAR2(512 CHAR), URI VARCHAR2(1024 CHAR), PARAM1 VARCHAR2(512 CHAR), PARAM2 VARCHAR2(512 CHAR), MESSAGE VARCHAR2(4000 CHAR), STACKTRACE CLOB, ENTITAT_ID NUMBER(19, 0), createddate TIMESTAMP(6), createdby_codi VARCHAR2(64 CHAR), LASTMODIFIEDBY_CODI VARCHAR2(64 CHAR), LASTMODIFIEDDATE TIMESTAMP(6), SIGNATURA VARCHAR2(64 CHAR), num_ocurrencies NUMBER(10, 0), data_darrera TIMESTAMP(6), CONSTRAINT not_EXCEPCIO_LOG_PK PRIMARY KEY (ID));

CREATE INDEX NOT_EXCEPCIO_LOG_SIGN_IDX ON NOT_EXCEPCIO_LOG (SIGNATURA, DATA_DARRERA);

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_EXCEPCIO_LOG TO WWW_NOTIB;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-60::limit
ALTER TABLE not_mon_int ADD excepcio_id NUMBER(19, 0);

ALTER TABLE not_mon_int ADD CONSTRAINT not_MON_INT_EXCEPCIO_FK FOREIGN KEY (excepcio_id) REFERENCES not_EXCEPCIO_LOG (ID);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-61::limit
INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 1, 'es.caib.notib.excepcions.retencio.dies', '30', 0, 'Dies que es conserven les excepcions del monitor d''excepcions abans de ser esborrades', 'SCHEDULLED', 'INT', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'SCHEDULLED'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'INT') );

INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 1, 'es.caib.notib.excepcions.neteja.cron', '0 0 3 * * SAT', 0, 'Expressió cron per indicar quan executar la tasca que esborra les excepcions antigues del monitor d''excepcions', 'SCHEDULLED', 'CRON', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'SCHEDULLED'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'CRON') );

INSERT INTO not_CONFIG (id,POSITION, KEY, VALUE, JBOSS_PROPERTY, DESCRIPTION, GROUP_CODE, TYPE_CODE, CONFIG_GROUP_ID, CONFIG_TYPE_ID) VALUES (not_HIBERNATE_SEQ.NEXTVAL, 1, 'es.caib.ripea.excepcions.dedup.minuts', '10', 0, 'Minuts durant els quals una excepció repetida no es torna a registrar (només s''incrementa el comptador d''ocurrències). 0 per desactivar', 'SCHEDULLED', 'INT', (SELECT ID FROM not_CONFIG_GROUP g WHERE g.code = 'SCHEDULLED'), (SELECT ID FROM not_CONFIG_TYPE t WHERE t.code = 'INT') );



