ALTER TABLE not_notificacio_table ADD caducitat TIMESTAMP;
ALTER TABLE not_notificacio_env ADD plazo_ampliado NUMBER(1, 0) DEFAULT '0';
ALTER TABLE not_entrega_postal ADD cie_error_desc VARCHAR2(250 CHAR);
ALTER TABLE not_notificacio_table ADD ENTREGA_POSTAL_ERROR NUMBER(1, 0) DEFAULT '0';

ALTER TABLE not_entrega_postal ADD cie_error_desc VARCHAR(250);

ALTER TABLE not_entrega_postal ADD cie_datat_recnif VARCHAR(9);

ALTER TABLE not_entrega_postal ADD cie_datat_recnom VARCHAR(400);

ALTER TABLE not_entrega_postal ADD cie_cer_data TIMESTAMP(6) WITHOUT TIME ZONE;

ALTER TABLE not_entrega_postal ADD cie_cer_arxiuid VARCHAR(50);

ALTER TABLE not_entrega_postal ADD cie_cer_hash VARCHAR(50);

ALTER TABLE not_entrega_postal ADD cie_cer_origen VARCHAR(20);

ALTER TABLE not_entrega_postal ADD cie_cer_metas VARCHAR(255);

ALTER TABLE not_entrega_postal ADD cie_cer_csv VARCHAR(50);

ALTER TABLE not_entrega_postal ADD cie_cer_mime VARCHAR(20);

ALTER TABLE not_entrega_postal ADD cie_cer_tamany INTEGER;

ALTER TABLE not_entrega_postal ADD cie_cer_tipus VARCHAR(20);

ALTER TABLE not_entrega_postal ADD cie_cer_arxtip VARCHAR(20);

ALTER TABLE not_entrega_postal ADD cie_cer_numseg VARCHAR(50);

ALTER TABLE not_entrega_postal ADD cie_estat_data TIMESTAMP(6) WITHOUT TIME ZONE;

ALTER TABLE not_entrega_postal ADD cie_estat_desc VARCHAR(255);

ALTER TABLE not_entrega_postal ADD cie_datat_origen VARCHAR(20);

ALTER TABLE not_entrega_postal ADD cie_datat_numseg VARCHAR(50);

ALTER TABLE not_entrega_postal ADD cie_datat_errdes VARCHAR(255);

ALTER TABLE not_entrega_postal ADD cie_estat_dataact TIMESTAMP(6) WITHOUT TIME ZONE;