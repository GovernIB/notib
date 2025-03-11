ALTER TABLE NOT_PERSONA MODIFY NIF VARCHAR(50);

ALTER TABLE NOT_NOTIFICACIO_ENV_TABLE  MODIFY TITULAR_NIF  VARCHAR(50);

ALTER TABLE not_mon_int ADD header_csrf NUMBER(1) DEFAULT '1';

ALTER TABLE not_columnes DROP COLUMN llibre_registre;

ALTER TABLE not_columnes DROP COLUMN titular_email;

ALTER TABLE not_columnes DROP COLUMN data_registre;

ALTER TABLE not_columnes DROP COLUMN num_certificacio;

ALTER TABLE not_mon_int ADD notificacio_id NUMBER(19, 0);

ALTER TABLE not_organ_gestor ADD entrega_cie_desactivada NUMBER(1) DEFAULT '0' NOT NULL;

ALTER TABLE not_organ_gestor ADD sobrescriure_cie_organ_emisor NUMBER(1) DEFAULT '0' NOT NULL;