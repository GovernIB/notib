-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 04/03/22 16:31
-- Against: null@offline:oracle?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.18/390.yaml::1634114082437-1::limit
ALTER TABLE not_organ_gestor ADD codi_pare VARCHAR2(32 CHAR);

-- Changeset db/changelog/changes/1.1.18/645.yaml::1634114082437-5::limit
ALTER TABLE not_notificacio_massiva ADD num_cancelades NUMBER(38, 0);

-- Changeset db/changelog/changes/1.1.18/695.yaml::1634114082437-4::limit
CREATE TABLE NOT_PROCESSOS_INICIALS (ID NUMBER(19, 0) NOT NULL, CODI VARCHAR2(100 CHAR) NOT NULL, INIT NUMBER(1, 0) NOT NULL, CONSTRAINT NOT_PROCESSOS_INICIALS_PK PRIMARY KEY (ID));

INSERT INTO NOT_PROCESSOS_INICIALS (id, codi, init) VALUES (1, 'ACTUALITZAR_REFERENCIES', 1);

-- Changeset db/changelog/changes/1.1.18/701.yaml::1638376153806-2::limit
ALTER TABLE not_notificacio MODIFY registre_data TIMESTAMP;

update not_notificacio n set registre_data = (select min(registre_data) from not_notificacio_env e where e.notificacio_id = n.id);

-- Changeset db/changelog/changes/1.1.18/705.yaml::1634114082437-3::limit
ALTER TABLE not_notificacio ADD referencia VARCHAR2(36);
ALTER TABLE not_notificacio ADD CONSTRAINT not_notificacio_ref_uk UNIQUE (referencia);
ALTER TABLE not_notificacio_table ADD referencia VARCHAR2(36);
ALTER TABLE not_notificacio_table ADD CONSTRAINT not_notificacio_table_ref_uk UNIQUE (referencia);
ALTER TABLE not_notificacio_audit ADD referencia VARCHAR2(36);
ALTER TABLE not_notificacio_env MODIFY notifica_ref VARCHAR2(36);
ALTER TABLE not_notificacio_env_table MODIFY notifica_ref VARCHAR2(36);
ALTER TABLE not_notificacio_env_audit MODIFY notifica_ref VARCHAR2(36);
ALTER TABLE not_columnes ADD referencia_notificacio NUMBER(1, 0);

-- Changeset db/changelog/changes/1.1.18/709.yaml::1638376153806-3::limit
ALTER TABLE not_persona ADD document_tipus VARCHAR2(32 CHAR);
ALTER TABLE not_notificacio_audit MODIFY estat VARCHAR2(32 CHAR);
ALTER TABLE not_notificacio_env ADD per_email NUMBER(1);
ALTER TABLE not_notificacio ADD justificant_creat NUMBER(1);

UPDATE not_notificacio_env SET per_email = '0';
UPDATE not_notificacio SET justificant_creat = '0';
