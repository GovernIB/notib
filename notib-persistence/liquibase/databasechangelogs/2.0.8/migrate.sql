
-- Changeset db/changelog/changes/2.0.8/785.yaml::1634114082437-1::limit
ALTER TABLE NOT_PERSONA MODIFY NIF VARCHAR(50);

ALTER TABLE NOT_NOTIFICACIO_ENV_TABLE  MODIFY TITULAR_NIF  VARCHAR(50);

-- Changeset db/changelog/changes/2.0.8/810.yaml::1634114082437-1::limit
ALTER TABLE not_mon_int ADD header_csrf NUMBER(1) DEFAULT '1';

-- Changeset db/changelog/changes/2.0.8/878.yaml::1634114082437-1::limit
ALTER TABLE not_columnes DROP COLUMN llibre_registre;

ALTER TABLE not_columnes DROP COLUMN titular_email;

ALTER TABLE not_columnes DROP COLUMN data_registre;

ALTER TABLE not_columnes DROP COLUMN num_certificacio;

-- Changeset db/changelog/changes/2.0.8/964.yaml::1634114082437-1::limit
ALTER TABLE not_mon_int ADD notificacio_id NUMBER(19, 0);

-- Changeset db/changelog/changes/2.0.8/967.yaml::1634114082437-1::limit
ALTER TABLE not_organ_gestor ADD entrega_cie_desactivada NUMBER(1) DEFAULT '0' NOT NULL;

-- Changeset db/changelog/changes/2.0.8/970.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (1, 'es.caib.notib.adviser.sir.actiu', 'true', 'Activar adviser SIR', 'BOOL', 'GENERAL' );

-- Changeset db/changelog/changes/2.0.8/974.yaml::1634114082437-1::limit
ALTER TABLE not_organ_gestor ADD sobrescriure_cie_organ_emisor NUMBER(1) DEFAULT '0' NOT NULL;