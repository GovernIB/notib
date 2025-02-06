ALTER TABLE not_notificacio_table ADD caducitat TIMESTAMP;
ALTER TABLE not_notificacio_env ADD plazo_ampliado NUMBER(1, 0) DEFAULT '0';
ALTER TABLE not_entrega_postal ADD cie_error_desc VARCHAR2(250 CHAR);