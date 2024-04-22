ALTER TABLE not_columnes ADD data_creacio_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD data_enviament_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD num_registre_remesa NUMBER(1, 0) DEFAULT '0';

ALTER TABLE not_columnes ADD organ_emisor_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD proc_ser_codi_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD num_expedient_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD concepte_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD creada_per_remesa NUMBER(1, 0) DEFAULT '0';

ALTER TABLE not_columnes ADD interessats_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE not_columnes ADD estat_remesa NUMBER(1, 0) DEFAULT '1';

ALTER TABLE NOT_COLUMNES DROP COLUMN TITULAR_NIF;

ALTER TABLE NOT_COLUMNES ADD DATA_CREACIO NUMBER(1, 0) DEFAULT '0';

ALTER TABLE not_usuari ADD NUM_ELEMENTS_PAGINA_DEFECTE VARCHAR2(30 CHAR) DEFAULT 'DEU';

ALTER TABLE not_notificacio_table ADD REGISTRE_NUMS VARCHAR2(2048 CHAR);

ALTER TABLE not_notificacio_env ADD SIR_FI_POOLING NUMBER(1) DEFAULT '0';
