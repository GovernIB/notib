ALTER TABLE not_notificacio_massiva ADD estat_validacio VARCHAR2(32);
ALTER TABLE not_notificacio_massiva ADD estat_proces VARCHAR2(32);
ALTER TABLE not_notificacio_massiva ADD num_notificacions INTEGER;
ALTER TABLE not_notificacio_massiva ADD num_validades INTEGER;
ALTER TABLE not_notificacio_massiva ADD num_processades INTEGER;
ALTER TABLE not_notificacio_massiva ADD num_error INTEGER;