-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-1::limit
ALTER TABLE not_config DROP CONSTRAINT not_config_pkey;

ALTER TABLE not_config ADD COLUMN id BIGINT;

UPDATE not_config SET id = nextval('not_hibernate_sequence');

ALTER TABLE not_config ADD CONSTRAINT not_config_pkey PRIMARY KEY (id);

ALTER TABLE not_config ADD CONSTRAINT not_config_key_uk UNIQUE (key);

ALTER TABLE not_config DROP CONSTRAINT not_config_group_fk;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-2::limit
ALTER TABLE not_config_group DROP CONSTRAINT not_config_group_pkey;

ALTER TABLE not_config_group ADD COLUMN id BIGINT;

UPDATE not_config_group SET id = nextval('not_hibernate_sequence');

ALTER TABLE not_config_group ADD CONSTRAINT not_config_group_pkey PRIMARY KEY (id);

ALTER TABLE not_config_group ADD CONSTRAINT not_config_group_code_uk UNIQUE (code);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-3::limit
ALTER TABLE not_config_type DROP CONSTRAINT not_config_type_pkey;

ALTER TABLE not_config_type ADD COLUMN id BIGINT;

UPDATE not_config_type SET id = nextval('not_hibernate_sequence');

ALTER TABLE not_config_type ADD CONSTRAINT not_config_type_pkey PRIMARY KEY (id);

ALTER TABLE not_config_type ADD CONSTRAINT not_config_type_code_uk UNIQUE (code);

ALTER TABLE not_config ADD CONSTRAINT not_config_group_fk FOREIGN KEY (group_code) REFERENCES not_config_group (code);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-4::limit
ALTER TABLE not_config ADD COLUMN config_group_id BIGINT;

ALTER TABLE not_config ADD COLUMN config_type_id BIGINT;

ALTER TABLE not_config ADD COLUMN entitat_id BIGINT;

ALTER TABLE not_config ADD CONSTRAINT not_config_group_id_fk FOREIGN KEY (config_group_id) REFERENCES not_config_group (id);

ALTER TABLE not_config ADD CONSTRAINT not_config_type_id_fk FOREIGN KEY (config_type_id) REFERENCES not_config_type (id);

ALTER TABLE not_config ADD CONSTRAINT not_config_entitat_fk FOREIGN KEY (entitat_id) REFERENCES not_entitat (id);

UPDATE not_config a SET config_group_id = (SELECT b.id FROM not_config_group b WHERE a.group_code = b.code) WHERE a.group_code IS NOT NULL;

UPDATE not_config a SET config_type_id = (SELECT b.id FROM not_config_type b WHERE a.type_code = b.code) WHERE a.type_code IS NOT NULL;

UPDATE not_config a SET entitat_id = (SELECT b.id FROM not_entitat b WHERE a.entitat_codi = b.codi) WHERE a.entitat_codi IS NOT NULL;

ALTER TABLE not_config ALTER COLUMN config_group_id SET NOT NULL;

ALTER TABLE not_config ALTER COLUMN config_type_id SET NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-5::limit
ALTER TABLE not_config_group ADD COLUMN parent_id BIGINT;

ALTER TABLE not_config_group ADD CONSTRAINT not_config_group_parent_fk FOREIGN KEY (parent_id) REFERENCES not_config_group (id);

