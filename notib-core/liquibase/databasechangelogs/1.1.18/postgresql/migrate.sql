-- *********************************************************************
-- Update Database Script
-- *********************************************************************
-- Change Log: db/changelog/db.changelog-master.yaml
-- Ran at: 04/03/22 16:40
-- Against: null@offline:postgresql?changeLogFile=liquibase/databasechangelog.csv
-- Liquibase version: 4.4.3
-- *********************************************************************

-- Changeset db/changelog/changes/1.1.18/390.yaml::1634114082437-1::limit
ALTER TABLE not_organ_gestor ADD codi_pare VARCHAR(32);

-- Changeset db/changelog/changes/1.1.18/645.yaml::1634114082437-5::limit
ALTER TABLE not_notificacio_massiva ADD num_cancelades numeric(38, 0);

-- Changeset db/changelog/changes/1.1.18/701.yaml::1638376153806-2::limit
ALTER TABLE not_notificacio ALTER COLUMN registre_data TYPE TIMESTAMP WITHOUT TIME ZONE USING (registre_data::TIMESTAMP WITHOUT TIME ZONE);

update not_notificacio n set registre_data = (select min(registre_data) from not_notificacio_env e where e.notificacio_id = n.id);

-- Changeset db/changelog/changes/1.1.18/705.yaml::1634114082437-3::limit
ALTER TABLE not_notificacio ADD referencia VARCHAR(36);
ALTER TABLE not_notificacio ADD CONSTRAINT not_notificacio_ref_uk UNIQUE (referencia);
ALTER TABLE not_notificacio_table ADD referencia VARCHAR(36);
ALTER TABLE not_notificacio_table ADD CONSTRAINT not_notificacio_table_ref_uk UNIQUE (referencia);
ALTER TABLE not_notificacio_audit ADD referencia VARCHAR(36);
ALTER TABLE not_notificacio_env ALTER COLUMN notifica_ref TYPE VARCHAR(36) USING (notifica_ref::VARCHAR(36));
ALTER TABLE not_notificacio_env_table ALTER COLUMN notifica_ref TYPE VARCHAR(36) USING (notifica_ref::VARCHAR(36));
ALTER TABLE not_notificacio_env_audit ALTER COLUMN notifica_ref TYPE VARCHAR(36) USING (notifica_ref::VARCHAR(36));
ALTER TABLE not_columnes ADD referencia_notificacio numeric(1, 0);

-- Changeset db/changelog/changes/1.1.18/709.yaml::1638376153806-3::limit
ALTER TABLE not_persona ADD document_tipus VARCHAR(32);
ALTER TABLE not_notificacio_audit ALTER COLUMN estat TYPE VARCHAR(32) USING (estat::VARCHAR(32));
ALTER TABLE not_notificacio_env ADD per_email BOOLEAN;
ALTER TABLE not_notificacio ADD justificant_creat BOOLEAN;

UPDATE not_notificacio_env SET per_email = 'false';
UPDATE not_notificacio SET justificant_creat = 'false';
