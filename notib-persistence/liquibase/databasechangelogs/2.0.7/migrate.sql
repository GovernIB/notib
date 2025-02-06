-- Changeset db/changelog/changes/2.0.7/902.yaml::1634114082437-1::limit
INSERT INTO NOT_CONFIG (POSITION, KEY, VALUE, DESCRIPTION, TYPE_CODE, GROUP_CODE) VALUES (0, 'es.caib.notib.cache.paisos.provincies', '1296000000', 'Temps en millisegons que expira la cache de paisos', 'INT', 'SCHEDULLED_CACHE');

INSERT INTO NOT_CONFIG_GROUP (CODE,PARENT_CODE,POSITION,DESCRIPTION) VALUES ('SCHEDULLED_CACHE','SCHEDULLED',9,'Tàsca periòdica de nateja de caches');

-- Changeset db/changelog/changes/2.0.7/961.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio_table ADD caducitat TIMESTAMP;

UPDATE not_notificacio_table t SET CADUCITAT = (SELECT caducitat FROM not_notificacio n WHERE t.id = n.id);

-- Changeset db/changelog/changes/2.0.7/859.yaml::1634114082437-1::limit
ALTER TABLE not_notificacio_env ADD plazo_ampliado NUMBER(1, 0) DEFAULT '0';
UPDATE NOT_NOTIFICACIO_ENV set plazo_ampliado = 0;

-- Changeset db/changelog/changes/2.0.7/930.yaml::1634114082437-1::limit
ALTER TABLE not_entrega_postal ADD cie_error_desc VARCHAR2(250 CHAR);