UPDATE not_config_group a SET parent_id = (SELECT b.id FROM not_config_group b WHERE a.parent_code = b.code) WHERE a.parent_code IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-6::limit
ALTER TABLE not_notificacio_env_table ADD CONSTRAINT not_env_table_organ_fk FOREIGN KEY (organ_id) REFERENCES not_organ_gestor (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-7::limit
ALTER TABLE not_notificacio_table ADD CONSTRAINT not_not_table_organ_fk FOREIGN KEY (organ_id) REFERENCES not_organ_gestor (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-8::limit
ALTER TABLE not_notificacio_env_table ADD PRIMARY KEY (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-9::limit
ALTER TABLE not_pagador_postal ALTER COLUMN contracte_num SET NOT NULL;

ALTER TABLE not_pagador_postal ALTER COLUMN facturacio_codi_client SET NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-10::limit
ALTER TABLE not_notificacio_env ALTER COLUMN notifica_datat_recnif TYPE VARCHAR(50);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-11::limit
ALTER TABLE not_organ_gestor ADD COLUMN organ_pare BIGINT;

UPDATE not_organ_gestor a SET organ_pare = (SELECT b.id FROM not_organ_gestor b WHERE b.entitat = a.entitat AND b.codi = a.codi_pare);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-12::limit
ALTER TABLE not_organ_gestor ADD CONSTRAINT not_organ_pare_fk FOREIGN KEY (organ_pare) REFERENCES not_organ_gestor (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-13::limit
ALTER TABLE not_usuari ADD COLUMN tema VARCHAR(10);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-14::limit
ALTER TABLE not_notificacio_env_table ADD COLUMN procediment_id BIGINT;

UPDATE not_notificacio_env_table t SET procediment_id = (SELECT n.procediment_id FROM not_notificacio n WHERE n.id = t.notificacio_id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-16::limit
ALTER TABLE not_persona ALTER COLUMN rao_social TYPE VARCHAR(255);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-17::limit
UPDATE not_notificacio_env_table SET registre_numero = NULL;

ALTER TABLE not_notificacio_env_table ALTER COLUMN registre_numero TYPE VARCHAR(50);

UPDATE not_notificacio_env_table t SET registre_numero = (SELECT registre_numero_formatat FROM not_notificacio_env e WHERE t.id = e.id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-18::limit
ALTER TABLE not_notificacio ADD COLUMN grup_id BIGINT;

UPDATE not_notificacio n SET grup_id = (SELECT id FROM not_grup g WHERE n.grup_codi = g.codi) WHERE n.grup_codi IS NOT NULL;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-19::limit
ALTER TABLE not_accio_massiva ADD CONSTRAINT not_accio_massiva_entitat_fk FOREIGN KEY (entitat_id) REFERENCES not_entitat (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-21::limit
INSERT INTO not_config (id, position, key, value, jboss_property, description, group_code, type_code, config_group_id, config_type_id) VALUES (nextval('not_hibernate_sequence'), 1, 'es.caib.notib.plugin.cie.https', 'true', 0, 'El WS del plugin CIE es via https', 'CIE', 'BOOL', (SELECT id FROM not_config_group g WHERE g.code = 'CIE'), (SELECT id FROM not_config_type t WHERE t.code = 'BOOL') );

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-23::limit
DELETE FROM not_callback c
WHERE c.notificacio_id IS NOT NULL
  AND NOT EXISTS (
    SELECT 1
    FROM not_notificacio n
    WHERE n.id = c.notificacio_id
);

ALTER TABLE not_callback ADD CONSTRAINT not_callback_not_fk FOREIGN KEY (notificacio_id) REFERENCES not_notificacio (id);

ALTER TABLE not_callback ADD CONSTRAINT not_callback_env_fk FOREIGN KEY (enviament_id) REFERENCES not_notificacio_env (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-24::limit
INSERT INTO not_config (id, position, key, value, jboss_property, description, group_code, type_code, config_group_id, config_type_id, configurable) VALUES (nextval('not_hibernate_sequence'), 20, 'es.caib.notib.app.maxresults.selects', '20', 0, 'Nombre de resultats màxim a mostrar per les llistes desplegables abans de paginar', 'GENERAL', 'INT', (SELECT id FROM not_config_group g WHERE g.code = 'GENERAL'), (SELECT id FROM not_config_type t WHERE t.code = 'INT'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-25::limit
ALTER TABLE not_usuari ADD COLUMN estil_menu VARCHAR(16) DEFAULT 'TEMA' NOT NULL;

UPDATE not_usuari SET tema = NULL WHERE tema IS NOT NULL AND tema NOT IN ('LIGHT','DARK','DRACULA','SISTEMA');

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-26::limit
INSERT INTO not_config (id, position, key, value, jboss_property, description, group_code, type_code, config_group_id, config_type_id, configurable) VALUES (nextval('not_hibernate_sequence'), 21, 'es.caib.notib.app.interficie.react.defecte', 'true', 0, 'Indica si en accedir a l''aplicació (/notibback) s''ha de mostrar per defecte la interfície nova (React) enlloc de la clàssica (JSP)', 'GENERAL', 'BOOL', (SELECT id FROM not_config_group g WHERE g.code = 'GENERAL'), (SELECT id FROM not_config_type t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-27::limit
ALTER TABLE not_accio_massiva ADD COLUMN estat VARCHAR(50);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-28::limit
CREATE INDEX not_notif_ent_created_i ON not_notificacio (entitat_id, createddate);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-29::limit
INSERT INTO not_config (id, position, key, value, jboss_property, description, group_code, type_code, config_group_id, config_type_id, configurable) VALUES (nextval('not_hibernate_sequence'), 22, 'es.caib.notib.app.llistat.remeses.estat.asincron', 'true', 0, 'Indica si la columna estat del llistat de remeses (interfície React) pendent d''actualitzar s''ha de calcular de manera asíncrona i enviar al navegador quan estigui llesta (true), o calcular-la abans de retornar el llistat (false)', 'GENERAL', 'BOOL', (SELECT id FROM not_config_group g WHERE g.code = 'GENERAL'), (SELECT id FROM not_config_type t WHERE t.code = 'BOOL'), 0);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-30::limit
CREATE INDEX not_notificacio_notdest_fk_i ON not_notificacio_env (notificacio_id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-31::limit
CREATE INDEX not_persona_notenv_id_index ON not_persona (notificacio_env_id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-32::limit
CREATE INDEX not_notdest_notevent_fk_i ON not_notificacio_event (notificacio_env_id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-33::limit
CREATE INDEX not_notif_organ_fk_i ON not_notificacio (organ_gestor);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-34::limit
CREATE INDEX not_notificacio_creatby_i ON not_notificacio (createdby_codi);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-42::limit
INSERT INTO not_processos_inicials (id, codi, init) VALUES (7, 'CREAR_REGISTRES_NOT_NOTIFICACIO_TABLE', 1);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-44::limit
ALTER TABLE not_notificacio_table ADD COLUMN procediment_id BIGINT;

UPDATE not_notificacio_table t SET procediment_id = (SELECT n.procediment_id FROM not_notificacio n WHERE n.id = t.id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-45::limit
CREATE INDEX not_table_ent_created_i ON not_notificacio_table (entitat_id, deleted, createddate, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-46::limit
CREATE INDEX not_table_ent_concepte_i ON not_notificacio_table (entitat_id, deleted, lower(concepte), organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-47::limit
CREATE INDEX not_table_ent_numexp_i ON not_notificacio_table (entitat_id, deleted, lower(registre_num_expedient), organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-48::limit
CREATE INDEX not_table_ent_creatby_i ON not_notificacio_table (entitat_id, deleted, lower(createdby_codi), organ_id, procediment_id, procediment_organ_id, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-49::limit
CREATE INDEX not_table_ent_envtipus_i ON not_notificacio_table (entitat_id, deleted, env_tipus, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-50::limit
CREATE INDEX not_table_ent_envdate_i ON not_notificacio_table (entitat_id, deleted, enviada_date, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-51::limit
CREATE INDEX not_table_ent_regnums_i ON not_notificacio_table (entitat_id, deleted, lower(registre_nums), organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-52::limit
CREATE INDEX not_table_ent_titular_i ON not_notificacio_table (entitat_id, deleted, lower(titular), organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-53::limit
CREATE INDEX not_table_ent_estat_i ON not_notificacio_table (entitat_id, deleted, estat, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-54::limit
CREATE INDEX not_table_ent_proc_i ON not_notificacio_table (entitat_id, deleted, procediment_id, organ_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-55::limit
CREATE INDEX not_table_ent_permis_i ON not_notificacio_table (entitat_id, deleted, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-56::limit
ALTER TABLE not_notificacio_table ADD COLUMN estat_llistat INTEGER;

UPDATE not_notificacio_table SET estat_llistat = CASE WHEN estat = 0 AND COALESCE(registre_env_intent, 0) = 0 AND notifica_error_date IS NULL THEN 11 ELSE estat END;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-57::limit
CREATE INDEX not_table_ent_estllist_i ON not_notificacio_table (entitat_id, deleted, estat_llistat, organ_id, procediment_id, procediment_organ_id, createdby_codi, id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-58::limit
-- Subgrups per a les propietats que penjaven directament dels grups GENERAL, SCHEDULLED, FIRMA i ASPECTE, que a més tenen subgrups. El menú de propietats de la interfície React només ha de tenir pares que s'expandeixen i fills amb les propietats. Es mouen també les còpies per entitat (comparteixen el grup).
INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (nextval('not_hibernate_sequence'), 'GENERAL_COMUNES', 'GENERAL', (SELECT g.id FROM not_config_group g WHERE g.code = 'GENERAL'), 0, 'Configuracions comunes');

INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (nextval('not_hibernate_sequence'), 'SCHEDULLED_COMUNES', 'SCHEDULLED', (SELECT g.id FROM not_config_group g WHERE g.code = 'SCHEDULLED'), 0, 'Configuració comuna de les tasques periòdiques');

INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (nextval('not_hibernate_sequence'), 'FIRMA_SERVIDOR', 'FIRMA', (SELECT g.id FROM not_config_group g WHERE g.code = 'FIRMA'), 0, 'Configuració de la firma en servidor');

INSERT INTO not_config_group (id, code, parent_code, parent_id, position, description) VALUES (nextval('not_hibernate_sequence'), 'ASPECTE_COMUNES', 'ASPECTE', (SELECT g.id FROM not_config_group g WHERE g.code = 'ASPECTE'), 0, 'Aspecte comú de l''aplicació');

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'GENERAL_COMUNES'), group_code = 'GENERAL_COMUNES' WHERE group_code = 'GENERAL';

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'SCHEDULLED_COMUNES'), group_code = 'SCHEDULLED_COMUNES' WHERE group_code = 'SCHEDULLED';

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'FIRMA_SERVIDOR'), group_code = 'FIRMA_SERVIDOR' WHERE group_code = 'FIRMA';

UPDATE not_config SET config_group_id = (SELECT g.id FROM not_config_group g WHERE g.code = 'ASPECTE_COMUNES'), group_code = 'ASPECTE_COMUNES' WHERE group_code = 'ASPECTE';

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-59::limit
CREATE TABLE not_excepcio_log (
    id BIGINT NOT NULL,
    data TIMESTAMP NOT NULL,
    tipus VARCHAR(512),
    object_id VARCHAR(512),
    object_class VARCHAR(512),
    uri VARCHAR(1024),
    param1 VARCHAR(512),
    param2 VARCHAR(512),
    message VARCHAR(4000),
    stacktrace TEXT,
    entitat_id BIGINT,
    createddate TIMESTAMP,
    createdby_codi VARCHAR(64),
    lastmodifiedby_codi VARCHAR(64),
    lastmodifieddate TIMESTAMP,
    signatura VARCHAR(64),
    num_ocurrencies INTEGER,
    data_darrera TIMESTAMP,
    CONSTRAINT not_excepcio_log_pk PRIMARY KEY (id)
);

CREATE INDEX not_excepcio_log_sign_idx ON not_excepcio_log (signatura, data_darrera);

GRANT SELECT, UPDATE, INSERT, DELETE ON NOT_EXCEPCIO_LOG TO WWW_NOTIB;

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-60::limit
ALTER TABLE not_mon_int ADD COLUMN excepcio_id BIGINT;

ALTER TABLE not_mon_int ADD CONSTRAINT not_mon_int_excepcio_fk FOREIGN KEY (excepcio_id) REFERENCES not_excepcio_log (id);

-- Changeset db/changelog/changes/2_1_1_000.yaml::2_1_1_000-61::limit
INSERT INTO not_config (id, position, key, value, jboss_property, description, group_code, type_code, config_group_id, config_type_id) VALUES (nextval('not_hibernate_sequence'), 1, 'es.caib.notib.excepcions.retencio.dies', '30', 0, 'Dies que es conserven les excepcions del monitor d''excepcions abans de ser esborrades', 'SCHEDULLED', 'INT', (SELECT id FROM not_config_group g WHERE g.code = 'SCHEDULLED'), (SELECT id FROM not_config_type t WHERE t.code = 'INT') );

INSERT INTO not_config (id, position, key, value, jboss_property, description, group_code, type_code, config_group_id, config_type_id) VALUES (nextval('not_hibernate_sequence'), 1, 'es.caib.notib.excepcions.neteja.cron', '0 0 3 * * SAT', 0, 'Expressió cron per indicar quan executar la tasca que esborra les excepcions antigues del monitor d''excepcions', 'SCHEDULLED', 'CRON', (SELECT id FROM not_config_group g WHERE g.code = 'SCHEDULLED'), (SELECT id FROM not_config_type t WHERE t.code = 'CRON') );

INSERT INTO not_config (id, position, key, value, jboss_property, description, group_code, type_code, config_group_id, config_type_id) VALUES (nextval('not_hibernate_sequence'), 1, 'es.caib.ripea.excepcions.dedup.minuts', '10', 0, 'Minuts durant els quals una excepció repetida no es torna a registrar (només s''incrementa el comptador d''ocurrències). 0 per desactivar', 'SCHEDULLED', 'INT', (SELECT id FROM not_config_group g WHERE g.code = 'SCHEDULLED'), (SELECT id FROM not_config_type t WHERE t.code = 'INT') );
